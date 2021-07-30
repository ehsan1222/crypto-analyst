package com.github.ehsan1222.ca.services;

import com.binance.api.client.domain.market.Candlestick;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoSenderService {

    private final KafkaTemplate<String, List<Candlestick>> kafkaTemplate;

    public CryptoSenderService(KafkaTemplate<String, List<Candlestick>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, List<Candlestick> data) {
        kafkaTemplate.send(topic, data);
    }

}
