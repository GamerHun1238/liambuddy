package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.TimeZone;


























































































































@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonFormat
{
  public static final String DEFAULT_LOCALE = "##default";
  public static final String DEFAULT_TIMEZONE = "##default";
  
  String pattern() default "";
  
  Shape shape() default Shape.ANY;
  
  String locale() default "##default";
  
  String timezone() default "##default";
  
  OptBoolean lenient() default OptBoolean.DEFAULT;
  
  Feature[] with() default {};
  
  Feature[] without() default {};
  
  public static enum Shape
  {
    ANY, 
    









    NATURAL, 
    




    SCALAR, 
    



    ARRAY, 
    



    OBJECT, 
    





    NUMBER, 
    



    NUMBER_FLOAT, 
    




    NUMBER_INT, 
    



    STRING, 
    




    BOOLEAN, 
    






    BINARY;
    
    private Shape() {}
    
    public boolean isNumeric() { return (this == NUMBER) || (this == NUMBER_INT) || (this == NUMBER_FLOAT); }
    
    public boolean isStructured()
    {
      return (this == OBJECT) || (this == ARRAY);
    }
  }
  

















  public static enum Feature
  {
    ACCEPT_SINGLE_VALUE_AS_ARRAY, 
    









    ACCEPT_CASE_INSENSITIVE_PROPERTIES, 
    








    ACCEPT_CASE_INSENSITIVE_VALUES, 
    




    WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, 
    




    WRITE_DATES_WITH_ZONE_ID, 
    





    WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, 
    





    WRITE_SORTED_MAP_ENTRIES, 
    













    ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
    

    private Feature() {}
  }
  

  public static class Features
  {
    private final int _enabled;
    
    private final int _disabled;
    
    private static final Features EMPTY = new Features(0, 0);
    
    private Features(int e, int d) {
      _enabled = e;
      _disabled = d;
    }
    
    public static Features empty() {
      return EMPTY;
    }
    
    public static Features construct(JsonFormat f) {
      return construct(f.with(), f.without());
    }
    
    public static Features construct(JsonFormat.Feature[] enabled, JsonFormat.Feature[] disabled)
    {
      int e = 0;
      JsonFormat.Feature[] arrayOfFeature1 = enabled;int i = arrayOfFeature1.length; for (JsonFormat.Feature localFeature1 = 0; localFeature1 < i; localFeature1++) { f = arrayOfFeature1[localFeature1];
        e |= 1 << f.ordinal();
      }
      int d = 0;
      JsonFormat.Feature[] arrayOfFeature2 = disabled;localFeature1 = arrayOfFeature2.length; for (JsonFormat.Feature f = 0; f < localFeature1; f++) { JsonFormat.Feature f = arrayOfFeature2[f];
        d |= 1 << f.ordinal();
      }
      return new Features(e, d);
    }
    
    public Features withOverrides(Features overrides)
    {
      if (overrides == null) {
        return this;
      }
      int overrideD = _disabled;
      int overrideE = _enabled;
      if ((overrideD == 0) && (overrideE == 0)) {
        return this;
      }
      if ((_enabled == 0) && (_disabled == 0)) {
        return overrides;
      }
      
      int newE = _enabled & (overrideD ^ 0xFFFFFFFF) | overrideE;
      int newD = _disabled & (overrideE ^ 0xFFFFFFFF) | overrideD;
      

      if ((newE == _enabled) && (newD == _disabled)) {
        return this;
      }
      
      return new Features(newE, newD);
    }
    
    public Features with(JsonFormat.Feature... features) {
      int e = _enabled;
      for (JsonFormat.Feature f : features) {
        e |= 1 << f.ordinal();
      }
      return e == _enabled ? this : new Features(e, _disabled);
    }
    
    public Features without(JsonFormat.Feature... features) {
      int d = _disabled;
      for (JsonFormat.Feature f : features) {
        d |= 1 << f.ordinal();
      }
      return d == _disabled ? this : new Features(_enabled, d);
    }
    
    public Boolean get(JsonFormat.Feature f) {
      int mask = 1 << f.ordinal();
      if ((_disabled & mask) != 0) {
        return Boolean.FALSE;
      }
      if ((_enabled & mask) != 0) {
        return Boolean.TRUE;
      }
      return null;
    }
    
    public String toString()
    {
      if (this == EMPTY) {
        return "EMPTY";
      }
      return String.format("(enabled=0x%x,disabled=0x%x)", new Object[] { Integer.valueOf(_enabled), Integer.valueOf(_disabled) });
    }
    
    public int hashCode()
    {
      return _disabled + _enabled;
    }
    
    public boolean equals(Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() != getClass()) return false;
      Features other = (Features)o;
      return (_enabled == _enabled) && (_disabled == _disabled);
    }
  }
  



  public static class Value
    implements JacksonAnnotationValue<JsonFormat>, Serializable
  {
    private static final long serialVersionUID = 1L;
    


    private static final Value EMPTY = new Value();
    

    private final String _pattern;
    

    private final JsonFormat.Shape _shape;
    

    private final Locale _locale;
    
    private final String _timezoneStr;
    
    private final Boolean _lenient;
    
    private final JsonFormat.Features _features;
    
    private transient TimeZone _timezone;
    

    public Value()
    {
      this("", JsonFormat.Shape.ANY, "", "", JsonFormat.Features.empty(), null);
    }
    
    public Value(JsonFormat ann) {
      this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), 
        JsonFormat.Features.construct(ann), ann.lenient().asBoolean());
    }
    




    public Value(String p, JsonFormat.Shape sh, String localeStr, String tzStr, JsonFormat.Features f, Boolean lenient)
    {
      this(p, sh, (localeStr == null) || 
        (localeStr.length() == 0) || ("##default".equals(localeStr)) ? null : new Locale(localeStr), (tzStr == null) || 
        
        (tzStr.length() == 0) || ("##default".equals(tzStr)) ? null : tzStr, null, f, lenient);
    }
    






    public Value(String p, JsonFormat.Shape sh, Locale l, TimeZone tz, JsonFormat.Features f, Boolean lenient)
    {
      _pattern = p;
      _shape = (sh == null ? JsonFormat.Shape.ANY : sh);
      _locale = l;
      _timezone = tz;
      _timezoneStr = null;
      _features = (f == null ? JsonFormat.Features.empty() : f);
      _lenient = lenient;
    }
    




    public Value(String p, JsonFormat.Shape sh, Locale l, String tzStr, TimeZone tz, JsonFormat.Features f, Boolean lenient)
    {
      _pattern = p;
      _shape = (sh == null ? JsonFormat.Shape.ANY : sh);
      _locale = l;
      _timezone = tz;
      _timezoneStr = tzStr;
      _features = (f == null ? JsonFormat.Features.empty() : f);
      _lenient = lenient;
    }
    
    @Deprecated
    public Value(String p, JsonFormat.Shape sh, Locale l, String tzStr, TimeZone tz, JsonFormat.Features f) {
      this(p, sh, l, tzStr, tz, f, null);
    }
    
    @Deprecated
    public Value(String p, JsonFormat.Shape sh, String localeStr, String tzStr, JsonFormat.Features f) {
      this(p, sh, localeStr, tzStr, f, null);
    }
    
    @Deprecated
    public Value(String p, JsonFormat.Shape sh, Locale l, TimeZone tz, JsonFormat.Features f) { this(p, sh, l, tz, f, null); }
    



    public static final Value empty()
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
    


    public static final Value from(JsonFormat ann)
    {
      return ann == null ? EMPTY : new Value(ann);
    }
    


    public final Value withOverrides(Value overrides)
    {
      if ((overrides == null) || (overrides == EMPTY) || (overrides == this)) {
        return this;
      }
      if (this == EMPTY) {
        return overrides;
      }
      String p = _pattern;
      if ((p == null) || (p.isEmpty())) {
        p = _pattern;
      }
      JsonFormat.Shape sh = _shape;
      if (sh == JsonFormat.Shape.ANY) {
        sh = _shape;
      }
      Locale l = _locale;
      if (l == null) {
        l = _locale;
      }
      JsonFormat.Features f = _features;
      if (f == null) {
        f = _features;
      } else {
        f = f.withOverrides(_features);
      }
      Boolean lenient = _lenient;
      if (lenient == null) {
        lenient = _lenient;
      }
      

      String tzStr = _timezoneStr;
      TimeZone tz;
      TimeZone tz;
      if ((tzStr == null) || (tzStr.isEmpty())) {
        tzStr = _timezoneStr;
        tz = _timezone;
      } else {
        tz = _timezone;
      }
      return new Value(p, sh, l, tzStr, tz, f, lenient);
    }
    


    public static Value forPattern(String p)
    {
      return new Value(p, null, null, null, null, JsonFormat.Features.empty(), null);
    }
    


    public static Value forShape(JsonFormat.Shape sh)
    {
      return new Value(null, sh, null, null, null, JsonFormat.Features.empty(), null);
    }
    


    public static Value forLeniency(boolean lenient)
    {
      return new Value(null, null, null, null, null, JsonFormat.Features.empty(), 
        Boolean.valueOf(lenient));
    }
    


    public Value withPattern(String p)
    {
      return new Value(p, _shape, _locale, _timezoneStr, _timezone, _features, _lenient);
    }
    



    public Value withShape(JsonFormat.Shape s)
    {
      if (s == _shape) {
        return this;
      }
      return new Value(_pattern, s, _locale, _timezoneStr, _timezone, _features, _lenient);
    }
    



    public Value withLocale(Locale l)
    {
      return new Value(_pattern, _shape, l, _timezoneStr, _timezone, _features, _lenient);
    }
    



    public Value withTimeZone(TimeZone tz)
    {
      return new Value(_pattern, _shape, _locale, null, tz, _features, _lenient);
    }
    



    public Value withLenient(Boolean lenient)
    {
      if (lenient == _lenient) {
        return this;
      }
      return new Value(_pattern, _shape, _locale, _timezoneStr, _timezone, _features, lenient);
    }
    



    public Value withFeature(JsonFormat.Feature f)
    {
      JsonFormat.Features newFeats = _features.with(new JsonFormat.Feature[] { f });
      return newFeats == _features ? this : new Value(_pattern, _shape, _locale, _timezoneStr, _timezone, newFeats, _lenient);
    }
    




    public Value withoutFeature(JsonFormat.Feature f)
    {
      JsonFormat.Features newFeats = _features.without(new JsonFormat.Feature[] { f });
      return newFeats == _features ? this : new Value(_pattern, _shape, _locale, _timezoneStr, _timezone, newFeats, _lenient);
    }
    


    public Class<JsonFormat> valueFor()
    {
      return JsonFormat.class;
    }
    
    public String getPattern() { return _pattern; }
    public JsonFormat.Shape getShape() { return _shape; }
    public Locale getLocale() { return _locale; }
    






    public Boolean getLenient()
    {
      return _lenient;
    }
    









    public boolean isLenient()
    {
      return Boolean.TRUE.equals(_lenient);
    }
    






    public String timeZoneAsString()
    {
      if (_timezone != null) {
        return _timezone.getID();
      }
      return _timezoneStr;
    }
    
    public TimeZone getTimeZone() {
      TimeZone tz = _timezone;
      if (tz == null) {
        if (_timezoneStr == null) {
          return null;
        }
        tz = TimeZone.getTimeZone(_timezoneStr);
        _timezone = tz;
      }
      return tz;
    }
    

    public boolean hasShape()
    {
      return _shape != JsonFormat.Shape.ANY;
    }
    

    public boolean hasPattern()
    {
      return (_pattern != null) && (_pattern.length() > 0);
    }
    

    public boolean hasLocale()
    {
      return _locale != null;
    }
    

    public boolean hasTimeZone()
    {
      return (_timezone != null) || ((_timezoneStr != null) && (!_timezoneStr.isEmpty()));
    }
    






    public boolean hasLenient()
    {
      return _lenient != null;
    }
    








    public Boolean getFeature(JsonFormat.Feature f)
    {
      return _features.get(f);
    }
    




    public JsonFormat.Features getFeatures()
    {
      return _features;
    }
    
    public String toString()
    {
      return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s,features=%s)", new Object[] { _pattern, _shape, _lenient, _locale, _timezoneStr, _features });
    }
    

    public int hashCode()
    {
      int hash = _timezoneStr == null ? 1 : _timezoneStr.hashCode();
      if (_pattern != null) {
        hash ^= _pattern.hashCode();
      }
      hash += _shape.hashCode();
      if (_lenient != null) {
        hash ^= _lenient.hashCode();
      }
      if (_locale != null) {
        hash += _locale.hashCode();
      }
      hash ^= _features.hashCode();
      return hash;
    }
    
    public boolean equals(Object o)
    {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() != getClass()) return false;
      Value other = (Value)o;
      
      if ((_shape != _shape) || 
        (!_features.equals(_features))) {
        return false;
      }
      return (_equal(_lenient, _lenient)) && 
        (_equal(_timezoneStr, _timezoneStr)) && 
        (_equal(_pattern, _pattern)) && 
        (_equal(_timezone, _timezone)) && 
        (_equal(_locale, _locale));
    }
    
    private static <T> boolean _equal(T value1, T value2)
    {
      if (value1 == null) {
        return value2 == null;
      }
      if (value2 == null) {
        return false;
      }
      return value1.equals(value2);
    }
  }
}
