package com.bank.model;

public class KafkaConstants {
    public static final String TRANSFER_TOPIC = "transfer-topic";
    public static final int PARTITIONS_COUNT = 3;
    public static final short REPLICATION_FACTOR = 3;
    public static final String CONSUMER_GROUP_ID = "transfer-consumer-group";
}