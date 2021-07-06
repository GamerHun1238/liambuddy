package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.Annotations;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;





public final class AnnotationMap
  implements Annotations
{
  protected HashMap<Class<?>, Annotation> _annotations;
  
  public AnnotationMap() {}
  
  public static AnnotationMap of(Class<?> type, Annotation value)
  {
    HashMap<Class<?>, Annotation> ann = new HashMap(4);
    ann.put(type, value);
    return new AnnotationMap(ann);
  }
  
  AnnotationMap(HashMap<Class<?>, Annotation> a) {
    _annotations = a;
  }
  








  public <A extends Annotation> A get(Class<A> cls)
  {
    if (_annotations == null) {
      return null;
    }
    return (Annotation)_annotations.get(cls);
  }
  

  public boolean has(Class<?> cls)
  {
    if (_annotations == null) {
      return false;
    }
    return _annotations.containsKey(cls);
  }
  






  public boolean hasOneOf(Class<? extends Annotation>[] annoClasses)
  {
    if (_annotations != null) {
      int i = 0; for (int end = annoClasses.length; i < end; i++) {
        if (_annotations.containsKey(annoClasses[i])) {
          return true;
        }
      }
    }
    return false;
  }
  








  public Iterable<Annotation> annotations()
  {
    if ((_annotations == null) || (_annotations.size() == 0)) {
      return Collections.emptyList();
    }
    return _annotations.values();
  }
  
  public static AnnotationMap merge(AnnotationMap primary, AnnotationMap secondary)
  {
    if ((primary == null) || (_annotations == null) || (_annotations.isEmpty())) {
      return secondary;
    }
    if ((secondary == null) || (_annotations == null) || (_annotations.isEmpty())) {
      return primary;
    }
    HashMap<Class<?>, Annotation> annotations = new HashMap();
    
    for (Annotation ann : _annotations.values()) {
      annotations.put(ann.annotationType(), ann);
    }
    
    for (Annotation ann : _annotations.values()) {
      annotations.put(ann.annotationType(), ann);
    }
    return new AnnotationMap(annotations);
  }
  
  public int size()
  {
    return _annotations == null ? 0 : _annotations.size();
  }
  




  public boolean addIfNotPresent(Annotation ann)
  {
    if ((_annotations == null) || (!_annotations.containsKey(ann.annotationType()))) {
      _add(ann);
      return true;
    }
    return false;
  }
  





  public boolean add(Annotation ann)
  {
    return _add(ann);
  }
  
  public String toString()
  {
    if (_annotations == null) {
      return "[null]";
    }
    return _annotations.toString();
  }
  





  protected final boolean _add(Annotation ann)
  {
    if (_annotations == null) {
      _annotations = new HashMap();
    }
    Annotation previous = (Annotation)_annotations.put(ann.annotationType(), ann);
    return (previous == null) || (!previous.equals(ann));
  }
}
