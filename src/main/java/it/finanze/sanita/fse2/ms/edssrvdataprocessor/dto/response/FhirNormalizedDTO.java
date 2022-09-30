package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.AbstractDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FhirNormalizedDTO implements AbstractDTO {
	/**
	 * Id of the document
	 */
	private String masterIdentifier;
	/**
	 * Bundle of the document normalized
	 */
	private String jsonString;
	/**
	 * Flag to indicate if data has been normalized
	 */
	private boolean normalized;
}
