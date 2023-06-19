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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERR_VAL_PAGE_NOT_EXISTS;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_QP_PAGE;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.ITransactionRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.ITransactionsSVR;

@Service
public class TransactionsSRV implements ITransactionsSVR {

    @Autowired
    private ITransactionRepo repository;

    @Override
    public SimpleImmutableEntry<Page<TransactionStatusETY>, List<String>> getTransactions(int page, int limit, Date timestamp) throws OperationException, OutOfRangeException {
        // Retrieve page
        Page<TransactionStatusETY> current = repository.getByTimestamp(timestamp, PageRequest.of(page, limit));
        // Check valid index was provided
        if(page > current.getTotalPages()) {
            // Let the caller know about it
            throw new OutOfRangeException(
                String.format(ERR_VAL_PAGE_NOT_EXISTS, 0, current.getTotalPages() - 1), API_QP_PAGE
            );
        }
        // Map to WIF
        List<String> wif = current.stream().map(TransactionStatusETY::getWorkflowInstanceId).collect(Collectors.toList());
        // Return page
        return new SimpleImmutableEntry<>(current, wif);
    }

    @Override
    public long deleteTransactions(Date timestamp) throws OperationException {
        return repository.deleteByTimestamp(timestamp);
    }
}
