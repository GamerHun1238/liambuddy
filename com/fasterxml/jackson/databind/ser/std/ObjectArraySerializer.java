package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
import java.io.IOException;




























@JacksonStdImpl
public class ObjectArraySerializer
  extends ArraySerializerBase<Object[]>
  implements ContextualSerializer
{
  protected final boolean _staticTyping;
  protected final JavaType _elementType;
  protected final TypeSerializer _valueTypeSerializer;
  protected JsonSerializer<Object> _elementSerializer;
  protected PropertySerializerMap _dynamicSerializers;
  
  public ObjectArraySerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> elementSerializer)
  {
    super([Ljava.lang.Object.class);
    _elementType = elemType;
    _staticTyping = staticTyping;
    _valueTypeSerializer = vts;
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _elementSerializer = elementSerializer;
  }
  
  public ObjectArraySerializer(ObjectArraySerializer src, TypeSerializer vts)
  {
    super(src);
    _elementType = _elementType;
    _valueTypeSerializer = vts;
    _staticTyping = _staticTyping;
    

    _dynamicSerializers = _dynamicSerializers;
    _elementSerializer = _elementSerializer;
  }
  



  public ObjectArraySerializer(ObjectArraySerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle)
  {
    super(src, property, unwrapSingle);
    _elementType = _elementType;
    _valueTypeSerializer = vts;
    _staticTyping = _staticTyping;
    
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _elementSerializer = elementSerializer;
  }
  
  public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle)
  {
    return new ObjectArraySerializer(this, prop, _valueTypeSerializer, _elementSerializer, unwrapSingle);
  }
  

  public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts)
  {
    return new ObjectArraySerializer(_elementType, _staticTyping, vts, _elementSerializer);
  }
  
  public ObjectArraySerializer withResolved(BeanProperty prop, TypeSerializer vts, JsonSerializer<?> ser, Boolean unwrapSingle)
  {
    if ((_property == prop) && (ser == _elementSerializer) && (_valueTypeSerializer == vts) && (_unwrapSingle == unwrapSingle))
    {
      return this;
    }
    return new ObjectArraySerializer(this, prop, vts, ser, unwrapSingle);
  }
  








  public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
    throws JsonMappingException
  {
    TypeSerializer vts = _valueTypeSerializer;
    if (vts != null) {
      vts = vts.forProperty(property);
    }
    JsonSerializer<?> ser = null;
    Boolean unwrapSingle = null;
    

    if (property != null) {
      AnnotatedMember m = property.getMember();
      AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
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
    if (ser == null) {
      ser = _elementSerializer;
    }
    
    ser = findContextualConvertingSerializer(serializers, property, ser);
    if (ser == null)
    {

      if ((_elementType != null) && 
        (_staticTyping) && (!_elementType.isJavaLangObject())) {
        ser = serializers.findValueSerializer(_elementType, property);
      }
    }
    
    return withResolved(property, vts, ser, unwrapSingle);
  }
  






  public JavaType getContentType()
  {
    return _elementType;
  }
  
  public JsonSerializer<?> getContentSerializer()
  {
    return _elementSerializer;
  }
  
  public boolean isEmpty(SerializerProvider prov, Object[] value)
  {
    return value.length == 0;
  }
  
  public boolean hasSingleElement(Object[] value)
  {
    return value.length == 1;
  }
  






  public final void serialize(Object[] value, JsonGenerator gen, SerializerProvider provider)
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
  
  public void serializeContents(Object[] value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    int len = value.length;
    if (len == 0) {
      return;
    }
    if (_elementSerializer != null) {
      serializeContentsUsing(value, gen, provider, _elementSerializer);
      return;
    }
    if (_valueTypeSerializer != null) {
      serializeTypedContents(value, gen, provider);
      return;
    }
    int i = 0;
    Object elem = null;
    try {
      PropertySerializerMap serializers = _dynamicSerializers;
      for (; i < len; i++) {
        elem = value[i];
        if (elem == null) {
          provider.defaultSerializeNull(gen);
        }
        else {
          Class<?> cc = elem.getClass();
          JsonSerializer<Object> serializer = serializers.serializerFor(cc);
          if (serializer == null) {
            if (_elementType.hasGenericTypes()) {
              serializer = _findAndAddDynamic(serializers, provider
                .constructSpecializedType(_elementType, cc), provider);
            } else {
              serializer = _findAndAddDynamic(serializers, cc, provider);
            }
          }
          serializer.serialize(elem, gen, provider);
        }
      }
    } catch (Exception e) { wrapAndThrow(provider, e, elem, i);
    }
  }
  
  public void serializeContentsUsing(Object[] value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser)
    throws IOException
  {
    int len = value.length;
    TypeSerializer typeSer = _valueTypeSerializer;
    
    int i = 0;
    Object elem = null;
    try {
      for (; i < len; i++) {
        elem = value[i];
        if (elem == null) {
          provider.defaultSerializeNull(jgen);

        }
        else if (typeSer == null) {
          ser.serialize(elem, jgen, provider);
        } else {
          ser.serializeWithType(elem, jgen, provider, typeSer);
        }
      }
    } catch (Exception e) {
      wrapAndThrow(provider, e, elem, i);
    }
  }
  
  public void serializeTypedContents(Object[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException
  {
    int len = value.length;
    TypeSerializer typeSer = _valueTypeSerializer;
    int i = 0;
    Object elem = null;
    try {
      PropertySerializerMap serializers = _dynamicSerializers;
      for (; i < len; i++) {
        elem = value[i];
        if (elem == null) {
          provider.defaultSerializeNull(jgen);
        }
        else {
          Class<?> cc = elem.getClass();
          JsonSerializer<Object> serializer = serializers.serializerFor(cc);
          if (serializer == null) {
            serializer = _findAndAddDynamic(serializers, cc, provider);
          }
          serializer.serializeWithType(elem, jgen, provider, typeSer);
        }
      }
    } catch (Exception e) { wrapAndThrow(provider, e, elem, i);
    }
  }
  

  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    JsonArrayFormatVisitor arrayVisitor = visitor.expectArrayFormat(typeHint);
    if (arrayVisitor != null) {
      JavaType contentType = _elementType;
      









      JsonSerializer<?> valueSer = _elementSerializer;
      if (valueSer == null) {
        valueSer = visitor.getProvider().findValueSerializer(contentType, _property);
      }
      arrayVisitor.itemsFormat(valueSer, contentType);
    }
  }
  
  protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
    throws JsonMappingException
  {
    PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
    
    if (map != map) {
      _dynamicSerializers = map;
    }
    return serializer;
  }
  
  protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider)
    throws JsonMappingException
  {
    PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
    
    if (map != map) {
      _dynamicSerializers = map;
    }
    return serializer;
  }
}
