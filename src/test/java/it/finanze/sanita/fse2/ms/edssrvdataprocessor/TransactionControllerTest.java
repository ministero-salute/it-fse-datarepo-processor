package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.AbstractTest;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.ITransactionsSVR;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.BDDMockito;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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