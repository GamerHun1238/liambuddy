package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.util.EnumMap;






































public class EnumMapDeserializer
  extends ContainerDeserializerBase<EnumMap<?, ?>>
  implements ContextualDeserializer, ResolvableDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final Class<?> _enumClass;
  protected KeyDeserializer _keyDeserializer;
  protected JsonDeserializer<Object> _valueDeserializer;
  protected final TypeDeserializer _valueTypeDeserializer;
  protected final ValueInstantiator _valueInstantiator;
  protected JsonDeserializer<Object> _delegateDeserializer;
  protected PropertyBasedCreator _propertyBasedCreator;
  
  public EnumMapDeserializer(JavaType mapType, ValueInstantiator valueInst, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller)
  {
    super(mapType, nuller, null);
    _enumClass = mapType.getKeyType().getRawClass();
    _keyDeserializer = keyDeser;
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = vtd;
    _valueInstantiator = valueInst;
  }
  





  protected EnumMapDeserializer(EnumMapDeserializer base, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller)
  {
    super(base, nuller, _unwrapSingle);
    _enumClass = _enumClass;
    _keyDeserializer = keyDeser;
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = vtd;
    
    _valueInstantiator = _valueInstantiator;
    _delegateDeserializer = _delegateDeserializer;
    _propertyBasedCreator = _propertyBasedCreator;
  }
  

  @Deprecated
  public EnumMapDeserializer(JavaType mapType, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd)
  {
    this(mapType, null, keyDeser, valueDeser, vtd, null);
  }
  


  public EnumMapDeserializer withResolved(KeyDeserializer keyDeserializer, JsonDeserializer<?> valueDeserializer, TypeDeserializer valueTypeDeser, NullValueProvider nuller)
  {
    if ((keyDeserializer == _keyDeserializer) && (nuller == _nullProvider) && (valueDeserializer == _valueDeserializer) && (valueTypeDeser == _valueTypeDeserializer))
    {
      return this;
    }
    return new EnumMapDeserializer(this, keyDeserializer, valueDeserializer, valueTypeDeser, nuller);
  }
  








  public void resolve(DeserializationContext ctxt)
    throws JsonMappingException
  {
    if (_valueInstantiator != null) {
      if (_valueInstantiator.canCreateUsingDelegate()) {
        JavaType delegateType = _valueInstantiator.getDelegateType(ctxt.getConfig());
        if (delegateType == null) {
          ctxt.reportBadDefinition(_containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] { _containerType, _valueInstantiator
          

            .getClass().getName() }));
        }
        



        _delegateDeserializer = findDeserializer(ctxt, delegateType, null);
      } else if (_valueInstantiator.canCreateUsingArrayDelegate()) {
        JavaType delegateType = _valueInstantiator.getArrayDelegateType(ctxt.getConfig());
        if (delegateType == null) {
          ctxt.reportBadDefinition(_containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] { _containerType, _valueInstantiator
          

            .getClass().getName() }));
        }
        _delegateDeserializer = findDeserializer(ctxt, delegateType, null);
      } else if (_valueInstantiator.canCreateFromObjectWith()) {
        SettableBeanProperty[] creatorProps = _valueInstantiator.getFromObjectArguments(ctxt.getConfig());
        _propertyBasedCreator = PropertyBasedCreator.construct(ctxt, _valueInstantiator, creatorProps, ctxt
          .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
      }
    }
  }
  







  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    KeyDeserializer keyDeser = _keyDeserializer;
    if (keyDeser == null) {
      keyDeser = ctxt.findKeyDeserializer(_containerType.getKeyType(), property);
    }
    JsonDeserializer<?> valueDeser = _valueDeserializer;
    JavaType vt = _containerType.getContentType();
    if (valueDeser == null) {
      valueDeser = ctxt.findContextualValueDeserializer(vt, property);
    } else {
      valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
    }
    TypeDeserializer vtd = _valueTypeDeserializer;
    if (vtd != null) {
      vtd = vtd.forProperty(property);
    }
    return withResolved(keyDeser, valueDeser, vtd, findContentNullProvider(ctxt, property, valueDeser));
  }
  





  public boolean isCachable()
  {
    return (_valueDeserializer == null) && (_keyDeserializer == null) && (_valueTypeDeserializer == null);
  }
  








  public JsonDeserializer<Object> getContentDeserializer()
  {
    return _valueDeserializer;
  }
  
  public Object getEmptyValue(DeserializationContext ctxt)
    throws JsonMappingException
  {
    return constructMap(ctxt);
  }
  







  public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_propertyBasedCreator != null) {
      return _deserializeUsingProperties(p, ctxt);
    }
    if (_delegateDeserializer != null) {
      return (EnumMap)_valueInstantiator.createUsingDelegate(ctxt, _delegateDeserializer
        .deserialize(p, ctxt));
    }
    
    JsonToken t = p.currentToken();
    if ((t != JsonToken.START_OBJECT) && (t != JsonToken.FIELD_NAME) && (t != JsonToken.END_OBJECT))
    {
      if (t == JsonToken.VALUE_STRING) {
        return (EnumMap)_valueInstantiator.createFromString(ctxt, p.getText());
      }
      
      return (EnumMap)_deserializeFromEmpty(p, ctxt);
    }
    EnumMap result = constructMap(ctxt);
    return deserialize(p, ctxt, result);
  }
  



  public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt, EnumMap result)
    throws IOException
  {
    p.setCurrentValue(result);
    
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    
    String keyStr;
    if (p.isExpectedStartObjectToken()) {
      keyStr = p.nextFieldName();
    } else {
      JsonToken t = p.currentToken();
      if (t != JsonToken.FIELD_NAME) {
        if (t == JsonToken.END_OBJECT) {
          return result;
        }
        ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
      } }
    for (String keyStr = p.getCurrentName(); 
        

        keyStr != null; keyStr = p.nextFieldName())
    {
      Enum<?> key = (Enum)_keyDeserializer.deserializeKey(keyStr, ctxt);
      JsonToken t = p.nextToken();
      if (key == null) {
        if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
          return (EnumMap)ctxt.handleWeirdStringValue(_enumClass, keyStr, "value not one of declared Enum instance names for %s", new Object[] {_containerType
          
            .getKeyType() });
        }
        

        p.skipChildren();
      }
      else
      {
        try
        {
          Object value;
          

          if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
              continue;
            }
            value = _nullProvider.getNullValue(ctxt); } else { Object value;
            if (typeDeser == null) {
              value = valueDes.deserialize(p, ctxt);
            } else
              value = valueDes.deserializeWithType(p, ctxt, typeDeser);
          }
        } catch (Exception e) { Object value;
          return (EnumMap)wrapAndThrow(e, result, keyStr); }
        Object value;
        result.put(key, value);
      } }
    return result;
  }
  



  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromObject(p, ctxt);
  }
  
  protected EnumMap<?, ?> constructMap(DeserializationContext ctxt) throws JsonMappingException {
    if (_valueInstantiator == null) {
      return new EnumMap(_enumClass);
    }
    try {
      if (!_valueInstantiator.canCreateUsingDefault()) {
        return (EnumMap)ctxt.handleMissingInstantiator(handledType(), 
          getValueInstantiator(), null, "no default constructor found", new Object[0]);
      }
      
      return (EnumMap)_valueInstantiator.createUsingDefault(ctxt);
    } catch (IOException e) {
      return (EnumMap)ClassUtil.throwAsMappingException(ctxt, e);
    }
  }
  
  public EnumMap<?, ?> _deserializeUsingProperties(JsonParser p, DeserializationContext ctxt) throws IOException
  {
    PropertyBasedCreator creator = _propertyBasedCreator;
    
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
    
    String keyName;
    if (p.isExpectedStartObjectToken()) {
      keyName = p.nextFieldName(); } else { String keyName;
      if (p.hasToken(JsonToken.FIELD_NAME))
        keyName = p.getCurrentName();
    }
    for (String keyName = null; 
        

        keyName != null; keyName = p.nextFieldName()) {
      JsonToken t = p.nextToken();
      
      SettableBeanProperty prop = creator.findCreatorProperty(keyName);
      if (prop != null)
      {
        if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
          p.nextToken();
          try
          {
            result = (EnumMap)creator.build(ctxt, buffer);
          } catch (Exception e) { EnumMap<?, ?> result;
            return (EnumMap)wrapAndThrow(e, _containerType.getRawClass(), keyName); }
          EnumMap<?, ?> result;
          return deserialize(p, ctxt, result);
        }
        
      }
      else
      {
        Enum<?> key = (Enum)_keyDeserializer.deserializeKey(keyName, ctxt);
        if (key == null) {
          if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
            return (EnumMap)ctxt.handleWeirdStringValue(_enumClass, keyName, "value not one of declared Enum instance names for %s", new Object[] {_containerType
            
              .getKeyType() });
          }
          

          p.nextToken();
          p.skipChildren();
        }
        else
        {
          try {
            Object value;
            if (t == JsonToken.VALUE_NULL) {
              if (_skipNullValues) {
                continue;
              }
              value = _nullProvider.getNullValue(ctxt); } else { Object value;
              if (_valueTypeDeserializer == null) {
                value = _valueDeserializer.deserialize(p, ctxt);
              } else
                value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
            }
          } catch (Exception e) { Object value;
            wrapAndThrow(e, _containerType.getRawClass(), keyName);
            return null; }
          Object value;
          buffer.bufferMapProperty(key, value);
        }
      }
    }
    try {
      return (EnumMap)creator.build(ctxt, buffer);
    } catch (Exception e) {
      wrapAndThrow(e, _containerType.getRawClass(), keyName); }
    return null;
  }
}
