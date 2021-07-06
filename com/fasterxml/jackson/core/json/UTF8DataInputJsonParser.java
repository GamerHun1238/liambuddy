package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
















public class UTF8DataInputJsonParser
  extends ParserBase
{
  static final byte BYTE_LF = 10;
  private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
  
  private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
  
  private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
  
  private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
  private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
  private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
  private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
  private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
  

  private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
  


  protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
  








  protected ObjectCodec _objectCodec;
  








  protected final ByteQuadsCanonicalizer _symbols;
  







  protected int[] _quadBuffer = new int[16];
  





  protected boolean _tokenIncomplete;
  





  private int _quad1;
  




  protected DataInput _inputData;
  




  protected int _nextByte = -1;
  








  public UTF8DataInputJsonParser(IOContext ctxt, int features, DataInput inputData, ObjectCodec codec, ByteQuadsCanonicalizer sym, int firstByte)
  {
    super(ctxt, features);
    _objectCodec = codec;
    _symbols = sym;
    _inputData = inputData;
    _nextByte = firstByte;
  }
  
  public ObjectCodec getCodec()
  {
    return _objectCodec;
  }
  
  public void setCodec(ObjectCodec c)
  {
    _objectCodec = c;
  }
  





  public int releaseBuffered(OutputStream out)
    throws IOException
  {
    return 0;
  }
  
  public Object getInputSource()
  {
    return _inputData;
  }
  






  protected void _closeInput()
    throws IOException
  {}
  





  protected void _releaseBuffers()
    throws IOException
  {
    super._releaseBuffers();
    
    _symbols.release();
  }
  






  public String getText()
    throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        return _finishAndReturnString();
      }
      return _textBuffer.contentsAsString();
    }
    return _getText2(_currToken);
  }
  
  public int getText(Writer writer)
    throws IOException
  {
    JsonToken t = _currToken;
    if (t == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        _finishString();
      }
      return _textBuffer.contentsToWriter(writer);
    }
    if (t == JsonToken.FIELD_NAME) {
      String n = _parsingContext.getCurrentName();
      writer.write(n);
      return n.length();
    }
    if (t != null) {
      if (t.isNumeric()) {
        return _textBuffer.contentsToWriter(writer);
      }
      char[] ch = t.asCharArray();
      writer.write(ch);
      return ch.length;
    }
    return 0;
  }
  

  public String getValueAsString()
    throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        return _finishAndReturnString();
      }
      return _textBuffer.contentsAsString();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return getCurrentName();
    }
    return super.getValueAsString(null);
  }
  
  public String getValueAsString(String defValue)
    throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        return _finishAndReturnString();
      }
      return _textBuffer.contentsAsString();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return getCurrentName();
    }
    return super.getValueAsString(defValue);
  }
  
  public int getValueAsInt()
    throws IOException
  {
    JsonToken t = _currToken;
    if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT))
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
    return super.getValueAsInt(0);
  }
  
  public int getValueAsInt(int defValue)
    throws IOException
  {
    JsonToken t = _currToken;
    if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT))
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
    return super.getValueAsInt(defValue);
  }
  
  protected final String _getText2(JsonToken t)
  {
    if (t == null) {
      return null;
    }
    switch (t.id()) {
    case 5: 
      return _parsingContext.getCurrentName();
    

    case 6: 
    case 7: 
    case 8: 
      return _textBuffer.contentsAsString();
    }
    return t.asString();
  }
  

  public char[] getTextCharacters()
    throws IOException
  {
    if (_currToken != null) {
      switch (_currToken.id())
      {
      case 5: 
        if (!_nameCopied) {
          String name = _parsingContext.getCurrentName();
          int nameLen = name.length();
          if (_nameCopyBuffer == null) {
            _nameCopyBuffer = _ioContext.allocNameCopyBuffer(nameLen);
          } else if (_nameCopyBuffer.length < nameLen) {
            _nameCopyBuffer = new char[nameLen];
          }
          name.getChars(0, nameLen, _nameCopyBuffer, 0);
          _nameCopied = true;
        }
        return _nameCopyBuffer;
      
      case 6: 
        if (_tokenIncomplete) {
          _tokenIncomplete = false;
          _finishString();
        }
      
      case 7: 
      case 8: 
        return _textBuffer.getTextBuffer();
      }
      
      return _currToken.asCharArray();
    }
    
    return null;
  }
  
  public int getTextLength()
    throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        _finishString();
      }
      return _textBuffer.size();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return _parsingContext.getCurrentName().length();
    }
    if (_currToken != null) {
      if (_currToken.isNumeric()) {
        return _textBuffer.size();
      }
      return _currToken.asCharArray().length;
    }
    return 0;
  }
  

  public int getTextOffset()
    throws IOException
  {
    if (_currToken != null) {
      switch (_currToken.id()) {
      case 5: 
        return 0;
      case 6: 
        if (_tokenIncomplete) {
          _tokenIncomplete = false;
          _finishString();
        }
      
      case 7: 
      case 8: 
        return _textBuffer.getTextOffset();
      }
      
    }
    return 0;
  }
  
  public byte[] getBinaryValue(Base64Variant b64variant)
    throws IOException
  {
    if ((_currToken != JsonToken.VALUE_STRING) && ((_currToken != JsonToken.VALUE_EMBEDDED_OBJECT) || (_binaryValue == null)))
    {
      _reportError("Current token (" + _currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
    }
    


    if (_tokenIncomplete) {
      try {
        _binaryValue = _decodeBase64(b64variant);
      } catch (IllegalArgumentException iae) {
        throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
      }
      


      _tokenIncomplete = false;
    }
    else if (_binaryValue == null)
    {
      ByteArrayBuilder builder = _getByteArrayBuilder();
      _decodeBase64(getText(), builder, b64variant);
      _binaryValue = builder.toByteArray();
    }
    
    return _binaryValue;
  }
  

  public int readBinaryValue(Base64Variant b64variant, OutputStream out)
    throws IOException
  {
    if ((!_tokenIncomplete) || (_currToken != JsonToken.VALUE_STRING)) {
      byte[] b = getBinaryValue(b64variant);
      out.write(b);
      return b.length;
    }
    
    byte[] buf = _ioContext.allocBase64Buffer();
    try {
      return _readBinary(b64variant, out, buf);
    } finally {
      _ioContext.releaseBase64Buffer(buf);
    }
  }
  
  protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer)
    throws IOException
  {
    int outputPtr = 0;
    int outputEnd = buffer.length - 3;
    int outputCount = 0;
    


    for (;;)
    {
      int ch = _inputData.readUnsignedByte();
      if (ch > 32) {
        int bits = b64variant.decodeBase64Char(ch);
        if (bits < 0) {
          if (ch == 34) {
            break;
          }
          bits = _decodeBase64Escape(b64variant, ch, 0);
          if (bits < 0) {}

        }
        else
        {

          if (outputPtr > outputEnd) {
            outputCount += outputPtr;
            out.write(buffer, 0, outputPtr);
            outputPtr = 0;
          }
          
          int decodedData = bits;
          

          ch = _inputData.readUnsignedByte();
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            bits = _decodeBase64Escape(b64variant, ch, 1);
          }
          decodedData = decodedData << 6 | bits;
          

          ch = _inputData.readUnsignedByte();
          bits = b64variant.decodeBase64Char(ch);
          

          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == 34) {
                decodedData >>= 4;
                buffer[(outputPtr++)] = ((byte)decodedData);
                if (!b64variant.usesPadding()) break;
                _handleBase64MissingPadding(b64variant); break;
              }
              

              bits = _decodeBase64Escape(b64variant, ch, 2);
            }
            if (bits == -2)
            {
              ch = _inputData.readUnsignedByte();
              if ((!b64variant.usesPaddingChar(ch)) && (
                (ch != 92) || 
                (_decodeBase64Escape(b64variant, ch, 3) != -2))) {
                throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
              }
              

              decodedData >>= 4;
              buffer[(outputPtr++)] = ((byte)decodedData);
              continue;
            }
          }
          
          decodedData = decodedData << 6 | bits;
          
          ch = _inputData.readUnsignedByte();
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == 34) {
                decodedData >>= 2;
                buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
                buffer[(outputPtr++)] = ((byte)decodedData);
                if (!b64variant.usesPadding()) break;
                _handleBase64MissingPadding(b64variant); break;
              }
              

              bits = _decodeBase64Escape(b64variant, ch, 3);
            }
            if (bits == -2)
            {





              decodedData >>= 2;
              buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
              buffer[(outputPtr++)] = ((byte)decodedData);
              continue;
            }
          }
          
          decodedData = decodedData << 6 | bits;
          buffer[(outputPtr++)] = ((byte)(decodedData >> 16));
          buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
          buffer[(outputPtr++)] = ((byte)decodedData);
        } } }
    _tokenIncomplete = false;
    if (outputPtr > 0) {
      outputCount += outputPtr;
      out.write(buffer, 0, outputPtr);
    }
    return outputCount;
  }
  










  public JsonToken nextToken()
    throws IOException
  {
    if (_closed) {
      return null;
    }
    



    if (_currToken == JsonToken.FIELD_NAME) {
      return _nextAfterName();
    }
    

    _numTypesValid = 0;
    if (_tokenIncomplete) {
      _skipString();
    }
    int i = _skipWSOrEnd();
    if (i < 0)
    {
      close();
      return this._currToken = null;
    }
    
    _binaryValue = null;
    _tokenInputRow = _currInputRow;
    

    if ((i == 93) || (i == 125)) {
      _closeScope(i);
      return _currToken;
    }
    

    if (_parsingContext.expectComma()) {
      if (i != 44) {
        _reportUnexpectedChar(i, "was expecting comma to separate " + _parsingContext.typeDesc() + " entries");
      }
      i = _skipWS();
      

      if (((_features & FEAT_MASK_TRAILING_COMMA) != 0) && (
        (i == 93) || (i == 125))) {
        _closeScope(i);
        return _currToken;
      }
    }
    





    if (!_parsingContext.inObject()) {
      return _nextTokenNotInObject(i);
    }
    
    String n = _parseName(i);
    _parsingContext.setCurrentName(n);
    _currToken = JsonToken.FIELD_NAME;
    
    i = _skipColon();
    

    if (i == 34) {
      _tokenIncomplete = true;
      _nextToken = JsonToken.VALUE_STRING;
      return _currToken; }
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t; JsonToken t; JsonToken t; JsonToken t; JsonToken t; switch (i) {
    case 45: 
      t = _parseNegNumber();
      break;
    




    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
    case 54: 
    case 55: 
    case 56: 
    case 57: 
      t = _parsePosNumber(i);
      break;
    case 102: 
      _matchToken("false", 1);
      t = JsonToken.VALUE_FALSE;
      break;
    case 110: 
      _matchToken("null", 1);
      t = JsonToken.VALUE_NULL;
      break;
    case 116: 
      _matchToken("true", 1);
      t = JsonToken.VALUE_TRUE;
      break;
    case 91: 
      t = JsonToken.START_ARRAY;
      break;
    case 123: 
      t = JsonToken.START_OBJECT;
      break;
    
    default: 
      t = _handleUnexpectedValue(i);
    }
    _nextToken = t;
    return _currToken;
  }
  
  private final JsonToken _nextTokenNotInObject(int i) throws IOException
  {
    if (i == 34) {
      _tokenIncomplete = true;
      return this._currToken = JsonToken.VALUE_STRING;
    }
    switch (i) {
    case 91: 
      _parsingContext = _parsingContext.createChildArrayContext(_tokenInputRow, _tokenInputCol);
      return this._currToken = JsonToken.START_ARRAY;
    case 123: 
      _parsingContext = _parsingContext.createChildObjectContext(_tokenInputRow, _tokenInputCol);
      return this._currToken = JsonToken.START_OBJECT;
    case 116: 
      _matchToken("true", 1);
      return this._currToken = JsonToken.VALUE_TRUE;
    case 102: 
      _matchToken("false", 1);
      return this._currToken = JsonToken.VALUE_FALSE;
    case 110: 
      _matchToken("null", 1);
      return this._currToken = JsonToken.VALUE_NULL;
    case 45: 
      return this._currToken = _parseNegNumber();
    



    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
    case 54: 
    case 55: 
    case 56: 
    case 57: 
      return this._currToken = _parsePosNumber(i);
    }
    return this._currToken = _handleUnexpectedValue(i);
  }
  
  private final JsonToken _nextAfterName()
  {
    _nameCopied = false;
    JsonToken t = _nextToken;
    _nextToken = null;
    

    if (t == JsonToken.START_ARRAY) {
      _parsingContext = _parsingContext.createChildArrayContext(_tokenInputRow, _tokenInputCol);
    } else if (t == JsonToken.START_OBJECT) {
      _parsingContext = _parsingContext.createChildObjectContext(_tokenInputRow, _tokenInputCol);
    }
    return this._currToken = t;
  }
  
  public void finishToken() throws IOException
  {
    if (_tokenIncomplete) {
      _tokenIncomplete = false;
      _finishString();
    }
  }
  











  public String nextFieldName()
    throws IOException
  {
    _numTypesValid = 0;
    if (_currToken == JsonToken.FIELD_NAME) {
      _nextAfterName();
      return null;
    }
    if (_tokenIncomplete) {
      _skipString();
    }
    int i = _skipWS();
    _binaryValue = null;
    _tokenInputRow = _currInputRow;
    
    if ((i == 93) || (i == 125)) {
      _closeScope(i);
      return null;
    }
    

    if (_parsingContext.expectComma()) {
      if (i != 44) {
        _reportUnexpectedChar(i, "was expecting comma to separate " + _parsingContext.typeDesc() + " entries");
      }
      i = _skipWS();
      

      if (((_features & FEAT_MASK_TRAILING_COMMA) != 0) && (
        (i == 93) || (i == 125))) {
        _closeScope(i);
        return null;
      }
    }
    

    if (!_parsingContext.inObject()) {
      _nextTokenNotInObject(i);
      return null;
    }
    
    String nameStr = _parseName(i);
    _parsingContext.setCurrentName(nameStr);
    _currToken = JsonToken.FIELD_NAME;
    
    i = _skipColon();
    if (i == 34) {
      _tokenIncomplete = true;
      _nextToken = JsonToken.VALUE_STRING;
      return nameStr; }
    JsonToken t;
    JsonToken t;
    JsonToken t; JsonToken t; JsonToken t; JsonToken t; JsonToken t; JsonToken t; switch (i) {
    case 45: 
      t = _parseNegNumber();
      break;
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
    case 54: 
    case 55: 
    case 56: 
    case 57: 
      t = _parsePosNumber(i);
      break;
    case 102: 
      _matchToken("false", 1);
      t = JsonToken.VALUE_FALSE;
      break;
    case 110: 
      _matchToken("null", 1);
      t = JsonToken.VALUE_NULL;
      break;
    case 116: 
      _matchToken("true", 1);
      t = JsonToken.VALUE_TRUE;
      break;
    case 91: 
      t = JsonToken.START_ARRAY;
      break;
    case 123: 
      t = JsonToken.START_OBJECT;
      break;
    
    default: 
      t = _handleUnexpectedValue(i);
    }
    _nextToken = t;
    return nameStr;
  }
  

  public String nextTextValue()
    throws IOException
  {
    if (_currToken == JsonToken.FIELD_NAME) {
      _nameCopied = false;
      JsonToken t = _nextToken;
      _nextToken = null;
      _currToken = t;
      if (t == JsonToken.VALUE_STRING) {
        if (_tokenIncomplete) {
          _tokenIncomplete = false;
          return _finishAndReturnString();
        }
        return _textBuffer.contentsAsString();
      }
      if (t == JsonToken.START_ARRAY) {
        _parsingContext = _parsingContext.createChildArrayContext(_tokenInputRow, _tokenInputCol);
      } else if (t == JsonToken.START_OBJECT) {
        _parsingContext = _parsingContext.createChildObjectContext(_tokenInputRow, _tokenInputCol);
      }
      return null;
    }
    return nextToken() == JsonToken.VALUE_STRING ? getText() : null;
  }
  

  public int nextIntValue(int defaultValue)
    throws IOException
  {
    if (_currToken == JsonToken.FIELD_NAME) {
      _nameCopied = false;
      JsonToken t = _nextToken;
      _nextToken = null;
      _currToken = t;
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return getIntValue();
      }
      if (t == JsonToken.START_ARRAY) {
        _parsingContext = _parsingContext.createChildArrayContext(_tokenInputRow, _tokenInputCol);
      } else if (t == JsonToken.START_OBJECT) {
        _parsingContext = _parsingContext.createChildObjectContext(_tokenInputRow, _tokenInputCol);
      }
      return defaultValue;
    }
    return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : defaultValue;
  }
  

  public long nextLongValue(long defaultValue)
    throws IOException
  {
    if (_currToken == JsonToken.FIELD_NAME) {
      _nameCopied = false;
      JsonToken t = _nextToken;
      _nextToken = null;
      _currToken = t;
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return getLongValue();
      }
      if (t == JsonToken.START_ARRAY) {
        _parsingContext = _parsingContext.createChildArrayContext(_tokenInputRow, _tokenInputCol);
      } else if (t == JsonToken.START_OBJECT) {
        _parsingContext = _parsingContext.createChildObjectContext(_tokenInputRow, _tokenInputCol);
      }
      return defaultValue;
    }
    return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : defaultValue;
  }
  

  public Boolean nextBooleanValue()
    throws IOException
  {
    if (_currToken == JsonToken.FIELD_NAME) {
      _nameCopied = false;
      JsonToken t = _nextToken;
      _nextToken = null;
      _currToken = t;
      if (t == JsonToken.VALUE_TRUE) {
        return Boolean.TRUE;
      }
      if (t == JsonToken.VALUE_FALSE) {
        return Boolean.FALSE;
      }
      if (t == JsonToken.START_ARRAY) {
        _parsingContext = _parsingContext.createChildArrayContext(_tokenInputRow, _tokenInputCol);
      } else if (t == JsonToken.START_OBJECT) {
        _parsingContext = _parsingContext.createChildObjectContext(_tokenInputRow, _tokenInputCol);
      }
      return null;
    }
    
    JsonToken t = nextToken();
    if (t == JsonToken.VALUE_TRUE) {
      return Boolean.TRUE;
    }
    if (t == JsonToken.VALUE_FALSE) {
      return Boolean.FALSE;
    }
    return null;
  }
  




















  protected JsonToken _parsePosNumber(int c)
    throws IOException
  {
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    
    int outPtr;
    
    int outPtr;
    if (c == 48) {
      c = _handleLeadingZeroes();
      int outPtr; if ((c <= 57) && (c >= 48)) {
        outPtr = 0;
      } else {
        outBuf[0] = '0';
        outPtr = 1;
      }
    } else {
      outBuf[0] = ((char)c);
      c = _inputData.readUnsignedByte();
      outPtr = 1;
    }
    int intLen = outPtr;
    

    while ((c <= 57) && (c >= 48)) {
      intLen++;
      outBuf[(outPtr++)] = ((char)c);
      c = _inputData.readUnsignedByte();
    }
    if ((c == 46) || (c == 101) || (c == 69)) {
      return _parseFloat(outBuf, outPtr, c, false, intLen);
    }
    _textBuffer.setCurrentLength(outPtr);
    
    if (_parsingContext.inRoot()) {
      _verifyRootSpace();
    } else {
      _nextByte = c;
    }
    
    return resetInt(false, intLen);
  }
  
  protected JsonToken _parseNegNumber() throws IOException
  {
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int outPtr = 0;
    

    outBuf[(outPtr++)] = '-';
    int c = _inputData.readUnsignedByte();
    outBuf[(outPtr++)] = ((char)c);
    
    if (c <= 48)
    {
      if (c == 48) {
        c = _handleLeadingZeroes();
      } else {
        return _handleInvalidNumberStart(c, true);
      }
    } else {
      if (c > 57) {
        return _handleInvalidNumberStart(c, true);
      }
      c = _inputData.readUnsignedByte();
    }
    
    int intLen = 1;
    

    while ((c <= 57) && (c >= 48)) {
      intLen++;
      outBuf[(outPtr++)] = ((char)c);
      c = _inputData.readUnsignedByte();
    }
    if ((c == 46) || (c == 101) || (c == 69)) {
      return _parseFloat(outBuf, outPtr, c, true, intLen);
    }
    _textBuffer.setCurrentLength(outPtr);
    
    _nextByte = c;
    if (_parsingContext.inRoot()) {
      _verifyRootSpace();
    }
    
    return resetInt(true, intLen);
  }
  






  private final int _handleLeadingZeroes()
    throws IOException
  {
    int ch = _inputData.readUnsignedByte();
    
    if ((ch < 48) || (ch > 57)) {
      return ch;
    }
    
    if ((_features & FEAT_MASK_LEADING_ZEROS) == 0) {
      reportInvalidNumber("Leading zeroes not allowed");
    }
    
    while (ch == 48) {
      ch = _inputData.readUnsignedByte();
    }
    return ch;
  }
  
  private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength)
    throws IOException
  {
    int fractLen = 0;
    

    if (c == 46) {
      outBuf[(outPtr++)] = ((char)c);
      
      for (;;)
      {
        c = _inputData.readUnsignedByte();
        if ((c < 48) || (c > 57)) {
          break;
        }
        fractLen++;
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
        }
        outBuf[(outPtr++)] = ((char)c);
      }
      
      if (fractLen == 0) {
        reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
      }
    }
    
    int expLen = 0;
    if ((c == 101) || (c == 69)) {
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      outBuf[(outPtr++)] = ((char)c);
      c = _inputData.readUnsignedByte();
      
      if ((c == 45) || (c == 43)) {
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
        }
        outBuf[(outPtr++)] = ((char)c);
        c = _inputData.readUnsignedByte();
      }
      while ((c <= 57) && (c >= 48)) {
        expLen++;
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
        }
        outBuf[(outPtr++)] = ((char)c);
        c = _inputData.readUnsignedByte();
      }
      
      if (expLen == 0) {
        reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
      }
    }
    


    _nextByte = c;
    if (_parsingContext.inRoot()) {
      _verifyRootSpace();
    }
    _textBuffer.setCurrentLength(outPtr);
    

    return resetFloat(negative, integerPartLength, fractLen, expLen);
  }
  







  private final void _verifyRootSpace()
    throws IOException
  {
    int ch = _nextByte;
    if (ch <= 32) {
      _nextByte = -1;
      if ((ch == 13) || (ch == 10)) {
        _currInputRow += 1;
      }
      return;
    }
    _reportMissingRootWS(ch);
  }
  





  protected final String _parseName(int i)
    throws IOException
  {
    if (i != 34) {
      return _handleOddName(i);
    }
    





    int[] codes = _icLatin1;
    
    int q = _inputData.readUnsignedByte();
    
    if (codes[q] == 0) {
      i = _inputData.readUnsignedByte();
      if (codes[i] == 0) {
        q = q << 8 | i;
        i = _inputData.readUnsignedByte();
        if (codes[i] == 0) {
          q = q << 8 | i;
          i = _inputData.readUnsignedByte();
          if (codes[i] == 0) {
            q = q << 8 | i;
            i = _inputData.readUnsignedByte();
            if (codes[i] == 0) {
              _quad1 = q;
              return _parseMediumName(i);
            }
            if (i == 34) {
              return findName(q, 4);
            }
            return parseName(q, i, 4);
          }
          if (i == 34) {
            return findName(q, 3);
          }
          return parseName(q, i, 3);
        }
        if (i == 34) {
          return findName(q, 2);
        }
        return parseName(q, i, 2);
      }
      if (i == 34) {
        return findName(q, 1);
      }
      return parseName(q, i, 1);
    }
    if (q == 34) {
      return "";
    }
    return parseName(0, q, 0);
  }
  
  private final String _parseMediumName(int q2) throws IOException
  {
    int[] codes = _icLatin1;
    

    int i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, 1);
      }
      return parseName(_quad1, q2, i, 1);
    }
    q2 = q2 << 8 | i;
    i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, 2);
      }
      return parseName(_quad1, q2, i, 2);
    }
    q2 = q2 << 8 | i;
    i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, 3);
      }
      return parseName(_quad1, q2, i, 3);
    }
    q2 = q2 << 8 | i;
    i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, 4);
      }
      return parseName(_quad1, q2, i, 4);
    }
    return _parseMediumName2(i, q2);
  }
  
  private final String _parseMediumName2(int q3, int q2) throws IOException
  {
    int[] codes = _icLatin1;
    

    int i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, q3, 1);
      }
      return parseName(_quad1, q2, q3, i, 1);
    }
    q3 = q3 << 8 | i;
    i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, q3, 2);
      }
      return parseName(_quad1, q2, q3, i, 2);
    }
    q3 = q3 << 8 | i;
    i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, q3, 3);
      }
      return parseName(_quad1, q2, q3, i, 3);
    }
    q3 = q3 << 8 | i;
    i = _inputData.readUnsignedByte();
    if (codes[i] != 0) {
      if (i == 34) {
        return findName(_quad1, q2, q3, 4);
      }
      return parseName(_quad1, q2, q3, i, 4);
    }
    return _parseLongName(i, q2, q3);
  }
  
  private final String _parseLongName(int q, int q2, int q3) throws IOException
  {
    _quadBuffer[0] = _quad1;
    _quadBuffer[1] = q2;
    _quadBuffer[2] = q3;
    

    int[] codes = _icLatin1;
    int qlen = 3;
    for (;;)
    {
      int i = _inputData.readUnsignedByte();
      if (codes[i] != 0) {
        if (i == 34) {
          return findName(_quadBuffer, qlen, q, 1);
        }
        return parseEscapedName(_quadBuffer, qlen, q, i, 1);
      }
      
      q = q << 8 | i;
      i = _inputData.readUnsignedByte();
      if (codes[i] != 0) {
        if (i == 34) {
          return findName(_quadBuffer, qlen, q, 2);
        }
        return parseEscapedName(_quadBuffer, qlen, q, i, 2);
      }
      
      q = q << 8 | i;
      i = _inputData.readUnsignedByte();
      if (codes[i] != 0) {
        if (i == 34) {
          return findName(_quadBuffer, qlen, q, 3);
        }
        return parseEscapedName(_quadBuffer, qlen, q, i, 3);
      }
      
      q = q << 8 | i;
      i = _inputData.readUnsignedByte();
      if (codes[i] != 0) {
        if (i == 34) {
          return findName(_quadBuffer, qlen, q, 4);
        }
        return parseEscapedName(_quadBuffer, qlen, q, i, 4);
      }
      

      if (qlen >= _quadBuffer.length) {
        _quadBuffer = _growArrayBy(_quadBuffer, qlen);
      }
      _quadBuffer[(qlen++)] = q;
      q = i;
    }
  }
  
  private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
    return parseEscapedName(_quadBuffer, 0, q1, ch, lastQuadBytes);
  }
  
  private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
    _quadBuffer[0] = q1;
    return parseEscapedName(_quadBuffer, 1, q2, ch, lastQuadBytes);
  }
  
  private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
    _quadBuffer[0] = q1;
    _quadBuffer[1] = q2;
    return parseEscapedName(_quadBuffer, 2, q3, ch, lastQuadBytes);
  }
  











  protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes)
    throws IOException
  {
    int[] codes = _icLatin1;
    for (;;)
    {
      if (codes[ch] != 0) {
        if (ch == 34) {
          break;
        }
        
        if (ch != 92)
        {
          _throwUnquotedSpace(ch, "name");
        }
        else {
          ch = _decodeEscaped();
        }
        




        if (ch > 127)
        {
          if (currQuadBytes >= 4) {
            if (qlen >= quads.length) {
              _quadBuffer = (quads = _growArrayBy(quads, quads.length));
            }
            quads[(qlen++)] = currQuad;
            currQuad = 0;
            currQuadBytes = 0;
          }
          if (ch < 2048) {
            currQuad = currQuad << 8 | 0xC0 | ch >> 6;
            currQuadBytes++;
          }
          else {
            currQuad = currQuad << 8 | 0xE0 | ch >> 12;
            currQuadBytes++;
            
            if (currQuadBytes >= 4) {
              if (qlen >= quads.length) {
                _quadBuffer = (quads = _growArrayBy(quads, quads.length));
              }
              quads[(qlen++)] = currQuad;
              currQuad = 0;
              currQuadBytes = 0;
            }
            currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
            currQuadBytes++;
          }
          
          ch = 0x80 | ch & 0x3F;
        }
      }
      
      if (currQuadBytes < 4) {
        currQuadBytes++;
        currQuad = currQuad << 8 | ch;
      } else {
        if (qlen >= quads.length) {
          _quadBuffer = (quads = _growArrayBy(quads, quads.length));
        }
        quads[(qlen++)] = currQuad;
        currQuad = ch;
        currQuadBytes = 1;
      }
      ch = _inputData.readUnsignedByte();
    }
    
    if (currQuadBytes > 0) {
      if (qlen >= quads.length) {
        _quadBuffer = (quads = _growArrayBy(quads, quads.length));
      }
      quads[(qlen++)] = pad(currQuad, currQuadBytes);
    }
    String name = _symbols.findName(quads, qlen);
    if (name == null) {
      name = addName(quads, qlen, currQuadBytes);
    }
    return name;
  }
  





  protected String _handleOddName(int ch)
    throws IOException
  {
    if ((ch == 39) && ((_features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0)) {
      return _parseAposName();
    }
    if ((_features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
      char c = (char)_decodeCharForError(ch);
      _reportUnexpectedChar(c, "was expecting double-quote to start field name");
    }
    



    int[] codes = CharTypes.getInputCodeUtf8JsNames();
    
    if (codes[ch] != 0) {
      _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
    }
    




    int[] quads = _quadBuffer;
    int qlen = 0;
    int currQuad = 0;
    int currQuadBytes = 0;
    
    for (;;)
    {
      if (currQuadBytes < 4) {
        currQuadBytes++;
        currQuad = currQuad << 8 | ch;
      } else {
        if (qlen >= quads.length) {
          _quadBuffer = (quads = _growArrayBy(quads, quads.length));
        }
        quads[(qlen++)] = currQuad;
        currQuad = ch;
        currQuadBytes = 1;
      }
      ch = _inputData.readUnsignedByte();
      if (codes[ch] != 0) {
        break;
      }
    }
    
    _nextByte = ch;
    if (currQuadBytes > 0) {
      if (qlen >= quads.length) {
        _quadBuffer = (quads = _growArrayBy(quads, quads.length));
      }
      quads[(qlen++)] = currQuad;
    }
    String name = _symbols.findName(quads, qlen);
    if (name == null) {
      name = addName(quads, qlen, currQuadBytes);
    }
    return name;
  }
  





  protected String _parseAposName()
    throws IOException
  {
    int ch = _inputData.readUnsignedByte();
    if (ch == 39) {
      return "";
    }
    int[] quads = _quadBuffer;
    int qlen = 0;
    int currQuad = 0;
    int currQuadBytes = 0;
    


    int[] codes = _icLatin1;
    

    while (ch != 39)
    {


      if ((ch != 34) && (codes[ch] != 0)) {
        if (ch != 92)
        {

          _throwUnquotedSpace(ch, "name");
        }
        else {
          ch = _decodeEscaped();
        }
        



        if (ch > 127)
        {
          if (currQuadBytes >= 4) {
            if (qlen >= quads.length) {
              _quadBuffer = (quads = _growArrayBy(quads, quads.length));
            }
            quads[(qlen++)] = currQuad;
            currQuad = 0;
            currQuadBytes = 0;
          }
          if (ch < 2048) {
            currQuad = currQuad << 8 | 0xC0 | ch >> 6;
            currQuadBytes++;
          }
          else {
            currQuad = currQuad << 8 | 0xE0 | ch >> 12;
            currQuadBytes++;
            
            if (currQuadBytes >= 4) {
              if (qlen >= quads.length) {
                _quadBuffer = (quads = _growArrayBy(quads, quads.length));
              }
              quads[(qlen++)] = currQuad;
              currQuad = 0;
              currQuadBytes = 0;
            }
            currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
            currQuadBytes++;
          }
          
          ch = 0x80 | ch & 0x3F;
        }
      }
      
      if (currQuadBytes < 4) {
        currQuadBytes++;
        currQuad = currQuad << 8 | ch;
      } else {
        if (qlen >= quads.length) {
          _quadBuffer = (quads = _growArrayBy(quads, quads.length));
        }
        quads[(qlen++)] = currQuad;
        currQuad = ch;
        currQuadBytes = 1;
      }
      ch = _inputData.readUnsignedByte();
    }
    
    if (currQuadBytes > 0) {
      if (qlen >= quads.length) {
        _quadBuffer = (quads = _growArrayBy(quads, quads.length));
      }
      quads[(qlen++)] = pad(currQuad, currQuadBytes);
    }
    String name = _symbols.findName(quads, qlen);
    if (name == null) {
      name = addName(quads, qlen, currQuadBytes);
    }
    return name;
  }
  





  private final String findName(int q1, int lastQuadBytes)
    throws JsonParseException
  {
    q1 = pad(q1, lastQuadBytes);
    
    String name = _symbols.findName(q1);
    if (name != null) {
      return name;
    }
    
    _quadBuffer[0] = q1;
    return addName(_quadBuffer, 1, lastQuadBytes);
  }
  
  private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException
  {
    q2 = pad(q2, lastQuadBytes);
    
    String name = _symbols.findName(q1, q2);
    if (name != null) {
      return name;
    }
    
    _quadBuffer[0] = q1;
    _quadBuffer[1] = q2;
    return addName(_quadBuffer, 2, lastQuadBytes);
  }
  
  private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException
  {
    q3 = pad(q3, lastQuadBytes);
    String name = _symbols.findName(q1, q2, q3);
    if (name != null) {
      return name;
    }
    int[] quads = _quadBuffer;
    quads[0] = q1;
    quads[1] = q2;
    quads[2] = pad(q3, lastQuadBytes);
    return addName(quads, 3, lastQuadBytes);
  }
  
  private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException
  {
    if (qlen >= quads.length) {
      _quadBuffer = (quads = _growArrayBy(quads, quads.length));
    }
    quads[(qlen++)] = pad(lastQuad, lastQuadBytes);
    String name = _symbols.findName(quads, qlen);
    if (name == null) {
      return addName(quads, qlen, lastQuadBytes);
    }
    return name;
  }
  










  private final String addName(int[] quads, int qlen, int lastQuadBytes)
    throws JsonParseException
  {
    int byteLen = (qlen << 2) - 4 + lastQuadBytes;
    



    int lastQuad;
    


    if (lastQuadBytes < 4) {
      int lastQuad = quads[(qlen - 1)];
      
      quads[(qlen - 1)] = (lastQuad << (4 - lastQuadBytes << 3));
    } else {
      lastQuad = 0;
    }
    

    char[] cbuf = _textBuffer.emptyAndGetCurrentSegment();
    int cix = 0;
    
    for (int ix = 0; ix < byteLen;) {
      int ch = quads[(ix >> 2)];
      int byteIx = ix & 0x3;
      ch = ch >> (3 - byteIx << 3) & 0xFF;
      ix++;
      
      if (ch > 127) { int needed;
        int needed;
        if ((ch & 0xE0) == 192) {
          ch &= 0x1F;
          needed = 1; } else { int needed;
          if ((ch & 0xF0) == 224) {
            ch &= 0xF;
            needed = 2; } else { int needed;
            if ((ch & 0xF8) == 240) {
              ch &= 0x7;
              needed = 3;
            } else {
              _reportInvalidInitial(ch);
              needed = ch = 1;
            } } }
        if (ix + needed > byteLen) {
          _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
        }
        

        int ch2 = quads[(ix >> 2)];
        byteIx = ix & 0x3;
        ch2 >>= 3 - byteIx << 3;
        ix++;
        
        if ((ch2 & 0xC0) != 128) {
          _reportInvalidOther(ch2);
        }
        ch = ch << 6 | ch2 & 0x3F;
        if (needed > 1) {
          ch2 = quads[(ix >> 2)];
          byteIx = ix & 0x3;
          ch2 >>= 3 - byteIx << 3;
          ix++;
          
          if ((ch2 & 0xC0) != 128) {
            _reportInvalidOther(ch2);
          }
          ch = ch << 6 | ch2 & 0x3F;
          if (needed > 2) {
            ch2 = quads[(ix >> 2)];
            byteIx = ix & 0x3;
            ch2 >>= 3 - byteIx << 3;
            ix++;
            if ((ch2 & 0xC0) != 128) {
              _reportInvalidOther(ch2 & 0xFF);
            }
            ch = ch << 6 | ch2 & 0x3F;
          }
        }
        if (needed > 2) {
          ch -= 65536;
          if (cix >= cbuf.length) {
            cbuf = _textBuffer.expandCurrentSegment();
          }
          cbuf[(cix++)] = ((char)(55296 + (ch >> 10)));
          ch = 0xDC00 | ch & 0x3FF;
        }
      }
      if (cix >= cbuf.length) {
        cbuf = _textBuffer.expandCurrentSegment();
      }
      cbuf[(cix++)] = ((char)ch);
    }
    

    String baseName = new String(cbuf, 0, cix);
    
    if (lastQuadBytes < 4) {
      quads[(qlen - 1)] = lastQuad;
    }
    return _symbols.addName(baseName, quads, qlen);
  }
  






  protected void _finishString()
    throws IOException
  {
    int outPtr = 0;
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int[] codes = _icUTF8;
    int outEnd = outBuf.length;
    do
    {
      int c = _inputData.readUnsignedByte();
      if (codes[c] != 0) {
        if (c == 34) {
          _textBuffer.setCurrentLength(outPtr);
          return;
        }
        _finishString2(outBuf, outPtr, c);
        return;
      }
      outBuf[(outPtr++)] = ((char)c);
    } while (outPtr < outEnd);
    _finishString2(outBuf, outPtr, _inputData.readUnsignedByte());
  }
  
  private String _finishAndReturnString() throws IOException
  {
    int outPtr = 0;
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int[] codes = _icUTF8;
    int outEnd = outBuf.length;
    do
    {
      int c = _inputData.readUnsignedByte();
      if (codes[c] != 0) {
        if (c == 34) {
          return _textBuffer.setCurrentAndReturn(outPtr);
        }
        _finishString2(outBuf, outPtr, c);
        return _textBuffer.contentsAsString();
      }
      outBuf[(outPtr++)] = ((char)c);
    } while (outPtr < outEnd);
    _finishString2(outBuf, outPtr, _inputData.readUnsignedByte());
    return _textBuffer.contentsAsString();
  }
  

  private final void _finishString2(char[] outBuf, int outPtr, int c)
    throws IOException
  {
    int[] codes = _icUTF8;
    int outEnd = outBuf.length;
    

    for (;;)
    {
      if (codes[c] == 0) {
        if (outPtr >= outEnd) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
          outEnd = outBuf.length;
        }
        outBuf[(outPtr++)] = ((char)c);
        c = _inputData.readUnsignedByte();
      }
      else {
        if (c == 34) {
          break;
        }
        switch (codes[c]) {
        case 1: 
          c = _decodeEscaped();
          break;
        case 2: 
          c = _decodeUtf8_2(c);
          break;
        case 3: 
          c = _decodeUtf8_3(c);
          break;
        case 4: 
          c = _decodeUtf8_4(c);
          
          outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
          if (outPtr >= outBuf.length) {
            outBuf = _textBuffer.finishCurrentSegment();
            outPtr = 0;
            outEnd = outBuf.length;
          }
          c = 0xDC00 | c & 0x3FF;
          
          break;
        default: 
          if (c < 32) {
            _throwUnquotedSpace(c, "string value");
          }
          else {
            _reportInvalidChar(c);
          }
          break;
        }
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
          outEnd = outBuf.length;
        }
        
        outBuf[(outPtr++)] = ((char)c);c = _inputData.readUnsignedByte();
      } }
    _textBuffer.setCurrentLength(outPtr);
  }
  




  protected void _skipString()
    throws IOException
  {
    _tokenIncomplete = false;
    

    int[] codes = _icUTF8;
    




    for (;;)
    {
      int c = _inputData.readUnsignedByte();
      if (codes[c] != 0)
      {



        if (c == 34) {
          break;
        }
        
        switch (codes[c]) {
        case 1: 
          _decodeEscaped();
          break;
        case 2: 
          _skipUtf8_2();
          break;
        case 3: 
          _skipUtf8_3();
          break;
        case 4: 
          _skipUtf8_4();
          break;
        default: 
          if (c < 32) {
            _throwUnquotedSpace(c, "string value");
          }
          else {
            _reportInvalidChar(c);
          }
          
          break;
        }
        
      }
    }
  }
  

  protected JsonToken _handleUnexpectedValue(int c)
    throws IOException
  {
    switch (c) {
    case 93: 
      if (!_parsingContext.inArray()) {}
      




      break;
    case 44: 
      if ((_features & FEAT_MASK_ALLOW_MISSING) != 0)
      {
        _nextByte = c;
        return JsonToken.VALUE_NULL;
      }
    


    case 125: 
      _reportUnexpectedChar(c, "expected a value");
    case 39: 
      if ((_features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
        return _handleApos();
      }
      break;
    case 78: 
      _matchToken("NaN", 1);
      if ((_features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
        return resetAsNaN("NaN", NaN.0D);
      }
      _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
      break;
    case 73: 
      _matchToken("Infinity", 1);
      if ((_features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
        return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
      }
      _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
      break;
    case 43: 
      return _handleInvalidNumberStart(_inputData.readUnsignedByte(), false);
    }
    
    if (Character.isJavaIdentifierStart(c)) {
      _reportInvalidToken(c, "" + (char)c, _validJsonTokenList());
    }
    
    _reportUnexpectedChar(c, "expected a valid value " + _validJsonValueList());
    return null;
  }
  
  protected JsonToken _handleApos() throws IOException
  {
    int c = 0;
    
    int outPtr = 0;
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    

    int[] codes = _icUTF8;
    



    for (;;)
    {
      int outEnd = outBuf.length;
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
        outEnd = outBuf.length;
      }
      do {
        c = _inputData.readUnsignedByte();
        if (c == 39) {
          break label239;
        }
        if (codes[c] != 0) {
          break;
        }
        outBuf[(outPtr++)] = ((char)c);
      } while (outPtr < outEnd);
      continue;
      switch (codes[c]) {
      case 1: 
        c = _decodeEscaped();
        break;
      case 2: 
        c = _decodeUtf8_2(c);
        break;
      case 3: 
        c = _decodeUtf8_3(c);
        break;
      case 4: 
        c = _decodeUtf8_4(c);
        
        outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
        }
        c = 0xDC00 | c & 0x3FF;
        
        break;
      default: 
        if (c < 32) {
          _throwUnquotedSpace(c, "string value");
        }
        
        _reportInvalidChar(c);
      }
      
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      
      outBuf[(outPtr++)] = ((char)c); }
    label239:
    _textBuffer.setCurrentLength(outPtr);
    
    return JsonToken.VALUE_STRING;
  }
  




  protected JsonToken _handleInvalidNumberStart(int ch, boolean neg)
    throws IOException
  {
    while (ch == 73) {
      ch = _inputData.readUnsignedByte();
      String match;
      String match; if (ch == 78) {
        match = neg ? "-INF" : "+INF";
      } else { if (ch != 110) break;
        match = neg ? "-Infinity" : "+Infinity";
      }
      

      _matchToken(match, 3);
      if ((_features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
        return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
      }
      _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
    }
    reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
    return null;
  }
  
  protected final void _matchToken(String matchStr, int i) throws IOException
  {
    int len = matchStr.length();
    do {
      int ch = _inputData.readUnsignedByte();
      if (ch != matchStr.charAt(i)) {
        _reportInvalidToken(ch, matchStr.substring(0, i));
      }
      i++; } while (i < len);
    
    int ch = _inputData.readUnsignedByte();
    if ((ch >= 48) && (ch != 93) && (ch != 125)) {
      _checkMatchEnd(matchStr, i, ch);
    }
    _nextByte = ch;
  }
  
  private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException
  {
    char c = (char)_decodeCharForError(ch);
    if (Character.isJavaIdentifierPart(c)) {
      _reportInvalidToken(c, matchStr.substring(0, i));
    }
  }
  





  private final int _skipWS()
    throws IOException
  {
    int i = _nextByte;
    if (i < 0) {
      i = _inputData.readUnsignedByte();
    } else {
      _nextByte = -1;
    }
    for (;;) {
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          return _skipWSComment(i);
        }
        return i;
      }
      

      if ((i == 13) || (i == 10)) {
        _currInputRow += 1;
      }
      
      i = _inputData.readUnsignedByte();
    }
  }
  





  private final int _skipWSOrEnd()
    throws IOException
  {
    int i = _nextByte;
    if (i < 0) {
      try {
        i = _inputData.readUnsignedByte();
      } catch (EOFException e) {
        return _eofAsNextChar();
      }
    } else {
      _nextByte = -1;
    }
    for (;;) {
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          return _skipWSComment(i);
        }
        return i;
      }
      

      if ((i == 13) || (i == 10)) {
        _currInputRow += 1;
      }
      try
      {
        i = _inputData.readUnsignedByte();
      } catch (EOFException e) {} }
    return _eofAsNextChar();
  }
  
  private final int _skipWSComment(int i)
    throws IOException
  {
    for (;;)
    {
      if (i > 32) {
        if (i == 47) {
          _skipComment();
        } else if (i == 35) {
          if (!_skipYAMLComment()) {
            return i;
          }
        } else {
          return i;
        }
        

      }
      else if ((i == 13) || (i == 10)) {
        _currInputRow += 1;
      }
      





      i = _inputData.readUnsignedByte();
    }
  }
  
  private final int _skipColon() throws IOException
  {
    int i = _nextByte;
    if (i < 0) {
      i = _inputData.readUnsignedByte();
    } else {
      _nextByte = -1;
    }
    
    if (i == 58) {
      i = _inputData.readUnsignedByte();
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          return _skipColon2(i, true);
        }
        return i;
      }
      if ((i == 32) || (i == 9)) {
        i = _inputData.readUnsignedByte();
        if (i > 32) {
          if ((i == 47) || (i == 35)) {
            return _skipColon2(i, true);
          }
          return i;
        }
      }
      return _skipColon2(i, true);
    }
    if ((i == 32) || (i == 9)) {
      i = _inputData.readUnsignedByte();
    }
    if (i == 58) {
      i = _inputData.readUnsignedByte();
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          return _skipColon2(i, true);
        }
        return i;
      }
      if ((i == 32) || (i == 9)) {
        i = _inputData.readUnsignedByte();
        if (i > 32) {
          if ((i == 47) || (i == 35)) {
            return _skipColon2(i, true);
          }
          return i;
        }
      }
      return _skipColon2(i, true);
    }
    return _skipColon2(i, false);
  }
  
  private final int _skipColon2(int i, boolean gotColon) throws IOException
  {
    for (;; i = _inputData.readUnsignedByte()) {
      if (i > 32) {
        if (i == 47) {
          _skipComment();

        }
        else if ((i != 35) || 
          (!_skipYAMLComment()))
        {


          if (gotColon) {
            return i;
          }
          if (i != 58) {
            _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
          }
          gotColon = true;
        }
        
      }
      else if ((i == 13) || (i == 10)) {
        _currInputRow += 1;
      }
    }
  }
  
  private final void _skipComment()
    throws IOException
  {
    if ((_features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
      _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
    }
    int c = _inputData.readUnsignedByte();
    if (c == 47) {
      _skipLine();
    } else if (c == 42) {
      _skipCComment();
    } else {
      _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
    }
  }
  
  private final void _skipCComment()
    throws IOException
  {
    int[] codes = CharTypes.getInputCodeComment();
    int i = _inputData.readUnsignedByte();
    

    for (;;)
    {
      int code = codes[i];
      if (code != 0) {
        switch (code) {
        case 42: 
          i = _inputData.readUnsignedByte();
          if (i != 47) continue;
          return;
        

        case 10: 
        case 13: 
          _currInputRow += 1;
          break;
        case 2: 
          _skipUtf8_2();
          break;
        case 3: 
          _skipUtf8_3();
          break;
        case 4: 
          _skipUtf8_4();
          break;
        
        default: 
          _reportInvalidChar(i);
        }
      } else {
        i = _inputData.readUnsignedByte();
      }
    }
  }
  
  private final boolean _skipYAMLComment() throws IOException {
    if ((_features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
      return false;
    }
    _skipLine();
    return true;
  }
  




  private final void _skipLine()
    throws IOException
  {
    int[] codes = CharTypes.getInputCodeComment();
    for (;;) {
      int i = _inputData.readUnsignedByte();
      int code = codes[i];
      if (code != 0) {
        switch (code) {
        case 10: 
        case 13: 
          _currInputRow += 1;
          return;
        case 42: 
          break;
        case 2: 
          _skipUtf8_2();
          break;
        case 3: 
          _skipUtf8_3();
          break;
        case 4: 
          _skipUtf8_4();
          break;
        default: 
          if (code < 0)
          {
            _reportInvalidChar(i);
          }
          break;
        }
      }
    }
  }
  
  protected char _decodeEscaped() throws IOException
  {
    int c = _inputData.readUnsignedByte();
    
    switch (c)
    {
    case 98: 
      return '\b';
    case 116: 
      return '\t';
    case 110: 
      return '\n';
    case 102: 
      return '\f';
    case 114: 
      return '\r';
    

    case 34: 
    case 47: 
    case 92: 
      return (char)c;
    
    case 117: 
      break;
    
    default: 
      return _handleUnrecognizedCharacterEscape((char)_decodeCharForError(c));
    }
    
    
    int value = 0;
    for (int i = 0; i < 4; i++) {
      int ch = _inputData.readUnsignedByte();
      int digit = CharTypes.charToHex(ch);
      if (digit < 0) {
        _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
      }
      value = value << 4 | digit;
    }
    return (char)value;
  }
  
  protected int _decodeCharForError(int firstByte) throws IOException
  {
    int c = firstByte & 0xFF;
    if (c > 127)
    {
      int needed;
      int needed;
      if ((c & 0xE0) == 192) {
        c &= 0x1F;
        needed = 1; } else { int needed;
        if ((c & 0xF0) == 224) {
          c &= 0xF;
          needed = 2; } else { int needed;
          if ((c & 0xF8) == 240)
          {
            c &= 0x7;
            needed = 3;
          } else {
            _reportInvalidInitial(c & 0xFF);
            needed = 1;
          }
        } }
      int d = _inputData.readUnsignedByte();
      if ((d & 0xC0) != 128) {
        _reportInvalidOther(d & 0xFF);
      }
      c = c << 6 | d & 0x3F;
      
      if (needed > 1) {
        d = _inputData.readUnsignedByte();
        if ((d & 0xC0) != 128) {
          _reportInvalidOther(d & 0xFF);
        }
        c = c << 6 | d & 0x3F;
        if (needed > 2) {
          d = _inputData.readUnsignedByte();
          if ((d & 0xC0) != 128) {
            _reportInvalidOther(d & 0xFF);
          }
          c = c << 6 | d & 0x3F;
        }
      }
    }
    return c;
  }
  





  private final int _decodeUtf8_2(int c)
    throws IOException
  {
    int d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    return (c & 0x1F) << 6 | d & 0x3F;
  }
  
  private final int _decodeUtf8_3(int c1) throws IOException
  {
    c1 &= 0xF;
    int d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    int c = c1 << 6 | d & 0x3F;
    d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    c = c << 6 | d & 0x3F;
    return c;
  }
  



  private final int _decodeUtf8_4(int c)
    throws IOException
  {
    int d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    c = (c & 0x7) << 6 | d & 0x3F;
    d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    c = c << 6 | d & 0x3F;
    d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    



    return (c << 6 | d & 0x3F) - 65536;
  }
  
  private final void _skipUtf8_2() throws IOException
  {
    int c = _inputData.readUnsignedByte();
    if ((c & 0xC0) != 128) {
      _reportInvalidOther(c & 0xFF);
    }
  }
  



  private final void _skipUtf8_3()
    throws IOException
  {
    int c = _inputData.readUnsignedByte();
    if ((c & 0xC0) != 128) {
      _reportInvalidOther(c & 0xFF);
    }
    c = _inputData.readUnsignedByte();
    if ((c & 0xC0) != 128) {
      _reportInvalidOther(c & 0xFF);
    }
  }
  
  private final void _skipUtf8_4() throws IOException
  {
    int d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
    d = _inputData.readUnsignedByte();
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF);
    }
  }
  





  protected void _reportInvalidToken(int ch, String matchedPart)
    throws IOException
  {
    _reportInvalidToken(ch, matchedPart, _validJsonTokenList());
  }
  
  protected void _reportInvalidToken(int ch, String matchedPart, String msg)
    throws IOException
  {
    StringBuilder sb = new StringBuilder(matchedPart);
    



    for (;;)
    {
      char c = (char)_decodeCharForError(ch);
      if (!Character.isJavaIdentifierPart(c)) {
        break;
      }
      sb.append(c);
      ch = _inputData.readUnsignedByte();
    }
    _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
  }
  

  protected void _reportInvalidChar(int c)
    throws JsonParseException
  {
    if (c < 32) {
      _throwInvalidSpace(c);
    }
    _reportInvalidInitial(c);
  }
  
  protected void _reportInvalidInitial(int mask)
    throws JsonParseException
  {
    _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
  }
  
  private void _reportInvalidOther(int mask)
    throws JsonParseException
  {
    _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
  }
  
  private static int[] _growArrayBy(int[] arr, int more)
  {
    if (arr == null) {
      return new int[more];
    }
    return Arrays.copyOf(arr, arr.length + more);
  }
  










  protected final byte[] _decodeBase64(Base64Variant b64variant)
    throws IOException
  {
    ByteArrayBuilder builder = _getByteArrayBuilder();
    



    for (;;)
    {
      int ch = _inputData.readUnsignedByte();
      if (ch > 32) {
        int bits = b64variant.decodeBase64Char(ch);
        if (bits < 0) {
          if (ch == 34) {
            return builder.toByteArray();
          }
          bits = _decodeBase64Escape(b64variant, ch, 0);
          if (bits < 0) {}
        }
        else
        {
          int decodedData = bits;
          

          ch = _inputData.readUnsignedByte();
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            bits = _decodeBase64Escape(b64variant, ch, 1);
          }
          decodedData = decodedData << 6 | bits;
          
          ch = _inputData.readUnsignedByte();
          bits = b64variant.decodeBase64Char(ch);
          

          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == 34) {
                decodedData >>= 4;
                builder.append(decodedData);
                if (b64variant.usesPadding()) {
                  _handleBase64MissingPadding(b64variant);
                }
                return builder.toByteArray();
              }
              bits = _decodeBase64Escape(b64variant, ch, 2);
            }
            if (bits == -2) {
              ch = _inputData.readUnsignedByte();
              if ((!b64variant.usesPaddingChar(ch)) && (
                (ch != 92) || 
                (_decodeBase64Escape(b64variant, ch, 3) != -2))) {
                throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
              }
              

              decodedData >>= 4;
              builder.append(decodedData);
              continue;
            }
          }
          
          decodedData = decodedData << 6 | bits;
          
          ch = _inputData.readUnsignedByte();
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == 34) {
                decodedData >>= 2;
                builder.appendTwoBytes(decodedData);
                if (b64variant.usesPadding()) {
                  _handleBase64MissingPadding(b64variant);
                }
                return builder.toByteArray();
              }
              bits = _decodeBase64Escape(b64variant, ch, 3);
            }
            if (bits == -2)
            {





              decodedData >>= 2;
              builder.appendTwoBytes(decodedData);
              continue;
            }
          }
          
          decodedData = decodedData << 6 | bits;
          builder.appendThreeBytes(decodedData);
        }
      }
    }
  }
  




  public JsonLocation getTokenLocation()
  {
    return new JsonLocation(_getSourceReference(), -1L, -1L, _tokenInputRow, -1);
  }
  
  public JsonLocation getCurrentLocation()
  {
    return new JsonLocation(_getSourceReference(), -1L, -1L, _currInputRow, -1);
  }
  




  private void _closeScope(int i)
    throws JsonParseException
  {
    if (i == 93) {
      if (!_parsingContext.inArray()) {
        _reportMismatchedEndMarker(i, '}');
      }
      _parsingContext = _parsingContext.clearAndGetParent();
      _currToken = JsonToken.END_ARRAY;
    }
    if (i == 125) {
      if (!_parsingContext.inObject()) {
        _reportMismatchedEndMarker(i, ']');
      }
      _parsingContext = _parsingContext.clearAndGetParent();
      _currToken = JsonToken.END_OBJECT;
    }
  }
  


  private static final int pad(int q, int bytes)
  {
    return bytes == 4 ? q : q | -1 << (bytes << 3);
  }
}
