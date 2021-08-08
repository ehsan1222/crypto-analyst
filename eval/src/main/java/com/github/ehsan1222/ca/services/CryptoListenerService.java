package com.github.ehsan1222.ca.services;

import com.binance.api.client.domain.market.Candlestick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ehsan1222.ca.crypto.RuleEvaluator;
import com.github.ehsan1222.ca.dao.Pattern;
import com.github.ehsan1222.ca.io.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.ehsan1222.ca.constants.BinanceConstants.FIRST_CANDLESTICK_BATCH_SIZE;
import static com.github.ehsan1222.ca.constants.KafkaConstants.BTC_TOPIC_NAME;
import static com.github.ehsan1222.ca.constants.KafkaConstants.ETH_TOPIC_NAME;

@Service
@Slf4j
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
    public void listenToBTCCandlestickBars(ArrayList<Candlestick> candlesticks, Acknowledgment ack) {
        if (isPatternChanged(patternConfigMD5)) {
            getPatters();
        }
        String BTCSymbol = "btc";
        addCryptoMap(BTCSymbol, candlesticks);
        ruleEvaluator.evaluate(cryptoMap.get(BTCSymbol), this.patternMap.get("BTC/USDT"));
        ack.acknowledge();
    }

    @KafkaListener(topics = ETH_TOPIC_NAME, groupId = "eth")
    public void listenToETHCandlestickBars(ArrayList<Candlestick> candlesticks, Acknowledgment ack) {
        if (isPatternChanged(patternConfigMD5)) {
            getPatters();
        }
        String ETHSymbol = "eth";
        addCryptoMap(ETHSymbol, candlesticks);
        ruleEvaluator.evaluate(cryptoMap.get(ETHSymbol), this.patternMap.get("ETH/USDT"));
        ack.acknowledge();
    }

    private void addCryptoMap(String symbol, ArrayList<Candlestick> candlesticks) {
        if (cryptoMap == null) {
            cryptoMap = new ConcurrentHashMap<>();
        }

        if (cryptoMap.containsKey(symbol)) {
            List<Candlestick> currentCandlesticks = cryptoMap.get(symbol);
            currentCandlesticks.addAll(candlesticks);
            if (currentCandlesticks.size() > FIRST_CANDLESTICK_BATCH_SIZE) {
                int numberOfExtraItems = currentCandlesticks.size() - FIRST_CANDLESTICK_BATCH_SIZE;
                currentCandlesticks.subList(0, numberOfExtraItems).clear();
            }
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
                log.info("The pattern map was updated!");
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
        if (pattersJson == null) {
            log.warn("null pattern json was entered");
            return new ArrayList<>();
        }
        try {
            return new ObjectMapper()
                    .readValue(pattersJson,
                            new TypeReference<ArrayList<Pattern>>() {
                            });
        } catch (JsonProcessingException e) {
            log.warn("invalid json format " + pattersJson);
            return new ArrayList<>();
        }
    }

}
