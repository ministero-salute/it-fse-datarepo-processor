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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.MicroservicesURLCFG;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EdsDataQualityClient implements IEdsDataQualityClient {

    /** 
     * Rest Template 
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Microservices URL Config 
     */
    @Autowired
    private MicroservicesURLCFG microservicesURLCFG;

    @Override
    public ValidationResultDTO validateBundleNormativeR4(FhirOperationDTO input) {
    	log.debug("[EDS DATAQUALITY] Calling EDS validate ep - START");

    	String url = microservicesURLCFG.getEdsDataQualityHost() + "/v1/validate-bundle";
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Content-Type", "application/json");
    	HttpEntity<?> entity = new HttpEntity<>(input, headers);
    	ResponseEntity<ValidationResultDTO> response = null;

    	try {
    		response = restTemplate.exchange(url, HttpMethod.POST, entity, ValidationResultDTO.class);
    		log.debug("{} status returned from eds", response.getStatusCode());
    	} catch(ResourceAccessException cex) {
    		log.error("Connect error while call eds query publish ep :" + cex);
    		throw new ConnectionRefusedException(microservicesURLCFG.getEdsQueryHost(), Constants.Logs.ERROR_CONNECTION_REFUSED);
    	}  

    	return response.getBody();
    }
}
