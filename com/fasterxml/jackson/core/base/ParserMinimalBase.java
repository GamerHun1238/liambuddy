package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.VersionUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;



















public abstract class ParserMinimalBase
  extends JsonParser
{
  protected static final int INT_TAB = 9;
  protected static final int INT_LF = 10;
  protected static final int INT_CR = 13;
  protected static final int INT_SPACE = 32;
  protected static final int INT_LBRACKET = 91;
  protected static final int INT_RBRACKET = 93;
  protected static final int INT_LCURLY = 123;
  protected static final int INT_RCURLY = 125;
  protected static final int INT_QUOTE = 34;
  protected static final int INT_APOS = 39;
  protected static final int INT_BACKSLASH = 92;
  protected static final int INT_SLASH = 47;
  protected static final int INT_ASTERISK = 42;
  protected static final int INT_COLON = 58;
  protected static final int INT_COMMA = 44;
  protected static final int INT_HASH = 35;
  protected static final int INT_0 = 48;
  protected static final int INT_9 = 57;
  protected static final int INT_MINUS = 45;
  protected static final int INT_PLUS = 43;
  protected static final int INT_PERIOD = 46;
  protected static final int INT_e = 101;
  protected static final int INT_E = 69;
  protected static final char CHAR_NULL = '\000';
  protected static final byte[] NO_BYTES = new byte[0];
  



  protected static final int[] NO_INTS = new int[0];
  


  protected static final int NR_UNKNOWN = 0;
  


  protected static final int NR_INT = 1;
  


  protected static final int NR_LONG = 2;
  


  protected static final int NR_BIGINT = 4;
  


  protected static final int NR_DOUBLE = 8;
  


  protected static final int NR_BIGDECIMAL = 16;
  

  protected static final int NR_FLOAT = 32;
  

  protected static final BigInteger BI_MIN_INT = BigInteger.valueOf(-2147483648L);
  protected static final BigInteger BI_MAX_INT = BigInteger.valueOf(2147483647L);
  
  protected static final BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
  protected static final BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
  
  protected static final BigDecimal BD_MIN_LONG = new BigDecimal(BI_MIN_LONG);
  protected static final BigDecimal BD_MAX_LONG = new BigDecimal(BI_MAX_LONG);
  
  protected static final BigDecimal BD_MIN_INT = new BigDecimal(BI_MIN_INT);
  protected static final BigDecimal BD_MAX_INT = new BigDecimal(BI_MAX_INT);
  



  protected static final long MIN_INT_L = -2147483648L;
  



  protected static final long MAX_INT_L = 2147483647L;
  



  protected static final double MIN_LONG_D = -9.223372036854776E18D;
  



  protected static final double MAX_LONG_D = 9.223372036854776E18D;
  



  protected static final double MIN_INT_D = -2.147483648E9D;
  


  protected static final double MAX_INT_D = 2.147483647E9D;
  


  protected static final int MAX_ERROR_TOKEN_LENGTH = 256;
  


  protected JsonToken _currToken;
  


  protected JsonToken _lastClearedToken;
  



  protected ParserMinimalBase() {}
  



  protected ParserMinimalBase(int features)
  {
    super(features);
  }
  










  public abstract JsonToken nextToken()
    throws IOException;
  










  public JsonToken currentToken() { return _currToken; }
  
  public int currentTokenId() { JsonToken t = _currToken;
    return t == null ? 0 : t.id();
  }
  
  public JsonToken getCurrentToken() { return _currToken; }
  
  public int getCurrentTokenId() { JsonToken t = _currToken;
    return t == null ? 0 : t.id();
  }
  
  public boolean hasCurrentToken() { return _currToken != null; }
  
  public boolean hasTokenId(int id) { JsonToken t = _currToken;
    if (t == null) {
      return 0 == id;
    }
    return t.id() == id;
  }
  
  public boolean hasToken(JsonToken t) {
    return _currToken == t;
  }
  
  public boolean isExpectedStartArrayToken() { return _currToken == JsonToken.START_ARRAY; }
  public boolean isExpectedStartObjectToken() { return _currToken == JsonToken.START_OBJECT; }
  

  public JsonToken nextValue()
    throws IOException
  {
    JsonToken t = nextToken();
    if (t == JsonToken.FIELD_NAME) {
      t = nextToken();
    }
    return t;
  }
  
  public JsonParser skipChildren()
    throws IOException
  {
    if ((_currToken != JsonToken.START_OBJECT) && (_currToken != JsonToken.START_ARRAY))
    {
      return this;
    }
    int open = 1;
    

    for (;;)
    {
      JsonToken t = nextToken();
      if (t == null) {
        _handleEOF();
        



        return this;
      }
      if (t.isStructStart()) {
        open++;
      } else if (t.isStructEnd()) {
        open--; if (open == 0) {
          return this;
        }
      }
      else if (t == JsonToken.NOT_AVAILABLE)
      {

        _reportError("Not enough content available for `skipChildren()`: non-blocking parser? (%s)", 
          getClass().getName());
      }
    }
  }
  


  protected abstract void _handleEOF()
    throws JsonParseException;
  


  public abstract String getCurrentName()
    throws IOException;
  


  public abstract void close()
    throws IOException;
  


  public abstract boolean isClosed();
  

  public abstract JsonStreamContext getParsingContext();
  

  public void clearCurrentToken()
  {
    if (_currToken != null) {
      _lastClearedToken = _currToken;
      _currToken = null;
    }
  }
  
  public JsonToken getLastClearedToken() { return _lastClearedToken; }
  


  public abstract void overrideCurrentName(String paramString);
  

  public abstract String getText()
    throws IOException;
  

  public abstract char[] getTextCharacters()
    throws IOException;
  

  public abstract boolean hasTextCharacters();
  

  public abstract int getTextLength()
    throws IOException;
  

  public abstract int getTextOffset()
    throws IOException;
  

  public abstract byte[] getBinaryValue(Base64Variant paramBase64Variant)
    throws IOException;
  

  public boolean getValueAsBoolean(boolean defaultValue)
    throws IOException
  {
    JsonToken t = _currToken;
    if (t != null) {
      switch (t.id()) {
      case 6: 
        String str = getText().trim();
        if ("true".equals(str)) {
          return true;
        }
        if ("false".equals(str)) {
          return false;
        }
        if (_hasTextualNull(str)) {
          return false;
        }
        break;
      case 7: 
        return getIntValue() != 0;
      case 9: 
        return true;
      case 10: 
      case 11: 
        return false;
      case 12: 
        Object value = getEmbeddedObject();
        if ((value instanceof Boolean)) {
          return ((Boolean)value).booleanValue();
        }
        break;
      }
      
    }
    return defaultValue;
  }
  
  public int getValueAsInt()
    throws IOException
  {
    JsonToken t = _currToken;
    if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
      return getIntValue();
    }
    return getValueAsInt(0);
  }
  
  public int getValueAsInt(int defaultValue)
    throws IOException
  {
    JsonToken t = _currToken;
    if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
      return getIntValue();
    }
    if (t != null) {
      switch (t.id()) {
      case 6: 
        String str = getText();
        if (_hasTextualNull(str)) {
          return 0;
        }
        return NumberInput.parseAsInt(str, defaultValue);
      case 9: 
        return 1;
      case 10: 
        return 0;
      case 11: 
        return 0;
      case 12: 
        Object value = getEmbeddedObject();
        if ((value instanceof Number))
          return ((Number)value).intValue();
        break;
      }
    }
    return defaultValue;
  }
  
  public long getValueAsLong()
    throws IOException
  {
    JsonToken t = _currToken;
    if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
      return getLongValue();
    }
    return getValueAsLong(0L);
  }
  
  public long getValueAsLong(long defaultValue)
    throws IOException
  {
    JsonToken t = _currToken;
    if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
      return getLongValue();
    }
    if (t != null) {
      switch (t.id()) {
      case 6: 
        String str = getText();
        if (_hasTextualNull(str)) {
          return 0L;
        }
        return NumberInput.parseAsLong(str, defaultValue);
      case 9: 
        return 1L;
      case 10: 
      case 11: 
        return 0L;
      case 12: 
        Object value = getEmbeddedObject();
        if ((value instanceof Number))
          return ((Number)value).longValue();
        break;
      }
    }
    return defaultValue;
  }
  
  public double getValueAsDouble(double defaultValue)
    throws IOException
  {
    JsonToken t = _currToken;
    if (t != null) {
      switch (t.id()) {
      case 6: 
        String str = getText();
        if (_hasTextualNull(str)) {
          return 0.0D;
        }
        return NumberInput.parseAsDouble(str, defaultValue);
      case 7: 
      case 8: 
        return getDoubleValue();
      case 9: 
        return 1.0D;
      case 10: 
      case 11: 
        return 0.0D;
      case 12: 
        Object value = getEmbeddedObject();
        if ((value instanceof Number))
          return ((Number)value).doubleValue();
        break;
      }
    }
    return defaultValue;
  }
  
  public String getValueAsString() throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      return getText();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return getCurrentName();
    }
    return getValueAsString(null);
  }
  
  public String getValueAsString(String defaultValue) throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      return getText();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return getCurrentName();
    }
    if ((_currToken == null) || (_currToken == JsonToken.VALUE_NULL) || (!_currToken.isScalarValue())) {
      return defaultValue;
    }
    return getText();
  }
  








  protected void _decodeBase64(String str, ByteArrayBuilder builder, Base64Variant b64variant)
    throws IOException
  {
    try
    {
      b64variant.decode(str, builder);
    } catch (IllegalArgumentException e) {
      _reportError(e.getMessage());
    }
  }
  











  protected boolean _hasTextualNull(String value)
  {
    return "null".equals(value);
  }
  



  protected void reportUnexpectedNumberChar(int ch, String comment)
    throws JsonParseException
  {
    String msg = String.format("Unexpected character (%s) in numeric value", new Object[] { _getCharDesc(ch) });
    if (comment != null) {
      msg = msg + ": " + comment;
    }
    _reportError(msg);
  }
  




  protected void reportInvalidNumber(String msg)
    throws JsonParseException
  {
    _reportError("Invalid numeric value: " + msg);
  }
  



  protected void reportOverflowInt()
    throws IOException
  {
    reportOverflowInt(getText());
  }
  
  protected void reportOverflowInt(String numDesc) throws IOException
  {
    reportOverflowInt(numDesc, JsonToken.VALUE_NUMBER_INT);
  }
  
  protected void reportOverflowInt(String numDesc, JsonToken inputType) throws IOException
  {
    _reportInputCoercion(String.format("Numeric value (%s) out of range of int (%d - %s)", new Object[] {
      _longIntegerDesc(numDesc), Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE) }), inputType, Integer.TYPE);
  }
  




  protected void reportOverflowLong()
    throws IOException
  {
    reportOverflowLong(getText());
  }
  
  protected void reportOverflowLong(String numDesc) throws IOException
  {
    reportOverflowLong(numDesc, JsonToken.VALUE_NUMBER_INT);
  }
  
  protected void reportOverflowLong(String numDesc, JsonToken inputType) throws IOException
  {
    _reportInputCoercion(String.format("Numeric value (%s) out of range of long (%d - %s)", new Object[] {
      _longIntegerDesc(numDesc), Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE) }), inputType, Long.TYPE);
  }
  



  protected void _reportInputCoercion(String msg, JsonToken inputType, Class<?> targetType)
    throws InputCoercionException
  {
    throw new InputCoercionException(this, msg, inputType, targetType);
  }
  
  protected String _longIntegerDesc(String rawNum)
  {
    int rawLen = rawNum.length();
    if (rawLen < 1000) {
      return rawNum;
    }
    if (rawNum.startsWith("-")) {
      rawLen--;
    }
    return String.format("[Integer with %d digits]", new Object[] { Integer.valueOf(rawLen) });
  }
  
  protected String _longNumberDesc(String rawNum)
  {
    int rawLen = rawNum.length();
    if (rawLen < 1000) {
      return rawNum;
    }
    if (rawNum.startsWith("-")) {
      rawLen--;
    }
    return String.format("[number with %d characters]", new Object[] { Integer.valueOf(rawLen) });
  }
  
  protected void _reportUnexpectedChar(int ch, String comment) throws JsonParseException
  {
    if (ch < 0) {
      _reportInvalidEOF();
    }
    String msg = String.format("Unexpected character (%s)", new Object[] { _getCharDesc(ch) });
    if (comment != null) {
      msg = msg + ": " + comment;
    }
    _reportError(msg);
  }
  
  protected void _reportInvalidEOF() throws JsonParseException {
    _reportInvalidEOF(" in " + _currToken, _currToken);
  }
  
  protected void _reportInvalidEOFInValue(JsonToken type)
    throws JsonParseException
  {
    String msg;
    String msg;
    if (type == JsonToken.VALUE_STRING) {
      msg = " in a String value"; } else { String msg;
      if ((type == JsonToken.VALUE_NUMBER_INT) || (type == JsonToken.VALUE_NUMBER_FLOAT))
      {
        msg = " in a Number value";
      } else
        msg = " in a value";
    }
    _reportInvalidEOF(msg, type);
  }
  

  protected void _reportInvalidEOF(String msg, JsonToken currToken)
    throws JsonParseException
  {
    throw new JsonEOFException(this, currToken, "Unexpected end-of-input" + msg);
  }
  

  @Deprecated
  protected void _reportInvalidEOFInValue()
    throws JsonParseException
  {
    _reportInvalidEOF(" in a value");
  }
  

  @Deprecated
  protected void _reportInvalidEOF(String msg)
    throws JsonParseException
  {
    throw new JsonEOFException(this, null, "Unexpected end-of-input" + msg);
  }
  
  protected void _reportMissingRootWS(int ch) throws JsonParseException {
    _reportUnexpectedChar(ch, "Expected space separating root-level values");
  }
  
  protected void _throwInvalidSpace(int i) throws JsonParseException {
    char c = (char)i;
    String msg = "Illegal character (" + _getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
    _reportError(msg);
  }
  






  protected static final String _getCharDesc(int ch)
  {
    char c = (char)ch;
    if (Character.isISOControl(c)) {
      return "(CTRL-CHAR, code " + ch + ")";
    }
    if (ch > 255) {
      return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
    }
    return "'" + c + "' (code " + ch + ")";
  }
  
  protected final void _reportError(String msg) throws JsonParseException {
    throw _constructError(msg);
  }
  
  protected final void _reportError(String msg, Object arg) throws JsonParseException
  {
    throw _constructError(String.format(msg, new Object[] { arg }));
  }
  
  protected final void _reportError(String msg, Object arg1, Object arg2) throws JsonParseException
  {
    throw _constructError(String.format(msg, new Object[] { arg1, arg2 }));
  }
  
  protected final void _wrapError(String msg, Throwable t) throws JsonParseException {
    throw _constructError(msg, t);
  }
  

  protected final void _throwInternal() {}
  
  protected final JsonParseException _constructError(String msg, Throwable t)
  {
    return new JsonParseException(this, msg, t);
  }
  
  protected static byte[] _asciiBytes(String str) {
    byte[] b = new byte[str.length()];
    int i = 0; for (int len = str.length(); i < len; i++) {
      b[i] = ((byte)str.charAt(i));
    }
    return b;
  }
  
  protected static String _ascii(byte[] b) {
    try {
      return new String(b, "US-ASCII");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
