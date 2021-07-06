package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class StdJdkSerializers
{
  public StdJdkSerializers() {}
  
  public static java.util.Collection<java.util.Map.Entry<Class<?>, Object>> all()
  {
    HashMap<Class<?>, Object> sers = new HashMap();
    

    sers.put(java.net.URL.class, new ToStringSerializer(java.net.URL.class));
    sers.put(java.net.URI.class, new ToStringSerializer(java.net.URI.class));
    
    sers.put(Currency.class, new ToStringSerializer(Currency.class));
    sers.put(java.util.UUID.class, new UUIDSerializer());
    sers.put(Pattern.class, new ToStringSerializer(Pattern.class));
    sers.put(Locale.class, new ToStringSerializer(Locale.class));
    

    sers.put(AtomicBoolean.class, AtomicBooleanSerializer.class);
    sers.put(AtomicInteger.class, AtomicIntegerSerializer.class);
    sers.put(AtomicLong.class, AtomicLongSerializer.class);
    

    sers.put(java.io.File.class, FileSerializer.class);
    sers.put(Class.class, ClassSerializer.class);
    

    sers.put(Void.class, NullSerializer.instance);
    sers.put(Void.TYPE, NullSerializer.instance);
    


    try
    {
      sers.put(java.sql.Timestamp.class, DateSerializer.instance);
      

      sers.put(java.sql.Date.class, SqlDateSerializer.class);
      sers.put(java.sql.Time.class, SqlTimeSerializer.class);
    }
    catch (NoClassDefFoundError localNoClassDefFoundError) {}
    

    return sers.entrySet();
  }
  




  public static class AtomicBooleanSerializer
    extends StdScalarSerializer<AtomicBoolean>
  {
    public AtomicBooleanSerializer()
    {
      super(false);
    }
    
    public void serialize(AtomicBoolean value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
      gen.writeBoolean(value.get());
    }
    
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    {
      return createSchemaNode("boolean", true);
    }
    
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
      visitor.expectBooleanFormat(typeHint);
    }
  }
  
  public static class AtomicIntegerSerializer extends StdScalarSerializer<AtomicInteger>
  {
    public AtomicIntegerSerializer() {
      super(false);
    }
    
    public void serialize(AtomicInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
      gen.writeNumber(value.get());
    }
    
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    {
      return createSchemaNode("integer", true);
    }
    
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
      throws JsonMappingException
    {
      visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
    }
  }
  
  public static class AtomicLongSerializer extends StdScalarSerializer<AtomicLong>
  {
    public AtomicLongSerializer() {
      super(false);
    }
    
    public void serialize(AtomicLong value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
      gen.writeNumber(value.get());
    }
    
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    {
      return createSchemaNode("integer", true);
    }
    

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
      throws JsonMappingException
    {
      visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG);
    }
  }
}
