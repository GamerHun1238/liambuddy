package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;



















public class JSONPObject
  implements JsonSerializable
{
  protected final String _function;
  protected final Object _value;
  protected final JavaType _serializationType;
  
  public JSONPObject(String function, Object value)
  {
    this(function, value, (JavaType)null);
  }
  
  public JSONPObject(String function, Object value, JavaType asType)
  {
    _function = function;
    _value = value;
    _serializationType = asType;
  }
  








  public void serializeWithType(JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    serialize(gen, provider);
  }
  


  public void serialize(JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    gen.writeRaw(_function);
    gen.writeRaw('(');
    
    if (_value == null) {
      provider.defaultSerializeNull(gen);

    }
    else
    {
      boolean override = gen.getCharacterEscapes() == null;
      if (override) {
        gen.setCharacterEscapes(JsonpCharacterEscapes.instance());
      }
      try
      {
        if (_serializationType != null) {
          provider.findTypedValueSerializer(_serializationType, true, null).serialize(_value, gen, provider);
        } else {
          provider.findTypedValueSerializer(_value.getClass(), true, null).serialize(_value, gen, provider);
        }
      } finally {
        if (override) {
          gen.setCharacterEscapes(null);
        }
      }
    }
    gen.writeRaw(')');
  }
  






  public String getFunction() { return _function; }
  public Object getValue() { return _value; }
  public JavaType getSerializationType() { return _serializationType; }
}
