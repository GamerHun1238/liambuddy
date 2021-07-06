package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collection;




public class StdTypeResolverBuilder
  implements TypeResolverBuilder<StdTypeResolverBuilder>
{
  protected JsonTypeInfo.Id _idType;
  protected JsonTypeInfo.As _includeAs;
  protected String _typeProperty;
  protected boolean _typeIdVisible = false;
  




  protected Class<?> _defaultImpl;
  



  protected TypeIdResolver _customIdResolver;
  




  public StdTypeResolverBuilder() {}
  




  protected StdTypeResolverBuilder(JsonTypeInfo.Id idType, JsonTypeInfo.As idAs, String propName)
  {
    _idType = idType;
    _includeAs = idAs;
    _typeProperty = propName;
  }
  
  public static StdTypeResolverBuilder noTypeInfoBuilder() {
    return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, null);
  }
  


  public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes)
  {
    if (idType == null) {
      throw new IllegalArgumentException("idType cannot be null");
    }
    _idType = idType;
    _customIdResolver = idRes;
    
    _typeProperty = idType.getDefaultPropertyName();
    return this;
  }
  


  public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
  {
    if (_idType == JsonTypeInfo.Id.NONE) { return null;
    }
    
    if (baseType.isPrimitive()) {
      return null;
    }
    TypeIdResolver idRes = idResolver(config, baseType, subTypeValidator(config), subtypes, true, false);
    
    switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonTypeInfo$As[_includeAs.ordinal()]) {
    case 1: 
      return new AsArrayTypeSerializer(idRes, null);
    case 2: 
      return new AsPropertyTypeSerializer(idRes, null, _typeProperty);
    case 3: 
      return new AsWrapperTypeSerializer(idRes, null);
    case 4: 
      return new AsExternalTypeSerializer(idRes, null, _typeProperty);
    
    case 5: 
      return new AsExistingPropertyTypeSerializer(idRes, null, _typeProperty);
    }
    throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + _includeAs);
  }
  








  public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
  {
    if (_idType == JsonTypeInfo.Id.NONE) { return null;
    }
    
    if (baseType.isPrimitive()) {
      return null;
    }
    


    PolymorphicTypeValidator subTypeValidator = verifyBaseTypeValidity(config, baseType);
    
    TypeIdResolver idRes = idResolver(config, baseType, subTypeValidator, subtypes, false, true);
    
    JavaType defaultImpl = defineDefaultImpl(config, baseType);
    

    switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonTypeInfo$As[_includeAs.ordinal()]) {
    case 1: 
      return new AsArrayTypeDeserializer(baseType, idRes, _typeProperty, _typeIdVisible, defaultImpl);
    
    case 2: 
    case 5: 
      return new AsPropertyTypeDeserializer(baseType, idRes, _typeProperty, _typeIdVisible, defaultImpl, _includeAs);
    
    case 3: 
      return new AsWrapperTypeDeserializer(baseType, idRes, _typeProperty, _typeIdVisible, defaultImpl);
    
    case 4: 
      return new AsExternalTypeDeserializer(baseType, idRes, _typeProperty, _typeIdVisible, defaultImpl);
    }
    
    throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + _includeAs);
  }
  
  protected JavaType defineDefaultImpl(DeserializationConfig config, JavaType baseType) { JavaType defaultImpl;
    JavaType defaultImpl;
    if (_defaultImpl == null) {
      JavaType defaultImpl;
      if ((config.isEnabled(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)) && (!baseType.isAbstract())) {
        defaultImpl = baseType;
      } else {
        defaultImpl = null;
      }
    }
    else
    {
      JavaType defaultImpl;
      


      if ((_defaultImpl == Void.class) || (_defaultImpl == NoClass.class))
      {
        defaultImpl = config.getTypeFactory().constructType(_defaultImpl);
      } else { JavaType defaultImpl;
        if (baseType.hasRawClass(_defaultImpl)) {
          defaultImpl = baseType; } else { JavaType defaultImpl;
          if (baseType.isTypeOrSuperTypeOf(_defaultImpl))
          {

            defaultImpl = config.getTypeFactory().constructSpecializedType(baseType, _defaultImpl);




          }
          else
          {




            defaultImpl = null; }
        }
      }
    }
    return defaultImpl;
  }
  






  public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs)
  {
    if (includeAs == null) {
      throw new IllegalArgumentException("includeAs cannot be null");
    }
    _includeAs = includeAs;
    return this;
  }
  





  public StdTypeResolverBuilder typeProperty(String typeIdPropName)
  {
    if ((typeIdPropName == null) || (typeIdPropName.length() == 0)) {
      typeIdPropName = _idType.getDefaultPropertyName();
    }
    _typeProperty = typeIdPropName;
    return this;
  }
  
  public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl)
  {
    _defaultImpl = defaultImpl;
    return this;
  }
  
  public StdTypeResolverBuilder typeIdVisibility(boolean isVisible)
  {
    _typeIdVisible = isVisible;
    return this;
  }
  






  public Class<?> getDefaultImpl() { return _defaultImpl; }
  
  public String getTypeProperty() { return _typeProperty; }
  public boolean isTypeIdVisible() { return _typeIdVisible; }
  














  protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator subtypeValidator, Collection<NamedType> subtypes, boolean forSer, boolean forDeser)
  {
    if (_customIdResolver != null) return _customIdResolver;
    if (_idType == null) throw new IllegalStateException("Cannot build, 'init()' not yet called");
    switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonTypeInfo$Id[_idType.ordinal()]) {
    case 1: 
      return ClassNameIdResolver.construct(baseType, config, subtypeValidator);
    case 2: 
      return MinimalClassNameIdResolver.construct(baseType, config, subtypeValidator);
    case 3: 
      return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
    case 4: 
      return null;
    }
    
    throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + _idType);
  }
  














  public PolymorphicTypeValidator subTypeValidator(MapperConfig<?> config)
  {
    return config.getPolymorphicTypeValidator();
  }
  








  protected PolymorphicTypeValidator verifyBaseTypeValidity(MapperConfig<?> config, JavaType baseType)
  {
    PolymorphicTypeValidator ptv = subTypeValidator(config);
    if ((_idType == JsonTypeInfo.Id.CLASS) || (_idType == JsonTypeInfo.Id.MINIMAL_CLASS)) {
      PolymorphicTypeValidator.Validity validity = ptv.validateBaseType(config, baseType);
      
      if (validity == PolymorphicTypeValidator.Validity.DENIED) {
        return reportInvalidBaseType(config, baseType, ptv);
      }
      
      if (validity == PolymorphicTypeValidator.Validity.ALLOWED) {
        return LaissezFaireSubTypeValidator.instance;
      }
    }
    
    return ptv;
  }
  




  protected PolymorphicTypeValidator reportInvalidBaseType(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator ptv)
  {
    throw new IllegalArgumentException(String.format("Configured `PolymorphicTypeValidator` (of type %s) denied resolution of all subtypes of base type %s", new Object[] {
    
      ClassUtil.classNameOf(ptv), ClassUtil.classNameOf(baseType.getRawClass()) }));
  }
}
