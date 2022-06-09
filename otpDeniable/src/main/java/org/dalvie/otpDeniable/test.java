package org.dalvie.otpDeniable;

import java.util.Hashtable;

public class test {

    public static void main(String[] args){
        String real = "Hello";
        String fake = "Fakes";

        sender   Sender   = new sender();
        receiver Receiver = new receiver();

        Hashtable<String, byte[]> result =  Sender.send(real, fake);
        Receiver.recieve(result);

    }
}
