package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import org.springframework.data.domain.Page;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.List;

public interface ITransactionsSVR {
    SimpleImmutableEntry<Page<TransactionStatusETY>, List<String>> getTransactions(int page, int limit, Date timestamp, String type) throws OperationException, OutOfRangeException;
}
