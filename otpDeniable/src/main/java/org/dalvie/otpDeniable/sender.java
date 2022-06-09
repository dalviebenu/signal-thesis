package org.thoughtcrime.securesms.otpDeniable;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Hashtable;

public class sender {

    auth    mac;
    Encrypt encr;
    int     n = 5;


    public sender(){

        mac = new auth();
        encr = new Encrypt();
    }

    public byte[] join_message(String message_real, String message_fake) {
        byte byteReal[] = message_real.getBytes(StandardCharsets.UTF_8);
        byte byteFake[] = message_fake.getBytes(StandardCharsets.UTF_8);

        if (byteFake.length != 5 ) {
            update_array(byteFake);
        }
        if (byteReal.length != 5 ) {
            update_array(byteReal);
        }

        return concatenate(byteReal, byteFake);

    }

    public Hashtable<String, byte[]> send(String message_real, String message_fake) {
        byte[] messageFull = join_message(message_real, message_fake);
        byte[] cipherText = encr.encr(messageFull);
        byte[] MAC = mac.run(cipherText);


        // replace key with fake key

        Hashtable<String, byte[]> result = new Hashtable<String, byte[]>();
        result.put("message", cipherText);
        result.put("MAC", MAC);
        return result;
    }

    private byte[] update_array(byte[] arr){
        int i = arr.length;
        arr = Arrays.copyOf(arr, n);
        for(int j = i; j < n; j++) {
            arr[j] = 30;
        }
        return arr;
    }

    private static <T> T concatenate(T a, T b) {
        if (!a.getClass().isArray() || !b.getClass().isArray()) {
            throw new IllegalArgumentException();
        }

        Class<?> resCompType;
        Class<?> aCompType = a.getClass().getComponentType();
        Class<?> bCompType = b.getClass().getComponentType();

        if (aCompType.isAssignableFrom(bCompType)) {
            resCompType = aCompType;
        } else if (bCompType.isAssignableFrom(aCompType)) {
            resCompType = bCompType;
        } else {
            throw new IllegalArgumentException();
        }

        int aLen = Array.getLength(a);
        int bLen = Array.getLength(b);

        @SuppressWarnings("unchecked")
        T result = (T) Array.newInstance(resCompType, aLen + bLen);
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);

        return result;
    }

}
