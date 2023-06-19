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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto;


import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MAX_SIZE;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MIN_SIZE;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.PriorityTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document Reference DTO 
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentReferenceDTO {

	/**
	 * The master identifier of the document 
	 */
	@JsonProperty("identifier")
	@Size(min = DEFAULT_STRING_MIN_SIZE, max = DEFAULT_STRING_MAX_SIZE)
	private String identifier; 
	
	/**
	 * An enum representing the operation to be performed 
	 */
	@JsonProperty("operation")
	@Size(min = DEFAULT_STRING_MIN_SIZE, max = DEFAULT_STRING_MAX_SIZE)
	private ProcessorOperationEnum operation;    

	/**
	 * The JSON String of the document 
	 */
	@JsonProperty(value = "jsonString", required = false)
	@Size(min = DEFAULT_STRING_MIN_SIZE, max = DEFAULT_STRING_MAX_SIZE)
	private String jsonString;

	/** 
	 * An Enum representing the priority of the operation (HIGH. MEDIUM or LOW)
	 */
	@JsonProperty(value = "priorityType", required = false)
	private PriorityTypeEnum priorityTypeEnum; 
	
}