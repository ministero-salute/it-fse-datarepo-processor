package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MAX_SIZE;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MIN_SIZE;

import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class DocumentResponseDTO extends ResponseDTO { 

	/**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = 21641554325694264L; 
	
	
	@Size(min = DEFAULT_STRING_MIN_SIZE, max = DEFAULT_STRING_MAX_SIZE)
	private String transactionId; 
	
	private Boolean result; 
	

	public DocumentResponseDTO() {
		super();
	}

	public DocumentResponseDTO(final LogTraceInfoDTO traceInfo, final String inTransactionId, Boolean _result) {
		super(traceInfo);
		transactionId = inTransactionId; 
		result = _result; 
	}
	
}