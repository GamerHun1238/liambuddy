package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;





public final class AnnotatedMethodMap
  implements Iterable<AnnotatedMethod>
{
  protected Map<MemberKey, AnnotatedMethod> _methods;
  
  public AnnotatedMethodMap() {}
  
  public AnnotatedMethodMap(Map<MemberKey, AnnotatedMethod> m)
  {
    _methods = m;
  }
  
  public int size() {
    return _methods == null ? 0 : _methods.size();
  }
  
  public AnnotatedMethod find(String name, Class<?>[] paramTypes)
  {
    if (_methods == null) {
      return null;
    }
    return (AnnotatedMethod)_methods.get(new MemberKey(name, paramTypes));
  }
  
  public AnnotatedMethod find(Method m)
  {
    if (_methods == null) {
      return null;
    }
    return (AnnotatedMethod)_methods.get(new MemberKey(m));
  }
  







  public Iterator<AnnotatedMethod> iterator()
  {
    if (_methods == null) {
      return Collections.emptyIterator();
    }
    return _methods.values().iterator();
  }
}
