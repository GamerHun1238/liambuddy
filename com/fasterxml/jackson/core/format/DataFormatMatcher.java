package com.fasterxml.jackson.core.format;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.MergedStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


























public class DataFormatMatcher
{
  protected final InputStream _originalStream;
  protected final byte[] _bufferedData;
  protected final int _bufferedStart;
  protected final int _bufferedLength;
  protected final JsonFactory _match;
  protected final MatchStrength _matchStrength;
  
  protected DataFormatMatcher(InputStream in, byte[] buffered, int bufferedStart, int bufferedLength, JsonFactory match, MatchStrength strength)
  {
    _originalStream = in;
    _bufferedData = buffered;
    _bufferedStart = bufferedStart;
    _bufferedLength = bufferedLength;
    _match = match;
    _matchStrength = strength;
    

    if (((bufferedStart | bufferedLength) < 0) || (bufferedStart + bufferedLength > buffered.length))
    {
      throw new IllegalArgumentException(String.format("Illegal start/length (%d/%d) wrt input array of %d bytes", new Object[] {
        Integer.valueOf(bufferedStart), Integer.valueOf(bufferedLength), Integer.valueOf(buffered.length) }));
    }
  }
  








  public boolean hasMatch()
  {
    return _match != null;
  }
  


  public MatchStrength getMatchStrength()
  {
    return _matchStrength == null ? MatchStrength.INCONCLUSIVE : _matchStrength;
  }
  

  public JsonFactory getMatch()
  {
    return _match;
  }
  





  public String getMatchedFormatName()
  {
    return _match.getFormatName();
  }
  









  public JsonParser createParserWithMatch()
    throws IOException
  {
    if (_match == null) {
      return null;
    }
    if (_originalStream == null) {
      return _match.createParser(_bufferedData, _bufferedStart, _bufferedLength);
    }
    return _match.createParser(getDataStream());
  }
  






  public InputStream getDataStream()
  {
    if (_originalStream == null) {
      return new ByteArrayInputStream(_bufferedData, _bufferedStart, _bufferedLength);
    }
    return new MergedStream(null, _originalStream, _bufferedData, _bufferedStart, _bufferedLength);
  }
}
