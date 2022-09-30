package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.AbstractDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LogTraceInfoDTO implements AbstractDTO {


	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = 879506664182961031L;

	
	/**
	 * Span.
	 */
	private final String spanID;
	
	/**
	 * Trace.
	 */
	private final String traceID;

}
