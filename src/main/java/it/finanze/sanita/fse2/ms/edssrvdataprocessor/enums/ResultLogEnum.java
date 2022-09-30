package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

@Getter
public enum ResultLogEnum implements ILogEnum {

	OK("OK", "Operazione eseguita con successo"),
	KO("KO", "Errore nell'esecuzione dell'operazione");

	private String code;
	private String description;

	ResultLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}
}

