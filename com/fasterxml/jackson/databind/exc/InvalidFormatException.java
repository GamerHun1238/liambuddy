package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;























public class InvalidFormatException
  extends MismatchedInputException
{
  private static final long serialVersionUID = 1L;
  protected final Object _value;
  
  @Deprecated
  public InvalidFormatException(String msg, Object value, Class<?> targetType)
  {
    super(null, msg);
    _value = value;
    _targetType = targetType;
  }
  




  @Deprecated
  public InvalidFormatException(String msg, JsonLocation loc, Object value, Class<?> targetType)
  {
    super(null, msg, loc);
    _value = value;
    _targetType = targetType;
  }
  




  public InvalidFormatException(JsonParser p, String msg, Object value, Class<?> targetType)
  {
    super(p, msg, targetType);
    _value = value;
  }
  

  public static InvalidFormatException from(JsonParser p, String msg, Object value, Class<?> targetType)
  {
    return new InvalidFormatException(p, msg, value, targetType);
  }
  











  public Object getValue()
  {
    return _value;
  }
}
