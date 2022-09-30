package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsDataQualityClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.IEdsQueryClient;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.FhirNormalizedDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.IDocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IFhirOperationSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FhirOperationSRV implements IFhirOperationSRV {

    @Autowired
    private IEdsQueryClient queryClient;

    @Autowired
    private IEdsDataQualityClient dataQualityClient;

    @Autowired
    private IDocumentRepo documentRepo;

    @Override
    public void publish(FhirOperationDTO fhirOperationDTO) {
        log.info("[EDS] Publication - START");
        try {
            // 1. Check FHIR existence
            queryClient.fhirCheckExist(fhirOperationDTO.getMasterIdentifier());

            // 2. Normalize
            FhirNormalizedDTO normalizedData = dataQualityClient.normalize(fhirOperationDTO);
            log.info("identifier: {} ; normalized data: {}", normalizedData.getMasterIdentifier(), normalizedData.isNormalized());

            // 3. Publish on FHIR through query client API
            queryClient.fhirPublication(normalizedData.getMasterIdentifier(), normalizedData.getJsonString(), ProcessorOperationEnum.PUBLISH);
        } catch (Exception e) {
            throw new BusinessException("Error: failed to publish bundle");
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
            // 1. Normalize
            FhirNormalizedDTO normalizedData = dataQualityClient.normalize(fhirOperationDTO);
            log.info("identifier: {} ; normalized data: {}", normalizedData.getMasterIdentifier(), normalizedData.isNormalized());

            // 2. Publish on FHIR through query client API
            queryClient.fhirPublication(normalizedData.getMasterIdentifier(), normalizedData.getJsonString(), ProcessorOperationEnum.REPLACE);
        } catch (Exception e) {
            throw new BusinessException("Error: failed to replace bundle");
        }
    }
}
