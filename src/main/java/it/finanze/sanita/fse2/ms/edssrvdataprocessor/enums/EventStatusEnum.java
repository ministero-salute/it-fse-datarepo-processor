/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

public enum EventStatusEnum {

	SUCCESS("SUCCESS"),
	BLOCKING_ERROR("BLOCKING_ERROR"),
	NON_BLOCKING_ERROR("NON_BLOCKING_ERROR"),
	BLOCKING_ERROR_MAX_RETRY("BLOCKING_ERROR_MAX_RETRY");

	@Getter
	private final String name;

	EventStatusEnum(String inName) {
		name = inName;
	}

}