package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;





















public class ReferenceType
  extends SimpleType
{
  private static final long serialVersionUID = 1L;
  protected final JavaType _referencedType;
  protected final JavaType _anchorType;
  
  protected ReferenceType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType, JavaType anchorType, Object valueHandler, Object typeHandler, boolean asStatic)
  {
    super(cls, bindings, superClass, superInts, refType.hashCode(), valueHandler, typeHandler, asStatic);
    
    _referencedType = refType;
    _anchorType = (anchorType == null ? this : anchorType);
  }
  







  protected ReferenceType(TypeBase base, JavaType refType)
  {
    super(base);
    _referencedType = refType;
    
    _anchorType = this;
  }
  








  public static ReferenceType upgradeFrom(JavaType baseType, JavaType refdType)
  {
    if (refdType == null) {
      throw new IllegalArgumentException("Missing referencedType");
    }
    

    if ((baseType instanceof TypeBase)) {
      return new ReferenceType((TypeBase)baseType, refdType);
    }
    throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
  }
  




  public static ReferenceType construct(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType)
  {
    return new ReferenceType(cls, bindings, superClass, superInts, refType, null, null, null, false);
  }
  
  @Deprecated
  public static ReferenceType construct(Class<?> cls, JavaType refType)
  {
    return new ReferenceType(cls, TypeBindings.emptyBindings(), null, null, null, refType, null, null, false);
  }
  


  public JavaType withContentType(JavaType contentType)
  {
    if (_referencedType == contentType) {
      return this;
    }
    return new ReferenceType(_class, _bindings, _superClass, _superInterfaces, contentType, _anchorType, _valueHandler, _typeHandler, _asStatic);
  }
  


  public ReferenceType withTypeHandler(Object h)
  {
    if (h == _typeHandler) {
      return this;
    }
    return new ReferenceType(_class, _bindings, _superClass, _superInterfaces, _referencedType, _anchorType, _valueHandler, h, _asStatic);
  }
  


  public ReferenceType withContentTypeHandler(Object h)
  {
    if (h == _referencedType.getTypeHandler()) {
      return this;
    }
    return new ReferenceType(_class, _bindings, _superClass, _superInterfaces, _referencedType
      .withTypeHandler(h), _anchorType, _valueHandler, _typeHandler, _asStatic);
  }
  

  public ReferenceType withValueHandler(Object h)
  {
    if (h == _valueHandler) {
      return this;
    }
    return new ReferenceType(_class, _bindings, _superClass, _superInterfaces, _referencedType, _anchorType, h, _typeHandler, _asStatic);
  }
  


  public ReferenceType withContentValueHandler(Object h)
  {
    if (h == _referencedType.getValueHandler()) {
      return this;
    }
    JavaType refdType = _referencedType.withValueHandler(h);
    return new ReferenceType(_class, _bindings, _superClass, _superInterfaces, refdType, _anchorType, _valueHandler, _typeHandler, _asStatic);
  }
  


  public ReferenceType withStaticTyping()
  {
    if (_asStatic) {
      return this;
    }
    return new ReferenceType(_class, _bindings, _superClass, _superInterfaces, _referencedType
      .withStaticTyping(), _anchorType, _valueHandler, _typeHandler, true);
  }
  


  public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
  {
    return new ReferenceType(rawType, _bindings, superClass, superInterfaces, _referencedType, _anchorType, _valueHandler, _typeHandler, _asStatic);
  }
  



  protected String buildCanonicalName()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(_class.getName());
    sb.append('<');
    sb.append(_referencedType.toCanonical());
    sb.append('>');
    return sb.toString();
  }
  








  @Deprecated
  protected JavaType _narrow(Class<?> subclass)
  {
    return new ReferenceType(subclass, _bindings, _superClass, _superInterfaces, _referencedType, _anchorType, _valueHandler, _typeHandler, _asStatic);
  }
  








  public JavaType getContentType()
  {
    return _referencedType;
  }
  
  public JavaType getReferencedType()
  {
    return _referencedType;
  }
  
  public boolean hasContentType()
  {
    return true;
  }
  
  public boolean isReferenceType()
  {
    return true;
  }
  
  public StringBuilder getErasedSignature(StringBuilder sb)
  {
    return _classSignature(_class, sb, true);
  }
  

  public StringBuilder getGenericSignature(StringBuilder sb)
  {
    _classSignature(_class, sb, false);
    sb.append('<');
    sb = _referencedType.getGenericSignature(sb);
    sb.append(">;");
    return sb;
  }
  





  public JavaType getAnchorType()
  {
    return _anchorType;
  }
  



  public boolean isAnchorType()
  {
    return _anchorType == this;
  }
  







  public String toString()
  {
    return 
    




      40 + "[reference type, class " + buildCanonicalName() + '<' + _referencedType + '>' + ']';
  }
  


  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if (o.getClass() != getClass()) { return false;
    }
    ReferenceType other = (ReferenceType)o;
    
    if (_class != _class) { return false;
    }
    
    return _referencedType.equals(_referencedType);
  }
}
