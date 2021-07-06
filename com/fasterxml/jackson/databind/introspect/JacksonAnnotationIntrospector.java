package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Attr;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.ext.Java7Support;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Field;
import java.util.List;

public class JacksonAnnotationIntrospector extends com.fasterxml.jackson.databind.AnnotationIntrospector implements java.io.Serializable
{
  private static final long serialVersionUID = 1L;
  private static final Class<? extends java.lang.annotation.Annotation>[] ANNOTATIONS_TO_INFER_SER = (Class[])new Class[] { JsonSerialize.class, JsonView.class, com.fasterxml.jackson.annotation.JsonFormat.class, JsonTypeInfo.class, com.fasterxml.jackson.annotation.JsonRawValue.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class };
  











  private static final Class<? extends java.lang.annotation.Annotation>[] ANNOTATIONS_TO_INFER_DESER = (Class[])new Class[] { JsonDeserialize.class, JsonView.class, com.fasterxml.jackson.annotation.JsonFormat.class, JsonTypeInfo.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class, com.fasterxml.jackson.annotation.JsonMerge.class };
  





  private static final Java7Support _java7Helper;
  





  static
  {
    Java7Support x = null;
    try {
      x = Java7Support.instance();
    } catch (Throwable localThrowable) {}
    _java7Helper = x;
  }
  









  protected transient com.fasterxml.jackson.databind.util.LRUMap<Class<?>, Boolean> _annotationsInside = new com.fasterxml.jackson.databind.util.LRUMap(48, 48);
  














  protected boolean _cfgConstructorPropertiesImpliesCreator = true;
  








  public com.fasterxml.jackson.core.Version version()
  {
    return com.fasterxml.jackson.databind.cfg.PackageVersion.VERSION;
  }
  
  protected Object readResolve() {
    if (_annotationsInside == null) {
      _annotationsInside = new com.fasterxml.jackson.databind.util.LRUMap(48, 48);
    }
    return this;
  }
  
















  public JacksonAnnotationIntrospector setConstructorPropertiesImpliesCreator(boolean b)
  {
    _cfgConstructorPropertiesImpliesCreator = b;
    return this;
  }
  














  public boolean isAnnotationBundle(java.lang.annotation.Annotation ann)
  {
    Class<?> type = ann.annotationType();
    Boolean b = (Boolean)_annotationsInside.get(type);
    if (b == null) {
      b = Boolean.valueOf(type.getAnnotation(com.fasterxml.jackson.annotation.JacksonAnnotationsInside.class) != null);
      _annotationsInside.putIfAbsent(type, b);
    }
    return b.booleanValue();
  }
  














  @Deprecated
  public String findEnumValue(Enum<?> value)
  {
    try
    {
      Field f = value.getClass().getField(value.name());
      if (f != null) {
        JsonProperty prop = (JsonProperty)f.getAnnotation(JsonProperty.class);
        if (prop != null) {
          String n = prop.value();
          if ((n != null) && (!n.isEmpty())) {
            return n;
          }
        }
      }
    }
    catch (SecurityException localSecurityException) {}catch (NoSuchFieldException localNoSuchFieldException) {}
    


    return value.name();
  }
  
