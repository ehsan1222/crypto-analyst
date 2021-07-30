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
import java.util.*;

import static com.github.ehsan1222.ca.constants.KafkaConstants.BTC_TOPIC_NAME;
import static com.github.ehsan1222.ca.constants.KafkaConstants.ETH_TOPIC_NAME;

@Service
public class CryptoReceiverService {

    @Value("${app.patternPath}")
    private String patternPath;

    private Map<String, List<Rule>> ruleMap;

    private final RuleEvaluator ruleEvaluator;

    public CryptoReceiverService(RuleEvaluator ruleEvaluator) {
        this.ruleEvaluator = ruleEvaluator;
    }


    @KafkaListener(topics = BTC_TOPIC_NAME, groupId = "btc")
    public void listenToBTCCandlestickBars(ArrayList<Candlestick> candlesticks) {
        if (ruleMap == null) {
            getPatters();
        }
        ruleEvaluator.evaluate(candlesticks, this.ruleMap.get("BTC/USDT"));
    }

    @KafkaListener(topics = ETH_TOPIC_NAME, groupId = "eth")
    public void listenToETHCandlestickBars(ArrayList<Candlestick> candlesticks) {
        if (ruleMap == null) {
            getPatters();
        }
        ruleEvaluator.evaluate(candlesticks, this.ruleMap.get("ETH/USDT"));
    }

    private void getPatters() {
        synchronized (CryptoReceiverService.class) {
            if (ruleMap == null) {
                FileManager fileManager = new FileManager();
                String pattersJson = fileManager.read(Path.of(patternPath));
                List<Rule> rules = convertJsonToRule(pattersJson);
                this.ruleMap = getRuleMap(rules);
            }
        }
    }

    private Map<String, List<Rule>> getRuleMap(List<Rule> rules) {
        Map<String, List<Rule>> ruleMap = new HashMap<>();
        for(Rule rule: rules) {
            if (ruleMap.containsKey(rule.getMarketName())) {
                ruleMap.get(rule.getMarketName()).add(rule);
            } else {
                ruleMap.put(rule.getMarketName(), List.of(rule));
            }
        }
        return Collections.unmodifiableMap(ruleMap);
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
