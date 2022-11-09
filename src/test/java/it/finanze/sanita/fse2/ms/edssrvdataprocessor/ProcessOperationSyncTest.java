/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import brave.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.TestProducer;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaPropertiesCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka.KafkaTopicCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl.DocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.PriorityTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl.DocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IFhirOperationSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.KafkaSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.OrchestratorSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(Constants.Profile.TEST_SYNC)
@DirtiesContext
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class ProcessOperationSyncTest {
    
	@Autowired
    ServletWebServerApplicationContext webServerAppCtxt;

    @MockBean
    Tracer tracer;

    @Autowired
    MockMvc mvc;

    @MockBean
    private IEdsQueryClient queryClient;

    @MockBean
    private IEdsDataQualityClient dataQualityClient;

    @Autowired
    DocumentCTL documentCTL;

    @MockBean
    ProfileUtility profileUtility;

    @Autowired
    DocumentRepo documentRepo;
    
    @MockBean
    private RestTemplate restTemplate; 
    
    @Autowired
    private IFhirOperationSRV fhirOperationSRV;
    
    @MockBean
    private OrchestratorSRV orchestratorSRV; 

    @Autowired
    private KafkaSRV kafkaService;

    private TestProducer testProducer;

    @Autowired
    private KafkaPropertiesCFG kafkaPropCFG;

    @Autowired
    private KafkaTopicCFG kafkaTopicConfig;
    
    
    // Test Data 
    private String TEST_MONGO_ID = "testMongoId"; 
    private String TEST_IDENTIFIER = "testIdentifier"; 
    private ProcessorOperationEnum TEST_OPERATION_UPDATE = ProcessorOperationEnum.UPDATE; 
    private ProcessorOperationEnum TEST_OPERATION_DELETE = ProcessorOperationEnum.DELETE; 
    private String TEST_JSON_STRING = "{\"test\": \"testString\"}"; 
    private PriorityTypeEnum TEST_PRIORITY_TYPE_ENUM = PriorityTypeEnum.HIGH;  
    
    
    @Test
	@DisplayName("Update Sync - Success test")
    void processUpdateTest() throws Exception {
    	DocumentReferenceDTO document = new DocumentReferenceDTO(); 
        ObjectMapper objectMapper = new ObjectMapper(); 

    	document.setIdentifier(TEST_IDENTIFIER); 
    	document.setOperation(TEST_OPERATION_UPDATE); 
    	document.setJsonString(TEST_JSON_STRING); 
    	document.setPriorityTypeEnum(TEST_PRIORITY_TYPE_ENUM); 
		    	
		
    	BDDMockito.doNothing().when(queryClient).fhirPublication(anyString(), anyString(), any(ProcessorOperationEnum.class)); 
		 		
	    MockHttpServletRequestBuilder builder =
	            MockMvcRequestBuilders.post("http://localhost:9089/v1/process").content(objectMapper.writeValueAsString(document)); 
	    
	    mvc.perform(builder
	            .contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(status().is2xxSuccessful()); 
    } 
       
    @Test
	@DisplayName("Delete Sync - Success test")
    void processDeleteTest() throws Exception {
    	DocumentReferenceDTO document = new DocumentReferenceDTO(); 
        ObjectMapper objectMapper = new ObjectMapper(); 

    	document.setIdentifier(TEST_IDENTIFIER); 
    	document.setOperation(TEST_OPERATION_DELETE); 
    	document.setJsonString(TEST_JSON_STRING); 
    	document.setPriorityTypeEnum(TEST_PRIORITY_TYPE_ENUM); 
    	   	
		BDDMockito.doNothing().when(queryClient).fhirDelete(anyString()); 
		 		
	    MockHttpServletRequestBuilder builder =
	            MockMvcRequestBuilders.post("http://localhost:9089/v1/process").content(objectMapper.writeValueAsString(document)); 
	    
	    mvc.perform(builder
	            .contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(status().is2xxSuccessful()); 
    } 

    
    @Test
	@DisplayName("Delete Sync - Exception test")
    void processDeleteExceptionTest() throws Exception {
    	DocumentReferenceDTO document = new DocumentReferenceDTO(); 
        ObjectMapper objectMapper = new ObjectMapper(); 

    	document.setIdentifier(TEST_IDENTIFIER); 
    	document.setOperation(TEST_OPERATION_DELETE); 
    	document.setJsonString(TEST_JSON_STRING); 
    	document.setPriorityTypeEnum(TEST_PRIORITY_TYPE_ENUM); 
    	   	
		BDDMockito.doThrow(BusinessException.class).when(orchestratorSRV)
				.dispatchAction(any(ProcessorOperationEnum.class), any(DispatchActionDTO.class)); 
		 		
	    MockHttpServletRequestBuilder builder =
	            MockMvcRequestBuilders.post("http://localhost:9089/v1/process").content(objectMapper.writeValueAsString(document)); 
	    
	    mvc.perform(builder
	            .contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(status().is5xxServerError()); 
    }  
    
    @Test
	@DisplayName("Delete Sync - Unsupported Operation Exception test")
    void processDeleteUnsupportedOperationExceptionTest() throws Exception {
    	DocumentReferenceDTO document = new DocumentReferenceDTO(); 
        ObjectMapper objectMapper = new ObjectMapper(); 

    	document.setIdentifier(TEST_IDENTIFIER); 
    	document.setOperation(TEST_OPERATION_DELETE); 
    	document.setJsonString(TEST_JSON_STRING); 
    	document.setPriorityTypeEnum(TEST_PRIORITY_TYPE_ENUM); 
    	   	
		BDDMockito.doThrow(UnsupportedOperationException.class).when(orchestratorSRV)
				.dispatchAction(any(ProcessorOperationEnum.class), any(DispatchActionDTO.class)); 
		 		
	    MockHttpServletRequestBuilder builder =
	            MockMvcRequestBuilders.post("http://localhost:9089/v1/process").content(objectMapper.writeValueAsString(document)); 
	    
	    mvc.perform(builder
	            .contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(status().is4xxClientError()); 
    }  
    
    @Test
	@DisplayName("Delete Sync - Connection Refused Exception test")
    void processConnectionRefusedOperationExceptionTest() throws Exception {
    	DocumentReferenceDTO document = new DocumentReferenceDTO(); 
        ObjectMapper objectMapper = new ObjectMapper(); 

    	document.setIdentifier(TEST_IDENTIFIER); 
    	document.setOperation(TEST_OPERATION_DELETE); 
    	document.setJsonString(TEST_JSON_STRING); 
    	document.setPriorityTypeEnum(TEST_PRIORITY_TYPE_ENUM); 
    	   	
		BDDMockito.doThrow(ConnectionRefusedException.class).when(orchestratorSRV)
				.dispatchAction(any(ProcessorOperationEnum.class), any(DispatchActionDTO.class)); 
		 		
	    MockHttpServletRequestBuilder builder =
	            MockMvcRequestBuilders.post("http://localhost:9089/v1/process").content(objectMapper.writeValueAsString(document)); 
	    
	    mvc.perform(builder
	            .contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(status().is5xxServerError()); 
    }

	@Test
	@DisplayName("Publish - Empty Message test")
	void processPublishEmptyMessageTest() throws Exception {
		DocumentReferenceDTO document = new DocumentReferenceDTO();
		ObjectMapper objectMapper = new ObjectMapper();

		document.setIdentifier("");
		document.setOperation(TEST_OPERATION_DELETE);
		document.setJsonString(TEST_JSON_STRING);
		document.setPriorityTypeEnum(TEST_PRIORITY_TYPE_ENUM);

		BDDMockito.doThrow(ConnectionRefusedException.class).when(orchestratorSRV)
				.dispatchAction(any(ProcessorOperationEnum.class), any(DispatchActionDTO.class));

		MockHttpServletRequestBuilder builder =
				MockMvcRequestBuilders.post("http://localhost:9089/v1/process").content(objectMapper.writeValueAsString(document));

		mvc.perform(builder
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is5xxServerError());
	}

}
