package com.bank.producer;

import com.bank.model.KafkaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    
    @Bean
    public NewTopic transferTopic() {
        return TopicBuilder.name(KafkaConstants.TRANSFER_TOPIC)
                .partitions(KafkaConstants.PARTITIONS_COUNT)
                .replicas(KafkaConstants.REPLICATION_FACTOR)
                .config("min.insync.replicas", "2")
                .config("retention.ms", "300000")
                .config("cleanup.policy", "delete")
                .build();
    }
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
                "kafka1:9092,kafka2:9092,kafka3:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
                StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
                JsonSerializer.class);
        
        // Exactly-once configuration
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transfer-producer-1");
        
        DefaultKafkaProducerFactory<String, Object> factory = 
                new DefaultKafkaProducerFactory<>(configProps);
        
        return factory;
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}