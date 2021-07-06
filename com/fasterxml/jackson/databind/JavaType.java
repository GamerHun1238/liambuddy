package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
























































public abstract class JavaType
  extends ResolvedType
  implements Serializable, Type
{
  private static final long serialVersionUID = 1L;
  protected final Class<?> _class;
  protected final int _hash;
  protected final Object _valueHandler;
  protected final Object _typeHandler;
  protected final boolean _asStatic;
  
  protected JavaType(Class<?> raw, int additionalHash, Object valueHandler, Object typeHandler, boolean asStatic)
  {
    _class = raw;
    _hash = (raw.getName().hashCode() + additionalHash);
    _valueHandler = valueHandler;
    _typeHandler = typeHandler;
    _asStatic = asStatic;
  }
  





  protected JavaType(JavaType base)
  {
    _class = _class;
    _hash = _hash;
    _valueHandler = _valueHandler;
    _typeHandler = _typeHandler;
    _asStatic = _asStatic;
  }
  







  public abstract JavaType withTypeHandler(Object paramObject);
  






  public abstract JavaType withContentTypeHandler(Object paramObject);
  






  public abstract JavaType withValueHandler(Object paramObject);
  






  public abstract JavaType withContentValueHandler(Object paramObject);
  






  public JavaType withHandlersFrom(JavaType src)
  {
    JavaType type = this;
    Object h = src.getTypeHandler();
    if (h != _typeHandler) {
      type = type.withTypeHandler(h);
    }
    h = src.getValueHandler();
    if (h != _valueHandler) {
      type = type.withValueHandler(h);
    }
    return type;
  }
  













  public abstract JavaType withContentType(JavaType paramJavaType);
  












  public abstract JavaType withStaticTyping();
  












  public abstract JavaType refine(Class<?> paramClass, TypeBindings paramTypeBindings, JavaType paramJavaType, JavaType[] paramArrayOfJavaType);
  












  @Deprecated
  public JavaType forcedNarrowBy(Class<?> subclass)
  {
    if (subclass == _class) {
      return this;
    }
    return _narrow(subclass);
  }
  



  @Deprecated
  protected abstract JavaType _narrow(Class<?> paramClass);
  


  public final Class<?> getRawClass()
  {
    return _class;
  }
  



  public final boolean hasRawClass(Class<?> clz)
  {
    return _class == clz;
  }
  





  public boolean hasContentType()
  {
    return true;
  }
  


  public final boolean isTypeOrSubTypeOf(Class<?> clz)
  {
    return (_class == clz) || (clz.isAssignableFrom(_class));
  }
  


  public final boolean isTypeOrSuperTypeOf(Class<?> clz)
  {
    return (_class == clz) || (_class.isAssignableFrom(clz));
  }
  
  public boolean isAbstract()
  {
    return Modifier.isAbstract(_class.getModifiers());
  }
  





  public boolean isConcrete()
  {
    int mod = _class.getModifiers();
    if ((mod & 0x600) == 0) {
      return true;
    }
    


    return _class.isPrimitive();
  }
  
  public boolean isThrowable() {
    return Throwable.class.isAssignableFrom(_class);
  }
  
  public boolean isArrayType() { return false; }
  




  public final boolean isEnumType()
  {
    return _class.isEnum();
  }
  
  public final boolean isInterface() {
    return _class.isInterface();
  }
  
  public final boolean isPrimitive() { return _class.isPrimitive(); }
  
  public final boolean isFinal() {
    return Modifier.isFinal(_class.getModifiers());
  }
  




  public abstract boolean isContainerType();
  




  public boolean isCollectionLikeType()
  {
    return false;
  }
  



  public boolean isMapLikeType()
  {
    return false;
  }
  






  public final boolean isJavaLangObject()
  {
    return _class == Object.class;
  }
  





  public final boolean useStaticType()
  {
    return _asStatic;
  }
  




  public boolean hasGenericTypes()
  {
    return containedTypeCount() > 0;
  }
  
  public JavaType getKeyType() { return null; }
  
  public JavaType getContentType() {
    return null;
  }
  
  public JavaType getReferencedType() { return null; }
  

  public abstract int containedTypeCount();
  

  public abstract JavaType containedType(int paramInt);
  

  @Deprecated
  public abstract String containedTypeName(int paramInt);
  
  @Deprecated
  public Class<?> getParameterSource()
  {
    return null;
  }
  





















  public JavaType containedTypeOrUnknown(int index)
  {
    JavaType t = containedType(index);
    return t == null ? TypeFactory.unknownType() : t;
  }
  







  public abstract TypeBindings getBindings();
  







  public abstract JavaType findSuperType(Class<?> paramClass);
  






  public abstract JavaType getSuperClass();
  






  public abstract List<JavaType> getInterfaces();
  






  public abstract JavaType[] findTypeParameters(Class<?> paramClass);
  






  public <T> T getValueHandler()
  {
    return _valueHandler;
  }
  

  public <T> T getTypeHandler()
  {
    return _typeHandler;
  }
  
  public Object getContentValueHandler()
  {
    return null;
  }
  
  public Object getContentTypeHandler()
  {
    return null;
  }
  
  public boolean hasValueHandler()
  {
    return _valueHandler != null;
  }
  






  public boolean hasHandlers()
  {
    return (_typeHandler != null) || (_valueHandler != null);
  }
  















  public String getGenericSignature()
  {
    StringBuilder sb = new StringBuilder(40);
    getGenericSignature(sb);
    return sb.toString();
  }
  






  public abstract StringBuilder getGenericSignature(StringBuilder paramStringBuilder);
  






  public String getErasedSignature()
  {
    StringBuilder sb = new StringBuilder(40);
    getErasedSignature(sb);
    return sb.toString();
  }
  





  public abstract StringBuilder getErasedSignature(StringBuilder paramStringBuilder);
  





  public abstract String toString();
  




  public abstract boolean equals(Object paramObject);
  




  public final int hashCode()
  {
    return _hash;
  }
}
