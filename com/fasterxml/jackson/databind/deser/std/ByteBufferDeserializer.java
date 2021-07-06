package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferDeserializer extends StdScalarDeserializer<ByteBuffer>
{
  private static final long serialVersionUID = 1L;
  
  protected ByteBufferDeserializer()
  {
    super(ByteBuffer.class);
  }
  
  public ByteBuffer deserialize(JsonParser parser, DeserializationContext cx) throws IOException {
    byte[] b = parser.getBinaryValue();
    return ByteBuffer.wrap(b);
  }
  
  public ByteBuffer deserialize(JsonParser jp, DeserializationContext ctxt, ByteBuffer intoValue)
    throws IOException
  {
    java.io.OutputStream out = new com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream(intoValue);
    jp.readBinaryValue(ctxt.getBase64Variant(), out);
    out.close();
    return intoValue;
  }
}
