package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class BasicDeserializerFactory extends DeserializerFactory implements java.io.Serializable
{
  private static final Class<?> CLASS_OBJECT = Object.class;
  private static final Class<?> CLASS_STRING = String.class;
  private static final Class<?> CLASS_CHAR_SEQUENCE = CharSequence.class;
  private static final Class<?> CLASS_ITERABLE = Iterable.class;
  private static final Class<?> CLASS_MAP_ENTRY = java.util.Map.Entry.class;
  private static final Class<?> CLASS_SERIALIZABLE = java.io.Serializable.class;
  




  protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
  







  protected final DeserializerFactoryConfig _factoryConfig;
  








  protected BasicDeserializerFactory(DeserializerFactoryConfig config)
  {
    _factoryConfig = config;
  }
  






  public DeserializerFactoryConfig getFactoryConfig()
  {
    return _factoryConfig;
  }
  





  protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig paramDeserializerFactoryConfig);
  





  public final DeserializerFactory withAdditionalDeserializers(Deserializers additional)
  {
    return withConfig(_factoryConfig.withAdditionalDeserializers(additional));
  }
  




  public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional)
  {
    return withConfig(_factoryConfig.withAdditionalKeyDeserializers(additional));
  }
  




  public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier)
  {
    return withConfig(_factoryConfig.withDeserializerModifier(modifier));
  }
  




  public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver)
  {
    return withConfig(_factoryConfig.withAbstractTypeResolver(resolver));
  }
  




  public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators)
  {
    return withConfig(_factoryConfig.withValueInstantiators(instantiators));
  }
  






  public JavaType mapAbstractType(DeserializationConfig config, JavaType type)
    throws JsonMappingException
  {
    for (;;)
    {
      JavaType next = _mapAbstractType2(config, type);
      if (next == null) {
        return type;
      }
      

      Class<?> prevCls = type.getRawClass();
      Class<?> nextCls = next.getRawClass();
      if ((prevCls == nextCls) || (!prevCls.isAssignableFrom(nextCls))) {
        throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
      }
      type = next;
    }
  }
  




  private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type)
    throws JsonMappingException
  {
    Class<?> currClass = type.getRawClass();
    if (_factoryConfig.hasAbstractTypeResolvers()) {
      for (AbstractTypeResolver resolver : _factoryConfig.abstractTypeResolvers()) {
        JavaType concrete = resolver.findTypeMapping(config, type);
        if ((concrete != null) && (!concrete.hasRawClass(currClass))) {
          return concrete;
        }
      }
    }
    return null;
  }
  













  public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    
    ValueInstantiator instantiator = null;
    
    com.fasterxml.jackson.databind.introspect.AnnotatedClass ac = beanDesc.getClassInfo();
    Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
    if (instDef != null) {
      instantiator = _valueInstantiatorInstance(config, ac, instDef);
    }
    if (instantiator == null)
    {

      instantiator = com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators.findStdValueInstantiator(config, beanDesc.getBeanClass());
      if (instantiator == null) {
        instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
      }
    }
    

    if (_factoryConfig.hasValueInstantiators()) {
      for (ValueInstantiators insts : _factoryConfig.valueInstantiators()) {
        instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
        
        if (instantiator == null) {
          ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", new Object[] {insts
          
            .getClass().getName() });
        }
      }
    }
    

    if (instantiator.getIncompleteParameter() != null) {
      AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
      AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
      throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
    }
    

    return instantiator;
  }
  





  protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc)
    throws JsonMappingException
  {
    CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    

    DeserializationConfig config = ctxt.getConfig();
    VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc
      .getClassInfo());
    








    Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
    


    _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
    
    if (beanDesc.getType().isConcrete()) {
      _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
    }
    return creators.constructValueInstantiator(ctxt);
  }
  
  protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc)
    throws JsonMappingException
  {
    Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = java.util.Collections.emptyMap();
    for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
      Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
      while (it.hasNext()) {
        AnnotatedParameter param = (AnnotatedParameter)it.next();
        AnnotatedWithParams owner = param.getOwner();
        BeanPropertyDefinition[] defs = (BeanPropertyDefinition[])result.get(owner);
        int index = param.getIndex();
        
        if (defs == null) {
          if (result.isEmpty()) {
            result = new java.util.LinkedHashMap();
          }
          defs = new BeanPropertyDefinition[owner.getParameterCount()];
          result.put(owner, defs);
        }
        else if (defs[index] != null) {
          ctxt.reportBadTypeDefinition(beanDesc, "Conflict: parameter #%d of %s bound to more than one property; %s vs %s", new Object[] {
          
            Integer.valueOf(index), owner, defs[index], propDef });
        }
        
        defs[index] = propDef;
      }
    }
    return result;
  }
  

  public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef)
    throws JsonMappingException
  {
    if (instDef == null) {
      return null;
    }
    


    if ((instDef instanceof ValueInstantiator)) {
      return (ValueInstantiator)instDef;
    }
    if (!(instDef instanceof Class))
    {
      throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
    }
    
    Class<?> instClass = (Class)instDef;
    if (ClassUtil.isBogusClass(instClass)) {
      return null;
    }
    if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
      throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
    }
    
    com.fasterxml.jackson.databind.cfg.HandlerInstantiator hi = config.getHandlerInstantiator();
    if (hi != null) {
      ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
      if (inst != null) {
        return inst;
      }
    }
    return (ValueInstantiator)ClassUtil.createInstance(instClass, config
      .canOverrideAccessModifiers());
  }
  












  protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams)
    throws JsonMappingException
  {
    boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
    if (isNonStaticInnerClass)
    {
      return;
    }
    



    com.fasterxml.jackson.databind.introspect.AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
    if ((defaultCtor != null) && (
      (!creators.hasDefaultCreator()) || (_hasCreatorAnnotation(ctxt, defaultCtor)))) {
      creators.setDefaultCreator(defaultCtor);
    }
    

    List<CreatorCandidate> nonAnnotated = new LinkedList();
    int explCount = 0;
    for (Iterator localIterator = beanDesc.getConstructors().iterator(); localIterator.hasNext();) { ctor = (com.fasterxml.jackson.databind.introspect.AnnotatedConstructor)localIterator.next();
      JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), ctor);
      if (JsonCreator.Mode.DISABLED != creatorMode)
      {

        if (creatorMode == null)
        {
          if (vchecker.isCreatorVisible(ctor)) {
            nonAnnotated.add(CreatorCandidate.construct(intr, ctor, (BeanPropertyDefinition[])creatorParams.get(ctor)));
          }
        }
        else {
          switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonCreator$Mode[creatorMode.ordinal()]) {
          case 1: 
            _addExplicitDelegatingCreator(ctxt, beanDesc, creators, 
              CreatorCandidate.construct(intr, ctor, null));
            break;
          case 2: 
            _addExplicitPropertyCreator(ctxt, beanDesc, creators, 
              CreatorCandidate.construct(intr, ctor, (BeanPropertyDefinition[])creatorParams.get(ctor)));
            break;
          default: 
            _addExplicitAnyCreator(ctxt, beanDesc, creators, 
              CreatorCandidate.construct(intr, ctor, (BeanPropertyDefinition[])creatorParams.get(ctor)));
          }
          
          explCount++;
        } } }
    com.fasterxml.jackson.databind.introspect.AnnotatedConstructor ctor;
    if (explCount > 0) {
      return;
    }
    Object implicitCtors = null;
    for (CreatorCandidate candidate : nonAnnotated) {
      int argCount = candidate.paramCount();
      AnnotatedWithParams ctor = candidate.creator();
      

      if (argCount == 1) {
        BeanPropertyDefinition propDef = candidate.propertyDef(0);
        boolean useProps = _checkIfCreatorPropertyBased(intr, ctor, propDef);
        
        if (useProps) {
          SettableBeanProperty[] properties = new SettableBeanProperty[1];
          PropertyName name = candidate.paramName(0);
          properties[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, candidate
            .parameter(0), candidate.injection(0));
          creators.addPropertyCreator(ctor, false, properties);
        } else {
          _handleSingleArgumentCreator(creators, ctor, false, vchecker
          
            .isCreatorVisible(ctor));
          

          if (propDef != null) {
            ((POJOPropertyBuilder)propDef).removeConstructors();

          }
          
        }
        

      }
      else
      {

        int nonAnnotatedParamIndex = -1;
        SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
        int explicitNameCount = 0;
        int implicitWithCreatorCount = 0;
        int injectCount = 0;
        
        for (int i = 0; i < argCount; i++) {
          AnnotatedParameter param = ctor.getParameter(i);
          BeanPropertyDefinition propDef = candidate.propertyDef(i);
          JacksonInject.Value injectId = intr.findInjectableValue(param);
          PropertyName name = propDef == null ? null : propDef.getFullName();
          
          if ((propDef != null) && (propDef.isExplicitlyNamed())) {
            explicitNameCount++;
            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);

          }
          else if (injectId != null) {
            injectCount++;
            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
          }
          else {
            com.fasterxml.jackson.databind.util.NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
            if (unwrapper != null) {
              _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);













            }
            else if (nonAnnotatedParamIndex < 0) {
              nonAnnotatedParamIndex = i;
            }
          }
        }
        int namedCount = explicitNameCount + implicitWithCreatorCount;
        
        if ((explicitNameCount > 0) || (injectCount > 0))
        {
          if (namedCount + injectCount == argCount) {
            creators.addPropertyCreator(ctor, false, properties);
            continue;
          }
          if ((explicitNameCount == 0) && (injectCount + 1 == argCount))
          {
            creators.addDelegatingCreator(ctor, false, properties, 0);
            continue;
          }
          


          PropertyName impl = candidate.findImplicitParamName(nonAnnotatedParamIndex);
          if ((impl == null) || (impl.isEmpty()))
          {







            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", new Object[] {
            
              Integer.valueOf(nonAnnotatedParamIndex), ctor });
          }
        }
        
        if (!creators.hasDefaultCreator()) {
          if (implicitCtors == null) {
            implicitCtors = new LinkedList();
          }
          ((List)implicitCtors).add(ctor);
        }
      }
    }
    
    if ((implicitCtors != null) && (!creators.hasDelegatingCreator()) && 
      (!creators.hasPropertyBasedCreator())) {
      _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, (List)implicitCtors);
    }
  }
  











  protected void _addExplicitDelegatingCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate)
    throws JsonMappingException
  {
    int ix = -1;
    int argCount = candidate.paramCount();
    SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
    for (int i = 0; i < argCount; i++) {
      AnnotatedParameter param = candidate.parameter(i);
      JacksonInject.Value injectId = candidate.injection(i);
      if (injectId != null) {
        properties[i] = constructCreatorProperty(ctxt, beanDesc, null, i, param, injectId);

      }
      else if (ix < 0) {
        ix = i;
      }
      else
      {
        ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", new Object[] {
        
          Integer.valueOf(ix), Integer.valueOf(i), candidate });
      }
    }
    if (ix < 0) {
      ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", new Object[] { candidate });
    }
    


    if (argCount == 1) {
      _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
      

      BeanPropertyDefinition paramDef = candidate.propertyDef(0);
      if (paramDef != null) {
        ((POJOPropertyBuilder)paramDef).removeConstructors();
      }
      return;
    }
    creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
  }
  







  protected void _addExplicitPropertyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate)
    throws JsonMappingException
  {
    int paramCount = candidate.paramCount();
    SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
    
    for (int i = 0; i < paramCount; i++) {
      JacksonInject.Value injectId = candidate.injection(i);
      AnnotatedParameter param = candidate.parameter(i);
      PropertyName name = candidate.paramName(i);
      if (name == null)
      {

        com.fasterxml.jackson.databind.util.NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(param);
        if (unwrapper != null) {
          _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
        }
        



        name = candidate.findImplicitParamName(i);
        
        if ((name == null) && (injectId == null)) {
          ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d has no property name, is not Injectable: can not use as Creator %s", new Object[] {
            Integer.valueOf(i), candidate });
        }
      }
      properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
    }
    creators.addPropertyCreator(candidate.creator(), true, properties);
  }
  








  protected void _addExplicitAnyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate)
    throws JsonMappingException
  {
    if (1 != candidate.paramCount())
    {

      int oneNotInjected = candidate.findOnlyParamWithoutInjection();
      if (oneNotInjected >= 0)
      {
        if (candidate.paramName(oneNotInjected) == null) {
          _addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
          return;
        }
      }
      _addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
      return;
    }
    AnnotatedParameter param = candidate.parameter(0);
    JacksonInject.Value injectId = candidate.injection(0);
    PropertyName paramName = candidate.explicitParamName(0);
    BeanPropertyDefinition paramDef = candidate.propertyDef(0);
    

    boolean useProps = (paramName != null) || (injectId != null);
    if ((!useProps) && (paramDef != null))
    {





      paramName = candidate.paramName(0);
      useProps = (paramName != null) && (paramDef.couldSerialize());
    }
    if (useProps)
    {
      SettableBeanProperty[] properties = { constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId) };
      
      creators.addPropertyCreator(candidate.creator(), true, properties);
      return;
    }
    _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
    


    if (paramDef != null) {
      ((POJOPropertyBuilder)paramDef).removeConstructors();
    }
  }
  


  private boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef)
  {
    if (((propDef != null) && (propDef.isExplicitlyNamed())) || 
      (intr.findInjectableValue(creator.getParameter(0)) != null)) {
      return true;
    }
    if (propDef != null)
    {

      String implName = propDef.getName();
      if ((implName != null) && (!implName.isEmpty()) && 
        (propDef.couldSerialize())) {
        return true;
      }
    }
    

    return false;
  }
  


  private void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedWithParams> implicitCtors)
    throws JsonMappingException
  {
    AnnotatedWithParams found = null;
    SettableBeanProperty[] foundProps = null;
    





    for (Iterator localIterator = implicitCtors.iterator(); localIterator.hasNext();) { ctor = (AnnotatedWithParams)localIterator.next();
      if (vchecker.isCreatorVisible(ctor))
      {


        argCount = ctor.getParameterCount();
        properties = new SettableBeanProperty[argCount];
        for (int i = 0;; i++) { if (i >= argCount) break label137;
          AnnotatedParameter param = ctor.getParameter(i);
          PropertyName name = _findParamName(param, intr);
          

          if ((name == null) || (name.isEmpty())) {
            break;
          }
          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
        }
        
        if (found != null) {
          found = null;
          break;
        }
        found = ctor;
        foundProps = properties; } }
    AnnotatedWithParams ctor;
    int argCount;
    SettableBeanProperty[] properties; label137: if (found != null) {
      creators.addPropertyCreator(found, false, foundProps);
      BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
      
      ctor = foundProps;argCount = ctor.length; for (properties = 0; properties < argCount; properties++) { SettableBeanProperty prop = ctor[properties];
        PropertyName pn = prop.getFullName();
        if (!bbd.hasProperty(pn)) {
          BeanPropertyDefinition newDef = com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition.construct(ctxt
            .getConfig(), prop.getMember(), pn);
          bbd.addProperty(newDef);
        }
      }
    }
  }
  



  protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams)
    throws JsonMappingException
  {
    List<CreatorCandidate> nonAnnotated = new LinkedList();
    int explCount = 0;
    

    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
      JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), factory);
      int argCount = factory.getParameterCount();
      if (creatorMode == null)
      {
        if ((argCount == 1) && (vchecker.isCreatorVisible(factory))) {
          nonAnnotated.add(CreatorCandidate.construct(intr, factory, null));
        }
        
      }
      else if (creatorMode != JsonCreator.Mode.DISABLED)
      {



        if (argCount == 0) {
          creators.setDefaultCreator(factory);
        }
        else
        {
          switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonCreator$Mode[creatorMode.ordinal()]) {
          case 1: 
            _addExplicitDelegatingCreator(ctxt, beanDesc, creators, 
              CreatorCandidate.construct(intr, factory, null));
            break;
          case 2: 
            _addExplicitPropertyCreator(ctxt, beanDesc, creators, 
              CreatorCandidate.construct(intr, factory, (BeanPropertyDefinition[])creatorParams.get(factory)));
            break;
          case 3: 
          default: 
            _addExplicitAnyCreator(ctxt, beanDesc, creators, 
              CreatorCandidate.construct(intr, factory, (BeanPropertyDefinition[])creatorParams.get(factory)));
          }
          
          explCount++;
        } }
    }
    if (explCount > 0) {
      return;
    }
    
    for (CreatorCandidate candidate : nonAnnotated) {
      int argCount = candidate.paramCount();
      AnnotatedWithParams factory = candidate.creator();
      BeanPropertyDefinition[] propDefs = (BeanPropertyDefinition[])creatorParams.get(factory);
      
      if (argCount == 1)
      {

        BeanPropertyDefinition argDef = candidate.propertyDef(0);
        boolean useProps = _checkIfCreatorPropertyBased(intr, factory, argDef);
        if (!useProps) {
          _handleSingleArgumentCreator(creators, factory, false, vchecker
            .isCreatorVisible(factory));
          

          if (argDef != null) {
            ((POJOPropertyBuilder)argDef).removeConstructors();
          }
        }
        else {
          AnnotatedParameter nonAnnotatedParam = null;
          SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
          int implicitNameCount = 0;
          int explicitNameCount = 0;
          int injectCount = 0;
          
          for (int i = 0; i < argCount; i++) {
            AnnotatedParameter param = factory.getParameter(i);
            BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
            JacksonInject.Value injectable = intr.findInjectableValue(param);
            PropertyName name = propDef == null ? null : propDef.getFullName();
            
            if ((propDef != null) && (propDef.isExplicitlyNamed())) {
              explicitNameCount++;
              properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);

            }
            else if (injectable != null) {
              injectCount++;
              properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
            }
            else {
              com.fasterxml.jackson.databind.util.NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
              if (unwrapper != null) {
                _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);



























              }
              else if (nonAnnotatedParam == null)
                nonAnnotatedParam = param;
            }
          }
          int namedCount = explicitNameCount + implicitNameCount;
          

          if ((explicitNameCount > 0) || (injectCount > 0))
          {
            if (namedCount + injectCount == argCount) {
              creators.addPropertyCreator(factory, false, properties);
            } else if ((explicitNameCount == 0) && (injectCount + 1 == argCount))
            {
              creators.addDelegatingCreator(factory, false, properties, 0);
            } else {
              ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of factory method %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", new Object[] {
              
                Integer.valueOf(nonAnnotatedParam.getIndex()), factory });
            }
          }
        }
      }
    }
  }
  
  protected boolean _handleSingleArgumentCreator(CreatorCollector creators, AnnotatedWithParams ctor, boolean isCreator, boolean isVisible)
  {
    Class<?> type = ctor.getRawParameterType(0);
    if ((type == String.class) || (type == CLASS_CHAR_SEQUENCE)) {
      if ((isCreator) || (isVisible)) {
        creators.addStringCreator(ctor, isCreator);
      }
      return true;
    }
    if ((type == Integer.TYPE) || (type == Integer.class)) {
      if ((isCreator) || (isVisible)) {
        creators.addIntCreator(ctor, isCreator);
      }
      return true;
    }
    if ((type == Long.TYPE) || (type == Long.class)) {
      if ((isCreator) || (isVisible)) {
        creators.addLongCreator(ctor, isCreator);
      }
      return true;
    }
    if ((type == Double.TYPE) || (type == Double.class)) {
      if ((isCreator) || (isVisible)) {
        creators.addDoubleCreator(ctor, isCreator);
      }
      return true;
    }
    if ((type == Boolean.TYPE) || (type == Boolean.class)) {
      if ((isCreator) || (isVisible)) {
        creators.addBooleanCreator(ctor, isCreator);
      }
      return true;
    }
    
    if (isCreator) {
      creators.addDelegatingCreator(ctor, isCreator, null, 0);
      return true;
    }
    return false;
  }
  



  protected void _reportUnwrappedCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedParameter param)
    throws JsonMappingException
  {
    ctxt.reportBadDefinition(beanDesc.getType(), String.format("Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", new Object[] {
    
      Integer.valueOf(param.getIndex()) }));
  }
  








  protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, JacksonInject.Value injectable)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    
    PropertyMetadata metadata;
    if (intr == null) {
      metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
    } else {
      Boolean b = intr.hasRequiredMarker(param);
      String desc = intr.findPropertyDescription(param);
      Integer idx = intr.findPropertyIndex(param);
      String def = intr.findPropertyDefaultValue(param);
      metadata = PropertyMetadata.construct(b, desc, idx, def);
    }
    
    JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
    
    com.fasterxml.jackson.databind.BeanProperty.Std property = new com.fasterxml.jackson.databind.BeanProperty.Std(name, type, intr.findWrapperName(param), param, metadata);
    
    TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
    
    if (typeDeser == null) {
      typeDeser = findTypeDeserializer(config, type);
    }
    


    PropertyMetadata metadata = _getSetterInfo(ctxt, property, metadata);
    


    Object injectableValueId = injectable == null ? null : injectable.getId();
    
    SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
    
    JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
    if (deser == null) {
      deser = (JsonDeserializer)type.getValueHandler();
    }
    if (deser != null)
    {
      deser = ctxt.handlePrimaryContextualization(deser, prop, type);
      prop = prop.withValueDeserializer(deser);
    }
    return prop;
  }
  
  private PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr)
  {
    if ((param != null) && (intr != null)) {
      PropertyName name = intr.findNameForDeserialization(param);
      if (name != null) {
        return name;
      }
      


      String str = intr.findImplicitPropertyName(param);
      if ((str != null) && (!str.isEmpty())) {
        return PropertyName.construct(str);
      }
    }
    return null;
  }
  







  protected PropertyMetadata _getSetterInfo(DeserializationContext ctxt, BeanProperty prop, PropertyMetadata metadata)
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    DeserializationConfig config = ctxt.getConfig();
    
    boolean needMerge = true;
    com.fasterxml.jackson.annotation.Nulls valueNulls = null;
    com.fasterxml.jackson.annotation.Nulls contentNulls = null;
    


    AnnotatedMember prim = prop.getMember();
    
    if (prim != null)
    {
      if (intr != null) {
        JsonSetter.Value setterInfo = intr.findSetterInfo(prim);
        if (setterInfo != null) {
          valueNulls = setterInfo.nonDefaultValueNulls();
          contentNulls = setterInfo.nonDefaultContentNulls();
        }
      }
      

      if ((needMerge) || (valueNulls == null) || (contentNulls == null)) {
        com.fasterxml.jackson.databind.cfg.ConfigOverride co = config.getConfigOverride(prop.getType().getRawClass());
        JsonSetter.Value setterInfo = co.getSetterInfo();
        if (setterInfo != null) {
          if (valueNulls == null) {
            valueNulls = setterInfo.nonDefaultValueNulls();
          }
          if (contentNulls == null) {
            contentNulls = setterInfo.nonDefaultContentNulls();
          }
        }
      }
    }
    if ((needMerge) || (valueNulls == null) || (contentNulls == null)) {
      JsonSetter.Value setterInfo = config.getDefaultSetterInfo();
      if (valueNulls == null) {
        valueNulls = setterInfo.nonDefaultValueNulls();
      }
      if (contentNulls == null) {
        contentNulls = setterInfo.nonDefaultContentNulls();
      }
    }
    if ((valueNulls != null) || (contentNulls != null)) {
      metadata = metadata.withNulls(valueNulls, contentNulls);
    }
    return metadata;
  }
  








  public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    JavaType elemType = type.getContentType();
    

    JsonDeserializer<Object> contentDeser = (JsonDeserializer)elemType.getValueHandler();
    
    TypeDeserializer elemTypeDeser = (TypeDeserializer)elemType.getTypeHandler();
    
    if (elemTypeDeser == null) {
      elemTypeDeser = findTypeDeserializer(config, elemType);
    }
    
    JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
    Class<?> raw;
    if (deser == null) {
      if (contentDeser == null) {
        raw = elemType.getRawClass();
        if (elemType.isPrimitive()) {
          return com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers.forType(raw);
        }
        if (raw == String.class) {
          return com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer.instance;
        }
      }
      deser = new com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
    }
    
    if (_factoryConfig.hasDeserializerModifiers()) {
      for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
        deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
      }
    }
    return deser;
  }
  








  public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    JavaType contentType = type.getContentType();
    
    JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
    DeserializationConfig config = ctxt.getConfig();
    

    TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
    
    if (contentTypeDeser == null) {
      contentTypeDeser = findTypeDeserializer(config, contentType);
    }
    
    JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
    
    if (deser == null) {
      Class<?> collectionClass = type.getRawClass();
      if (contentDeser == null)
      {
        if (java.util.EnumSet.class.isAssignableFrom(collectionClass)) {
          deser = new com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer(contentType, null);
        }
      }
    }
    




    ValueInstantiator inst;
    



    if (deser == null) {
      if ((type.isInterface()) || (type.isAbstract())) {
        CollectionType implType = _mapAbstractCollectionType(type, config);
        if (implType == null)
        {
          if (type.getTypeHandler() == null) {
            throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type);
          }
          deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
        } else {
          type = implType;
          
          beanDesc = config.introspectForCreation(type);
        }
      }
      if (deser == null) {
        inst = findValueInstantiator(ctxt, beanDesc);
        if (!inst.canCreateUsingDefault())
        {
          if (type.hasRawClass(java.util.concurrent.ArrayBlockingQueue.class)) {
            return new com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
          }
          
          deser = com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers.findForCollection(ctxt, type);
          if (deser != null) {
            return deser;
          }
        }
        
        if (contentType.hasRawClass(String.class))
        {
          deser = new com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer(type, contentDeser, inst);
        } else {
          deser = new com.fasterxml.jackson.databind.deser.std.CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
        }
      }
    }
    
    if (_factoryConfig.hasDeserializerModifiers()) {
      for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
        deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
      }
    }
    return deser;
  }
  
  protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config)
  {
    Class<?> collectionClass = ContainerDefaultMappings.findCollectionFallback(type);
    if (collectionClass != null) {
      return (CollectionType)config.constructSpecializedType(type, collectionClass);
    }
    return null;
  }
  



  public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    JavaType contentType = type.getContentType();
    
    JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
    DeserializationConfig config = ctxt.getConfig();
    

    TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
    
    if (contentTypeDeser == null) {
      contentTypeDeser = findTypeDeserializer(config, contentType);
    }
    JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
    
    if (deser != null)
    {
      if (_factoryConfig.hasDeserializerModifiers()) {
        for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
          deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
        }
      }
    }
    return deser;
  }
  








  public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    JavaType keyType = type.getKeyType();
    JavaType contentType = type.getContentType();
    


    JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
    

    KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
    
    TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
    
    if (contentTypeDeser == null) {
      contentTypeDeser = findTypeDeserializer(config, contentType);
    }
    

    JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
    
    Class<?> mapClass;
    if (deser == null)
    {
      mapClass = type.getRawClass();
      if (java.util.EnumMap.class.isAssignableFrom(mapClass))
      {
        ValueInstantiator inst;
        
        ValueInstantiator inst;
        if (mapClass == java.util.EnumMap.class) {
          inst = null;
        } else {
          inst = findValueInstantiator(ctxt, beanDesc);
        }
        Class<?> kt = keyType.getRawClass();
        if ((kt == null) || (!ClassUtil.isEnumType(kt))) {
          throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
        }
        deser = new com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer(type, inst, null, contentDeser, contentTypeDeser, null);
      }
      












      if (deser == null) {
        if ((type.isInterface()) || (type.isAbstract())) {
          MapType fallback = _mapAbstractMapType(type, config);
          if (fallback != null) {
            type = fallback;
            mapClass = type.getRawClass();
            
            beanDesc = config.introspectForCreation(type);
          }
          else {
            if (type.getTypeHandler() == null) {
              throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type);
            }
            deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
          }
        }
        else {
          deser = com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers.findForMap(ctxt, type);
          if (deser != null) {
            return deser;
          }
        }
        if (deser == null) {
          ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
          



          com.fasterxml.jackson.databind.deser.std.MapDeserializer md = new com.fasterxml.jackson.databind.deser.std.MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
          com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc
            .getClassInfo());
          
          java.util.Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForDeserialization();
          md.setIgnorableProperties(ignored);
          deser = md;
        }
      }
    }
    if (_factoryConfig.hasDeserializerModifiers()) {
      for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
        deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
      }
    }
    return deser;
  }
  
  protected MapType _mapAbstractMapType(JavaType type, DeserializationConfig config)
  {
    Class<?> mapClass = ContainerDefaultMappings.findMapFallback(type);
    if (mapClass != null) {
      return (MapType)config.constructSpecializedType(type, mapClass);
    }
    return null;
  }
  




  public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    JavaType keyType = type.getKeyType();
    JavaType contentType = type.getContentType();
    DeserializationConfig config = ctxt.getConfig();
    


    JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
    

    KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
    





    TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
    
    if (contentTypeDeser == null) {
      contentTypeDeser = findTypeDeserializer(config, contentType);
    }
    JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
    
    if (deser != null)
    {
      if (_factoryConfig.hasDeserializerModifiers()) {
        for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
          deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
        }
      }
    }
    return deser;
  }
  











  public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    Class<?> enumClass = type.getRawClass();
    
    JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
    ValueInstantiator valueInstantiator;
    if (deser == null) {
      valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
      
      SettableBeanProperty[] creatorProps = valueInstantiator == null ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
      
      for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
        if (_hasCreatorAnnotation(ctxt, factory)) {
          if (factory.getParameterCount() == 0) {
            deser = com.fasterxml.jackson.databind.deser.std.EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
            break;
          }
          Class<?> returnType = factory.getRawReturnType();
          
          if (returnType.isAssignableFrom(enumClass)) {
            deser = com.fasterxml.jackson.databind.deser.std.EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
            break;
          }
        }
      }
      

      if (deser == null)
      {

        deser = new com.fasterxml.jackson.databind.deser.std.EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor()), Boolean.valueOf(config.isEnabled(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)));
      }
    }
    

    if (_factoryConfig.hasDeserializerModifiers()) {
      for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
        deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser);
      }
    }
    return deser;
  }
  



  public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc)
    throws JsonMappingException
  {
    Class<? extends com.fasterxml.jackson.databind.JsonNode> nodeClass = nodeType.getRawClass();
    
    JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
    
    if (custom != null) {
      return custom;
    }
    return com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer.getDeserializer(nodeClass);
  }
  


  public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    JavaType contentType = type.getContentType();
    
    JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
    DeserializationConfig config = ctxt.getConfig();
    
    TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
    if (contentTypeDeser == null) {
      contentTypeDeser = findTypeDeserializer(config, contentType);
    }
    JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
    
    Class<?> rawType;
    if (deser == null)
    {
      if (type.isTypeOrSubTypeOf(java.util.concurrent.atomic.AtomicReference.class)) {
        rawType = type.getRawClass();
        ValueInstantiator inst;
        ValueInstantiator inst; if (rawType == java.util.concurrent.atomic.AtomicReference.class) {
          inst = null;

        }
        else
        {

          inst = findValueInstantiator(ctxt, beanDesc);
        }
        return new com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
      }
    }
    if (deser != null)
    {
      if (_factoryConfig.hasDeserializerModifiers()) {
        for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
          deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
        }
      }
    }
    return deser;
  }
  








  public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType)
    throws JsonMappingException
  {
    BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
    com.fasterxml.jackson.databind.introspect.AnnotatedClass ac = bean.getClassInfo();
    AnnotationIntrospector ai = config.getAnnotationIntrospector();
    TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
    


    Collection<com.fasterxml.jackson.databind.jsontype.NamedType> subtypes = null;
    if (b == null) {
      b = config.getDefaultTyper(baseType);
      if (b == null) {
        return null;
      }
    } else {
      subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
    }
    

    if ((b.getDefaultImpl() == null) && (baseType.isAbstract())) {
      JavaType defaultType = mapAbstractType(config, baseType);
      if ((defaultType != null) && (!defaultType.hasRawClass(baseType.getRawClass()))) {
        b = b.defaultImpl(defaultType.getRawClass());
      }
    }
    
    try
    {
      return b.buildTypeDeserializer(config, baseType, subtypes);
    } catch (IllegalArgumentException e0) {
      InvalidDefinitionException e = InvalidDefinitionException.from((com.fasterxml.jackson.core.JsonParser)null, 
        ClassUtil.exceptionMessage(e0), baseType);
      e.initCause(e0);
      throw e;
    }
  }
  






  protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    return com.fasterxml.jackson.databind.ext.OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
  }
  








  public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    KeyDeserializer deser = null;
    BeanDescription beanDesc; if (_factoryConfig.hasKeyDeserializers()) {
      beanDesc = config.introspectClassAnnotations(type.getRawClass());
      for (KeyDeserializers d : _factoryConfig.keyDeserializers()) {
        deser = d.findKeyDeserializer(type, config, beanDesc);
        if (deser != null) {
          break;
        }
      }
    }
    
    if (deser == null) {
      if (type.isEnumType()) {
        deser = _createEnumKeyDeserializer(ctxt, type);
      } else {
        deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
      }
    }
    
    if ((deser != null) && 
      (_factoryConfig.hasDeserializerModifiers())) {
      for (BeanDeserializerModifier mod : _factoryConfig.deserializerModifiers()) {
        deser = mod.modifyKeyDeserializer(config, type, deser);
      }
    }
    
    return deser;
  }
  

  private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type)
    throws JsonMappingException
  {
    DeserializationConfig config = ctxt.getConfig();
    Class<?> enumClass = type.getRawClass();
    
    BeanDescription beanDesc = config.introspect(type);
    
    KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
    if (des != null) {
      return des;
    }
    
    JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
    if (custom != null) {
      return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
    }
    JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
    if (valueDesForKey != null) {
      return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
    }
    
    com.fasterxml.jackson.databind.util.EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor());
    
    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
      if (_hasCreatorAnnotation(ctxt, factory)) {
        int argCount = factory.getParameterCount();
        if (argCount == 1) {
          Class<?> returnType = factory.getRawReturnType();
          
          if (returnType.isAssignableFrom(enumClass))
          {
            if (factory.getRawParameterType(0) != String.class) {
              throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
            }
            if (config.canOverrideAccessModifiers()) {
              ClassUtil.checkAndFixAccess(factory.getMember(), ctxt
                .isEnabled(com.fasterxml.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
          }
        }
        
        throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
      }
    }
    
    return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
  }
  




















  public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated)
    throws JsonMappingException
  {
    AnnotationIntrospector ai = config.getAnnotationIntrospector();
    TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
    
    if (b == null) {
      return findTypeDeserializer(config, baseType);
    }
    
    Collection<com.fasterxml.jackson.databind.jsontype.NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
    try
    {
      return b.buildTypeDeserializer(config, baseType, subtypes);
    } catch (IllegalArgumentException e0) {
      InvalidDefinitionException e = InvalidDefinitionException.from((com.fasterxml.jackson.core.JsonParser)null, 
        ClassUtil.exceptionMessage(e0), baseType);
      e.initCause(e0);
      throw e;
    }
  }
  












  public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity)
    throws JsonMappingException
  {
    AnnotationIntrospector ai = config.getAnnotationIntrospector();
    TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
    JavaType contentType = containerType.getContentType();
    
    if (b == null) {
      return findTypeDeserializer(config, contentType);
    }
    
    Collection<com.fasterxml.jackson.databind.jsontype.NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
    
    return b.buildTypeDeserializer(config, contentType, subtypes);
  }
  








  public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
    throws JsonMappingException
  {
    Class<?> rawType = type.getRawClass();
    
    if ((rawType == CLASS_OBJECT) || (rawType == CLASS_SERIALIZABLE))
    {
      DeserializationConfig config = ctxt.getConfig();
      JavaType mt;
      JavaType mt;
      JavaType lt; if (_factoryConfig.hasAbstractTypeResolvers()) {
        JavaType lt = _findRemappedType(config, List.class);
        mt = _findRemappedType(config, Map.class);
      } else {
        lt = mt = null;
      }
      return new com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer(lt, mt);
    }
    
    if ((rawType == CLASS_STRING) || (rawType == CLASS_CHAR_SEQUENCE)) {
      return com.fasterxml.jackson.databind.deser.std.StringDeserializer.instance;
    }
    if (rawType == CLASS_ITERABLE)
    {
      com.fasterxml.jackson.databind.type.TypeFactory tf = ctxt.getTypeFactory();
      JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
      JavaType elemType = (tps == null) || (tps.length != 1) ? com.fasterxml.jackson.databind.type.TypeFactory.unknownType() : tps[0];
      CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
      
      return createCollectionDeserializer(ctxt, ct, beanDesc);
    }
    if (rawType == CLASS_MAP_ENTRY)
    {
      JavaType kt = type.containedTypeOrUnknown(0);
      JavaType vt = type.containedTypeOrUnknown(1);
      TypeDeserializer vts = (TypeDeserializer)vt.getTypeHandler();
      if (vts == null) {
        vts = findTypeDeserializer(ctxt.getConfig(), vt);
      }
      JsonDeserializer<Object> valueDeser = (JsonDeserializer)vt.getValueHandler();
      KeyDeserializer keyDes = (KeyDeserializer)kt.getValueHandler();
      return new com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer(type, keyDes, valueDeser, vts);
    }
    String clsName = rawType.getName();
    if ((rawType.isPrimitive()) || (clsName.startsWith("java.")))
    {
      JsonDeserializer<?> deser = com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(rawType, clsName);
      if (deser == null) {
        deser = com.fasterxml.jackson.databind.deser.std.DateDeserializers.find(rawType, clsName);
      }
      if (deser != null) {
        return deser;
      }
    }
    
    if (rawType == com.fasterxml.jackson.databind.util.TokenBuffer.class) {
      return new com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer();
    }
    JsonDeserializer<?> deser = findOptionalStdDeserializer(ctxt, type, beanDesc);
    if (deser != null) {
      return deser;
    }
    return com.fasterxml.jackson.databind.deser.std.JdkDeserializers.find(rawType, clsName);
  }
  
  protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
    JavaType type = mapAbstractType(config, config.constructType(rawType));
    return (type == null) || (type.hasRawClass(rawType)) ? null : type;
  }
  







  protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends com.fasterxml.jackson.databind.JsonNode> type, DeserializationConfig config, BeanDescription beanDesc)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  


  protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
      
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  


  protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  


  protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
      
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  


  protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
      
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  


  protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
      
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  

  protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  



  protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
      
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  



  protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
    throws JsonMappingException
  {
    for (Deserializers d : _factoryConfig.deserializers()) {
      JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
      
      if (deser != null) {
        return deser;
      }
    }
    return null;
  }
  















  protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (intr != null) {
      Object deserDef = intr.findDeserializer(ann);
      if (deserDef != null) {
        return ctxt.deserializerInstance(ann, deserDef);
      }
    }
    return null;
  }
  






  protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (intr != null) {
      Object deserDef = intr.findKeyDeserializer(ann);
      if (deserDef != null) {
        return ctxt.keyDeserializerInstance(ann, deserDef);
      }
    }
    return null;
  }
  




  protected JsonDeserializer<Object> findContentDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (intr != null) {
      Object deserDef = intr.findContentDeserializer(ann);
      if (deserDef != null) {
        return ctxt.deserializerInstance(ann, deserDef);
      }
    }
    return null;
  }
  










  protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (intr == null) {
      return type;
    }
    



    if (type.isMapLikeType()) {
      JavaType keyType = type.getKeyType();
      if (keyType != null) {
        Object kdDef = intr.findKeyDeserializer(member);
        KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
        if (kd != null) {
          type = ((MapLikeType)type).withKeyValueHandler(kd);
          keyType = type.getKeyType();
        }
      }
    }
    
    if (type.hasContentType()) {
      Object cdDef = intr.findContentDeserializer(member);
      JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
      if (cd != null) {
        type = type.withContentValueHandler(cd);
      }
      TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt
        .getConfig(), type, member);
      if (contentTypeDeser != null) {
        type = type.withContentTypeHandler(contentTypeDeser);
      }
    }
    TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
    
    if (valueTypeDeser != null) {
      type = type.withTypeHandler(valueTypeDeser);
    }
    





    type = intr.refineDeserializationType(ctxt.getConfig(), member, type);
    return type;
  }
  

  protected com.fasterxml.jackson.databind.util.EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMember jsonValueAccessor)
  {
    if (jsonValueAccessor != null) {
      if (config.canOverrideAccessModifiers()) {
        ClassUtil.checkAndFixAccess(jsonValueAccessor.getMember(), config
          .isEnabled(com.fasterxml.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      }
      return com.fasterxml.jackson.databind.util.EnumResolver.constructUnsafeUsingMethod(enumClass, jsonValueAccessor, config
        .getAnnotationIntrospector());
    }
    

    return com.fasterxml.jackson.databind.util.EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
  }
  



  protected boolean _hasCreatorAnnotation(DeserializationContext ctxt, Annotated ann)
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (intr != null) {
      JsonCreator.Mode mode = intr.findCreatorAnnotation(ctxt.getConfig(), ann);
      return (mode != null) && (mode != JsonCreator.Mode.DISABLED);
    }
    return false;
  }
  













  @Deprecated
  protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (intr == null) {
      return type;
    }
    return intr.refineDeserializationType(ctxt.getConfig(), a, type);
  }
  




  @Deprecated
  protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member)
    throws JsonMappingException
  {
    return resolveMemberAndTypeAnnotations(ctxt, member, type);
  }
  



  @Deprecated
  protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType)
  {
    if (enumType == null) {
      return null;
    }
    BeanDescription beanDesc = config.introspect(enumType);
    return beanDesc.findJsonValueMethod();
  }
  



  protected static class ContainerDefaultMappings
  {
    static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
    

    static final HashMap<String, Class<? extends Map>> _mapFallbacks;
    


    static
    {
      HashMap<String, Class<? extends Collection>> fallbacks = new HashMap();
      
      Class<? extends Collection> DEFAULT_LIST = java.util.ArrayList.class;
      Class<? extends Collection> DEFAULT_SET = java.util.HashSet.class;
      
      fallbacks.put(Collection.class.getName(), DEFAULT_LIST);
      fallbacks.put(List.class.getName(), DEFAULT_LIST);
      fallbacks.put(java.util.Set.class.getName(), DEFAULT_SET);
      fallbacks.put(java.util.SortedSet.class.getName(), java.util.TreeSet.class);
      fallbacks.put(java.util.Queue.class.getName(), LinkedList.class);
      

      fallbacks.put(java.util.AbstractList.class.getName(), DEFAULT_LIST);
      fallbacks.put(java.util.AbstractSet.class.getName(), DEFAULT_SET);
      

      fallbacks.put(java.util.Deque.class.getName(), LinkedList.class);
      fallbacks.put(java.util.NavigableSet.class.getName(), java.util.TreeSet.class);
      
      _collectionFallbacks = fallbacks;
      






      HashMap<String, Class<? extends Map>> fallbacks = new HashMap();
      
      Class<? extends Map> DEFAULT_MAP = java.util.LinkedHashMap.class;
      fallbacks.put(Map.class.getName(), DEFAULT_MAP);
      fallbacks.put(java.util.AbstractMap.class.getName(), DEFAULT_MAP);
      fallbacks.put(java.util.concurrent.ConcurrentMap.class.getName(), java.util.concurrent.ConcurrentHashMap.class);
      fallbacks.put(java.util.SortedMap.class.getName(), java.util.TreeMap.class);
      
      fallbacks.put(java.util.NavigableMap.class.getName(), java.util.TreeMap.class);
      fallbacks.put(java.util.concurrent.ConcurrentNavigableMap.class.getName(), java.util.concurrent.ConcurrentSkipListMap.class);
      

      _mapFallbacks = fallbacks;
    }
    
    public static Class<?> findCollectionFallback(JavaType type) {
      return (Class)_collectionFallbacks.get(type.getRawClass().getName());
    }
    
    public static Class<?> findMapFallback(JavaType type) {
      return (Class)_mapFallbacks.get(type.getRawClass().getName());
    }
    
    protected ContainerDefaultMappings() {}
  }
}
