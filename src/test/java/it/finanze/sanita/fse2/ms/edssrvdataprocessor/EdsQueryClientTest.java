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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.impl.EdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class EdsQueryClientTest {

    @Autowired
    private EdsQueryClient client;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void fhirCheckExistTest() {
        // Mock response entity
        ResourceExistResDTO expected = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), true);
        ResponseEntity<ResourceExistResDTO> mockResponse = new ResponseEntity<>(expected, HttpStatus.OK);
        // Configure mock
        when(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).thenReturn(mockResponse);
        // Perform method
        ResourceExistResDTO response = client.checkExist("test_id");
        // Assertion
        assertTrue(response.isExist());
    }

    @Test
    void fhirPublicationTest() {
        // Mock response entity
        ResponseDTO expected = new ResponseDTO(new LogTraceInfoDTO(null, null));
        ResponseEntity<ResponseDTO> mockResponse = new ResponseEntity<>(expected, HttpStatus.OK);
        expected.setEsito(true);
        expected.setMessage("Message");
        // Configure mock
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ResponseDTO.class)))
                .thenReturn(mockResponse);
        client.fhirPublication("id_test", "json_test", ProcessorOperationEnum.PUBLISH);
        client.fhirPublication("id_test", "json_test", ProcessorOperationEnum.REPLACE);
        client.fhirPublication("id_test", "json_test", ProcessorOperationEnum.UPDATE);
        // Assertions
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ResponseDTO.class));
        verify(restTemplate, times(2)).exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(ResponseDTO.class));
    }

    @Test
    void fireCheckExistExceptionTest() {
        // Configure mock
        when(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class)))
                .thenThrow(ResourceAccessException.class);
        // Assertion and perform fhirCheckExist
        assertThrows(ResourceAccessException.class, () -> client.checkExist("masterIdentifier"));
    }

}
