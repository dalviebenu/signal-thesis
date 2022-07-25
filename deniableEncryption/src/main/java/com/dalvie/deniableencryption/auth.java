package com.dalvie.deniableencryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class auth {
    SecretKey secretKey;

    public auth(){
        try {
            byte[] seed = {42, 69, 30}; // hard coded seed creates same key each instance. VERY BAD ONLY FOR TESTING
            SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.setSeed(seed);
            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA512");
            keygen.init(256, rnd);
            secretKey = keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public byte[] run(byte[] message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(secretKey.getEncoded(), "HmacSHA512"));
            mac.update(message);
            byte[] Mac = mac.doFinal();
            return Mac;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

}
