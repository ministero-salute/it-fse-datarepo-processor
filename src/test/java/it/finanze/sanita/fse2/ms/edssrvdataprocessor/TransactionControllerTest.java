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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.mongodb.MongoException;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.AbstractTest;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.ITransactionsSVR;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@AutoConfigureMockMvc
@EmbeddedKafka
class TransactionControllerTest extends AbstractTest {

    @Autowired
    MockMvc mvc;

    @SpyBean
    private ITransactionsSVR service;

    @Test
    void getTransactionConstraintViolationException() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(getBaseUrl() + "/v1/transactions")
                        .queryParam("timestamp", DateTimeFormatter.ISO_DATE_TIME.format(new Date().toInstant().plusSeconds(120).atOffset(ZoneOffset.UTC)));

        mvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactionArgumentMismatchException() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(getBaseUrl() + "/v1/transactions")
                        .queryParam("timestamp", "generic string");

        mvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactionOperationException() throws Exception {

        Mockito.doThrow(MongoException.class).when(mongoTemplate).count(any(Query.class), eq(TransactionStatusETY.class));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(getBaseUrl() + "/v1/transactions")
                        .queryParam("timestamp", DateTimeFormatter.ISO_DATE_TIME.format(new Date().toInstant().minusSeconds(120).atOffset(ZoneOffset.UTC)))
                        .queryParam("page", "2")
                        .queryParam("limit", "1");

        mvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getNextAndPrevPageTest() throws Exception {

        TransactionStatusETY transactionStatusETY1 = new TransactionStatusETY();
        transactionStatusETY1.setId("_id1");
        transactionStatusETY1.setType(ProcessorOperationEnum.PUBLISH);
        transactionStatusETY1.setInsertionDate(new Date());
        transactionStatusETY1.setWorkflowInstanceId("wif1");

        TransactionStatusETY transactionStatusETY2 = new TransactionStatusETY();
        transactionStatusETY2.setId("_id2");
        transactionStatusETY2.setType(ProcessorOperationEnum.PUBLISH);
        transactionStatusETY2.setInsertionDate(new Date());
        transactionStatusETY2.setWorkflowInstanceId("wif2");

        List<TransactionStatusETY> list = new ArrayList<>();
        list.add(transactionStatusETY1);
        list.add(transactionStatusETY2);

        Mockito.doReturn(Long.valueOf("2")).when(mongoTemplate).count(any(Query.class), eq(TransactionStatusETY.class));
        Mockito.doReturn(list).when(mongoTemplate).find(any(Query.class), eq(TransactionStatusETY.class));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(getBaseUrl() + "/v1/transactions")
                        .queryParam("timestamp", DateTimeFormatter.ISO_DATE_TIME.format(new Date().toInstant().minusSeconds(120).atOffset(ZoneOffset.UTC)))
                        .queryParam("page", "1")
                        .queryParam("limit", "10");

        mvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }
}
