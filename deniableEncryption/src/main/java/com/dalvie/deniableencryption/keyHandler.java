package com.dalvie.deniableencryption;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



public class keyHandler {

  String PREFERENCE_KEY_FILE_NAME = "com.dalvie.deniableencryption";
  private byte[] key;
  private byte[] mac;

  public keyHandler() { }

  /**
   * Returns the byte array to be used as the encryption/decryption key in OTP.
   * @param messageLength The length of the message. To know how long the key must be
   * @param ReceiverID Each receiver has its own key
   * @param context To maintain where in the key we should start.
   * @return
   */
  public void genKeys(int messageLength, String ReceiverID, Context context) {
    try {
      byte[] keyBytes = getKeyBytes(ReceiverID, context);
      int state = getState(context, ReceiverID);
      byte[] key = new byte[messageLength];
      byte[] mac = new byte[32];
      int i;
      for (i = state; i < messageLength + state; i++) {
        key[i - state] = keyBytes[i];
      }

      int j;
      int counter = 0;
      for(j = i; j < i + 32; j++) {
        mac[counter++] = keyBytes[j];
      }

      this.key = key;
      this.mac = mac;
      setState(context, ReceiverID, i + 32);

    } catch ( ArrayIndexOutOfBoundsException e) {
      // What to do if file doesn't exist ? Assume file exists or have function to create key file ?
      // not enough key material
      this.key = null;
    }
  }

  public byte[] getKeyBytes(String ReceiverID, Context context) {
    try {
      File file = new File(context.getFilesDir(), ReceiverID); // Files should be named by their receiverID.
      FileInputStream inStream = new FileInputStream(file);
      byte[] keyBytes = new byte[(int) file.length()];
      inStream.read(keyBytes);
      inStream.close();
      return keyBytes;
    } catch (FileNotFoundException e) {
      // What to do if file doesn't exist ? Assume file exists or have function to create key file ?
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public int getState(Context context, String ReceiverID) {
    SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_KEY_FILE_NAME, Context.MODE_PRIVATE);
    boolean exists = sharedPref.contains(ReceiverID);
    int state;
    if (exists) {
      state = sharedPref.getInt(ReceiverID, -1);
    }
    else {
      state = 0;
      setState(context, ReceiverID, state);
    }
    return state;
  }

  private void setState(Context context, String ReceiverID, int value) {
    SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_KEY_FILE_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putInt(ReceiverID, value);
    editor.apply();
  }

  public void clearState(Context context, String ReceiverID) {
    SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_KEY_FILE_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.remove(ReceiverID);
    editor.apply();
  }

  public byte[] getEncKey() {
    return this.key;
  }

  public byte[] getMacKey() {
    return this.mac;
  }


}