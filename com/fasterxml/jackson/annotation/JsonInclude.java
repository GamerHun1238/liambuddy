package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;






















































































@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonInclude
{
  Include value() default Include.ALWAYS;
  
  Include content() default Include.ALWAYS;
  
  Class<?> valueFilter() default Void.class;
  
  Class<?> contentFilter() default Void.class;
  
  public static enum Include
  {
    ALWAYS, 
    




    NON_NULL, 
    













    NON_ABSENT, 
    













































    NON_EMPTY, 
    





















    NON_DEFAULT, 
    











    CUSTOM, 
    









    USE_DEFAULTS;
    





    private Include() {}
  }
  





  public static class Value
    implements JacksonAnnotationValue<JsonInclude>, Serializable
  {
    private static final long serialVersionUID = 1L;
    



    protected static final Value EMPTY = new Value(JsonInclude.Include.USE_DEFAULTS, JsonInclude.Include.USE_DEFAULTS, null, null);
    

    protected final JsonInclude.Include _valueInclusion;
    

    protected final JsonInclude.Include _contentInclusion;
    

    protected final Class<?> _valueFilter;
    

    protected final Class<?> _contentFilter;
    

    public Value(JsonInclude src)
    {
      this(src.value(), src.content(), src
        .valueFilter(), src.contentFilter());
    }
    
    protected Value(JsonInclude.Include vi, JsonInclude.Include ci, Class<?> valueFilter, Class<?> contentFilter)
    {
      _valueInclusion = (vi == null ? JsonInclude.Include.USE_DEFAULTS : vi);
      _contentInclusion = (ci == null ? JsonInclude.Include.USE_DEFAULTS : ci);
      _valueFilter = (valueFilter == Void.class ? null : valueFilter);
      _contentFilter = (contentFilter == Void.class ? null : contentFilter);
    }
    
    public static Value empty() {
      return EMPTY;
    }
    











    public static Value merge(Value base, Value overrides)
    {
      return base == null ? overrides : base
        .withOverrides(overrides);
    }
    



    public static Value mergeAll(Value... values)
    {
      Value result = null;
      for (Value curr : values) {
        if (curr != null) {
          result = result == null ? curr : result.withOverrides(curr);
        }
      }
      return result;
    }
    
    protected Object readResolve()
    {
      if ((_valueInclusion == JsonInclude.Include.USE_DEFAULTS) && (_contentInclusion == JsonInclude.Include.USE_DEFAULTS) && (_valueFilter == null) && (_contentFilter == null))
      {



        return EMPTY;
      }
      return this;
    }
    





    public Value withOverrides(Value overrides)
    {
      if ((overrides == null) || (overrides == EMPTY)) {
        return this;
      }
      JsonInclude.Include vi = _valueInclusion;
      JsonInclude.Include ci = _contentInclusion;
      Class<?> vf = _valueFilter;
      Class<?> cf = _contentFilter;
      
      boolean viDiff = (vi != _valueInclusion) && (vi != JsonInclude.Include.USE_DEFAULTS);
      boolean ciDiff = (ci != _contentInclusion) && (ci != JsonInclude.Include.USE_DEFAULTS);
      boolean filterDiff = (vf != _valueFilter) || (cf != _valueFilter);
      
      if (viDiff) {
        if (ciDiff) {
          return new Value(vi, ci, vf, cf);
        }
        return new Value(vi, _contentInclusion, vf, cf); }
      if (ciDiff)
        return new Value(_valueInclusion, ci, vf, cf);
      if (filterDiff) {
        return new Value(_valueInclusion, _contentInclusion, vf, cf);
      }
      return this;
    }
    


    public static Value construct(JsonInclude.Include valueIncl, JsonInclude.Include contentIncl)
    {
      if (((valueIncl == JsonInclude.Include.USE_DEFAULTS) || (valueIncl == null)) && ((contentIncl == JsonInclude.Include.USE_DEFAULTS) || (contentIncl == null)))
      {
        return EMPTY;
      }
      return new Value(valueIncl, contentIncl, null, null);
    }
    






    public static Value construct(JsonInclude.Include valueIncl, JsonInclude.Include contentIncl, Class<?> valueFilter, Class<?> contentFilter)
    {
      if (valueFilter == Void.class) {
        valueFilter = null;
      }
      if (contentFilter == Void.class) {
        contentFilter = null;
      }
      if (((valueIncl == JsonInclude.Include.USE_DEFAULTS) || (valueIncl == null)) && ((contentIncl == JsonInclude.Include.USE_DEFAULTS) || (contentIncl == null)) && (valueFilter == null) && (contentFilter == null))
      {



        return EMPTY;
      }
      return new Value(valueIncl, contentIncl, valueFilter, contentFilter);
    }
    



    public static Value from(JsonInclude src)
    {
      if (src == null) {
        return EMPTY;
      }
      JsonInclude.Include vi = src.value();
      JsonInclude.Include ci = src.content();
      
      if ((vi == JsonInclude.Include.USE_DEFAULTS) && (ci == JsonInclude.Include.USE_DEFAULTS)) {
        return EMPTY;
      }
      Class<?> vf = src.valueFilter();
      if (vf == Void.class) {
        vf = null;
      }
      Class<?> cf = src.contentFilter();
      if (cf == Void.class) {
        cf = null;
      }
      return new Value(vi, ci, vf, cf);
    }
    
    public Value withValueInclusion(JsonInclude.Include incl) {
      return incl == _valueInclusion ? this : new Value(incl, _contentInclusion, _valueFilter, _contentFilter);
    }
    






    public Value withValueFilter(Class<?> filter)
    {
      JsonInclude.Include incl;
      





      if ((filter == null) || (filter == Void.class)) {
        JsonInclude.Include incl = JsonInclude.Include.USE_DEFAULTS;
        filter = null;
      } else {
        incl = JsonInclude.Include.CUSTOM;
      }
      return construct(incl, _contentInclusion, filter, _contentFilter);
    }
    






    public Value withContentFilter(Class<?> filter)
    {
      JsonInclude.Include incl;
      




      if ((filter == null) || (filter == Void.class)) {
        JsonInclude.Include incl = JsonInclude.Include.USE_DEFAULTS;
        filter = null;
      } else {
        incl = JsonInclude.Include.CUSTOM;
      }
      return construct(_valueInclusion, incl, _valueFilter, filter);
    }
    
    public Value withContentInclusion(JsonInclude.Include incl) {
      return incl == _contentInclusion ? this : new Value(_valueInclusion, incl, _valueFilter, _contentFilter);
    }
    

    public Class<JsonInclude> valueFor()
    {
      return JsonInclude.class;
    }
    
    public JsonInclude.Include getValueInclusion() {
      return _valueInclusion;
    }
    
    public JsonInclude.Include getContentInclusion() {
      return _contentInclusion;
    }
    
    public Class<?> getValueFilter() {
      return _valueFilter;
    }
    
    public Class<?> getContentFilter() {
      return _contentFilter;
    }
    
    public String toString()
    {
      StringBuilder sb = new StringBuilder(80);
      sb.append("JsonInclude.Value(value=")
        .append(_valueInclusion)
        .append(",content=")
        .append(_contentInclusion);
      if (_valueFilter != null) {
        sb.append(",valueFilter=").append(_valueFilter.getName()).append(".class");
      }
      if (_contentFilter != null) {
        sb.append(",contentFilter=").append(_contentFilter.getName()).append(".class");
      }
      return ')';
    }
    
    public int hashCode()
    {
      return 
        (_valueInclusion.hashCode() << 2) + _contentInclusion.hashCode();
    }
    
    public boolean equals(Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() != getClass()) return false;
      Value other = (Value)o;
      
      return (_valueInclusion == _valueInclusion) && (_contentInclusion == _contentInclusion) && (_valueFilter == _valueFilter) && (_contentFilter == _contentFilter);
    }
  }
}
