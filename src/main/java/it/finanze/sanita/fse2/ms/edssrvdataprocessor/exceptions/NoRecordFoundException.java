/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

/**
 * Document Not Found Exception 
 *
 */
public class NoRecordFoundException extends Exception {

	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = -588291874057815740L;
	
	/**
     * Complete constructor.
     *
     * @param msg	Message to be shown.
     *              It should describe what the operation was trying to accomplish.
     */
    public NoRecordFoundException(final String msg) {
        super(msg);
        
    }
    
}