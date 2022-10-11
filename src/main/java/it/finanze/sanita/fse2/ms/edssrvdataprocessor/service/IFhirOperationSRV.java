package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;

/**
 * Interface for FHIR Operation Service 
 *
 */
public interface IFhirOperationSRV {
    /**
     * Publish document on FHIR server
     * @param fhirOperationDTO  A DTO representing the document to insert  
     */
    void publish(FhirOperationDTO fhirOperationDTO);

    /**
     * Update document on FHIR server
     * @param masterIdentifier  The master identifier of the document 
     * @param jsonString  The Json string of the document 
     */
    void update(String masterIdentifier, String jsonString);

    /**
     * Delete document on FHIR server
     * 
     * @param masterIdentifier  The master identifier of the document 
     */
    void delete(String masterIdentifier);

    /**
     * Replace document on FHIR server
     * 
     * @param fhirOperationDTO  A DTO representing the document to replace
     */
    void replace(FhirOperationDTO fhirOperationDTO);
}
