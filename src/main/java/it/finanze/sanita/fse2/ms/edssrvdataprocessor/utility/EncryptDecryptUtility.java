/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import org.jasypt.util.text.AES256TextEncryptor;


public class EncryptDecryptUtility {

    private EncryptDecryptUtility() {

    }

    /** 
     *  Method for encrypt 
     *  
     *  @param pwd  The password for the encryption 
     *  @param msg  The message to encrypt 
     *  @return String  The encrypted string 
     */
	public static final String encrypt(String pwd, String msg) {
	    AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
    	textEncryptor.setPassword(pwd);
    	return textEncryptor.encrypt(msg);
	}
	
    /**
     *  Method for decrypt 
     *  
     *  @param pwd  The password for the decryption 
     *  @param cryptedMsg  The encrypted message to decrypt 
     *  @return String  The decrypted message 
     */
    public static String decrypt(String pwd, String cryptedMsg) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(pwd);
        return textEncryptor.decrypt(cryptedMsg);
    }
}