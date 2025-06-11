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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.impl.EdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class EdsDataQualityClientTest {

    @Autowired
    private EdsDataQualityClient edsDataQualityClient;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    public void testValidateBundleNormativeR4_Success() {
        // Mock input data
        FhirOperationDTO input = new FhirOperationDTO();
        input.setJsonString("{test:\"test\"}");
        input.setMasterIdentifier("id_test");
        input.setWorkflowInstanceId("wiif_test");

        // Mock response entity
        ValidationResultDTO expected = new ValidationResultDTO();
        expected.setMessage("test_bundle");
        ResponseEntity<ValidationResultDTO> mockResponse = new ResponseEntity<>(expected, HttpStatus.OK);

        // Configure mock
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ValidationResultDTO.class))).thenReturn(mockResponse);

        // Perform the method to test
        ValidationResultDTO result = edsDataQualityClient.validateBundleNormativeR4(input);

        // Assertions
        assertEquals(HttpStatus.OK, mockResponse.getStatusCode());
        assertEquals(expected.getMessage(), result.getMessage());
    }

    @Test
    public void testValidateBundleNormativeR4_ConnectionRefusedException() {
        // Mock input data
        FhirOperationDTO input = new FhirOperationDTO();
        input.setJsonString("{test:\"test\"}");
        input.setMasterIdentifier("id_test");
        input.setWorkflowInstanceId("wiif_test");

        // Configure mock
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(ValidationResultDTO.class))).thenThrow(ConnectionRefusedException.class);

        // Assertion
        assertThrows(ConnectionRefusedException.class, () -> edsDataQualityClient.validateBundleNormativeR4(input));
    }

}
