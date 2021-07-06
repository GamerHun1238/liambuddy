package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.TypeVariable;











public final class MapType
  extends MapLikeType
{
  private static final long serialVersionUID = 1L;
  
  private MapType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic)
  {
    super(mapType, bindings, superClass, superInts, keyT, valueT, valueHandler, typeHandler, asStatic);
  }
  



  protected MapType(TypeBase base, JavaType keyT, JavaType valueT)
  {
    super(base, keyT, valueT);
  }
  




  public static MapType construct(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT)
  {
    return new MapType(rawType, bindings, superClass, superInts, keyT, valueT, null, null, false);
  }
  


  @Deprecated
  public static MapType construct(Class<?> rawType, JavaType keyT, JavaType valueT)
  {
    TypeVariable<?>[] vars = rawType.getTypeParameters();
    TypeBindings bindings;
    TypeBindings bindings; if ((vars == null) || (vars.length != 2)) {
      bindings = TypeBindings.emptyBindings();
    } else {
      bindings = TypeBindings.create(rawType, keyT, valueT);
    }
    
    return new MapType(rawType, bindings, _bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
  }
  

  @Deprecated
  protected JavaType _narrow(Class<?> subclass)
  {
    return new MapType(subclass, _bindings, _superClass, _superInterfaces, _keyType, _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  


  public MapType withTypeHandler(Object h)
  {
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType, _valueHandler, h, _asStatic);
  }
  


  public MapType withContentTypeHandler(Object h)
  {
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType
      .withTypeHandler(h), _valueHandler, _typeHandler, _asStatic);
  }
  

  public MapType withValueHandler(Object h)
  {
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType, h, _typeHandler, _asStatic);
  }
  

  public MapType withContentValueHandler(Object h)
  {
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType
      .withValueHandler(h), _valueHandler, _typeHandler, _asStatic);
  }
  

  public MapType withStaticTyping()
  {
    if (_asStatic) {
      return this;
    }
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType
      .withStaticTyping(), _valueType.withStaticTyping(), _valueHandler, _typeHandler, true);
  }
  

  public JavaType withContentType(JavaType contentType)
  {
    if (_valueType == contentType) {
      return this;
    }
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType, contentType, _valueHandler, _typeHandler, _asStatic);
  }
  

  public MapType withKeyType(JavaType keyType)
  {
    if (keyType == _keyType) {
      return this;
    }
    return new MapType(_class, _bindings, _superClass, _superInterfaces, keyType, _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  


  public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
  {
    return new MapType(rawType, bindings, superClass, superInterfaces, _keyType, _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  









  public MapType withKeyTypeHandler(Object h)
  {
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType
      .withTypeHandler(h), _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  

  public MapType withKeyValueHandler(Object h)
  {
    return new MapType(_class, _bindings, _superClass, _superInterfaces, _keyType
      .withValueHandler(h), _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  








  public String toString()
  {
    return "[map type; class " + _class.getName() + ", " + _keyType + " -> " + _valueType + "]";
  }
}
