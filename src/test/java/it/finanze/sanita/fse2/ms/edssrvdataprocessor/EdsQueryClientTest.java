package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.impl.EdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;

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

}
