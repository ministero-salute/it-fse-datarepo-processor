/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.AccreditamentoSimulationDTO;

public interface IAccreditamentoSimulationSRV {

	AccreditamentoSimulationDTO runSimulation(String idDocumento);
}
