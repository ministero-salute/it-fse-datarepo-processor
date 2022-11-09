/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MAX_SIZE;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MIN_SIZE;

import javax.validation.constraints.Size;

import lombok.Data;

/**
 * A DTO representing the result of the process operation 
 * 
 *
 */
@Data
public class DocumentResponseDTO extends ResponseDTO { 

	/**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = 21641554325694264L; 
	
	/**
	 * Tx ID 
	 */
	@Size(min = DEFAULT_STRING_MIN_SIZE, max = DEFAULT_STRING_MAX_SIZE)
	private String transactionId; 
	
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

	public DocumentResponseDTO(final LogTraceInfoDTO traceInfo, final String inTransactionId, Boolean _result) {
		super(traceInfo);
		transactionId = inTransactionId; 
		result = _result; 
	}
	
}