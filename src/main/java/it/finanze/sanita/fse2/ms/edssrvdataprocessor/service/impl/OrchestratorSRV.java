package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.FhirOperationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.IDocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IFhirOperationSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IOrchestratorSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class OrchestratorSRV implements IOrchestratorSRV {

    /**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = 6157760736386483794L; 
	

	@Autowired
    private transient IFhirOperationSRV fhirOperationSRV;

    @Autowired
    private transient IDocumentRepo documentRepo;

    @Value("${eds.dataprocessor.operation.sync}")
    private boolean syncOperation;

    @Override
    public void dispatchAction(ProcessorOperationEnum operationEnum, DispatchActionDTO dispatchActionDTO) throws DocumentNotFoundException {
        log.info("[EDS] Dispatching action from type received: {}", operationEnum.getName());
        FhirOperationDTO fhirOperationDTO = null;
        switch (operationEnum) {
            case PUBLISH:
                fhirOperationDTO = this.extractFhirData(dispatchActionDTO.getMongoId());
                fhirOperationSRV.publish(fhirOperationDTO);
                break;
            case UPDATE:
                if (syncOperation) {
                    String jsonString = dispatchActionDTO.getDocumentReferenceDTO().getJsonString();
                    String masterIdentifier = dispatchActionDTO.getDocumentReferenceDTO().getIdentifier();
                    fhirOperationSRV.update(masterIdentifier, jsonString);
                } else {
                    fhirOperationDTO = this.extractFhirData(dispatchActionDTO.getMongoId());
                    fhirOperationSRV.update(fhirOperationDTO.getMasterIdentifier(), fhirOperationDTO.getJsonString());
                }
                break;
            case REPLACE:
                fhirOperationDTO = this.extractFhirData(dispatchActionDTO.getMongoId());
                fhirOperationSRV.replace(fhirOperationDTO);
                break;
            case DELETE:
                if (syncOperation) {
                    fhirOperationSRV.delete(dispatchActionDTO.getDocumentReferenceDTO().getIdentifier());
                } else {
                    fhirOperationDTO = this.extractFhirData(dispatchActionDTO.getMongoId());
                    fhirOperationSRV.delete(fhirOperationDTO.getMasterIdentifier());
                }
                break;
            default:
                throw new UnsupportedOperationException("Operation not configured");
        }
    }

    /**
     * Extract FHIR data from staging DB
     * @param mongoId
     * @return
     */
    private FhirOperationDTO extractFhirData(String mongoId) throws DocumentNotFoundException {
        DocumentReferenceETY documentReferenceETY = documentRepo.findById(mongoId);

        if (documentReferenceETY == null) {
            throw new DocumentNotFoundException(Constants.Logs.ERROR_DOCUMENT_NOT_FOUND);
        }

        if (!StringUtils.hasText(documentReferenceETY.getIdentifier())) {
            throw new BusinessException("Error: master identifier not defined on DB");
        }
        String masterIdentifier = documentReferenceETY.getIdentifier();
        String jsonString = documentReferenceETY.getJsonString();
        return FhirOperationDTO.builder()
                .masterIdentifier(masterIdentifier)
                .jsonString(jsonString)
                .build();
    } 

}
