package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.Constructor;

















public final class InnerClassProperty
  extends SettableBeanProperty.Delegating
{
  private static final long serialVersionUID = 1L;
  protected final transient Constructor<?> _creator;
  protected AnnotatedConstructor _annotated;
  
  public InnerClassProperty(SettableBeanProperty delegate, Constructor<?> ctor)
  {
    super(delegate);
    _creator = ctor;
  }
  




  protected InnerClassProperty(SettableBeanProperty src, AnnotatedConstructor ann)
  {
    super(src);
    _annotated = ann;
    _creator = (_annotated == null ? null : _annotated.getAnnotated());
    if (_creator == null) {
      throw new IllegalArgumentException("Missing constructor (broken JDK (de)serialization?)");
    }
  }
  
  protected SettableBeanProperty withDelegate(SettableBeanProperty d)
  {
    if (d == delegate) {
      return this;
    }
    return new InnerClassProperty(d, _creator);
  }
  







  public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean)
    throws IOException
  {
    JsonToken t = p.currentToken();
    Object value;
    Object value; if (t == JsonToken.VALUE_NULL) {
      value = _valueDeserializer.getNullValue(ctxt); } else { Object value;
      if (_valueTypeDeserializer != null) {
        value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
      } else {
        try {
          value = _creator.newInstance(new Object[] { bean });
        } catch (Exception e) { Object value;
          ClassUtil.unwrapAndThrowAsIAE(e, String.format("Failed to instantiate class %s, problem: %s", new Object[] {_creator
          
            .getDeclaringClass().getName(), e.getMessage() }));
          value = null;
        }
        _valueDeserializer.deserialize(p, ctxt, value);
      } }
    set(bean, value);
  }
  

  public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    return setAndReturn(instance, deserialize(p, ctxt));
  }
  










  Object readResolve()
  {
    return new InnerClassProperty(this, _annotated);
  }
  
  Object writeReplace()
  {
    if (_annotated == null) {
      return new InnerClassProperty(this, new AnnotatedConstructor(null, _creator, null, null));
    }
    return this;
  }
}
