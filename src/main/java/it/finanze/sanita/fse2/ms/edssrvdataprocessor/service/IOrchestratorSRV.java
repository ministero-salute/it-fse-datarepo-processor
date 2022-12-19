/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;

/**
 * Orchestrator Service Interface
 */
public interface IOrchestratorSRV {
	
    /**
     * Dispatch action starting from type of operation
     * 
     * @param operationEnum  The enum of the operation to execute 
     * @param dispatchActionDTO  A DTO representing the document to process  
     * @throws NoRecordFoundException  An exception thrown when the document is not found on MongoDB 
     */
    void dispatchAction(ProcessorOperationEnum operationEnum, DispatchActionDTO dispatchActionDTO) throws NoRecordFoundException, OperationException;

    String getWorkflowInstanceId(String id) throws OperationException;
}
