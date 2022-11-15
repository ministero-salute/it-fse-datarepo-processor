/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.data.domain.Page;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.*;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@Data
@AllArgsConstructor
public class GetTxPageLinksDTO {

    private String next;
    private String prev;

    public static GetTxPageLinksDTO fromPage(String type, Page<DocumentReferenceETY> page) {
        return new GetTxPageLinksDTO(
            getNext(type, page),
            getPrev(type, page)
        );
    }

    private static String getPrev(String type, Page<DocumentReferenceETY> page) {
        // Default state
        String prev = null;
        // Check if there is a previous page and the current page is on the right index
        if(page.hasPrevious() && page.hasContent()) {
            // Create URI
            prev = new URIBuilder(fromCurrentContextPath().build().toUri())
                .setPathSegments(
                    API_VERSION,
                    API_TRANSACTIONS,
                    type
                )
                .addParameter(API_QP_PAGE, String.valueOf(page.getNumber() - 1))
                .addParameter(API_QP_LIMIT, String.valueOf(page.getSize()))
                .toString();
        }
        return prev;
    }

    private static String getNext(String type, Page<DocumentReferenceETY> page) {
        // Default state
        String next = null;
        // Check if there is a next page
        if(page.hasNext()) {
            // Create URI
            next = new URIBuilder(fromCurrentContextPath().build().toUri())
                .setPathSegments(
                    API_VERSION,
                    API_TRANSACTIONS,
                    type
                )
                .addParameter(API_QP_PAGE, String.valueOf(page.getNumber() + 1))
                .addParameter(API_QP_LIMIT, String.valueOf(page.getSize()))
                .toString();
        }
        return next;
    }

}
