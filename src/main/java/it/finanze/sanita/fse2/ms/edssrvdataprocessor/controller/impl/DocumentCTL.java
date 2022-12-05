/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.IDocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.DocumentResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.OrchestratorSRV;
import lombok.extern.slf4j.Slf4j;

/** 
 * The implementation of the Document Controller 
 */
@RestController
@Slf4j
public class DocumentCTL extends AbstractCTL implements IDocumentCTL {

	/**
	 * Orchestrator Service 
	 */
	@Autowired
	private OrchestratorSRV orchestratorSRV;
	
	/**
	 * Proocess Operation Implementation 
	 */
	@Override
	public DocumentResponseDTO processOperation(DocumentReferenceDTO document)
		throws DocumentNotFoundException, OperationException {
		log.info("Called POST /ingest"); 
		log.info("Received masterIdentifier: " + document.getIdentifier());
		
		// DELETE e UPDATE sync
		orchestratorSRV.dispatchAction(document.getOperation(), DispatchActionDTO
					.builder()
					.mongoId(null)
					.documentReferenceDTO(document)
					.build());
		return new DocumentResponseDTO(getLogTraceInfo(), true);
	}
}
