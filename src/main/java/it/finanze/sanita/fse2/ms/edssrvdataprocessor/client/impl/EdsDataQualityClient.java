package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.MicroservicesURLCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.FhirNormalizedDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class EdsDataQualityClient implements IEdsDataQualityClient {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5665880440554069040L;

    @Autowired
    private transient RestTemplate restTemplate;

    @Autowired
    private transient MicroservicesURLCFG microservicesURLCFG;

    @Override
    public FhirNormalizedDTO normalize(FhirOperationDTO input) {
        log.info("[EDS DATAQUALITY] Calling EDS normalize ep - START");

        // prepare request
        String url = microservicesURLCFG.getEdsDataQualityHost() + "/v1/normalize";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<?> entity = new HttpEntity<>(StringUtility.toJSON(input), headers);
        ResponseEntity<FhirNormalizedDTO> response = null;

        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, FhirNormalizedDTO.class);
            log.info("{} status returned from eds", response.getStatusCode());
        } catch(ResourceAccessException cex) {
            log.error("Connect error while call eds query publish ep :" + cex);
            throw new ConnectionRefusedException(microservicesURLCFG.getEdsQueryHost(), Constants.Logs.ERROR_CONNECTION_REFUSED);
        } catch(Exception ex) {
            log.error("Generic error while call eds query publish ep :" + ex);
            throw new BusinessException("Generic error while call eds query publish ep :" + ex);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException("Failed to normalize resource on FHIR server");
        }

        return response.getBody();
    }
}
