package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import java.util.Map;










public class AnyGetterWriter
{
  protected final BeanProperty _property;
  protected final AnnotatedMember _accessor;
  protected JsonSerializer<Object> _serializer;
  protected MapSerializer _mapSerializer;
  
  public AnyGetterWriter(BeanProperty property, AnnotatedMember accessor, JsonSerializer<?> serializer)
  {
    _accessor = accessor;
    _property = property;
    _serializer = serializer;
    if ((serializer instanceof MapSerializer)) {
      _mapSerializer = ((MapSerializer)serializer);
    }
  }
  


  public void fixAccess(SerializationConfig config)
  {
    _accessor.fixAccess(config
      .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
  }
  
  public void getAndSerialize(Object bean, JsonGenerator gen, SerializerProvider provider)
    throws Exception
  {
    Object value = _accessor.getValue(bean);
    if (value == null) {
      return;
    }
    if (!(value instanceof Map)) {
      provider.reportBadDefinition(_property.getType(), String.format("Value returned by 'any-getter' %s() not java.util.Map but %s", new Object[] {_accessor
      
        .getName(), value.getClass().getName() }));
    }
    
    if (_mapSerializer != null) {
      _mapSerializer.serializeFields((Map)value, gen, provider);
      return;
    }
    _serializer.serialize(value, gen, provider);
  }
  




  public void getAndFilter(Object bean, JsonGenerator gen, SerializerProvider provider, PropertyFilter filter)
    throws Exception
  {
    Object value = _accessor.getValue(bean);
    if (value == null) {
      return;
    }
    if (!(value instanceof Map)) {
      provider.reportBadDefinition(_property.getType(), 
        String.format("Value returned by 'any-getter' (%s()) not java.util.Map but %s", new Object[] {_accessor
        .getName(), value.getClass().getName() }));
    }
    
    if (_mapSerializer != null) {
      _mapSerializer.serializeFilteredAnyProperties(provider, gen, bean, (Map)value, filter, null);
      
      return;
    }
    
    _serializer.serialize(value, gen, provider);
  }
  


  public void resolve(SerializerProvider provider)
    throws JsonMappingException
  {
    if ((_serializer instanceof ContextualSerializer)) {
      JsonSerializer<?> ser = provider.handlePrimaryContextualization(_serializer, _property);
      _serializer = ser;
      if ((ser instanceof MapSerializer)) {
        _mapSerializer = ((MapSerializer)ser);
      }
    }
  }
}
