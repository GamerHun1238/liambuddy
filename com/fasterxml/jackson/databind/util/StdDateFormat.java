package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.io.NumberInput;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


















public class StdDateFormat
  extends DateFormat
{
  protected static final String PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d";
  protected static final Pattern PATTERN_PLAIN = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d");
  protected static final Pattern PATTERN_ISO8601;
  public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd"; protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz"; protected static final String[] ALL_FORMATS; protected static final TimeZone DEFAULT_TIMEZONE;
  static { Pattern p = null;
    try {
      p = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?(\\.\\d+)?(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?");

    }
    catch (Throwable t)
    {

      throw new RuntimeException(t);
    }
    PATTERN_ISO8601 = p;
    























    ALL_FORMATS = new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd" };
    











    DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
    

    DEFAULT_LOCALE = Locale.US;
    











    DATE_FORMAT_RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", DEFAULT_LOCALE);
    DATE_FORMAT_RFC1123.setTimeZone(DEFAULT_TIMEZONE);
    DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DEFAULT_LOCALE);
    DATE_FORMAT_ISO8601.setTimeZone(DEFAULT_TIMEZONE);
  }
  
  protected static final Locale DEFAULT_LOCALE;
  protected static final DateFormat DATE_FORMAT_RFC1123;
  protected static final DateFormat DATE_FORMAT_ISO8601;
  public static final StdDateFormat instance = new StdDateFormat();
  







  protected static final Calendar CALENDAR = new GregorianCalendar(DEFAULT_TIMEZONE, DEFAULT_LOCALE);
  




  protected transient TimeZone _timezone;
  




  protected final Locale _locale;
  




  protected Boolean _lenient;
  




  private transient Calendar _calendar;
  




  private transient DateFormat _formatRFC1123;
  




  private boolean _tzSerializedWithColon = false;
  





  public StdDateFormat()
  {
    _locale = DEFAULT_LOCALE;
  }
  
  @Deprecated
  public StdDateFormat(TimeZone tz, Locale loc) {
    _timezone = tz;
    _locale = loc;
  }
  
  protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient) {
    this(tz, loc, lenient, false);
  }
  



  protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient, boolean formatTzOffsetWithColon)
  {
    _timezone = tz;
    _locale = loc;
    _lenient = lenient;
    _tzSerializedWithColon = formatTzOffsetWithColon;
  }
  
  public static TimeZone getDefaultTimeZone() {
    return DEFAULT_TIMEZONE;
  }
  



  public StdDateFormat withTimeZone(TimeZone tz)
  {
    if (tz == null) {
      tz = DEFAULT_TIMEZONE;
    }
    if ((tz == _timezone) || (tz.equals(_timezone))) {
      return this;
    }
    return new StdDateFormat(tz, _locale, _lenient, _tzSerializedWithColon);
  }
  





  public StdDateFormat withLocale(Locale loc)
  {
    if (loc.equals(_locale)) {
      return this;
    }
    return new StdDateFormat(_timezone, loc, _lenient, _tzSerializedWithColon);
  }
  






  public StdDateFormat withLenient(Boolean b)
  {
    if (_equals(b, _lenient)) {
      return this;
    }
    return new StdDateFormat(_timezone, _locale, b, _tzSerializedWithColon);
  }
  












  public StdDateFormat withColonInTimeZone(boolean b)
  {
    if (_tzSerializedWithColon == b) {
      return this;
    }
    return new StdDateFormat(_timezone, _locale, _lenient, b);
  }
  


  public StdDateFormat clone()
  {
    return new StdDateFormat(_timezone, _locale, _lenient, _tzSerializedWithColon);
  }
  








  @Deprecated
  public static DateFormat getISO8601Format(TimeZone tz, Locale loc)
  {
    return _cloneFormat(DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", tz, loc, null);
  }
  








  @Deprecated
  public static DateFormat getRFC1123Format(TimeZone tz, Locale loc)
  {
    return _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", tz, loc, null);
  }
  







  public TimeZone getTimeZone()
  {
    return _timezone;
  }
  




  public void setTimeZone(TimeZone tz)
  {
    if (!tz.equals(_timezone)) {
      _clearFormats();
      _timezone = tz;
    }
  }
  





  public void setLenient(boolean enabled)
  {
    Boolean newValue = Boolean.valueOf(enabled);
    if (!_equals(newValue, _lenient)) {
      _lenient = newValue;
      
      _clearFormats();
    }
  }
  

  public boolean isLenient()
  {
    return (_lenient == null) || (_lenient.booleanValue());
  }
  













  public boolean isColonIncludedInTimeZone()
  {
    return _tzSerializedWithColon;
  }
  






  public Date parse(String dateStr)
    throws ParseException
  {
    dateStr = dateStr.trim();
    ParsePosition pos = new ParsePosition(0);
    Date dt = _parseDate(dateStr, pos);
    if (dt != null) {
      return dt;
    }
    StringBuilder sb = new StringBuilder();
    for (String f : ALL_FORMATS) {
      if (sb.length() > 0) {
        sb.append("\", \"");
      } else {
        sb.append('"');
      }
      sb.append(f);
    }
    sb.append('"');
    

    throw new ParseException(String.format("Cannot parse date \"%s\": not compatible with any of standard forms (%s)", new Object[] { dateStr, sb.toString() }), pos.getErrorIndex());
  }
  

  public Date parse(String dateStr, ParsePosition pos)
  {
    try
    {
      return _parseDate(dateStr, pos);
    }
    catch (ParseException localParseException) {}
    
    return null;
  }
  
  protected Date _parseDate(String dateStr, ParsePosition pos) throws ParseException
  {
    if (looksLikeISO8601(dateStr)) {
      return parseAsISO8601(dateStr, pos);
    }
    
    int i = dateStr.length();
    for (;;) { i--; if (i < 0) break;
      char ch = dateStr.charAt(i);
      if (((ch < '0') || (ch > '9')) && (
      
        (i > 0) || (ch != '-'))) {
        break;
      }
    }
    
    if (i < 0)
    {
      if ((dateStr.charAt(0) == '-') || (NumberInput.inLongRange(dateStr, false))) {
        return _parseDateFromLong(dateStr, pos);
      }
    }
    return parseAsRFC1123(dateStr, pos);
  }
  








  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition)
  {
    TimeZone tz = _timezone;
    if (tz == null) {
      tz = DEFAULT_TIMEZONE;
    }
    _format(tz, _locale, date, toAppendTo);
    return toAppendTo;
  }
  

  protected void _format(TimeZone tz, Locale loc, Date date, StringBuffer buffer)
  {
    Calendar cal = _getCalendar(tz);
    cal.setTime(date);
    
    int year = cal.get(1);
    

    if (cal.get(0) == 0) {
      _formatBCEYear(buffer, year);
    } else {
      if (year > 9999)
      {




        buffer.append('+');
      }
      pad4(buffer, year);
    }
    buffer.append('-');
    pad2(buffer, cal.get(2) + 1);
    buffer.append('-');
    pad2(buffer, cal.get(5));
    buffer.append('T');
    pad2(buffer, cal.get(11));
    buffer.append(':');
    pad2(buffer, cal.get(12));
    buffer.append(':');
    pad2(buffer, cal.get(13));
    buffer.append('.');
    pad3(buffer, cal.get(14));
    
    int offset = tz.getOffset(cal.getTimeInMillis());
    if (offset != 0) {
      int hours = Math.abs(offset / 60000 / 60);
      int minutes = Math.abs(offset / 60000 % 60);
      buffer.append(offset < 0 ? '-' : '+');
      pad2(buffer, hours);
      if (_tzSerializedWithColon) {
        buffer.append(':');
      }
      pad2(buffer, minutes);



    }
    else if (_tzSerializedWithColon) {
      buffer.append("+00:00");
    }
    else {
      buffer.append("+0000");
    }
  }
  


  protected void _formatBCEYear(StringBuffer buffer, int bceYearNoSign)
  {
    if (bceYearNoSign == 1) {
      buffer.append("+0000");
      return;
    }
    int isoYear = bceYearNoSign - 1;
    buffer.append('-');
    


    pad4(buffer, isoYear);
  }
  
  private static void pad2(StringBuffer buffer, int value) {
    int tens = value / 10;
    if (tens == 0) {
      buffer.append('0');
    } else {
      buffer.append((char)(48 + tens));
      value -= 10 * tens;
    }
    buffer.append((char)(48 + value));
  }
  
  private static void pad3(StringBuffer buffer, int value) {
    int h = value / 100;
    if (h == 0) {
      buffer.append('0');
    } else {
      buffer.append((char)(48 + h));
      value -= h * 100;
    }
    pad2(buffer, value);
  }
  
  private static void pad4(StringBuffer buffer, int value) {
    int h = value / 100;
    if (h == 0) {
      buffer.append('0').append('0');
    } else {
      if (h > 99) {
        buffer.append(h);
      } else {
        pad2(buffer, h);
      }
      value -= 100 * h;
    }
    pad2(buffer, value);
  }
  






  public String toString()
  {
    return String.format("DateFormat %s: (timezone: %s, locale: %s, lenient: %s)", new Object[] {
      getClass().getName(), _timezone, _locale, _lenient });
  }
  
  public String toPattern() {
    StringBuilder sb = new StringBuilder(100);
    sb.append("[one of: '")
      .append("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
      .append("', '")
      .append("EEE, dd MMM yyyy HH:mm:ss zzz")
      .append("' (");
    
    sb.append(Boolean.FALSE.equals(_lenient) ? "strict" : "lenient")
    
      .append(")]");
    return sb.toString();
  }
  
  public boolean equals(Object o)
  {
    return o == this;
  }
  
  public int hashCode()
  {
    return System.identityHashCode(this);
  }
  










  protected boolean looksLikeISO8601(String dateStr)
  {
    if ((dateStr.length() >= 7) && 
      (Character.isDigit(dateStr.charAt(0))) && 
      (Character.isDigit(dateStr.charAt(3))) && 
      (dateStr.charAt(4) == '-') && 
      (Character.isDigit(dateStr.charAt(5))))
    {
      return true;
    }
    return false;
  }
  
  private Date _parseDateFromLong(String longStr, ParsePosition pos) throws ParseException
  {
    try
    {
      ts = NumberInput.parseLong(longStr);
    }
    catch (NumberFormatException e) {
      long ts;
      throw new ParseException(String.format("Timestamp value %s out of 64-bit value range", new Object[] { longStr }), pos.getErrorIndex()); }
    long ts;
    return new Date(ts);
  }
  
  protected Date parseAsISO8601(String dateStr, ParsePosition pos) throws ParseException
  {
    try
    {
      return _parseAsISO8601(dateStr, pos);
    }
    catch (IllegalArgumentException e)
    {
      throw new ParseException(String.format("Cannot parse date \"%s\", problem: %s", new Object[] { dateStr, e.getMessage() }), pos.getErrorIndex());
    }
  }
  
  protected Date _parseAsISO8601(String dateStr, ParsePosition bogus)
    throws IllegalArgumentException, ParseException
  {
    int totalLen = dateStr.length();
    
    TimeZone tz = DEFAULT_TIMEZONE;
    if ((_timezone != null) && ('Z' != dateStr.charAt(totalLen - 1))) {
      tz = _timezone;
    }
    Calendar cal = _getCalendar(tz);
    cal.clear();
    String formatStr;
    String formatStr; if (totalLen <= 10) {
      Matcher m = PATTERN_PLAIN.matcher(dateStr);
      if (m.matches()) {
        int year = _parse4D(dateStr, 0);
        int month = _parse2D(dateStr, 5) - 1;
        int day = _parse2D(dateStr, 8);
        
        cal.set(year, month, day, 0, 0, 0);
        cal.set(14, 0);
        return cal.getTime();
      }
      formatStr = "yyyy-MM-dd";
    } else {
      Matcher m = PATTERN_ISO8601.matcher(dateStr);
      if (m.matches())
      {

        int start = m.start(2);
        int end = m.end(2);
        int len = end - start;
        if (len > 1)
        {
          int offsetSecs = _parse2D(dateStr, start + 1) * 3600;
          if (len >= 5) {
            offsetSecs += _parse2D(dateStr, end - 2) * 60;
          }
          if (dateStr.charAt(start) == '-') {
            offsetSecs *= 64536;
          } else {
            offsetSecs *= 1000;
          }
          cal.set(15, offsetSecs);
          
          cal.set(16, 0);
        }
        
        int year = _parse4D(dateStr, 0);
        int month = _parse2D(dateStr, 5) - 1;
        int day = _parse2D(dateStr, 8);
        

        int hour = _parse2D(dateStr, 11);
        int minute = _parse2D(dateStr, 14);
        
        int seconds;
        int seconds;
        if ((totalLen > 16) && (dateStr.charAt(16) == ':')) {
          seconds = _parse2D(dateStr, 17);
        } else {
          seconds = 0;
        }
        cal.set(year, month, day, hour, minute, seconds);
        

        start = m.start(1) + 1;
        end = m.end(1);
        int msecs = 0;
        if (start >= end) {
          cal.set(14, 0);
        }
        else {
          msecs = 0;
          int fractLen = end - start;
          switch (fractLen)
          {
          default: 
            if (fractLen > 9) {
              throw new ParseException(String.format("Cannot parse date \"%s\": invalid fractional seconds '%s'; can use at most 9 digits", new Object[] { dateStr, m
              
                .group(1).substring(1) }), start);
            }
          

          case 3: 
            msecs += dateStr.charAt(start + 2) - '0';
          case 2: 
            msecs += 10 * (dateStr.charAt(start + 1) - '0');
          case 1: 
            msecs += 100 * (dateStr.charAt(start) - '0');
            break;
          }
          
          
          cal.set(14, msecs);
        }
        return cal.getTime();
      }
      formatStr = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    }
    

    throw new ParseException(String.format("Cannot parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)", new Object[] { dateStr, formatStr, _lenient }), 0);
  }
  



  private static int _parse4D(String str, int index)
  {
    return 
    

      1000 * (str.charAt(index) - '0') + 100 * (str.charAt(index + 1) - '0') + 10 * (str.charAt(index + 2) - '0') + (str.charAt(index + 3) - '0');
  }
  
  private static int _parse2D(String str, int index) {
    return 
      10 * (str.charAt(index) - '0') + (str.charAt(index + 1) - '0');
  }
  
  protected Date parseAsRFC1123(String dateStr, ParsePosition pos)
  {
    if (_formatRFC1123 == null) {
      _formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", _timezone, _locale, _lenient);
    }
    
    return _formatRFC1123.parse(dateStr, pos);
  }
  







  private static final DateFormat _cloneFormat(DateFormat df, String format, TimeZone tz, Locale loc, Boolean lenient)
  {
    if (!loc.equals(DEFAULT_LOCALE)) {
      df = new SimpleDateFormat(format, loc);
      df.setTimeZone(tz == null ? DEFAULT_TIMEZONE : tz);
    } else {
      df = (DateFormat)df.clone();
      if (tz != null) {
        df.setTimeZone(tz);
      }
    }
    if (lenient != null) {
      df.setLenient(lenient.booleanValue());
    }
    return df;
  }
  
  protected void _clearFormats() {
    _formatRFC1123 = null;
  }
  
  protected Calendar _getCalendar(TimeZone tz) {
    Calendar cal = _calendar;
    if (cal == null) {
      _calendar = (cal = (Calendar)CALENDAR.clone());
    }
    if (!cal.getTimeZone().equals(tz)) {
      cal.setTimeZone(tz);
    }
    cal.setLenient(isLenient());
    return cal;
  }
  
  protected static <T> boolean _equals(T value1, T value2) {
    if (value1 == value2) {
      return true;
    }
    return (value1 != null) && (value1.equals(value2));
  }
}
