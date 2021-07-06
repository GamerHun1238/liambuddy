package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;



































public class TreeTraversingParser
  extends ParserMinimalBase
{
  protected ObjectCodec _objectCodec;
  protected NodeCursor _nodeCursor;
  protected JsonToken _nextToken;
  protected boolean _startContainer;
  protected boolean _closed;
  
  public TreeTraversingParser(JsonNode n)
  {
    this(n, null);
  }
  
  public TreeTraversingParser(JsonNode n, ObjectCodec codec) {
    super(0);
    _objectCodec = codec;
    if (n.isArray()) {
      _nextToken = JsonToken.START_ARRAY;
      _nodeCursor = new NodeCursor.ArrayCursor(n, null);
    } else if (n.isObject()) {
      _nextToken = JsonToken.START_OBJECT;
      _nodeCursor = new NodeCursor.ObjectCursor(n, null);
    } else {
      _nodeCursor = new NodeCursor.RootCursor(n, null);
    }
  }
  
  public void setCodec(ObjectCodec c)
  {
    _objectCodec = c;
  }
  
  public ObjectCodec getCodec()
  {
    return _objectCodec;
  }
  
  public Version version()
  {
    return PackageVersion.VERSION;
  }
  






  public void close()
    throws IOException
  {
    if (!_closed) {
      _closed = true;
      _nodeCursor = null;
      _currToken = null;
    }
  }
  






  public JsonToken nextToken()
    throws IOException, JsonParseException
  {
    if (_nextToken != null) {
      _currToken = _nextToken;
      _nextToken = null;
      return _currToken;
    }
    
    if (_startContainer) {
      _startContainer = false;
      
      if (!_nodeCursor.currentHasChildren()) {
        _currToken = (_currToken == JsonToken.START_OBJECT ? JsonToken.END_OBJECT : JsonToken.END_ARRAY);
        
        return _currToken;
      }
      _nodeCursor = _nodeCursor.iterateChildren();
      _currToken = _nodeCursor.nextToken();
      if ((_currToken == JsonToken.START_OBJECT) || (_currToken == JsonToken.START_ARRAY)) {
        _startContainer = true;
      }
      return _currToken;
    }
    
    if (_nodeCursor == null) {
      _closed = true;
      return null;
    }
    
    _currToken = _nodeCursor.nextToken();
    if (_currToken != null) {
      if ((_currToken == JsonToken.START_OBJECT) || (_currToken == JsonToken.START_ARRAY)) {
        _startContainer = true;
      }
      return _currToken;
    }
    
    _currToken = _nodeCursor.endToken();
    _nodeCursor = _nodeCursor.getParent();
    return _currToken;
  }
  



  public JsonParser skipChildren()
    throws IOException, JsonParseException
  {
    if (_currToken == JsonToken.START_OBJECT) {
      _startContainer = false;
      _currToken = JsonToken.END_OBJECT;
    } else if (_currToken == JsonToken.START_ARRAY) {
      _startContainer = false;
      _currToken = JsonToken.END_ARRAY;
    }
    return this;
  }
  
  public boolean isClosed()
  {
    return _closed;
  }
  






  public String getCurrentName()
  {
    return _nodeCursor == null ? null : _nodeCursor.getCurrentName();
  }
  

  public void overrideCurrentName(String name)
  {
    if (_nodeCursor != null) {
      _nodeCursor.overrideCurrentName(name);
    }
  }
  
  public JsonStreamContext getParsingContext()
  {
    return _nodeCursor;
  }
  
  public JsonLocation getTokenLocation()
  {
    return JsonLocation.NA;
  }
  
  public JsonLocation getCurrentLocation()
  {
    return JsonLocation.NA;
  }
  







  public String getText()
  {
    if (_closed) {
      return null;
    }
    
    switch (1.$SwitchMap$com$fasterxml$jackson$core$JsonToken[_currToken.ordinal()]) {
    case 1: 
      return _nodeCursor.getCurrentName();
    case 2: 
      return currentNode().textValue();
    case 3: 
    case 4: 
      return String.valueOf(currentNode().numberValue());
    case 5: 
      JsonNode n = currentNode();
      if ((n != null) && (n.isBinary()))
      {
        return n.asText(); }
      break;
    }
    return _currToken == null ? null : _currToken.asString();
  }
  
  public char[] getTextCharacters()
    throws IOException, JsonParseException
  {
    return getText().toCharArray();
  }
  
  public int getTextLength() throws IOException, JsonParseException
  {
    return getText().length();
  }
  
  public int getTextOffset() throws IOException, JsonParseException
  {
    return 0;
  }
  

  public boolean hasTextCharacters()
  {
    return false;
  }
  







  public JsonParser.NumberType getNumberType()
    throws IOException
  {
    JsonNode n = currentNumericNode();
    return n == null ? null : n.numberType();
  }
  
  public BigInteger getBigIntegerValue()
    throws IOException
  {
    return currentNumericNode().bigIntegerValue();
  }
  
  public BigDecimal getDecimalValue() throws IOException
  {
    return currentNumericNode().decimalValue();
  }
  
  public double getDoubleValue() throws IOException
  {
    return currentNumericNode().doubleValue();
  }
  
  public float getFloatValue() throws IOException
  {
    return (float)currentNumericNode().doubleValue();
  }
  
  public int getIntValue() throws IOException
  {
    NumericNode node = (NumericNode)currentNumericNode();
    if (!node.canConvertToInt()) {
      reportOverflowInt();
    }
    return node.intValue();
  }
  
  public long getLongValue() throws IOException
  {
    NumericNode node = (NumericNode)currentNumericNode();
    if (!node.canConvertToLong()) {
      reportOverflowLong();
    }
    return node.longValue();
  }
  
  public Number getNumberValue() throws IOException
  {
    return currentNumericNode().numberValue();
  }
  

  public Object getEmbeddedObject()
  {
    if (!_closed) {
      JsonNode n = currentNode();
      if (n != null) {
        if (n.isPojo()) {
          return ((POJONode)n).getPojo();
        }
        if (n.isBinary()) {
          return ((BinaryNode)n).binaryValue();
        }
      }
    }
    return null;
  }
  
  public boolean isNaN()
  {
    if (!_closed) {
      JsonNode n = currentNode();
      if ((n instanceof NumericNode)) {
        return ((NumericNode)n).isNaN();
      }
    }
    return false;
  }
  








  public byte[] getBinaryValue(Base64Variant b64variant)
    throws IOException, JsonParseException
  {
    JsonNode n = currentNode();
    if (n != null)
    {

      if ((n instanceof TextNode)) {
        return ((TextNode)n).getBinaryValue(b64variant);
      }
      return n.binaryValue();
    }
    
    return null;
  }
  


  public int readBinaryValue(Base64Variant b64variant, OutputStream out)
    throws IOException, JsonParseException
  {
    byte[] data = getBinaryValue(b64variant);
    if (data != null) {
      out.write(data, 0, data.length);
      return data.length;
    }
    return 0;
  }
  





  protected JsonNode currentNode()
  {
    if ((_closed) || (_nodeCursor == null)) {
      return null;
    }
    return _nodeCursor.currentNode();
  }
  
  protected JsonNode currentNumericNode()
    throws JsonParseException
  {
    JsonNode n = currentNode();
    if ((n == null) || (!n.isNumber())) {
      JsonToken t = n == null ? null : n.asToken();
      throw _constructError("Current token (" + t + ") not numeric, cannot use numeric value accessors");
    }
    return n;
  }
  
  protected void _handleEOF() throws JsonParseException
  {
    _throwInternal();
  }
}
