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
        this.links = GetTxPageLinksDTO.fromPage(timestamp, page);
    }
}
