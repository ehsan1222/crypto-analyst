package com.github.ehsan1222.ca.services;

import com.binance.api.client.domain.market.Candlestick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ehsan1222.ca.crypto.RuleEvaluator;
import com.github.ehsan1222.ca.dao.Pattern;
import com.github.ehsan1222.ca.io.FileManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.ehsan1222.ca.constants.KafkaConstants.BTC_TOPIC_NAME;
import static com.github.ehsan1222.ca.constants.KafkaConstants.ETH_TOPIC_NAME;

@Service
public class CryptoListenerService {

    private final RuleEvaluator ruleEvaluator;
    @Value("${app.patternPath}")
    private String patternConfigPath;
    private Map<String, List<Pattern>> patternMap;
    private Map<String, List<Candlestick>> cryptoMap;
    private String patternConfigMD5;

    public CryptoListenerService(RuleEvaluator ruleEvaluator) {
        this.ruleEvaluator = ruleEvaluator;
    }


    @KafkaListener(topics = BTC_TOPIC_NAME, groupId = "btc")
    public void listenToBTCCandlestickBars(ArrayList<Candlestick> candlesticks) {
        if (isPatternChanged(patternConfigMD5)) {
            getPatters();
        }
        addCryptoMap("btc", candlesticks);
        ruleEvaluator.evaluate(candlesticks, this.patternMap.get("BTC/USDT"));
    }

    @KafkaListener(topics = ETH_TOPIC_NAME, groupId = "eth")
    public void listenToETHCandlestickBars(ArrayList<Candlestick> candlesticks) {
        if (isPatternChanged(patternConfigMD5)) {
            getPatters();
        }
        addCryptoMap("eth", candlesticks);
        ruleEvaluator.evaluate(candlesticks, this.patternMap.get("ETH/USDT"));
    }

    private void addCryptoMap(String symbol, ArrayList<Candlestick> candlesticks) {
        if (cryptoMap == null) {
            cryptoMap = new ConcurrentHashMap<>();
        }

        if (cryptoMap.containsKey(symbol)) {
            cryptoMap.get(symbol).addAll(candlesticks);
            cryptoMap.get(symbol).subList(0, candlesticks.size()).clear();
        } else {
            cryptoMap.put(symbol, candlesticks);
        }
    }

    private boolean isPatternChanged(String patternMD5) {
        if (patternMD5 == null) {
            return true;
        }
        FileManager fileManager = new FileManager();
        String currentPatternMD5Hash = fileManager.getMD5Hash(Paths.get(patternConfigPath));
        return !currentPatternMD5Hash.equals(patternMD5);
    }

    private void getPatters() {
        synchronized (CryptoListenerService.class) {
            if (isPatternChanged(patternConfigMD5)) {
                FileManager fileManager = new FileManager();
                String pattersPath = fileManager.read(Path.of(patternConfigPath));
                List<Pattern> patterns = convertJsonToPattern(pattersPath);
                this.patternMap = getPatternMap(patterns);
                this.patternConfigMD5 = fileManager.getMD5Hash(Paths.get(patternConfigPath));
            }
        }
    }

    private Map<String, List<Pattern>> getPatternMap(List<Pattern> patterns) {
        Map<String, List<Pattern>> patternMap = new HashMap<>();
        for (Pattern pattern : patterns) {
            if (patternMap.containsKey(pattern.getMarketName())) {
                patternMap.get(pattern.getMarketName()).add(pattern);
            } else {
                patternMap.put(pattern.getMarketName(), new ArrayList<>(List.of(pattern)));
            }
        }
        return patternMap;
    }

    private List<Pattern> convertJsonToPattern(String pattersJson) {
        try {
            return new ObjectMapper()
                    .readValue(pattersJson,
                            new TypeReference<ArrayList<Pattern>>() {
                            });
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

}
