package org.dalvie.otpDeniable;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

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

    public String doFinalEncrypt(String message, String fakeMessage) {
        /**
         * This method takes in a message and a fake message, combines them, encrypts and returns a string representation of the result according to the
         * otpDeniable FAKES protocol.
         */
        byte[] fake = this.append_0(fakeMessage.getBytes(StandardCharsets.UTF_8)); // get bytes of message that is of size N appended with 0s
        byte[] message_bytes = this.append_0(message.getBytes(StandardCharsets.UTF_8));
        byte[] fin = new byte[this.N * 2];
        System.arraycopy(message_bytes, 0, fin, 0, message_bytes.length);
        System.arraycopy(fake, 0, fin, message_bytes.length, fake.length);
        byte[] ciphertbytes = this.encr(fin);
        return new String(ciphertbytes); // Maybe use base64 ?
    }

    public HashMap<String, String> doFinalDecrypt(String message) {
        /**
         * This method return a key value pair of real message and fake message. It expects an encrypted String, it will extract the decrypted real and fake
         * message from it.
         */
        HashMap<String, String> rtn = new HashMap<>();
        byte[] plainbytes = this.decr(message.getBytes(StandardCharsets.UTF_8));
        byte[] mr = Arrays.copyOfRange(plainbytes, 0, this.N);
        byte[] mf = Arrays.copyOfRange(plainbytes, this.N, this.N * 2);
        byte[] MR = Encrypt.remove_trailing_0(mr);
        byte[] MF = Encrypt.remove_trailing_0(mf);
        String plaintext = new String(MR);
        String fake = new String(MF);
        rtn.put("plaintext", plaintext);
        rtn.put("fake", fake);
        return rtn;
    }
}
