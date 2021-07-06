package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class TypeBase extends JavaType implements JsonSerializable
{
  private static final long serialVersionUID = 1L;
  private static final TypeBindings NO_BINDINGS = ;
  private static final JavaType[] NO_TYPES = new JavaType[0];
  



  protected final JavaType _superClass;
  



  protected final JavaType[] _superInterfaces;
  



  protected final TypeBindings _bindings;
  



  volatile transient String _canonicalName;
  



  protected TypeBase(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int hash, Object valueHandler, Object typeHandler, boolean asStatic)
  {
    super(raw, hash, valueHandler, typeHandler, asStatic);
    _bindings = (bindings == null ? NO_BINDINGS : bindings);
    _superClass = superClass;
    _superInterfaces = superInts;
  }
  




  protected TypeBase(TypeBase base)
  {
    super(base);
    _superClass = _superClass;
    _superInterfaces = _superInterfaces;
    _bindings = _bindings;
  }
  

  public String toCanonical()
  {
    String str = _canonicalName;
    if (str == null) {
      str = buildCanonicalName();
    }
    return str;
  }
  
  protected String buildCanonicalName() {
    return _class.getName();
  }
  

  public abstract StringBuilder getGenericSignature(StringBuilder paramStringBuilder);
  

  public abstract StringBuilder getErasedSignature(StringBuilder paramStringBuilder);
  
  public TypeBindings getBindings()
  {
    return _bindings;
  }
  
  public int containedTypeCount()
  {
    return _bindings.size();
  }
  
  public JavaType containedType(int index)
  {
    return _bindings.getBoundType(index);
  }
  
  @Deprecated
  public String containedTypeName(int index)
  {
    return _bindings.getBoundName(index);
  }
  
  public JavaType getSuperClass()
  {
    return _superClass;
  }
  
  public List<JavaType> getInterfaces()
  {
    if (_superInterfaces == null) {
      return Collections.emptyList();
    }
    switch (_superInterfaces.length) {
    case 0: 
      return Collections.emptyList();
    case 1: 
      return Collections.singletonList(_superInterfaces[0]);
    }
    return Arrays.asList(_superInterfaces);
  }
  

  public final JavaType findSuperType(Class<?> rawTarget)
  {
    if (rawTarget == _class) {
      return this;
    }
    
    if ((rawTarget.isInterface()) && (_superInterfaces != null)) {
      int i = 0; for (int count = _superInterfaces.length; i < count; i++) {
        JavaType type = _superInterfaces[i].findSuperType(rawTarget);
        if (type != null) {
          return type;
        }
      }
    }
    
    if (_superClass != null) {
      JavaType type = _superClass.findSuperType(rawTarget);
      if (type != null) {
        return type;
      }
    }
    return null;
  }
  

  public JavaType[] findTypeParameters(Class<?> expType)
  {
    JavaType match = findSuperType(expType);
    if (match == null) {
      return NO_TYPES;
    }
    return match.getBindings().typeParameterArray();
  }
  








  public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    WritableTypeId typeIdDef = new WritableTypeId(this, JsonToken.VALUE_STRING);
    typeSer.writeTypePrefix(g, typeIdDef);
    serialize(g, provider);
    typeSer.writeTypeSuffix(g, typeIdDef);
  }
  

  public void serialize(JsonGenerator gen, SerializerProvider provider)
    throws IOException, JsonProcessingException
  {
    gen.writeString(toCanonical());
  }
  











  protected static StringBuilder _classSignature(Class<?> cls, StringBuilder sb, boolean trailingSemicolon)
  {
    if (cls.isPrimitive()) {
      if (cls == Boolean.TYPE) {
        sb.append('Z');
      } else if (cls == Byte.TYPE) {
        sb.append('B');
      }
      else if (cls == Short.TYPE) {
        sb.append('S');
      }
      else if (cls == Character.TYPE) {
        sb.append('C');
      }
      else if (cls == Integer.TYPE) {
        sb.append('I');
      }
      else if (cls == Long.TYPE) {
        sb.append('J');
      }
      else if (cls == Float.TYPE) {
        sb.append('F');
      }
      else if (cls == Double.TYPE) {
        sb.append('D');
      }
      else if (cls == Void.TYPE) {
        sb.append('V');
      } else {
        throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
      }
    } else {
      sb.append('L');
      String name = cls.getName();
      int i = 0; for (int len = name.length(); i < len; i++) {
        char c = name.charAt(i);
        if (c == '.') c = '/';
        sb.append(c);
      }
      if (trailingSemicolon) {
        sb.append(';');
      }
    }
    return sb;
  }
  








  protected static JavaType _bogusSuperClass(Class<?> cls)
  {
    Class<?> parent = cls.getSuperclass();
    if (parent == null) {
      return null;
    }
    return TypeFactory.unknownType();
  }
}
