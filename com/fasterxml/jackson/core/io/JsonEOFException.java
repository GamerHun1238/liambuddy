package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;












public class JsonEOFException
  extends JsonParseException
{
  private static final long serialVersionUID = 1L;
  protected final JsonToken _token;
  
  public JsonEOFException(JsonParser p, JsonToken token, String msg)
  {
    super(p, msg);
    _token = token;
  }
  



  public JsonToken getTokenBeingDecoded()
  {
    return _token;
  }
}
