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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.IngestionStagingETY;

/**
 * Documents Repo Interface
 */
public interface IDocumentRepo {

	/**
	 * Insert one or more documents on the staging database.
	 * 
	 * @param ety Document to insert.
	 * @return DocumentReferenceETY  The inserted Entity 
	 * @throws OperationException  A generic MongoDB Exception 
	 */
	IngestionStagingETY insert(IngestionStagingETY ety) throws OperationException;
	
	/**
	 * Returns a document from the staging database given its identifier. 
	 * 
	 * @param id  The Mongo ID of the document 
	 * @return DocumentReferenceETY  The entity having the given id 
	 */
	IngestionStagingETY findById(String id) throws OperationException;
	
	/**
	 * Returns a boolean if the record is deleted. 
	 * 
	 * @param wii  		 The WII of the document
	 * @param eventType  The event type of operation 
	 * @return boolean 
	 */
	boolean deleteById(String wii, ProcessorOperationEnum operation) throws OperationException;

}
