/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO use to return a document as response to getDocument by ID request.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetTxPageResDTO extends ResponseDTO {

    @ArraySchema(schema = @Schema(implementation = String.class))
    private List<String> wif;
    private GetTxPageLinksDTO links;

    public GetTxPageResDTO(
        LogTraceInfoDTO traceInfo,
        List<String> wif,
        String type,
        Page<TransactionStatusETY> page
    ) {
        super(traceInfo);
        this.wif = wif;
        this.links = GetTxPageLinksDTO.fromPage(type, page);
    }
}
