package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.TypeVariable;
import java.util.Map;
























public class MapLikeType
  extends TypeBase
{
  private static final long serialVersionUID = 1L;
  protected final JavaType _keyType;
  protected final JavaType _valueType;
  
  protected MapLikeType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic)
  {
    super(mapType, bindings, superClass, superInts, keyT.hashCode() ^ valueT
      .hashCode(), valueHandler, typeHandler, asStatic);
    _keyType = keyT;
    _valueType = valueT;
  }
  


  protected MapLikeType(TypeBase base, JavaType keyT, JavaType valueT)
  {
    super(base);
    _keyType = keyT;
    _valueType = valueT;
  }
  









  public static MapLikeType upgradeFrom(JavaType baseType, JavaType keyT, JavaType valueT)
  {
    if ((baseType instanceof TypeBase)) {
      return new MapLikeType((TypeBase)baseType, keyT, valueT);
    }
    
    throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
  }
  



  @Deprecated
  public static MapLikeType construct(Class<?> rawType, JavaType keyT, JavaType valueT)
  {
    TypeVariable<?>[] vars = rawType.getTypeParameters();
    TypeBindings bindings;
    TypeBindings bindings; if ((vars == null) || (vars.length != 2)) {
      bindings = TypeBindings.emptyBindings();
    } else {
      bindings = TypeBindings.create(rawType, keyT, valueT);
    }
    return new MapLikeType(rawType, bindings, _bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
  }
  


  @Deprecated
  protected JavaType _narrow(Class<?> subclass)
  {
    return new MapLikeType(subclass, _bindings, _superClass, _superInterfaces, _keyType, _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  




  public MapLikeType withKeyType(JavaType keyType)
  {
    if (keyType == _keyType) {
      return this;
    }
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, keyType, _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  


  public JavaType withContentType(JavaType contentType)
  {
    if (_valueType == contentType) {
      return this;
    }
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType, contentType, _valueHandler, _typeHandler, _asStatic);
  }
  


  public MapLikeType withTypeHandler(Object h)
  {
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType, _valueHandler, h, _asStatic);
  }
  


  public MapLikeType withContentTypeHandler(Object h)
  {
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType
      .withTypeHandler(h), _valueHandler, _typeHandler, _asStatic);
  }
  

  public MapLikeType withValueHandler(Object h)
  {
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType, h, _typeHandler, _asStatic);
  }
  


  public MapLikeType withContentValueHandler(Object h)
  {
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType
      .withValueHandler(h), _valueHandler, _typeHandler, _asStatic);
  }
  

  public JavaType withHandlersFrom(JavaType src)
  {
    JavaType type = super.withHandlersFrom(src);
    JavaType srcKeyType = src.getKeyType();
    
    if (((type instanceof MapLikeType)) && 
      (srcKeyType != null)) {
      JavaType ct = _keyType.withHandlersFrom(srcKeyType);
      if (ct != _keyType) {
        type = ((MapLikeType)type).withKeyType(ct);
      }
    }
    
    JavaType srcCt = src.getContentType();
    if (srcCt != null) {
      JavaType ct = _valueType.withHandlersFrom(srcCt);
      if (ct != _valueType) {
        type = type.withContentType(ct);
      }
    }
    return type;
  }
  
  public MapLikeType withStaticTyping()
  {
    if (_asStatic) {
      return this;
    }
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType, _valueType
      .withStaticTyping(), _valueHandler, _typeHandler, true);
  }
  


  public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
  {
    return new MapLikeType(rawType, bindings, superClass, superInterfaces, _keyType, _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  

  protected String buildCanonicalName()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(_class.getName());
    if (_keyType != null) {
      sb.append('<');
      sb.append(_keyType.toCanonical());
      sb.append(',');
      sb.append(_valueType.toCanonical());
      sb.append('>');
    }
    return sb.toString();
  }
  






  public boolean isContainerType()
  {
    return true;
  }
  
  public boolean isMapLikeType()
  {
    return true;
  }
  
  public JavaType getKeyType()
  {
    return _keyType;
  }
  
  public JavaType getContentType()
  {
    return _valueType;
  }
  
  public Object getContentValueHandler()
  {
    return _valueType.getValueHandler();
  }
  
  public Object getContentTypeHandler()
  {
    return _valueType.getTypeHandler();
  }
  
  public boolean hasHandlers()
  {
    return (super.hasHandlers()) || (_valueType.hasHandlers()) || 
      (_keyType.hasHandlers());
  }
  
  public StringBuilder getErasedSignature(StringBuilder sb)
  {
    return _classSignature(_class, sb, true);
  }
  
  public StringBuilder getGenericSignature(StringBuilder sb)
  {
    _classSignature(_class, sb, false);
    sb.append('<');
    _keyType.getGenericSignature(sb);
    _valueType.getGenericSignature(sb);
    sb.append(">;");
    return sb;
  }
  





  public MapLikeType withKeyTypeHandler(Object h)
  {
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType
      .withTypeHandler(h), _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  
  public MapLikeType withKeyValueHandler(Object h)
  {
    return new MapLikeType(_class, _bindings, _superClass, _superInterfaces, _keyType
      .withValueHandler(h), _valueType, _valueHandler, _typeHandler, _asStatic);
  }
  





  public boolean isTrueMapType()
  {
    return Map.class.isAssignableFrom(_class);
  }
  






  public String toString()
  {
    return String.format("[map-like type; class %s, %s -> %s]", new Object[] {_class
      .getName(), _keyType, _valueType });
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if (o.getClass() != getClass()) { return false;
    }
    MapLikeType other = (MapLikeType)o;
    return (_class == _class) && (_keyType.equals(_keyType)) && 
      (_valueType.equals(_valueType));
  }
}
