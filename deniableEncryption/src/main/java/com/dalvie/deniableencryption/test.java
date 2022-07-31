package com.dalvie.deniableencryption;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class test {

    public static void test(String RecieverID, Context context){
        String  keyString     = " The first rule of fight club is you don't talk about fight club.  The second rule of fight club is -- you don't talk about fight club.  The third rule of fight club is -- when someone says stop or goes limp, "
                          + " the fight is over.  Fourth rule is -- only two guys to a fight.  Fifth rule -- one fight at a time.  Sixth  rule -- no shirts, no shoes.  Seventh rule -- fights go on as long as they"
                          + " have to.  And the eighth and final rule -- if this is your first night  at fight club, you have to fight.";
        byte[] key = keyString.getBytes();

        try {
            Random rng = new Random(12345678L);
            //rng.setSeed(12345678L);
            byte[] bytes = new byte[5000];
            rng.nextBytes(bytes);
            // Secure random doesn't produce same bytes every iteration despite setting seed. Using hardcoded byte array instead.

            File file = new File(context.getFilesDir(), RecieverID);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);

        } catch ( FileNotFoundException e) {
            Log.println(Log.ERROR, "error", "File Not Found");
        } catch (IOException e) {
            Log.println(Log.ERROR, "error", "IOException");
        }

    }
}
