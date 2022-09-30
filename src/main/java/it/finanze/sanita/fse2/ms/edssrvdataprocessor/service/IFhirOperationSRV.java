package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;

public interface IFhirOperationSRV {
    /**
     * Publish document on FHIR server
     * @param masterIdentifier
     * @return
     */
    void publish(FhirOperationDTO fhirOperationDTO);

    /**
     * Update document on FHIR server
     * @param masterIdentifier
     * @return
     */
    void update(String masterIdentifier, String jsonString);

    /**
     * Delete document on FHIR server
     * @param masterIdentifier
     * @return
     */
    void delete(String masterIdentifier);

    /**
     * Replace document on FHIR server
     * @param masterIdentifier
     * @return
     */
    void replace(FhirOperationDTO fhirOperationDTO);
}
