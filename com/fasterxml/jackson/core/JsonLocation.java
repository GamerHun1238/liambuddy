package com.fasterxml.jackson.core;

import java.io.Serializable;
import java.nio.charset.Charset;


























public class JsonLocation
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final int MAX_CONTENT_SNIPPET = 500;
  public static final JsonLocation NA = new JsonLocation(null, -1L, -1L, -1, -1);
  


  protected final long _totalBytes;
  

  protected final long _totalChars;
  

  protected final int _lineNr;
  

  protected final int _columnNr;
  

  final transient Object _sourceRef;
  


  public JsonLocation(Object srcRef, long totalChars, int lineNr, int colNr)
  {
    this(srcRef, -1L, totalChars, lineNr, colNr);
  }
  

  public JsonLocation(Object sourceRef, long totalBytes, long totalChars, int lineNr, int columnNr)
  {
    _sourceRef = sourceRef;
    _totalBytes = totalBytes;
    _totalChars = totalChars;
    _lineNr = lineNr;
    _columnNr = columnNr;
  }
  






  public Object getSourceRef()
  {
    return _sourceRef;
  }
  
  public int getLineNr()
  {
    return _lineNr;
  }
  
  public int getColumnNr()
  {
    return _columnNr;
  }
  

  public long getCharOffset()
  {
    return _totalChars;
  }
  



  public long getByteOffset()
  {
    return _totalBytes;
  }
  








  public String sourceDescription()
  {
    return _appendSourceDesc(new StringBuilder(100)).toString();
  }
  







  public int hashCode()
  {
    int hash = _sourceRef == null ? 1 : _sourceRef.hashCode();
    hash ^= _lineNr;
    hash += _columnNr;
    hash ^= (int)_totalChars;
    hash += (int)_totalBytes;
    return hash;
  }
  

  public boolean equals(Object other)
  {
    if (other == this) return true;
    if (other == null) return false;
    if (!(other instanceof JsonLocation)) return false;
    JsonLocation otherLoc = (JsonLocation)other;
    
    if (_sourceRef == null) {
      if (_sourceRef != null) return false;
    } else if (!_sourceRef.equals(_sourceRef)) { return false;
    }
    if ((_lineNr == _lineNr) && (_columnNr == _columnNr) && (_totalChars == _totalChars)) {} return 
    

      getByteOffset() == otherLoc.getByteOffset();
  }
  


  public String toString()
  {
    StringBuilder sb = new StringBuilder(80);
    sb.append("[Source: ");
    _appendSourceDesc(sb);
    sb.append("; line: ");
    sb.append(_lineNr);
    sb.append(", column: ");
    sb.append(_columnNr);
    sb.append(']');
    return sb.toString();
  }
  
  protected StringBuilder _appendSourceDesc(StringBuilder sb)
  {
    Object srcRef = _sourceRef;
    
    if (srcRef == null) {
      sb.append("UNKNOWN");
      return sb;
    }
    

    Class<?> srcType = (srcRef instanceof Class) ? (Class)srcRef : srcRef.getClass();
    String tn = srcType.getName();
    
    if (tn.startsWith("java.")) {
      tn = srcType.getSimpleName();
    } else if ((srcRef instanceof byte[])) {
      tn = "byte[]";
    } else if ((srcRef instanceof char[])) {
      tn = "char[]";
    }
    sb.append('(').append(tn).append(')');
    

    String charStr = " chars";
    int len;
    if ((srcRef instanceof CharSequence)) {
      CharSequence cs = (CharSequence)srcRef;
      int len = cs.length();
      len -= _append(sb, cs.subSequence(0, Math.min(len, 500)).toString());
    } else if ((srcRef instanceof char[])) {
      char[] ch = (char[])srcRef;
      int len = ch.length;
      len -= _append(sb, new String(ch, 0, Math.min(len, 500)));
    } else if ((srcRef instanceof byte[])) {
      byte[] b = (byte[])srcRef;
      int maxLen = Math.min(b.length, 500);
      _append(sb, new String(b, 0, maxLen, Charset.forName("UTF-8")));
      int len = b.length - maxLen;
      charStr = " bytes";
    } else {
      len = 0;
    }
    if (len > 0) {
      sb.append("[truncated ").append(len).append(charStr).append(']');
    }
    return sb;
  }
  
  private int _append(StringBuilder sb, String content) {
    sb.append('"').append(content).append('"');
    return content.length();
  }
}
