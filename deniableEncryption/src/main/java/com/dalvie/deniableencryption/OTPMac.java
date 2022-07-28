package com.dalvie.deniableencryption;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OTPMac {

    public String run(String ciphertext, byte[] key) {
        /**
         *  This function generates a message authentication code from a key (byte array) and a message (string). The MAC is generated using the HMACSHA512
         *  algorithm. This function is used inside the verify function to authenticate the MAC on the receiving side.
         *  Input: String Message in Base64, byte array key
         *  Output: String Base64 representation of the resulting MAC from the message and key.
         */
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key, "HmacSHA512"));
            byte[] cipherbytes = Base64.decode(ciphertext, Base64.DEFAULT);
            // byte[] cipherbytes = ciphertext.getBytes(StandardCharsets.UTF_8);
            mac.update(cipherbytes);
            byte[] Mac = mac.doFinal();
            return Base64.encodeToString(Mac, Base64.DEFAULT);
            // return new String(Mac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean verify(String Mac, String ciphertext, byte[] key) {
        String computedMac = run(ciphertext, key);
        if (Mac.equals(computedMac)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean verify(byte[] Mac, byte[] cipherbytes, byte[] key) {

        String ciphertext = Base64.encodeToString(cipherbytes, Base64.DEFAULT);
        // String ciphertext = new String(cipherbytes);
        String computedMac = run(ciphertext, key);
        //byte[] computedMacBytes = computedMac.getBytes(StandardCharsets.UTF_8);
        byte[] computedMacBytes = Base64.decode(computedMac, Base64.DEFAULT);
        // Formatting does not affect byte comparison. Mac can be formatted any which way outside.
        if (Arrays.equals(Mac, computedMacBytes)) {
            return true;
        } else {
            return false;
        }

    }

}