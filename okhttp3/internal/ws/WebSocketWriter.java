package okhttp3.internal.ws;

import java.io.IOException;
import java.util.Random;
import okio.Buffer;
import okio.Buffer.UnsafeCursor;
import okio.BufferedSink;
import okio.ByteString;
import okio.Sink;
import okio.Timeout;



































final class WebSocketWriter
{
  final boolean isClient;
  final Random random;
  final BufferedSink sink;
  final Buffer sinkBuffer;
  boolean writerClosed;
  final Buffer buffer = new Buffer();
  final FrameSink frameSink = new FrameSink();
  
  boolean activeWriter;
  private final byte[] maskKey;
  private final Buffer.UnsafeCursor maskCursor;
  
  WebSocketWriter(boolean isClient, BufferedSink sink, Random random)
  {
    if (sink == null) throw new NullPointerException("sink == null");
    if (random == null) throw new NullPointerException("random == null");
    this.isClient = isClient;
    this.sink = sink;
    sinkBuffer = sink.buffer();
    this.random = random;
    

    maskKey = (isClient ? new byte[4] : null);
    maskCursor = (isClient ? new Buffer.UnsafeCursor() : null);
  }
  
  void writePing(ByteString payload) throws IOException
  {
    writeControlFrame(9, payload);
  }
  
  void writePong(ByteString payload) throws IOException
  {
    writeControlFrame(10, payload);
  }
  





  void writeClose(int code, ByteString reason)
    throws IOException
  {
    ByteString payload = ByteString.EMPTY;
    if ((code != 0) || (reason != null)) {
      if (code != 0) {
        WebSocketProtocol.validateCloseCode(code);
      }
      Buffer buffer = new Buffer();
      buffer.writeShort(code);
      if (reason != null) {
        buffer.write(reason);
      }
      payload = buffer.readByteString();
    }
    try
    {
      writeControlFrame(8, payload);
    } finally {
      writerClosed = true;
    }
  }
  
  private void writeControlFrame(int opcode, ByteString payload) throws IOException {
    if (writerClosed) { throw new IOException("closed");
    }
    int length = payload.size();
    if (length > 125L) {
      throw new IllegalArgumentException("Payload size must be less than or equal to 125");
    }
    

    int b0 = 0x80 | opcode;
    sinkBuffer.writeByte(b0);
    
    int b1 = length;
    if (isClient) {
      b1 |= 0x80;
      sinkBuffer.writeByte(b1);
      
      random.nextBytes(maskKey);
      sinkBuffer.write(maskKey);
      
      if (length > 0) {
        long payloadStart = sinkBuffer.size();
        sinkBuffer.write(payload);
        
        sinkBuffer.readAndWriteUnsafe(maskCursor);
        maskCursor.seek(payloadStart);
        WebSocketProtocol.toggleMask(maskCursor, maskKey);
        maskCursor.close();
      }
    } else {
      sinkBuffer.writeByte(b1);
      sinkBuffer.write(payload);
    }
    
    sink.flush();
  }
  



  Sink newMessageSink(int formatOpcode, long contentLength)
  {
    if (activeWriter) {
      throw new IllegalStateException("Another message writer is active. Did you call close()?");
    }
    activeWriter = true;
    

    frameSink.formatOpcode = formatOpcode;
    frameSink.contentLength = contentLength;
    frameSink.isFirstFrame = true;
    frameSink.closed = false;
    
    return frameSink;
  }
  
  void writeMessageFrame(int formatOpcode, long byteCount, boolean isFirstFrame, boolean isFinal) throws IOException
  {
    if (writerClosed) { throw new IOException("closed");
    }
    int b0 = isFirstFrame ? formatOpcode : 0;
    if (isFinal) {
      b0 |= 0x80;
    }
    sinkBuffer.writeByte(b0);
    
    int b1 = 0;
    if (isClient) {
      b1 |= 0x80;
    }
    if (byteCount <= 125L) {
      b1 |= (int)byteCount;
      sinkBuffer.writeByte(b1);
    } else if (byteCount <= 65535L) {
      b1 |= 0x7E;
      sinkBuffer.writeByte(b1);
      sinkBuffer.writeShort((int)byteCount);
    } else {
      b1 |= 0x7F;
      sinkBuffer.writeByte(b1);
      sinkBuffer.writeLong(byteCount);
    }
    
    if (isClient) {
      random.nextBytes(maskKey);
      sinkBuffer.write(maskKey);
      
      if (byteCount > 0L) {
        long bufferStart = sinkBuffer.size();
        sinkBuffer.write(buffer, byteCount);
        
        sinkBuffer.readAndWriteUnsafe(maskCursor);
        maskCursor.seek(bufferStart);
        WebSocketProtocol.toggleMask(maskCursor, maskKey);
        maskCursor.close();
      }
    } else {
      sinkBuffer.write(buffer, byteCount);
    }
    
    sink.emit();
  }
  
  final class FrameSink implements Sink { int formatOpcode;
    long contentLength;
    boolean isFirstFrame;
    boolean closed;
    
    FrameSink() {}
    
    public void write(Buffer source, long byteCount) throws IOException { if (closed) { throw new IOException("closed");
      }
      buffer.write(source, byteCount);
      

      if ((isFirstFrame) && (contentLength != -1L)) {}
      
      boolean deferWrite = buffer.size() > contentLength - 8192L;
      
      long emitCount = buffer.completeSegmentByteCount();
      if ((emitCount > 0L) && (!deferWrite)) {
        writeMessageFrame(formatOpcode, emitCount, isFirstFrame, false);
        isFirstFrame = false;
      }
    }
    
    public void flush() throws IOException {
      if (closed) { throw new IOException("closed");
      }
      writeMessageFrame(formatOpcode, buffer.size(), isFirstFrame, false);
      isFirstFrame = false;
    }
    
    public Timeout timeout() {
      return sink.timeout();
    }
    
    public void close() throws IOException {
      if (closed) { throw new IOException("closed");
      }
      writeMessageFrame(formatOpcode, buffer.size(), isFirstFrame, true);
      closed = true;
      activeWriter = false;
    }
  }
}
