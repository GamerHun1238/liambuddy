package com.fasterxml.jackson.core.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.RequestPayload;












public abstract class StreamReadException
  extends JsonProcessingException
{
  static final long serialVersionUID = 1L;
  protected transient JsonParser _processor;
  protected RequestPayload _requestPayload;
  
  public StreamReadException(JsonParser p, String msg)
  {
    super(msg, p == null ? null : p.getCurrentLocation());
    _processor = p;
  }
  
  public StreamReadException(JsonParser p, String msg, Throwable root) {
    super(msg, p == null ? null : p.getCurrentLocation(), root);
    _processor = p;
  }
  
  public StreamReadException(JsonParser p, String msg, JsonLocation loc) {
    super(msg, loc, null);
    _processor = p;
  }
  
  protected StreamReadException(String msg, JsonLocation loc, Throwable rootCause) {
    super(msg);
    if (rootCause != null) {
      initCause(rootCause);
    }
    _location = loc;
  }
  




  public abstract StreamReadException withParser(JsonParser paramJsonParser);
  




  public abstract StreamReadException withRequestPayload(RequestPayload paramRequestPayload);
  




  public JsonParser getProcessor()
  {
    return _processor;
  }
  





  public RequestPayload getRequestPayload()
  {
    return _requestPayload;
  }
  





  public String getRequestPayloadAsString()
  {
    return _requestPayload != null ? _requestPayload.toString() : null;
  }
  



  public String getMessage()
  {
    String msg = super.getMessage();
    if (_requestPayload != null) {
      msg = msg + "\nRequest payload : " + _requestPayload.toString();
    }
    return msg;
  }
}
