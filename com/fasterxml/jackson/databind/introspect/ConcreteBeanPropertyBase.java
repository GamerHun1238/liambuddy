package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
















public abstract class ConcreteBeanPropertyBase
  implements BeanProperty, Serializable
{
  private static final long serialVersionUID = 1L;
  protected final PropertyMetadata _metadata;
  protected transient List<PropertyName> _aliases;
  
  protected ConcreteBeanPropertyBase(PropertyMetadata md)
  {
    _metadata = (md == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : md);
  }
  
  protected ConcreteBeanPropertyBase(ConcreteBeanPropertyBase src) {
    _metadata = _metadata;
  }
  
  public boolean isRequired() {
    return _metadata.isRequired();
  }
  
  public PropertyMetadata getMetadata() { return _metadata; }
  
  public boolean isVirtual() {
    return false;
  }
  
  @Deprecated
  public final JsonFormat.Value findFormatOverrides(AnnotationIntrospector intr) {
    JsonFormat.Value f = null;
    if (intr != null) {
      AnnotatedMember member = getMember();
      if (member != null) {
        f = intr.findFormat(member);
      }
    }
    if (f == null) {
      f = EMPTY_FORMAT;
    }
    return f;
  }
  

  public JsonFormat.Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType)
  {
    JsonFormat.Value v1 = config.getDefaultPropertyFormat(baseType);
    JsonFormat.Value v2 = null;
    AnnotationIntrospector intr = config.getAnnotationIntrospector();
    if (intr != null) {
      AnnotatedMember member = getMember();
      if (member != null) {
        v2 = intr.findFormat(member);
      }
    }
    if (v1 == null) {
      return v2 == null ? EMPTY_FORMAT : v2;
    }
    return v2 == null ? v1 : v1.withOverrides(v2);
  }
  

  public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType)
  {
    AnnotationIntrospector intr = config.getAnnotationIntrospector();
    AnnotatedMember member = getMember();
    if (member == null) {
      JsonInclude.Value def = config.getDefaultPropertyInclusion(baseType);
      return def;
    }
    JsonInclude.Value v0 = config.getDefaultInclusion(baseType, member.getRawType());
    if (intr == null) {
      return v0;
    }
    JsonInclude.Value v = intr.findPropertyInclusion(member);
    if (v0 == null) {
      return v;
    }
    return v0.withOverrides(v);
  }
  

  public List<PropertyName> findAliases(MapperConfig<?> config)
  {
    List<PropertyName> aliases = _aliases;
    if (aliases == null) {
      AnnotationIntrospector intr = config.getAnnotationIntrospector();
      if (intr != null) {
        AnnotatedMember member = getMember();
        if (member != null) {
          aliases = intr.findPropertyAliases(member);
        }
      }
      if (aliases == null) {
        aliases = Collections.emptyList();
      }
      _aliases = aliases;
    }
    return aliases;
  }
}
