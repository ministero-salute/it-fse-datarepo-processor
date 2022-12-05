/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.ITransactionRepo;

@DataMongoTest
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(PER_CLASS)
class TransactionRepoTest {

    @MockBean
    private Tracer tracer;

    @Autowired
    private MongoTemplate mongo;

    @Autowired
    private ITransactionRepo repository;

    @BeforeAll
    void setup() {
        mongo.dropCollection(TransactionStatusETY.class);
    }

    @Test
    void init() throws OperationException {
        for (int i = 0; i < 100; ++i) {
            repository.insert(TransactionStatusETY.from("WIF - #" + i, ProcessorOperationEnum.PUBLISH));
        }
    }

    @AfterAll
    void teardown() {
        mongo.dropCollection(TransactionStatusETY.class);
    }
}
