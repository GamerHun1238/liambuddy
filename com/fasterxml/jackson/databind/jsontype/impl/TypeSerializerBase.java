package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;




public abstract class TypeSerializerBase
  extends TypeSerializer
{
  protected final TypeIdResolver _idResolver;
  protected final BeanProperty _property;
  
  protected TypeSerializerBase(TypeIdResolver idRes, BeanProperty property)
  {
    _idResolver = idRes;
    _property = property;
  }
  



  public abstract JsonTypeInfo.As getTypeInclusion();
  



  public String getPropertyName()
  {
    return null;
  }
  
  public TypeIdResolver getTypeIdResolver() { return _idResolver; }
  

  public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId idMetadata)
    throws IOException
  {
    _generateTypeId(idMetadata);
    return g.writeTypePrefix(idMetadata);
  }
  

  public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId idMetadata)
    throws IOException
  {
    return g.writeTypeSuffix(idMetadata);
  }
  




  protected void _generateTypeId(WritableTypeId idMetadata)
  {
    Object id = id;
    if (id == null) {
      Object value = forValue;
      Class<?> typeForId = forValueType;
      if (typeForId == null) {
        id = idFromValue(value);
      } else {
        id = idFromValueAndType(value, typeForId);
      }
      id = id;
    }
  }
  





  protected String idFromValue(Object value)
  {
    String id = _idResolver.idFromValue(value);
    if (id == null) {
      handleMissingId(value);
    }
    return id;
  }
  
  protected String idFromValueAndType(Object value, Class<?> type) {
    String id = _idResolver.idFromValueAndType(value, type);
    if (id == null) {
      handleMissingId(value);
    }
    return id;
  }
  
  protected void handleMissingId(Object value) {}
}
