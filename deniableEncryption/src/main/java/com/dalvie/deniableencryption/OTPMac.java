package com.dalvie.deniableencryption;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OTPMac {

    public String run(String ciphertext, byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key, "HmacSHA512"));
            byte[] cipherbytes = Base64.decode(ciphertext, Base64.DEFAULT);
            mac.update(cipherbytes);
            byte[] Mac = mac.doFinal();
            return Base64.encodeToString(Mac, Base64.DEFAULT);
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

}
