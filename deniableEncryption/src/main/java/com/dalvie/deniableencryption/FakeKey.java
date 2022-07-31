package com.dalvie.deniableencryption;

import android.content.Context;

public class FakeKey {

  private int MAC_LENGTH = 32;

  public FakeKey() {  }

  public byte[] generateFakeKey(byte[] cipherBytes, byte[] fakeMessageBytes) {
    byte[] fakeKey = new byte[cipherBytes.length];

    int i = 0;
    for (byte b : fakeMessageBytes) {
      fakeKey[i] = (byte) (b ^ cipherBytes[i++]);
    }

    return fakeKey;
  }

  public void replaceWithFakeKey(Context context, String ReceiverID, byte[] fakeKey, int length) {
    keyHandler keyhandler = new keyHandler();
    int state = keyhandler.getState(context, ReceiverID);
    byte[] fileBytes = keyhandler.getKeyBytes(ReceiverID, context);
    int count = state - MAC_LENGTH - length;

    for(byte b : fakeKey) {
      fileBytes[count++] = b;
    }
  }

}
