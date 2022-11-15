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

import java.util.Date;

/**
 * Transaction status entity
 */
@Document(collection = "#{@transactionBean}")
@Data
@NoArgsConstructor
public class TransactionStatusETY {

	public static final String FIELD_ID = "_id";
	public static final String FIELD_WIF = "workflow_instance_id";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_INSERTION_DATE = "insertion_date";

	@Id
	private String id; 

	@Field(FIELD_WIF)
	private String workflowInstanceId;

	@Field(FIELD_TYPE)
	private ProcessorOperationEnum type;

	@Field(FIELD_INSERTION_DATE)
	private Date insertionDate;

	public static TransactionStatusETY from(String wif, ProcessorOperationEnum type) {
		TransactionStatusETY out = new TransactionStatusETY();
		out.setWorkflowInstanceId(wif);
		out.setType(type);
		out.setInsertionDate(new Date());
		return out;
	}

}
