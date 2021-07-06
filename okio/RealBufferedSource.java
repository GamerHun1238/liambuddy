package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.annotation.Nullable;
















final class RealBufferedSource
  implements BufferedSource
{
  public final Buffer buffer = new Buffer();
  public final Source source;
  boolean closed;
  
  RealBufferedSource(Source source) {
    if (source == null) throw new NullPointerException("source == null");
    this.source = source;
  }
  
  public Buffer buffer() {
    return buffer;
  }
  
  public Buffer getBuffer() {
    return buffer;
  }
  
  public long read(Buffer sink, long byteCount) throws IOException {
    if (sink == null) throw new IllegalArgumentException("sink == null");
    if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (closed) { throw new IllegalStateException("closed");
    }
    if (buffer.size == 0L) {
      long read = source.read(buffer, 8192L);
      if (read == -1L) { return -1L;
      }
    }
    long toRead = Math.min(byteCount, buffer.size);
    return buffer.read(sink, toRead);
  }
  
  public boolean exhausted() throws IOException {
    if (closed) throw new IllegalStateException("closed");
    return (buffer.exhausted()) && (source.read(buffer, 8192L) == -1L);
  }
  
  public void require(long byteCount) throws IOException {
    if (!request(byteCount)) throw new EOFException();
  }
  
  public boolean request(long byteCount) throws IOException {
    if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (closed) throw new IllegalStateException("closed");
    while (buffer.size < byteCount) {
      if (source.read(buffer, 8192L) == -1L) return false;
    }
    return true;
  }
  
  public byte readByte() throws IOException {
    require(1L);
    return buffer.readByte();
  }
  
  public ByteString readByteString() throws IOException {
    buffer.writeAll(source);
    return buffer.readByteString();
  }
  
  public ByteString readByteString(long byteCount) throws IOException {
    require(byteCount);
    return buffer.readByteString(byteCount);
  }
  
  public int select(Options options) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    for (;;)
    {
      int index = buffer.selectPrefix(options, true);
      if (index == -1) return -1;
      if (index == -2)
      {
        if (source.read(buffer, 8192L) == -1L) return -1;
      }
      else {
        int selectedSize = byteStrings[index].size();
        buffer.skip(selectedSize);
        return index;
      }
    }
  }
  
  public byte[] readByteArray() throws IOException {
    buffer.writeAll(source);
    return buffer.readByteArray();
  }
  
  public byte[] readByteArray(long byteCount) throws IOException {
    require(byteCount);
    return buffer.readByteArray(byteCount);
  }
  
  public int read(byte[] sink) throws IOException {
    return read(sink, 0, sink.length);
  }
  
  public void readFully(byte[] sink) throws IOException {
    try {
      require(sink.length);
    }
    catch (EOFException e) {
      int offset = 0;
      while (buffer.size > 0L) {
        int read = buffer.read(sink, offset, (int)buffer.size);
        if (read == -1) throw new AssertionError();
        offset += read;
      }
      throw e;
    }
    buffer.readFully(sink);
  }
  
  public int read(byte[] sink, int offset, int byteCount) throws IOException {
    Util.checkOffsetAndCount(sink.length, offset, byteCount);
    
    if (buffer.size == 0L) {
      long read = source.read(buffer, 8192L);
      if (read == -1L) { return -1;
      }
    }
    int toRead = (int)Math.min(byteCount, buffer.size);
    return buffer.read(sink, offset, toRead);
  }
  
  public int read(ByteBuffer sink) throws IOException {
    if (buffer.size == 0L) {
      long read = source.read(buffer, 8192L);
      if (read == -1L) { return -1;
      }
    }
    return buffer.read(sink);
  }
  
  public void readFully(Buffer sink, long byteCount) throws IOException {
    try {
      require(byteCount);
    }
    catch (EOFException e) {
      sink.writeAll(buffer);
      throw e;
    }
    buffer.readFully(sink, byteCount);
  }
  
  public long readAll(Sink sink) throws IOException {
    if (sink == null) { throw new IllegalArgumentException("sink == null");
    }
    long totalBytesWritten = 0L;
    while (source.read(buffer, 8192L) != -1L) {
      long emitByteCount = buffer.completeSegmentByteCount();
      if (emitByteCount > 0L) {
        totalBytesWritten += emitByteCount;
        sink.write(buffer, emitByteCount);
      }
    }
    if (buffer.size() > 0L) {
      totalBytesWritten += buffer.size();
      sink.write(buffer, buffer.size());
    }
    return totalBytesWritten;
  }
  
  public String readUtf8() throws IOException {
    buffer.writeAll(source);
    return buffer.readUtf8();
  }
  
  public String readUtf8(long byteCount) throws IOException {
    require(byteCount);
    return buffer.readUtf8(byteCount);
  }
  
  public String readString(Charset charset) throws IOException {
    if (charset == null) { throw new IllegalArgumentException("charset == null");
    }
    buffer.writeAll(source);
    return buffer.readString(charset);
  }
  
  public String readString(long byteCount, Charset charset) throws IOException {
    require(byteCount);
    if (charset == null) throw new IllegalArgumentException("charset == null");
    return buffer.readString(byteCount, charset);
  }
  
  @Nullable
  public String readUtf8Line() throws IOException { long newline = indexOf((byte)10);
    
    if (newline == -1L) {
      return buffer.size != 0L ? readUtf8(buffer.size) : null;
    }
    
    return buffer.readUtf8Line(newline);
  }
  
  public String readUtf8LineStrict() throws IOException {
    return readUtf8LineStrict(Long.MAX_VALUE);
  }
  
  public String readUtf8LineStrict(long limit) throws IOException {
    if (limit < 0L) throw new IllegalArgumentException("limit < 0: " + limit);
    long scanLength = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1L;
    long newline = indexOf((byte)10, 0L, scanLength);
    if (newline != -1L) return buffer.readUtf8Line(newline);
    if ((scanLength < Long.MAX_VALUE) && 
      (request(scanLength)) && (buffer.getByte(scanLength - 1L) == 13) && 
      (request(scanLength + 1L)) && (buffer.getByte(scanLength) == 10)) {
      return buffer.readUtf8Line(scanLength);
    }
    Buffer data = new Buffer();
    buffer.copyTo(data, 0L, Math.min(32L, buffer.size()));
    
    throw new EOFException("\\n not found: limit=" + Math.min(buffer.size(), limit) + " content=" + data.readByteString().hex() + '…');
  }
  
  public int readUtf8CodePoint() throws IOException {
    require(1L);
    
    byte b0 = buffer.getByte(0L);
    if ((b0 & 0xE0) == 192) {
      require(2L);
    } else if ((b0 & 0xF0) == 224) {
      require(3L);
    } else if ((b0 & 0xF8) == 240) {
      require(4L);
    }
    
    return buffer.readUtf8CodePoint();
  }
  
  public short readShort() throws IOException {
    require(2L);
    return buffer.readShort();
  }
  
  public short readShortLe() throws IOException {
    require(2L);
    return buffer.readShortLe();
  }
  
  public int readInt() throws IOException {
    require(4L);
    return buffer.readInt();
  }
  
  public int readIntLe() throws IOException {
    require(4L);
    return buffer.readIntLe();
  }
  
  public long readLong() throws IOException {
    require(8L);
    return buffer.readLong();
  }
  
  public long readLongLe() throws IOException {
    require(8L);
    return buffer.readLongLe();
  }
  
  public long readDecimalLong() throws IOException {
    require(1L);
    
    for (int pos = 0; request(pos + 1); pos++) {
      byte b = buffer.getByte(pos);
      if (((b < 48) || (b > 57)) && ((pos != 0) || (b != 45)))
      {
        if (pos != 0) break;
        throw new NumberFormatException(String.format("Expected leading [0-9] or '-' character but was %#x", new Object[] {
          Byte.valueOf(b) }));
      }
    }
    


    return buffer.readDecimalLong();
  }
  
  public long readHexadecimalUnsignedLong() throws IOException {
    require(1L);
    
    for (int pos = 0; request(pos + 1); pos++) {
      byte b = buffer.getByte(pos);
      if (((b < 48) || (b > 57)) && ((b < 97) || (b > 102)) && ((b < 65) || (b > 70)))
      {
        if (pos != 0) break;
        throw new NumberFormatException(String.format("Expected leading [0-9a-fA-F] character but was %#x", new Object[] {
          Byte.valueOf(b) }));
      }
    }
    


    return buffer.readHexadecimalUnsignedLong();
  }
  
  public void skip(long byteCount) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    while (byteCount > 0L) {
      if ((buffer.size == 0L) && (source.read(buffer, 8192L) == -1L)) {
        throw new EOFException();
      }
      long toSkip = Math.min(byteCount, buffer.size());
      buffer.skip(toSkip);
      byteCount -= toSkip;
    }
  }
  
  public long indexOf(byte b) throws IOException {
    return indexOf(b, 0L, Long.MAX_VALUE);
  }
  
  public long indexOf(byte b, long fromIndex) throws IOException {
    return indexOf(b, fromIndex, Long.MAX_VALUE);
  }
  
  public long indexOf(byte b, long fromIndex, long toIndex) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    if ((fromIndex < 0L) || (toIndex < fromIndex))
    {
      throw new IllegalArgumentException(String.format("fromIndex=%s toIndex=%s", new Object[] {Long.valueOf(fromIndex), Long.valueOf(toIndex) }));
    }
    
    while (fromIndex < toIndex) {
      long result = buffer.indexOf(b, fromIndex, toIndex);
      if (result != -1L) { return result;
      }
      

      long lastBufferSize = buffer.size;
      if ((lastBufferSize >= toIndex) || (source.read(buffer, 8192L) == -1L)) { return -1L;
      }
      
      fromIndex = Math.max(fromIndex, lastBufferSize);
    }
    return -1L;
  }
  
  public long indexOf(ByteString bytes) throws IOException {
    return indexOf(bytes, 0L);
  }
  
  public long indexOf(ByteString bytes, long fromIndex) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    for (;;)
    {
      long result = buffer.indexOf(bytes, fromIndex);
      if (result != -1L) { return result;
      }
      long lastBufferSize = buffer.size;
      if (source.read(buffer, 8192L) == -1L) { return -1L;
      }
      
      fromIndex = Math.max(fromIndex, lastBufferSize - bytes.size() + 1L);
    }
  }
  
  public long indexOfElement(ByteString targetBytes) throws IOException {
    return indexOfElement(targetBytes, 0L);
  }
  
  public long indexOfElement(ByteString targetBytes, long fromIndex) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    for (;;)
    {
      long result = buffer.indexOfElement(targetBytes, fromIndex);
      if (result != -1L) { return result;
      }
      long lastBufferSize = buffer.size;
      if (source.read(buffer, 8192L) == -1L) { return -1L;
      }
      
      fromIndex = Math.max(fromIndex, lastBufferSize);
    }
  }
  
  public boolean rangeEquals(long offset, ByteString bytes) throws IOException {
    return rangeEquals(offset, bytes, 0, bytes.size());
  }
  
  public boolean rangeEquals(long offset, ByteString bytes, int bytesOffset, int byteCount)
    throws IOException
  {
    if (closed) { throw new IllegalStateException("closed");
    }
    if ((offset < 0L) || (bytesOffset < 0) || (byteCount < 0) || 
    

      (bytes.size() - bytesOffset < byteCount)) {
      return false;
    }
    for (int i = 0; i < byteCount; i++) {
      long bufferOffset = offset + i;
      if (!request(bufferOffset + 1L)) return false;
      if (buffer.getByte(bufferOffset) != bytes.getByte(bytesOffset + i)) return false;
    }
    return true;
  }
  
  public BufferedSource peek() {
    return Okio.buffer(new PeekSource(this));
  }
  
  public InputStream inputStream() {
    new InputStream() {
      public int read() throws IOException {
        if (closed) throw new IOException("closed");
        if (buffer.size == 0L) {
          long count = source.read(buffer, 8192L);
          if (count == -1L) return -1;
        }
        return buffer.readByte() & 0xFF;
      }
      
      public int read(byte[] data, int offset, int byteCount) throws IOException {
        if (closed) throw new IOException("closed");
        Util.checkOffsetAndCount(data.length, offset, byteCount);
        
        if (buffer.size == 0L) {
          long count = source.read(buffer, 8192L);
          if (count == -1L) { return -1;
          }
        }
        return buffer.read(data, offset, byteCount);
      }
      
      public int available() throws IOException {
        if (closed) throw new IOException("closed");
        return (int)Math.min(buffer.size, 2147483647L);
      }
      
      public void close() throws IOException {
        RealBufferedSource.this.close();
      }
      
      public String toString() {
        return RealBufferedSource.this + ".inputStream()";
      }
    };
  }
  
  public boolean isOpen() {
    return !closed;
  }
  
  public void close() throws IOException {
    if (closed) return;
    closed = true;
    source.close();
    buffer.clear();
  }
  
  public Timeout timeout() {
    return source.timeout();
  }
  
  public String toString() {
    return "buffer(" + source + ")";
  }
}
