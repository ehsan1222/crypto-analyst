package com.github.ehsan1222.ca.services;

import com.binance.api.client.domain.market.Candlestick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ehsan1222.ca.crypto.RuleEvaluator;
import com.github.ehsan1222.ca.dao.Rule;
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

import static com.github.ehsan1222.ca.constants.KafkaConstants.BTC_TOPIC_NAME;
import static com.github.ehsan1222.ca.constants.KafkaConstants.ETH_TOPIC_NAME;

@Service
public class CryptoListenerService {

    private final RuleEvaluator ruleEvaluator;
    @Value("${app.patternPath}")
    private String patternConfigPath;
    private Map<String, List<Rule>> ruleMap;
    private String patternConfigMD5;

    public CryptoListenerService(RuleEvaluator ruleEvaluator) {
        this.ruleEvaluator = ruleEvaluator;
    }


    @KafkaListener(topics = BTC_TOPIC_NAME, groupId = "btc")
    public void listenToBTCCandlestickBars(ArrayList<Candlestick> candlesticks) {
        if (isPatternChanged(patternConfigMD5)) {
            getPatters();
        }
        ruleEvaluator.evaluate(candlesticks, this.ruleMap.get("BTC/USDT"));
    }

    @KafkaListener(topics = ETH_TOPIC_NAME, groupId = "eth")
    public void listenToETHCandlestickBars(ArrayList<Candlestick> candlesticks) {
        if (isPatternChanged(patternConfigMD5)) {
            getPatters();
        }
        ruleEvaluator.evaluate(candlesticks, this.ruleMap.get("ETH/USDT"));
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
                List<Rule> rules = convertJsonToRule(pattersPath);
                this.ruleMap = getRuleMap(rules);
                this.patternConfigMD5 = fileManager.getMD5Hash(Paths.get(patternConfigPath));
            }
        }
    }

    private Map<String, List<Rule>> getRuleMap(List<Rule> rules) {
        Map<String, List<Rule>> ruleMap = new HashMap<>();
        for (Rule rule : rules) {
            if (ruleMap.containsKey(rule.getMarketName())) {
                ruleMap.get(rule.getMarketName()).add(rule);
            } else {
                ruleMap.put(rule.getMarketName(), new ArrayList<>(List.of(rule)));
            }
        }
        return ruleMap;
    }

    private List<Rule> convertJsonToRule(String pattersJson) {
        try {
            return new ObjectMapper()
                    .readValue(pattersJson,
                            new TypeReference<ArrayList<Rule>>() {
                            });
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

}
