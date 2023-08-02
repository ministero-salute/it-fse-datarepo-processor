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

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.IngestionStagingETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl.DocumentRepo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest {
	
    @Autowired
    public DocumentRepo documentRepo;

    @SpyBean
    protected MongoTemplate mongoTemplate;

    @Autowired
    ServletWebServerApplicationContext webServerAppCtxt;

    protected AbstractTest() {}

    @BeforeEach
    void clearDB() {
        mongoTemplate.dropCollection(IngestionStagingETY.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected ConsumerRecord<String, String> kafkaInit(
            String topic,
            ProcessorOperationEnum operation,
            boolean encDocumentNotFound,
            boolean encError,
            boolean encEmptyMessage
    ) throws OperationException {
        IngestionStagingETY ety = new IngestionStagingETY();
        ety.setIdentifier(TestConstants.TEST_IDENTIFIER);
        ety.setOperation(operation);
        if (operation != ProcessorOperationEnum.DELETE) {
            ety.setDocument(Document.parse(TestConstants.TEST_JSON_STRING));
        }

        IngestionStagingETY insertedEty = documentRepo.insert(ety);
        String mongoId = insertedEty.getId();

        // Send message to Kafka with ID TEST_IDENTIFIER
        String message = null;

        if (encError) {
            // will fail to decrypt
            message = "mockMessage";
        } else if (encEmptyMessage) {
            message = "";
        } else if (encDocumentNotFound) {
            // will be decrypted but document will not be found
            message = "mockMessage";
        } else {
            message = mongoId;
        }

        MockProducer mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        TestProducer testProducer = new TestProducer(mockProducer);
        testProducer.send(
                topic,
                operation.getName(),
                message
        );

        return new ConsumerRecord<>(
                topic,
                1,
                0,
                operation.getName(),
                message
        );
    }

    public String getBaseUrl() {
        return "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + webServerAppCtxt.getServletContext().getContextPath();
    }
}
