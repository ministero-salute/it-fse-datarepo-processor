/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

/**
 * Enum that represents the operation in Structured Logs 
 *
 */
@Getter
public enum OperationLogEnum implements ILogEnum {

	KAFKA_SENDING_MESSAGE("KAFKA-SENDING-MESSAGE", "Invio Messaggio su Kafka"),
	KAFKA_RECEIVING_MESSAGE("KAFKA-RECEIVING-MESSAGE", "Ricezione Messaggio da Kafka"),
	FHIR_PUBLISH("FHIR-PUBLISH", "Pubblicazione su server FHIR"),
	FHIR_UPDATE("FHIR-UPDATE", "Update su server FHIR"),
	FHIR_REPLACE("FHIR-REPLACE", "Replace su server FHIR"),
	FHIR_DELETE("FHIR-DELETE", "Delete su server FHIR"),
	VALIDATE_NORMATIVE_R4("FHIR-VALIDATE-NORMATIVE-R4", "Validazione normative R4");

	/**
	 * The operation code 
	 */
	private String code;

	/**
	 * The operation description 
	 */
	private String description;

	OperationLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}
}

