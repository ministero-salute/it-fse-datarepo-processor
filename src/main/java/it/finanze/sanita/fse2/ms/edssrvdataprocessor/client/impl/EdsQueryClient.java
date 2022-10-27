package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.MicroservicesURLCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.request.FhirPublicationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
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

import java.util.Optional;

/**
 * The implementation of the Srv Query Client 
 *
 */
@Slf4j
@Component
public class EdsQueryClient implements IEdsQueryClient {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5665880440554069040L;

    /**
     * Rest Template 
     */
    @Autowired
    private transient RestTemplate restTemplate;

    /*
     * Microservices URL Config 
     */
    @Autowired
    private transient MicroservicesURLCFG microservicesURLCFG;

    @Override
    public void fhirCheckExist(String masterIdentifier) throws DocumentAlreadyExistsException {
        log.info("[EDS QUERY] Calling EDS check exist ep - START");

        ResponseEntity<ResourceExistResDTO> response = null;
        String url = this.buildRequestPath(masterIdentifier, ProcessorOperationEnum.READ);
        
        try {
            response = restTemplate.getForEntity(url, ResourceExistResDTO.class);
            log.info(Constants.Logs.SRV_QUERY_RESPONSE, response.getStatusCode());
        } catch(ResourceAccessException cex) {
            log.error("Connect error while call eds query check exist ep :" + cex);
            throw new ConnectionRefusedException(microservicesURLCFG.getEdsQueryHost(), Constants.Logs.ERROR_CONNECTION_REFUSED);
        } catch(Exception ex) {
            log.error("Generic error while call eds query check exist ep :" + ex);
            throw new BusinessException("Generic error while call eds query check exist ep :" + ex);
        }

        boolean isSuccessfulCall = response.getStatusCode().is2xxSuccessful();
        boolean isExist = Optional.ofNullable(response.getBody()).orElse(new ResourceExistResDTO()).isExist();
        if (!(isExist && isSuccessfulCall)) {
            log.info(Constants.Logs.DOCUMENT_NOT_FOUND_ON_FHIR_SERVER);
        } else {
            throw new DocumentAlreadyExistsException(Constants.Logs.DOCUMENT_NOT_FOUND_ON_FHIR_SERVER);
        }
    }

    @Override
    public void fhirDelete(String masterIdentifier) {
        log.info("[EDS QUERY] Calling EDS delete ep - START");

        ResponseEntity<ResponseDTO> response = null;
        String url = this.buildRequestPath(masterIdentifier, ProcessorOperationEnum.DELETE);

        try {
            response = restTemplate.exchange(url, HttpMethod.DELETE, null, ResponseDTO.class);
            log.info(Constants.Logs.SRV_QUERY_RESPONSE, response.getStatusCode());
        } catch(ResourceAccessException cex) {
            log.error("Connect error while call eds query delete ep :" + cex);
            throw new ConnectionRefusedException(microservicesURLCFG.getEdsQueryHost(),Constants.Logs.ERROR_CONNECTION_REFUSED);
        } catch(Exception ex) {
            log.error("Generic error while call eds query delete ep :" + ex);
            throw new BusinessException("Generic error while call eds query delete ep :" + ex);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException("Failed to delete resource on FHIR server");
        }
    }

    @Override
    public void fhirPublication(String masterIdentifier, String jsonString, ProcessorOperationEnum processorOperationEnum) {
        log.info("[EDS QUERY] Calling EDS {} QUERY ep - START", processorOperationEnum.getName());

        String url;
        HttpMethod operationMethod = null;

        // prepare request
        FhirPublicationDTO requestBody = new FhirPublicationDTO();
        requestBody.setIdentifier(masterIdentifier);
        requestBody.setJsonString(jsonString);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<?> entity = new HttpEntity<>(StringUtility.toJSON(requestBody), headers);

        switch (processorOperationEnum) {
            case PUBLISH:
                url = this.buildRequestPath(masterIdentifier, ProcessorOperationEnum.PUBLISH);
                operationMethod = HttpMethod.POST;
                break;
            case REPLACE:
                url = this.buildRequestPath(masterIdentifier, ProcessorOperationEnum.REPLACE);
                operationMethod = HttpMethod.PUT;
                break;
            case UPDATE:
                url = this.buildRequestPath(masterIdentifier, ProcessorOperationEnum.UPDATE);
                operationMethod = HttpMethod.PUT;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation for this method");
        }

        ResponseEntity<ResponseDTO> response = null;

        try {
            response = restTemplate.exchange(url, operationMethod, entity, ResponseDTO.class);
            log.info(Constants.Logs.SRV_QUERY_RESPONSE, response.getStatusCode());
        } catch(ResourceAccessException cex) {
            log.error("Connect error while call eds query publish ep :" + cex);
            throw new ConnectionRefusedException(microservicesURLCFG.getEdsQueryHost(),Constants.Logs.ERROR_CONNECTION_REFUSED);
        } catch(Exception ex) {
            log.error("Generic error while call eds query publish ep :" + ex);
            throw new BusinessException("Generic error while call eds query publish ep :" + ex);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException("Failed to publish resource on FHIR server");
        }
    }

    /**
     * Build request path based on incoming operation value
     * @param masterIdentifier  The master identifier of the bundle 
     * @param processorOperationEnum  An enum representing the operation 
     * @return String  The request path 
     */
    private String buildRequestPath(String masterIdentifier, ProcessorOperationEnum processorOperationEnum) {
        log.info("[EDS QUERY] Build req path {} QUERY ep - START", processorOperationEnum.getName());
        switch (processorOperationEnum) {
            case PUBLISH:
                return  microservicesURLCFG.getEdsQueryHost() +
                        "/v1/document/create";
            case DELETE:
                return  microservicesURLCFG.getEdsQueryHost() +
                        "/v1/document/delete/" +
                        masterIdentifier;
            case UPDATE:
                return  microservicesURLCFG.getEdsQueryHost() +
                        "/v1/document/metadata";
            case REPLACE:
                return  microservicesURLCFG.getEdsQueryHost() +
                        "/v1/document/replace";
            case READ:
                return  microservicesURLCFG.getEdsQueryHost() +
                        "/v1/document/check-exist/" +
                        masterIdentifier;
            default:
                throw new UnsupportedOperationException("Unsupported operation");
        }
    }
}
