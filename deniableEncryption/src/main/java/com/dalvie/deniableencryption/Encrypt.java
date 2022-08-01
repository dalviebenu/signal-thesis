package com.dalvie.deniableencryption;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

import android.util.Base64;

public class Encrypt {

    private static int n = 255;
    int state;
    SecureRandom rng;
    int SIZE = 100;
    byte keys[];
    public final int N = 255;

    public Encrypt(){
        byte[] seed = {42, 69, 30};
        state = 0;
        try {
            rng = new SecureRandom();
            rng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        rng.setSeed(seed); // replace this later so that rnd byte array is common between encr and decr
        keys = generate_array(SIZE);
    }


    private byte[] generate_array(int size) { // this array should be shared. (key material)
        byte bytes[] = new byte[size];
        rng.nextBytes(bytes);
        return bytes;
    }

    public void update_key(byte[] key) {
        this.keys = key;
    }

    public byte[] encr(byte[] byte_message, byte[] key) {

        byte[] cipher = new byte[byte_message.length]; // cipher same size as message
        int i = 0;
        for (byte b : byte_message) {
            cipher[i] = (byte) (b ^ key[i++]);
        }

        return cipher;

    }

    public String encr(String message, byte[] key) {
        byte[] message_bytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertext = encr(message_bytes, key);
        return new String(ciphertext);
    }

    public byte[] decr(byte[] ciphertext, byte[] key) {

        byte[] rtn = new byte[ciphertext.length]; // cipher same size as message
        int i = 0;
        for (byte b : ciphertext) {
            rtn[i] = (byte) (b ^ key[i++]);
        }

        String out = new String(rtn, StandardCharsets.UTF_8);
        return rtn;
    }

    public String decr(String message, byte[] key) {
        byte[] cipherbytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] plaintext = decr(cipherbytes, key);
        return new String(plaintext);
    }

    public static byte[] append_0(byte[] arr) {
        if (arr.length < n) {
            byte[] rtn = Arrays.copyOf(arr, n);
            return rtn;
        }
        return arr;
    }

    public static byte[] remove_trailing_0(byte[] arr) {
        int pos = arr.length - 1;
        while (arr[pos] == 0) {
            --pos;
        }
        return Arrays.copyOfRange(arr,0,  pos + 1);
    }

    public String doFinalEncrypt(String message, byte[] key) {
        /**
         * This method takes in a message (string), encrypts and returns a string representation of the result according to the
         * otpDeniable FAKES protocol.
         * UTF-8 -> Byte Array -> Base64
         */
        // byte[] fake = append_0(fakeMessage.getBytes(StandardCharsets.UTF_8)); // get bytes of message that is of size N appended with 0s
        // byte[] message_bytes = append_0(message.getBytes(StandardCharsets.UTF_8));
        //byte[] fin = new byte[this.N * 2];
        //System.arraycopy(message_bytes, 0, fin, 0, message_bytes.length);
        //System.arraycopy(fake, 0, fin, message_bytes.length, fake.length);
        byte[] ciphertbytes = this.encr(message.getBytes(), key);
        return Base64.encodeToString(ciphertbytes, Base64.DEFAULT);
        // return new String(ciphertbytes);
    }

    public String doFinalDecrypt(String message, byte[] key) {
        /**
         * This method return a key value pair of real message and fake message. It expects an encrypted String, it will extract the combined decrypted real and fake
         * message from it.
         * Base64 String -> Byte Array -> UTF-8
         */
        HashMap<String, String> rtn = new HashMap<>();
        byte[] base64Decoded = Base64.decode(message, Base64.DEFAULT);
        byte[] plainbytes = this.decr(base64Decoded, key);
        // byte[] plainbytes = this.decr(message.getBytes(StandardCharsets.UTF_8), key);
        // byte[] mr = Arrays.copyOfRange(plainbytes, 0, this.N);
        // byte[] mf = Arrays.copyOfRange(plainbytes, this.N, this.N * 2);
        // byte[] MR = Encrypt.remove_trailing_0(mr);
        // byte[] MF = Encrypt.remove_trailing_0(mf);
        // String plaintext = new String(MR);
        // String fake = new String(MF);
        // rtn.put("plaintext", plaintext);
        // rtn.put("fake", fake);
        return new String(plainbytes);
    }
}
