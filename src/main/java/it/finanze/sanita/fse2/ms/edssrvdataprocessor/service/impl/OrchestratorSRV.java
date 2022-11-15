/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.IDocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IFhirOperationSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IOrchestratorSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Orchestrator Service Implementation 
 *
 */
@Service
@Slf4j
public class OrchestratorSRV implements IOrchestratorSRV {
	
	/**
	 * FHIR Operation Service 
	 */
	@Autowired
    private IFhirOperationSRV fhirOperationSRV;

	/**
	 * Document Repo 
	 */
    @Autowired
    private IDocumentRepo documentRepo;

    @Override
    public void dispatchAction(ProcessorOperationEnum operationEnum, DispatchActionDTO dispatchActionDTO) throws DocumentNotFoundException, OperationException {
        log.info("[EDS] Dispatching action from type received: {}", operationEnum.getName());
        FhirOperationDTO fhirOperationDTO;
        switch (operationEnum) {
            case PUBLISH:
                fhirOperationDTO = extractFhirData(dispatchActionDTO.getMongoId());
                fhirOperationSRV.publish(fhirOperationDTO);
                break;
            case UPDATE:
                String jsonString = dispatchActionDTO.getDocumentReferenceDTO().getJsonString();
                String masterIdentifier = dispatchActionDTO.getDocumentReferenceDTO().getIdentifier();
                fhirOperationSRV.update(masterIdentifier, jsonString);
                break;
            case REPLACE:
                fhirOperationDTO = extractFhirData(dispatchActionDTO.getMongoId());
                fhirOperationSRV.replace(fhirOperationDTO);
                break;
            case DELETE:
                fhirOperationSRV.delete(dispatchActionDTO.getDocumentReferenceDTO().getIdentifier());
                break;
            default:
                throw new UnsupportedOperationException("Operation not configured");
        }
    }

    /**
     * Extract FHIR data from staging DB
     * @param mongoId  The Mongo ID of the document 
     * @return FhirOperationDTO  A DTO representing the retrieved document 
     */
    private FhirOperationDTO extractFhirData(String mongoId) throws DocumentNotFoundException, OperationException {
        DocumentReferenceETY documentReferenceETY = documentRepo.findById(mongoId);

        if (documentReferenceETY == null) {
            throw new DocumentNotFoundException(Constants.Logs.ERROR_DOCUMENT_NOT_FOUND);
        }

        if (!StringUtils.hasText(documentReferenceETY.getIdentifier())) {
            throw new BusinessException("Error: master identifier not defined on DB");
        }
        String masterIdentifier = documentReferenceETY.getIdentifier();
        String jsonString = documentReferenceETY.getDocument().toJson();
        return FhirOperationDTO.builder()
                .masterIdentifier(masterIdentifier)
                .jsonString(jsonString)
                .workflowInstanceId(documentReferenceETY.getWorkflowInstanceId())
                .build();
    } 

    public String getWorkflowInstanceId(String id) throws OperationException {
        Optional<DocumentReferenceETY> entity = Optional.ofNullable(documentRepo.findById(id));
        return entity.isPresent() ? entity.get().getWorkflowInstanceId() : Constants.App.MISSING_WORKFLOW_PLACEHOLDER;
    }

}
