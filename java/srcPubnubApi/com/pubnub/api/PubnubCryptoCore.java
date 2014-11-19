package com.pubnub.api;


import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PubNub 3.1 Cryptography
 *
 */
abstract class PubnubCryptoCore {


    String CIPHER_KEY;

    public PubnubCryptoCore(String CIPHER_KEY) {
        this.CIPHER_KEY = CIPHER_KEY;
    }
    public PubnubCryptoCore(String CIPHER_KEY, String initialization_vector) {

    }

    public String encrypt(String input)  {
    	return input;
    }

    public String decrypt(String cipher_text)  {
    	return cipher_text;
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                                  .digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Get MD5
     *
     * @param input
     * @return byte[]
     */
    public static byte[] md5(String input) {
    	
    	MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
	        //Add input bytes to digest
	        md.update(input.getBytes());
	        //Get the hash's bytes
	        return md.digest();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
    }

    /**
     * Get SHA256
     *
     * @param input
     * @return byte[]
     */
    public static byte[] sha256(byte[] input) {
    	MessageDigest sha;
		try {
			sha = MessageDigest.getInstance("SHA-256");
	        //Add input bytes to digest
	        sha.update(input);
	        //Get the hash's bytes
	        return sha.digest();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
    }

}
