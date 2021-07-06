package com.fasterxml.jackson.databind.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;



public class ByteBufferBackedInputStream
  extends InputStream
{
  protected final ByteBuffer _b;
  
  public ByteBufferBackedInputStream(ByteBuffer buf) { _b = buf; }
  
  public int available() { return _b.remaining(); }
  
  public int read() throws IOException {
    return _b.hasRemaining() ? _b.get() & 0xFF : -1;
  }
  
  public int read(byte[] bytes, int off, int len) throws IOException {
    if (!_b.hasRemaining()) return -1;
    len = Math.min(len, _b.remaining());
    _b.get(bytes, off, len);
    return len;
  }
}
