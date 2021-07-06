package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.Annotations;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;






public abstract class AnnotationCollector
{
  protected static final Annotations NO_ANNOTATIONS = new NoAnnotations();
  

  protected final Object _data;
  

  protected AnnotationCollector(Object d)
  {
    _data = d;
  }
  
  public static Annotations emptyAnnotations() { return NO_ANNOTATIONS; }
  
  public static AnnotationCollector emptyCollector() {
    return EmptyCollector.instance;
  }
  
  public static AnnotationCollector emptyCollector(Object data) {
    return new EmptyCollector(data);
  }
  
  public abstract Annotations asAnnotations();
  
  public abstract AnnotationMap asAnnotationMap();
  
  public Object getData() { return _data; }
  




  public abstract boolean isPresent(Annotation paramAnnotation);
  




  public abstract AnnotationCollector addOrOverride(Annotation paramAnnotation);
  




  static class EmptyCollector
    extends AnnotationCollector
  {
    public static final EmptyCollector instance = new EmptyCollector(null);
    
    EmptyCollector(Object data) { super(); }
    
    public Annotations asAnnotations()
    {
      return NO_ANNOTATIONS;
    }
    
    public AnnotationMap asAnnotationMap()
    {
      return new AnnotationMap();
    }
    
    public boolean isPresent(Annotation ann) {
      return false;
    }
    
    public AnnotationCollector addOrOverride(Annotation ann) {
      return new AnnotationCollector.OneCollector(_data, ann.annotationType(), ann);
    }
  }
  
  static class OneCollector extends AnnotationCollector
  {
    private Class<?> _type;
    private Annotation _value;
    
    public OneCollector(Object data, Class<?> type, Annotation value)
    {
      super();
      _type = type;
      _value = value;
    }
    
    public Annotations asAnnotations()
    {
      return new AnnotationCollector.OneAnnotation(_type, _value);
    }
    
    public AnnotationMap asAnnotationMap()
    {
      return AnnotationMap.of(_type, _value);
    }
    
    public boolean isPresent(Annotation ann)
    {
      return ann.annotationType() == _type;
    }
    
    public AnnotationCollector addOrOverride(Annotation ann)
    {
      Class<?> type = ann.annotationType();
      
      if (_type == type) {
        _value = ann;
        return this;
      }
      return new AnnotationCollector.NCollector(_data, _type, _value, type, ann);
    }
  }
  
  static class NCollector
    extends AnnotationCollector
  {
    protected final HashMap<Class<?>, Annotation> _annotations;
    
    public NCollector(Object data, Class<?> type1, Annotation value1, Class<?> type2, Annotation value2)
    {
      super();
      _annotations = new HashMap();
      _annotations.put(type1, value1);
      _annotations.put(type2, value2);
    }
    
    public Annotations asAnnotations()
    {
      if (_annotations.size() == 2) {
        Iterator<Map.Entry<Class<?>, Annotation>> it = _annotations.entrySet().iterator();
        Map.Entry<Class<?>, Annotation> en1 = (Map.Entry)it.next();Map.Entry<Class<?>, Annotation> en2 = (Map.Entry)it.next();
        return new AnnotationCollector.TwoAnnotations((Class)en1.getKey(), (Annotation)en1.getValue(), 
          (Class)en2.getKey(), (Annotation)en2.getValue());
      }
      return new AnnotationMap(_annotations);
    }
    
    public AnnotationMap asAnnotationMap()
    {
      AnnotationMap result = new AnnotationMap();
      for (Annotation ann : _annotations.values()) {
        result.add(ann);
      }
      return result;
    }
    
    public boolean isPresent(Annotation ann)
    {
      return _annotations.containsKey(ann.annotationType());
    }
    
    public AnnotationCollector addOrOverride(Annotation ann)
    {
      _annotations.put(ann.annotationType(), ann);
      return this;
    }
  }
  




  public static class NoAnnotations
    implements Annotations, Serializable
  {
    private static final long serialVersionUID = 1L;
    




    NoAnnotations() {}
    




    public <A extends Annotation> A get(Class<A> cls)
    {
      return null;
    }
    
    public boolean has(Class<?> cls)
    {
      return false;
    }
    
    public boolean hasOneOf(Class<? extends Annotation>[] annoClasses)
    {
      return false;
    }
    
    public int size()
    {
      return 0;
    }
  }
  
  public static class OneAnnotation
    implements Annotations, Serializable
  {
    private static final long serialVersionUID = 1L;
    private final Class<?> _type;
    private final Annotation _value;
    
    public OneAnnotation(Class<?> type, Annotation value)
    {
      _type = type;
      _value = value;
    }
    

    public <A extends Annotation> A get(Class<A> cls)
    {
      if (_type == cls) {
        return _value;
      }
      return null;
    }
    
    public boolean has(Class<?> cls)
    {
      return _type == cls;
    }
    
    public boolean hasOneOf(Class<? extends Annotation>[] annoClasses)
    {
      for (Class<?> cls : annoClasses) {
        if (cls == _type) {
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return 1;
    }
  }
  
  public static class TwoAnnotations implements Annotations, Serializable
  {
    private static final long serialVersionUID = 1L;
    private final Class<?> _type1;
    private final Class<?> _type2;
    private final Annotation _value1;
    private final Annotation _value2;
    
    public TwoAnnotations(Class<?> type1, Annotation value1, Class<?> type2, Annotation value2)
    {
      _type1 = type1;
      _value1 = value1;
      _type2 = type2;
      _value2 = value2;
    }
    

    public <A extends Annotation> A get(Class<A> cls)
    {
      if (_type1 == cls) {
        return _value1;
      }
      if (_type2 == cls) {
        return _value2;
      }
      return null;
    }
    
    public boolean has(Class<?> cls)
    {
      return (_type1 == cls) || (_type2 == cls);
    }
    
    public boolean hasOneOf(Class<? extends Annotation>[] annoClasses)
    {
      for (Class<?> cls : annoClasses) {
        if ((cls == _type1) || (cls == _type2)) {
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return 2;
    }
  }
}
