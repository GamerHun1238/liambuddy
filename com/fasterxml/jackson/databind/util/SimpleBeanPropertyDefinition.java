package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;































public class SimpleBeanPropertyDefinition
  extends BeanPropertyDefinition
{
  protected final AnnotationIntrospector _annotationIntrospector;
  protected final AnnotatedMember _member;
  protected final PropertyMetadata _metadata;
  protected final PropertyName _fullName;
  protected final JsonInclude.Value _inclusion;
  
  protected SimpleBeanPropertyDefinition(AnnotationIntrospector intr, AnnotatedMember member, PropertyName fullName, PropertyMetadata metadata, JsonInclude.Value inclusion)
  {
    _annotationIntrospector = intr;
    _member = member;
    _fullName = fullName;
    _metadata = (metadata == null ? PropertyMetadata.STD_OPTIONAL : metadata);
    _inclusion = inclusion;
  }
  




  public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member)
  {
    return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, 
      PropertyName.construct(member.getName()), null, EMPTY_INCLUDE);
  }
  



  public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name)
  {
    return construct(config, member, name, null, EMPTY_INCLUDE);
  }
  









  public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, JsonInclude.Include inclusion)
  {
    JsonInclude.Value inclValue = (inclusion == null) || (inclusion == JsonInclude.Include.USE_DEFAULTS) ? EMPTY_INCLUDE : JsonInclude.Value.construct(inclusion, null);
    return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclValue);
  }
  





  public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, JsonInclude.Value inclusion)
  {
    return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclusion);
  }
  







  public BeanPropertyDefinition withSimpleName(String newName)
  {
    if ((_fullName.hasSimpleName(newName)) && (!_fullName.hasNamespace())) {
      return this;
    }
    return new SimpleBeanPropertyDefinition(_annotationIntrospector, _member, new PropertyName(newName), _metadata, _inclusion);
  }
  

  public BeanPropertyDefinition withName(PropertyName newName)
  {
    if (_fullName.equals(newName)) {
      return this;
    }
    return new SimpleBeanPropertyDefinition(_annotationIntrospector, _member, newName, _metadata, _inclusion);
  }
  



  public BeanPropertyDefinition withMetadata(PropertyMetadata metadata)
  {
    if (metadata.equals(_metadata)) {
      return this;
    }
    return new SimpleBeanPropertyDefinition(_annotationIntrospector, _member, _fullName, metadata, _inclusion);
  }
  



  public BeanPropertyDefinition withInclusion(JsonInclude.Value inclusion)
  {
    if (_inclusion == inclusion) {
      return this;
    }
    return new SimpleBeanPropertyDefinition(_annotationIntrospector, _member, _fullName, _metadata, inclusion);
  }
  






  public String getName()
  {
    return _fullName.getSimpleName();
  }
  
  public PropertyName getFullName() { return _fullName; }
  
  public boolean hasName(PropertyName name)
  {
    return _fullName.equals(name);
  }
  
  public String getInternalName() {
    return getName();
  }
  
  public PropertyName getWrapperName() {
    if ((_annotationIntrospector == null) || (_member == null)) {
      return null;
    }
    return _annotationIntrospector.findWrapperName(_member);
  }
  


  public boolean isExplicitlyIncluded() { return false; }
  public boolean isExplicitlyNamed() { return false; }
  




  public PropertyMetadata getMetadata()
  {
    return _metadata;
  }
  
  public JavaType getPrimaryType()
  {
    if (_member == null) {
      return TypeFactory.unknownType();
    }
    return _member.getType();
  }
  
  public Class<?> getRawPrimaryType()
  {
    if (_member == null) {
      return Object.class;
    }
    return _member.getRawType();
  }
  
  public JsonInclude.Value findInclusion()
  {
    return _inclusion;
  }
  





  public boolean hasGetter()
  {
    return getGetter() != null;
  }
  
  public boolean hasSetter() { return getSetter() != null; }
  
  public boolean hasField() {
    return _member instanceof AnnotatedField;
  }
  
  public boolean hasConstructorParameter() { return _member instanceof AnnotatedParameter; }
  
  public AnnotatedMethod getGetter()
  {
    if (((_member instanceof AnnotatedMethod)) && 
      (((AnnotatedMethod)_member).getParameterCount() == 0)) {
      return (AnnotatedMethod)_member;
    }
    return null;
  }
  
  public AnnotatedMethod getSetter()
  {
    if (((_member instanceof AnnotatedMethod)) && 
      (((AnnotatedMethod)_member).getParameterCount() == 1)) {
      return (AnnotatedMethod)_member;
    }
    return null;
  }
  
  public AnnotatedField getField()
  {
    return (_member instanceof AnnotatedField) ? (AnnotatedField)_member : null;
  }
  
  public AnnotatedParameter getConstructorParameter()
  {
    return (_member instanceof AnnotatedParameter) ? (AnnotatedParameter)_member : null;
  }
  
  public Iterator<AnnotatedParameter> getConstructorParameters()
  {
    AnnotatedParameter param = getConstructorParameter();
    if (param == null) {
      return ClassUtil.emptyIterator();
    }
    return Collections.singleton(param).iterator();
  }
  
  public AnnotatedMember getPrimaryMember() {
    return _member;
  }
}
