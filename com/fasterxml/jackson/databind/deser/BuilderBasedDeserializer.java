package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;




















public class BuilderBasedDeserializer
  extends BeanDeserializerBase
{
  private static final long serialVersionUID = 1L;
  protected final AnnotatedMethod _buildMethod;
  protected final JavaType _targetType;
  
  public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, JavaType targetType, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews)
  {
    super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
    
    _targetType = targetType;
    _buildMethod = builder.getBuildMethod();
    
    if (_objectIdReader != null)
    {
      throw new IllegalArgumentException("Cannot use Object Id with Builder-based deserialization (type " + beanDesc.getType() + ")");
    }
  }
  







  @Deprecated
  public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews)
  {
    this(builder, beanDesc, beanDesc
      .getType(), properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
  }
  





  protected BuilderBasedDeserializer(BuilderBasedDeserializer src)
  {
    this(src, _ignoreAllUnknown);
  }
  
  protected BuilderBasedDeserializer(BuilderBasedDeserializer src, boolean ignoreAllUnknown)
  {
    super(src, ignoreAllUnknown);
    _buildMethod = _buildMethod;
    _targetType = _targetType;
  }
  
  protected BuilderBasedDeserializer(BuilderBasedDeserializer src, NameTransformer unwrapper) {
    super(src, unwrapper);
    _buildMethod = _buildMethod;
    _targetType = _targetType;
  }
  
  public BuilderBasedDeserializer(BuilderBasedDeserializer src, ObjectIdReader oir) {
    super(src, oir);
    _buildMethod = _buildMethod;
    _targetType = _targetType;
  }
  
  public BuilderBasedDeserializer(BuilderBasedDeserializer src, Set<String> ignorableProps) {
    super(src, ignorableProps);
    _buildMethod = _buildMethod;
    _targetType = _targetType;
  }
  
  public BuilderBasedDeserializer(BuilderBasedDeserializer src, BeanPropertyMap props) {
    super(src, props);
    _buildMethod = _buildMethod;
    _targetType = _targetType;
  }
  





  public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper)
  {
    return new BuilderBasedDeserializer(this, unwrapper);
  }
  
  public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir)
  {
    return new BuilderBasedDeserializer(this, oir);
  }
  
  public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps)
  {
    return new BuilderBasedDeserializer(this, ignorableProps);
  }
  
  public BeanDeserializerBase withBeanProperties(BeanPropertyMap props)
  {
    return new BuilderBasedDeserializer(this, props);
  }
  
  protected BeanDeserializerBase asArrayDeserializer()
  {
    SettableBeanProperty[] props = _beanProperties.getPropertiesInInsertionOrder();
    return new BeanAsArrayBuilderDeserializer(this, _targetType, props, _buildMethod);
  }
  







  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return Boolean.FALSE;
  }
  







  protected Object finishBuild(DeserializationContext ctxt, Object builder)
    throws IOException
  {
    if (null == _buildMethod) {
      return builder;
    }
    try {
      return _buildMethod.getMember().invoke(builder, (Object[])null);
    } catch (Exception e) {
      return wrapInstantiationProblem(e, ctxt);
    }
  }
  





  public Object deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.isExpectedStartObjectToken()) {
      JsonToken t = p.nextToken();
      if (_vanillaProcessing) {
        return finishBuild(ctxt, vanillaDeserialize(p, ctxt, t));
      }
      Object builder = deserializeFromObject(p, ctxt);
      return finishBuild(ctxt, builder);
    }
    
    switch (p.getCurrentTokenId()) {
    case 6: 
      return finishBuild(ctxt, deserializeFromString(p, ctxt));
    case 7: 
      return finishBuild(ctxt, deserializeFromNumber(p, ctxt));
    case 8: 
      return finishBuild(ctxt, deserializeFromDouble(p, ctxt));
    case 12: 
      return p.getEmbeddedObject();
    case 9: 
    case 10: 
      return finishBuild(ctxt, deserializeFromBoolean(p, ctxt));
    
    case 3: 
      return finishBuild(ctxt, deserializeFromArray(p, ctxt));
    case 2: 
    case 5: 
      return finishBuild(ctxt, deserializeFromObject(p, ctxt));
    }
    
    return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
  }
  








  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value)
    throws IOException
  {
    JavaType valueType = _targetType;
    
    Class<?> builderRawType = handledType();
    Class<?> instRawType = value.getClass();
    if (builderRawType.isAssignableFrom(instRawType)) {
      return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing Builder (%s) instance not supported", new Object[] { valueType, builderRawType
      
        .getName() }));
    }
    return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing instance (of %s) not supported", new Object[] { valueType, instRawType
    
      .getName() }));
  }
  











  private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t)
    throws IOException
  {
    Object bean = _valueInstantiator.createUsingDefault(ctxt);
    for (; p.getCurrentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
      String propName = p.getCurrentName();
      
      p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      if (prop != null) {
        try {
          bean = prop.deserializeSetAndReturn(p, ctxt, bean);
        } catch (Exception e) {
          wrapAndThrow(e, bean, propName, ctxt);
        }
      } else {
        handleUnknownVanilla(p, ctxt, bean, propName);
      }
    }
    return bean;
  }
  





  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_nonStandardCreation) {
      if (_unwrappedPropertyHandler != null) {
        return deserializeWithUnwrapped(p, ctxt);
      }
      if (_externalTypeIdHandler != null) {
        return deserializeWithExternalTypeId(p, ctxt);
      }
      return deserializeFromObjectUsingNonDefault(p, ctxt);
    }
    Object bean = _valueInstantiator.createUsingDefault(ctxt);
    if (_injectables != null) {
      injectValues(ctxt, bean);
    }
    if (_needViewProcesing) {
      Class<?> view = ctxt.getActiveView();
      if (view != null) {
        return deserializeWithView(p, ctxt, bean, view);
      }
    }
    for (; p.getCurrentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
      String propName = p.getCurrentName();
      
      p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      if (prop != null) {
        try {
          bean = prop.deserializeSetAndReturn(p, ctxt, bean);
        } catch (Exception e) {
          wrapAndThrow(e, bean, propName, ctxt);
        }
        
      } else
        handleUnknownVanilla(p, ctxt, bean, propName);
    }
    return bean;
  }
  













  protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    PropertyBasedCreator creator = _propertyBasedCreator;
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, _objectIdReader);
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    

    TokenBuffer unknown = null;
    
    for (JsonToken t = p.getCurrentToken(); 
        t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      p.nextToken();
      
      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
      if (creatorProp != null) {
        if ((activeView != null) && (!creatorProp.visibleInView(activeView))) {
          p.skipChildren();


        }
        else if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
          p.nextToken();
          try
          {
            builder = creator.build(ctxt, buffer);
          } catch (Exception e) { Object builder;
            wrapAndThrow(e, _beanType.getRawClass(), propName, ctxt);
            continue;
          }
          Object builder;
          if (builder.getClass() != _beanType.getRawClass()) {
            return handlePolymorphic(p, ctxt, builder, unknown);
          }
          if (unknown != null) {
            builder = handleUnknownProperties(ctxt, builder, unknown);
          }
          
          return _deserialize(p, ctxt, builder);
        }
        

      }
      else if (!buffer.readIdProperty(propName))
      {


        SettableBeanProperty prop = _beanProperties.find(propName);
        if (prop != null) {
          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));



        }
        else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
          handleIgnoredProperty(p, ctxt, handledType(), propName);


        }
        else if (_anySetter != null) {
          buffer.bufferAnyProperty(_anySetter, propName, _anySetter.deserialize(p, ctxt));
        }
        else
        {
          if (unknown == null) {
            unknown = new TokenBuffer(p, ctxt);
          }
          unknown.writeFieldName(propName);
          unknown.copyCurrentStructure(p);
        }
      }
    }
    Object builder;
    try {
      builder = creator.build(ctxt, buffer);
    } catch (Exception e) { Object builder;
      builder = wrapInstantiationProblem(e, ctxt);
    }
    if (unknown != null)
    {
      if (builder.getClass() != _beanType.getRawClass()) {
        return handlePolymorphic(null, ctxt, builder, unknown);
      }
      
      return handleUnknownProperties(ctxt, builder, unknown);
    }
    return builder;
  }
  

  protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, Object builder)
    throws IOException
  {
    if (_injectables != null) {
      injectValues(ctxt, builder);
    }
    if (_unwrappedPropertyHandler != null) {
      if (p.hasToken(JsonToken.START_OBJECT)) {
        p.nextToken();
      }
      TokenBuffer tokens = new TokenBuffer(p, ctxt);
      tokens.writeStartObject();
      return deserializeWithUnwrapped(p, ctxt, builder, tokens);
    }
    if (_externalTypeIdHandler != null) {
      return deserializeWithExternalTypeId(p, ctxt, builder);
    }
    if (_needViewProcesing) {
      Class<?> view = ctxt.getActiveView();
      if (view != null) {
        return deserializeWithView(p, ctxt, builder, view);
      }
    }
    JsonToken t = p.getCurrentToken();
    
    if (t == JsonToken.START_OBJECT) {}
    for (t = p.nextToken(); 
        
        t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      
      p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      
      if (prop != null) {
        try {
          builder = prop.deserializeSetAndReturn(p, ctxt, builder);
        } catch (Exception e) {
          wrapAndThrow(e, builder, propName, ctxt);
        }
        
      } else
        handleUnknownVanilla(p, ctxt, builder, propName);
    }
    return builder;
  }
  







  protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView)
    throws IOException
  {
    for (JsonToken t = p.getCurrentToken(); 
        t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      
      p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      if (prop != null) {
        if (!prop.visibleInView(activeView)) {
          p.skipChildren();
        } else {
          try
          {
            bean = prop.deserializeSetAndReturn(p, ctxt, bean);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
        }
      } else
        handleUnknownVanilla(p, ctxt, bean, propName);
    }
    return bean;
  }
  











  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_delegateDeserializer != null) {
      return _valueInstantiator.createUsingDelegate(ctxt, _delegateDeserializer.deserialize(p, ctxt));
    }
    if (_propertyBasedCreator != null) {
      return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
    }
    TokenBuffer tokens = new TokenBuffer(p, ctxt);
    tokens.writeStartObject();
    Object bean = _valueInstantiator.createUsingDefault(ctxt);
    
    if (_injectables != null) {
      injectValues(ctxt, bean);
    }
    
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    for (; p.getCurrentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
      String propName = p.getCurrentName();
      p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      if (prop != null) {
        if ((activeView != null) && (!prop.visibleInView(activeView))) {
          p.skipChildren();
        } else {
          try
          {
            bean = prop.deserializeSetAndReturn(p, ctxt, bean);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
          
        }
      }
      else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
        handleIgnoredProperty(p, ctxt, bean, propName);
      }
      else
      {
        tokens.writeFieldName(propName);
        tokens.copyCurrentStructure(p);
        
        if (_anySetter != null) {
          try {
            _anySetter.deserializeAndSet(p, ctxt, bean, propName);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
        }
      }
    }
    tokens.writeEndObject();
    return _unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
  }
  


  protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    PropertyBasedCreator creator = _propertyBasedCreator;
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, _objectIdReader);
    
    TokenBuffer tokens = new TokenBuffer(p, ctxt);
    tokens.writeStartObject();
    Object builder = null;
    
    for (JsonToken t = p.getCurrentToken(); 
        t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      p.nextToken();
      
      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
      if (creatorProp != null)
      {
        if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
          t = p.nextToken();
          try {
            builder = creator.build(ctxt, buffer);
          } catch (Exception e) {
            wrapAndThrow(e, _beanType.getRawClass(), propName, ctxt);
            continue;
          }
          if (builder.getClass() != _beanType.getRawClass()) {
            return handlePolymorphic(p, ctxt, builder, tokens);
          }
          return deserializeWithUnwrapped(p, ctxt, builder, tokens);
        }
        

      }
      else if (!buffer.readIdProperty(propName))
      {


        SettableBeanProperty prop = _beanProperties.find(propName);
        if (prop != null) {
          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));

        }
        else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
          handleIgnoredProperty(p, ctxt, handledType(), propName);
        }
        else {
          tokens.writeFieldName(propName);
          tokens.copyCurrentStructure(p);
          
          if (_anySetter != null)
            buffer.bufferAnyProperty(_anySetter, propName, _anySetter.deserialize(p, ctxt));
        }
      } }
    tokens.writeEndObject();
    

    if (builder == null) {
      try {
        builder = creator.build(ctxt, buffer);
      } catch (Exception e) {
        return wrapInstantiationProblem(e, ctxt);
      }
    }
    return _unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
  }
  

  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object builder, TokenBuffer tokens)
    throws IOException
  {
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      SettableBeanProperty prop = _beanProperties.find(propName);
      p.nextToken();
      if (prop != null) {
        if ((activeView != null) && (!prop.visibleInView(activeView))) {
          p.skipChildren();
        } else {
          try
          {
            builder = prop.deserializeSetAndReturn(p, ctxt, builder);
          } catch (Exception e) {
            wrapAndThrow(e, builder, propName, ctxt);
          }
        }
      }
      else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
        handleIgnoredProperty(p, ctxt, builder, propName);
      }
      else
      {
        tokens.writeFieldName(propName);
        tokens.copyCurrentStructure(p);
        
        if (_anySetter != null)
          _anySetter.deserializeAndSet(p, ctxt, builder, propName);
      }
    }
    tokens.writeEndObject();
    return _unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
  }
  







  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_propertyBasedCreator != null) {
      return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
    }
    return deserializeWithExternalTypeId(p, ctxt, _valueInstantiator.createUsingDefault(ctxt));
  }
  

  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean)
    throws IOException
  {
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    ExternalTypeHandler ext = _externalTypeIdHandler.start();
    
    for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      t = p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      if (prop != null)
      {
        if (t.isScalarValue()) {
          ext.handleTypePropertyValue(p, ctxt, propName, bean);
        }
        if ((activeView != null) && (!prop.visibleInView(activeView))) {
          p.skipChildren();
        } else {
          try
          {
            bean = prop.deserializeSetAndReturn(p, ctxt, bean);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
          
        }
      }
      else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
        handleIgnoredProperty(p, ctxt, bean, propName);


      }
      else if (!ext.handlePropertyValue(p, ctxt, propName, bean))
      {


        if (_anySetter != null) {
          try {
            _anySetter.deserializeAndSet(p, ctxt, bean, propName);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
          
        }
        else {
          handleUnknownProperty(p, ctxt, bean, propName);
        }
      }
    }
    return ext.complete(p, ctxt, bean);
  }
  


  protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JavaType t = _targetType;
    return ctxt.reportBadDefinition(t, String.format("Deserialization (of %s) with Builder, External type id, @JsonCreator not yet implemented", new Object[] { t }));
  }
}
