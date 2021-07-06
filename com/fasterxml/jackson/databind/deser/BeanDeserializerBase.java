package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, ValueInstantiator.Gettable, java.io.Serializable
{
  private static final long serialVersionUID = 1L;
  protected static final PropertyName TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
  







  protected final JavaType _beanType;
  







  protected final com.fasterxml.jackson.annotation.JsonFormat.Shape _serializationShape;
  







  protected final ValueInstantiator _valueInstantiator;
  







  protected JsonDeserializer<Object> _delegateDeserializer;
  






  protected JsonDeserializer<Object> _arrayDelegateDeserializer;
  






  protected PropertyBasedCreator _propertyBasedCreator;
  






  protected boolean _nonStandardCreation;
  






  protected boolean _vanillaProcessing;
  






  protected final BeanPropertyMap _beanProperties;
  






  protected final ValueInjector[] _injectables;
  






  protected SettableAnyProperty _anySetter;
  






  protected final Set<String> _ignorableProps;
  






  protected final boolean _ignoreAllUnknown;
  






  protected final boolean _needViewProcesing;
  






  protected final java.util.Map<String, SettableBeanProperty> _backRefs;
  






  protected transient java.util.HashMap<com.fasterxml.jackson.databind.type.ClassKey, JsonDeserializer<Object>> _subDeserializers;
  






  protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
  






  protected com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler _externalTypeIdHandler;
  






  protected final ObjectIdReader _objectIdReader;
  







  protected BeanDeserializerBase(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, java.util.Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews)
  {
    super(beanDesc.getType());
    _beanType = beanDesc.getType();
    _valueInstantiator = builder.getValueInstantiator();
    
    _beanProperties = properties;
    _backRefs = backRefs;
    _ignorableProps = ignorableProps;
    _ignoreAllUnknown = ignoreAllUnknown;
    
    _anySetter = builder.getAnySetter();
    List<ValueInjector> injectables = builder.getInjectables();
    
    _injectables = ((injectables == null) || (injectables.isEmpty()) ? null : (ValueInjector[])injectables.toArray(new ValueInjector[injectables.size()]));
    _objectIdReader = builder.getObjectIdReader();
    



    _nonStandardCreation = ((_unwrappedPropertyHandler != null) || (_valueInstantiator.canCreateUsingDelegate()) || (_valueInstantiator.canCreateUsingArrayDelegate()) || (_valueInstantiator.canCreateFromObjectWith()) || (!_valueInstantiator.canCreateUsingDefault()));
    


    JsonFormat.Value format = beanDesc.findExpectedFormat(null);
    _serializationShape = (format == null ? null : format.getShape());
    
    _needViewProcesing = hasViews;
    _vanillaProcessing = ((!_nonStandardCreation) && (_injectables == null) && (!_needViewProcesing) && (_objectIdReader == null));
  }
  




  protected BeanDeserializerBase(BeanDeserializerBase src)
  {
    this(src, _ignoreAllUnknown);
  }
  
  protected BeanDeserializerBase(BeanDeserializerBase src, boolean ignoreAllUnknown)
  {
    super(_beanType);
    
    _beanType = _beanType;
    
    _valueInstantiator = _valueInstantiator;
    _delegateDeserializer = _delegateDeserializer;
    _propertyBasedCreator = _propertyBasedCreator;
    
    _beanProperties = _beanProperties;
    _backRefs = _backRefs;
    _ignorableProps = _ignorableProps;
    _ignoreAllUnknown = ignoreAllUnknown;
    _anySetter = _anySetter;
    _injectables = _injectables;
    _objectIdReader = _objectIdReader;
    
    _nonStandardCreation = _nonStandardCreation;
    _unwrappedPropertyHandler = _unwrappedPropertyHandler;
    _needViewProcesing = _needViewProcesing;
    _serializationShape = _serializationShape;
    
    _vanillaProcessing = _vanillaProcessing;
  }
  
  protected BeanDeserializerBase(BeanDeserializerBase src, NameTransformer unwrapper)
  {
    super(_beanType);
    
    _beanType = _beanType;
    
    _valueInstantiator = _valueInstantiator;
    _delegateDeserializer = _delegateDeserializer;
    _propertyBasedCreator = _propertyBasedCreator;
    
    _backRefs = _backRefs;
    _ignorableProps = _ignorableProps;
    _ignoreAllUnknown = ((unwrapper != null) || (_ignoreAllUnknown));
    _anySetter = _anySetter;
    _injectables = _injectables;
    _objectIdReader = _objectIdReader;
    
    _nonStandardCreation = _nonStandardCreation;
    UnwrappedPropertyHandler uph = _unwrappedPropertyHandler;
    
    if (unwrapper != null)
    {
      if (uph != null) {
        uph = uph.renameAll(unwrapper);
      }
      
      _beanProperties = _beanProperties.renameAll(unwrapper);
    } else {
      _beanProperties = _beanProperties;
    }
    _unwrappedPropertyHandler = uph;
    _needViewProcesing = _needViewProcesing;
    _serializationShape = _serializationShape;
    

    _vanillaProcessing = false;
  }
  
  public BeanDeserializerBase(BeanDeserializerBase src, ObjectIdReader oir)
  {
    super(_beanType);
    _beanType = _beanType;
    
    _valueInstantiator = _valueInstantiator;
    _delegateDeserializer = _delegateDeserializer;
    _propertyBasedCreator = _propertyBasedCreator;
    
    _backRefs = _backRefs;
    _ignorableProps = _ignorableProps;
    _ignoreAllUnknown = _ignoreAllUnknown;
    _anySetter = _anySetter;
    _injectables = _injectables;
    
    _nonStandardCreation = _nonStandardCreation;
    _unwrappedPropertyHandler = _unwrappedPropertyHandler;
    _needViewProcesing = _needViewProcesing;
    _serializationShape = _serializationShape;
    

    _objectIdReader = oir;
    
    if (oir == null) {
      _beanProperties = _beanProperties;
      _vanillaProcessing = _vanillaProcessing;

    }
    else
    {

      com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty idProp = new com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
      _beanProperties = _beanProperties.withProperty(idProp);
      _vanillaProcessing = false;
    }
  }
  
  public BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps)
  {
    super(_beanType);
    _beanType = _beanType;
    
    _valueInstantiator = _valueInstantiator;
    _delegateDeserializer = _delegateDeserializer;
    _propertyBasedCreator = _propertyBasedCreator;
    
    _backRefs = _backRefs;
    _ignorableProps = ignorableProps;
    _ignoreAllUnknown = _ignoreAllUnknown;
    _anySetter = _anySetter;
    _injectables = _injectables;
    
    _nonStandardCreation = _nonStandardCreation;
    _unwrappedPropertyHandler = _unwrappedPropertyHandler;
    _needViewProcesing = _needViewProcesing;
    _serializationShape = _serializationShape;
    
    _vanillaProcessing = _vanillaProcessing;
    _objectIdReader = _objectIdReader;
    


    _beanProperties = _beanProperties.withoutProperties(ignorableProps);
  }
  



  protected BeanDeserializerBase(BeanDeserializerBase src, BeanPropertyMap beanProps)
  {
    super(_beanType);
    _beanType = _beanType;
    
    _valueInstantiator = _valueInstantiator;
    _delegateDeserializer = _delegateDeserializer;
    _propertyBasedCreator = _propertyBasedCreator;
    
    _beanProperties = beanProps;
    _backRefs = _backRefs;
    _ignorableProps = _ignorableProps;
    _ignoreAllUnknown = _ignoreAllUnknown;
    _anySetter = _anySetter;
    _injectables = _injectables;
    _objectIdReader = _objectIdReader;
    
    _nonStandardCreation = _nonStandardCreation;
    _unwrappedPropertyHandler = _unwrappedPropertyHandler;
    _needViewProcesing = _needViewProcesing;
    _serializationShape = _serializationShape;
    
    _vanillaProcessing = _vanillaProcessing;
  }
  


  public abstract JsonDeserializer<Object> unwrappingDeserializer(NameTransformer paramNameTransformer);
  


  public abstract BeanDeserializerBase withObjectIdReader(ObjectIdReader paramObjectIdReader);
  

  public abstract BeanDeserializerBase withIgnorableProperties(Set<String> paramSet);
  

  public BeanDeserializerBase withBeanProperties(BeanPropertyMap props)
  {
    throw new UnsupportedOperationException("Class " + getClass().getName() + " does not override `withBeanProperties()`, needs to");
  }
  










  protected abstract BeanDeserializerBase asArrayDeserializer();
  









  public void resolve(DeserializationContext ctxt)
    throws JsonMappingException
  {
    com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder extTypes = null;
    
    int end;
    SettableBeanProperty[] creatorProps;
    if (_valueInstantiator.canCreateFromObjectWith()) {
      SettableBeanProperty[] creatorProps = _valueInstantiator.getFromObjectArguments(ctxt.getConfig());
      



      if (_ignorableProps != null) {
        int i = 0; for (end = creatorProps.length; i < end; i++) {
          SettableBeanProperty prop = creatorProps[i];
          if (_ignorableProps.contains(prop.getName())) {
            creatorProps[i].markAsIgnorable();
          }
        }
      }
    } else {
      creatorProps = null;
    }
    UnwrappedPropertyHandler unwrapped = null;
    








    for (SettableBeanProperty prop : _beanProperties) {
      if (!prop.hasValueDeserializer())
      {
        JsonDeserializer<?> deser = findConvertingDeserializer(ctxt, prop);
        if (deser == null) {
          deser = ctxt.findNonContextualValueDeserializer(prop.getType());
        }
        SettableBeanProperty newProp = prop.withValueDeserializer(deser);
        _replaceProperty(_beanProperties, creatorProps, prop, newProp);
      }
    }
    

    for (SettableBeanProperty origProp : _beanProperties) {
      SettableBeanProperty prop = origProp;
      JsonDeserializer<?> deser = prop.getValueDeserializer();
      deser = ctxt.handlePrimaryContextualization(deser, prop, prop.getType());
      prop = prop.withValueDeserializer(deser);
      
      prop = _resolveManagedReferenceProperty(ctxt, prop);
      

      if (!(prop instanceof com.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty)) {
        prop = _resolvedObjectIdProperty(ctxt, prop);
      }
      
      NameTransformer xform = _findPropertyUnwrapper(ctxt, prop);
      if (xform != null) {
        JsonDeserializer<Object> orig = prop.getValueDeserializer();
        JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(xform);
        if ((unwrapping != orig) && (unwrapping != null)) {
          prop = prop.withValueDeserializer(unwrapping);
          if (unwrapped == null) {
            unwrapped = new UnwrappedPropertyHandler();
          }
          unwrapped.addProperty(prop);
          



          _beanProperties.remove(prop);
          continue;
        }
      }
      


      PropertyMetadata md = prop.getMetadata();
      prop = _resolveMergeAndNullSettings(ctxt, prop, md);
      

      prop = _resolveInnerClassValuedProperty(ctxt, prop);
      if (prop != origProp) {
        _replaceProperty(_beanProperties, creatorProps, origProp, prop);
      }
      


      if (prop.hasValueTypeDeserializer()) {
        TypeDeserializer typeDeser = prop.getValueTypeDeserializer();
        if (typeDeser.getTypeInclusion() == com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY) {
          if (extTypes == null) {
            extTypes = com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.builder(_beanType);
          }
          extTypes.addExternal(prop, typeDeser);
          
          _beanProperties.remove(prop);
        }
      }
    }
    

    if ((_anySetter != null) && (!_anySetter.hasValueDeserializer())) {
      _anySetter = _anySetter.withValueDeserializer(findDeserializer(ctxt, _anySetter
        .getType(), _anySetter.getProperty()));
    }
    
    if (_valueInstantiator.canCreateUsingDelegate()) {
      JavaType delegateType = _valueInstantiator.getDelegateType(ctxt.getConfig());
      if (delegateType == null) {
        ctxt.reportBadDefinition(_beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] { _beanType, _valueInstantiator
        
          .getClass().getName() }));
      }
      _delegateDeserializer = _findDelegateDeserializer(ctxt, delegateType, _valueInstantiator
        .getDelegateCreator());
    }
    

    if (_valueInstantiator.canCreateUsingArrayDelegate()) {
      JavaType delegateType = _valueInstantiator.getArrayDelegateType(ctxt.getConfig());
      if (delegateType == null) {
        ctxt.reportBadDefinition(_beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] { _beanType, _valueInstantiator
        
          .getClass().getName() }));
      }
      _arrayDelegateDeserializer = _findDelegateDeserializer(ctxt, delegateType, _valueInstantiator
        .getArrayDelegateCreator());
    }
    

    if (creatorProps != null) {
      _propertyBasedCreator = PropertyBasedCreator.construct(ctxt, _valueInstantiator, creatorProps, _beanProperties);
    }
    

    if (extTypes != null)
    {

      _externalTypeIdHandler = extTypes.build(_beanProperties);
      
      _nonStandardCreation = true;
    }
    
    _unwrappedPropertyHandler = unwrapped;
    if (unwrapped != null) {
      _nonStandardCreation = true;
    }
    
    _vanillaProcessing = ((_vanillaProcessing) && (!_nonStandardCreation));
  }
  




  protected void _replaceProperty(BeanPropertyMap props, SettableBeanProperty[] creatorProps, SettableBeanProperty origProp, SettableBeanProperty newProp)
  {
    props.replace(origProp, newProp);
    
    if (creatorProps != null)
    {

      int i = 0; for (int len = creatorProps.length; i < len; i++) {
        if (creatorProps[i] == origProp) {
          creatorProps[i] = newProp;
          return;
        }
      }
    }
  }
  












  private JsonDeserializer<Object> _findDelegateDeserializer(DeserializationContext ctxt, JavaType delegateType, com.fasterxml.jackson.databind.introspect.AnnotatedWithParams delegateCreator)
    throws JsonMappingException
  {
    com.fasterxml.jackson.databind.BeanProperty.Std property = new com.fasterxml.jackson.databind.BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, null, delegateCreator, PropertyMetadata.STD_OPTIONAL);
    

    TypeDeserializer td = (TypeDeserializer)delegateType.getTypeHandler();
    if (td == null) {
      td = ctxt.getConfig().findTypeDeserializer(delegateType);
    }
    

    JsonDeserializer<Object> dd = (JsonDeserializer)delegateType.getValueHandler();
    if (dd == null) {
      dd = findDeserializer(ctxt, delegateType, property);
    } else {
      dd = ctxt.handleSecondaryContextualization(dd, property, delegateType);
    }
    if (td != null) {
      td = td.forProperty(property);
      return new com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer(td, dd);
    }
    return dd;
  }
  











  protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, SettableBeanProperty prop)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (intr != null) {
      Object convDef = intr.findDeserializationConverter(prop.getMember());
      if (convDef != null) {
        com.fasterxml.jackson.databind.util.Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
        JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        

        JsonDeserializer<?> deser = ctxt.findNonContextualValueDeserializer(delegateType);
        return new com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer(conv, delegateType, deser);
      }
    }
    return null;
  }
  








  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, com.fasterxml.jackson.databind.BeanProperty property)
    throws JsonMappingException
  {
    ObjectIdReader oir = _objectIdReader;
    

    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    com.fasterxml.jackson.databind.introspect.AnnotatedMember accessor = _neitherNull(property, intr) ? property.getMember() : null;
    if (accessor != null) {
      ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
      if (objectIdInfo != null)
      {
        objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
        
        Class<?> implClass = objectIdInfo.getGeneratorType();
        



        com.fasterxml.jackson.annotation.ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
        com.fasterxml.jackson.annotation.ObjectIdGenerator<?> idGen; JavaType idType; SettableBeanProperty idProp; com.fasterxml.jackson.annotation.ObjectIdGenerator<?> idGen; if (implClass == com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class) {
          PropertyName propName = objectIdInfo.getPropertyName();
          SettableBeanProperty idProp = findProperty(propName);
          if (idProp == null) {
            ctxt.reportBadDefinition(_beanType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", new Object[] {
            
              handledType().getName(), propName }));
          }
          JavaType idType = idProp.getType();
          idGen = new com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
        } else {
          JavaType type = ctxt.constructType(implClass);
          idType = ctxt.getTypeFactory().findTypeParameters(type, com.fasterxml.jackson.annotation.ObjectIdGenerator.class)[0];
          idProp = null;
          idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
        }
        JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
        oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
      }
    }
    

    BeanDeserializerBase contextual = this;
    if ((oir != null) && (oir != _objectIdReader)) {
      contextual = contextual.withObjectIdReader(oir);
    }
    
    if (accessor != null) {
      com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
      if (ignorals != null) {
        Set<String> ignored = ignorals.findIgnoredForDeserialization();
        if (!ignored.isEmpty()) {
          Set<String> prev = _ignorableProps;
          if ((prev != null) && (!prev.isEmpty())) {
            ignored = new java.util.HashSet(ignored);
            ignored.addAll(prev);
          }
          contextual = contextual.withIgnorableProperties(ignored);
        }
      }
    }
    

    JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
    com.fasterxml.jackson.annotation.JsonFormat.Shape shape = null;
    if (format != null) {
      if (format.hasShape()) {
        shape = format.getShape();
      }
      
      Boolean B = format.getFeature(com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
      if (B != null) {
        BeanPropertyMap propsOrig = _beanProperties;
        BeanPropertyMap props = propsOrig.withCaseInsensitivity(B.booleanValue());
        if (props != propsOrig) {
          contextual = contextual.withBeanProperties(props);
        }
      }
    }
    
    if (shape == null) {
      shape = _serializationShape;
    }
    if (shape == com.fasterxml.jackson.annotation.JsonFormat.Shape.ARRAY) {
      contextual = contextual.asArrayDeserializer();
    }
    return contextual;
  }
  





  protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationContext ctxt, SettableBeanProperty prop)
    throws JsonMappingException
  {
    String refName = prop.getManagedReferenceName();
    if (refName == null) {
      return prop;
    }
    JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
    SettableBeanProperty backProp = valueDeser.findBackReference(refName);
    if (backProp == null) {
      ctxt.reportBadDefinition(_beanType, String.format("Cannot handle managed/back reference '%s': no back reference property found from type %s", new Object[] { refName, prop
      
        .getType() }));
    }
    
    JavaType referredType = _beanType;
    JavaType backRefType = backProp.getType();
    boolean isContainer = prop.getType().isContainerType();
    if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
      ctxt.reportBadDefinition(_beanType, String.format("Cannot handle managed/back reference '%s': back reference type (%s) not compatible with managed type (%s)", new Object[] { refName, backRefType
      
        .getRawClass().getName(), referredType
        .getRawClass().getName() }));
    }
    return new com.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty(prop, refName, backProp, isContainer);
  }
  




  protected SettableBeanProperty _resolvedObjectIdProperty(DeserializationContext ctxt, SettableBeanProperty prop)
    throws JsonMappingException
  {
    ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
    JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
    ObjectIdReader objectIdReader = valueDeser == null ? null : valueDeser.getObjectIdReader();
    if ((objectIdInfo == null) && (objectIdReader == null)) {
      return prop;
    }
    return new com.fasterxml.jackson.databind.deser.impl.ObjectIdReferenceProperty(prop, objectIdInfo);
  }
  





  protected NameTransformer _findPropertyUnwrapper(DeserializationContext ctxt, SettableBeanProperty prop)
    throws JsonMappingException
  {
    com.fasterxml.jackson.databind.introspect.AnnotatedMember am = prop.getMember();
    if (am != null) {
      NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
      if (unwrapper != null)
      {

        if ((prop instanceof CreatorProperty)) {
          ctxt.reportBadDefinition(getValueType(), String.format("Cannot define Creator property \"%s\" as `@JsonUnwrapped`: combination not yet supported", new Object[] {prop
          
            .getName() }));
        }
        return unwrapper;
      }
    }
    return null;
  }
  








  protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationContext ctxt, SettableBeanProperty prop)
  {
    JsonDeserializer<Object> deser = prop.getValueDeserializer();
    
    if ((deser instanceof BeanDeserializerBase)) {
      BeanDeserializerBase bd = (BeanDeserializerBase)deser;
      ValueInstantiator vi = bd.getValueInstantiator();
      if (!vi.canCreateUsingDefault()) {
        Class<?> valueClass = prop.getType().getRawClass();
        
        Class<?> enclosing = ClassUtil.getOuterClass(valueClass);
        
        if ((enclosing != null) && (enclosing == _beanType.getRawClass())) {
          for (java.lang.reflect.Constructor<?> ctor : valueClass.getConstructors()) {
            Class<?>[] paramTypes = ctor.getParameterTypes();
            if ((paramTypes.length == 1) && 
              (enclosing.equals(paramTypes[0]))) {
              if (ctxt.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(ctor, ctxt.isEnabled(com.fasterxml.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
              }
              return new com.fasterxml.jackson.databind.deser.impl.InnerClassProperty(prop, ctor);
            }
          }
        }
      }
    }
    
    return prop;
  }
  


  protected SettableBeanProperty _resolveMergeAndNullSettings(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata)
    throws JsonMappingException
  {
    com.fasterxml.jackson.databind.PropertyMetadata.MergeInfo merge = propMetadata.getMergeInfo();
    
    if (merge != null) {
      JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
      Boolean mayMerge = valueDeser.supportsUpdate(ctxt.getConfig());
      
      if (mayMerge == null)
      {
        if (fromDefaults) {
          return prop;
        }
      } else if (!mayMerge.booleanValue()) {
        if (!fromDefaults)
        {

          ctxt.handleBadMerge(valueDeser);
        }
        return prop;
      }
      
      com.fasterxml.jackson.databind.introspect.AnnotatedMember accessor = getter;
      accessor.fixAccess(ctxt.isEnabled(com.fasterxml.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      if (!(prop instanceof com.fasterxml.jackson.databind.deser.impl.SetterlessProperty)) {
        prop = com.fasterxml.jackson.databind.deser.impl.MergingSettableBeanProperty.construct(prop, accessor);
      }
    }
    

    NullValueProvider nuller = findValueNullProvider(ctxt, prop, propMetadata);
    if (nuller != null) {
      prop = prop.withNullProvider(nuller);
    }
    return prop;
  }
  







  public com.fasterxml.jackson.databind.util.AccessPattern getNullAccessPattern()
  {
    return com.fasterxml.jackson.databind.util.AccessPattern.ALWAYS_NULL;
  }
  

  public com.fasterxml.jackson.databind.util.AccessPattern getEmptyAccessPattern()
  {
    return com.fasterxml.jackson.databind.util.AccessPattern.DYNAMIC;
  }
  
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
  {
    try
    {
      return _valueInstantiator.createUsingDefault(ctxt);
    } catch (IOException e) {
      return ClassUtil.throwAsMappingException(ctxt, e);
    }
  }
  





  public boolean isCachable()
  {
    return true;
  }
  


  public Boolean supportsUpdate(com.fasterxml.jackson.databind.DeserializationConfig config)
  {
    return Boolean.TRUE;
  }
  
  public Class<?> handledType()
  {
    return _beanType.getRawClass();
  }
  





  public ObjectIdReader getObjectIdReader()
  {
    return _objectIdReader;
  }
  
  public boolean hasProperty(String propertyName) {
    return _beanProperties.find(propertyName) != null;
  }
  
  public boolean hasViews() {
    return _needViewProcesing;
  }
  


  public int getPropertyCount()
  {
    return _beanProperties.size();
  }
  
  public java.util.Collection<Object> getKnownPropertyNames()
  {
    java.util.ArrayList<Object> names = new java.util.ArrayList();
    for (SettableBeanProperty prop : _beanProperties) {
      names.add(prop.getName());
    }
    return names;
  }
  

  @Deprecated
  public final Class<?> getBeanClass()
  {
    return _beanType.getRawClass();
  }
  
  public JavaType getValueType() { return _beanType; }
  







  public java.util.Iterator<SettableBeanProperty> properties()
  {
    if (_beanProperties == null) {
      throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
    }
    return _beanProperties.iterator();
  }
  







  public java.util.Iterator<SettableBeanProperty> creatorProperties()
  {
    if (_propertyBasedCreator == null) {
      return java.util.Collections.emptyList().iterator();
    }
    return _propertyBasedCreator.properties().iterator();
  }
  

  public SettableBeanProperty findProperty(PropertyName propertyName)
  {
    return findProperty(propertyName.getSimpleName());
  }
  








  public SettableBeanProperty findProperty(String propertyName)
  {
    SettableBeanProperty prop = _beanProperties == null ? null : _beanProperties.find(propertyName);
    if ((prop == null) && (_propertyBasedCreator != null)) {
      prop = _propertyBasedCreator.findCreatorProperty(propertyName);
    }
    return prop;
  }
  











  public SettableBeanProperty findProperty(int propertyIndex)
  {
    SettableBeanProperty prop = _beanProperties == null ? null : _beanProperties.find(propertyIndex);
    if ((prop == null) && (_propertyBasedCreator != null)) {
      prop = _propertyBasedCreator.findCreatorProperty(propertyIndex);
    }
    return prop;
  }
  





  public SettableBeanProperty findBackReference(String logicalName)
  {
    if (_backRefs == null) {
      return null;
    }
    return (SettableBeanProperty)_backRefs.get(logicalName);
  }
  
  public ValueInstantiator getValueInstantiator()
  {
    return _valueInstantiator;
  }
  



















  public void replaceProperty(SettableBeanProperty original, SettableBeanProperty replacement)
  {
    _beanProperties.replace(original, replacement);
  }
  







  public abstract Object deserializeFromObject(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
    throws IOException;
  






  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    if (_objectIdReader != null)
    {
      if (p.canReadObjectId()) {
        Object id = p.getObjectId();
        if (id != null) {
          Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
          return _handleTypedObjectId(p, ctxt, ob, id);
        }
      }
      
      JsonToken t = p.getCurrentToken();
      if (t != null)
      {
        if (t.isScalarValue()) {
          return deserializeFromObjectId(p, ctxt);
        }
        
        if (t == JsonToken.START_OBJECT) {
          t = p.nextToken();
        }
        if ((t == JsonToken.FIELD_NAME) && (_objectIdReader.maySerializeAsObject()) && 
          (_objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p))) {
          return deserializeFromObjectId(p, ctxt);
        }
      }
    }
    
    return typeDeserializer.deserializeTypedFromObject(p, ctxt);
  }
  









  protected Object _handleTypedObjectId(JsonParser p, DeserializationContext ctxt, Object pojo, Object rawId)
    throws IOException
  {
    JsonDeserializer<Object> idDeser = _objectIdReader.getDeserializer();
    
    Object id;
    Object id;
    if (idDeser.handledType() == rawId.getClass())
    {
      id = rawId;
    } else {
      id = _convertObjectId(p, ctxt, rawId, idDeser);
    }
    
    com.fasterxml.jackson.databind.deser.impl.ReadableObjectId roid = ctxt.findObjectId(id, _objectIdReader.generator, _objectIdReader.resolver);
    roid.bindItem(pojo);
    
    SettableBeanProperty idProp = _objectIdReader.idProperty;
    if (idProp != null) {
      return idProp.setAndReturn(pojo, id);
    }
    return pojo;
  }
  










  protected Object _convertObjectId(JsonParser p, DeserializationContext ctxt, Object rawId, JsonDeserializer<Object> idDeser)
    throws IOException
  {
    TokenBuffer buf = new TokenBuffer(p, ctxt);
    if ((rawId instanceof String)) {
      buf.writeString((String)rawId);
    } else if ((rawId instanceof Long)) {
      buf.writeNumber(((Long)rawId).longValue());
    } else if ((rawId instanceof Integer)) {
      buf.writeNumber(((Integer)rawId).intValue());


    }
    else
    {

      buf.writeObject(rawId);
    }
    JsonParser bufParser = buf.asParser();
    bufParser.nextToken();
    return idDeser.deserialize(bufParser, ctxt);
  }
  







  protected Object deserializeWithObjectId(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    return deserializeFromObject(p, ctxt);
  }
  



  protected Object deserializeFromObjectId(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    Object id = _objectIdReader.readObjectReference(p, ctxt);
    com.fasterxml.jackson.databind.deser.impl.ReadableObjectId roid = ctxt.findObjectId(id, _objectIdReader.generator, _objectIdReader.resolver);
    
    Object pojo = roid.resolve();
    if (pojo == null)
    {

      throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] (for " + _beanType + ").", p.getCurrentLocation(), roid);
    }
    return pojo;
  }
  
  protected Object deserializeFromObjectUsingNonDefault(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
    if (delegateDeser != null) {
      return _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
        .deserialize(p, ctxt));
    }
    if (_propertyBasedCreator != null) {
      return _deserializeUsingPropertyBased(p, ctxt);
    }
    


    Class<?> raw = _beanType.getRawClass();
    if (ClassUtil.isNonStaticInnerClass(raw)) {
      return ctxt.handleMissingInstantiator(raw, null, p, "non-static inner classes like this can only by instantiated using default, no-argument constructor", new Object[0]);
    }
    
    return ctxt.handleMissingInstantiator(raw, getValueInstantiator(), p, "cannot deserialize from Object value (no delegate- or property-based Creator)", new Object[0]);
  }
  

  protected abstract Object _deserializeUsingPropertyBased(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
    throws IOException;
  

  public Object deserializeFromNumber(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_objectIdReader != null) {
      return deserializeFromObjectId(p, ctxt);
    }
    JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
    JsonParser.NumberType nt = p.getNumberType();
    if (nt == JsonParser.NumberType.INT) {
      if ((delegateDeser != null) && 
        (!_valueInstantiator.canCreateFromInt())) {
        Object bean = _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
          .deserialize(p, ctxt));
        if (_injectables != null) {
          injectValues(ctxt, bean);
        }
        return bean;
      }
      
      return _valueInstantiator.createFromInt(ctxt, p.getIntValue());
    }
    if (nt == JsonParser.NumberType.LONG) {
      if ((delegateDeser != null) && 
        (!_valueInstantiator.canCreateFromInt())) {
        Object bean = _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
          .deserialize(p, ctxt));
        if (_injectables != null) {
          injectValues(ctxt, bean);
        }
        return bean;
      }
      
      return _valueInstantiator.createFromLong(ctxt, p.getLongValue());
    }
    
    if (delegateDeser != null) {
      Object bean = _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
        .deserialize(p, ctxt));
      if (_injectables != null) {
        injectValues(ctxt, bean);
      }
      return bean;
    }
    return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", new Object[] {p
    
      .getNumberValue() });
  }
  

  public Object deserializeFromString(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_objectIdReader != null) {
      return deserializeFromObjectId(p, ctxt);
    }
    

    JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
    if ((delegateDeser != null) && 
      (!_valueInstantiator.canCreateFromString())) {
      Object bean = _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
        .deserialize(p, ctxt));
      if (_injectables != null) {
        injectValues(ctxt, bean);
      }
      return bean;
    }
    
    return _valueInstantiator.createFromString(ctxt, p.getText());
  }
  



  public Object deserializeFromDouble(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonParser.NumberType t = p.getNumberType();
    
    if ((t == JsonParser.NumberType.DOUBLE) || (t == JsonParser.NumberType.FLOAT)) {
      JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
      if ((delegateDeser != null) && 
        (!_valueInstantiator.canCreateFromDouble())) {
        Object bean = _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
          .deserialize(p, ctxt));
        if (_injectables != null) {
          injectValues(ctxt, bean);
        }
        return bean;
      }
      
      return _valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
    }
    
    JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
    if (delegateDeser != null) {
      return _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
        .deserialize(p, ctxt));
    }
    return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", new Object[] {p
    
      .getNumberValue() });
  }
  


  public Object deserializeFromBoolean(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
    if ((delegateDeser != null) && 
      (!_valueInstantiator.canCreateFromBoolean())) {
      Object bean = _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
        .deserialize(p, ctxt));
      if (_injectables != null) {
        injectValues(ctxt, bean);
      }
      return bean;
    }
    
    boolean value = p.getCurrentToken() == JsonToken.VALUE_TRUE;
    return _valueInstantiator.createFromBoolean(ctxt, value);
  }
  
  public Object deserializeFromArray(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonDeserializer<Object> delegateDeser = _arrayDelegateDeserializer;
    
    if ((delegateDeser != null) || ((delegateDeser = _delegateDeserializer) != null)) {
      Object bean = _valueInstantiator.createUsingArrayDelegate(ctxt, delegateDeser
        .deserialize(p, ctxt));
      if (_injectables != null) {
        injectValues(ctxt, bean);
      }
      return bean;
    }
    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
      JsonToken t = p.nextToken();
      if ((t == JsonToken.END_ARRAY) && (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT))) {
        return null;
      }
      Object value = deserialize(p, ctxt);
      if (p.nextToken() != JsonToken.END_ARRAY) {
        handleMissingEndArrayForSingle(p, ctxt);
      }
      return value;
    }
    if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
      JsonToken t = p.nextToken();
      if (t == JsonToken.END_ARRAY) {
        return null;
      }
      return ctxt.handleUnexpectedToken(getValueType(ctxt), JsonToken.START_ARRAY, p, null, new Object[0]);
    }
    return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
  }
  


  public Object deserializeFromEmbedded(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_objectIdReader != null) {
      return deserializeFromObjectId(p, ctxt);
    }
    
    JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
    if ((delegateDeser != null) && 
      (!_valueInstantiator.canCreateFromString())) {
      Object bean = _valueInstantiator.createUsingDelegate(ctxt, delegateDeser
        .deserialize(p, ctxt));
      if (_injectables != null) {
        injectValues(ctxt, bean);
      }
      return bean;
    }
    





    Object value = p.getEmbeddedObject();
    if ((value != null) && 
      (!_beanType.isTypeOrSuperTypeOf(value.getClass())))
    {
      value = ctxt.handleWeirdNativeValue(_beanType, value, p);
    }
    
    return value;
  }
  


  private final JsonDeserializer<Object> _delegateDeserializer()
  {
    JsonDeserializer<Object> deser = _delegateDeserializer;
    if (deser == null) {
      deser = _arrayDelegateDeserializer;
    }
    return deser;
  }
  






  protected void injectValues(DeserializationContext ctxt, Object bean)
    throws IOException
  {
    for (ValueInjector injector : _injectables) {
      injector.inject(ctxt, bean);
    }
  }
  








  protected Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens)
    throws IOException
  {
    unknownTokens.writeEndObject();
    

    JsonParser bufferParser = unknownTokens.asParser();
    while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
      String propName = bufferParser.getCurrentName();
      
      bufferParser.nextToken();
      handleUnknownProperty(bufferParser, ctxt, bean, propName);
    }
    return bean;
  }
  









  protected void handleUnknownVanilla(JsonParser p, DeserializationContext ctxt, Object beanOrBuilder, String propName)
    throws IOException
  {
    if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
      handleIgnoredProperty(p, ctxt, beanOrBuilder, propName);
    } else if (_anySetter != null) {
      try
      {
        _anySetter.deserializeAndSet(p, ctxt, beanOrBuilder, propName);
      } catch (Exception e) {
        wrapAndThrow(e, beanOrBuilder, propName, ctxt);
      }
      
    } else {
      handleUnknownProperty(p, ctxt, beanOrBuilder, propName);
    }
  }
  






  protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName)
    throws IOException
  {
    if (_ignoreAllUnknown) {
      p.skipChildren();
      return;
    }
    if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
      handleIgnoredProperty(p, ctxt, beanOrClass, propName);
    }
    

    super.handleUnknownProperty(p, ctxt, beanOrClass, propName);
  }
  







  protected void handleIgnoredProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName)
    throws IOException
  {
    if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
      throw com.fasterxml.jackson.databind.exc.IgnoredPropertyException.from(p, beanOrClass, propName, getKnownPropertyNames());
    }
    p.skipChildren();
  }
  














  protected Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens)
    throws IOException
  {
    JsonDeserializer<Object> subDeser = _findSubclassDeserializer(ctxt, bean, unknownTokens);
    if (subDeser != null) {
      if (unknownTokens != null)
      {
        unknownTokens.writeEndObject();
        JsonParser p2 = unknownTokens.asParser();
        p2.nextToken();
        bean = subDeser.deserialize(p2, ctxt, bean);
      }
      
      if (p != null) {
        bean = subDeser.deserialize(p, ctxt, bean);
      }
      return bean;
    }
    
    if (unknownTokens != null) {
      bean = handleUnknownProperties(ctxt, bean, unknownTokens);
    }
    
    if (p != null) {
      bean = deserialize(p, ctxt, bean);
    }
    return bean;
  }
  




  protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens)
    throws IOException
  {
    JsonDeserializer<Object> subDeser;
    


    synchronized (this) {
      subDeser = _subDeserializers == null ? null : (JsonDeserializer)_subDeserializers.get(new com.fasterxml.jackson.databind.type.ClassKey(bean.getClass()));
    }
    if (subDeser != null) {
      return subDeser;
    }
    
    JavaType type = ctxt.constructType(bean.getClass());
    





    JsonDeserializer<Object> subDeser = ctxt.findRootValueDeserializer(type);
    
    if (subDeser != null) {
      synchronized (this) {
        if (_subDeserializers == null) {
          _subDeserializers = new java.util.HashMap();
        }
        _subDeserializers.put(new com.fasterxml.jackson.databind.type.ClassKey(bean.getClass()), subDeser);
      }
    }
    return subDeser;
  }
  



















  public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt)
    throws IOException
  {
    throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
  }
  




  private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt)
    throws IOException
  {
    while (((t instanceof java.lang.reflect.InvocationTargetException)) && (t.getCause() != null)) {
      t = t.getCause();
    }
    
    ClassUtil.throwIfError(t);
    boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
    
    if ((t instanceof IOException)) {
      if ((!wrap) || (!(t instanceof com.fasterxml.jackson.core.JsonProcessingException))) {
        throw ((IOException)t);
      }
    } else if (!wrap) {
      ClassUtil.throwIfRTE(t);
    }
    return t;
  }
  
  protected Object wrapInstantiationProblem(Throwable t, DeserializationContext ctxt)
    throws IOException
  {
    while (((t instanceof java.lang.reflect.InvocationTargetException)) && (t.getCause() != null)) {
      t = t.getCause();
    }
    
    ClassUtil.throwIfError(t);
    if ((t instanceof IOException))
    {
      throw ((IOException)t);
    }
    boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
    if (!wrap) {
      ClassUtil.throwIfRTE(t);
    }
    return ctxt.handleInstantiationProblem(_beanType.getRawClass(), null, t);
  }
}
