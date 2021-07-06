package okio;

import java.io.IOException;
import java.util.zip.Deflater;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;





























public final class DeflaterSink
  implements Sink
{
  private final BufferedSink sink;
  private final Deflater deflater;
  private boolean closed;
  
  public DeflaterSink(Sink sink, Deflater deflater)
  {
    this(Okio.buffer(sink), deflater);
  }
  




  DeflaterSink(BufferedSink sink, Deflater deflater)
  {
    if (sink == null) throw new IllegalArgumentException("source == null");
    if (deflater == null) throw new IllegalArgumentException("inflater == null");
    this.sink = sink;
    this.deflater = deflater;
  }
  
  public void write(Buffer source, long byteCount) throws IOException {
    Util.checkOffsetAndCount(size, 0L, byteCount);
    while (byteCount > 0L)
    {
      Segment head = head;
      int toDeflate = (int)Math.min(byteCount, limit - pos);
      deflater.setInput(data, pos, toDeflate);
      

      deflate(false);
      

      size -= toDeflate;
      pos += toDeflate;
      if (pos == limit) {
        head = head.pop();
        SegmentPool.recycle(head);
      }
      
      byteCount -= toDeflate;
    }
  }
  
  @IgnoreJRERequirement
  private void deflate(boolean syncFlush) throws IOException {
    Buffer buffer = sink.buffer();
    for (;;) {
      Segment s = buffer.writableSegment(1);
      






      int deflated = syncFlush ? deflater.deflate(data, limit, 8192 - limit, 2) : deflater.deflate(data, limit, 8192 - limit);
      
      if (deflated > 0) {
        limit += deflated;
        size += deflated;
        sink.emitCompleteSegments();
      } else if (deflater.needsInput()) {
        if (pos == limit)
        {
          head = s.pop();
          SegmentPool.recycle(s);
        }
        return;
      }
    }
  }
  
  public void flush() throws IOException {
    deflate(true);
    sink.flush();
  }
  
  void finishDeflate() throws IOException {
    deflater.finish();
    deflate(false);
  }
  
  public void close() throws IOException {
    if (closed) { return;
    }
    

    Throwable thrown = null;
    try {
      finishDeflate();
    } catch (Throwable e) {
      thrown = e;
    }
    try
    {
      deflater.end();
    } catch (Throwable e) {
      if (thrown == null) thrown = e;
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
    return "DeflaterSink(" + sink + ")";
  }
}
