package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;






public abstract interface TypeResolutionContext
{
  public abstract JavaType resolveType(Type paramType);
  
  public static class Basic
    implements TypeResolutionContext
  {
    private final TypeFactory _typeFactory;
    private final TypeBindings _bindings;
    
    public Basic(TypeFactory tf, TypeBindings b)
    {
      _typeFactory = tf;
      _bindings = b;
    }
    
    public JavaType resolveType(Type type)
    {
      return _typeFactory.constructType(type, _bindings);
    }
  }
}
