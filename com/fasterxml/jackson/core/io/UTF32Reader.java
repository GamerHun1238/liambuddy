package com.fasterxml.jackson.core.io;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;



















public class UTF32Reader
  extends Reader
{
  protected static final int LAST_VALID_UNICODE_CHAR = 1114111;
  protected static final char NC = '\000';
  protected final IOContext _context;
  protected InputStream _in;
  protected byte[] _buffer;
  protected int _ptr;
  protected int _length;
  protected final boolean _bigEndian;
  protected char _surrogate = '\000';
  


  protected int _charCount;
  


  protected int _byteCount;
  


  protected final boolean _managedBuffers;
  

  protected char[] _tmpBuf;
  


  public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian)
  {
    _context = ctxt;
    _in = in;
    _buffer = buf;
    _ptr = ptr;
    _length = len;
    _bigEndian = isBigEndian;
    _managedBuffers = (in != null);
  }
  





  public void close()
    throws IOException
  {
    InputStream in = _in;
    
    if (in != null) {
      _in = null;
      freeBuffers();
      in.close();
    }
  }
  






  public int read()
    throws IOException
  {
    if (_tmpBuf == null) {
      _tmpBuf = new char[1];
    }
    if (read(_tmpBuf, 0, 1) < 1) {
      return -1;
    }
    return _tmpBuf[0];
  }
  
  public int read(char[] cbuf, int start, int len)
    throws IOException
  {
    if (_buffer == null) return -1;
    if (len < 1) { return len;
    }
    if ((start < 0) || (start + len > cbuf.length)) {
      reportBounds(cbuf, start, len);
    }
    
    int outPtr = start;
    int outEnd = len + start;
    

    if (_surrogate != 0) {
      cbuf[(outPtr++)] = _surrogate;
      _surrogate = '\000';

    }
    else
    {
      int left = _length - _ptr;
      if ((left < 4) && 
        (!loadMore(left)))
      {
        if (left == 0) {
          return -1;
        }
        reportUnexpectedEOF(_length - _ptr, 4);
      }
    }
    


    int lastValidInputStart = _length - 4;
    

    while (outPtr < outEnd) {
      int ptr = _ptr;
      int lo;
      int lo;
      int hi; if (_bigEndian) {
        int hi = _buffer[ptr] << 8 | _buffer[(ptr + 1)] & 0xFF;
        lo = (_buffer[(ptr + 2)] & 0xFF) << 8 | _buffer[(ptr + 3)] & 0xFF;
      } else {
        lo = _buffer[ptr] & 0xFF | (_buffer[(ptr + 1)] & 0xFF) << 8;
        hi = _buffer[(ptr + 2)] & 0xFF | _buffer[(ptr + 3)] << 8;
      }
      _ptr += 4;
      


      if (hi != 0) {
        hi &= 0xFFFF;
        int ch = hi - 1 << 16 | lo;
        if (hi > 16) {
          reportInvalid(ch, outPtr - start, 
            String.format(" (above 0x%08x)", new Object[] {Integer.valueOf(1114111) }));
        }
        cbuf[(outPtr++)] = ((char)(55296 + (ch >> 10)));
        
        lo = 0xDC00 | ch & 0x3FF;
        
        if (outPtr >= outEnd) {
          _surrogate = ((char)ch);
          break;
        }
      }
      cbuf[(outPtr++)] = ((char)lo);
      if (_ptr > lastValidInputStart) {
        break;
      }
    }
    int actualLen = outPtr - start;
    _charCount += actualLen;
    return actualLen;
  }
  




  private void reportUnexpectedEOF(int gotBytes, int needed)
    throws IOException
  {
    int bytePos = _byteCount + gotBytes;int charPos = _charCount;
    
    throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
  }
  
  private void reportInvalid(int value, int offset, String msg) throws IOException {
    int bytePos = _byteCount + _ptr - 1;int charPos = _charCount + offset;
    
    throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
  }
  




  private boolean loadMore(int available)
    throws IOException
  {
    _byteCount += _length - available;
    

    if (available > 0) {
      if (_ptr > 0) {
        System.arraycopy(_buffer, _ptr, _buffer, 0, available);
        _ptr = 0;
      }
      _length = available;

    }
    else
    {
      _ptr = 0;
      int count = _in == null ? -1 : _in.read(_buffer);
      if (count < 1) {
        _length = 0;
        if (count < 0) {
          if (_managedBuffers) {
            freeBuffers();
          }
          return false;
        }
        
        reportStrangeStream();
      }
      _length = count;
    }
    



    while (_length < 4) {
      int count = _in == null ? -1 : _in.read(_buffer, _length, _buffer.length - _length);
      if (count < 1) {
        if (count < 0) {
          if (_managedBuffers) {
            freeBuffers();
          }
          reportUnexpectedEOF(_length, 4);
        }
        
        reportStrangeStream();
      }
      _length += count;
    }
    return true;
  }
  




  private void freeBuffers()
  {
    byte[] buf = _buffer;
    if (buf != null) {
      _buffer = null;
      _context.releaseReadIOBuffer(buf);
    }
  }
  
  private void reportBounds(char[] cbuf, int start, int len) throws IOException {
    throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
  }
  
  private void reportStrangeStream() throws IOException {
    throw new IOException("Strange I/O stream, returned 0 bytes on read");
  }
}
