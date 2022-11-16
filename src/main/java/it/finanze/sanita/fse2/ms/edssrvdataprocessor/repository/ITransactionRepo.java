/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository;

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

    Page<TransactionStatusETY> getByTimestamp(String type, Date timestamp, Pageable of) throws OperationException;
}
