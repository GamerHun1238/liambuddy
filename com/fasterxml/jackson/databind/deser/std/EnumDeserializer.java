package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.CompactStringObjectMap;
import com.fasterxml.jackson.databind.util.EnumResolver;
import java.io.IOException;





















@JacksonStdImpl
public class EnumDeserializer
  extends StdScalarDeserializer<Object>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 1L;
  protected Object[] _enumsByIndex;
  private final Enum<?> _enumDefaultValue;
  protected final CompactStringObjectMap _lookupByName;
  protected CompactStringObjectMap _lookupByToString;
  protected final Boolean _caseInsensitive;
  
  public EnumDeserializer(EnumResolver byNameResolver, Boolean caseInsensitive)
  {
    super(byNameResolver.getEnumClass());
    _lookupByName = byNameResolver.constructLookup();
    _enumsByIndex = byNameResolver.getRawEnums();
    _enumDefaultValue = byNameResolver.getDefaultValue();
    _caseInsensitive = caseInsensitive;
  }
  



  protected EnumDeserializer(EnumDeserializer base, Boolean caseInsensitive)
  {
    super(base);
    _lookupByName = _lookupByName;
    _enumsByIndex = _enumsByIndex;
    _enumDefaultValue = _enumDefaultValue;
    _caseInsensitive = caseInsensitive;
  }
  


  @Deprecated
  public EnumDeserializer(EnumResolver byNameResolver)
  {
    this(byNameResolver, null);
  }
  



  @Deprecated
  public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory)
  {
    return deserializerForCreator(config, enumClass, factory, null, null);
  }
  










  public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps)
  {
    if (config.canOverrideAccessModifiers()) {
      ClassUtil.checkAndFixAccess(factory.getMember(), config
        .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    return new FactoryBasedEnumDeserializer(enumClass, factory, factory
      .getParameterType(0), valueInstantiator, creatorProps);
  }
  










  public static JsonDeserializer<?> deserializerForNoArgsCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory)
  {
    if (config.canOverrideAccessModifiers()) {
      ClassUtil.checkAndFixAccess(factory.getMember(), config
        .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    return new FactoryBasedEnumDeserializer(enumClass, factory);
  }
  


  public EnumDeserializer withResolved(Boolean caseInsensitive)
  {
    if (_caseInsensitive == caseInsensitive) {
      return this;
    }
    return new EnumDeserializer(this, caseInsensitive);
  }
  

  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    Boolean caseInsensitive = findFormatFeature(ctxt, property, handledType(), JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    
    if (caseInsensitive == null) {
      caseInsensitive = _caseInsensitive;
    }
    return withResolved(caseInsensitive);
  }
  









  public boolean isCachable()
  {
    return true;
  }
  
  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
  {
    JsonToken curr = p.currentToken();
    

    if ((curr == JsonToken.VALUE_STRING) || (curr == JsonToken.FIELD_NAME))
    {
      CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? _getToStringLookup(ctxt) : _lookupByName;
      String name = p.getText();
      Object result = lookup.find(name);
      if (result == null) {
        return _deserializeAltString(p, ctxt, lookup, name);
      }
      return result;
    }
    
    if (curr == JsonToken.VALUE_NUMBER_INT)
    {
      int index = p.getIntValue();
      if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
        return ctxt.handleWeirdNumberValue(_enumClass(), Integer.valueOf(index), "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow", new Object[0]);
      }
      

      if ((index >= 0) && (index < _enumsByIndex.length)) {
        return _enumsByIndex[index];
      }
      if ((_enumDefaultValue != null) && 
        (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE))) {
        return _enumDefaultValue;
      }
      if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
        return ctxt.handleWeirdNumberValue(_enumClass(), Integer.valueOf(index), "index value outside legal index range [0..%s]", new Object[] {
        
          Integer.valueOf(_enumsByIndex.length - 1) });
      }
      return null;
    }
    return _deserializeOther(p, ctxt);
  }
  






  private final Object _deserializeAltString(JsonParser p, DeserializationContext ctxt, CompactStringObjectMap lookup, String name)
    throws IOException
  {
    name = name.trim();
    if (name.length() == 0) {
      if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
        return getEmptyValue(ctxt);
      }
      
    }
    else if (Boolean.TRUE.equals(_caseInsensitive)) {
      Object match = lookup.findCaseInsensitive(name);
      if (match != null) {
        return match;
      }
    } else if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS))
    {
      char c = name.charAt(0);
      if ((c >= '0') && (c <= '9')) {
        try {
          int index = Integer.parseInt(name);
          if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            return ctxt.handleWeirdStringValue(_enumClass(), name, "value looks like quoted Enum index, but `MapperFeature.ALLOW_COERCION_OF_SCALARS` prevents use", new Object[0]);
          }
          

          if ((index >= 0) && (index < _enumsByIndex.length)) {
            return _enumsByIndex[index];
          }
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
    }
    

    if ((_enumDefaultValue != null) && 
      (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE))) {
      return _enumDefaultValue;
    }
    if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
      return ctxt.handleWeirdStringValue(_enumClass(), name, "not one of the values accepted for Enum class: %s", new Object[] {lookup
        .keys() });
    }
    return null;
  }
  
  protected Object _deserializeOther(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.hasToken(JsonToken.START_ARRAY)) {
      return _deserializeFromArray(p, ctxt);
    }
    return ctxt.handleUnexpectedToken(_enumClass(), p);
  }
  
  protected Class<?> _enumClass() {
    return handledType();
  }
  
  protected CompactStringObjectMap _getToStringLookup(DeserializationContext ctxt)
  {
    CompactStringObjectMap lookup = _lookupByToString;
    

    if (lookup == null) {
      synchronized (this)
      {

        lookup = EnumResolver.constructUnsafeUsingToString(_enumClass(), ctxt.getAnnotationIntrospector()).constructLookup();
      }
      _lookupByToString = lookup;
    }
    return lookup;
  }
}
