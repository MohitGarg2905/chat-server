package com.dante.chat.commons;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class KafkaProducerFactory {

    private static KafkaProducer<String, byte[]> producer;

    private KafkaProducerFactory(){}

    public static KafkaProducer<String, byte[]> getKafkaProducer(Properties properties) {
        if(producer==null)
            producer = new KafkaProducer<>(properties);
        return producer;
    }
}
