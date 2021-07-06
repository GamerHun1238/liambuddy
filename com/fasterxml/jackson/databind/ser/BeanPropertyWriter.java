package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;


@JacksonStdImpl
public class BeanPropertyWriter
  extends PropertyWriter
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
  








  protected final SerializedString _name;
  








  protected final PropertyName _wrapperName;
  








  protected final JavaType _declaredType;
  







  protected final JavaType _cfgSerializationType;
  







  protected JavaType _nonTrivialBaseType;
  







  protected final transient Annotations _contextAnnotations;
  







  protected final AnnotatedMember _member;
  







  protected transient Method _accessorMethod;
  







  protected transient Field _field;
  







  protected JsonSerializer<Object> _serializer;
  







  protected JsonSerializer<Object> _nullSerializer;
  







  protected TypeSerializer _typeSerializer;
  







  protected transient PropertySerializerMap _dynamicSerializers;
  







  protected final boolean _suppressNulls;
  







  protected final Object _suppressableValue;
  







  protected final Class<?>[] _includeInViews;
  







  protected transient HashMap<Object, Object> _internalSettings;
  








  public BeanPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue, Class<?>[] includeInViews)
  {
    super(propDef);
    _member = member;
    _contextAnnotations = contextAnnotations;
    
    _name = new SerializedString(propDef.getName());
    _wrapperName = propDef.getWrapperName();
    
    _declaredType = declaredType;
    _serializer = ser;
    
    _dynamicSerializers = (ser == null ? PropertySerializerMap.emptyForProperties() : null);
    _typeSerializer = typeSer;
    _cfgSerializationType = serType;
    
    if ((member instanceof AnnotatedField)) {
      _accessorMethod = null;
      _field = ((Field)member.getMember());
    } else if ((member instanceof AnnotatedMethod)) {
      _accessorMethod = ((Method)member.getMember());
      _field = null;
    }
    else
    {
      _accessorMethod = null;
      _field = null;
    }
    _suppressNulls = suppressNulls;
    _suppressableValue = suppressableValue;
    

    _nullSerializer = null;
    _includeInViews = includeInViews;
  }
  




  @Deprecated
  public BeanPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue)
  {
    this(propDef, member, contextAnnotations, declaredType, ser, typeSer, serType, suppressNulls, suppressableValue, null);
  }
  








  protected BeanPropertyWriter()
  {
    super(PropertyMetadata.STD_REQUIRED_OR_OPTIONAL);
    _member = null;
    _contextAnnotations = null;
    
    _name = null;
    _wrapperName = null;
    _includeInViews = null;
    
    _declaredType = null;
    _serializer = null;
    _dynamicSerializers = null;
    _typeSerializer = null;
    _cfgSerializationType = null;
    
    _accessorMethod = null;
    _field = null;
    _suppressNulls = false;
    _suppressableValue = null;
    
    _nullSerializer = null;
  }
  


  protected BeanPropertyWriter(BeanPropertyWriter base)
  {
    this(base, _name);
  }
  


  protected BeanPropertyWriter(BeanPropertyWriter base, PropertyName name)
  {
    super(base);
    





    _name = new SerializedString(name.getSimpleName());
    _wrapperName = _wrapperName;
    
    _contextAnnotations = _contextAnnotations;
    _declaredType = _declaredType;
    
    _member = _member;
    _accessorMethod = _accessorMethod;
    _field = _field;
    
    _serializer = _serializer;
    _nullSerializer = _nullSerializer;
    
    if (_internalSettings != null) {
      _internalSettings = new HashMap(_internalSettings);
    }
    
    _cfgSerializationType = _cfgSerializationType;
    _dynamicSerializers = _dynamicSerializers;
    _suppressNulls = _suppressNulls;
    _suppressableValue = _suppressableValue;
    _includeInViews = _includeInViews;
    _typeSerializer = _typeSerializer;
    _nonTrivialBaseType = _nonTrivialBaseType;
  }
  
  protected BeanPropertyWriter(BeanPropertyWriter base, SerializedString name) {
    super(base);
    _name = name;
    _wrapperName = _wrapperName;
    
    _member = _member;
    _contextAnnotations = _contextAnnotations;
    _declaredType = _declaredType;
    _accessorMethod = _accessorMethod;
    _field = _field;
    _serializer = _serializer;
    _nullSerializer = _nullSerializer;
    if (_internalSettings != null) {
      _internalSettings = new HashMap(_internalSettings);
    }
    
    _cfgSerializationType = _cfgSerializationType;
    _dynamicSerializers = _dynamicSerializers;
    _suppressNulls = _suppressNulls;
    _suppressableValue = _suppressableValue;
    _includeInViews = _includeInViews;
    _typeSerializer = _typeSerializer;
    _nonTrivialBaseType = _nonTrivialBaseType;
  }
  
  public BeanPropertyWriter rename(NameTransformer transformer) {
    String newName = transformer.transform(_name.getValue());
    if (newName.equals(_name.toString())) {
      return this;
    }
    return _new(PropertyName.construct(newName));
  }
  




  protected BeanPropertyWriter _new(PropertyName newName)
  {
    return new BeanPropertyWriter(this, newName);
  }
  





  public void assignTypeSerializer(TypeSerializer typeSer)
  {
    _typeSerializer = typeSer;
  }
  



  public void assignSerializer(JsonSerializer<Object> ser)
  {
    if ((_serializer != null) && (_serializer != ser)) {
      throw new IllegalStateException(String.format("Cannot override _serializer: had a %s, trying to set to %s", new Object[] {
      
        ClassUtil.classNameOf(_serializer), ClassUtil.classNameOf(ser) }));
    }
    _serializer = ser;
  }
  



  public void assignNullSerializer(JsonSerializer<Object> nullSer)
  {
    if ((_nullSerializer != null) && (_nullSerializer != nullSer)) {
      throw new IllegalStateException(String.format("Cannot override _nullSerializer: had a %s, trying to set to %s", new Object[] {
      
        ClassUtil.classNameOf(_nullSerializer), ClassUtil.classNameOf(nullSer) }));
    }
    _nullSerializer = nullSer;
  }
  



  public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper)
  {
    return new UnwrappingBeanPropertyWriter(this, unwrapper);
  }
  




  public void setNonTrivialBaseType(JavaType t)
  {
    _nonTrivialBaseType = t;
  }
  






  public void fixAccess(SerializationConfig config)
  {
    _member.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
  }
  










  Object readResolve()
  {
    if ((_member instanceof AnnotatedField)) {
      _accessorMethod = null;
      _field = ((Field)_member.getMember());
    } else if ((_member instanceof AnnotatedMethod)) {
      _accessorMethod = ((Method)_member.getMember());
      _field = null;
    }
    if (_serializer == null) {
      _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }
    return this;
  }
  







  public String getName()
  {
    return _name.getValue();
  }
  

  public PropertyName getFullName()
  {
    return new PropertyName(_name.getValue());
  }
  
  public JavaType getType()
  {
    return _declaredType;
  }
  
  public PropertyName getWrapperName()
  {
    return _wrapperName;
  }
  

  public <A extends Annotation> A getAnnotation(Class<A> acls)
  {
    return _member == null ? null : _member.getAnnotation(acls);
  }
  

  public <A extends Annotation> A getContextAnnotation(Class<A> acls)
  {
    return _contextAnnotations == null ? null : _contextAnnotations
      .get(acls);
  }
  
  public AnnotatedMember getMember()
  {
    return _member;
  }
  

  protected void _depositSchemaProperty(ObjectNode propertiesNode, JsonNode schemaNode)
  {
    propertiesNode.set(getName(), schemaNode);
  }
  











  public Object getInternalSetting(Object key)
  {
    return _internalSettings == null ? null : _internalSettings.get(key);
  }
  




  public Object setInternalSetting(Object key, Object value)
  {
    if (_internalSettings == null) {
      _internalSettings = new HashMap();
    }
    return _internalSettings.put(key, value);
  }
  




  public Object removeInternalSetting(Object key)
  {
    Object removed = null;
    if (_internalSettings != null) {
      removed = _internalSettings.remove(key);
      
      if (_internalSettings.size() == 0) {
        _internalSettings = null;
      }
    }
    return removed;
  }
  





  public SerializableString getSerializedName()
  {
    return _name;
  }
  
  public boolean hasSerializer() {
    return _serializer != null;
  }
  
  public boolean hasNullSerializer() {
    return _nullSerializer != null;
  }
  


  public TypeSerializer getTypeSerializer()
  {
    return _typeSerializer;
  }
  









  public boolean isUnwrapping()
  {
    return false;
  }
  
  public boolean willSuppressNulls() {
    return _suppressNulls;
  }
  





  public boolean wouldConflictWithName(PropertyName name)
  {
    if (_wrapperName != null) {
      return _wrapperName.equals(name);
    }
    
    return (name.hasSimpleName(_name.getValue())) && (!name.hasNamespace());
  }
  
  public JsonSerializer<Object> getSerializer()
  {
    return _serializer;
  }
  
  public JavaType getSerializationType() {
    return _cfgSerializationType;
  }
  
  @Deprecated
  public Class<?> getRawSerializationType() {
    return _cfgSerializationType == null ? null : _cfgSerializationType
      .getRawClass();
  }
  


  @Deprecated
  public Class<?> getPropertyType()
  {
    if (_accessorMethod != null) {
      return _accessorMethod.getReturnType();
    }
    if (_field != null) {
      return _field.getType();
    }
    return null;
  }
  






  @Deprecated
  public Type getGenericPropertyType()
  {
    if (_accessorMethod != null) {
      return _accessorMethod.getGenericReturnType();
    }
    if (_field != null) {
      return _field.getGenericType();
    }
    return null;
  }
  
  public Class<?>[] getViews() {
    return _includeInViews;
  }
  













  public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
    throws Exception
  {
    Object value = _accessorMethod == null ? _field.get(bean) : _accessorMethod.invoke(bean, (Object[])null);
    

    if (value == null) {
      if (_nullSerializer != null) {
        gen.writeFieldName(_name);
        _nullSerializer.serialize(null, gen, prov);
      }
      return;
    }
    
    JsonSerializer<Object> ser = _serializer;
    if (ser == null) {
      Class<?> cls = value.getClass();
      PropertySerializerMap m = _dynamicSerializers;
      ser = m.serializerFor(cls);
      if (ser == null) {
        ser = _findAndAddDynamic(m, cls, prov);
      }
    }
    
    if (_suppressableValue != null) {
      if (MARKER_FOR_EMPTY == _suppressableValue) {
        if (!ser.isEmpty(prov, value)) {}

      }
      else if (_suppressableValue.equals(value)) {
        return;
      }
    }
    
    if (value == bean)
    {
      if (_handleSelfReference(bean, gen, prov, ser)) {
        return;
      }
    }
    gen.writeFieldName(_name);
    if (_typeSerializer == null) {
      ser.serialize(value, gen, prov);
    } else {
      ser.serializeWithType(value, gen, prov, _typeSerializer);
    }
  }
  







  public void serializeAsOmittedField(Object bean, JsonGenerator gen, SerializerProvider prov)
    throws Exception
  {
    if (!gen.canOmitFields()) {
      gen.writeOmittedField(_name.getValue());
    }
  }
  









  public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov)
    throws Exception
  {
    Object value = _accessorMethod == null ? _field.get(bean) : _accessorMethod.invoke(bean, (Object[])null);
    if (value == null) {
      if (_nullSerializer != null) {
        _nullSerializer.serialize(null, gen, prov);
      } else {
        gen.writeNull();
      }
      return;
    }
    
    JsonSerializer<Object> ser = _serializer;
    if (ser == null) {
      Class<?> cls = value.getClass();
      PropertySerializerMap map = _dynamicSerializers;
      ser = map.serializerFor(cls);
      if (ser == null) {
        ser = _findAndAddDynamic(map, cls, prov);
      }
    }
    
    if (_suppressableValue != null) {
      if (MARKER_FOR_EMPTY == _suppressableValue) {
        if (ser.isEmpty(prov, value))
        {
          serializeAsPlaceholder(bean, gen, prov);
        }
      }
      else if (_suppressableValue.equals(value))
      {

        serializeAsPlaceholder(bean, gen, prov);
        return;
      }
    }
    
    if ((value == bean) && 
      (_handleSelfReference(bean, gen, prov, ser))) {
      return;
    }
    
    if (_typeSerializer == null) {
      ser.serialize(value, gen, prov);
    } else {
      ser.serializeWithType(value, gen, prov, _typeSerializer);
    }
  }
  








  public void serializeAsPlaceholder(Object bean, JsonGenerator gen, SerializerProvider prov)
    throws Exception
  {
    if (_nullSerializer != null) {
      _nullSerializer.serialize(null, gen, prov);
    } else {
      gen.writeNull();
    }
  }
  







  public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider)
    throws JsonMappingException
  {
    if (v != null) {
      if (isRequired()) {
        v.property(this);
      } else {
        v.optionalProperty(this);
      }
    }
  }
  













  @Deprecated
  public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider provider)
    throws JsonMappingException
  {
    JavaType propType = getSerializationType();
    

    Type hint = (Type)(propType == null ? getType() : propType.getRawClass());
    

    JsonSerializer<Object> ser = getSerializer();
    if (ser == null) {
      ser = provider.findValueSerializer(getType(), this);
    }
    boolean isOptional = !isRequired();
    JsonNode schemaNode; JsonNode schemaNode; if ((ser instanceof SchemaAware)) {
      schemaNode = ((SchemaAware)ser).getSchema(provider, hint, isOptional);
    }
    else
    {
      schemaNode = JsonSchema.getDefaultSchemaNode();
    }
    _depositSchemaProperty(propertiesNode, schemaNode);
  }
  


  protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
    throws JsonMappingException
  {
    PropertySerializerMap.SerializerAndMapResult result;
    

    PropertySerializerMap.SerializerAndMapResult result;
    
    if (_nonTrivialBaseType != null) {
      JavaType t = provider.constructSpecializedType(_nonTrivialBaseType, type);
      
      result = map.findAndAddPrimarySerializer(t, provider, this);
    } else {
      result = map.findAndAddPrimarySerializer(type, provider, this);
    }
    
    if (map != map) {
      _dynamicSerializers = map;
    }
    return serializer;
  }
  






  public final Object get(Object bean)
    throws Exception
  {
    return _accessorMethod == null ? _field.get(bean) : _accessorMethod
      .invoke(bean, (Object[])null);
  }
  














  protected boolean _handleSelfReference(Object bean, JsonGenerator gen, SerializerProvider prov, JsonSerializer<?> ser)
    throws JsonMappingException
  {
    if ((prov.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES)) && 
      (!ser.usesObjectId()))
    {




      if ((ser instanceof BeanSerializerBase)) {
        prov.reportBadDefinition(getType(), "Direct self-reference leading to cycle");
      }
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder(40);
    sb.append("property '").append(getName()).append("' (");
    if (_accessorMethod != null)
    {

      sb.append("via method ").append(_accessorMethod.getDeclaringClass().getName()).append("#").append(_accessorMethod.getName());
    } else if (_field != null)
    {
      sb.append("field \"").append(_field.getDeclaringClass().getName()).append("#").append(_field.getName());
    } else {
      sb.append("virtual");
    }
    if (_serializer == null) {
      sb.append(", no static serializer");
    } else {
      sb.append(", static serializer of type " + _serializer
        .getClass().getName());
    }
    sb.append(')');
    return sb.toString();
  }
}
