package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.BeanProperty.Bogus;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;


public class MapProperty
  extends PropertyWriter
{
  private static final long serialVersionUID = 1L;
  private static final BeanProperty BOGUS_PROP = new BeanProperty.Bogus();
  
  protected final TypeSerializer _typeSerializer;
  
  protected final BeanProperty _property;
  protected Object _key;
  protected Object _value;
  protected JsonSerializer<Object> _keySerializer;
  protected JsonSerializer<Object> _valueSerializer;
  
  public MapProperty(TypeSerializer typeSer, BeanProperty prop)
  {
    super(prop == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : prop.getMetadata());
    _typeSerializer = typeSer;
    _property = (prop == null ? BOGUS_PROP : prop);
  }
  







  public void reset(Object key, Object value, JsonSerializer<Object> keySer, JsonSerializer<Object> valueSer)
  {
    _key = key;
    _value = value;
    _keySerializer = keySer;
    _valueSerializer = valueSer;
  }
  

  @Deprecated
  public void reset(Object key, JsonSerializer<Object> keySer, JsonSerializer<Object> valueSer)
  {
    reset(key, _value, keySer, valueSer);
  }
  
  public String getName()
  {
    if ((_key instanceof String)) {
      return (String)_key;
    }
    return String.valueOf(_key);
  }
  


  public Object getValue()
  {
    return _value;
  }
  


  public void setValue(Object v)
  {
    _value = v;
  }
  
  public PropertyName getFullName()
  {
    return new PropertyName(getName());
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> acls)
  {
    return _property.getAnnotation(acls);
  }
  
  public <A extends Annotation> A getContextAnnotation(Class<A> acls)
  {
    return _property.getContextAnnotation(acls);
  }
  

  public void serializeAsField(Object map, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    _keySerializer.serialize(_key, gen, provider);
    if (_typeSerializer == null) {
      _valueSerializer.serialize(_value, gen, provider);
    } else {
      _valueSerializer.serializeWithType(_value, gen, provider, _typeSerializer);
    }
  }
  

  public void serializeAsOmittedField(Object map, JsonGenerator gen, SerializerProvider provider)
    throws Exception
  {
    if (!gen.canOmitFields()) {
      gen.writeOmittedField(getName());
    }
  }
  

  public void serializeAsElement(Object map, JsonGenerator gen, SerializerProvider provider)
    throws Exception
  {
    if (_typeSerializer == null) {
      _valueSerializer.serialize(_value, gen, provider);
    } else {
      _valueSerializer.serializeWithType(_value, gen, provider, _typeSerializer);
    }
  }
  

  public void serializeAsPlaceholder(Object value, JsonGenerator gen, SerializerProvider provider)
    throws Exception
  {
    gen.writeNull();
  }
  








  public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
    throws JsonMappingException
  {
    _property.depositSchemaProperty(objectVisitor, provider);
  }
  

  @Deprecated
  public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider provider)
    throws JsonMappingException
  {}
  

  public JavaType getType()
  {
    return _property.getType();
  }
  
  public PropertyName getWrapperName()
  {
    return _property.getWrapperName();
  }
  
  public AnnotatedMember getMember()
  {
    return _property.getMember();
  }
}
