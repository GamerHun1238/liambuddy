package okio;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;









































public final class GzipSink
  implements Sink
{
  private final BufferedSink sink;
  private final Deflater deflater;
  private final DeflaterSink deflaterSink;
  private boolean closed;
  private final CRC32 crc = new CRC32();
  
  public GzipSink(Sink sink) {
    if (sink == null) throw new IllegalArgumentException("sink == null");
    deflater = new Deflater(-1, true);
    this.sink = Okio.buffer(sink);
    deflaterSink = new DeflaterSink(this.sink, deflater);
    
    writeHeader();
  }
  
  public void write(Buffer source, long byteCount) throws IOException {
    if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (byteCount == 0L) { return;
    }
    updateCrc(source, byteCount);
    deflaterSink.write(source, byteCount);
  }
  
  public void flush() throws IOException {
    deflaterSink.flush();
  }
  
  public Timeout timeout() {
    return sink.timeout();
  }
  
  public void close() throws IOException {
    if (closed) { return;
    }
    




    Throwable thrown = null;
    try {
      deflaterSink.finishDeflate();
      writeFooter();
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
    
    if (thrown != null) { Util.sneakyRethrow(thrown);
    }
  }
  


  public final Deflater deflater()
  {
    return deflater;
  }
  
  private void writeHeader()
  {
    Buffer buffer = sink.buffer();
    buffer.writeShort(8075);
    buffer.writeByte(8);
    buffer.writeByte(0);
    buffer.writeInt(0);
    buffer.writeByte(0);
    buffer.writeByte(0);
  }
  
  private void writeFooter() throws IOException {
    sink.writeIntLe((int)crc.getValue());
    sink.writeIntLe((int)deflater.getBytesRead());
  }
  
  private void updateCrc(Buffer buffer, long byteCount)
  {
    for (Segment head = head; byteCount > 0L; head = next) {
      int segmentLength = (int)Math.min(byteCount, limit - pos);
      crc.update(data, pos, segmentLength);
      byteCount -= segmentLength;
    }
  }
}
