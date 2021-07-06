package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;












public class InvalidDefinitionException
  extends JsonMappingException
{
  protected final JavaType _type;
  protected transient BeanDescription _beanDesc;
  protected transient BeanPropertyDefinition _property;
  
  protected InvalidDefinitionException(JsonParser p, String msg, JavaType type)
  {
    super(p, msg);
    _type = type;
    _beanDesc = null;
    _property = null;
  }
  
  protected InvalidDefinitionException(JsonGenerator g, String msg, JavaType type)
  {
    super(g, msg);
    _type = type;
    _beanDesc = null;
    _property = null;
  }
  
  protected InvalidDefinitionException(JsonParser p, String msg, BeanDescription bean, BeanPropertyDefinition prop)
  {
    super(p, msg);
    _type = (bean == null ? null : bean.getType());
    _beanDesc = bean;
    _property = prop;
  }
  
  protected InvalidDefinitionException(JsonGenerator g, String msg, BeanDescription bean, BeanPropertyDefinition prop)
  {
    super(g, msg);
    _type = (bean == null ? null : bean.getType());
    _beanDesc = bean;
    _property = prop;
  }
  
  public static InvalidDefinitionException from(JsonParser p, String msg, BeanDescription bean, BeanPropertyDefinition prop)
  {
    return new InvalidDefinitionException(p, msg, bean, prop);
  }
  
  public static InvalidDefinitionException from(JsonParser p, String msg, JavaType type)
  {
    return new InvalidDefinitionException(p, msg, type);
  }
  
  public static InvalidDefinitionException from(JsonGenerator g, String msg, BeanDescription bean, BeanPropertyDefinition prop)
  {
    return new InvalidDefinitionException(g, msg, bean, prop);
  }
  
  public static InvalidDefinitionException from(JsonGenerator g, String msg, JavaType type)
  {
    return new InvalidDefinitionException(g, msg, type);
  }
  



  public JavaType getType()
  {
    return _type;
  }
  



  public BeanDescription getBeanDescription()
  {
    return _beanDesc;
  }
  




  public BeanPropertyDefinition getProperty()
  {
    return _property;
  }
}
