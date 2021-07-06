package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;




@JacksonStdImpl
public final class StringArrayDeserializer
  extends StdDeserializer<String[]>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 2L;
  private static final String[] NO_STRINGS = new String[0];
  
  public static final StringArrayDeserializer instance = new StringArrayDeserializer();
  




  protected JsonDeserializer<String> _elementDeserializer;
  




  protected final NullValueProvider _nullProvider;
  




  protected final Boolean _unwrapSingle;
  




  protected final boolean _skipNullValues;
  




  public StringArrayDeserializer()
  {
    this(null, null, null);
  }
  

  protected StringArrayDeserializer(JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super([Ljava.lang.String.class);
    _elementDeserializer = deser;
    _nullProvider = nuller;
    _unwrapSingle = unwrapSingle;
    _skipNullValues = NullsConstantProvider.isSkipper(nuller);
  }
  
  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return Boolean.TRUE;
  }
  

  public AccessPattern getEmptyAccessPattern()
  {
    return AccessPattern.CONSTANT;
  }
  
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
  {
    return NO_STRINGS;
  }
  





  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    JsonDeserializer<?> deser = _elementDeserializer;
    
    deser = findConvertingContentDeserializer(ctxt, property, deser);
    JavaType type = ctxt.constructType(String.class);
    if (deser == null) {
      deser = ctxt.findContextualValueDeserializer(type, property);
    } else {
      deser = ctxt.handleSecondaryContextualization(deser, property, type);
    }
    
    Boolean unwrapSingle = findFormatFeature(ctxt, property, [Ljava.lang.String.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    
    NullValueProvider nuller = findContentNullProvider(ctxt, property, deser);
    
    if ((deser != null) && (isDefaultDeserializer(deser))) {
      deser = null;
    }
    if ((_elementDeserializer == deser) && (_unwrapSingle == unwrapSingle) && (_nullProvider == nuller))
    {

      return this;
    }
    return new StringArrayDeserializer(deser, nuller, unwrapSingle);
  }
  

  public String[] deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      return handleNonArray(p, ctxt);
    }
    if (_elementDeserializer != null) {
      return _deserializeCustom(p, ctxt, null);
    }
    
    ObjectBuffer buffer = ctxt.leaseObjectBuffer();
    Object[] chunk = buffer.resetAndStart();
    
    int ix = 0;
    try
    {
      for (;;) {
        String value = p.nextTextValue();
        if (value == null) {
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
    String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
    ctxt.returnObjectBuffer(buffer);
    return result;
  }
  



  protected final String[] _deserializeCustom(JsonParser p, DeserializationContext ctxt, String[] old)
    throws IOException
  {
    ObjectBuffer buffer = ctxt.leaseObjectBuffer();
    Object[] chunk;
    int ix;
    Object[] chunk;
    if (old == null) {
      int ix = 0;
      chunk = buffer.resetAndStart();
    } else {
      ix = old.length;
      chunk = buffer.resetAndStart(old, ix);
    }
    
    JsonDeserializer<String> deser = _elementDeserializer;
    

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
        if (ix >= chunk.length) {
          chunk = buffer.appendCompletedChunk(chunk);
          ix = 0;
        }
        chunk[(ix++)] = value;
      }
    }
    catch (Exception e) {
      throw JsonMappingException.wrapWithPath(e, String.class, ix);
    }
    String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
    ctxt.returnObjectBuffer(buffer);
    return result;
  }
  
  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException
  {
    return typeDeserializer.deserializeTypedFromArray(p, ctxt);
  }
  


  public String[] deserialize(JsonParser p, DeserializationContext ctxt, String[] intoValue)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      String[] arr = handleNonArray(p, ctxt);
      if (arr == null) {
        return intoValue;
      }
      int offset = intoValue.length;
      String[] result = new String[offset + arr.length];
      System.arraycopy(intoValue, 0, result, 0, offset);
      System.arraycopy(arr, 0, result, offset, arr.length);
      return result;
    }
    
    if (_elementDeserializer != null) {
      return _deserializeCustom(p, ctxt, intoValue);
    }
    ObjectBuffer buffer = ctxt.leaseObjectBuffer();
    int ix = intoValue.length;
    Object[] chunk = buffer.resetAndStart(intoValue, ix);
    try
    {
      for (;;) {
        String value = p.nextTextValue();
        if (value == null) {
          JsonToken t = p.getCurrentToken();
          if (t == JsonToken.END_ARRAY) {
            break;
          }
          if (t == JsonToken.VALUE_NULL)
          {
            if (_skipNullValues) {
              return NO_STRINGS;
            }
            value = (String)_nullProvider.getNullValue(ctxt);
          } else {
            value = _parseString(p, ctxt);
          }
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
    String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
    ctxt.returnObjectBuffer(buffer);
    return result;
  }
  
  private final String[] handleNonArray(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_unwrapSingle != Boolean.TRUE) if (_unwrapSingle != null) break label31;
    label31:
    boolean canWrap = ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    if (canWrap)
    {

      String value = p.hasToken(JsonToken.VALUE_NULL) ? (String)_nullProvider.getNullValue(ctxt) : _parseString(p, ctxt);
      return new String[] { value };
    }
    if ((p.hasToken(JsonToken.VALUE_STRING)) && 
      (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT))) {
      String str = p.getText();
      if (str.length() == 0) {
        return null;
      }
    }
    return (String[])ctxt.handleUnexpectedToken(_valueClass, p);
  }
}
