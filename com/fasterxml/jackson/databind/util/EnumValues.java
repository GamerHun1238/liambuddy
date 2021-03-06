package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EnumValues
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final Class<Enum<?>> _enumClass;
  private final Enum<?>[] _values;
  private final SerializableString[] _textual;
  private transient EnumMap<?, SerializableString> _asMap;
  
  private EnumValues(Class<Enum<?>> enumClass, SerializableString[] textual)
  {
    _enumClass = enumClass;
    _values = ((Enum[])enumClass.getEnumConstants());
    _textual = textual;
  }
  



  public static EnumValues construct(SerializationConfig config, Class<Enum<?>> enumClass)
  {
    if (config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
      return constructFromToString(config, enumClass);
    }
    return constructFromName(config, enumClass);
  }
  

  public static EnumValues constructFromName(MapperConfig<?> config, Class<Enum<?>> enumClass)
  {
    Class<? extends Enum<?>> enumCls = ClassUtil.findEnumType(enumClass);
    Enum<?>[] enumValues = (Enum[])enumCls.getEnumConstants();
    if (enumValues == null) {
      throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
    }
    String[] names = config.getAnnotationIntrospector().findEnumValues(enumCls, enumValues, new String[enumValues.length]);
    SerializableString[] textual = new SerializableString[enumValues.length];
    int i = 0; for (int len = enumValues.length; i < len; i++) {
      Enum<?> en = enumValues[i];
      String name = names[i];
      if (name == null) {
        name = en.name();
      }
      textual[en.ordinal()] = config.compileString(name);
    }
    return new EnumValues(enumClass, textual);
  }
  
  public static EnumValues constructFromToString(MapperConfig<?> config, Class<Enum<?>> enumClass)
  {
    Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
    Enum<?>[] values = (Enum[])cls.getEnumConstants();
    if (values != null) {
      SerializableString[] textual = new SerializableString[values.length];
      for (Enum<?> en : values) {
        textual[en.ordinal()] = config.compileString(en.toString());
      }
      return new EnumValues(enumClass, textual);
    }
    throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
  }
  
  public SerializableString serializedValueFor(Enum<?> key) {
    return _textual[key.ordinal()];
  }
  
  public Collection<SerializableString> values() {
    return Arrays.asList(_textual);
  }
  




  public List<Enum<?>> enums()
  {
    return Arrays.asList(_values);
  }
  



  public EnumMap<?, SerializableString> internalMap()
  {
    EnumMap<?, SerializableString> result = _asMap;
    if (result == null)
    {
      Map<Enum<?>, SerializableString> map = new LinkedHashMap();
      for (Enum<?> en : _values) {
        map.put(en, _textual[en.ordinal()]);
      }
      result = new EnumMap(map);
    }
    return result;
  }
  

  public Class<Enum<?>> getEnumClass()
  {
    return _enumClass;
  }
}
