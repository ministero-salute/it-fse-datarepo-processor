package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.DispatchActionDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;

import java.io.Serializable;

public interface IOrchestratorSRV extends Serializable {
    /**
     * Dispatch action starting from type of operation
     * @param operationEnum
     * @param identifier
     * @return
     */
    void dispatchAction(ProcessorOperationEnum operationEnum, DispatchActionDTO dispatchActionDTO) throws DocumentNotFoundException;
}
