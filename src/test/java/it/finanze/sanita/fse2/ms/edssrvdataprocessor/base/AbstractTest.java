package it.finanze.sanita.fse2.ms.edssrvdataprocessor.base;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl.DocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.EncryptDecryptUtility;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.Document;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Future;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest {
	
    @Autowired
    public DocumentRepo documentRepo;

    @Autowired
    private KafkaPropertiesCFG kafkaPropCFG;

    private TestProducer testProducer;
    protected AbstractTest() {}

    protected ConsumerRecord<String, String> kafkaInit(
            String topic,
            ProcessorOperationEnum operation,
            boolean encDocumentNotFound,
            boolean encError,
            boolean encEmptyMessage
    ) throws OperationException {
        DocumentReferenceETY ety = new DocumentReferenceETY();
        ety.setIdentifier(TestConstants.TEST_IDENTIFIER);
        ety.setOperation(operation);
        if (operation != ProcessorOperationEnum.DELETE) {
            ety.setDocument(Document.parse(TestConstants.TEST_JSON_STRING));
        }

        DocumentReferenceETY insertedEty = documentRepo.insert(ety);
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
            message = EncryptDecryptUtility.encrypt(kafkaPropCFG.getCrypto(), "mockMessage");
        } else {
            message = EncryptDecryptUtility.encrypt(kafkaPropCFG.getCrypto(), mongoId);
        }

        MockProducer mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        TestProducer testProducer = new TestProducer(mockProducer);
        Future<RecordMetadata> recordMetadataFuture = testProducer.send(
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
}
