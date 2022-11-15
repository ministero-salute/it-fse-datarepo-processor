/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.ITransactionRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERROR_MONGO_INSERT;


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

}
