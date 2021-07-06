package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;









































public class JsonParserSequence
  extends JsonParserDelegate
{
  protected final JsonParser[] _parsers;
  protected final boolean _checkForExistingToken;
  protected int _nextParserIndex;
  protected boolean _hasToken;
  
  @Deprecated
  protected JsonParserSequence(JsonParser[] parsers)
  {
    this(false, parsers);
  }
  



  protected JsonParserSequence(boolean checkForExistingToken, JsonParser[] parsers)
  {
    super(parsers[0]);
    _checkForExistingToken = checkForExistingToken;
    _hasToken = ((checkForExistingToken) && (delegate.hasCurrentToken()));
    _parsers = parsers;
    _nextParserIndex = 1;
  }
  










  public static JsonParserSequence createFlattened(boolean checkForExistingToken, JsonParser first, JsonParser second)
  {
    if ((!(first instanceof JsonParserSequence)) && (!(second instanceof JsonParserSequence))) {
      return new JsonParserSequence(checkForExistingToken, new JsonParser[] { first, second });
    }
    
    ArrayList<JsonParser> p = new ArrayList();
    if ((first instanceof JsonParserSequence)) {
      ((JsonParserSequence)first).addFlattenedActiveParsers(p);
    } else {
      p.add(first);
    }
    if ((second instanceof JsonParserSequence)) {
      ((JsonParserSequence)second).addFlattenedActiveParsers(p);
    } else {
      p.add(second);
    }
    return new JsonParserSequence(checkForExistingToken, 
      (JsonParser[])p.toArray(new JsonParser[p.size()]));
  }
  



  @Deprecated
  public static JsonParserSequence createFlattened(JsonParser first, JsonParser second)
  {
    return createFlattened(false, first, second);
  }
  

  protected void addFlattenedActiveParsers(List<JsonParser> listToAddIn)
  {
    int i = _nextParserIndex - 1; for (int len = _parsers.length; i < len; i++) {
      JsonParser p = _parsers[i];
      if ((p instanceof JsonParserSequence)) {
        ((JsonParserSequence)p).addFlattenedActiveParsers(listToAddIn);
      } else {
        listToAddIn.add(p);
      }
    }
  }
  




  public void close()
    throws IOException
  {
    do
    {
      delegate.close(); } while (switchToNext());
  }
  
  public JsonToken nextToken()
    throws IOException
  {
    if (delegate == null) {
      return null;
    }
    if (_hasToken) {
      _hasToken = false;
      return delegate.currentToken();
    }
    JsonToken t = delegate.nextToken();
    if (t == null) {
      return switchAndReturnNext();
    }
    return t;
  }
  





  public JsonParser skipChildren()
    throws IOException
  {
    if ((delegate.currentToken() != JsonToken.START_OBJECT) && 
      (delegate.currentToken() != JsonToken.START_ARRAY)) {
      return this;
    }
    int open = 1;
    

    for (;;)
    {
      JsonToken t = nextToken();
      if (t == null) {
        return this;
      }
      if (t.isStructStart()) {
        open++;
      } else if (t.isStructEnd()) {
        open--; if (open == 0) {
          return this;
        }
      }
    }
  }
  










  public int containedParsersCount()
  {
    return _parsers.length;
  }
  















  protected boolean switchToNext()
  {
    if (_nextParserIndex < _parsers.length) {
      delegate = _parsers[(_nextParserIndex++)];
      return true;
    }
    return false;
  }
  
  protected JsonToken switchAndReturnNext() throws IOException
  {
    while (_nextParserIndex < _parsers.length) {
      delegate = _parsers[(_nextParserIndex++)];
      if ((_checkForExistingToken) && (delegate.hasCurrentToken())) {
        return delegate.getCurrentToken();
      }
      JsonToken t = delegate.nextToken();
      if (t != null) {
        return t;
      }
    }
    return null;
  }
}
