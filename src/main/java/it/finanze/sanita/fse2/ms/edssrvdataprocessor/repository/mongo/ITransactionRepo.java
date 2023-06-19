/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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
