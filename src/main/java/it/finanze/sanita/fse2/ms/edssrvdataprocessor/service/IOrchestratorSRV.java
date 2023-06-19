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
