package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.impl;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.IDocumentCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DocumentReferenceDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.DocumentResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.UIDModeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl.OrchestratorSRV;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/** 
 * The implementation of the Document Controller 
 * 
 * @author Guido Rocco
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
	 * Serial version UID 
	 */
	private static final long serialVersionUID = -8298415975725845794L; 
	
	

	@Override
	public ResponseEntity<DocumentResponseDTO> processOperation(HttpServletRequest request, @RequestBody DocumentReferenceDTO document)
			throws DocumentNotFoundException {
		log.info("Called POST /ingest"); 
		log.info("Received masterIdentifier: " + document.getIdentifier());
		final String transactionId = StringUtility.generateTransactionUID(UIDModeEnum.UUID_UUID);
		
		// DELETE e UPDATE sync
		orchestratorSRV.dispatchAction(document.getOperation(), DispatchActionDTO
					.builder()
					.mongoId(null)
					.documentReferenceDTO(document)
					.build());
		return new ResponseEntity<>(new DocumentResponseDTO(getLogTraceInfo(), transactionId, true), HttpStatus.OK);
	}
}
