package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


























































































@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIgnoreProperties
{
  String[] value() default {};
  
  boolean ignoreUnknown() default false;
  
  boolean allowGetters() default false;
  
  boolean allowSetters() default false;
  
  public static class Value
    implements JacksonAnnotationValue<JsonIgnoreProperties>, Serializable
  {
    private static final long serialVersionUID = 1L;
    protected static final Value EMPTY = new Value(Collections.emptySet(), false, false, false, true);
    

    protected final Set<String> _ignored;
    

    protected final boolean _ignoreUnknown;
    

    protected final boolean _allowGetters;
    
    protected final boolean _allowSetters;
    
    protected final boolean _merge;
    

    protected Value(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge)
    {
      if (ignored == null) {
        _ignored = Collections.emptySet();
      } else {
        _ignored = ignored;
      }
      _ignoreUnknown = ignoreUnknown;
      _allowGetters = allowGetters;
      _allowSetters = allowSetters;
      _merge = merge;
    }
    
    public static Value from(JsonIgnoreProperties src) {
      if (src == null) {
        return EMPTY;
      }
      return construct(_asSet(src.value()), src
        .ignoreUnknown(), src.allowGetters(), src.allowSetters(), false);
    }
    












    public static Value construct(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge)
    {
      if (_empty(ignored, ignoreUnknown, allowGetters, allowSetters, merge)) {
        return EMPTY;
      }
      return new Value(ignored, ignoreUnknown, allowGetters, allowSetters, merge);
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
    
    public static Value forIgnoredProperties(Set<String> propNames) {
      return EMPTY.withIgnored(propNames);
    }
    
    public static Value forIgnoredProperties(String... propNames) {
      if (propNames.length == 0) {
        return EMPTY;
      }
      return EMPTY.withIgnored(_asSet(propNames));
    }
    
    public static Value forIgnoreUnknown(boolean state) {
      return state ? EMPTY.withIgnoreUnknown() : EMPTY
        .withoutIgnoreUnknown();
    }
    





    public Value withOverrides(Value overrides)
    {
      if ((overrides == null) || (overrides == EMPTY)) {
        return this;
      }
      

      if (!_merge) {
        return overrides;
      }
      if (_equals(this, overrides)) {
        return this;
      }
      

      Set<String> ignored = _merge(_ignored, _ignored);
      boolean ignoreUnknown = (_ignoreUnknown) || (_ignoreUnknown);
      boolean allowGetters = (_allowGetters) || (_allowGetters);
      boolean allowSetters = (_allowSetters) || (_allowSetters);
      

      return construct(ignored, ignoreUnknown, allowGetters, allowSetters, true);
    }
    
    public Value withIgnored(Set<String> ignored) {
      return construct(ignored, _ignoreUnknown, _allowGetters, _allowSetters, _merge);
    }
    
    public Value withIgnored(String... ignored) {
      return construct(_asSet(ignored), _ignoreUnknown, _allowGetters, _allowSetters, _merge);
    }
    
    public Value withoutIgnored() {
      return construct(null, _ignoreUnknown, _allowGetters, _allowSetters, _merge);
    }
    
    public Value withIgnoreUnknown() {
      return _ignoreUnknown ? this : 
        construct(_ignored, true, _allowGetters, _allowSetters, _merge);
    }
    
    public Value withoutIgnoreUnknown() { return !_ignoreUnknown ? this : 
        construct(_ignored, false, _allowGetters, _allowSetters, _merge);
    }
    
    public Value withAllowGetters() {
      return _allowGetters ? this : 
        construct(_ignored, _ignoreUnknown, true, _allowSetters, _merge);
    }
    
    public Value withoutAllowGetters() { return !_allowGetters ? this : 
        construct(_ignored, _ignoreUnknown, false, _allowSetters, _merge);
    }
    
    public Value withAllowSetters() {
      return _allowSetters ? this : 
        construct(_ignored, _ignoreUnknown, _allowGetters, true, _merge);
    }
    
    public Value withoutAllowSetters() { return !_allowSetters ? this : 
        construct(_ignored, _ignoreUnknown, _allowGetters, false, _merge);
    }
    
    public Value withMerge() {
      return _merge ? this : 
        construct(_ignored, _ignoreUnknown, _allowGetters, _allowSetters, true);
    }
    
    public Value withoutMerge() {
      return !_merge ? this : 
        construct(_ignored, _ignoreUnknown, _allowGetters, _allowSetters, false);
    }
    
    public Class<JsonIgnoreProperties> valueFor()
    {
      return JsonIgnoreProperties.class;
    }
    
    protected Object readResolve()
    {
      if (_empty(_ignored, _ignoreUnknown, _allowGetters, _allowSetters, _merge)) {
        return EMPTY;
      }
      return this;
    }
    
    public Set<String> getIgnored() {
      return _ignored;
    }
    






    public Set<String> findIgnoredForSerialization()
    {
      if (_allowGetters) {
        return Collections.emptySet();
      }
      return _ignored;
    }
    






    public Set<String> findIgnoredForDeserialization()
    {
      if (_allowSetters) {
        return Collections.emptySet();
      }
      return _ignored;
    }
    
    public boolean getIgnoreUnknown() {
      return _ignoreUnknown;
    }
    
    public boolean getAllowGetters() {
      return _allowGetters;
    }
    
    public boolean getAllowSetters() {
      return _allowSetters;
    }
    
    public boolean getMerge() {
      return _merge;
    }
    
    public String toString()
    {
      return String.format("JsonIgnoreProperties.Value(ignored=%s,ignoreUnknown=%s,allowGetters=%s,allowSetters=%s,merge=%s)", new Object[] { _ignored, 
        Boolean.valueOf(_ignoreUnknown), Boolean.valueOf(_allowGetters), Boolean.valueOf(_allowSetters), Boolean.valueOf(_merge) });
    }
    
    public int hashCode()
    {
      return _ignored.size() + (_ignoreUnknown ? 1 : -3) + (_allowGetters ? 3 : -7) + (_allowSetters ? 7 : -11) + (_merge ? 11 : -13);
    }
    





    public boolean equals(Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      return (o.getClass() == getClass()) && 
        (_equals(this, (Value)o));
    }
    
    private static boolean _equals(Value a, Value b)
    {
      if ((_ignoreUnknown == _ignoreUnknown) && (_merge == _merge) && (_allowGetters == _allowGetters) && (_allowSetters == _allowSetters)) {} return 
      



        _ignored.equals(_ignored);
    }
    
    private static Set<String> _asSet(String[] v)
    {
      if ((v == null) || (v.length == 0)) {
        return Collections.emptySet();
      }
      Set<String> s = new HashSet(v.length);
      for (String str : v) {
        s.add(str);
      }
      return s;
    }
    
    private static Set<String> _merge(Set<String> s1, Set<String> s2)
    {
      if (s1.isEmpty())
        return s2;
      if (s2.isEmpty()) {
        return s1;
      }
      HashSet<String> result = new HashSet(s1.size() + s2.size());
      result.addAll(s1);
      result.addAll(s2);
      return result;
    }
    

    private static boolean _empty(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge)
    {
      if ((ignoreUnknown == EMPTY_ignoreUnknown) && (allowGetters == EMPTY_allowGetters) && (allowSetters == EMPTY_allowSetters) && (merge == EMPTY_merge))
      {


        return (ignored == null) || (ignored.size() == 0);
      }
      return false;
    }
  }
}
