package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import org.jasypt.util.text.AES256TextEncryptor;


public class EncryptDecryptUtility {

    private EncryptDecryptUtility() {

    }

    /** 
     *  Metodo per l'encrypt.
     *  @param pwd
     *  @param msg
     *  @return String
     */
	public static final String encrypt(String pwd, String msg) {
	    AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
    	textEncryptor.setPassword(pwd);
    	return textEncryptor.encrypt(msg);
	}
	
    /**
     *  Metodo per il decrypt.
     *  @param pwd
     *  @param cryptedMsg
     *  @return String
     */
    public static String decrypt(String pwd, String cryptedMsg) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(pwd);
        return textEncryptor.decrypt(cryptedMsg);
    }
}