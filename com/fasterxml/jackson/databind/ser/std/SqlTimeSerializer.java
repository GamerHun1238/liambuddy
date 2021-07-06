package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.lang.reflect.Type;
import java.sql.Time;

@JacksonStdImpl
public class SqlTimeSerializer extends StdScalarSerializer<Time>
{
  public SqlTimeSerializer()
  {
    super(Time.class);
  }
  
  public void serialize(Time value, JsonGenerator g, SerializerProvider provider) throws java.io.IOException
  {
    g.writeString(value.toString());
  }
  
  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
  {
    return createSchemaNode("string", true);
  }
  

  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws com.fasterxml.jackson.databind.JsonMappingException
  {
    visitStringFormat(visitor, typeHint, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.DATE_TIME);
  }
}
