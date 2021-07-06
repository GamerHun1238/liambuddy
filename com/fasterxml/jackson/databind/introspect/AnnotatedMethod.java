package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

















public final class AnnotatedMethod
  extends AnnotatedWithParams
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final transient Method _method;
  protected Class<?>[] _paramClasses;
  protected Serialization _serialization;
  
  public AnnotatedMethod(TypeResolutionContext ctxt, Method method, AnnotationMap classAnn, AnnotationMap[] paramAnnotations)
  {
    super(ctxt, classAnn, paramAnnotations);
    if (method == null) {
      throw new IllegalArgumentException("Cannot construct AnnotatedMethod with null Method");
    }
    _method = method;
  }
  




  protected AnnotatedMethod(Serialization ser)
  {
    super(null, null, null);
    _method = null;
    _serialization = ser;
  }
  
  public AnnotatedMethod withAnnotations(AnnotationMap ann)
  {
    return new AnnotatedMethod(_typeContext, _method, ann, _paramAnnotations);
  }
  
  public Method getAnnotated()
  {
    return _method;
  }
  
  public int getModifiers() { return _method.getModifiers(); }
  
  public String getName() {
    return _method.getName();
  }
  




  public JavaType getType()
  {
    return _typeContext.resolveType(_method.getGenericReturnType());
  }
  





  public Class<?> getRawType()
  {
    return _method.getReturnType();
  }
  
  @Deprecated
  public Type getGenericType()
  {
    return _method.getGenericReturnType();
  }
  





  public final Object call()
    throws Exception
  {
    return _method.invoke(null, new Object[0]);
  }
  
  public final Object call(Object[] args) throws Exception
  {
    return _method.invoke(null, args);
  }
  
  public final Object call1(Object arg) throws Exception
  {
    return _method.invoke(null, new Object[] { arg });
  }
  
  public final Object callOn(Object pojo) throws Exception {
    return _method.invoke(pojo, (Object[])null);
  }
  
  public final Object callOnWith(Object pojo, Object... args) throws Exception {
    return _method.invoke(pojo, args);
  }
  






  public int getParameterCount()
  {
    return getRawParameterTypes().length;
  }
  

  public Class<?> getRawParameterType(int index)
  {
    Class<?>[] types = getRawParameterTypes();
    return index >= types.length ? null : types[index];
  }
  
  public JavaType getParameterType(int index)
  {
    Type[] types = _method.getGenericParameterTypes();
    if (index >= types.length) {
      return null;
    }
    return _typeContext.resolveType(types[index]);
  }
  
  @Deprecated
  public Type getGenericParameterType(int index)
  {
    Type[] types = getGenericParameterTypes();
    if (index >= types.length) {
      return null;
    }
    return types[index];
  }
  
  public Class<?> getDeclaringClass() {
    return _method.getDeclaringClass();
  }
  
  public Method getMember() { return _method; }
  
  public void setValue(Object pojo, Object value) throws IllegalArgumentException
  {
    try
    {
      _method.invoke(pojo, new Object[] { value });
    }
    catch (IllegalAccessException|InvocationTargetException e) {
      throw new IllegalArgumentException("Failed to setValue() with method " + getFullName() + ": " + e.getMessage(), e);
    }
  }
  
  public Object getValue(Object pojo) throws IllegalArgumentException
  {
    try
    {
      return _method.invoke(pojo, (Object[])null);
    }
    catch (IllegalAccessException|InvocationTargetException e) {
      throw new IllegalArgumentException("Failed to getValue() with method " + getFullName() + ": " + e.getMessage(), e);
    }
  }
  






  public String getFullName()
  {
    return String.format("%s(%d params)", new Object[] { super.getFullName(), Integer.valueOf(getParameterCount()) });
  }
  
  public Class<?>[] getRawParameterTypes()
  {
    if (_paramClasses == null) {
      _paramClasses = _method.getParameterTypes();
    }
    return _paramClasses;
  }
  
  @Deprecated
  public Type[] getGenericParameterTypes() {
    return _method.getGenericParameterTypes();
  }
  
  public Class<?> getRawReturnType() {
    return _method.getReturnType();
  }
  






  public boolean hasReturnType()
  {
    Class<?> rt = getRawReturnType();
    return (rt != Void.TYPE) && (rt != Void.class);
  }
  






  public String toString()
  {
    return "[method " + getFullName() + "]";
  }
  
  public int hashCode()
  {
    return _method.getName().hashCode();
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    return (ClassUtil.hasClass(o, getClass())) && (_method == _method);
  }
  






  Object writeReplace()
  {
    return new AnnotatedMethod(new Serialization(_method));
  }
  
  Object readResolve() {
    Class<?> clazz = _serialization.clazz;
    try {
      Method m = clazz.getDeclaredMethod(_serialization.name, _serialization.args);
      

      if (!m.isAccessible()) {
        ClassUtil.checkAndFixAccess(m, false);
      }
      return new AnnotatedMethod(null, m, null, null);
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
    
    protected Class<?>[] args;
    
    public Serialization(Method setter)
    {
      clazz = setter.getDeclaringClass();
      name = setter.getName();
      args = setter.getParameterTypes();
    }
  }
}
