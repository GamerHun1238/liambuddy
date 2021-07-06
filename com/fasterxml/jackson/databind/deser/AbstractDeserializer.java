package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;























public class AbstractDeserializer
  extends JsonDeserializer<Object>
  implements ContextualDeserializer, Serializable
{
  private static final long serialVersionUID = 1L;
  protected final JavaType _baseType;
  protected final ObjectIdReader _objectIdReader;
  protected final Map<String, SettableBeanProperty> _backRefProperties;
  protected transient Map<String, SettableBeanProperty> _properties;
  protected final boolean _acceptString;
  protected final boolean _acceptBoolean;
  protected final boolean _acceptInt;
  protected final boolean _acceptDouble;
  
  public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps, Map<String, SettableBeanProperty> props)
  {
    _baseType = beanDesc.getType();
    _objectIdReader = builder.getObjectIdReader();
    _backRefProperties = backRefProps;
    _properties = props;
    Class<?> cls = _baseType.getRawClass();
    _acceptString = cls.isAssignableFrom(String.class);
    _acceptBoolean = ((cls == Boolean.TYPE) || (cls.isAssignableFrom(Boolean.class)));
    _acceptInt = ((cls == Integer.TYPE) || (cls.isAssignableFrom(Integer.class)));
    _acceptDouble = ((cls == Double.TYPE) || (cls.isAssignableFrom(Double.class)));
  }
  
  @Deprecated
  public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps)
  {
    this(builder, beanDesc, backRefProps, null);
  }
  
  protected AbstractDeserializer(BeanDescription beanDesc)
  {
    _baseType = beanDesc.getType();
    _objectIdReader = null;
    _backRefProperties = null;
    Class<?> cls = _baseType.getRawClass();
    _acceptString = cls.isAssignableFrom(String.class);
    _acceptBoolean = ((cls == Boolean.TYPE) || (cls.isAssignableFrom(Boolean.class)));
    _acceptInt = ((cls == Integer.TYPE) || (cls.isAssignableFrom(Integer.class)));
    _acceptDouble = ((cls == Double.TYPE) || (cls.isAssignableFrom(Double.class)));
  }
  




  protected AbstractDeserializer(AbstractDeserializer base, ObjectIdReader objectIdReader, Map<String, SettableBeanProperty> props)
  {
    _baseType = _baseType;
    _backRefProperties = _backRefProperties;
    _acceptString = _acceptString;
    _acceptBoolean = _acceptBoolean;
    _acceptInt = _acceptInt;
    _acceptDouble = _acceptDouble;
    
    _objectIdReader = objectIdReader;
    _properties = props;
  }
  





  public static AbstractDeserializer constructForNonPOJO(BeanDescription beanDesc)
  {
    return new AbstractDeserializer(beanDesc);
  }
  

  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if ((property != null) && (intr != null)) {
      AnnotatedMember accessor = property.getMember();
      if (accessor != null) {
        ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
        if (objectIdInfo != null)
        {

          SettableBeanProperty idProp = null;
          ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
          

          objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
          Class<?> implClass = objectIdInfo.getGeneratorType();
          ObjectIdGenerator<?> idGen;
          JavaType idType; ObjectIdGenerator<?> idGen; if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            PropertyName propName = objectIdInfo.getPropertyName();
            idProp = _properties == null ? null : (SettableBeanProperty)_properties.get(propName.getSimpleName());
            if (idProp == null) {
              ctxt.reportBadDefinition(_baseType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", new Object[] {
              
                handledType().getName(), propName }));
            }
            JavaType idType = idProp.getType();
            idGen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());


          }
          else
          {


            resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
            JavaType type = ctxt.constructType(implClass);
            idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
            idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
          }
          JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
          ObjectIdReader oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
          
          return new AbstractDeserializer(this, oir, null);
        }
      }
    }
    if (_properties == null) {
      return this;
    }
    
    return new AbstractDeserializer(this, _objectIdReader, null);
  }
  






  public Class<?> handledType()
  {
    return _baseType.getRawClass();
  }
  
  public boolean isCachable() {
    return true;
  }
  




  public Boolean supportsUpdate(DeserializationConfig config)
  {
    return null;
  }
  





  public ObjectIdReader getObjectIdReader()
  {
    return _objectIdReader;
  }
  




  public SettableBeanProperty findBackReference(String logicalName)
  {
    return _backRefProperties == null ? null : (SettableBeanProperty)_backRefProperties.get(logicalName);
  }
  










  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    if (_objectIdReader != null) {
      JsonToken t = p.currentToken();
      if (t != null)
      {
        if (t.isScalarValue()) {
          return _deserializeFromObjectId(p, ctxt);
        }
        
        if (t == JsonToken.START_OBJECT) {
          t = p.nextToken();
        }
        if ((t == JsonToken.FIELD_NAME) && (_objectIdReader.maySerializeAsObject()) && 
          (_objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p))) {
          return _deserializeFromObjectId(p, ctxt);
        }
      }
    }
    
    Object result = _deserializeIfNatural(p, ctxt);
    if (result != null) {
      return result;
    }
    return typeDeserializer.deserializeTypedFromObject(p, ctxt);
  }
  




  public Object deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    ValueInstantiator bogus = new ValueInstantiator.Base(_baseType);
    return ctxt.handleMissingInstantiator(_baseType.getRawClass(), bogus, p, "abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information", new Object[0]);
  }
  












  protected Object _deserializeIfNatural(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    switch (p.currentTokenId()) {
    case 6: 
      if (_acceptString) {
        return p.getText();
      }
      break;
    case 7: 
      if (_acceptInt) {
        return Integer.valueOf(p.getIntValue());
      }
      break;
    case 8: 
      if (_acceptDouble) {
        return Double.valueOf(p.getDoubleValue());
      }
      break;
    case 9: 
      if (_acceptBoolean) {
        return Boolean.TRUE;
      }
      break;
    case 10: 
      if (_acceptBoolean) {
        return Boolean.FALSE;
      }
      break;
    }
    return null;
  }
  



  protected Object _deserializeFromObjectId(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    Object id = _objectIdReader.readObjectReference(p, ctxt);
    ReadableObjectId roid = ctxt.findObjectId(id, _objectIdReader.generator, _objectIdReader.resolver);
    
    Object pojo = roid.resolve();
    if (pojo == null)
    {
      throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] -- unresolved forward-reference?", p.getCurrentLocation(), roid);
    }
    return pojo;
  }
}
