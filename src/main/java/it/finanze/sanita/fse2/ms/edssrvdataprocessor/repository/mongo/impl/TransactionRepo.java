/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl;

import com.mongodb.MongoException;
import com.mongodb.client.result.DeleteResult;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.ITransactionRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.*;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * The transaction repository implementation
 */
@Repository
public class TransactionRepo implements ITransactionRepo {

	/**
	 * Mongo Template 
	 */
	@Autowired
	private MongoTemplate mongo;
	
	@Override
	public TransactionStatusETY insert(TransactionStatusETY tx) throws OperationException {
		TransactionStatusETY out;
		try {
			 out = mongo.insert(tx);
		} catch(MongoException ex) {
			throw new OperationException(ERROR_MONGO_INSERT, ex);
		}
		return out;
	}

	@Override
	public Page<TransactionStatusETY> getByTimestamp(Date timestamp, Pageable page) throws OperationException {
		// Working vars
		List<TransactionStatusETY> entities;
		long count;
		// Create query
		Query query = new Query();
		query.addCriteria(where(FIELD_INSERTION_DATE).lte(timestamp));
		try {
			// Get count
			count = mongo.count(query, TransactionStatusETY.class);
			// Retrive slice with pagination
			entities = mongo.find(query.with(page), TransactionStatusETY.class);
		} catch (MongoException e) {
			// Catch data-layer runtime exceptions and turn into a checked exception
			throw new OperationException(ERR_REP_DOCS_NOT_FOUND, e);
		}
		// Return data
		return new PageImpl<>(entities, page, count);
	}

	@Override
	public long deleteByTimestamp(Date timestamp) throws OperationException {
		// Working var
		DeleteResult res;
		// Create query
		Query query = new Query();
		query.addCriteria(where(FIELD_INSERTION_DATE).lte(timestamp));
		try {
			res = mongo.remove(query, TransactionStatusETY.class);
		}catch (MongoException e) {
			// Catch data-layer runtime exceptions and turn into a checked exception
			throw new OperationException(ERR_REP_DEL_DOCS, e);
		}
		return res.getDeletedCount();
	}

}
