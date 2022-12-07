/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *	DTO used to return check exist result.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExistResDTO extends ResponseDTO {


	/**
	 * True if the document exists 
	 */
	private boolean exist;

	public ResourceExistResDTO() {
		super();
		exist = false;
	}

	public ResourceExistResDTO(final LogTraceInfoDTO traceInfo, final boolean inExist) {
		super(traceInfo);
		exist = inExist;
	}
}
