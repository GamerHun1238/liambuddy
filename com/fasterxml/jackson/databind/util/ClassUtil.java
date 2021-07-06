package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public final class ClassUtil
{
  private static final Class<?> CLS_OBJECT = Object.class;
  
  private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
  private static final Ctor[] NO_CTORS = new Ctor[0];
  
  private static final java.util.Iterator<?> EMPTY_ITERATOR = java.util.Collections.emptyIterator();
  




  public ClassUtil() {}
  



  public static <T> java.util.Iterator<T> emptyIterator()
  {
    return EMPTY_ITERATOR;
  }
  




















  public static List<JavaType> findSuperTypes(JavaType type, Class<?> endBefore, boolean addClassItself)
  {
    if ((type == null) || (type.hasRawClass(endBefore)) || (type.hasRawClass(Object.class))) {
      return java.util.Collections.emptyList();
    }
    List<JavaType> result = new java.util.ArrayList(8);
    _addSuperTypes(type, endBefore, result, addClassItself);
    return result;
  }
  


  public static List<Class<?>> findRawSuperTypes(Class<?> cls, Class<?> endBefore, boolean addClassItself)
  {
    if ((cls == null) || (cls == endBefore) || (cls == Object.class)) {
      return java.util.Collections.emptyList();
    }
    List<Class<?>> result = new java.util.ArrayList(8);
    _addRawSuperTypes(cls, endBefore, result, addClassItself);
    return result;
  }
  










  public static List<Class<?>> findSuperClasses(Class<?> cls, Class<?> endBefore, boolean addClassItself)
  {
    List<Class<?>> result = new java.util.ArrayList(8);
    if ((cls != null) && (cls != endBefore)) {
      if (addClassItself) {
        result.add(cls);
      }
      while (((cls = cls.getSuperclass()) != null) && 
        (cls != endBefore))
      {

        result.add(cls);
      }
    }
    return result;
  }
  
  @Deprecated
  public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
    return findSuperTypes(cls, endBefore, new java.util.ArrayList(8));
  }
  
  @Deprecated
  public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
    _addRawSuperTypes(cls, endBefore, result, false);
    return result;
  }
  

  private static void _addSuperTypes(JavaType type, Class<?> endBefore, Collection<JavaType> result, boolean addClassItself)
  {
    if (type == null) {
      return;
    }
    Class<?> cls = type.getRawClass();
    if ((cls == endBefore) || (cls == Object.class)) return;
    if (addClassItself) {
      if (result.contains(type)) {
        return;
      }
      result.add(type);
    }
    for (JavaType intCls : type.getInterfaces()) {
      _addSuperTypes(intCls, endBefore, result, true);
    }
    _addSuperTypes(type.getSuperClass(), endBefore, result, true);
  }
  
  private static void _addRawSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
    if ((cls == endBefore) || (cls == null) || (cls == Object.class)) return;
    if (addClassItself) {
      if (result.contains(cls)) {
        return;
      }
      result.add(cls);
    }
    for (Class<?> intCls : _interfaces(cls)) {
      _addRawSuperTypes(intCls, endBefore, result, true);
    }
    _addRawSuperTypes(cls.getSuperclass(), endBefore, result, true);
  }
  











  public static String canBeABeanType(Class<?> type)
  {
    if (type.isAnnotation()) {
      return "annotation";
    }
    if (type.isArray()) {
      return "array";
    }
    if (Enum.class.isAssignableFrom(type)) {
      return "enum";
    }
    if (type.isPrimitive()) {
      return "primitive";
    }
    

    return null;
  }
  




  public static String isLocalType(Class<?> type, boolean allowNonStatic)
  {
    try
    {
      if (hasEnclosingMethod(type)) {
        return "local/anonymous";
      }
      




      if ((!allowNonStatic) && 
        (!Modifier.isStatic(type.getModifiers())) && 
        (getEnclosingClass(type) != null)) {
        return "non-static member class";
      }
    }
    catch (SecurityException localSecurityException) {}catch (NullPointerException localNullPointerException) {}
    


    return null;
  }
  




  public static Class<?> getOuterClass(Class<?> type)
  {
    try
    {
      if (hasEnclosingMethod(type)) {
        return null;
      }
      if (!Modifier.isStatic(type.getModifiers())) {
        return getEnclosingClass(type);
      }
    } catch (SecurityException localSecurityException) {}
    return null;
  }
  













  public static boolean isProxyType(Class<?> type)
  {
    String name = type.getName();
    
    if ((name.startsWith("net.sf.cglib.proxy.")) || 
      (name.startsWith("org.hibernate.proxy."))) {
      return true;
    }
    
    return false;
  }
  




  public static boolean isConcrete(Class<?> type)
  {
    int mod = type.getModifiers();
    return (mod & 0x600) == 0;
  }
  
  public static boolean isConcrete(Member member)
  {
    int mod = member.getModifiers();
    return (mod & 0x600) == 0;
  }
  
  public static boolean isCollectionMapOrArray(Class<?> type)
  {
    if (type.isArray()) return true;
    if (Collection.class.isAssignableFrom(type)) return true;
    if (java.util.Map.class.isAssignableFrom(type)) return true;
    return false;
  }
  
  public static boolean isBogusClass(Class<?> cls) {
    return (cls == Void.class) || (cls == Void.TYPE) || (cls == com.fasterxml.jackson.databind.annotation.NoClass.class);
  }
  
  public static boolean isNonStaticInnerClass(Class<?> cls)
  {
    return (!Modifier.isStatic(cls.getModifiers())) && 
      (getEnclosingClass(cls) != null);
  }
  


  public static boolean isObjectOrPrimitive(Class<?> cls)
  {
    return (cls == CLS_OBJECT) || (cls.isPrimitive());
  }
  




  public static boolean hasClass(Object inst, Class<?> raw)
  {
    return (inst != null) && (inst.getClass() == raw);
  }
  




  public static void verifyMustOverride(Class<?> expType, Object instance, String method)
  {
    if (instance.getClass() != expType) {
      throw new IllegalStateException(String.format("Sub-class %s (of class %s) must override method '%s'", new Object[] {instance
      
        .getClass().getName(), expType.getName(), method }));
    }
  }
  










  @Deprecated
  public static boolean hasGetterSignature(Method m)
  {
    if (Modifier.isStatic(m.getModifiers())) {
      return false;
    }
    
    Class<?>[] pts = m.getParameterTypes();
    if ((pts != null) && (pts.length != 0)) {
      return false;
    }
    
    if (Void.TYPE == m.getReturnType()) {
      return false;
    }
    
    return true;
  }
  











  public static Throwable throwIfError(Throwable t)
  {
    if ((t instanceof Error)) {
      throw ((Error)t);
    }
    return t;
  }
  





  public static Throwable throwIfRTE(Throwable t)
  {
    if ((t instanceof RuntimeException)) {
      throw ((RuntimeException)t);
    }
    return t;
  }
  




  public static Throwable throwIfIOE(Throwable t)
    throws IOException
  {
    if ((t instanceof IOException)) {
      throw ((IOException)t);
    }
    return t;
  }
  










  public static Throwable getRootCause(Throwable t)
  {
    while (t.getCause() != null) {
      t = t.getCause();
    }
    return t;
  }
  





  public static Throwable throwRootCauseIfIOE(Throwable t)
    throws IOException
  {
    return throwIfIOE(getRootCause(t));
  }
  



  public static void throwAsIAE(Throwable t)
  {
    throwAsIAE(t, t.getMessage());
  }
  





  public static void throwAsIAE(Throwable t, String msg)
  {
    throwIfRTE(t);
    throwIfError(t);
    throw new IllegalArgumentException(msg, t);
  }
  


  public static <T> T throwAsMappingException(com.fasterxml.jackson.databind.DeserializationContext ctxt, IOException e0)
    throws com.fasterxml.jackson.databind.JsonMappingException
  {
    if ((e0 instanceof com.fasterxml.jackson.databind.JsonMappingException)) {
      throw ((com.fasterxml.jackson.databind.JsonMappingException)e0);
    }
    com.fasterxml.jackson.databind.JsonMappingException e = com.fasterxml.jackson.databind.JsonMappingException.from(ctxt, e0.getMessage());
    e.initCause(e0);
    throw e;
  }
  





  public static void unwrapAndThrowAsIAE(Throwable t)
  {
    throwAsIAE(getRootCause(t));
  }
  





  public static void unwrapAndThrowAsIAE(Throwable t, String msg)
  {
    throwAsIAE(getRootCause(t), msg);
  }
  












  public static void closeOnFailAndThrowAsIOE(JsonGenerator g, Exception fail)
    throws IOException
  {
    g.disable(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
    try {
      g.close();
    } catch (Exception e) {
      fail.addSuppressed(e);
    }
    throwIfIOE(fail);
    throwIfRTE(fail);
    throw new RuntimeException(fail);
  }
  










  public static void closeOnFailAndThrowAsIOE(JsonGenerator g, java.io.Closeable toClose, Exception fail)
    throws IOException
  {
    if (g != null) {
      g.disable(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
      try {
        g.close();
      } catch (Exception e) {
        fail.addSuppressed(e);
      }
    }
    if (toClose != null) {
      try {
        toClose.close();
      } catch (Exception e) {
        fail.addSuppressed(e);
      }
    }
    throwIfIOE(fail);
    throwIfRTE(fail);
    throw new RuntimeException(fail);
  }
  



















  public static <T> T createInstance(Class<T> cls, boolean canFixAccess)
    throws IllegalArgumentException
  {
    Constructor<T> ctor = findConstructor(cls, canFixAccess);
    if (ctor == null) {
      throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
    }
    try {
      return ctor.newInstance(new Object[0]);
    } catch (Exception e) {
      unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage()); }
    return null;
  }
  
  public static <T> Constructor<T> findConstructor(Class<T> cls, boolean forceAccess)
    throws IllegalArgumentException
  {
    try
    {
      Constructor<T> ctor = cls.getDeclaredConstructor(new Class[0]);
      if (forceAccess) {
        checkAndFixAccess(ctor, forceAccess);

      }
      else if (!Modifier.isPublic(ctor.getModifiers())) {
        throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: cannot instantiate type");
      }
      
      return ctor;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}catch (Exception e)
    {
      unwrapAndThrowAsIAE(e, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e.getMessage());
    }
    return null;
  }
  








  public static Class<?> classOf(Object inst)
  {
    if (inst == null) {
      return null;
    }
    return inst.getClass();
  }
  


  public static Class<?> rawClass(JavaType t)
  {
    if (t == null) {
      return null;
    }
    return t.getRawClass();
  }
  


  public static <T> T nonNull(T valueOrNull, T defaultValue)
  {
    return valueOrNull == null ? defaultValue : valueOrNull;
  }
  


  public static String nullOrToString(Object value)
  {
    if (value == null) {
      return null;
    }
    return value.toString();
  }
  


  public static String nonNullString(String str)
  {
    if (str == null) {
      return "";
    }
    return str;
  }
  





  public static String quotedOr(Object str, String forNull)
  {
    if (str == null) {
      return forNull;
    }
    return String.format("\"%s\"", new Object[] { str });
  }
  











  public static String getClassDescription(Object classOrInstance)
  {
    if (classOrInstance == null) {
      return "unknown";
    }
    
    Class<?> cls = (classOrInstance instanceof Class) ? (Class)classOrInstance : classOrInstance.getClass();
    return nameOf(cls);
  }
  











  public static String getTypeDescription(JavaType fullType)
  {
    if (fullType == null) {
      return "[null]";
    }
    StringBuilder sb = new StringBuilder(80).append('`');
    sb.append(fullType.toCanonical());
    return '`';
  }
  






  public static String classNameOf(Object inst)
  {
    if (inst == null) {
      return "[null]";
    }
    Class<?> raw = (inst instanceof Class) ? (Class)inst : inst.getClass();
    return nameOf(raw);
  }
  





  public static String nameOf(Class<?> cls)
  {
    if (cls == null) {
      return "[null]";
    }
    int index = 0;
    while (cls.isArray()) {
      index++;
      cls = cls.getComponentType();
    }
    String base = cls.isPrimitive() ? cls.getSimpleName() : cls.getName();
    if (index > 0) {
      StringBuilder sb = new StringBuilder(base);
      do {
        sb.append("[]");
        index--; } while (index > 0);
      base = sb.toString();
    }
    return backticked(base);
  }
  





  public static String nameOf(Named named)
  {
    if (named == null) {
      return "[null]";
    }
    return backticked(named.getName());
  }
  










  public static String backticked(String text)
  {
    if (text == null) {
      return "[null]";
    }
    return text.length() + 2 + '`' + text + '`';
  }
  








  public static String exceptionMessage(Throwable t)
  {
    if ((t instanceof com.fasterxml.jackson.core.JsonProcessingException)) {
      return ((com.fasterxml.jackson.core.JsonProcessingException)t).getOriginalMessage();
    }
    return t.getMessage();
  }
  










  public static Object defaultValue(Class<?> cls)
  {
    if (cls == Integer.TYPE) {
      return Integer.valueOf(0);
    }
    if (cls == Long.TYPE) {
      return Long.valueOf(0L);
    }
    if (cls == Boolean.TYPE) {
      return Boolean.FALSE;
    }
    if (cls == Double.TYPE) {
      return Double.valueOf(0.0D);
    }
    if (cls == Float.TYPE) {
      return Float.valueOf(0.0F);
    }
    if (cls == Byte.TYPE) {
      return Byte.valueOf((byte)0);
    }
    if (cls == Short.TYPE) {
      return Short.valueOf((short)0);
    }
    if (cls == Character.TYPE) {
      return Character.valueOf('\000');
    }
    throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
  }
  




  public static Class<?> wrapperType(Class<?> primitiveType)
  {
    if (primitiveType == Integer.TYPE) {
      return Integer.class;
    }
    if (primitiveType == Long.TYPE) {
      return Long.class;
    }
    if (primitiveType == Boolean.TYPE) {
      return Boolean.class;
    }
    if (primitiveType == Double.TYPE) {
      return Double.class;
    }
    if (primitiveType == Float.TYPE) {
      return Float.class;
    }
    if (primitiveType == Byte.TYPE) {
      return Byte.class;
    }
    if (primitiveType == Short.TYPE) {
      return Short.class;
    }
    if (primitiveType == Character.TYPE) {
      return Character.class;
    }
    throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
  }
  






  public static Class<?> primitiveType(Class<?> type)
  {
    if (type.isPrimitive()) {
      return type;
    }
    
    if (type == Integer.class) {
      return Integer.TYPE;
    }
    if (type == Long.class) {
      return Long.TYPE;
    }
    if (type == Boolean.class) {
      return Boolean.TYPE;
    }
    if (type == Double.class) {
      return Double.TYPE;
    }
    if (type == Float.class) {
      return Float.TYPE;
    }
    if (type == Byte.class) {
      return Byte.TYPE;
    }
    if (type == Short.class) {
      return Short.TYPE;
    }
    if (type == Character.class) {
      return Character.TYPE;
    }
    return null;
  }
  













  @Deprecated
  public static void checkAndFixAccess(Member member)
  {
    checkAndFixAccess(member, false);
  }
  












  public static void checkAndFixAccess(Member member, boolean force)
  {
    java.lang.reflect.AccessibleObject ao = (java.lang.reflect.AccessibleObject)member;
    



    try
    {
      if ((force) || 
        (!Modifier.isPublic(member.getModifiers())) || 
        (!Modifier.isPublic(member.getDeclaringClass().getModifiers()))) {
        ao.setAccessible(true);
      }
    }
    catch (SecurityException se)
    {
      if (!ao.isAccessible()) {
        Class<?> declClass = member.getDeclaringClass();
        throw new IllegalArgumentException("Cannot access " + member + " (from class " + declClass.getName() + "; failed to set access: " + se.getMessage());
      }
    }
  }
  











  public static boolean isEnumType(Class<?> rawType)
  {
    return Enum.class.isAssignableFrom(rawType);
  }
  







  public static Class<? extends Enum<?>> findEnumType(EnumSet<?> s)
  {
    if (!s.isEmpty()) {
      return findEnumType((Enum)s.iterator().next());
    }
    
    return EnumTypeLocator.instance.enumTypeFor(s);
  }
  






  public static Class<? extends Enum<?>> findEnumType(java.util.EnumMap<?, ?> m)
  {
    if (!m.isEmpty()) {
      return findEnumType((Enum)m.keySet().iterator().next());
    }
    
    return EnumTypeLocator.instance.enumTypeFor(m);
  }
  








  public static Class<? extends Enum<?>> findEnumType(Enum<?> en)
  {
    Class<?> ec = en.getClass();
    if (ec.getSuperclass() != Enum.class) {
      ec = ec.getSuperclass();
    }
    return ec;
  }
  








  public static Class<? extends Enum<?>> findEnumType(Class<?> cls)
  {
    if (cls.getSuperclass() != Enum.class) {
      cls = cls.getSuperclass();
    }
    return cls;
  }
  











  public static <T extends Annotation> Enum<?> findFirstAnnotatedEnumValue(Class<Enum<?>> enumClass, Class<T> annotationClass)
  {
    Field[] fields = getDeclaredFields(enumClass);
    for (Field field : fields) {
      if (field.isEnumConstant()) {
        Annotation defaultValueAnnotation = field.getAnnotation(annotationClass);
        if (defaultValueAnnotation != null) {
          String name = field.getName();
          for (Enum<?> enumValue : (Enum[])enumClass.getEnumConstants()) {
            if (name.equals(enumValue.name())) {
              return enumValue;
            }
          }
        }
      }
    }
    return null;
  }
  















  public static boolean isJacksonStdImpl(Object impl)
  {
    return (impl == null) || (isJacksonStdImpl(impl.getClass()));
  }
  
  public static boolean isJacksonStdImpl(Class<?> implClass) {
    return implClass.getAnnotation(com.fasterxml.jackson.databind.annotation.JacksonStdImpl.class) != null;
  }
  











  public static String getPackageName(Class<?> cls)
  {
    Package pkg = cls.getPackage();
    return pkg == null ? null : pkg.getName();
  }
  


  public static boolean hasEnclosingMethod(Class<?> cls)
  {
    return (!isObjectOrPrimitive(cls)) && (cls.getEnclosingMethod() != null);
  }
  


  public static Field[] getDeclaredFields(Class<?> cls)
  {
    return cls.getDeclaredFields();
  }
  


  public static Method[] getDeclaredMethods(Class<?> cls)
  {
    return cls.getDeclaredMethods();
  }
  


  public static Annotation[] findClassAnnotations(Class<?> cls)
  {
    if (isObjectOrPrimitive(cls)) {
      return NO_ANNOTATIONS;
    }
    return cls.getDeclaredAnnotations();
  }
  






  public static Method[] getClassMethods(Class<?> cls)
  {
    try
    {
      return getDeclaredMethods(cls);
    }
    catch (NoClassDefFoundError ex)
    {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      if (loader == null)
      {
        throw ex;
      }
      try
      {
        contextClass = loader.loadClass(cls.getName());
      } catch (ClassNotFoundException e) { Class<?> contextClass;
        ex.addSuppressed(e);
        throw ex; }
      Class<?> contextClass;
      return contextClass.getDeclaredMethods();
    }
  }
  




  public static Ctor[] getConstructors(Class<?> cls)
  {
    if ((cls.isInterface()) || (isObjectOrPrimitive(cls))) {
      return NO_CTORS;
    }
    Constructor<?>[] rawCtors = cls.getDeclaredConstructors();
    int len = rawCtors.length;
    Ctor[] result = new Ctor[len];
    for (int i = 0; i < len; i++) {
      result[i] = new Ctor(rawCtors[i]);
    }
    return result;
  }
  





  public static Class<?> getDeclaringClass(Class<?> cls)
  {
    return isObjectOrPrimitive(cls) ? null : cls.getDeclaringClass();
  }
  


  public static java.lang.reflect.Type getGenericSuperclass(Class<?> cls)
  {
    return cls.getGenericSuperclass();
  }
  


  public static java.lang.reflect.Type[] getGenericInterfaces(Class<?> cls)
  {
    return cls.getGenericInterfaces();
  }
  



  public static Class<?> getEnclosingClass(Class<?> cls)
  {
    return isObjectOrPrimitive(cls) ? null : cls.getEnclosingClass();
  }
  
  private static Class<?>[] _interfaces(Class<?> cls) {
    return cls.getInterfaces();
  }
  










  private static class EnumTypeLocator
  {
    static final EnumTypeLocator instance = new EnumTypeLocator();
    
    private final Field enumSetTypeField;
    
    private final Field enumMapTypeField;
    
    private EnumTypeLocator()
    {
      enumSetTypeField = locateField(EnumSet.class, "elementType", Class.class);
      enumMapTypeField = locateField(java.util.EnumMap.class, "elementType", Class.class);
    }
    

    public Class<? extends Enum<?>> enumTypeFor(EnumSet<?> set)
    {
      if (enumSetTypeField != null) {
        return (Class)get(set, enumSetTypeField);
      }
      throw new IllegalStateException("Cannot figure out type for EnumSet (odd JDK platform?)");
    }
    

    public Class<? extends Enum<?>> enumTypeFor(java.util.EnumMap<?, ?> set)
    {
      if (enumMapTypeField != null) {
        return (Class)get(set, enumMapTypeField);
      }
      throw new IllegalStateException("Cannot figure out type for EnumMap (odd JDK platform?)");
    }
    
    private Object get(Object bean, Field field)
    {
      try {
        return field.get(bean);
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
    
    private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type)
    {
      Field found = null;
      
      Field[] fields = ClassUtil.getDeclaredFields(fromClass);
      for (Field f : fields) {
        if ((expectedName.equals(f.getName())) && (f.getType() == type)) {
          found = f;
          break;
        }
      }
      
      if (found == null) {
        for (Field f : fields) {
          if (f.getType() == type)
          {
            if (found != null) return null;
            found = f;
          }
        }
      }
      if (found != null) {
        try {
          found.setAccessible(true);
        } catch (Throwable localThrowable1) {}
      }
      return found;
    }
  }
  




  public static final class Ctor
  {
    public final Constructor<?> _ctor;
    



    private Annotation[] _annotations;
    



    private Annotation[][] _paramAnnotations;
    


    private int _paramCount = -1;
    
    public Ctor(Constructor<?> ctor) {
      _ctor = ctor;
    }
    
    public Constructor<?> getConstructor() {
      return _ctor;
    }
    
    public int getParamCount() {
      int c = _paramCount;
      if (c < 0) {
        c = _ctor.getParameterTypes().length;
        _paramCount = c;
      }
      return c;
    }
    
    public Class<?> getDeclaringClass() {
      return _ctor.getDeclaringClass();
    }
    
    public Annotation[] getDeclaredAnnotations() {
      Annotation[] result = _annotations;
      if (result == null) {
        result = _ctor.getDeclaredAnnotations();
        _annotations = result;
      }
      return result;
    }
    
    public Annotation[][] getParameterAnnotations() {
      Annotation[][] result = _paramAnnotations;
      if (result == null) {
        result = _ctor.getParameterAnnotations();
        _paramAnnotations = result;
      }
      return result;
    }
  }
}
