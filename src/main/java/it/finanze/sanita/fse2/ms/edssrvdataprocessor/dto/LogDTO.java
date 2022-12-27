/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LogDTO {

	final String log_type = "control-structured-log";
	
	private String message;
	
	private String operation;
	
	private String op_result;
	
	private String op_timestamp_start;
	
	private String op_timestamp_end;
	
	private String op_error;
	
	private String op_error_description;

	private String microservice_name;
	
	private String workflow_instance_id;
	 
}
