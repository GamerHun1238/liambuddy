package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;



























public final class AnnotatedParameter
  extends AnnotatedMember
{
  private static final long serialVersionUID = 1L;
  protected final AnnotatedWithParams _owner;
  protected final JavaType _type;
  protected final int _index;
  
  public AnnotatedParameter(AnnotatedWithParams owner, JavaType type, TypeResolutionContext typeContext, AnnotationMap annotations, int index)
  {
    super(typeContext, annotations);
    _owner = owner;
    _type = type;
    _index = index;
  }
  
  public AnnotatedParameter withAnnotations(AnnotationMap ann)
  {
    if (ann == _annotations) {
      return this;
    }
    return _owner.replaceParameterAnnotations(_index, ann);
  }
  









  public AnnotatedElement getAnnotated()
  {
    return null;
  }
  


  public int getModifiers()
  {
    return _owner.getModifiers();
  }
  


  public String getName()
  {
    return "";
  }
  
  public Class<?> getRawType() {
    return _type.getRawClass();
  }
  
  public JavaType getType()
  {
    return _type;
  }
  
  @Deprecated
  public Type getGenericType()
  {
    return _owner.getGenericParameterType(_index);
  }
  






  public Class<?> getDeclaringClass()
  {
    return _owner.getDeclaringClass();
  }
  



  public Member getMember()
  {
    return _owner.getMember();
  }
  

  public void setValue(Object pojo, Object value)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Cannot call setValue() on constructor parameter of " + getDeclaringClass().getName());
  }
  

  public Object getValue(Object pojo)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Cannot call getValue() on constructor parameter of " + getDeclaringClass().getName());
  }
  




  public Type getParameterType()
  {
    return _type;
  }
  



  public AnnotatedWithParams getOwner()
  {
    return _owner;
  }
  


  public int getIndex()
  {
    return _index;
  }
  





  public int hashCode()
  {
    return _owner.hashCode() + _index;
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (!ClassUtil.hasClass(o, getClass())) {
      return false;
    }
    AnnotatedParameter other = (AnnotatedParameter)o;
    return (_owner.equals(_owner)) && (_index == _index);
  }
  
  public String toString()
  {
    return "[parameter #" + getIndex() + ", annotations: " + _annotations + "]";
  }
}
