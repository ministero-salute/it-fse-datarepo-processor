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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.LogDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ILogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
 
@Service
@Slf4j
public class LoggerHelper {
    
	Logger kafkaLog = LoggerFactory.getLogger("kafka-logger"); 
	
	
	@Value("${log.kafka-log.enable}")
	private boolean kafkaLogEnable;

	@Value("${spring.application.name}")
	private String msName;
	
	/* 
	 * Specify here the format for the dates 
	 */
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS"); 
	
	/* 
	 * Implements structured logs, at all logging levels
	 */
	public void trace(String workflowInstanceId, String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation) {
		
		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				microservice_name(msName).
				workflow_instance_id(workflowInstanceId).
				build();

		final String logMessage = StringUtility.toJSON(logDTO);
		log.trace(logMessage);

		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.trace(logMessage);
		}
	}

	public void debug(String workflowInstanceId, String message,  ILogEnum operation, ResultLogEnum result, Date startDateOperation) {

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				microservice_name(msName).
				workflow_instance_id(workflowInstanceId).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.debug(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.debug(logMessage);
		}
	} 
	 
	public void info(String workflowInstanceId, String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation) {

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				microservice_name(msName).
				workflow_instance_id(workflowInstanceId).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.info(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.info(logMessage);
			log.info("After send kafka message");
		}
	} 
	
	public void warn(String workflowInstanceId, String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation) {

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				microservice_name(msName).
				workflow_instance_id(workflowInstanceId).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.warn(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.warn(logMessage);
		}
 
	} 
	
	public void error(String workflowInstanceId, String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation,ILogEnum error) {
		
		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				op_error(error.getCode()).
				op_error_description(error.getDescription()).
				microservice_name(msName).
				workflow_instance_id(workflowInstanceId).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.error(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.error(logMessage);
		}
		
	}
 
	
}
