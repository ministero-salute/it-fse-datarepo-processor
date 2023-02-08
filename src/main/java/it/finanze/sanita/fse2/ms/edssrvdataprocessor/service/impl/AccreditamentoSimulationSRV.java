package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.impl;

import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.AccreditamentoSimulationDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.AccreditamentoPrefixEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BlockingException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.service.IAccreditamentoSimulationSRV;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccreditamentoSimulationSRV implements IAccreditamentoSimulationSRV {

	
	@Override
	public AccreditamentoSimulationDTO runSimulation(final String idDocumento) {
		AccreditamentoSimulationDTO output = null;
		AccreditamentoPrefixEnum prefixEnum = AccreditamentoPrefixEnum.getStartWith(idDocumento);
		if(prefixEnum!=null) {
			switch (prefixEnum) {
			case CRASH_WF_EDS:
				simulateCrashEdsWorkflow();
				break;
			default:
				break;
			}
		}
		return output;
	}
 

	private void simulateCrashEdsWorkflow() {
		log.info("Crash eds workflow exception");
		throw new BlockingException("Crash eds workflow exception");
	}

}
