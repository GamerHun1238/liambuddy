package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.nio.file.Path;





public class Java7HandlersImpl
  extends Java7Handlers
{
  private final Class<?> _pathClass;
  
  public Java7HandlersImpl()
  {
    _pathClass = Path.class;
  }
  
  public Class<?> getClassJavaNioFilePath()
  {
    return _pathClass;
  }
  
  public JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> rawType)
  {
    if (rawType == _pathClass) {
      return new NioPathDeserializer();
    }
    return null;
  }
  
  public JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> rawType)
  {
    if (_pathClass.isAssignableFrom(rawType)) {
      return new NioPathSerializer();
    }
    return null;
  }
}
