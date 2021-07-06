package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;















































public abstract class ParserBase
  extends ParserMinimalBase
{
  protected final IOContext _ioContext;
  protected boolean _closed;
  protected int _inputPtr;
  protected int _inputEnd;
  protected long _currInputProcessed;
  protected int _currInputRow = 1;
  








  protected int _currInputRowStart;
  








  protected long _tokenInputTotal;
  







  protected int _tokenInputRow = 1;
  







  protected int _tokenInputCol;
  







  protected JsonReadContext _parsingContext;
  







  protected JsonToken _nextToken;
  






  protected final TextBuffer _textBuffer;
  






  protected char[] _nameCopyBuffer;
  






  protected boolean _nameCopied;
  






  protected ByteArrayBuilder _byteArrayBuilder;
  






  protected byte[] _binaryValue;
  






  protected int _numTypesValid = 0;
  



  protected int _numberInt;
  



  protected long _numberLong;
  



  protected double _numberDouble;
  



  protected BigInteger _numberBigInt;
  



  protected BigDecimal _numberBigDecimal;
  



  protected boolean _numberNegative;
  



  protected int _intLength;
  



  protected int _fractLength;
  


  protected int _expLength;
  



  protected ParserBase(IOContext ctxt, int features)
  {
    super(features);
    _ioContext = ctxt;
    _textBuffer = ctxt.constructTextBuffer();
    
    DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
    _parsingContext = JsonReadContext.createRootContext(dups);
  }
  
  public Version version() { return PackageVersion.VERSION; }
  
  public Object getCurrentValue()
  {
    return _parsingContext.getCurrentValue();
  }
  
  public void setCurrentValue(Object v)
  {
    _parsingContext.setCurrentValue(v);
  }
  






  public JsonParser enable(JsonParser.Feature f)
  {
    _features |= f.getMask();
    if ((f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) && 
      (_parsingContext.getDupDetector() == null)) {
      _parsingContext = _parsingContext.withDupDetector(DupDetector.rootDetector(this));
    }
    
    return this;
  }
  
  public JsonParser disable(JsonParser.Feature f)
  {
    _features &= (f.getMask() ^ 0xFFFFFFFF);
    if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) {
      _parsingContext = _parsingContext.withDupDetector(null);
    }
    return this;
  }
  
  @Deprecated
  public JsonParser setFeatureMask(int newMask)
  {
    int changes = _features ^ newMask;
    if (changes != 0) {
      _features = newMask;
      _checkStdFeatureChanges(newMask, changes);
    }
    return this;
  }
  
  public JsonParser overrideStdFeatures(int values, int mask)
  {
    int oldState = _features;
    int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
    int changed = oldState ^ newState;
    if (changed != 0) {
      _features = newState;
      _checkStdFeatureChanges(newState, changed);
    }
    return this;
  }
  









  protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures)
  {
    int f = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.getMask();
    
    if (((changedFeatures & f) != 0) && 
      ((newFeatureFlags & f) != 0)) {
      if (_parsingContext.getDupDetector() == null) {
        _parsingContext = _parsingContext.withDupDetector(DupDetector.rootDetector(this));
      } else {
        _parsingContext = _parsingContext.withDupDetector(null);
      }
    }
  }
  










  public String getCurrentName()
    throws IOException
  {
    if ((_currToken == JsonToken.START_OBJECT) || (_currToken == JsonToken.START_ARRAY)) {
      JsonReadContext parent = _parsingContext.getParent();
      if (parent != null) {
        return parent.getCurrentName();
      }
    }
    return _parsingContext.getCurrentName();
  }
  
  public void overrideCurrentName(String name)
  {
    JsonReadContext ctxt = _parsingContext;
    if ((_currToken == JsonToken.START_OBJECT) || (_currToken == JsonToken.START_ARRAY)) {
      ctxt = ctxt.getParent();
    }
    

    try
    {
      ctxt.setCurrentName(name);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public void close() throws IOException {
    if (!_closed)
    {
      _inputPtr = Math.max(_inputPtr, _inputEnd);
      _closed = true;
      try {
        _closeInput();
        


        _releaseBuffers(); } finally { _releaseBuffers();
      }
    }
  }
  
  public boolean isClosed() { return _closed; }
  public JsonReadContext getParsingContext() { return _parsingContext; }
  





  public JsonLocation getTokenLocation()
  {
    return new JsonLocation(_getSourceReference(), -1L, 
      getTokenCharacterOffset(), 
      getTokenLineNr(), 
      getTokenColumnNr());
  }
  




  public JsonLocation getCurrentLocation()
  {
    int col = _inputPtr - _currInputRowStart + 1;
    return new JsonLocation(_getSourceReference(), -1L, _currInputProcessed + _inputPtr, _currInputRow, col);
  }
  








  public boolean hasTextCharacters()
  {
    if (_currToken == JsonToken.VALUE_STRING) return true;
    if (_currToken == JsonToken.FIELD_NAME) return _nameCopied;
    return false;
  }
  

  public byte[] getBinaryValue(Base64Variant variant)
    throws IOException
  {
    if (_binaryValue == null) {
      if (_currToken != JsonToken.VALUE_STRING) {
        _reportError("Current token (" + _currToken + ") not VALUE_STRING, can not access as binary");
      }
      ByteArrayBuilder builder = _getByteArrayBuilder();
      _decodeBase64(getText(), builder, variant);
      _binaryValue = builder.toByteArray();
    }
    return _binaryValue;
  }
  






  public long getTokenCharacterOffset() { return _tokenInputTotal; }
  public int getTokenLineNr() { return _tokenInputRow; }
  
  public int getTokenColumnNr() {
    int col = _tokenInputCol;
    return col < 0 ? col : col + 1;
  }
  








  protected abstract void _closeInput()
    throws IOException;
  







  protected void _releaseBuffers()
    throws IOException
  {
    _textBuffer.releaseBuffers();
    char[] buf = _nameCopyBuffer;
    if (buf != null) {
      _nameCopyBuffer = null;
      _ioContext.releaseNameCopyBuffer(buf);
    }
  }
  




  protected void _handleEOF()
    throws JsonParseException
  {
    if (!_parsingContext.inRoot()) {
      String marker = _parsingContext.inArray() ? "Array" : "Object";
      _reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", new Object[] { marker, _parsingContext
      

        .getStartLocation(_getSourceReference()) }), null);
    }
  }
  


  protected final int _eofAsNextChar()
    throws JsonParseException
  {
    _handleEOF();
    return -1;
  }
  






  public ByteArrayBuilder _getByteArrayBuilder()
  {
    if (_byteArrayBuilder == null) {
      _byteArrayBuilder = new ByteArrayBuilder();
    } else {
      _byteArrayBuilder.reset();
    }
    return _byteArrayBuilder;
  }
  








  protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen)
  {
    if ((fractLen < 1) && (expLen < 1)) {
      return resetInt(negative, intLen);
    }
    return resetFloat(negative, intLen, fractLen, expLen);
  }
  
  protected final JsonToken resetInt(boolean negative, int intLen)
  {
    _numberNegative = negative;
    _intLength = intLen;
    _fractLength = 0;
    _expLength = 0;
    _numTypesValid = 0;
    return JsonToken.VALUE_NUMBER_INT;
  }
  
  protected final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen)
  {
    _numberNegative = negative;
    _intLength = intLen;
    _fractLength = fractLen;
    _expLength = expLen;
    _numTypesValid = 0;
    return JsonToken.VALUE_NUMBER_FLOAT;
  }
  
  protected final JsonToken resetAsNaN(String valueStr, double value)
  {
    _textBuffer.resetWithString(valueStr);
    _numberDouble = value;
    _numTypesValid = 8;
    return JsonToken.VALUE_NUMBER_FLOAT;
  }
  
  public boolean isNaN()
  {
    if ((_currToken == JsonToken.VALUE_NUMBER_FLOAT) && 
      ((_numTypesValid & 0x8) != 0))
    {
      double d = _numberDouble;
      return (Double.isNaN(d)) || (Double.isInfinite(d));
    }
    
    return false;
  }
  






  public Number getNumberValue()
    throws IOException
  {
    if (_numTypesValid == 0) {
      _parseNumericValue(0);
    }
    
    if (_currToken == JsonToken.VALUE_NUMBER_INT) {
      if ((_numTypesValid & 0x1) != 0) {
        return Integer.valueOf(_numberInt);
      }
      if ((_numTypesValid & 0x2) != 0) {
        return Long.valueOf(_numberLong);
      }
      if ((_numTypesValid & 0x4) != 0) {
        return _numberBigInt;
      }
      
      return _numberBigDecimal;
    }
    



    if ((_numTypesValid & 0x10) != 0) {
      return _numberBigDecimal;
    }
    if ((_numTypesValid & 0x8) == 0) {
      _throwInternal();
    }
    return Double.valueOf(_numberDouble);
  }
  
  public JsonParser.NumberType getNumberType()
    throws IOException
  {
    if (_numTypesValid == 0) {
      _parseNumericValue(0);
    }
    if (_currToken == JsonToken.VALUE_NUMBER_INT) {
      if ((_numTypesValid & 0x1) != 0) {
        return JsonParser.NumberType.INT;
      }
      if ((_numTypesValid & 0x2) != 0) {
        return JsonParser.NumberType.LONG;
      }
      return JsonParser.NumberType.BIG_INTEGER;
    }
    






    if ((_numTypesValid & 0x10) != 0) {
      return JsonParser.NumberType.BIG_DECIMAL;
    }
    return JsonParser.NumberType.DOUBLE;
  }
  
  public int getIntValue()
    throws IOException
  {
    if ((_numTypesValid & 0x1) == 0) {
      if (_numTypesValid == 0) {
        return _parseIntValue();
      }
      if ((_numTypesValid & 0x1) == 0) {
        convertNumberToInt();
      }
    }
    return _numberInt;
  }
  
  public long getLongValue()
    throws IOException
  {
    if ((_numTypesValid & 0x2) == 0) {
      if (_numTypesValid == 0) {
        _parseNumericValue(2);
      }
      if ((_numTypesValid & 0x2) == 0) {
        convertNumberToLong();
      }
    }
    return _numberLong;
  }
  
  public BigInteger getBigIntegerValue()
    throws IOException
  {
    if ((_numTypesValid & 0x4) == 0) {
      if (_numTypesValid == 0) {
        _parseNumericValue(4);
      }
      if ((_numTypesValid & 0x4) == 0) {
        convertNumberToBigInteger();
      }
    }
    return _numberBigInt;
  }
  
  public float getFloatValue()
    throws IOException
  {
    double value = getDoubleValue();
    







    return (float)value;
  }
  
  public double getDoubleValue()
    throws IOException
  {
    if ((_numTypesValid & 0x8) == 0) {
      if (_numTypesValid == 0) {
        _parseNumericValue(8);
      }
      if ((_numTypesValid & 0x8) == 0) {
        convertNumberToDouble();
      }
    }
    return _numberDouble;
  }
  
  public BigDecimal getDecimalValue()
    throws IOException
  {
    if ((_numTypesValid & 0x10) == 0) {
      if (_numTypesValid == 0) {
        _parseNumericValue(16);
      }
      if ((_numTypesValid & 0x10) == 0) {
        convertNumberToBigDecimal();
      }
    }
    return _numberBigDecimal;
  }
  















  protected void _parseNumericValue(int expType)
    throws IOException
  {
    if (_currToken == JsonToken.VALUE_NUMBER_INT) {
      int len = _intLength;
      
      if (len <= 9) {
        int i = _textBuffer.contentsAsInt(_numberNegative);
        _numberInt = i;
        _numTypesValid = 1;
        return;
      }
      if (len <= 18) {
        long l = _textBuffer.contentsAsLong(_numberNegative);
        
        if (len == 10) {
          if (_numberNegative) {
            if (l >= -2147483648L) {
              _numberInt = ((int)l);
              _numTypesValid = 1;
            }
            
          }
          else if (l <= 2147483647L) {
            _numberInt = ((int)l);
            _numTypesValid = 1;
            return;
          }
        }
        
        _numberLong = l;
        _numTypesValid = 2;
        return;
      }
      _parseSlowInt(expType);
      return;
    }
    if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
      _parseSlowFloat(expType);
      return;
    }
    _reportError("Current token (%s) not numeric, can not use numeric value accessors", _currToken);
  }
  



  protected int _parseIntValue()
    throws IOException
  {
    if ((_currToken == JsonToken.VALUE_NUMBER_INT) && 
      (_intLength <= 9)) {
      int i = _textBuffer.contentsAsInt(_numberNegative);
      _numberInt = i;
      _numTypesValid = 1;
      return i;
    }
    

    _parseNumericValue(1);
    if ((_numTypesValid & 0x1) == 0) {
      convertNumberToInt();
    }
    return _numberInt;
  }
  





  private void _parseSlowFloat(int expType)
    throws IOException
  {
    try
    {
      if (expType == 16) {
        _numberBigDecimal = _textBuffer.contentsAsDecimal();
        _numTypesValid = 16;
      }
      else {
        _numberDouble = _textBuffer.contentsAsDouble();
        _numTypesValid = 8;
      }
    }
    catch (NumberFormatException nex) {
      _wrapError("Malformed numeric value (" + _longNumberDesc(_textBuffer.contentsAsString()) + ")", nex);
    }
  }
  
  private void _parseSlowInt(int expType) throws IOException
  {
    String numStr = _textBuffer.contentsAsString();
    try {
      int len = _intLength;
      char[] buf = _textBuffer.getTextBuffer();
      int offset = _textBuffer.getTextOffset();
      if (_numberNegative) {
        offset++;
      }
      
      if (NumberInput.inLongRange(buf, offset, len, _numberNegative))
      {
        _numberLong = Long.parseLong(numStr);
        _numTypesValid = 2;
      }
      else {
        if ((expType == 1) || (expType == 2)) {
          _reportTooLongIntegral(expType, numStr);
        }
        if ((expType == 8) || (expType == 32)) {
          _numberDouble = NumberInput.parseDouble(numStr);
          _numTypesValid = 8;
        }
        else {
          _numberBigInt = new BigInteger(numStr);
          _numTypesValid = 4;
        }
      }
    }
    catch (NumberFormatException nex) {
      _wrapError("Malformed numeric value (" + _longNumberDesc(numStr) + ")", nex);
    }
  }
  
  protected void _reportTooLongIntegral(int expType, String rawNum)
    throws IOException
  {
    if (expType == 1) {
      reportOverflowInt(rawNum);
    } else {
      reportOverflowLong(rawNum);
    }
  }
  






  protected void convertNumberToInt()
    throws IOException
  {
    if ((_numTypesValid & 0x2) != 0)
    {
      int result = (int)_numberLong;
      if (result != _numberLong) {
        reportOverflowInt(getText(), currentToken());
      }
      _numberInt = result;
    } else if ((_numTypesValid & 0x4) != 0) {
      if ((BI_MIN_INT.compareTo(_numberBigInt) > 0) || 
        (BI_MAX_INT.compareTo(_numberBigInt) < 0)) {
        reportOverflowInt();
      }
      _numberInt = _numberBigInt.intValue();
    } else if ((_numTypesValid & 0x8) != 0)
    {
      if ((_numberDouble < -2.147483648E9D) || (_numberDouble > 2.147483647E9D)) {
        reportOverflowInt();
      }
      _numberInt = ((int)_numberDouble);
    } else if ((_numTypesValid & 0x10) != 0) {
      if ((BD_MIN_INT.compareTo(_numberBigDecimal) > 0) || 
        (BD_MAX_INT.compareTo(_numberBigDecimal) < 0)) {
        reportOverflowInt();
      }
      _numberInt = _numberBigDecimal.intValue();
    } else {
      _throwInternal();
    }
    _numTypesValid |= 0x1;
  }
  
  protected void convertNumberToLong() throws IOException
  {
    if ((_numTypesValid & 0x1) != 0) {
      _numberLong = _numberInt;
    } else if ((_numTypesValid & 0x4) != 0) {
      if ((BI_MIN_LONG.compareTo(_numberBigInt) > 0) || 
        (BI_MAX_LONG.compareTo(_numberBigInt) < 0)) {
        reportOverflowLong();
      }
      _numberLong = _numberBigInt.longValue();
    } else if ((_numTypesValid & 0x8) != 0)
    {
      if ((_numberDouble < -9.223372036854776E18D) || (_numberDouble > 9.223372036854776E18D)) {
        reportOverflowLong();
      }
      _numberLong = (_numberDouble);
    } else if ((_numTypesValid & 0x10) != 0) {
      if ((BD_MIN_LONG.compareTo(_numberBigDecimal) > 0) || 
        (BD_MAX_LONG.compareTo(_numberBigDecimal) < 0)) {
        reportOverflowLong();
      }
      _numberLong = _numberBigDecimal.longValue();
    } else {
      _throwInternal();
    }
    _numTypesValid |= 0x2;
  }
  
  protected void convertNumberToBigInteger() throws IOException
  {
    if ((_numTypesValid & 0x10) != 0)
    {
      _numberBigInt = _numberBigDecimal.toBigInteger();
    } else if ((_numTypesValid & 0x2) != 0) {
      _numberBigInt = BigInteger.valueOf(_numberLong);
    } else if ((_numTypesValid & 0x1) != 0) {
      _numberBigInt = BigInteger.valueOf(_numberInt);
    } else if ((_numTypesValid & 0x8) != 0) {
      _numberBigInt = BigDecimal.valueOf(_numberDouble).toBigInteger();
    } else {
      _throwInternal();
    }
    _numTypesValid |= 0x4;
  }
  





  protected void convertNumberToDouble()
    throws IOException
  {
    if ((_numTypesValid & 0x10) != 0) {
      _numberDouble = _numberBigDecimal.doubleValue();
    } else if ((_numTypesValid & 0x4) != 0) {
      _numberDouble = _numberBigInt.doubleValue();
    } else if ((_numTypesValid & 0x2) != 0) {
      _numberDouble = _numberLong;
    } else if ((_numTypesValid & 0x1) != 0) {
      _numberDouble = _numberInt;
    } else {
      _throwInternal();
    }
    _numTypesValid |= 0x8;
  }
  





  protected void convertNumberToBigDecimal()
    throws IOException
  {
    if ((_numTypesValid & 0x8) != 0)
    {


      _numberBigDecimal = NumberInput.parseBigDecimal(getText());
    } else if ((_numTypesValid & 0x4) != 0) {
      _numberBigDecimal = new BigDecimal(_numberBigInt);
    } else if ((_numTypesValid & 0x2) != 0) {
      _numberBigDecimal = BigDecimal.valueOf(_numberLong);
    } else if ((_numTypesValid & 0x1) != 0) {
      _numberBigDecimal = BigDecimal.valueOf(_numberInt);
    } else {
      _throwInternal();
    }
    _numTypesValid |= 0x10;
  }
  




  protected void _reportMismatchedEndMarker(int actCh, char expCh)
    throws JsonParseException
  {
    JsonReadContext ctxt = getParsingContext();
    _reportError(String.format("Unexpected close marker '%s': expected '%c' (for %s starting at %s)", new Object[] {
    
      Character.valueOf((char)actCh), Character.valueOf(expCh), ctxt.typeDesc(), ctxt.getStartLocation(_getSourceReference()) }));
  }
  
  protected char _handleUnrecognizedCharacterEscape(char ch)
    throws JsonProcessingException
  {
    if (isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
      return ch;
    }
    
    if ((ch == '\'') && (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES))) {
      return ch;
    }
    _reportError("Unrecognized character escape " + _getCharDesc(ch));
    return ch;
  }
  





  protected void _throwUnquotedSpace(int i, String ctxtDesc)
    throws JsonParseException
  {
    if ((!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)) || (i > 32)) {
      char c = (char)i;
      String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
      _reportError(msg);
    }
  }
  





  protected String _validJsonTokenList()
    throws IOException
  {
    return _validJsonValueList();
  }
  






  protected String _validJsonValueList()
    throws IOException
  {
    if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
      return "(JSON String, Number (or 'NaN'/'INF'/'+INF'), Array, Object or token 'null', 'true' or 'false')";
    }
    return "(JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
  }
  









  protected char _decodeEscaped()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index)
    throws IOException
  {
    if (ch != 92) {
      throw reportInvalidBase64Char(b64variant, ch, index);
    }
    int unescaped = _decodeEscaped();
    
    if ((unescaped <= 32) && 
      (index == 0)) {
      return -1;
    }
    

    int bits = b64variant.decodeBase64Char(unescaped);
    if ((bits < 0) && 
      (bits != -2)) {
      throw reportInvalidBase64Char(b64variant, unescaped, index);
    }
    
    return bits;
  }
  
  protected final int _decodeBase64Escape(Base64Variant b64variant, char ch, int index) throws IOException
  {
    if (ch != '\\') {
      throw reportInvalidBase64Char(b64variant, ch, index);
    }
    char unescaped = _decodeEscaped();
    
    if ((unescaped <= ' ') && 
      (index == 0)) {
      return -1;
    }
    

    int bits = b64variant.decodeBase64Char(unescaped);
    if (bits < 0)
    {
      if ((bits != -2) || (index < 2)) {
        throw reportInvalidBase64Char(b64variant, unescaped, index);
      }
    }
    return bits;
  }
  
  protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex) throws IllegalArgumentException {
    return reportInvalidBase64Char(b64variant, ch, bindex, null);
  }
  

  protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex, String msg)
    throws IllegalArgumentException
  {
    String base;
    String base;
    if (ch <= 32) {
      base = String.format("Illegal white space character (code 0x%s) as character #%d of 4-char base64 unit: can only used between units", new Object[] {
        Integer.toHexString(ch), Integer.valueOf(bindex + 1) }); } else { String base;
      if (b64variant.usesPaddingChar(ch)) {
        base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character"; } else { String base;
        if ((!Character.isDefined(ch)) || (Character.isISOControl(ch)))
        {
          base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        } else
          base = "Illegal character '" + (char)ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
      } }
    if (msg != null) {
      base = base + ": " + msg;
    }
    return new IllegalArgumentException(base);
  }
  
  protected void _handleBase64MissingPadding(Base64Variant b64variant)
    throws IOException
  {
    _reportError(b64variant.missingPaddingMessage());
  }
  











  protected Object _getSourceReference()
  {
    if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(_features)) {
      return _ioContext.getSourceReference();
    }
    return null;
  }
  
  protected static int[] growArrayBy(int[] arr, int more)
  {
    if (arr == null) {
      return new int[more];
    }
    return Arrays.copyOf(arr, arr.length + more);
  }
  





  @Deprecated
  protected void loadMoreGuaranteed()
    throws IOException
  {
    if (!loadMore()) _reportInvalidEOF();
  }
  
  @Deprecated
  protected boolean loadMore() throws IOException { return false; }
  
  protected void _finishString()
    throws IOException
  {}
}
