/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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