package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.Collection;




























@JacksonStdImpl
public final class StringCollectionDeserializer
  extends ContainerDeserializerBase<Collection<String>>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final JsonDeserializer<String> _valueDeserializer;
  protected final ValueInstantiator _valueInstantiator;
  protected final JsonDeserializer<Object> _delegateDeserializer;
  
  public StringCollectionDeserializer(JavaType collectionType, JsonDeserializer<?> valueDeser, ValueInstantiator valueInstantiator)
  {
    this(collectionType, valueInstantiator, null, valueDeser, valueDeser, null);
  }
  




  protected StringCollectionDeserializer(JavaType collectionType, ValueInstantiator valueInstantiator, JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super(collectionType, nuller, unwrapSingle);
    _valueDeserializer = valueDeser;
    _valueInstantiator = valueInstantiator;
    _delegateDeserializer = delegateDeser;
  }
  


  protected StringCollectionDeserializer withResolved(JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    if ((_unwrapSingle == unwrapSingle) && (_nullProvider == nuller) && (_valueDeserializer == valueDeser) && (_delegateDeserializer == delegateDeser))
    {
      return this;
    }
    return new StringCollectionDeserializer(_containerType, _valueInstantiator, delegateDeser, valueDeser, nuller, unwrapSingle);
  }
  



  public boolean isCachable()
  {
    return (_valueDeserializer == null) && (_delegateDeserializer == null);
  }
  







  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    JsonDeserializer<Object> delegate = null;
    if (_valueInstantiator != null)
    {
      AnnotatedWithParams delegateCreator = _valueInstantiator.getArrayDelegateCreator();
      if (delegateCreator != null) {
        JavaType delegateType = _valueInstantiator.getArrayDelegateType(ctxt.getConfig());
        delegate = findDeserializer(ctxt, delegateType, property);
      } else if ((delegateCreator = _valueInstantiator.getDelegateCreator()) != null) {
        JavaType delegateType = _valueInstantiator.getDelegateType(ctxt.getConfig());
        delegate = findDeserializer(ctxt, delegateType, property);
      }
    }
    JsonDeserializer<?> valueDeser = _valueDeserializer;
    JavaType valueType = _containerType.getContentType();
    if (valueDeser == null)
    {
      valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
      if (valueDeser == null)
      {
        valueDeser = ctxt.findContextualValueDeserializer(valueType, property);
      }
    } else {
      valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, valueType);
    }
    

    Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    
    NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
    if (isDefaultDeserializer(valueDeser)) {
      valueDeser = null;
    }
    return withResolved(delegate, valueDeser, nuller, unwrapSingle);
  }
  







  public JsonDeserializer<Object> getContentDeserializer()
  {
    JsonDeserializer<?> deser = _valueDeserializer;
    return deser;
  }
  
  public ValueInstantiator getValueInstantiator()
  {
    return _valueInstantiator;
  }
  








  public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_delegateDeserializer != null) {
      return (Collection)_valueInstantiator.createUsingDelegate(ctxt, _delegateDeserializer
        .deserialize(p, ctxt));
    }
    Collection<String> result = (Collection)_valueInstantiator.createUsingDefault(ctxt);
    return deserialize(p, ctxt, result);
  }
  



  public Collection<String> deserialize(JsonParser p, DeserializationContext ctxt, Collection<String> result)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      return handleNonArray(p, ctxt, result);
    }
    
    if (_valueDeserializer != null) {
      return deserializeUsingCustom(p, ctxt, result, _valueDeserializer);
    }
    try
    {
      for (;;) {
        String value = p.nextTextValue();
        if (value != null) {
          result.add(value);
        }
        else {
          JsonToken t = p.getCurrentToken();
          if (t == JsonToken.END_ARRAY) {
            break;
          }
          if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
              continue;
            }
            value = (String)_nullProvider.getNullValue(ctxt);
          } else {
            value = _parseString(p, ctxt);
          }
          result.add(value);
        }
      }
    } catch (Exception e) { throw JsonMappingException.wrapWithPath(e, result, result.size());
    }
    return result;
  }
  


  private Collection<String> deserializeUsingCustom(JsonParser p, DeserializationContext ctxt, Collection<String> result, JsonDeserializer<String> deser)
    throws IOException
  {
    try
    {
      for (;;)
      {
        String value;
        
        if (p.nextTextValue() == null) {
          JsonToken t = p.getCurrentToken();
          if (t == JsonToken.END_ARRAY) {
            break;
          }
          String value;
          if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
              continue;
            }
            String value = (String)_nullProvider.getNullValue(ctxt);
          } else {
            value = (String)deser.deserialize(p, ctxt);
          }
        } else {
          value = (String)deser.deserialize(p, ctxt);
        }
        result.add(value);
      }
    } catch (Exception e) {
      throw JsonMappingException.wrapWithPath(e, result, result.size());
    }
    return result;
  }
  

  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromArray(p, ctxt);
  }
  







  private final Collection<String> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<String> result)
    throws IOException
  {
    if (_unwrapSingle != Boolean.TRUE) if (_unwrapSingle != null) break label31;
    label31:
    boolean canWrap = ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    if (!canWrap) {
      return (Collection)ctxt.handleUnexpectedToken(_containerType.getRawClass(), p);
    }
    
    JsonDeserializer<String> valueDes = _valueDeserializer;
    JsonToken t = p.getCurrentToken();
    
    String value;
    
    if (t == JsonToken.VALUE_NULL)
    {
      if (_skipNullValues) {
        return result;
      }
      value = (String)_nullProvider.getNullValue(ctxt);
    } else {
      try {
        value = valueDes == null ? _parseString(p, ctxt) : (String)valueDes.deserialize(p, ctxt);
      } catch (Exception e) { String value;
        throw JsonMappingException.wrapWithPath(e, result, result.size());
      } }
    String value;
    result.add(value);
    return result;
  }
}
