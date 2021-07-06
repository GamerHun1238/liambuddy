package okio;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Inflater;





















public final class GzipSource
  implements Source
{
  private static final byte FHCRC = 1;
  private static final byte FEXTRA = 2;
  private static final byte FNAME = 3;
  private static final byte FCOMMENT = 4;
  private static final byte SECTION_HEADER = 0;
  private static final byte SECTION_BODY = 1;
  private static final byte SECTION_TRAILER = 2;
  private static final byte SECTION_DONE = 3;
  private int section = 0;
  



  private final BufferedSource source;
  



  private final Inflater inflater;
  



  private final InflaterSource inflaterSource;
  


  private final CRC32 crc = new CRC32();
  
  public GzipSource(Source source) {
    if (source == null) throw new IllegalArgumentException("source == null");
    inflater = new Inflater(true);
    this.source = Okio.buffer(source);
    inflaterSource = new InflaterSource(this.source, inflater);
  }
  
  public long read(Buffer sink, long byteCount) throws IOException {
    if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (byteCount == 0L) { return 0L;
    }
    
    if (section == 0) {
      consumeHeader();
      section = 1;
    }
    

    if (section == 1) {
      long offset = size;
      long result = inflaterSource.read(sink, byteCount);
      if (result != -1L) {
        updateCrc(sink, offset, result);
        return result;
      }
      section = 2;
    }
    



    if (section == 2) {
      consumeTrailer();
      section = 3;
      




      if (!source.exhausted()) {
        throw new IOException("gzip finished without exhausting source");
      }
    }
    
    return -1L;
  }
  




  private void consumeHeader()
    throws IOException
  {
    source.require(10L);
    byte flags = source.buffer().getByte(3L);
    boolean fhcrc = (flags >> 1 & 0x1) == 1;
    if (fhcrc) { updateCrc(source.buffer(), 0L, 10L);
    }
    short id1id2 = source.readShort();
    checkEqual("ID1ID2", 8075, id1id2);
    source.skip(8L);
    




    if ((flags >> 2 & 0x1) == 1) {
      source.require(2L);
      if (fhcrc) updateCrc(source.buffer(), 0L, 2L);
      int xlen = source.buffer().readShortLe();
      source.require(xlen);
      if (fhcrc) updateCrc(source.buffer(), 0L, xlen);
      source.skip(xlen);
    }
    




    if ((flags >> 3 & 0x1) == 1) {
      long index = source.indexOf((byte)0);
      if (index == -1L) throw new EOFException();
      if (fhcrc) updateCrc(source.buffer(), 0L, index + 1L);
      source.skip(index + 1L);
    }
    




    if ((flags >> 4 & 0x1) == 1) {
      long index = source.indexOf((byte)0);
      if (index == -1L) throw new EOFException();
      if (fhcrc) updateCrc(source.buffer(), 0L, index + 1L);
      source.skip(index + 1L);
    }
    




    if (fhcrc) {
      checkEqual("FHCRC", source.readShortLe(), (short)(int)crc.getValue());
      crc.reset();
    }
  }
  


  private void consumeTrailer()
    throws IOException
  {
    checkEqual("CRC", source.readIntLe(), (int)crc.getValue());
    checkEqual("ISIZE", source.readIntLe(), (int)inflater.getBytesWritten());
  }
  
  public Timeout timeout() {
    return source.timeout();
  }
  
  public void close() throws IOException {
    inflaterSource.close();
  }
  

  private void updateCrc(Buffer buffer, long offset, long byteCount)
  {
    for (Segment s = head; 
        offset >= limit - pos; s = next) {
      offset -= limit - pos;
    }
    for (; 
        
        byteCount > 0L; s = next) {
      int pos = (int)(pos + offset);
      int toUpdate = (int)Math.min(limit - pos, byteCount);
      crc.update(data, pos, toUpdate);
      byteCount -= toUpdate;
      offset = 0L;
    }
  }
  
  private void checkEqual(String name, int expected, int actual) throws IOException {
    if (actual != expected) {
      throw new IOException(String.format("%s: actual 0x%08x != expected 0x%08x", new Object[] { name, 
        Integer.valueOf(actual), Integer.valueOf(expected) }));
    }
  }
}
