package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;




public class AnnotatedFieldCollector
  extends CollectorBase
{
  private final TypeFactory _typeFactory;
  private final ClassIntrospector.MixInResolver _mixInResolver;
  
  AnnotatedFieldCollector(AnnotationIntrospector intr, TypeFactory types, ClassIntrospector.MixInResolver mixins)
  {
    super(intr);
    _typeFactory = types;
    _mixInResolver = (intr == null ? null : mixins);
  }
  



  public static List<AnnotatedField> collectFields(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type)
  {
    return new AnnotatedFieldCollector(intr, types, mixins).collect(tc, type);
  }
  
  List<AnnotatedField> collect(TypeResolutionContext tc, JavaType type)
  {
    Map<String, FieldBuilder> foundFields = _findFields(tc, type, null);
    if (foundFields == null) {
      return Collections.emptyList();
    }
    List<AnnotatedField> result = new ArrayList(foundFields.size());
    for (FieldBuilder b : foundFields.values()) {
      result.add(b.build());
    }
    return result;
  }
  




  private Map<String, FieldBuilder> _findFields(TypeResolutionContext tc, JavaType type, Map<String, FieldBuilder> fields)
  {
    JavaType parent = type.getSuperClass();
    if (parent == null) {
      return fields;
    }
    Class<?> cls = type.getRawClass();
    
    fields = _findFields(new TypeResolutionContext.Basic(_typeFactory, parent.getBindings()), parent, fields);
    
    for (Field f : ClassUtil.getDeclaredFields(cls))
    {
      if (_isIncludableField(f))
      {




        if (fields == null) {
          fields = new LinkedHashMap();
        }
        FieldBuilder b = new FieldBuilder(tc, f);
        if (_intr != null) {
          annotations = collectAnnotations(annotations, f.getDeclaredAnnotations());
        }
        fields.put(f.getName(), b);
      }
    }
    if (_mixInResolver != null) {
      Object mixin = _mixInResolver.findMixInClassFor(cls);
      if (mixin != null) {
        _addFieldMixIns((Class)mixin, cls, fields);
      }
    }
    return fields;
  }
  






  private void _addFieldMixIns(Class<?> mixInCls, Class<?> targetClass, Map<String, FieldBuilder> fields)
  {
    List<Class<?>> parents = ClassUtil.findSuperClasses(mixInCls, targetClass, true);
    for (Class<?> mixin : parents) {
      for (Field mixinField : ClassUtil.getDeclaredFields(mixin))
      {
        if (_isIncludableField(mixinField))
        {

          String name = mixinField.getName();
          
          FieldBuilder b = (FieldBuilder)fields.get(name);
          if (b != null) {
            annotations = collectAnnotations(annotations, mixinField.getDeclaredAnnotations());
          }
        }
      }
    }
  }
  
  private boolean _isIncludableField(Field f)
  {
    if (f.isSynthetic()) {
      return false;
    }
    

    int mods = f.getModifiers();
    if (Modifier.isStatic(mods)) {
      return false;
    }
    return true;
  }
  
  private static final class FieldBuilder
  {
    public final TypeResolutionContext typeContext;
    public final Field field;
    public AnnotationCollector annotations;
    
    public FieldBuilder(TypeResolutionContext tc, Field f) {
      typeContext = tc;
      field = f;
      annotations = AnnotationCollector.emptyCollector();
    }
    
    public AnnotatedField build() {
      return new AnnotatedField(typeContext, field, annotations.asAnnotationMap());
    }
  }
}
