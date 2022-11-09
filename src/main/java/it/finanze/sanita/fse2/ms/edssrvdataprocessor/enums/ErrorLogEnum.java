/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

/**
 * Enum that carries the error description in Structured Lsogs 
 *
 */
@Getter
public enum ErrorLogEnum implements ILogEnum {

	KO_KAFKA_SEND_MESSAGE("KO-KAFKA-SEND-MESSAGE", "Error while sending message on Kafka topic"),
	KO_KAFKA_RECEIVE_MESSAGE("KO-KAFKA-RECEIVE-MESSAGE", "Error while retrieving message from Kafka topic"),
	KO_FHIR_PUBLISH("KO_FHIR_PUBLISH", "Error while publishing on FHIR server"),
	KO_FHIR_REPLACE("KO_FHIR_REPLACE", "Error while replacing on FHIR server"),
	KO_FHIR_UPDATE("KO_FHIR_UPDATE", "Error while update on FHIR server"),
	KO_FHIR_DELETE("KO_FHIR_DELETE", "Error while delete on FHIR server");

	/**
	 * Error Code 
	 */
	private String code; 
	
	/**
	 * Error Description 
	 */
	private String description;

	ErrorLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}
}

