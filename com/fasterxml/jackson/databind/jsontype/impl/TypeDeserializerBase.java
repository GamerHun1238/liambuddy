package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;








































public abstract class TypeDeserializerBase
  extends TypeDeserializer
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final TypeIdResolver _idResolver;
  protected final JavaType _baseType;
  protected final BeanProperty _property;
  protected final JavaType _defaultImpl;
  protected final String _typePropertyName;
  protected final boolean _typeIdVisible;
  protected final Map<String, JsonDeserializer<Object>> _deserializers;
  protected JsonDeserializer<Object> _defaultImplDeserializer;
  
  protected TypeDeserializerBase(JavaType baseType, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl)
  {
    _baseType = baseType;
    _idResolver = idRes;
    _typePropertyName = ClassUtil.nonNullString(typePropertyName);
    _typeIdVisible = typeIdVisible;
    
    _deserializers = new ConcurrentHashMap(16, 0.75F, 2);
    _defaultImpl = defaultImpl;
    _property = null;
  }
  
  protected TypeDeserializerBase(TypeDeserializerBase src, BeanProperty property)
  {
    _baseType = _baseType;
    _idResolver = _idResolver;
    _typePropertyName = _typePropertyName;
    _typeIdVisible = _typeIdVisible;
    _deserializers = _deserializers;
    _defaultImpl = _defaultImpl;
    _defaultImplDeserializer = _defaultImplDeserializer;
    _property = property;
  }
  


  public abstract TypeDeserializer forProperty(BeanProperty paramBeanProperty);
  


  public abstract JsonTypeInfo.As getTypeInclusion();
  


  public String baseTypeName()
  {
    return _baseType.getRawClass().getName();
  }
  
  public final String getPropertyName() { return _typePropertyName; }
  
  public TypeIdResolver getTypeIdResolver() {
    return _idResolver;
  }
  
  public Class<?> getDefaultImpl() {
    return ClassUtil.rawClass(_defaultImpl);
  }
  


  public JavaType baseType()
  {
    return _baseType;
  }
  

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append('[').append(getClass().getName());
    sb.append("; base-type:").append(_baseType);
    sb.append("; id-resolver: ").append(_idResolver);
    sb.append(']');
    return sb.toString();
  }
  






  protected final JsonDeserializer<Object> _findDeserializer(DeserializationContext ctxt, String typeId)
    throws IOException
  {
    JsonDeserializer<Object> deser = (JsonDeserializer)_deserializers.get(typeId);
    if (deser == null)
    {




      JavaType type = _idResolver.typeFromId(ctxt, typeId);
      if (type == null)
      {
        deser = _findDefaultImplDeserializer(ctxt);
        if (deser == null)
        {
          JavaType actual = _handleUnknownTypeId(ctxt, typeId);
          if (actual == null)
          {
            return NullifyingDeserializer.instance;
          }
          
          deser = ctxt.findContextualValueDeserializer(actual, _property);

        }
        


      }
      else
      {


        if ((_baseType != null) && 
          (_baseType.getClass() == type.getClass()))
        {








          if (!type.hasGenericTypes()) {
            type = ctxt.getTypeFactory().constructSpecializedType(_baseType, type.getRawClass());
          }
        }
        deser = ctxt.findContextualValueDeserializer(type, _property);
      }
      _deserializers.put(typeId, deser);
    }
    return deser;
  }
  



  protected final JsonDeserializer<Object> _findDefaultImplDeserializer(DeserializationContext ctxt)
    throws IOException
  {
    if (_defaultImpl == null) {
      if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
        return NullifyingDeserializer.instance;
      }
      return null;
    }
    Class<?> raw = _defaultImpl.getRawClass();
    if (ClassUtil.isBogusClass(raw)) {
      return NullifyingDeserializer.instance;
    }
    
    synchronized (_defaultImpl) {
      if (_defaultImplDeserializer == null) {
        _defaultImplDeserializer = ctxt.findContextualValueDeserializer(_defaultImpl, _property);
      }
      
      return _defaultImplDeserializer;
    }
  }
  





  @Deprecated
  protected Object _deserializeWithNativeTypeId(JsonParser jp, DeserializationContext ctxt)
    throws IOException
  {
    return _deserializeWithNativeTypeId(jp, ctxt, jp.getTypeId());
  }
  



  protected Object _deserializeWithNativeTypeId(JsonParser p, DeserializationContext ctxt, Object typeId)
    throws IOException
  {
    JsonDeserializer<Object> deser;
    


    if (typeId == null)
    {

      JsonDeserializer<Object> deser = _findDefaultImplDeserializer(ctxt);
      if (deser == null) {
        return ctxt.reportInputMismatch(baseType(), "No (native) type id found when one was expected for polymorphic type handling", new Object[0]);
      }
    }
    else {
      String typeIdStr = (typeId instanceof String) ? (String)typeId : String.valueOf(typeId);
      deser = _findDeserializer(ctxt, typeIdStr);
    }
    return deser.deserialize(p, ctxt);
  }
  













  protected JavaType _handleUnknownTypeId(DeserializationContext ctxt, String typeId)
    throws IOException
  {
    String extraDesc = _idResolver.getDescForKnownTypeIds();
    if (extraDesc == null) {
      extraDesc = "type ids are not statically known";
    } else {
      extraDesc = "known type ids = " + extraDesc;
    }
    if (_property != null) {
      extraDesc = String.format("%s (for POJO property '%s')", new Object[] { extraDesc, _property
        .getName() });
    }
    return ctxt.handleUnknownTypeId(_baseType, typeId, _idResolver, extraDesc);
  }
  



  protected JavaType _handleMissingTypeId(DeserializationContext ctxt, String extraDesc)
    throws IOException
  {
    return ctxt.handleMissingTypeId(_baseType, _idResolver, extraDesc);
  }
}
