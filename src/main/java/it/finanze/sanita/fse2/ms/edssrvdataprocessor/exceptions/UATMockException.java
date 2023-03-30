package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum;
import lombok.Getter;

@Getter
public class UATMockException  extends RuntimeException {

	private static final long serialVersionUID = 2940592148957767936L;

	private final EventStatusEnum status;
	private final String message;

	public UATMockException(EventStatusEnum status, String message) {
		this.status = status;
		this.message = message;
	}
}
