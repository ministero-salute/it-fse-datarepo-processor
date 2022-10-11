package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;

import java.util.List;

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
	 * Returns all the documents from the staging database.
	 * 
	 * @param identifier The identifier of the document to delete
	 */
	void deleteByIdentifier(String identifier); 
	
	
	/**
	 * Returns a document from the staging database given its identifier. 
	 * 
	 * @param id  The Mongo ID of the document 
	 * @return DocumentReferenceETY  The entity having the given id 
	 */
	DocumentReferenceETY findById(String id); 

	
	/**
	 * Returns all the documents from the staging database.
	 * 
	 * @return ArrayList  The list of all documents 
	 */
	public List<DocumentReferenceETY> findAll();
}
