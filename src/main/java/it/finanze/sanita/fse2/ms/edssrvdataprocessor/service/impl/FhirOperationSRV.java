/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ResourceExistResDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.ValidationResultDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.UATMockException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.ITransactionRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IFhirOperationSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.KafkaAbstractSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Date;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum.BLOCKING_ERROR;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum.SUCCESS;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum.VALIDATE_NORMATIVE_R4;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum.VALIDATE_RESOURCE_BUNDLE;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ResultLogEnum.KO;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.TransactionStatusETY.from;

/**
 * FHIR Operation Service Implementation 
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
    
    @Autowired
    private IDocumentRepo documentRepo;
    
    @Override
    public void publish(final FhirOperationDTO dto) {
		log.info("[EDS] Publication - START");
		try {
			ResourceExistResDTO response = queryClient.fhirCheckExist(dto.getMasterIdentifier());
			if(Boolean.FALSE.equals(response.isExist())){
				Date startDate = new Date();
				ValidationResultDTO validatedData = dataQualityClient.validateBundleNormativeR4(dto);
				if(!validatedData.isValid()) {
					sendKafkaInfo(dto.getWorkflowInstanceId(), startDate, validatedData);
					if(dto.isUATMock()) throw new UATMockException(BLOCKING_ERROR, validatedData.getMessage());
				}
				queryClient.fhirPublication(dto.getMasterIdentifier(), dto.getJsonString(), ProcessorOperationEnum.PUBLISH);
				transactionRepo.insert(from(dto.getWorkflowInstanceId(), ProcessorOperationEnum.PUBLISH));
				documentRepo.deleteById(dto.getWorkflowInstanceId(),ProcessorOperationEnum.PUBLISH);
				if (dto.isUATMock()) throw new UATMockException(SUCCESS, validatedData.getMessage());
			} else {
				log.error("Documento già esistente sul server fhir : " + dto.getMasterIdentifier());
				throw new DocumentAlreadyExistsException("Documento già esistente");
			}
		} catch(DocumentAlreadyExistsException | UATMockException | BusinessException daEx) {
			throw daEx;
		} catch(ResourceAccessException cex) {
			log.error("Connect error while call eds query check exist ep :" + cex);
			throw cex;
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
    public void replace(FhirOperationDTO dto) {
        log.info("[EDS] Replace - START");
        try {
			Date startDate = new Date();
			ValidationResultDTO validatedData = dataQualityClient.validateBundleNormativeR4(dto);
			if(!validatedData.isValid()) {
				kafkaLogger.info(dto.getWorkflowInstanceId(), validatedData.getMessage(), VALIDATE_NORMATIVE_R4, KO, startDate);
				if(dto.isUATMock()) throw new UATMockException(BLOCKING_ERROR, validatedData.getMessage());
			}
			queryClient.fhirPublication(dto.getMasterIdentifier(), dto.getJsonString(), ProcessorOperationEnum.REPLACE);
			transactionRepo.insert(from(dto.getWorkflowInstanceId(), ProcessorOperationEnum.REPLACE));
			documentRepo.deleteById(dto.getWorkflowInstanceId(),ProcessorOperationEnum.REPLACE);
			if (dto.isUATMock()) throw new UATMockException(SUCCESS, validatedData.getMessage());
        } catch(UATMockException daEx) {
			throw daEx;
		} catch (Exception e) {
            throw new BusinessException("Error: failed to replace bundle");
        }
    }
    
    private void sendKafkaInfo(String wif, Date timestamp, ValidationResultDTO res) {
		if(!res.getNormativeR4Messages().isEmpty()) {
			kafkaLogger.info(wif, res.getNormativeR4Messages().toString(), VALIDATE_NORMATIVE_R4, KO, timestamp);
		}
		if(!res.getNotTraversedResources().isEmpty()) {
			kafkaLogger.info(wif, res.getNotTraversedResources().toString(), VALIDATE_RESOURCE_BUNDLE, KO, timestamp);
		}
	}
   
}
