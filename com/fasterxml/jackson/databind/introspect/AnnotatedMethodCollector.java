package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AnnotatedMethodCollector extends CollectorBase
{
  private final ClassIntrospector.MixInResolver _mixInResolver;
  
  AnnotatedMethodCollector(AnnotationIntrospector intr, ClassIntrospector.MixInResolver mixins)
  {
    super(intr);
    _mixInResolver = (intr == null ? null : mixins);
  }
  




  public static AnnotatedMethodMap collectMethods(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type, List<JavaType> superTypes, Class<?> primaryMixIn)
  {
    return 
      new AnnotatedMethodCollector(intr, mixins).collect(types, tc, type, superTypes, primaryMixIn);
  }
  

  AnnotatedMethodMap collect(TypeFactory typeFactory, TypeResolutionContext tc, JavaType mainType, List<JavaType> superTypes, Class<?> primaryMixIn)
  {
    Map<MemberKey, MethodBuilder> methods = new LinkedHashMap();
    

    _addMemberMethods(tc, mainType.getRawClass(), methods, primaryMixIn);
    

    for (JavaType type : superTypes) {
      Class<?> mixin = _mixInResolver == null ? null : _mixInResolver.findMixInClassFor(type.getRawClass());
      _addMemberMethods(new TypeResolutionContext.Basic(typeFactory, type
        .getBindings()), type
        .getRawClass(), methods, mixin);
    }
    
    boolean checkJavaLangObject = false;
    Class<?> mixin; if (_mixInResolver != null) {
      mixin = _mixInResolver.findMixInClassFor(Object.class);
      if (mixin != null) {
        _addMethodMixIns(tc, mainType.getRawClass(), methods, mixin);
        checkJavaLangObject = true;
      }
    }
    




    if ((checkJavaLangObject) && (_intr != null) && (!methods.isEmpty()))
    {
      for (mixin = methods.entrySet().iterator(); mixin.hasNext();) { entry = (Map.Entry)mixin.next();
        MemberKey k = (MemberKey)entry.getKey();
        if (("hashCode".equals(k.getName())) && (0 == k.argCount()))
        {
          try
          {

            Method m = Object.class.getDeclaredMethod(k.getName(), new Class[0]);
            if (m != null) {
              MethodBuilder b = (MethodBuilder)entry.getValue();
              annotations = collectDefaultAnnotations(annotations, m
                .getDeclaredAnnotations());
              method = m;
            }
          } catch (Exception localException) {}
        }
      }
    }
    Map.Entry<MemberKey, MethodBuilder> entry;
    if (methods.isEmpty()) {
      return new AnnotatedMethodMap();
    }
    Map<MemberKey, AnnotatedMethod> actual = new LinkedHashMap(methods.size());
    for (Map.Entry<MemberKey, MethodBuilder> entry : methods.entrySet()) {
      AnnotatedMethod am = ((MethodBuilder)entry.getValue()).build();
      if (am != null) {
        actual.put(entry.getKey(), am);
      }
    }
    return new AnnotatedMethodMap(actual);
  }
  


  private void _addMemberMethods(TypeResolutionContext tc, Class<?> cls, Map<MemberKey, MethodBuilder> methods, Class<?> mixInCls)
  {
    if (mixInCls != null) {
      _addMethodMixIns(tc, cls, methods, mixInCls);
    }
    if (cls == null) {
      return;
    }
    
    for (Method m : ClassUtil.getClassMethods(cls)) {
      if (_isIncludableMemberMethod(m))
      {

        MemberKey key = new MemberKey(m);
        MethodBuilder b = (MethodBuilder)methods.get(key);
        if (b == null)
        {
          AnnotationCollector c = _intr == null ? AnnotationCollector.emptyCollector() : collectAnnotations(m.getDeclaredAnnotations());
          methods.put(key, new MethodBuilder(tc, m, c));
        } else {
          if (_intr != null) {
            annotations = collectDefaultAnnotations(annotations, m.getDeclaredAnnotations());
          }
          Method old = method;
          if (old == null) {
            method = m;
          }
          else if ((Modifier.isAbstract(old.getModifiers())) && 
            (!Modifier.isAbstract(m.getModifiers())))
          {





            method = m;
            

            typeContext = tc;
          }
        }
      }
    }
  }
  
  protected void _addMethodMixIns(TypeResolutionContext tc, Class<?> targetClass, Map<MemberKey, MethodBuilder> methods, Class<?> mixInCls)
  {
    if (_intr == null) {
      return;
    }
    for (Class<?> mixin : ClassUtil.findRawSuperTypes(mixInCls, targetClass, true)) {
      for (Method m : ClassUtil.getDeclaredMethods(mixin)) {
        if (_isIncludableMemberMethod(m))
        {

          MemberKey key = new MemberKey(m);
          MethodBuilder b = (MethodBuilder)methods.get(key);
          java.lang.annotation.Annotation[] anns = m.getDeclaredAnnotations();
          if (b == null)
          {

            methods.put(key, new MethodBuilder(tc, null, collectAnnotations(anns)));
          } else {
            annotations = collectDefaultAnnotations(annotations, anns);
          }
        }
      }
    }
  }
  
  private boolean _isIncludableMemberMethod(Method m) {
    if ((Modifier.isStatic(m.getModifiers())) || 
    

      (m.isSynthetic()) || (m.isBridge())) {
      return false;
    }
    

    int pcount = m.getParameterTypes().length;
    return pcount <= 2;
  }
  

  private static final class MethodBuilder
  {
    public TypeResolutionContext typeContext;
    public Method method;
    public AnnotationCollector annotations;
    
    public MethodBuilder(TypeResolutionContext tc, Method m, AnnotationCollector ann)
    {
      typeContext = tc;
      method = m;
      annotations = ann;
    }
    
    public AnnotatedMethod build() {
      if (method == null) {
        return null;
      }
      

      return new AnnotatedMethod(typeContext, method, annotations.asAnnotationMap(), null);
    }
  }
}
