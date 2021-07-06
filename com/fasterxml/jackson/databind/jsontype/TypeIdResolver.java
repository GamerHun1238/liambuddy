package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;

public abstract interface TypeIdResolver
{
  public abstract void init(JavaType paramJavaType);
  
  public abstract String idFromValue(Object paramObject);
  
  public abstract String idFromValueAndType(Object paramObject, Class<?> paramClass);
  
  public abstract String idFromBaseType();
  
  public abstract JavaType typeFromId(DatabindContext paramDatabindContext, String paramString)
    throws IOException;
  
  public abstract String getDescForKnownTypeIds();
  
  public abstract JsonTypeInfo.Id getMechanism();
}
