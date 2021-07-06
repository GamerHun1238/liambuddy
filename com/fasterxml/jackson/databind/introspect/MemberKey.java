package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;






public final class MemberKey
{
  static final Class<?>[] NO_CLASSES = new Class[0];
  
  final String _name;
  final Class<?>[] _argTypes;
  
  public MemberKey(Method m)
  {
    this(m.getName(), m.getParameterTypes());
  }
  
  public MemberKey(Constructor<?> ctor)
  {
    this("", ctor.getParameterTypes());
  }
  
  public MemberKey(String name, Class<?>[] argTypes)
  {
    _name = name;
    _argTypes = (argTypes == null ? NO_CLASSES : argTypes);
  }
  
  public String getName() {
    return _name;
  }
  
  public int argCount() {
    return _argTypes.length;
  }
  
  public String toString()
  {
    return _name + "(" + _argTypes.length + "-args)";
  }
  
  public int hashCode()
  {
    return _name.hashCode() + _argTypes.length;
  }
  

  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if (o.getClass() != getClass()) {
      return false;
    }
    MemberKey other = (MemberKey)o;
    if (!_name.equals(_name)) {
      return false;
    }
    Class<?>[] otherArgs = _argTypes;
    int len = _argTypes.length;
    if (otherArgs.length != len) {
      return false;
    }
    for (int i = 0; i < len; i++) {
      Class<?> type1 = otherArgs[i];
      Class<?> type2 = _argTypes[i];
      if (type1 != type2)
      {


















        return false; }
    }
    return true;
  }
}
