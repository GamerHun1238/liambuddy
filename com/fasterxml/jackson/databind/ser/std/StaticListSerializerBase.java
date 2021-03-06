package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;





public abstract class StaticListSerializerBase<T extends Collection<?>>
  extends StdSerializer<T>
  implements ContextualSerializer
{
  protected final Boolean _unwrapSingle;
  
  protected StaticListSerializerBase(Class<?> cls)
  {
    super(cls, false);
    _unwrapSingle = null;
  }
  



  protected StaticListSerializerBase(StaticListSerializerBase<?> src, Boolean unwrapSingle)
  {
    super(src);
    _unwrapSingle = unwrapSingle;
  }
  







  public abstract JsonSerializer<?> _withResolved(BeanProperty paramBeanProperty, Boolean paramBoolean);
  






  public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<?> ser = null;
    Boolean unwrapSingle = null;
    
    if (property != null) {
      AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
      AnnotatedMember m = property.getMember();
      if (m != null) {
        Object serDef = intr.findContentSerializer(m);
        if (serDef != null) {
          ser = serializers.serializerInstance(m, serDef);
        }
      }
    }
    JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
    if (format != null) {
      unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    }
    
    ser = findContextualConvertingSerializer(serializers, property, ser);
    if (ser == null) {
      ser = serializers.findValueSerializer(String.class, property);
    }
    
    if (isDefaultSerializer(ser)) {
      if (unwrapSingle == _unwrapSingle) {
        return this;
      }
      return _withResolved(property, unwrapSingle);
    }
    

    return new CollectionSerializer(serializers.constructType(String.class), true, null, ser);
  }
  

  public boolean isEmpty(SerializerProvider provider, T value)
  {
    return (value == null) || (value.size() == 0);
  }
  
  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
  {
    return createSchemaNode("array", true).set("items", contentSchema());
  }
  
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
  {
    JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
    if (v2 != null) {
      acceptContentVisitor(v2);
    }
  }
  
  protected abstract JsonNode contentSchema();
  
  protected abstract void acceptContentVisitor(JsonArrayFormatVisitor paramJsonArrayFormatVisitor)
    throws JsonMappingException;
  
  public abstract void serializeWithType(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider, TypeSerializer paramTypeSerializer)
    throws IOException;
}
