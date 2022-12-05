/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import lombok.Data;

/**
 * A DTO representing the result of the process operation 
 */
@Data
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