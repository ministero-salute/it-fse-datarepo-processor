package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "ingestion-staging")
@Data
@NoArgsConstructor
public class DocumentReferenceETY {

	@Id
	private String id; 

	@Field("identifier")
	private String identifier;
	
	@Field("operation")
	private ProcessorOperationEnum operation;

	@Field("json_string")
	private String jsonString;
	
}
