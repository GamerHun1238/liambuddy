package com.fasterxml.jackson.core.async;

import java.io.IOException;

public abstract interface ByteArrayFeeder
  extends NonBlockingInputFeeder
{
  public abstract void feedInput(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
}
