/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;

/**
 * Interface of Eds client.
 */
public interface IEdsQueryClient {
	
    /**
     * EDS SRV Query - check existence of identifier
     * 
     * @param masterIdentifier  The master identifier of the document
     * @throws DocumentAlreadyExistsException  An exception thrown when the document already exists on FHIR Server 
     */
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
