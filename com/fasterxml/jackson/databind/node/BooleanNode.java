package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;










public class BooleanNode
  extends ValueNode
{
  private static final long serialVersionUID = 2L;
  public static final BooleanNode TRUE = new BooleanNode(true);
  public static final BooleanNode FALSE = new BooleanNode(false);
  

  private final boolean _value;
  

  protected BooleanNode(boolean v)
  {
    _value = v;
  }
  
  protected Object readResolve() {
    return _value ? TRUE : FALSE;
  }
  
  public static BooleanNode getTrue() { return TRUE; }
  public static BooleanNode getFalse() { return FALSE; }
  
  public static BooleanNode valueOf(boolean b) { return b ? TRUE : FALSE; }
  
  public JsonNodeType getNodeType()
  {
    return JsonNodeType.BOOLEAN;
  }
  
  public JsonToken asToken() {
    return _value ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
  }
  
  public boolean booleanValue()
  {
    return _value;
  }
  
  public String asText()
  {
    return _value ? "true" : "false";
  }
  
  public boolean asBoolean()
  {
    return _value;
  }
  
  public boolean asBoolean(boolean defaultValue)
  {
    return _value;
  }
  
  public int asInt(int defaultValue)
  {
    return _value ? 1 : 0;
  }
  
  public long asLong(long defaultValue) {
    return _value ? 1L : 0L;
  }
  
  public double asDouble(double defaultValue) {
    return _value ? 1.0D : 0.0D;
  }
  
  public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException
  {
    g.writeBoolean(_value);
  }
  
  public int hashCode()
  {
    return _value ? 3 : 1;
  }
  





  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if (!(o instanceof BooleanNode)) {
      return false;
    }
    return _value == _value;
  }
}
