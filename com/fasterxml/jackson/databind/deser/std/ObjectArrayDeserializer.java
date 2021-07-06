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
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.lang.reflect.Array;


@JacksonStdImpl
public class ObjectArrayDeserializer
  extends ContainerDeserializerBase<Object[]>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 1L;
  protected static final Object[] NO_OBJECTS = new Object[0];
  





  protected final boolean _untyped;
  





  protected final Class<?> _elementClass;
  





  protected JsonDeserializer<Object> _elementDeserializer;
  




  protected final TypeDeserializer _elementTypeDeserializer;
  





  public ObjectArrayDeserializer(JavaType arrayType, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser)
  {
    super(arrayType, null, null);
    _elementClass = arrayType.getContentType().getRawClass();
    _untyped = (_elementClass == Object.class);
    _elementDeserializer = elemDeser;
    _elementTypeDeserializer = elemTypeDeser;
  }
  


  protected ObjectArrayDeserializer(ObjectArrayDeserializer base, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super(base, nuller, unwrapSingle);
    _elementClass = _elementClass;
    _untyped = _untyped;
    
    _elementDeserializer = elemDeser;
    _elementTypeDeserializer = elemTypeDeser;
  }
  




  public ObjectArrayDeserializer withDeserializer(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser)
  {
    return withResolved(elemTypeDeser, elemDeser, _nullProvider, _unwrapSingle);
  }
  






  public ObjectArrayDeserializer withResolved(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    if ((unwrapSingle == _unwrapSingle) && (nuller == _nullProvider) && (elemDeser == _elementDeserializer) && (elemTypeDeser == _elementTypeDeserializer))
    {

      return this;
    }
    return new ObjectArrayDeserializer(this, elemDeser, elemTypeDeser, nuller, unwrapSingle);
  }
  




  public boolean isCachable()
  {
    return (_elementDeserializer == null) && (_elementTypeDeserializer == null);
  }
  

  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    JsonDeserializer<?> valueDeser = _elementDeserializer;
    Boolean unwrapSingle = findFormatFeature(ctxt, property, _containerType.getRawClass(), JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    

    valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
    JavaType vt = _containerType.getContentType();
    if (valueDeser == null) {
      valueDeser = ctxt.findContextualValueDeserializer(vt, property);
    } else {
      valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
    }
    TypeDeserializer elemTypeDeser = _elementTypeDeserializer;
    if (elemTypeDeser != null) {
      elemTypeDeser = elemTypeDeser.forProperty(property);
    }
    NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
    return withResolved(elemTypeDeser, valueDeser, nuller, unwrapSingle);
  }
  






  public JsonDeserializer<Object> getContentDeserializer()
  {
    return _elementDeserializer;
  }
  

  public AccessPattern getEmptyAccessPattern()
  {
    return AccessPattern.CONSTANT;
  }
  
  public Object getEmptyValue(DeserializationContext ctxt)
    throws JsonMappingException
  {
    return NO_OBJECTS;
  }
  








  public Object[] deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      return handleNonArray(p, ctxt);
    }
    
    ObjectBuffer buffer = ctxt.leaseObjectBuffer();
    Object[] chunk = buffer.resetAndStart();
    int ix = 0;
    
    TypeDeserializer typeDeser = _elementTypeDeserializer;
    try {
      JsonToken t;
      while ((t = p.nextToken()) != JsonToken.END_ARRAY)
      {
        Object value;
        
        if (t == JsonToken.VALUE_NULL) {
          if (_skipNullValues) {
            continue;
          }
          Object value = _nullProvider.getNullValue(ctxt); } else { Object value;
          if (typeDeser == null) {
            value = _elementDeserializer.deserialize(p, ctxt);
          } else
            value = _elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
        }
        if (ix >= chunk.length) {
          chunk = buffer.appendCompletedChunk(chunk);
          ix = 0;
        }
        chunk[(ix++)] = value;
      }
    } catch (Exception e) {
      throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
    }
    JsonToken t;
    Object[] result;
    Object[] result;
    if (_untyped) {
      result = buffer.completeAndClearBuffer(chunk, ix);
    } else {
      result = buffer.completeAndClearBuffer(chunk, ix, _elementClass);
    }
    ctxt.returnObjectBuffer(buffer);
    return result;
  }
  




  public Object[] deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return (Object[])typeDeserializer.deserializeTypedFromArray(p, ctxt);
  }
  

  public Object[] deserialize(JsonParser p, DeserializationContext ctxt, Object[] intoValue)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      Object[] arr = handleNonArray(p, ctxt);
      if (arr == null) {
        return intoValue;
      }
      int offset = intoValue.length;
      Object[] result = new Object[offset + arr.length];
      System.arraycopy(intoValue, 0, result, 0, offset);
      System.arraycopy(arr, 0, result, offset, arr.length);
      return result;
    }
    
    ObjectBuffer buffer = ctxt.leaseObjectBuffer();
    int ix = intoValue.length;
    Object[] chunk = buffer.resetAndStart(intoValue, ix);
    
    TypeDeserializer typeDeser = _elementTypeDeserializer;
    try {
      JsonToken t;
      while ((t = p.nextToken()) != JsonToken.END_ARRAY)
      {
        Object value;
        if (t == JsonToken.VALUE_NULL) {
          if (_skipNullValues) {
            continue;
          }
          Object value = _nullProvider.getNullValue(ctxt); } else { Object value;
          if (typeDeser == null) {
            value = _elementDeserializer.deserialize(p, ctxt);
          } else
            value = _elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
        }
        if (ix >= chunk.length) {
          chunk = buffer.appendCompletedChunk(chunk);
          ix = 0;
        }
        chunk[(ix++)] = value;
      }
    } catch (Exception e) {
      throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
    }
    JsonToken t;
    Object[] result;
    Object[] result;
    if (_untyped) {
      result = buffer.completeAndClearBuffer(chunk, ix);
    } else {
      result = buffer.completeAndClearBuffer(chunk, ix, _elementClass);
    }
    ctxt.returnObjectBuffer(buffer);
    return result;
  }
  







  protected Byte[] deserializeFromBase64(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    byte[] b = p.getBinaryValue(ctxt.getBase64Variant());
    
    Byte[] result = new Byte[b.length];
    int i = 0; for (int len = b.length; i < len; i++) {
      result[i] = Byte.valueOf(b[i]);
    }
    return result;
  }
  

  protected Object[] handleNonArray(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if ((p.hasToken(JsonToken.VALUE_STRING)) && 
      (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT))) {
      String str = p.getText();
      if (str.length() == 0) {
        return null;
      }
    }
    

    if (_unwrapSingle != Boolean.TRUE) if (_unwrapSingle != null) break label65;
    label65:
    boolean canWrap = ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    if (!canWrap)
    {
      if ((p.hasToken(JsonToken.VALUE_STRING)) && (_elementClass == Byte.class))
      {

        return deserializeFromBase64(p, ctxt);
      }
      return (Object[])ctxt.handleUnexpectedToken(_containerType.getRawClass(), p);
    }
    Object value;
    Object value;
    if (p.hasToken(JsonToken.VALUE_NULL))
    {
      if (_skipNullValues) {
        return NO_OBJECTS;
      }
      value = _nullProvider.getNullValue(ctxt); } else { Object value;
      if (_elementTypeDeserializer == null) {
        value = _elementDeserializer.deserialize(p, ctxt);
      } else {
        value = _elementDeserializer.deserializeWithType(p, ctxt, _elementTypeDeserializer);
      }
    }
    Object[] result;
    Object[] result;
    if (_untyped) {
      result = new Object[1];
    } else {
      result = (Object[])Array.newInstance(_elementClass, 1);
    }
    result[0] = value;
    return result;
  }
}
