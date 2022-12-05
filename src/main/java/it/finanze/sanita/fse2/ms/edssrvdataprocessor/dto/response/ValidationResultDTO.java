/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Validation result DTO 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResultDTO {
	
	private boolean isValid;
	
	private String message;
}
