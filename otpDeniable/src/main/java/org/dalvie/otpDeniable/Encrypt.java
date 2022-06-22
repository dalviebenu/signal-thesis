package org.dalvie.otpDeniable;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Encrypt {

    int state;
    SecureRandom rng;
    int SIZE = 100;
    byte keys[];
    public final int N = 10;

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

    public byte[] encr(byte[] byte_message) {
        // byte byte_message[] = message.getBytes(StandardCharsets.UTF_8);


        if ((state + byte_message.length) > keys.length) {
            return null;
        }

        byte[] key = Arrays.copyOfRange(keys, state, (state + byte_message.length));
        state = state + byte_message.length;

        byte[] cipher = new byte[byte_message.length]; // cipher same size as message
        int i = 0;
        for (byte b : byte_message) {
            cipher[i] = (byte) (b ^ key[i++]);
        }

        return cipher;

    }

    public String encr(String message) {
        byte[] message_bytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertext = encr(message_bytes);
        return new String(ciphertext);
    }

    public byte[] decr(byte[] ciphertext) {
        // byte byte_message[] = message.getBytes();

        if ((state + ciphertext.length) > keys.length) {
            return null;
        }

        byte[] key = Arrays.copyOfRange(keys, state, (state + ciphertext.length));
        state = state + ciphertext.length;

        byte[] rtn = new byte[ciphertext.length]; // cipher same size as message
        int i = 0;
        for (byte b : ciphertext) {
            rtn[i] = (byte) (b ^ key[i++]);
        }

        String out = new String(rtn, StandardCharsets.UTF_8);
        return rtn;
    }

    public String decr(String message) {
        byte[] cipherbytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] plaintext = decr(cipherbytes);
        return new String(plaintext);
    }

    public byte[] append_0(byte[] arr) {
        if (arr.length < N) {
            byte[] rtn = Arrays.copyOf(arr, N);
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
}
