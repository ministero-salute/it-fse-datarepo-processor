package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FHIR Operation DTO 
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FhirOperationDTO {
	
	/**
	 * The Master Identifier of the document 
	 */
    private String masterIdentifier;
    
    /**
     * The JSON String of the document 
     */
    private String jsonString;
}
