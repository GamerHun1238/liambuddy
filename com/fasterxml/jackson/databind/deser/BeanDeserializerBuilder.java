package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.Annotations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



















public class BeanDeserializerBuilder
{
  protected final DeserializationConfig _config;
  protected final DeserializationContext _context;
  protected final BeanDescription _beanDesc;
  protected final Map<String, SettableBeanProperty> _properties = new LinkedHashMap();
  




  protected List<ValueInjector> _injectables;
  




  protected HashMap<String, SettableBeanProperty> _backRefProperties;
  




  protected HashSet<String> _ignorableProps;
  




  protected ValueInstantiator _valueInstantiator;
  




  protected ObjectIdReader _objectIdReader;
  




  protected SettableAnyProperty _anySetter;
  




  protected boolean _ignoreAllUnknown;
  




  protected AnnotatedMethod _buildMethod;
  




  protected JsonPOJOBuilder.Value _builderConfig;
  





  public BeanDeserializerBuilder(BeanDescription beanDesc, DeserializationContext ctxt)
  {
    _beanDesc = beanDesc;
    _context = ctxt;
    _config = ctxt.getConfig();
  }
  




  protected BeanDeserializerBuilder(BeanDeserializerBuilder src)
  {
    _beanDesc = _beanDesc;
    _context = _context;
    _config = _config;
    

    _properties.putAll(_properties);
    _injectables = _copy(_injectables);
    _backRefProperties = _copy(_backRefProperties);
    
    _ignorableProps = _ignorableProps;
    _valueInstantiator = _valueInstantiator;
    _objectIdReader = _objectIdReader;
    
    _anySetter = _anySetter;
    _ignoreAllUnknown = _ignoreAllUnknown;
    
    _buildMethod = _buildMethod;
    _builderConfig = _builderConfig;
  }
  
  private static HashMap<String, SettableBeanProperty> _copy(HashMap<String, SettableBeanProperty> src) {
    return src == null ? null : new HashMap(src);
  }
  
  private static <T> List<T> _copy(List<T> src)
  {
    return src == null ? null : new ArrayList(src);
  }
  








  public void addOrReplaceProperty(SettableBeanProperty prop, boolean allowOverride)
  {
    _properties.put(prop.getName(), prop);
  }
  





  public void addProperty(SettableBeanProperty prop)
  {
    SettableBeanProperty old = (SettableBeanProperty)_properties.put(prop.getName(), prop);
    if ((old != null) && (old != prop)) {
      throw new IllegalArgumentException("Duplicate property '" + prop.getName() + "' for " + _beanDesc.getType());
    }
  }
  





  public void addBackReferenceProperty(String referenceName, SettableBeanProperty prop)
  {
    if (_backRefProperties == null) {
      _backRefProperties = new HashMap(4);
    }
    


    prop.fixAccess(_config);
    _backRefProperties.put(referenceName, prop);
  }
  











  public void addInjectable(PropertyName propName, JavaType propType, Annotations contextAnnotations, AnnotatedMember member, Object valueId)
  {
    if (_injectables == null) {
      _injectables = new ArrayList();
    }
    boolean fixAccess = _config.canOverrideAccessModifiers();
    boolean forceAccess = (fixAccess) && (_config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    if (fixAccess) {
      member.fixAccess(forceAccess);
    }
    _injectables.add(new ValueInjector(propName, propType, member, valueId));
  }
  




  public void addIgnorable(String propName)
  {
    if (_ignorableProps == null) {
      _ignorableProps = new HashSet();
    }
    _ignorableProps.add(propName);
  }
  










  public void addCreatorProperty(SettableBeanProperty prop)
  {
    addProperty(prop);
  }
  
  public void setAnySetter(SettableAnyProperty s)
  {
    if ((_anySetter != null) && (s != null)) {
      throw new IllegalStateException("_anySetter already set to non-null");
    }
    _anySetter = s;
  }
  
  public void setIgnoreUnknownProperties(boolean ignore) {
    _ignoreAllUnknown = ignore;
  }
  
  public void setValueInstantiator(ValueInstantiator inst) {
    _valueInstantiator = inst;
  }
  
  public void setObjectIdReader(ObjectIdReader r) {
    _objectIdReader = r;
  }
  
  public void setPOJOBuilder(AnnotatedMethod buildMethod, JsonPOJOBuilder.Value config) {
    _buildMethod = buildMethod;
    _builderConfig = config;
  }
  













  public Iterator<SettableBeanProperty> getProperties()
  {
    return _properties.values().iterator();
  }
  
  public SettableBeanProperty findProperty(PropertyName propertyName) {
    return (SettableBeanProperty)_properties.get(propertyName.getSimpleName());
  }
  
  public boolean hasProperty(PropertyName propertyName) {
    return findProperty(propertyName) != null;
  }
  
  public SettableBeanProperty removeProperty(PropertyName name) {
    return (SettableBeanProperty)_properties.remove(name.getSimpleName());
  }
  
  public SettableAnyProperty getAnySetter() {
    return _anySetter;
  }
  
  public ValueInstantiator getValueInstantiator() {
    return _valueInstantiator;
  }
  
  public List<ValueInjector> getInjectables() {
    return _injectables;
  }
  
  public ObjectIdReader getObjectIdReader() {
    return _objectIdReader;
  }
  
  public AnnotatedMethod getBuildMethod() {
    return _buildMethod;
  }
  
  public JsonPOJOBuilder.Value getBuilderConfig() {
    return _builderConfig;
  }
  


  public boolean hasIgnorable(String name)
  {
    return (_ignorableProps != null) && (_ignorableProps.contains(name));
  }
  










  public JsonDeserializer<?> build()
  {
    Collection<SettableBeanProperty> props = _properties.values();
    _fixAccess(props);
    BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, _config
      .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), 
      _collectAliases(props));
    propertyMap.assignIndexes();
    



