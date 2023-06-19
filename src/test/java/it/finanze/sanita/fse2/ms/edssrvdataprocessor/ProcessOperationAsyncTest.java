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

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.AbstractTest;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl.DocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.*;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.IngestionStagingETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl.DocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.KafkaSRV;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@EmbeddedKafka
class ProcessOperationAsyncTest extends AbstractTest {
    
    @MockBean
    private RestTemplate restTemplate;
	
	@Autowired
	DocumentCTL documentCTL;

	@Autowired
	DocumentRepo documentRepo;

    @Autowired
    private KafkaSRV kafkaService;
    
    @Autowired
    private KafkaTopicCFG kafkaTopicConfig;

	@Test
	@DisplayName("Publish - All priority Success test")
	@Disabled
	void processPublishTest() throws OperationException {
		String topicHigh = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		String topicMed = kafkaTopicConfig.getIngestorPublishMediumPriorityTopic();
		String topicLow = kafkaTopicConfig.getIngestorPublishLowPriorityTopic();

		ConsumerRecord<String, String> consumerRecordHigh = this.kafkaInit(topicHigh, ProcessorOperationEnum.PUBLISH, false, false, false);

		// Start restTemplate mock

		ResourceExistResDTO getMock = new ResourceExistResDTO();
		getMock.setExist(false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willReturn(new ResponseEntity<>(getMock, HttpStatus.OK));

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		ResponseEntity<ResponseDTO> responsePubMock = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(responsePubMock);

		// End rest template mock

		assertDoesNotThrow(() ->
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecordHigh, 0)
		);

		ConsumerRecord<String, String> consumerRecordMed  = this.kafkaInit(topicMed, ProcessorOperationEnum.PUBLISH, false, false, false);

		assertDoesNotThrow(() ->
				kafkaService.mediumPriorityListenerPublishIngestor(consumerRecordMed, 0)
		);

		ConsumerRecord<String, String> consumerRecordLow  = this.kafkaInit(topicLow, ProcessorOperationEnum.PUBLISH, false, false, false);
		assertDoesNotThrow(() ->
				kafkaService.lowPriorityListenerPublishIngestor(consumerRecordLow, 0)
		);

		List<IngestionStagingETY> stagingDocuments = mongoTemplate.findAll(IngestionStagingETY.class);

		assertTrue(CollectionUtils.isEmpty(stagingDocuments));
	}

	@Test
	@DisplayName("Publish - Document already exists on FHIR test")
	void processPublishDocumentAlreadyExistsTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);
		
		ResourceExistResDTO getMock = new ResourceExistResDTO();
		getMock.setExist(false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class)))
				.willAnswer(invocation -> new DocumentAlreadyExistsException(""));

		assertThrows(BusinessException.class, () ->
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}
	
	@Test
	@DisplayName("Publish - Empty Message test")
	void processPublishEmptyMessageTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, true);
		assertThrows(EmptyIdentifierException.class, () ->
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Replace - Success test")
	@Disabled
	void processReplaceTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		// Start rest template mock

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		ResponseEntity<ResponseDTO> responsePubMock = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(responsePubMock);

		// End rest template mock

		assertDoesNotThrow(() ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - CheckExist - Rest template exception test")
	void processPublishCheckExistRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willThrow(new BusinessException(""));
		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - CheckExist - Connection refused test")
	void processPublishCheckExistConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class)))
				.willThrow(new ResourceAccessException(""));
		assertThrows(ResourceAccessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - CheckExist - Bad response test")
	void processPublishCheckExistBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class)))
				.willReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - Normalize - Rest template exception test")
	void processPublishNormalizeRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ResourceExistResDTO getMock = new ResourceExistResDTO();
		getMock.setExist(true);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willReturn(new ResponseEntity<>(getMock, HttpStatus.OK));
		assertThrows(DocumentAlreadyExistsException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - Normalize - Connection refused test")
	void processPublishNormalizeConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ResourceExistResDTO getMock = new ResourceExistResDTO();
		getMock.setExist(false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willReturn(new ResponseEntity<>(getMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class)))
				.thenThrow(new ResourceAccessException(""));
		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - Normalize - Bad response")
	void processPublishNormalizeBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ResourceExistResDTO getMock = new ResourceExistResDTO();
		getMock.setExist(false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willReturn(new ResponseEntity<>(getMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - FHIR - Rest template exception test")
	void processPublishFHIRRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new BusinessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - FHIR - Connection refused test")
	void processPublishFHIRConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new ResourceAccessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Publish - FHIR - Bad response")
	void processPublishFHIRBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Replace - FHIR - Rest template exception test")
	void processReplaceFHIRRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new BusinessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Replace - FHIR - Connection refused test")
	void processReplaceFHIRConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new ResourceAccessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, 0)
		);
	}

	@Test
	@DisplayName("Replace - FHIR - Bad response")
	void processReplaceFHIRBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		ValidationResultDTO validatedMock = new ValidationResultDTO();
		validatedMock.setValid(true);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, 0)
		);
	}
}
