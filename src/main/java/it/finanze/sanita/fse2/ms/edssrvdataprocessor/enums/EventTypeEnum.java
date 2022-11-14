/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

public enum EventTypeEnum {

	EDS_PROCESSOR("EDS_PROCESSOR"),
	VALIDAZIONE_NORMATIVE_R4("VALIDAZIONE_NORMATIVE_R4");

	private final String name;

	EventTypeEnum(String inName) {
		name = inName;
	}

	public String getName() {
		return name;
	}

}