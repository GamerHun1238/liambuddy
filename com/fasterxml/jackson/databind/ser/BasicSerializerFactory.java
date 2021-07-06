package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.fasterxml.jackson.databind.ser.std.AtomicReferenceSerializer;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BasicSerializerFactory extends SerializerFactory implements java.io.Serializable
{
  protected static final HashMap<String, JsonSerializer<?>> _concrete;
  protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy;
  protected final SerializerFactoryConfig _factoryConfig;
  
  static
  {
    HashMap<String, Class<? extends JsonSerializer<?>>> concLazy = new HashMap();
    
    HashMap<String, JsonSerializer<?>> concrete = new HashMap();
    





    concrete.put(String.class.getName(), new com.fasterxml.jackson.databind.ser.std.StringSerializer());
    ToStringSerializer sls = ToStringSerializer.instance;
    concrete.put(StringBuffer.class.getName(), sls);
    concrete.put(StringBuilder.class.getName(), sls);
    concrete.put(Character.class.getName(), sls);
    concrete.put(Character.TYPE.getName(), sls);
    

    com.fasterxml.jackson.databind.ser.std.NumberSerializers.addAll(concrete);
    concrete.put(Boolean.TYPE.getName(), new BooleanSerializer(true));
    concrete.put(Boolean.class.getName(), new BooleanSerializer(false));
    

    concrete.put(java.math.BigInteger.class.getName(), new NumberSerializer(java.math.BigInteger.class));
    concrete.put(BigDecimal.class.getName(), new NumberSerializer(BigDecimal.class));
    


    concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
    concrete.put(Date.class.getName(), DateSerializer.instance);
    

    for (Map.Entry<Class<?>, Object> en : com.fasterxml.jackson.databind.ser.std.StdJdkSerializers.all()) {
      Object value = en.getValue();
      if ((value instanceof JsonSerializer)) {
        concrete.put(((Class)en.getKey()).getName(), (JsonSerializer)value);
      }
      else {
        Class<? extends JsonSerializer<?>> cls = (Class)value;
        concLazy.put(((Class)en.getKey()).getName(), cls);
      }
    }
    


    concLazy.put(com.fasterxml.jackson.databind.util.TokenBuffer.class.getName(), com.fasterxml.jackson.databind.ser.std.TokenBufferSerializer.class);
    
    _concrete = concrete;
    _concreteLazy = concLazy;
  }
  






















  protected BasicSerializerFactory(SerializerFactoryConfig config)
  {
    _factoryConfig = (config == null ? new SerializerFactoryConfig() : config);
  }
  






  public SerializerFactoryConfig getFactoryConfig()
  {
    return _factoryConfig;
  }
  
















  public final SerializerFactory withAdditionalSerializers(Serializers additional)
  {
    return withConfig(_factoryConfig.withAdditionalSerializers(additional));
  }
  




  public final SerializerFactory withAdditionalKeySerializers(Serializers additional)
  {
    return withConfig(_factoryConfig.withAdditionalKeySerializers(additional));
  }
  




  public final SerializerFactory withSerializerModifier(BeanSerializerModifier modifier)
  {
    return withConfig(_factoryConfig.withSerializerModifier(modifier));
  }
  

















  public JsonSerializer<Object> createKeySerializer(SerializationConfig config, JavaType keyType, JsonSerializer<Object> defaultImpl)
  {
    BeanDescription beanDesc = config.introspectClassAnnotations(keyType.getRawClass());
    JsonSerializer<?> ser = null;
    
    if (_factoryConfig.hasKeySerializers())
    {
      for (Serializers serializers : _factoryConfig.keySerializers()) {
        ser = serializers.findSerializer(config, keyType, beanDesc);
        if (ser != null)
          break;
      }
    }
    Object am;
    if (ser == null) {
      ser = defaultImpl;
      if (ser == null) {
        ser = StdKeySerializers.getStdKeySerializer(config, keyType.getRawClass(), false);
        
        if (ser == null) {
          beanDesc = config.introspect(keyType);
          am = beanDesc.findJsonValueAccessor();
          if (am != null) {
            Class<?> rawType = ((AnnotatedMember)am).getRawType();
            JsonSerializer<?> delegate = StdKeySerializers.getStdKeySerializer(config, rawType, true);
            
            if (config.canOverrideAccessModifiers()) {
              ClassUtil.checkAndFixAccess(((AnnotatedMember)am).getMember(), config
                .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            ser = new JsonValueSerializer((AnnotatedMember)am, delegate);
          } else {
            ser = StdKeySerializers.getFallbackKeySerializer(config, keyType.getRawClass());
          }
        }
      }
    }
    

    if (_factoryConfig.hasSerializerModifiers()) {
      for (am = _factoryConfig.serializerModifiers().iterator(); ((Iterator)am).hasNext();) { BeanSerializerModifier mod = (BeanSerializerModifier)((Iterator)am).next();
        ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
      }
    }
    return ser;
  }
  







  public TypeSerializer createTypeSerializer(SerializationConfig config, JavaType baseType)
  {
    BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
    com.fasterxml.jackson.databind.introspect.AnnotatedClass ac = bean.getClassInfo();
    AnnotationIntrospector ai = config.getAnnotationIntrospector();
    TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
    


    java.util.Collection<com.fasterxml.jackson.databind.jsontype.NamedType> subtypes = null;
    if (b == null) {
      b = config.getDefaultTyper(baseType);
    } else {
      subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, ac);
    }
    if (b == null) {
      return null;
    }
    

    return b.buildTypeSerializer(config, baseType, subtypes);
  }
  




















  protected final JsonSerializer<?> findSerializerByLookup(JavaType type, SerializationConfig config, BeanDescription beanDesc, boolean staticTyping)
  {
    Class<?> raw = type.getRawClass();
    String clsName = raw.getName();
    JsonSerializer<?> ser = (JsonSerializer)_concrete.get(clsName);
    if (ser == null) {
      Class<? extends JsonSerializer<?>> serClass = (Class)_concreteLazy.get(clsName);
      if (serClass != null)
      {


        return (JsonSerializer)ClassUtil.createInstance(serClass, false);
      }
    }
    return ser;
  }
  

















  protected final JsonSerializer<?> findSerializerByAnnotations(SerializerProvider prov, JavaType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    Class<?> raw = type.getRawClass();
    
    if (com.fasterxml.jackson.databind.JsonSerializable.class.isAssignableFrom(raw)) {
      return com.fasterxml.jackson.databind.ser.std.SerializableSerializer.instance;
    }
    
    AnnotatedMember valueAccessor = beanDesc.findJsonValueAccessor();
    if (valueAccessor != null) {
      if (prov.canOverrideAccessModifiers()) {
        ClassUtil.checkAndFixAccess(valueAccessor.getMember(), prov
          .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      }
      JsonSerializer<Object> ser = findSerializerFromAnnotation(prov, valueAccessor);
      return new JsonValueSerializer(valueAccessor, ser);
    }
    
    return null;
  }
  









  protected final JsonSerializer<?> findSerializerByPrimaryType(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping)
    throws JsonMappingException
  {
    Class<?> raw = type.getRawClass();
    

    JsonSerializer<?> ser = findOptionalStdSerializer(prov, type, beanDesc, staticTyping);
    if (ser != null) {
      return ser;
    }
    
    if (Calendar.class.isAssignableFrom(raw)) {
      return CalendarSerializer.instance;
    }
    if (Date.class.isAssignableFrom(raw)) {
      return DateSerializer.instance;
    }
    if (Map.Entry.class.isAssignableFrom(raw))
    {
      JavaType mapEntryType = type.findSuperType(Map.Entry.class);
      

      JavaType kt = mapEntryType.containedTypeOrUnknown(0);
      JavaType vt = mapEntryType.containedTypeOrUnknown(1);
      return buildMapEntrySerializer(prov, type, beanDesc, staticTyping, kt, vt);
    }
    if (java.nio.ByteBuffer.class.isAssignableFrom(raw)) {
      return new com.fasterxml.jackson.databind.ser.std.ByteBufferSerializer();
    }
    if (java.net.InetAddress.class.isAssignableFrom(raw)) {
      return new com.fasterxml.jackson.databind.ser.std.InetAddressSerializer();
    }
    if (java.net.InetSocketAddress.class.isAssignableFrom(raw)) {
      return new com.fasterxml.jackson.databind.ser.std.InetSocketAddressSerializer();
    }
    if (java.util.TimeZone.class.isAssignableFrom(raw)) {
      return new com.fasterxml.jackson.databind.ser.std.TimeZoneSerializer();
    }
    if (java.nio.charset.Charset.class.isAssignableFrom(raw)) {
      return ToStringSerializer.instance;
    }
    if (Number.class.isAssignableFrom(raw))
    {
      JsonFormat.Value format = beanDesc.findExpectedFormat(null);
      if (format != null) {
        switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonFormat$Shape[format.getShape().ordinal()]) {
        case 1: 
          return ToStringSerializer.instance;
        case 2: 
        case 3: 
          return null;
        }
        
      }
      return NumberSerializer.instance;
    }
    if ((ClassUtil.isEnumType(raw)) && (raw != Enum.class)) {
      return buildEnumSerializer(prov.getConfig(), type, beanDesc);
    }
    return null;
  }
  






  protected JsonSerializer<?> findOptionalStdSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping)
    throws JsonMappingException
  {
    return OptionalHandlerFactory.instance.findSerializer(prov.getConfig(), type, beanDesc);
  }
  








  protected final JsonSerializer<?> findSerializerByAddonType(SerializationConfig config, JavaType javaType, BeanDescription beanDesc, boolean staticTyping)
    throws JsonMappingException
  {
    Class<?> rawType = javaType.getRawClass();
    
    if (Iterator.class.isAssignableFrom(rawType)) {
      JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterator.class);
      
      JavaType vt = (params == null) || (params.length != 1) ? TypeFactory.unknownType() : params[0];
      return buildIteratorSerializer(config, javaType, beanDesc, staticTyping, vt);
    }
    if (Iterable.class.isAssignableFrom(rawType)) {
      JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterable.class);
      
      JavaType vt = (params == null) || (params.length != 1) ? TypeFactory.unknownType() : params[0];
      return buildIterableSerializer(config, javaType, beanDesc, staticTyping, vt);
    }
    if (CharSequence.class.isAssignableFrom(rawType)) {
      return ToStringSerializer.instance;
    }
    return null;
  }
  









  protected JsonSerializer<Object> findSerializerFromAnnotation(SerializerProvider prov, Annotated a)
    throws JsonMappingException
  {
    Object serDef = prov.getAnnotationIntrospector().findSerializer(a);
    if (serDef == null) {
      return null;
    }
    JsonSerializer<Object> ser = prov.serializerInstance(a, serDef);
    
    return findConvertingSerializer(prov, a, ser);
  }
  







  protected JsonSerializer<?> findConvertingSerializer(SerializerProvider prov, Annotated a, JsonSerializer<?> ser)
    throws JsonMappingException
  {
    Converter<Object, Object> conv = findConverter(prov, a);
    if (conv == null) {
      return ser;
    }
    JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
    return new com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer(conv, delegateType, ser);
  }
  

  protected Converter<Object, Object> findConverter(SerializerProvider prov, Annotated a)
    throws JsonMappingException
  {
    Object convDef = prov.getAnnotationIntrospector().findSerializationConverter(a);
    if (convDef == null) {
      return null;
    }
    return prov.converterInstance(a, convDef);
  }
  










  protected JsonSerializer<?> buildContainerSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping)
    throws JsonMappingException
  {
    SerializationConfig config = prov.getConfig();
    




    if ((!staticTyping) && (type.useStaticType()) && (
      (!type.isContainerType()) || (!type.getContentType().isJavaLangObject()))) {
      staticTyping = true;
    }
    


    JavaType elementType = type.getContentType();
    TypeSerializer elementTypeSerializer = createTypeSerializer(config, elementType);
    


    if (elementTypeSerializer != null) {
      staticTyping = false;
    }
    JsonSerializer<Object> elementValueSerializer = _findContentSerializer(prov, beanDesc
      .getClassInfo());
    MapLikeType mlType; if (type.isMapLikeType()) {
      MapLikeType mlt = (MapLikeType)type;
      




      JsonSerializer<Object> keySerializer = _findKeySerializer(prov, beanDesc.getClassInfo());
      if (mlt.isTrueMapType()) {
        return buildMapSerializer(prov, (MapType)mlt, beanDesc, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
      }
      

      JsonSerializer<?> ser = null;
      mlType = (MapLikeType)type;
      for (Serializers serializers : customSerializers()) {
        ser = serializers.findMapLikeSerializer(config, mlType, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
        
        if (ser != null) {
          break;
        }
      }
      if (ser == null) {
        ser = findSerializerByAnnotations(prov, type, beanDesc);
      }
      if ((ser != null) && 
        (_factoryConfig.hasSerializerModifiers())) {
        for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
          ser = mod.modifyMapLikeSerializer(config, mlType, beanDesc, ser);
        }
      }
      
      return ser;
    }
    if (type.isCollectionLikeType()) {
      CollectionLikeType clt = (CollectionLikeType)type;
      if (clt.isTrueCollectionType()) {
        return buildCollectionSerializer(prov, (CollectionType)clt, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
      }
      

      JsonSerializer<?> ser = null;
      CollectionLikeType clType = (CollectionLikeType)type;
      for (Serializers serializers : customSerializers()) {
        ser = serializers.findCollectionLikeSerializer(config, clType, beanDesc, elementTypeSerializer, elementValueSerializer);
        
        if (ser != null) {
          break;
        }
      }
      if (ser == null) {
        ser = findSerializerByAnnotations(prov, type, beanDesc);
      }
      if ((ser != null) && 
        (_factoryConfig.hasSerializerModifiers())) {
        for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
          ser = mod.modifyCollectionLikeSerializer(config, clType, beanDesc, ser);
        }
      }
      
      return ser;
    }
    if (type.isArrayType()) {
      return buildArraySerializer(prov, (ArrayType)type, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
    }
    
    return null;
  }
  








  protected JsonSerializer<?> buildCollectionSerializer(SerializerProvider prov, CollectionType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    throws JsonMappingException
  {
    SerializationConfig config = prov.getConfig();
    JsonSerializer<?> ser = null;
    



    for (Serializers serializers : customSerializers()) {
      ser = serializers.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
      
      if (ser != null) {
        break;
      }
    }
    Object format;
    if (ser == null) {
      ser = findSerializerByAnnotations(prov, type, beanDesc);
      if (ser == null)
      {

        format = beanDesc.findExpectedFormat(null);
        if ((format != null) && (((JsonFormat.Value)format).getShape() == JsonFormat.Shape.OBJECT)) {
          return null;
        }
        Class<?> raw = type.getRawClass();
        if (java.util.EnumSet.class.isAssignableFrom(raw))
        {
          JavaType enumType = type.getContentType();
          
          if (!enumType.isEnumType()) {
            enumType = null;
          }
          ser = buildEnumSetSerializer(enumType);
        } else {
          Class<?> elementRaw = type.getContentType().getRawClass();
          if (isIndexedList(raw)) {
            if (elementRaw == String.class)
            {
              if (ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                ser = com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer.instance;
              }
            } else {
              ser = buildIndexedListSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
            }
          }
          else if (elementRaw == String.class)
          {
            if (ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
              ser = com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer.instance;
            }
          }
          if (ser == null) {
            ser = buildCollectionSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
          }
        }
      }
    }
    

    if (_factoryConfig.hasSerializerModifiers()) {
      for (format = _factoryConfig.serializerModifiers().iterator(); ((Iterator)format).hasNext();) { BeanSerializerModifier mod = (BeanSerializerModifier)((Iterator)format).next();
        ser = mod.modifyCollectionSerializer(config, type, beanDesc, ser);
      }
    }
    return ser;
  }
  






  protected boolean isIndexedList(Class<?> cls)
  {
    return java.util.RandomAccess.class.isAssignableFrom(cls);
  }
  
  public ContainerSerializer<?> buildIndexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer)
  {
    return new com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer(elemType, staticTyping, vts, valueSerializer);
  }
  
  public ContainerSerializer<?> buildCollectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer)
  {
    return new com.fasterxml.jackson.databind.ser.std.CollectionSerializer(elemType, staticTyping, vts, valueSerializer);
  }
  
  public JsonSerializer<?> buildEnumSetSerializer(JavaType enumType) {
    return new com.fasterxml.jackson.databind.ser.std.EnumSetSerializer(enumType);
  }
  















  protected JsonSerializer<?> buildMapSerializer(SerializerProvider prov, MapType type, BeanDescription beanDesc, boolean staticTyping, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    throws JsonMappingException
  {
    JsonFormat.Value format = beanDesc.findExpectedFormat(null);
    if ((format != null) && (format.getShape() == JsonFormat.Shape.OBJECT)) {
      return null;
    }
    
    JsonSerializer<?> ser = null;
    





    SerializationConfig config = prov.getConfig();
    for (Serializers serializers : customSerializers()) {
      ser = serializers.findMapSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
      
      if (ser != null) break; }
    Object filterId;
    if (ser == null) {
      ser = findSerializerByAnnotations(prov, type, beanDesc);
      if (ser == null) {
        filterId = findFilterId(config, beanDesc);
        



        JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc
          .getClassInfo());
        
        java.util.Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForSerialization();
        MapSerializer mapSer = MapSerializer.construct(ignored, type, staticTyping, elementTypeSerializer, keySerializer, elementValueSerializer, filterId);
        

        ser = _checkMapContentInclusion(prov, beanDesc, mapSer);
      }
    }
    
    if (_factoryConfig.hasSerializerModifiers()) {
      for (filterId = _factoryConfig.serializerModifiers().iterator(); ((Iterator)filterId).hasNext();) { BeanSerializerModifier mod = (BeanSerializerModifier)((Iterator)filterId).next();
        ser = mod.modifyMapSerializer(config, type, beanDesc, ser);
      }
    }
    return ser;
  }
  








  protected MapSerializer _checkMapContentInclusion(SerializerProvider prov, BeanDescription beanDesc, MapSerializer mapSer)
    throws JsonMappingException
  {
    JavaType contentType = mapSer.getContentType();
    JsonInclude.Value inclV = _findInclusionWithContent(prov, beanDesc, contentType, Map.class);
    


    JsonInclude.Include incl = inclV == null ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
    if ((incl == JsonInclude.Include.USE_DEFAULTS) || (incl == JsonInclude.Include.ALWAYS))
    {
      if (!prov.isEnabled(com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES)) {
        return mapSer.withContentInclusion(null, true);
      }
      return mapSer;
    }
    



    boolean suppressNulls = true;
    Object valueToSuppress;
    Object valueToSuppress; Object valueToSuppress; switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonInclude$Include[incl.ordinal()]) {
    case 1: 
      Object valueToSuppress = BeanUtil.getDefaultValue(contentType);
      if ((valueToSuppress != null) && 
        (valueToSuppress.getClass().isArray())) {
        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
      }
      

      break;
    case 2: 
      valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null;
      
      break;
    case 3: 
      valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
      break;
    case 4: 
      Object valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter());
      if (valueToSuppress == null) {
        suppressNulls = true;
      } else {
        suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
      }
      break;
    case 5: 
    default: 
      valueToSuppress = null;
    }
    
    return mapSer.withContentInclusion(valueToSuppress, suppressNulls);
  }
  







  protected JsonSerializer<?> buildMapEntrySerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType keyType, JavaType valueType)
    throws JsonMappingException
  {
    JsonFormat.Value formatOverride = prov.getDefaultPropertyFormat(Map.Entry.class);
    JsonFormat.Value formatFromAnnotation = beanDesc.findExpectedFormat(null);
    JsonFormat.Value format = JsonFormat.Value.merge(formatFromAnnotation, formatOverride);
    if (format.getShape() == JsonFormat.Shape.OBJECT) {
      return null;
    }
    
    MapEntrySerializer ser = new MapEntrySerializer(valueType, keyType, valueType, staticTyping, createTypeSerializer(prov.getConfig(), valueType), null);
    
    JavaType contentType = ser.getContentType();
    JsonInclude.Value inclV = _findInclusionWithContent(prov, beanDesc, contentType, Map.Entry.class);
    


    JsonInclude.Include incl = inclV == null ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
    if ((incl == JsonInclude.Include.USE_DEFAULTS) || (incl == JsonInclude.Include.ALWAYS))
    {
      return ser;
    }
    



    boolean suppressNulls = true;
    Object valueToSuppress;
    Object valueToSuppress; Object valueToSuppress; switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonInclude$Include[incl.ordinal()]) {
    case 1: 
      Object valueToSuppress = BeanUtil.getDefaultValue(contentType);
      if ((valueToSuppress != null) && 
        (valueToSuppress.getClass().isArray())) {
        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
      }
      
      break;
    case 2: 
      valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null;
      
      break;
    case 3: 
      valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
      break;
    case 4: 
      Object valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter());
      if (valueToSuppress == null) {
        suppressNulls = true;
      } else {
        suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
      }
      break;
    case 5: 
    default: 
      valueToSuppress = null;
    }
    
    return ser.withContentInclusion(valueToSuppress, suppressNulls);
  }
  










  protected JsonInclude.Value _findInclusionWithContent(SerializerProvider prov, BeanDescription beanDesc, JavaType contentType, Class<?> configType)
    throws JsonMappingException
  {
    SerializationConfig config = prov.getConfig();
    





    JsonInclude.Value inclV = beanDesc.findPropertyInclusion(config.getDefaultPropertyInclusion());
    inclV = config.getDefaultPropertyInclusion(configType, inclV);
    


    JsonInclude.Value valueIncl = config.getDefaultPropertyInclusion(contentType.getRawClass(), null);
    
    if (valueIncl != null) {
      switch (valueIncl.getValueInclusion()) {
      case USE_DEFAULTS: 
        break;
      case CUSTOM: 
        inclV = inclV.withContentFilter(valueIncl.getContentFilter());
        break;
      default: 
        inclV = inclV.withContentInclusion(valueIncl.getValueInclusion());
      }
    }
    return inclV;
  }
  

















  protected JsonSerializer<?> buildArraySerializer(SerializerProvider prov, ArrayType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    throws JsonMappingException
  {
    SerializationConfig config = prov.getConfig();
    JsonSerializer<?> ser = null;
    
    for (Serializers serializers : customSerializers()) {
      ser = serializers.findArraySerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
      
      if (ser != null) {
        break;
      }
    }
    Object raw;
    if (ser == null) {
      raw = type.getRawClass();
      
      if ((elementValueSerializer == null) || (ClassUtil.isJacksonStdImpl(elementValueSerializer))) {
        if ([Ljava.lang.String.class == raw) {
          ser = com.fasterxml.jackson.databind.ser.impl.StringArraySerializer.instance;
        }
        else {
          ser = com.fasterxml.jackson.databind.ser.std.StdArraySerializers.findStandardImpl((Class)raw);
        }
      }
      if (ser == null) {
        ser = new com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
      }
    }
    

    if (_factoryConfig.hasSerializerModifiers()) {
      for (raw = _factoryConfig.serializerModifiers().iterator(); ((Iterator)raw).hasNext();) { BeanSerializerModifier mod = (BeanSerializerModifier)((Iterator)raw).next();
        ser = mod.modifyArraySerializer(config, type, beanDesc, ser);
      }
    }
    return ser;
  }
  











  public JsonSerializer<?> findReferenceSerializer(SerializerProvider prov, ReferenceType refType, BeanDescription beanDesc, boolean staticTyping)
    throws JsonMappingException
  {
    JavaType contentType = refType.getContentType();
    TypeSerializer contentTypeSerializer = (TypeSerializer)contentType.getTypeHandler();
    SerializationConfig config = prov.getConfig();
    if (contentTypeSerializer == null) {
      contentTypeSerializer = createTypeSerializer(config, contentType);
    }
    JsonSerializer<Object> contentSerializer = (JsonSerializer)contentType.getValueHandler();
    for (Serializers serializers : customSerializers()) {
      JsonSerializer<?> ser = serializers.findReferenceSerializer(config, refType, beanDesc, contentTypeSerializer, contentSerializer);
      
      if (ser != null) {
        return ser;
      }
    }
    if (refType.isTypeOrSubTypeOf(AtomicReference.class)) {
      return buildAtomicReferenceSerializer(prov, refType, beanDesc, staticTyping, contentTypeSerializer, contentSerializer);
    }
    
    return null;
  }
  


  protected JsonSerializer<?> buildAtomicReferenceSerializer(SerializerProvider prov, ReferenceType refType, BeanDescription beanDesc, boolean staticTyping, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentSerializer)
    throws JsonMappingException
  {
    JavaType contentType = refType.getReferencedType();
    JsonInclude.Value inclV = _findInclusionWithContent(prov, beanDesc, contentType, AtomicReference.class);
    


    JsonInclude.Include incl = inclV == null ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
    boolean suppressNulls;
    boolean suppressNulls;
    Object valueToSuppress;
    if ((incl == JsonInclude.Include.USE_DEFAULTS) || (incl == JsonInclude.Include.ALWAYS))
    {
      Object valueToSuppress = null;
      suppressNulls = false;
    } else {
      suppressNulls = true;
      Object valueToSuppress; Object valueToSuppress; switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonInclude$Include[incl.ordinal()]) {
      case 1: 
        Object valueToSuppress = BeanUtil.getDefaultValue(contentType);
        if ((valueToSuppress != null) && 
          (valueToSuppress.getClass().isArray())) {
          valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
        }
        
        break;
      case 2: 
        valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null;
        
        break;
      case 3: 
        valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
        break;
      case 4: 
        Object valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter());
        if (valueToSuppress == null) {
          suppressNulls = true;
        } else {
          suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
        }
        break;
      case 5: 
      default: 
        valueToSuppress = null;
      }
      
    }
    AtomicReferenceSerializer ser = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer);
    
    return ser.withContentInclusion(valueToSuppress, suppressNulls);
  }
  











  protected JsonSerializer<?> buildIteratorSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType)
    throws JsonMappingException
  {
    return new com.fasterxml.jackson.databind.ser.impl.IteratorSerializer(valueType, staticTyping, createTypeSerializer(config, valueType));
  }
  





  protected JsonSerializer<?> buildIterableSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType)
    throws JsonMappingException
  {
    return new com.fasterxml.jackson.databind.ser.std.IterableSerializer(valueType, staticTyping, createTypeSerializer(config, valueType));
  }
  






  protected JsonSerializer<?> buildEnumSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    JsonFormat.Value format = beanDesc.findExpectedFormat(null);
    if ((format != null) && (format.getShape() == JsonFormat.Shape.OBJECT))
    {
      ((BasicBeanDescription)beanDesc).removeProperty("declaringClass");
      
      return null;
    }
    
    Class<Enum<?>> enumClass = type.getRawClass();
    JsonSerializer<?> ser = com.fasterxml.jackson.databind.ser.std.EnumSerializer.construct(enumClass, config, beanDesc, format);
    
    if (_factoryConfig.hasSerializerModifiers()) {
      for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
        ser = mod.modifyEnumSerializer(config, type, beanDesc, ser);
      }
    }
    return ser;
  }
  












  protected JsonSerializer<Object> _findKeySerializer(SerializerProvider prov, Annotated a)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = prov.getAnnotationIntrospector();
    Object serDef = intr.findKeySerializer(a);
    if (serDef != null) {
      return prov.serializerInstance(a, serDef);
    }
    return null;
  }
  






  protected JsonSerializer<Object> _findContentSerializer(SerializerProvider prov, Annotated a)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = prov.getAnnotationIntrospector();
    Object serDef = intr.findContentSerializer(a);
    if (serDef != null) {
      return prov.serializerInstance(a, serDef);
    }
    return null;
  }
  



  protected Object findFilterId(SerializationConfig config, BeanDescription beanDesc)
  {
    return config.getAnnotationIntrospector().findFilterId(beanDesc.getClassInfo());
  }
  












  protected boolean usesStaticTyping(SerializationConfig config, BeanDescription beanDesc, TypeSerializer typeSer)
  {
    if (typeSer != null) {
      return false;
    }
    AnnotationIntrospector intr = config.getAnnotationIntrospector();
    JsonSerialize.Typing t = intr.findSerializationTyping(beanDesc.getClassInfo());
    if ((t != null) && (t != JsonSerialize.Typing.DEFAULT_TYPING)) {
      return t == JsonSerialize.Typing.STATIC;
    }
    return config.isEnabled(MapperFeature.USE_STATIC_TYPING);
  }
  
  public abstract SerializerFactory withConfig(SerializerFactoryConfig paramSerializerFactoryConfig);
  
  public abstract JsonSerializer<Object> createSerializer(SerializerProvider paramSerializerProvider, JavaType paramJavaType)
    throws JsonMappingException;
  
  protected abstract Iterable<Serializers> customSerializers();
}
