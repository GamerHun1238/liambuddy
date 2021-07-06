package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ClassUtil.Ctor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;














final class AnnotatedCreatorCollector
  extends CollectorBase
{
  private final TypeResolutionContext _typeContext;
  private AnnotatedConstructor _defaultConstructor;
  
  AnnotatedCreatorCollector(AnnotationIntrospector intr, TypeResolutionContext tc)
  {
    super(intr);
    _typeContext = tc;
  }
  



  public static AnnotatedClass.Creators collectCreators(AnnotationIntrospector intr, TypeResolutionContext tc, JavaType type, Class<?> primaryMixIn)
  {
    return 
      new AnnotatedCreatorCollector(intr, tc).collect(type, primaryMixIn);
  }
  




  AnnotatedClass.Creators collect(JavaType type, Class<?> primaryMixIn)
  {
    List<AnnotatedConstructor> constructors = _findPotentialConstructors(type, primaryMixIn);
    List<AnnotatedMethod> factories = _findPotentialFactories(type, primaryMixIn);
    




    if (_intr != null) {
      if ((_defaultConstructor != null) && 
        (_intr.hasIgnoreMarker(_defaultConstructor))) {
        _defaultConstructor = null;
      }
      

      int i = constructors.size(); for (;;) { i--; if (i < 0) break;
        if (_intr.hasIgnoreMarker((AnnotatedMember)constructors.get(i))) {
          constructors.remove(i);
        }
      }
      int i = factories.size(); for (;;) { i--; if (i < 0) break;
        if (_intr.hasIgnoreMarker((AnnotatedMember)factories.get(i))) {
          factories.remove(i);
        }
      }
    }
    return new AnnotatedClass.Creators(_defaultConstructor, constructors, factories);
  }
  






  private List<AnnotatedConstructor> _findPotentialConstructors(JavaType type, Class<?> primaryMixIn)
  {
    ClassUtil.Ctor defaultCtor = null;
    List<ClassUtil.Ctor> ctors = null;
    


    ClassUtil.Ctor ctor;
    

    if (!type.isEnumType()) {
      ClassUtil.Ctor[] declaredCtors = ClassUtil.getConstructors(type.getRawClass());
      for (ctor : declaredCtors)
        if (isIncludableConstructor(ctor.getConstructor()))
        {

          if (ctor.getParamCount() == 0) {
            defaultCtor = ctor;
          } else {
            if (ctors == null) {
              ctors = new ArrayList();
            }
            ctors.add(ctor);
          } }
    }
    int ctorCount;
    int ctorCount;
    List<AnnotatedConstructor> result;
    if (ctors == null) {
      List<AnnotatedConstructor> result = Collections.emptyList();
      
      if (defaultCtor == null) {
        return result;
      }
      ctorCount = 0;
    } else {
      ctorCount = ctors.size();
      result = new ArrayList(ctorCount);
      for (int i = 0; i < ctorCount; i++) {
        result.add(null);
      }
    }
    

    if (primaryMixIn != null) {
      MemberKey[] ctorKeys = null;
      ClassUtil.Ctor[] arrayOfCtor2 = ClassUtil.getConstructors(primaryMixIn);ctor = arrayOfCtor2.length; for (ClassUtil.Ctor localCtor1 = 0; localCtor1 < ctor; localCtor1++) { ClassUtil.Ctor mixinCtor = arrayOfCtor2[localCtor1];
        if (mixinCtor.getParamCount() == 0) {
          if (defaultCtor != null) {
            _defaultConstructor = constructDefaultConstructor(defaultCtor, mixinCtor);
            defaultCtor = null;
          }
          
        }
        else if (ctors != null) {
          if (ctorKeys == null) {
            ctorKeys = new MemberKey[ctorCount];
            for (int i = 0; i < ctorCount; i++) {
              ctorKeys[i] = new MemberKey(((ClassUtil.Ctor)ctors.get(i)).getConstructor());
            }
          }
          MemberKey key = new MemberKey(mixinCtor.getConstructor());
          
          for (int i = 0; i < ctorCount; i++) {
            if (key.equals(ctorKeys[i])) {
              result.set(i, 
                constructNonDefaultConstructor((ClassUtil.Ctor)ctors.get(i), mixinCtor));
              break;
            }
          }
        }
      }
    }
    
    if (defaultCtor != null) {
      _defaultConstructor = constructDefaultConstructor(defaultCtor, null);
    }
    for (int i = 0; i < ctorCount; i++) {
      AnnotatedConstructor ctor = (AnnotatedConstructor)result.get(i);
      if (ctor == null) {
        result.set(i, 
          constructNonDefaultConstructor((ClassUtil.Ctor)ctors.get(i), null));
      }
    }
    return result;
  }
  
  private List<AnnotatedMethod> _findPotentialFactories(JavaType type, Class<?> primaryMixIn)
  {
    List<Method> candidates = null;
    
    Method m;
    for (m : ClassUtil.getClassMethods(type.getRawClass())) {
      if (Modifier.isStatic(m.getModifiers()))
      {



        if (candidates == null) {
          candidates = new ArrayList();
        }
        candidates.add(m);
      }
    }
    if (candidates == null) {
      return Collections.emptyList();
    }
    int factoryCount = candidates.size();
    Object result = new ArrayList(factoryCount);
    for (int i = 0; i < factoryCount; i++) {
      ((List)result).add(null);
    }
    
    if (primaryMixIn != null) {
      MemberKey[] methodKeys = null;
      for (Method mixinFactory : ClassUtil.getDeclaredMethods(primaryMixIn)) {
        if (Modifier.isStatic(mixinFactory.getModifiers()))
        {

          if (methodKeys == null) {
            methodKeys = new MemberKey[factoryCount];
            for (int i = 0; i < factoryCount; i++) {
              methodKeys[i] = new MemberKey((Method)candidates.get(i));
            }
          }
          MemberKey key = new MemberKey(mixinFactory);
          for (int i = 0; i < factoryCount; i++) {
            if (key.equals(methodKeys[i])) {
              ((List)result).set(i, 
                constructFactoryCreator((Method)candidates.get(i), mixinFactory));
              break;
            }
          }
        }
      }
    }
    for (int i = 0; i < factoryCount; i++) {
      AnnotatedMethod factory = (AnnotatedMethod)((List)result).get(i);
      if (factory == null) {
        ((List)result).set(i, 
          constructFactoryCreator((Method)candidates.get(i), null));
      }
    }
    return result;
  }
  

  protected AnnotatedConstructor constructDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin)
  {
    if (_intr == null) {
      return new AnnotatedConstructor(_typeContext, ctor.getConstructor(), 
        _emptyAnnotationMap(), NO_ANNOTATION_MAPS);
    }
    return new AnnotatedConstructor(_typeContext, ctor.getConstructor(), 
      collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
  }
  



  protected AnnotatedConstructor constructNonDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin)
  {
    int paramCount = ctor.getParamCount();
    if (_intr == null) {
      return new AnnotatedConstructor(_typeContext, ctor.getConstructor(), 
        _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
    }
    




    if (paramCount == 0) {
      return new AnnotatedConstructor(_typeContext, ctor.getConstructor(), 
        collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
    }
    


    Annotation[][] paramAnns = ctor.getParameterAnnotations();
    AnnotationMap[] resolvedAnnotations; if (paramCount != paramAnns.length)
    {


      AnnotationMap[] resolvedAnnotations = null;
      Class<?> dc = ctor.getDeclaringClass();
      
      if ((ClassUtil.isEnumType(dc)) && (paramCount == paramAnns.length + 2)) {
        Annotation[][] old = paramAnns;
        paramAnns = new Annotation[old.length + 2][];
        System.arraycopy(old, 0, paramAnns, 2, old.length);
        resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][])null);
      } else if (dc.isMemberClass())
      {
        if (paramCount == paramAnns.length + 1)
        {
          Annotation[][] old = paramAnns;
          paramAnns = new Annotation[old.length + 1][];
          System.arraycopy(old, 0, paramAnns, 1, old.length);
          paramAnns[0] = NO_ANNOTATIONS;
          resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][])null);
        }
      }
      if (resolvedAnnotations == null) {
        throw new IllegalStateException(String.format("Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations", new Object[] {ctor
        
          .getDeclaringClass().getName(), Integer.valueOf(paramCount), Integer.valueOf(paramAnns.length) }));
      }
    } else {
      resolvedAnnotations = collectAnnotations(paramAnns, mixin == null ? (Annotation[][])null : mixin
        .getParameterAnnotations());
    }
    return new AnnotatedConstructor(_typeContext, ctor.getConstructor(), 
      collectAnnotations(ctor, mixin), resolvedAnnotations);
  }
  
  protected AnnotatedMethod constructFactoryCreator(Method m, Method mixin)
  {
    int paramCount = m.getParameterTypes().length;
    if (_intr == null) {
      return new AnnotatedMethod(_typeContext, m, _emptyAnnotationMap(), 
        _emptyAnnotationMaps(paramCount));
    }
    if (paramCount == 0) {
      return new AnnotatedMethod(_typeContext, m, collectAnnotations(m, mixin), NO_ANNOTATION_MAPS);
    }
    
    return new AnnotatedMethod(_typeContext, m, collectAnnotations(m, mixin), 
      collectAnnotations(m.getParameterAnnotations(), mixin == null ? (Annotation[][])null : mixin
      .getParameterAnnotations()));
  }
  
  private AnnotationMap[] collectAnnotations(Annotation[][] mainAnns, Annotation[][] mixinAnns) {
    int count = mainAnns.length;
    AnnotationMap[] result = new AnnotationMap[count];
    for (int i = 0; i < count; i++) {
      AnnotationCollector c = collectAnnotations(AnnotationCollector.emptyCollector(), mainAnns[i]);
      
      if (mixinAnns != null) {
        c = collectAnnotations(c, mixinAnns[i]);
      }
      result[i] = c.asAnnotationMap();
    }
    return result;
  }
  

  private AnnotationMap collectAnnotations(ClassUtil.Ctor main, ClassUtil.Ctor mixin)
  {
    AnnotationCollector c = collectAnnotations(main.getConstructor().getDeclaredAnnotations());
    if (mixin != null) {
      c = collectAnnotations(c, mixin.getConstructor().getDeclaredAnnotations());
    }
    return c.asAnnotationMap();
  }
  
  private final AnnotationMap collectAnnotations(AnnotatedElement main, AnnotatedElement mixin) {
    AnnotationCollector c = collectAnnotations(main.getDeclaredAnnotations());
    if (mixin != null) {
      c = collectAnnotations(c, mixin.getDeclaredAnnotations());
    }
    return c.asAnnotationMap();
  }
  
  private static boolean isIncludableConstructor(Constructor<?> c)
  {
    return !c.isSynthetic();
  }
}
