package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.ComponentScan.CONFIG_MONGO;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.ComponentScan.REPOSITORY;
import static org.junit.jupiter.api.TestInstance.Lifecycle.*;

@DataMongoTest
@ComponentScans( value = {
    @ComponentScan(CONFIG_MONGO),
    @ComponentScan(REPOSITORY),
})
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
            repository.insert(TransactionStatusETY.from("WIF - #" + i, ProcessorOperationEnum.REPLACE));
        }
    }

    @AfterAll
    void teardown() {
        mongo.dropCollection(TransactionStatusETY.class);
    }
}
