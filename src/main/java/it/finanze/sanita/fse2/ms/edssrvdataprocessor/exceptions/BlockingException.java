package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

public class BlockingException extends RuntimeException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 2940592148957767936L;

	/**
	 * Complete constructor.
	 * 
	 * @param msg Message to be shown.
	 * @param e   Exception to be shown.
	 */
	public BlockingException(final String msg, final Exception e) {
		super(msg, e);
	}

}