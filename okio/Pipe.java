package okio;

import java.io.IOException;
import javax.annotation.Nullable;































public final class Pipe
{
  final long maxBufferSize;
  final Buffer buffer = new Buffer();
  boolean sinkClosed;
  boolean sourceClosed;
  private final Sink sink = new PipeSink();
  private final Source source = new PipeSource();
  @Nullable
  private Sink foldedSink;
  
  public Pipe(long maxBufferSize) { if (maxBufferSize < 1L) {
      throw new IllegalArgumentException("maxBufferSize < 1: " + maxBufferSize);
    }
    this.maxBufferSize = maxBufferSize;
  }
  
  public final Source source() {
    return source;
  }
  
  public final Sink sink() {
    return sink;
  }
  






  public void fold(Sink sink)
    throws IOException
  {
    for (;;)
    {
      synchronized (buffer) {
        if (foldedSink != null) { throw new IllegalStateException("sink already folded");
        }
        if (buffer.exhausted()) {
          sourceClosed = true;
          foldedSink = sink;
          return;
        }
        
        Buffer sinkBuffer = new Buffer();
        sinkBuffer.write(buffer, buffer.size);
        buffer.notifyAll();
      }
      Buffer sinkBuffer;
      boolean success = false;
      try {
        sink.write(sinkBuffer, size);
        sink.flush();
        success = true;
      } finally {
        if (!success)
          synchronized (buffer) {
            sourceClosed = true;
            buffer.notifyAll();
          }
      }
    }
  }
  
  final class PipeSink implements Sink { PipeSink() {}
    
    final PushableTimeout timeout = new PushableTimeout();
    
    public void write(Buffer source, long byteCount) throws IOException {
      Sink delegate = null;
      synchronized (buffer) {
        if (sinkClosed) { throw new IllegalStateException("closed");
        }
        while (byteCount > 0L) {
          if (foldedSink != null) {
            delegate = foldedSink;
            break;
          }
          
          if (sourceClosed) { throw new IOException("source is closed");
          }
          long bufferSpaceAvailable = maxBufferSize - buffer.size();
          if (bufferSpaceAvailable == 0L) {
            timeout.waitUntilNotified(buffer);
          }
          else
          {
            long bytesToWrite = Math.min(bufferSpaceAvailable, byteCount);
            buffer.write(source, bytesToWrite);
            byteCount -= bytesToWrite;
            buffer.notifyAll();
          }
        }
      }
      if (delegate != null) {
        timeout.push(delegate.timeout());
        try {
          delegate.write(source, byteCount);
        } finally {
          timeout.pop();
        }
      }
    }
    
    public void flush() throws IOException {
      Sink delegate = null;
      synchronized (buffer) {
        if (sinkClosed) { throw new IllegalStateException("closed");
        }
        if (foldedSink != null) {
          delegate = foldedSink;
        } else if ((sourceClosed) && (buffer.size() > 0L)) {
          throw new IOException("source is closed");
        }
      }
      
      if (delegate != null) {
        timeout.push(delegate.timeout());
        try {
          delegate.flush();
        } finally {
          timeout.pop();
        }
      }
    }
    
    public void close() throws IOException {
      Sink delegate = null;
      synchronized (buffer) {
        if (sinkClosed) { return;
        }
        if (foldedSink != null) {
          delegate = foldedSink;
        } else {
          if ((sourceClosed) && (buffer.size() > 0L)) throw new IOException("source is closed");
          sinkClosed = true;
          buffer.notifyAll();
        }
      }
      
      if (delegate != null) {
        timeout.push(delegate.timeout());
        try {
          delegate.close();
        } finally {
          timeout.pop();
        }
      }
    }
    

    public Timeout timeout() { return timeout; }
  }
  
  final class PipeSource implements Source { PipeSource() {}
    
    final Timeout timeout = new Timeout();
    
    public long read(Buffer sink, long byteCount) throws IOException {
      synchronized (buffer) {
        if (sourceClosed) { throw new IllegalStateException("closed");
        }
        while (buffer.size() == 0L) {
          if (sinkClosed) return -1L;
          timeout.waitUntilNotified(buffer);
        }
        
        long result = buffer.read(sink, byteCount);
        buffer.notifyAll();
        return result;
      }
    }
    
    public void close() throws IOException {
      synchronized (buffer) {
        sourceClosed = true;
        buffer.notifyAll();
      }
    }
    
    public Timeout timeout() {
      return timeout;
    }
  }
}
