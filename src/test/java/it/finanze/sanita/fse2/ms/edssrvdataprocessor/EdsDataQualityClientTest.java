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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
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

    @MockBean
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
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenReturn(mockResponse);
        
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
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ValidationResultDTO.class))).thenThrow(ConnectionRefusedException.class);
        
        // Assertion
        assertThrows(ConnectionRefusedException.class, () -> edsDataQualityClient.validateBundleNormativeR4(input));
    }

}
