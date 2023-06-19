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

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.MiscUtility.convertToOffsetDateTime;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.*;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@Data
@AllArgsConstructor
public class GetTxPageLinksDTO {

    private String next;
    private String prev;

    public static GetTxPageLinksDTO fromPage(Date timestamp, Page<TransactionStatusETY> page) {
        return new GetTxPageLinksDTO(
            getNext(timestamp, page),
            getPrev(timestamp, page)
        );
    }

    private static String getPrev(Date timestamp, Page<TransactionStatusETY> page) {
        // Default state
        String prev = null;
        // Check if there is a previous page and the current page is on the right index
        if(page.hasPrevious() && page.hasContent()) {
            // Create URI
            prev = UriComponentsBuilder.fromUri(fromCurrentContextPath().build().toUri())
                .pathSegment(
                    API_VERSION,
                    API_TRANSACTIONS
                )
                .queryParam(API_QP_TIMESTAMP, ISO_DATE_TIME.format(convertToOffsetDateTime(timestamp)))
                .queryParam(API_QP_PAGE, String.valueOf(page.getNumber() - 1))
                .queryParam(API_QP_LIMIT, String.valueOf(page.getSize()))
                .toUriString();
        }
        return prev;
    }

    private static String getNext(Date timestamp, Page<TransactionStatusETY> page) {
        // Default state
        String next = null;
        // Check if there is a next page
        if(page.hasNext()) {
            // Create URI
            next = UriComponentsBuilder.fromUri(fromCurrentContextPath().build().toUri())
                .pathSegment(
                    API_VERSION,
                    API_TRANSACTIONS
                )
                .queryParam(API_QP_TIMESTAMP, ISO_DATE_TIME.format(convertToOffsetDateTime(timestamp)))
                .queryParam(API_QP_PAGE, String.valueOf(page.getNumber() + 1))
                .queryParam(API_QP_LIMIT, String.valueOf(page.getSize()))
                .toUriString();
        }
        return next;
    }

}
