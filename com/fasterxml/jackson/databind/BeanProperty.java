package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.Named;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;





















public abstract interface BeanProperty
  extends Named
{
  public static final JsonFormat.Value EMPTY_FORMAT = new JsonFormat.Value();
  public static final JsonInclude.Value EMPTY_INCLUDE = JsonInclude.Value.empty();
  







  public abstract String getName();
  







  public abstract PropertyName getFullName();
  







  public abstract JavaType getType();
  







  public abstract PropertyName getWrapperName();
  







  public abstract PropertyMetadata getMetadata();
  







  public abstract boolean isRequired();
  







  public abstract boolean isVirtual();
  







  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
  







  public abstract <A extends Annotation> A getContextAnnotation(Class<A> paramClass);
  







  public abstract AnnotatedMember getMember();
  







  @Deprecated
  public abstract JsonFormat.Value findFormatOverrides(AnnotationIntrospector paramAnnotationIntrospector);
  







  public abstract JsonFormat.Value findPropertyFormat(MapperConfig<?> paramMapperConfig, Class<?> paramClass);
  







  public abstract JsonInclude.Value findPropertyInclusion(MapperConfig<?> paramMapperConfig, Class<?> paramClass);
  







  public abstract List<PropertyName> findAliases(MapperConfig<?> paramMapperConfig);
  







  public abstract void depositSchemaProperty(JsonObjectFormatVisitor paramJsonObjectFormatVisitor, SerializerProvider paramSerializerProvider)
    throws JsonMappingException;
  







  public static class Std
    implements BeanProperty, Serializable
  {
    private static final long serialVersionUID = 1L;
    






    protected final PropertyName _name;
    





    protected final JavaType _type;
    





    protected final PropertyName _wrapperName;
    





    protected final PropertyMetadata _metadata;
    





    protected final AnnotatedMember _member;
    






    public Std(PropertyName name, JavaType type, PropertyName wrapperName, AnnotatedMember member, PropertyMetadata metadata)
    {
      _name = name;
      _type = type;
      _wrapperName = wrapperName;
      _metadata = metadata;
      _member = member;
    }
    





    @Deprecated
    public Std(PropertyName name, JavaType type, PropertyName wrapperName, Annotations contextAnnotations, AnnotatedMember member, PropertyMetadata metadata)
    {
      this(name, type, wrapperName, member, metadata);
    }
    


    public Std(Std base, JavaType newType)
    {
      this(_name, newType, _wrapperName, _member, _metadata);
    }
    
    public Std withType(JavaType type) {
      return new Std(this, type);
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> acls)
    {
      return _member == null ? null : _member.getAnnotation(acls);
    }
    
    public <A extends Annotation> A getContextAnnotation(Class<A> acls)
    {
      return null;
    }
    
    @Deprecated
    public JsonFormat.Value findFormatOverrides(AnnotationIntrospector intr)
    {
      if ((_member != null) && (intr != null)) {
        JsonFormat.Value v = intr.findFormat(_member);
        if (v != null) {
          return v;
        }
      }
      return EMPTY_FORMAT;
    }
    
    public JsonFormat.Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType)
    {
      JsonFormat.Value v0 = config.getDefaultPropertyFormat(baseType);
      AnnotationIntrospector intr = config.getAnnotationIntrospector();
      if ((intr == null) || (_member == null)) {
        return v0;
      }
      JsonFormat.Value v = intr.findFormat(_member);
      if (v == null) {
        return v0;
      }
      return v0.withOverrides(v);
    }
    

    public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType)
    {
      JsonInclude.Value v0 = config.getDefaultInclusion(baseType, _type.getRawClass());
      AnnotationIntrospector intr = config.getAnnotationIntrospector();
      if ((intr == null) || (_member == null)) {
        return v0;
      }
      JsonInclude.Value v = intr.findPropertyInclusion(_member);
      if (v == null) {
        return v0;
      }
      return v0.withOverrides(v);
    }
    


    public List<PropertyName> findAliases(MapperConfig<?> config)
    {
      return Collections.emptyList();
    }
    
    public String getName() { return _name.getSimpleName(); }
    public PropertyName getFullName() { return _name; }
    public JavaType getType() { return _type; }
    public PropertyName getWrapperName() { return _wrapperName; }
    public boolean isRequired() { return _metadata.isRequired(); }
    public PropertyMetadata getMetadata() { return _metadata; }
    public AnnotatedMember getMember() { return _member; }
    
    public boolean isVirtual() {
      return false;
    }
    






    public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
    {
      throw new UnsupportedOperationException("Instances of " + getClass().getName() + " should not get visited");
    }
  }
  


  public static class Bogus
    implements BeanProperty
  {
    public Bogus() {}
    

    public String getName()
    {
      return "";
    }
    
    public PropertyName getFullName()
    {
      return PropertyName.NO_NAME;
    }
    
    public JavaType getType()
    {
      return TypeFactory.unknownType();
    }
    
    public PropertyName getWrapperName()
    {
      return null;
    }
    
    public PropertyMetadata getMetadata()
    {
      return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
    }
    
    public boolean isRequired()
    {
      return false;
    }
    
    public boolean isVirtual()
    {
      return false;
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> acls)
    {
      return null;
    }
    
    public <A extends Annotation> A getContextAnnotation(Class<A> acls)
    {
      return null;
    }
    
    public AnnotatedMember getMember()
    {
      return null;
    }
    
    @Deprecated
    public JsonFormat.Value findFormatOverrides(AnnotationIntrospector intr)
    {
      return JsonFormat.Value.empty();
    }
    
    public JsonFormat.Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType)
    {
      return JsonFormat.Value.empty();
    }
    


    public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType)
    {
      return null;
    }
    
    public List<PropertyName> findAliases(MapperConfig<?> config)
    {
      return Collections.emptyList();
    }
    
    public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
      throws JsonMappingException
    {}
  }
}
