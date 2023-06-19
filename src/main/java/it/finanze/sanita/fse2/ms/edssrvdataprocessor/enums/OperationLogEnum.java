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
	VALIDATE_NORMATIVE_R4("FHIR-VALIDATE-NORMATIVE-R4", "Validazione normative R4"),
	VALIDATE_RESOURCE_BUNDLE("FHIR-VALIDATE-RESOURCE-BUNDLE", "Validazione attraversabilità risorse bundle");

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

