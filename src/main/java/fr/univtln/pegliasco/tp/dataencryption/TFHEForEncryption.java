package fr.univtln.pegliasco.tp.dataencryption;

import io.github.cdimascio.dotenv.Dotenv;

public class TFHEForEncryption {
    private int[] key = searchPrivateKey();

    /**
     * Load the native library "tfhe" at runtime. This library contains the native
     * methods for encryption and decryption.
     */
    static {
        System.loadLibrary("tfhe_encryptor");
    }

    /**
     * Retrieves the string key used for encryption from environment variable
     * 
     * @return private key convert to int array.
     */
    private int[] searchPrivateKey() {
        String environmentKey = Dotenv.configure().load().get("DB_KEY");
        char[] keyChars = environmentKey.toCharArray();
        int[] privateKey = new int[keyChars.length];
        for (int i = 0; i < keyChars.length; i++) {
            privateKey[i] = Character.getNumericValue(keyChars[i]);
        }
        return privateKey;
    }

    public native void encrypt();

    public native void decrypt();

}