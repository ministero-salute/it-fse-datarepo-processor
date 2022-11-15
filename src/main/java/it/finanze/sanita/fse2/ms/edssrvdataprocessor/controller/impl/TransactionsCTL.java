package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.ITransactionsCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.GetTxPageResDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class TransactionsCTL extends AbstractCTL implements ITransactionsCTL {
    @Override
    public GetTxPageResDTO getTransactions(String type, Date timestamp, int page, int limit) {
        return null;
    }
}
