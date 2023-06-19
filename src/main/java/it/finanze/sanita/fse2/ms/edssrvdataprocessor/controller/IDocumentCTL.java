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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.DocumentResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_PROCESSOR_TAG;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.RoutesUtility.API_PROCESS_PATH;

/**
 * Ingestion Controller.
 */
@Tag(name = API_PROCESSOR_TAG)
@Validated
public interface IDocumentCTL {

	/**
	 * Called to process a document bundle inserted from GTW onto EDS 
	 * 
	 * @param document  Document Bundle inserted into Data Processor
	 * @return DocumentResponseDTO  A DTO representing the result of the process 
	 * @throws IOException  A generic IO Exception 
	 * @throws OperationException  A generic MongoDB Exception 
	 * @throws NoRecordFoundException  An exception which is thrown when a document is not found on the FHIR Server 
	 */
    @PostMapping(value = API_PROCESS_PATH, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Receives a document to be saved in the staging MongoDB (Update and Delete Flows)", description = "Servizio che consente di salvare un documento alla base dati di staging (Flussi Update e Delete).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creazione Documento avvenuta con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DocumentResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))
	})
    DocumentResponseDTO processOperation(@RequestBody DocumentReferenceDTO document) throws IOException, OperationException, NoRecordFoundException;
}
