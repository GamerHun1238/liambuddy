package okhttp3.internal.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import okhttp3.Headers;
import okhttp3.internal.Util;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;
























public final class Http2Stream
{
  long unacknowledgedBytesRead = 0L;
  



  long bytesLeftInWriteWindow;
  


  final int id;
  


  final Http2Connection connection;
  


  private final Deque<Headers> headersQueue = new ArrayDeque();
  
  private boolean hasResponseHeaders;
  
  private final FramingSource source;
  
  final FramingSink sink;
  final StreamTimeout readTimeout = new StreamTimeout();
  final StreamTimeout writeTimeout = new StreamTimeout();
  





  ErrorCode errorCode = null;
  
  Http2Stream(int id, Http2Connection connection, boolean outFinished, boolean inFinished, @Nullable Headers headers)
  {
    if (connection == null) { throw new NullPointerException("connection == null");
    }
    this.id = id;
    this.connection = connection;
    
    bytesLeftInWriteWindow = peerSettings.getInitialWindowSize();
    source = new FramingSource(okHttpSettings.getInitialWindowSize());
    sink = new FramingSink();
    source.finished = inFinished;
    sink.finished = outFinished;
    if (headers != null) {
      headersQueue.add(headers);
    }
    
    if ((isLocallyInitiated()) && (headers != null))
      throw new IllegalStateException("locally-initiated streams shouldn't have headers yet");
    if ((!isLocallyInitiated()) && (headers == null)) {
      throw new IllegalStateException("remotely-initiated streams should have headers");
    }
  }
  
  public int getId() {
    return id;
  }
  










  public synchronized boolean isOpen()
  {
    if (errorCode != null) {
      return false;
    }
    if (((source.finished) || (source.closed)) && ((sink.finished) || (sink.closed)) && (hasResponseHeaders))
    {

      return false;
    }
    return true;
  }
  
  public boolean isLocallyInitiated()
  {
    boolean streamIsClient = (id & 0x1) == 1;
    return connection.client == streamIsClient;
  }
  
  public Http2Connection getConnection() {
    return connection;
  }
  



  public synchronized Headers takeHeaders()
    throws IOException
  {
    readTimeout.enter();
    try {
      while ((headersQueue.isEmpty()) && (errorCode == null)) {
        waitForIo();
      }
      
      readTimeout.exitAndThrowIfTimedOut(); } finally { readTimeout.exitAndThrowIfTimedOut();
    }
    
    return (Headers)headersQueue.removeFirst();
    
    throw new StreamResetException(errorCode);
  }
  


  public synchronized Headers trailers()
    throws IOException
  {
    if (errorCode != null) {
      throw new StreamResetException(errorCode);
    }
    if ((!source.finished) || (!source.receiveBuffer.exhausted()) || (!source.readBuffer.exhausted())) {
      throw new IllegalStateException("too early; can't read the trailers yet");
    }
    return source.trailers != null ? source.trailers : Util.EMPTY_HEADERS;
  }
  



  public synchronized ErrorCode getErrorCode()
  {
    return errorCode;
  }
  







  public void writeHeaders(List<Header> responseHeaders, boolean outFinished, boolean flushHeaders)
    throws IOException
  {
    assert (!Thread.holdsLock(this));
    if (responseHeaders == null) {
      throw new NullPointerException("headers == null");
    }
    synchronized (this) {
      hasResponseHeaders = true;
      if (outFinished) {
        sink.finished = true;
      }
    }
    


    if (!flushHeaders) {
      synchronized (connection) {
        flushHeaders = connection.bytesLeftInWriteWindow == 0L;
      }
    }
    
    connection.writeHeaders(id, outFinished, responseHeaders);
    
    if (flushHeaders) {
      connection.flush();
    }
  }
  
  public void enqueueTrailers(Headers trailers) {
    synchronized (this) {
      if (sink.finished) throw new IllegalStateException("already finished");
      if (trailers.size() == 0) throw new IllegalArgumentException("trailers.size() == 0");
      sink.trailers = trailers;
    }
  }
  
  public Timeout readTimeout() {
    return readTimeout;
  }
  
  public Timeout writeTimeout() {
    return writeTimeout;
  }
  
  public Source getSource()
  {
    return source;
  }
  





  public Sink getSink()
  {
    synchronized (this) {
      if ((!hasResponseHeaders) && (!isLocallyInitiated())) {
        throw new IllegalStateException("reply before requesting the sink");
      }
    }
    return sink;
  }
  


  public void close(ErrorCode rstStatusCode)
    throws IOException
  {
    if (!closeInternal(rstStatusCode)) {
      return;
    }
    connection.writeSynReset(id, rstStatusCode);
  }
  



