package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.util.ArrayList;








public final class ClassStack
{
  protected final ClassStack _parent;
  protected final Class<?> _current;
  private ArrayList<ResolvedRecursiveType> _selfRefs;
  
  public ClassStack(Class<?> rootType)
  {
    this(null, rootType);
  }
  
  private ClassStack(ClassStack parent, Class<?> curr) {
    _parent = parent;
    _current = curr;
  }
  


  public ClassStack child(Class<?> cls)
  {
    return new ClassStack(this, cls);
  }
  




  public void addSelfReference(ResolvedRecursiveType ref)
  {
    if (_selfRefs == null) {
      _selfRefs = new ArrayList();
    }
    _selfRefs.add(ref);
  }
  





  public void resolveSelfReferences(JavaType resolved)
  {
    if (_selfRefs != null) {
      for (ResolvedRecursiveType ref : _selfRefs) {
        ref.setReference(resolved);
      }
    }
  }
  
  public ClassStack find(Class<?> cls)
  {
    if (_current == cls) return this;
    for (ClassStack curr = _parent; curr != null; curr = _parent) {
      if (_current == cls) {
        return curr;
      }
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[ClassStack (self-refs: ")
      .append(_selfRefs == null ? "0" : String.valueOf(_selfRefs.size()))
      .append(')');
    
    for (ClassStack curr = this; curr != null; curr = _parent) {
      sb.append(' ').append(_current.getName());
    }
    sb.append(']');
    return sb.toString();
  }
}
