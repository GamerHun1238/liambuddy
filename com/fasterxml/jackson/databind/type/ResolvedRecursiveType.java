package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;





public class ResolvedRecursiveType
  extends TypeBase
{
  private static final long serialVersionUID = 1L;
  protected JavaType _referencedType;
  
  public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings)
  {
    super(erasedType, bindings, null, null, 0, null, null, false);
  }
  

  public void setReference(JavaType ref)
  {
    if (_referencedType != null) {
      throw new IllegalStateException("Trying to re-set self reference; old value = " + _referencedType + ", new = " + ref);
    }
    _referencedType = ref;
  }
  
  public JavaType getSuperClass()
  {
    if (_referencedType != null) {
      return _referencedType.getSuperClass();
    }
    return super.getSuperClass();
  }
  
  public JavaType getSelfReferencedType() { return _referencedType; }
  

  public TypeBindings getBindings()
  {
    if (_referencedType != null) {
      return _referencedType.getBindings();
    }
    return super.getBindings();
  }
  



  public StringBuilder getGenericSignature(StringBuilder sb)
  {
    if (_referencedType != null)
    {
      return _referencedType.getErasedSignature(sb);
    }
    return sb.append("?");
  }
  
  public StringBuilder getErasedSignature(StringBuilder sb)
  {
    if (_referencedType != null) {
      return _referencedType.getErasedSignature(sb);
    }
    return sb;
  }
  
  public JavaType withContentType(JavaType contentType)
  {
    return this;
  }
  
  public JavaType withTypeHandler(Object h)
  {
    return this;
  }
  
  public JavaType withContentTypeHandler(Object h)
  {
    return this;
  }
  
  public JavaType withValueHandler(Object h)
  {
    return this;
  }
  
  public JavaType withContentValueHandler(Object h)
  {
    return this;
  }
  
  public JavaType withStaticTyping()
  {
    return this;
  }
  
  @Deprecated
  protected JavaType _narrow(Class<?> subclass)
  {
    return this;
  }
  

  public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
  {
    return null;
  }
  
  public boolean isContainerType()
  {
    return false;
  }
  

  public String toString()
  {
    StringBuilder sb = new StringBuilder(40).append("[recursive type; ");
    if (_referencedType == null) {
      sb.append("UNRESOLVED");
    }
    else
    {
      sb.append(_referencedType.getRawClass().getName());
    }
    return sb.toString();
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (o == null) return false;
    if (o.getClass() == getClass())
    {



      return false;
    }
    








    return false;
  }
}
