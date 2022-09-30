package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.MessageHeaders;

import java.io.Serializable;
 

public interface IKafkaSRV extends Serializable {
	/**
	 * Kafka listener for Publish Ingestor communications in low priority
	 * @param cr
	 * @param messageHeaders
	 * @throws Exception 
	 * @throws EmptyIdentifierException 
	 */
	void lowPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException;

	/**
	 * Kafka listener for Publish Ingestor communications in medium priority
	 * @param cr
	 * @param messageHeaders
	 * @throws Exception 
	 * @throws EmptyIdentifierException 
	 */
	void mediumPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException;

	/**
	 * Kafka listener for Publish Ingestor communications in high priority
	 * @param cr
	 * @param messageHeaders
	 * @throws Exception 
	 * @throws EmptyIdentifierException 
	 */
	void highPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException;

	/**
	 * Kafka listener for Replace CDA Ingestor communications
	 * @param cr
	 * @param messageHeaders
	 * @throws Exception 
	 * @throws EmptyIdentifierException 
	 */
	void genericListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws DocumentNotFoundException, EmptyIdentifierException;
}
