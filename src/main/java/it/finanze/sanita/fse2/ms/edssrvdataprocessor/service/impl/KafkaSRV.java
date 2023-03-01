/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaConsumerPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.*;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IOrchestratorSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.KafkaAbstractSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Kafka management service.
 */
@Service
@Slf4j
public class KafkaSRV extends KafkaAbstractSRV implements IKafkaSRV {

	/**
	 * Kafka Consumer Proeprties 
	 */
	@Autowired
	private KafkaConsumerPropertiesCFG kafkaConsumerPropertiesCFG;

	/**
	 * Orchestrator Service 
	 */
	@Autowired
	private IOrchestratorSRV orchestratorSRV;
	
	
	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.low-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.low}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void lowPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws NoRecordFoundException, EmptyIdentifierException, OperationException {
		genericListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.medium-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.medium}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void mediumPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws NoRecordFoundException, EmptyIdentifierException, OperationException {
		genericListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.high-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.high}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void highPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws NoRecordFoundException, EmptyIdentifierException, OperationException {
		genericListener(cr);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.dataprocessor.generic.topic}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.replace}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void genericListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws NoRecordFoundException, EmptyIdentifierException, OperationException {
		genericListener(cr);
	}

	private void genericListener(final ConsumerRecord<String, String> cr) throws NoRecordFoundException, EmptyIdentifierException, OperationException {
		log.info("[EDS] Consuming ingestor Event - Message received from topic {} with key {}", cr.topic(), cr.key());

		boolean esito = false;
		int counter = 0;

		String wif = Constants.App.MISSING_WORKFLOW_PLACEHOLDER;

		while(Boolean.FALSE.equals(esito) && counter<=kafkaConsumerPropertiesCFG.getNRetry()) {
			try {
				if (StringUtils.hasText(cr.value())) {
					String mongoId = cr.value();
					wif = orchestratorSRV.getWorkflowInstanceId(mongoId);
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
			} catch (UATMockException e) {
				sendStatusMessage(wif, EventTypeEnum.EDS_WORKFLOW, EventStatusEnum.SUCCESS, null);
			} catch (Exception e) {
				HelperUtility.deadLetterHelper(e);
				if(kafkaConsumerPropertiesCFG.getDeadLetterExceptions().contains(e.getClass().getName())) {
					log.error("Dead letter exception - exiting...");
					sendStatusMessage(wif, EventTypeEnum.EDS_WORKFLOW, EventStatusEnum.BLOCKING_ERROR, e.getMessage());
					throw e;
				} else if(kafkaConsumerPropertiesCFG.getTemporaryExceptions().contains(ExceptionUtils.getRootCause(e).getClass().getCanonicalName())) {
					log.error("Temporary exception - exiting...");
					sendStatusMessage(wif, EventTypeEnum.EDS_WORKFLOW, EventStatusEnum.NON_BLOCKING_ERROR, e.getMessage());
					throw e;
				} else {
					counter++;
					if(counter==kafkaConsumerPropertiesCFG.getNRetry()) {
						sendStatusMessage(wif, EventTypeEnum.EDS_WORKFLOW, EventStatusEnum.BLOCKING_ERROR_MAX_RETRY, e.getMessage());
						throw new BlockingException("Numero di retry massimo raggiunto :" , e);
					}
				}
			}

		}
	}

}
