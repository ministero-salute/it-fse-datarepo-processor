/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERROR_MONGO_FIND_BY_ID;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERROR_MONGO_INSERT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoException;
import com.mongodb.client.result.DeleteResult;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.IngestionStagingETY;
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
	public IngestionStagingETY insert(IngestionStagingETY entity) throws OperationException {
		IngestionStagingETY out;
		try {
			 out = mongo.insert(entity);
		} catch(MongoException ex) {
			throw new OperationException(ERROR_MONGO_INSERT, ex);
		}
		return out;
	} 

	@Override
	public IngestionStagingETY findById(String id) throws OperationException {
		IngestionStagingETY out;
		try {
			out = mongo.findById(id, IngestionStagingETY.class);
		}catch(MongoException ex) {
			throw new OperationException(ERROR_MONGO_FIND_BY_ID, ex);
		}
		return out;
	}
	
	@Override
	public boolean deleteById(final String wii, final ProcessorOperationEnum operation) throws OperationException {
		boolean output = false;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("workflow_instance_id").is(wii).and("operation").is(operation));
			DeleteResult dRes = mongo.remove(query, IngestionStagingETY.class);
			output = dRes.getDeletedCount()>0;
		}catch(MongoException ex) {
			throw new OperationException(ERROR_MONGO_FIND_BY_ID, ex);
		}
		return output;
	}
}
