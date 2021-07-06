package com.fasterxml.jackson.core.json.async;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.TextBuffer;
import com.fasterxml.jackson.core.util.VersionUtil;
import java.io.IOException;
import java.io.OutputStream;

public class NonBlockingJsonParser
  extends NonBlockingJsonParserBase
  implements ByteArrayFeeder
{
  private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
  
  private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
  
  private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
  private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
  private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
  private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
  private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
  

  private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
  


  protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
  









  protected byte[] _inputBuffer = NO_BYTES;
  








  protected int _origBufferLen;
  









  public NonBlockingJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym)
  {
    super(ctxt, parserFeatures, sym);
  }
  






  public ByteArrayFeeder getNonBlockingInputFeeder()
  {
    return this;
  }
  
  public final boolean needMoreInput()
  {
    return (_inputPtr >= _inputEnd) && (!_endOfInput);
  }
  

  public void feedInput(byte[] buf, int start, int end)
    throws IOException
  {
    if (_inputPtr < _inputEnd) {
      _reportError("Still have %d undecoded bytes, should not call 'feedInput'", Integer.valueOf(_inputEnd - _inputPtr));
    }
    if (end < start) {
      _reportError("Input end (%d) may not be before start (%d)", Integer.valueOf(end), Integer.valueOf(start));
    }
    
    if (_endOfInput) {
      _reportError("Already closed, can not feed more input");
    }
    
    _currInputProcessed += _origBufferLen;
    

    _currInputRowStart = (start - (_inputEnd - _currInputRowStart));
    

    _currBufferStart = start;
    _inputBuffer = buf;
    _inputPtr = start;
    _inputEnd = end;
    _origBufferLen = (end - start);
  }
  
  public void endOfInput()
  {
    _endOfInput = true;
  }
  
















  public int releaseBuffered(OutputStream out)
    throws IOException
  {
    int avail = _inputEnd - _inputPtr;
    if (avail > 0) {
      out.write(_inputBuffer, _inputPtr, avail);
    }
    return avail;
  }
  

  protected char _decodeEscaped()
    throws IOException
  {
    VersionUtil.throwInternal();
    return ' ';
  }
  








  public JsonToken nextToken()
    throws IOException
  {
    if (_inputPtr >= _inputEnd) {
      if (_closed) {
        return null;
      }
      
      if (_endOfInput)
      {

        if (_currToken == JsonToken.NOT_AVAILABLE) {
          return _finishTokenWithEOF();
        }
        return _eofAsNextToken();
      }
      return JsonToken.NOT_AVAILABLE;
    }
    
    if (_currToken == JsonToken.NOT_AVAILABLE) {
      return _finishToken();
    }
    

    _numTypesValid = 0;
    _tokenInputTotal = (_currInputProcessed + _inputPtr);
    
    _binaryValue = null;
    int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
    
    switch (_majorState) {
    case 0: 
      return _startDocument(ch);
    
    case 1: 
      return _startValue(ch);
    
    case 2: 
      return _startFieldName(ch);
    case 3: 
      return _startFieldNameAfterComma(ch);
    
    case 4: 
      return _startValueExpectColon(ch);
    
    case 5: 
      return _startValue(ch);
    
    case 6: 
      return _startValueExpectComma(ch);
    }
    
    
    VersionUtil.throwInternal();
    return null;
  }
  





  protected final JsonToken _finishToken()
    throws IOException
  {
    switch (_minorState) {
    case 1: 
      return _finishBOM(_pending32);
    case 4: 
      return _startFieldName(_inputBuffer[(_inputPtr++)] & 0xFF);
    case 5: 
      return _startFieldNameAfterComma(_inputBuffer[(_inputPtr++)] & 0xFF);
    

    case 7: 
      return _parseEscapedName(_quadLength, _pending32, _pendingBytes);
    case 8: 
      return _finishFieldWithEscape();
    case 9: 
      return _finishAposName(_quadLength, _pending32, _pendingBytes);
    case 10: 
      return _finishUnquotedName(_quadLength, _pending32, _pendingBytes);
    


    case 12: 
      return _startValue(_inputBuffer[(_inputPtr++)] & 0xFF);
    case 15: 
      return _startValueAfterComma(_inputBuffer[(_inputPtr++)] & 0xFF);
    case 13: 
      return _startValueExpectComma(_inputBuffer[(_inputPtr++)] & 0xFF);
    case 14: 
      return _startValueExpectColon(_inputBuffer[(_inputPtr++)] & 0xFF);
    
    case 16: 
      return _finishKeywordToken("null", _pending32, JsonToken.VALUE_NULL);
    case 17: 
      return _finishKeywordToken("true", _pending32, JsonToken.VALUE_TRUE);
    case 18: 
      return _finishKeywordToken("false", _pending32, JsonToken.VALUE_FALSE);
    case 19: 
      return _finishNonStdToken(_nonStdTokenType, _pending32);
    
    case 23: 
      return _finishNumberMinus(_inputBuffer[(_inputPtr++)] & 0xFF);
    case 24: 
      return _finishNumberLeadingZeroes();
    case 25: 
      return _finishNumberLeadingNegZeroes();
    case 26: 
      return _finishNumberIntegralPart(_textBuffer.getBufferWithoutReset(), _textBuffer
        .getCurrentSegmentSize());
    case 30: 
      return _finishFloatFraction();
    case 31: 
      return _finishFloatExponent(true, _inputBuffer[(_inputPtr++)] & 0xFF);
    case 32: 
      return _finishFloatExponent(false, _inputBuffer[(_inputPtr++)] & 0xFF);
    
    case 40: 
      return _finishRegularString();
    case 42: 
      _textBuffer.append((char)_decodeUTF8_2(_pending32, _inputBuffer[(_inputPtr++)]));
      if (_minorStateAfterSplit == 45) {
        return _finishAposString();
      }
      return _finishRegularString();
    case 43: 
      if (!_decodeSplitUTF8_3(_pending32, _pendingBytes, _inputBuffer[(_inputPtr++)])) {
        return JsonToken.NOT_AVAILABLE;
      }
      if (_minorStateAfterSplit == 45) {
        return _finishAposString();
      }
      return _finishRegularString();
    case 44: 
      if (!_decodeSplitUTF8_4(_pending32, _pendingBytes, _inputBuffer[(_inputPtr++)])) {
        return JsonToken.NOT_AVAILABLE;
      }
      if (_minorStateAfterSplit == 45) {
        return _finishAposString();
      }
      return _finishRegularString();
    

    case 41: 
      int c = _decodeSplitEscaped(_quoted32, _quotedDigits);
      if (c < 0) {
        return JsonToken.NOT_AVAILABLE;
      }
      _textBuffer.append((char)c);
      
      if (_minorStateAfterSplit == 45) {
        return _finishAposString();
      }
      return _finishRegularString();
    
    case 45: 
      return _finishAposString();
    
    case 50: 
      return _finishErrorToken();
    


    case 51: 
      return _startSlashComment(_pending32);
    case 52: 
      return _finishCComment(_pending32, true);
    case 53: 
      return _finishCComment(_pending32, false);
    case 54: 
      return _finishCppComment(_pending32);
    case 55: 
      return _finishHashComment(_pending32);
    }
    VersionUtil.throwInternal();
    return null;
  }
  






  protected final JsonToken _finishTokenWithEOF()
    throws IOException
  {
    JsonToken t = _currToken;
    switch (_minorState) {
    case 3: 
      return _eofAsNextToken();
    case 12: 
      return _eofAsNextToken();
    

    case 16: 
      return _finishKeywordTokenWithEOF("null", _pending32, JsonToken.VALUE_NULL);
    case 17: 
      return _finishKeywordTokenWithEOF("true", _pending32, JsonToken.VALUE_TRUE);
    case 18: 
      return _finishKeywordTokenWithEOF("false", _pending32, JsonToken.VALUE_FALSE);
    case 19: 
      return _finishNonStdTokenWithEOF(_nonStdTokenType, _pending32);
    case 50: 
      return _finishErrorTokenWithEOF();
    



    case 24: 
    case 25: 
      return _valueCompleteInt(0, "0");
    

    case 26: 
      int len = _textBuffer.getCurrentSegmentSize();
      if (_numberNegative) {
        len--;
      }
      _intLength = len;
      
      return _valueComplete(JsonToken.VALUE_NUMBER_INT);
    
    case 30: 
      _expLength = 0;
    
    case 32: 
      return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
    
    case 31: 
      _reportInvalidEOF(": was expecting fraction after exponent marker", JsonToken.VALUE_NUMBER_FLOAT);
    




    case 52: 
    case 53: 
      _reportInvalidEOF(": was expecting closing '*/' for comment", JsonToken.NOT_AVAILABLE);
    

    case 54: 
    case 55: 
      return _eofAsNextToken();
    }
    
    
    _reportInvalidEOF(": was expecting rest of token (internal state: " + _minorState + ")", _currToken);
    return t;
  }
  





  private final JsonToken _startDocument(int ch)
    throws IOException
  {
    ch &= 0xFF;
    

    if ((ch == 239) && (_minorState != 1)) {
      return _finishBOM(1);
    }
    

    while (ch <= 32) {
      if (ch != 32) {
        if (ch == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (ch == 13) {
          _currInputRowAlt += 1;
          _currInputRowStart = _inputPtr;
        } else if (ch != 9) {
          _throwInvalidSpace(ch);
        }
      }
      if (_inputPtr >= _inputEnd) {
        _minorState = 3;
        if (_closed) {
          return null;
        }
        
        if (_endOfInput) {
          return _eofAsNextToken();
        }
        return JsonToken.NOT_AVAILABLE;
      }
      ch = _inputBuffer[(_inputPtr++)] & 0xFF;
    }
    return _startValue(ch);
  }
  



  private final JsonToken _finishBOM(int bytesHandled)
    throws IOException
  {
    while (_inputPtr < _inputEnd) {
      int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      switch (bytesHandled)
      {

      case 3: 
        _currInputProcessed -= 3L;
        return _startDocument(ch);
      case 2: 
        if (ch != 191) {
          _reportError("Unexpected byte 0x%02x following 0xEF 0xBB; should get 0xBF as third byte of UTF-8 BOM", Integer.valueOf(ch));
        }
        break;
      case 1: 
        if (ch != 187)
          _reportError("Unexpected byte 0x%02x following 0xEF; should get 0xBB as second byte UTF-8 BOM", Integer.valueOf(ch));
        break;
      }
      
      bytesHandled++;
    }
    _pending32 = bytesHandled;
    _minorState = 1;
    return this._currToken = JsonToken.NOT_AVAILABLE;
  }
  










  private final JsonToken _startFieldName(int ch)
    throws IOException
  {
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 4;
        return _currToken;
      }
    }
    _updateTokenLocation();
    if (ch != 34) {
      if (ch == 125) {
        return _closeObjectScope();
      }
      return _handleOddName(ch);
    }
    
    if (_inputPtr + 13 <= _inputEnd) {
      String n = _fastParseName();
      if (n != null) {
        return _fieldComplete(n);
      }
    }
    return _parseEscapedName(0, 0, 0);
  }
  
  private final JsonToken _startFieldNameAfterComma(int ch)
    throws IOException
  {
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 5;
        return _currToken;
      }
    }
    if (ch != 44) {
      if (ch == 125) {
        return _closeObjectScope();
      }
      if (ch == 35) {
        return _finishHashComment(5);
      }
      if (ch == 47) {
        return _startSlashComment(5);
      }
      _reportUnexpectedChar(ch, "was expecting comma to separate " + _parsingContext.typeDesc() + " entries");
    }
    int ptr = _inputPtr;
    if (ptr >= _inputEnd) {
      _minorState = 4;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    ch = _inputBuffer[ptr];
    _inputPtr = (ptr + 1);
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 4;
        return _currToken;
      }
    }
    _updateTokenLocation();
    if (ch != 34) {
      if ((ch == 125) && 
        ((_features & FEAT_MASK_TRAILING_COMMA) != 0)) {
        return _closeObjectScope();
      }
      
      return _handleOddName(ch);
    }
    
    if (_inputPtr + 13 <= _inputEnd) {
      String n = _fastParseName();
      if (n != null) {
        return _fieldComplete(n);
      }
    }
    return _parseEscapedName(0, 0, 0);
  }
  











  private final JsonToken _startValue(int ch)
    throws IOException
  {
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 12;
        return _currToken;
      }
    }
    _updateTokenLocation();
    
    _parsingContext.expectComma();
    
    if (ch == 34) {
      return _startString();
    }
    switch (ch) {
    case 35: 
      return _finishHashComment(12);
    case 45: 
      return _startNegativeNumber();
    case 47: 
      return _startSlashComment(12);
    



    case 48: 
      return _startNumberLeadingZero();
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
    case 54: 
    case 55: 
    case 56: 
    case 57: 
      return _startPositiveNumber(ch);
    case 102: 
      return _startFalseToken();
    case 110: 
      return _startNullToken();
    case 116: 
      return _startTrueToken();
    case 91: 
      return _startArrayScope();
    case 93: 
      return _closeArrayScope();
    case 123: 
      return _startObjectScope();
    case 125: 
      return _closeObjectScope();
    }
    
    return _startUnexpectedValue(false, ch);
  }
  




  private final JsonToken _startValueExpectComma(int ch)
    throws IOException
  {
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 13;
        return _currToken;
      }
    }
    if (ch != 44) {
      if (ch == 93) {
        return _closeArrayScope();
      }
      if (ch == 125) {
        return _closeObjectScope();
      }
      if (ch == 47) {
        return _startSlashComment(13);
      }
      if (ch == 35) {
        return _finishHashComment(13);
      }
      _reportUnexpectedChar(ch, "was expecting comma to separate " + _parsingContext.typeDesc() + " entries");
    }
    

    _parsingContext.expectComma();
    
    int ptr = _inputPtr;
    if (ptr >= _inputEnd) {
      _minorState = 15;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    ch = _inputBuffer[ptr];
    _inputPtr = (ptr + 1);
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 15;
        return _currToken;
      }
    }
    _updateTokenLocation();
    if (ch == 34) {
      return _startString();
    }
    switch (ch) {
    case 35: 
      return _finishHashComment(15);
    case 45: 
      return _startNegativeNumber();
    case 47: 
      return _startSlashComment(15);
    



    case 48: 
      return _startNumberLeadingZero();
    case 49: case 50: 
    case 51: case 52: 
    case 53: case 54: 
    case 55: 
    case 56: 
    case 57: 
      return _startPositiveNumber(ch);
    case 102: 
      return _startFalseToken();
    case 110: 
      return _startNullToken();
    case 116: 
      return _startTrueToken();
    case 91: 
      return _startArrayScope();
    
    case 93: 
      if ((_features & FEAT_MASK_TRAILING_COMMA) != 0) {
        return _closeArrayScope();
      }
      break;
    case 123: 
      return _startObjectScope();
    
    case 125: 
      if ((_features & FEAT_MASK_TRAILING_COMMA) != 0) {
        return _closeObjectScope();
      }
      break;
    }
    
    return _startUnexpectedValue(true, ch);
  }
  





  private final JsonToken _startValueExpectColon(int ch)
    throws IOException
  {
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 14;
        return _currToken;
      }
    }
    if (ch != 58) {
      if (ch == 47) {
        return _startSlashComment(14);
      }
      if (ch == 35) {
        return _finishHashComment(14);
      }
      
      _reportUnexpectedChar(ch, "was expecting a colon to separate field name and value");
    }
    int ptr = _inputPtr;
    if (ptr >= _inputEnd) {
      _minorState = 12;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    ch = _inputBuffer[ptr];
    _inputPtr = (ptr + 1);
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 12;
        return _currToken;
      }
    }
    _updateTokenLocation();
    if (ch == 34) {
      return _startString();
    }
    switch (ch) {
    case 35: 
      return _finishHashComment(12);
    case 45: 
      return _startNegativeNumber();
    case 47: 
      return _startSlashComment(12);
    



    case 48: 
      return _startNumberLeadingZero();
    case 49: case 50: 
    case 51: case 52: 
    case 53: case 54: 
    case 55: 
    case 56: 
    case 57: 
      return _startPositiveNumber(ch);
    case 102: 
      return _startFalseToken();
    case 110: 
      return _startNullToken();
    case 116: 
      return _startTrueToken();
    case 91: 
      return _startArrayScope();
    case 123: 
      return _startObjectScope();
    }
    
    return _startUnexpectedValue(false, ch);
  }
  


  private final JsonToken _startValueAfterComma(int ch)
    throws IOException
  {
    if (ch <= 32) {
      ch = _skipWS(ch);
      if (ch <= 0) {
        _minorState = 15;
        return _currToken;
      }
    }
    _updateTokenLocation();
    if (ch == 34) {
      return _startString();
    }
    switch (ch) {
    case 35: 
      return _finishHashComment(15);
    case 45: 
      return _startNegativeNumber();
    case 47: 
      return _startSlashComment(15);
    



    case 48: 
      return _startNumberLeadingZero();
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
    case 54: 
    case 55: 
    case 56: 
    case 57: 
      return _startPositiveNumber(ch);
    case 102: 
      return _startFalseToken();
    case 110: 
      return _startNullToken();
    case 116: 
      return _startTrueToken();
    case 91: 
      return _startArrayScope();
    
    case 93: 
      if ((_features & FEAT_MASK_TRAILING_COMMA) != 0) {
        return _closeArrayScope();
      }
      break;
    case 123: 
      return _startObjectScope();
    
    case 125: 
      if ((_features & FEAT_MASK_TRAILING_COMMA) != 0) {
        return _closeObjectScope();
      }
      break;
    }
    
    return _startUnexpectedValue(true, ch);
  }
  
  protected JsonToken _startUnexpectedValue(boolean leadingComma, int ch) throws IOException
  {
    switch (ch) {
    case 93: 
      if (!_parsingContext.inArray()) {}
      




      break;
    case 44: 
      if ((_features & FEAT_MASK_ALLOW_MISSING) != 0) {
        _inputPtr -= 1;
        return _valueComplete(JsonToken.VALUE_NULL);
      }
    
    case 125: 
      break;
    

    case 39: 
      if ((_features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
        return _startAposString();
      }
      break;
    case 43: 
      return _finishNonStdToken(2, 1);
    case 78: 
      return _finishNonStdToken(0, 1);
    case 73: 
      return _finishNonStdToken(1, 1);
    }
    
    _reportUnexpectedChar(ch, "expected a valid value " + _validJsonValueList());
    return null;
  }
  




  private final int _skipWS(int ch)
    throws IOException
  {
    do
    {
      if (ch != 32) {
        if (ch == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (ch == 13) {
          _currInputRowAlt += 1;
          _currInputRowStart = _inputPtr;
        } else if (ch != 9) {
          _throwInvalidSpace(ch);
        }
      }
      if (_inputPtr >= _inputEnd) {
        _currToken = JsonToken.NOT_AVAILABLE;
        return 0;
      }
      ch = _inputBuffer[(_inputPtr++)] & 0xFF;
    } while (ch <= 32);
    return ch;
  }
  
  private final JsonToken _startSlashComment(int fromMinorState) throws IOException
  {
    if ((_features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
      _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
    }
    

    if (_inputPtr >= _inputEnd) {
      _pending32 = fromMinorState;
      _minorState = 51;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    int ch = _inputBuffer[(_inputPtr++)];
    if (ch == 42) {
      return _finishCComment(fromMinorState, false);
    }
    if (ch == 47) {
      return _finishCppComment(fromMinorState);
    }
    _reportUnexpectedChar(ch & 0xFF, "was expecting either '*' or '/' for a comment");
    return null;
  }
  
  private final JsonToken _finishHashComment(int fromMinorState)
    throws IOException
  {
    if ((_features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
      _reportUnexpectedChar(35, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_YAML_COMMENTS' not enabled for parser)");
    }
    for (;;) {
      if (_inputPtr >= _inputEnd) {
        _minorState = 55;
        _pending32 = fromMinorState;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      if (ch < 32) {
        if (ch == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
          break; }
        if (ch == 13) {
          _currInputRowAlt += 1;
          _currInputRowStart = _inputPtr;
          break; }
        if (ch != 9) {
          _throwInvalidSpace(ch);
        }
      }
    }
    return _startAfterComment(fromMinorState);
  }
  
  private final JsonToken _finishCppComment(int fromMinorState) throws IOException
  {
    for (;;) {
      if (_inputPtr >= _inputEnd) {
        _minorState = 54;
        _pending32 = fromMinorState;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      if (ch < 32) {
        if (ch == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
          break; }
        if (ch == 13) {
          _currInputRowAlt += 1;
          _currInputRowStart = _inputPtr;
          break; }
        if (ch != 9) {
          _throwInvalidSpace(ch);
        }
      }
    }
    return _startAfterComment(fromMinorState);
  }
  
  private final JsonToken _finishCComment(int fromMinorState, boolean gotStar) throws IOException
  {
    for (;;) {
      if (_inputPtr >= _inputEnd) {
        _minorState = (gotStar ? 52 : 53);
        _pending32 = fromMinorState;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      if (ch < 32) {
        if (ch == 10) {
          _currInputRow += 1;
          _currInputRowStart = _inputPtr;
        } else if (ch == 13) {
          _currInputRowAlt += 1;
          _currInputRowStart = _inputPtr;
        } else if (ch != 9) {
          _throwInvalidSpace(ch);
        }
      } else { if (ch == 42) {
          gotStar = true;
          continue; }
        if ((ch == 47) && 
          (gotStar)) {
          break;
        }
      }
      gotStar = false;
    }
    return _startAfterComment(fromMinorState);
  }
  
  private final JsonToken _startAfterComment(int fromMinorState)
    throws IOException
  {
    if (_inputPtr >= _inputEnd) {
      _minorState = fromMinorState;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
    switch (fromMinorState) {
    case 4: 
      return _startFieldName(ch);
    case 5: 
      return _startFieldNameAfterComma(ch);
    case 12: 
      return _startValue(ch);
    case 13: 
      return _startValueExpectComma(ch);
    case 14: 
      return _startValueExpectColon(ch);
    case 15: 
      return _startValueAfterComma(ch);
    }
    
    VersionUtil.throwInternal();
    return null;
  }
  





  protected JsonToken _startFalseToken()
    throws IOException
  {
    int ptr = _inputPtr;
    if (ptr + 4 < _inputEnd) {
      byte[] buf = _inputBuffer;
      if ((buf[(ptr++)] == 97) && (buf[(ptr++)] == 108) && (buf[(ptr++)] == 115) && (buf[(ptr++)] == 101))
      {


        int ch = buf[ptr] & 0xFF;
        if ((ch < 48) || (ch == 93) || (ch == 125)) {
          _inputPtr = ptr;
          return _valueComplete(JsonToken.VALUE_FALSE);
        }
      }
    }
    _minorState = 18;
    return _finishKeywordToken("false", 1, JsonToken.VALUE_FALSE);
  }
  
  protected JsonToken _startTrueToken() throws IOException
  {
    int ptr = _inputPtr;
    if (ptr + 3 < _inputEnd) {
      byte[] buf = _inputBuffer;
      if ((buf[(ptr++)] == 114) && (buf[(ptr++)] == 117) && (buf[(ptr++)] == 101))
      {

        int ch = buf[ptr] & 0xFF;
        if ((ch < 48) || (ch == 93) || (ch == 125)) {
          _inputPtr = ptr;
          return _valueComplete(JsonToken.VALUE_TRUE);
        }
      }
    }
    _minorState = 17;
    return _finishKeywordToken("true", 1, JsonToken.VALUE_TRUE);
  }
  
  protected JsonToken _startNullToken() throws IOException
  {
    int ptr = _inputPtr;
    if (ptr + 3 < _inputEnd) {
      byte[] buf = _inputBuffer;
      if ((buf[(ptr++)] == 117) && (buf[(ptr++)] == 108) && (buf[(ptr++)] == 108))
      {

        int ch = buf[ptr] & 0xFF;
        if ((ch < 48) || (ch == 93) || (ch == 125)) {
          _inputPtr = ptr;
          return _valueComplete(JsonToken.VALUE_NULL);
        }
      }
    }
    _minorState = 16;
    return _finishKeywordToken("null", 1, JsonToken.VALUE_NULL);
  }
  
  protected JsonToken _finishKeywordToken(String expToken, int matched, JsonToken result)
    throws IOException
  {
    int end = expToken.length();
    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _pending32 = matched;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[_inputPtr];
      if (matched == end) {
        if ((ch >= 48) && (ch != 93) && (ch != 125)) break;
        return _valueComplete(result);
      }
      

      if (ch != expToken.charAt(matched)) {
        break;
      }
      matched++;
      _inputPtr += 1;
    }
    _minorState = 50;
    _textBuffer.resetWithCopy(expToken, 0, matched);
    return _finishErrorToken();
  }
  
  protected JsonToken _finishKeywordTokenWithEOF(String expToken, int matched, JsonToken result)
    throws IOException
  {
    if (matched == expToken.length()) {
      return this._currToken = result;
    }
    _textBuffer.resetWithCopy(expToken, 0, matched);
    return _finishErrorTokenWithEOF();
  }
  
  protected JsonToken _finishNonStdToken(int type, int matched) throws IOException
  {
    String expToken = _nonStdToken(type);
    int end = expToken.length();
    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _nonStdTokenType = type;
        _pending32 = matched;
        _minorState = 19;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[_inputPtr];
      if (matched == end) {
        if ((ch >= 48) && (ch != 93) && (ch != 125)) break;
        return _valueNonStdNumberComplete(type);
      }
      

      if (ch != expToken.charAt(matched)) {
        break;
      }
      matched++;
      _inputPtr += 1;
    }
    _minorState = 50;
    _textBuffer.resetWithCopy(expToken, 0, matched);
    return _finishErrorToken();
  }
  
  protected JsonToken _finishNonStdTokenWithEOF(int type, int matched) throws IOException
  {
    String expToken = _nonStdToken(type);
    if (matched == expToken.length()) {
      return _valueNonStdNumberComplete(type);
    }
    _textBuffer.resetWithCopy(expToken, 0, matched);
    return _finishErrorTokenWithEOF();
  }
  
  protected JsonToken _finishErrorToken() throws IOException
  {
    while (_inputPtr < _inputEnd) {
      int i = _inputBuffer[(_inputPtr++)];
      



      char ch = (char)i;
      if (Character.isJavaIdentifierPart(ch))
      {

        _textBuffer.append(ch);
        if (_textBuffer.size() < 256) {
          break;
        }
      } else {
        return _reportErrorToken(_textBuffer.contentsAsString());
      } }
    return this._currToken = JsonToken.NOT_AVAILABLE;
  }
  
  protected JsonToken _finishErrorTokenWithEOF() throws IOException
  {
    return _reportErrorToken(_textBuffer.contentsAsString());
  }
  
  protected JsonToken _reportErrorToken(String actualToken)
    throws IOException
  {
    _reportError("Unrecognized token '%s': was expecting %s", _textBuffer.contentsAsString(), 
      _validJsonTokenList());
    return JsonToken.NOT_AVAILABLE;
  }
  





  protected JsonToken _startPositiveNumber(int ch)
    throws IOException
  {
    _numberNegative = false;
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    outBuf[0] = ((char)ch);
    
    if (_inputPtr >= _inputEnd) {
      _minorState = 26;
      _textBuffer.setCurrentLength(1);
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    
    int outPtr = 1;
    
    ch = _inputBuffer[_inputPtr] & 0xFF;
    for (;;) {
      if (ch < 48) {
        if (ch != 46) break;
        _intLength = outPtr;
        _inputPtr += 1;
        return _startFloat(outBuf, outPtr, ch);
      }
      

      if (ch > 57) {
        if ((ch != 101) && (ch != 69)) break;
        _intLength = outPtr;
        _inputPtr += 1;
        return _startFloat(outBuf, outPtr, ch);
      }
      

      if (outPtr >= outBuf.length)
      {

        outBuf = _textBuffer.expandCurrentSegment();
      }
      outBuf[(outPtr++)] = ((char)ch);
      if (++_inputPtr >= _inputEnd) {
        _minorState = 26;
        _textBuffer.setCurrentLength(outPtr);
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      ch = _inputBuffer[_inputPtr] & 0xFF;
    }
    _intLength = outPtr;
    _textBuffer.setCurrentLength(outPtr);
    return _valueComplete(JsonToken.VALUE_NUMBER_INT);
  }
  
  protected JsonToken _startNegativeNumber() throws IOException
  {
    _numberNegative = true;
    if (_inputPtr >= _inputEnd) {
      _minorState = 23;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
    if (ch <= 48) {
      if (ch == 48) {
        return _finishNumberLeadingNegZeroes();
      }
      
      reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
    } else if (ch > 57) {
      if (ch == 73) {
        return _finishNonStdToken(3, 2);
      }
      reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
    }
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    outBuf[0] = '-';
    outBuf[1] = ((char)ch);
    if (_inputPtr >= _inputEnd) {
      _minorState = 26;
      _textBuffer.setCurrentLength(2);
      _intLength = 1;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    ch = _inputBuffer[_inputPtr];
    int outPtr = 2;
    for (;;)
    {
      if (ch < 48) {
        if (ch != 46) break;
        _intLength = (outPtr - 1);
        _inputPtr += 1;
        return _startFloat(outBuf, outPtr, ch);
      }
      

      if (ch > 57) {
        if ((ch != 101) && (ch != 69)) break;
        _intLength = (outPtr - 1);
        _inputPtr += 1;
        return _startFloat(outBuf, outPtr, ch);
      }
      

      if (outPtr >= outBuf.length)
      {
        outBuf = _textBuffer.expandCurrentSegment();
      }
      outBuf[(outPtr++)] = ((char)ch);
      if (++_inputPtr >= _inputEnd) {
        _minorState = 26;
        _textBuffer.setCurrentLength(outPtr);
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      ch = _inputBuffer[_inputPtr] & 0xFF;
    }
    _intLength = (outPtr - 1);
    _textBuffer.setCurrentLength(outPtr);
    return _valueComplete(JsonToken.VALUE_NUMBER_INT);
  }
  
  protected JsonToken _startNumberLeadingZero() throws IOException
  {
    int ptr = _inputPtr;
    if (ptr >= _inputEnd) {
      _minorState = 24;
      return this._currToken = JsonToken.NOT_AVAILABLE;
    }
    




    int ch = _inputBuffer[(ptr++)] & 0xFF;
    
    if (ch < 48) {
      if (ch == 46) {
        _inputPtr = ptr;
        _intLength = 1;
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = '0';
        return _startFloat(outBuf, 1, ch);
      }
    } else if (ch > 57) {
      if ((ch == 101) || (ch == 69)) {
        _inputPtr = ptr;
        _intLength = 1;
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = '0';
        return _startFloat(outBuf, 1, ch);
      }
      


      if ((ch != 93) && (ch != 125)) {
        reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
      }
    }
    else
    {
      return _finishNumberLeadingZeroes();
    }
    
    return _valueCompleteInt(0, "0");
  }
  
  protected JsonToken _finishNumberMinus(int ch) throws IOException
  {
    if (ch <= 48) {
      if (ch == 48) {
        return _finishNumberLeadingNegZeroes();
      }
      reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
    } else if (ch > 57) {
      if (ch == 73) {
        return _finishNonStdToken(3, 2);
      }
      reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
    }
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    outBuf[0] = '-';
    outBuf[1] = ((char)ch);
    _intLength = 1;
    return _finishNumberIntegralPart(outBuf, 2);
  }
  
  protected JsonToken _finishNumberLeadingZeroes() throws IOException
  {
    int ch;
    do
    {
      if (_inputPtr >= _inputEnd) {
        _minorState = 24;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      if (ch < 48) {
        if (ch != 46) break;
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = '0';
        _intLength = 1;
        return _startFloat(outBuf, 1, ch);
      }
      if (ch > 57) {
        if ((ch == 101) || (ch == 69)) {
          char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
          outBuf[0] = '0';
          _intLength = 1;
          return _startFloat(outBuf, 1, ch);
        }
        


        if ((ch == 93) || (ch == 125)) break;
        reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'"); break;
      }
      



      if ((_features & FEAT_MASK_LEADING_ZEROS) == 0) {
        reportInvalidNumber("Leading zeroes not allowed");
      }
    } while (ch == 48);
    

    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    
    outBuf[0] = ((char)ch);
    _intLength = 1;
    return _finishNumberIntegralPart(outBuf, 1);
    
    _inputPtr -= 1;
    return _valueCompleteInt(0, "0");
  }
  
  protected JsonToken _finishNumberLeadingNegZeroes()
    throws IOException
  {
    int ch;
    do
    {
      if (_inputPtr >= _inputEnd) {
        _minorState = 25;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      if (ch < 48) {
        if (ch != 46) break;
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = '-';
        outBuf[1] = '0';
        _intLength = 1;
        return _startFloat(outBuf, 2, ch);
      }
      if (ch > 57) {
        if ((ch == 101) || (ch == 69)) {
          char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
          outBuf[0] = '-';
          outBuf[1] = '0';
          _intLength = 1;
          return _startFloat(outBuf, 2, ch);
        }
        


        if ((ch == 93) || (ch == 125)) break;
        reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'"); break;
      }
      



      if ((_features & FEAT_MASK_LEADING_ZEROS) == 0) {
        reportInvalidNumber("Leading zeroes not allowed");
      }
    } while (ch == 48);
    

    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    
    outBuf[0] = '-';
    outBuf[1] = ((char)ch);
    _intLength = 1;
    return _finishNumberIntegralPart(outBuf, 2);
    
    _inputPtr -= 1;
    return _valueCompleteInt(0, "0");
  }
  
  protected JsonToken _finishNumberIntegralPart(char[] outBuf, int outPtr)
    throws IOException
  {
    int negMod = _numberNegative ? -1 : 0;
    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _minorState = 26;
        _textBuffer.setCurrentLength(outPtr);
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[_inputPtr] & 0xFF;
      if (ch < 48) {
        if (ch != 46) break;
        _intLength = (outPtr + negMod);
        _inputPtr += 1;
        return _startFloat(outBuf, outPtr, ch);
      }
      

      if (ch > 57) {
        if ((ch != 101) && (ch != 69)) break;
        _intLength = (outPtr + negMod);
        _inputPtr += 1;
        return _startFloat(outBuf, outPtr, ch);
      }
      

      _inputPtr += 1;
      if (outPtr >= outBuf.length)
      {

        outBuf = _textBuffer.expandCurrentSegment();
      }
      outBuf[(outPtr++)] = ((char)ch);
    }
    _intLength = (outPtr + negMod);
    _textBuffer.setCurrentLength(outPtr);
    return _valueComplete(JsonToken.VALUE_NUMBER_INT);
  }
  
  protected JsonToken _startFloat(char[] outBuf, int outPtr, int ch) throws IOException
  {
    int fractLen = 0;
    if (ch == 46) {
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.expandCurrentSegment();
      }
      outBuf[(outPtr++)] = '.';
      for (;;) {
        if (_inputPtr >= _inputEnd) {
          _textBuffer.setCurrentLength(outPtr);
          _minorState = 30;
          _fractLength = fractLen;
          return this._currToken = JsonToken.NOT_AVAILABLE;
        }
        ch = _inputBuffer[(_inputPtr++)];
        if ((ch < 48) || (ch > 57)) {
          ch &= 0xFF;
          
          if (fractLen != 0) break;
          reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit"); break;
        }
        

        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.expandCurrentSegment();
        }
        outBuf[(outPtr++)] = ((char)ch);
        fractLen++;
      }
    }
    _fractLength = fractLen;
    int expLen = 0;
    if ((ch == 101) || (ch == 69)) {
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.expandCurrentSegment();
      }
      outBuf[(outPtr++)] = ((char)ch);
      if (_inputPtr >= _inputEnd) {
        _textBuffer.setCurrentLength(outPtr);
        _minorState = 31;
        _expLength = 0;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      ch = _inputBuffer[(_inputPtr++)];
      if ((ch == 45) || (ch == 43)) {
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.expandCurrentSegment();
        }
        outBuf[(outPtr++)] = ((char)ch);
        if (_inputPtr >= _inputEnd) {
          _textBuffer.setCurrentLength(outPtr);
          _minorState = 32;
          _expLength = 0;
          return this._currToken = JsonToken.NOT_AVAILABLE;
        }
        ch = _inputBuffer[(_inputPtr++)];
      }
      while ((ch >= 48) && (ch <= 57)) {
        expLen++;
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.expandCurrentSegment();
        }
        outBuf[(outPtr++)] = ((char)ch);
        if (_inputPtr >= _inputEnd) {
          _textBuffer.setCurrentLength(outPtr);
          _minorState = 32;
          _expLength = expLen;
          return this._currToken = JsonToken.NOT_AVAILABLE;
        }
        ch = _inputBuffer[(_inputPtr++)];
      }
      
      ch &= 0xFF;
      if (expLen == 0) {
        reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
      }
    }
    
    _inputPtr -= 1;
    _textBuffer.setCurrentLength(outPtr);
    
    _expLength = expLen;
    return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
  }
  
  protected JsonToken _finishFloatFraction() throws IOException
  {
    int fractLen = _fractLength;
    char[] outBuf = _textBuffer.getBufferWithoutReset();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    
    int ch;
    
    while (((ch = _inputBuffer[(_inputPtr++)]) >= 48) && (ch <= 57)) {
      fractLen++;
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.expandCurrentSegment();
      }
      outBuf[(outPtr++)] = ((char)ch);
      if (_inputPtr >= _inputEnd) {
        _textBuffer.setCurrentLength(outPtr);
        _fractLength = fractLen;
        return JsonToken.NOT_AVAILABLE;
      }
    }
    


    if (fractLen == 0) {
      reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
    }
    _fractLength = fractLen;
    _textBuffer.setCurrentLength(outPtr);
    

    if ((ch == 101) || (ch == 69)) {
      _textBuffer.append((char)ch);
      _expLength = 0;
      if (_inputPtr >= _inputEnd) {
        _minorState = 31;
        return JsonToken.NOT_AVAILABLE;
      }
      _minorState = 32;
      return _finishFloatExponent(true, _inputBuffer[(_inputPtr++)] & 0xFF);
    }
    

    _inputPtr -= 1;
    _textBuffer.setCurrentLength(outPtr);
    
    _expLength = 0;
    return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
  }
  
  protected JsonToken _finishFloatExponent(boolean checkSign, int ch) throws IOException
  {
    if (checkSign) {
      _minorState = 32;
      if ((ch == 45) || (ch == 43)) {
        _textBuffer.append((char)ch);
        if (_inputPtr >= _inputEnd) {
          _minorState = 32;
          _expLength = 0;
          return JsonToken.NOT_AVAILABLE;
        }
        ch = _inputBuffer[(_inputPtr++)];
      }
    }
    
    char[] outBuf = _textBuffer.getBufferWithoutReset();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    int expLen = _expLength;
    
    while ((ch >= 48) && (ch <= 57)) {
      expLen++;
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.expandCurrentSegment();
      }
      outBuf[(outPtr++)] = ((char)ch);
      if (_inputPtr >= _inputEnd) {
        _textBuffer.setCurrentLength(outPtr);
        _expLength = expLen;
        return JsonToken.NOT_AVAILABLE;
      }
      ch = _inputBuffer[(_inputPtr++)];
    }
    
    ch &= 0xFF;
    if (expLen == 0) {
      reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
    }
    
    _inputPtr -= 1;
    _textBuffer.setCurrentLength(outPtr);
    
    _expLength = expLen;
    return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
  }
  










  private final String _fastParseName()
    throws IOException
  {
    byte[] input = _inputBuffer;
    int[] codes = _icLatin1;
    int ptr = _inputPtr;
    
    int q0 = input[(ptr++)] & 0xFF;
    if (codes[q0] == 0) {
      int i = input[(ptr++)] & 0xFF;
      if (codes[i] == 0) {
        int q = q0 << 8 | i;
        i = input[(ptr++)] & 0xFF;
        if (codes[i] == 0) {
          q = q << 8 | i;
          i = input[(ptr++)] & 0xFF;
          if (codes[i] == 0) {
            q = q << 8 | i;
            i = input[(ptr++)] & 0xFF;
            if (codes[i] == 0) {
              _quad1 = q;
              return _parseMediumName(ptr, i);
            }
            if (i == 34) {
              _inputPtr = ptr;
              return _findName(q, 4);
            }
            return null;
          }
          if (i == 34) {
            _inputPtr = ptr;
            return _findName(q, 3);
          }
          return null;
        }
        if (i == 34) {
          _inputPtr = ptr;
          return _findName(q, 2);
        }
        return null;
      }
      if (i == 34) {
        _inputPtr = ptr;
        return _findName(q0, 1);
      }
      return null;
    }
    if (q0 == 34) {
      _inputPtr = ptr;
      return "";
    }
    return null;
  }
  
  private final String _parseMediumName(int ptr, int q2) throws IOException
  {
    byte[] input = _inputBuffer;
    int[] codes = _icLatin1;
    

    int i = input[(ptr++)] & 0xFF;
    if (codes[i] == 0) {
      q2 = q2 << 8 | i;
      i = input[(ptr++)] & 0xFF;
      if (codes[i] == 0) {
        q2 = q2 << 8 | i;
        i = input[(ptr++)] & 0xFF;
        if (codes[i] == 0) {
          q2 = q2 << 8 | i;
          i = input[(ptr++)] & 0xFF;
          if (codes[i] == 0) {
            return _parseMediumName2(ptr, i, q2);
          }
          if (i == 34) {
            _inputPtr = ptr;
            return _findName(_quad1, q2, 4);
          }
          return null;
        }
        if (i == 34) {
          _inputPtr = ptr;
          return _findName(_quad1, q2, 3);
        }
        return null;
      }
      if (i == 34) {
        _inputPtr = ptr;
        return _findName(_quad1, q2, 2);
      }
      return null;
    }
    if (i == 34) {
      _inputPtr = ptr;
      return _findName(_quad1, q2, 1);
    }
    return null;
  }
  
  private final String _parseMediumName2(int ptr, int q3, int q2) throws IOException
  {
    byte[] input = _inputBuffer;
    int[] codes = _icLatin1;
    

    int i = input[(ptr++)] & 0xFF;
    if (codes[i] != 0) {
      if (i == 34) {
        _inputPtr = ptr;
        return _findName(_quad1, q2, q3, 1);
      }
      return null;
    }
    q3 = q3 << 8 | i;
    i = input[(ptr++)] & 0xFF;
    if (codes[i] != 0) {
      if (i == 34) {
        _inputPtr = ptr;
        return _findName(_quad1, q2, q3, 2);
      }
      return null;
    }
    q3 = q3 << 8 | i;
    i = input[(ptr++)] & 0xFF;
    if (codes[i] != 0) {
      if (i == 34) {
        _inputPtr = ptr;
        return _findName(_quad1, q2, q3, 3);
      }
      return null;
    }
    q3 = q3 << 8 | i;
    i = input[(ptr++)] & 0xFF;
    if (i == 34) {
      _inputPtr = ptr;
      return _findName(_quad1, q2, q3, 4);
    }
    
    return null;
  }
  










  private final JsonToken _parseEscapedName(int qlen, int currQuad, int currQuadBytes)
    throws IOException
  {
    int[] quads = _quadBuffer;
    int[] codes = _icLatin1;
    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _quadLength = qlen;
        _pending32 = currQuad;
        _pendingBytes = currQuadBytes;
        _minorState = 7;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      if (codes[ch] == 0) {
        if (currQuadBytes < 4) {
          currQuadBytes++;
          currQuad = currQuad << 8 | ch;
        }
        else {
          if (qlen >= quads.length) {
            _quadBuffer = (quads = growArrayBy(quads, quads.length));
          }
          quads[(qlen++)] = currQuad;
          currQuad = ch;
          currQuadBytes = 1;
        }
      }
      else
      {
        if (ch == 34) {
          break;
        }
        
        if (ch != 92)
        {
          _throwUnquotedSpace(ch, "name");
        }
        else {
          ch = _decodeCharEscape();
          if (ch < 0) {
            _minorState = 8;
            _minorStateAfterSplit = 7;
            _quadLength = qlen;
            _pending32 = currQuad;
            _pendingBytes = currQuadBytes;
            return this._currToken = JsonToken.NOT_AVAILABLE;
          }
        }
        



        if (qlen >= quads.length) {
          _quadBuffer = (quads = growArrayBy(quads, quads.length));
        }
        if (ch > 127)
        {
          if (currQuadBytes >= 4) {
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
              quads[(qlen++)] = currQuad;
              currQuad = 0;
              currQuadBytes = 0;
            }
            currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
            currQuadBytes++;
          }
          
          ch = 0x80 | ch & 0x3F;
        }
        if (currQuadBytes < 4) {
          currQuadBytes++;
          currQuad = currQuad << 8 | ch;
        }
        else {
          quads[(qlen++)] = currQuad;
          currQuad = ch;
          currQuadBytes = 1;
        }
      } }
    if (currQuadBytes > 0) {
      if (qlen >= quads.length) {
        _quadBuffer = (quads = growArrayBy(quads, quads.length));
      }
      quads[(qlen++)] = _padLastQuad(currQuad, currQuadBytes);
    } else if (qlen == 0) {
      return _fieldComplete("");
    }
    String name = _symbols.findName(quads, qlen);
    if (name == null) {
      name = _addName(quads, qlen, currQuadBytes);
    }
    return _fieldComplete(name);
  }
  






  private JsonToken _handleOddName(int ch)
    throws IOException
  {
    switch (ch)
    {

    case 35: 
      if ((_features & FEAT_MASK_ALLOW_YAML_COMMENTS) != 0) {
        return _finishHashComment(4);
      }
      break;
    case 47: 
      return _startSlashComment(4);
    case 39: 
      if ((_features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
        return _finishAposName(0, 0, 0);
      }
      break;
    case 93: 
      return _closeArrayScope();
    }
    
    if ((_features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0)
    {

      char c = (char)ch;
      _reportUnexpectedChar(c, "was expecting double-quote to start field name");
    }
    

    int[] codes = CharTypes.getInputCodeUtf8JsNames();
    
    if (codes[ch] != 0) {
      _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
    }
    
    return _finishUnquotedName(0, ch, 1);
  }
  





  private JsonToken _finishUnquotedName(int qlen, int currQuad, int currQuadBytes)
    throws IOException
  {
    int[] quads = _quadBuffer;
    int[] codes = CharTypes.getInputCodeUtf8JsNames();
    

    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _quadLength = qlen;
        _pending32 = currQuad;
        _pendingBytes = currQuadBytes;
        _minorState = 10;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[_inputPtr] & 0xFF;
      if (codes[ch] != 0) {
        break;
      }
      _inputPtr += 1;
      
      if (currQuadBytes < 4) {
        currQuadBytes++;
        currQuad = currQuad << 8 | ch;
      } else {
        if (qlen >= quads.length) {
          _quadBuffer = (quads = growArrayBy(quads, quads.length));
        }
        quads[(qlen++)] = currQuad;
        currQuad = ch;
        currQuadBytes = 1;
      }
    }
    
    if (currQuadBytes > 0) {
      if (qlen >= quads.length) {
        _quadBuffer = (quads = growArrayBy(quads, quads.length));
      }
      quads[(qlen++)] = currQuad;
    }
    String name = _symbols.findName(quads, qlen);
    if (name == null) {
      name = _addName(quads, qlen, currQuadBytes);
    }
    return _fieldComplete(name);
  }
  
  private JsonToken _finishAposName(int qlen, int currQuad, int currQuadBytes)
    throws IOException
  {
    int[] quads = _quadBuffer;
    int[] codes = _icLatin1;
    for (;;)
    {
      if (_inputPtr >= _inputEnd) {
        _quadLength = qlen;
        _pending32 = currQuad;
        _pendingBytes = currQuadBytes;
        _minorState = 9;
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      int ch = _inputBuffer[(_inputPtr++)] & 0xFF;
      if (ch == 39) {
        break;
      }
      
      if ((ch != 34) && (codes[ch] != 0)) {
        if (ch != 92)
        {
          _throwUnquotedSpace(ch, "name");
        }
        else {
          ch = _decodeCharEscape();
          if (ch < 0) {
            _minorState = 8;
            _minorStateAfterSplit = 9;
            _quadLength = qlen;
            _pending32 = currQuad;
            _pendingBytes = currQuadBytes;
            return this._currToken = JsonToken.NOT_AVAILABLE;
          }
        }
        if (ch > 127)
        {
          if (currQuadBytes >= 4) {
            if (qlen >= quads.length) {
              _quadBuffer = (quads = growArrayBy(quads, quads.length));
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
                _quadBuffer = (quads = growArrayBy(quads, quads.length));
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
          _quadBuffer = (quads = growArrayBy(quads, quads.length));
        }
        quads[(qlen++)] = currQuad;
        currQuad = ch;
        currQuadBytes = 1;
      }
    }
    
    if (currQuadBytes > 0) {
      if (qlen >= quads.length) {
        _quadBuffer = (quads = growArrayBy(quads, quads.length));
      }
      quads[(qlen++)] = _padLastQuad(currQuad, currQuadBytes);
    } else if (qlen == 0) {
      return _fieldComplete("");
    }
    String name = _symbols.findName(quads, qlen);
    if (name == null) {
      name = _addName(quads, qlen, currQuadBytes);
    }
    return _fieldComplete(name);
  }
  
  protected final JsonToken _finishFieldWithEscape()
    throws IOException
  {
    int ch = _decodeSplitEscaped(_quoted32, _quotedDigits);
    if (ch < 0) {
      _minorState = 8;
      return JsonToken.NOT_AVAILABLE;
    }
    if (_quadLength >= _quadBuffer.length) {
      _quadBuffer = growArrayBy(_quadBuffer, 32);
    }
    int currQuad = _pending32;
    int currQuadBytes = _pendingBytes;
    if (ch > 127)
    {
      if (currQuadBytes >= 4) {
        _quadBuffer[(_quadLength++)] = currQuad;
        currQuad = 0;
        currQuadBytes = 0;
      }
      if (ch < 2048) {
        currQuad = currQuad << 8 | 0xC0 | ch >> 6;
        currQuadBytes++;
      }
      else {
        currQuad = currQuad << 8 | 0xE0 | ch >> 12;
        
        currQuadBytes++; if (currQuadBytes >= 4) {
          _quadBuffer[(_quadLength++)] = currQuad;
          currQuad = 0;
          currQuadBytes = 0;
        }
        currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
        currQuadBytes++;
      }
      
      ch = 0x80 | ch & 0x3F;
    }
    if (currQuadBytes < 4) {
      currQuadBytes++;
      currQuad = currQuad << 8 | ch;
    } else {
      _quadBuffer[(_quadLength++)] = currQuad;
      currQuad = ch;
      currQuadBytes = 1;
    }
    if (_minorStateAfterSplit == 9) {
      return _finishAposName(_quadLength, currQuad, currQuadBytes);
    }
    return _parseEscapedName(_quadLength, currQuad, currQuadBytes);
  }
  
  private int _decodeSplitEscaped(int value, int bytesRead) throws IOException
  {
    if (_inputPtr >= _inputEnd) {
      _quoted32 = value;
      _quotedDigits = bytesRead;
      return -1;
    }
    int c = _inputBuffer[(_inputPtr++)];
    if (bytesRead == -1) {
      switch (c)
      {
      case 98: 
        return 8;
      case 116: 
        return 9;
      case 110: 
        return 10;
      case 102: 
        return 12;
      case 114: 
        return 13;
      

      case 34: 
      case 47: 
      case 92: 
        return c;
      

      case 117: 
        break;
      


      default: 
        char ch = (char)c;
        return _handleUnrecognizedCharacterEscape(ch);
      }
      
      if (_inputPtr >= _inputEnd) {
        _quotedDigits = 0;
        _quoted32 = 0;
        return -1;
      }
      c = _inputBuffer[(_inputPtr++)];
      bytesRead = 0;
    }
    c &= 0xFF;
    for (;;) {
      int digit = CharTypes.charToHex(c);
      if (digit < 0) {
        _reportUnexpectedChar(c & 0xFF, "expected a hex-digit for character escape sequence");
      }
      value = value << 4 | digit;
      bytesRead++; if (bytesRead == 4) {
        return value;
      }
      if (_inputPtr >= _inputEnd) {
        _quotedDigits = bytesRead;
        _quoted32 = value;
        return -1;
      }
      c = _inputBuffer[(_inputPtr++)] & 0xFF;
    }
  }
  





  protected JsonToken _startString()
    throws IOException
  {
    int ptr = _inputPtr;
    int outPtr = 0;
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int[] codes = _icUTF8;
    
    int max = Math.min(_inputEnd, ptr + outBuf.length);
    byte[] inputBuffer = _inputBuffer;
    while (ptr < max) {
      int c = inputBuffer[ptr] & 0xFF;
      if (codes[c] != 0) {
        if (c != 34) break;
        _inputPtr = (ptr + 1);
        _textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_STRING);
      }
      

      ptr++;
      outBuf[(outPtr++)] = ((char)c);
    }
    _textBuffer.setCurrentLength(outPtr);
    _inputPtr = ptr;
    return _finishRegularString();
  }
  


  private final JsonToken _finishRegularString()
    throws IOException
  {
    int[] codes = _icUTF8;
    byte[] inputBuffer = _inputBuffer;
    
    char[] outBuf = _textBuffer.getBufferWithoutReset();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    int ptr = _inputPtr;
    int safeEnd = _inputEnd - 5;
    



    for (;;)
    {
      if (ptr >= _inputEnd) {
        _inputPtr = ptr;
        _minorState = 40;
        _textBuffer.setCurrentLength(outPtr);
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      int max = Math.min(_inputEnd, ptr + (outBuf.length - outPtr));
      while (ptr < max) {
        int c = inputBuffer[(ptr++)] & 0xFF;
        if (codes[c] != 0) {
          break label162;
        }
        outBuf[(outPtr++)] = ((char)c);
      }
      continue;
      label162:
      int c; if (c == 34) {
        _inputPtr = ptr;
        _textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_STRING);
      }
      
      if (ptr >= safeEnd) {
        _inputPtr = ptr;
        _textBuffer.setCurrentLength(outPtr);
        if (!_decodeSplitMultiByte(c, codes[c], ptr < _inputEnd)) {
          _minorStateAfterSplit = 40;
          return this._currToken = JsonToken.NOT_AVAILABLE;
        }
        outBuf = _textBuffer.getBufferWithoutReset();
        outPtr = _textBuffer.getCurrentSegmentSize();
        ptr = _inputPtr;
      }
      else
      {
        switch (codes[c]) {
        case 1: 
          _inputPtr = ptr;
          c = _decodeFastCharEscape();
          ptr = _inputPtr;
          break;
        case 2: 
          c = _decodeUTF8_2(c, _inputBuffer[(ptr++)]);
          break;
        case 3: 
          c = _decodeUTF8_3(c, _inputBuffer[(ptr++)], _inputBuffer[(ptr++)]);
          break;
        case 4: 
          c = _decodeUTF8_4(c, _inputBuffer[(ptr++)], _inputBuffer[(ptr++)], _inputBuffer[(ptr++)]);
          

          outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
          if (outPtr >= outBuf.length) {
            outBuf = _textBuffer.finishCurrentSegment();
            outPtr = 0;
          }
          c = 0xDC00 | c & 0x3FF;
          
          break;
        default: 
          if (c < 32)
          {
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
        }
        
        outBuf[(outPtr++)] = ((char)c);
      }
    }
  }
  
  protected JsonToken _startAposString() throws IOException {
    int ptr = _inputPtr;
    int outPtr = 0;
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int[] codes = _icUTF8;
    
    int max = Math.min(_inputEnd, ptr + outBuf.length);
    byte[] inputBuffer = _inputBuffer;
    while (ptr < max) {
      int c = inputBuffer[ptr] & 0xFF;
      if (c == 39) {
        _inputPtr = (ptr + 1);
        _textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_STRING);
      }
      
      if (codes[c] != 0) {
        break;
      }
      ptr++;
      outBuf[(outPtr++)] = ((char)c);
    }
    _textBuffer.setCurrentLength(outPtr);
    _inputPtr = ptr;
    return _finishAposString();
  }
  
  private final JsonToken _finishAposString()
    throws IOException
  {
    int[] codes = _icUTF8;
    byte[] inputBuffer = _inputBuffer;
    
    char[] outBuf = _textBuffer.getBufferWithoutReset();
    int outPtr = _textBuffer.getCurrentSegmentSize();
    int ptr = _inputPtr;
    int safeEnd = _inputEnd - 5;
    


    for (;;)
    {
      if (ptr >= _inputEnd) {
        _inputPtr = ptr;
        _minorState = 45;
        _textBuffer.setCurrentLength(outPtr);
        return this._currToken = JsonToken.NOT_AVAILABLE;
      }
      if (outPtr >= outBuf.length) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
      }
      int max = Math.min(_inputEnd, ptr + (outBuf.length - outPtr));
      while (ptr < max) {
        int c = inputBuffer[(ptr++)] & 0xFF;
        if ((codes[c] != 0) && (c != 34)) {
          break label197;
        }
        if (c == 39) {
          _inputPtr = ptr;
          _textBuffer.setCurrentLength(outPtr);
          return _valueComplete(JsonToken.VALUE_STRING);
        }
        outBuf[(outPtr++)] = ((char)c);
      }
      continue;
      label197:
      int c;
      if (ptr >= safeEnd) {
        _inputPtr = ptr;
        _textBuffer.setCurrentLength(outPtr);
        if (!_decodeSplitMultiByte(c, codes[c], ptr < _inputEnd)) {
          _minorStateAfterSplit = 45;
          return this._currToken = JsonToken.NOT_AVAILABLE;
        }
        outBuf = _textBuffer.getBufferWithoutReset();
        outPtr = _textBuffer.getCurrentSegmentSize();
        ptr = _inputPtr;
      }
      else
      {
        switch (codes[c]) {
        case 1: 
          _inputPtr = ptr;
          c = _decodeFastCharEscape();
          ptr = _inputPtr;
          break;
        case 2: 
          c = _decodeUTF8_2(c, _inputBuffer[(ptr++)]);
          break;
        case 3: 
          c = _decodeUTF8_3(c, _inputBuffer[(ptr++)], _inputBuffer[(ptr++)]);
          break;
        case 4: 
          c = _decodeUTF8_4(c, _inputBuffer[(ptr++)], _inputBuffer[(ptr++)], _inputBuffer[(ptr++)]);
          

          outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
          if (outPtr >= outBuf.length) {
            outBuf = _textBuffer.finishCurrentSegment();
            outPtr = 0;
          }
          c = 0xDC00 | c & 0x3FF;
          
          break;
        default: 
          if (c < 32)
          {
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
        }
        
        outBuf[(outPtr++)] = ((char)c);
      }
    }
  }
  
  private final boolean _decodeSplitMultiByte(int c, int type, boolean gotNext) throws IOException
  {
    switch (type) {
    case 1: 
      c = _decodeSplitEscaped(0, -1);
      if (c < 0) {
        _minorState = 41;
        return false;
      }
      _textBuffer.append((char)c);
      return true;
    case 2: 
      if (gotNext)
      {
        c = _decodeUTF8_2(c, _inputBuffer[(_inputPtr++)]);
        _textBuffer.append((char)c);
        return true;
      }
      _minorState = 42;
      _pending32 = c;
      return false;
    case 3: 
      c &= 0xF;
      if (gotNext) {
        return _decodeSplitUTF8_3(c, 1, _inputBuffer[(_inputPtr++)]);
      }
      _minorState = 43;
      _pending32 = c;
      _pendingBytes = 1;
      return false;
    case 4: 
      c &= 0x7;
      if (gotNext) {
        return _decodeSplitUTF8_4(c, 1, _inputBuffer[(_inputPtr++)]);
      }
      _pending32 = c;
      _pendingBytes = 1;
      _minorState = 44;
      return false;
    }
    if (c < 32)
    {
      _throwUnquotedSpace(c, "string value");
    }
    else {
      _reportInvalidChar(c);
    }
    _textBuffer.append((char)c);
    return true;
  }
  

  private final boolean _decodeSplitUTF8_3(int prev, int prevCount, int next)
    throws IOException
  {
    if (prevCount == 1) {
      if ((next & 0xC0) != 128) {
        _reportInvalidOther(next & 0xFF, _inputPtr);
      }
      prev = prev << 6 | next & 0x3F;
      if (_inputPtr >= _inputEnd) {
        _minorState = 43;
        _pending32 = prev;
        _pendingBytes = 2;
        return false;
      }
      next = _inputBuffer[(_inputPtr++)];
    }
    if ((next & 0xC0) != 128) {
      _reportInvalidOther(next & 0xFF, _inputPtr);
    }
    _textBuffer.append((char)(prev << 6 | next & 0x3F));
    return true;
  }
  


  private final boolean _decodeSplitUTF8_4(int prev, int prevCount, int next)
    throws IOException
  {
    if (prevCount == 1) {
      if ((next & 0xC0) != 128) {
        _reportInvalidOther(next & 0xFF, _inputPtr);
      }
      prev = prev << 6 | next & 0x3F;
      if (_inputPtr >= _inputEnd) {
        _minorState = 44;
        _pending32 = prev;
        _pendingBytes = 2;
        return false;
      }
      prevCount = 2;
      next = _inputBuffer[(_inputPtr++)];
    }
    if (prevCount == 2) {
      if ((next & 0xC0) != 128) {
        _reportInvalidOther(next & 0xFF, _inputPtr);
      }
      prev = prev << 6 | next & 0x3F;
      if (_inputPtr >= _inputEnd) {
        _minorState = 44;
        _pending32 = prev;
        _pendingBytes = 3;
        return false;
      }
      next = _inputBuffer[(_inputPtr++)];
    }
    if ((next & 0xC0) != 128) {
      _reportInvalidOther(next & 0xFF, _inputPtr);
    }
    int c = (prev << 6 | next & 0x3F) - 65536;
    
    _textBuffer.append((char)(0xD800 | c >> 10));
    c = 0xDC00 | c & 0x3FF;
    
    _textBuffer.append((char)c);
    return true;
  }
  





  private final int _decodeCharEscape()
    throws IOException
  {
    int left = _inputEnd - _inputPtr;
    if (left < 5) {
      return _decodeSplitEscaped(0, -1);
    }
    return _decodeFastCharEscape();
  }
  
  private final int _decodeFastCharEscape() throws IOException
  {
    int c = _inputBuffer[(_inputPtr++)];
    switch (c)
    {
    case 98: 
      return 8;
    case 116: 
      return 9;
    case 110: 
      return 10;
    case 102: 
      return 12;
    case 114: 
      return 13;
    

    case 34: 
    case 47: 
    case 92: 
      return (char)c;
    

    case 117: 
      break;
    


    default: 
      char ch = (char)c;
      return _handleUnrecognizedCharacterEscape(ch);
    }
    
    
    int ch = _inputBuffer[(_inputPtr++)];
    int digit = CharTypes.charToHex(ch);
    int result = digit;
    
    if (digit >= 0) {
      ch = _inputBuffer[(_inputPtr++)];
      digit = CharTypes.charToHex(ch);
      if (digit >= 0) {
        result = result << 4 | digit;
        ch = _inputBuffer[(_inputPtr++)];
        digit = CharTypes.charToHex(ch);
        if (digit >= 0) {
          result = result << 4 | digit;
          ch = _inputBuffer[(_inputPtr++)];
          digit = CharTypes.charToHex(ch);
          if (digit >= 0) {
            return result << 4 | digit;
          }
        }
      }
    }
    _reportUnexpectedChar(ch & 0xFF, "expected a hex-digit for character escape sequence");
    return -1;
  }
  





  private final int _decodeUTF8_2(int c, int d)
    throws IOException
  {
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    return (c & 0x1F) << 6 | d & 0x3F;
  }
  
  private final int _decodeUTF8_3(int c, int d, int e) throws IOException
  {
    c &= 0xF;
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = c << 6 | d & 0x3F;
    if ((e & 0xC0) != 128) {
      _reportInvalidOther(e & 0xFF, _inputPtr);
    }
    return c << 6 | e & 0x3F;
  }
  

  private final int _decodeUTF8_4(int c, int d, int e, int f)
    throws IOException
  {
    if ((d & 0xC0) != 128) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = (c & 0x7) << 6 | d & 0x3F;
    if ((e & 0xC0) != 128) {
      _reportInvalidOther(e & 0xFF, _inputPtr);
    }
    c = c << 6 | e & 0x3F;
    if ((f & 0xC0) != 128) {
      _reportInvalidOther(f & 0xFF, _inputPtr);
    }
    return (c << 6 | f & 0x3F) - 65536;
  }
}
