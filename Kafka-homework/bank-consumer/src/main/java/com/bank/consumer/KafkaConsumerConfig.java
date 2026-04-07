package com.bank.consumer;

import com.bank.model.KafkaConstants;
import com.bank.dto.TransferMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    
    @Bean
    public ConsumerFactory<String, TransferMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
                "kafka1:9092,kafka2:9092,kafka3:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.CONSUMER_GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
                JsonDeserializer.class);
        
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        
        JsonDeserializer<TransferMessage> deserializer = 
                new JsonDeserializer<>(TransferMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("com.bank.dto");
        deserializer.setUseTypeMapperForKey(true);
        
        return new DefaultKafkaConsumerFactory<>(
                props, 
                new StringDeserializer(), 
                deserializer
        );
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransferMessage> 
            kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransferMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(KafkaConstants.PARTITIONS_COUNT);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(
                ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        return factory;
    }
}