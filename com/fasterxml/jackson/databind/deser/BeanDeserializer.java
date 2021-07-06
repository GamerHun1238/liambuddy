package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring;
import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


























public class BeanDeserializer
  extends BeanDeserializerBase
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected transient Exception _nullFromCreator;
  private volatile transient NameTransformer _currentlyTransforming;
  
  public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews)
  {
    super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
  }
  




  protected BeanDeserializer(BeanDeserializerBase src)
  {
    super(src, _ignoreAllUnknown);
  }
  
  protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
    super(src, ignoreAllUnknown);
  }
  
  protected BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
    super(src, unwrapper);
  }
  
  public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
    super(src, oir);
  }
  
  public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps) {
    super(src, ignorableProps);
  }
  
  public BeanDeserializer(BeanDeserializerBase src, BeanPropertyMap props) {
    super(src, props);
  }
  



  public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer transformer)
  {
    if (getClass() != BeanDeserializer.class) {
      return this;
    }
    

    if (_currentlyTransforming == transformer) {
      return this;
    }
    _currentlyTransforming = transformer;
    try {
      return new BeanDeserializer(this, transformer);
    } finally { _currentlyTransforming = null;
    }
  }
  
  public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {
    return new BeanDeserializer(this, oir);
  }
  
  public BeanDeserializer withIgnorableProperties(Set<String> ignorableProps)
  {
    return new BeanDeserializer(this, ignorableProps);
  }
  
  public BeanDeserializerBase withBeanProperties(BeanPropertyMap props)
  {
    return new BeanDeserializer(this, props);
  }
  
  protected BeanDeserializerBase asArrayDeserializer()
  {
    SettableBeanProperty[] props = _beanProperties.getPropertiesInInsertionOrder();
    return new BeanAsArrayDeserializer(this, props);
  }
  










  public Object deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.isExpectedStartObjectToken()) {
      if (_vanillaProcessing) {
        return vanillaDeserialize(p, ctxt, p.nextToken());
      }
      

      p.nextToken();
      if (_objectIdReader != null) {
        return deserializeWithObjectId(p, ctxt);
      }
      return deserializeFromObject(p, ctxt);
    }
    return _deserializeOther(p, ctxt, p.getCurrentToken());
  }
  

  protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t)
    throws IOException
  {
    if (t != null) {
      switch (1.$SwitchMap$com$fasterxml$jackson$core$JsonToken[t.ordinal()]) {
      case 1: 
        return deserializeFromString(p, ctxt);
      case 2: 
        return deserializeFromNumber(p, ctxt);
      case 3: 
        return deserializeFromDouble(p, ctxt);
      case 4: 
        return deserializeFromEmbedded(p, ctxt);
      case 5: 
      case 6: 
        return deserializeFromBoolean(p, ctxt);
      case 7: 
        return deserializeFromNull(p, ctxt);
      
      case 8: 
        return deserializeFromArray(p, ctxt);
      case 9: 
      case 10: 
        if (_vanillaProcessing) {
          return vanillaDeserialize(p, ctxt, t);
        }
        if (_objectIdReader != null) {
          return deserializeWithObjectId(p, ctxt);
        }
        return deserializeFromObject(p, ctxt);
      }
      
    }
    return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
  }
  
  @Deprecated
  protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
    throw ctxt.endOfInputException(handledType());
  }
  






  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean)
    throws IOException
  {
    p.setCurrentValue(bean);
    if (_injectables != null) {
      injectValues(ctxt, bean);
    }
    if (_unwrappedPropertyHandler != null) {
      return deserializeWithUnwrapped(p, ctxt, bean);
    }
    if (_externalTypeIdHandler != null) {
      return deserializeWithExternalTypeId(p, ctxt, bean);
    }
    


    if (p.isExpectedStartObjectToken()) {
      String propName = p.nextFieldName();
      if (propName == null)
        return bean;
    } else {
      String propName;
      if (p.hasTokenId(5)) {
        propName = p.getCurrentName();
      } else
        return bean;
    }
    String propName;
    if (_needViewProcesing) {
      Class<?> view = ctxt.getActiveView();
      if (view != null) {
        return deserializeWithView(p, ctxt, bean, view);
      }
    }
    do {
      p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      
      if (prop != null) {
        try {
          prop.deserializeAndSet(p, ctxt, bean);
        } catch (Exception e) {
          wrapAndThrow(e, bean, propName, ctxt);
        }
        
      } else
        handleUnknownVanilla(p, ctxt, bean, propName);
    } while ((propName = p.nextFieldName()) != null);
    return bean;
  }
  











  private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t)
    throws IOException
  {
    Object bean = _valueInstantiator.createUsingDefault(ctxt);
    
    p.setCurrentValue(bean);
    if (p.hasTokenId(5)) {
      String propName = p.getCurrentName();
      do {
        p.nextToken();
        SettableBeanProperty prop = _beanProperties.find(propName);
        
        if (prop != null) {
          try {
            prop.deserializeAndSet(p, ctxt, bean);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
          
        } else
          handleUnknownVanilla(p, ctxt, bean, propName);
      } while ((propName = p.nextFieldName()) != null);
    }
    return bean;
  }
  










  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if ((_objectIdReader != null) && (_objectIdReader.maySerializeAsObject()) && 
      (p.hasTokenId(5)) && 
      (_objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p))) {
      return deserializeFromObjectId(p, ctxt);
    }
    
    if (_nonStandardCreation) {
      if (_unwrappedPropertyHandler != null) {
        return deserializeWithUnwrapped(p, ctxt);
      }
      if (_externalTypeIdHandler != null) {
        return deserializeWithExternalTypeId(p, ctxt);
      }
      Object bean = deserializeFromObjectUsingNonDefault(p, ctxt);
      if (_injectables != null) {
        injectValues(ctxt, bean);
      }
      











      return bean;
    }
    Object bean = _valueInstantiator.createUsingDefault(ctxt);
    
    p.setCurrentValue(bean);
    if (p.canReadObjectId()) {
      Object id = p.getObjectId();
      if (id != null) {
        _handleTypedObjectId(p, ctxt, bean, id);
      }
    }
    if (_injectables != null) {
      injectValues(ctxt, bean);
    }
    if (_needViewProcesing) {
      Class<?> view = ctxt.getActiveView();
      if (view != null) {
        return deserializeWithView(p, ctxt, bean, view);
      }
    }
    if (p.hasTokenId(5)) {
      String propName = p.getCurrentName();
      do {
        p.nextToken();
        SettableBeanProperty prop = _beanProperties.find(propName);
        if (prop != null) {
          try {
            prop.deserializeAndSet(p, ctxt, bean);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
          
        } else
          handleUnknownVanilla(p, ctxt, bean, propName);
      } while ((propName = p.nextFieldName()) != null);
    }
    return bean;
  }
  










  protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    PropertyBasedCreator creator = _propertyBasedCreator;
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, _objectIdReader);
    TokenBuffer unknown = null;
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    
    JsonToken t = p.getCurrentToken();
    List<BeanReferring> referrings = null;
    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      p.nextToken();
      
      if (!buffer.readIdProperty(propName))
      {


        SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
        if (creatorProp != null)
        {

          if ((activeView != null) && (!creatorProp.visibleInView(activeView))) {
            p.skipChildren();
          }
          else {
            Object value = _deserializeWithErrorWrapping(p, ctxt, creatorProp);
            if (buffer.assignParameter(creatorProp, value)) {
              p.nextToken();
              Object bean;
              try {
                bean = creator.build(ctxt, buffer);
              } catch (Exception e) { Object bean;
                bean = wrapInstantiationProblem(e, ctxt);
              }
              if (bean == null) {
                return ctxt.handleInstantiationProblem(handledType(), null, 
                  _creatorReturnedNullException());
              }
              
              p.setCurrentValue(bean);
              

              if (bean.getClass() != _beanType.getRawClass()) {
                return handlePolymorphic(p, ctxt, bean, unknown);
              }
              if (unknown != null) {
                bean = handleUnknownProperties(ctxt, bean, unknown);
              }
              
              return deserialize(p, ctxt, bean);
            }
          }
        }
        else {
          SettableBeanProperty prop = _beanProperties.find(propName);
          if (prop != null) {
            try {
              buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));

            }
            catch (UnresolvedForwardReference reference)
            {
              BeanReferring referring = handleUnresolvedReference(ctxt, prop, buffer, reference);
              
              if (referrings == null) {
                referrings = new ArrayList();
              }
              referrings.add(referring);
            }
            

          }
          else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
            handleIgnoredProperty(p, ctxt, handledType(), propName);


          }
          else if (_anySetter != null) {
            try {
              buffer.bufferAnyProperty(_anySetter, propName, _anySetter.deserialize(p, ctxt));
            } catch (Exception e) {
              wrapAndThrow(e, _beanType.getRawClass(), propName, ctxt);
            }
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
    }
    Object bean;
    try { bean = creator.build(ctxt, buffer);
    } catch (Exception e) { Object bean;
      wrapInstantiationProblem(e, ctxt);
      bean = null;
    }
    if (referrings != null) {
      for (BeanReferring referring : referrings) {
        referring.setBean(bean);
      }
    }
    if (unknown != null)
    {
      if (bean.getClass() != _beanType.getRawClass()) {
        return handlePolymorphic(null, ctxt, bean, unknown);
      }
      
      return handleUnknownProperties(ctxt, bean, unknown);
    }
    return bean;
  }
  






  private BeanReferring handleUnresolvedReference(DeserializationContext ctxt, SettableBeanProperty prop, PropertyValueBuffer buffer, UnresolvedForwardReference reference)
    throws JsonMappingException
  {
    BeanReferring referring = new BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
    reference.getRoid().appendReferring(referring);
    return referring;
  }
  
  protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop)
    throws IOException
  {
    try
    {
      return prop.deserialize(p, ctxt);
    } catch (Exception e) {
      wrapAndThrow(e, _beanType.getRawClass(), prop.getName(), ctxt);
    }
    return null;
  }
  












  protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.requiresCustomCodec())
    {
      TokenBuffer tb = new TokenBuffer(p, ctxt);
      tb.writeEndObject();
      JsonParser p2 = tb.asParser(p);
      p2.nextToken();
      

      Object ob = _vanillaProcessing ? vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : deserializeFromObject(p2, ctxt);
      p2.close();
      return ob;
    }
    return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
  }
  







  protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView)
    throws IOException
  {
    if (p.hasTokenId(5)) {
      String propName = p.getCurrentName();
      do {
        p.nextToken();
        
        SettableBeanProperty prop = _beanProperties.find(propName);
        if (prop != null) {
          if (!prop.visibleInView(activeView)) {
            p.skipChildren();
          } else {
            try
            {
              prop.deserializeAndSet(p, ctxt, bean);
            } catch (Exception e) {
              wrapAndThrow(e, bean, propName, ctxt);
            }
          }
        } else
          handleUnknownVanilla(p, ctxt, bean, propName);
      } while ((propName = p.nextFieldName()) != null);
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
    

    p.setCurrentValue(bean);
    
    if (_injectables != null) {
      injectValues(ctxt, bean);
    }
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    for (String propName = p.hasTokenId(5) ? p.getCurrentName() : null; 
        
        propName != null; propName = p.nextFieldName()) {
      p.nextToken();
      SettableBeanProperty prop = _beanProperties.find(propName);
      if (prop != null) {
        if ((activeView != null) && (!prop.visibleInView(activeView))) {
          p.skipChildren();
        } else {
          try
          {
            prop.deserializeAndSet(p, ctxt, bean);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
          
        }
      }
      else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
        handleIgnoredProperty(p, ctxt, bean, propName);





      }
      else if (_anySetter == null)
      {
        tokens.writeFieldName(propName);
        tokens.copyCurrentStructure(p);
      }
      else
      {
        TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
        tokens.writeFieldName(propName);
        tokens.append(b2);
        try {
          _anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
        } catch (Exception e) {
          wrapAndThrow(e, bean, propName, ctxt);
        }
      } }
    tokens.writeEndObject();
    _unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
    return bean;
  }
  


  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean)
    throws IOException
  {
    JsonToken t = p.getCurrentToken();
    if (t == JsonToken.START_OBJECT) {
      t = p.nextToken();
    }
    TokenBuffer tokens = new TokenBuffer(p, ctxt);
    tokens.writeStartObject();
    Class<?> activeView = _needViewProcesing ? ctxt.getActiveView() : null;
    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      SettableBeanProperty prop = _beanProperties.find(propName);
      p.nextToken();
      if (prop != null) {
        if ((activeView != null) && (!prop.visibleInView(activeView))) {
          p.skipChildren();
        } else {
          try
          {
            prop.deserializeAndSet(p, ctxt, bean);
          } catch (Exception e) {
            wrapAndThrow(e, bean, propName, ctxt);
          }
        }
      }
      else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
        handleIgnoredProperty(p, ctxt, bean, propName);





      }
      else if (_anySetter == null)
      {
        tokens.writeFieldName(propName);
        tokens.copyCurrentStructure(p);
      }
      else {
        TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
        tokens.writeFieldName(propName);
        tokens.append(b2);
        try {
          _anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
        } catch (Exception e) {
          wrapAndThrow(e, bean, propName, ctxt);
        }
      }
    }
    
    tokens.writeEndObject();
    _unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
    return bean;
  }
  





  protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    PropertyBasedCreator creator = _propertyBasedCreator;
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, _objectIdReader);
    
    TokenBuffer tokens = new TokenBuffer(p, ctxt);
    tokens.writeStartObject();
    
    for (JsonToken t = p.getCurrentToken(); 
        t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      p.nextToken();
      
      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
      if (creatorProp != null)
      {
        if (buffer.assignParameter(creatorProp, 
          _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
          t = p.nextToken();
          Object bean;
          try {
            bean = creator.build(ctxt, buffer);
          } catch (Exception e) { Object bean;
            bean = wrapInstantiationProblem(e, ctxt);
          }
          
          p.setCurrentValue(bean);
          
          while (t == JsonToken.FIELD_NAME)
          {
            tokens.copyCurrentStructure(p);
            t = p.nextToken();
          }
          

          if (t != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(this, JsonToken.END_OBJECT, "Attempted to unwrap '%s' value", new Object[] {
            
              handledType().getName() });
          }
          tokens.writeEndObject();
          if (bean.getClass() != _beanType.getRawClass())
          {

            ctxt.reportInputMismatch(creatorProp, "Cannot create polymorphic instances with unwrapped values", new Object[0]);
            
            return null;
          }
          return _unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
        }
        

      }
      else if (!buffer.readIdProperty(propName))
      {


        SettableBeanProperty prop = _beanProperties.find(propName);
        if (prop != null) {
          buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));


        }
        else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
          handleIgnoredProperty(p, ctxt, handledType(), propName);





        }
        else if (_anySetter == null)
        {
          tokens.writeFieldName(propName);
          tokens.copyCurrentStructure(p);
        }
        else {
          TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
          tokens.writeFieldName(propName);
          tokens.append(b2);
          try {
            buffer.bufferAnyProperty(_anySetter, propName, _anySetter
              .deserialize(b2.asParserOnFirstToken(), ctxt));
          } catch (Exception e) {
            wrapAndThrow(e, _beanType.getRawClass(), propName, ctxt);
          }
        }
      }
    }
    

    try
    {
      bean = creator.build(ctxt, buffer);
    } catch (Exception e) { Object bean;
      wrapInstantiationProblem(e, ctxt);
      return null; }
    Object bean;
    return _unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
  }
  







  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_propertyBasedCreator != null) {
      return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
    }
    if (_delegateDeserializer != null)
    {




      return _valueInstantiator.createUsingDelegate(ctxt, _delegateDeserializer
        .deserialize(p, ctxt));
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
            prop.deserializeAndSet(p, ctxt, bean);
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
        else
          handleUnknownProperty(p, ctxt, bean, propName);
      }
    }
    return ext.complete(p, ctxt, bean);
  }
  

  protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    ExternalTypeHandler ext = _externalTypeIdHandler.start();
    PropertyBasedCreator creator = _propertyBasedCreator;
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, _objectIdReader);
    
    TokenBuffer tokens = new TokenBuffer(p, ctxt);
    tokens.writeStartObject();
    
    for (JsonToken t = p.getCurrentToken(); 
        t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String propName = p.getCurrentName();
      p.nextToken();
      
      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
      if (creatorProp != null)
      {


        if (!ext.handlePropertyValue(p, ctxt, propName, null))
        {


          if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
            t = p.nextToken();
            try
            {
              bean = creator.build(ctxt, buffer);
            } catch (Exception e) { Object bean;
              wrapAndThrow(e, _beanType.getRawClass(), propName, ctxt);
              continue;
            }
            Object bean;
            while (t == JsonToken.FIELD_NAME) {
              p.nextToken();
              tokens.copyCurrentStructure(p);
              t = p.nextToken();
            }
            if (bean.getClass() != _beanType.getRawClass())
            {

              return ctxt.reportBadDefinition(_beanType, String.format("Cannot create polymorphic instances with external type ids (%s -> %s)", new Object[] { _beanType, bean
              
                .getClass() }));
            }
            return ext.complete(p, ctxt, bean);
          }
          
        }
        
      }
      else if (!buffer.readIdProperty(propName))
      {


        SettableBeanProperty prop = _beanProperties.find(propName);
        if (prop != null) {
          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));


        }
        else if (!ext.handlePropertyValue(p, ctxt, propName, null))
        {


          if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
            handleIgnoredProperty(p, ctxt, handledType(), propName);


          }
          else if (_anySetter != null)
            buffer.bufferAnyProperty(_anySetter, propName, _anySetter
              .deserialize(p, ctxt)); }
      }
    }
    tokens.writeEndObject();
    
    try
    {
      return ext.complete(p, ctxt, buffer, creator);
    } catch (Exception e) {
      return wrapInstantiationProblem(e, ctxt);
    }
  }
  





  protected Exception _creatorReturnedNullException()
  {
    if (_nullFromCreator == null) {
      _nullFromCreator = new NullPointerException("JSON Creator returned null");
    }
    return _nullFromCreator;
  }
  

  static class BeanReferring
    extends ReadableObjectId.Referring
  {
    private final DeserializationContext _context;
    
    private final SettableBeanProperty _prop;
    
    private Object _bean;
    
    BeanReferring(DeserializationContext ctxt, UnresolvedForwardReference ref, JavaType valueType, PropertyValueBuffer buffer, SettableBeanProperty prop)
    {
      super(valueType);
      _context = ctxt;
      _prop = prop;
    }
    
    public void setBean(Object bean) {
      _bean = bean;
    }
    
    public void handleResolvedForwardReference(Object id, Object value)
      throws IOException
    {
      if (_bean == null) {
        _context.reportInputMismatch(_prop, "Cannot resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", new Object[] {_prop
        
          .getName(), _prop.getDeclaringClass().getName() });
      }
      _prop.set(_bean, value);
    }
  }
}
