package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.base;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.AbstractDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * The Class ErrorResponseDTO.
 *
 * @author vincenzoingenito
 * 
 * 	Error response.
 */
@Data
public class ErrorResponseDTO implements AbstractDTO {



	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = 3533528093076044614L;

	
	/**
	 * Trace id log.
	 */
	@Schema(description = "Indentificativo univoco della richiesta dell'utente")
	@Size(min = 0, max = 100)
	private String traceID;
	
	/**
	 * Span id log.
	 */
	@Schema(description = "Indentificativo univoco di un task della richiesta dell'utente (differisce dal traceID solo in caso di chiamate sincrone in cascata)")
	@Size(min = 0, max = 100)
	private String spanID;

	/**
	 * Identifier of the problem 
	 */
	@Schema(description = "Identificativo del problema verificatosi")
	@Size(min = 0, max = 100)
	private String type;
	
	/**
	 * Error title 
	 */
	@Schema(description = "Sintesi del problema (invariante per occorrenze diverse dello stesso problema)")
	@Size(min = 0, max = 1000)
	private String title;

	/**
	 * Error description 
	 */
	@Schema(description = "Descrizione del problema")
	@Size(min = 0, max = 1000)
	private String detail;

	/**
	 * HTTP Status 
	 */
	@Schema(description = "Stato http")
	@Min(value = 100)
	@Max(value = 599)
	private Integer status;
	
	/**
	 * URI that carries further info about the problem 
	 */
	@Schema(description = "URI che potrebbe fornire ulteriori informazioni riguardo l'occorrenza del problema")
	@Size(min = 0, max = 100)
	private String instance;

	public ErrorResponseDTO(final LogTraceInfoDTO traceInfo, final String inType, final String inTitle, final String inDetail, final Integer inStatus, final String inInstance) {
		traceID = traceInfo.getTraceID();
		spanID = traceInfo.getSpanID();
		type = inType;
		title = inTitle;
		detail = inDetail;
		status = inStatus;
		instance = inInstance;
	}

	public ErrorResponseDTO(final LogTraceInfoDTO traceInfo) {
		traceID = traceInfo.getTraceID();
		spanID = traceInfo.getSpanID(); 
	}

}