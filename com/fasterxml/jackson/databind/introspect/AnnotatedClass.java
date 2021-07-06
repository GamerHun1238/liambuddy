package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;


public final class AnnotatedClass
  extends Annotated
  implements TypeResolutionContext
{
  private static final Creators NO_CREATORS = new Creators(null, 
    Collections.emptyList(), 
    Collections.emptyList());
  






  protected final JavaType _type;
  






  protected final Class<?> _class;
  






  protected final TypeBindings _bindings;
  






  protected final List<JavaType> _superTypes;
  






  protected final AnnotationIntrospector _annotationIntrospector;
  






  protected final TypeFactory _typeFactory;
  






  protected final ClassIntrospector.MixInResolver _mixInResolver;
  






  protected final Class<?> _primaryMixIn;
  






  protected final Annotations _classAnnotations;
  






  protected Creators _creators;
  






  protected AnnotatedMethodMap _memberMethods;
  





  protected List<AnnotatedField> _fields;
  





  protected transient Boolean _nonStaticInnerClass;
  






  AnnotatedClass(JavaType type, Class<?> rawType, List<JavaType> superTypes, Class<?> primaryMixIn, Annotations classAnnotations, TypeBindings bindings, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, TypeFactory tf)
  {
    _type = type;
    _class = rawType;
    _superTypes = superTypes;
    _primaryMixIn = primaryMixIn;
    _classAnnotations = classAnnotations;
    _bindings = bindings;
    _annotationIntrospector = aintr;
    _mixInResolver = mir;
    _typeFactory = tf;
  }
  





  AnnotatedClass(Class<?> rawType)
  {
    _type = null;
    _class = rawType;
    _superTypes = Collections.emptyList();
    _primaryMixIn = null;
    _classAnnotations = AnnotationCollector.emptyAnnotations();
    _bindings = TypeBindings.emptyBindings();
    _annotationIntrospector = null;
    _mixInResolver = null;
    _typeFactory = null;
  }
  


  @Deprecated
  public static AnnotatedClass construct(JavaType type, MapperConfig<?> config)
  {
    return construct(type, config, config);
  }
  




  @Deprecated
  public static AnnotatedClass construct(JavaType type, MapperConfig<?> config, ClassIntrospector.MixInResolver mir)
  {
    return AnnotatedClassResolver.resolve(config, type, mir);
  }
  







  @Deprecated
  public static AnnotatedClass constructWithoutSuperTypes(Class<?> raw, MapperConfig<?> config)
  {
    return constructWithoutSuperTypes(raw, config, config);
  }
  




  @Deprecated
  public static AnnotatedClass constructWithoutSuperTypes(Class<?> raw, MapperConfig<?> config, ClassIntrospector.MixInResolver mir)
  {
    return AnnotatedClassResolver.resolveWithoutSuperTypes(config, raw, mir);
  }
  






  public JavaType resolveType(Type type)
  {
    return _typeFactory.constructType(type, _bindings);
  }
  





  public Class<?> getAnnotated()
  {
    return _class;
  }
  
  public int getModifiers() { return _class.getModifiers(); }
  
  public String getName() {
    return _class.getName();
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> acls) {
    return _classAnnotations.get(acls);
  }
  
  public boolean hasAnnotation(Class<?> acls)
  {
    return _classAnnotations.has(acls);
  }
  
  public boolean hasOneOf(Class<? extends Annotation>[] annoClasses)
  {
    return _classAnnotations.hasOneOf(annoClasses);
  }
  
  public Class<?> getRawType()
  {
    return _class;
  }
  
  @Deprecated
  public Iterable<Annotation> annotations()
  {
    if ((_classAnnotations instanceof AnnotationMap))
      return ((AnnotationMap)_classAnnotations).annotations();
    if (((_classAnnotations instanceof AnnotationCollector.OneAnnotation)) || ((_classAnnotations instanceof AnnotationCollector.TwoAnnotations)))
    {
      throw new UnsupportedOperationException("please use getAnnotations/ hasAnnotation to check for Annotations");
    }
    return Collections.emptyList();
  }
  
  public JavaType getType()
  {
    return _type;
  }
  





  public Annotations getAnnotations()
  {
    return _classAnnotations;
  }
  
  public boolean hasAnnotations() {
    return _classAnnotations.size() > 0;
  }
  
  public AnnotatedConstructor getDefaultConstructor() {
    return _creatorsdefaultConstructor;
  }
  
  public List<AnnotatedConstructor> getConstructors() {
    return _creatorsconstructors;
  }
  


  public List<AnnotatedMethod> getFactoryMethods()
  {
    return _creatorscreatorMethods;
  }
  


  @Deprecated
  public List<AnnotatedMethod> getStaticMethods()
  {
    return getFactoryMethods();
  }
  
  public Iterable<AnnotatedMethod> memberMethods() {
    return _methods();
  }
  
  public int getMemberMethodCount() {
    return _methods().size();
  }
  
  public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
    return _methods().find(name, paramTypes);
  }
  
  public int getFieldCount() {
    return _fields().size();
  }
  
  public Iterable<AnnotatedField> fields() {
    return _fields();
  }
  



  public boolean isNonStaticInnerClass()
  {
    Boolean B = _nonStaticInnerClass;
    if (B == null) {
      _nonStaticInnerClass = (B = Boolean.valueOf(ClassUtil.isNonStaticInnerClass(_class)));
    }
    return B.booleanValue();
  }
  





  private final List<AnnotatedField> _fields()
  {
    List<AnnotatedField> f = _fields;
    if (f == null)
    {
      if (_type == null) {
        f = Collections.emptyList();
      } else {
        f = AnnotatedFieldCollector.collectFields(_annotationIntrospector, this, _mixInResolver, _typeFactory, _type);
      }
      
      _fields = f;
    }
    return f;
  }
  
  private final AnnotatedMethodMap _methods() {
    AnnotatedMethodMap m = _memberMethods;
    if (m == null)
    {

      if (_type == null) {
        m = new AnnotatedMethodMap();
      } else {
        m = AnnotatedMethodCollector.collectMethods(_annotationIntrospector, this, _mixInResolver, _typeFactory, _type, _superTypes, _primaryMixIn);
      }
      


      _memberMethods = m;
    }
    return m;
  }
  
  private final Creators _creators() {
    Creators c = _creators;
    if (c == null) {
      if (_type == null) {
        c = NO_CREATORS;
      } else {
        c = AnnotatedCreatorCollector.collectCreators(_annotationIntrospector, this, _type, _primaryMixIn);
      }
      
      _creators = c;
    }
    return c;
  }
  






  public String toString()
  {
    return "[AnnotedClass " + _class.getName() + "]";
  }
  
  public int hashCode()
  {
    return _class.getName().hashCode();
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if (!ClassUtil.hasClass(o, getClass())) {
      return false;
    }
    return _class == _class;
  }
  





  public static final class Creators
  {
    public final AnnotatedConstructor defaultConstructor;
    




    public final List<AnnotatedConstructor> constructors;
    




    public final List<AnnotatedMethod> creatorMethods;
    





    public Creators(AnnotatedConstructor defCtor, List<AnnotatedConstructor> ctors, List<AnnotatedMethod> ctorMethods)
    {
      defaultConstructor = defCtor;
      constructors = ctors;
      creatorMethods = ctorMethods;
    }
  }
}
