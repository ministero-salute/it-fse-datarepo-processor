/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

public class BundleNotValidException  extends Exception {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5010722346646050171L;



	/**
     * Complete constructor.
     *
     * @param msg	Message to be shown.
     *              It should describe what the operation was trying to accomplish.
     */
    public BundleNotValidException(final String msg) {
        super(msg);
        
    }
    
}
