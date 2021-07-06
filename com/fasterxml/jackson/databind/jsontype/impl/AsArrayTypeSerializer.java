package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;




public class AsArrayTypeSerializer
  extends TypeSerializerBase
{
  public AsArrayTypeSerializer(TypeIdResolver idRes, BeanProperty property)
  {
    super(idRes, property);
  }
  
  public AsArrayTypeSerializer forProperty(BeanProperty prop)
  {
    return _property == prop ? this : new AsArrayTypeSerializer(_idResolver, prop);
  }
  
  public JsonTypeInfo.As getTypeInclusion() {
    return JsonTypeInfo.As.WRAPPER_ARRAY;
  }
}
