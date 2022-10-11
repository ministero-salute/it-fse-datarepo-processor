package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

/**
 * Empty Identifier Exception 
 *
 */
public class EmptyIdentifierException extends Exception {


	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 5488934735322353273L;


	/**
     * Complete constructor.
     *
     * @param msg	Message to be shown.
     *              It should describe what the operation was trying to accomplish.
     */
    public EmptyIdentifierException(final String msg) {
        super(msg);
        
    }
    
}
