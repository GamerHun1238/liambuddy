package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;









public class AnnotatedClassResolver
{
  private static final Annotations NO_ANNOTATIONS = ;
  
  private final MapperConfig<?> _config;
  private final AnnotationIntrospector _intr;
  private final ClassIntrospector.MixInResolver _mixInResolver;
  private final TypeBindings _bindings;
  private final JavaType _type;
  private final Class<?> _class;
  private final Class<?> _primaryMixin;
  
  AnnotatedClassResolver(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r)
  {
    _config = config;
    _type = type;
    _class = type.getRawClass();
    _mixInResolver = r;
    _bindings = type.getBindings();
    
    _intr = (config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null);
    _primaryMixin = _config.findMixInClassFor(_class);
  }
  
  AnnotatedClassResolver(MapperConfig<?> config, Class<?> cls, ClassIntrospector.MixInResolver r) {
    _config = config;
    _type = null;
    _class = cls;
    _mixInResolver = r;
    _bindings = TypeBindings.emptyBindings();
    if (config == null) {
      _intr = null;
      _primaryMixin = null;
    }
    else {
      _intr = (config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null);
      _primaryMixin = _config.findMixInClassFor(_class);
    }
  }
  

  public static AnnotatedClass resolve(MapperConfig<?> config, JavaType forType, ClassIntrospector.MixInResolver r)
  {
    if ((forType.isArrayType()) && (skippableArray(config, forType.getRawClass()))) {
      return createArrayType(config, forType.getRawClass());
    }
    return new AnnotatedClassResolver(config, forType, r).resolveFully();
  }
  
  public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, Class<?> forType) {
    return resolveWithoutSuperTypes(config, forType, config);
  }
  

  public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, JavaType forType, ClassIntrospector.MixInResolver r)
  {
    if ((forType.isArrayType()) && (skippableArray(config, forType.getRawClass()))) {
      return createArrayType(config, forType.getRawClass());
    }
    return new AnnotatedClassResolver(config, forType, r).resolveWithoutSuperTypes();
  }
  

  public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, Class<?> forType, ClassIntrospector.MixInResolver r)
  {
    if ((forType.isArray()) && (skippableArray(config, forType))) {
      return createArrayType(config, forType);
    }
    return new AnnotatedClassResolver(config, forType, r).resolveWithoutSuperTypes();
  }
  
  private static boolean skippableArray(MapperConfig<?> config, Class<?> type) {
    return (config == null) || (config.findMixInClassFor(type) == null);
  }
  




  static AnnotatedClass createPrimordial(Class<?> raw)
  {
    return new AnnotatedClass(raw);
  }
  



  static AnnotatedClass createArrayType(MapperConfig<?> config, Class<?> raw)
  {
    return new AnnotatedClass(raw);
  }
  
  AnnotatedClass resolveFully() {
    List<JavaType> superTypes = ClassUtil.findSuperTypes(_type, null, false);
    return new AnnotatedClass(_type, _class, superTypes, _primaryMixin, 
      resolveClassAnnotations(superTypes), _bindings, _intr, _mixInResolver, _config
      .getTypeFactory());
  }
  
  AnnotatedClass resolveWithoutSuperTypes()
  {
    List<JavaType> superTypes = Collections.emptyList();
    return new AnnotatedClass(null, _class, superTypes, _primaryMixin, 
      resolveClassAnnotations(superTypes), _bindings, _intr, _config, _config
      .getTypeFactory());
  }
  












  private Annotations resolveClassAnnotations(List<JavaType> superTypes)
  {
    if (_intr == null) {
      return NO_ANNOTATIONS;
    }
    AnnotationCollector resolvedCA = AnnotationCollector.emptyCollector();
    
    if (_primaryMixin != null) {
      resolvedCA = _addClassMixIns(resolvedCA, _class, _primaryMixin);
    }
    
    resolvedCA = _addAnnotationsIfNotPresent(resolvedCA, 
      ClassUtil.findClassAnnotations(_class));
    

    for (JavaType type : superTypes)
    {
      if (_mixInResolver != null) {
        Class<?> cls = type.getRawClass();
        resolvedCA = _addClassMixIns(resolvedCA, cls, _mixInResolver
          .findMixInClassFor(cls));
      }
      resolvedCA = _addAnnotationsIfNotPresent(resolvedCA, 
        ClassUtil.findClassAnnotations(type.getRawClass()));
    }
    





    if (_mixInResolver != null) {
      resolvedCA = _addClassMixIns(resolvedCA, Object.class, _mixInResolver
        .findMixInClassFor(Object.class));
    }
    return resolvedCA.asAnnotations();
  }
  

  private AnnotationCollector _addClassMixIns(AnnotationCollector annotations, Class<?> target, Class<?> mixin)
  {
    if (mixin != null)
    {
      annotations = _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(mixin));
      





      for (Class<?> parent : ClassUtil.findSuperClasses(mixin, target, false)) {
        annotations = _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(parent));
      }
    }
    return annotations;
  }
  

  private AnnotationCollector _addAnnotationsIfNotPresent(AnnotationCollector c, Annotation[] anns)
  {
    if (anns != null) {
      for (Annotation ann : anns)
      {
        if (!c.isPresent(ann)) {
          c = c.addOrOverride(ann);
          if (_intr.isAnnotationBundle(ann)) {
            c = _addFromBundleIfNotPresent(c, ann);
          }
        }
      }
    }
    return c;
  }
  

  private AnnotationCollector _addFromBundleIfNotPresent(AnnotationCollector c, Annotation bundle)
  {
    for (Annotation ann : ClassUtil.findClassAnnotations(bundle.annotationType()))
    {
      if ((!(ann instanceof Target)) && (!(ann instanceof Retention)))
      {

        if (!c.isPresent(ann)) {
          c = c.addOrOverride(ann);
          if (_intr.isAnnotationBundle(ann))
            c = _addFromBundleIfNotPresent(c, ann);
        }
      }
    }
    return c;
  }
}
