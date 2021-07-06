package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;








































@JacksonStdImpl
public class StdValueInstantiator
  extends ValueInstantiator
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final String _valueTypeDesc;
  protected final Class<?> _valueClass;
  protected AnnotatedWithParams _defaultCreator;
  protected AnnotatedWithParams _withArgsCreator;
  protected SettableBeanProperty[] _constructorArguments;
  protected JavaType _delegateType;
  protected AnnotatedWithParams _delegateCreator;
  protected SettableBeanProperty[] _delegateArguments;
  protected JavaType _arrayDelegateType;
  protected AnnotatedWithParams _arrayDelegateCreator;
  protected SettableBeanProperty[] _arrayDelegateArguments;
  protected AnnotatedWithParams _fromStringCreator;
  protected AnnotatedWithParams _fromIntCreator;
  protected AnnotatedWithParams _fromLongCreator;
  protected AnnotatedWithParams _fromDoubleCreator;
  protected AnnotatedWithParams _fromBooleanCreator;
  protected AnnotatedParameter _incompleteParameter;
  
  @Deprecated
  public StdValueInstantiator(DeserializationConfig config, Class<?> valueType)
  {
    _valueTypeDesc = ClassUtil.nameOf(valueType);
    _valueClass = (valueType == null ? Object.class : valueType);
  }
  
  public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
    _valueTypeDesc = (valueType == null ? "UNKNOWN TYPE" : valueType.toString());
    _valueClass = (valueType == null ? Object.class : valueType.getRawClass());
  }
  




  protected StdValueInstantiator(StdValueInstantiator src)
  {
    _valueTypeDesc = _valueTypeDesc;
    _valueClass = _valueClass;
    
    _defaultCreator = _defaultCreator;
    
    _constructorArguments = _constructorArguments;
    _withArgsCreator = _withArgsCreator;
    
    _delegateType = _delegateType;
    _delegateCreator = _delegateCreator;
    _delegateArguments = _delegateArguments;
    
    _arrayDelegateType = _arrayDelegateType;
    _arrayDelegateCreator = _arrayDelegateCreator;
    _arrayDelegateArguments = _arrayDelegateArguments;
    
    _fromStringCreator = _fromStringCreator;
    _fromIntCreator = _fromIntCreator;
    _fromLongCreator = _fromLongCreator;
    _fromDoubleCreator = _fromDoubleCreator;
    _fromBooleanCreator = _fromBooleanCreator;
  }
  







  public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs)
  {
    _defaultCreator = defaultCreator;
    _delegateCreator = delegateCreator;
    _delegateType = delegateType;
    _delegateArguments = delegateArgs;
    _withArgsCreator = withArgsCreator;
    _constructorArguments = constructorArgs;
  }
  



  public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs)
  {
    _arrayDelegateCreator = arrayDelegateCreator;
    _arrayDelegateType = arrayDelegateType;
    _arrayDelegateArguments = arrayDelegateArgs;
  }
  
  public void configureFromStringCreator(AnnotatedWithParams creator) {
    _fromStringCreator = creator;
  }
  
  public void configureFromIntCreator(AnnotatedWithParams creator) {
    _fromIntCreator = creator;
  }
  
  public void configureFromLongCreator(AnnotatedWithParams creator) {
    _fromLongCreator = creator;
  }
  
  public void configureFromDoubleCreator(AnnotatedWithParams creator) {
    _fromDoubleCreator = creator;
  }
  
  public void configureFromBooleanCreator(AnnotatedWithParams creator) {
    _fromBooleanCreator = creator;
  }
  
  public void configureIncompleteParameter(AnnotatedParameter parameter) {
    _incompleteParameter = parameter;
  }
  






  public String getValueTypeDesc()
  {
    return _valueTypeDesc;
  }
  
  public Class<?> getValueClass()
  {
    return _valueClass;
  }
  
  public boolean canCreateFromString()
  {
    return _fromStringCreator != null;
  }
  
  public boolean canCreateFromInt()
  {
    return _fromIntCreator != null;
  }
  
  public boolean canCreateFromLong()
  {
    return _fromLongCreator != null;
  }
  
  public boolean canCreateFromDouble()
  {
    return _fromDoubleCreator != null;
  }
  
  public boolean canCreateFromBoolean()
  {
    return _fromBooleanCreator != null;
  }
  
  public boolean canCreateUsingDefault()
  {
    return _defaultCreator != null;
  }
  
  public boolean canCreateUsingDelegate()
  {
    return _delegateType != null;
  }
  
  public boolean canCreateUsingArrayDelegate()
  {
    return _arrayDelegateType != null;
  }
  
  public boolean canCreateFromObjectWith()
  {
    return _withArgsCreator != null;
  }
  
  public boolean canInstantiate()
  {
    return (canCreateUsingDefault()) || 
      (canCreateUsingDelegate()) || (canCreateUsingArrayDelegate()) || 
      (canCreateFromObjectWith()) || (canCreateFromString()) || 
      (canCreateFromInt()) || (canCreateFromLong()) || 
      (canCreateFromDouble()) || (canCreateFromBoolean());
  }
  
  public JavaType getDelegateType(DeserializationConfig config)
  {
    return _delegateType;
  }
  
  public JavaType getArrayDelegateType(DeserializationConfig config)
  {
    return _arrayDelegateType;
  }
  
  public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config)
  {
    return _constructorArguments;
  }
  






  public Object createUsingDefault(DeserializationContext ctxt)
    throws IOException
  {
    if (_defaultCreator == null) {
      return super.createUsingDefault(ctxt);
    }
    try {
      return _defaultCreator.call();
    } catch (Exception e) {
      return ctxt.handleInstantiationProblem(_valueClass, null, rewrapCtorProblem(ctxt, e));
    }
  }
  
  public Object createFromObjectWith(DeserializationContext ctxt, Object[] args)
    throws IOException
  {
    if (_withArgsCreator == null) {
      return super.createFromObjectWith(ctxt, args);
    }
    try {
      return _withArgsCreator.call(args);
    } catch (Exception e) {
      return ctxt.handleInstantiationProblem(_valueClass, args, rewrapCtorProblem(ctxt, e));
    }
  }
  

  public Object createUsingDelegate(DeserializationContext ctxt, Object delegate)
    throws IOException
  {
    if ((_delegateCreator == null) && 
      (_arrayDelegateCreator != null)) {
      return _createUsingDelegate(_arrayDelegateCreator, _arrayDelegateArguments, ctxt, delegate);
    }
    
    return _createUsingDelegate(_delegateCreator, _delegateArguments, ctxt, delegate);
  }
  
  public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate)
    throws IOException
  {
    if ((_arrayDelegateCreator == null) && 
      (_delegateCreator != null))
    {
      return createUsingDelegate(ctxt, delegate);
    }
    
    return _createUsingDelegate(_arrayDelegateCreator, _arrayDelegateArguments, ctxt, delegate);
  }
  






  public Object createFromString(DeserializationContext ctxt, String value)
    throws IOException
  {
    if (_fromStringCreator == null) {
      return _createFromStringFallbacks(ctxt, value);
    }
    try {
      return _fromStringCreator.call1(value);
    } catch (Throwable t) {
      return ctxt.handleInstantiationProblem(_fromStringCreator.getDeclaringClass(), value, 
        rewrapCtorProblem(ctxt, t));
    }
  }
  

  public Object createFromInt(DeserializationContext ctxt, int value)
    throws IOException
  {
    if (_fromIntCreator != null) {
      Object arg = Integer.valueOf(value);
      try {
        return _fromIntCreator.call1(arg);
      } catch (Throwable t0) {
        return ctxt.handleInstantiationProblem(_fromIntCreator.getDeclaringClass(), arg, 
          rewrapCtorProblem(ctxt, t0));
      }
    }
    
    if (_fromLongCreator != null) {
      Object arg = Long.valueOf(value);
      try {
        return _fromLongCreator.call1(arg);
      } catch (Throwable t0) {
        return ctxt.handleInstantiationProblem(_fromLongCreator.getDeclaringClass(), arg, 
          rewrapCtorProblem(ctxt, t0));
      }
    }
    return super.createFromInt(ctxt, value);
  }
  
  public Object createFromLong(DeserializationContext ctxt, long value)
    throws IOException
  {
    if (_fromLongCreator == null) {
      return super.createFromLong(ctxt, value);
    }
    Object arg = Long.valueOf(value);
    try {
      return _fromLongCreator.call1(arg);
    } catch (Throwable t0) {
      return ctxt.handleInstantiationProblem(_fromLongCreator.getDeclaringClass(), arg, 
        rewrapCtorProblem(ctxt, t0));
    }
  }
  
  public Object createFromDouble(DeserializationContext ctxt, double value)
    throws IOException
  {
    if (_fromDoubleCreator == null) {
      return super.createFromDouble(ctxt, value);
    }
    Object arg = Double.valueOf(value);
    try {
      return _fromDoubleCreator.call1(arg);
    } catch (Throwable t0) {
      return ctxt.handleInstantiationProblem(_fromDoubleCreator.getDeclaringClass(), arg, 
        rewrapCtorProblem(ctxt, t0));
    }
  }
  
  public Object createFromBoolean(DeserializationContext ctxt, boolean value)
    throws IOException
  {
    if (_fromBooleanCreator == null) {
      return super.createFromBoolean(ctxt, value);
    }
    Boolean arg = Boolean.valueOf(value);
    try {
      return _fromBooleanCreator.call1(arg);
    } catch (Throwable t0) {
      return ctxt.handleInstantiationProblem(_fromBooleanCreator.getDeclaringClass(), arg, 
        rewrapCtorProblem(ctxt, t0));
    }
  }
  






  public AnnotatedWithParams getDelegateCreator()
  {
    return _delegateCreator;
  }
  
  public AnnotatedWithParams getArrayDelegateCreator()
  {
    return _arrayDelegateCreator;
  }
  
  public AnnotatedWithParams getDefaultCreator()
  {
    return _defaultCreator;
  }
  
  public AnnotatedWithParams getWithArgsCreator()
  {
    return _withArgsCreator;
  }
  
  public AnnotatedParameter getIncompleteParameter()
  {
    return _incompleteParameter;
  }
  












  @Deprecated
  protected JsonMappingException wrapException(Throwable t)
  {
    for (Throwable curr = t; curr != null; curr = curr.getCause()) {
      if ((curr instanceof JsonMappingException)) {
        return (JsonMappingException)curr;
      }
    }
    return new JsonMappingException(null, "Instantiation of " + 
      getValueTypeDesc() + " value failed: " + ClassUtil.exceptionMessage(t), t);
  }
  






  @Deprecated
  protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t)
  {
    for (Throwable curr = t; curr != null; curr = curr.getCause()) {
      if ((curr instanceof JsonMappingException)) {
        return (JsonMappingException)curr;
      }
    }
    return ctxt.instantiationException(getValueClass(), t);
  }
  










  protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t)
  {
    if ((t instanceof JsonMappingException)) {
      return (JsonMappingException)t;
    }
    return ctxt.instantiationException(getValueClass(), t);
  }
  










  protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t)
  {
    if (((t instanceof ExceptionInInitializerError)) || ((t instanceof InvocationTargetException)))
    {

      Throwable cause = t.getCause();
      if (cause != null) {
        t = cause;
      }
    }
    return wrapAsJsonMappingException(ctxt, t);
  }
  









  private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate)
    throws IOException
  {
    if (delegateCreator == null) {
      throw new IllegalStateException("No delegate constructor for " + getValueTypeDesc());
    }
    try
    {
      if (delegateArguments == null) {
        return delegateCreator.call1(delegate);
      }
      
      int len = delegateArguments.length;
      Object[] args = new Object[len];
      for (int i = 0; i < len; i++) {
        SettableBeanProperty prop = delegateArguments[i];
        if (prop == null) {
          args[i] = delegate;
        } else {
          args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
        }
      }
      
      return delegateCreator.call(args);
    } catch (Throwable t) {
      throw rewrapCtorProblem(ctxt, t);
    }
  }
}
