/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

/**
 * Describes the operation to be executed 
 *
 */
@Getter
public enum ProcessorOperationEnum {

	PUBLISH("PUBLISH"),
	DELETE("DELETE"),
	REPLACE("REPLACE"),
	UPDATE("UPDATE"),
	READ("READ");

	/**
	 * The operation name 
	 */
	private String name;

	ProcessorOperationEnum(String pname) {
		name = pname;
	}
}