package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;




































@JacksonStdImpl
public class JsonValueSerializer
  extends StdSerializer<Object>
  implements ContextualSerializer, JsonFormatVisitable, SchemaAware
{
  protected final AnnotatedMember _accessor;
  protected final JsonSerializer<Object> _valueSerializer;
  protected final BeanProperty _property;
  protected final boolean _forceTypeInformation;
  
  public JsonValueSerializer(AnnotatedMember accessor, JsonSerializer<?> ser)
  {
    super(accessor.getType());
    _accessor = accessor;
    _valueSerializer = ser;
    _property = null;
    _forceTypeInformation = true;
  }
  


  public JsonValueSerializer(JsonValueSerializer src, BeanProperty property, JsonSerializer<?> ser, boolean forceTypeInfo)
  {
    super(_notNullClass(src.handledType()));
    _accessor = _accessor;
    _valueSerializer = ser;
    _property = property;
    _forceTypeInformation = forceTypeInfo;
  }
  
  private static final Class<Object> _notNullClass(Class<?> cls)
  {
    return cls == null ? Object.class : cls;
  }
  

  public JsonValueSerializer withResolved(BeanProperty property, JsonSerializer<?> ser, boolean forceTypeInfo)
  {
    if ((_property == property) && (_valueSerializer == ser) && (forceTypeInfo == _forceTypeInformation))
    {
      return this;
    }
    return new JsonValueSerializer(this, property, ser, forceTypeInfo);
  }
  












  public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
    throws JsonMappingException
  {
    JsonSerializer<?> ser = _valueSerializer;
    if (ser == null)
    {



      JavaType t = _accessor.getType();
      if ((provider.isEnabled(MapperFeature.USE_STATIC_TYPING)) || (t.isFinal()))
      {





        ser = provider.findPrimaryPropertySerializer(t, property);
        



        boolean forceTypeInformation = isNaturalTypeWithStdHandling(t.getRawClass(), ser);
        return withResolved(property, ser, forceTypeInformation);
      }
    }
    else {
      ser = provider.handlePrimaryContextualization(ser, property);
      return withResolved(property, ser, _forceTypeInformation);
    }
    return this;
  }
  





  public void serialize(Object bean, JsonGenerator gen, SerializerProvider prov)
    throws IOException
  {
    try
    {
      Object value = _accessor.getValue(bean);
      if (value == null) {
        prov.defaultSerializeNull(gen);
        return;
      }
      JsonSerializer<Object> ser = _valueSerializer;
      if (ser == null) {
        Class<?> c = value.getClass();
        




        ser = prov.findTypedValueSerializer(c, true, _property);
      }
      ser.serialize(value, gen, prov);
    } catch (Exception e) {
      wrapAndThrow(prov, e, bean, _accessor.getName() + "()");
    }
  }
  


  public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer0)
    throws IOException
  {
    Object value = null;
    try {
      value = _accessor.getValue(bean);
      
      if (value == null) {
        provider.defaultSerializeNull(gen);
        return;
      }
      JsonSerializer<Object> ser = _valueSerializer;
      if (ser == null) {
        ser = provider.findValueSerializer(value.getClass(), _property);


      }
      else if (_forceTypeInformation)
      {
        WritableTypeId typeIdDef = typeSer0.writeTypePrefix(gen, typeSer0
          .typeId(bean, JsonToken.VALUE_STRING));
        ser.serialize(value, gen, provider);
        typeSer0.writeTypeSuffix(gen, typeIdDef);
        
        return;
      }
      



      TypeSerializerRerouter rr = new TypeSerializerRerouter(typeSer0, bean);
      ser.serializeWithType(value, gen, provider, rr);
    } catch (Exception e) {
      wrapAndThrow(provider, e, bean, _accessor.getName() + "()");
    }
  }
  


  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    throws JsonMappingException
  {
    if ((_valueSerializer instanceof SchemaAware)) {
      return ((SchemaAware)_valueSerializer).getSchema(provider, null);
    }
    return JsonSchema.getDefaultSchemaNode();
  }
  










  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    JavaType type = _accessor.getType();
    Class<?> declaring = _accessor.getDeclaringClass();
    if ((declaring != null) && (ClassUtil.isEnumType(declaring)) && 
      (_acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring))) {
      return;
    }
    
    JsonSerializer<Object> ser = _valueSerializer;
    if (ser == null) {
      ser = visitor.getProvider().findTypedValueSerializer(type, false, _property);
      if (ser == null) {
        visitor.expectAnyFormat(typeHint);
        return;
      }
    }
    ser.acceptJsonFormatVisitor(visitor, type);
  }
  











  protected boolean _acceptJsonFormatVisitorForEnum(JsonFormatVisitorWrapper visitor, JavaType typeHint, Class<?> enumType)
    throws JsonMappingException
  {
    JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
    if (stringVisitor != null) {
      Set<String> enums = new LinkedHashSet();
      for (Object en : enumType.getEnumConstants())
      {
        try
        {

          enums.add(String.valueOf(_accessor.getValue(en)));
        } catch (Exception e) {
          Throwable t = e;
          while (((t instanceof InvocationTargetException)) && (t.getCause() != null)) {
            t = t.getCause();
          }
          ClassUtil.throwIfError(t);
          throw JsonMappingException.wrapWithPath(t, en, _accessor.getName() + "()");
        }
      }
      stringVisitor.enumTypes(enums);
    }
    return true;
  }
  

  protected boolean isNaturalTypeWithStdHandling(Class<?> rawType, JsonSerializer<?> ser)
  {
    if (rawType.isPrimitive()) {
      if ((rawType != Integer.TYPE) && (rawType != Boolean.TYPE) && (rawType != Double.TYPE)) {
        return false;
      }
    }
    else if ((rawType != String.class) && (rawType != Integer.class) && (rawType != Boolean.class) && (rawType != Double.class))
    {
      return false;
    }
    
    return isDefaultSerializer(ser);
  }
  






  public String toString()
  {
    return "(@JsonValue serializer for method " + _accessor.getDeclaringClass() + "#" + _accessor.getName() + ")";
  }
  




  static class TypeSerializerRerouter
    extends TypeSerializer
  {
    protected final TypeSerializer _typeSerializer;
    


    protected final Object _forObject;
    



    public TypeSerializerRerouter(TypeSerializer ts, Object ob)
    {
      _typeSerializer = ts;
      _forObject = ob;
    }
    
    public TypeSerializer forProperty(BeanProperty prop)
    {
      throw new UnsupportedOperationException();
    }
    
    public JsonTypeInfo.As getTypeInclusion()
    {
      return _typeSerializer.getTypeInclusion();
    }
    
    public String getPropertyName()
    {
      return _typeSerializer.getPropertyName();
    }
    
    public TypeIdResolver getTypeIdResolver()
    {
      return _typeSerializer.getTypeIdResolver();
    }
    



    public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId typeId)
      throws IOException
    {
      forValue = _forObject;
      return _typeSerializer.writeTypePrefix(g, typeId);
    }
    

    public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId typeId)
      throws IOException
    {
      return _typeSerializer.writeTypeSuffix(g, typeId);
    }
    

    @Deprecated
    public void writeTypePrefixForScalar(Object value, JsonGenerator gen)
      throws IOException
    {
      _typeSerializer.writeTypePrefixForScalar(_forObject, gen);
    }
    
    @Deprecated
    public void writeTypePrefixForObject(Object value, JsonGenerator gen) throws IOException
    {
      _typeSerializer.writeTypePrefixForObject(_forObject, gen);
    }
    
    @Deprecated
    public void writeTypePrefixForArray(Object value, JsonGenerator gen) throws IOException
    {
      _typeSerializer.writeTypePrefixForArray(_forObject, gen);
    }
    
    @Deprecated
    public void writeTypeSuffixForScalar(Object value, JsonGenerator gen) throws IOException
    {
      _typeSerializer.writeTypeSuffixForScalar(_forObject, gen);
    }
    
    @Deprecated
    public void writeTypeSuffixForObject(Object value, JsonGenerator gen) throws IOException
    {
      _typeSerializer.writeTypeSuffixForObject(_forObject, gen);
    }
    
    @Deprecated
    public void writeTypeSuffixForArray(Object value, JsonGenerator gen) throws IOException
    {
      _typeSerializer.writeTypeSuffixForArray(_forObject, gen);
    }
    
    @Deprecated
    public void writeTypePrefixForScalar(Object value, JsonGenerator gen, Class<?> type) throws IOException
    {
      _typeSerializer.writeTypePrefixForScalar(_forObject, gen, type);
    }
    
    @Deprecated
    public void writeTypePrefixForObject(Object value, JsonGenerator gen, Class<?> type) throws IOException
    {
      _typeSerializer.writeTypePrefixForObject(_forObject, gen, type);
    }
    
    @Deprecated
    public void writeTypePrefixForArray(Object value, JsonGenerator gen, Class<?> type) throws IOException
    {
      _typeSerializer.writeTypePrefixForArray(_forObject, gen, type);
    }
    






    @Deprecated
    public void writeCustomTypePrefixForScalar(Object value, JsonGenerator gen, String typeId)
      throws IOException
    {
      _typeSerializer.writeCustomTypePrefixForScalar(_forObject, gen, typeId);
    }
    
    @Deprecated
    public void writeCustomTypePrefixForObject(Object value, JsonGenerator gen, String typeId) throws IOException
    {
      _typeSerializer.writeCustomTypePrefixForObject(_forObject, gen, typeId);
    }
    
    @Deprecated
    public void writeCustomTypePrefixForArray(Object value, JsonGenerator gen, String typeId) throws IOException
    {
      _typeSerializer.writeCustomTypePrefixForArray(_forObject, gen, typeId);
    }
    
    @Deprecated
    public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException
    {
      _typeSerializer.writeCustomTypeSuffixForScalar(_forObject, gen, typeId);
    }
    
    @Deprecated
    public void writeCustomTypeSuffixForObject(Object value, JsonGenerator gen, String typeId) throws IOException
    {
      _typeSerializer.writeCustomTypeSuffixForObject(_forObject, gen, typeId);
    }
    
    @Deprecated
    public void writeCustomTypeSuffixForArray(Object value, JsonGenerator gen, String typeId) throws IOException
    {
      _typeSerializer.writeCustomTypeSuffixForArray(_forObject, gen, typeId);
    }
  }
}
