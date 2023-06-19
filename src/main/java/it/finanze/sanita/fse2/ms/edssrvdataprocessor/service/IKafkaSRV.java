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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
 
/**
 * Kafka Service Interface 
 *
 */
public interface IKafkaSRV {
	/**
	 * Kafka listener for Publish Ingestor communications in low priority
	 * 
	 * @param cr  Consumer Record 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void lowPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, int delivery) throws Exception;

	/**
	 * Kafka listener for Publish Ingestor communications in medium priority
	 * 
	 * @param cr  Consumer Record 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void mediumPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, int delivery) throws Exception;

	/**
	 * Kafka listener for Publish Ingestor communications in high priority
	 * 
	 * @param cr  Consumer Record 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void highPriorityListenerPublishIngestor(ConsumerRecord<String, String> cr, int delivery) throws Exception;

	/**
	 * Kafka listener for Replace CDA Ingestor communications
	 * 
	 * @param cr  Consumer Record 
	 * @throws NoRecordFoundException  An exception thrown when the document has not been found on the FHIR Server
	 * @throws EmptyIdentifierException  An exception thrown when the document has an empty identifier 
	 */
	void genericListenerPublishIngestor(ConsumerRecord<String, String> cr, int delivery) throws Exception;
	
}