  public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names)
  {
    java.util.HashMap<String, String> expl = null;
    for (Field f : ClassUtil.getDeclaredFields(enumType))
      if (f.isEnumConstant())
      {

        JsonProperty prop = (JsonProperty)f.getAnnotation(JsonProperty.class);
        if (prop != null)
        {

          String n = prop.value();
          if (!n.isEmpty())
          {

            if (expl == null) {
              expl = new java.util.HashMap();
            }
            expl.put(f.getName(), n);
          }
        } }
    if (expl != null) {
      int i = 0; for (int end = enumValues.length; i < end; i++) {
        String defName = enumValues[i].name();
        String explValue = (String)expl.get(defName);
        if (explValue != null) {
          names[i] = explValue;
        }
      }
    }
    return names;
  }
  









  public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls)
  {
    return ClassUtil.findFirstAnnotatedEnumValue(enumCls, com.fasterxml.jackson.annotation.JsonEnumDefaultValue.class);
  }
  







  public PropertyName findRootName(AnnotatedClass ac)
  {
    com.fasterxml.jackson.annotation.JsonRootName ann = (com.fasterxml.jackson.annotation.JsonRootName)_findAnnotation(ac, com.fasterxml.jackson.annotation.JsonRootName.class);
    if (ann == null) {
      return null;
    }
    String ns = ann.namespace();
    if ((ns != null) && (ns.length() == 0)) {
      ns = null;
    }
    return PropertyName.construct(ann.value(), ns);
  }
  

  public com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value findPropertyIgnorals(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonIgnoreProperties v = (com.fasterxml.jackson.annotation.JsonIgnoreProperties)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonIgnoreProperties.class);
    if (v == null) {
      return com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value.empty();
    }
    return com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value.from(v);
  }
  
  public Boolean isIgnorableType(AnnotatedClass ac)
  {
    com.fasterxml.jackson.annotation.JsonIgnoreType ignore = (com.fasterxml.jackson.annotation.JsonIgnoreType)_findAnnotation(ac, com.fasterxml.jackson.annotation.JsonIgnoreType.class);
    return ignore == null ? null : Boolean.valueOf(ignore.value());
  }
  
  public Object findFilterId(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonFilter ann = (com.fasterxml.jackson.annotation.JsonFilter)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonFilter.class);
    if (ann != null) {
      String id = ann.value();
      
      if (id.length() > 0) {
        return id;
      }
    }
    return null;
  }
  

  public Object findNamingStrategy(AnnotatedClass ac)
  {
    com.fasterxml.jackson.databind.annotation.JsonNaming ann = (com.fasterxml.jackson.databind.annotation.JsonNaming)_findAnnotation(ac, com.fasterxml.jackson.databind.annotation.JsonNaming.class);
    return ann == null ? null : ann.value();
  }
  
  public String findClassDescription(AnnotatedClass ac)
  {
    com.fasterxml.jackson.annotation.JsonClassDescription ann = (com.fasterxml.jackson.annotation.JsonClassDescription)_findAnnotation(ac, com.fasterxml.jackson.annotation.JsonClassDescription.class);
    return ann == null ? null : ann.value();
  }
  








  public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker)
  {
    com.fasterxml.jackson.annotation.JsonAutoDetect ann = (com.fasterxml.jackson.annotation.JsonAutoDetect)_findAnnotation(ac, com.fasterxml.jackson.annotation.JsonAutoDetect.class);
    return ann == null ? checker : checker.with(ann);
  }
  






  public String findImplicitPropertyName(AnnotatedMember m)
  {
    PropertyName n = _findConstructorName(m);
    return n == null ? null : n.getSimpleName();
  }
  
  public List<PropertyName> findPropertyAliases(Annotated m)
  {
    com.fasterxml.jackson.annotation.JsonAlias ann = (com.fasterxml.jackson.annotation.JsonAlias)_findAnnotation(m, com.fasterxml.jackson.annotation.JsonAlias.class);
    if (ann == null) {
      return null;
    }
    String[] strs = ann.value();
    int len = strs.length;
    if (len == 0) {
      return java.util.Collections.emptyList();
    }
    List<PropertyName> result = new java.util.ArrayList(len);
    for (int i = 0; i < len; i++) {
      result.add(PropertyName.construct(strs[i]));
    }
    return result;
  }
  
  public boolean hasIgnoreMarker(AnnotatedMember m)
  {
    return _isIgnorable(m);
  }
  

  public Boolean hasRequiredMarker(AnnotatedMember m)
  {
    JsonProperty ann = (JsonProperty)_findAnnotation(m, JsonProperty.class);
    if (ann != null) {
      return Boolean.valueOf(ann.required());
    }
    return null;
  }
  
  public com.fasterxml.jackson.annotation.JsonProperty.Access findPropertyAccess(Annotated m)
  {
    JsonProperty ann = (JsonProperty)_findAnnotation(m, JsonProperty.class);
    if (ann != null) {
      return ann.access();
    }
    return null;
  }
  
  public String findPropertyDescription(Annotated ann)
  {
    com.fasterxml.jackson.annotation.JsonPropertyDescription desc = (com.fasterxml.jackson.annotation.JsonPropertyDescription)_findAnnotation(ann, com.fasterxml.jackson.annotation.JsonPropertyDescription.class);
    return desc == null ? null : desc.value();
  }
  
  public Integer findPropertyIndex(Annotated ann)
  {
    JsonProperty prop = (JsonProperty)_findAnnotation(ann, JsonProperty.class);
    if (prop != null) {
      int ix = prop.index();
      if (ix != -1) {
        return Integer.valueOf(ix);
      }
    }
    return null;
  }
  
  public String findPropertyDefaultValue(Annotated ann)
  {
    JsonProperty prop = (JsonProperty)_findAnnotation(ann, JsonProperty.class);
    if (prop == null) {
      return null;
    }
    String str = prop.defaultValue();
    
    return str.isEmpty() ? null : str;
  }
  
  public com.fasterxml.jackson.annotation.JsonFormat.Value findFormat(Annotated ann)
  {
    com.fasterxml.jackson.annotation.JsonFormat f = (com.fasterxml.jackson.annotation.JsonFormat)_findAnnotation(ann, com.fasterxml.jackson.annotation.JsonFormat.class);
    return f == null ? null : new com.fasterxml.jackson.annotation.JsonFormat.Value(f);
  }
  

  public com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member)
  {
    JsonManagedReference ref1 = (JsonManagedReference)_findAnnotation(member, JsonManagedReference.class);
    if (ref1 != null) {
      return com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.managed(ref1.value());
    }
    JsonBackReference ref2 = (JsonBackReference)_findAnnotation(member, JsonBackReference.class);
    if (ref2 != null) {
      return com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.back(ref2.value());
    }
    return null;
  }
  

  public com.fasterxml.jackson.databind.util.NameTransformer findUnwrappingNameTransformer(AnnotatedMember member)
  {
    JsonUnwrapped ann = (JsonUnwrapped)_findAnnotation(member, JsonUnwrapped.class);
    

    if ((ann == null) || (!ann.enabled())) {
      return null;
    }
    String prefix = ann.prefix();
    String suffix = ann.suffix();
    return com.fasterxml.jackson.databind.util.NameTransformer.simpleTransformer(prefix, suffix);
  }
  
  public JacksonInject.Value findInjectableValue(AnnotatedMember m)
  {
    com.fasterxml.jackson.annotation.JacksonInject ann = (com.fasterxml.jackson.annotation.JacksonInject)_findAnnotation(m, com.fasterxml.jackson.annotation.JacksonInject.class);
    if (ann == null) {
      return null;
    }
    
    JacksonInject.Value v = JacksonInject.Value.from(ann);
    if (!v.hasId()) {
      Object id;
      Object id;
      if (!(m instanceof AnnotatedMethod)) {
        id = m.getRawType().getName();
      } else {
        AnnotatedMethod am = (AnnotatedMethod)m;
        Object id; if (am.getParameterCount() == 0) {
          id = m.getRawType().getName();
        } else {
          id = am.getRawParameterType(0).getName();
        }
      }
      v = v.withId(id);
    }
    return v;
  }
  
  @Deprecated
  public Object findInjectableValueId(AnnotatedMember m)
  {
    JacksonInject.Value v = findInjectableValue(m);
    return v == null ? null : v.getId();
  }
  

  public Class<?>[] findViews(Annotated a)
  {
    JsonView ann = (JsonView)_findAnnotation(a, JsonView.class);
    return ann == null ? null : ann.value();
  }
  


  public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2)
  {
    Class<?> cls1 = setter1.getRawParameterType(0);
    Class<?> cls2 = setter2.getRawParameterType(0);
    


    if (cls1.isPrimitive()) {
      if (!cls2.isPrimitive()) {
        return setter1;
      }
    } else if (cls2.isPrimitive()) {
      return setter2;
    }
    
    if (cls1 == String.class) {
      if (cls2 != String.class) {
        return setter1;
      }
    } else if (cls2 == String.class) {
      return setter2;
    }
    
    return null;
  }
  








  public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType)
  {
    return _findTypeResolver(config, ac, baseType);
  }
  






  public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType)
  {
    if ((baseType.isContainerType()) || (baseType.isReferenceType())) {
      return null;
    }
    
    return _findTypeResolver(config, am, baseType);
  }
  





  public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType)
  {
    if (containerType.getContentType() == null) {
      throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
    }
    return _findTypeResolver(config, am, containerType);
  }
  

  public List<com.fasterxml.jackson.databind.jsontype.NamedType> findSubtypes(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonSubTypes t = (com.fasterxml.jackson.annotation.JsonSubTypes)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonSubTypes.class);
    if (t == null) return null;
    com.fasterxml.jackson.annotation.JsonSubTypes.Type[] types = t.value();
    java.util.ArrayList<com.fasterxml.jackson.databind.jsontype.NamedType> result = new java.util.ArrayList(types.length);
    for (com.fasterxml.jackson.annotation.JsonSubTypes.Type type : types) {
      result.add(new com.fasterxml.jackson.databind.jsontype.NamedType(type.value(), type.name()));
    }
    return result;
  }
  

  public String findTypeName(AnnotatedClass ac)
  {
    com.fasterxml.jackson.annotation.JsonTypeName tn = (com.fasterxml.jackson.annotation.JsonTypeName)_findAnnotation(ac, com.fasterxml.jackson.annotation.JsonTypeName.class);
    return tn == null ? null : tn.value();
  }
  
  public Boolean isTypeId(AnnotatedMember member)
  {
    return Boolean.valueOf(_hasAnnotation(member, com.fasterxml.jackson.annotation.JsonTypeId.class));
  }
  






  public ObjectIdInfo findObjectIdInfo(Annotated ann)
  {
    JsonIdentityInfo info = (JsonIdentityInfo)_findAnnotation(ann, JsonIdentityInfo.class);
    if ((info == null) || (info.generator() == com.fasterxml.jackson.annotation.ObjectIdGenerators.None.class)) {
      return null;
    }
    
    PropertyName name = PropertyName.construct(info.property());
    return new ObjectIdInfo(name, info.scope(), info.generator(), info.resolver());
  }
  
  public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo)
  {
    com.fasterxml.jackson.annotation.JsonIdentityReference ref = (com.fasterxml.jackson.annotation.JsonIdentityReference)_findAnnotation(ann, com.fasterxml.jackson.annotation.JsonIdentityReference.class);
    if (ref == null) {
      return objectIdInfo;
    }
    if (objectIdInfo == null) {
      objectIdInfo = ObjectIdInfo.empty();
    }
    return objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
  }
  







  public Object findSerializer(Annotated a)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    if (ann != null)
    {
      Class<? extends com.fasterxml.jackson.databind.JsonSerializer> serClass = ann.using();
      if (serClass != com.fasterxml.jackson.databind.JsonSerializer.None.class) {
        return serClass;
      }
    }
    




    com.fasterxml.jackson.annotation.JsonRawValue annRaw = (com.fasterxml.jackson.annotation.JsonRawValue)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonRawValue.class);
    if ((annRaw != null) && (annRaw.value()))
    {
      Class<?> cls = a.getRawType();
      return new com.fasterxml.jackson.databind.ser.std.RawSerializer(cls);
    }
    return null;
  }
  

  public Object findKeySerializer(Annotated a)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    if (ann != null)
    {
      Class<? extends com.fasterxml.jackson.databind.JsonSerializer> serClass = ann.keyUsing();
      if (serClass != com.fasterxml.jackson.databind.JsonSerializer.None.class) {
        return serClass;
      }
    }
    return null;
  }
  

  public Object findContentSerializer(Annotated a)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    if (ann != null)
    {
      Class<? extends com.fasterxml.jackson.databind.JsonSerializer> serClass = ann.contentUsing();
      if (serClass != com.fasterxml.jackson.databind.JsonSerializer.None.class) {
        return serClass;
      }
    }
    return null;
  }
  

  public Object findNullSerializer(Annotated a)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    if (ann != null)
    {
      Class<? extends com.fasterxml.jackson.databind.JsonSerializer> serClass = ann.nullsUsing();
      if (serClass != com.fasterxml.jackson.databind.JsonSerializer.None.class) {
        return serClass;
      }
    }
    return null;
  }
  

  public JsonInclude.Value findPropertyInclusion(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonInclude inc = (com.fasterxml.jackson.annotation.JsonInclude)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonInclude.class);
    JsonInclude.Value value = inc == null ? JsonInclude.Value.empty() : JsonInclude.Value.from(inc);
    

    if (value.getValueInclusion() == com.fasterxml.jackson.annotation.JsonInclude.Include.USE_DEFAULTS) {
      value = _refinePropertyInclusion(a, value);
    }
    return value;
  }
  
  private JsonInclude.Value _refinePropertyInclusion(Annotated a, JsonInclude.Value value)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    if (ann != null) {
      switch (1.$SwitchMap$com$fasterxml$jackson$databind$annotation$JsonSerialize$Inclusion[ann.include().ordinal()]) {
      case 1: 
        return value.withValueInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS);
      case 2: 
        return value.withValueInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
      case 3: 
        return value.withValueInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT);
      case 4: 
        return value.withValueInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY);
      }
      
    }
    
    return value;
  }
  

  public com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing findSerializationTyping(Annotated a)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    return ann == null ? null : ann.typing();
  }
  
  public Object findSerializationConverter(Annotated a)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    return ann == null ? null : _classIfExplicit(ann.converter(), com.fasterxml.jackson.databind.util.Converter.None.class);
  }
  
  public Object findSerializationContentConverter(AnnotatedMember a)
  {
    JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    return ann == null ? null : _classIfExplicit(ann.contentConverter(), com.fasterxml.jackson.databind.util.Converter.None.class);
  }
  







  public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType)
    throws JsonMappingException
  {
    JavaType type = baseType;
    TypeFactory tf = config.getTypeFactory();
    
    JsonSerialize jsonSer = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
    


    Class<?> serClass = jsonSer == null ? null : _classIfExplicit(jsonSer.as());
    if (serClass != null) {
      if (type.hasRawClass(serClass))
      {

        type = type.withStaticTyping();
      } else {
        Class<?> currRaw = type.getRawClass();
        
        try
        {
          if (serClass.isAssignableFrom(currRaw)) {
            type = tf.constructGeneralizedType(type, serClass);
          } else if (currRaw.isAssignableFrom(serClass)) {
            type = tf.constructSpecializedType(type, serClass);
          } else if (_primitiveAndWrapper(currRaw, serClass))
          {
            type = type.withStaticTyping();
          }
          else {
            throw new JsonMappingException(null, String.format("Cannot refine serialization type %s into %s; types not related", new Object[] { type, serClass
              .getName() }));
          }
        }
        catch (IllegalArgumentException iae) {
          throw new JsonMappingException(null, String.format("Failed to widen type %s with annotation (value %s), from '%s': %s", new Object[] { type, serClass
            .getName(), a.getName(), iae.getMessage() }), iae);
        }
      }
    }
    



    if (type.isMapLikeType()) {
      JavaType keyType = type.getKeyType();
      Class<?> keyClass = jsonSer == null ? null : _classIfExplicit(jsonSer.keyAs());
      if (keyClass != null) {
        if (keyType.hasRawClass(keyClass)) {
          keyType = keyType.withStaticTyping();
        } else {
          Class<?> currRaw = keyType.getRawClass();
          

          try
          {
            if (keyClass.isAssignableFrom(currRaw)) {
              keyType = tf.constructGeneralizedType(keyType, keyClass);
            } else if (currRaw.isAssignableFrom(keyClass)) {
              keyType = tf.constructSpecializedType(keyType, keyClass);
            } else if (_primitiveAndWrapper(currRaw, keyClass))
            {
              keyType = keyType.withStaticTyping();
            }
            else {
              throw new JsonMappingException(null, String.format("Cannot refine serialization key type %s into %s; types not related", new Object[] { keyType, keyClass
                .getName() }));
            }
          }
          catch (IllegalArgumentException iae) {
            throw new JsonMappingException(null, String.format("Failed to widen key type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] { type, keyClass
              .getName(), a.getName(), iae.getMessage() }), iae);
          }
        }
        
        type = ((com.fasterxml.jackson.databind.type.MapLikeType)type).withKeyType(keyType);
      }
    }
    
    JavaType contentType = type.getContentType();
    if (contentType != null)
    {
      Class<?> contentClass = jsonSer == null ? null : _classIfExplicit(jsonSer.contentAs());
      if (contentClass != null) {
        if (contentType.hasRawClass(contentClass)) {
          contentType = contentType.withStaticTyping();

        }
        else
        {
          Class<?> currRaw = contentType.getRawClass();
          try {
            if (contentClass.isAssignableFrom(currRaw)) {
              contentType = tf.constructGeneralizedType(contentType, contentClass);
            } else if (currRaw.isAssignableFrom(contentClass)) {
              contentType = tf.constructSpecializedType(contentType, contentClass);
            } else if (_primitiveAndWrapper(currRaw, contentClass))
            {
              contentType = contentType.withStaticTyping();
            }
            else {
              throw new JsonMappingException(null, String.format("Cannot refine serialization content type %s into %s; types not related", new Object[] { contentType, contentClass
                .getName() }));
            }
          }
          catch (IllegalArgumentException iae) {
            throw new JsonMappingException(null, String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] { type, contentClass
              .getName(), a.getName(), iae.getMessage() }), iae);
          }
        }
        
        type = type.withContentType(contentType);
      }
    }
    return type;
  }
  
  @Deprecated
  public Class<?> findSerializationType(Annotated am)
  {
    return null;
  }
  
  @Deprecated
  public Class<?> findSerializationKeyType(Annotated am, JavaType baseType)
  {
    return null;
  }
  
  @Deprecated
  public Class<?> findSerializationContentType(Annotated am, JavaType baseType)
  {
    return null;
  }
  






  public String[] findSerializationPropertyOrder(AnnotatedClass ac)
  {
    JsonPropertyOrder order = (JsonPropertyOrder)_findAnnotation(ac, JsonPropertyOrder.class);
    return order == null ? null : order.value();
  }
  
  public Boolean findSerializationSortAlphabetically(Annotated ann)
  {
    return _findSortAlpha(ann);
  }
  
  private final Boolean _findSortAlpha(Annotated ann) {
    JsonPropertyOrder order = (JsonPropertyOrder)_findAnnotation(ann, JsonPropertyOrder.class);
    

    if ((order != null) && (order.alphabetic())) {
      return Boolean.TRUE;
    }
    return null;
  }
  

  public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties)
  {
    JsonAppend ann = (JsonAppend)_findAnnotation(ac, JsonAppend.class);
    if (ann == null) {
      return;
    }
    boolean prepend = ann.prepend();
    JavaType propType = null;
    

    JsonAppend.Attr[] attrs = ann.attrs();
    int i = 0; for (int len = attrs.length; i < len; i++) {
      if (propType == null) {
        propType = config.constructType(Object.class);
      }
      BeanPropertyWriter bpw = _constructVirtualProperty(attrs[i], config, ac, propType);
      
      if (prepend) {
        properties.add(i, bpw);
      } else {
        properties.add(bpw);
      }
    }
    

    JsonAppend.Prop[] props = ann.props();
    int i = 0; for (int len = props.length; i < len; i++) {
      BeanPropertyWriter bpw = _constructVirtualProperty(props[i], config, ac);
      
      if (prepend) {
        properties.add(i, bpw);
      } else {
        properties.add(bpw);
      }
    }
  }
  

  protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Attr attr, MapperConfig<?> config, AnnotatedClass ac, JavaType type)
  {
    PropertyMetadata metadata = attr.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
    

    String attrName = attr.value();
    

    PropertyName propName = _propertyName(attr.propName(), attr.propNamespace());
    if (!propName.hasSimpleName()) {
      propName = PropertyName.construct(attrName);
    }
    
    AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), attrName, type);
    

    com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition propDef = com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, attr
      .include());
    
    return com.fasterxml.jackson.databind.ser.impl.AttributePropertyWriter.construct(attrName, propDef, ac
      .getAnnotations(), type);
  }
  

  protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Prop prop, MapperConfig<?> config, AnnotatedClass ac)
  {
    PropertyMetadata metadata = prop.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
    
    PropertyName propName = _propertyName(prop.name(), prop.namespace());
    JavaType type = config.constructType(prop.type());
    

    AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), propName.getSimpleName(), type);
    
    com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition propDef = com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, prop
      .include());
    
    Class<?> implClass = prop.value();
    
    com.fasterxml.jackson.databind.cfg.HandlerInstantiator hi = config.getHandlerInstantiator();
    
    com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter bpw = hi == null ? null : hi.virtualPropertyWriterInstance(config, implClass);
    if (bpw == null) {
      bpw = (com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter)ClassUtil.createInstance(implClass, config
        .canOverrideAccessModifiers());
    }
    

    return bpw.withConfig(config, ac, propDef, type);
  }
  







  public PropertyName findNameForSerialization(Annotated a)
  {
    boolean useDefault = false;
    com.fasterxml.jackson.annotation.JsonGetter jg = (com.fasterxml.jackson.annotation.JsonGetter)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonGetter.class);
    if (jg != null) {
      String s = jg.value();
      
      if (!s.isEmpty()) {
        return PropertyName.construct(s);
      }
      useDefault = true;
    }
    JsonProperty pann = (JsonProperty)_findAnnotation(a, JsonProperty.class);
    if (pann != null) {
      return PropertyName.construct(pann.value());
    }
    if ((useDefault) || (_hasOneOf(a, ANNOTATIONS_TO_INFER_SER))) {
      return PropertyName.USE_DEFAULT;
    }
    return null;
  }
  
  public Boolean hasAsValue(Annotated a)
  {
    JsonValue ann = (JsonValue)_findAnnotation(a, JsonValue.class);
    if (ann == null) {
      return null;
    }
    return Boolean.valueOf(ann.value());
  }
  
  public Boolean hasAnyGetter(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonAnyGetter ann = (com.fasterxml.jackson.annotation.JsonAnyGetter)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonAnyGetter.class);
    if (ann == null) {
      return null;
    }
    return Boolean.valueOf(ann.enabled());
  }
  

  @Deprecated
  public boolean hasAnyGetterAnnotation(AnnotatedMethod am)
  {
    return _hasAnnotation(am, com.fasterxml.jackson.annotation.JsonAnyGetter.class);
  }
  
  @Deprecated
  public boolean hasAsValueAnnotation(AnnotatedMethod am)
  {
    JsonValue ann = (JsonValue)_findAnnotation(am, JsonValue.class);
    
    return (ann != null) && (ann.value());
  }
  







  public Object findDeserializer(Annotated a)
  {
    JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
    if (ann != null)
    {
      Class<? extends com.fasterxml.jackson.databind.JsonDeserializer> deserClass = ann.using();
      if (deserClass != com.fasterxml.jackson.databind.JsonDeserializer.None.class) {
        return deserClass;
      }
    }
    return null;
  }
  

  public Object findKeyDeserializer(Annotated a)
  {
    JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
    if (ann != null) {
      Class<? extends com.fasterxml.jackson.databind.KeyDeserializer> deserClass = ann.keyUsing();
      if (deserClass != com.fasterxml.jackson.databind.KeyDeserializer.None.class) {
        return deserClass;
      }
    }
    return null;
  }
  

  public Object findContentDeserializer(Annotated a)
  {
    JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
    if (ann != null)
    {
      Class<? extends com.fasterxml.jackson.databind.JsonDeserializer> deserClass = ann.contentUsing();
      if (deserClass != com.fasterxml.jackson.databind.JsonDeserializer.None.class) {
        return deserClass;
      }
    }
    return null;
  }
  

  public Object findDeserializationConverter(Annotated a)
  {
    JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
    return ann == null ? null : _classIfExplicit(ann.converter(), com.fasterxml.jackson.databind.util.Converter.None.class);
  }
  

  public Object findDeserializationContentConverter(AnnotatedMember a)
  {
    JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
    return ann == null ? null : _classIfExplicit(ann.contentConverter(), com.fasterxml.jackson.databind.util.Converter.None.class);
  }
  







  public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType)
    throws JsonMappingException
  {
    JavaType type = baseType;
    TypeFactory tf = config.getTypeFactory();
    
    JsonDeserialize jsonDeser = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
    

    Class<?> valueClass = jsonDeser == null ? null : _classIfExplicit(jsonDeser.as());
    if ((valueClass != null) && (!type.hasRawClass(valueClass)) && 
      (!_primitiveAndWrapper(type, valueClass))) {
      try {
        type = tf.constructSpecializedType(type, valueClass);
      }
      catch (IllegalArgumentException iae) {
        throw new JsonMappingException(null, String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", new Object[] { type, valueClass
          .getName(), a.getName(), iae.getMessage() }), iae);
      }
    }
    



    if (type.isMapLikeType()) {
      JavaType keyType = type.getKeyType();
      Class<?> keyClass = jsonDeser == null ? null : _classIfExplicit(jsonDeser.keyAs());
      if ((keyClass != null) && 
        (!_primitiveAndWrapper(keyType, keyClass))) {
        try {
          keyType = tf.constructSpecializedType(keyType, keyClass);
          type = ((com.fasterxml.jackson.databind.type.MapLikeType)type).withKeyType(keyType);
        }
        catch (IllegalArgumentException iae) {
          throw new JsonMappingException(null, String.format("Failed to narrow key type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] { type, keyClass
            .getName(), a.getName(), iae.getMessage() }), iae);
        }
      }
    }
    
    JavaType contentType = type.getContentType();
    if (contentType != null)
    {
      Class<?> contentClass = jsonDeser == null ? null : _classIfExplicit(jsonDeser.contentAs());
      if ((contentClass != null) && 
        (!_primitiveAndWrapper(contentType, contentClass))) {
        try {
          contentType = tf.constructSpecializedType(contentType, contentClass);
          type = type.withContentType(contentType);
        }
        catch (IllegalArgumentException iae) {
          throw new JsonMappingException(null, String.format("Failed to narrow value type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] { type, contentClass
            .getName(), a.getName(), iae.getMessage() }), iae);
        }
      }
    }
    
    return type;
  }
  
  @Deprecated
  public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType)
  {
    return null;
  }
  
  @Deprecated
  public Class<?> findDeserializationType(Annotated am, JavaType baseType)
  {
    return null;
  }
  
  @Deprecated
  public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType)
  {
    return null;
  }
  







  public Object findValueInstantiator(AnnotatedClass ac)
  {
    com.fasterxml.jackson.databind.annotation.JsonValueInstantiator ann = (com.fasterxml.jackson.databind.annotation.JsonValueInstantiator)_findAnnotation(ac, com.fasterxml.jackson.databind.annotation.JsonValueInstantiator.class);
    
    return ann == null ? null : ann.value();
  }
  

  public Class<?> findPOJOBuilder(AnnotatedClass ac)
  {
    JsonDeserialize ann = (JsonDeserialize)_findAnnotation(ac, JsonDeserialize.class);
    return ann == null ? null : _classIfExplicit(ann.builder());
  }
  

  public com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac)
  {
    com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder ann = (com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder)_findAnnotation(ac, com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.class);
    return ann == null ? null : new com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value(ann);
  }
  









  public PropertyName findNameForDeserialization(Annotated a)
  {
    boolean useDefault = false;
    JsonSetter js = (JsonSetter)_findAnnotation(a, JsonSetter.class);
    if (js != null) {
      String s = js.value();
      
      if (s.isEmpty()) {
        useDefault = true;
      } else {
        return PropertyName.construct(s);
      }
    }
    JsonProperty pann = (JsonProperty)_findAnnotation(a, JsonProperty.class);
    if (pann != null) {
      return PropertyName.construct(pann.value());
    }
    if ((useDefault) || (_hasOneOf(a, ANNOTATIONS_TO_INFER_DESER))) {
      return PropertyName.USE_DEFAULT;
    }
    return null;
  }
  
  public Boolean hasAnySetter(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonAnySetter ann = (com.fasterxml.jackson.annotation.JsonAnySetter)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonAnySetter.class);
    return ann == null ? null : Boolean.valueOf(ann.enabled());
  }
  
  public com.fasterxml.jackson.annotation.JsonSetter.Value findSetterInfo(Annotated a)
  {
    return com.fasterxml.jackson.annotation.JsonSetter.Value.from((JsonSetter)_findAnnotation(a, JsonSetter.class));
  }
  
  public Boolean findMergeInfo(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonMerge ann = (com.fasterxml.jackson.annotation.JsonMerge)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonMerge.class);
    return ann == null ? null : ann.value().asBoolean();
  }
  
  @Deprecated
  public boolean hasAnySetterAnnotation(AnnotatedMethod am)
  {
    return _hasAnnotation(am, com.fasterxml.jackson.annotation.JsonAnySetter.class);
  }
  




  @Deprecated
  public boolean hasCreatorAnnotation(Annotated a)
  {
    JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
    if (ann != null) {
      return ann.mode() != com.fasterxml.jackson.annotation.JsonCreator.Mode.DISABLED;
    }
    

    if ((_cfgConstructorPropertiesImpliesCreator) && 
      ((a instanceof AnnotatedConstructor)) && 
      (_java7Helper != null)) {
      Boolean b = _java7Helper.hasCreatorAnnotation(a);
      if (b != null) {
        return b.booleanValue();
      }
    }
    

    return false;
  }
  
  @Deprecated
  public com.fasterxml.jackson.annotation.JsonCreator.Mode findCreatorBinding(Annotated a)
  {
    JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
    return ann == null ? null : ann.mode();
  }
  
  public com.fasterxml.jackson.annotation.JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a)
  {
    JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
    if (ann != null) {
      return ann.mode();
    }
    if ((_cfgConstructorPropertiesImpliesCreator) && 
      (config.isEnabled(com.fasterxml.jackson.databind.MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES)))
    {
      if (((a instanceof AnnotatedConstructor)) && 
        (_java7Helper != null)) {
        Boolean b = _java7Helper.hasCreatorAnnotation(a);
        if ((b != null) && (b.booleanValue()))
        {

          return com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;
        }
      }
    }
    
    return null;
  }
  






  protected boolean _isIgnorable(Annotated a)
  {
    com.fasterxml.jackson.annotation.JsonIgnore ann = (com.fasterxml.jackson.annotation.JsonIgnore)_findAnnotation(a, com.fasterxml.jackson.annotation.JsonIgnore.class);
    if (ann != null) {
      return ann.value();
    }
    if (_java7Helper != null) {
      Boolean b = _java7Helper.findTransient(a);
      if (b != null) {
        return b.booleanValue();
      }
    }
    return false;
  }
  
  protected Class<?> _classIfExplicit(Class<?> cls) {
    if ((cls == null) || (ClassUtil.isBogusClass(cls))) {
      return null;
    }
    return cls;
  }
  
  protected Class<?> _classIfExplicit(Class<?> cls, Class<?> implicit) {
    cls = _classIfExplicit(cls);
    return (cls == null) || (cls == implicit) ? null : cls;
  }
  
  protected PropertyName _propertyName(String localName, String namespace) {
    if (localName.isEmpty()) {
      return PropertyName.USE_DEFAULT;
    }
    if ((namespace == null) || (namespace.isEmpty())) {
      return PropertyName.construct(localName);
    }
    return PropertyName.construct(localName, namespace);
  }
  
  protected PropertyName _findConstructorName(Annotated a)
  {
    if ((a instanceof AnnotatedParameter)) {
      AnnotatedParameter p = (AnnotatedParameter)a;
      AnnotatedWithParams ctor = p.getOwner();
      
      if ((ctor != null) && 
        (_java7Helper != null)) {
        PropertyName name = _java7Helper.findConstructorName(p);
        if (name != null) {
          return name;
        }
      }
    }
    
    return null;
  }
  








  protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType)
  {
    JsonTypeInfo info = (JsonTypeInfo)_findAnnotation(ann, JsonTypeInfo.class);
    com.fasterxml.jackson.databind.annotation.JsonTypeResolver resAnn = (com.fasterxml.jackson.databind.annotation.JsonTypeResolver)_findAnnotation(ann, com.fasterxml.jackson.databind.annotation.JsonTypeResolver.class);
    TypeResolverBuilder<?> b;
    if (resAnn != null) {
      if (info == null) {
        return null;
      }
      

      b = config.typeResolverBuilderInstance(ann, resAnn.value());
    } else {
      if (info == null) {
        return null;
      }
      
      if (info.use() == com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NONE) {
        return _constructNoTypeResolverBuilder();
      }
      b = _constructStdTypeResolverBuilder();
    }
    
    com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver idResInfo = (com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver)_findAnnotation(ann, com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver.class);
    
    com.fasterxml.jackson.databind.jsontype.TypeIdResolver idRes = idResInfo == null ? null : config.typeIdResolverInstance(ann, idResInfo.value());
    if (idRes != null) {
      idRes.init(baseType);
    }
    TypeResolverBuilder<?> b = b.init(info.use(), idRes);
    


    com.fasterxml.jackson.annotation.JsonTypeInfo.As inclusion = info.include();
    if ((inclusion == com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY) && ((ann instanceof AnnotatedClass))) {
      inclusion = com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
    }
    b = b.inclusion(inclusion);
    b = b.typeProperty(info.property());
    Class<?> defaultImpl = info.defaultImpl();
    




    if ((defaultImpl != com.fasterxml.jackson.annotation.JsonTypeInfo.None.class) && (!defaultImpl.isAnnotation())) {
      b = b.defaultImpl(defaultImpl);
    }
    b = b.typeIdVisibility(info.visible());
    return b;
  }
  



  protected com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder _constructStdTypeResolverBuilder()
  {
    return new com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder();
  }
  



  protected com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder _constructNoTypeResolverBuilder()
  {
    return com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder.noTypeInfoBuilder();
  }
  
  private boolean _primitiveAndWrapper(Class<?> baseType, Class<?> refinement)
  {
    if (baseType.isPrimitive()) {
      return baseType == ClassUtil.primitiveType(refinement);
    }
    if (refinement.isPrimitive()) {
      return refinement == ClassUtil.primitiveType(baseType);
    }
    return false;
  }
  
  private boolean _primitiveAndWrapper(JavaType baseType, Class<?> refinement)
  {
    if (baseType.isPrimitive()) {
      return baseType.hasRawClass(ClassUtil.primitiveType(refinement));
    }
    if (refinement.isPrimitive()) {
      return refinement == ClassUtil.primitiveType(baseType.getRawClass());
    }
    return false;
  }
  
  public JacksonAnnotationIntrospector() {}
}
