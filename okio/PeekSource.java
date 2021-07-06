package okio;

import java.io.IOException;
























final class PeekSource
  implements Source
{
  private final BufferedSource upstream;
  private final Buffer buffer;
  private Segment expectedSegment;
  private int expectedPos;
  private boolean closed;
  private long pos;
  
  PeekSource(BufferedSource upstream)
  {
    this.upstream = upstream;
    buffer = upstream.buffer();
    expectedSegment = buffer.head;
    expectedPos = (expectedSegment != null ? expectedSegment.pos : -1);
  }
  
  public long read(Buffer sink, long byteCount) throws IOException {
    if (closed) { throw new IllegalStateException("closed");
    }
    

    if ((expectedSegment != null) && ((expectedSegment != buffer.head) || (expectedPos != buffer.head.pos)))
    {
      throw new IllegalStateException("Peek source is invalid because upstream source was used");
    }
    
    upstream.request(pos + byteCount);
    if ((expectedSegment == null) && (buffer.head != null))
    {


      expectedSegment = buffer.head;
      expectedPos = buffer.head.pos;
    }
    
    long toCopy = Math.min(byteCount, buffer.size - pos);
    if (toCopy <= 0L) { return -1L;
    }
    buffer.copyTo(sink, pos, toCopy);
    pos += toCopy;
    return toCopy;
  }
  
  public Timeout timeout() {
    return upstream.timeout();
  }
  
  public void close() throws IOException {
    closed = true;
  }
}
