package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
import com.fasterxml.jackson.databind.util.EnumValues;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public abstract class StdKeySerializers
{
  protected static final JsonSerializer<Object> DEFAULT_KEY_SERIALIZER = new StdKeySerializer();
  
  protected static final JsonSerializer<Object> DEFAULT_STRING_SERIALIZER = new StringKeySerializer();
  






  public StdKeySerializers() {}
  






  public static JsonSerializer<Object> getStdKeySerializer(SerializationConfig config, Class<?> rawKeyType, boolean useDefault)
  {
    if ((rawKeyType == null) || (rawKeyType == Object.class)) {
      return new Dynamic();
    }
    if (rawKeyType == String.class) {
      return DEFAULT_STRING_SERIALIZER;
    }
    if (rawKeyType.isPrimitive()) {
      rawKeyType = com.fasterxml.jackson.databind.util.ClassUtil.wrapperType(rawKeyType);
    }
    if (rawKeyType == Integer.class) {
      return new Default(5, rawKeyType);
    }
    if (rawKeyType == Long.class) {
      return new Default(6, rawKeyType);
    }
    if ((rawKeyType.isPrimitive()) || (Number.class.isAssignableFrom(rawKeyType)))
    {

      return new Default(8, rawKeyType);
    }
    if (rawKeyType == Class.class) {
      return new Default(3, rawKeyType);
    }
    if (Date.class.isAssignableFrom(rawKeyType)) {
      return new Default(1, rawKeyType);
    }
    if (Calendar.class.isAssignableFrom(rawKeyType)) {
      return new Default(2, rawKeyType);
    }
    
    if (rawKeyType == java.util.UUID.class) {
      return new Default(8, rawKeyType);
    }
    if (rawKeyType == [B.class) {
      return new Default(7, rawKeyType);
    }
    if (useDefault)
    {
      return new Default(8, rawKeyType);
    }
    return null;
  }
  








  public static JsonSerializer<Object> getFallbackKeySerializer(SerializationConfig config, Class<?> rawKeyType)
  {
    if (rawKeyType != null)
    {





      if (rawKeyType == Enum.class) {
        return new Dynamic();
      }
      

      if (com.fasterxml.jackson.databind.util.ClassUtil.isEnumType(rawKeyType)) {
        return EnumKeySerializer.construct(rawKeyType, 
          EnumValues.constructFromName(config, rawKeyType));
      }
    }
    
    return new Default(8, rawKeyType);
  }
  


  @Deprecated
  public static JsonSerializer<Object> getDefault()
  {
    return DEFAULT_KEY_SERIALIZER;
  }
  


  public static class Default
    extends StdSerializer<Object>
  {
    static final int TYPE_DATE = 1;
    

    static final int TYPE_CALENDAR = 2;
    
    static final int TYPE_CLASS = 3;
    
    static final int TYPE_ENUM = 4;
    
    static final int TYPE_INTEGER = 5;
    
    static final int TYPE_LONG = 6;
    
    static final int TYPE_BYTE_ARRAY = 7;
    
    static final int TYPE_TO_STRING = 8;
    
    protected final int _typeId;
    

    public Default(int typeId, Class<?> type)
    {
      super(false);
      _typeId = typeId;
    }
    
    public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException
    {
      switch (_typeId) {
      case 1: 
        provider.defaultSerializeDateKey((Date)value, g);
        break;
      case 2: 
        provider.defaultSerializeDateKey(((Calendar)value).getTimeInMillis(), g);
        break;
      case 3: 
        g.writeFieldName(((Class)value).getName());
        break;
      case 4: 
        String key;
        
        String key;
        if (provider.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
          key = value.toString();
        } else {
          Enum<?> e = (Enum)value;
          String key;
          if (provider.isEnabled(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)) {
            key = String.valueOf(e.ordinal());
          } else {
            key = e.name();
          }
        }
        g.writeFieldName(key);
        
        break;
      case 5: 
      case 6: 
        g.writeFieldId(((Number)value).longValue());
        break;
      
      case 7: 
        String encoded = provider.getConfig().getBase64Variant().encode((byte[])value);
        g.writeFieldName(encoded);
        
        break;
      case 8: 
      default: 
        g.writeFieldName(value.toString());
      }
      
    }
  }
  

  public static class Dynamic
    extends StdSerializer<Object>
  {
    protected transient PropertySerializerMap _dynamicSerializers;
    

    public Dynamic()
    {
      super(false);
      _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }
    
    Object readResolve()
    {
      _dynamicSerializers = PropertySerializerMap.emptyForProperties();
      return this;
    }
    

    public void serialize(Object value, JsonGenerator g, SerializerProvider provider)
      throws IOException
    {
      Class<?> cls = value.getClass();
      PropertySerializerMap m = _dynamicSerializers;
      JsonSerializer<Object> ser = m.serializerFor(cls);
      if (ser == null) {
        ser = _findAndAddDynamic(m, cls, provider);
      }
      ser.serialize(value, g, provider);
    }
    
    public void acceptJsonFormatVisitor(com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper visitor, JavaType typeHint) throws com.fasterxml.jackson.databind.JsonMappingException
    {
      visitStringFormat(visitor, typeHint);
    }
    

    protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
      throws com.fasterxml.jackson.databind.JsonMappingException
    {
      if (type == Object.class)
      {
        JsonSerializer<Object> ser = new StdKeySerializers.Default(8, type);
        _dynamicSerializers = map.newWith(type, ser);
        return ser;
      }
      

      PropertySerializerMap.SerializerAndMapResult result = map.findAndAddKeySerializer(type, provider, null);
      
      if (map != map) {
        _dynamicSerializers = map;
      }
      return serializer;
    }
  }
  
  public static class StringKeySerializer
    extends StdSerializer<Object>
  {
    public StringKeySerializer()
    {
      super(false);
    }
    
    public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeFieldName((String)value);
    }
  }
  


  public static class EnumKeySerializer
    extends StdSerializer<Object>
  {
    protected final EnumValues _values;
    

    protected EnumKeySerializer(Class<?> enumType, EnumValues values)
    {
      super(false);
      _values = values;
    }
    

    public static EnumKeySerializer construct(Class<?> enumType, EnumValues enumValues)
    {
      return new EnumKeySerializer(enumType, enumValues);
    }
    

    public void serialize(Object value, JsonGenerator g, SerializerProvider serializers)
      throws IOException
    {
      if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
        g.writeFieldName(value.toString());
        return;
      }
      Enum<?> en = (Enum)value;
      
      if (serializers.isEnabled(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)) {
        g.writeFieldName(String.valueOf(en.ordinal()));
        return;
      }
      g.writeFieldName(_values.serializedValueFor(en));
    }
  }
}
