package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class ReaderBasedJsonParser extends ParserBase
{
  private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
  

  private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
  

  private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
  

  private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
  private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
  private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
  
  private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
  private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
  


  protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
  







  protected Reader _reader;
  






  protected char[] _inputBuffer;
  






  protected boolean _bufferRecyclable;
  






  protected ObjectCodec _objectCodec;
  






  protected final CharsToNameCanonicalizer _symbols;
  






  protected final int _hashSeed;
  






  protected boolean _tokenIncomplete;
  






  protected long _nameStartOffset;
  






  protected int _nameStartRow;
  






  protected int _nameStartCol;
  







  public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st, char[] inputBuffer, int start, int end, boolean bufferRecyclable)
  {
    super(ctxt, features);
    _reader = r;
    _inputBuffer = inputBuffer;
    _inputPtr = start;
    _inputEnd = end;
    _objectCodec = codec;
    _symbols = st;
    _hashSeed = st.hashSeed();
    _bufferRecyclable = bufferRecyclable;
  }
  





  public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st)
  {
    super(ctxt, features);
    _reader = r;
    _inputBuffer = ctxt.allocTokenBuffer();
    _inputPtr = 0;
    _inputEnd = 0;
    _objectCodec = codec;
    _symbols = st;
    _hashSeed = st.hashSeed();
    _bufferRecyclable = true;
  }
  






  public ObjectCodec getCodec() { return _objectCodec; }
  public void setCodec(ObjectCodec c) { _objectCodec = c; }
  
  public int releaseBuffered(Writer w) throws IOException
  {
    int count = _inputEnd - _inputPtr;
    if (count < 1) { return 0;
    }
    int origPtr = _inputPtr;
    w.write(_inputBuffer, origPtr, count);
    return count;
  }
  
  public Object getInputSource() { return _reader; }
  
  @Deprecated
  protected char getNextChar(String eofMsg) throws IOException {
    return getNextChar(eofMsg, null);
  }
  
  protected char getNextChar(String eofMsg, JsonToken forToken) throws IOException {
    if ((_inputPtr >= _inputEnd) && 
      (!_loadMore())) {
      _reportInvalidEOF(eofMsg, forToken);
    }
    
    return _inputBuffer[(_inputPtr++)];
  }
  






  protected void _closeInput()
    throws IOException
  {
    if (_reader != null) {
      if ((_ioContext.isResourceManaged()) || (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE))) {
        _reader.close();
      }
      _reader = null;
    }
  }
  





  protected void _releaseBuffers()
    throws IOException
  {
    super._releaseBuffers();
    
    _symbols.release();
    
    if (_bufferRecyclable) {
      char[] buf = _inputBuffer;
      if (buf != null) {
        _inputBuffer = null;
        _ioContext.releaseTokenBuffer(buf);
      }
    }
  }
  




  protected void _loadMoreGuaranteed()
    throws IOException
  {
    if (!_loadMore()) _reportInvalidEOF();
  }
  
  protected boolean _loadMore() throws IOException
  {
    int bufSize = _inputEnd;
    
    if (_reader != null) {
      int count = _reader.read(_inputBuffer, 0, _inputBuffer.length);
      if (count > 0) {
        _inputPtr = 0;
        _inputEnd = count;
        
        _currInputProcessed += bufSize;
        _currInputRowStart -= bufSize;
        



        _nameStartOffset -= bufSize;
        
        return true;
      }
      
      _closeInput();
      
      if (count == 0) {
        throw new IOException("Reader returned 0 characters when trying to read " + _inputEnd);
      }
    }
    return false;
  }
  












  public final String getText()
    throws IOException
  {
    JsonToken t = _currToken;
    if (t == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        _finishString();
      }
      return _textBuffer.contentsAsString();
    }
    return _getText2(t);
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
  



  public final String getValueAsString()
    throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        _finishString();
      }
      return _textBuffer.contentsAsString();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return getCurrentName();
    }
    return super.getValueAsString(null);
  }
  
  public final String getValueAsString(String defValue)
    throws IOException
  {
    if (_currToken == JsonToken.VALUE_STRING) {
      if (_tokenIncomplete) {
        _tokenIncomplete = false;
        _finishString();
      }
      return _textBuffer.contentsAsString();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return getCurrentName();
    }
    return super.getValueAsString(defValue);
  }
  
  protected final String _getText2(JsonToken t) {
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
  

  public final char[] getTextCharacters()
    throws IOException
  {
    if (_currToken != null) {
      switch (_currToken.id()) {
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
  
  public final int getTextLength()
    throws IOException
  {
    if (_currToken != null) {
      switch (_currToken.id()) {
      case 5: 
        return _parsingContext.getCurrentName().length();
      case 6: 
        if (_tokenIncomplete) {
          _tokenIncomplete = false;
          _finishString();
        }
      
      case 7: 
      case 8: 
        return _textBuffer.size();
      }
      return _currToken.asCharArray().length;
    }
    
    return 0;
  }
  

  public final int getTextOffset()
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
    if ((_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) && (_binaryValue != null)) {
      return _binaryValue;
    }
    if (_currToken != JsonToken.VALUE_STRING) {
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
  
  protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException
  {
    int outputPtr = 0;
    int outputEnd = buffer.length - 3;
    int outputCount = 0;
    


    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _loadMoreGuaranteed();
      }
      char ch = _inputBuffer[(_inputPtr++)];
      if (ch > ' ') {
        int bits = b64variant.decodeBase64Char(ch);
        if (bits < 0) {
          if (ch == '"') {
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
          


          if (_inputPtr >= _inputEnd) {
            _loadMoreGuaranteed();
          }
          ch = _inputBuffer[(_inputPtr++)];
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            bits = _decodeBase64Escape(b64variant, ch, 1);
          }
          decodedData = decodedData << 6 | bits;
          

          if (_inputPtr >= _inputEnd) {
            _loadMoreGuaranteed();
          }
          ch = _inputBuffer[(_inputPtr++)];
          bits = b64variant.decodeBase64Char(ch);
          

          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == '"') {
                decodedData >>= 4;
                buffer[(outputPtr++)] = ((byte)decodedData);
                if (!b64variant.usesPadding()) break;
                _inputPtr -= 1;
                _handleBase64MissingPadding(b64variant); break;
              }
              

              bits = _decodeBase64Escape(b64variant, ch, 2);
            }
            if (bits == -2)
            {
              if (_inputPtr >= _inputEnd) {
                _loadMoreGuaranteed();
              }
              ch = _inputBuffer[(_inputPtr++)];
              if ((!b64variant.usesPaddingChar(ch)) && 
                (_decodeBase64Escape(b64variant, ch, 3) != -2)) {
                throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
              }
              

              decodedData >>= 4;
              buffer[(outputPtr++)] = ((byte)decodedData);
              continue;
            }
          }
          
          decodedData = decodedData << 6 | bits;
          
          if (_inputPtr >= _inputEnd) {
            _loadMoreGuaranteed();
          }
          ch = _inputBuffer[(_inputPtr++)];
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == '"') {
                decodedData >>= 2;
                buffer[(outputPtr++)] = ((byte)(decodedData >> 8));
                buffer[(outputPtr++)] = ((byte)decodedData);
                if (!b64variant.usesPadding()) break;
                _inputPtr -= 1;
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
  














  public final JsonToken nextToken()
    throws IOException
  {
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
    

    if ((i == 93) || (i == 125)) {
      _closeScope(i);
      return _currToken;
    }
    

    if (_parsingContext.expectComma()) {
      i = _skipComma(i);
      

      if (((_features & FEAT_MASK_TRAILING_COMMA) != 0) && (
        (i == 93) || (i == 125))) {
        _closeScope(i);
        return _currToken;
      }
    }
    




    boolean inObject = _parsingContext.inObject();
    if (inObject)
    {
      _updateNameLocation();
      String name = i == 34 ? _parseName() : _handleOddName(i);
      _parsingContext.setCurrentName(name);
      _currToken = JsonToken.FIELD_NAME;
      i = _skipColon();
    }
    _updateLocation();
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t; JsonToken t; JsonToken t; JsonToken t; switch (i) {
    case 34: 
      _tokenIncomplete = true;
      t = JsonToken.VALUE_STRING;
      break;
    case 91: 
      if (!inObject) {
        _parsingContext = _parsingContext.createChildArrayContext(_tokenInputRow, _tokenInputCol);
      }
      t = JsonToken.START_ARRAY;
      break;
    case 123: 
      if (!inObject) {
        _parsingContext = _parsingContext.createChildObjectContext(_tokenInputRow, _tokenInputCol);
      }
      t = JsonToken.START_OBJECT;
      break;
    

    case 125: 
      _reportUnexpectedChar(i, "expected a value");
    case 116: 
      _matchTrue();
      t = JsonToken.VALUE_TRUE;
      break;
    case 102: 
      _matchFalse();
      t = JsonToken.VALUE_FALSE;
      break;
    case 110: 
      _matchNull();
      t = JsonToken.VALUE_NULL;
      break;
    




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
    default: 
      t = _handleOddValue(i);
    }
    
    
    if (inObject) {
      _nextToken = t;
      return _currToken;
    }
    _currToken = t;
    return t;
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
  









  public boolean nextFieldName(SerializableString sstr)
    throws IOException
  {
    _numTypesValid = 0;
    if (_currToken == JsonToken.FIELD_NAME) {
      _nextAfterName();
      return false;
    }
    if (_tokenIncomplete) {
      _skipString();
    }
    int i = _skipWSOrEnd();
    if (i < 0) {
      close();
      _currToken = null;
      return false;
    }
    _binaryValue = null;
    

    if ((i == 93) || (i == 125)) {
      _closeScope(i);
      return false;
    }
    
    if (_parsingContext.expectComma()) {
      i = _skipComma(i);
      

      if (((_features & FEAT_MASK_TRAILING_COMMA) != 0) && (
        (i == 93) || (i == 125))) {
        _closeScope(i);
        return false;
      }
    }
    

    if (!_parsingContext.inObject()) {
      _updateLocation();
      _nextTokenNotInObject(i);
      return false;
    }
    
    _updateNameLocation();
    if (i == 34)
    {
      char[] nameChars = sstr.asQuotedChars();
      int len = nameChars.length;
      

      if (_inputPtr + len + 4 < _inputEnd)
      {
        int end = _inputPtr + len;
        if (_inputBuffer[end] == '"') {
          int offset = 0;
          int ptr = _inputPtr;
          for (;;) {
            if (ptr == end) {
              _parsingContext.setCurrentName(sstr.getValue());
              _isNextTokenNameYes(_skipColonFast(ptr + 1));
              return true;
            }
            if (nameChars[offset] != _inputBuffer[ptr]) {
              break;
            }
            offset++;
            ptr++;
          }
        }
      }
    }
    return _isNextTokenNameMaybe(i, sstr.getValue());
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
    int i = _skipWSOrEnd();
    if (i < 0) {
      close();
      _currToken = null;
      return null;
    }
    _binaryValue = null;
    if ((i == 93) || (i == 125)) {
      _closeScope(i);
      return null;
    }
    if (_parsingContext.expectComma()) {
      i = _skipComma(i);
      if (((_features & FEAT_MASK_TRAILING_COMMA) != 0) && (
        (i == 93) || (i == 125))) {
        _closeScope(i);
        return null;
      }
    }
    
    if (!_parsingContext.inObject()) {
      _updateLocation();
      _nextTokenNotInObject(i);
      return null;
    }
    
    _updateNameLocation();
    String name = i == 34 ? _parseName() : _handleOddName(i);
    _parsingContext.setCurrentName(name);
    _currToken = JsonToken.FIELD_NAME;
    i = _skipColon();
    
    _updateLocation();
    if (i == 34) {
      _tokenIncomplete = true;
      _nextToken = JsonToken.VALUE_STRING;
      return name; }
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t;
    JsonToken t; JsonToken t; switch (i) {
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
      _matchFalse();
      t = JsonToken.VALUE_FALSE;
      break;
    case 110: 
      _matchNull();
      t = JsonToken.VALUE_NULL;
      break;
    case 116: 
      _matchTrue();
      t = JsonToken.VALUE_TRUE;
      break;
    case 91: 
      t = JsonToken.START_ARRAY;
      break;
    case 123: 
      t = JsonToken.START_OBJECT;
      break;
    default: 
      t = _handleOddValue(i);
    }
    
    _nextToken = t;
    return name;
  }
  
  private final void _isNextTokenNameYes(int i) throws IOException
  {
    _currToken = JsonToken.FIELD_NAME;
    _updateLocation();
    
    switch (i) {
    case 34: 
      _tokenIncomplete = true;
      _nextToken = JsonToken.VALUE_STRING;
      return;
    case 91: 
      _nextToken = JsonToken.START_ARRAY;
      return;
    case 123: 
      _nextToken = JsonToken.START_OBJECT;
      return;
    case 116: 
      _matchToken("true", 1);
      _nextToken = JsonToken.VALUE_TRUE;
      return;
    case 102: 
      _matchToken("false", 1);
      _nextToken = JsonToken.VALUE_FALSE;
      return;
    case 110: 
      _matchToken("null", 1);
      _nextToken = JsonToken.VALUE_NULL;
      return;
    case 45: 
      _nextToken = _parseNegNumber();
      return;
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
      _nextToken = _parsePosNumber(i);
      return;
    }
    _nextToken = _handleOddValue(i);
  }
  
  protected boolean _isNextTokenNameMaybe(int i, String nameToMatch)
    throws IOException
  {
    String name = i == 34 ? _parseName() : _handleOddName(i);
    _parsingContext.setCurrentName(name);
    _currToken = JsonToken.FIELD_NAME;
    i = _skipColon();
    _updateLocation();
    if (i == 34) {
      _tokenIncomplete = true;
      _nextToken = JsonToken.VALUE_STRING;
      return nameToMatch.equals(name); }
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
      _matchFalse();
      t = JsonToken.VALUE_FALSE;
      break;
    case 110: 
      _matchNull();
      t = JsonToken.VALUE_NULL;
      break;
    case 116: 
      _matchTrue();
      t = JsonToken.VALUE_TRUE;
      break;
    case 91: 
      t = JsonToken.START_ARRAY;
      break;
    case 123: 
      t = JsonToken.START_OBJECT;
      break;
    default: 
      t = _handleOddValue(i);
    }
    
    _nextToken = t;
    return nameToMatch.equals(name);
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
    







    case 44: 
    case 93: 
      if ((_features & FEAT_MASK_ALLOW_MISSING) != 0) {
        _inputPtr -= 1;
        return this._currToken = JsonToken.VALUE_NULL;
      }
      break; }
    return this._currToken = _handleOddValue(i);
  }
  
  public final String nextTextValue()
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
          _finishString();
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
  

  public final int nextIntValue(int defaultValue)
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
  

  public final long nextLongValue(long defaultValue)
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
  

  public final Boolean nextBooleanValue()
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
    if (t != null) {
      int id = t.id();
      if (id == 9) return Boolean.TRUE;
      if (id == 10) return Boolean.FALSE;
    }
    return null;
  }
  

























  protected final JsonToken _parsePosNumber(int ch)
    throws IOException
  {
    int ptr = _inputPtr;
    int startPtr = ptr - 1;
    int inputLen = _inputEnd;
    

    if (ch == 48) {
      return _parseNumber2(false, startPtr);
    }
    






    int intLen = 1;
    

    for (;;)
    {
      if (ptr >= inputLen) {
        _inputPtr = startPtr;
        return _parseNumber2(false, startPtr);
      }
      ch = _inputBuffer[(ptr++)];
      if ((ch < 48) || (ch > 57)) {
        break;
      }
      intLen++;
    }
    if ((ch == 46) || (ch == 101) || (ch == 69)) {
      _inputPtr = ptr;
      return _parseFloat(ch, startPtr, ptr, false, intLen);
    }
    
    ptr--;
    _inputPtr = ptr;
    
    if (_parsingContext.inRoot()) {
      _verifyRootSpace(ch);
    }
    int len = ptr - startPtr;
    _textBuffer.resetWithShared(_inputBuffer, startPtr, len);
    return resetInt(false, intLen);
  }
  
  private final JsonToken _parseFloat(int ch, int startPtr, int ptr, boolean neg, int intLen)
    throws IOException
  {
    int inputLen = _inputEnd;
    int fractLen = 0;
    

    if (ch == 46)
    {
      for (;;) {
        if (ptr >= inputLen) {
          return _parseNumber2(neg, startPtr);
        }
        ch = _inputBuffer[(ptr++)];
        if ((ch < 48) || (ch > 57)) {
          break;
        }
        fractLen++;
      }
      
      if (fractLen == 0) {
        reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
      }
    }
    int expLen = 0;
    if ((ch == 101) || (ch == 69)) {
      if (ptr >= inputLen) {
        _inputPtr = startPtr;
        return _parseNumber2(neg, startPtr);
      }
      
      ch = _inputBuffer[(ptr++)];
      if ((ch == 45) || (ch == 43)) {
        if (ptr >= inputLen) {
          _inputPtr = startPtr;
          return _parseNumber2(neg, startPtr);
        }
        ch = _inputBuffer[(ptr++)];
      }
      while ((ch <= 57) && (ch >= 48)) {
        expLen++;
        if (ptr >= inputLen) {
          _inputPtr = startPtr;
          return _parseNumber2(neg, startPtr);
        }
        ch = _inputBuffer[(ptr++)];
      }
      
      if (expLen == 0) {
        reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
      }
    }
    ptr--;
    _inputPtr = ptr;
    
    if (_parsingContext.inRoot()) {
      _verifyRootSpace(ch);
    }
    int len = ptr - startPtr;
    _textBuffer.resetWithShared(_inputBuffer, startPtr, len);
    
    return resetFloat(neg, intLen, fractLen, expLen);
  }
  
  protected final JsonToken _parseNegNumber() throws IOException
  {
    int ptr = _inputPtr;
    int startPtr = ptr - 1;
    int inputLen = _inputEnd;
    
    if (ptr >= inputLen) {
      return _parseNumber2(true, startPtr);
    }
    int ch = _inputBuffer[(ptr++)];
    
    if ((ch > 57) || (ch < 48)) {
      _inputPtr = ptr;
      return _handleInvalidNumberStart(ch, true);
    }
    
    if (ch == 48) {
      return _parseNumber2(true, startPtr);
    }
    int intLen = 1;
    

    for (;;)
    {
      if (ptr >= inputLen) {
        return _parseNumber2(true, startPtr);
      }
      ch = _inputBuffer[(ptr++)];
      if ((ch < 48) || (ch > 57)) {
        break;
      }
      intLen++;
    }
    
    if ((ch == 46) || (ch == 101) || (ch == 69)) {
      _inputPtr = ptr;
      return _parseFloat(ch, startPtr, ptr, true, intLen);
    }
    ptr--;
    _inputPtr = ptr;
    if (_parsingContext.inRoot()) {
      _verifyRootSpace(ch);
    }
    int len = ptr - startPtr;
    _textBuffer.resetWithShared(_inputBuffer, startPtr, len);
    return resetInt(true, intLen);
  }
  






  private final JsonToken _parseNumber2(boolean neg, int startPtr)
    throws IOException
  {
    _inputPtr = (neg ? startPtr + 1 : startPtr);
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int outPtr = 0;
    

    if (neg) {
      outBuf[(outPtr++)] = '-';
    }
    

    int intLen = 0;
    
    char c = _inputPtr < _inputEnd ? _inputBuffer[(_inputPtr++)] : getNextChar("No digit following minus sign", JsonToken.VALUE_NUMBER_INT);
    if (c == '0') {
      c = _verifyNoLeadingZeroes();
    }
    boolean eof = false;
    


    while ((c >= '0') && (c <= '9')) {
      intLen++;
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      outBuf[(outPtr++)] = c;
      if ((_inputPtr >= _inputEnd) && (!_loadMore()))
      {
        c = '\000';
        eof = true;
        break;
      }
      c = _inputBuffer[(_inputPtr++)];
    }
    
    if (intLen == 0) {
      return _handleInvalidNumberStart(c, neg);
    }
    
    int fractLen = 0;
    
    if (c == '.') {
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      outBuf[(outPtr++)] = c;
      
      for (;;)
      {
        if ((_inputPtr >= _inputEnd) && (!_loadMore())) {
          eof = true;
          break;
        }
        c = _inputBuffer[(_inputPtr++)];
        if ((c < '0') || (c > '9')) {
          break;
        }
        fractLen++;
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
        }
        outBuf[(outPtr++)] = c;
      }
      
      if (fractLen == 0) {
        reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
      }
    }
    
    int expLen = 0;
    if ((c == 'e') || (c == 'E')) {
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      outBuf[(outPtr++)] = c;
      

      c = _inputPtr < _inputEnd ? _inputBuffer[(_inputPtr++)] : getNextChar("expected a digit for number exponent");
      
      if ((c == '-') || (c == '+')) {
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
        }
        outBuf[(outPtr++)] = c;
        

        c = _inputPtr < _inputEnd ? _inputBuffer[(_inputPtr++)] : getNextChar("expected a digit for number exponent");
      }
      

      while ((c <= '9') && (c >= '0')) {
        expLen++;
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
        }
        outBuf[(outPtr++)] = c;
        if ((_inputPtr >= _inputEnd) && (!_loadMore())) {
          eof = true;
          break;
        }
        c = _inputBuffer[(_inputPtr++)];
      }
      
      if (expLen == 0) {
        reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
      }
    }
    

    if (!eof) {
      _inputPtr -= 1;
      if (_parsingContext.inRoot()) {
        _verifyRootSpace(c);
      }
    }
    _textBuffer.setCurrentLength(outPtr);
    
    return reset(neg, intLen, fractLen, expLen);
  }
  




  private final char _verifyNoLeadingZeroes()
    throws IOException
  {
    if (_inputPtr < _inputEnd) {
      char ch = _inputBuffer[_inputPtr];
      
      if ((ch < '0') || (ch > '9')) {
        return '0';
      }
    }
    
    return _verifyNLZ2();
  }
  
  private char _verifyNLZ2() throws IOException
  {
    if ((_inputPtr >= _inputEnd) && (!_loadMore())) {
      return '0';
    }
    char ch = _inputBuffer[_inputPtr];
    if ((ch < '0') || (ch > '9')) {
      return '0';
    }
    if ((_features & FEAT_MASK_LEADING_ZEROS) == 0) {
      reportInvalidNumber("Leading zeroes not allowed");
    }
    
    _inputPtr += 1;
    if (ch == '0') {
      while ((_inputPtr < _inputEnd) || (_loadMore())) {
        ch = _inputBuffer[_inputPtr];
        if ((ch < '0') || (ch > '9')) {
          return '0';
        }
        _inputPtr += 1;
        if (ch != '0') {
          break;
        }
      }
    }
    return ch;
  }
  



  protected JsonToken _handleInvalidNumberStart(int ch, boolean negative)
    throws IOException
  {
    if (ch == 73) {
      if ((_inputPtr >= _inputEnd) && 
        (!_loadMore())) {
        _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
      }
      
      ch = _inputBuffer[(_inputPtr++)];
      if (ch == 78) {
        String match = negative ? "-INF" : "+INF";
        _matchToken(match, 3);
        if ((_features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
          return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        }
        _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
      } else if (ch == 110) {
        String match = negative ? "-Infinity" : "+Infinity";
        _matchToken(match, 3);
        if ((_features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
          return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        }
        _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
      }
    }
    reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
    return null;
  }
  







  private final void _verifyRootSpace(int ch)
    throws IOException
  {
    _inputPtr += 1;
    switch (ch) {
    case 9: 
    case 32: 
      return;
    case 13: 
      _skipCR();
      return;
    case 10: 
      _currInputRow += 1;
      _currInputRowStart = _inputPtr;
      return;
    }
    _reportMissingRootWS(ch);
  }
  







  protected final String _parseName()
    throws IOException
  {
    int ptr = _inputPtr;
    int hash = _hashSeed;
    int[] codes = _icLatin1;
    
    while (ptr < _inputEnd) {
      int ch = _inputBuffer[ptr];
      if ((ch < codes.length) && (codes[ch] != 0)) {
        if (ch != 34) break;
        int start = _inputPtr;
        _inputPtr = (ptr + 1);
        return _symbols.findSymbol(_inputBuffer, start, ptr - start, hash);
      }
      

      hash = hash * 33 + ch;
      ptr++;
    }
    int start = _inputPtr;
    _inputPtr = ptr;
    return _parseName2(start, hash, 34);
  }
  
  private String _parseName2(int startPtr, int hash, int endChar) throws IOException
  {
    _textBuffer.resetWithShared(_inputBuffer, startPtr, _inputPtr - startPtr);
    



    char[] outBuf = _textBuffer.getCurrentSegment();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    for (;;)
    {
      if ((_inputPtr >= _inputEnd) && 
        (!_loadMore())) {
        _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
      }
      
      char c = _inputBuffer[(_inputPtr++)];
      int i = c;
      if (i <= 92) {
        if (i == 92)
        {



          c = _decodeEscaped();
        } else if (i <= endChar) {
          if (i == endChar) {
            break;
          }
          if (i < 32) {
            _throwUnquotedSpace(i, "name");
          }
        }
      }
      hash = hash * 33 + c;
      
      outBuf[(outPtr++)] = c;
      

      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
    }
    _textBuffer.setCurrentLength(outPtr);
    
    TextBuffer tb = _textBuffer;
    char[] buf = tb.getTextBuffer();
    int start = tb.getTextOffset();
    int len = tb.size();
    return _symbols.findSymbol(buf, start, len, hash);
  }
  







  protected String _handleOddName(int i)
    throws IOException
  {
    if ((i == 39) && ((_features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0)) {
      return _parseAposName();
    }
    
    if ((_features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
      _reportUnexpectedChar(i, "was expecting double-quote to start field name");
    }
    int[] codes = CharTypes.getInputCodeLatin1JsNames();
    int maxCode = codes.length;
    
    boolean firstOk;
    
    boolean firstOk;
    if (i < maxCode) {
      firstOk = codes[i] == 0;
    } else {
      firstOk = Character.isJavaIdentifierPart((char)i);
    }
    if (!firstOk) {
      _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
    }
    int ptr = _inputPtr;
    int hash = _hashSeed;
    int inputLen = _inputEnd;
    
    if (ptr < inputLen) {
      do {
        int ch = _inputBuffer[ptr];
        if (ch < maxCode) {
          if (codes[ch] != 0) {
            int start = _inputPtr - 1;
            _inputPtr = ptr;
            return _symbols.findSymbol(_inputBuffer, start, ptr - start, hash);
          }
        } else if (!Character.isJavaIdentifierPart((char)ch)) {
          int start = _inputPtr - 1;
          _inputPtr = ptr;
          return _symbols.findSymbol(_inputBuffer, start, ptr - start, hash);
        }
        hash = hash * 33 + ch;
        ptr++;
      } while (ptr < inputLen);
    }
    int start = _inputPtr - 1;
    _inputPtr = ptr;
    return _handleOddName2(start, hash, codes);
  }
  
  protected String _parseAposName()
    throws IOException
  {
    int ptr = _inputPtr;
    int hash = _hashSeed;
    int inputLen = _inputEnd;
    
    if (ptr < inputLen) {
      int[] codes = _icLatin1;
      int maxCode = codes.length;
      do
      {
        int ch = _inputBuffer[ptr];
        if (ch == 39) {
          int start = _inputPtr;
          _inputPtr = (ptr + 1);
          return _symbols.findSymbol(_inputBuffer, start, ptr - start, hash);
        }
        if ((ch < maxCode) && (codes[ch] != 0)) {
          break;
        }
        hash = hash * 33 + ch;
        ptr++;
      } while (ptr < inputLen);
    }
    
    int start = _inputPtr;
    _inputPtr = ptr;
    
    return _parseName2(start, hash, 39);
  }
  




  protected JsonToken _handleOddValue(int i)
    throws IOException
  {
    switch (i)
    {




    case 39: 
      if ((_features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
        return _handleApos();
      }
      



      break;
    case 93: 
      if (!_parsingContext.inArray()) {
        break;
      }
    
    case 44: 
      if ((_features & FEAT_MASK_ALLOW_MISSING) != 0) {
        _inputPtr -= 1;
        return JsonToken.VALUE_NULL;
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
      if ((_inputPtr >= _inputEnd) && 
        (!_loadMore())) {
        _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
      }
      
      return _handleInvalidNumberStart(_inputBuffer[(_inputPtr++)], false);
    }
    
    if (Character.isJavaIdentifierStart(i)) {
      _reportInvalidToken("" + (char)i, _validJsonTokenList());
    }
    
    _reportUnexpectedChar(i, "expected a valid value " + _validJsonValueList());
    return null;
  }
  
  protected JsonToken _handleApos() throws IOException
  {
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    for (;;)
    {
      if ((_inputPtr >= _inputEnd) && 
        (!_loadMore())) {
        _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
      }
      

      char c = _inputBuffer[(_inputPtr++)];
      int i = c;
      if (i <= 92) {
        if (i == 92)
        {



          c = _decodeEscaped();
        } else if (i <= 39) {
          if (i == 39) {
            break;
          }
          if (i < 32) {
            _throwUnquotedSpace(i, "string value");
          }
        }
      }
      
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      
      outBuf[(outPtr++)] = c;
    }
    _textBuffer.setCurrentLength(outPtr);
    return JsonToken.VALUE_STRING;
  }
  
  private String _handleOddName2(int startPtr, int hash, int[] codes) throws IOException
  {
    _textBuffer.resetWithShared(_inputBuffer, startPtr, _inputPtr - startPtr);
    char[] outBuf = _textBuffer.getCurrentSegment();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    int maxCode = codes.length;
    

    while ((_inputPtr < _inputEnd) || 
      (_loadMore()))
    {


      char c = _inputBuffer[_inputPtr];
      int i = c;
      if (i < maxCode ? 
        codes[i] != 0 : 
        

        !Character.isJavaIdentifierPart(c)) {
        break;
      }
      _inputPtr += 1;
      hash = hash * 33 + i;
      
      outBuf[(outPtr++)] = c;
      

      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
    }
    _textBuffer.setCurrentLength(outPtr);
    
    TextBuffer tb = _textBuffer;
    char[] buf = tb.getTextBuffer();
    int start = tb.getTextOffset();
    int len = tb.size();
    
    return _symbols.findSymbol(buf, start, len, hash);
  }
  





  protected final void _finishString()
    throws IOException
  {
    int ptr = _inputPtr;
    int inputLen = _inputEnd;
    
    if (ptr < inputLen) {
      int[] codes = _icLatin1;
      int maxCode = codes.length;
      do
      {
        int ch = _inputBuffer[ptr];
        if ((ch < maxCode) && (codes[ch] != 0)) {
          if (ch != 34) break;
          _textBuffer.resetWithShared(_inputBuffer, _inputPtr, ptr - _inputPtr);
          _inputPtr = (ptr + 1);
          
          return;
        }
        

        ptr++;
      } while (ptr < inputLen);
    }
    

    _textBuffer.resetWithCopy(_inputBuffer, _inputPtr, ptr - _inputPtr);
    _inputPtr = ptr;
    _finishString2();
  }
  
  protected void _finishString2() throws IOException
  {
    char[] outBuf = _textBuffer.getCurrentSegment();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    int[] codes = _icLatin1;
    int maxCode = codes.length;
    for (;;)
    {
      if ((_inputPtr >= _inputEnd) && 
        (!_loadMore())) {
        _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
      }
      

      char c = _inputBuffer[(_inputPtr++)];
      int i = c;
      if ((i < maxCode) && (codes[i] != 0)) {
        if (i == 34)
          break;
        if (i == 92)
        {



          c = _decodeEscaped();
        } else if (i < 32) {
          _throwUnquotedSpace(i, "string value");
        }
      }
      
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      
      outBuf[(outPtr++)] = c;
    }
    _textBuffer.setCurrentLength(outPtr);
  }
  




  protected final void _skipString()
    throws IOException
  {
    _tokenIncomplete = false;
    
    int inPtr = _inputPtr;
    int inLen = _inputEnd;
    char[] inBuf = _inputBuffer;
    for (;;)
    {
      if (inPtr >= inLen) {
        _inputPtr = inPtr;
        if (!_loadMore()) {
          _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
        }
        
        inPtr = _inputPtr;
        inLen = _inputEnd;
      }
      char c = inBuf[(inPtr++)];
      int i = c;
      if (i <= 92) {
        if (i == 92)
        {

          _inputPtr = inPtr;
          _decodeEscaped();
          inPtr = _inputPtr;
          inLen = _inputEnd;
        } else if (i <= 34) {
          if (i == 34) {
            _inputPtr = inPtr;
            break;
          }
          if (i < 32) {
            _inputPtr = inPtr;
            _throwUnquotedSpace(i, "string value");
          }
        }
      }
    }
  }
  








  protected final void _skipCR()
    throws IOException
  {
    if (((_inputPtr < _inputEnd) || (_loadMore())) && 
      (_inputBuffer[_inputPtr] == '\n')) {
      _inputPtr += 1;
    }
    
    _currInputRow += 1;
    _currInputRowStart = _inputPtr;
  }
  
  private final int _skipColon() throws IOException
  {
    if (_inputPtr + 4 >= _inputEnd) {
      return _skipColon2(false);
    }
    char c = _inputBuffer[_inputPtr];
    if (c == ':') {
      int i = _inputBuffer[(++_inputPtr)];
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          return _skipColon2(true);
        }
        _inputPtr += 1;
        return i;
      }
      if ((i == 32) || (i == 9)) {
        i = _inputBuffer[(++_inputPtr)];
        if (i > 32) {
          if ((i == 47) || (i == 35)) {
            return _skipColon2(true);
          }
          _inputPtr += 1;
          return i;
        }
      }
      return _skipColon2(true);
    }
    if ((c == ' ') || (c == '\t')) {
      c = _inputBuffer[(++_inputPtr)];
    }
    if (c == ':') {
      int i = _inputBuffer[(++_inputPtr)];
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          return _skipColon2(true);
        }
        _inputPtr += 1;
        return i;
      }
      if ((i == 32) || (i == 9)) {
        i = _inputBuffer[(++_inputPtr)];
        if (i > 32) {
          if ((i == 47) || (i == 35)) {
            return _skipColon2(true);
          }
          _inputPtr += 1;
          return i;
        }
      }
      return _skipColon2(true);
    }
    return _skipColon2(false);
  }
  
  private final int _skipColon2(boolean gotColon) throws IOException
  {
    while ((_inputPtr < _inputEnd) || (_loadMore())) {
      int i = _inputBuffer[(_inputPtr++)];
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
      else if (i < 32) {
        if (i == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (i == 13) {
          _skipCR();
        } else if (i != 9) {
          _throwInvalidSpace(i);
        }
      }
    }
    _reportInvalidEOF(" within/between " + _parsingContext.typeDesc() + " entries", null);
    
    return -1;
  }
  
  private final int _skipColonFast(int ptr)
    throws IOException
  {
    int i = _inputBuffer[(ptr++)];
    if (i == 58) {
      i = _inputBuffer[(ptr++)];
      if (i > 32) {
        if ((i != 47) && (i != 35)) {
          _inputPtr = ptr;
          return i;
        }
      } else if ((i == 32) || (i == 9)) {
        i = _inputBuffer[(ptr++)];
        if ((i > 32) && 
          (i != 47) && (i != 35)) {
          _inputPtr = ptr;
          return i;
        }
      }
      
      _inputPtr = (ptr - 1);
      return _skipColon2(true);
    }
    if ((i == 32) || (i == 9)) {
      i = _inputBuffer[(ptr++)];
    }
    boolean gotColon = i == 58;
    if (gotColon) {
      i = _inputBuffer[(ptr++)];
      if (i > 32) {
        if ((i != 47) && (i != 35)) {
          _inputPtr = ptr;
          return i;
        }
      } else if ((i == 32) || (i == 9)) {
        i = _inputBuffer[(ptr++)];
        if ((i > 32) && 
          (i != 47) && (i != 35)) {
          _inputPtr = ptr;
          return i;
        }
      }
    }
    
    _inputPtr = (ptr - 1);
    return _skipColon2(gotColon);
  }
  
  private final int _skipComma(int i)
    throws IOException
  {
    if (i != 44) {
      _reportUnexpectedChar(i, "was expecting comma to separate " + _parsingContext.typeDesc() + " entries");
    }
    while (_inputPtr < _inputEnd) {
      i = _inputBuffer[(_inputPtr++)];
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          _inputPtr -= 1;
          return _skipAfterComma2();
        }
        return i;
      }
      if (i < 32) {
        if (i == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (i == 13) {
          _skipCR();
        } else if (i != 9) {
          _throwInvalidSpace(i);
        }
      }
    }
    return _skipAfterComma2();
  }
  
  private final int _skipAfterComma2() throws IOException
  {
    while ((_inputPtr < _inputEnd) || (_loadMore())) {
      int i = _inputBuffer[(_inputPtr++)];
      if (i > 32) {
        if (i == 47) {
          _skipComment();

        }
        else if ((i != 35) || 
          (!_skipYAMLComment()))
        {


          return i;
        }
      } else if (i < 32) {
        if (i == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (i == 13) {
          _skipCR();
        } else if (i != 9) {
          _throwInvalidSpace(i);
        }
      }
    }
    throw _constructError("Unexpected end-of-input within/between " + _parsingContext.typeDesc() + " entries");
  }
  

  private final int _skipWSOrEnd()
    throws IOException
  {
    if ((_inputPtr >= _inputEnd) && 
      (!_loadMore())) {
      return _eofAsNextChar();
    }
    
    int i = _inputBuffer[(_inputPtr++)];
    if (i > 32) {
      if ((i == 47) || (i == 35)) {
        _inputPtr -= 1;
        return _skipWSOrEnd2();
      }
      return i;
    }
    if (i != 32) {
      if (i == 10) {
        _currInputRow += 1;
        _currInputRowStart = _inputPtr;
      } else if (i == 13) {
        _skipCR();
      } else if (i != 9) {
        _throwInvalidSpace(i);
      }
    }
    
    while (_inputPtr < _inputEnd) {
      i = _inputBuffer[(_inputPtr++)];
      if (i > 32) {
        if ((i == 47) || (i == 35)) {
          _inputPtr -= 1;
          return _skipWSOrEnd2();
        }
        return i;
      }
      if (i != 32) {
        if (i == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (i == 13) {
          _skipCR();
        } else if (i != 9) {
          _throwInvalidSpace(i);
        }
      }
    }
    return _skipWSOrEnd2();
  }
  
  private int _skipWSOrEnd2() throws IOException
  {
    for (;;) {
      if ((_inputPtr >= _inputEnd) && 
        (!_loadMore())) {
        return _eofAsNextChar();
      }
      
      int i = _inputBuffer[(_inputPtr++)];
      if (i > 32) {
        if (i == 47) {
          _skipComment();

        }
        else if ((i != 35) || 
          (!_skipYAMLComment()))
        {


          return i; }
      } else if (i != 32) {
        if (i == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (i == 13) {
          _skipCR();
        } else if (i != 9) {
          _throwInvalidSpace(i);
        }
      }
    }
  }
  
  private void _skipComment() throws IOException
  {
    if ((_features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
      _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
    }
    
    if ((_inputPtr >= _inputEnd) && (!_loadMore())) {
      _reportInvalidEOF(" in a comment", null);
    }
    char c = _inputBuffer[(_inputPtr++)];
    if (c == '/') {
      _skipLine();
    } else if (c == '*') {
      _skipCComment();
    } else {
      _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
    }
  }
  
  private void _skipCComment()
    throws IOException
  {
    while ((_inputPtr < _inputEnd) || (_loadMore())) {
      int i = _inputBuffer[(_inputPtr++)];
      if (i <= 42) {
        if (i == 42) {
          if ((_inputPtr >= _inputEnd) && (!_loadMore())) {
            break;
          }
          if (_inputBuffer[_inputPtr] == '/') {
            _inputPtr += 1;
          }
          

        }
        else if (i < 32) {
          if (i == 10) {
            _currInputRow += 1;
            _currInputRowStart = _inputPtr;
          } else if (i == 13) {
            _skipCR();
          } else if (i != 9) {
            _throwInvalidSpace(i);
          }
        }
      }
    }
    _reportInvalidEOF(" in a comment", null);
  }
  
  private boolean _skipYAMLComment() throws IOException
  {
    if ((_features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
      return false;
    }
    _skipLine();
    return true;
  }
  
  private void _skipLine()
    throws IOException
  {
    while ((_inputPtr < _inputEnd) || (_loadMore())) {
      int i = _inputBuffer[(_inputPtr++)];
      if (i < 32) {
        if (i == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
          break; }
        if (i == 13) {
          _skipCR();
          break; }
        if (i != 9) {
          _throwInvalidSpace(i);
        }
      }
    }
  }
  
  protected char _decodeEscaped()
    throws IOException
  {
    if ((_inputPtr >= _inputEnd) && 
      (!_loadMore())) {
      _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
    }
    
    char c = _inputBuffer[(_inputPtr++)];
    
    switch (c)
    {
    case 'b': 
      return '\b';
    case 't': 
      return '\t';
    case 'n': 
      return '\n';
    case 'f': 
      return '\f';
    case 'r': 
      return '\r';
    

    case '"': 
    case '/': 
    case '\\': 
      return c;
    
    case 'u': 
      break;
    
    default: 
      return _handleUnrecognizedCharacterEscape(c);
    }
    
    
    int value = 0;
    for (int i = 0; i < 4; i++) {
      if ((_inputPtr >= _inputEnd) && 
        (!_loadMore())) {
        _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
      }
      
      int ch = _inputBuffer[(_inputPtr++)];
      int digit = CharTypes.charToHex(ch);
      if (digit < 0) {
        _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
      }
      value = value << 4 | digit;
    }
    return (char)value;
  }
  
  private final void _matchTrue() throws IOException {
    int ptr = _inputPtr;
    if (ptr + 3 < _inputEnd) {
      char[] b = _inputBuffer;
      if ((b[ptr] == 'r') && (b[(++ptr)] == 'u') && (b[(++ptr)] == 'e')) {
        char c = b[(++ptr)];
        if ((c < '0') || (c == ']') || (c == '}')) {
          _inputPtr = ptr;
          return;
        }
      }
    }
    
    _matchToken("true", 1);
  }
  
  private final void _matchFalse() throws IOException {
    int ptr = _inputPtr;
    if (ptr + 4 < _inputEnd) {
      char[] b = _inputBuffer;
      if ((b[ptr] == 'a') && (b[(++ptr)] == 'l') && (b[(++ptr)] == 's') && (b[(++ptr)] == 'e')) {
        char c = b[(++ptr)];
        if ((c < '0') || (c == ']') || (c == '}')) {
          _inputPtr = ptr;
          return;
        }
      }
    }
    
    _matchToken("false", 1);
  }
  
  private final void _matchNull() throws IOException {
    int ptr = _inputPtr;
    if (ptr + 3 < _inputEnd) {
      char[] b = _inputBuffer;
      if ((b[ptr] == 'u') && (b[(++ptr)] == 'l') && (b[(++ptr)] == 'l')) {
        char c = b[(++ptr)];
        if ((c < '0') || (c == ']') || (c == '}')) {
          _inputPtr = ptr;
          return;
        }
      }
    }
    
    _matchToken("null", 1);
  }
  


  protected final void _matchToken(String matchStr, int i)
    throws IOException
  {
    int len = matchStr.length();
    if (_inputPtr + len >= _inputEnd) {
      _matchToken2(matchStr, i);
    }
    else
    {
      do {
        if (_inputBuffer[_inputPtr] != matchStr.charAt(i)) {
          _reportInvalidToken(matchStr.substring(0, i));
        }
        _inputPtr += 1;
        i++; } while (i < len);
      int ch = _inputBuffer[_inputPtr];
      if ((ch >= 48) && (ch != 93) && (ch != 125)) {
        _checkMatchEnd(matchStr, i, ch);
      }
    }
  }
  
  private final void _matchToken2(String matchStr, int i) throws IOException {
    int len = matchStr.length();
    do {
      if (((_inputPtr >= _inputEnd) && (!_loadMore())) || 
        (_inputBuffer[_inputPtr] != matchStr.charAt(i))) {
        _reportInvalidToken(matchStr.substring(0, i));
      }
      _inputPtr += 1;
      i++; } while (i < len);
    

    if ((_inputPtr >= _inputEnd) && (!_loadMore())) {
      return;
    }
    int ch = _inputBuffer[_inputPtr];
    if ((ch >= 48) && (ch != 93) && (ch != 125)) {
      _checkMatchEnd(matchStr, i, ch);
    }
  }
  
  private final void _checkMatchEnd(String matchStr, int i, int c) throws IOException
  {
    char ch = (char)c;
    if (Character.isJavaIdentifierPart(ch)) {
      _reportInvalidToken(matchStr.substring(0, i));
    }
  }
  










  protected byte[] _decodeBase64(Base64Variant b64variant)
    throws IOException
  {
    ByteArrayBuilder builder = _getByteArrayBuilder();
    



    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _loadMoreGuaranteed();
      }
      char ch = _inputBuffer[(_inputPtr++)];
      if (ch > ' ') {
        int bits = b64variant.decodeBase64Char(ch);
        if (bits < 0) {
          if (ch == '"') {
            return builder.toByteArray();
          }
          bits = _decodeBase64Escape(b64variant, ch, 0);
          if (bits < 0) {}
        }
        else
        {
          int decodedData = bits;
          


          if (_inputPtr >= _inputEnd) {
            _loadMoreGuaranteed();
          }
          ch = _inputBuffer[(_inputPtr++)];
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            bits = _decodeBase64Escape(b64variant, ch, 1);
          }
          decodedData = decodedData << 6 | bits;
          

          if (_inputPtr >= _inputEnd) {
            _loadMoreGuaranteed();
          }
          ch = _inputBuffer[(_inputPtr++)];
          bits = b64variant.decodeBase64Char(ch);
          

          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == '"') {
                decodedData >>= 4;
                builder.append(decodedData);
                if (b64variant.usesPadding()) {
                  _inputPtr -= 1;
                  _handleBase64MissingPadding(b64variant);
                }
                return builder.toByteArray();
              }
              bits = _decodeBase64Escape(b64variant, ch, 2);
            }
            if (bits == -2)
            {
              if (_inputPtr >= _inputEnd) {
                _loadMoreGuaranteed();
              }
              ch = _inputBuffer[(_inputPtr++)];
              if ((!b64variant.usesPaddingChar(ch)) && 
                (_decodeBase64Escape(b64variant, ch, 3) != -2)) {
                throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
              }
              

              decodedData >>= 4;
              builder.append(decodedData);
              continue;
            }
          }
          

          decodedData = decodedData << 6 | bits;
          
          if (_inputPtr >= _inputEnd) {
            _loadMoreGuaranteed();
          }
          ch = _inputBuffer[(_inputPtr++)];
          bits = b64variant.decodeBase64Char(ch);
          if (bits < 0) {
            if (bits != -2)
            {
              if (ch == '"') {
                decodedData >>= 2;
                builder.appendTwoBytes(decodedData);
                if (b64variant.usesPadding()) {
                  _inputPtr -= 1;
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
    if (_currToken == JsonToken.FIELD_NAME) {
      long total = _currInputProcessed + (_nameStartOffset - 1L);
      return new JsonLocation(_getSourceReference(), -1L, total, _nameStartRow, _nameStartCol);
    }
    
    return new JsonLocation(_getSourceReference(), -1L, _tokenInputTotal - 1L, _tokenInputRow, _tokenInputCol);
  }
  

  public JsonLocation getCurrentLocation()
  {
    int col = _inputPtr - _currInputRowStart + 1;
    return new JsonLocation(_getSourceReference(), -1L, _currInputProcessed + _inputPtr, _currInputRow, col);
  }
  



  private final void _updateLocation()
  {
    int ptr = _inputPtr;
    _tokenInputTotal = (_currInputProcessed + ptr);
    _tokenInputRow = _currInputRow;
    _tokenInputCol = (ptr - _currInputRowStart);
  }
  

  private final void _updateNameLocation()
  {
    int ptr = _inputPtr;
    _nameStartOffset = ptr;
    _nameStartRow = _currInputRow;
    _nameStartCol = (ptr - _currInputRowStart);
  }
  




  protected void _reportInvalidToken(String matchedPart)
    throws IOException
  {
    _reportInvalidToken(matchedPart, _validJsonTokenList());
  }
  



  protected void _reportInvalidToken(String matchedPart, String msg)
    throws IOException
  {
    StringBuilder sb = new StringBuilder(matchedPart);
    while ((_inputPtr < _inputEnd) || (_loadMore())) {
      char c = _inputBuffer[_inputPtr];
      if (!Character.isJavaIdentifierPart(c)) {
        break;
      }
      _inputPtr += 1;
      sb.append(c);
      if (sb.length() >= 256) {
        sb.append("...");
        break;
      }
    }
    _reportError("Unrecognized token '%s': was expecting %s", sb, msg);
  }
  




  private void _closeScope(int i)
    throws JsonParseException
  {
    if (i == 93) {
      _updateLocation();
      if (!_parsingContext.inArray()) {
        _reportMismatchedEndMarker(i, '}');
      }
      _parsingContext = _parsingContext.clearAndGetParent();
      _currToken = JsonToken.END_ARRAY;
    }
    if (i == 125) {
      _updateLocation();
      if (!_parsingContext.inObject()) {
        _reportMismatchedEndMarker(i, ']');
      }
      _parsingContext = _parsingContext.clearAndGetParent();
      _currToken = JsonToken.END_OBJECT;
    }
  }
}
