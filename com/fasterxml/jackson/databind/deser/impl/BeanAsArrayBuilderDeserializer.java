package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;


























public class BeanAsArrayBuilderDeserializer
  extends BeanDeserializerBase
{
  private static final long serialVersionUID = 1L;
  protected final BeanDeserializerBase _delegate;
  protected final SettableBeanProperty[] _orderedProperties;
  protected final AnnotatedMethod _buildMethod;
  protected final JavaType _targetType;
  
  public BeanAsArrayBuilderDeserializer(BeanDeserializerBase delegate, JavaType targetType, SettableBeanProperty[] ordered, AnnotatedMethod buildMethod)
  {
    super(delegate);
    _delegate = delegate;
    _targetType = targetType;
    _orderedProperties = ordered;
    _buildMethod = buildMethod;
  }
  





  public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper)
  {
    return _delegate.unwrappingDeserializer(unwrapper);
  }
  
  public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir)
  {
    return new BeanAsArrayBuilderDeserializer(_delegate.withObjectIdReader(oir), _targetType, _orderedProperties, _buildMethod);
  }
  

  public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps)
  {
    return new BeanAsArrayBuilderDeserializer(_delegate.withIgnorableProperties(ignorableProps), _targetType, _orderedProperties, _buildMethod);
  }
  

  public BeanDeserializerBase withBeanProperties(BeanPropertyMap props)
  {
    return new BeanAsArrayBuilderDeserializer(_delegate.withBeanProperties(props), _targetType, _orderedProperties, _buildMethod);
  }
  

  protected BeanDeserializerBase asArrayDeserializer()
  {
    return this;
  }
  







  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return Boolean.FALSE;
  }
  





  protected final Object finishBuild(DeserializationContext ctxt, Object builder)
    throws IOException
  {
    try
    {
      return _buildMethod.getMember().invoke(builder, (Object[])null);
    } catch (Exception e) {
      return wrapInstantiationProblem(e, ctxt);
    }
  }
  


  public Object deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      return finishBuild(ctxt, _deserializeFromNonArray(p, ctxt));
    }
    if (!_vanillaProcessing) {
      return finishBuild(ctxt, _deserializeNonVanilla(p, ctxt));
    }
    Object builder = _valueInstantiator.createUsingDefault(ctxt);
    SettableBeanProperty[] props = _orderedProperties;
    int i = 0;
    int propCount = props.length;
    for (;;) {
      if (p.nextToken() == JsonToken.END_ARRAY) {
        return finishBuild(ctxt, builder);
      }
      if (i == propCount) {
        break;
      }
      SettableBeanProperty prop = props[i];
      if (prop != null) {
        try {
          builder = prop.deserializeSetAndReturn(p, ctxt, builder);
        } catch (Exception e) {
          wrapAndThrow(e, builder, prop.getName(), ctxt);
        }
      } else {
        p.skipChildren();
      }
      i++;
    }
    

    if ((!_ignoreAllUnknown) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES))) {
      ctxt.reportInputMismatch(handledType(), "Unexpected JSON values; expected at most %d properties (in JSON Array)", new Object[] {
      
        Integer.valueOf(propCount) });
    }
    

    while (p.nextToken() != JsonToken.END_ARRAY) {
      p.skipChildren();
    }
    return finishBuild(ctxt, builder);
  }
  


  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value)
    throws IOException
  {
    return _delegate.deserialize(p, ctxt, value);
  }
  

  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    return _deserializeFromNonArray(p, ctxt);
  }
  













  protected Object _deserializeNonVanilla(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_nonStandardCreation) {
      return deserializeFromObjectUsingNonDefault(p, ctxt);
    }
    Object builder = _valueInstantiator.createUsingDefault(ctxt);
    if (_injectables != null) {
      injectValues(ctxt, builder);
    }
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    SettableBeanProperty[] props = _orderedProperties;
    int i = 0;
    int propCount = props.length;
    for (;;) {
      if (p.nextToken() == JsonToken.END_ARRAY) {
        return builder;
      }
      if (i == propCount) {
        break;
      }
      SettableBeanProperty prop = props[i];
      i++;
      if ((prop != null) && (
        (activeView == null) || (prop.visibleInView(activeView)))) {
        try {
          prop.deserializeSetAndReturn(p, ctxt, builder);
        } catch (Exception e) {
          wrapAndThrow(e, builder, prop.getName(), ctxt);
        }
        
      }
      else
      {
        p.skipChildren();
      }
    }
    if ((!_ignoreAllUnknown) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES))) {
      ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON value(s); expected at most %d properties (in JSON Array)", new Object[] {
      
        Integer.valueOf(propCount) });
    }
    

    while (p.nextToken() != JsonToken.END_ARRAY) {
      p.skipChildren();
    }
    return builder;
  }
  










  protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    PropertyBasedCreator creator = _propertyBasedCreator;
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, _objectIdReader);
    
    SettableBeanProperty[] props = _orderedProperties;
    int propCount = props.length;
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    int i = 0;
    Object builder = null;
    for (; 
        p.nextToken() != JsonToken.END_ARRAY; i++) {
      SettableBeanProperty prop = i < propCount ? props[i] : null;
      if (prop == null) {
        p.skipChildren();

      }
      else if ((activeView != null) && (!prop.visibleInView(activeView))) {
        p.skipChildren();


      }
      else if (builder != null) {
        try {
          builder = prop.deserializeSetAndReturn(p, ctxt, builder);
        } catch (Exception e) {
          wrapAndThrow(e, builder, prop.getName(), ctxt);
        }
      }
      else {
        String propName = prop.getName();
        
        SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
        if (creatorProp != null)
        {
          if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
            try {
              builder = creator.build(ctxt, buffer);
            } catch (Exception e) {
              wrapAndThrow(e, _beanType.getRawClass(), propName, ctxt);
              continue;
            }
            
            if (builder.getClass() != _beanType.getRawClass())
            {



              return ctxt.reportBadDefinition(_beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", new Object[] {_beanType
              
                .getRawClass().getName(), builder.getClass().getName() }));
            }
            
          }
          
        }
        else if (!buffer.readIdProperty(propName))
        {


          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
        }
      }
    }
    if (builder == null) {
      try {
        builder = creator.build(ctxt, buffer);
      } catch (Exception e) {
        return wrapInstantiationProblem(e, ctxt);
      }
    }
    return builder;
  }
  







  protected Object _deserializeFromNonArray(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    String message = "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array";
    
    return ctxt.handleUnexpectedToken(getValueType(ctxt), p.getCurrentToken(), p, message, new Object[] { _beanType.getRawClass().getName(), p.getCurrentToken() });
  }
}
