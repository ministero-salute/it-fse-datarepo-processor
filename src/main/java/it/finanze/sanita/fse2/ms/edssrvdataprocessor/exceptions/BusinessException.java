package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

/**
 * @author AndreaPerquoti
 * 
 *         Generic business exception.
 *
 */
public class BusinessException extends RuntimeException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 2940592148957767936L;

	
	/**
	 * Message constructor.
	 * 
	 * @param msg Message to be shown.
	 */
	public BusinessException(final String msg) {
		super(msg);
	}

	/**
	 * Complete constructor.
	 * 
	 * @param msg Message to be shown.
	 * @param e   Exception to be shown.
	 */
	public BusinessException(final String msg, final Exception e) {
		super(msg, e);
	}

	/**
	 * Exception constructor.
	 * 
	 * @param e Exception to be shown.
	 */
	public BusinessException(final Exception e) {
		super(e);
	}

}