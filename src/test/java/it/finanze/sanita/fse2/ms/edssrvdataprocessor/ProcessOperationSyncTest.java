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

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.MockRequests.postProcessReq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.AbstractTest;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl.DocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.PriorityTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl.DocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.OrchestratorSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@AutoConfigureMockMvc
@EmbeddedKafka
class ProcessOperationSyncTest extends AbstractTest {
    
	@Autowired
    ServletWebServerApplicationContext webServerAppCtxt;

    @MockBean
    Tracer tracer;

    @Autowired
    MockMvc mvc;

    @SpyBean
    private IEdsQueryClient queryClient;

    @SpyBean
    private IEdsDataQualityClient dataQualityClient;

    @Autowired
    DocumentCTL documentCTL;

    @MockBean
    ProfileUtility profileUtility;

    @Autowired
    DocumentRepo documentRepo;
    
    @MockBean
    private RestTemplate restTemplate; 
    
    @SpyBean
    private OrchestratorSRV orchestratorSRV; 

    private String TEST_IDENTIFIER = "testIdentifier"; 
    private ProcessorOperationEnum TEST_OPERATION_UPDATE = ProcessorOperationEnum.UPDATE; 
    private ProcessorOperationEnum TEST_OPERATION_DELETE = ProcessorOperationEnum.DELETE; 
    private String TEST_JSON_STRING = "{\"test\": \"testString\"}"; 
    private PriorityTypeEnum TEST_PRIORITY_TYPE_ENUM = PriorityTypeEnum.HIGH;  
    
    
    @Test
	@DisplayName("Update Sync - Success test")
    void processUpdateTest() throws Exception {
		// Data preparation
		DocumentReferenceDTO document = new DocumentReferenceDTO(TEST_IDENTIFIER, TEST_OPERATION_UPDATE, TEST_JSON_STRING, TEST_PRIORITY_TYPE_ENUM);
		// Mock
    	BDDMockito.doNothing().when(queryClient).fhirPublication(anyString(), anyString(), any(ProcessorOperationEnum.class)); 
		// Perform
	    mvc.perform(
			postProcessReq(document)
		).andExpect(status().is2xxSuccessful()); 
    } 
       
    @Test
	@DisplayName("Delete Sync - Success test")
    void processDeleteTest() throws Exception {
		// Data preparation
    	DocumentReferenceDTO document = new DocumentReferenceDTO(TEST_IDENTIFIER, TEST_OPERATION_DELETE, TEST_JSON_STRING, TEST_PRIORITY_TYPE_ENUM);
		// Mock
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEsito(true);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.isNull(), Mockito.eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(responseDTO, HttpStatus.OK));
		// Perform
	    mvc.perform(
			postProcessReq(document)
		).andExpect(status().is2xxSuccessful()); 
    } 

    
    @Test
	@DisplayName("Delete Sync - Exception test")
    void processDeleteExceptionTest() throws Exception {
		// Data preparation
    	DocumentReferenceDTO document = new DocumentReferenceDTO(TEST_IDENTIFIER, TEST_OPERATION_DELETE, TEST_JSON_STRING, TEST_PRIORITY_TYPE_ENUM);
		// Mock
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.isNull(), Mockito.eq(ResponseDTO.class)))
				.thenThrow(BusinessException.class);
		// Perform
	    mvc.perform(
			postProcessReq(document)
		).andExpect(status().is5xxServerError());
    }  
    
    @Test
	@DisplayName("Delete Sync - Unsupported Operation Exception test")
    void processDeleteUnsupportedOperationExceptionTest() throws Exception {
    	// Data preparation
    	DocumentReferenceDTO document = new DocumentReferenceDTO(TEST_IDENTIFIER, TEST_OPERATION_DELETE, TEST_JSON_STRING, TEST_PRIORITY_TYPE_ENUM);
		// Mock
		BDDMockito.doThrow(UnsupportedOperationException.class).when(orchestratorSRV)
				.dispatchAction(any(ProcessorOperationEnum.class), any(DispatchActionDTO.class)); 
		// Perform
	    mvc.perform(
			postProcessReq(document)
		).andExpect(status().is4xxClientError());
    }  
    
    @Test
	@DisplayName("Delete Sync - Connection Refused Exception test")
    void processConnectionRefusedOperationExceptionTest() throws Exception {
    	// Data preparation
    	DocumentReferenceDTO document = new DocumentReferenceDTO(TEST_IDENTIFIER, TEST_OPERATION_DELETE, TEST_JSON_STRING, TEST_PRIORITY_TYPE_ENUM);
		// Mock
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.isNull(), Mockito.eq(ResponseDTO.class)))
				.thenThrow(ResourceAccessException.class);
		// Perform
	    mvc.perform(
			postProcessReq(document)
		).andExpect(status().is5xxServerError());
    }

	@Test
	@DisplayName("Publish - Empty Message test")
	void processPublishEmptyMessageTest() throws Exception {
		// Data preparation
    	DocumentReferenceDTO document = new DocumentReferenceDTO(TEST_IDENTIFIER, TEST_OPERATION_DELETE, TEST_JSON_STRING, TEST_PRIORITY_TYPE_ENUM);
		// Mock
		BDDMockito.doThrow(ConnectionRefusedException.class).when(orchestratorSRV)
				.dispatchAction(any(ProcessorOperationEnum.class), any(DispatchActionDTO.class));
		// Perform
	    mvc.perform(
			postProcessReq(document)
		).andExpect(status().is5xxServerError());
	}
}
