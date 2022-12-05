/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client;


import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;

/**
 * Interface of Eds client.
 */
public interface IEdsDataQualityClient {
	
    /**
     * Send input to data quality srv to be validated
     * 
     * @param input  The DTO to validate 
     * @return ValidationResultDTO  A DTO representing the result of the validation 
     */
    ValidationResultDTO validateBundleNormativeR4(FhirOperationDTO input);
}
