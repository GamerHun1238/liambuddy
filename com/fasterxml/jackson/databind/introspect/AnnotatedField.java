package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;



















public final class AnnotatedField
  extends AnnotatedMember
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final transient Field _field;
  protected Serialization _serialization;
  
  public AnnotatedField(TypeResolutionContext contextClass, Field field, AnnotationMap annMap)
  {
    super(contextClass, annMap);
    _field = field;
  }
  
  public AnnotatedField withAnnotations(AnnotationMap ann)
  {
    return new AnnotatedField(_typeContext, _field, ann);
  }
  



  protected AnnotatedField(Serialization ser)
  {
    super(null, null);
    _field = null;
    _serialization = ser;
  }
  





  public Field getAnnotated()
  {
    return _field;
  }
  
  public int getModifiers() { return _field.getModifiers(); }
  
  public String getName() {
    return _field.getName();
  }
  
  public Class<?> getRawType() {
    return _field.getType();
  }
  
  @Deprecated
  public Type getGenericType()
  {
    return _field.getGenericType();
  }
  
  public JavaType getType()
  {
    return _typeContext.resolveType(_field.getGenericType());
  }
  





  public Class<?> getDeclaringClass()
  {
    return _field.getDeclaringClass();
  }
  
  public Member getMember() { return _field; }
  
  public void setValue(Object pojo, Object value) throws IllegalArgumentException
  {
    try
    {
      _field.set(pojo, value);
    }
    catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Failed to setValue() for field " + getFullName() + ": " + e.getMessage(), e);
    }
  }
  
  public Object getValue(Object pojo) throws IllegalArgumentException
  {
    try
    {
      return _field.get(pojo);
    }
    catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Failed to getValue() for field " + getFullName() + ": " + e.getMessage(), e);
    }
  }
  




  public int getAnnotationCount()
  {
    return _annotations.size();
  }
  
  public boolean isTransient()
  {
    return Modifier.isTransient(getModifiers());
  }
  
  public int hashCode() {
    return _field.getName().hashCode();
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    return (ClassUtil.hasClass(o, getClass())) && (_field == _field);
  }
  

  public String toString()
  {
    return "[field " + getFullName() + "]";
  }
  





  Object writeReplace()
  {
    return new AnnotatedField(new Serialization(_field));
  }
  
  Object readResolve() {
    Class<?> clazz = _serialization.clazz;
    try {
      Field f = clazz.getDeclaredField(_serialization.name);
      
      if (!f.isAccessible()) {
        ClassUtil.checkAndFixAccess(f, false);
      }
      return new AnnotatedField(null, f, null);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Could not find method '" + _serialization.name + "' from Class '" + clazz.getName());
    }
  }
  

  private static final class Serialization
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    
    protected Class<?> clazz;
    
    protected String name;
    

    public Serialization(Field f)
    {
      clazz = f.getDeclaringClass();
      name = f.getName();
    }
  }
}
