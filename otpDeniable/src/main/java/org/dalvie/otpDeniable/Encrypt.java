package org.thoughtcrime.securesms.otpDeniable;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Encrypt {

    int state;
    SecureRandom rng;
    int SIZE = 10;
    byte keys[];

    public Encrypt(){
        byte[] seed = {42, 69, 30};
        state = 0;
        rng = new SecureRandom(seed); // replace this later so that rnd byte array is common between encr and decr
        //keys = generate_array(SIZE);
        //key hard coded
        keys = new byte[]{102, -2, 117, -15, 36, -123, 53, -85, -86, 10, -2, -81, 64, -18, 103, 42, 5, -20, 119, 11};
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
        byte[] ciphertext = decr(message_bytes);
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
}
