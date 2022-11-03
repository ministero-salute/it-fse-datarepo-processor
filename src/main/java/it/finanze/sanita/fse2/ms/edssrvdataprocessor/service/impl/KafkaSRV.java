package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaConsumerPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IOrchestratorSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.EncryptDecryptUtility;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.HelperUtility;
import lombok.extern.slf4j.Slf4j;

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
	 
	/**
	 * Kafka Consumer Proeprties 
	 */
	@Autowired
	private transient KafkaConsumerPropertiesCFG kafkaConsumerPropertiesCFG;

	/**
	 * Orchestrator Service 
	 */
	@Autowired
	private transient IOrchestratorSRV orchestratorSRV;

	/**
	 * Kafka Properties 
	 */
	@Autowired
	private transient KafkaPropertiesCFG kafkaPropCFG;
	
	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.low-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.low}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void lowPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		genericListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.medium-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.medium}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void mediumPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		genericListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.high-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.high}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void highPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		genericListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.dataprocessor.generic.topic}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.replace}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void genericListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException {
		genericListener(cr);
	}

	private void genericListener(final ConsumerRecord<String, String> cr) throws DocumentNotFoundException, EmptyIdentifierException {
		log.info("[EDS] Consuming ingestor Event - Message received from topic {} with key {}", cr.topic(), cr.key());

		boolean esito = false;
		int counter = 0;

		while(Boolean.FALSE.equals(esito) && counter<=kafkaConsumerPropertiesCFG.getNRetry()) {
			try {
				if (StringUtils.hasText(cr.value())) {
					String mongoId = EncryptDecryptUtility.decrypt(kafkaPropCFG.getCrypto(), cr.value());
					DispatchActionDTO dispatchActionDTO = DispatchActionDTO.builder()
							.mongoId(mongoId)
							.documentReferenceDTO(null)
							.build();
					orchestratorSRV.dispatchAction(ProcessorOperationEnum.valueOf(cr.key()), dispatchActionDTO);
					esito = true;
				} else {
					log.warn(Constants.Logs.ERROR_EMPTY_IDENTIFIER + " | key: {}", cr.key());
					throw new EmptyIdentifierException("Error: empty message on topic " + cr.topic()); 
				}
			} catch (Exception e) {
				HelperUtility.deadLetterHelper(e);
				if(kafkaConsumerPropertiesCFG.getDeadLetterExceptions().contains(e.getClass().getName())) {
					log.error("Dead letter exception - exiting...");
					throw e;
				} else if(kafkaConsumerPropertiesCFG.getTemporaryExceptions().contains(e.getClass().getName())) {
					log.error("Temporary exception - exiting...");
				} else {
					counter++;
					if(counter==kafkaConsumerPropertiesCFG.getNRetry()) {
						throw e;
					}
				}
			}

		}
	}

}
