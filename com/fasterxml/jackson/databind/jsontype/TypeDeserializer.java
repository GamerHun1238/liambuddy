package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;


































































































public abstract class TypeDeserializer
{
  public TypeDeserializer() {}
  
  public abstract TypeDeserializer forProperty(BeanProperty paramBeanProperty);
  
  public abstract JsonTypeInfo.As getTypeInclusion();
  
  public abstract String getPropertyName();
  
  public abstract TypeIdResolver getTypeIdResolver();
  
  public abstract Class<?> getDefaultImpl();
  
  public abstract Object deserializeTypedFromObject(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
    throws IOException;
  
  public abstract Object deserializeTypedFromArray(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
    throws IOException;
  
  public abstract Object deserializeTypedFromScalar(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
    throws IOException;
  
  public abstract Object deserializeTypedFromAny(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
    throws IOException;
  
  public static Object deserializeIfNatural(JsonParser p, DeserializationContext ctxt, JavaType baseType)
    throws IOException
  {
    return deserializeIfNatural(p, ctxt, baseType.getRawClass());
  }
  

  public static Object deserializeIfNatural(JsonParser p, DeserializationContext ctxt, Class<?> base)
    throws IOException
  {
    JsonToken t = p.currentToken();
    if (t == null) {
      return null;
    }
    switch (1.$SwitchMap$com$fasterxml$jackson$core$JsonToken[t.ordinal()]) {
    case 1: 
      if (base.isAssignableFrom(String.class)) {
        return p.getText();
      }
      break;
    case 2: 
      if (base.isAssignableFrom(Integer.class)) {
        return Integer.valueOf(p.getIntValue());
      }
      
      break;
    case 3: 
      if (base.isAssignableFrom(Double.class)) {
        return Double.valueOf(p.getDoubleValue());
      }
      break;
    case 4: 
      if (base.isAssignableFrom(Boolean.class)) {
        return Boolean.TRUE;
      }
      break;
    case 5: 
      if (base.isAssignableFrom(Boolean.class)) {
        return Boolean.FALSE;
      }
      break;
    }
    return null;
  }
}
