/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
@Document(collection = "#{@referenceBean}")
@Data
@NoArgsConstructor
public class DocumentReferenceETY {

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
