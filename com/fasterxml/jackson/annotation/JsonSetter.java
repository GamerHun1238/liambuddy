package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


















































@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSetter
{
  String value() default "";
  
  Nulls nulls() default Nulls.DEFAULT;
  
  Nulls contentNulls() default Nulls.DEFAULT;
  
  public static class Value
    implements JacksonAnnotationValue<JsonSetter>, Serializable
  {
    private static final long serialVersionUID = 1L;
    private final Nulls _nulls;
    private final Nulls _contentNulls;
    protected static final Value EMPTY = new Value(Nulls.DEFAULT, Nulls.DEFAULT);
    
    protected Value(Nulls nulls, Nulls contentNulls) {
      _nulls = nulls;
      _contentNulls = contentNulls;
    }
    
    public Class<JsonSetter> valueFor()
    {
      return JsonSetter.class;
    }
    
    protected Object readResolve()
    {
      if (_empty(_nulls, _contentNulls)) {
        return EMPTY;
      }
      return this;
    }
    
    public static Value from(JsonSetter src) {
      if (src == null) {
        return EMPTY;
      }
      return construct(src.nulls(), src.contentNulls());
    }
    






    public static Value construct(Nulls nulls, Nulls contentNulls)
    {
      if (nulls == null) {
        nulls = Nulls.DEFAULT;
      }
      if (contentNulls == null) {
        contentNulls = Nulls.DEFAULT;
      }
      if (_empty(nulls, contentNulls)) {
        return EMPTY;
      }
      return new Value(nulls, contentNulls);
    }
    






    public static Value empty()
    {
      return EMPTY;
    }
    









    public static Value merge(Value base, Value overrides)
    {
      return base == null ? overrides : base
        .withOverrides(overrides);
    }
    
    public static Value forValueNulls(Nulls nulls) {
      return construct(nulls, Nulls.DEFAULT);
    }
    
    public static Value forValueNulls(Nulls nulls, Nulls contentNulls) {
      return construct(nulls, contentNulls);
    }
    
    public static Value forContentNulls(Nulls nulls) {
      return construct(Nulls.DEFAULT, nulls);
    }
    





    public Value withOverrides(Value overrides)
    {
      if ((overrides == null) || (overrides == EMPTY)) {
        return this;
      }
      Nulls nulls = _nulls;
      Nulls contentNulls = _contentNulls;
      
      if (nulls == Nulls.DEFAULT) {
        nulls = _nulls;
      }
      if (contentNulls == Nulls.DEFAULT) {
        contentNulls = _contentNulls;
      }
      
      if ((nulls == _nulls) && (contentNulls == _contentNulls)) {
        return this;
      }
      return construct(nulls, contentNulls);
    }
    
    public Value withValueNulls(Nulls nulls) {
      if (nulls == null) {
        nulls = Nulls.DEFAULT;
      }
      if (nulls == _nulls) {
        return this;
      }
      return construct(nulls, _contentNulls);
    }
    
    public Value withValueNulls(Nulls valueNulls, Nulls contentNulls) {
      if (valueNulls == null) {
        valueNulls = Nulls.DEFAULT;
      }
      if (contentNulls == null) {
        contentNulls = Nulls.DEFAULT;
      }
      if ((valueNulls == _nulls) && (contentNulls == _contentNulls)) {
        return this;
      }
      return construct(valueNulls, contentNulls);
    }
    
    public Value withContentNulls(Nulls nulls) {
      if (nulls == null) {
        nulls = Nulls.DEFAULT;
      }
      if (nulls == _contentNulls) {
        return this;
      }
      return construct(_nulls, nulls);
    }
    
    public Nulls getValueNulls() { return _nulls; }
    public Nulls getContentNulls() { return _contentNulls; }
    



    public Nulls nonDefaultValueNulls()
    {
      return _nulls == Nulls.DEFAULT ? null : _nulls;
    }
    



    public Nulls nonDefaultContentNulls()
    {
      return _contentNulls == Nulls.DEFAULT ? null : _contentNulls;
    }
    






    public String toString()
    {
      return String.format("JsonSetter.Value(valueNulls=%s,contentNulls=%s)", new Object[] { _nulls, _contentNulls });
    }
    

    public int hashCode()
    {
      return _nulls.ordinal() + (_contentNulls.ordinal() << 2);
    }
    
    public boolean equals(Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() == getClass()) {
        Value other = (Value)o;
        return (_nulls == _nulls) && (_contentNulls == _contentNulls);
      }
      
      return false;
    }
    





    private static boolean _empty(Nulls nulls, Nulls contentNulls)
    {
      return (nulls == Nulls.DEFAULT) && (contentNulls == Nulls.DEFAULT);
    }
  }
}
