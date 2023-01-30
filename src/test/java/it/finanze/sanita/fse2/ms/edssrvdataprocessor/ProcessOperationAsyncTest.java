/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.IngestionStagingETY;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.AbstractTest;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl.DocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BlockingException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl.DocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.KafkaSRV;


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

	private MessageHeaders headers;

	@BeforeAll
	void init() {
		headers = new MessageHeaders(new HashMap<>());
	}

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

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		ResponseEntity<ResponseDTO> responsePubMock = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(responsePubMock);

		// End rest template mock

		assertDoesNotThrow(() ->
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecordHigh, headers)
		);

		ConsumerRecord<String, String> consumerRecordMed  = this.kafkaInit(topicMed, ProcessorOperationEnum.PUBLISH, false, false, false);

		assertDoesNotThrow(() ->
				kafkaService.mediumPriorityListenerPublishIngestor(consumerRecordMed, headers)
		);

		ConsumerRecord<String, String> consumerRecordLow  = this.kafkaInit(topicLow, ProcessorOperationEnum.PUBLISH, false, false, false);
		assertDoesNotThrow(() ->
				kafkaService.lowPriorityListenerPublishIngestor(consumerRecordLow, headers)
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
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}
	
	@Test
	@DisplayName("Publish - Empty Message test")
	void processPublishEmptyMessageTest() throws OperationException, NoRecordFoundException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, true);
		assertThrows(EmptyIdentifierException.class, () ->
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Replace - Success test")
	@Disabled
	void processReplaceTest() throws OperationException, NoRecordFoundException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		// Start rest template mock

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		ResponseEntity<ResponseDTO> responsePubMock = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(responsePubMock);

		// End rest template mock

		assertDoesNotThrow(() ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Publish - CheckExist - Rest template exception test")
	void processPublishCheckExistRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willThrow(new BusinessException(""));
		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
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
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
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
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
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
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
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
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
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
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Publish - FHIR - Rest template exception test")
	void processPublishFHIRRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new BusinessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Publish - FHIR - Connection refused test")
	void processPublishFHIRConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new ResourceAccessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Publish - FHIR - Bad response")
	void processPublishFHIRBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Replace - FHIR - Rest template exception test")
	void processReplaceFHIRRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new BusinessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Replace - FHIR - Connection refused test")
	void processReplaceFHIRConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new ResourceAccessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Replace - FHIR - Bad response")
	void processReplaceFHIRBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		ValidationResultDTO validatedMock = ValidationResultDTO
				.builder()
				.isValid(true)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(new ResponseEntity<>(validatedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}
}
