package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
































final class Http2Writer
  implements Closeable
{
  private static final Logger logger = Logger.getLogger(Http2.class.getName());
  
  private final BufferedSink sink;
  private final boolean client;
  private final Buffer hpackBuffer;
  private int maxFrameSize;
  private boolean closed;
  final Hpack.Writer hpackWriter;
  
  Http2Writer(BufferedSink sink, boolean client)
  {
    this.sink = sink;
    this.client = client;
    hpackBuffer = new Buffer();
    hpackWriter = new Hpack.Writer(hpackBuffer);
    maxFrameSize = 16384;
  }
  
  public synchronized void connectionPreface() throws IOException {
    if (closed) throw new IOException("closed");
    if (!client) return;
    if (logger.isLoggable(Level.FINE)) {
      logger.fine(Util.format(">> CONNECTION %s", new Object[] { Http2.CONNECTION_PREFACE.hex() }));
    }
    sink.write(Http2.CONNECTION_PREFACE.toByteArray());
    sink.flush();
  }
  
  public synchronized void applyAndAckSettings(Settings peerSettings) throws IOException
  {
    if (closed) throw new IOException("closed");
    maxFrameSize = peerSettings.getMaxFrameSize(maxFrameSize);
    if (peerSettings.getHeaderTableSize() != -1) {
      hpackWriter.setHeaderTableSizeSetting(peerSettings.getHeaderTableSize());
    }
    int length = 0;
    byte type = 4;
    byte flags = 1;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    sink.flush();
  }
  












  public synchronized void pushPromise(int streamId, int promisedStreamId, List<Header> requestHeaders)
    throws IOException
  {
    if (closed) throw new IOException("closed");
    hpackWriter.writeHeaders(requestHeaders);
    
    long byteCount = hpackBuffer.size();
    int length = (int)Math.min(maxFrameSize - 4, byteCount);
    byte type = 5;
    byte flags = byteCount == length ? 4 : 0;
    frameHeader(streamId, length + 4, type, flags);
    sink.writeInt(promisedStreamId & 0x7FFFFFFF);
    sink.write(hpackBuffer, length);
    
    if (byteCount > length) writeContinuationFrames(streamId, byteCount - length);
  }
  
  public synchronized void flush() throws IOException {
    if (closed) throw new IOException("closed");
    sink.flush();
  }
  
  public synchronized void rstStream(int streamId, ErrorCode errorCode) throws IOException
  {
    if (closed) throw new IOException("closed");
    if (httpCode == -1) { throw new IllegalArgumentException();
    }
    int length = 4;
    byte type = 3;
    byte flags = 0;
    frameHeader(streamId, length, type, flags);
    sink.writeInt(httpCode);
    sink.flush();
  }
  
  public int maxDataLength()
  {
    return maxFrameSize;
  }
  







  public synchronized void data(boolean outFinished, int streamId, Buffer source, int byteCount)
    throws IOException
  {
    if (closed) throw new IOException("closed");
    byte flags = 0;
    if (outFinished) flags = (byte)(flags | 0x1);
    dataFrame(streamId, flags, source, byteCount);
  }
  
  void dataFrame(int streamId, byte flags, Buffer buffer, int byteCount) throws IOException {
    byte type = 0;
    frameHeader(streamId, byteCount, type, flags);
    if (byteCount > 0) {
      sink.write(buffer, byteCount);
    }
  }
  
  public synchronized void settings(Settings settings) throws IOException
  {
    if (closed) throw new IOException("closed");
    int length = settings.size() * 6;
    byte type = 4;
    byte flags = 0;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    for (int i = 0; i < 10; i++)
      if (settings.isSet(i)) {
        int id = i;
        if (id == 4) {
          id = 3;
        } else if (id == 7) {
          id = 4;
        }
        sink.writeShort(id);
        sink.writeInt(settings.get(i));
      }
    sink.flush();
  }
  


  public synchronized void ping(boolean ack, int payload1, int payload2)
    throws IOException
  {
    if (closed) throw new IOException("closed");
    int length = 8;
    byte type = 6;
    byte flags = ack ? 1 : 0;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    sink.writeInt(payload1);
    sink.writeInt(payload2);
    sink.flush();
  }
  







  public synchronized void goAway(int lastGoodStreamId, ErrorCode errorCode, byte[] debugData)
    throws IOException
  {
    if (closed) throw new IOException("closed");
    if (httpCode == -1) throw Http2.illegalArgument("errorCode.httpCode == -1", new Object[0]);
    int length = 8 + debugData.length;
    byte type = 7;
    byte flags = 0;
    int streamId = 0;
    frameHeader(streamId, length, type, flags);
    sink.writeInt(lastGoodStreamId);
    sink.writeInt(httpCode);
    if (debugData.length > 0) {
      sink.write(debugData);
    }
    sink.flush();
  }
  


  public synchronized void windowUpdate(int streamId, long windowSizeIncrement)
    throws IOException
  {
    if (closed) throw new IOException("closed");
    if ((windowSizeIncrement == 0L) || (windowSizeIncrement > 2147483647L)) {
      throw Http2.illegalArgument("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: %s", new Object[] {
        Long.valueOf(windowSizeIncrement) });
    }
    int length = 4;
    byte type = 8;
    byte flags = 0;
    frameHeader(streamId, length, type, flags);
    sink.writeInt((int)windowSizeIncrement);
    sink.flush();
  }
  
  public void frameHeader(int streamId, int length, byte type, byte flags) throws IOException {
    if (logger.isLoggable(Level.FINE)) logger.fine(Http2.frameLog(false, streamId, length, type, flags));
    if (length > maxFrameSize) {
      throw Http2.illegalArgument("FRAME_SIZE_ERROR length > %d: %d", new Object[] { Integer.valueOf(maxFrameSize), Integer.valueOf(length) });
    }
    if ((streamId & 0x80000000) != 0) throw Http2.illegalArgument("reserved bit set: %s", new Object[] { Integer.valueOf(streamId) });
    writeMedium(sink, length);
    sink.writeByte(type & 0xFF);
    sink.writeByte(flags & 0xFF);
    sink.writeInt(streamId & 0x7FFFFFFF);
  }
  
  public synchronized void close() throws IOException {
    closed = true;
    sink.close();
  }
  
  private static void writeMedium(BufferedSink sink, int i) throws IOException {
    sink.writeByte(i >>> 16 & 0xFF);
    sink.writeByte(i >>> 8 & 0xFF);
    sink.writeByte(i & 0xFF);
  }
  
  private void writeContinuationFrames(int streamId, long byteCount) throws IOException {
    while (byteCount > 0L) {
      int length = (int)Math.min(maxFrameSize, byteCount);
      byteCount -= length;
      frameHeader(streamId, length, (byte)9, (byte)(byteCount == 0L ? 4 : 0));
      sink.write(hpackBuffer, length);
    }
  }
  
  public synchronized void headers(boolean outFinished, int streamId, List<Header> headerBlock) throws IOException
  {
    if (closed) throw new IOException("closed");
    hpackWriter.writeHeaders(headerBlock);
    
    long byteCount = hpackBuffer.size();
    int length = (int)Math.min(maxFrameSize, byteCount);
    byte type = 1;
    byte flags = byteCount == length ? 4 : 0;
    if (outFinished) flags = (byte)(flags | 0x1);
    frameHeader(streamId, length, type, flags);
    sink.write(hpackBuffer, length);
    
    if (byteCount > length) writeContinuationFrames(streamId, byteCount - length);
  }
}
