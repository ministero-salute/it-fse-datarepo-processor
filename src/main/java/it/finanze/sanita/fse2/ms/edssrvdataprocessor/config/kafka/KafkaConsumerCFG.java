/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kafka Consumer Config 
 */
@Slf4j
@Configuration
public class KafkaConsumerCFG {

	/**
	 *	Kafka consumer properties.
	 */
	@Autowired
	private KafkaConsumerPropertiesCFG kafkaConsumerPropCFG;

	/**
	 * Kafka Topic Config 
	 */
	@Autowired
	private KafkaTopicCFG kafkaTopicCFG;

	/**
	 * Configurazione consumer.
	 * 
	 * @return Map 	Configurazione Consumer
	 */
	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConsumerPropCFG.getClientId());
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerPropCFG.getConsumerBootstrapServers());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerPropCFG.getConsumerGroupId());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerPropCFG.getConsumerKeyDeserializer());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerPropCFG.getConsumerValueDeserializer());
		props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, kafkaConsumerPropCFG.getIsolationLevel());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerPropCFG.getAutoCommit());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerPropCFG.getAutoOffsetReset());
		
		if(!StringUtility.isNullOrEmpty(kafkaConsumerPropCFG.getProtocol())) {
			props.put("security.protocol", kafkaConsumerPropCFG.getProtocol());
		}
		
		if(!StringUtility.isNullOrEmpty(kafkaConsumerPropCFG.getMechanism())) {
			props.put("sasl.mechanism", kafkaConsumerPropCFG.getMechanism());
		}
		
		if(!StringUtility.isNullOrEmpty(kafkaConsumerPropCFG.getConfigJaas())) {
			props.put("sasl.jaas.config", kafkaConsumerPropCFG.getConfigJaas());
		}
		
		if(!StringUtility.isNullOrEmpty(kafkaConsumerPropCFG.getTrustoreLocation())) {
			props.put("ssl.truststore.location", kafkaConsumerPropCFG.getTrustoreLocation());
		}
		
		if(!StringUtility.isNullOrEmpty(String.valueOf(kafkaConsumerPropCFG.getTrustorePassword()))) {
			props.put("ssl.truststore.password", String.valueOf(kafkaConsumerPropCFG.getTrustorePassword()));
		}
		return props;
	}


	/**
	 * Consumer factory.
	 * 
	 * @return	factory Factory 
	 */
	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}

	/**
	 * Factory with dead letter configuration.
	 * 
	 * @param deadLetterKafkaTemplate  Dead Letter Config 
	 * @return	factory  Factory
	 */
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerDeadLetterContainerFactory(final @Qualifier("notxkafkadeadtemplate") KafkaTemplate<Object, Object> deadLetterKafkaTemplate) {

		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.getContainerProperties().setDeliveryAttemptHeader(true);
		
		// Definizione nome topic deadLetter
		log.info("TOPIC: " + kafkaTopicCFG.getIngestorPublishDeadLetterTopic());
		DeadLetterPublishingRecoverer dlpr = new DeadLetterPublishingRecoverer(deadLetterKafkaTemplate, (consumerRecord, ex) -> new TopicPartition(kafkaTopicCFG.getIngestorPublishDeadLetterTopic(), -1));
		
		// Set classificazione errori da gestire per la deadLetter.
		DefaultErrorHandler sceh = new DefaultErrorHandler(dlpr, new FixedBackOff(FixedBackOff.DEFAULT_INTERVAL, FixedBackOff.UNLIMITED_ATTEMPTS));
		
		log.info("setClassification - kafkaListenerDeadLetterContainerFactory: ");
		setClassification(sceh);
		
		// da eliminare se non si volesse gestire la dead letter
		factory.setCommonErrorHandler(sceh); 

		return factory;
	}
	
	private void setClassification(final DefaultErrorHandler sceh) {
		List<Class<? extends Exception>> out = getExceptionsConfig();

		for (Class<? extends Exception> ex : out) {
			log.info("addNotRetryableException: " + ex);
			sceh.addNotRetryableExceptions(ex);
		}
		
	}

	/**
	 * Get Exceptions Config 
	 * 
	 * @return List	 Exceptions List
	 */
	@SuppressWarnings("unchecked")
	private List<Class<? extends Exception>> getExceptionsConfig() {
		List<Class<? extends Exception>> out = new ArrayList<>();
		String temp = null;
		try {
			for (String excs : kafkaConsumerPropCFG.getDeadLetterExceptions()) {
				temp = excs;
				Class<? extends Exception> s = (Class<? extends Exception>) Class.forName(excs, false, Thread.currentThread().getContextClassLoader());
				out.add(s);
			}
		} catch (Exception e) {
			log.error("Error retrieving the exception with fully qualified name: <{}>", temp);
			log.error("Error : ", e);
		}
		
		return out;
	}
	
	/**
	 * Default Container factory.
	 * @return KafkaListenerContainerFactory factory
	 */
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}
