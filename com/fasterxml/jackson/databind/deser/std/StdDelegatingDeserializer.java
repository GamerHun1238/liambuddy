package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;






































public class StdDelegatingDeserializer<T>
  extends StdDeserializer<T>
  implements ContextualDeserializer, ResolvableDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final Converter<Object, T> _converter;
  protected final JavaType _delegateType;
  protected final JsonDeserializer<Object> _delegateDeserializer;
  
  public StdDelegatingDeserializer(Converter<?, T> converter)
  {
    super(Object.class);
    _converter = converter;
    _delegateType = null;
    _delegateDeserializer = null;
  }
  


  public StdDelegatingDeserializer(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer)
  {
    super(delegateType);
    _converter = converter;
    _delegateType = delegateType;
    _delegateDeserializer = delegateDeserializer;
  }
  



  protected StdDelegatingDeserializer(StdDelegatingDeserializer<T> src)
  {
    super(src);
    _converter = _converter;
    _delegateType = _delegateType;
    _delegateDeserializer = _delegateDeserializer;
  }
  





  protected StdDelegatingDeserializer<T> withDelegate(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer)
  {
    ClassUtil.verifyMustOverride(StdDelegatingDeserializer.class, this, "withDelegate");
    return new StdDelegatingDeserializer(converter, delegateType, delegateDeserializer);
  }
  









  public void resolve(DeserializationContext ctxt)
    throws JsonMappingException
  {
    if ((_delegateDeserializer != null) && ((_delegateDeserializer instanceof ResolvableDeserializer))) {
      ((ResolvableDeserializer)_delegateDeserializer).resolve(ctxt);
    }
  }
  


  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    if (_delegateDeserializer != null) {
      JsonDeserializer<?> deser = ctxt.handleSecondaryContextualization(_delegateDeserializer, property, _delegateType);
      
      if (deser != _delegateDeserializer) {
        return withDelegate(_converter, _delegateType, deser);
      }
      return this;
    }
    
    JavaType delegateType = _converter.getInputType(ctxt.getTypeFactory());
    return withDelegate(_converter, delegateType, ctxt
      .findContextualValueDeserializer(delegateType, property));
  }
  






  public JsonDeserializer<?> getDelegatee()
  {
    return _delegateDeserializer;
  }
  
  public Class<?> handledType()
  {
    return _delegateDeserializer.handledType();
  }
  
  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return _delegateDeserializer.supportsUpdate(config);
  }
  






  public T deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    Object delegateValue = _delegateDeserializer.deserialize(p, ctxt);
    if (delegateValue == null) {
      return null;
    }
    return convertValue(delegateValue);
  }
  












  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    Object delegateValue = _delegateDeserializer.deserialize(p, ctxt);
    if (delegateValue == null) {
      return null;
    }
    return convertValue(delegateValue);
  }
  


  public T deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue)
    throws IOException
  {
    if (_delegateType.getRawClass().isAssignableFrom(intoValue.getClass())) {
      return _delegateDeserializer.deserialize(p, ctxt, intoValue);
    }
    return _handleIncompatibleUpdateValue(p, ctxt, intoValue);
  }
  











  protected Object _handleIncompatibleUpdateValue(JsonParser p, DeserializationContext ctxt, Object intoValue)
    throws IOException
  {
    throw new UnsupportedOperationException(String.format("Cannot update object of type %s (using deserializer for type %s)" + intoValue
      .getClass().getName(), new Object[] { _delegateType }));
  }
  

















  protected T convertValue(Object delegateValue)
  {
    return _converter.convert(delegateValue);
  }
}
