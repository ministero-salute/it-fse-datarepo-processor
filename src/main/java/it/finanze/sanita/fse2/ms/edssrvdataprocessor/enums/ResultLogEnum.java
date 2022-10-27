package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

/**
 * Enum that describes the operation result in Structured Logs  
 *
 */
@Getter
public enum ResultLogEnum implements ILogEnum {

	OK("OK", "Operazione eseguita con successo"),
	KO("KO", "Errore nell'esecuzione dell'operazione");

	/**
	 * Result Code (OK, KO) 
	 */
	private String code;
	
	/**
	 * Result Description 
	 */
	private String description;

	ResultLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}
}

