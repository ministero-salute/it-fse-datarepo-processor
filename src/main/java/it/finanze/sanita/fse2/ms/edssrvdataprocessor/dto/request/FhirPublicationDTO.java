package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MAX_SIZE;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ValidationUtility.DEFAULT_STRING_MIN_SIZE;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FhirPublicationDTO {

	/**
	 * This class can cover all use cases for publication/replace/update
	 */

	@Size(min = DEFAULT_STRING_MIN_SIZE, max = DEFAULT_STRING_MAX_SIZE)
	private String identifier; 
	
	@Size(min = DEFAULT_STRING_MIN_SIZE, max = DEFAULT_STRING_MAX_SIZE)
	private String jsonString;
}