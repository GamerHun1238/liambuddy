package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.TextBuffer;


















































































public class IOContext
{
  protected final Object _sourceRef;
  protected JsonEncoding _encoding;
  protected final boolean _managedResource;
  protected final BufferRecycler _bufferRecycler;
  protected byte[] _readIOBuffer;
  protected byte[] _writeEncodingBuffer;
  protected byte[] _base64Buffer;
  protected char[] _tokenCBuffer;
  protected char[] _concatCBuffer;
  protected char[] _nameCopyBuffer;
  
  public IOContext(BufferRecycler br, Object sourceRef, boolean managedResource)
  {
    _bufferRecycler = br;
    _sourceRef = sourceRef;
    _managedResource = managedResource;
  }
  
  public void setEncoding(JsonEncoding enc) {
    _encoding = enc;
  }
  


  public IOContext withEncoding(JsonEncoding enc)
  {
    _encoding = enc;
    return this;
  }
  






  public Object getSourceReference() { return _sourceRef; }
  public JsonEncoding getEncoding() { return _encoding; }
  public boolean isResourceManaged() { return _managedResource; }
  





  public TextBuffer constructTextBuffer()
  {
    return new TextBuffer(_bufferRecycler);
  }
  




  public byte[] allocReadIOBuffer()
  {
    _verifyAlloc(_readIOBuffer);
    return this._readIOBuffer = _bufferRecycler.allocByteBuffer(0);
  }
  


  public byte[] allocReadIOBuffer(int minSize)
  {
    _verifyAlloc(_readIOBuffer);
    return this._readIOBuffer = _bufferRecycler.allocByteBuffer(0, minSize);
  }
  
  public byte[] allocWriteEncodingBuffer() {
    _verifyAlloc(_writeEncodingBuffer);
    return this._writeEncodingBuffer = _bufferRecycler.allocByteBuffer(1);
  }
  


  public byte[] allocWriteEncodingBuffer(int minSize)
  {
    _verifyAlloc(_writeEncodingBuffer);
    return this._writeEncodingBuffer = _bufferRecycler.allocByteBuffer(1, minSize);
  }
  


  public byte[] allocBase64Buffer()
  {
    _verifyAlloc(_base64Buffer);
    return this._base64Buffer = _bufferRecycler.allocByteBuffer(3);
  }
  


  public byte[] allocBase64Buffer(int minSize)
  {
    _verifyAlloc(_base64Buffer);
    return this._base64Buffer = _bufferRecycler.allocByteBuffer(3, minSize);
  }
  
  public char[] allocTokenBuffer() {
    _verifyAlloc(_tokenCBuffer);
    return this._tokenCBuffer = _bufferRecycler.allocCharBuffer(0);
  }
  


  public char[] allocTokenBuffer(int minSize)
  {
    _verifyAlloc(_tokenCBuffer);
    return this._tokenCBuffer = _bufferRecycler.allocCharBuffer(0, minSize);
  }
  
  public char[] allocConcatBuffer() {
    _verifyAlloc(_concatCBuffer);
    return this._concatCBuffer = _bufferRecycler.allocCharBuffer(1);
  }
  
  public char[] allocNameCopyBuffer(int minSize) {
    _verifyAlloc(_nameCopyBuffer);
    return this._nameCopyBuffer = _bufferRecycler.allocCharBuffer(3, minSize);
  }
  



  public void releaseReadIOBuffer(byte[] buf)
  {
    if (buf != null)
    {


      _verifyRelease(buf, _readIOBuffer);
      _readIOBuffer = null;
      _bufferRecycler.releaseByteBuffer(0, buf);
    }
  }
  
  public void releaseWriteEncodingBuffer(byte[] buf) {
    if (buf != null)
    {


      _verifyRelease(buf, _writeEncodingBuffer);
      _writeEncodingBuffer = null;
      _bufferRecycler.releaseByteBuffer(1, buf);
    }
  }
  
  public void releaseBase64Buffer(byte[] buf) {
    if (buf != null) {
      _verifyRelease(buf, _base64Buffer);
      _base64Buffer = null;
      _bufferRecycler.releaseByteBuffer(3, buf);
    }
  }
  
  public void releaseTokenBuffer(char[] buf) {
    if (buf != null) {
      _verifyRelease(buf, _tokenCBuffer);
      _tokenCBuffer = null;
      _bufferRecycler.releaseCharBuffer(0, buf);
    }
  }
  
  public void releaseConcatBuffer(char[] buf) {
    if (buf != null)
    {
      _verifyRelease(buf, _concatCBuffer);
      _concatCBuffer = null;
      _bufferRecycler.releaseCharBuffer(1, buf);
    }
  }
  
  public void releaseNameCopyBuffer(char[] buf) {
    if (buf != null)
    {
      _verifyRelease(buf, _nameCopyBuffer);
      _nameCopyBuffer = null;
      _bufferRecycler.releaseCharBuffer(3, buf);
    }
  }
  





  protected final void _verifyAlloc(Object buffer)
  {
    if (buffer != null) throw new IllegalStateException("Trying to call same allocXxx() method second time");
  }
  
  protected final void _verifyRelease(byte[] toRelease, byte[] src)
  {
    if ((toRelease != src) && (toRelease.length < src.length)) throw wrongBuf();
  }
  
  protected final void _verifyRelease(char[] toRelease, char[] src)
  {
    if ((toRelease != src) && (toRelease.length < src.length)) throw wrongBuf();
  }
  
  private IllegalArgumentException wrongBuf()
  {
    return new IllegalArgumentException("Trying to release buffer smaller than original");
  }
}
