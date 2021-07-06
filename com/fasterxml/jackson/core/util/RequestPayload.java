package com.fasterxml.jackson.core.util;

import java.io.IOException;
import java.io.Serializable;













public class RequestPayload
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected byte[] _payloadAsBytes;
  protected CharSequence _payloadAsText;
  protected String _charset;
  
  public RequestPayload(byte[] bytes, String charset)
  {
    if (bytes == null) {
      throw new IllegalArgumentException();
    }
    _payloadAsBytes = bytes;
    _charset = ((charset == null) || (charset.isEmpty()) ? "UTF-8" : charset);
  }
  
  public RequestPayload(CharSequence str) {
    if (str == null) {
      throw new IllegalArgumentException();
    }
    _payloadAsText = str;
  }
  





  public Object getRawPayload()
  {
    if (_payloadAsBytes != null) {
      return _payloadAsBytes;
    }
    
    return _payloadAsText;
  }
  
  public String toString()
  {
    if (_payloadAsBytes != null) {
      try {
        return new String(_payloadAsBytes, _charset);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return _payloadAsText.toString();
  }
}
