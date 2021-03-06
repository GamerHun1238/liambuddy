package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.JsonDeserializer;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;





public class JdkDeserializers
{
  private static final HashSet<String> _classNames = new HashSet();
  
  static {
    Class<?>[] types = { UUID.class, AtomicBoolean.class, StackTraceElement.class, ByteBuffer.class, Void.class };
    





    for (Class<?> cls : types) _classNames.add(cls.getName());
    for (Class<?> cls : FromStringDeserializer.types()) _classNames.add(cls.getName());
  }
  
  public static JsonDeserializer<?> find(Class<?> rawType, String clsName)
  {
    if (_classNames.contains(clsName)) {
      JsonDeserializer<?> d = FromStringDeserializer.findDeserializer(rawType);
      if (d != null) {
        return d;
      }
      if (rawType == UUID.class) {
        return new UUIDDeserializer();
      }
      if (rawType == StackTraceElement.class) {
        return new StackTraceElementDeserializer();
      }
      if (rawType == AtomicBoolean.class)
      {
        return new AtomicBooleanDeserializer();
      }
      if (rawType == ByteBuffer.class) {
        return new ByteBufferDeserializer();
      }
      if (rawType == Void.class) {
        return NullifyingDeserializer.instance;
      }
    }
    return null;
  }
  
  public JdkDeserializers() {}
}
