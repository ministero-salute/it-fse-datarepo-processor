package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.ITransactionsCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.DeleteTxResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.GetTxPageResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.ITransactionsSVR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class TransactionsCTL extends AbstractCTL implements ITransactionsCTL {

    @Autowired
    private ITransactionsSVR service;

    @Override
    public GetTxPageResDTO getTransactions(String type, Date timestamp, int page, int limit) throws OperationException, OutOfRangeException {
        // Retrieve Pair<Page, Entities>
        SimpleImmutableEntry<Page<TransactionStatusETY>, List<String>> slice = service.getTransactions(page, limit, timestamp, type);
        // When returning, it builds the URL according to provided values
        return new GetTxPageResDTO(getLogTraceInfo(), slice.getValue(), type, timestamp, slice.getKey());
    }

    public DeleteTxResDTO deleteTransactions(String type, Date timestamp) throws OperationException {
        return new DeleteTxResDTO(type, timestamp, service.deleteTransactions(type, timestamp));
    }
}
