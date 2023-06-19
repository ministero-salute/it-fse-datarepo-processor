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
