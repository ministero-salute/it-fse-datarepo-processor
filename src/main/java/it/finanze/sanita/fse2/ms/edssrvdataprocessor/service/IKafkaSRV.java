/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.MessageHeaders;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
 
/**
 * Kafka Service Interface 
 *
 */
public interface IKafkaSRV {
	/**
	 * Kafka listener for Publish Ingestor communications in low priority
	 * 
	 * @param cr  Consumer Record 
	 * @param messageHeaders  Message Headers 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server  
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void lowPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws NoRecordFoundException, EmptyIdentifierException, OperationException;

	/**
	 * Kafka listener for Publish Ingestor communications in medium priority
	 * 
	 * @param cr  Consumer Record 
	 * @param messageHeaders  Message Headers 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server  
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void mediumPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws NoRecordFoundException, EmptyIdentifierException, OperationException;

	/**
	 * Kafka listener for Publish Ingestor communications in high priority
	 * 
	 * @param cr  Consumer Record 
	 * @param messageHeaders  Message Headers 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server  
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void highPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws NoRecordFoundException, EmptyIdentifierException, OperationException;

	/**
	 * Kafka listener for Replace CDA Ingestor communications
	 * 
	 * @param cr  Consumer Record 
	 * @param messageHeaders  Message Headers 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server  
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void genericListenerPublishIngestor(ConsumerRecord<String, String> cr, MessageHeaders messageHeaders) throws NoRecordFoundException, EmptyIdentifierException, OperationException;
	
}