    boolean anyViews = !_config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
    if (!anyViews) {
      for (SettableBeanProperty prop : props) {
        if (prop.hasViews()) {
          anyViews = true;
          break;
        }
      }
    }
    

    if (_objectIdReader != null)
    {



      ObjectIdValueProperty prop = new ObjectIdValueProperty(_objectIdReader, PropertyMetadata.STD_REQUIRED);
      propertyMap = propertyMap.withProperty(prop);
    }
    
    return new BeanDeserializer(this, _beanDesc, propertyMap, _backRefProperties, _ignorableProps, _ignoreAllUnknown, anyViews);
  }
  








  public AbstractDeserializer buildAbstract()
  {
    return new AbstractDeserializer(this, _beanDesc, _backRefProperties, _properties);
  }
  





  public JsonDeserializer<?> buildBuilderBased(JavaType valueType, String expBuildMethodName)
    throws JsonMappingException
  {
    if (_buildMethod == null)
    {
      if (!expBuildMethodName.isEmpty()) {
        _context.reportBadDefinition(_beanDesc.getType(), 
          String.format("Builder class %s does not have build method (name: '%s')", new Object[] {_beanDesc
          .getBeanClass().getName(), expBuildMethodName }));
      }
    }
    else
    {
      Class<?> rawBuildType = _buildMethod.getRawReturnType();
      Class<?> rawValueType = valueType.getRawClass();
      if ((rawBuildType != rawValueType) && 
        (!rawBuildType.isAssignableFrom(rawValueType)) && 
        (!rawValueType.isAssignableFrom(rawBuildType))) {
        _context.reportBadDefinition(_beanDesc.getType(), 
          String.format("Build method '%s' has wrong return type (%s), not compatible with POJO type (%s)", new Object[] {_buildMethod
          .getFullName(), rawBuildType
          .getName(), valueType
          .getRawClass().getName() }));
      }
    }
    
    Collection<SettableBeanProperty> props = _properties.values();
    _fixAccess(props);
    BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, _config
      .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), 
      _collectAliases(props));
    propertyMap.assignIndexes();
    
    boolean anyViews = !_config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
    
    if (!anyViews) {
      for (SettableBeanProperty prop : props) {
        if (prop.hasViews()) {
          anyViews = true;
          break;
        }
      }
    }
    
    if (_objectIdReader != null)
    {

      ObjectIdValueProperty prop = new ObjectIdValueProperty(_objectIdReader, PropertyMetadata.STD_REQUIRED);
      
      propertyMap = propertyMap.withProperty(prop);
    }
    
    return createBuilderBasedDeserializer(valueType, propertyMap, anyViews);
  }
  





  protected JsonDeserializer<?> createBuilderBasedDeserializer(JavaType valueType, BeanPropertyMap propertyMap, boolean anyViews)
  {
    return new BuilderBasedDeserializer(this, _beanDesc, valueType, propertyMap, _backRefProperties, _ignorableProps, _ignoreAllUnknown, anyViews);
  }
  




















  protected void _fixAccess(Collection<SettableBeanProperty> mainProps)
  {
    for (SettableBeanProperty prop : mainProps)
    {





      prop.fixAccess(_config);
    }
    








    if (_anySetter != null) {
      _anySetter.fixAccess(_config);
    }
    if (_buildMethod != null) {
      _buildMethod.fixAccess(_config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
  }
  
  protected Map<String, List<PropertyName>> _collectAliases(Collection<SettableBeanProperty> props)
  {
    Map<String, List<PropertyName>> mapping = null;
    AnnotationIntrospector intr = _config.getAnnotationIntrospector();
    if (intr != null)
      for (SettableBeanProperty prop : props) {
        List<PropertyName> aliases = intr.findPropertyAliases(prop.getMember());
        if ((aliases != null) && (!aliases.isEmpty()))
        {

          if (mapping == null) {
            mapping = new HashMap();
          }
          mapping.put(prop.getName(), aliases);
        }
      }
    if (mapping == null) {
      return Collections.emptyMap();
    }
    return mapping;
  }
}
