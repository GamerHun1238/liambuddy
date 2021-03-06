package com.fasterxml.jackson.core.io;

import java.math.BigDecimal;










public final class NumberInput
{
  public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
  static final long L_BILLION = 1000000000L;
  static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
  static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);
  



  public NumberInput() {}
  


  public static int parseInt(char[] ch, int off, int len)
  {
    int num = ch[(off + len - 1)] - '0';
    
    switch (len) {
    case 9: 
      num += (ch[(off++)] - '0') * 100000000;
    case 8: 
      num += (ch[(off++)] - '0') * 10000000;
    case 7: 
      num += (ch[(off++)] - '0') * 1000000;
    case 6: 
      num += (ch[(off++)] - '0') * 100000;
    case 5: 
      num += (ch[(off++)] - '0') * 10000;
    case 4: 
      num += (ch[(off++)] - '0') * 1000;
    case 3: 
      num += (ch[(off++)] - '0') * 100;
    case 2: 
      num += (ch[off] - '0') * 10;
    }
    return num;
  }
  








  public static int parseInt(String s)
  {
    char c = s.charAt(0);
    int len = s.length();
    boolean neg = c == '-';
    int offset = 1;
    

    if (neg) {
      if ((len == 1) || (len > 10)) {
        return Integer.parseInt(s);
      }
      c = s.charAt(offset++);
    }
    else if (len > 9) {
      return Integer.parseInt(s);
    }
    
    if ((c > '9') || (c < '0')) {
      return Integer.parseInt(s);
    }
    int num = c - '0';
    if (offset < len) {
      c = s.charAt(offset++);
      if ((c > '9') || (c < '0')) {
        return Integer.parseInt(s);
      }
      num = num * 10 + (c - '0');
      if (offset < len) {
        c = s.charAt(offset++);
        if ((c > '9') || (c < '0')) {
          return Integer.parseInt(s);
        }
        num = num * 10 + (c - '0');
        
        if (offset < len) {
          do {
            c = s.charAt(offset++);
            if ((c > '9') || (c < '0')) {
              return Integer.parseInt(s);
            }
            num = num * 10 + (c - '0');
          } while (offset < len);
        }
      }
    }
    return neg ? -num : num;
  }
  

  public static long parseLong(char[] ch, int off, int len)
  {
    int len1 = len - 9;
    long val = parseInt(ch, off, len1) * 1000000000L;
    return val + parseInt(ch, off + len1, 9);
  }
  



  public static long parseLong(String s)
  {
    int length = s.length();
    if (length <= 9) {
      return parseInt(s);
    }
    
    return Long.parseLong(s);
  }
  










  public static boolean inLongRange(char[] ch, int off, int len, boolean negative)
  {
    String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
    int cmpLen = cmpStr.length();
    if (len < cmpLen) return true;
    if (len > cmpLen) { return false;
    }
    for (int i = 0; i < cmpLen; i++) {
      int diff = ch[(off + i)] - cmpStr.charAt(i);
      if (diff != 0) {
        return diff < 0;
      }
    }
    return true;
  }
  







  public static boolean inLongRange(String s, boolean negative)
  {
    String cmp = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
    int cmpLen = cmp.length();
    int alen = s.length();
    if (alen < cmpLen) return true;
    if (alen > cmpLen) { return false;
    }
    
    for (int i = 0; i < cmpLen; i++) {
      int diff = s.charAt(i) - cmp.charAt(i);
      if (diff != 0) {
        return diff < 0;
      }
    }
    return true;
  }
  
  public static int parseAsInt(String s, int def)
  {
    if (s == null) {
      return def;
    }
    s = s.trim();
    int len = s.length();
    if (len == 0) {
      return def;
    }
    
    int i = 0;
    if (i < len) {
      char c = s.charAt(0);
      if (c == '+') {
        s = s.substring(1);
        len = s.length();
      } else if (c == '-') {
        i++;
      }
    }
    for (; i < len; i++) {
      char c = s.charAt(i);
      
      if ((c > '9') || (c < '0')) {
        try {
          return (int)parseDouble(s);
        } catch (NumberFormatException e) {
          return def;
        }
      }
    }
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException localNumberFormatException1) {}
    return def;
  }
  
  public static long parseAsLong(String s, long def)
  {
    if (s == null) {
      return def;
    }
    s = s.trim();
    int len = s.length();
    if (len == 0) {
      return def;
    }
    
    int i = 0;
    if (i < len) {
      char c = s.charAt(0);
      if (c == '+') {
        s = s.substring(1);
        len = s.length();
      } else if (c == '-') {
        i++;
      }
    }
    for (; i < len; i++) {
      char c = s.charAt(i);
      
      if ((c > '9') || (c < '0')) {
        try {
          return parseDouble(s);
        } catch (NumberFormatException e) {
          return def;
        }
      }
    }
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException localNumberFormatException1) {}
    return def;
  }
  
  public static double parseAsDouble(String s, double def)
  {
    if (s == null) return def;
    s = s.trim();
    int len = s.length();
    if (len == 0) {
      return def;
    }
    try {
      return parseDouble(s);
    } catch (NumberFormatException localNumberFormatException) {}
    return def;
  }
  


  public static double parseDouble(String s)
    throws NumberFormatException
  {
    if ("2.2250738585072012e-308".equals(s)) {
      return Double.MIN_VALUE;
    }
    return Double.parseDouble(s);
  }
  
  public static BigDecimal parseBigDecimal(String s) throws NumberFormatException {
    try { return new BigDecimal(s);
    } catch (NumberFormatException e) { throw _badBD(s);
    }
  }
  

  public static BigDecimal parseBigDecimal(char[] b) throws NumberFormatException { return parseBigDecimal(b, 0, b.length); }
  
  public static BigDecimal parseBigDecimal(char[] b, int off, int len) throws NumberFormatException {
    try {
      return new BigDecimal(b, off, len);
    } catch (NumberFormatException e) { throw _badBD(new String(b, off, len));
    }
  }
  
  private static NumberFormatException _badBD(String s) {
    return new NumberFormatException("Value \"" + s + "\" can not be represented as BigDecimal");
  }
}
