package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.TypeVariable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypeBindings implements java.io.Serializable
{
  private static final long serialVersionUID = 1L;
  private static final String[] NO_STRINGS = new String[0];
  
  private static final JavaType[] NO_TYPES = new JavaType[0];
  
  private static final TypeBindings EMPTY = new TypeBindings(NO_STRINGS, NO_TYPES, null);
  




  private final String[] _names;
  




  private final JavaType[] _types;
  




  private final String[] _unboundVariables;
  




  private final int _hashCode;
  





  private TypeBindings(String[] names, JavaType[] types, String[] uvars)
  {
    _names = (names == null ? NO_STRINGS : names);
    _types = (types == null ? NO_TYPES : types);
    if (_names.length != _types.length) {
      throw new IllegalArgumentException("Mismatching names (" + _names.length + "), types (" + _types.length + ")");
    }
    int h = 1;
    int i = 0; for (int len = _types.length; i < len; i++) {
      h += _types[i].hashCode();
    }
    _unboundVariables = uvars;
    _hashCode = h;
  }
  
  public static TypeBindings emptyBindings() {
    return EMPTY;
  }
  
  protected Object readResolve()
  {
    if ((_names == null) || (_names.length == 0)) {
      return EMPTY;
    }
    return this;
  }
  





  public static TypeBindings create(Class<?> erasedType, List<JavaType> typeList)
  {
    JavaType[] types = (typeList == null) || (typeList.isEmpty()) ? NO_TYPES : (JavaType[])typeList.toArray(NO_TYPES);
    return create(erasedType, types);
  }
  
  public static TypeBindings create(Class<?> erasedType, JavaType[] types)
  {
    if (types == null)
      types = NO_TYPES; else
      switch (types.length) {
      case 1: 
        return create(erasedType, types[0]);
      case 2: 
        return create(erasedType, types[0], types[1]);
      }
    TypeVariable<?>[] vars = erasedType.getTypeParameters();
    String[] names;
    String[] names; if ((vars == null) || (vars.length == 0)) {
      names = NO_STRINGS;
    } else {
      int len = vars.length;
      names = new String[len];
      for (int i = 0; i < len; i++) {
        names[i] = vars[i].getName();
      }
    }
    
    if (names.length != types.length) {
      throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + (types.length == 1 ? "" : "s") + ": class expects " + names.length);
    }
    

    return new TypeBindings(names, types, null);
  }
  

  public static TypeBindings create(Class<?> erasedType, JavaType typeArg1)
  {
    TypeVariable<?>[] vars = TypeParamStash.paramsFor1(erasedType);
    int varLen = vars == null ? 0 : vars.length;
    if (varLen != 1) {
      throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
    }
    
    return new TypeBindings(new String[] { vars[0].getName() }, new JavaType[] { typeArg1 }, null);
  }
  


  public static TypeBindings create(Class<?> erasedType, JavaType typeArg1, JavaType typeArg2)
  {
    TypeVariable<?>[] vars = TypeParamStash.paramsFor2(erasedType);
    int varLen = vars == null ? 0 : vars.length;
    if (varLen != 2) {
      throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 2 type parameters: class expects " + varLen);
    }
    
    return new TypeBindings(new String[] { vars[0].getName(), vars[1].getName() }, new JavaType[] { typeArg1, typeArg2 }, null);
  }
  






  public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType typeArg1)
  {
    TypeVariable<?>[] vars = erasedType.getTypeParameters();
    int varLen = vars == null ? 0 : vars.length;
    if (varLen == 0) {
      return EMPTY;
    }
    if (varLen != 1) {
      throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
    }
    
    return new TypeBindings(new String[] { vars[0].getName() }, new JavaType[] { typeArg1 }, null);
  }
  






  public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType[] types)
  {
    TypeVariable<?>[] vars = erasedType.getTypeParameters();
    if ((vars == null) || (vars.length == 0)) {
      return EMPTY;
    }
    if (types == null) {
      types = NO_TYPES;
    }
    int len = vars.length;
    String[] names = new String[len];
    for (int i = 0; i < len; i++) {
      names[i] = vars[i].getName();
    }
    
    if (names.length != types.length) {
      throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + (types.length == 1 ? "" : "s") + ": class expects " + names.length);
    }
    

    return new TypeBindings(names, types, null);
  }
  





  public TypeBindings withUnboundVariable(String name)
  {
    int len = _unboundVariables == null ? 0 : _unboundVariables.length;
    
    String[] names = len == 0 ? new String[1] : (String[])Arrays.copyOf(_unboundVariables, len + 1);
    names[len] = name;
    return new TypeBindings(_names, _types, names);
  }
  









  public JavaType findBoundType(String name)
  {
    int i = 0; for (int len = _names.length; i < len; i++) {
      if (name.equals(_names[i])) {
        JavaType t = _types[i];
        if ((t instanceof ResolvedRecursiveType)) {
          ResolvedRecursiveType rrt = (ResolvedRecursiveType)t;
          JavaType t2 = rrt.getSelfReferencedType();
          if (t2 != null) {
            t = t2;
          }
        }
        









        return t;
      }
    }
    return null;
  }
  
  public boolean isEmpty() {
    return _types.length == 0;
  }
  


  public int size()
  {
    return _types.length;
  }
  
  public String getBoundName(int index)
  {
    if ((index < 0) || (index >= _names.length)) {
      return null;
    }
    return _names[index];
  }
  
  public JavaType getBoundType(int index)
  {
    if ((index < 0) || (index >= _types.length)) {
      return null;
    }
    return _types[index];
  }
  



  public List<JavaType> getTypeParameters()
  {
    if (_types.length == 0) {
      return java.util.Collections.emptyList();
    }
    return Arrays.asList(_types);
  }
  


  public boolean hasUnbound(String name)
  {
    if (_unboundVariables != null) {
      int i = _unboundVariables.length; do { i--; if (i < 0) break;
      } while (!name.equals(_unboundVariables[i]));
      return true;
    }
    

    return false;
  }
  







  public Object asKey(Class<?> rawBase)
  {
    return new AsKey(rawBase, _types, _hashCode);
  }
  






  public String toString()
  {
    if (_types.length == 0) {
      return "<>";
    }
    StringBuilder sb = new StringBuilder();
    sb.append('<');
    int i = 0; for (int len = _types.length; i < len; i++) {
      if (i > 0) {
        sb.append(',');
      }
      
      String sig = _types[i].getGenericSignature();
      sb.append(sig);
    }
    sb.append('>');
    return sb.toString();
  }
  
  public int hashCode() { return _hashCode; }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (!com.fasterxml.jackson.databind.util.ClassUtil.hasClass(o, getClass())) {
      return false;
    }
    TypeBindings other = (TypeBindings)o;
    int len = _types.length;
    if (len != other.size()) {
      return false;
    }
    JavaType[] otherTypes = _types;
    for (int i = 0; i < len; i++) {
      if (!otherTypes[i].equals(_types[i])) {
        return false;
      }
    }
    return true;
  }
  





  protected JavaType[] typeParameterArray()
  {
    return _types;
  }
  
















  static class TypeParamStash
  {
    private static final TypeVariable<?>[] VARS_ABSTRACT_LIST = AbstractList.class.getTypeParameters();
    private static final TypeVariable<?>[] VARS_COLLECTION = Collection.class.getTypeParameters();
    private static final TypeVariable<?>[] VARS_ITERABLE = Iterable.class.getTypeParameters();
    private static final TypeVariable<?>[] VARS_LIST = List.class.getTypeParameters();
    private static final TypeVariable<?>[] VARS_ARRAY_LIST = ArrayList.class.getTypeParameters();
    
    private static final TypeVariable<?>[] VARS_MAP = Map.class.getTypeParameters();
    private static final TypeVariable<?>[] VARS_HASH_MAP = HashMap.class.getTypeParameters();
    private static final TypeVariable<?>[] VARS_LINKED_HASH_MAP = LinkedHashMap.class.getTypeParameters();
    
    TypeParamStash() {}
    
    public static TypeVariable<?>[] paramsFor1(Class<?> erasedType) { if (erasedType == Collection.class) {
        return VARS_COLLECTION;
      }
      if (erasedType == List.class) {
        return VARS_LIST;
      }
      if (erasedType == ArrayList.class) {
        return VARS_ARRAY_LIST;
      }
      if (erasedType == AbstractList.class) {
        return VARS_ABSTRACT_LIST;
      }
      if (erasedType == Iterable.class) {
        return VARS_ITERABLE;
      }
      return erasedType.getTypeParameters();
    }
    
    public static TypeVariable<?>[] paramsFor2(Class<?> erasedType)
    {
      if (erasedType == Map.class) {
        return VARS_MAP;
      }
      if (erasedType == HashMap.class) {
        return VARS_HASH_MAP;
      }
      if (erasedType == LinkedHashMap.class) {
        return VARS_LINKED_HASH_MAP;
      }
      return erasedType.getTypeParameters();
    }
  }
  

  static final class AsKey
  {
    private final Class<?> _raw;
    
    private final JavaType[] _params;
    
    private final int _hash;
    
    public AsKey(Class<?> raw, JavaType[] params, int hash)
    {
      _raw = raw;
      _params = params;
      _hash = hash;
    }
    
    public int hashCode() {
      return _hash;
    }
    
    public boolean equals(Object o) {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() != getClass()) return false;
      AsKey other = (AsKey)o;
      
      if ((_hash == _hash) && (_raw == _raw)) {
        JavaType[] otherParams = _params;
        int len = _params.length;
        
        if (len == otherParams.length) {
          for (int i = 0; i < len; i++) {
            if (!_params[i].equals(otherParams[i])) {
              return false;
            }
          }
          return true;
        }
      }
      return false;
    }
    
    public String toString()
    {
      return _raw.getName() + "<>";
    }
  }
}
