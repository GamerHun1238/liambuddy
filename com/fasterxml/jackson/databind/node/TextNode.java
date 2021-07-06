package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;




public class TextNode
  extends ValueNode
{
  private static final long serialVersionUID = 2L;
  static final TextNode EMPTY_STRING_NODE = new TextNode("");
  protected final String _value;
  
  public TextNode(String v) {
    _value = v;
  }
  








  public static TextNode valueOf(String v)
  {
    if (v == null) {
      return null;
    }
    if (v.length() == 0) {
      return EMPTY_STRING_NODE;
    }
    return new TextNode(v);
  }
  
  public JsonNodeType getNodeType()
  {
    return JsonNodeType.STRING;
  }
  
  public JsonToken asToken() { return JsonToken.VALUE_STRING; }
  
  public String textValue()
  {
    return _value;
  }
  





  public byte[] getBinaryValue(Base64Variant b64variant)
    throws IOException
  {
    String str = _value.trim();
    ByteArrayBuilder builder = new ByteArrayBuilder(4 + (str.length() * 3 >> 2));
    try {
      b64variant.decode(str, builder);
    } catch (IllegalArgumentException e) {
      throw InvalidFormatException.from(null, 
        String.format("Cannot access contents of TextNode as binary due to broken Base64 encoding: %s", new Object[] {e
        
        .getMessage() }), str, [B.class);
    }
    
    return builder.toByteArray();
  }
  
  public byte[] binaryValue() throws IOException
  {
    return getBinaryValue(Base64Variants.getDefaultVariant());
  }
  






  public String asText()
  {
    return _value;
  }
  
  public String asText(String defaultValue)
  {
    return _value == null ? defaultValue : _value;
  }
  


  public boolean asBoolean(boolean defaultValue)
  {
    if (_value != null) {
      String v = _value.trim();
      if ("true".equals(v)) {
        return true;
      }
      if ("false".equals(v)) {
        return false;
      }
    }
    return defaultValue;
  }
  
  public int asInt(int defaultValue)
  {
    return NumberInput.parseAsInt(_value, defaultValue);
  }
  
  public long asLong(long defaultValue)
  {
    return NumberInput.parseAsLong(_value, defaultValue);
  }
  
  public double asDouble(double defaultValue)
  {
    return NumberInput.parseAsDouble(_value, defaultValue);
  }
  






  public final void serialize(JsonGenerator g, SerializerProvider provider)
    throws IOException
  {
    if (_value == null) {
      g.writeNull();
    } else {
      g.writeString(_value);
    }
  }
  







  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if ((o instanceof TextNode)) {
      return _value.equals(_value);
    }
    return false;
  }
  
  public int hashCode() {
    return _value.hashCode();
  }
  
  @Deprecated
  protected static void appendQuoted(StringBuilder sb, String content) {
    sb.append('"');
    CharTypes.appendQuoted(sb, content);
    sb.append('"');
  }
}
