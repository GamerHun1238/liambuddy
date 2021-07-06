package okio;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;























public final class InflaterSource
  implements Source
{
  private final BufferedSource source;
  private final Inflater inflater;
  private int bufferBytesHeldByInflater;
  private boolean closed;
  
  public InflaterSource(Source source, Inflater inflater)
  {
    this(Okio.buffer(source), inflater);
  }
  




  InflaterSource(BufferedSource source, Inflater inflater)
  {
    if (source == null) throw new IllegalArgumentException("source == null");
    if (inflater == null) throw new IllegalArgumentException("inflater == null");
    this.source = source;
    this.inflater = inflater;
  }
  
  public long read(Buffer sink, long byteCount) throws IOException
  {
    if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (closed) throw new IllegalStateException("closed");
    if (byteCount == 0L) return 0L;
    for (;;)
    {
      boolean sourceExhausted = refill();
      
      try
      {
        Segment tail = sink.writableSegment(1);
        int toRead = (int)Math.min(byteCount, 8192 - limit);
        int bytesInflated = inflater.inflate(data, limit, toRead);
        if (bytesInflated > 0) {
          limit += bytesInflated;
          size += bytesInflated;
          return bytesInflated;
        }
        if ((inflater.finished()) || (inflater.needsDictionary())) {
          releaseInflatedBytes();
          if (pos == limit)
          {
            head = tail.pop();
            SegmentPool.recycle(tail);
          }
          return -1L;
        }
        if (sourceExhausted) throw new EOFException("source exhausted prematurely");
      } catch (DataFormatException e) {
        throw new IOException(e);
      }
    }
  }
  



  public final boolean refill()
    throws IOException
  {
    if (!inflater.needsInput()) { return false;
    }
    releaseInflatedBytes();
    if (inflater.getRemaining() != 0) { throw new IllegalStateException("?");
    }
    
    if (source.exhausted()) { return true;
    }
    
    Segment head = source.buffer().head;
    bufferBytesHeldByInflater = (limit - pos);
    inflater.setInput(data, pos, bufferBytesHeldByInflater);
    return false;
  }
  
  private void releaseInflatedBytes() throws IOException
  {
    if (bufferBytesHeldByInflater == 0) return;
    int toRelease = bufferBytesHeldByInflater - inflater.getRemaining();
    bufferBytesHeldByInflater -= toRelease;
    source.skip(toRelease);
  }
  
  public Timeout timeout() {
    return source.timeout();
  }
  
  public void close() throws IOException {
    if (closed) return;
    inflater.end();
    closed = true;
    source.close();
  }
}
