package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collections;



















public abstract class AnnotatedMember
  extends Annotated
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final transient TypeResolutionContext _typeContext;
  protected final transient AnnotationMap _annotations;
  
  protected AnnotatedMember(TypeResolutionContext ctxt, AnnotationMap annotations)
  {
    _typeContext = ctxt;
    _annotations = annotations;
  }
  




  protected AnnotatedMember(AnnotatedMember base)
  {
    _typeContext = _typeContext;
    _annotations = _annotations;
  }
  


  public abstract Annotated withAnnotations(AnnotationMap paramAnnotationMap);
  


  public abstract Class<?> getDeclaringClass();
  


  public abstract Member getMember();
  


  public String getFullName()
  {
    return getDeclaringClass().getName() + "#" + getName();
  }
  







  @Deprecated
  public TypeResolutionContext getTypeContext()
  {
    return _typeContext;
  }
  
  public final <A extends Annotation> A getAnnotation(Class<A> acls)
  {
    if (_annotations == null) {
      return null;
    }
    return _annotations.get(acls);
  }
  
  public final boolean hasAnnotation(Class<?> acls)
  {
    if (_annotations == null) {
      return false;
    }
    return _annotations.has(acls);
  }
  
  public boolean hasOneOf(Class<? extends Annotation>[] annoClasses)
  {
    if (_annotations == null) {
      return false;
    }
    return _annotations.hasOneOf(annoClasses);
  }
  
  @Deprecated
  public Iterable<Annotation> annotations()
  {
    if (_annotations == null) {
      return Collections.emptyList();
    }
    return _annotations.annotations();
  }
  



  public AnnotationMap getAllAnnotations()
  {
    return _annotations;
  }
  











  public final void fixAccess(boolean force)
  {
    Member m = getMember();
    if (m != null) {
      ClassUtil.checkAndFixAccess(m, force);
    }
  }
  
  public abstract void setValue(Object paramObject1, Object paramObject2)
    throws UnsupportedOperationException, IllegalArgumentException;
  
  public abstract Object getValue(Object paramObject)
    throws UnsupportedOperationException, IllegalArgumentException;
}
