package it.finanze.sanita.fse2.ms.edssrvdataprocessor.base;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_PROCESS_PATH;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_QP_LIMIT;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_QP_PAGE;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_QP_TIMESTAMP;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_TRANSACTIONS_PATH;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;

public final class MockRequests {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Private constructor to disallow to access from other classes
     */
    private MockRequests() {}

    public static MockHttpServletRequestBuilder getTransactionsReq(Date timestamp, int page, int limit) {
        // Default GET without parameter
        MockHttpServletRequestBuilder req = get(API_TRANSACTIONS_PATH).contentType(MediaType.APPLICATION_JSON_VALUE);
        // Add timestamp
        if(timestamp != null) {
            // Set timezone
            // Truncate to millis
            OffsetDateTime update = timestamp
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS);
            // Add queryParam with format
            req.queryParam(API_QP_TIMESTAMP, ISO_DATE_TIME.format(update));
        } else {
            req.queryParam(API_QP_TIMESTAMP, String.valueOf(timestamp));
        }
        // Add page in queryParam
        req.queryParam(API_QP_PAGE, String.valueOf(page));
        // Add limit in queryParam
        req.queryParam(API_QP_LIMIT, String.valueOf(limit));

        return req;
    }
    
    public static MockHttpServletRequestBuilder getTransactionsByStringReq(String timestamp, int page, int limit) {
        // Default GET without parameter
        MockHttpServletRequestBuilder req = get(API_TRANSACTIONS_PATH).contentType(MediaType.APPLICATION_JSON_VALUE);
        // Add string timestamp
        req.queryParam(API_QP_TIMESTAMP, timestamp);
        // Add page in queryParam
        req.queryParam(API_QP_PAGE, String.valueOf(page));
        // Add limit in queryParam
        req.queryParam(API_QP_LIMIT, String.valueOf(limit));

        return req;
    }

    public static MockHttpServletRequestBuilder deleteTransactionsReq(Date timestamp) {
        // Default GET without parameter
        MockHttpServletRequestBuilder req = delete(API_TRANSACTIONS_PATH).contentType(MediaType.APPLICATION_JSON_VALUE);
        // Add timestamp
        if(timestamp != null) {
            // Set timezone
            // Truncate to millis
            OffsetDateTime update = timestamp
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS);
            // Add queryParam with format
            req.queryParam(API_QP_TIMESTAMP, ISO_DATE_TIME.format(update));
        } else {
            req.queryParam(API_QP_TIMESTAMP, String.valueOf(timestamp));
        }

        return req;
    }

    public static MockHttpServletRequestBuilder postProcessReq(DocumentReferenceDTO document) throws JsonProcessingException {
        // Default GET without parameter
        MockHttpServletRequestBuilder req = post(API_PROCESS_PATH).contentType(MediaType.APPLICATION_JSON_VALUE);
        req.content(objectMapper.writeValueAsString(document));
        return req;
    }

}
