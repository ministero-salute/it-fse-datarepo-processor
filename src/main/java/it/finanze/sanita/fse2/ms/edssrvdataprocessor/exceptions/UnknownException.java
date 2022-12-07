package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

public class UnknownException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4420700371354323215L;

	/**
	 * Message constructor.
	 * 
	 * @param msg	Message to be shown.
	 */
	public UnknownException(final String msg) {
		super(msg);
	}
	
}
