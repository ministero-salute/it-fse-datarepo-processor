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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.base.MultiClientCallback;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.FhirAdvicesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaConsumerPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BlockingException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.UATMockException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IOrchestratorSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.KafkaAbstractSRV;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.App.MISSING_WORKFLOW_PLACEHOLDER;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum.BLOCKING_ERROR;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum.BLOCKING_ERROR_MAX_RETRY;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventTypeEnum.DESERIALIZE;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventTypeEnum.EDS_WORKFLOW;

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
	
	@Autowired
	private FhirAdvicesCFG advices;
	
	
	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.low-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.low}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void lowPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws Exception {
		loop(cr, this::dispatchAction, advices.map(), delivery);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.medium-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.medium}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void mediumPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws Exception {
		loop(cr, this::dispatchAction, advices.map(), delivery);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.ingestor-publish.topic.high-priority}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.high}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void highPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws Exception {
		loop(cr, this::dispatchAction, advices.map(), delivery);
	}

	@Override
	@KafkaListener(topics = "#{'${kafka.dataprocessor.generic.topic}'}", clientIdPrefix = "#{'${kafka.consumer.client-id.replace}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id-publish}'}")
	public void genericListenerPublishIngestor(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws Exception {
		loop(cr, this::dispatchAction, advices.map(), delivery);
	}

	private void dispatchAction(String id, String action) throws OperationException, NoRecordFoundException {
		DispatchActionDTO dispatchActionDTO = DispatchActionDTO.builder().mongoId(id).documentReferenceDTO(null).build();
		orchestratorSRV.dispatchAction(ProcessorOperationEnum.valueOf(action), dispatchActionDTO);
	}

	private void loop(ConsumerRecord<String, String> cr, MultiClientCallback<String> cb, Function<Exception, String> extra, int delivery) throws Exception {

		// ====================
		// Deserialize request
		// ====================
		// Retrieve request body
		String id = cr.value();
		String action = cr.key();
		String wif = MISSING_WORKFLOW_PLACEHOLDER;

		boolean exit = false;
		// Convert request
		try {
			// Require id and action not null
			Objects.requireNonNull(id, "The id value cannot be null");
			Objects.requireNonNull(action, "The action value cannot be null");
			// Require id and action not empty
			if(id.isEmpty()) throw new IllegalArgumentException("The id value cannot be empty");
			if(action.isEmpty()) throw new IllegalArgumentException("The action value cannot be empty");
			// Retrieve wif
			wif = orchestratorSRV.getWorkflowInstanceId(id);
			// Require wif not null and not empty
			Objects.requireNonNull(wif, "The wif value cannot be null");
			if(wif.isEmpty()) throw new IllegalArgumentException("The wif value cannot be empty");
		} catch (Exception e) {
			log.error("Unable to deserialize request with wif {}, id {} and action {} due to: {}", wif, id, action, e.getMessage());
			sendStatusMessage(id, DESERIALIZE, BLOCKING_ERROR, action, null);
			throw new BlockingException(e.getMessage());
		}

		// ====================
		// Retry iterations
		// ====================
		Exception ex = new Exception("Errore generico durante l'invocazione del client di eds-workflow");
		// Iterate
		for (int i = 0; i <= kafkaConsumerPropertiesCFG.getNRetry() && !exit; ++i) {
			try {
				// Execute request
				cb.request(id, action);
				// Quit flag
				exit = true;
			} catch (UATMockException e) {
				sendStatusMessage(wif, EDS_WORKFLOW, e.getStatus(), e.getMessage(), null);
				// Quit flag
				exit = true;
			} catch (Exception e) {
				// Assign
				ex = e;
				// Display help
				kafkaConsumerPropertiesCFG.deadLetterHelper(e);
				// Try to identify the exception type
				Optional<EventStatusEnum> type = kafkaConsumerPropertiesCFG.asExceptionType(e);
				// If we found it, we are good to make an action, otherwise, let's retry
				if(type.isPresent()) {
					// Get type [BLOCKING or NON_BLOCKING_ERROR]
					EventStatusEnum status = type.get();
					// Send to kafka
					if (delivery <= KafkaConsumerPropertiesCFG.MAX_ATTEMPT) {
						String details = extra == null ? null : extra.apply(e);
						// Send to kafka
						sendStatusMessage(wif, EDS_WORKFLOW, status, e.getMessage(), details);
					}
					// We are going re-process it
					throw e;
				}
			}
		}

		// We didn't exit properly from the loop,
		// We reached the max amount of retries
		if(!exit) {
			sendStatusMessage(wif, EDS_WORKFLOW, BLOCKING_ERROR_MAX_RETRY, "Massimo numero di retry raggiunto: " + ex.getMessage(), null);
			throw new BlockingException(ex.getMessage());
		}

	}

}
