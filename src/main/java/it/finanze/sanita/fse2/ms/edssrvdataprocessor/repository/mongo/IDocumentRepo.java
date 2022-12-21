/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
