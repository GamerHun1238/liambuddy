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
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.IOException;
import java.util.Collection;










public abstract class DelegatingDeserializer
  extends StdDeserializer<Object>
  implements ContextualDeserializer, ResolvableDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final JsonDeserializer<?> _delegatee;
  
  public DelegatingDeserializer(JsonDeserializer<?> d)
  {
    super(d.handledType());
    _delegatee = d;
  }
  






  protected abstract JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> paramJsonDeserializer);
  





  public void resolve(DeserializationContext ctxt)
    throws JsonMappingException
  {
    if ((_delegatee instanceof ResolvableDeserializer)) {
      ((ResolvableDeserializer)_delegatee).resolve(ctxt);
    }
  }
  


  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    JavaType vt = ctxt.constructType(_delegatee.handledType());
    JsonDeserializer<?> del = ctxt.handleSecondaryContextualization(_delegatee, property, vt);
    
    if (del == _delegatee) {
      return this;
    }
    return newDelegatingInstance(del);
  }
  

  public JsonDeserializer<?> replaceDelegatee(JsonDeserializer<?> delegatee)
  {
    if (delegatee == _delegatee) {
      return this;
    }
    return newDelegatingInstance(delegatee);
  }
  







  public Object deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    return _delegatee.deserialize(p, ctxt);
  }
  



  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue)
    throws IOException
  {
    return _delegatee.deserialize(p, ctxt, intoValue);
  }
  


  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return _delegatee.deserializeWithType(p, ctxt, typeDeserializer);
  }
  





  public boolean isCachable()
  {
    return _delegatee.isCachable();
  }
  
  public Boolean supportsUpdate(DeserializationConfig config) {
    return _delegatee.supportsUpdate(config);
  }
  
  public JsonDeserializer<?> getDelegatee()
  {
    return _delegatee;
  }
  

  public SettableBeanProperty findBackReference(String logicalName)
  {
    return _delegatee.findBackReference(logicalName);
  }
  
  public AccessPattern getNullAccessPattern()
  {
    return _delegatee.getNullAccessPattern();
  }
  
  public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException
  {
    return _delegatee.getNullValue(ctxt);
  }
  
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
  {
    return _delegatee.getEmptyValue(ctxt);
  }
  
  public Collection<Object> getKnownPropertyNames() {
    return _delegatee.getKnownPropertyNames();
  }
  
  public ObjectIdReader getObjectIdReader() { return _delegatee.getObjectIdReader(); }
}
