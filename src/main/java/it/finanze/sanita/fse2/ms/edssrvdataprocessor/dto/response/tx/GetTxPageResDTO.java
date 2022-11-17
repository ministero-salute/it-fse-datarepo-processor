/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * DTO use to return a document as response to getDocument by ID request.
 */
@Data
public class GetTxPageResDTO {

    private String traceID;
    private String spanID;
    private Date timestamp;
    private List<String> wif;
    private GetTxPageLinksDTO links;

    public GetTxPageResDTO(
        LogTraceInfoDTO info,
        List<String> wif,
        Date timestamp,
        Page<TransactionStatusETY> page
    ) {
        this.traceID = info.getTraceID();
        this.spanID = info.getSpanID();
        this.timestamp = timestamp;
        this.wif = wif;
        this.links = GetTxPageLinksDTO.fromPage(page);
    }
}
