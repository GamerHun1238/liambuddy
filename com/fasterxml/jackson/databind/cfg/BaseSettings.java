package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;















public final class BaseSettings
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
  








  protected final ClassIntrospector _classIntrospector;
  








  protected final AnnotationIntrospector _annotationIntrospector;
  








  protected final PropertyNamingStrategy _propertyNamingStrategy;
  








  protected final TypeFactory _typeFactory;
  








  protected final TypeResolverBuilder<?> _typeResolverBuilder;
  







  protected final PolymorphicTypeValidator _typeValidator;
  







  protected final DateFormat _dateFormat;
  







  protected final HandlerInstantiator _handlerInstantiator;
  







  protected final Locale _locale;
  







  protected final TimeZone _timeZone;
  







  protected final Base64Variant _defaultBase64;
  








  public BaseSettings(ClassIntrospector ci, AnnotationIntrospector ai, PropertyNamingStrategy pns, TypeFactory tf, TypeResolverBuilder<?> typer, DateFormat dateFormat, HandlerInstantiator hi, Locale locale, TimeZone tz, Base64Variant defaultBase64, PolymorphicTypeValidator ptv)
  {
    _classIntrospector = ci;
    _annotationIntrospector = ai;
    _propertyNamingStrategy = pns;
    _typeFactory = tf;
    _typeResolverBuilder = typer;
    _dateFormat = dateFormat;
    _handlerInstantiator = hi;
    _locale = locale;
    _timeZone = tz;
    _defaultBase64 = defaultBase64;
    _typeValidator = ptv;
  }
  



  @Deprecated
  public BaseSettings(ClassIntrospector ci, AnnotationIntrospector ai, PropertyNamingStrategy pns, TypeFactory tf, TypeResolverBuilder<?> typer, DateFormat dateFormat, HandlerInstantiator hi, Locale locale, TimeZone tz, Base64Variant defaultBase64)
  {
    this(ci, ai, pns, tf, typer, dateFormat, hi, locale, tz, defaultBase64, null);
  }
  





  public BaseSettings copy()
  {
    return new BaseSettings(_classIntrospector.copy(), _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, _dateFormat, _handlerInstantiator, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  
















  public BaseSettings withClassIntrospector(ClassIntrospector ci)
  {
    if (_classIntrospector == ci) {
      return this;
    }
    return new BaseSettings(ci, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, _dateFormat, _handlerInstantiator, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  

  public BaseSettings withAnnotationIntrospector(AnnotationIntrospector ai)
  {
    if (_annotationIntrospector == ai) {
      return this;
    }
    return new BaseSettings(_classIntrospector, ai, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, _dateFormat, _handlerInstantiator, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  

  public BaseSettings withInsertedAnnotationIntrospector(AnnotationIntrospector ai)
  {
    return withAnnotationIntrospector(AnnotationIntrospectorPair.create(ai, _annotationIntrospector));
  }
  
  public BaseSettings withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
    return withAnnotationIntrospector(AnnotationIntrospectorPair.create(_annotationIntrospector, ai));
  }
  









  public BaseSettings withPropertyNamingStrategy(PropertyNamingStrategy pns)
  {
    if (_propertyNamingStrategy == pns) {
      return this;
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, pns, _typeFactory, _typeResolverBuilder, _dateFormat, _handlerInstantiator, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  

  public BaseSettings withTypeFactory(TypeFactory tf)
  {
    if (_typeFactory == tf) {
      return this;
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, tf, _typeResolverBuilder, _dateFormat, _handlerInstantiator, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  

  public BaseSettings withTypeResolverBuilder(TypeResolverBuilder<?> typer)
  {
    if (_typeResolverBuilder == typer) {
      return this;
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, typer, _dateFormat, _handlerInstantiator, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  

  public BaseSettings withDateFormat(DateFormat df)
  {
    if (_dateFormat == df) {
      return this;
    }
    

    if ((df != null) && (hasExplicitTimeZone())) {
      df = _force(df, _timeZone);
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, df, _handlerInstantiator, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  

  public BaseSettings withHandlerInstantiator(HandlerInstantiator hi)
  {
    if (_handlerInstantiator == hi) {
      return this;
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, _dateFormat, hi, _locale, _timeZone, _defaultBase64, _typeValidator);
  }
  

  public BaseSettings with(Locale l)
  {
    if (_locale == l) {
      return this;
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, _dateFormat, _handlerInstantiator, l, _timeZone, _defaultBase64, _typeValidator);
  }
  







  public BaseSettings with(TimeZone tz)
  {
    if (tz == null) {
      throw new IllegalArgumentException();
    }
    if (tz == _timeZone) {
      return this;
    }
    
    DateFormat df = _force(_dateFormat, tz);
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, df, _handlerInstantiator, _locale, tz, _defaultBase64, _typeValidator);
  }
  





  public BaseSettings with(Base64Variant base64)
  {
    if (base64 == _defaultBase64) {
      return this;
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, _dateFormat, _handlerInstantiator, _locale, _timeZone, base64, _typeValidator);
  }
  





  public BaseSettings with(PolymorphicTypeValidator v)
  {
    if (v == _typeValidator) {
      return this;
    }
    return new BaseSettings(_classIntrospector, _annotationIntrospector, _propertyNamingStrategy, _typeFactory, _typeResolverBuilder, _dateFormat, _handlerInstantiator, _locale, _timeZone, _defaultBase64, v);
  }
  








  public ClassIntrospector getClassIntrospector()
  {
    return _classIntrospector;
  }
  
  public AnnotationIntrospector getAnnotationIntrospector() {
    return _annotationIntrospector;
  }
  
  public PropertyNamingStrategy getPropertyNamingStrategy() {
    return _propertyNamingStrategy;
  }
  
  public TypeFactory getTypeFactory() {
    return _typeFactory;
  }
  
  public TypeResolverBuilder<?> getTypeResolverBuilder() {
    return _typeResolverBuilder;
  }
  


  public PolymorphicTypeValidator getPolymorphicTypeValidator()
  {
    return _typeValidator;
  }
  
  public DateFormat getDateFormat() {
    return _dateFormat;
  }
  
  public HandlerInstantiator getHandlerInstantiator() {
    return _handlerInstantiator;
  }
  
  public Locale getLocale() {
    return _locale;
  }
  
  public TimeZone getTimeZone() {
    TimeZone tz = _timeZone;
    return tz == null ? DEFAULT_TIMEZONE : tz;
  }
  






  public boolean hasExplicitTimeZone()
  {
    return _timeZone != null;
  }
  
  public Base64Variant getBase64Variant() {
    return _defaultBase64;
  }
  






  private DateFormat _force(DateFormat df, TimeZone tz)
  {
    if ((df instanceof StdDateFormat)) {
      return ((StdDateFormat)df).withTimeZone(tz);
    }
    
    df = (DateFormat)df.clone();
    df.setTimeZone(tz);
    return df;
  }
}
