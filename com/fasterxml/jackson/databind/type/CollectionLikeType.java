package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.TypeVariable;
import java.util.Collection;




















public class CollectionLikeType
  extends TypeBase
{
  private static final long serialVersionUID = 1L;
  protected final JavaType _elementType;
  
  protected CollectionLikeType(Class<?> collT, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT, Object valueHandler, Object typeHandler, boolean asStatic)
  {
    super(collT, bindings, superClass, superInts, elemT
      .hashCode(), valueHandler, typeHandler, asStatic);
    _elementType = elemT;
  }
  



  protected CollectionLikeType(TypeBase base, JavaType elemT)
  {
    super(base);
    _elementType = elemT;
  }
  



  public static CollectionLikeType construct(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT)
  {
    return new CollectionLikeType(rawType, bindings, superClass, superInts, elemT, null, null, false);
  }
  






  @Deprecated
  public static CollectionLikeType construct(Class<?> rawType, JavaType elemT)
  {
    TypeVariable<?>[] vars = rawType.getTypeParameters();
    TypeBindings bindings;
    TypeBindings bindings; if ((vars == null) || (vars.length != 1)) {
      bindings = TypeBindings.emptyBindings();
    } else {
      bindings = TypeBindings.create(rawType, elemT);
    }
    return new CollectionLikeType(rawType, bindings, 
      _bogusSuperClass(rawType), null, elemT, null, null, false);
  }
  








  public static CollectionLikeType upgradeFrom(JavaType baseType, JavaType elementType)
  {
    if ((baseType instanceof TypeBase)) {
      return new CollectionLikeType((TypeBase)baseType, elementType);
    }
    throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
  }
  
  @Deprecated
  protected JavaType _narrow(Class<?> subclass)
  {
    return new CollectionLikeType(subclass, _bindings, _superClass, _superInterfaces, _elementType, _valueHandler, _typeHandler, _asStatic);
  }
  


  public JavaType withContentType(JavaType contentType)
  {
    if (_elementType == contentType) {
      return this;
    }
    return new CollectionLikeType(_class, _bindings, _superClass, _superInterfaces, contentType, _valueHandler, _typeHandler, _asStatic);
  }
  

  public CollectionLikeType withTypeHandler(Object h)
  {
    return new CollectionLikeType(_class, _bindings, _superClass, _superInterfaces, _elementType, _valueHandler, h, _asStatic);
  }
  


  public CollectionLikeType withContentTypeHandler(Object h)
  {
    return new CollectionLikeType(_class, _bindings, _superClass, _superInterfaces, _elementType
      .withTypeHandler(h), _valueHandler, _typeHandler, _asStatic);
  }
  

  public CollectionLikeType withValueHandler(Object h)
  {
    return new CollectionLikeType(_class, _bindings, _superClass, _superInterfaces, _elementType, h, _typeHandler, _asStatic);
  }
  

  public CollectionLikeType withContentValueHandler(Object h)
  {
    return new CollectionLikeType(_class, _bindings, _superClass, _superInterfaces, _elementType
      .withValueHandler(h), _valueHandler, _typeHandler, _asStatic);
  }
  

  public JavaType withHandlersFrom(JavaType src)
  {
    JavaType type = super.withHandlersFrom(src);
    JavaType srcCt = src.getContentType();
    if (srcCt != null) {
      JavaType ct = _elementType.withHandlersFrom(srcCt);
      if (ct != _elementType) {
        type = type.withContentType(ct);
      }
    }
    return type;
  }
  
  public CollectionLikeType withStaticTyping()
  {
    if (_asStatic) {
      return this;
    }
    return new CollectionLikeType(_class, _bindings, _superClass, _superInterfaces, _elementType
      .withStaticTyping(), _valueHandler, _typeHandler, true);
  }
  


  public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
  {
    return new CollectionLikeType(rawType, bindings, superClass, superInterfaces, _elementType, _valueHandler, _typeHandler, _asStatic);
  }
  







  public boolean isContainerType()
  {
    return true;
  }
  
  public boolean isCollectionLikeType() { return true; }
  
  public JavaType getContentType() {
    return _elementType;
  }
  
  public Object getContentValueHandler() {
    return _elementType.getValueHandler();
  }
  
  public Object getContentTypeHandler()
  {
    return _elementType.getTypeHandler();
  }
  
  public boolean hasHandlers()
  {
    return (super.hasHandlers()) || (_elementType.hasHandlers());
  }
  
  public StringBuilder getErasedSignature(StringBuilder sb)
  {
    return _classSignature(_class, sb, true);
  }
  
  public StringBuilder getGenericSignature(StringBuilder sb)
  {
    _classSignature(_class, sb, false);
    sb.append('<');
    _elementType.getGenericSignature(sb);
    sb.append(">;");
    return sb;
  }
  
  protected String buildCanonicalName()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(_class.getName());
    if (_elementType != null) {
      sb.append('<');
      sb.append(_elementType.toCanonical());
      sb.append('>');
    }
    return sb.toString();
  }
  











  public boolean isTrueCollectionType()
  {
    return Collection.class.isAssignableFrom(_class);
  }
  







  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if (o.getClass() != getClass()) { return false;
    }
    CollectionLikeType other = (CollectionLikeType)o;
    return (_class == _class) && (_elementType.equals(_elementType));
  }
  

  public String toString()
  {
    return "[collection-like type; class " + _class.getName() + ", contains " + _elementType + "]";
  }
}
