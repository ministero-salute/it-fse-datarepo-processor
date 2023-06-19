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
