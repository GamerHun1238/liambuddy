package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.TimeZone;

public abstract class MapperBuilder<M extends ObjectMapper, B extends MapperBuilder<M, B>>
{
  protected final M _mapper;
  
  protected MapperBuilder(M mapper)
  {
    _mapper = mapper;
  }
  






  public M build()
  {
    return _mapper;
  }
  





  public boolean isEnabled(MapperFeature f)
  {
    return _mapper.isEnabled(f);
  }
  
  public boolean isEnabled(DeserializationFeature f) { return _mapper.isEnabled(f); }
  
  public boolean isEnabled(SerializationFeature f) {
    return _mapper.isEnabled(f);
  }
  
  public boolean isEnabled(JsonParser.Feature f) {
    return _mapper.isEnabled(f);
  }
  
  public boolean isEnabled(JsonGenerator.Feature f) { return _mapper.isEnabled(f); }
  






  public com.fasterxml.jackson.core.TokenStreamFactory streamFactory()
  {
    return _mapper.tokenStreamFactory();
  }
  





  public B enable(MapperFeature... features)
  {
    _mapper.enable(features);
    return _this();
  }
  
  public B disable(MapperFeature... features) {
    _mapper.disable(features);
    return _this();
  }
  
  public B configure(MapperFeature feature, boolean state) {
    _mapper.configure(feature, state);
    return _this();
  }
  
  public B enable(SerializationFeature... features) {
    for (SerializationFeature f : features) {
      _mapper.enable(f);
    }
    return _this();
  }
  
  public B disable(SerializationFeature... features) {
    for (SerializationFeature f : features) {
      _mapper.disable(f);
    }
    return _this();
  }
  
  public B configure(SerializationFeature feature, boolean state) {
    _mapper.configure(feature, state);
    return _this();
  }
  
  public B enable(DeserializationFeature... features) {
    for (DeserializationFeature f : features) {
      _mapper.enable(f);
    }
    return _this();
  }
  
  public B disable(DeserializationFeature... features) {
    for (DeserializationFeature f : features) {
      _mapper.disable(f);
    }
    return _this();
  }
  
  public B configure(DeserializationFeature feature, boolean state) {
    _mapper.configure(feature, state);
    return _this();
  }
  





  public B enable(JsonParser.Feature... features)
  {
    _mapper.enable(features);
    return _this();
  }
  
  public B disable(JsonParser.Feature... features) {
    _mapper.disable(features);
    return _this();
  }
  
  public B configure(JsonParser.Feature feature, boolean state) {
    _mapper.configure(feature, state);
    return _this();
  }
  
  public B enable(JsonGenerator.Feature... features) {
    _mapper.enable(features);
    return _this();
  }
  
  public B disable(JsonGenerator.Feature... features) {
    _mapper.disable(features);
    return _this();
  }
  
  public B configure(JsonGenerator.Feature feature, boolean state) {
    _mapper.configure(feature, state);
    return _this();
  }
  





  public B enable(StreamReadFeature... features)
  {
    for (StreamReadFeature f : features) {
      _mapper.enable(new JsonParser.Feature[] { f.mappedFeature() });
    }
    return _this();
  }
  
  public B disable(StreamReadFeature... features) {
    for (StreamReadFeature f : features) {
      _mapper.disable(new JsonParser.Feature[] { f.mappedFeature() });
    }
    return _this();
  }
  
  public B configure(StreamReadFeature feature, boolean state) {
    _mapper.configure(feature.mappedFeature(), state);
    return _this();
  }
  
  public B enable(StreamWriteFeature... features) {
    for (StreamWriteFeature f : features) {
      _mapper.enable(new JsonGenerator.Feature[] { f.mappedFeature() });
    }
    return _this();
  }
  
  public B disable(StreamWriteFeature... features) {
    for (StreamWriteFeature f : features) {
      _mapper.disable(new JsonGenerator.Feature[] { f.mappedFeature() });
    }
    return _this();
  }
  
  public B configure(StreamWriteFeature feature, boolean state) {
    _mapper.configure(feature.mappedFeature(), state);
    return _this();
  }
  






  public B addModule(Module module)
  {
    _mapper.registerModule(module);
    return _this();
  }
  
  public B addModules(Module... modules)
  {
    for (Module module : modules) {
      addModule(module);
    }
    return _this();
  }
  
  public B addModules(Iterable<? extends Module> modules)
  {
    for (Module module : modules) {
      addModule(module);
    }
    return _this();
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
    (ServiceLoader)java.security.AccessController.doPrivileged(new java.security.PrivilegedAction()
    {
      public ServiceLoader<T> run() {
        return val$classLoader == null ? 
          ServiceLoader.load(clazz) : ServiceLoader.load(clazz, val$classLoader);
      }
    });
  }
  









