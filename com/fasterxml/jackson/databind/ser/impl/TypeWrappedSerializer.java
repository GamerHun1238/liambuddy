package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;






public final class TypeWrappedSerializer
  extends JsonSerializer<Object>
  implements ContextualSerializer
{
  protected final TypeSerializer _typeSerializer;
  protected final JsonSerializer<Object> _serializer;
  
  public TypeWrappedSerializer(TypeSerializer typeSer, JsonSerializer<?> ser)
  {
    _typeSerializer = typeSer;
    _serializer = ser;
  }
  
  public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException
  {
    _serializer.serializeWithType(value, g, provider, _typeSerializer);
  }
  



  public void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    _serializer.serializeWithType(value, g, provider, typeSer);
  }
  
  public Class<Object> handledType() {
    return Object.class;
  }
  







  public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<?> ser = _serializer;
    if ((ser instanceof ContextualSerializer)) {
      ser = provider.handleSecondaryContextualization(ser, property);
    }
    if (ser == _serializer) {
      return this;
    }
    return new TypeWrappedSerializer(_typeSerializer, ser);
  }
  





  public JsonSerializer<Object> valueSerializer()
  {
    return _serializer;
  }
  
  public TypeSerializer typeSerializer() {
    return _typeSerializer;
  }
}
