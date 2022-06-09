package org.dalvie.otpDeniable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Hashtable;

public class receiver {
    auth    mac;
    Encrypt encr;

    public receiver(){
        mac = new auth();
        encr = new Encrypt();
    }

    public boolean verifyMac(byte[] cipher, byte[] senderMac){
        byte[] computedMac = mac.run(cipher);

        if (Arrays.equals(senderMac, computedMac)){
            return true;
        } else {
            return false;
        }
    }

    private byte[][] splitCipherText(byte[] cipherText) {
        int len = cipherText.length;

        byte[] real = Arrays.copyOfRange(cipherText, 0, len/2);
        byte[] fake = Arrays.copyOfRange(cipherText, (len/2), len);

        byte[][] rtn = {real, fake};
        return rtn;
    }

    public void recieve(Hashtable<String, byte[]> received) {
        byte[] MAC = received.get("MAC");
        byte[] cipherText = received.get("message");

        if (verifyMac(cipherText, MAC) != true) {
            System.out.println("Bad MAC ... process ended");
            return;
        }

        // replace key with fake key

        byte[] messageFull = encr.decr(cipherText);
        byte[][] split = splitCipherText(messageFull);
        byte[] real = split[0];
        byte[] fake = split[1];

        String realString = new String(real, StandardCharsets.UTF_8);
        String fakeString = new String(fake, StandardCharsets.UTF_8);

        System.out.println("Real: " + realString);
        System.out.println("Fake: " + fakeString);

    }
}
