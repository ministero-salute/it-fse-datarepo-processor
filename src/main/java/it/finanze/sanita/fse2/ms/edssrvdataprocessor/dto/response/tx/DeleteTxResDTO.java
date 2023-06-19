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
import lombok.Data;

import java.util.Date;

@Data
public class DeleteTxResDTO {
    private String traceID;
    private String spanID;
    private Date timestamp;
    private long deletedTransactions;

    public DeleteTxResDTO(LogTraceInfoDTO info, Date timestamp, long deletedTransactions) {
        this.traceID = info.getTraceID();
        this.spanID = info.getSpanID();
        this.timestamp = timestamp;
        this.deletedTransactions = deletedTransactions;
    }
}
