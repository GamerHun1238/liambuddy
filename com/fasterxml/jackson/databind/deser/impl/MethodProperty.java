package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;











public final class MethodProperty
  extends SettableBeanProperty
{
  private static final long serialVersionUID = 1L;
  protected final AnnotatedMethod _annotated;
  protected final transient Method _setter;
  protected final boolean _skipNulls;
  
  public MethodProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method)
  {
    super(propDef, type, typeDeser, contextAnnotations);
    _annotated = method;
    _setter = method.getAnnotated();
    _skipNulls = NullsConstantProvider.isSkipper(_nullProvider);
  }
  
  protected MethodProperty(MethodProperty src, JsonDeserializer<?> deser, NullValueProvider nva)
  {
    super(src, deser, nva);
    _annotated = _annotated;
    _setter = _setter;
    _skipNulls = NullsConstantProvider.isSkipper(nva);
  }
  
  protected MethodProperty(MethodProperty src, PropertyName newName) {
    super(src, newName);
    _annotated = _annotated;
    _setter = _setter;
    _skipNulls = _skipNulls;
  }
  


  protected MethodProperty(MethodProperty src, Method m)
  {
    super(src);
    _annotated = _annotated;
    _setter = m;
    _skipNulls = _skipNulls;
  }
  
  public SettableBeanProperty withName(PropertyName newName)
  {
    return new MethodProperty(this, newName);
  }
  
  public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser)
  {
    if (_valueDeserializer == deser) {
      return this;
    }
    
    NullValueProvider nvp = _valueDeserializer == _nullProvider ? deser : _nullProvider;
    return new MethodProperty(this, deser, nvp);
  }
  
  public SettableBeanProperty withNullProvider(NullValueProvider nva)
  {
    return new MethodProperty(this, _valueDeserializer, nva);
  }
  
  public void fixAccess(DeserializationConfig config)
  {
    _annotated.fixAccess(config
      .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
  }
  






  public <A extends Annotation> A getAnnotation(Class<A> acls)
  {
    return _annotated == null ? null : _annotated.getAnnotation(acls);
  }
  
  public AnnotatedMember getMember() { return _annotated; }
  


  public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    Object value;
    

    Object value;
    

    if (p.hasToken(JsonToken.VALUE_NULL)) {
      if (_skipNulls) {
        return;
      }
      value = _nullProvider.getNullValue(ctxt);
    } else if (_valueTypeDeserializer == null) {
      Object value = _valueDeserializer.deserialize(p, ctxt);
      
      if (value == null) {
        if (_skipNulls) {
          return;
        }
        value = _nullProvider.getNullValue(ctxt);
      }
    } else {
      value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }
    try {
      _setter.invoke(instance, new Object[] { value });
    } catch (Exception e) {
      _throwAsIOE(p, e, value);
    }
  }
  
  public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    Object value;
    Object value;
    if (p.hasToken(JsonToken.VALUE_NULL)) {
      if (_skipNulls) {
        return instance;
      }
      value = _nullProvider.getNullValue(ctxt);
    } else if (_valueTypeDeserializer == null) {
      Object value = _valueDeserializer.deserialize(p, ctxt);
      
      if (value == null) {
        if (_skipNulls) {
          return instance;
        }
        value = _nullProvider.getNullValue(ctxt);
      }
    } else {
      value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }
    try {
      Object result = _setter.invoke(instance, new Object[] { value });
      return result == null ? instance : result;
    } catch (Exception e) {
      _throwAsIOE(p, e, value); }
    return null;
  }
  
  public final void set(Object instance, Object value)
    throws IOException
  {
    try
    {
      _setter.invoke(instance, new Object[] { value });
    }
    catch (Exception e) {
      _throwAsIOE(e, value);
    }
  }
  
  public Object setAndReturn(Object instance, Object value) throws IOException
  {
    try
    {
      Object result = _setter.invoke(instance, new Object[] { value });
      return result == null ? instance : result;
    }
    catch (Exception e) {
      _throwAsIOE(e, value); }
    return null;
  }
  






  Object readResolve()
  {
    return new MethodProperty(this, _annotated.getAnnotated());
  }
}