  public B findAndAddModules()
  {
    return addModules(findModules());
  }
  















  public B annotationIntrospector(AnnotationIntrospector intr)
  {
    _mapper.setAnnotationIntrospector(intr);
    return _this();
  }
  
  public B nodeFactory(JsonNodeFactory f) {
    _mapper.setNodeFactory(f);
    return _this();
  }
  





  public B typeFactory(TypeFactory f)
  {
    _mapper.setTypeFactory(f);
    return _this();
  }
  
  public B subtypeResolver(SubtypeResolver r) {
    _mapper.setSubtypeResolver(r);
    return _this();
  }
  
  public B visibility(VisibilityChecker<?> vc) {
    _mapper.setVisibility(vc);
    return _this();
  }
  
  public B visibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
    _mapper.setVisibility(forMethod, visibility);
    return _this();
  }
  






  public B handlerInstantiator(HandlerInstantiator hi)
  {
    _mapper.setHandlerInstantiator(hi);
    return _this();
  }
  
  public B propertyNamingStrategy(PropertyNamingStrategy s) {
    _mapper.setPropertyNamingStrategy(s);
    return _this();
  }
  





  public B serializerFactory(SerializerFactory f)
  {
    _mapper.setSerializerFactory(f);
    return _this();
  }
  







  public B filterProvider(FilterProvider prov)
  {
    _mapper.setFilterProvider(prov);
    return _this();
  }
  
  public B defaultPrettyPrinter(PrettyPrinter pp) {
    _mapper.setDefaultPrettyPrinter(pp);
    return _this();
  }
  





  public B injectableValues(InjectableValues v)
  {
    _mapper.setInjectableValues(v);
    return _this();
  }
  




  public B addHandler(DeserializationProblemHandler h)
  {
    _mapper.addHandler(h);
    return _this();
  }
  



  public B clearProblemHandlers()
  {
    _mapper.clearProblemHandlers();
    return _this();
  }
  





  public B defaultSetterInfo(JsonSetter.Value v)
  {
    _mapper.setDefaultSetterInfo(v);
    return _this();
  }
  




  public B defaultMergeable(Boolean b)
  {
    _mapper.setDefaultMergeable(b);
    return _this();
  }
  




  public B defaultLeniency(Boolean b)
  {
    _mapper.setDefaultLeniency(b);
    return _this();
  }
  











  public B defaultDateFormat(DateFormat df)
  {
    _mapper.setDateFormat(df);
    return _this();
  }
  



  public B defaultTimeZone(TimeZone tz)
  {
    _mapper.setTimeZone(tz);
    return _this();
  }
  



  public B defaultLocale(Locale locale)
  {
    _mapper.setLocale(locale);
    return _this();
  }
  













  public B defaultBase64Variant(Base64Variant v)
  {
    _mapper.setBase64Variant(v);
    return _this();
  }
  
  public B serializationInclusion(JsonInclude.Include incl) {
    _mapper.setSerializationInclusion(incl);
    return _this();
  }
  




















  public B addMixIn(Class<?> target, Class<?> mixinSource)
  {
    _mapper.addMixIn(target, mixinSource);
    return _this();
  }
  





  public B registerSubtypes(Class<?>... subtypes)
  {
    _mapper.registerSubtypes(subtypes);
    return _this();
  }
  
  public B registerSubtypes(NamedType... subtypes) {
    _mapper.registerSubtypes(subtypes);
    return _this();
  }
  
  public B registerSubtypes(Collection<Class<?>> subtypes) {
    _mapper.registerSubtypes(subtypes);
    return _this();
  }
  












  public B polymorphicTypeValidator(PolymorphicTypeValidator ptv)
  {
    _mapper.setPolymorphicTypeValidator(ptv);
    return _this();
  }
  














  public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator)
  {
    _mapper.activateDefaultTyping(subtypeValidator);
    return _this();
  }
  









  public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping dti)
  {
    _mapper.activateDefaultTyping(subtypeValidator, dti);
    return _this();
  }
  
















  public B activateDefaultTyping(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping applicability, JsonTypeInfo.As includeAs)
  {
    _mapper.activateDefaultTyping(subtypeValidator, applicability, includeAs);
    return _this();
  }
  












  public B activateDefaultTypingAsProperty(PolymorphicTypeValidator subtypeValidator, ObjectMapper.DefaultTyping applicability, String propertyName)
  {
    _mapper.activateDefaultTypingAsProperty(subtypeValidator, applicability, propertyName);
    return _this();
  }
  





  public B deactivateDefaultTyping()
  {
    _mapper.deactivateDefaultTyping();
    return _this();
  }
  






  protected final B _this()
  {
    return this;
  }
}
