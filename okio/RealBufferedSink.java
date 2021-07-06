package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;














final class RealBufferedSink
  implements BufferedSink
{
  public final Buffer buffer = new Buffer();
  public final Sink sink;
  boolean closed;
  
  RealBufferedSink(Sink sink) {
    if (sink == null) throw new NullPointerException("sink == null");
    this.sink = sink;
  }
  
  public Buffer buffer() {
    return buffer;
  }
  
  public void write(Buffer source, long byteCount) throws IOException
  {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(source, byteCount);
    emitCompleteSegments();
  }
  
  public BufferedSink write(ByteString byteString) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(byteString);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeUtf8(String string) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeUtf8(string);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeUtf8(String string, int beginIndex, int endIndex) throws IOException
  {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeUtf8(string, beginIndex, endIndex);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeUtf8CodePoint(int codePoint) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeUtf8CodePoint(codePoint);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeString(String string, Charset charset) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeString(string, charset);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeString(String string, int beginIndex, int endIndex, Charset charset) throws IOException
  {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeString(string, beginIndex, endIndex, charset);
    return emitCompleteSegments();
  }
  
  public BufferedSink write(byte[] source) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(source);
    return emitCompleteSegments();
  }
  
  public BufferedSink write(byte[] source, int offset, int byteCount) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.write(source, offset, byteCount);
    return emitCompleteSegments();
  }
  
  public int write(ByteBuffer source) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    int result = buffer.write(source);
    emitCompleteSegments();
    return result;
  }
  
  public long writeAll(Source source) throws IOException {
    if (source == null) throw new IllegalArgumentException("source == null");
    long totalBytesRead = 0L;
    long readCount; while ((readCount = source.read(buffer, 8192L)) != -1L) {
      totalBytesRead += readCount;
      emitCompleteSegments();
    }
    return totalBytesRead;
  }
  
  public BufferedSink write(Source source, long byteCount) throws IOException {
    while (byteCount > 0L) {
      long read = source.read(buffer, byteCount);
      if (read == -1L) throw new EOFException();
      byteCount -= read;
      emitCompleteSegments();
    }
    return this;
  }
  
  public BufferedSink writeByte(int b) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeByte(b);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeShort(int s) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeShort(s);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeShortLe(int s) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeShortLe(s);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeInt(int i) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeInt(i);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeIntLe(int i) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeIntLe(i);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeLong(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeLong(v);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeLongLe(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeLongLe(v);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeDecimalLong(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeDecimalLong(v);
    return emitCompleteSegments();
  }
  
  public BufferedSink writeHexadecimalUnsignedLong(long v) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    buffer.writeHexadecimalUnsignedLong(v);
    return emitCompleteSegments();
  }
  
  public BufferedSink emitCompleteSegments() throws IOException {
    if (closed) throw new IllegalStateException("closed");
    long byteCount = buffer.completeSegmentByteCount();
    if (byteCount > 0L) sink.write(buffer, byteCount);
    return this;
  }
  
  public BufferedSink emit() throws IOException {
    if (closed) throw new IllegalStateException("closed");
    long byteCount = buffer.size();
    if (byteCount > 0L) sink.write(buffer, byteCount);
    return this;
  }
  
  public OutputStream outputStream() {
    new OutputStream() {
      public void write(int b) throws IOException {
        if (closed) throw new IOException("closed");
        buffer.writeByte((byte)b);
        emitCompleteSegments();
      }
      
      public void write(byte[] data, int offset, int byteCount) throws IOException {
        if (closed) throw new IOException("closed");
        buffer.write(data, offset, byteCount);
        emitCompleteSegments();
      }
      
      public void flush() throws IOException
      {
        if (!closed) {
          RealBufferedSink.this.flush();
        }
      }
      
      public void close() throws IOException {
        RealBufferedSink.this.close();
      }
      
      public String toString() {
        return RealBufferedSink.this + ".outputStream()";
      }
    };
  }
  
  public void flush() throws IOException {
    if (closed) throw new IllegalStateException("closed");
    if (buffer.size > 0L) {
      sink.write(buffer, buffer.size);
    }
    sink.flush();
  }
  
  public boolean isOpen() {
    return !closed;
  }
  
  public void close() throws IOException {
    if (closed) { return;
    }
    

    Throwable thrown = null;
    try {
      if (buffer.size > 0L) {
        sink.write(buffer, buffer.size);
      }
    } catch (Throwable e) {
      thrown = e;
    }
    try
    {
      sink.close();
    } catch (Throwable e) {
      if (thrown == null) thrown = e;
    }
    closed = true;
    
    if (thrown != null) Util.sneakyRethrow(thrown);
  }
  
  public Timeout timeout() {
    return sink.timeout();
  }
  
  public String toString() {
    return "buffer(" + sink + ")";
  }
}
