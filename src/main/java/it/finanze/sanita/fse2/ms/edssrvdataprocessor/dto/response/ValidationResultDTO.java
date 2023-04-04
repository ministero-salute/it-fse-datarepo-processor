/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Validation result DTO 
 */
@Data
@NoArgsConstructor
public class ValidationResultDTO {
	private boolean isValid;
	private List<String> normativeR4Messages;
	private List<String> notTraversedResources;
	private String message;
}
