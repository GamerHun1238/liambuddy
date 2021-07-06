package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import java.io.IOException;
import java.util.BitSet;




























































public class PropertyValueBuffer
{
  protected final JsonParser _parser;
  protected final DeserializationContext _context;
  protected final ObjectIdReader _objectIdReader;
  protected final Object[] _creatorParameters;
  protected int _paramsNeeded;
  protected int _paramsSeen;
  protected final BitSet _paramsSeenBig;
  protected PropertyValue _buffered;
  protected Object _idValue;
  
  public PropertyValueBuffer(JsonParser p, DeserializationContext ctxt, int paramCount, ObjectIdReader oir)
  {
    _parser = p;
    _context = ctxt;
    _paramsNeeded = paramCount;
    _objectIdReader = oir;
    _creatorParameters = new Object[paramCount];
    if (paramCount < 32) {
      _paramsSeenBig = null;
    } else {
      _paramsSeenBig = new BitSet();
    }
  }
  






  public final boolean hasParameter(SettableBeanProperty prop)
  {
    if (_paramsSeenBig == null) {
      return (_paramsSeen >> prop.getCreatorIndex() & 0x1) == 1;
    }
    return _paramsSeenBig.get(prop.getCreatorIndex());
  }
  



  public Object getParameter(SettableBeanProperty prop)
    throws JsonMappingException
  {
    Object value;
    


    Object value;
    


    if (hasParameter(prop)) {
      value = _creatorParameters[prop.getCreatorIndex()];
    } else {
      value = _creatorParameters[prop.getCreatorIndex()] =  = _findMissing(prop);
    }
    if ((value == null) && (_context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES))) {
      return _context.reportInputMismatch(prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS` enabled", new Object[] {prop
      
        .getName(), Integer.valueOf(prop.getCreatorIndex()) });
    }
    return value;
  }
  








  public Object[] getParameters(SettableBeanProperty[] props)
    throws JsonMappingException
  {
    if (_paramsNeeded > 0) {
      if (_paramsSeenBig == null) {
        int mask = _paramsSeen;
        

        int ix = 0; for (int len = _creatorParameters.length; ix < len; mask >>= 1) {
          if ((mask & 0x1) == 0) {
            _creatorParameters[ix] = _findMissing(props[ix]);
          }
          ix++;
        }
        
      }
      else
      {
        int len = _creatorParameters.length;
        for (int ix = 0; (ix = _paramsSeenBig.nextClearBit(ix)) < len; ix++) {
          _creatorParameters[ix] = _findMissing(props[ix]);
        }
      }
    }
    
    if (_context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
      for (int ix = 0; ix < props.length; ix++) {
        if (_creatorParameters[ix] == null) {
          SettableBeanProperty prop = props[ix];
          _context.reportInputMismatch(prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS` enabled", new Object[] {prop
          
            .getName(), Integer.valueOf(props[ix].getCreatorIndex()) });
        }
      }
    }
    return _creatorParameters;
  }
  
  protected Object _findMissing(SettableBeanProperty prop)
    throws JsonMappingException
  {
    Object injectableValueId = prop.getInjectableValueId();
    if (injectableValueId != null) {
      return _context.findInjectableValue(prop.getInjectableValueId(), prop, null);
    }
    

    if (prop.isRequired()) {
      _context.reportInputMismatch(prop, "Missing required creator property '%s' (index %d)", new Object[] {prop
        .getName(), Integer.valueOf(prop.getCreatorIndex()) });
    }
    if (_context.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)) {
      _context.reportInputMismatch(prop, "Missing creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES` enabled", new Object[] {prop
      
        .getName(), Integer.valueOf(prop.getCreatorIndex()) });
    }
    
    Object nullValue = prop.getNullValueProvider().getNullValue(_context);
    if (nullValue != null) {
      return nullValue;
    }
    

    JsonDeserializer<Object> deser = prop.getValueDeserializer();
    return deser.getNullValue(_context);
  }
  











  public boolean readIdProperty(String propName)
    throws IOException
  {
    if ((_objectIdReader != null) && (propName.equals(_objectIdReader.propertyName.getSimpleName()))) {
      _idValue = _objectIdReader.readObjectReference(_parser, _context);
      return true;
    }
    return false;
  }
  


  public Object handleIdValue(DeserializationContext ctxt, Object bean)
    throws IOException
  {
    if (_objectIdReader != null) {
      if (_idValue != null) {
        ReadableObjectId roid = ctxt.findObjectId(_idValue, _objectIdReader.generator, _objectIdReader.resolver);
        roid.bindItem(bean);
        
        SettableBeanProperty idProp = _objectIdReader.idProperty;
        if (idProp != null) {
          return idProp.setAndReturn(bean, _idValue);
        }
      }
      else {
        ctxt.reportUnresolvedObjectId(_objectIdReader, bean);
      }
    }
    return bean;
  }
  
  protected PropertyValue buffered() { return _buffered; }
  
  public boolean isComplete() { return _paramsNeeded <= 0; }
  








  public boolean assignParameter(SettableBeanProperty prop, Object value)
  {
    int ix = prop.getCreatorIndex();
    _creatorParameters[ix] = value;
    if (_paramsSeenBig == null) {
      int old = _paramsSeen;
      int newValue = old | 1 << ix;
      if (old != newValue) {
        _paramsSeen = newValue;
        if (--_paramsNeeded <= 0)
        {
          return (_objectIdReader == null) || (_idValue != null);
        }
      }
    }
    else if (!_paramsSeenBig.get(ix)) {
      _paramsSeenBig.set(ix);
      if (--_paramsNeeded > 0) {}
    }
    


    return false;
  }
  
  public void bufferProperty(SettableBeanProperty prop, Object value) {
    _buffered = new PropertyValue.Regular(_buffered, value, prop);
  }
  
  public void bufferAnyProperty(SettableAnyProperty prop, String propName, Object value) {
    _buffered = new PropertyValue.Any(_buffered, value, prop, propName);
  }
  
  public void bufferMapProperty(Object key, Object value) {
    _buffered = new PropertyValue.Map(_buffered, value, key);
  }
}
