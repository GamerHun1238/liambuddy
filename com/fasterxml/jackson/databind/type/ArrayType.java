package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Array;



















public final class ArrayType
  extends TypeBase
{
  private static final long serialVersionUID = 1L;
  protected final JavaType _componentType;
  protected final Object _emptyArray;
  
  protected ArrayType(JavaType componentType, TypeBindings bindings, Object emptyInstance, Object valueHandler, Object typeHandler, boolean asStatic)
  {
    super(emptyInstance.getClass(), bindings, null, null, componentType
      .hashCode(), valueHandler, typeHandler, asStatic);
    
    _componentType = componentType;
    _emptyArray = emptyInstance;
  }
  
  public static ArrayType construct(JavaType componentType, TypeBindings bindings) {
    return construct(componentType, bindings, null, null);
  }
  

  public static ArrayType construct(JavaType componentType, TypeBindings bindings, Object valueHandler, Object typeHandler)
  {
    Object emptyInstance = Array.newInstance(componentType.getRawClass(), 0);
    return new ArrayType(componentType, bindings, emptyInstance, valueHandler, typeHandler, false);
  }
  
  public JavaType withContentType(JavaType contentType)
  {
    Object emptyInstance = Array.newInstance(contentType.getRawClass(), 0);
    return new ArrayType(contentType, _bindings, emptyInstance, _valueHandler, _typeHandler, _asStatic);
  }
  


  public ArrayType withTypeHandler(Object h)
  {
    if (h == _typeHandler) {
      return this;
    }
    return new ArrayType(_componentType, _bindings, _emptyArray, _valueHandler, h, _asStatic);
  }
  

  public ArrayType withContentTypeHandler(Object h)
  {
    if (h == _componentType.getTypeHandler()) {
      return this;
    }
    return new ArrayType(_componentType.withTypeHandler(h), _bindings, _emptyArray, _valueHandler, _typeHandler, _asStatic);
  }
  

  public ArrayType withValueHandler(Object h)
  {
    if (h == _valueHandler) {
      return this;
    }
    return new ArrayType(_componentType, _bindings, _emptyArray, h, _typeHandler, _asStatic);
  }
  
  public ArrayType withContentValueHandler(Object h)
  {
    if (h == _componentType.getValueHandler()) {
      return this;
    }
    return new ArrayType(_componentType.withValueHandler(h), _bindings, _emptyArray, _valueHandler, _typeHandler, _asStatic);
  }
  

  public ArrayType withStaticTyping()
  {
    if (_asStatic) {
      return this;
    }
    return new ArrayType(_componentType.withStaticTyping(), _bindings, _emptyArray, _valueHandler, _typeHandler, true);
  }
  











  @Deprecated
  protected JavaType _narrow(Class<?> subclass)
  {
    return _reportUnsupported();
  }
  



  public JavaType refine(Class<?> contentClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
  {
    return null;
  }
  
  private JavaType _reportUnsupported() {
    throw new UnsupportedOperationException("Cannot narrow or widen array types");
  }
  





  public boolean isArrayType()
  {
    return true;
  }
  



  public boolean isAbstract()
  {
    return false;
  }
  



  public boolean isConcrete()
  {
    return true;
  }
  
  public boolean hasGenericTypes()
  {
    return _componentType.hasGenericTypes();
  }
  





  public boolean isContainerType()
  {
    return true;
  }
  
  public JavaType getContentType() { return _componentType; }
  
  public Object getContentValueHandler()
  {
    return _componentType.getValueHandler();
  }
  
  public Object getContentTypeHandler()
  {
    return _componentType.getTypeHandler();
  }
  
  public boolean hasHandlers()
  {
    return (super.hasHandlers()) || (_componentType.hasHandlers());
  }
  
  public StringBuilder getGenericSignature(StringBuilder sb)
  {
    sb.append('[');
    return _componentType.getGenericSignature(sb);
  }
  
  public StringBuilder getErasedSignature(StringBuilder sb)
  {
    sb.append('[');
    return _componentType.getErasedSignature(sb);
  }
  







  public String toString()
  {
    return "[array type, component type: " + _componentType + "]";
  }
  

  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if (o.getClass() != getClass()) { return false;
    }
    ArrayType other = (ArrayType)o;
    return _componentType.equals(_componentType);
  }
}
