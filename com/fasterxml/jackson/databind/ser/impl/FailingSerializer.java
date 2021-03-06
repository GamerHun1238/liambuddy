package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.lang.reflect.Type;









public class FailingSerializer
  extends StdSerializer<Object>
{
  protected final String _msg;
  
  public FailingSerializer(String msg)
  {
    super(Object.class);
    _msg = msg;
  }
  
  public void serialize(Object value, JsonGenerator g, SerializerProvider provider)
    throws IOException
  {
    provider.reportMappingProblem(_msg, new Object[0]);
  }
  
  public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException
  {
    return null;
  }
  
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) {}
}
