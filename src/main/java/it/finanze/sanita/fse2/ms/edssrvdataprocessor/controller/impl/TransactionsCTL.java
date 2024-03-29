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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.ITransactionsCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.DeleteTxResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.GetTxPageResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.ITransactionsSVR;

@RestController
public class TransactionsCTL extends AbstractCTL implements ITransactionsCTL {

    @Autowired
    private ITransactionsSVR service;

    @Override
    public GetTxPageResDTO getTransactions(Date timestamp, int page, int limit) throws OperationException, OutOfRangeException {
        // Retrieve Pair<Page, Entities>
        SimpleImmutableEntry<Page<TransactionStatusETY>, List<String>> slice = service.getTransactions(page, limit, timestamp);
        // When returning, it builds the URL according to provided values
        return new GetTxPageResDTO(getLogTraceInfo(), slice.getValue(), timestamp, slice.getKey());
    }

    @Override
    public DeleteTxResDTO deleteTransactions(Date timestamp) throws OperationException {
        return new DeleteTxResDTO(getLogTraceInfo(), timestamp, service.deleteTransactions(timestamp));
    }
}
