package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
import java.io.IOException;
import java.lang.reflect.Type;





































public abstract class AsArraySerializerBase<T>
  extends ContainerSerializer<T>
  implements ContextualSerializer
{
  protected final JavaType _elementType;
  protected final BeanProperty _property;
  protected final boolean _staticTyping;
  protected final Boolean _unwrapSingle;
  protected final TypeSerializer _valueTypeSerializer;
  protected final JsonSerializer<Object> _elementSerializer;
  protected PropertySerializerMap _dynamicSerializers;
  
  protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> elementSerializer)
  {
    super(cls, false);
    _elementType = et;
    
    _staticTyping = ((staticTyping) || ((et != null) && (et.isFinal())));
    _valueTypeSerializer = vts;
    _property = null;
    _elementSerializer = elementSerializer;
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _unwrapSingle = null;
  }
  






  @Deprecated
  protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> elementSerializer)
  {
    super(cls, false);
    _elementType = et;
    
    _staticTyping = ((staticTyping) || ((et != null) && (et.isFinal())));
    _valueTypeSerializer = vts;
    _property = property;
    _elementSerializer = elementSerializer;
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _unwrapSingle = null;
  }
  



  protected AsArraySerializerBase(AsArraySerializerBase<?> src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle)
  {
    super(src);
    _elementType = _elementType;
    _staticTyping = _staticTyping;
    _valueTypeSerializer = vts;
    _property = property;
    _elementSerializer = elementSerializer;
    
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _unwrapSingle = unwrapSingle;
  }
  




  @Deprecated
  protected AsArraySerializerBase(AsArraySerializerBase<?> src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer)
  {
    this(src, property, vts, elementSerializer, _unwrapSingle);
  }
  



  @Deprecated
  public final AsArraySerializerBase<T> withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer)
  {
    return withResolved(property, vts, elementSerializer, _unwrapSingle);
  }
  










  public abstract AsArraySerializerBase<T> withResolved(BeanProperty paramBeanProperty, TypeSerializer paramTypeSerializer, JsonSerializer<?> paramJsonSerializer, Boolean paramBoolean);
  









  public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
    throws JsonMappingException
  {
    TypeSerializer typeSer = _valueTypeSerializer;
    if (typeSer != null) {
      typeSer = typeSer.forProperty(property);
    }
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
    
    if ((ser != _elementSerializer) || (property != _property) || (_valueTypeSerializer != typeSer) || (_unwrapSingle != unwrapSingle))
    {


      return withResolved(property, typeSer, ser, unwrapSingle);
    }
    return this;
  }
  






  public JavaType getContentType()
  {
    return _elementType;
  }
  
  public JsonSerializer<?> getContentSerializer()
  {
    return _elementSerializer;
  }
  









  public void serialize(T value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    if ((provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) && 
      (hasSingleElement(value))) {
      serializeContents(value, gen, provider);
      return;
    }
    gen.writeStartArray(value);
    serializeContents(value, gen, provider);
    gen.writeEndArray();
  }
  

  public void serializeWithType(T value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
      .typeId(value, JsonToken.START_ARRAY));
    
    g.setCurrentValue(value);
    serializeContents(value, g, provider);
    typeSer.writeTypeSuffix(g, typeIdDef);
  }
  

  protected abstract void serializeContents(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
    throws IOException;
  

  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    throws JsonMappingException
  {
    ObjectNode o = createSchemaNode("array", true);
    if (_elementSerializer != null) {
      JsonNode schemaNode = null;
      if ((_elementSerializer instanceof SchemaAware)) {
        schemaNode = ((SchemaAware)_elementSerializer).getSchema(provider, null);
      }
      if (schemaNode == null) {
        schemaNode = JsonSchema.getDefaultSchemaNode();
      }
      o.set("items", schemaNode);
    }
    return o;
  }
  

  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    JsonSerializer<?> valueSer = _elementSerializer;
    if (valueSer == null)
    {

      if (_elementType != null) {
        valueSer = visitor.getProvider().findValueSerializer(_elementType, _property);
      }
    }
    visitArrayFormat(visitor, typeHint, valueSer, _elementType);
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
