/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY.from;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.ITransactionRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IFhirOperationSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.KafkaAbstractSRV;
import lombok.extern.slf4j.Slf4j;

/**
 * FHIR Operation Service Implementation 
 * 
 */
@Service
@Slf4j
public class FhirOperationSRV extends KafkaAbstractSRV implements IFhirOperationSRV {

	/**
	 * Srv Query Client 
	 */
    @Autowired
    private IEdsQueryClient queryClient;

    @Autowired
    private ITransactionRepo transactionRepo;

    /**
     * Data Quality Client 
     */
    @Autowired
    private IEdsDataQualityClient dataQualityClient;

    @Autowired
    private LoggerHelper kafkaLogger;
    
    @Override
    public void publish(final FhirOperationDTO fhirOperationDTO) {
    	log.info("[EDS] Publication - START");
    	try {
    		ResourceExistResDTO response = queryClient.fhirCheckExist(fhirOperationDTO.getMasterIdentifier());
    		if(Boolean.FALSE.equals(response.isExist())){
    			Date startDate = new Date();
    			ValidationResultDTO validatedData = dataQualityClient.validateBundleNormativeR4(fhirOperationDTO);
    			if(!validatedData.isValid()) {
    				kafkaLogger.info(validatedData.getMessage(), OperationLogEnum.VALIDATE_NORMATIVE_R4, ResultLogEnum.KO, startDate);
    			}
    			queryClient.fhirPublication(fhirOperationDTO.getMasterIdentifier(), fhirOperationDTO.getJsonString(), ProcessorOperationEnum.PUBLISH);
    			transactionRepo.insert(from(fhirOperationDTO.getWorkflowInstanceId(), ProcessorOperationEnum.PUBLISH));
    		} else {
    			log.error("Documento già esistente sul server fhir : " + fhirOperationDTO.getMasterIdentifier());
    			throw new DocumentAlreadyExistsException("Documento già esistente"); 
    		}
    	} catch (Exception ex) {
    		log.error("Error: failed to publish bundle", ex);
    		throw new BusinessException("Error: failed to publish bundle", ex);
    	}
    }

    @Override
    public void update(String masterIdentifier, String jsonString) {
        log.info("[EDS] Update - START");
        try {
            // 1. Update document reference
            queryClient.fhirPublication(masterIdentifier, jsonString, ProcessorOperationEnum.UPDATE);
        } catch (Exception e) {
            throw new BusinessException("Error: failed to update document reference");
        }
    }

    @Override
    public void delete(String identifier) {
        log.info("[EDS] Delete - START");
        try {
            // 1. delete on FHIR through query client API
           queryClient.fhirDelete(identifier);
        } catch (Exception e) {
            throw new BusinessException("Error: failed to delete bundle");
        }
    }

    @Override
    public void replace(FhirOperationDTO fhirOperationDTO) {
        log.info("[EDS] Replace - START");
        try {
        	Date startDate = new Date();
        	ValidationResultDTO validatedData = dataQualityClient.validateBundleNormativeR4(fhirOperationDTO);
			if(!validatedData.isValid()) {
				kafkaLogger.info(validatedData.getMessage(), OperationLogEnum.VALIDATE_NORMATIVE_R4, ResultLogEnum.KO, startDate);
			}
			queryClient.fhirPublication(fhirOperationDTO.getMasterIdentifier(), fhirOperationDTO.getJsonString(), ProcessorOperationEnum.REPLACE);
			transactionRepo.insert(from(fhirOperationDTO.getWorkflowInstanceId(), ProcessorOperationEnum.REPLACE));
        } catch (Exception e) {
            throw new BusinessException("Error: failed to replace bundle");
        }
    }
    
    
   
}
