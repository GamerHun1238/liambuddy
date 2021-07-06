package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;





public class POJONode
  extends ValueNode
{
  private static final long serialVersionUID = 2L;
  protected final Object _value;
  
  public POJONode(Object v)
  {
    _value = v;
  }
  





  public JsonNodeType getNodeType()
  {
    return JsonNodeType.POJO;
  }
  
  public JsonToken asToken() { return JsonToken.VALUE_EMBEDDED_OBJECT; }
  





  public byte[] binaryValue()
    throws IOException
  {
    if ((_value instanceof byte[])) {
      return (byte[])_value;
    }
    return super.binaryValue();
  }
  





  public String asText()
  {
    return _value == null ? "null" : _value.toString();
  }
  
  public String asText(String defaultValue) { return _value == null ? defaultValue : _value.toString(); }
  


  public boolean asBoolean(boolean defaultValue)
  {
    if ((_value != null) && ((_value instanceof Boolean))) {
      return ((Boolean)_value).booleanValue();
    }
    return defaultValue;
  }
  

  public int asInt(int defaultValue)
  {
    if ((_value instanceof Number)) {
      return ((Number)_value).intValue();
    }
    return defaultValue;
  }
  

  public long asLong(long defaultValue)
  {
    if ((_value instanceof Number)) {
      return ((Number)_value).longValue();
    }
    return defaultValue;
  }
  

  public double asDouble(double defaultValue)
  {
    if ((_value instanceof Number)) {
      return ((Number)_value).doubleValue();
    }
    return defaultValue;
  }
  






  public final void serialize(JsonGenerator gen, SerializerProvider ctxt)
    throws IOException
  {
    if (_value == null) {
      ctxt.defaultSerializeNull(gen);
    } else if ((_value instanceof JsonSerializable)) {
      ((JsonSerializable)_value).serialize(gen, ctxt);
    }
    else
    {
      ctxt.defaultSerializeValue(_value, gen);
    }
  }
  







  public Object getPojo()
  {
    return _value;
  }
  






  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if ((o instanceof POJONode)) {
      return _pojoEquals((POJONode)o);
    }
    return false;
  }
  



  protected boolean _pojoEquals(POJONode other)
  {
    if (_value == null) {
      return _value == null;
    }
    return _value.equals(_value);
  }
  
  public int hashCode() {
    return _value.hashCode();
  }
}
