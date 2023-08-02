package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.PriorityTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.IngestionStagingETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IFhirOperationSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IOrchestratorSRV;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class OrchestratorSRVTest {

    @Autowired
    private IOrchestratorSRV service;

    @MockBean
    private IFhirOperationSRV fhirOperationSRV;

    @MockBean
    private IDocumentRepo documentRepo;
    
    @Test
    void dispatchActionPublishTest() throws NoRecordFoundException, OperationException {
        // Data preparation
        DispatchActionDTO actionDto = new DispatchActionDTO();
        DocumentReferenceDTO referenceDto = new DocumentReferenceDTO("test", ProcessorOperationEnum.PUBLISH, "test", PriorityTypeEnum.HIGH);
        actionDto.setMongoId("test");
        actionDto.setDocumentReferenceDTO(referenceDto);
        // Mock
        IngestionStagingETY ingestionEty = new IngestionStagingETY();
        ingestionEty.setId("id_test");
        ingestionEty.setOperation(ProcessorOperationEnum.PUBLISH);
        ingestionEty.setIdentifier("identifier_test");
        ingestionEty.setDocument(new Document());
        when(documentRepo.findById(anyString())).thenReturn(ingestionEty);
        // Perform dispatchAction
        service.dispatchAction(ProcessorOperationEnum.PUBLISH, actionDto);
        // Assertion
        verify(fhirOperationSRV, times(1)).publish(any());
    }

}
