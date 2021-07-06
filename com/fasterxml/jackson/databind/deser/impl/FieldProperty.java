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
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;














public final class FieldProperty
  extends SettableBeanProperty
{
  private static final long serialVersionUID = 1L;
  protected final AnnotatedField _annotated;
  protected final transient Field _field;
  protected final boolean _skipNulls;
  
  public FieldProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedField field)
  {
    super(propDef, type, typeDeser, contextAnnotations);
    _annotated = field;
    _field = field.getAnnotated();
    _skipNulls = NullsConstantProvider.isSkipper(_nullProvider);
  }
  
  protected FieldProperty(FieldProperty src, JsonDeserializer<?> deser, NullValueProvider nva)
  {
    super(src, deser, nva);
    _annotated = _annotated;
    _field = _field;
    _skipNulls = NullsConstantProvider.isSkipper(nva);
  }
  
  protected FieldProperty(FieldProperty src, PropertyName newName) {
    super(src, newName);
    _annotated = _annotated;
    _field = _field;
    _skipNulls = _skipNulls;
  }
  



  protected FieldProperty(FieldProperty src)
  {
    super(src);
    _annotated = _annotated;
    Field f = _annotated.getAnnotated();
    if (f == null) {
      throw new IllegalArgumentException("Missing field (broken JDK (de)serialization?)");
    }
    _field = f;
    _skipNulls = _skipNulls;
  }
  
  public SettableBeanProperty withName(PropertyName newName)
  {
    return new FieldProperty(this, newName);
  }
  
  public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser)
  {
    if (_valueDeserializer == deser) {
      return this;
    }
    
    NullValueProvider nvp = _valueDeserializer == _nullProvider ? deser : _nullProvider;
    return new FieldProperty(this, deser, nvp);
  }
  
  public SettableBeanProperty withNullProvider(NullValueProvider nva)
  {
    return new FieldProperty(this, _valueDeserializer, nva);
  }
  
  public void fixAccess(DeserializationConfig config)
  {
    ClassUtil.checkAndFixAccess(_field, config
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
      _field.set(instance, value);
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
      _field.set(instance, value);
    } catch (Exception e) {
      _throwAsIOE(p, e, value);
    }
    return instance;
  }
  
  public void set(Object instance, Object value) throws IOException
  {
    try
    {
      _field.set(instance, value);
    }
    catch (Exception e) {
      _throwAsIOE(e, value);
    }
  }
  
  public Object setAndReturn(Object instance, Object value) throws IOException
  {
    try
    {
      _field.set(instance, value);
    }
    catch (Exception e) {
      _throwAsIOE(e, value);
    }
    return instance;
  }
  





  Object readResolve()
  {
    return new FieldProperty(this);
  }
}
