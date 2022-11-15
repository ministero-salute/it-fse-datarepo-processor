package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.GetTxPageResDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.*;

@Tag(name = API_TRANSACTIONS_TAG)
@Validated
public interface ITransactionsCTL {

    @GetMapping(value = API_TRANSACTIONS_PATH)
    GetTxPageResDTO getTransactions(
        @PathVariable(API_PATH_TYPE_VAR)
        @Parameter(description = "Identificatore tipologia transazioni")
        String type,
        @RequestParam(API_QP_TIMESTAMP)
        @Parameter(description = "Identificatore arco-temporale")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Date timestamp,
        @RequestParam(API_QP_PAGE)
        @Parameter(description = "Indice pagina richiesto (eg. 0, 1, 2...)")
        int page,
        @RequestParam(API_QP_LIMIT)
        @Parameter(description = "Limite documenti per pagina (eg. 10, 20 ...)")
        int limit
    );

}
