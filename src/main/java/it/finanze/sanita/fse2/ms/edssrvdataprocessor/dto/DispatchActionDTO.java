/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dispatch Action DTO 
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchActionDTO {
    /**
     * Mongo id passed on kafka topic, defined if async operation (PUBLISH/REPLACE) (TBD: UPDATE/DELETE)
     */
    private String mongoId;

    /**
     * Document reference passed on http req, defined if sync operation (DELETE/UPDATE)
     */
    private DocumentReferenceDTO documentReferenceDTO;
}
