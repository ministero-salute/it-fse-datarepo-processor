package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.ITransactionRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.ITransactionsSVR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERR_VAL_PAGE_NOT_EXISTS;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_QP_PAGE;

@Service
@Slf4j
public class TransactionsSVR implements ITransactionsSVR {

    @Autowired
    private ITransactionRepo repository;

    @Override
    public SimpleImmutableEntry<Page<TransactionStatusETY>, List<String>> getTransactions(int page, int limit, Date timestamp, String type) throws OperationException, OutOfRangeException {
        // Retrieve page
        Page<TransactionStatusETY> current = repository.getByTimestamp(type, timestamp, PageRequest.of(page, limit));
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
}
