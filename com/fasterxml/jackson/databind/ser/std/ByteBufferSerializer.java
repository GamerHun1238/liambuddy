package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferSerializer extends StdScalarSerializer<ByteBuffer>
{
  public ByteBufferSerializer()
  {
    super(ByteBuffer.class);
  }
  
  public void serialize(ByteBuffer bbuf, JsonGenerator gen, SerializerProvider provider)
    throws java.io.IOException
  {
    if (bbuf.hasArray()) {
      gen.writeBinary(bbuf.array(), bbuf.arrayOffset(), bbuf.limit());
      return;
    }
    

    ByteBuffer copy = bbuf.asReadOnlyBuffer();
    if (copy.position() > 0) {
      copy.rewind();
    }
    InputStream in = new ByteBufferBackedInputStream(copy);
    gen.writeBinary(in, copy.remaining());
    in.close();
  }
  


  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws com.fasterxml.jackson.databind.JsonMappingException
  {
    JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
    if (v2 != null) {
      v2.itemsFormat(com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes.INTEGER);
    }
  }
}
