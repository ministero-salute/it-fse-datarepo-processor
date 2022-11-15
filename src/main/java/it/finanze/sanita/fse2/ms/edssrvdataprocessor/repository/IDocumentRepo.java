/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;

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
	DocumentReferenceETY insert(DocumentReferenceETY ety) throws OperationException;
	
	/**
	 * Returns a document from the staging database given its identifier. 
	 * 
	 * @param id  The Mongo ID of the document 
	 * @return DocumentReferenceETY  The entity having the given id 
	 */
	DocumentReferenceETY findById(String id) throws OperationException;

}
