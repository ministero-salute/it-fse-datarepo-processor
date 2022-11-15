/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.IDocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The document repository implementation
 */
@Repository
public class DocumentRepo implements IDocumentRepo {

	/**
	 * Mongo Template 
	 */
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public DocumentReferenceETY insert(DocumentReferenceETY ety) throws OperationException {
		try {
			return mongoTemplate.insert(ety);
		} catch(MongoException ex) {
			throw new OperationException(Constants.Logs.ERROR_MONGO_INSERT, ex); 
		}
	} 
	
	@Override
	public void deleteByIdentifier(String identifier) {
		Query query = Query.query(Criteria.where(Constants.App.IDENTIFIER).is(identifier)); 
		mongoTemplate.remove(query, DocumentReferenceETY.class);
	}
	
	@Override
	public DocumentReferenceETY findById(String id) {
		DocumentReferenceETY ety = mongoTemplate.findById(id, DocumentReferenceETY.class);
		return ObjectUtils.isEmpty(ety) ? null : ety;
	} 
	
	@Override
	public List<DocumentReferenceETY> findAll() {
		return mongoTemplate.findAll(DocumentReferenceETY.class);
	}
}
