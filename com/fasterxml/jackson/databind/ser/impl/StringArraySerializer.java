package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.ArraySerializerBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.reflect.Type;


@JacksonStdImpl
public class StringArraySerializer
  extends ArraySerializerBase<String[]>
  implements ContextualSerializer
{
  private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(String.class);
  
  public static final StringArraySerializer instance = new StringArraySerializer();
  




  protected final JsonSerializer<Object> _elementSerializer;
  





  protected StringArraySerializer()
  {
    super([Ljava.lang.String.class);
    _elementSerializer = null;
  }
  

  public StringArraySerializer(StringArraySerializer src, BeanProperty prop, JsonSerializer<?> ser, Boolean unwrapSingle)
  {
    super(src, prop, unwrapSingle);
    _elementSerializer = ser;
  }
  
  public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle)
  {
    return new StringArraySerializer(this, prop, _elementSerializer, unwrapSingle);
  }
  




  public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts)
  {
    return this;
  }
  












  public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<?> ser = null;
    

    if (property != null) {
      AnnotationIntrospector ai = provider.getAnnotationIntrospector();
      AnnotatedMember m = property.getMember();
      if (m != null) {
        Object serDef = ai.findContentSerializer(m);
        if (serDef != null) {
          ser = provider.serializerInstance(m, serDef);
        }
      }
    }
    

    Boolean unwrapSingle = findFormatFeature(provider, property, [Ljava.lang.String.class, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    
    if (ser == null) {
      ser = _elementSerializer;
    }
    
    ser = findContextualConvertingSerializer(provider, property, ser);
    if (ser == null) {
      ser = provider.findValueSerializer(String.class, property);
    }
    
    if (isDefaultSerializer(ser)) {
      ser = null;
    }
    
    if ((ser == _elementSerializer) && (unwrapSingle == _unwrapSingle)) {
      return this;
    }
    return new StringArraySerializer(this, property, ser, unwrapSingle);
  }
  






  public JavaType getContentType()
  {
    return VALUE_TYPE;
  }
  
  public JsonSerializer<?> getContentSerializer()
  {
    return _elementSerializer;
  }
  
  public boolean isEmpty(SerializerProvider prov, String[] value)
  {
    return value.length == 0;
  }
  
  public boolean hasSingleElement(String[] value)
  {
    return value.length == 1;
  }
  







  public final void serialize(String[] value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    int len = value.length;
    if ((len == 1) && (
      ((_unwrapSingle == null) && 
      (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED))) || (_unwrapSingle == Boolean.TRUE)))
    {
      serializeContents(value, gen, provider);
      return;
    }
    
    gen.writeStartArray(value, len);
    serializeContents(value, gen, provider);
    gen.writeEndArray();
  }
  

  public void serializeContents(String[] value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    int len = value.length;
    if (len == 0) {
      return;
    }
    if (_elementSerializer != null) {
      serializeContentsSlow(value, gen, provider, _elementSerializer);
      return;
    }
    for (int i = 0; i < len; i++) {
      String str = value[i];
      if (str == null) {
        gen.writeNull();
      } else {
        gen.writeString(value[i]);
      }
    }
  }
  
  private void serializeContentsSlow(String[] value, JsonGenerator gen, SerializerProvider provider, JsonSerializer<Object> ser)
    throws IOException
  {
    int i = 0; for (int len = value.length; i < len; i++) {
      String str = value[i];
      if (str == null) {
        provider.defaultSerializeNull(gen);
      } else {
        ser.serialize(value[i], gen, provider);
      }
    }
  }
  
  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
  {
    return createSchemaNode("array", true).set("items", createSchemaNode("string"));
  }
  
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    visitArrayFormat(visitor, typeHint, JsonFormatTypes.STRING);
  }
}
