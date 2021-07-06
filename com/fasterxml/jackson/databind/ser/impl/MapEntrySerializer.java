package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;




@JacksonStdImpl
public class MapEntrySerializer
  extends ContainerSerializer<Map.Entry<?, ?>>
  implements ContextualSerializer
{
  public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
  





  protected final BeanProperty _property;
  





  protected final boolean _valueTypeIsStatic;
  





  protected final JavaType _entryType;
  




  protected final JavaType _keyType;
  




  protected final JavaType _valueType;
  




  protected JsonSerializer<Object> _keySerializer;
  




  protected JsonSerializer<Object> _valueSerializer;
  




  protected final TypeSerializer _valueTypeSerializer;
  




  protected PropertySerializerMap _dynamicValueSerializers;
  




  protected final Object _suppressableValue;
  




  protected final boolean _suppressNulls;
  





  public MapEntrySerializer(JavaType type, JavaType keyType, JavaType valueType, boolean staticTyping, TypeSerializer vts, BeanProperty property)
  {
    super(type);
    _entryType = type;
    _keyType = keyType;
    _valueType = valueType;
    _valueTypeIsStatic = staticTyping;
    _valueTypeSerializer = vts;
    _property = property;
    _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
    _suppressableValue = null;
    _suppressNulls = false;
  }
  


  @Deprecated
  protected MapEntrySerializer(MapEntrySerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> keySer, JsonSerializer<?> valueSer)
  {
    this(src, property, vts, keySer, valueSer, _suppressableValue, _suppressNulls);
  }
  





  protected MapEntrySerializer(MapEntrySerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> keySer, JsonSerializer<?> valueSer, Object suppressableValue, boolean suppressNulls)
  {
    super(Map.class, false);
    _entryType = _entryType;
    _keyType = _keyType;
    _valueType = _valueType;
    _valueTypeIsStatic = _valueTypeIsStatic;
    _valueTypeSerializer = _valueTypeSerializer;
    _keySerializer = keySer;
    _valueSerializer = valueSer;
    
    _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
    _property = _property;
    _suppressableValue = suppressableValue;
    _suppressNulls = suppressNulls;
  }
  
  public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts)
  {
    return new MapEntrySerializer(this, _property, vts, _keySerializer, _valueSerializer, _suppressableValue, _suppressNulls);
  }
  





  public MapEntrySerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Object suppressableValue, boolean suppressNulls)
  {
    return new MapEntrySerializer(this, property, _valueTypeSerializer, keySerializer, valueSerializer, suppressableValue, suppressNulls);
  }
  




  public MapEntrySerializer withContentInclusion(Object suppressableValue, boolean suppressNulls)
  {
    if ((_suppressableValue == suppressableValue) && (_suppressNulls == suppressNulls))
    {
      return this;
    }
    return new MapEntrySerializer(this, _property, _valueTypeSerializer, _keySerializer, _valueSerializer, suppressableValue, suppressNulls);
  }
  


  public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<?> ser = null;
    JsonSerializer<?> keySer = null;
    AnnotationIntrospector intr = provider.getAnnotationIntrospector();
    AnnotatedMember propertyAcc = property == null ? null : property.getMember();
    

    if ((propertyAcc != null) && (intr != null)) {
      Object serDef = intr.findKeySerializer(propertyAcc);
      if (serDef != null) {
        keySer = provider.serializerInstance(propertyAcc, serDef);
      }
      serDef = intr.findContentSerializer(propertyAcc);
      if (serDef != null) {
        ser = provider.serializerInstance(propertyAcc, serDef);
      }
    }
    if (ser == null) {
      ser = _valueSerializer;
    }
    
    ser = findContextualConvertingSerializer(provider, property, ser);
    if (ser == null)
    {


      if ((_valueTypeIsStatic) && (!_valueType.isJavaLangObject())) {
        ser = provider.findValueSerializer(_valueType, property);
      }
    }
    if (keySer == null) {
      keySer = _keySerializer;
    }
    if (keySer == null) {
      keySer = provider.findKeySerializer(_keyType, property);
    } else {
      keySer = provider.handleSecondaryContextualization(keySer, property);
    }
    
    Object valueToSuppress = _suppressableValue;
    boolean suppressNulls = _suppressNulls;
    if (property != null) {
      JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), null);
      if (inclV != null) {
        JsonInclude.Include incl = inclV.getContentInclusion();
        if (incl != JsonInclude.Include.USE_DEFAULTS) {
          switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonInclude$Include[incl.ordinal()]) {
          case 1: 
            valueToSuppress = BeanUtil.getDefaultValue(_valueType);
            suppressNulls = true;
            if ((valueToSuppress != null) && 
              (valueToSuppress.getClass().isArray())) {
              valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
            }
            
            break;
          case 2: 
            suppressNulls = true;
            valueToSuppress = _valueType.isReferenceType() ? MARKER_FOR_EMPTY : null;
            break;
          case 3: 
            suppressNulls = true;
            valueToSuppress = MARKER_FOR_EMPTY;
            break;
          case 4: 
            valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
            if (valueToSuppress == null) {
              suppressNulls = true;
            } else {
              suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
            }
            break;
          case 5: 
            valueToSuppress = null;
            suppressNulls = true;
            break;
          case 6: 
          default: 
            valueToSuppress = null;
            

            suppressNulls = false;
          }
          
        }
      }
    }
    
    MapEntrySerializer mser = withResolved(property, keySer, ser, valueToSuppress, suppressNulls);
    

    return mser;
  }
  






  public JavaType getContentType()
  {
    return _valueType;
  }
  
  public JsonSerializer<?> getContentSerializer()
  {
    return _valueSerializer;
  }
  
  public boolean hasSingleElement(Map.Entry<?, ?> value)
  {
    return true;
  }
  

  public boolean isEmpty(SerializerProvider prov, Map.Entry<?, ?> entry)
  {
    Object value = entry.getValue();
    if (value == null) {
      return _suppressNulls;
    }
    if (_suppressableValue == null) {
      return false;
    }
    JsonSerializer<Object> valueSer = _valueSerializer;
    if (valueSer == null)
    {

      Class<?> cc = value.getClass();
      valueSer = _dynamicValueSerializers.serializerFor(cc);
      if (valueSer == null) {
        try {
          valueSer = _findAndAddDynamic(_dynamicValueSerializers, cc, prov);
        } catch (JsonMappingException e) {
          return false;
        }
      }
    }
    if (_suppressableValue == MARKER_FOR_EMPTY) {
      return valueSer.isEmpty(prov, value);
    }
    return _suppressableValue.equals(value);
  }
  







  public void serialize(Map.Entry<?, ?> value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    gen.writeStartObject(value);
    serializeDynamic(value, gen, provider);
    gen.writeEndObject();
  }
  


  public void serializeWithType(Map.Entry<?, ?> value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    g.setCurrentValue(value);
    WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
      .typeId(value, JsonToken.START_OBJECT));
    serializeDynamic(value, g, provider);
    typeSer.writeTypeSuffix(g, typeIdDef);
  }
  

  protected void serializeDynamic(Map.Entry<?, ?> value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    TypeSerializer vts = _valueTypeSerializer;
    Object keyElem = value.getKey();
    JsonSerializer<Object> keySerializer;
    JsonSerializer<Object> keySerializer;
    if (keyElem == null) {
      keySerializer = provider.findNullKeySerializer(_keyType, _property);
    } else {
      keySerializer = _keySerializer;
    }
    
    Object valueElem = value.getValue();
    JsonSerializer<Object> valueSer;
    JsonSerializer<Object> valueSer;
    if (valueElem == null) {
      if (_suppressNulls) {
        return;
      }
      valueSer = provider.getDefaultNullValueSerializer();
    } else {
      valueSer = _valueSerializer;
      if (valueSer == null) {
        Class<?> cc = valueElem.getClass();
        valueSer = _dynamicValueSerializers.serializerFor(cc);
        if (valueSer == null) {
          if (_valueType.hasGenericTypes()) {
            valueSer = _findAndAddDynamic(_dynamicValueSerializers, provider
              .constructSpecializedType(_valueType, cc), provider);
          } else {
            valueSer = _findAndAddDynamic(_dynamicValueSerializers, cc, provider);
          }
        }
      }
      
      if (_suppressableValue != null) {
        if ((_suppressableValue == MARKER_FOR_EMPTY) && 
          (valueSer.isEmpty(provider, valueElem))) {
          return;
        }
        if (_suppressableValue.equals(valueElem)) {
          return;
        }
      }
    }
    keySerializer.serialize(keyElem, gen, provider);
    try {
      if (vts == null) {
        valueSer.serialize(valueElem, gen, provider);
      } else {
        valueSer.serializeWithType(valueElem, gen, provider, vts);
      }
    } catch (Exception e) {
      String keyDesc = "" + keyElem;
      wrapAndThrow(provider, e, value, keyDesc);
    }
  }
  






  protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
    throws JsonMappingException
  {
    PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
    if (map != map) {
      _dynamicValueSerializers = map;
    }
    return serializer;
  }
  
  protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider)
    throws JsonMappingException
  {
    PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
    if (map != map) {
      _dynamicValueSerializers = map;
    }
    return serializer;
  }
}
