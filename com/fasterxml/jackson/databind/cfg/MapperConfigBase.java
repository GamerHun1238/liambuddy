package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements java.io.Serializable
{
  protected static final ConfigOverride EMPTY_OVERRIDE = ;
  
  private static final int DEFAULT_MAPPER_FEATURES = collectFeatureDefaults(MapperFeature.class);
  



  private static final int AUTO_DETECT_MASK = MapperFeature.AUTO_DETECT_FIELDS
    .getMask() | MapperFeature.AUTO_DETECT_GETTERS
    .getMask() | MapperFeature.AUTO_DETECT_IS_GETTERS
    .getMask() | MapperFeature.AUTO_DETECT_SETTERS
    .getMask() | MapperFeature.AUTO_DETECT_CREATORS
    .getMask();
  








  protected final SimpleMixInResolver _mixIns;
  








  protected final SubtypeResolver _subtypeResolver;
  








  protected final PropertyName _rootName;
  








  protected final Class<?> _view;
  








  protected final ContextAttributes _attributes;
  








  protected final RootNameLookup _rootNames;
  








  protected final ConfigOverrides _configOverrides;
  









  protected MapperConfigBase(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides)
  {
    super(base, DEFAULT_MAPPER_FEATURES);
    _mixIns = mixins;
    _subtypeResolver = str;
    _rootNames = rootNames;
    _rootName = null;
    _view = null;
    
    _attributes = ContextAttributes.getEmpty();
    _configOverrides = configOverrides;
  }
  










  protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides)
  {
    super(src, _base.copy());
    _mixIns = mixins;
    _subtypeResolver = _subtypeResolver;
    _rootNames = rootNames;
    _rootName = _rootName;
    _view = _view;
    _attributes = _attributes;
    _configOverrides = configOverrides;
  }
  




  protected MapperConfigBase(MapperConfigBase<CFG, T> src)
  {
    super(src);
    _mixIns = _mixIns;
    _subtypeResolver = _subtypeResolver;
    _rootNames = _rootNames;
    _rootName = _rootName;
    _view = _view;
    _attributes = _attributes;
    _configOverrides = _configOverrides;
  }
  
  protected MapperConfigBase(MapperConfigBase<CFG, T> src, BaseSettings base)
  {
    super(src, base);
    _mixIns = _mixIns;
    _subtypeResolver = _subtypeResolver;
    _rootNames = _rootNames;
    _rootName = _rootName;
    _view = _view;
    _attributes = _attributes;
    _configOverrides = _configOverrides;
  }
  
  protected MapperConfigBase(MapperConfigBase<CFG, T> src, int mapperFeatures)
  {
    super(src, mapperFeatures);
    _mixIns = _mixIns;
    _subtypeResolver = _subtypeResolver;
    _rootNames = _rootNames;
    _rootName = _rootName;
    _view = _view;
    _attributes = _attributes;
    _configOverrides = _configOverrides;
  }
  
  protected MapperConfigBase(MapperConfigBase<CFG, T> src, SubtypeResolver str) {
    super(src);
    _mixIns = _mixIns;
    _subtypeResolver = str;
    _rootNames = _rootNames;
    _rootName = _rootName;
    _view = _view;
    _attributes = _attributes;
    _configOverrides = _configOverrides;
  }
  
  protected MapperConfigBase(MapperConfigBase<CFG, T> src, PropertyName rootName) {
    super(src);
    _mixIns = _mixIns;
    _subtypeResolver = _subtypeResolver;
    _rootNames = _rootNames;
    _rootName = rootName;
    _view = _view;
    _attributes = _attributes;
    _configOverrides = _configOverrides;
  }
  
  protected MapperConfigBase(MapperConfigBase<CFG, T> src, Class<?> view)
  {
    super(src);
    _mixIns = _mixIns;
    _subtypeResolver = _subtypeResolver;
    _rootNames = _rootNames;
    _rootName = _rootName;
    _view = view;
    _attributes = _attributes;
    _configOverrides = _configOverrides;
  }
  



  protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins)
  {
    super(src);
    _mixIns = mixins;
    _subtypeResolver = _subtypeResolver;
    _rootNames = _rootNames;
    _rootName = _rootName;
    _view = _view;
    _attributes = _attributes;
    _configOverrides = _configOverrides;
  }
  



  protected MapperConfigBase(MapperConfigBase<CFG, T> src, ContextAttributes attr)
  {
    super(src);
    _mixIns = _mixIns;
    _subtypeResolver = _subtypeResolver;
    _rootNames = _rootNames;
    _rootName = _rootName;
    _view = _view;
    _attributes = attr;
    _configOverrides = _configOverrides;
  }
  








  protected abstract T _withBase(BaseSettings paramBaseSettings);
  








  protected abstract T _withMapperFeatures(int paramInt);
  








  public final T with(MapperFeature... features)
  {
    int newMapperFlags = _mapperFeatures;
    for (MapperFeature f : features) {
      newMapperFlags |= f.getMask();
    }
    if (newMapperFlags == _mapperFeatures) {
      return this;
    }
    return _withMapperFeatures(newMapperFlags);
  }
  






  public final T without(MapperFeature... features)
  {
    int newMapperFlags = _mapperFeatures;
    for (MapperFeature f : features) {
      newMapperFlags &= (f.getMask() ^ 0xFFFFFFFF);
    }
    if (newMapperFlags == _mapperFeatures) {
      return this;
    }
    return _withMapperFeatures(newMapperFlags);
  }
  

  public final T with(MapperFeature feature, boolean state)
  {
    int newMapperFlags;
    int newMapperFlags;
    if (state) {
      newMapperFlags = _mapperFeatures | feature.getMask();
    } else {
      newMapperFlags = _mapperFeatures & (feature.getMask() ^ 0xFFFFFFFF);
    }
    if (newMapperFlags == _mapperFeatures) {
      return this;
    }
    return _withMapperFeatures(newMapperFlags);
  }
  












  public final T with(AnnotationIntrospector ai)
  {
    return _withBase(_base.withAnnotationIntrospector(ai));
  }
  



  public final T withAppendedAnnotationIntrospector(AnnotationIntrospector ai)
  {
    return _withBase(_base.withAppendedAnnotationIntrospector(ai));
  }
  



  public final T withInsertedAnnotationIntrospector(AnnotationIntrospector ai)
  {
    return _withBase(_base.withInsertedAnnotationIntrospector(ai));
  }
  







  public final T with(ClassIntrospector ci)
  {
    return _withBase(_base.withClassIntrospector(ci));
  }
  









  public abstract T with(ContextAttributes paramContextAttributes);
  








  public T withAttributes(Map<?, ?> attributes)
  {
    return with(getAttributes().withSharedAttributes(attributes));
  }
  





  public T withAttribute(Object key, Object value)
  {
    return with(getAttributes().withSharedAttribute(key, value));
  }
  





  public T withoutAttribute(Object key)
  {
    return with(getAttributes().withoutSharedAttribute(key));
  }
  










  public final T with(TypeFactory tf)
  {
    return _withBase(_base.withTypeFactory(tf));
  }
  



  public final T with(TypeResolverBuilder<?> trb)
  {
    return _withBase(_base.withTypeResolverBuilder(trb));
  }
  







  public final T with(PropertyNamingStrategy pns)
  {
    return _withBase(_base.withPropertyNamingStrategy(pns));
  }
  







  public final T with(HandlerInstantiator hi)
  {
    return _withBase(_base.withHandlerInstantiator(hi));
  }
  









  public final T with(Base64Variant base64)
  {
    return _withBase(_base.with(base64));
  }
  






  public T with(DateFormat df)
  {
    return _withBase(_base.withDateFormat(df));
  }
  



  public final T with(Locale l)
  {
    return _withBase(_base.with(l));
  }
  



  public final T with(TimeZone tz)
  {
    return _withBase(_base.with(tz));
  }
  








  public abstract T withRootName(PropertyName paramPropertyName);
  







  public T withRootName(String rootName)
  {
    if (rootName == null) {
      return withRootName((PropertyName)null);
    }
    return withRootName(PropertyName.construct(rootName));
  }
  








  public abstract T with(SubtypeResolver paramSubtypeResolver);
  








  public abstract T withView(Class<?> paramClass);
  







  public final SubtypeResolver getSubtypeResolver()
  {
    return _subtypeResolver;
  }
  


  @Deprecated
  public final String getRootName()
  {
    return _rootName == null ? null : _rootName.getSimpleName();
  }
  


  public final PropertyName getFullRootName()
  {
    return _rootName;
  }
  
  public final Class<?> getActiveView()
  {
    return _view;
  }
  
  public final ContextAttributes getAttributes()
  {
    return _attributes;
  }
  






  public final ConfigOverride getConfigOverride(Class<?> type)
  {
    ConfigOverride override = _configOverrides.findOverride(type);
    return override == null ? EMPTY_OVERRIDE : override;
  }
  
  public final ConfigOverride findConfigOverride(Class<?> type)
  {
    return _configOverrides.findOverride(type);
  }
  
  public final JsonInclude.Value getDefaultPropertyInclusion()
  {
    return _configOverrides.getDefaultInclusion();
  }
  
  public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType)
  {
    JsonInclude.Value v = getConfigOverride(baseType).getInclude();
    JsonInclude.Value def = getDefaultPropertyInclusion();
    if (def == null) {
      return v;
    }
    return def.withOverrides(v);
  }
  

  public final JsonInclude.Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType)
  {
    JsonInclude.Value v = getConfigOverride(propertyType).getIncludeAsProperty();
    JsonInclude.Value def = getDefaultPropertyInclusion(baseType);
    if (def == null) {
      return v;
    }
    return def.withOverrides(v);
  }
  
  public final JsonFormat.Value getDefaultPropertyFormat(Class<?> type)
  {
    return _configOverrides.findFormatDefaults(type);
  }
  
  public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> type)
  {
    ConfigOverride overrides = _configOverrides.findOverride(type);
    if (overrides != null) {
      JsonIgnoreProperties.Value v = overrides.getIgnorals();
      if (v != null) {
        return v;
      }
    }
    

    return null;
  }
  


  public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> baseType, AnnotatedClass actualClass)
  {
    AnnotationIntrospector intr = getAnnotationIntrospector();
    
    JsonIgnoreProperties.Value base = intr == null ? null : intr.findPropertyIgnorals(actualClass);
    JsonIgnoreProperties.Value overrides = getDefaultPropertyIgnorals(baseType);
    return JsonIgnoreProperties.Value.merge(base, overrides);
  }
  

  public final VisibilityChecker<?> getDefaultVisibilityChecker()
  {
    VisibilityChecker<?> vchecker = _configOverrides.getDefaultVisibility();
    

    if ((_mapperFeatures & AUTO_DETECT_MASK) != AUTO_DETECT_MASK) {
      if (!isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
        vchecker = vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
      }
      if (!isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
        vchecker = vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
      }
      if (!isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
        vchecker = vchecker.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
      }
      if (!isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
        vchecker = vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE);
      }
      if (!isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
        vchecker = vchecker.withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
      }
    }
    return vchecker;
  }
  

  public final VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> baseType, AnnotatedClass actualClass)
  {
    VisibilityChecker<?> vc = getDefaultVisibilityChecker();
    AnnotationIntrospector intr = getAnnotationIntrospector();
    if (intr != null) {
      vc = intr.findAutoDetectVisibility(actualClass, vc);
    }
    ConfigOverride overrides = _configOverrides.findOverride(baseType);
    if (overrides != null) {
      vc = vc.withOverrides(overrides.getVisibility());
    }
    return vc;
  }
  
  public final JsonSetter.Value getDefaultSetterInfo()
  {
    return _configOverrides.getDefaultSetterInfo();
  }
  
  public Boolean getDefaultMergeable()
  {
    return _configOverrides.getDefaultMergeable();
  }
  

  public Boolean getDefaultMergeable(Class<?> baseType)
  {
    ConfigOverride cfg = _configOverrides.findOverride(baseType);
    if (cfg != null) {
      Boolean b = cfg.getMergeable();
      if (b != null) {
        return b;
      }
    }
    return _configOverrides.getDefaultMergeable();
  }
  






  public PropertyName findRootName(JavaType rootType)
  {
    if (_rootName != null) {
      return _rootName;
    }
    return _rootNames.findRootName(rootType, this);
  }
  
  public PropertyName findRootName(Class<?> rawRootType)
  {
    if (_rootName != null) {
      return _rootName;
    }
    return _rootNames.findRootName(rawRootType, this);
  }
  










  public final Class<?> findMixInClassFor(Class<?> cls)
  {
    return _mixIns.findMixInClassFor(cls);
  }
  

  public ClassIntrospector.MixInResolver copy()
  {
    throw new UnsupportedOperationException();
  }
  



  public final int mixInCount()
  {
    return _mixIns.localSize();
  }
}
