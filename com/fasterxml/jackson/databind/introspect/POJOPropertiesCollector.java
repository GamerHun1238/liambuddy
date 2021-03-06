package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;















































































public class POJOPropertiesCollector
{
  protected final MapperConfig<?> _config;
  protected final boolean _forSerialization;
  protected final boolean _stdBeanNaming;
  protected final JavaType _type;
  protected final AnnotatedClass _classDef;
  protected final VisibilityChecker<?> _visibilityChecker;
  protected final AnnotationIntrospector _annotationIntrospector;
  protected final boolean _useAnnotations;
  protected final String _mutatorPrefix;
  protected boolean _collected;
  protected LinkedHashMap<String, POJOPropertyBuilder> _properties;
  protected LinkedList<POJOPropertyBuilder> _creatorProperties;
  protected LinkedList<AnnotatedMember> _anyGetters;
  protected LinkedList<AnnotatedMethod> _anySetters;
  protected LinkedList<AnnotatedMember> _anySetterField;
  protected LinkedList<AnnotatedMember> _jsonValueAccessors;
  protected HashSet<String> _ignoredPropertyNames;
  protected LinkedHashMap<Object, AnnotatedMember> _injectables;
  
  protected POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix)
  {
    _config = config;
    _stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
    _forSerialization = forSerialization;
    _type = type;
    _classDef = classDef;
    _mutatorPrefix = (mutatorPrefix == null ? "set" : mutatorPrefix);
    if (config.isAnnotationProcessingEnabled()) {
      _useAnnotations = true;
      _annotationIntrospector = _config.getAnnotationIntrospector();
    } else {
      _useAnnotations = false;
      _annotationIntrospector = AnnotationIntrospector.nopInstance();
    }
    _visibilityChecker = _config.getDefaultVisibilityChecker(type.getRawClass(), classDef);
  }
  






  public MapperConfig<?> getConfig()
  {
    return _config;
  }
  
  public JavaType getType() {
    return _type;
  }
  
  public AnnotatedClass getClassDef() {
    return _classDef;
  }
  
  public AnnotationIntrospector getAnnotationIntrospector() {
    return _annotationIntrospector;
  }
  
  public List<BeanPropertyDefinition> getProperties()
  {
    Map<String, POJOPropertyBuilder> props = getPropertyMap();
    return new ArrayList(props.values());
  }
  
  public Map<Object, AnnotatedMember> getInjectables() {
    if (!_collected) {
      collectAll();
    }
    return _injectables;
  }
  
  @Deprecated
  public AnnotatedMethod getJsonValueMethod() {
    AnnotatedMember m = getJsonValueAccessor();
    if ((m instanceof AnnotatedMethod)) {
      return (AnnotatedMethod)m;
    }
    return null;
  }
  



  public AnnotatedMember getJsonValueAccessor()
  {
    if (!_collected) {
      collectAll();
    }
    
    if (_jsonValueAccessors != null) {
      if (_jsonValueAccessors.size() > 1) {
        reportProblem("Multiple 'as-value' properties defined (%s vs %s)", new Object[] {_jsonValueAccessors
          .get(0), _jsonValueAccessors
          .get(1) });
      }
      
      return (AnnotatedMember)_jsonValueAccessors.get(0);
    }
    return null;
  }
  
  public AnnotatedMember getAnyGetter()
  {
    if (!_collected) {
      collectAll();
    }
    if (_anyGetters != null) {
      if (_anyGetters.size() > 1) {
        reportProblem("Multiple 'any-getters' defined (%s vs %s)", new Object[] {_anyGetters
          .get(0), _anyGetters.get(1) });
      }
      return (AnnotatedMember)_anyGetters.getFirst();
    }
    return null;
  }
  
  public AnnotatedMember getAnySetterField()
  {
    if (!_collected) {
      collectAll();
    }
    if (_anySetterField != null) {
      if (_anySetterField.size() > 1) {
        reportProblem("Multiple 'any-setter' fields defined (%s vs %s)", new Object[] {_anySetterField
          .get(0), _anySetterField.get(1) });
      }
      return (AnnotatedMember)_anySetterField.getFirst();
    }
    return null;
  }
  
  public AnnotatedMethod getAnySetterMethod()
  {
    if (!_collected) {
      collectAll();
    }
    if (_anySetters != null) {
      if (_anySetters.size() > 1) {
        reportProblem("Multiple 'any-setter' methods defined (%s vs %s)", new Object[] {_anySetters
          .get(0), _anySetters.get(1) });
      }
      return (AnnotatedMethod)_anySetters.getFirst();
    }
    return null;
  }
  



  public Set<String> getIgnoredPropertyNames()
  {
    return _ignoredPropertyNames;
  }
  




  public ObjectIdInfo getObjectIdInfo()
  {
    ObjectIdInfo info = _annotationIntrospector.findObjectIdInfo(_classDef);
    if (info != null) {
      info = _annotationIntrospector.findObjectReferenceInfo(_classDef, info);
    }
    return info;
  }
  


  public Class<?> findPOJOBuilderClass()
  {
    return _annotationIntrospector.findPOJOBuilder(_classDef);
  }
  
  protected Map<String, POJOPropertyBuilder> getPropertyMap()
  {
    if (!_collected) {
      collectAll();
    }
    return _properties;
  }
  











  protected void collectAll()
  {
    LinkedHashMap<String, POJOPropertyBuilder> props = new LinkedHashMap();
    

    _addFields(props);
    _addMethods(props);
    

    if (!_classDef.isNonStaticInnerClass()) {
      _addCreators(props);
    }
    _addInjectables(props);
    


    _removeUnwantedProperties(props);
    
    _removeUnwantedAccessor(props);
    

    _renameProperties(props);
    



    for (Iterator localIterator = props.values().iterator(); localIterator.hasNext();) { property = (POJOPropertyBuilder)localIterator.next();
      property.mergeAnnotations(_forSerialization);
    }
    
    POJOPropertyBuilder property;
    PropertyNamingStrategy naming = _findNamingStrategy();
    if (naming != null) {
      _renameUsing(props, naming);
    }
    




    for (POJOPropertyBuilder property : props.values()) {
      property.trimByVisibility();
    }
    



    if (_config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
      _renameWithWrappers(props);
    }
    

    _sortProperties(props);
    _properties = props;
    _collected = true;
  }
  









  protected void _addFields(Map<String, POJOPropertyBuilder> props)
  {
    AnnotationIntrospector ai = _annotationIntrospector;
    



    boolean pruneFinalFields = (!_forSerialization) && (!_config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS));
    boolean transientAsIgnoral = _config.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
    
    for (AnnotatedField f : _classDef.fields()) {
      String implName = ai.findImplicitPropertyName(f);
      
      if (Boolean.TRUE.equals(ai.hasAsValue(f))) {
        if (_jsonValueAccessors == null) {
          _jsonValueAccessors = new LinkedList();
        }
        _jsonValueAccessors.add(f);


      }
      else if (Boolean.TRUE.equals(ai.hasAnySetter(f))) {
        if (_anySetterField == null) {
          _anySetterField = new LinkedList();
        }
        _anySetterField.add(f);
      }
      else {
        if (implName == null) {
          implName = f.getName();
        }
        PropertyName pn;
        PropertyName pn;
        if (_forSerialization)
        {




          pn = ai.findNameForSerialization(f);
        } else {
          pn = ai.findNameForDeserialization(f);
        }
        boolean hasName = pn != null;
        boolean nameExplicit = hasName;
        
        if ((nameExplicit) && (pn.isEmpty())) {
          pn = _propNameFromSimple(implName);
          nameExplicit = false;
        }
        
        boolean visible = pn != null;
        if (!visible) {
          visible = _visibilityChecker.isFieldVisible(f);
        }
        
        boolean ignored = ai.hasIgnoreMarker(f);
        

        if (f.isTransient())
        {

          if (!hasName) {
            visible = false;
            if (transientAsIgnoral) {
              ignored = true;
            }
          }
        }
        




        if ((!pruneFinalFields) || (pn != null) || (ignored) || 
          (!Modifier.isFinal(f.getModifiers())))
        {

          _property(props, implName).addField(f, pn, nameExplicit, visible, ignored);
        }
      }
    }
  }
  


  protected void _addCreators(Map<String, POJOPropertyBuilder> props)
  {
    if (!_useAnnotations) {
      return;
    }
    for (AnnotatedConstructor ctor : _classDef.getConstructors()) {
      if (_creatorProperties == null) {
        _creatorProperties = new LinkedList();
      }
      int i = 0; for (int len = ctor.getParameterCount(); i < len; i++) {
        _addCreatorParam(props, ctor.getParameter(i));
      }
    }
    for (AnnotatedMethod factory : _classDef.getFactoryMethods()) {
      if (_creatorProperties == null) {
        _creatorProperties = new LinkedList();
      }
      int i = 0; for (int len = factory.getParameterCount(); i < len; i++) {
        _addCreatorParam(props, factory.getParameter(i));
      }
    }
  }
  





  protected void _addCreatorParam(Map<String, POJOPropertyBuilder> props, AnnotatedParameter param)
  {
    String impl = _annotationIntrospector.findImplicitPropertyName(param);
    if (impl == null) {
      impl = "";
    }
    PropertyName pn = _annotationIntrospector.findNameForDeserialization(param);
    boolean expl = (pn != null) && (!pn.isEmpty());
    if (!expl) {
      if (impl.isEmpty())
      {

        return;
      }
      
      JsonCreator.Mode creatorMode = _annotationIntrospector.findCreatorAnnotation(_config, param
        .getOwner());
      if ((creatorMode == null) || (creatorMode == JsonCreator.Mode.DISABLED)) {
        return;
      }
      pn = PropertyName.construct(impl);
    }
    








    POJOPropertyBuilder prop = (expl) && (impl.isEmpty()) ? _property(props, pn) : _property(props, impl);
    prop.addCtor(param, pn, expl, true, false);
    _creatorProperties.add(prop);
  }
  



  protected void _addMethods(Map<String, POJOPropertyBuilder> props)
  {
    AnnotationIntrospector ai = _annotationIntrospector;
    for (AnnotatedMethod m : _classDef.memberMethods())
    {




      int argCount = m.getParameterCount();
      if (argCount == 0) {
        _addGetterMethod(props, m, ai);
      } else if (argCount == 1) {
        _addSetterMethod(props, m, ai);
      } else if ((argCount == 2) && 
        (ai != null) && 
        (Boolean.TRUE.equals(ai.hasAnySetter(m)))) {
        if (_anySetters == null) {
          _anySetters = new LinkedList();
        }
        _anySetters.add(m);
      }
    }
  }
  




  protected void _addGetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai)
  {
    if (!m.hasReturnType()) {
      return;
    }
    


    if (Boolean.TRUE.equals(ai.hasAnyGetter(m))) {
      if (_anyGetters == null) {
        _anyGetters = new LinkedList();
      }
      _anyGetters.add(m);
      return;
    }
    
    if (Boolean.TRUE.equals(ai.hasAsValue(m))) {
      if (_jsonValueAccessors == null) {
        _jsonValueAccessors = new LinkedList();
      }
      _jsonValueAccessors.add(m);
      return;
    }
    


    PropertyName pn = ai.findNameForSerialization(m);
    boolean nameExplicit = pn != null;
    boolean visible;
    String implName; boolean visible; if (!nameExplicit) {
      String implName = ai.findImplicitPropertyName(m);
      if (implName == null)
        implName = BeanUtil.okNameForRegularGetter(m, m.getName(), _stdBeanNaming);
      boolean visible;
      if (implName == null) {
        implName = BeanUtil.okNameForIsGetter(m, m.getName(), _stdBeanNaming);
        if (implName == null) {
          return;
        }
        visible = _visibilityChecker.isIsGetterVisible(m);
      } else {
        visible = _visibilityChecker.isGetterVisible(m);
      }
    }
    else {
      implName = ai.findImplicitPropertyName(m);
      if (implName == null) {
        implName = BeanUtil.okNameForGetter(m, _stdBeanNaming);
      }
      
      if (implName == null) {
        implName = m.getName();
      }
      if (pn.isEmpty())
      {
        pn = _propNameFromSimple(implName);
        nameExplicit = false;
      }
      visible = true;
    }
    boolean ignore = ai.hasIgnoreMarker(m);
    _property(props, implName).addGetter(m, pn, nameExplicit, visible, ignore);
  }
  



  protected void _addSetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai)
  {
    PropertyName pn = ai == null ? null : ai.findNameForDeserialization(m);
    boolean nameExplicit = pn != null;
    boolean visible; String implName; boolean visible; if (!nameExplicit) {
      String implName = ai == null ? null : ai.findImplicitPropertyName(m);
      if (implName == null) {
        implName = BeanUtil.okNameForMutator(m, _mutatorPrefix, _stdBeanNaming);
      }
      if (implName == null) {
        return;
      }
      visible = _visibilityChecker.isSetterVisible(m);
    }
    else {
      implName = ai == null ? null : ai.findImplicitPropertyName(m);
      if (implName == null) {
        implName = BeanUtil.okNameForMutator(m, _mutatorPrefix, _stdBeanNaming);
      }
      
      if (implName == null) {
        implName = m.getName();
      }
      if (pn.isEmpty())
      {
        pn = _propNameFromSimple(implName);
        nameExplicit = false;
      }
      visible = true;
    }
    boolean ignore = ai == null ? false : ai.hasIgnoreMarker(m);
    _property(props, implName).addSetter(m, pn, nameExplicit, visible, ignore);
  }
  
  protected void _addInjectables(Map<String, POJOPropertyBuilder> props)
  {
    AnnotationIntrospector ai = _annotationIntrospector;
    
    for (AnnotatedField f : _classDef.fields()) {
      _doAddInjectable(ai.findInjectableValue(f), f);
    }
    
    for (AnnotatedMethod m : _classDef.memberMethods())
    {
      if (m.getParameterCount() == 1)
      {

        _doAddInjectable(ai.findInjectableValue(m), m);
      }
    }
  }
  
  protected void _doAddInjectable(JacksonInject.Value injectable, AnnotatedMember m) {
    if (injectable == null) {
      return;
    }
    Object id = injectable.getId();
    if (_injectables == null) {
      _injectables = new LinkedHashMap();
    }
    AnnotatedMember prev = (AnnotatedMember)_injectables.put(id, m);
    if (prev != null)
    {
      if (prev.getClass() == m.getClass()) {
        String type = id.getClass().getName();
        
        throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
      }
    }
  }
  
  private PropertyName _propNameFromSimple(String simpleName) {
    return PropertyName.construct(simpleName, null);
  }
  










  protected void _removeUnwantedProperties(Map<String, POJOPropertyBuilder> props)
  {
    Iterator<POJOPropertyBuilder> it = props.values().iterator();
    while (it.hasNext()) {
      POJOPropertyBuilder prop = (POJOPropertyBuilder)it.next();
      

      if (!prop.anyVisible()) {
        it.remove();


      }
      else if (prop.anyIgnorals())
      {
        if (!prop.isExplicitlyIncluded()) {
          it.remove();
          _collectIgnorals(prop.getName());
        }
        else
        {
          prop.removeIgnored();
          if (!prop.couldDeserialize()) {
            _collectIgnorals(prop.getName());
          }
        }
      }
    }
  }
  




  protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props)
  {
    boolean inferMutators = _config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
    Iterator<POJOPropertyBuilder> it = props.values().iterator();
    
    while (it.hasNext()) {
      POJOPropertyBuilder prop = (POJOPropertyBuilder)it.next();
      
      JsonProperty.Access acc = prop.removeNonVisible(inferMutators);
      if (acc == JsonProperty.Access.READ_ONLY) {
        _collectIgnorals(prop.getName());
      }
    }
  }
  





  private void _collectIgnorals(String name)
  {
    if (!_forSerialization) {
      if (_ignoredPropertyNames == null) {
        _ignoredPropertyNames = new HashSet();
      }
      _ignoredPropertyNames.add(name);
    }
  }
  







  protected void _renameProperties(Map<String, POJOPropertyBuilder> props)
  {
    Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
    LinkedList<POJOPropertyBuilder> renamed = null;
    Map.Entry<String, POJOPropertyBuilder> entry; while (it.hasNext()) {
      entry = (Map.Entry)it.next();
      POJOPropertyBuilder prop = (POJOPropertyBuilder)entry.getValue();
      
      Collection<PropertyName> l = prop.findExplicitNames();
      

      if (!l.isEmpty())
      {

        it.remove();
        if (renamed == null) {
          renamed = new LinkedList();
        }
        
        if (l.size() == 1) {
          PropertyName n = (PropertyName)l.iterator().next();
          renamed.add(prop.withName(n));
        }
        else
        {
          renamed.addAll(prop.explode(l));
        }
      }
    }
    











    if (renamed != null) {
      for (POJOPropertyBuilder prop : renamed) {
        String name = prop.getName();
        POJOPropertyBuilder old = (POJOPropertyBuilder)props.get(name);
        if (old == null) {
          props.put(name, prop);
        } else {
          old.addAll(prop);
        }
        
        _updateCreatorProperty(prop, _creatorProperties);
        


        if (_ignoredPropertyNames != null) {
          _ignoredPropertyNames.remove(name);
        }
      }
    }
  }
  

  protected void _renameUsing(Map<String, POJOPropertyBuilder> propMap, PropertyNamingStrategy naming)
  {
    POJOPropertyBuilder[] props = (POJOPropertyBuilder[])propMap.values().toArray(new POJOPropertyBuilder[propMap.size()]);
    propMap.clear();
    for (POJOPropertyBuilder prop : props) {
      PropertyName fullName = prop.getFullName();
      String rename = null;
      

      if ((!prop.isExplicitlyNamed()) || (_config.isEnabled(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING))) {
        if (_forSerialization) {
          if (prop.hasGetter()) {
            rename = naming.nameForGetterMethod(_config, prop.getGetter(), fullName.getSimpleName());
          } else if (prop.hasField()) {
            rename = naming.nameForField(_config, prop.getField(), fullName.getSimpleName());
          }
        }
        else if (prop.hasSetter()) {
          rename = naming.nameForSetterMethod(_config, prop.getSetter(), fullName.getSimpleName());
        } else if (prop.hasConstructorParameter()) {
          rename = naming.nameForConstructorParameter(_config, prop.getConstructorParameter(), fullName.getSimpleName());
        } else if (prop.hasField()) {
          rename = naming.nameForField(_config, prop.getField(), fullName.getSimpleName());
        } else if (prop.hasGetter())
        {


          rename = naming.nameForGetterMethod(_config, prop.getGetter(), fullName.getSimpleName());
        }
      }
      String simpleName;
      String simpleName;
      if ((rename != null) && (!fullName.hasSimpleName(rename))) {
        prop = prop.withSimpleName(rename);
        simpleName = rename;
      } else {
        simpleName = fullName.getSimpleName();
      }
      
      POJOPropertyBuilder old = (POJOPropertyBuilder)propMap.get(simpleName);
      if (old == null) {
        propMap.put(simpleName, prop);
      } else {
        old.addAll(prop);
      }
      

      _updateCreatorProperty(prop, _creatorProperties);
    }
  }
  


  protected void _renameWithWrappers(Map<String, POJOPropertyBuilder> props)
  {
    Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
    LinkedList<POJOPropertyBuilder> renamed = null;
    Map.Entry<String, POJOPropertyBuilder> entry; while (it.hasNext()) {
      entry = (Map.Entry)it.next();
      POJOPropertyBuilder prop = (POJOPropertyBuilder)entry.getValue();
      AnnotatedMember member = prop.getPrimaryMember();
      if (member != null)
      {

        PropertyName wrapperName = _annotationIntrospector.findWrapperName(member);
        


        if ((wrapperName != null) && (wrapperName.hasSimpleName()))
        {

          if (!wrapperName.equals(prop.getFullName())) {
            if (renamed == null) {
              renamed = new LinkedList();
            }
            prop = prop.withName(wrapperName);
            renamed.add(prop);
            it.remove();
          } }
      }
    }
    if (renamed != null) {
      for (POJOPropertyBuilder prop : renamed) {
        String name = prop.getName();
        POJOPropertyBuilder old = (POJOPropertyBuilder)props.get(name);
        if (old == null) {
          props.put(name, prop);
        } else {
          old.addAll(prop);
        }
      }
    }
  }
  










  protected void _sortProperties(Map<String, POJOPropertyBuilder> props)
  {
    AnnotationIntrospector intr = _annotationIntrospector;
    Boolean alpha = intr.findSerializationSortAlphabetically(_classDef);
    boolean sort;
    boolean sort;
    if (alpha == null) {
      sort = _config.shouldSortPropertiesAlphabetically();
    } else {
      sort = alpha.booleanValue();
    }
    String[] propertyOrder = intr.findSerializationPropertyOrder(_classDef);
    

    if ((!sort) && (_creatorProperties == null) && (propertyOrder == null)) {
      return;
    }
    int size = props.size();
    Map<String, POJOPropertyBuilder> all;
    Map<String, POJOPropertyBuilder> all;
    if (sort) {
      all = new TreeMap();
    } else {
      all = new LinkedHashMap(size + size);
    }
    
    for (Iterator localIterator1 = props.values().iterator(); localIterator1.hasNext();) { prop = (POJOPropertyBuilder)localIterator1.next();
      all.put(prop.getName(), prop); }
    POJOPropertyBuilder prop;
    Object ordered = new LinkedHashMap(size + size);
    
    if (propertyOrder != null) {
      for (String name : propertyOrder) {
        POJOPropertyBuilder w = (POJOPropertyBuilder)all.get(name);
        if (w == null) {
          for (POJOPropertyBuilder prop : props.values()) {
            if (name.equals(prop.getInternalName())) {
              w = prop;
              
              name = prop.getName();
              break;
            }
          }
        }
        if (w != null) {
          ((Map)ordered).put(name, w);
        }
      }
    }
    Object sorted;
    if (_creatorProperties != null)
    {
      Collection<POJOPropertyBuilder> cr;
      

      Collection<POJOPropertyBuilder> cr;
      
      if (sort) {
        sorted = new TreeMap();
        
        for (POJOPropertyBuilder prop : _creatorProperties) {
          ((TreeMap)sorted).put(prop.getName(), prop);
        }
        cr = ((TreeMap)sorted).values();
      } else {
        cr = _creatorProperties;
      }
      for (sorted = cr.iterator(); ((Iterator)sorted).hasNext();) { POJOPropertyBuilder prop = (POJOPropertyBuilder)((Iterator)sorted).next();
        

        String name = prop.getName();
        if (all.containsKey(name)) {
          ((Map)ordered).put(name, prop);
        }
      }
    }
    
    ((Map)ordered).putAll(all);
    props.clear();
    props.putAll((Map)ordered);
  }
  





  protected void reportProblem(String msg, Object... args)
  {
    if (args.length > 0) {
      msg = String.format(msg, args);
    }
    throw new IllegalArgumentException("Problem with definition of " + _classDef + ": " + msg);
  }
  
  protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, PropertyName name)
  {
    String simpleName = name.getSimpleName();
    POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(simpleName);
    if (prop == null) {
      prop = new POJOPropertyBuilder(_config, _annotationIntrospector, _forSerialization, name);
      
      props.put(simpleName, prop);
    }
    return prop;
  }
  


  protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, String implName)
  {
    POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(implName);
    if (prop == null)
    {
      prop = new POJOPropertyBuilder(_config, _annotationIntrospector, _forSerialization, PropertyName.construct(implName));
      props.put(implName, prop);
    }
    return prop;
  }
  
  private PropertyNamingStrategy _findNamingStrategy()
  {
    Object namingDef = _annotationIntrospector.findNamingStrategy(_classDef);
    if (namingDef == null) {
      return _config.getPropertyNamingStrategy();
    }
    if ((namingDef instanceof PropertyNamingStrategy)) {
      return (PropertyNamingStrategy)namingDef;
    }
    


    if (!(namingDef instanceof Class))
    {
      throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef.getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead");
    }
    Class<?> namingClass = (Class)namingDef;
    
    if (namingClass == PropertyNamingStrategy.class) {
      return null;
    }
    
    if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass))
    {
      throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass.getName() + "; expected Class<PropertyNamingStrategy>");
    }
    HandlerInstantiator hi = _config.getHandlerInstantiator();
    if (hi != null) {
      PropertyNamingStrategy pns = hi.namingStrategyInstance(_config, _classDef, namingClass);
      if (pns != null) {
        return pns;
      }
    }
    return (PropertyNamingStrategy)ClassUtil.createInstance(namingClass, _config
      .canOverrideAccessModifiers());
  }
  
  protected void _updateCreatorProperty(POJOPropertyBuilder prop, List<POJOPropertyBuilder> creatorProperties)
  {
    if (creatorProperties != null) {
      String intName = prop.getInternalName();
      int i = 0; for (int len = creatorProperties.size(); i < len; i++) {
        if (((POJOPropertyBuilder)creatorProperties.get(i)).getInternalName().equals(intName)) {
          creatorProperties.set(i, prop);
          break;
        }
      }
    }
  }
}
