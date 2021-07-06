package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.util.EnumSet;
































public class EnumSetDeserializer
  extends StdDeserializer<EnumSet<?>>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final JavaType _enumType;
  protected final Class<Enum> _enumClass;
  protected JsonDeserializer<Enum<?>> _enumDeserializer;
  protected final NullValueProvider _nullProvider;
  protected final boolean _skipNullValues;
  protected final Boolean _unwrapSingle;
  
  public EnumSetDeserializer(JavaType enumType, JsonDeserializer<?> deser)
  {
    super(EnumSet.class);
    _enumType = enumType;
    _enumClass = enumType.getRawClass();
    
    if (!ClassUtil.isEnumType(_enumClass)) {
      throw new IllegalArgumentException("Type " + enumType + " not Java Enum type");
    }
    _enumDeserializer = deser;
    _unwrapSingle = null;
    _nullProvider = null;
    _skipNullValues = false;
  }
  




  @Deprecated
  protected EnumSetDeserializer(EnumSetDeserializer base, JsonDeserializer<?> deser, Boolean unwrapSingle)
  {
    this(base, deser, _nullProvider, unwrapSingle);
  }
  




  protected EnumSetDeserializer(EnumSetDeserializer base, JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super(base);
    _enumType = _enumType;
    _enumClass = _enumClass;
    _enumDeserializer = deser;
    _nullProvider = nuller;
    _skipNullValues = NullsConstantProvider.isSkipper(nuller);
    _unwrapSingle = unwrapSingle;
  }
  
  public EnumSetDeserializer withDeserializer(JsonDeserializer<?> deser) {
    if (_enumDeserializer == deser) {
      return this;
    }
    return new EnumSetDeserializer(this, deser, _nullProvider, _unwrapSingle);
  }
  
  @Deprecated
  public EnumSetDeserializer withResolved(JsonDeserializer<?> deser, Boolean unwrapSingle) {
    return withResolved(deser, _nullProvider, unwrapSingle);
  }
  



  public EnumSetDeserializer withResolved(JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    if ((_unwrapSingle == unwrapSingle) && (_enumDeserializer == deser) && (_nullProvider == deser)) {
      return this;
    }
    return new EnumSetDeserializer(this, deser, nuller, unwrapSingle);
  }
  











  public boolean isCachable()
  {
    if (_enumType.getValueHandler() != null) {
      return false;
    }
    return true;
  }
  
  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return Boolean.TRUE;
  }
  
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
  {
    return constructSet();
  }
  
  public AccessPattern getEmptyAccessPattern()
  {
    return AccessPattern.DYNAMIC;
  }
  







  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    Boolean unwrapSingle = findFormatFeature(ctxt, property, EnumSet.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    
    JsonDeserializer<?> deser = _enumDeserializer;
    if (deser == null) {
      deser = ctxt.findContextualValueDeserializer(_enumType, property);
    } else {
      deser = ctxt.handleSecondaryContextualization(deser, property, _enumType);
    }
    return withResolved(deser, findContentNullProvider(ctxt, property, deser), unwrapSingle);
  }
  






  public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    EnumSet result = constructSet();
    
    if (!p.isExpectedStartArrayToken()) {
      return handleNonArray(p, ctxt, result);
    }
    return _deserialize(p, ctxt, result);
  }
  


  public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt, EnumSet<?> result)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      return handleNonArray(p, ctxt, result);
    }
    return _deserialize(p, ctxt, result);
  }
  

  protected final EnumSet<?> _deserialize(JsonParser p, DeserializationContext ctxt, EnumSet result)
    throws IOException
  {
    try
    {
      JsonToken t;
      while ((t = p.nextToken()) != JsonToken.END_ARRAY)
      {
        Enum<?> value;
        

        if (t == JsonToken.VALUE_NULL) {
          if (_skipNullValues) {
            continue;
          }
          Enum<?> value = (Enum)_nullProvider.getNullValue(ctxt);
        } else {
          value = (Enum)_enumDeserializer.deserialize(p, ctxt);
        }
        if (value != null) {
          result.add(value);
        }
      }
    } catch (Exception e) {
      throw JsonMappingException.wrapWithPath(e, result, result.size()); }
    JsonToken t;
    return result;
  }
  


  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException, JsonProcessingException
  {
    return typeDeserializer.deserializeTypedFromArray(p, ctxt);
  }
  

  private EnumSet constructSet()
  {
    return EnumSet.noneOf(_enumClass);
  }
  


  protected EnumSet<?> handleNonArray(JsonParser p, DeserializationContext ctxt, EnumSet result)
    throws IOException
  {
    if (_unwrapSingle != Boolean.TRUE) if (_unwrapSingle != null) break label31;
    label31:
    boolean canWrap = ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    
    if (!canWrap) {
      return (EnumSet)ctxt.handleUnexpectedToken(EnumSet.class, p);
    }
    
    if (p.hasToken(JsonToken.VALUE_NULL)) {
      return (EnumSet)ctxt.handleUnexpectedToken(_enumClass, p);
    }
    try {
      Enum<?> value = (Enum)_enumDeserializer.deserialize(p, ctxt);
      if (value != null) {
        result.add(value);
      }
    } catch (Exception e) {
      throw JsonMappingException.wrapWithPath(e, result, result.size());
    }
    return result;
  }
}
