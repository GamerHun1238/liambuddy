package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.Serializable;

public class NullsFailProvider implements NullValueProvider, Serializable
{
  private static final long serialVersionUID = 1L;
  protected final PropertyName _name;
  protected final JavaType _type;
  
  protected NullsFailProvider(PropertyName name, JavaType type)
  {
    _name = name;
    _type = type;
  }
  
  public static NullsFailProvider constructForProperty(BeanProperty prop) {
    return new NullsFailProvider(prop.getFullName(), prop.getType());
  }
  
  public static NullsFailProvider constructForRootValue(JavaType t) {
    return new NullsFailProvider(null, t);
  }
  

  public AccessPattern getNullAccessPattern()
  {
    return AccessPattern.DYNAMIC;
  }
  
  public Object getNullValue(DeserializationContext ctxt)
    throws JsonMappingException
  {
    throw InvalidNullException.from(ctxt, _name, _type);
  }
}
