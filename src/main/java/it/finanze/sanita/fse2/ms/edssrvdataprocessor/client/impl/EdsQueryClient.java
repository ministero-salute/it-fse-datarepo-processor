/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

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
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.UnknownException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * The implementation of the Srv Query Client 
 */
@Slf4j
@Component
public class EdsQueryClient implements IEdsQueryClient {

    /**
     * Rest Template 
     */
    @Autowired
    private RestTemplate restTemplate;

    /*
     * Microservices URL Config 
     */
    @Autowired
    private MicroservicesURLCFG microservicesURLCFG;
    
    @Autowired
    private ProfileUtility profileUtility;
 
    @Override
    public ResourceExistResDTO fhirCheckExist(final String masterIdentifier) throws DocumentAlreadyExistsException {
    	log.debug("[EDS QUERY] Calling EDS check exist ep - START");
    	ResponseEntity<ResourceExistResDTO> response = null;
    	String url = microservicesURLCFG.getEdsQueryHost() + "/v1/document/check-exist/" + masterIdentifier;

    	try {
    		response = restTemplate.getForEntity(url, ResourceExistResDTO.class);
    		log.info(Constants.Logs.SRV_QUERY_RESPONSE, response.getStatusCode());
    	} catch(ResourceAccessException cex) {
    		log.error("Connect error while call eds query check exist ep :" + cex);
    		throw cex;
    	}  
    	return response.getBody();
    }


    @Override
    public void fhirDelete(String masterIdentifier) {
    	log.info("[EDS QUERY] Calling EDS delete ep - START");

    	ResponseEntity<ResponseDTO> response = null;
    	String url = microservicesURLCFG.getEdsQueryHost() + "/v1/document/delete/" + masterIdentifier;

    	try {
    		response = restTemplate.exchange(url, HttpMethod.DELETE, null, ResponseDTO.class);
    		log.info(Constants.Logs.SRV_QUERY_RESPONSE, response.getStatusCode());
    	} catch(ResourceAccessException cex) {
    		log.error("Connect error while call eds query delete ep :" + cex);
    		throw new ConnectionRefusedException(microservicesURLCFG.getEdsQueryHost(),Constants.Logs.ERROR_CONNECTION_REFUSED);
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
                url = microservicesURLCFG.getEdsQueryHost() + "/v1/document/create";
                operationMethod = HttpMethod.POST;
                break;
            case REPLACE:
                url = microservicesURLCFG.getEdsQueryHost() + "/v1/document/replace";
                operationMethod = HttpMethod.PUT;
                break;
            case UPDATE:
                url = microservicesURLCFG.getEdsQueryHost() + "/v1/document/metadata/" + masterIdentifier;
                operationMethod = HttpMethod.PUT;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation for this method");
        }

        try {
        	ResponseEntity<ResponseDTO> response = restTemplate.exchange(url, operationMethod, entity, ResponseDTO.class);
        	
        	if(response.getBody()!=null) {
        		if(profileUtility.isDevProfile() && "Eccezione di test".equals(response.getBody().getMessage())) {
        			throw new UnknownException("Eccezione di test");
        		}
        	}
        	
            log.info(Constants.Logs.SRV_QUERY_RESPONSE, response.getStatusCode());
        } catch(ResourceAccessException cex) {
            log.error("Connect error while call eds query publish ep :" + cex);
            throw new ConnectionRefusedException(microservicesURLCFG.getEdsQueryHost(),Constants.Logs.ERROR_CONNECTION_REFUSED);
        } catch(Exception ex) {
            log.error("Generic error while call eds query publish ep :" + ex);
            throw new BusinessException("Generic error while call eds query publish ep :" + ex);
        }
 
    }
    
}
