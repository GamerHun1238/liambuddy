package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Value;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.Impl;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.Impl;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;































public class ObjectMapper
  extends ObjectCodec
  implements Versioned, Serializable
{
  private static final long serialVersionUID = 2L;
  
  public static enum DefaultTyping
  {
    JAVA_LANG_OBJECT, 
    








    OBJECT_AND_NON_CONCRETE, 
    







    NON_CONCRETE_AND_ARRAYS, 
    









    NON_FINAL, 
    














    EVERYTHING;
    





    private DefaultTyping() {}
  }
  





  public static class DefaultTypeResolverBuilder
    extends StdTypeResolverBuilder
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    



    protected final ObjectMapper.DefaultTyping _appliesFor;
    



    protected final PolymorphicTypeValidator _subtypeValidator;
    




    @Deprecated
    public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t)
    {
      this(t, LaissezFaireSubTypeValidator.instance);
    }
    


    public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv)
    {
      _appliesFor = ((ObjectMapper.DefaultTyping)Objects.requireNonNull(t, "Can not pass `null` DefaultTyping"));
      _subtypeValidator = ((PolymorphicTypeValidator)Objects.requireNonNull(ptv, "Can not pass `null` PolymorphicTypeValidator"));
    }
    



    public static DefaultTypeResolverBuilder construct(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv)
    {
      return new DefaultTypeResolverBuilder(t, ptv);
    }
    
    public PolymorphicTypeValidator subTypeValidator(MapperConfig<?> config)
    {
      return _subtypeValidator;
    }
    


    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
    {
      return useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
    }
    


    public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
    {
      return useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
    }
    










    public boolean useForType(JavaType t)
    {
      if (t.isPrimitive()) {
        return false;
      }
      
      switch (ObjectMapper.3.$SwitchMap$com$fasterxml$jackson$databind$ObjectMapper$DefaultTyping[_appliesFor.ordinal()]) {
      case 1: 
        while (t.isArrayType()) {
          t = t.getContentType();
        }
      

      case 2: 
        while (t.isReferenceType()) {
          t = t.getReferencedType();
        }
        if (!t.isJavaLangObject()) {
          if (t.isConcrete()) {
            break label116;
          }
        }
        return 
        

          !TreeNode.class.isAssignableFrom(t.getRawClass());
      
      case 3: 
        while (t.isArrayType()) {
          t = t.getContentType();
        }
        
        while (t.isReferenceType()) {
          t = t.getReferencedType();
        }
        
        return (!t.isFinal()) && (!TreeNode.class.isAssignableFrom(t.getRawClass()));
      case 4: 
        label116:
        
        return true;
      }
      
      return t.isJavaLangObject();
    }
  }
  










  protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
  




  protected static final BaseSettings DEFAULT_BASE = new BaseSettings(null, DEFAULT_ANNOTATION_INTROSPECTOR, null, 
  

    TypeFactory.defaultInstance(), null, StdDateFormat.instance, null, 
    
    Locale.getDefault(), null, 
    
    Base64Variants.getDefaultVariant(), LaissezFaireSubTypeValidator.instance);
  










  protected final JsonFactory _jsonFactory;
  










  protected TypeFactory _typeFactory;
  










  protected InjectableValues _injectableValues;
  










  protected SubtypeResolver _subtypeResolver;
  










  protected final ConfigOverrides _configOverrides;
  










  protected SimpleMixInResolver _mixIns;
  










  protected SerializationConfig _serializationConfig;
  










  protected DefaultSerializerProvider _serializerProvider;
  










  protected SerializerFactory _serializerFactory;
  










  protected DeserializationConfig _deserializationConfig;
  










  protected DefaultDeserializationContext _deserializationContext;
  










  protected Set<Object> _registeredModuleTypes;
  









  protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers = new ConcurrentHashMap(64, 0.6F, 2);
  


















  public ObjectMapper()
  {
    this(null, null, null);
  }
  




  public ObjectMapper(JsonFactory jf)
  {
    this(jf, null, null);
  }
  





  protected ObjectMapper(ObjectMapper src)
  {
    _jsonFactory = _jsonFactory.copy();
    _jsonFactory.setCodec(this);
    _subtypeResolver = _subtypeResolver;
    _typeFactory = _typeFactory;
    _injectableValues = _injectableValues;
    _configOverrides = _configOverrides.copy();
    _mixIns = _mixIns.copy();
    
    RootNameLookup rootNames = new RootNameLookup();
    _serializationConfig = new SerializationConfig(_serializationConfig, _mixIns, rootNames, _configOverrides);
    
    _deserializationConfig = new DeserializationConfig(_deserializationConfig, _mixIns, rootNames, _configOverrides);
    
    _serializerProvider = _serializerProvider.copy();
    _deserializationContext = _deserializationContext.copy();
    

    _serializerFactory = _serializerFactory;
    

    Set<Object> reg = _registeredModuleTypes;
    if (reg == null) {
      _registeredModuleTypes = null;
    } else {
      _registeredModuleTypes = new LinkedHashSet(reg);
    }
  }
  



















  public ObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc)
  {
    if (jf == null) {
      _jsonFactory = new MappingJsonFactory(this);
    } else {
      _jsonFactory = jf;
      if (jf.getCodec() == null) {
        _jsonFactory.setCodec(this);
      }
    }
    _subtypeResolver = new StdSubtypeResolver();
    RootNameLookup rootNames = new RootNameLookup();
    
    _typeFactory = TypeFactory.defaultInstance();
    
    SimpleMixInResolver mixins = new SimpleMixInResolver(null);
    _mixIns = mixins;
    BaseSettings base = DEFAULT_BASE.withClassIntrospector(defaultClassIntrospector());
    _configOverrides = new ConfigOverrides();
    _serializationConfig = new SerializationConfig(base, _subtypeResolver, mixins, rootNames, _configOverrides);
    
    _deserializationConfig = new DeserializationConfig(base, _subtypeResolver, mixins, rootNames, _configOverrides);
    


    boolean needOrder = _jsonFactory.requiresPropertyOrdering();
    if ((needOrder ^ _serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY))) {
      configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
    }
    
    _serializerProvider = (sp == null ? new DefaultSerializerProvider.Impl() : sp);
    _deserializationContext = (dc == null ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc);
    


    _serializerFactory = BeanSerializerFactory.instance;
  }
  





  protected ClassIntrospector defaultClassIntrospector()
  {
    return new BasicClassIntrospector();
  }
  




















  public ObjectMapper copy()
  {
    _checkInvalidCopy(ObjectMapper.class);
    return new ObjectMapper(this);
  }
  



  protected void _checkInvalidCopy(Class<?> exp)
  {
    if (getClass() != exp)
    {

      throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to");
    }
  }
  












  protected ObjectReader _newReader(DeserializationConfig config)
  {
    return new ObjectReader(this, config);
  }
  







  protected ObjectReader _newReader(DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues)
  {
    return new ObjectReader(this, config, valueType, valueToUpdate, schema, injectableValues);
  }
  





  protected ObjectWriter _newWriter(SerializationConfig config)
  {
    return new ObjectWriter(this, config);
  }
  





  protected ObjectWriter _newWriter(SerializationConfig config, FormatSchema schema)
  {
    return new ObjectWriter(this, config, schema);
  }
  






  protected ObjectWriter _newWriter(SerializationConfig config, JavaType rootType, PrettyPrinter pp)
  {
    return new ObjectWriter(this, config, rootType, pp);
  }
  










  public Version version()
  {
    return PackageVersion.VERSION;
  }
  













  public ObjectMapper registerModule(Module module)
  {
    _assertNotNull("module", module);
    


    String name = module.getModuleName();
    if (name == null) {
      throw new IllegalArgumentException("Module without defined name");
    }
    Version version = module.version();
    if (version == null) {
      throw new IllegalArgumentException("Module without defined version");
    }
    

    for (Module dep : module.getDependencies()) {
      registerModule(dep);
    }
    

    if (isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)) {
      Object typeId = module.getTypeId();
      if (typeId != null) {
        if (_registeredModuleTypes == null)
        {

          _registeredModuleTypes = new LinkedHashSet();
        }
        
        if (!_registeredModuleTypes.add(typeId)) {
          return this;
        }
      }
    }
    

    module.setupModule(new Module.SetupContext()
    {

      public Version getMapperVersion()
      {

        return version();
      }
      


      public <C extends ObjectCodec> C getOwner()
      {
        return ObjectMapper.this;
      }
      
      public TypeFactory getTypeFactory()
      {
        return _typeFactory;
      }
      
      public boolean isEnabled(MapperFeature f)
      {
        return ObjectMapper.this.isEnabled(f);
      }
      
      public boolean isEnabled(DeserializationFeature f)
      {
        return ObjectMapper.this.isEnabled(f);
      }
      
      public boolean isEnabled(SerializationFeature f)
      {
        return ObjectMapper.this.isEnabled(f);
      }
      
      public boolean isEnabled(JsonFactory.Feature f)
      {
        return ObjectMapper.this.isEnabled(f);
      }
      
      public boolean isEnabled(JsonParser.Feature f)
      {
        return ObjectMapper.this.isEnabled(f);
      }
      
      public boolean isEnabled(JsonGenerator.Feature f)
      {
        return ObjectMapper.this.isEnabled(f);
      }
      


      public MutableConfigOverride configOverride(Class<?> type)
      {
        return ObjectMapper.this.configOverride(type);
      }
      


      public void addDeserializers(Deserializers d)
      {
        DeserializerFactory df = _deserializationContext._factory.withAdditionalDeserializers(d);
        _deserializationContext = _deserializationContext.with(df);
      }
      
      public void addKeyDeserializers(KeyDeserializers d)
      {
        DeserializerFactory df = _deserializationContext._factory.withAdditionalKeyDeserializers(d);
        _deserializationContext = _deserializationContext.with(df);
      }
      
      public void addBeanDeserializerModifier(BeanDeserializerModifier modifier)
      {
        DeserializerFactory df = _deserializationContext._factory.withDeserializerModifier(modifier);
        _deserializationContext = _deserializationContext.with(df);
      }
      


      public void addSerializers(Serializers s)
      {
        _serializerFactory = _serializerFactory.withAdditionalSerializers(s);
      }
      
      public void addKeySerializers(Serializers s)
      {
        _serializerFactory = _serializerFactory.withAdditionalKeySerializers(s);
      }
      
      public void addBeanSerializerModifier(BeanSerializerModifier modifier)
      {
        _serializerFactory = _serializerFactory.withSerializerModifier(modifier);
      }
      


      public void addAbstractTypeResolver(AbstractTypeResolver resolver)
      {
        DeserializerFactory df = _deserializationContext._factory.withAbstractTypeResolver(resolver);
        _deserializationContext = _deserializationContext.with(df);
      }
      
      public void addTypeModifier(TypeModifier modifier)
      {
        TypeFactory f = _typeFactory;
        f = f.withModifier(modifier);
        setTypeFactory(f);
      }
      
      public void addValueInstantiators(ValueInstantiators instantiators)
      {
        DeserializerFactory df = _deserializationContext._factory.withValueInstantiators(instantiators);
        _deserializationContext = _deserializationContext.with(df);
      }
      
      public void setClassIntrospector(ClassIntrospector ci)
      {
        _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(ci));
        _serializationConfig = ((SerializationConfig)_serializationConfig.with(ci));
      }
      
      public void insertAnnotationIntrospector(AnnotationIntrospector ai)
      {
        _deserializationConfig = ((DeserializationConfig)_deserializationConfig.withInsertedAnnotationIntrospector(ai));
        _serializationConfig = ((SerializationConfig)_serializationConfig.withInsertedAnnotationIntrospector(ai));
      }
      
      public void appendAnnotationIntrospector(AnnotationIntrospector ai)
      {
        _deserializationConfig = ((DeserializationConfig)_deserializationConfig.withAppendedAnnotationIntrospector(ai));
        _serializationConfig = ((SerializationConfig)_serializationConfig.withAppendedAnnotationIntrospector(ai));
      }
      
      public void registerSubtypes(Class<?>... subtypes)
      {
        ObjectMapper.this.registerSubtypes(subtypes);
      }
      
      public void registerSubtypes(NamedType... subtypes)
      {
        ObjectMapper.this.registerSubtypes(subtypes);
      }
      
      public void registerSubtypes(Collection<Class<?>> subtypes)
      {
        ObjectMapper.this.registerSubtypes(subtypes);
      }
      
      public void setMixInAnnotations(Class<?> target, Class<?> mixinSource)
      {
        addMixIn(target, mixinSource);
      }
      
      public void addDeserializationProblemHandler(DeserializationProblemHandler handler)
      {
        addHandler(handler);
      }
      
      public void setNamingStrategy(PropertyNamingStrategy naming)
      {
        setPropertyNamingStrategy(naming);
      }
      
    });
    return this;
  }
  











  public ObjectMapper registerModules(Module... modules)
  {
    for (Module module : modules) {
      registerModule(module);
    }
    return this;
  }
  











  public ObjectMapper registerModules(Iterable<? extends Module> modules)
  {
    _assertNotNull("modules", modules);
    for (Module module : modules) {
      registerModule(module);
    }
    return this;
  }
  







  public Set<Object> getRegisteredModuleIds()
  {
    return _registeredModuleTypes == null ? 
      Collections.emptySet() : Collections.unmodifiableSet(_registeredModuleTypes);
  }
  








  public static List<Module> findModules()
  {
    return findModules(null);
  }
  









  public static List<Module> findModules(ClassLoader classLoader)
  {
    ArrayList<Module> modules = new ArrayList();
    ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
    for (Module module : loader) {
      modules.add(module);
    }
    return modules;
  }
  
  private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, ClassLoader classLoader) {
    SecurityManager sm = System.getSecurityManager();
    if (sm == null) {
      return classLoader == null ? 
        ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
    }
    (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ServiceLoader<T> run() {
        return val$classLoader == null ? 
          ServiceLoader.load(clazz) : ServiceLoader.load(clazz, val$classLoader);
      }
    });
  }
  











  public ObjectMapper findAndRegisterModules()
  {
    return registerModules(findModules());
  }
  













  public SerializationConfig getSerializationConfig()
  {
    return _serializationConfig;
  }
  








  public DeserializationConfig getDeserializationConfig()
  {
    return _deserializationConfig;
  }
  






  public DeserializationContext getDeserializationContext()
  {
    return _deserializationContext;
  }
  









  public ObjectMapper setSerializerFactory(SerializerFactory f)
  {
    _serializerFactory = f;
    return this;
  }
  






  public SerializerFactory getSerializerFactory()
  {
    return _serializerFactory;
  }
  




  public ObjectMapper setSerializerProvider(DefaultSerializerProvider p)
  {
    _serializerProvider = p;
    return this;
  }
  






  public SerializerProvider getSerializerProvider()
  {
    return _serializerProvider;
  }
  







  public SerializerProvider getSerializerProviderInstance()
  {
    return _serializerProvider(_serializationConfig);
  }
  
























  public ObjectMapper setMixIns(Map<Class<?>, Class<?>> sourceMixins)
  {
    _mixIns.setLocalDefinitions(sourceMixins);
    return this;
  }
  












  public ObjectMapper addMixIn(Class<?> target, Class<?> mixinSource)
  {
    _mixIns.addLocalDefinition(target, mixinSource);
    return this;
  }
  








  public ObjectMapper setMixInResolver(ClassIntrospector.MixInResolver resolver)
  {
    SimpleMixInResolver r = _mixIns.withOverrides(resolver);
    if (r != _mixIns) {
      _mixIns = r;
      _deserializationConfig = new DeserializationConfig(_deserializationConfig, r);
      _serializationConfig = new SerializationConfig(_serializationConfig, r);
    }
    return this;
  }
  
  public Class<?> findMixInClassFor(Class<?> cls) {
    return _mixIns.findMixInClassFor(cls);
  }
  
  public int mixInCount()
  {
    return _mixIns.localSize();
  }
  


  @Deprecated
  public void setMixInAnnotations(Map<Class<?>, Class<?>> sourceMixins)
  {
    setMixIns(sourceMixins);
  }
  


  @Deprecated
  public final void addMixInAnnotations(Class<?> target, Class<?> mixinSource)
  {
    addMixIn(target, mixinSource);
  }
  










  public VisibilityChecker<?> getVisibilityChecker()
  {
    return _serializationConfig.getDefaultVisibilityChecker();
  }
  









  public ObjectMapper setVisibility(VisibilityChecker<?> vc)
  {
    _configOverrides.setDefaultVisibility(vc);
    return this;
  }
  
























  public ObjectMapper setVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility)
  {
    VisibilityChecker<?> vc = _configOverrides.getDefaultVisibility();
    vc = vc.withVisibility(forMethod, visibility);
    _configOverrides.setDefaultVisibility(vc);
    return this;
  }
  


  public SubtypeResolver getSubtypeResolver()
  {
    return _subtypeResolver;
  }
  


  public ObjectMapper setSubtypeResolver(SubtypeResolver str)
  {
    _subtypeResolver = str;
    _deserializationConfig = _deserializationConfig.with(str);
    _serializationConfig = _serializationConfig.with(str);
    return this;
  }
  









  public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai)
  {
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(ai));
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(ai));
    return this;
  }
  















  public ObjectMapper setAnnotationIntrospectors(AnnotationIntrospector serializerAI, AnnotationIntrospector deserializerAI)
  {
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(serializerAI));
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(deserializerAI));
    return this;
  }
  


  public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s)
  {
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(s));
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(s));
    return this;
  }
  



  public PropertyNamingStrategy getPropertyNamingStrategy()
  {
    return _serializationConfig.getPropertyNamingStrategy();
  }
  









  public ObjectMapper setDefaultPrettyPrinter(PrettyPrinter pp)
  {
    _serializationConfig = _serializationConfig.withDefaultPrettyPrinter(pp);
    return this;
  }
  


  @Deprecated
  public void setVisibilityChecker(VisibilityChecker<?> vc)
  {
    setVisibility(vc);
  }
  







  public ObjectMapper setPolymorphicTypeValidator(PolymorphicTypeValidator ptv)
  {
    BaseSettings s = _deserializationConfig.getBaseSettings().with(ptv);
    _deserializationConfig = _deserializationConfig._withBase(s);
    return this;
  }
  







  public PolymorphicTypeValidator getPolymorphicTypeValidator()
  {
    return _deserializationConfig.getBaseSettings().getPolymorphicTypeValidator();
  }
  














  public ObjectMapper setSerializationInclusion(JsonInclude.Include incl)
  {
    setPropertyInclusion(JsonInclude.Value.construct(incl, incl));
    return this;
  }
  



  @Deprecated
  public ObjectMapper setPropertyInclusion(JsonInclude.Value incl)
  {
    return setDefaultPropertyInclusion(incl);
  }
  






  public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Value incl)
  {
    _configOverrides.setDefaultInclusion(incl);
    return this;
  }
  







  public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Include incl)
  {
    _configOverrides.setDefaultInclusion(JsonInclude.Value.construct(incl, incl));
    return this;
  }
  






  public ObjectMapper setDefaultSetterInfo(JsonSetter.Value v)
  {
    _configOverrides.setDefaultSetterInfo(v);
    return this;
  }
  







  public ObjectMapper setDefaultVisibility(JsonAutoDetect.Value vis)
  {
    _configOverrides.setDefaultVisibility(VisibilityChecker.Std.construct(vis));
    return this;
  }
  






  public ObjectMapper setDefaultMergeable(Boolean b)
  {
    _configOverrides.setDefaultMergeable(b);
    return this;
  }
  


  public ObjectMapper setDefaultLeniency(Boolean b)
  {
    _configOverrides.setDefaultLeniency(b);
    return this;
  }
  












  public void registerSubtypes(Class<?>... classes)
  {
    getSubtypeResolver().registerSubtypes(classes);
  }
  







  public void registerSubtypes(NamedType... types)
  {
    getSubtypeResolver().registerSubtypes(types);
  }
  


  public void registerSubtypes(Collection<Class<?>> subtypes)
  {
    getSubtypeResolver().registerSubtypes(subtypes);
  }
  



















  public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv)
  {
    return activateDefaultTyping(ptv, DefaultTyping.OBJECT_AND_NON_CONCRETE);
  }
  
















  public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv, DefaultTyping applicability)
  {
    return activateDefaultTyping(ptv, applicability, JsonTypeInfo.As.WRAPPER_ARRAY);
  }
  























  public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv, DefaultTyping applicability, JsonTypeInfo.As includeAs)
  {
    if (includeAs == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
      throw new IllegalArgumentException("Cannot use includeAs of " + includeAs);
    }
    
    TypeResolverBuilder<?> typer = _constructDefaultTypeResolverBuilder(applicability, ptv);
    
    typer = typer.init(JsonTypeInfo.Id.CLASS, null);
    typer = typer.inclusion(includeAs);
    return setDefaultTyping(typer);
  }
  




















  public ObjectMapper activateDefaultTypingAsProperty(PolymorphicTypeValidator ptv, DefaultTyping applicability, String propertyName)
  {
    TypeResolverBuilder<?> typer = _constructDefaultTypeResolverBuilder(applicability, 
      getPolymorphicTypeValidator());
    

    typer = typer.init(JsonTypeInfo.Id.CLASS, null);
    typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
    typer = typer.typeProperty(propertyName);
    return setDefaultTyping(typer);
  }
  





  public ObjectMapper deactivateDefaultTyping()
  {
    return setDefaultTyping(null);
  }
  














  public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer)
  {
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(typer));
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(typer));
    return this;
  }
  








  @Deprecated
  public ObjectMapper enableDefaultTyping()
  {
    return activateDefaultTyping(getPolymorphicTypeValidator());
  }
  


  @Deprecated
  public ObjectMapper enableDefaultTyping(DefaultTyping dti)
  {
    return enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
  }
  


  @Deprecated
  public ObjectMapper enableDefaultTyping(DefaultTyping applicability, JsonTypeInfo.As includeAs)
  {
    return activateDefaultTyping(getPolymorphicTypeValidator(), applicability, includeAs);
  }
  


  @Deprecated
  public ObjectMapper enableDefaultTypingAsProperty(DefaultTyping applicability, String propertyName)
  {
    return activateDefaultTypingAsProperty(getPolymorphicTypeValidator(), applicability, propertyName);
  }
  


  @Deprecated
  public ObjectMapper disableDefaultTyping()
  {
    return setDefaultTyping(null);
  }
  






















  public MutableConfigOverride configOverride(Class<?> type)
  {
    return _configOverrides.findOrCreateOverride(type);
  }
  








  public TypeFactory getTypeFactory()
  {
    return _typeFactory;
  }
  







  public ObjectMapper setTypeFactory(TypeFactory f)
  {
    _typeFactory = f;
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(f));
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(f));
    return this;
  }
  




  public JavaType constructType(Type t)
  {
    _assertNotNull("t", t);
    return _typeFactory.constructType(t);
  }
  















  public JsonNodeFactory getNodeFactory()
  {
    return _deserializationConfig.getNodeFactory();
  }
  




  public ObjectMapper setNodeFactory(JsonNodeFactory f)
  {
    _deserializationConfig = _deserializationConfig.with(f);
    return this;
  }
  



  public ObjectMapper addHandler(DeserializationProblemHandler h)
  {
    _deserializationConfig = _deserializationConfig.withHandler(h);
    return this;
  }
  



  public ObjectMapper clearProblemHandlers()
  {
    _deserializationConfig = _deserializationConfig.withNoProblemHandlers();
    return this;
  }
  













  public ObjectMapper setConfig(DeserializationConfig config)
  {
    _assertNotNull("config", config);
    _deserializationConfig = config;
    return this;
  }
  








  @Deprecated
  public void setFilters(FilterProvider filterProvider)
  {
    _serializationConfig = _serializationConfig.withFilters(filterProvider);
  }
  










  public ObjectMapper setFilterProvider(FilterProvider filterProvider)
  {
    _serializationConfig = _serializationConfig.withFilters(filterProvider);
    return this;
  }
  









  public ObjectMapper setBase64Variant(Base64Variant v)
  {
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(v));
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(v));
    return this;
  }
  













  public ObjectMapper setConfig(SerializationConfig config)
  {
    _assertNotNull("config", config);
    _serializationConfig = config;
    return this;
  }
  























  public JsonFactory tokenStreamFactory()
  {
    return _jsonFactory;
  }
  
  public JsonFactory getFactory() { return _jsonFactory; }
  


  @Deprecated
  public JsonFactory getJsonFactory()
  {
    return getFactory();
  }
  









  public ObjectMapper setDateFormat(DateFormat dateFormat)
  {
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(dateFormat));
    _serializationConfig = _serializationConfig.with(dateFormat);
    return this;
  }
  



  public DateFormat getDateFormat()
  {
    return _serializationConfig.getDateFormat();
  }
  







  public Object setHandlerInstantiator(HandlerInstantiator hi)
  {
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(hi));
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(hi));
    return this;
  }
  



  public ObjectMapper setInjectableValues(InjectableValues injectableValues)
  {
    _injectableValues = injectableValues;
    return this;
  }
  


  public InjectableValues getInjectableValues()
  {
    return _injectableValues;
  }
  



  public ObjectMapper setLocale(Locale l)
  {
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(l));
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(l));
    return this;
  }
  



  public ObjectMapper setTimeZone(TimeZone tz)
  {
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(tz));
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(tz));
    return this;
  }
  









  public boolean isEnabled(MapperFeature f)
  {
    return _serializationConfig.isEnabled(f);
  }
  



  public ObjectMapper configure(MapperFeature f, boolean state)
  {
    _serializationConfig = (state ? (SerializationConfig)_serializationConfig.with(new MapperFeature[] { f }) : (SerializationConfig)_serializationConfig.without(new MapperFeature[] { f }));
    
    _deserializationConfig = (state ? (DeserializationConfig)_deserializationConfig.with(new MapperFeature[] { f }) : (DeserializationConfig)_deserializationConfig.without(new MapperFeature[] { f }));
    return this;
  }
  


  public ObjectMapper enable(MapperFeature... f)
  {
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.with(f));
    _serializationConfig = ((SerializationConfig)_serializationConfig.with(f));
    return this;
  }
  


  public ObjectMapper disable(MapperFeature... f)
  {
    _deserializationConfig = ((DeserializationConfig)_deserializationConfig.without(f));
    _serializationConfig = ((SerializationConfig)_serializationConfig.without(f));
    return this;
  }
  









  public boolean isEnabled(SerializationFeature f)
  {
    return _serializationConfig.isEnabled(f);
  }
  




  public ObjectMapper configure(SerializationFeature f, boolean state)
  {
    _serializationConfig = (state ? _serializationConfig.with(f) : _serializationConfig.without(f));
    return this;
  }
  



  public ObjectMapper enable(SerializationFeature f)
  {
    _serializationConfig = _serializationConfig.with(f);
    return this;
  }
  




  public ObjectMapper enable(SerializationFeature first, SerializationFeature... f)
  {
    _serializationConfig = _serializationConfig.with(first, f);
    return this;
  }
  



  public ObjectMapper disable(SerializationFeature f)
  {
    _serializationConfig = _serializationConfig.without(f);
    return this;
  }
  




  public ObjectMapper disable(SerializationFeature first, SerializationFeature... f)
  {
    _serializationConfig = _serializationConfig.without(first, f);
    return this;
  }
  









  public boolean isEnabled(DeserializationFeature f)
  {
    return _deserializationConfig.isEnabled(f);
  }
  




  public ObjectMapper configure(DeserializationFeature f, boolean state)
  {
    _deserializationConfig = (state ? _deserializationConfig.with(f) : _deserializationConfig.without(f));
    return this;
  }
  



  public ObjectMapper enable(DeserializationFeature feature)
  {
    _deserializationConfig = _deserializationConfig.with(feature);
    return this;
  }
  




  public ObjectMapper enable(DeserializationFeature first, DeserializationFeature... f)
  {
    _deserializationConfig = _deserializationConfig.with(first, f);
    return this;
  }
  



  public ObjectMapper disable(DeserializationFeature feature)
  {
    _deserializationConfig = _deserializationConfig.without(feature);
    return this;
  }
  




  public ObjectMapper disable(DeserializationFeature first, DeserializationFeature... f)
  {
    _deserializationConfig = _deserializationConfig.without(first, f);
    return this;
  }
  





  public boolean isEnabled(JsonParser.Feature f)
  {
    return _deserializationConfig.isEnabled(f, _jsonFactory);
  }
  










  public ObjectMapper configure(JsonParser.Feature f, boolean state)
  {
    _jsonFactory.configure(f, state);
    return this;
  }
  











  public ObjectMapper enable(JsonParser.Feature... features)
  {
    for (JsonParser.Feature f : features) {
      _jsonFactory.enable(f);
    }
    return this;
  }
  











  public ObjectMapper disable(JsonParser.Feature... features)
  {
    for (JsonParser.Feature f : features) {
      _jsonFactory.disable(f);
    }
    return this;
  }
  





  public boolean isEnabled(JsonGenerator.Feature f)
  {
    return _serializationConfig.isEnabled(f, _jsonFactory);
  }
  










  public ObjectMapper configure(JsonGenerator.Feature f, boolean state)
  {
    _jsonFactory.configure(f, state);
    return this;
  }
  











  public ObjectMapper enable(JsonGenerator.Feature... features)
  {
    for (JsonGenerator.Feature f : features) {
      _jsonFactory.enable(f);
    }
    return this;
  }
  











  public ObjectMapper disable(JsonGenerator.Feature... features)
  {
    for (JsonGenerator.Feature f : features) {
      _jsonFactory.disable(f);
    }
    return this;
  }
  











  public boolean isEnabled(JsonFactory.Feature f)
  {
    return _jsonFactory.isEnabled(f);
  }
  








  public boolean isEnabled(StreamReadFeature f)
  {
    return isEnabled(f.mappedFeature());
  }
  


  public boolean isEnabled(StreamWriteFeature f)
  {
    return isEnabled(f.mappedFeature());
  }
  





























  public <T> T readValue(JsonParser p, Class<T> valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("p", p);
    return _readValue(getDeserializationConfig(), p, _typeFactory.constructType(valueType));
  }
  


















  public <T> T readValue(JsonParser p, TypeReference<T> valueTypeRef)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("p", p);
    return _readValue(getDeserializationConfig(), p, _typeFactory.constructType(valueTypeRef));
  }
  

















  public final <T> T readValue(JsonParser p, ResolvedType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("p", p);
    return _readValue(getDeserializationConfig(), p, (JavaType)valueType);
  }
  













  public <T> T readValue(JsonParser p, JavaType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("p", p);
    return _readValue(getDeserializationConfig(), p, valueType);
  }
  
























  public <T extends TreeNode> T readTree(JsonParser p)
    throws IOException, JsonProcessingException
  {
    _assertNotNull("p", p);
    
    DeserializationConfig cfg = getDeserializationConfig();
    JsonToken t = p.getCurrentToken();
    if (t == null) {
      t = p.nextToken();
      if (t == null) {
        return null;
      }
    }
    
    JsonNode n = (JsonNode)_readValue(cfg, p, constructType(JsonNode.class));
    if (n == null) {
      n = getNodeFactory().nullNode();
    }
    
    T result = n;
    return result;
  }
  



















  public <T> MappingIterator<T> readValues(JsonParser p, ResolvedType valueType)
    throws IOException, JsonProcessingException
  {
    return readValues(p, (JavaType)valueType);
  }
  








  public <T> MappingIterator<T> readValues(JsonParser p, JavaType valueType)
    throws IOException, JsonProcessingException
  {
    _assertNotNull("p", p);
    DeserializationConfig config = getDeserializationConfig();
    DeserializationContext ctxt = createDeserializationContext(p, config);
    JsonDeserializer<?> deser = _findRootDeserializer(ctxt, valueType);
    
    return new MappingIterator(valueType, p, ctxt, deser, false, null);
  }
  










  public <T> MappingIterator<T> readValues(JsonParser p, Class<T> valueType)
    throws IOException, JsonProcessingException
  {
    return readValues(p, _typeFactory.constructType(valueType));
  }
  




  public <T> MappingIterator<T> readValues(JsonParser p, TypeReference<T> valueTypeRef)
    throws IOException, JsonProcessingException
  {
    return readValues(p, _typeFactory.constructType(valueTypeRef));
  }
  
































  public JsonNode readTree(InputStream in)
    throws IOException
  {
    _assertNotNull("in", in);
    return _readTreeAndClose(_jsonFactory.createParser(in));
  }
  


  public JsonNode readTree(Reader r)
    throws IOException
  {
    _assertNotNull("r", r);
    return _readTreeAndClose(_jsonFactory.createParser(r));
  }
  


  public JsonNode readTree(String content)
    throws JsonProcessingException, JsonMappingException
  {
    _assertNotNull("content", content);
    try {
      return _readTreeAndClose(_jsonFactory.createParser(content));
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
  }
  


  public JsonNode readTree(byte[] content)
    throws IOException
  {
    _assertNotNull("content", content);
    return _readTreeAndClose(_jsonFactory.createParser(content));
  }
  


  public JsonNode readTree(byte[] content, int offset, int len)
    throws IOException
  {
    _assertNotNull("content", content);
    return _readTreeAndClose(_jsonFactory.createParser(content, offset, len));
  }
  




  public JsonNode readTree(File file)
    throws IOException, JsonProcessingException
  {
    _assertNotNull("file", file);
    return _readTreeAndClose(_jsonFactory.createParser(file));
  }
  








  public JsonNode readTree(URL source)
    throws IOException
  {
    _assertNotNull("source", source);
    return _readTreeAndClose(_jsonFactory.createParser(source));
  }
  












  public void writeValue(JsonGenerator g, Object value)
    throws IOException, JsonGenerationException, JsonMappingException
  {
    _assertNotNull("g", g);
    SerializationConfig config = getSerializationConfig();
    






    if ((config.isEnabled(SerializationFeature.INDENT_OUTPUT)) && 
      (g.getPrettyPrinter() == null)) {
      g.setPrettyPrinter(config.constructDefaultPrettyPrinter());
    }
    
    if ((config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE)) && ((value instanceof Closeable))) {
      _writeCloseableValue(g, value, config);
    } else {
      _serializerProvider(config).serializeValue(g, value);
      if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
        g.flush();
      }
    }
  }
  







  public void writeTree(JsonGenerator g, TreeNode rootNode)
    throws IOException, JsonProcessingException
  {
    _assertNotNull("g", g);
    SerializationConfig config = getSerializationConfig();
    _serializerProvider(config).serializeValue(g, rootNode);
    if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
      g.flush();
    }
  }
  




  public void writeTree(JsonGenerator g, JsonNode rootNode)
    throws IOException, JsonProcessingException
  {
    _assertNotNull("g", g);
    SerializationConfig config = getSerializationConfig();
    _serializerProvider(config).serializeValue(g, rootNode);
    if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
      g.flush();
    }
  }
  







  public ObjectNode createObjectNode()
  {
    return _deserializationConfig.getNodeFactory().objectNode();
  }
  







  public ArrayNode createArrayNode()
  {
    return _deserializationConfig.getNodeFactory().arrayNode();
  }
  
  public JsonNode missingNode()
  {
    return _deserializationConfig.getNodeFactory().missingNode();
  }
  
  public JsonNode nullNode()
  {
    return _deserializationConfig.getNodeFactory().nullNode();
  }
  






  public JsonParser treeAsTokens(TreeNode n)
  {
    _assertNotNull("n", n);
    return new TreeTraversingParser((JsonNode)n, this);
  }
  











  public <T> T treeToValue(TreeNode n, Class<T> valueType)
    throws JsonProcessingException
  {
    if (n == null) {
      return null;
    }
    
    try
    {
      if ((TreeNode.class.isAssignableFrom(valueType)) && 
        (valueType.isAssignableFrom(n.getClass()))) {
        return n;
      }
      JsonToken tt = n.asToken();
      
      if (tt == JsonToken.VALUE_NULL) {
        return null;
      }
      

      if ((tt == JsonToken.VALUE_EMBEDDED_OBJECT) && 
        ((n instanceof POJONode))) {
        Object ob = ((POJONode)n).getPojo();
        if ((ob == null) || (valueType.isInstance(ob))) {
          return ob;
        }
      }
      
      return readValue(treeAsTokens(n), valueType);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
  
























  public <T extends JsonNode> T valueToTree(Object fromValue)
    throws IllegalArgumentException
  {
    if (fromValue == null) {
      return getNodeFactory().nullNode();
    }
    TokenBuffer buf = new TokenBuffer(this, false);
    if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
      buf = buf.forceUseOfBigDecimal(true);
    }
    try
    {
      writeValue(buf, fromValue);
      JsonParser p = buf.asParser();
      JsonNode result = (JsonNode)readTree(p);
      p.close();
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e); }
    JsonNode result;
    return result;
  }
  




















  public boolean canSerialize(Class<?> type)
  {
    return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, null);
  }
  






  public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause)
  {
    return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, cause);
  }
  

















  public boolean canDeserialize(JavaType type)
  {
    return 
      createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, null);
  }
  







  public boolean canDeserialize(JavaType type, AtomicReference<Throwable> cause)
  {
    return 
      createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, cause);
  }
  




















  public <T> T readValue(File src, Class<T> valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueType));
  }
  













  public <T> T readValue(File src, TypeReference<T> valueTypeRef)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueTypeRef));
  }
  













  public <T> T readValue(File src, JavaType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), valueType);
  }
  



















  public <T> T readValue(URL src, Class<T> valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueType));
  }
  




  public <T> T readValue(URL src, TypeReference<T> valueTypeRef)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueTypeRef));
  }
  




  public <T> T readValue(URL src, JavaType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), valueType);
  }
  








  public <T> T readValue(String content, Class<T> valueType)
    throws JsonProcessingException, JsonMappingException
  {
    _assertNotNull("content", content);
    return readValue(content, _typeFactory.constructType(valueType));
  }
  








  public <T> T readValue(String content, TypeReference<T> valueTypeRef)
    throws JsonProcessingException, JsonMappingException
  {
    _assertNotNull("content", content);
    return readValue(content, _typeFactory.constructType(valueTypeRef));
  }
  









  public <T> T readValue(String content, JavaType valueType)
    throws JsonProcessingException, JsonMappingException
  {
    _assertNotNull("content", content);
    try {
      return _readMapAndClose(_jsonFactory.createParser(content), valueType);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
  }
  

  public <T> T readValue(Reader src, Class<T> valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueType));
  }
  

  public <T> T readValue(Reader src, TypeReference<T> valueTypeRef)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueTypeRef));
  }
  

  public <T> T readValue(Reader src, JavaType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), valueType);
  }
  

  public <T> T readValue(InputStream src, Class<T> valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueType));
  }
  

  public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueTypeRef));
  }
  

  public <T> T readValue(InputStream src, JavaType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), valueType);
  }
  

  public <T> T readValue(byte[] src, Class<T> valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueType));
  }
  


  public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src, offset, len), _typeFactory.constructType(valueType));
  }
  

  public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory.constructType(valueTypeRef));
  }
  


  public <T> T readValue(byte[] src, int offset, int len, TypeReference<T> valueTypeRef)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src, offset, len), _typeFactory.constructType(valueTypeRef));
  }
  

  public <T> T readValue(byte[] src, JavaType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), valueType);
  }
  


  public <T> T readValue(byte[] src, int offset, int len, JavaType valueType)
    throws IOException, JsonParseException, JsonMappingException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src, offset, len), valueType);
  }
  
  public <T> T readValue(DataInput src, Class<T> valueType)
    throws IOException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), _typeFactory
      .constructType(valueType));
  }
  
  public <T> T readValue(DataInput src, JavaType valueType)
    throws IOException
  {
    _assertNotNull("src", src);
    return _readMapAndClose(_jsonFactory.createParser(src), valueType);
  }
  











  public void writeValue(File resultFile, Object value)
    throws IOException, JsonGenerationException, JsonMappingException
  {
    _assertNotNull("resultFile", resultFile);
    _configAndWriteValue(_jsonFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
  }
  











  public void writeValue(OutputStream out, Object value)
    throws IOException, JsonGenerationException, JsonMappingException
  {
    _assertNotNull("out", out);
    _configAndWriteValue(_jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
  }
  


  public void writeValue(DataOutput out, Object value)
    throws IOException
  {
    _assertNotNull("out", out);
    _configAndWriteValue(_jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
  }
  










  public void writeValue(Writer w, Object value)
    throws IOException, JsonGenerationException, JsonMappingException
  {
    _assertNotNull("w", w);
    _configAndWriteValue(_jsonFactory.createGenerator(w), value);
  }
  










  public String writeValueAsString(Object value)
    throws JsonProcessingException
  {
    SegmentedStringWriter sw = new SegmentedStringWriter(_jsonFactory._getBufferRecycler());
    try {
      _configAndWriteValue(_jsonFactory.createGenerator(sw), value);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
    return sw.getAndClear();
  }
  










  public byte[] writeValueAsBytes(Object value)
    throws JsonProcessingException
  {
    ByteArrayBuilder bb = new ByteArrayBuilder(_jsonFactory._getBufferRecycler());
    try {
      _configAndWriteValue(_jsonFactory.createGenerator(bb, JsonEncoding.UTF8), value);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
    byte[] result = bb.toByteArray();
    bb.release();
    return result;
  }
  










  public ObjectWriter writer()
  {
    return _newWriter(getSerializationConfig());
  }
  




  public ObjectWriter writer(SerializationFeature feature)
  {
    return _newWriter(getSerializationConfig().with(feature));
  }
  





  public ObjectWriter writer(SerializationFeature first, SerializationFeature... other)
  {
    return _newWriter(getSerializationConfig().with(first, other));
  }
  




  public ObjectWriter writer(DateFormat df)
  {
    return _newWriter(getSerializationConfig().with(df));
  }
  



  public ObjectWriter writerWithView(Class<?> serializationView)
  {
    return _newWriter(getSerializationConfig().withView(serializationView));
  }
  










  public ObjectWriter writerFor(Class<?> rootType)
  {
    return _newWriter(getSerializationConfig(), rootType == null ? null : _typeFactory
      .constructType(rootType), null);
  }
  











  public ObjectWriter writerFor(TypeReference<?> rootType)
  {
    return _newWriter(getSerializationConfig(), rootType == null ? null : _typeFactory
      .constructType(rootType), null);
  }
  











  public ObjectWriter writerFor(JavaType rootType)
  {
    return _newWriter(getSerializationConfig(), rootType, null);
  }
  




  public ObjectWriter writer(PrettyPrinter pp)
  {
    if (pp == null) {
      pp = ObjectWriter.NULL_PRETTY_PRINTER;
    }
    return _newWriter(getSerializationConfig(), null, pp);
  }
  



  public ObjectWriter writerWithDefaultPrettyPrinter()
  {
    SerializationConfig config = getSerializationConfig();
    return _newWriter(config, null, config
      .getDefaultPrettyPrinter());
  }
  



  public ObjectWriter writer(FilterProvider filterProvider)
  {
    return _newWriter(getSerializationConfig().withFilters(filterProvider));
  }
  






  public ObjectWriter writer(FormatSchema schema)
  {
    _verifySchemaType(schema);
    return _newWriter(getSerializationConfig(), schema);
  }
  





  public ObjectWriter writer(Base64Variant defaultBase64)
  {
    return _newWriter((SerializationConfig)getSerializationConfig().with(defaultBase64));
  }
  





  public ObjectWriter writer(CharacterEscapes escapes)
  {
    return _newWriter(getSerializationConfig()).with(escapes);
  }
  





  public ObjectWriter writer(ContextAttributes attrs)
  {
    return _newWriter(getSerializationConfig().with(attrs));
  }
  


  @Deprecated
  public ObjectWriter writerWithType(Class<?> rootType)
  {
    return _newWriter(getSerializationConfig(), rootType == null ? null : _typeFactory
    
      .constructType(rootType), null);
  }
  



  @Deprecated
  public ObjectWriter writerWithType(TypeReference<?> rootType)
  {
    return _newWriter(getSerializationConfig(), rootType == null ? null : _typeFactory
    
      .constructType(rootType), null);
  }
  



  @Deprecated
  public ObjectWriter writerWithType(JavaType rootType)
  {
    return _newWriter(getSerializationConfig(), rootType, null);
  }
  











  public ObjectReader reader()
  {
    return _newReader(getDeserializationConfig()).with(_injectableValues);
  }
  






  public ObjectReader reader(DeserializationFeature feature)
  {
    return _newReader(getDeserializationConfig().with(feature));
  }
  







  public ObjectReader reader(DeserializationFeature first, DeserializationFeature... other)
  {
    return _newReader(getDeserializationConfig().with(first, other));
  }
  









  public ObjectReader readerForUpdating(Object valueToUpdate)
  {
    JavaType t = _typeFactory.constructType(valueToUpdate.getClass());
    return _newReader(getDeserializationConfig(), t, valueToUpdate, null, _injectableValues);
  }
  






  public ObjectReader readerFor(JavaType type)
  {
    return _newReader(getDeserializationConfig(), type, null, null, _injectableValues);
  }
  






  public ObjectReader readerFor(Class<?> type)
  {
    return _newReader(getDeserializationConfig(), _typeFactory.constructType(type), null, null, _injectableValues);
  }
  






  public ObjectReader readerFor(TypeReference<?> type)
  {
    return _newReader(getDeserializationConfig(), _typeFactory.constructType(type), null, null, _injectableValues);
  }
  




  public ObjectReader reader(JsonNodeFactory f)
  {
    return _newReader(getDeserializationConfig()).with(f);
  }
  






  public ObjectReader reader(FormatSchema schema)
  {
    _verifySchemaType(schema);
    return _newReader(getDeserializationConfig(), null, null, schema, _injectableValues);
  }
  






  public ObjectReader reader(InjectableValues injectableValues)
  {
    return _newReader(getDeserializationConfig(), null, null, null, injectableValues);
  }
  




  public ObjectReader readerWithView(Class<?> view)
  {
    return _newReader(getDeserializationConfig().withView(view));
  }
  





  public ObjectReader reader(Base64Variant defaultBase64)
  {
    return _newReader((DeserializationConfig)getDeserializationConfig().with(defaultBase64));
  }
  





  public ObjectReader reader(ContextAttributes attrs)
  {
    return _newReader(getDeserializationConfig().with(attrs));
  }
  


  @Deprecated
  public ObjectReader reader(JavaType type)
  {
    return _newReader(getDeserializationConfig(), type, null, null, _injectableValues);
  }
  



  @Deprecated
  public ObjectReader reader(Class<?> type)
  {
    return _newReader(getDeserializationConfig(), _typeFactory.constructType(type), null, null, _injectableValues);
  }
  



  @Deprecated
  public ObjectReader reader(TypeReference<?> type)
  {
    return _newReader(getDeserializationConfig(), _typeFactory.constructType(type), null, null, _injectableValues);
  }
  



































  public <T> T convertValue(Object fromValue, Class<T> toValueType)
    throws IllegalArgumentException
  {
    return _convert(fromValue, _typeFactory.constructType(toValueType));
  }
  




  public <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef)
    throws IllegalArgumentException
  {
    return _convert(fromValue, _typeFactory.constructType(toValueTypeRef));
  }
  




  public <T> T convertValue(Object fromValue, JavaType toValueType)
    throws IllegalArgumentException
  {
    return _convert(fromValue, toValueType);
  }
  












  protected Object _convert(Object fromValue, JavaType toValueType)
    throws IllegalArgumentException
  {
    TokenBuffer buf = new TokenBuffer(this, false);
    if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
      buf = buf.forceUseOfBigDecimal(true);
    }
    
    try
    {
      SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
      
      _serializerProvider(config).serializeValue(buf, fromValue);
      

      JsonParser p = buf.asParser();
      

      DeserializationConfig deserConfig = getDeserializationConfig();
      JsonToken t = _initForReading(p, toValueType);
      Object result; Object result; if (t == JsonToken.VALUE_NULL) {
        DeserializationContext ctxt = createDeserializationContext(p, deserConfig);
        result = _findRootDeserializer(ctxt, toValueType).getNullValue(ctxt); } else { Object result;
        if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
          result = null;
        } else {
          DeserializationContext ctxt = createDeserializationContext(p, deserConfig);
          JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, toValueType);
          
          result = deser.deserialize(p, ctxt);
        } }
      p.close();
      return result;
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
  




































  public <T> T updateValue(T valueToUpdate, Object overrides)
    throws JsonMappingException
  {
    T result = valueToUpdate;
    if ((valueToUpdate != null) && (overrides != null)) {
      TokenBuffer buf = new TokenBuffer(this, false);
      if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
        buf = buf.forceUseOfBigDecimal(true);
      }
      try
      {
        SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
        _serializerProvider(config).serializeValue(buf, overrides);
        JsonParser p = buf.asParser();
        result = readerForUpdating(valueToUpdate).readValue(p);
        p.close();
      } catch (IOException e) {
        if ((e instanceof JsonMappingException)) {
          throw ((JsonMappingException)e);
        }
        
        throw JsonMappingException.fromUnexpectedIOE(e);
      }
    }
    return result;
  }
  















  @Deprecated
  public JsonSchema generateJsonSchema(Class<?> t)
    throws JsonMappingException
  {
    return _serializerProvider(getSerializationConfig()).generateJsonSchema(t);
  }
  











  public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor)
    throws JsonMappingException
  {
    acceptJsonFormatVisitor(_typeFactory.constructType(type), visitor);
  }
  












  public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor)
    throws JsonMappingException
  {
    if (type == null) {
      throw new IllegalArgumentException("type must be provided");
    }
    _serializerProvider(getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
  }
  












  protected TypeResolverBuilder<?> _constructDefaultTypeResolverBuilder(DefaultTyping applicability, PolymorphicTypeValidator ptv)
  {
    return DefaultTypeResolverBuilder.construct(applicability, ptv);
  }
  









  protected DefaultSerializerProvider _serializerProvider(SerializationConfig config)
  {
    return _serializerProvider.createInstance(config, _serializerFactory);
  }
  




  protected final void _configAndWriteValue(JsonGenerator g, Object value)
    throws IOException
  {
    SerializationConfig cfg = getSerializationConfig();
    cfg.initialize(g);
    if ((cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE)) && ((value instanceof Closeable))) {
      _configAndWriteCloseable(g, value, cfg);
      return;
    }
    try {
      _serializerProvider(cfg).serializeValue(g, value);
    } catch (Exception e) {
      ClassUtil.closeOnFailAndThrowAsIOE(g, e);
      return;
    }
    g.close();
  }
  




  private final void _configAndWriteCloseable(JsonGenerator g, Object value, SerializationConfig cfg)
    throws IOException
  {
    Closeable toClose = (Closeable)value;
    try {
      _serializerProvider(cfg).serializeValue(g, value);
      Closeable tmpToClose = toClose;
      toClose = null;
      tmpToClose.close();
    } catch (Exception e) {
      ClassUtil.closeOnFailAndThrowAsIOE(g, toClose, e);
      return;
    }
    g.close();
  }
  




  private final void _writeCloseableValue(JsonGenerator g, Object value, SerializationConfig cfg)
    throws IOException
  {
    Closeable toClose = (Closeable)value;
    try {
      _serializerProvider(cfg).serializeValue(g, value);
      if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
        g.flush();
      }
    } catch (Exception e) {
      ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
      return;
    }
    toClose.close();
  }
  















  protected Object _readValue(DeserializationConfig cfg, JsonParser p, JavaType valueType)
    throws IOException
  {
    JsonToken t = _initForReading(p, valueType);
    DeserializationContext ctxt = createDeserializationContext(p, cfg);
    Object result; Object result; if (t == JsonToken.VALUE_NULL)
    {
      result = _findRootDeserializer(ctxt, valueType).getNullValue(ctxt); } else { Object result;
      if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
        result = null;
      } else {
        JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
        Object result;
        if (cfg.useRootWrapping()) {
          result = _unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
        } else {
          result = deser.deserialize(p, ctxt);
        }
      }
    }
    p.clearCurrentToken();
    if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
      _verifyNoTrailingTokens(p, ctxt, valueType);
    }
    return result;
  }
  
  protected Object _readMapAndClose(JsonParser p0, JavaType valueType)
    throws IOException
  {
    JsonParser p = p0;Throwable localThrowable3 = null;
    try {
      JsonToken t = _initForReading(p, valueType);
      DeserializationConfig cfg = getDeserializationConfig();
      DeserializationContext ctxt = createDeserializationContext(p, cfg);
      Object result; JsonDeserializer<Object> deser; Object result; if (t == JsonToken.VALUE_NULL)
      {
        result = _findRootDeserializer(ctxt, valueType).getNullValue(ctxt); } else { Object result;
        if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
          result = null;
        } else {
          deser = _findRootDeserializer(ctxt, valueType);
          Object result; if (cfg.useRootWrapping()) {
            result = _unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
          } else {
            result = deser.deserialize(p, ctxt);
          }
          ctxt.checkUnresolvedObjectId();
        } }
      if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
        _verifyNoTrailingTokens(p, ctxt, valueType);
      }
      return result;
    }
    catch (Throwable localThrowable1)
    {
      localThrowable3 = localThrowable1;throw localThrowable1;










    }
    finally
    {









      if (p != null) { if (localThrowable3 != null) try { p.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { p.close();
        }
      }
    }
  }
  


  protected JsonNode _readTreeAndClose(JsonParser p0)
    throws IOException
  {
    JsonParser p = p0;Throwable localThrowable5 = null;
    try { JavaType valueType = constructType(JsonNode.class);
      
      DeserializationConfig cfg = getDeserializationConfig();
      


      cfg.initialize(p);
      JsonToken t = p.getCurrentToken();
      if (t == null) {
        t = p.nextToken();
        if (t == null)
        {

          return cfg.getNodeFactory().missingNode();
        }
      }
      boolean checkTrailing = cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
      DeserializationContext ctxt;
      DeserializationContext ctxt;
      Object deser;
      JsonNode resultNode; if (t == JsonToken.VALUE_NULL) {
        JsonNode resultNode = cfg.getNodeFactory().nullNode();
        if (!checkTrailing) {
          return resultNode;
        }
        ctxt = createDeserializationContext(p, cfg);
      } else {
        ctxt = createDeserializationContext(p, cfg);
        deser = _findRootDeserializer(ctxt, valueType);
        JsonNode resultNode; if (cfg.useRootWrapping()) {
          resultNode = (JsonNode)_unwrapAndDeserialize(p, ctxt, cfg, valueType, (JsonDeserializer)deser);
        } else {
          resultNode = (JsonNode)((JsonDeserializer)deser).deserialize(p, ctxt);
        }
      }
      if (checkTrailing) {
        _verifyNoTrailingTokens(p, ctxt, valueType);
      }
      

      return resultNode;
    }
    catch (Throwable localThrowable3)
    {
      localThrowable5 = localThrowable3;throw localThrowable3;



















    }
    finally
    {



















      if (p != null) if (localThrowable5 != null) try { p.close(); } catch (Throwable localThrowable4) { localThrowable5.addSuppressed(localThrowable4); } else { p.close();
        }
    }
  }
  
  protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, DeserializationConfig config, JavaType rootType, JsonDeserializer<Object> deser)
    throws IOException
  {
    PropertyName expRootName = config.findRootName(rootType);
    
    String expSimpleName = expRootName.getSimpleName();
    if (p.getCurrentToken() != JsonToken.START_OBJECT) {
      ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", new Object[] { expSimpleName, p
      
        .getCurrentToken() });
    }
    if (p.nextToken() != JsonToken.FIELD_NAME) {
      ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", new Object[] { expSimpleName, p
      
        .getCurrentToken() });
    }
    String actualName = p.getCurrentName();
    if (!expSimpleName.equals(actualName)) {
      ctxt.reportPropertyInputMismatch(rootType, actualName, "Root name '%s' does not match expected ('%s') for type %s", new Object[] { actualName, expSimpleName, rootType });
    }
    


    p.nextToken();
    Object result = deser.deserialize(p, ctxt);
    
    if (p.nextToken() != JsonToken.END_OBJECT) {
      ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", new Object[] { expSimpleName, p
      
        .getCurrentToken() });
    }
    if (config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
      _verifyNoTrailingTokens(p, ctxt, rootType);
    }
    return result;
  }
  





  protected DefaultDeserializationContext createDeserializationContext(JsonParser p, DeserializationConfig cfg)
  {
    return _deserializationContext.createInstance(cfg, p, _injectableValues);
  }
  














  protected JsonToken _initForReading(JsonParser p, JavaType targetType)
    throws IOException
  {
    _deserializationConfig.initialize(p);
    



    JsonToken t = p.getCurrentToken();
    if (t == null)
    {
      t = p.nextToken();
      if (t == null)
      {

        throw MismatchedInputException.from(p, targetType, "No content to map due to end-of-input");
      }
    }
    
    return t;
  }
  
  @Deprecated
  protected JsonToken _initForReading(JsonParser p) throws IOException {
    return _initForReading(p, null);
  }
  




  protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType)
    throws IOException
  {
    JsonToken t = p.nextToken();
    if (t != null) {
      Class<?> bt = ClassUtil.rawClass(bindType);
      ctxt.reportTrailingTokens(bt, p, t);
    }
  }
  











  protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType)
    throws JsonMappingException
  {
    JsonDeserializer<Object> deser = (JsonDeserializer)_rootDeserializers.get(valueType);
    if (deser != null) {
      return deser;
    }
    
    deser = ctxt.findRootValueDeserializer(valueType);
    if (deser == null) {
      return (JsonDeserializer)ctxt.reportBadDefinition(valueType, "Cannot find a deserializer for type " + valueType);
    }
    
    _rootDeserializers.put(valueType, deser);
    return deser;
  }
  



  protected void _verifySchemaType(FormatSchema schema)
  {
    if ((schema != null) && 
      (!_jsonFactory.canUseSchema(schema)))
    {
      throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + _jsonFactory.getFormatName());
    }
  }
  
  protected final void _assertNotNull(String paramName, Object src)
  {
    if (src == null) {
      throw new IllegalArgumentException(String.format("argument \"%s\" is null", new Object[] { paramName }));
    }
  }
}
