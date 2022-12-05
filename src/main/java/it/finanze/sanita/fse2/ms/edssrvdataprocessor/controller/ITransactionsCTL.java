/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.DeleteTxResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx.GetTxPageResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.validators.NoFutureDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.*;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@Tag(name = API_TRANSACTIONS_TAG)
@Validated
public interface ITransactionsCTL {

    @GetMapping(value = API_TRANSACTIONS_PATH, produces = { APPLICATION_JSON_VALUE })
    @Operation(summary = "Restituisce transazioni processate in base al timestamp",description = "Restituisce tutte le transazioni correttamente processate in base al timestamp")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Documenti restituite correttamente",content = @Content(mediaType = APPLICATION_JSON_VALUE,schema = @Schema(implementation = GetTxPageResDTO.class))),
    		@ApiResponse(responseCode = "400",description = "I parametri forniti non sono validi",content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,schema = @Schema(implementation = ErrorResponseDTO.class))),
    		@ApiResponse(responseCode = "500",description = "Errore interno del server",content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,schema = @Schema(implementation = ErrorResponseDTO.class)))})
    GetTxPageResDTO getTransactions(
        @RequestParam(API_QP_TIMESTAMP)
        @Parameter(description = "Identificatore arco-temporale")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @NoFutureDate(message = ERR_VAL_FUTURE_DATE)
        Date timestamp,
        @RequestParam(API_QP_PAGE)
        @Parameter(description = "Indice pagina richiesto (eg. 0, 1, 2...)")
        @PositiveOrZero(message = ERR_VAL_PAGE_IDX_LESS_ZERO)
        int page,
        @RequestParam(API_QP_LIMIT)
        @Parameter(description = "Limite documenti per pagina (eg. 10, 20 ...)")
        @Positive(message = ERR_VAL_PAGE_LIMIT_LESS_ZERO)
        int limit
    ) throws OperationException, OutOfRangeException;

    @DeleteMapping(value = API_TRANSACTIONS_PATH,produces = { APPLICATION_JSON_VALUE })
    @Operation(summary = "Cancella transazioni processate in base al timestamp",description = "Restituisce il numero di transazioni cancellate in base al timestamp")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",description = "Documenti cancellati correttamente",content = @Content(mediaType = APPLICATION_JSON_VALUE,schema = @Schema(implementation = DeleteTxResDTO.class))),
        @ApiResponse(responseCode = "400",description = "I parametri forniti non sono validi",content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500",description = "Errore interno del server",content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,schema = @Schema(implementation = ErrorResponseDTO.class)))})
    DeleteTxResDTO deleteTransactions(
        @RequestParam(API_QP_TIMESTAMP)
        @Parameter(description = "Identificatore arco-temporale")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @NoFutureDate(message = ERR_VAL_FUTURE_DATE)
        Date timestamp
    ) throws OperationException;

}
