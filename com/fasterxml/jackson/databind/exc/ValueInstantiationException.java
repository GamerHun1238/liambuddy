package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

















public class ValueInstantiationException
  extends JsonMappingException
{
  protected final JavaType _type;
  
  protected ValueInstantiationException(JsonParser p, String msg, JavaType type, Throwable cause)
  {
    super(p, msg, cause);
    _type = type;
  }
  
  protected ValueInstantiationException(JsonParser p, String msg, JavaType type)
  {
    super(p, msg);
    _type = type;
  }
  
  public static ValueInstantiationException from(JsonParser p, String msg, JavaType type)
  {
    return new ValueInstantiationException(p, msg, type);
  }
  
  public static ValueInstantiationException from(JsonParser p, String msg, JavaType type, Throwable cause)
  {
    return new ValueInstantiationException(p, msg, type, cause);
  }
  



  public JavaType getType()
  {
    return _type;
  }
}
