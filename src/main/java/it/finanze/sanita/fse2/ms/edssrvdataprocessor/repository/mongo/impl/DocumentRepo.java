/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERROR_MONGO_FIND_BY_ID;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERROR_MONGO_INSERT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoException;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.IDocumentRepo;


/**
 * The document repository implementation
 */
@Repository
public class DocumentRepo implements IDocumentRepo {

	/**
	 * Mongo Template 
	 */
	@Autowired
	private MongoTemplate mongo;
	
	@Override
	public DocumentReferenceETY insert(DocumentReferenceETY entity) throws OperationException {
		DocumentReferenceETY out;
		try {
			 out = mongo.insert(entity);
		} catch(MongoException ex) {
			throw new OperationException(ERROR_MONGO_INSERT, ex);
		}
		return out;
	} 

	@Override
	public DocumentReferenceETY findById(String id) throws OperationException {
		DocumentReferenceETY out;
		try {
			out = mongo.findById(id, DocumentReferenceETY.class);
		}catch(MongoException ex) {
			throw new OperationException(ERROR_MONGO_FIND_BY_ID, ex);
		}
		return out;
	}
}
