package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;













public final class ManagedReferenceProperty
  extends SettableBeanProperty.Delegating
{
  private static final long serialVersionUID = 1L;
  protected final String _referenceName;
  protected final boolean _isContainer;
  protected final SettableBeanProperty _backProperty;
  
  public ManagedReferenceProperty(SettableBeanProperty forward, String refName, SettableBeanProperty backward, boolean isContainer)
  {
    super(forward);
    _referenceName = refName;
    _backProperty = backward;
    _isContainer = isContainer;
  }
  
  protected SettableBeanProperty withDelegate(SettableBeanProperty d)
  {
    throw new IllegalStateException("Should never try to reset delegate");
  }
  

  public void fixAccess(DeserializationConfig config)
  {
    delegate.fixAccess(config);
    _backProperty.fixAccess(config);
  }
  






  public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    set(instance, delegate.deserialize(p, ctxt));
  }
  
  public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    return setAndReturn(instance, deserialize(p, ctxt));
  }
  
  public final void set(Object instance, Object value) throws IOException
  {
    setAndReturn(instance, value);
  }
  



  public Object setAndReturn(Object instance, Object value)
    throws IOException
  {
    if (value != null) {
      if (_isContainer) {
        if ((value instanceof Object[])) {
          for (Object ob : (Object[])value) {
            if (ob != null) _backProperty.set(ob, instance);
          }
        } else if ((value instanceof Collection)) {
          for (??? = ((Collection)value).iterator(); ((Iterator)???).hasNext();) { Object ob = ((Iterator)???).next();
            if (ob != null) _backProperty.set(ob, instance);
          }
        } else if ((value instanceof Map)) {
          for (??? = ((Map)value).values().iterator(); ((Iterator)???).hasNext();) { Object ob = ((Iterator)???).next();
            if (ob != null) _backProperty.set(ob, instance);
          }
        } else {
          throw new IllegalStateException("Unsupported container type (" + value.getClass().getName() + ") when resolving reference '" + _referenceName + "'");
        }
      }
      else {
        _backProperty.set(value, instance);
      }
    }
    
    return delegate.setAndReturn(instance, value);
  }
}
