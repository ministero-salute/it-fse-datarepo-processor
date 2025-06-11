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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.ResourceAccessException;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ILogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.UATMockException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.FhirOperationSRV;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class FhirOperationSRVTest {

    @Autowired
    private FhirOperationSRV service;

    @MockitoBean
    private LoggerHelper kafkaLogger;

    @MockitoBean
    private IEdsDataQualityClient dataQuality;

    @MockitoBean
    private IEdsQueryClient query;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection(TransactionStatusETY.class);
    }

    @Test
    void publishTest() {
        // Data preparation
        FhirOperationDTO dto = new FhirOperationDTO();
        dto.setJsonString("json_test");
        dto.setMasterIdentifier("id_test");
        dto.setWorkflowInstanceId("wif_test");
        // Mock
        ResourceExistResDTO resourceDto = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), false);
        ValidationResultDTO validationDto = new ValidationResultDTO();
        validationDto.setValid(true);
        when(query.checkExist(dto.getMasterIdentifier())).thenReturn(resourceDto);
        when(dataQuality.validateBundleNormativeR4(dto)).thenReturn(validationDto);
        // Perform publish
        assertDoesNotThrow(() -> service.publish(dto));
        // Check transaction on MongoDB
        List<TransactionStatusETY> transactions = mongoTemplate.findAll(TransactionStatusETY.class);
        // Assertions
        assertFalse(transactions.isEmpty());
        assertEquals(dto.getWorkflowInstanceId(), transactions.get(0).getWorkflowInstanceId());
    }

    @Test
    void publishAlreadyExistTest() {
        // Data preparation
        FhirOperationDTO dto = new FhirOperationDTO();
        dto.setJsonString("json_test");
        dto.setMasterIdentifier("id_test");
        dto.setWorkflowInstanceId("wif_test");
        // Mock
        ValidationResultDTO validationDto = new ValidationResultDTO();
        validationDto.setValid(true);
        ResourceExistResDTO resourceDto = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), true);
        when(query.checkExist(dto.getMasterIdentifier())).thenReturn(resourceDto);
        when(dataQuality.validateBundleNormativeR4(dto)).thenReturn(validationDto);
        // Perform publish and assert exception
        assertThrows(DocumentAlreadyExistsException.class, () -> service.publish(dto));
    }

    @Test
    void publishFailedTest() {
        // Data preparation
        FhirOperationDTO dto = new FhirOperationDTO();
        dto.setJsonString("json_test");
        dto.setMasterIdentifier("id_test");
        dto.setWorkflowInstanceId("wif_test");
        // Mock
        ResourceExistResDTO resourceDto = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), false);
        ValidationResultDTO validationDto = new ValidationResultDTO();
        validationDto.setValid(false);
        validationDto.setNormativeR4Messages(Arrays.asList("error1", "error2"));
        when(query.checkExist(dto.getMasterIdentifier())).thenReturn(resourceDto);
        when(dataQuality.validateBundleNormativeR4(dto)).thenReturn(validationDto);
        // Perform publish and assert
        assertThrows(BusinessException.class, () -> service.publish(dto));
        verify(kafkaLogger, times(1)).info(anyString(), anyString(), any(ILogEnum.class), any(ResultLogEnum.class),
                any(Date.class));
    }

    @Test
    void replaceTest() {
        // Data preparation
        FhirOperationDTO dto = new FhirOperationDTO();
        dto.setJsonString("json_test");
        dto.setMasterIdentifier("id_test");
        dto.setWorkflowInstanceId("wif_test");
        // Mock
        ResourceExistResDTO resourceDto = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), false);
        ValidationResultDTO validationDto = new ValidationResultDTO();
        validationDto.setValid(true);
        when(query.checkExist(dto.getMasterIdentifier())).thenReturn(resourceDto);
        when(dataQuality.validateBundleNormativeR4(dto)).thenReturn(validationDto);
        // Perform publish
        assertDoesNotThrow(() -> service.replace(dto));
        // Check transaction on MongoDB
        List<TransactionStatusETY> transactions = mongoTemplate.findAll(TransactionStatusETY.class);
        // Assertions
        assertFalse(transactions.isEmpty());
        assertEquals(dto.getWorkflowInstanceId(), transactions.get(0).getWorkflowInstanceId());
    }

    @Test
    void publishUatMockTest() {
        // Data preparation
        FhirOperationDTO dto = new FhirOperationDTO();
        dto.setJsonString("json_test");
        dto.setMasterIdentifier("UAT_GTW_ID_test");
        dto.setWorkflowInstanceId("wif_test");
        // Mock
        ResourceExistResDTO resourceDto = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), false);
        ValidationResultDTO validationDto = new ValidationResultDTO();
        validationDto.setValid(false);
        validationDto.setNormativeR4Messages(Arrays.asList("error1", "error2"));
        validationDto.setNotTraversedResources(Arrays.asList("error1", "error2"));
        when(query.checkExist(dto.getMasterIdentifier())).thenReturn(resourceDto);
        when(dataQuality.validateBundleNormativeR4(dto)).thenReturn(validationDto);
        // Perform publish and assert
        assertThrows(UATMockException.class, () -> service.publish(dto));
        verify(kafkaLogger, times(2)).info(any(), any(), any(ILogEnum.class), any(ResultLogEnum.class),
                any(Date.class));
    }

    @Test
    void replaceUatMockTest() {
        // Data preparation
        FhirOperationDTO dto = new FhirOperationDTO();
        dto.setJsonString("json_test");
        dto.setMasterIdentifier("UAT_GTW_ID_test");
        dto.setWorkflowInstanceId("wif_test");
        // Mock
        ResourceExistResDTO resourceDto = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), false);
        ValidationResultDTO validationDto = new ValidationResultDTO();
        validationDto.setValid(false);
        validationDto.setNormativeR4Messages(Arrays.asList("error1", "error2"));
        validationDto.setNotTraversedResources(Arrays.asList("error1", "error2"));
        when(query.checkExist(dto.getMasterIdentifier())).thenReturn(resourceDto);
        when(dataQuality.validateBundleNormativeR4(dto)).thenReturn(validationDto);
        // Perform publish and assert
        assertThrows(UATMockException.class, () -> service.replace(dto));
        verify(kafkaLogger, times(1)).info(any(), any(), any(ILogEnum.class), any(ResultLogEnum.class),
                any(Date.class));
    }

    @Test
    void publishResourceAccessExceptionTest() {
        // Data preparation
        FhirOperationDTO dto = new FhirOperationDTO();
        dto.setJsonString("json_test");
        dto.setMasterIdentifier("id_test");
        dto.setWorkflowInstanceId("wif_test");
        // Mock
        when(query.checkExist(dto.getMasterIdentifier())).thenThrow(ResourceAccessException.class);
        // Perform publish and assert exception
        assertThrows(ResourceAccessException.class, () -> service.publish(dto));
    }

}
