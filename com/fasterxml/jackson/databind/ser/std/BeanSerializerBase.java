package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.AnyGetterWriter;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

public abstract class BeanSerializerBase extends StdSerializer<Object> implements com.fasterxml.jackson.databind.ser.ContextualSerializer, com.fasterxml.jackson.databind.ser.ResolvableSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable, com.fasterxml.jackson.databind.jsonschema.SchemaAware
{
  protected static final PropertyName NAME_FOR_OBJECT_REF = new PropertyName("#object-ref");
  
  protected static final BeanPropertyWriter[] NO_PROPS = new BeanPropertyWriter[0];
  






  protected final JavaType _beanType;
  






  protected final BeanPropertyWriter[] _props;
  






  protected final BeanPropertyWriter[] _filteredProps;
  





  protected final AnyGetterWriter _anyGetterWriter;
  





  protected final Object _propertyFilterId;
  





  protected final AnnotatedMember _typeId;
  





  protected final ObjectIdWriter _objectIdWriter;
  





  protected final JsonFormat.Shape _serializationShape;
  






  protected BeanSerializerBase(JavaType type, BeanSerializerBuilder builder, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties)
  {
    super(type);
    _beanType = type;
    _props = properties;
    _filteredProps = filteredProperties;
    if (builder == null)
    {

      _typeId = null;
      _anyGetterWriter = null;
      _propertyFilterId = null;
      _objectIdWriter = null;
      _serializationShape = null;
    } else {
      _typeId = builder.getTypeId();
      _anyGetterWriter = builder.getAnyGetter();
      _propertyFilterId = builder.getFilterId();
      _objectIdWriter = builder.getObjectIdWriter();
      JsonFormat.Value format = builder.getBeanDescription().findExpectedFormat(null);
      _serializationShape = (format == null ? null : format.getShape());
    }
  }
  

  public BeanSerializerBase(BeanSerializerBase src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties)
  {
    super(_handledType);
    _beanType = _beanType;
    _props = properties;
    _filteredProps = filteredProperties;
    
    _typeId = _typeId;
    _anyGetterWriter = _anyGetterWriter;
    _objectIdWriter = _objectIdWriter;
    _propertyFilterId = _propertyFilterId;
    _serializationShape = _serializationShape;
  }
  

  protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter)
  {
    this(src, objectIdWriter, _propertyFilterId);
  }
  




  protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId)
  {
    super(_handledType);
    _beanType = _beanType;
    _props = _props;
    _filteredProps = _filteredProps;
    
    _typeId = _typeId;
    _anyGetterWriter = _anyGetterWriter;
    _objectIdWriter = objectIdWriter;
    _propertyFilterId = filterId;
    _serializationShape = _serializationShape;
  }
  
  @Deprecated
  protected BeanSerializerBase(BeanSerializerBase src, String[] toIgnore)
  {
    this(src, ArrayBuilders.arrayToSet(toIgnore));
  }
  
  protected BeanSerializerBase(BeanSerializerBase src, Set<String> toIgnore)
  {
    super(_handledType);
    
    _beanType = _beanType;
    BeanPropertyWriter[] propsIn = _props;
    BeanPropertyWriter[] fpropsIn = _filteredProps;
    int len = propsIn.length;
    
    ArrayList<BeanPropertyWriter> propsOut = new ArrayList(len);
    ArrayList<BeanPropertyWriter> fpropsOut = fpropsIn == null ? null : new ArrayList(len);
    
    for (int i = 0; i < len; i++) {
      BeanPropertyWriter bpw = propsIn[i];
      
      if ((toIgnore == null) || (!toIgnore.contains(bpw.getName())))
      {

        propsOut.add(bpw);
        if (fpropsIn != null)
          fpropsOut.add(fpropsIn[i]);
      }
    }
    _props = ((BeanPropertyWriter[])propsOut.toArray(new BeanPropertyWriter[propsOut.size()]));
    _filteredProps = (fpropsOut == null ? null : (BeanPropertyWriter[])fpropsOut.toArray(new BeanPropertyWriter[fpropsOut.size()]));
    
    _typeId = _typeId;
    _anyGetterWriter = _anyGetterWriter;
    _objectIdWriter = _objectIdWriter;
    _propertyFilterId = _propertyFilterId;
    _serializationShape = _serializationShape;
  }
  






  public abstract BeanSerializerBase withObjectIdWriter(ObjectIdWriter paramObjectIdWriter);
  






  protected abstract BeanSerializerBase withIgnorals(Set<String> paramSet);
  





  @Deprecated
  protected BeanSerializerBase withIgnorals(String[] toIgnore)
  {
    return withIgnorals(ArrayBuilders.arrayToSet(toIgnore));
  }
  






  protected abstract BeanSerializerBase asArraySerializer();
  






  public abstract BeanSerializerBase withFilterId(Object paramObject);
  





  protected BeanSerializerBase(BeanSerializerBase src)
  {
    this(src, _props, _filteredProps);
  }
  



  protected BeanSerializerBase(BeanSerializerBase src, NameTransformer unwrapper)
  {
    this(src, rename(_props, unwrapper), rename(_filteredProps, unwrapper));
  }
  

  private static final BeanPropertyWriter[] rename(BeanPropertyWriter[] props, NameTransformer transformer)
  {
    if ((props == null) || (props.length == 0) || (transformer == null) || (transformer == NameTransformer.NOP)) {
      return props;
    }
    int len = props.length;
    BeanPropertyWriter[] result = new BeanPropertyWriter[len];
    for (int i = 0; i < len; i++) {
      BeanPropertyWriter bpw = props[i];
      if (bpw != null) {
        result[i] = bpw.rename(transformer);
      }
    }
    return result;
  }
  











  public void resolve(SerializerProvider provider)
    throws JsonMappingException
  {
    int filteredCount = _filteredProps == null ? 0 : _filteredProps.length;
    int i = 0; for (int len = _props.length; i < len; i++) {
      BeanPropertyWriter prop = _props[i];
      
      if ((!prop.willSuppressNulls()) && (!prop.hasNullSerializer())) {
        JsonSerializer<Object> nullSer = provider.findNullValueSerializer(prop);
        if (nullSer != null) {
          prop.assignNullSerializer(nullSer);
          
          if (i < filteredCount) {
            BeanPropertyWriter w2 = _filteredProps[i];
            if (w2 != null) {
              w2.assignNullSerializer(nullSer);
            }
          }
        }
      }
      
      if (!prop.hasSerializer())
      {


        JsonSerializer<Object> ser = findConvertingSerializer(provider, prop);
        if (ser == null)
        {
          JavaType type = prop.getSerializationType();
          


          if (type == null) {
            type = prop.getType();
            if (!type.isFinal()) {
              if ((!type.isContainerType()) && (type.containedTypeCount() <= 0)) continue;
              prop.setNonTrivialBaseType(type); continue;
            }
          }
          

          ser = provider.findValueSerializer(type, prop);
          


          if (type.isContainerType()) {
            TypeSerializer typeSer = (TypeSerializer)type.getContentType().getTypeHandler();
            if (typeSer != null)
            {
              if ((ser instanceof ContainerSerializer))
              {

                JsonSerializer<Object> ser2 = ((ContainerSerializer)ser).withValueTypeSerializer(typeSer);
                ser = ser2;
              }
            }
          }
        }
        
        if (i < filteredCount) {
          BeanPropertyWriter w2 = _filteredProps[i];
          if (w2 != null) {
            w2.assignSerializer(ser);
            


            continue;
          }
        }
        prop.assignSerializer(ser);
      }
    }
    
    if (_anyGetterWriter != null)
    {
      _anyGetterWriter.resolve(provider);
    }
  }
  








  protected JsonSerializer<Object> findConvertingSerializer(SerializerProvider provider, BeanPropertyWriter prop)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = provider.getAnnotationIntrospector();
    if (intr != null) {
      AnnotatedMember m = prop.getMember();
      if (m != null) {
        Object convDef = intr.findSerializationConverter(m);
        if (convDef != null) {
          Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
          JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
          

          JsonSerializer<?> ser = delegateType.isJavaLangObject() ? null : provider.findValueSerializer(delegateType, prop);
          return new StdDelegatingSerializer(conv, delegateType, ser);
        }
      }
    }
    return null;
  }
  



  public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = provider.getAnnotationIntrospector();
    
    AnnotatedMember accessor = (property == null) || (intr == null) ? null : property.getMember();
    SerializationConfig config = provider.getConfig();
    


    JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
    JsonFormat.Shape shape = null;
    if ((format != null) && (format.hasShape())) {
      shape = format.getShape();
      
      if ((shape != JsonFormat.Shape.ANY) && (shape != _serializationShape)) {
        if (com.fasterxml.jackson.databind.util.ClassUtil.isEnumType(_handledType)) {
          switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonFormat$Shape[shape.ordinal()])
          {

          case 1: 
          case 2: 
          case 3: 
            com.fasterxml.jackson.databind.BeanDescription desc = config.introspectClassAnnotations(_beanType);
            JsonSerializer<?> ser = EnumSerializer.construct(_beanType.getRawClass(), provider
              .getConfig(), desc, format);
            return provider.handlePrimaryContextualization(ser, property);
          }
          
        } else if ((shape == JsonFormat.Shape.NATURAL) && (
          (!_beanType.isMapLikeType()) || (!java.util.Map.class.isAssignableFrom(_handledType))))
        {
          if (java.util.Map.Entry.class.isAssignableFrom(_handledType)) {
            JavaType mapEntryType = _beanType.findSuperType(java.util.Map.Entry.class);
            
            JavaType kt = mapEntryType.containedTypeOrUnknown(0);
            JavaType vt = mapEntryType.containedTypeOrUnknown(1);
            


            JsonSerializer<?> ser = new com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer(_beanType, kt, vt, false, null, property);
            
            return provider.handlePrimaryContextualization(ser, property);
          }
        }
      }
    }
    
    ObjectIdWriter oiw = _objectIdWriter;
    Set<String> ignoredProps = null;
    Object newFilterId = null;
    

    if (accessor != null) {
      JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
      if (ignorals != null) {
        ignoredProps = ignorals.findIgnoredForSerialization();
      }
      ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
      if (objectIdInfo == null)
      {
        if (oiw != null) {
          objectIdInfo = intr.findObjectReferenceInfo(accessor, null);
          if (objectIdInfo != null) {
            oiw = _objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
          }
          
        }
        
      }
      else
      {
        objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
        
        Class<?> implClass = objectIdInfo.getGeneratorType();
        JavaType type = provider.constructType(implClass);
        JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
        
        if (implClass == com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class) {
          String propName = objectIdInfo.getPropertyName().getSimpleName();
          BeanPropertyWriter idProp = null;
          
          int i = 0; for (int len = _props.length;; i++) {
            if (i == len) {
              provider.reportBadDefinition(_beanType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", new Object[] {
              
                handledType().getName(), propName }));
            }
            BeanPropertyWriter prop = _props[i];
            if (propName.equals(prop.getName())) {
              idProp = prop;
              

              if (i <= 0) break;
              System.arraycopy(_props, 0, _props, 1, i);
              _props[0] = idProp;
              if (_filteredProps == null) break;
              BeanPropertyWriter fp = _filteredProps[i];
              System.arraycopy(_filteredProps, 0, _filteredProps, 1, i);
              _filteredProps[0] = fp;
              break;
            }
          }
          

          idType = idProp.getType();
          ObjectIdGenerator<?> gen = new com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
          oiw = ObjectIdWriter.construct(idType, (PropertyName)null, gen, objectIdInfo.getAlwaysAsId());
        } else {
          ObjectIdGenerator<?> gen = provider.objectIdGeneratorInstance(accessor, objectIdInfo);
          oiw = ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo
            .getAlwaysAsId());
        }
      }
      
      Object filterId = intr.findFilterId(accessor);
      if (filterId != null)
      {
        if ((_propertyFilterId == null) || (!filterId.equals(_propertyFilterId))) {
          newFilterId = filterId;
        }
      }
    }
    
    BeanSerializerBase contextual = this;
    if (oiw != null) {
      JsonSerializer<?> ser = provider.findValueSerializer(idType, property);
      oiw = oiw.withSerializer(ser);
      if (oiw != _objectIdWriter) {
        contextual = contextual.withObjectIdWriter(oiw);
      }
    }
    
    if ((ignoredProps != null) && (!ignoredProps.isEmpty())) {
      contextual = contextual.withIgnorals(ignoredProps);
    }
    if (newFilterId != null) {
      contextual = contextual.withFilterId(newFilterId);
    }
    if (shape == null) {
      shape = _serializationShape;
    }
    
    if (shape == JsonFormat.Shape.ARRAY) {
      return contextual.asArraySerializer();
    }
    return contextual;
  }
  






  public java.util.Iterator<com.fasterxml.jackson.databind.ser.PropertyWriter> properties()
  {
    return java.util.Arrays.asList(_props).iterator();
  }
  






  public boolean usesObjectId()
  {
    return _objectIdWriter != null;
  }
  



  public abstract void serialize(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
    throws IOException;
  


  public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    if (_objectIdWriter != null) {
      gen.setCurrentValue(bean);
      _serializeWithObjectId(bean, gen, provider, typeSer);
      return;
    }
    
    gen.setCurrentValue(bean);
    WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
    typeSer.writeTypePrefix(gen, typeIdDef);
    if (_propertyFilterId != null) {
      serializeFieldsFiltered(bean, gen, provider);
    } else {
      serializeFields(bean, gen, provider);
    }
    typeSer.writeTypeSuffix(gen, typeIdDef);
  }
  
  protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, boolean startEndObject)
    throws IOException
  {
    ObjectIdWriter w = _objectIdWriter;
    WritableObjectId objectId = provider.findObjectId(bean, generator);
    
    if (objectId.writeAsId(gen, provider, w)) {
      return;
    }
    
    Object id = objectId.generateId(bean);
    if (alwaysAsId) {
      serializer.serialize(id, gen, provider);
      return;
    }
    if (startEndObject) {
      gen.writeStartObject(bean);
    }
    objectId.writeAsField(gen, provider, w);
    if (_propertyFilterId != null) {
      serializeFieldsFiltered(bean, gen, provider);
    } else {
      serializeFields(bean, gen, provider);
    }
    if (startEndObject) {
      gen.writeEndObject();
    }
  }
  
  protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    ObjectIdWriter w = _objectIdWriter;
    WritableObjectId objectId = provider.findObjectId(bean, generator);
    
    if (objectId.writeAsId(gen, provider, w)) {
      return;
    }
    
    Object id = objectId.generateId(bean);
    if (alwaysAsId) {
      serializer.serialize(id, gen, provider);
      return;
    }
    _serializeObjectId(bean, gen, provider, typeSer, objectId);
  }
  

  protected void _serializeObjectId(Object bean, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer, WritableObjectId objectId)
    throws IOException
  {
    ObjectIdWriter w = _objectIdWriter;
    WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
    
    typeSer.writeTypePrefix(g, typeIdDef);
    objectId.writeAsField(g, provider, w);
    if (_propertyFilterId != null) {
      serializeFieldsFiltered(bean, g, provider);
    } else {
      serializeFields(bean, g, provider);
    }
    typeSer.writeTypeSuffix(g, typeIdDef);
  }
  



  protected final WritableTypeId _typeIdDef(TypeSerializer typeSer, Object bean, JsonToken valueShape)
  {
    if (_typeId == null) {
      return typeSer.typeId(bean, valueShape);
    }
    Object typeId = _typeId.getValue(bean);
    if (typeId == null)
    {
      typeId = "";
    }
    return typeSer.typeId(bean, valueShape, typeId);
  }
  
  @Deprecated
  protected final String _customTypeId(Object bean)
  {
    Object typeId = _typeId.getValue(bean);
    if (typeId == null) {
      return "";
    }
    return (typeId instanceof String) ? (String)typeId : typeId.toString();
  }
  


  protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    BeanPropertyWriter[] props;
    

    BeanPropertyWriter[] props;
    
    if ((_filteredProps != null) && (provider.getActiveView() != null)) {
      props = _filteredProps;
    } else {
      props = _props;
    }
    int i = 0;
    try {
      for (int len = props.length; i < len; i++) {
        BeanPropertyWriter prop = props[i];
        if (prop != null) {
          prop.serializeAsField(bean, gen, provider);
        }
      }
      if (_anyGetterWriter != null) {
        _anyGetterWriter.getAndSerialize(bean, gen, provider);
      }
    } catch (Exception e) {
      String name = i == props.length ? "[anySetter]" : props[i].getName();
      wrapAndThrow(provider, e, bean, name);


    }
    catch (StackOverflowError e)
    {

      JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e);
      
      String name = i == props.length ? "[anySetter]" : props[i].getName();
      mapE.prependPath(new JsonMappingException.Reference(bean, name));
      throw mapE;
    }
  }
  



  protected void serializeFieldsFiltered(Object bean, JsonGenerator gen, SerializerProvider provider)
    throws IOException, com.fasterxml.jackson.core.JsonGenerationException
  {
    BeanPropertyWriter[] props;
    


    BeanPropertyWriter[] props;
    

    if ((_filteredProps != null) && (provider.getActiveView() != null)) {
      props = _filteredProps;
    } else {
      props = _props;
    }
    PropertyFilter filter = findPropertyFilter(provider, _propertyFilterId, bean);
    
    if (filter == null) {
      serializeFields(bean, gen, provider);
      return;
    }
    int i = 0;
    try {
      for (int len = props.length; i < len; i++) {
        BeanPropertyWriter prop = props[i];
        if (prop != null) {
          filter.serializeAsField(bean, gen, provider, prop);
        }
      }
      if (_anyGetterWriter != null) {
        _anyGetterWriter.getAndFilter(bean, gen, provider, filter);
      }
    } catch (Exception e) {
      String name = i == props.length ? "[anySetter]" : props[i].getName();
      wrapAndThrow(provider, e, bean, name);
    }
    catch (StackOverflowError e)
    {
      JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e);
      String name = i == props.length ? "[anySetter]" : props[i].getName();
      mapE.prependPath(new JsonMappingException.Reference(bean, name));
      throw mapE;
    }
  }
  

  @Deprecated
  public com.fasterxml.jackson.databind.JsonNode getSchema(SerializerProvider provider, Type typeHint)
    throws JsonMappingException
  {
    ObjectNode o = createSchemaNode("object", true);
    

    JsonSerializableSchema ann = (JsonSerializableSchema)_handledType.getAnnotation(JsonSerializableSchema.class);
    if (ann != null) {
      String id = ann.id();
      if ((id != null) && (id.length() > 0)) {
        o.put("id", id);
      }
    }
    


    ObjectNode propertiesNode = o.objectNode();
    PropertyFilter filter;
    PropertyFilter filter; if (_propertyFilterId != null) {
      filter = findPropertyFilter(provider, _propertyFilterId, null);
    } else {
      filter = null;
    }
    
    for (int i = 0; i < _props.length; i++) {
      BeanPropertyWriter prop = _props[i];
      if (filter == null) {
        prop.depositSchemaProperty(propertiesNode, provider);
      } else {
        filter.depositSchemaProperty(prop, propertiesNode, provider);
      }
    }
    
    o.set("properties", propertiesNode);
    return o;
  }
  


  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    if (visitor == null) {
      return;
    }
    com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
    if (objectVisitor == null) {
      return;
    }
    SerializerProvider provider = visitor.getProvider();
    if (_propertyFilterId != null) {
      PropertyFilter filter = findPropertyFilter(visitor.getProvider(), _propertyFilterId, null);
      
      int i = 0; for (int end = _props.length; i < end; i++) {
        filter.depositSchemaProperty(_props[i], objectVisitor, provider);
      }
    }
    else {
      Class<?> view = (_filteredProps == null) || (provider == null) ? null : provider.getActiveView();
      BeanPropertyWriter[] props;
      BeanPropertyWriter[] props; if (view != null) {
        props = _filteredProps;
      } else {
        props = _props;
      }
      
      int i = 0; for (int end = props.length; i < end; i++) {
        BeanPropertyWriter prop = props[i];
        if (prop != null) {
          prop.depositSchemaProperty(objectVisitor, provider);
        }
      }
    }
  }
}
