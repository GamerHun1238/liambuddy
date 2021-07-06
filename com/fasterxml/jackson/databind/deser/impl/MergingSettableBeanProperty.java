package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.IOException;
























public class MergingSettableBeanProperty
  extends SettableBeanProperty.Delegating
{
  private static final long serialVersionUID = 1L;
  protected final AnnotatedMember _accessor;
  
  protected MergingSettableBeanProperty(SettableBeanProperty delegate, AnnotatedMember accessor)
  {
    super(delegate);
    _accessor = accessor;
  }
  

  protected MergingSettableBeanProperty(MergingSettableBeanProperty src, SettableBeanProperty delegate)
  {
    super(delegate);
    _accessor = _accessor;
  }
  

  public static MergingSettableBeanProperty construct(SettableBeanProperty delegate, AnnotatedMember accessor)
  {
    return new MergingSettableBeanProperty(delegate, accessor);
  }
  
  protected SettableBeanProperty withDelegate(SettableBeanProperty d)
  {
    return new MergingSettableBeanProperty(d, _accessor);
  }
  







  public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    Object oldValue = _accessor.getValue(instance);
    
    Object newValue;
    Object newValue;
    if (oldValue == null) {
      newValue = delegate.deserialize(p, ctxt);
    } else {
      newValue = delegate.deserializeWith(p, ctxt, oldValue);
    }
    if (newValue != oldValue)
    {

      delegate.set(instance, newValue);
    }
  }
  

  public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    Object oldValue = _accessor.getValue(instance);
    
    Object newValue;
    Object newValue;
    if (oldValue == null) {
      newValue = delegate.deserialize(p, ctxt);
    } else {
      newValue = delegate.deserializeWith(p, ctxt, oldValue);
    }
    


    if (newValue != oldValue)
    {

      if (newValue != null) {
        return delegate.setAndReturn(instance, newValue);
      }
    }
    return instance;
  }
  

  public void set(Object instance, Object value)
    throws IOException
  {
    if (value != null) {
      delegate.set(instance, value);
    }
  }
  

  public Object setAndReturn(Object instance, Object value)
    throws IOException
  {
    if (value != null) {
      return delegate.setAndReturn(instance, value);
    }
    return instance;
  }
}
