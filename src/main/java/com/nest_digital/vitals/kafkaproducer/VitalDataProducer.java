/*
 * Copyright (c) NeST Digital Pvt Ltd, 2023.
 * All Rights Reserved. Confidential.
 */
package com.nest_digital.vitals.kafkaproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * VitalDataProducer.java
 * A class that represents the Kafka Producer for sending vital sign data to topic.
 *
 * @author Aston-Martin
 * @since 25-01-2023
 */
@Service
public class VitalDataProducer {
    @Value("${topic.name}")
    public String vitalTopic;
    @Autowired
    private KafkaTemplate<String, Map<String,Object>> kafkaTemplate;

    /**
     * Sends vital sign data to Kafka topic.
     *
     * @param data - vital sign data to be sent.
     */
    public void sendVitalData(Map<String, Object> data) {
        kafkaTemplate.send(vitalTopic, data);
    }
}
