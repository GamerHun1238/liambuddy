package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;






















public abstract class SerializerProvider
  extends DatabindContext
{
  protected static final boolean CACHE_UNKNOWN_MAPPINGS = false;
  public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER = new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
  










  protected static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();
  









  protected final SerializationConfig _config;
  









  protected final Class<?> _serializationView;
  








  protected final SerializerFactory _serializerFactory;
  








  protected final SerializerCache _serializerCache;
  








  protected transient ContextAttributes _attributes;
  








  protected JsonSerializer<Object> _unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
  





  protected JsonSerializer<Object> _keySerializer;
  




  protected JsonSerializer<Object> _nullValueSerializer = NullSerializer.instance;
  







  protected JsonSerializer<Object> _nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
  








  protected final ReadOnlyClassToSerializerMap _knownSerializers;
  








  protected DateFormat _dateFormat;
  







  protected final boolean _stdNullValueSerializer;
  








  public SerializerProvider()
  {
    _config = null;
    _serializerFactory = null;
    _serializerCache = new SerializerCache();
    
    _knownSerializers = null;
    
    _serializationView = null;
    _attributes = null;
    

    _stdNullValueSerializer = true;
  }
  







  protected SerializerProvider(SerializerProvider src, SerializationConfig config, SerializerFactory f)
  {
    _serializerFactory = f;
    _config = config;
    
    _serializerCache = _serializerCache;
    _unknownTypeSerializer = _unknownTypeSerializer;
    _keySerializer = _keySerializer;
    _nullValueSerializer = _nullValueSerializer;
    _nullKeySerializer = _nullKeySerializer;
    
    _stdNullValueSerializer = (_nullValueSerializer == DEFAULT_NULL_KEY_SERIALIZER);
    
    _serializationView = config.getActiveView();
    _attributes = config.getAttributes();
    



    _knownSerializers = _serializerCache.getReadOnlyLookupMap();
  }
  






  protected SerializerProvider(SerializerProvider src)
  {
    _config = null;
    _serializationView = null;
    _serializerFactory = null;
    _knownSerializers = null;
    

    _serializerCache = new SerializerCache();
    
    _unknownTypeSerializer = _unknownTypeSerializer;
    _keySerializer = _keySerializer;
    _nullValueSerializer = _nullValueSerializer;
    _nullKeySerializer = _nullKeySerializer;
    
    _stdNullValueSerializer = _stdNullValueSerializer;
  }
  












  public void setDefaultKeySerializer(JsonSerializer<Object> ks)
  {
    if (ks == null) {
      throw new IllegalArgumentException("Cannot pass null JsonSerializer");
    }
    _keySerializer = ks;
  }
  









  public void setNullValueSerializer(JsonSerializer<Object> nvs)
  {
    if (nvs == null) {
      throw new IllegalArgumentException("Cannot pass null JsonSerializer");
    }
    _nullValueSerializer = nvs;
  }
  









  public void setNullKeySerializer(JsonSerializer<Object> nks)
  {
    if (nks == null) {
      throw new IllegalArgumentException("Cannot pass null JsonSerializer");
    }
    _nullKeySerializer = nks;
  }
  









  public final SerializationConfig getConfig()
  {
    return _config;
  }
  
  public final AnnotationIntrospector getAnnotationIntrospector() {
    return _config.getAnnotationIntrospector();
  }
  
  public final TypeFactory getTypeFactory()
  {
    return _config.getTypeFactory();
  }
  
  public final Class<?> getActiveView() {
    return _serializationView;
  }
  
  @Deprecated
  public final Class<?> getSerializationView()
  {
    return _serializationView;
  }
  
  public final boolean canOverrideAccessModifiers() {
    return _config.canOverrideAccessModifiers();
  }
  
  public final boolean isEnabled(MapperFeature feature)
  {
    return _config.isEnabled(feature);
  }
  
  public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType)
  {
    return _config.getDefaultPropertyFormat(baseType);
  }
  


  public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType)
  {
    return _config.getDefaultPropertyInclusion();
  }
  






  public Locale getLocale()
  {
    return _config.getLocale();
  }
  






  public TimeZone getTimeZone()
  {
    return _config.getTimeZone();
  }
  






  public Object getAttribute(Object key)
  {
    return _attributes.getAttribute(key);
  }
  

  public SerializerProvider setAttribute(Object key, Object value)
  {
    _attributes = _attributes.withPerCallAttribute(key, value);
    return this;
  }
  













  public final boolean isEnabled(SerializationFeature feature)
  {
    return _config.isEnabled(feature);
  }
  





  public final boolean hasSerializationFeatures(int featureMask)
  {
    return _config.hasSerializationFeatures(featureMask);
  }
  






  public final FilterProvider getFilterProvider()
  {
    return _config.getFilterProvider();
  }
  






  public JsonGenerator getGenerator()
  {
    return null;
  }
  



















  public abstract WritableObjectId findObjectId(Object paramObject, ObjectIdGenerator<?> paramObjectIdGenerator);
  



















  public JsonSerializer<Object> findValueSerializer(Class<?> valueType, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
    if (ser == null)
    {
      ser = _serializerCache.untypedValueSerializer(valueType);
      if (ser == null)
      {
        ser = _serializerCache.untypedValueSerializer(_config.constructType(valueType));
        if (ser == null)
        {
          ser = _createAndCacheUntypedSerializer(valueType);
          
          if (ser == null) {
            ser = getUnknownTypeSerializer(valueType);
            



            return ser;
          }
        }
      }
    }
    
    return handleSecondaryContextualization(ser, property);
  }
  













  public JsonSerializer<Object> findValueSerializer(JavaType valueType, BeanProperty property)
    throws JsonMappingException
  {
    if (valueType == null) {
      reportMappingProblem("Null passed for `valueType` of `findValueSerializer()`", new Object[0]);
    }
    
    JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
    if (ser == null) {
      ser = _serializerCache.untypedValueSerializer(valueType);
      if (ser == null) {
        ser = _createAndCacheUntypedSerializer(valueType);
        if (ser == null) {
          ser = getUnknownTypeSerializer(valueType.getRawClass());
          


          return ser;
        }
      }
    }
    return handleSecondaryContextualization(ser, property);
  }
  







  public JsonSerializer<Object> findValueSerializer(Class<?> valueType)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
    if (ser == null) {
      ser = _serializerCache.untypedValueSerializer(valueType);
      if (ser == null) {
        ser = _serializerCache.untypedValueSerializer(_config.constructType(valueType));
        if (ser == null) {
          ser = _createAndCacheUntypedSerializer(valueType);
          if (ser == null) {
            ser = getUnknownTypeSerializer(valueType);
          }
        }
      }
    }
    


    return ser;
  }
  








  public JsonSerializer<Object> findValueSerializer(JavaType valueType)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
    if (ser == null) {
      ser = _serializerCache.untypedValueSerializer(valueType);
      if (ser == null) {
        ser = _createAndCacheUntypedSerializer(valueType);
        if (ser == null) {
          ser = getUnknownTypeSerializer(valueType.getRawClass());
        }
      }
    }
    


    return ser;
  }
  













  public JsonSerializer<Object> findPrimaryPropertySerializer(JavaType valueType, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
    if (ser == null) {
      ser = _serializerCache.untypedValueSerializer(valueType);
      if (ser == null) {
        ser = _createAndCacheUntypedSerializer(valueType);
        if (ser == null) {
          ser = getUnknownTypeSerializer(valueType.getRawClass());
          



          return ser;
        }
      }
    }
    return handlePrimaryContextualization(ser, property);
  }
  





  public JsonSerializer<Object> findPrimaryPropertySerializer(Class<?> valueType, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
    if (ser == null) {
      ser = _serializerCache.untypedValueSerializer(valueType);
      if (ser == null) {
        ser = _serializerCache.untypedValueSerializer(_config.constructType(valueType));
        if (ser == null) {
          ser = _createAndCacheUntypedSerializer(valueType);
          if (ser == null) {
            ser = getUnknownTypeSerializer(valueType);
            


            return ser;
          }
        }
      }
    }
    return handlePrimaryContextualization(ser, property);
  }
  

















  public JsonSerializer<Object> findTypedValueSerializer(Class<?> valueType, boolean cache, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.typedValueSerializer(valueType);
    if (ser != null) {
      return ser;
    }
    
    ser = _serializerCache.typedValueSerializer(valueType);
    if (ser != null) {
      return ser;
    }
    

    ser = findValueSerializer(valueType, property);
    TypeSerializer typeSer = _serializerFactory.createTypeSerializer(_config, _config
      .constructType(valueType));
    if (typeSer != null) {
      typeSer = typeSer.forProperty(property);
      ser = new TypeWrappedSerializer(typeSer, ser);
    }
    if (cache) {
      _serializerCache.addTypedSerializer(valueType, ser);
    }
    return ser;
  }
  


















  public JsonSerializer<Object> findTypedValueSerializer(JavaType valueType, boolean cache, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.typedValueSerializer(valueType);
    if (ser != null) {
      return ser;
    }
    
    ser = _serializerCache.typedValueSerializer(valueType);
    if (ser != null) {
      return ser;
    }
    

    ser = findValueSerializer(valueType, property);
    TypeSerializer typeSer = _serializerFactory.createTypeSerializer(_config, valueType);
    if (typeSer != null) {
      typeSer = typeSer.forProperty(property);
      ser = new TypeWrappedSerializer(typeSer, ser);
    }
    if (cache) {
      _serializerCache.addTypedSerializer(valueType, ser);
    }
    return ser;
  }
  





  public TypeSerializer findTypeSerializer(JavaType javaType)
    throws JsonMappingException
  {
    return _serializerFactory.createTypeSerializer(_config, javaType);
  }
  










  public JsonSerializer<Object> findKeySerializer(JavaType keyType, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _serializerFactory.createKeySerializer(_config, keyType, _keySerializer);
    
    return _handleContextualResolvable(ser, property);
  }
  



  public JsonSerializer<Object> findKeySerializer(Class<?> rawKeyType, BeanProperty property)
    throws JsonMappingException
  {
    return findKeySerializer(_config.constructType(rawKeyType), property);
  }
  








  public JsonSerializer<Object> getDefaultNullKeySerializer()
  {
    return _nullKeySerializer;
  }
  


  public JsonSerializer<Object> getDefaultNullValueSerializer()
  {
    return _nullValueSerializer;
  }
  


















  public JsonSerializer<Object> findNullKeySerializer(JavaType serializationType, BeanProperty property)
    throws JsonMappingException
  {
    return _nullKeySerializer;
  }
  










  public JsonSerializer<Object> findNullValueSerializer(BeanProperty property)
    throws JsonMappingException
  {
    return _nullValueSerializer;
  }
  












  public JsonSerializer<Object> getUnknownTypeSerializer(Class<?> unknownType)
  {
    if (unknownType == Object.class) {
      return _unknownTypeSerializer;
    }
    
    return new UnknownSerializer(unknownType);
  }
  






  public boolean isUnknownTypeSerializer(JsonSerializer<?> ser)
  {
    if ((ser == _unknownTypeSerializer) || (ser == null)) {
      return true;
    }
    

    if ((isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) && 
      (ser.getClass() == UnknownSerializer.class)) {
      return true;
    }
    
    return false;
  }
  













  public abstract JsonSerializer<Object> serializerInstance(Annotated paramAnnotated, Object paramObject)
    throws JsonMappingException;
  













  public abstract Object includeFilterInstance(BeanPropertyDefinition paramBeanPropertyDefinition, Class<?> paramClass)
    throws JsonMappingException;
  













  public abstract boolean includeFilterSuppressNulls(Object paramObject)
    throws JsonMappingException;
  













  public JsonSerializer<?> handlePrimaryContextualization(JsonSerializer<?> ser, BeanProperty property)
    throws JsonMappingException
  {
    if ((ser != null) && 
      ((ser instanceof ContextualSerializer))) {
      ser = ((ContextualSerializer)ser).createContextual(this, property);
    }
    
    return ser;
  }
  

















  public JsonSerializer<?> handleSecondaryContextualization(JsonSerializer<?> ser, BeanProperty property)
    throws JsonMappingException
  {
    if ((ser != null) && 
      ((ser instanceof ContextualSerializer))) {
      ser = ((ContextualSerializer)ser).createContextual(this, property);
    }
    
    return ser;
  }
  












  public final void defaultSerializeValue(Object value, JsonGenerator gen)
    throws IOException
  {
    if (value == null) {
      if (_stdNullValueSerializer) {
        gen.writeNull();
      } else {
        _nullValueSerializer.serialize(null, gen, this);
      }
    } else {
      Class<?> cls = value.getClass();
      findTypedValueSerializer(cls, true, null).serialize(value, gen, this);
    }
  }
  





  public final void defaultSerializeField(String fieldName, Object value, JsonGenerator gen)
    throws IOException
  {
    gen.writeFieldName(fieldName);
    if (value == null)
    {


      if (_stdNullValueSerializer) {
        gen.writeNull();
      } else {
        _nullValueSerializer.serialize(null, gen, this);
      }
    } else {
      Class<?> cls = value.getClass();
      findTypedValueSerializer(cls, true, null).serialize(value, gen, this);
    }
  }
  







  public final void defaultSerializeDateValue(long timestamp, JsonGenerator gen)
    throws IOException
  {
    if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
      gen.writeNumber(timestamp);
    } else {
      gen.writeString(_dateFormat().format(new Date(timestamp)));
    }
  }
  






  public final void defaultSerializeDateValue(Date date, JsonGenerator gen)
    throws IOException
  {
    if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
      gen.writeNumber(date.getTime());
    } else {
      gen.writeString(_dateFormat().format(date));
    }
  }
  




  public void defaultSerializeDateKey(long timestamp, JsonGenerator gen)
    throws IOException
  {
    if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
      gen.writeFieldName(String.valueOf(timestamp));
    } else {
      gen.writeFieldName(_dateFormat().format(new Date(timestamp)));
    }
  }
  




  public void defaultSerializeDateKey(Date date, JsonGenerator gen)
    throws IOException
  {
    if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
      gen.writeFieldName(String.valueOf(date.getTime()));
    } else {
      gen.writeFieldName(_dateFormat().format(date));
    }
  }
  
  public final void defaultSerializeNull(JsonGenerator gen) throws IOException
  {
    if (_stdNullValueSerializer) {
      gen.writeNull();
    } else {
      _nullValueSerializer.serialize(null, gen, this);
    }
  }
  











  public void reportMappingProblem(String message, Object... args)
    throws JsonMappingException
  {
    throw mappingException(message, args);
  }
  






  public <T> T reportBadTypeDefinition(BeanDescription bean, String msg, Object... msgArgs)
    throws JsonMappingException
  {
    String beanDesc = "N/A";
    if (bean != null) {
      beanDesc = ClassUtil.nameOf(bean.getBeanClass());
    }
    msg = String.format("Invalid type definition for type %s: %s", new Object[] { beanDesc, 
      _format(msg, msgArgs) });
    throw InvalidDefinitionException.from(getGenerator(), msg, bean, null);
  }
  






  public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String message, Object... msgArgs)
    throws JsonMappingException
  {
    message = _format(message, msgArgs);
    String propName = "N/A";
    if (prop != null) {
      propName = _quotedString(prop.getName());
    }
    String beanDesc = "N/A";
    if (bean != null) {
      beanDesc = ClassUtil.nameOf(bean.getBeanClass());
    }
    message = String.format("Invalid definition for property %s (of type %s): %s", new Object[] { propName, beanDesc, message });
    
    throw InvalidDefinitionException.from(getGenerator(), message, bean, prop);
  }
  
  public <T> T reportBadDefinition(JavaType type, String msg) throws JsonMappingException
  {
    throw InvalidDefinitionException.from(getGenerator(), msg, type);
  }
  


  public <T> T reportBadDefinition(JavaType type, String msg, Throwable cause)
    throws JsonMappingException
  {
    InvalidDefinitionException e = InvalidDefinitionException.from(getGenerator(), msg, type);
    e.initCause(cause);
    throw e;
  }
  


  public <T> T reportBadDefinition(Class<?> raw, String msg, Throwable cause)
    throws JsonMappingException
  {
    InvalidDefinitionException e = InvalidDefinitionException.from(getGenerator(), msg, constructType(raw));
    e.initCause(cause);
    throw e;
  }
  





  public void reportMappingProblem(Throwable t, String message, Object... msgArgs)
    throws JsonMappingException
  {
    message = _format(message, msgArgs);
    throw JsonMappingException.from(getGenerator(), message, t);
  }
  

  public JsonMappingException invalidTypeIdException(JavaType baseType, String typeId, String extraDesc)
  {
    String msg = String.format("Could not resolve type id '%s' as a subtype of %s", new Object[] { typeId, 
      ClassUtil.getTypeDescription(baseType) });
    return InvalidTypeIdException.from(null, _colonConcat(msg, extraDesc), baseType, typeId);
  }
  














  @Deprecated
  public JsonMappingException mappingException(String message, Object... msgArgs)
  {
    return JsonMappingException.from(getGenerator(), _format(message, msgArgs));
  }
  








  @Deprecated
  protected JsonMappingException mappingException(Throwable t, String message, Object... msgArgs)
  {
    return JsonMappingException.from(getGenerator(), _format(message, msgArgs), t);
  }
  






  protected void _reportIncompatibleRootType(Object value, JavaType rootType)
    throws IOException
  {
    if (rootType.isPrimitive()) {
      Class<?> wrapperType = ClassUtil.wrapperType(rootType.getRawClass());
      
      if (wrapperType.isAssignableFrom(value.getClass())) {
        return;
      }
    }
    reportBadDefinition(rootType, String.format("Incompatible types: declared root type (%s) vs %s", new Object[] { rootType, 
    
      ClassUtil.classNameOf(value) }));
  }
  








  protected JsonSerializer<Object> _findExplicitUntypedSerializer(Class<?> runtimeType)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(runtimeType);
    if (ser == null)
    {
      ser = _serializerCache.untypedValueSerializer(runtimeType);
      if (ser == null) {
        ser = _createAndCacheUntypedSerializer(runtimeType);
      }
    }
    




    if (isUnknownTypeSerializer(ser)) {
      return null;
    }
    return ser;
  }
  











  protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> rawType)
    throws JsonMappingException
  {
    JavaType fullType = _config.constructType(rawType);
    JsonSerializer<Object> ser;
    try {
      ser = _createUntypedSerializer(fullType);
    }
    catch (IllegalArgumentException iae) {
      JsonSerializer<Object> ser;
      ser = null;
      reportMappingProblem(iae, ClassUtil.exceptionMessage(iae), new Object[0]);
    }
    
    if (ser != null)
    {
      _serializerCache.addAndResolveNonTypedSerializer(rawType, fullType, ser, this);
    }
    return ser;
  }
  
  protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type) throws JsonMappingException
  {
    JsonSerializer<Object> ser;
    try
    {
      ser = _createUntypedSerializer(type);
    }
    catch (IllegalArgumentException iae) {
      JsonSerializer<Object> ser;
      ser = null;
      reportMappingProblem(iae, ClassUtil.exceptionMessage(iae), new Object[0]);
    }
    
    if (ser != null)
    {
      _serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
    }
    return ser;
  }
  












  protected JsonSerializer<Object> _createUntypedSerializer(JavaType type)
    throws JsonMappingException
  {
    synchronized (_serializerCache)
    {
      return _serializerFactory.createSerializer(this, type);
    }
  }
  






  protected JsonSerializer<Object> _handleContextualResolvable(JsonSerializer<?> ser, BeanProperty property)
    throws JsonMappingException
  {
    if ((ser instanceof ResolvableSerializer)) {
      ((ResolvableSerializer)ser).resolve(this);
    }
    return handleSecondaryContextualization(ser, property);
  }
  

  protected JsonSerializer<Object> _handleResolvable(JsonSerializer<?> ser)
    throws JsonMappingException
  {
    if ((ser instanceof ResolvableSerializer)) {
      ((ResolvableSerializer)ser).resolve(this);
    }
    return ser;
  }
  






  protected final DateFormat _dateFormat()
  {
    if (_dateFormat != null) {
      return _dateFormat;
    }
    



    DateFormat df = _config.getDateFormat();
    _dateFormat = (df = (DateFormat)df.clone());
    







    return df;
  }
}
