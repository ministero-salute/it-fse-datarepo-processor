package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaConsumerPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.logging.ElasticLoggerHelper;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IOrchestratorSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.EncryptDecryptUtility;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.HelperUtility;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 
 * @author vincenzoingenito
 *
 * Kafka management service.
 */
@Service
@Slf4j
public class KafkaSRV implements IKafkaSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 987723954716001270L;

	/**
	 * Transactional producer.
	 */
	@Autowired
	@Qualifier("txkafkatemplate")
	private transient KafkaTemplate<String, String> txKafkaTemplate;

	/**
	 * Not transactional producer.
	 */
	@Autowired
	@Qualifier("notxkafkatemplate")
	private transient KafkaTemplate<String, String> notxKafkaTemplate;
	
	@Autowired
	private transient KafkaTopicCFG kafkaTopicCFG;

	@Autowired
	private transient ElasticLoggerHelper elasticLogger;

	@Autowired
	private transient ProfileUtility profileUtility;

	@Autowired
	private transient KafkaConsumerPropertiesCFG kafkaConsumerPropertiesCFG;

	@Autowired
	private transient IOrchestratorSRV orchestratorSRV;

	@Autowired
	private transient KafkaPropertiesCFG kafkaPropCFG;

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.low-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.low}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void lowPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		this.abstractListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.medium-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.medium}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void mediumPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		this.abstractListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.high-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.high}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void highPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		this.abstractListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.dataprocessor.generic.topic}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.replace}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void genericListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		this.abstractListener(cr);
	}

	private void abstractListener(final ConsumerRecord<String, String> cr) throws DocumentNotFoundException, EmptyIdentifierException {
		log.info("[EDS] Consuming ingestor Event - Message received from topic {} with key {}", cr.topic(), cr.key());
		Date startDateOperation = new Date();
		try {
			if (StringUtils.hasText(cr.value())) {
				String mongoId = EncryptDecryptUtility.decrypt(kafkaPropCFG.getCrypto(), cr.value());
				DispatchActionDTO dispatchActionDTO = DispatchActionDTO.builder()
						.mongoId(mongoId)
						.documentReferenceDTO(null)
						.build();
				orchestratorSRV.dispatchAction(ProcessorOperationEnum.valueOf(cr.key()), dispatchActionDTO);
			} else {
				log.warn(Constants.Logs.ERROR_EMPTY_IDENTIFIER + " | key: {}", cr.key());
				elasticLogger.error(Constants.Logs.ERROR_EMPTY_IDENTIFIER + " | key: " + cr.key(), Constants.AppConstants.logMap.get(ProcessorOperationEnum.valueOf(cr.key())), ResultLogEnum.KO, startDateOperation, Constants.AppConstants.logErrorMap.get(ProcessorOperationEnum.valueOf(cr.key()))); 
				throw new EmptyIdentifierException("Error: empty message on topic " + cr.topic()); 
			}
		} catch (Exception e) {
			elasticLogger.error("Error operation to FHIR", OperationLogEnum.KAFKA_RECEIVING_MESSAGE, ResultLogEnum.KO, startDateOperation, Constants.AppConstants.logErrorMap.get(ProcessorOperationEnum.valueOf(cr.key())));
			HelperUtility.deadLetterHelper(e);
			if (!kafkaConsumerPropertiesCFG.getDeadLetterExceptions().contains(e.getClass().getName())) {
				log.error("Dead letter exception - exiting...");
			}
			throw e;
		}
	}
}
