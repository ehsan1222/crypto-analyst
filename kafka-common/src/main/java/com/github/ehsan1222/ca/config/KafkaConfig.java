package com.github.ehsan1222.ca.config;

import com.github.ehsan1222.ca.constants.KafkaConstants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

import static com.github.ehsan1222.ca.constants.KafkaConstants.*;

@Configuration
public class KafkaConfig {

    @Value("${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaAdmin.NewTopics topics() {
        NewTopic btcTopic = new NewTopic(BTC_TOPIC_NAME, NUM_PARTITIONS, REPLICATION_FACTORY);
        NewTopic ethTopic = new NewTopic(ETH_TOPIC_NAME, NUM_PARTITIONS, REPLICATION_FACTORY);
        return new KafkaAdmin.NewTopics(btcTopic, ethTopic);
    }

}
