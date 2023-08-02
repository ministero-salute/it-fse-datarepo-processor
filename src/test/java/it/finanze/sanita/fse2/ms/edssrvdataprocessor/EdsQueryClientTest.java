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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
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

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void fhirCheckExistTest() {
        // Mock response entity
        ResourceExistResDTO expected = new ResourceExistResDTO(new LogTraceInfoDTO(null, null), true);
        ResponseEntity<ResourceExistResDTO> mockResponse = new ResponseEntity<>(expected, HttpStatus.OK);
        // Configure mock
        when(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).thenReturn(mockResponse);
        // Perform method
        ResourceExistResDTO response = client.fhirCheckExist("test_id");
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
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(ResponseDTO.class))).thenReturn(mockResponse);
        client.fhirPublication("id_test", "json_test", ProcessorOperationEnum.PUBLISH);
        client.fhirPublication("id_test", "json_test", ProcessorOperationEnum.REPLACE);
        client.fhirPublication("id_test", "json_test", ProcessorOperationEnum.UPDATE);
        // Assertions
        verify(restTemplate, times(1)).exchange(
            any(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(ResponseDTO.class),
            any(ResponseDTO.class)
        );
        verify(restTemplate, times(2)).exchange(
            any(),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(ResponseDTO.class),
            any(ResponseDTO.class)
        );
    }

    @Test
    void fireCheckExistExceptionTest() {
        // Configure mock
        when(restTemplate.getForEntity(anyString(), eq(ResourceExistResDTO.class))).thenThrow(ResourceAccessException.class);
        // Assertion and perform fhirCheckExist
        assertThrows(ResourceAccessException.class, () -> client.fhirCheckExist("masterIdentifier"));
    }

}
