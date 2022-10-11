package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client;


import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.FhirNormalizedDTO;

import java.io.Serializable;

/**
 * Interface of Eds client.
 * 
 * @author Riccardo Bonesi
 */
public interface IEdsDataQualityClient extends Serializable {
    /**
     * Send input to data quality srv to be normalized
     * 
     * @param input  The DTO to normalize 
     * @return FhirNormalizedDTO  A DTO representing the result of the normalization 
     */
    FhirNormalizedDTO normalize(FhirOperationDTO input);
}
