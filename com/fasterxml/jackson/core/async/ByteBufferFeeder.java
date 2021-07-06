package com.fasterxml.jackson.core.async;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ByteBufferFeeder
  extends NonBlockingInputFeeder
{
  public abstract void feedInput(ByteBuffer paramByteBuffer)
    throws IOException;
}
