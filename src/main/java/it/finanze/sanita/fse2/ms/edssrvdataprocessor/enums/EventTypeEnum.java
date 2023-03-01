/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

public enum EventTypeEnum {

	EDS_WORKFLOW("EDS_WORKFLOW"),
	DESERIALIZE("DESERIALIZE");

	@Getter
	private final String name;

	EventTypeEnum(String inName) {
		name = inName;
	}

}