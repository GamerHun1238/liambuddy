package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializers
{
  private static final HashSet<String> _classNames = new HashSet();
  
  static { Class<?>[] numberTypes = { Calendar.class, GregorianCalendar.class, java.sql.Date.class, java.util.Date.class, Timestamp.class };
    





    for (Class<?> cls : numberTypes) {
      _classNames.add(cls.getName());
    }
  }
  
  public static JsonDeserializer<?> find(Class<?> rawType, String clsName)
  {
    if (_classNames.contains(clsName))
    {
      if (rawType == Calendar.class) {
        return new CalendarDeserializer();
      }
      if (rawType == java.util.Date.class) {
        return DateDeserializer.instance;
      }
      if (rawType == java.sql.Date.class) {
        return new SqlDateDeserializer();
      }
      if (rawType == Timestamp.class) {
        return new TimestampDeserializer();
      }
      if (rawType == GregorianCalendar.class) {
        return new CalendarDeserializer(GregorianCalendar.class);
      }
    }
    return null;
  }
  



  public DateDeserializers() {}
  



  protected static abstract class DateBasedDeserializer<T>
    extends StdScalarDeserializer<T>
    implements ContextualDeserializer
  {
    protected final DateFormat _customFormat;
    


    protected final String _formatString;
    


    protected DateBasedDeserializer(Class<?> clz)
    {
      super();
      _customFormat = null;
      _formatString = null;
    }
    
    protected DateBasedDeserializer(DateBasedDeserializer<T> base, DateFormat format, String formatStr)
    {
      super();
      _customFormat = format;
      _formatString = formatStr;
    }
    

    protected abstract DateBasedDeserializer<T> withDateFormat(DateFormat paramDateFormat, String paramString);
    

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
      throws com.fasterxml.jackson.databind.JsonMappingException
    {
      JsonFormat.Value format = findFormatOverrides(ctxt, property, 
        handledType());
      
      if (format != null) {
        TimeZone tz = format.getTimeZone();
        Boolean lenient = format.getLenient();
        

        if (format.hasPattern()) {
          String pattern = format.getPattern();
          Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
          SimpleDateFormat df = new SimpleDateFormat(pattern, loc);
          if (tz == null) {
            tz = ctxt.getTimeZone();
          }
          df.setTimeZone(tz);
          if (lenient != null) {
            df.setLenient(lenient.booleanValue());
          }
          return withDateFormat(df, pattern);
        }
        
        if (tz != null) {
          DateFormat df = ctxt.getConfig().getDateFormat();
          
          if (df.getClass() == StdDateFormat.class) {
            Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
            StdDateFormat std = (StdDateFormat)df;
            std = std.withTimeZone(tz);
            std = std.withLocale(loc);
            if (lenient != null) {
              std = std.withLenient(lenient);
            }
            df = std;
          }
          else {
            df = (DateFormat)df.clone();
            df.setTimeZone(tz);
            if (lenient != null) {
              df.setLenient(lenient.booleanValue());
            }
          }
          return withDateFormat(df, _formatString);
        }
        
        if (lenient != null) {
          DateFormat df = ctxt.getConfig().getDateFormat();
          String pattern = _formatString;
          
          if (df.getClass() == StdDateFormat.class) {
            StdDateFormat std = (StdDateFormat)df;
            std = std.withLenient(lenient);
            df = std;
            pattern = std.toPattern();
          }
          else {
            df = (DateFormat)df.clone();
            df.setLenient(lenient.booleanValue());
            if ((df instanceof SimpleDateFormat)) {
              ((SimpleDateFormat)df).toPattern();
            }
          }
          if (pattern == null) {
            pattern = "[unknown]";
          }
          return withDateFormat(df, pattern);
        }
      }
      return this;
    }
    

    protected java.util.Date _parseDate(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      if ((_customFormat != null) && 
        (p.hasToken(com.fasterxml.jackson.core.JsonToken.VALUE_STRING))) {
        String str = p.getText().trim();
        if (str.length() == 0) {
          return (java.util.Date)getEmptyValue(ctxt);
        }
        synchronized (_customFormat) {
          try {
            return _customFormat.parse(str);
          } catch (ParseException e) {
            return (java.util.Date)ctxt.handleWeirdStringValue(handledType(), str, "expected format \"%s\"", new Object[] { _formatString });
          }
        }
      }
      

      return super._parseDate(p, ctxt);
    }
  }
  





  @JacksonStdImpl
  public static class CalendarDeserializer
    extends DateDeserializers.DateBasedDeserializer<Calendar>
  {
    protected final Constructor<Calendar> _defaultCtor;
    





    public CalendarDeserializer()
    {
      super();
      _defaultCtor = null;
    }
    
    public CalendarDeserializer(Class<? extends Calendar> cc)
    {
      super();
      _defaultCtor = com.fasterxml.jackson.databind.util.ClassUtil.findConstructor(cc, false);
    }
    
    public CalendarDeserializer(CalendarDeserializer src, DateFormat df, String formatString) {
      super(df, formatString);
      _defaultCtor = _defaultCtor;
    }
    
    protected CalendarDeserializer withDateFormat(DateFormat df, String formatString)
    {
      return new CalendarDeserializer(this, df, formatString);
    }
    
    public Calendar deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      java.util.Date d = _parseDate(p, ctxt);
      if (d == null) {
        return null;
      }
      if (_defaultCtor == null) {
        return ctxt.constructCalendar(d);
      }
      try {
        Calendar c = (Calendar)_defaultCtor.newInstance(new Object[0]);
        c.setTimeInMillis(d.getTime());
        TimeZone tz = ctxt.getTimeZone();
        if (tz != null) {
          c.setTimeZone(tz);
        }
        return c;
      } catch (Exception e) {
        return (Calendar)ctxt.handleInstantiationProblem(handledType(), d, e);
      }
    }
  }
  






  @JacksonStdImpl
  public static class DateDeserializer
    extends DateDeserializers.DateBasedDeserializer<java.util.Date>
  {
    public static final DateDeserializer instance = new DateDeserializer();
    
    public DateDeserializer() { super(); }
    
    public DateDeserializer(DateDeserializer base, DateFormat df, String formatString) { super(df, formatString); }
    

    protected DateDeserializer withDateFormat(DateFormat df, String formatString)
    {
      return new DateDeserializer(this, df, formatString);
    }
    
    public java.util.Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      return _parseDate(p, ctxt);
    }
  }
  




  public static class SqlDateDeserializer
    extends DateDeserializers.DateBasedDeserializer<java.sql.Date>
  {
    public SqlDateDeserializer() { super(); }
    
    public SqlDateDeserializer(SqlDateDeserializer src, DateFormat df, String formatString) { super(df, formatString); }
    

    protected SqlDateDeserializer withDateFormat(DateFormat df, String formatString)
    {
      return new SqlDateDeserializer(this, df, formatString);
    }
    
    public java.sql.Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
      java.util.Date d = _parseDate(p, ctxt);
      return d == null ? null : new java.sql.Date(d.getTime());
    }
  }
  






  public static class TimestampDeserializer
    extends DateDeserializers.DateBasedDeserializer<Timestamp>
  {
    public TimestampDeserializer() { super(); }
    
    public TimestampDeserializer(TimestampDeserializer src, DateFormat df, String formatString) { super(df, formatString); }
    

    protected TimestampDeserializer withDateFormat(DateFormat df, String formatString)
    {
      return new TimestampDeserializer(this, df, formatString);
    }
    
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
    {
      java.util.Date d = _parseDate(p, ctxt);
      return d == null ? null : new Timestamp(d.getTime());
    }
  }
}
