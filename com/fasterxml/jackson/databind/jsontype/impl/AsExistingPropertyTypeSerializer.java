package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;







public class AsExistingPropertyTypeSerializer
  extends AsPropertyTypeSerializer
{
  public AsExistingPropertyTypeSerializer(TypeIdResolver idRes, BeanProperty property, String propName)
  {
    super(idRes, property, propName);
  }
  
  public AsExistingPropertyTypeSerializer forProperty(BeanProperty prop)
  {
    return _property == prop ? this : new AsExistingPropertyTypeSerializer(_idResolver, prop, _typePropertyName);
  }
  
  public JsonTypeInfo.As getTypeInclusion()
  {
    return JsonTypeInfo.As.EXISTING_PROPERTY;
  }
}
