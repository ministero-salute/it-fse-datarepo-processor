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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO representing the result of the process operation 
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentResponseDTO extends ResponseDTO { 

	
	/**
	 * A boolean representing the result of the operation 
	 */
	private Boolean result; 
	

	/**
	 * Constructor 
	 */
	public DocumentResponseDTO() {
		super();
	}

	public DocumentResponseDTO(final LogTraceInfoDTO traceInfo, Boolean inResult) {
		super(traceInfo);
		result = inResult; 
	}
	
}