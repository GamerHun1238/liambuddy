package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.Converter.None;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BasicBeanDescription extends BeanDescription
{
  private static final Class<?>[] NO_VIEWS = new Class[0];
  





  protected final POJOPropertiesCollector _propCollector;
  





  protected final MapperConfig<?> _config;
  





  protected final AnnotationIntrospector _annotationIntrospector;
  





  protected final AnnotatedClass _classInfo;
  





  protected Class<?>[] _defaultViews;
  





  protected boolean _defaultViewsResolved;
  





  protected List<BeanPropertyDefinition> _properties;
  





  protected ObjectIdInfo _objectIdInfo;
  





  protected BasicBeanDescription(POJOPropertiesCollector coll, JavaType type, AnnotatedClass classDef)
  {
    super(type);
    _propCollector = coll;
    _config = coll.getConfig();
    
    if (_config == null) {
      _annotationIntrospector = null;
    } else {
      _annotationIntrospector = _config.getAnnotationIntrospector();
    }
    _classInfo = classDef;
  }
  





  protected BasicBeanDescription(MapperConfig<?> config, JavaType type, AnnotatedClass classDef, List<BeanPropertyDefinition> props)
  {
    super(type);
    _propCollector = null;
    _config = config;
    
    if (_config == null) {
      _annotationIntrospector = null;
    } else {
      _annotationIntrospector = _config.getAnnotationIntrospector();
    }
    _classInfo = classDef;
    _properties = props;
  }
  
  protected BasicBeanDescription(POJOPropertiesCollector coll)
  {
    this(coll, coll.getType(), coll.getClassDef());
    _objectIdInfo = coll.getObjectIdInfo();
  }
  



  public static BasicBeanDescription forDeserialization(POJOPropertiesCollector coll)
  {
    return new BasicBeanDescription(coll);
  }
  



  public static BasicBeanDescription forSerialization(POJOPropertiesCollector coll)
  {
    return new BasicBeanDescription(coll);
  }
  






  public static BasicBeanDescription forOtherUse(MapperConfig<?> config, JavaType type, AnnotatedClass ac)
  {
    return new BasicBeanDescription(config, type, ac, 
      Collections.emptyList());
  }
  
  protected List<BeanPropertyDefinition> _properties() {
    if (_properties == null) {
      _properties = _propCollector.getProperties();
    }
    return _properties;
  }
  













  public boolean removeProperty(String propName)
  {
    Iterator<BeanPropertyDefinition> it = _properties().iterator();
    while (it.hasNext()) {
      BeanPropertyDefinition prop = (BeanPropertyDefinition)it.next();
      if (prop.getName().equals(propName)) {
        it.remove();
        return true;
      }
    }
    return false;
  }
  

  public boolean addProperty(BeanPropertyDefinition def)
  {
    if (hasProperty(def.getFullName())) {
      return false;
    }
    _properties().add(def);
    return true;
  }
  


  public boolean hasProperty(PropertyName name)
  {
    return findProperty(name) != null;
  }
  



  public BeanPropertyDefinition findProperty(PropertyName name)
  {
    for (BeanPropertyDefinition prop : _properties()) {
      if (prop.hasName(name)) {
        return prop;
      }
    }
    return null;
  }
  





  public AnnotatedClass getClassInfo()
  {
    return _classInfo;
  }
  
  public ObjectIdInfo getObjectIdInfo() { return _objectIdInfo; }
  
  public List<BeanPropertyDefinition> findProperties()
  {
    return _properties();
  }
  
  @Deprecated
  public AnnotatedMethod findJsonValueMethod()
  {
    return _propCollector == null ? null : _propCollector
      .getJsonValueMethod();
  }
  
  public AnnotatedMember findJsonValueAccessor()
  {
    return _propCollector == null ? null : _propCollector
      .getJsonValueAccessor();
  }
  

  public Set<String> getIgnoredPropertyNames()
  {
    Set<String> ign = _propCollector == null ? null : _propCollector.getIgnoredPropertyNames();
    if (ign == null) {
      return Collections.emptySet();
    }
    return ign;
  }
  
  public boolean hasKnownClassAnnotations()
  {
    return _classInfo.hasAnnotations();
  }
  
  public com.fasterxml.jackson.databind.util.Annotations getClassAnnotations()
  {
    return _classInfo.getAnnotations();
  }
  
  @Deprecated
  public com.fasterxml.jackson.databind.type.TypeBindings bindingsForBeanType()
  {
    return _type.getBindings();
  }
  
  @Deprecated
  public JavaType resolveType(Type jdkType)
  {
    if (jdkType == null) {
      return null;
    }
    return _config.getTypeFactory().constructType(jdkType, _type.getBindings());
  }
  
  public AnnotatedConstructor findDefaultConstructor()
  {
    return _classInfo.getDefaultConstructor();
  }
  
  public AnnotatedMember findAnySetterAccessor()
    throws IllegalArgumentException
  {
    if (_propCollector != null) {
      AnnotatedMethod anyMethod = _propCollector.getAnySetterMethod();
      if (anyMethod != null)
      {





        Class<?> type = anyMethod.getRawParameterType(0);
        if ((type != String.class) && (type != Object.class)) {
          throw new IllegalArgumentException(String.format("Invalid 'any-setter' annotation on method '%s()': first argument not of type String or Object, but %s", new Object[] {anyMethod
          
            .getName(), type.getName() }));
        }
        return anyMethod;
      }
      AnnotatedMember anyField = _propCollector.getAnySetterField();
      if (anyField != null)
      {

        Class<?> type = anyField.getRawType();
        if (!Map.class.isAssignableFrom(type)) {
          throw new IllegalArgumentException(String.format("Invalid 'any-setter' annotation on field '%s': type is not instance of java.util.Map", new Object[] {anyField
          
            .getName() }));
        }
        return anyField;
      }
    }
    return null;
  }
  
  public Map<Object, AnnotatedMember> findInjectables()
  {
    if (_propCollector != null) {
      return _propCollector.getInjectables();
    }
    return Collections.emptyMap();
  }
  
  public List<AnnotatedConstructor> getConstructors()
  {
    return _classInfo.getConstructors();
  }
  
  public Object instantiateBean(boolean fixAccess)
  {
    AnnotatedConstructor ac = _classInfo.getDefaultConstructor();
    if (ac == null) {
      return null;
    }
    if (fixAccess) {
      ac.fixAccess(_config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    try {
      return ac.getAnnotated().newInstance(new Object[0]);
    } catch (Exception e) {
      Throwable t = e;
      while (t.getCause() != null) {
        t = t.getCause();
      }
      ClassUtil.throwIfError(t);
      ClassUtil.throwIfRTE(t);
      

      throw new IllegalArgumentException("Failed to instantiate bean of type " + _classInfo.getAnnotated().getName() + ": (" + t.getClass().getName() + ") " + ClassUtil.exceptionMessage(t), t);
    }
  }
  






  public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes)
  {
    return _classInfo.findMethod(name, paramTypes);
  }
  









  public JsonFormat.Value findExpectedFormat(JsonFormat.Value defValue)
  {
    if (_annotationIntrospector != null) {
      JsonFormat.Value v = _annotationIntrospector.findFormat(_classInfo);
      if (v != null) {
        if (defValue == null) {
          defValue = v;
        } else {
          defValue = defValue.withOverrides(v);
        }
      }
    }
    JsonFormat.Value v = _config.getDefaultPropertyFormat(_classInfo.getRawType());
    if (v != null) {
      if (defValue == null) {
        defValue = v;
      } else {
        defValue = defValue.withOverrides(v);
      }
    }
    return defValue;
  }
  

  public Class<?>[] findDefaultViews()
  {
    if (!_defaultViewsResolved) {
      _defaultViewsResolved = true;
      
      Class<?>[] def = _annotationIntrospector == null ? null : _annotationIntrospector.findViews(_classInfo);
      
      if ((def == null) && 
        (!_config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION))) {
        def = NO_VIEWS;
      }
      
      _defaultViews = def;
    }
    return _defaultViews;
  }
  







  public Converter<Object, Object> findSerializationConverter()
  {
    if (_annotationIntrospector == null) {
      return null;
    }
    return _createConverter(_annotationIntrospector.findSerializationConverter(_classInfo));
  }
  






  public JsonInclude.Value findPropertyInclusion(JsonInclude.Value defValue)
  {
    if (_annotationIntrospector != null) {
      JsonInclude.Value incl = _annotationIntrospector.findPropertyInclusion(_classInfo);
      if (incl != null) {
        return defValue == null ? incl : defValue.withOverrides(incl);
      }
    }
    return defValue;
  }
  







  public AnnotatedMember findAnyGetter()
    throws IllegalArgumentException
  {
    AnnotatedMember anyGetter = _propCollector == null ? null : _propCollector.getAnyGetter();
    if (anyGetter != null)
    {


      Class<?> type = anyGetter.getRawType();
      if (!Map.class.isAssignableFrom(type)) {
        throw new IllegalArgumentException("Invalid 'any-getter' annotation on method " + anyGetter.getName() + "(): return type is not instance of java.util.Map");
      }
    }
    return anyGetter;
  }
  

  public List<BeanPropertyDefinition> findBackReferences()
  {
    List<BeanPropertyDefinition> result = null;
    HashSet<String> names = null;
    for (BeanPropertyDefinition property : _properties()) {
      AnnotationIntrospector.ReferenceProperty refDef = property.findReferenceType();
      if ((refDef != null) && (refDef.isBackReference()))
      {

        String refName = refDef.getName();
        if (result == null) {
          result = new ArrayList();
          names = new HashSet();
          names.add(refName);
        }
        else if (!names.add(refName)) {
          throw new IllegalArgumentException("Multiple back-reference properties with name '" + refName + "'");
        }
        
        result.add(property);
      } }
    return result;
  }
  

  @Deprecated
  public Map<String, AnnotatedMember> findBackReferenceProperties()
  {
    List<BeanPropertyDefinition> props = findBackReferences();
    if (props == null) {
      return null;
    }
    Map<String, AnnotatedMember> result = new java.util.HashMap();
    for (BeanPropertyDefinition prop : props) {
      result.put(prop.getName(), prop.getMutator());
    }
    return result;
  }
  








  public List<AnnotatedMethod> getFactoryMethods()
  {
    List<AnnotatedMethod> candidates = _classInfo.getFactoryMethods();
    if (candidates.isEmpty()) {
      return candidates;
    }
    List<AnnotatedMethod> result = null;
    for (AnnotatedMethod am : candidates) {
      if (isFactoryMethod(am)) {
        if (result == null) {
          result = new ArrayList();
        }
        result.add(am);
      }
    }
    if (result == null) {
      return Collections.emptyList();
    }
    return result;
  }
  

  public Constructor<?> findSingleArgConstructor(Class<?>... argTypes)
  {
    for (AnnotatedConstructor ac : _classInfo.getConstructors())
    {



      if (ac.getParameterCount() == 1) {
        Class<?> actArg = ac.getRawParameterType(0);
        for (Class<?> expArg : argTypes) {
          if (expArg == actArg) {
            return ac.getAnnotated();
          }
        }
      }
    }
    return null;
  }
  


  public java.lang.reflect.Method findFactoryMethod(Class<?>... expArgTypes)
  {
    for (AnnotatedMethod am : _classInfo.getFactoryMethods())
    {
      if ((isFactoryMethod(am)) && (am.getParameterCount() == 1))
      {
        Class<?> actualArgType = am.getRawParameterType(0);
        for (Class<?> expArgType : expArgTypes)
        {
          if (actualArgType.isAssignableFrom(expArgType)) {
            return am.getAnnotated();
          }
        }
      }
    }
    return null;
  }
  


  protected boolean isFactoryMethod(AnnotatedMethod am)
  {
    Class<?> rt = am.getRawReturnType();
    if (!getBeanClass().isAssignableFrom(rt)) {
      return false;
    }
    



    JsonCreator.Mode mode = _annotationIntrospector.findCreatorAnnotation(_config, am);
    if ((mode != null) && (mode != JsonCreator.Mode.DISABLED)) {
      return true;
    }
    String name = am.getName();
    
    if (("valueOf".equals(name)) && 
      (am.getParameterCount() == 1)) {
      return true;
    }
    

    if (("fromString".equals(name)) && 
      (am.getParameterCount() == 1)) {
      Class<?> cls = am.getRawParameterType(0);
      if ((cls == String.class) || (CharSequence.class.isAssignableFrom(cls))) {
        return true;
      }
    }
    
    return false;
  }
  



  @Deprecated
  protected PropertyName _findCreatorPropertyName(AnnotatedParameter param)
  {
    PropertyName name = _annotationIntrospector.findNameForDeserialization(param);
    if ((name == null) || (name.isEmpty())) {
      String str = _annotationIntrospector.findImplicitPropertyName(param);
      if ((str != null) && (!str.isEmpty())) {
        name = PropertyName.construct(str);
      }
    }
    return name;
  }
  






  public Class<?> findPOJOBuilder()
  {
    return _annotationIntrospector == null ? null : _annotationIntrospector
      .findPOJOBuilder(_classInfo);
  }
  

  public com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value findPOJOBuilderConfig()
  {
    return _annotationIntrospector == null ? null : _annotationIntrospector
      .findPOJOBuilderConfig(_classInfo);
  }
  

  public Converter<Object, Object> findDeserializationConverter()
  {
    if (_annotationIntrospector == null) {
      return null;
    }
    return _createConverter(_annotationIntrospector.findDeserializationConverter(_classInfo));
  }
  
  public String findClassDescription()
  {
    return _annotationIntrospector == null ? null : _annotationIntrospector
      .findClassDescription(_classInfo);
  }
  



















  @Deprecated
  public LinkedHashMap<String, AnnotatedField> _findPropertyFields(Collection<String> ignoredProperties, boolean forSerialization)
  {
    LinkedHashMap<String, AnnotatedField> results = new LinkedHashMap();
    for (BeanPropertyDefinition property : _properties()) {
      AnnotatedField f = property.getField();
      if (f != null) {
        String name = property.getName();
        if ((ignoredProperties == null) || 
          (!ignoredProperties.contains(name)))
        {


          results.put(name, f); }
      }
    }
    return results;
  }
  







  protected Converter<Object, Object> _createConverter(Object converterDef)
  {
    if (converterDef == null) {
      return null;
    }
    if ((converterDef instanceof Converter)) {
      return (Converter)converterDef;
    }
    if (!(converterDef instanceof Class))
    {
      throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
    }
    Class<?> converterClass = (Class)converterDef;
    
    if ((converterClass == Converter.None.class) || (ClassUtil.isBogusClass(converterClass))) {
      return null;
    }
    if (!Converter.class.isAssignableFrom(converterClass))
    {
      throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
    }
    HandlerInstantiator hi = _config.getHandlerInstantiator();
    Converter<?, ?> conv = hi == null ? null : hi.converterInstance(_config, _classInfo, converterClass);
    if (conv == null) {
      conv = (Converter)ClassUtil.createInstance(converterClass, _config
        .canOverrideAccessModifiers());
    }
    return conv;
  }
}
