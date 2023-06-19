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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


/**
 * Document Reference Entity 
 */
@Document(collection = "#{@ingestionStagingBean}")
@Data
@NoArgsConstructor
public class IngestionStagingETY {

	public static final String FIELD_ID = "_id";
	public static final String FIELD_IDENTIFIER = "identifier";
	public static final String FIELD_OPERATION = "operation";
	public static final String FIELD_DOCUMENT = "document";
	public static final String FIELD_WIF = "workflow_instance_id";

	/** 
	 * Mongo ID  
	 */
	@Id
	private String id; 

	/**
	 * Identifier 
	 */
	@Field(FIELD_IDENTIFIER)
	private String identifier;
	
	/**
	 * Operation 
	 */
	@Field(FIELD_OPERATION)
	private ProcessorOperationEnum operation;

	/**
	 * Json String
	 */
	@Field(FIELD_DOCUMENT)
	private org.bson.Document document;
	
	/**
	 * Workflow instance id.
	 */
	@Field(FIELD_WIF)
	private String workflowInstanceId;
	
}
