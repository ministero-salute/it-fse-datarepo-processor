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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
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

    @MockitoBean
    private IFhirOperationSRV fhirOperationSRV;

    @MockitoBean
    private IDocumentRepo documentRepo;

    @Test
    void dispatchActionPublishTest() throws NoRecordFoundException, OperationException {
        // Data preparation
        DispatchActionDTO actionDto = new DispatchActionDTO();
        DocumentReferenceDTO referenceDto = new DocumentReferenceDTO("test", ProcessorOperationEnum.PUBLISH, "test");
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
