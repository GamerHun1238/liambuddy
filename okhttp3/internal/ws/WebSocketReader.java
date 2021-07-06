package okhttp3.internal.ws;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import okio.Buffer.UnsafeCursor;
import okio.BufferedSource;
import okio.ByteString;
import okio.Timeout;



















































final class WebSocketReader
{
  final boolean isClient;
  final BufferedSource source;
  final FrameCallback frameCallback;
  boolean closed;
  int opcode;
  long frameLength;
  boolean isFinalFrame;
  boolean isControlFrame;
  private final Buffer controlFrameBuffer = new Buffer();
  private final Buffer messageFrameBuffer = new Buffer();
  private final byte[] maskKey;
  private final Buffer.UnsafeCursor maskCursor;
  
  WebSocketReader(boolean isClient, BufferedSource source, FrameCallback frameCallback)
  {
    if (source == null) throw new NullPointerException("source == null");
    if (frameCallback == null) throw new NullPointerException("frameCallback == null");
    this.isClient = isClient;
    this.source = source;
    this.frameCallback = frameCallback;
    

    maskKey = (isClient ? null : new byte[4]);
    maskCursor = (isClient ? null : new Buffer.UnsafeCursor());
  }
  








  void processNextFrame()
    throws IOException
  {
    readHeader();
    if (isControlFrame) {
      readControlFrame();
    } else {
      readMessageFrame();
    }
  }
  
  private void readHeader() throws IOException {
    if (closed) { throw new IOException("closed");
    }
    

    long timeoutBefore = source.timeout().timeoutNanos();
    source.timeout().clearTimeout();
    try {
      b0 = source.readByte() & 0xFF;
    } finally { int b0;
      source.timeout().timeout(timeoutBefore, TimeUnit.NANOSECONDS);
    }
    int b0;
    opcode = (b0 & 0xF);
    isFinalFrame = ((b0 & 0x80) != 0);
    isControlFrame = ((b0 & 0x8) != 0);
    

    if ((isControlFrame) && (!isFinalFrame)) {
      throw new ProtocolException("Control frames must be final.");
    }
    
    boolean reservedFlag1 = (b0 & 0x40) != 0;
    boolean reservedFlag2 = (b0 & 0x20) != 0;
    boolean reservedFlag3 = (b0 & 0x10) != 0;
    if ((reservedFlag1) || (reservedFlag2) || (reservedFlag3))
    {
      throw new ProtocolException("Reserved flags are unsupported.");
    }
    
    int b1 = source.readByte() & 0xFF;
    
    boolean isMasked = (b1 & 0x80) != 0;
    if (isMasked == isClient)
    {


      throw new ProtocolException(isClient ? "Server-sent frames must not be masked." : "Client-sent frames must be masked.");
    }
    

    frameLength = (b1 & 0x7F);
    if (frameLength == 126L) {
      frameLength = (source.readShort() & 0xFFFF);
    } else if (frameLength == 127L) {
      frameLength = source.readLong();
      if (frameLength < 0L)
      {
        throw new ProtocolException("Frame length 0x" + Long.toHexString(frameLength) + " > 0x7FFFFFFFFFFFFFFF");
      }
    }
    
    if ((isControlFrame) && (frameLength > 125L)) {
      throw new ProtocolException("Control frame must be less than 125B.");
    }
    
    if (isMasked)
    {
      source.readFully(maskKey);
    }
  }
  
  private void readControlFrame() throws IOException {
    if (frameLength > 0L) {
      source.readFully(controlFrameBuffer, frameLength);
      
      if (!isClient) {
        controlFrameBuffer.readAndWriteUnsafe(maskCursor);
        maskCursor.seek(0L);
        WebSocketProtocol.toggleMask(maskCursor, maskKey);
        maskCursor.close();
      }
    }
    
    switch (opcode) {
    case 9: 
      frameCallback.onReadPing(controlFrameBuffer.readByteString());
      break;
    case 10: 
      frameCallback.onReadPong(controlFrameBuffer.readByteString());
      break;
    case 8: 
      int code = 1005;
      String reason = "";
      long bufferSize = controlFrameBuffer.size();
      if (bufferSize == 1L)
        throw new ProtocolException("Malformed close payload length of 1.");
      if (bufferSize != 0L) {
        code = controlFrameBuffer.readShort();
        reason = controlFrameBuffer.readUtf8();
        String codeExceptionMessage = WebSocketProtocol.closeCodeExceptionMessage(code);
        if (codeExceptionMessage != null) throw new ProtocolException(codeExceptionMessage);
      }
      frameCallback.onReadClose(code, reason);
      closed = true;
      break;
    default: 
      throw new ProtocolException("Unknown control opcode: " + Integer.toHexString(opcode));
    }
  }
  
  private void readMessageFrame() throws IOException {
    int opcode = this.opcode;
    if ((opcode != 1) && (opcode != 2)) {
      throw new ProtocolException("Unknown opcode: " + Integer.toHexString(opcode));
    }
    
    readMessage();
    
    if (opcode == 1) {
      frameCallback.onReadMessage(messageFrameBuffer.readUtf8());
    } else {
      frameCallback.onReadMessage(messageFrameBuffer.readByteString());
    }
  }
  
  private void readUntilNonControlFrame() throws IOException
  {
    while (!closed) {
      readHeader();
      if (!isControlFrame) {
        break;
      }
      readControlFrame();
    }
  }
  


  private void readMessage()
    throws IOException
  {
    do
    {
      if (closed) { throw new IOException("closed");
      }
      if (frameLength > 0L) {
        source.readFully(messageFrameBuffer, frameLength);
        
        if (!isClient) {
          messageFrameBuffer.readAndWriteUnsafe(maskCursor);
          maskCursor.seek(messageFrameBuffer.size() - frameLength);
          WebSocketProtocol.toggleMask(maskCursor, maskKey);
          maskCursor.close();
        }
      }
      
      if (isFinalFrame)
        break;
      readUntilNonControlFrame();
    } while (opcode == 0);
    throw new ProtocolException("Expected continuation opcode. Got: " + Integer.toHexString(opcode));
  }
  
  public static abstract interface FrameCallback
  {
    public abstract void onReadMessage(String paramString)
      throws IOException;
    
    public abstract void onReadMessage(ByteString paramByteString)
      throws IOException;
    
    public abstract void onReadPing(ByteString paramByteString);
    
    public abstract void onReadPong(ByteString paramByteString);
    
    public abstract void onReadClose(int paramInt, String paramString);
  }
}
