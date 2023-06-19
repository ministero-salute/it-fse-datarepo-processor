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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.IDocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.DocumentResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
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
		throws NoRecordFoundException, OperationException {
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
