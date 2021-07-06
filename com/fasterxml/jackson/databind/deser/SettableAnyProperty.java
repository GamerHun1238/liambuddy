package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

























public class SettableAnyProperty
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final BeanProperty _property;
  protected final AnnotatedMember _setter;
  final boolean _setterIsField;
  protected final JavaType _type;
  protected JsonDeserializer<Object> _valueDeserializer;
  protected final TypeDeserializer _valueTypeDeserializer;
  protected final KeyDeserializer _keyDeserializer;
  
  public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser)
  {
    _property = property;
    _setter = setter;
    _type = type;
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = typeDeser;
    _keyDeserializer = keyDeser;
    _setterIsField = (setter instanceof AnnotatedField);
  }
  

  @Deprecated
  public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser)
  {
    this(property, setter, type, null, valueDeser, typeDeser);
  }
  
  public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
    return new SettableAnyProperty(_property, _setter, _type, _keyDeserializer, deser, _valueTypeDeserializer);
  }
  
  public void fixAccess(DeserializationConfig config)
  {
    _setter.fixAccess(config
      .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
  }
  









  Object readResolve()
  {
    if ((_setter == null) || (_setter.getAnnotated() == null)) {
      throw new IllegalArgumentException("Missing method (broken JDK (de)serialization?)");
    }
    return this;
  }
  






  public BeanProperty getProperty() { return _property; }
  
  public boolean hasValueDeserializer() { return _valueDeserializer != null; }
  
  public JavaType getType() { return _type; }
  











  public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance, String propName)
    throws IOException
  {
    try
    {
      Object key = _keyDeserializer == null ? propName : _keyDeserializer.deserializeKey(propName, ctxt);
      set(instance, key, deserialize(p, ctxt));
    } catch (UnresolvedForwardReference reference) {
      if (_valueDeserializer.getObjectIdReader() == null) {
        throw JsonMappingException.from(p, "Unresolved forward reference but no identity info.", reference);
      }
      
      AnySetterReferring referring = new AnySetterReferring(this, reference, _type.getRawClass(), instance, propName);
      reference.getRoid().appendReferring(referring);
    }
  }
  
  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NULL)) {
      return _valueDeserializer.getNullValue(ctxt);
    }
    if (_valueTypeDeserializer != null) {
      return _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }
    return _valueDeserializer.deserialize(p, ctxt);
  }
  
  public void set(Object instance, Object propName, Object value)
    throws IOException
  {
    try
    {
      if (_setterIsField) {
        AnnotatedField field = (AnnotatedField)_setter;
        Map<Object, Object> val = (Map)field.getValue(instance);
        




        if (val != null)
        {
          val.put(propName, value);
        }
      }
      else {
        ((AnnotatedMethod)_setter).callOnWith(instance, new Object[] { propName, value });
      }
    } catch (Exception e) {
      _throwAsIOE(e, propName, value);
    }
  }
  











  protected void _throwAsIOE(Exception e, Object propName, Object value)
    throws IOException
  {
    if ((e instanceof IllegalArgumentException)) {
      String actType = ClassUtil.classNameOf(value);
      StringBuilder msg = new StringBuilder("Problem deserializing \"any\" property '").append(propName);
      msg.append("' of class " + getClassName() + " (expected type: ").append(_type);
      msg.append("; actual type: ").append(actType).append(")");
      String origMsg = ClassUtil.exceptionMessage(e);
      if (origMsg != null) {
        msg.append(", problem: ").append(origMsg);
      } else {
        msg.append(" (no error message provided)");
      }
      throw new JsonMappingException(null, msg.toString(), e);
    }
    ClassUtil.throwIfIOE(e);
    ClassUtil.throwIfRTE(e);
    
    Throwable t = ClassUtil.getRootCause(e);
    throw new JsonMappingException(null, ClassUtil.exceptionMessage(t), t);
  }
  
  private String getClassName() { return _setter.getDeclaringClass().getName(); }
  
  public String toString() { return "[any property on class " + getClassName() + "]"; }
  
  private static class AnySetterReferring extends ReadableObjectId.Referring
  {
    private final SettableAnyProperty _parent;
    private final Object _pojo;
    private final String _propName;
    
    public AnySetterReferring(SettableAnyProperty parent, UnresolvedForwardReference reference, Class<?> type, Object instance, String propName)
    {
      super(type);
      _parent = parent;
      _pojo = instance;
      _propName = propName;
    }
    

    public void handleResolvedForwardReference(Object id, Object value)
      throws IOException
    {
      if (!hasId(id)) {
        throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
      }
      
      _parent.set(_pojo, _propName, value);
    }
  }
}
