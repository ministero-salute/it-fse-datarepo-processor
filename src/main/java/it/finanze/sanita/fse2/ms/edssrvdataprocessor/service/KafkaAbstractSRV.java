/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility.toJSONJackson;

import java.util.Date;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaProducerPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.KafkaStatusManagerDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public abstract class KafkaAbstractSRV {
	
	/**
	 * Transactional producer.
	 */
	@Autowired
	@Qualifier("txkafkatemplate")
	protected KafkaTemplate<String, String> txKafkaTemplate;

	/**
	 * Not transactional producer.
	 */
	@Autowired
	@Qualifier("notxkafkatemplate")
	protected KafkaTemplate<String, String> notxKafkaTemplate;

	@Autowired
	protected KafkaTopicCFG kafkaTopicCFG;
	
	@Value("${spring.application.name}")
	private String msName;
	
	@Autowired
	private KafkaProducerPropertiesCFG kafkaProducerCFG;

	public void sendStatusMessage(String workflowInstanceId,EventTypeEnum eventType,EventStatusEnum eventStatus,String message) {
		try {
			KafkaStatusManagerDTO statusManagerMessage = KafkaStatusManagerDTO.builder().
				eventType(eventType).
				eventDate(new Date()).
				eventStatus(eventStatus).
				message(message).
				microserviceName(msName).
				build();
			String json = toJSONJackson(statusManagerMessage);
			sendMessage(kafkaTopicCFG.getStatusManagerTopic(), workflowInstanceId, json);
		} catch(Exception ex) {
			log.error("Error while send status message on indexer : " , ex);
			throw new BusinessException(ex);
		}
	}

	public RecordMetadata sendMessage(String topic, String key, String value) {
		RecordMetadata out;
		ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
		try {
			out = kafkaSend(producerRecord);
		} catch (Exception e) {
			log.error("Send failed.", e);
			throw new BusinessException(e);
		}
		return out;
	}

	private RecordMetadata kafkaSend(ProducerRecord<String, String> producerRecord) {
		RecordMetadata out = null;
		SendResult<String, String> result = null;

		boolean trans = !StringUtility.isNullOrEmpty(kafkaProducerCFG.getTransactionalId());
		if (trans) {
			result = txKafkaTemplate.executeInTransaction(t -> {
				try {
					return t.send(producerRecord).get();
				} catch (InterruptedException e) {
					log.error("InterruptedException caught. Interrupting thread...");
					Thread.currentThread().interrupt();
					throw new BusinessException(e);
				} catch (Exception e) {
					throw new BusinessException(e);
				}
			});
		} else {
			notxKafkaTemplate.send(producerRecord);
		}

		if (result != null) {
			out = result.getRecordMetadata();
			log.debug("Kafka message sent successfully.");
		}

		return out;
	}
}
