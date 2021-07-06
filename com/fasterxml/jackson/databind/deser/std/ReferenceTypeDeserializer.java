package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.IOException;





















public abstract class ReferenceTypeDeserializer<T>
  extends StdDeserializer<T>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 2L;
  protected final JavaType _fullType;
  protected final ValueInstantiator _valueInstantiator;
  protected final TypeDeserializer _valueTypeDeserializer;
  protected final JsonDeserializer<Object> _valueDeserializer;
  
  public ReferenceTypeDeserializer(JavaType fullType, ValueInstantiator vi, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
  {
    super(fullType);
    _valueInstantiator = vi;
    _fullType = fullType;
    _valueDeserializer = deser;
    _valueTypeDeserializer = typeDeser;
  }
  

  @Deprecated
  public ReferenceTypeDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
  {
    this(fullType, null, typeDeser, deser);
  }
  

  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    JsonDeserializer<?> deser = _valueDeserializer;
    if (deser == null) {
      deser = ctxt.findContextualValueDeserializer(_fullType.getReferencedType(), property);
    } else {
      deser = ctxt.handleSecondaryContextualization(deser, property, _fullType.getReferencedType());
    }
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    if (typeDeser != null) {
      typeDeser = typeDeser.forProperty(property);
    }
    
    if ((deser == _valueDeserializer) && (typeDeser == _valueTypeDeserializer)) {
      return this;
    }
    return withResolved(typeDeser, deser);
  }
  










  public AccessPattern getNullAccessPattern()
  {
    return AccessPattern.DYNAMIC;
  }
  
  public AccessPattern getEmptyAccessPattern()
  {
    return AccessPattern.DYNAMIC;
  }
  





  protected abstract ReferenceTypeDeserializer<T> withResolved(TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer);
  




  public abstract T getNullValue(DeserializationContext paramDeserializationContext)
    throws JsonMappingException;
  




  public Object getEmptyValue(DeserializationContext ctxt)
    throws JsonMappingException
  {
    return getNullValue(ctxt);
  }
  





  public abstract T referenceValue(Object paramObject);
  





  public abstract T updateReference(T paramT, Object paramObject);
  





  public abstract Object getReferenced(T paramT);
  





  public JavaType getValueType()
  {
    return _fullType;
  }
  




  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return _valueDeserializer == null ? null : _valueDeserializer
      .supportsUpdate(config);
  }
  







  public T deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_valueInstantiator != null)
    {
      T value = _valueInstantiator.createUsingDefault(ctxt);
      return deserialize(p, ctxt, value);
    }
    

    Object contents = _valueTypeDeserializer == null ? _valueDeserializer.deserialize(p, ctxt) : _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    return referenceValue(contents);
  }
  


  public T deserialize(JsonParser p, DeserializationContext ctxt, T reference)
    throws IOException
  {
    Boolean B = _valueDeserializer.supportsUpdate(ctxt.getConfig());
    Object contents;
    Object contents; if ((B.equals(Boolean.FALSE)) || (_valueTypeDeserializer != null))
    {

      contents = _valueTypeDeserializer == null ? _valueDeserializer.deserialize(p, ctxt) : _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }
    else {
      contents = getReferenced(reference);
      
      if (contents == null)
      {

        contents = _valueTypeDeserializer == null ? _valueDeserializer.deserialize(p, ctxt) : _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
        return referenceValue(contents);
      }
      contents = _valueDeserializer.deserialize(p, ctxt, contents);
    }
    
    return updateReference(reference, contents);
  }
  

  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NULL)) {
      return getNullValue(ctxt);
    }
    











    if (_valueTypeDeserializer == null) {
      return deserialize(p, ctxt);
    }
    return referenceValue(_valueTypeDeserializer.deserializeTypedFromAny(p, ctxt));
  }
}
