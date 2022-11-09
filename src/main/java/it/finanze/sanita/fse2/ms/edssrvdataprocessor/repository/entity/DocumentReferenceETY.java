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
 *
 */
@Document(collection = "ingestion-staging")
@Data
@NoArgsConstructor
public class DocumentReferenceETY {

	/** 
	 * Mongo ID  
	 */
	@Id
	private String id; 

	/**
	 * Identifier 
	 */
	@Field("identifier")
	private String identifier;
	
	/**
	 * Operation 
	 */
	@Field("operation")
	private ProcessorOperationEnum operation;

	/**
	 * Json String
	 */
	@Field("document")
	private org.bson.Document document;
	
}
