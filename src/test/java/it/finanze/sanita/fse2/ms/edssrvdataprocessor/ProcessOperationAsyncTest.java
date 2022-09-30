package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.AbstractTest;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.TestConstants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.MicroservicesURLCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl.DocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.FhirNormalizedDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl.DocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.KafkaSRV;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(Constants.Profile.TEST_ASYNC)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class ProcessOperationAsyncTest extends AbstractTest {
    @Autowired
    private IEdsQueryClient queryClient;

    @Autowired
    private IEdsDataQualityClient dataQualityClient;
    
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

	@Autowired
	private MicroservicesURLCFG microservicesURLCFG;

	@BeforeAll
	void init() {
		headers = new MessageHeaders(new HashMap<>());
	}

	@Test
	@DisplayName("Publish - All priority Success test")
	void processPublishTest() throws OperationException {
		String topicHigh = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		String topicMed = kafkaTopicConfig.getIngestorPublishMediumPriorityTopic();
		String topicLow = kafkaTopicConfig.getIngestorPublishLowPriorityTopic();

		ConsumerRecord<String, String> consumerRecordHigh = this.kafkaInit(topicHigh, ProcessorOperationEnum.PUBLISH, false, false, false);
		ConsumerRecord<String, String> consumerRecordMed  = this.kafkaInit(topicMed, ProcessorOperationEnum.PUBLISH, false, false, false);
		ConsumerRecord<String, String> consumerRecordLow  = this.kafkaInit(topicLow, ProcessorOperationEnum.PUBLISH, false, false, false);

		// Start restTemplate mock

		ResourceExistResDTO getMock = new ResourceExistResDTO();
		getMock.setExist(false);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willReturn(new ResponseEntity<>(getMock, HttpStatus.OK));

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.jsonString(TestConstants.TEST_JSON_STRING)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

		ResponseEntity<ResponseDTO> responsePubMock = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(responsePubMock);

		// End rest template mock

		assertDoesNotThrow(() ->
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecordHigh, headers)
		);
		assertDoesNotThrow(() ->
				kafkaService.mediumPriorityListenerPublishIngestor(consumerRecordMed, headers)
		);
		assertDoesNotThrow(() ->
				kafkaService.lowPriorityListenerPublishIngestor(consumerRecordLow, headers)
		);
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
	void processPublishEmptyMessageTest() throws OperationException, DocumentNotFoundException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, true);
		assertThrows(EmptyIdentifierException.class, () ->
			 kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}
	
	
	@Test
	@DisplayName("Update - Success test")
	void processUpdateTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic(); 
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.UPDATE, false, false, false); 

		ResponseEntity<ResponseDTO> response = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(response);
		
		assertDoesNotThrow(() ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
			);
	}


	@Test
	@DisplayName("Replace - Success test")
	void processReplaceTest() throws OperationException, DocumentNotFoundException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		// Start rest template mock

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

		ResponseEntity<ResponseDTO> responsePubMock = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(responsePubMock);

		// End rest template mock

		assertDoesNotThrow(() ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Delete - Success test")
	void processDeleteTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.DELETE, false, false, false);

		ResponseEntity<ResponseDTO> response = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(null), eq(ResponseDTO.class))).thenReturn(response);

		assertDoesNotThrow(() ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	
	@Test
	@DisplayName("Document not found test")
	void processDocumentNotFoundTest() throws OperationException {
		String topicPub = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		String topicGen = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord1 = this.kafkaInit(topicPub, ProcessorOperationEnum.PUBLISH, true, false, false);
		ConsumerRecord<String, String> consumerRecord2 = this.kafkaInit(topicGen, ProcessorOperationEnum.REPLACE, true, false, false);
		ConsumerRecord<String, String> consumerRecord3 = this.kafkaInit(topicGen, ProcessorOperationEnum.UPDATE, true, false, false);
		ConsumerRecord<String, String> consumerRecord4 = this.kafkaInit(topicGen, ProcessorOperationEnum.DELETE, true, false, false);
		assertThrows(DocumentNotFoundException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord1, headers)
		);
		assertThrows(DocumentNotFoundException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord2, headers)
		);
		assertThrows(DocumentNotFoundException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord3, headers)
		);
		assertThrows(DocumentNotFoundException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord4, headers)
		);
	}

	@Test
	@DisplayName("Decryption error test")
	void processDecryptionTest() throws OperationException {
		String topicPub = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		String topicGen = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord1 = this.kafkaInit(topicPub, ProcessorOperationEnum.PUBLISH, false, true, false);
		ConsumerRecord<String, String> consumerRecord2 = this.kafkaInit(topicGen, ProcessorOperationEnum.REPLACE, false, true, false);
		ConsumerRecord<String, String> consumerRecord3 = this.kafkaInit(topicGen, ProcessorOperationEnum.UPDATE,  false, true, false);
		ConsumerRecord<String, String> consumerRecord4 = this.kafkaInit(topicGen, ProcessorOperationEnum.DELETE,  false, true, false);
		assertThrows(EncryptionOperationNotPossibleException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord1, headers)
		);
		assertThrows(EncryptionOperationNotPossibleException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord2, headers)
		);
		assertThrows(EncryptionOperationNotPossibleException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord3, headers)
		);
		assertThrows(EncryptionOperationNotPossibleException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord4, headers)
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
		assertThrows(BusinessException.class, () ->
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

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class)))
				.thenThrow(new BusinessException(""));
		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Publish - Normalize - Connection refused test")
	void processPublishNormalizeConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.PUBLISH, false, false, false);

		ResourceExistResDTO getMock = new ResourceExistResDTO();
		getMock.setExist(true);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willReturn(new ResponseEntity<>(getMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class)))
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
		getMock.setExist(true);
		given(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).willReturn(new ResponseEntity<>(getMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class)))
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

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

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

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

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

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Delete - Rest template exception test")
	void processDeleteRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.DELETE, false, false, false);

		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(null), eq(ResponseDTO.class)))
				.thenThrow(new BusinessException(""));
		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Delete - Connection refused test")
	void processDeleteConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.DELETE, false, false, false);

		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(null), eq(ResponseDTO.class)))
				.thenThrow(new ResourceAccessException(""));
		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Delete - Bad response")
	void processDeleteBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorPublishHighPriorityTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.DELETE, false, false, false);

		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(null), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.highPriorityListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Update - FHIR - Rest template exception test")
	void processUpdateFHIRRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.UPDATE, false, false, false);

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new BusinessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Update - FHIR - Connection refused test")
	void processUpdateFHIRConnectionRefusedTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.UPDATE, false, false, false);

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenThrow(new ResourceAccessException(""));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Update - FHIR - Bad response")
	void processUpdateFHIRBadResponseTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.UPDATE, false, false, false);

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}

	@Test
	@DisplayName("Replace - FHIR - Rest template exception test")
	void processReplaceFHIRRestTemplateExceptionTest() throws OperationException {
		String topic = kafkaTopicConfig.getIngestorGenericTopic();
		ConsumerRecord<String, String> consumerRecord = this.kafkaInit(topic, ProcessorOperationEnum.REPLACE, false, false, false);

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

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

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

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

		FhirNormalizedDTO normalizedMock = FhirNormalizedDTO
				.builder()
				.masterIdentifier(TestConstants.TEST_MASTER_IDENTIFIER)
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(FhirNormalizedDTO.class))).thenReturn(new ResponseEntity<>(normalizedMock, HttpStatus.OK));

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ResponseDTO.class)))
				.thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

		assertThrows(BusinessException.class, () ->
				kafkaService.genericListenerPublishIngestor(consumerRecord, headers)
		);
	}
}
