/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * Transaction repository interface
 */
public interface ITransactionRepo {

	/**
	 * Insert one transaction
	 * 
	 * @param tx Transaction to insert
	 * @return The inserted transaction
	 * @throws OperationException If a data-layer exception occurs
	 */
	TransactionStatusETY insert(TransactionStatusETY tx) throws OperationException;

    Page<TransactionStatusETY> getByTimestamp(Date timestamp, Pageable of) throws OperationException;

    long deleteByTimestamp(Date timestamp) throws OperationException;
}
