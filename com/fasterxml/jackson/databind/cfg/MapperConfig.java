package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;









public abstract class MapperConfig<T extends MapperConfig<T>>
  implements ClassIntrospector.MixInResolver, Serializable
{
  private static final long serialVersionUID = 2L;
  protected static final JsonInclude.Value EMPTY_INCLUDE = ;
  



  protected static final JsonFormat.Value EMPTY_FORMAT = JsonFormat.Value.empty();
  




  protected final int _mapperFeatures;
  




  protected final BaseSettings _base;
  




  protected MapperConfig(BaseSettings base, int mapperFeatures)
  {
    _base = base;
    _mapperFeatures = mapperFeatures;
  }
  
  protected MapperConfig(MapperConfig<T> src, int mapperFeatures)
  {
    _base = _base;
    _mapperFeatures = mapperFeatures;
  }
  
  protected MapperConfig(MapperConfig<T> src, BaseSettings base)
  {
    _base = base;
    _mapperFeatures = _mapperFeatures;
  }
  
  protected MapperConfig(MapperConfig<T> src)
  {
    _base = _base;
    _mapperFeatures = _mapperFeatures;
  }
  




  public static <F extends Enum<F>,  extends ConfigFeature> int collectFeatureDefaults(Class<F> enumClass)
  {
    int flags = 0;
    for (F value : (Enum[])enumClass.getEnumConstants()) {
      if (((ConfigFeature)value).enabledByDefault()) {
        flags |= ((ConfigFeature)value).getMask();
      }
    }
    return flags;
  }
  







  public abstract T with(MapperFeature... paramVarArgs);
  







  public abstract T without(MapperFeature... paramVarArgs);
  






  public abstract T with(MapperFeature paramMapperFeature, boolean paramBoolean);
  






  public final boolean isEnabled(MapperFeature f)
  {
    return (_mapperFeatures & f.getMask()) != 0;
  }
  





  public final boolean hasMapperFeatures(int featureMask)
  {
    return (_mapperFeatures & featureMask) == featureMask;
  }
  





  public final boolean isAnnotationProcessingEnabled()
  {
    return isEnabled(MapperFeature.USE_ANNOTATIONS);
  }
  










  public final boolean canOverrideAccessModifiers()
  {
    return isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
  }
  



  public final boolean shouldSortPropertiesAlphabetically()
  {
    return isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
  }
  













  public abstract boolean useRootWrapping();
  












  public SerializableString compileString(String src)
  {
    return new SerializedString(src);
  }
  





  public ClassIntrospector getClassIntrospector()
  {
    return _base.getClassIntrospector();
  }
  





  public AnnotationIntrospector getAnnotationIntrospector()
  {
    if (isEnabled(MapperFeature.USE_ANNOTATIONS)) {
      return _base.getAnnotationIntrospector();
    }
    return NopAnnotationIntrospector.instance;
  }
  
  public final PropertyNamingStrategy getPropertyNamingStrategy() {
    return _base.getPropertyNamingStrategy();
  }
  
  public final HandlerInstantiator getHandlerInstantiator() {
    return _base.getHandlerInstantiator();
  }
  











  public final TypeResolverBuilder<?> getDefaultTyper(JavaType baseType)
  {
    return _base.getTypeResolverBuilder();
  }
  

  public abstract SubtypeResolver getSubtypeResolver();
  

  public PolymorphicTypeValidator getPolymorphicTypeValidator()
  {
    return _base.getPolymorphicTypeValidator();
  }
  
  public final TypeFactory getTypeFactory() {
    return _base.getTypeFactory();
  }
  







  public final JavaType constructType(Class<?> cls)
  {
    return getTypeFactory().constructType(cls);
  }
  







  public final JavaType constructType(TypeReference<?> valueTypeRef)
  {
    return getTypeFactory().constructType(valueTypeRef.getType());
  }
  
  public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
    return getTypeFactory().constructSpecializedType(baseType, subclass);
  }
  









  public BeanDescription introspectClassAnnotations(Class<?> cls)
  {
    return introspectClassAnnotations(constructType(cls));
  }
  



  public BeanDescription introspectClassAnnotations(JavaType type)
  {
    return getClassIntrospector().forClassAnnotations(this, type, this);
  }
  




  public BeanDescription introspectDirectClassAnnotations(Class<?> cls)
  {
    return introspectDirectClassAnnotations(constructType(cls));
  }
  




  public final BeanDescription introspectDirectClassAnnotations(JavaType type)
  {
    return getClassIntrospector().forDirectClassAnnotations(this, type, this);
  }
  











  public abstract ConfigOverride findConfigOverride(Class<?> paramClass);
  











  public abstract ConfigOverride getConfigOverride(Class<?> paramClass);
  











  public abstract JsonInclude.Value getDefaultPropertyInclusion();
  










  public abstract JsonInclude.Value getDefaultPropertyInclusion(Class<?> paramClass);
  










  public JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType, JsonInclude.Value defaultIncl)
  {
    JsonInclude.Value v = getConfigOverride(baseType).getInclude();
    if (v != null) {
      return v;
    }
    return defaultIncl;
  }
  













  public abstract JsonInclude.Value getDefaultInclusion(Class<?> paramClass1, Class<?> paramClass2);
  













  public JsonInclude.Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType, JsonInclude.Value defaultIncl)
  {
    JsonInclude.Value baseOverride = getConfigOverride(baseType).getInclude();
    JsonInclude.Value propOverride = getConfigOverride(propertyType).getIncludeAsProperty();
    
    JsonInclude.Value result = JsonInclude.Value.mergeAll(new JsonInclude.Value[] { defaultIncl, baseOverride, propOverride });
    return result;
  }
  










  public abstract JsonFormat.Value getDefaultPropertyFormat(Class<?> paramClass);
  










  public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> paramClass);
  










  public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> paramClass, AnnotatedClass paramAnnotatedClass);
  










  public abstract VisibilityChecker<?> getDefaultVisibilityChecker();
  










  public abstract VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> paramClass, AnnotatedClass paramAnnotatedClass);
  










  public abstract JsonSetter.Value getDefaultSetterInfo();
  










  public abstract Boolean getDefaultMergeable();
  









  public abstract Boolean getDefaultMergeable(Class<?> paramClass);
  









  public final DateFormat getDateFormat()
  {
    return _base.getDateFormat();
  }
  


  public final Locale getLocale()
  {
    return _base.getLocale();
  }
  


  public final TimeZone getTimeZone()
  {
    return _base.getTimeZone();
  }
  




  public abstract Class<?> getActiveView();
  




  public Base64Variant getBase64Variant()
  {
    return _base.getBase64Variant();
  }
  






  public abstract ContextAttributes getAttributes();
  






  public abstract PropertyName findRootName(JavaType paramJavaType);
  






  public abstract PropertyName findRootName(Class<?> paramClass);
  






  public TypeResolverBuilder<?> typeResolverBuilderInstance(Annotated annotated, Class<? extends TypeResolverBuilder<?>> builderClass)
  {
    HandlerInstantiator hi = getHandlerInstantiator();
    if (hi != null) {
      TypeResolverBuilder<?> builder = hi.typeResolverBuilderInstance(this, annotated, builderClass);
      if (builder != null) {
        return builder;
      }
    }
    return (TypeResolverBuilder)ClassUtil.createInstance(builderClass, canOverrideAccessModifiers());
  }
  





  public TypeIdResolver typeIdResolverInstance(Annotated annotated, Class<? extends TypeIdResolver> resolverClass)
  {
    HandlerInstantiator hi = getHandlerInstantiator();
    if (hi != null) {
      TypeIdResolver builder = hi.typeIdResolverInstance(this, annotated, resolverClass);
      if (builder != null) {
        return builder;
      }
    }
    return (TypeIdResolver)ClassUtil.createInstance(resolverClass, canOverrideAccessModifiers());
  }
}
