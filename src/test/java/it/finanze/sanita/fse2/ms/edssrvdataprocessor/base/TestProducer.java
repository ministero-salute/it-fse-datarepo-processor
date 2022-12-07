/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.base;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


public class TestProducer {

    
    
    private final Producer<String, String> producer;

    public TestProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Future<RecordMetadata> send(String topic, String key, String value) {
        ProducerRecord record = new ProducerRecord(topic, key, value);
        return producer.send(record);
    }
}