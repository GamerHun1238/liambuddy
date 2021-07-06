package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;





public class EnumResolver
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final Class<Enum<?>> _enumClass;
  protected final Enum<?>[] _enums;
  protected final HashMap<String, Enum<?>> _enumsById;
  protected final Enum<?> _defaultValue;
  
  protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue)
  {
    _enumClass = enumClass;
    _enums = enums;
    _enumsById = map;
    _defaultValue = defaultValue;
  }
  




  public static EnumResolver constructFor(Class<Enum<?>> enumCls, AnnotationIntrospector ai)
  {
    Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
    if (enumValues == null) {
      throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
    }
    String[] names = ai.findEnumValues(enumCls, enumValues, new String[enumValues.length]);
    HashMap<String, Enum<?>> map = new HashMap();
    int i = 0; for (int len = enumValues.length; i < len; i++) {
      String name = names[i];
      if (name == null) {
        name = enumValues[i].name();
      }
      map.put(name, enumValues[i]);
    }
    
    Enum<?> defaultEnum = ai.findDefaultEnumValue(enumCls);
    
    return new EnumResolver(enumCls, enumValues, map, defaultEnum);
  }
  


  @Deprecated
  public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls)
  {
    return constructUsingToString(enumCls, null);
  }
  







  public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls, AnnotationIntrospector ai)
  {
    Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
    HashMap<String, Enum<?>> map = new HashMap();
    
    int i = enumValues.length; for (;;) { i--; if (i < 0) break;
      Enum<?> e = enumValues[i];
      map.put(e.toString(), e);
    }
    Enum<?> defaultEnum = ai == null ? null : ai.findDefaultEnumValue(enumCls);
    return new EnumResolver(enumCls, enumValues, map, defaultEnum);
  }
  





  public static EnumResolver constructUsingMethod(Class<Enum<?>> enumCls, AnnotatedMember accessor, AnnotationIntrospector ai)
  {
    Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
    HashMap<String, Enum<?>> map = new HashMap();
    
    int i = enumValues.length; for (;;) { i--; if (i < 0) break;
      Enum<?> en = enumValues[i];
      try {
        Object o = accessor.getValue(en);
        if (o != null) {
          map.put(o.toString(), en);
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("Failed to access @JsonValue of Enum value " + en + ": " + e.getMessage());
      }
    }
    Enum<?> defaultEnum = ai != null ? ai.findDefaultEnumValue(enumCls) : null;
    return new EnumResolver(enumCls, enumValues, map, defaultEnum);
  }
  








  public static EnumResolver constructUnsafe(Class<?> rawEnumCls, AnnotationIntrospector ai)
  {
    Class<Enum<?>> enumCls = rawEnumCls;
    return constructFor(enumCls, ai);
  }
  









  public static EnumResolver constructUnsafeUsingToString(Class<?> rawEnumCls, AnnotationIntrospector ai)
  {
    Class<Enum<?>> enumCls = rawEnumCls;
    return constructUsingToString(enumCls, ai);
  }
  










  public static EnumResolver constructUnsafeUsingMethod(Class<?> rawEnumCls, AnnotatedMember accessor, AnnotationIntrospector ai)
  {
    Class<Enum<?>> enumCls = rawEnumCls;
    return constructUsingMethod(enumCls, accessor, ai);
  }
  
  public CompactStringObjectMap constructLookup() {
    return CompactStringObjectMap.construct(_enumsById);
  }
  
  public Enum<?> findEnum(String key) { return (Enum)_enumsById.get(key); }
  
  public Enum<?> getEnum(int index) {
    if ((index < 0) || (index >= _enums.length)) {
      return null;
    }
    return _enums[index];
  }
  
  public Enum<?> getDefaultValue() {
    return _defaultValue;
  }
  
  public Enum<?>[] getRawEnums() {
    return _enums;
  }
  
  public List<Enum<?>> getEnums() {
    ArrayList<Enum<?>> enums = new ArrayList(_enums.length);
    for (Enum<?> e : _enums) {
      enums.add(e);
    }
    return enums;
  }
  


  public Collection<String> getEnumIds()
  {
    return _enumsById.keySet();
  }
  
  public Class<Enum<?>> getEnumClass() { return _enumClass; }
  
  public int lastValidIndex() { return _enums.length - 1; }
}
