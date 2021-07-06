package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;
















final class Http2Reader
  implements Closeable
{
  static abstract interface Handler
  {
    public abstract void data(boolean paramBoolean, int paramInt1, BufferedSource paramBufferedSource, int paramInt2)
      throws IOException;
    
    public abstract void headers(boolean paramBoolean, int paramInt1, int paramInt2, List<Header> paramList);
    
    public abstract void rstStream(int paramInt, ErrorCode paramErrorCode);
    
    public abstract void settings(boolean paramBoolean, Settings paramSettings);
    
    public abstract void ackSettings();
    
    public abstract void ping(boolean paramBoolean, int paramInt1, int paramInt2);
    
    public abstract void goAway(int paramInt, ErrorCode paramErrorCode, ByteString paramByteString);
    
    public abstract void windowUpdate(int paramInt, long paramLong);
    
    public abstract void priority(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
    
    public abstract void pushPromise(int paramInt1, int paramInt2, List<Header> paramList)
      throws IOException;
    
    public abstract void alternateService(int paramInt1, String paramString1, ByteString paramByteString, String paramString2, int paramInt2, long paramLong);
  }
  
  static final Logger logger = Logger.getLogger(Http2.class.getName());
  
  private final BufferedSource source;
  
  private final ContinuationSource continuation;
  
  private final boolean client;
  final Hpack.Reader hpackReader;
  
  Http2Reader(BufferedSource source, boolean client)
  {
    this.source = source;
    this.client = client;
    continuation = new ContinuationSource(this.source);
    hpackReader = new Hpack.Reader(4096, continuation);
  }
  
  public void readConnectionPreface(Handler handler) throws IOException {
    if (client)
    {
      if (!nextFrame(true, handler)) {
        throw Http2.ioException("Required SETTINGS preface not received", new Object[0]);
      }
    }
    else {
      ByteString connectionPreface = source.readByteString(Http2.CONNECTION_PREFACE.size());
      if (logger.isLoggable(Level.FINE)) logger.fine(Util.format("<< CONNECTION %s", new Object[] { connectionPreface.hex() }));
      if (!Http2.CONNECTION_PREFACE.equals(connectionPreface)) {
        throw Http2.ioException("Expected a connection header but was %s", new Object[] { connectionPreface.utf8() });
      }
    }
  }
  
  public boolean nextFrame(boolean requireSettings, Handler handler) throws IOException {
    try {
      source.require(9L);
    } catch (IOException e) {
      return false;
    }
    











    int length = readMedium(source);
    if ((length < 0) || (length > 16384)) {
      throw Http2.ioException("FRAME_SIZE_ERROR: %s", new Object[] { Integer.valueOf(length) });
    }
    byte type = (byte)(source.readByte() & 0xFF);
    if ((requireSettings) && (type != 4)) {
      throw Http2.ioException("Expected a SETTINGS frame but was %s", new Object[] { Byte.valueOf(type) });
    }
    byte flags = (byte)(source.readByte() & 0xFF);
    int streamId = source.readInt() & 0x7FFFFFFF;
    if (logger.isLoggable(Level.FINE)) { logger.fine(Http2.frameLog(true, streamId, length, type, flags));
    }
    switch (type) {
    case 0: 
      readData(handler, length, flags, streamId);
      break;
    
    case 1: 
      readHeaders(handler, length, flags, streamId);
      break;
    
    case 2: 
      readPriority(handler, length, flags, streamId);
      break;
    
    case 3: 
      readRstStream(handler, length, flags, streamId);
      break;
    
    case 4: 
      readSettings(handler, length, flags, streamId);
      break;
    
    case 5: 
      readPushPromise(handler, length, flags, streamId);
      break;
    
    case 6: 
      readPing(handler, length, flags, streamId);
      break;
    
    case 7: 
      readGoAway(handler, length, flags, streamId);
      break;
    
    case 8: 
      readWindowUpdate(handler, length, flags, streamId);
      break;
    

    default: 
      source.skip(length);
    }
    return true;
  }
  
  private void readHeaders(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (streamId == 0) { throw Http2.ioException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0", new Object[0]);
    }
    boolean endStream = (flags & 0x1) != 0;
    
    short padding = (flags & 0x8) != 0 ? (short)(source.readByte() & 0xFF) : 0;
    
    if ((flags & 0x20) != 0) {
      readPriority(handler, streamId);
      length -= 5;
    }
    
    length = lengthWithoutPadding(length, flags, padding);
    
    List<Header> headerBlock = readHeaderBlock(length, padding, flags, streamId);
    
    handler.headers(endStream, streamId, -1, headerBlock);
  }
  
  private List<Header> readHeaderBlock(int length, short padding, byte flags, int streamId) throws IOException
  {
    continuation.length = (continuation.left = length);
    continuation.padding = padding;
    continuation.flags = flags;
    continuation.streamId = streamId;
    


    hpackReader.readHeaders();
    return hpackReader.getAndResetHeaderList();
  }
  
  private void readData(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (streamId == 0) { throw Http2.ioException("PROTOCOL_ERROR: TYPE_DATA streamId == 0", new Object[0]);
    }
    
    boolean inFinished = (flags & 0x1) != 0;
    boolean gzipped = (flags & 0x20) != 0;
    if (gzipped) {
      throw Http2.ioException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA", new Object[0]);
    }
    
    short padding = (flags & 0x8) != 0 ? (short)(source.readByte() & 0xFF) : 0;
    length = lengthWithoutPadding(length, flags, padding);
    
    handler.data(inFinished, streamId, source, length);
    source.skip(padding);
  }
  
  private void readPriority(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (length != 5) throw Http2.ioException("TYPE_PRIORITY length: %d != 5", new Object[] { Integer.valueOf(length) });
    if (streamId == 0) throw Http2.ioException("TYPE_PRIORITY streamId == 0", new Object[0]);
    readPriority(handler, streamId);
  }
  
  private void readPriority(Handler handler, int streamId) throws IOException {
    int w1 = source.readInt();
    boolean exclusive = (w1 & 0x80000000) != 0;
    int streamDependency = w1 & 0x7FFFFFFF;
    int weight = (source.readByte() & 0xFF) + 1;
    handler.priority(streamId, streamDependency, weight, exclusive);
  }
  
  private void readRstStream(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (length != 4) throw Http2.ioException("TYPE_RST_STREAM length: %d != 4", new Object[] { Integer.valueOf(length) });
    if (streamId == 0) throw Http2.ioException("TYPE_RST_STREAM streamId == 0", new Object[0]);
    int errorCodeInt = source.readInt();
    ErrorCode errorCode = ErrorCode.fromHttp2(errorCodeInt);
    if (errorCode == null) {
      throw Http2.ioException("TYPE_RST_STREAM unexpected error code: %d", new Object[] { Integer.valueOf(errorCodeInt) });
    }
    handler.rstStream(streamId, errorCode);
  }
  
  private void readSettings(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (streamId != 0) throw Http2.ioException("TYPE_SETTINGS streamId != 0", new Object[0]);
    if ((flags & 0x1) != 0) {
      if (length != 0) throw Http2.ioException("FRAME_SIZE_ERROR ack frame should be empty!", new Object[0]);
      handler.ackSettings();
      return;
    }
    
    if (length % 6 != 0) throw Http2.ioException("TYPE_SETTINGS length %% 6 != 0: %s", new Object[] { Integer.valueOf(length) });
    Settings settings = new Settings();
    for (int i = 0; i < length; i += 6) {
      int id = source.readShort() & 0xFFFF;
      int value = source.readInt();
      
      switch (id) {
      case 1: 
        break;
      case 2: 
        if ((value != 0) && (value != 1)) {
          throw Http2.ioException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1", new Object[0]);
        }
        break;
      case 3: 
        id = 4;
        break;
      case 4: 
        id = 7;
        if (value < 0) {
          throw Http2.ioException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1", new Object[0]);
        }
        break;
      case 5: 
        if ((value < 16384) || (value > 16777215)) {
          throw Http2.ioException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: %s", new Object[] { Integer.valueOf(value) });
        }
        
        break;
      case 6: 
        break;
      }
      
      settings.set(id, value);
    }
    handler.settings(false, settings);
  }
  
  private void readPushPromise(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (streamId == 0) {
      throw Http2.ioException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0", new Object[0]);
    }
    short padding = (flags & 0x8) != 0 ? (short)(source.readByte() & 0xFF) : 0;
    int promisedStreamId = source.readInt() & 0x7FFFFFFF;
    length -= 4;
    length = lengthWithoutPadding(length, flags, padding);
    List<Header> headerBlock = readHeaderBlock(length, padding, flags, streamId);
    handler.pushPromise(streamId, promisedStreamId, headerBlock);
  }
  
  private void readPing(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (length != 8) throw Http2.ioException("TYPE_PING length != 8: %s", new Object[] { Integer.valueOf(length) });
    if (streamId != 0) throw Http2.ioException("TYPE_PING streamId != 0", new Object[0]);
    int payload1 = source.readInt();
    int payload2 = source.readInt();
    boolean ack = (flags & 0x1) != 0;
    handler.ping(ack, payload1, payload2);
  }
  
  private void readGoAway(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (length < 8) throw Http2.ioException("TYPE_GOAWAY length < 8: %s", new Object[] { Integer.valueOf(length) });
    if (streamId != 0) throw Http2.ioException("TYPE_GOAWAY streamId != 0", new Object[0]);
    int lastStreamId = source.readInt();
    int errorCodeInt = source.readInt();
    int opaqueDataLength = length - 8;
    ErrorCode errorCode = ErrorCode.fromHttp2(errorCodeInt);
    if (errorCode == null) {
      throw Http2.ioException("TYPE_GOAWAY unexpected error code: %d", new Object[] { Integer.valueOf(errorCodeInt) });
    }
    ByteString debugData = ByteString.EMPTY;
    if (opaqueDataLength > 0) {
      debugData = source.readByteString(opaqueDataLength);
    }
    handler.goAway(lastStreamId, errorCode, debugData);
  }
  
  private void readWindowUpdate(Handler handler, int length, byte flags, int streamId) throws IOException
  {
    if (length != 4) throw Http2.ioException("TYPE_WINDOW_UPDATE length !=4: %s", new Object[] { Integer.valueOf(length) });
    long increment = source.readInt() & 0x7FFFFFFF;
    if (increment == 0L) throw Http2.ioException("windowSizeIncrement was 0", new Object[] { Long.valueOf(increment) });
    handler.windowUpdate(streamId, increment);
  }
  
  public void close() throws IOException {
    source.close();
  }
  





  static final class ContinuationSource
    implements Source
  {
    private final BufferedSource source;
    





    ContinuationSource(BufferedSource source) { this.source = source; }
    
    int length;
    
    public long read(Buffer sink, long byteCount) throws IOException { while (left == 0) {
        source.skip(padding);
        padding = 0;
        if ((flags & 0x4) != 0) return -1L;
        readContinuationHeader();
      }
      

      long read = source.read(sink, Math.min(byteCount, left));
      if (read == -1L) return -1L;
      left = ((int)(left - read));
      return read; }
    
    byte flags;
    
    public Timeout timeout() { return source.timeout(); }
    
    public void close() throws IOException
    {}
    int streamId;
    int left;
    
    private void readContinuationHeader() throws IOException { int previousStreamId = streamId;
      
      length = (this.left = Http2Reader.readMedium(source));
      byte type = (byte)(source.readByte() & 0xFF);
      flags = ((byte)(source.readByte() & 0xFF));
      if (Http2Reader.logger.isLoggable(Level.FINE)) Http2Reader.logger.fine(Http2.frameLog(true, streamId, length, type, flags));
      streamId = (source.readInt() & 0x7FFFFFFF);
      if (type != 9) throw Http2.ioException("%s != TYPE_CONTINUATION", new Object[] { Byte.valueOf(type) });
      if (streamId != previousStreamId) throw Http2.ioException("TYPE_CONTINUATION streamId changed", new Object[0]);
    }
    
    short padding; }
  
  static int readMedium(BufferedSource source) throws IOException { return 
    
      (source.readByte() & 0xFF) << 16 | (source.readByte() & 0xFF) << 8 | source.readByte() & 0xFF;
  }
  
  static int lengthWithoutPadding(int length, byte flags, short padding) throws IOException
  {
    if ((flags & 0x8) != 0) length--;
    if (padding > length) {
      throw Http2.ioException("PROTOCOL_ERROR padding %s > remaining length %s", new Object[] { Short.valueOf(padding), Integer.valueOf(length) });
    }
    return (short)(length - padding);
  }
}