  public void closeLater(ErrorCode errorCode)
  {
    if (!closeInternal(errorCode)) {
      return;
    }
    connection.writeSynResetLater(id, errorCode);
  }
  
  private boolean closeInternal(ErrorCode errorCode)
  {
    assert (!Thread.holdsLock(this));
    synchronized (this) {
      if (this.errorCode != null) {
        return false;
      }
      if ((source.finished) && (sink.finished)) {
        return false;
      }
      this.errorCode = errorCode;
      notifyAll();
    }
    connection.removeStream(id);
    return true;
  }
  
  void receiveData(BufferedSource in, int length) throws IOException {
    assert (!Thread.holdsLock(this));
    source.receive(in, length);
  }
  



  void receiveHeaders(Headers headers, boolean inFinished)
  {
    assert (!Thread.holdsLock(this));
    
    synchronized (this) {
      if ((!hasResponseHeaders) || (!inFinished)) {
        hasResponseHeaders = true;
        headersQueue.add(headers);
      } else {
        source.trailers = headers;
      }
      if (inFinished) {
        source.finished = true;
      }
      boolean open = isOpen();
      notifyAll(); }
    boolean open;
    if (!open) {
      connection.removeStream(id);
    }
  }
  
  synchronized void receiveRstStream(ErrorCode errorCode) {
    if (this.errorCode == null) {
      this.errorCode = errorCode;
      notifyAll();
    }
  }
  




  private final class FramingSource
    implements Source
  {
    private final Buffer receiveBuffer = new Buffer();
    

    private final Buffer readBuffer = new Buffer();
    


    private final long maxByteCount;
    


    private Headers trailers;
    


    boolean closed;
    

    boolean finished;
    


    FramingSource(long maxByteCount)
    {
      this.maxByteCount = maxByteCount;
    }
    
    public long read(Buffer sink, long byteCount) throws IOException {
      if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
      long readBytesDelivered;
      ErrorCode errorCodeToDeliver;
      for (;;) { readBytesDelivered = -1L;
        errorCodeToDeliver = null;
        


        synchronized (Http2Stream.this) {
          readTimeout.enter();
          try {
            if (errorCode != null)
            {
              errorCodeToDeliver = errorCode;
            }
            
            if (closed) {
              throw new IOException("stream closed");
            }
            if (readBuffer.size() > 0L)
            {
              readBytesDelivered = readBuffer.read(sink, Math.min(byteCount, readBuffer.size()));
              unacknowledgedBytesRead += readBytesDelivered;
              
              if (errorCodeToDeliver == null)
              {
                if (unacknowledgedBytesRead >= connection.okHttpSettings.getInitialWindowSize() / 2)
                {

                  connection.writeWindowUpdateLater(id, unacknowledgedBytesRead);
                  unacknowledgedBytesRead = 0L;
                } }
            } else if ((!finished) && (errorCodeToDeliver == null))
            {
              waitForIo();
              


              readTimeout.exitAndThrowIfTimedOut(); continue; } } finally { readTimeout.exitAndThrowIfTimedOut();
          }
        }
      }
      

      if (readBytesDelivered != -1L)
      {
        updateConnectionFlowControl(readBytesDelivered);
        return readBytesDelivered;
      }
      
      if (errorCodeToDeliver != null)
      {



        throw new StreamResetException(errorCodeToDeliver);
      }
      
      return -1L;
    }
    
    private void updateConnectionFlowControl(long read)
    {
      assert (!Thread.holdsLock(Http2Stream.this));
      connection.updateConnectionFlowControl(read);
    }
    
    void receive(BufferedSource in, long byteCount) throws IOException {
      assert (!Thread.holdsLock(Http2Stream.this));
      
      while (byteCount > 0L)
      {
        boolean flowControlError;
        synchronized (Http2Stream.this) {
          boolean finished = this.finished;
          flowControlError = byteCount + readBuffer.size() > maxByteCount;
        }
        boolean flowControlError;
        boolean finished;
        if (flowControlError) {
          in.skip(byteCount);
          closeLater(ErrorCode.FLOW_CONTROL_ERROR);
          return;
        }
        

        if (finished) {
          in.skip(byteCount);
          return;
        }
        

        long read = in.read(receiveBuffer, byteCount);
        if (read == -1L) throw new EOFException();
        byteCount -= read;
        

        synchronized (Http2Stream.this) {
          boolean wasEmpty = readBuffer.size() == 0L;
          readBuffer.writeAll(receiveBuffer);
          if (wasEmpty) {
            notifyAll();
          }
        }
      }
    }
    
    public Timeout timeout() {
      return readTimeout;
    }
    
    public void close() throws IOException
    {
      synchronized (Http2Stream.this) {
        closed = true;
        long bytesDiscarded = readBuffer.size();
        readBuffer.clear();
        notifyAll(); }
      long bytesDiscarded;
      if (bytesDiscarded > 0L) {
        updateConnectionFlowControl(bytesDiscarded);
      }
      cancelStreamIfNecessary();
    }
  }
  
