package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;









public class AsWrapperTypeSerializer
  extends TypeSerializerBase
{
  public AsWrapperTypeSerializer(TypeIdResolver idRes, BeanProperty property)
  {
    super(idRes, property);
  }
  
  public AsWrapperTypeSerializer forProperty(BeanProperty prop)
  {
    return _property == prop ? this : new AsWrapperTypeSerializer(_idResolver, prop);
  }
  
  public JsonTypeInfo.As getTypeInclusion() {
    return JsonTypeInfo.As.WRAPPER_OBJECT;
  }
  










  protected String _validTypeId(String typeId)
  {
    return ClassUtil.nonNullString(typeId);
  }
  
  protected final void _writeTypeId(JsonGenerator g, String typeId)
    throws IOException
  {
    if (typeId != null) {
      g.writeTypeId(typeId);
    }
  }
}
