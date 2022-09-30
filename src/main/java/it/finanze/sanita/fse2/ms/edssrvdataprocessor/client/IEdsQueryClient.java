package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;

import java.io.Serializable;

/**
 * Interface of Eds client.
 * 
 * @author Riccardo Bonesi
 */
public interface IEdsQueryClient extends Serializable {
    /**
     * EDS SRV Query - check existence of identifier
     * @param id
     * @return
     */
    void fhirCheckExist(String masterIdentifier) throws DocumentAlreadyExistsException;

    /**
     * Delete resource on FHIR server by masterIdentifier
     * @param masterIdentifier
     */
    void fhirDelete(String masterIdentifier);

    /**
     * This method can cover all use cases for publication/replace/update on FHIR server
     * @param masterIdentifier
     * @param jsonString
     */
    void fhirPublication(String masterIdentifier, String jsonString, ProcessorOperationEnum processorOperationEnum);
}
