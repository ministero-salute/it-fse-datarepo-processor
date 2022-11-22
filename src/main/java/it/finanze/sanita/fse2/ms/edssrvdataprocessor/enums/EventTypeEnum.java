/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

public enum EventTypeEnum {

	EDS_WORKFLOW("EDS_WORKFLOW");

	private final String name;

	EventTypeEnum(String inName) {
		name = inName;
	}

	public String getName() {
		return name;
	}

}