/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;

import java.io.Serializable;

/**
 * Interface of Eds client.
 * 
 */
public interface IEdsQueryClient extends Serializable {
    /**
     * EDS SRV Query - check existence of identifier
     * 
     * @param masterIdentifier  The master identifier of the document
     * @throws DocumentAlreadyExistsException  An exception thrown when the document already exists on FHIR Server 
     */
//    void fhirCheckExist(String masterIdentifier) throws DocumentAlreadyExistsException;
    ResourceExistResDTO fhirCheckExist(String masterIdentifier) throws DocumentAlreadyExistsException;
    /**
     * Delete resource on FHIR server by masterIdentifier
     * 
     * @param masterIdentifier  The master identifier of the document 
     */
    void fhirDelete(String masterIdentifier);

    /**
     * This method can cover all use cases for publication/replace/update on FHIR server
     * 
     * @param masterIdentifier  The master identifier of the document 
     * @param jsonString  The Json String of the document 
     * @param processorOperationEnum  The Enum that describes the operation to execute 
     */
    void fhirPublication(String masterIdentifier, String jsonString, ProcessorOperationEnum processorOperationEnum);
}
