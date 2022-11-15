/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.DocumentResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;

/**
 * Ingestion Controller.
 * 
 */
@RequestMapping(path = "/v1")
@Tag(name = "Document Data Processor Controller")
@Validated
public interface IDocumentCTL {

	/**
	 * Called to process a document bundle inserted from GTW onto EDS 
	 * 
	 * @param request  The HTTP Servlet Request 
	 * @param document  Document Bundle inserted into Data Processor 
	 * @return DocumentResponseDTO  A DTO representing the result of the process 
	 * @throws IOException  A generic IO Exception 
	 * @throws OperationException  A generic MongoDB Exception 
	 * @throws DocumentNotFoundException  An exception which is thrown when a document is not found on the FHIR Server 
	 */
    @PostMapping(value = "/process", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Receives a document to be saved in the staging MongoDB (Update and Delete Flows)", description = "Servizio che consente di salvare un documento alla base dati di staging (Flussi Update e Delete).")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = DocumentResponseDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creazione Documento avvenuta con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DocumentResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))) })
    ResponseEntity<DocumentResponseDTO> processOperation(HttpServletRequest request, @RequestBody DocumentReferenceDTO document) throws IOException, OperationException, DocumentNotFoundException;


}
