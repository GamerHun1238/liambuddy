package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;









public final class TypeWrappedDeserializer
  extends JsonDeserializer<Object>
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final TypeDeserializer _typeDeserializer;
  protected final JsonDeserializer<Object> _deserializer;
  
  public TypeWrappedDeserializer(TypeDeserializer typeDeser, JsonDeserializer<?> deser)
  {
    _typeDeserializer = typeDeser;
    _deserializer = deser;
  }
  
  public Class<?> handledType()
  {
    return _deserializer.handledType();
  }
  
  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return _deserializer.supportsUpdate(config);
  }
  
  public JsonDeserializer<?> getDelegatee()
  {
    return _deserializer.getDelegatee();
  }
  
  public Collection<Object> getKnownPropertyNames()
  {
    return _deserializer.getKnownPropertyNames();
  }
  
  public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException
  {
    return _deserializer.getNullValue(ctxt);
  }
  
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
  {
    return _deserializer.getEmptyValue(ctxt);
  }
  
  public Object deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    return _deserializer.deserializeWithType(p, ctxt, _typeDeserializer);
  }
  


  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    throw new IllegalStateException("Type-wrapped deserializer's deserializeWithType should never get called");
  }
  




  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue)
    throws IOException
  {
    return _deserializer.deserialize(p, ctxt, intoValue);
  }
}
