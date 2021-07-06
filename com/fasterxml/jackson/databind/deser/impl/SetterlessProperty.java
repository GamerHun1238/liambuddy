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









public final class SetterlessProperty
  extends SettableBeanProperty
{
  private static final long serialVersionUID = 1L;
  protected final AnnotatedMethod _annotated;
  protected final Method _getter;
  
  public SetterlessProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method)
  {
    super(propDef, type, typeDeser, contextAnnotations);
    _annotated = method;
    _getter = method.getAnnotated();
  }
  
  protected SetterlessProperty(SetterlessProperty src, JsonDeserializer<?> deser, NullValueProvider nva)
  {
    super(src, deser, nva);
    _annotated = _annotated;
    _getter = _getter;
  }
  
  protected SetterlessProperty(SetterlessProperty src, PropertyName newName) {
    super(src, newName);
    _annotated = _annotated;
    _getter = _getter;
  }
  
  public SettableBeanProperty withName(PropertyName newName)
  {
    return new SetterlessProperty(this, newName);
  }
  
  public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser)
  {
    if (_valueDeserializer == deser) {
      return this;
    }
    
    NullValueProvider nvp = _valueDeserializer == _nullProvider ? deser : _nullProvider;
    return new SetterlessProperty(this, deser, nvp);
  }
  
  public SettableBeanProperty withNullProvider(NullValueProvider nva)
  {
    return new SetterlessProperty(this, _valueDeserializer, nva);
  }
  
  public void fixAccess(DeserializationConfig config)
  {
    _annotated.fixAccess(config
      .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
  }
  






  public <A extends Annotation> A getAnnotation(Class<A> acls)
  {
    return _annotated.getAnnotation(acls);
  }
  
  public AnnotatedMember getMember() { return _annotated; }
  







  public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NULL))
    {

      return;
    }
    
    if (_valueTypeDeserializer != null) {
      ctxt.reportBadDefinition(getType(), String.format("Problem deserializing 'setterless' property (\"%s\"): no way to handle typed deser with setterless yet", new Object[] {
      
        getName() }));
    }
    

    try
    {
      toModify = _getter.invoke(instance, (Object[])null);
    } catch (Exception e) { Object toModify;
      _throwAsIOE(p, e); return;
    }
    

    Object toModify;
    
    if (toModify == null) {
      ctxt.reportBadDefinition(getType(), String.format("Problem deserializing 'setterless' property '%s': get method returned null", new Object[] {
      
        getName() }));
    }
    _valueDeserializer.deserialize(p, ctxt, toModify);
  }
  

  public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    deserializeAndSet(p, ctxt, instance);
    return instance;
  }
  
  public final void set(Object instance, Object value) throws IOException
  {
    throw new UnsupportedOperationException("Should never call `set()` on setterless property ('" + getName() + "')");
  }
  
  public Object setAndReturn(Object instance, Object value)
    throws IOException
  {
    set(instance, value);
    return instance;
  }
}
