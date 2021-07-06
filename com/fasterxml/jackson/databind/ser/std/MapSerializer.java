package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonGenerator;
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
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@com.fasterxml.jackson.databind.annotation.JacksonStdImpl
public class MapSerializer extends ContainerSerializer<Map<?, ?>> implements ContextualSerializer
{
  private static final long serialVersionUID = 1L;
  protected static final JavaType UNSPECIFIED_TYPE = ;
  



  public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
  







  protected final BeanProperty _property;
  







  protected final boolean _valueTypeIsStatic;
  







  protected final JavaType _keyType;
  







  protected final JavaType _valueType;
  







  protected JsonSerializer<Object> _keySerializer;
  







  protected JsonSerializer<Object> _valueSerializer;
  







  protected final TypeSerializer _valueTypeSerializer;
  







  protected PropertySerializerMap _dynamicValueSerializers;
  






  protected final Set<String> _ignoredEntries;
  






  protected final Object _filterId;
  






  protected final Object _suppressableValue;
  






  protected final boolean _suppressNulls;
  






  protected final boolean _sortKeys;
  







  protected MapSerializer(Set<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer)
  {
    super(Map.class, false);
    _ignoredEntries = ((ignoredEntries == null) || (ignoredEntries.isEmpty()) ? null : ignoredEntries);
    
    _keyType = keyType;
    _valueType = valueType;
    _valueTypeIsStatic = valueTypeIsStatic;
    _valueTypeSerializer = vts;
    _keySerializer = keySerializer;
    _valueSerializer = valueSerializer;
    _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
    _property = null;
    _filterId = null;
    _sortKeys = false;
    _suppressableValue = null;
    _suppressNulls = false;
  }
  



  protected MapSerializer(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries)
  {
    super(Map.class, false);
    _ignoredEntries = ((ignoredEntries == null) || (ignoredEntries.isEmpty()) ? null : ignoredEntries);
    
    _keyType = _keyType;
    _valueType = _valueType;
    _valueTypeIsStatic = _valueTypeIsStatic;
    _valueTypeSerializer = _valueTypeSerializer;
    _keySerializer = keySerializer;
    _valueSerializer = valueSerializer;
    
    _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
    _property = property;
    _filterId = _filterId;
    _sortKeys = _sortKeys;
    _suppressableValue = _suppressableValue;
    _suppressNulls = _suppressNulls;
  }
  




  protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue, boolean suppressNulls)
  {
    super(Map.class, false);
    _ignoredEntries = _ignoredEntries;
    _keyType = _keyType;
    _valueType = _valueType;
    _valueTypeIsStatic = _valueTypeIsStatic;
    _valueTypeSerializer = vts;
    _keySerializer = _keySerializer;
    _valueSerializer = _valueSerializer;
    

    _dynamicValueSerializers = _dynamicValueSerializers;
    _property = _property;
    _filterId = _filterId;
    _sortKeys = _sortKeys;
    _suppressableValue = suppressableValue;
    _suppressNulls = suppressNulls;
  }
  
  protected MapSerializer(MapSerializer src, Object filterId, boolean sortKeys)
  {
    super(Map.class, false);
    _ignoredEntries = _ignoredEntries;
    _keyType = _keyType;
    _valueType = _valueType;
    _valueTypeIsStatic = _valueTypeIsStatic;
    _valueTypeSerializer = _valueTypeSerializer;
    _keySerializer = _keySerializer;
    _valueSerializer = _valueSerializer;
    
    _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
    _property = _property;
    _filterId = filterId;
    _sortKeys = sortKeys;
    _suppressableValue = _suppressableValue;
    _suppressNulls = _suppressNulls;
  }
  
  public MapSerializer _withValueTypeSerializer(TypeSerializer vts)
  {
    if (_valueTypeSerializer == vts) {
      return this;
    }
    _ensureOverride("_withValueTypeSerializer");
    return new MapSerializer(this, vts, _suppressableValue, _suppressNulls);
  }
  





  public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, boolean sortKeys)
  {
    _ensureOverride("withResolved");
    MapSerializer ser = new MapSerializer(this, property, keySerializer, valueSerializer, ignored);
    if (sortKeys != _sortKeys) {
      ser = new MapSerializer(ser, _filterId, sortKeys);
    }
    return ser;
  }
  
  public MapSerializer withFilterId(Object filterId)
  {
    if (_filterId == filterId) {
      return this;
    }
    _ensureOverride("withFilterId");
    return new MapSerializer(this, filterId, _sortKeys);
  }
  





  public MapSerializer withContentInclusion(Object suppressableValue, boolean suppressNulls)
  {
    if ((suppressableValue == _suppressableValue) && (suppressNulls == _suppressNulls)) {
      return this;
    }
    _ensureOverride("withContentInclusion");
    return new MapSerializer(this, _valueTypeSerializer, suppressableValue, suppressNulls);
  }
  


  public static MapSerializer construct(Set<String> ignoredEntries, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId)
  {
    JavaType keyType;
    
    JavaType keyType;
    
    JavaType valueType;
    
    if (mapType == null) { JavaType valueType;
      keyType = valueType = UNSPECIFIED_TYPE;
    } else {
      keyType = mapType.getKeyType();
      valueType = mapType.getContentType();
    }
    
    if (!staticValueType) {
      staticValueType = (valueType != null) && (valueType.isFinal());

    }
    else if (valueType.getRawClass() == Object.class) {
      staticValueType = false;
    }
    
    MapSerializer ser = new MapSerializer(ignoredEntries, keyType, valueType, staticValueType, vts, keySerializer, valueSerializer);
    
    if (filterId != null) {
      ser = ser.withFilterId(filterId);
    }
    return ser;
  }
  


  protected void _ensureOverride(String method)
  {
    com.fasterxml.jackson.databind.util.ClassUtil.verifyMustOverride(MapSerializer.class, this, method);
  }
  


  @Deprecated
  protected void _ensureOverride()
  {
    _ensureOverride("N/A");
  }
  











  @Deprecated
  protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue)
  {
    this(src, vts, suppressableValue, false);
  }
  


  @Deprecated
  public MapSerializer withContentInclusion(Object suppressableValue)
  {
    return new MapSerializer(this, _valueTypeSerializer, suppressableValue, _suppressNulls);
  }
  








  @Deprecated
  public static MapSerializer construct(String[] ignoredList, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId)
  {
    Set<String> ignoredEntries = ArrayBuilders.arrayToSet(ignoredList);
    return construct(ignoredEntries, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
  }
  









  public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<?> ser = null;
    JsonSerializer<?> keySer = null;
    AnnotationIntrospector intr = provider.getAnnotationIntrospector();
    AnnotatedMember propertyAcc = property == null ? null : property.getMember();
    

    if (_neitherNull(propertyAcc, intr)) {
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
    Set<String> ignored = _ignoredEntries;
    boolean sortKeys = false;
    if (_neitherNull(propertyAcc, intr)) {
      JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(propertyAcc);
      if (ignorals != null) {
        Set<String> newIgnored = ignorals.findIgnoredForSerialization();
        if (_nonEmpty(newIgnored)) {
          ignored = ignored == null ? new HashSet() : new HashSet(ignored);
          for (String str : newIgnored) {
            ignored.add(str);
          }
        }
      }
      Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
      sortKeys = Boolean.TRUE.equals(b);
    }
    JsonFormat.Value format = findFormatOverrides(provider, property, Map.class);
    if (format != null) {
      Boolean B = format.getFeature(JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES);
      if (B != null) {
        sortKeys = B.booleanValue();
      }
    }
    MapSerializer mser = withResolved(property, keySer, ser, ignored, sortKeys);
    

    if (property != null) {
      AnnotatedMember m = property.getMember();
      if (m != null) {
        Object filterId = intr.findFilterId(m);
        if (filterId != null) {
          mser = mser.withFilterId(filterId);
        }
      }
      JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), null);
      if (inclV != null) {
        JsonInclude.Include incl = inclV.getContentInclusion();
        
        if (incl != JsonInclude.Include.USE_DEFAULTS) { Object valueToSuppress;
          Object valueToSuppress;
          boolean suppressNulls;
          boolean suppressNulls; Object valueToSuppress; boolean suppressNulls; switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonInclude$Include[incl.ordinal()]) {
          case 1: 
            Object valueToSuppress = com.fasterxml.jackson.databind.util.BeanUtil.getDefaultValue(_valueType);
            boolean suppressNulls = true;
            if ((valueToSuppress != null) && 
              (valueToSuppress.getClass().isArray())) {
              valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
            }
            
            break;
          case 2: 
            boolean suppressNulls = true;
            valueToSuppress = _valueType.isReferenceType() ? MARKER_FOR_EMPTY : null;
            break;
          case 3: 
            boolean suppressNulls = true;
            valueToSuppress = MARKER_FOR_EMPTY;
            break;
          case 4: 
            Object valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
            boolean suppressNulls; if (valueToSuppress == null) {
              suppressNulls = true;
            } else {
              suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
            }
            break;
          case 5: 
            Object valueToSuppress = null;
            suppressNulls = true;
            break;
          case 6: 
          default: 
            valueToSuppress = null;
            

            suppressNulls = false;
          }
          
          mser = mser.withContentInclusion(valueToSuppress, suppressNulls);
        }
      }
    }
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
  

  public boolean isEmpty(SerializerProvider prov, Map<?, ?> value)
  {
    if (value.isEmpty()) {
      return true;
    }
    


    Object supp = _suppressableValue;
    if ((supp == null) && (!_suppressNulls)) {
      return false;
    }
    JsonSerializer<Object> valueSer = _valueSerializer;
    boolean checkEmpty = MARKER_FOR_EMPTY == supp;
    if (valueSer != null) {
      for (Object elemValue : value.values()) {
        if (elemValue == null) {
          if (!_suppressNulls)
          {

            return false;
          }
        } else if (checkEmpty) {
          if (!valueSer.isEmpty(prov, elemValue)) {
            return false;
          }
        } else if ((supp == null) || (!supp.equals(value))) {
          return false;
        }
      }
      return true;
    }
    
    for (Object elemValue : value.values()) {
      if (elemValue == null) {
        if (!_suppressNulls)
        {

          return false; }
      } else {
        try {
          valueSer = _findSerializer(prov, elemValue);
        }
        catch (JsonMappingException e) {
          return false;
        }
        if (checkEmpty) {
          if (!valueSer.isEmpty(prov, elemValue)) {
            return false;
          }
        } else if ((supp == null) || (!supp.equals(value)))
          return false;
      }
    }
    return true;
  }
  
  public boolean hasSingleElement(Map<?, ?> value)
  {
    return value.size() == 1;
  }
  















  public JsonSerializer<?> getKeySerializer()
  {
    return _keySerializer;
  }
  







  public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    gen.writeStartObject(value);
    if (!value.isEmpty()) {
      if ((_sortKeys) || (provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS))) {
        value = _orderEntries(value, gen, provider);
      }
      PropertyFilter pf;
      if ((_filterId != null) && ((pf = findPropertyFilter(provider, _filterId, value)) != null)) {
        serializeFilteredFields(value, gen, provider, pf, _suppressableValue);
      } else if ((_suppressableValue != null) || (_suppressNulls)) {
        serializeOptionalFields(value, gen, provider, _suppressableValue);
      } else if (_valueSerializer != null) {
        serializeFieldsUsing(value, gen, provider, _valueSerializer);
      } else {
        serializeFields(value, gen, provider);
      }
    }
    gen.writeEndObject();
  }
  



  public void serializeWithType(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    gen.setCurrentValue(value);
    com.fasterxml.jackson.core.type.WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer
      .typeId(value, com.fasterxml.jackson.core.JsonToken.START_OBJECT));
    if (!value.isEmpty()) {
      if ((_sortKeys) || (provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS))) {
        value = _orderEntries(value, gen, provider);
      }
      PropertyFilter pf;
      if ((_filterId != null) && ((pf = findPropertyFilter(provider, _filterId, value)) != null)) {
        serializeFilteredFields(value, gen, provider, pf, _suppressableValue);
      } else if ((_suppressableValue != null) || (_suppressNulls)) {
        serializeOptionalFields(value, gen, provider, _suppressableValue);
      } else if (_valueSerializer != null) {
        serializeFieldsUsing(value, gen, provider, _valueSerializer);
      } else {
        serializeFields(value, gen, provider);
      }
    }
    typeSer.writeTypeSuffix(gen, typeIdDef);
  }
  












  public void serializeFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    if (_valueTypeSerializer != null) {
      serializeTypedFields(value, gen, provider, null);
      return;
    }
    JsonSerializer<Object> keySerializer = _keySerializer;
    Set<String> ignored = _ignoredEntries;
    Object keyElem = null;
    try
    {
      for (Map.Entry<?, ?> entry : value.entrySet()) {
        Object valueElem = entry.getValue();
        
        keyElem = entry.getKey();
        if (keyElem == null) {
          provider.findNullKeySerializer(_keyType, _property).serialize(null, gen, provider);
        }
        else {
          if ((ignored != null) && (ignored.contains(keyElem))) {
            continue;
          }
          keySerializer.serialize(keyElem, gen, provider);
        }
        
        if (valueElem == null) {
          provider.defaultSerializeNull(gen);
        }
        else {
          JsonSerializer<Object> serializer = _valueSerializer;
          if (serializer == null) {
            serializer = _findSerializer(provider, valueElem);
          }
          serializer.serialize(valueElem, gen, provider);
        }
      }
    } catch (Exception e) { wrapAndThrow(provider, e, value, String.valueOf(keyElem));
    }
  }
  





  public void serializeOptionalFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, Object suppressableValue)
    throws IOException
  {
    if (_valueTypeSerializer != null) {
      serializeTypedFields(value, gen, provider, suppressableValue);
      return;
    }
    Set<String> ignored = _ignoredEntries;
    boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;
    
    for (Map.Entry<?, ?> entry : value.entrySet())
    {
      Object keyElem = entry.getKey();
      JsonSerializer<Object> keySerializer;
      JsonSerializer<Object> keySerializer; if (keyElem == null) {
        keySerializer = provider.findNullKeySerializer(_keyType, _property);
      } else {
        if ((ignored != null) && (ignored.contains(keyElem))) continue;
        keySerializer = _keySerializer;
      }
      

      Object valueElem = entry.getValue();
      JsonSerializer<Object> valueSer;
      if (valueElem == null) {
        if (_suppressNulls) {
          continue;
        }
        JsonSerializer<Object> valueSer = provider.getDefaultNullValueSerializer();
      } else {
        valueSer = _valueSerializer;
        if (valueSer == null) {
          valueSer = _findSerializer(provider, valueElem);
        }
        
        if (checkEmpty ? 
          valueSer.isEmpty(provider, valueElem) : 
          

          (suppressableValue != null) && 
          (suppressableValue.equals(valueElem))) {
          continue;
        }
      }
      
      try
      {
        keySerializer.serialize(keyElem, gen, provider);
        valueSer.serialize(valueElem, gen, provider);
      } catch (Exception e) {
        wrapAndThrow(provider, e, value, String.valueOf(keyElem));
      }
    }
  }
  






  public void serializeFieldsUsing(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, JsonSerializer<Object> ser)
    throws IOException
  {
    JsonSerializer<Object> keySerializer = _keySerializer;
    Set<String> ignored = _ignoredEntries;
    TypeSerializer typeSer = _valueTypeSerializer;
    
    for (Map.Entry<?, ?> entry : value.entrySet()) {
      Object keyElem = entry.getKey();
      if ((ignored == null) || (!ignored.contains(keyElem)))
      {
        if (keyElem == null) {
          provider.findNullKeySerializer(_keyType, _property).serialize(null, gen, provider);
        } else {
          keySerializer.serialize(keyElem, gen, provider);
        }
        Object valueElem = entry.getValue();
        if (valueElem == null) {
          provider.defaultSerializeNull(gen);
        } else {
          try {
            if (typeSer == null) {
              ser.serialize(valueElem, gen, provider);
            } else {
              ser.serializeWithType(valueElem, gen, provider, typeSer);
            }
          } catch (Exception e) {
            wrapAndThrow(provider, e, value, String.valueOf(keyElem));
          }
        }
      }
    }
  }
  







  public void serializeFilteredFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, PropertyFilter filter, Object suppressableValue)
    throws IOException
  {
    Set<String> ignored = _ignoredEntries;
    MapProperty prop = new MapProperty(_valueTypeSerializer, _property);
    boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;
    
    for (Map.Entry<?, ?> entry : value.entrySet())
    {
      Object keyElem = entry.getKey();
      if ((ignored == null) || (!ignored.contains(keyElem))) {
        JsonSerializer<Object> keySerializer;
        JsonSerializer<Object> keySerializer;
        if (keyElem == null) {
          keySerializer = provider.findNullKeySerializer(_keyType, _property);
        } else {
          keySerializer = _keySerializer;
        }
        
        Object valueElem = entry.getValue();
        
        JsonSerializer<Object> valueSer;
        
        if (valueElem == null) {
          if (_suppressNulls) {
            continue;
          }
          JsonSerializer<Object> valueSer = provider.getDefaultNullValueSerializer();
        } else {
          valueSer = _valueSerializer;
          if (valueSer == null) {
            valueSer = _findSerializer(provider, valueElem);
          }
          
          if (checkEmpty ? 
            valueSer.isEmpty(provider, valueElem) : 
            

            (suppressableValue != null) && 
            (suppressableValue.equals(valueElem))) {
            continue;
          }
        }
        

        prop.reset(keyElem, valueElem, keySerializer, valueSer);
        try {
          filter.serializeAsField(value, gen, provider, prop);
        } catch (Exception e) {
          wrapAndThrow(provider, e, value, String.valueOf(keyElem));
        }
      }
    }
  }
  



  public void serializeTypedFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, Object suppressableValue)
    throws IOException
  {
    Set<String> ignored = _ignoredEntries;
    boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;
    
    for (Map.Entry<?, ?> entry : value.entrySet()) {
      Object keyElem = entry.getKey();
      JsonSerializer<Object> keySerializer;
      JsonSerializer<Object> keySerializer; if (keyElem == null) {
        keySerializer = provider.findNullKeySerializer(_keyType, _property);
      }
      else {
        if ((ignored != null) && (ignored.contains(keyElem))) continue;
        keySerializer = _keySerializer;
      }
      Object valueElem = entry.getValue();
      
      JsonSerializer<Object> valueSer;
      
      if (valueElem == null) {
        if (_suppressNulls) {
          continue;
        }
        JsonSerializer<Object> valueSer = provider.getDefaultNullValueSerializer();
      } else {
        valueSer = _valueSerializer;
        if (valueSer == null) {
          valueSer = _findSerializer(provider, valueElem);
        }
        
        if (checkEmpty ? 
          valueSer.isEmpty(provider, valueElem) : 
          

          (suppressableValue != null) && 
          (suppressableValue.equals(valueElem))) {
          continue;
        }
      }
      
      keySerializer.serialize(keyElem, gen, provider);
      try {
        valueSer.serializeWithType(valueElem, gen, provider, _valueTypeSerializer);
      } catch (Exception e) {
        wrapAndThrow(provider, e, value, String.valueOf(keyElem));
      }
    }
  }
  










  public void serializeFilteredAnyProperties(SerializerProvider provider, JsonGenerator gen, Object bean, Map<?, ?> value, PropertyFilter filter, Object suppressableValue)
    throws IOException
  {
    Set<String> ignored = _ignoredEntries;
    MapProperty prop = new MapProperty(_valueTypeSerializer, _property);
    boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;
    
    for (Map.Entry<?, ?> entry : value.entrySet())
    {
      Object keyElem = entry.getKey();
      if ((ignored == null) || (!ignored.contains(keyElem))) {
        JsonSerializer<Object> keySerializer;
        JsonSerializer<Object> keySerializer;
        if (keyElem == null) {
          keySerializer = provider.findNullKeySerializer(_keyType, _property);
        } else {
          keySerializer = _keySerializer;
        }
        
        Object valueElem = entry.getValue();
        
        JsonSerializer<Object> valueSer;
        
        if (valueElem == null) {
          if (_suppressNulls) {
            continue;
          }
          JsonSerializer<Object> valueSer = provider.getDefaultNullValueSerializer();
        } else {
          valueSer = _valueSerializer;
          if (valueSer == null) {
            valueSer = _findSerializer(provider, valueElem);
          }
          
          if (checkEmpty ? 
            valueSer.isEmpty(provider, valueElem) : 
            

            (suppressableValue != null) && 
            (suppressableValue.equals(valueElem))) {
            continue;
          }
        }
        

        prop.reset(keyElem, valueElem, keySerializer, valueSer);
        try {
          filter.serializeAsField(bean, gen, provider, prop);
        } catch (Exception e) {
          wrapAndThrow(provider, e, value, String.valueOf(keyElem));
        }
      }
    }
  }
  








  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
  {
    return createSchemaNode("object", true);
  }
  

  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    JsonMapFormatVisitor v2 = visitor.expectMapFormat(typeHint);
    if (v2 != null) {
      v2.keyFormat(_keySerializer, _keyType);
      JsonSerializer<?> valueSer = _valueSerializer;
      if (valueSer == null) {
        valueSer = _findAndAddDynamic(_dynamicValueSerializers, _valueType, visitor
          .getProvider());
      }
      v2.valueFormat(valueSer, _valueType);
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
  

  protected Map<?, ?> _orderEntries(Map<?, ?> input, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    if ((input instanceof SortedMap)) {
      return input;
    }
    



    if (_hasNullKey(input)) {
      TreeMap<Object, Object> result = new TreeMap();
      for (Map.Entry<?, ?> entry : input.entrySet()) {
        Object key = entry.getKey();
        if (key == null) {
          _writeNullKeyedEntry(gen, provider, entry.getValue());
        }
        else
          result.put(key, entry.getValue());
      }
      return result;
    }
    return new TreeMap(input);
  }
  











  protected boolean _hasNullKey(Map<?, ?> input)
  {
    return ((input instanceof java.util.HashMap)) && (input.containsKey(null));
  }
  
  protected void _writeNullKeyedEntry(JsonGenerator gen, SerializerProvider provider, Object value)
    throws IOException
  {
    JsonSerializer<Object> keySerializer = provider.findNullKeySerializer(_keyType, _property);
    JsonSerializer<Object> valueSer;
    JsonSerializer<Object> valueSer; if (value == null) {
      if (_suppressNulls) {
        return;
      }
      valueSer = provider.getDefaultNullValueSerializer();
    } else {
      valueSer = _valueSerializer;
      if (valueSer == null) {
        valueSer = _findSerializer(provider, value);
      }
      if (_suppressableValue == MARKER_FOR_EMPTY) {
        if (!valueSer.isEmpty(provider, value)) {}

      }
      else if ((_suppressableValue != null) && 
        (_suppressableValue.equals(value))) {
        return;
      }
    }
    try
    {
      keySerializer.serialize(null, gen, provider);
      valueSer.serialize(value, gen, provider);
    } catch (Exception e) {
      wrapAndThrow(provider, e, value, "");
    }
  }
  
  private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Object value)
    throws JsonMappingException
  {
    Class<?> cc = value.getClass();
    JsonSerializer<Object> valueSer = _dynamicValueSerializers.serializerFor(cc);
    if (valueSer != null) {
      return valueSer;
    }
    if (_valueType.hasGenericTypes()) {
      return _findAndAddDynamic(_dynamicValueSerializers, provider
        .constructSpecializedType(_valueType, cc), provider);
    }
    return _findAndAddDynamic(_dynamicValueSerializers, cc, provider);
  }
}
