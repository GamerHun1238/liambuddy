package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.io.IOException;
import java.io.Serializable;
































public abstract class ValueInstantiator
{
  public ValueInstantiator() {}
  
  public Class<?> getValueClass()
  {
    return Object.class;
  }
  



  public String getValueTypeDesc()
  {
    Class<?> cls = getValueClass();
    if (cls == null) {
      return "UNKNOWN";
    }
    return cls.getName();
  }
  




  public boolean canInstantiate()
  {
    return (canCreateUsingDefault()) || 
      (canCreateUsingDelegate()) || (canCreateUsingArrayDelegate()) || 
      (canCreateFromObjectWith()) || (canCreateFromString()) || 
      (canCreateFromInt()) || (canCreateFromLong()) || 
      (canCreateFromDouble()) || (canCreateFromBoolean());
  }
  


  public boolean canCreateFromString()
  {
    return false;
  }
  

  public boolean canCreateFromInt()
  {
    return false;
  }
  

  public boolean canCreateFromLong()
  {
    return false;
  }
  

  public boolean canCreateFromDouble()
  {
    return false;
  }
  

  public boolean canCreateFromBoolean()
  {
    return false;
  }
  


  public boolean canCreateUsingDefault()
  {
    return getDefaultCreator() != null;
  }
  


  public boolean canCreateUsingDelegate()
  {
    return false;
  }
  




  public boolean canCreateUsingArrayDelegate()
  {
    return false;
  }
  


  public boolean canCreateFromObjectWith()
  {
    return false;
  }
  








  public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config)
  {
    return null;
  }
  





  public JavaType getDelegateType(DeserializationConfig config)
  {
    return null;
  }
  






  public JavaType getArrayDelegateType(DeserializationConfig config)
  {
    return null;
  }
  













  public Object createUsingDefault(DeserializationContext ctxt)
    throws IOException
  {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no default no-arguments constructor found", new Object[0]);
  }
  








  public Object createFromObjectWith(DeserializationContext ctxt, Object[] args)
    throws IOException
  {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no creator with arguments specified", new Object[0]);
  }
  




















  public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer)
    throws IOException
  {
    return createFromObjectWith(ctxt, buffer.getParameters(props));
  }
  


  public Object createUsingDelegate(DeserializationContext ctxt, Object delegate)
    throws IOException
  {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no delegate creator specified", new Object[0]);
  }
  



  public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate)
    throws IOException
  {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no array delegate creator specified", new Object[0]);
  }
  






  public Object createFromString(DeserializationContext ctxt, String value)
    throws IOException
  {
    return _createFromStringFallbacks(ctxt, value);
  }
  
  public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no int/Int-argument constructor/factory method to deserialize from Number value (%s)", new Object[] {
    
      Integer.valueOf(value) });
  }
  
  public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", new Object[] {
    
      Long.valueOf(value) });
  }
  
  public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", new Object[] {
    
      Double.valueOf(value) });
  }
  
  public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", new Object[] {
    
      Boolean.valueOf(value) });
  }
  














  public AnnotatedWithParams getDefaultCreator()
  {
    return null;
  }
  





  public AnnotatedWithParams getDelegateCreator()
  {
    return null;
  }
  





  public AnnotatedWithParams getArrayDelegateCreator()
  {
    return null;
  }
  






  public AnnotatedWithParams getWithArgsCreator()
  {
    return null;
  }
  

  public AnnotatedParameter getIncompleteParameter()
  {
    return null;
  }
  












  protected Object _createFromStringFallbacks(DeserializationContext ctxt, String value)
    throws IOException
  {
    if (canCreateFromBoolean()) {
      String str = value.trim();
      if ("true".equals(str)) {
        return createFromBoolean(ctxt, true);
      }
      if ("false".equals(str)) {
        return createFromBoolean(ctxt, false);
      }
    }
    
    if ((value.length() == 0) && 
      (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT))) {
      return null;
    }
    
    return ctxt.handleMissingInstantiator(getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", new Object[] { value });
  }
  








  public static class Base
    extends ValueInstantiator
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    







    protected final Class<?> _valueType;
    








    public Base(Class<?> type)
    {
      _valueType = type;
    }
    
    public Base(JavaType type) {
      _valueType = type.getRawClass();
    }
    
    public String getValueTypeDesc()
    {
      return _valueType.getName();
    }
    
    public Class<?> getValueClass()
    {
      return _valueType;
    }
  }
  
  public static abstract interface Gettable
  {
    public abstract ValueInstantiator getValueInstantiator();
  }
}