  void cancelStreamIfNecessary() throws IOException {
    assert (!Thread.holdsLock(this));
    
    boolean open;
    synchronized (this) {
      boolean cancel = (!source.finished) && (source.closed) && ((sink.finished) || (sink.closed));
      open = isOpen(); }
    boolean cancel;
    boolean open; if (cancel)
    {



      close(ErrorCode.CANCEL);
    } else if (!open) {
      connection.removeStream(id);
    }
  }
  


  final class FramingSink
    implements Sink
  {
    private static final long EMIT_BUFFER_SIZE = 16384L;
    

    private final Buffer sendBuffer = new Buffer();
    
    private Headers trailers;
    
    boolean closed;
    
    boolean finished;
    
    FramingSink() {}
    
    public void write(Buffer source, long byteCount)
      throws IOException
    {
      assert (!Thread.holdsLock(Http2Stream.this));
      sendBuffer.write(source, byteCount);
      while (sendBuffer.size() >= 16384L) {
        emitFrame(false);
      }
    }
    



    private void emitFrame(boolean outFinishedOnLastFrame)
      throws IOException
    {
      synchronized (Http2Stream.this) {
        writeTimeout.enter();
        try {
          while ((bytesLeftInWriteWindow <= 0L) && (!finished) && (!closed) && (errorCode == null)) {
            waitForIo();
          }
        } finally {
          writeTimeout.exitAndThrowIfTimedOut();
        }
        
        checkOutNotClosed();
        long toWrite = Math.min(bytesLeftInWriteWindow, sendBuffer.size());
        bytesLeftInWriteWindow -= toWrite;
      }
      long toWrite;
      writeTimeout.enter();
      try {
        boolean outFinished = (outFinishedOnLastFrame) && (toWrite == sendBuffer.size());
        connection.writeData(id, outFinished, sendBuffer, toWrite);
      } finally {
        writeTimeout.exitAndThrowIfTimedOut();
      }
    }
    
    public void flush() throws IOException {
      assert (!Thread.holdsLock(Http2Stream.this));
      synchronized (Http2Stream.this) {
        checkOutNotClosed();
      }
      while (sendBuffer.size() > 0L) {
        emitFrame(false);
        connection.flush();
      }
    }
    
    public Timeout timeout() {
      return writeTimeout;
    }
    
    public void close() throws IOException {
      assert (!Thread.holdsLock(Http2Stream.this));
      synchronized (Http2Stream.this) {
        if (closed) return;
      }
      if (!sink.finished)
      {


        boolean hasData = sendBuffer.size() > 0L;
        boolean hasTrailers = trailers != null;
        if (hasTrailers) {
          while (sendBuffer.size() > 0L) {
            emitFrame(false);
          }
          connection.writeHeaders(id, true, Util.toHeaderBlock(trailers));
        } else { if (hasData) {
            while (sendBuffer.size() > 0L) {
              emitFrame(true);
            }
          }
          connection.writeData(id, true, null, 0L);
        }
      }
      synchronized (Http2Stream.this) {
        closed = true;
      }
      connection.flush();
      cancelStreamIfNecessary();
    }
  }
  


  void addBytesToWriteWindow(long delta)
  {
    bytesLeftInWriteWindow += delta;
    if (delta > 0L) notifyAll();
  }
  
  void checkOutNotClosed() throws IOException {
    if (sink.closed)
      throw new IOException("stream closed");
    if (sink.finished)
      throw new IOException("stream finished");
    if (errorCode != null) {
      throw new StreamResetException(errorCode);
    }
  }
  

  void waitForIo()
    throws InterruptedIOException
  {
    try
    {
      wait();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InterruptedIOException();
    }
  }
  
  class StreamTimeout extends AsyncTimeout
  {
    StreamTimeout() {}
    
    protected void timedOut()
    {
      closeLater(ErrorCode.CANCEL);
    }
    
    protected IOException newTimeoutException(IOException cause) {
      SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
      if (cause != null) {
        socketTimeoutException.initCause(cause);
      }
      return socketTimeoutException;
    }
    
    public void exitAndThrowIfTimedOut() throws IOException {
      if (exit()) throw newTimeoutException(null);
    }
  }
}
