package okio;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
































public final class Buffer
  implements BufferedSource, BufferedSink, Cloneable, ByteChannel
{
  private static final byte[] DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  
  static final int REPLACEMENT_CHARACTER = 65533;
  
  @Nullable
  Segment head;
  long size;
  
  public Buffer() {}
  
  public final long size()
  {
    return size;
  }
  
  public Buffer buffer() {
    return this;
  }
  
  public Buffer getBuffer() {
    return this;
  }
  
  public OutputStream outputStream() {
    new OutputStream() {
      public void write(int b) {
        writeByte((byte)b);
      }
      
      public void write(byte[] data, int offset, int byteCount) {
        write(data, offset, byteCount);
      }
      

      public void flush() {}
      
      public void close() {}
      
      public String toString()
      {
        return Buffer.this + ".outputStream()";
      }
    };
  }
  
  public Buffer emitCompleteSegments() {
    return this;
  }
  
  public BufferedSink emit() {
    return this;
  }
  
  public boolean exhausted() {
    return size == 0L;
  }
  
  public void require(long byteCount) throws EOFException {
    if (size < byteCount) throw new EOFException();
  }
  
  public boolean request(long byteCount) {
    return size >= byteCount;
  }
  
  public BufferedSource peek() {
    return Okio.buffer(new PeekSource(this));
  }
  
  public InputStream inputStream() {
    new InputStream() {
      public int read() {
        if (size > 0L) return readByte() & 0xFF;
        return -1;
      }
      
      public int read(byte[] sink, int offset, int byteCount) {
        return Buffer.this.read(sink, offset, byteCount);
      }
      
      public int available() {
        return (int)Math.min(size, 2147483647L);
      }
      
      public void close() {}
      
      public String toString()
      {
        return Buffer.this + ".inputStream()";
      }
    };
  }
  
  public final Buffer copyTo(OutputStream out) throws IOException
  {
    return copyTo(out, 0L, size);
  }
  


  public final Buffer copyTo(OutputStream out, long offset, long byteCount)
    throws IOException
  {
    if (out == null) throw new IllegalArgumentException("out == null");
    Util.checkOffsetAndCount(size, offset, byteCount);
    if (byteCount == 0L) { return this;
    }
    
    for (Segment s = head; 
        offset >= limit - pos; s = next) {
      offset -= limit - pos;
    }
    for (; 
        
        byteCount > 0L; s = next) {
      int pos = (int)(pos + offset);
      int toCopy = (int)Math.min(limit - pos, byteCount);
      out.write(data, pos, toCopy);
      byteCount -= toCopy;
      offset = 0L;
    }
    
    return this;
  }
  
  public final Buffer copyTo(Buffer out, long offset, long byteCount)
  {
    if (out == null) throw new IllegalArgumentException("out == null");
    Util.checkOffsetAndCount(size, offset, byteCount);
    if (byteCount == 0L) { return this;
    }
    size += byteCount;
    

    for (Segment s = head; 
        offset >= limit - pos; s = next) {
      offset -= limit - pos;
    }
    for (; 
        
        byteCount > 0L; tmp108_106 = next) {
      Segment copy = s.sharedCopy(); Segment 
        tmp108_106 = copy;108106pos = ((int)(108106pos + offset));
      limit = Math.min(pos + (int)byteCount, limit);
      if (head == null) {
        head = (copy.next = copy.prev = copy);
      } else {
        head.prev.push(copy);
      }
      byteCount -= limit - pos;
      offset = 0L;
    }
    
    return this;
  }
  
  public final Buffer writeTo(OutputStream out) throws IOException
  {
    return writeTo(out, size);
  }
  
  public final Buffer writeTo(OutputStream out, long byteCount) throws IOException
  {
    if (out == null) throw new IllegalArgumentException("out == null");
    Util.checkOffsetAndCount(size, 0L, byteCount);
    
    Segment s = head;
    while (byteCount > 0L) {
      int toCopy = (int)Math.min(byteCount, limit - pos);
      out.write(data, pos, toCopy);
      
      pos += toCopy;
      size -= toCopy;
      byteCount -= toCopy;
      
      if (pos == limit) {
        Segment toRecycle = s;
        head = (s = toRecycle.pop());
        SegmentPool.recycle(toRecycle);
      }
    }
    
    return this;
  }
  
  public final Buffer readFrom(InputStream in) throws IOException
  {
    readFrom(in, Long.MAX_VALUE, true);
    return this;
  }
  
  public final Buffer readFrom(InputStream in, long byteCount) throws IOException
  {
    if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    readFrom(in, byteCount, false);
    return this;
  }
  
  private void readFrom(InputStream in, long byteCount, boolean forever) throws IOException {
    if (in == null) throw new IllegalArgumentException("in == null");
    while ((byteCount > 0L) || (forever)) {
      Segment tail = writableSegment(1);
      int maxToCopy = (int)Math.min(byteCount, 8192 - limit);
      int bytesRead = in.read(data, limit, maxToCopy);
      if (bytesRead == -1) {
        if (forever) return;
        throw new EOFException();
      }
      limit += bytesRead;
      size += bytesRead;
      byteCount -= bytesRead;
    }
  }
  




  public final long completeSegmentByteCount()
  {
    long result = size;
    if (result == 0L) { return 0L;
    }
    
    Segment tail = head.prev;
    if ((limit < 8192) && (owner)) {
      result -= limit - pos;
    }
    
    return result;
  }
  
  public byte readByte() {
    if (size == 0L) { throw new IllegalStateException("size == 0");
    }
    Segment segment = head;
    int pos = pos;
    int limit = limit;
    
    byte[] data = data;
    byte b = data[(pos++)];
    size -= 1L;
    
    if (pos == limit) {
      head = segment.pop();
      SegmentPool.recycle(segment);
    } else {
      pos = pos;
    }
    
    return b;
  }
  
  public final byte getByte(long pos)
  {
    Util.checkOffsetAndCount(size, pos, 1L);
    if (size - pos > pos) {
      for (Segment s = head;; s = next) {
        int segmentByteCount = limit - pos;
        if (pos < segmentByteCount) return data[(pos + (int)pos)];
        pos -= segmentByteCount;
      }
    }
    pos -= size;
    for (Segment s = head.prev;; s = prev) {
      pos += limit - pos;
      if (pos >= 0L) return data[(pos + (int)pos)];
    }
  }
  
  public short readShort()
  {
    if (size < 2L) { throw new IllegalStateException("size < 2: " + size);
    }
    Segment segment = head;
    int pos = pos;
    int limit = limit;
    

    if (limit - pos < 2)
    {
      int s = (readByte() & 0xFF) << 8 | readByte() & 0xFF;
      return (short)s;
    }
    
    byte[] data = data;
    int s = (data[(pos++)] & 0xFF) << 8 | data[(pos++)] & 0xFF;
    
    size -= 2L;
    
    if (pos == limit) {
      head = segment.pop();
      SegmentPool.recycle(segment);
    } else {
      pos = pos;
    }
    
    return (short)s;
  }
  
  public int readInt() {
    if (size < 4L) { throw new IllegalStateException("size < 4: " + size);
    }
    Segment segment = head;
    int pos = pos;
    int limit = limit;
    

    if (limit - pos < 4) {
      return 
      

        (readByte() & 0xFF) << 24 | (readByte() & 0xFF) << 16 | (readByte() & 0xFF) << 8 | readByte() & 0xFF;
    }
    
    byte[] data = data;
    int i = (data[(pos++)] & 0xFF) << 24 | (data[(pos++)] & 0xFF) << 16 | (data[(pos++)] & 0xFF) << 8 | data[(pos++)] & 0xFF;
    


    size -= 4L;
    
    if (pos == limit) {
      head = segment.pop();
      SegmentPool.recycle(segment);
    } else {
      pos = pos;
    }
    
    return i;
  }
  
  public long readLong() {
    if (size < 8L) { throw new IllegalStateException("size < 8: " + size);
    }
    Segment segment = head;
    int pos = pos;
    int limit = limit;
    

    if (limit - pos < 8) {
      return 
        (readInt() & 0xFFFFFFFF) << 32 | readInt() & 0xFFFFFFFF;
    }
    
    byte[] data = data;
    long v = (data[(pos++)] & 0xFF) << 56 | (data[(pos++)] & 0xFF) << 48 | (data[(pos++)] & 0xFF) << 40 | (data[(pos++)] & 0xFF) << 32 | (data[(pos++)] & 0xFF) << 24 | (data[(pos++)] & 0xFF) << 16 | (data[(pos++)] & 0xFF) << 8 | data[(pos++)] & 0xFF;
    






    size -= 8L;
    
    if (pos == limit) {
      head = segment.pop();
      SegmentPool.recycle(segment);
    } else {
      pos = pos;
    }
    
    return v;
  }
  
  public short readShortLe() {
    return Util.reverseBytesShort(readShort());
  }
  
  public int readIntLe() {
    return Util.reverseBytesInt(readInt());
  }
  
  public long readLongLe() {
    return Util.reverseBytesLong(readLong());
  }
  
  public long readDecimalLong() {
    if (size == 0L) { throw new IllegalStateException("size == 0");
    }
    
    long value = 0L;
    int seen = 0;
    boolean negative = false;
    boolean done = false;
    
    long overflowZone = -922337203685477580L;
    long overflowDigit = -7L;
    do
    {
      Segment segment = head;
      
      byte[] data = data;
      int pos = pos;
      int limit = limit;
      for (; 
          pos < limit; seen++) {
        byte b = data[pos];
        if ((b >= 48) && (b <= 57)) {
          int digit = 48 - b;
          

          if ((value < overflowZone) || ((value == overflowZone) && (digit < overflowDigit))) {
            Buffer buffer = new Buffer().writeDecimalLong(value).writeByte(b);
            if (!negative) buffer.readByte();
            throw new NumberFormatException("Number too large: " + buffer.readUtf8());
          }
          value *= 10L;
          value += digit;
        } else if ((b == 45) && (seen == 0)) {
          negative = true;
          overflowDigit -= 1L;
        } else {
          if (seen == 0)
          {
            throw new NumberFormatException("Expected leading [0-9] or '-' character but was 0x" + Integer.toHexString(b));
          }
          
          done = true;
          break;
        }
        pos++;
      }
      
























      if (pos == limit) {
        head = segment.pop();
        SegmentPool.recycle(segment);
      } else {
        pos = pos;
      }
    } while ((!done) && (head != null));
    
    size -= seen;
    return negative ? value : -value;
  }
  
  public long readHexadecimalUnsignedLong() {
    if (size == 0L) { throw new IllegalStateException("size == 0");
    }
    long value = 0L;
    int seen = 0;
    boolean done = false;
    do
    {
      Segment segment = head;
      
      byte[] data = data;
      int pos = pos;
      int limit = limit;
      for (; 
          pos < limit; seen++)
      {

        byte b = data[pos];
        int digit; if ((b >= 48) && (b <= 57)) {
          digit = b - 48; } else { int digit;
          if ((b >= 97) && (b <= 102)) {
            digit = b - 97 + 10; } else { int digit;
            if ((b >= 65) && (b <= 70)) {
              digit = b - 65 + 10;
            } else {
              if (seen == 0)
              {
                throw new NumberFormatException("Expected leading [0-9a-fA-F] character but was 0x" + Integer.toHexString(b));
              }
              
              done = true;
              break;
            }
          } }
        int digit;
        if ((value & 0xF000000000000000) != 0L) {
          Buffer buffer = new Buffer().writeHexadecimalUnsignedLong(value).writeByte(b);
          throw new NumberFormatException("Number too large: " + buffer.readUtf8());
        }
        
        value <<= 4;
        value |= digit;pos++;
      }
      

      if (pos == limit) {
        head = segment.pop();
        SegmentPool.recycle(segment);
      } else {
        pos = pos;
      }
    } while ((!done) && (head != null));
    
    size -= seen;
    return value;
  }
  
  public ByteString readByteString() {
    return new ByteString(readByteArray());
  }
  
  public ByteString readByteString(long byteCount) throws EOFException {
    return new ByteString(readByteArray(byteCount));
  }
  
  public int select(Options options) {
    int index = selectPrefix(options, false);
    if (index == -1) { return -1;
    }
    
    int selectedSize = byteStrings[index].size();
    try {
      skip(selectedSize);
    } catch (EOFException e) {
      throw new AssertionError();
    }
    return index;
  }
  











  int selectPrefix(Options options, boolean selectTruncated)
  {
    Segment head = this.head;
    if (head == null) {
      if (selectTruncated) return -2;
      return options.indexOf(ByteString.EMPTY);
    }
    
    Segment s = head;
    byte[] data = data;
    int pos = pos;
    int limit = limit;
    
    int[] trie = trie;
    int triePos = 0;
    
    int prefixIndex = -1;
    
    for (;;)
    {
      int scanOrSelect = trie[(triePos++)];
      
      int possiblePrefixIndex = trie[(triePos++)];
      if (possiblePrefixIndex != -1) {
        prefixIndex = possiblePrefixIndex;
      }
      


      if (s == null) break;
      int nextStep;
      int nextStep; if (scanOrSelect < 0)
      {
        int scanByteCount = -1 * scanOrSelect;
        int trieLimit = triePos + scanByteCount;
        for (;;) {
          int b = data[(pos++)] & 0xFF;
          if (b != trie[(triePos++)]) return prefixIndex;
          boolean scanComplete = triePos == trieLimit;
          

          if (pos == limit) {
            s = next;
            pos = pos;
            data = data;
            limit = limit;
            if (s == head) {
              if (!scanComplete) break label354;
              s = null;
            }
          }
          
          if (scanComplete) {
            int nextStep = trie[triePos];
            break;
          }
        }
      }
      else {
        int selectChoiceCount = scanOrSelect;
        int b = data[(pos++)] & 0xFF;
        int selectLimit = triePos + selectChoiceCount;
        for (;;) {
          if (triePos == selectLimit) { return prefixIndex;
          }
          if (b == trie[triePos]) {
            int nextStep = trie[(triePos + selectChoiceCount)];
            break;
          }
          
          triePos++;
        }
        

        if (pos == limit) {
          s = next;
          pos = pos;
          data = data;
          limit = limit;
          if (s == head) {
            s = null;
          }
        }
      }
      
      if (nextStep >= 0) return nextStep;
      triePos = -nextStep;
    }
    
    label354:
    if (selectTruncated) return -2;
    return prefixIndex;
  }
  
  public void readFully(Buffer sink, long byteCount) throws EOFException {
    if (size < byteCount) {
      sink.write(this, size);
      throw new EOFException();
    }
    sink.write(this, byteCount);
  }
  
  public long readAll(Sink sink) throws IOException {
    long byteCount = size;
    if (byteCount > 0L) {
      sink.write(this, byteCount);
    }
    return byteCount;
  }
  
  public String readUtf8() {
    try {
      return readString(size, Util.UTF_8);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }
  
  public String readUtf8(long byteCount) throws EOFException {
    return readString(byteCount, Util.UTF_8);
  }
  
  public String readString(Charset charset) {
    try {
      return readString(size, charset);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }
  
  public String readString(long byteCount, Charset charset) throws EOFException {
    Util.checkOffsetAndCount(size, 0L, byteCount);
    if (charset == null) throw new IllegalArgumentException("charset == null");
    if (byteCount > 2147483647L) {
      throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
    }
    if (byteCount == 0L) { return "";
    }
    Segment s = head;
    if (pos + byteCount > limit)
    {
      return new String(readByteArray(byteCount), charset);
    }
    
    String result = new String(data, pos, (int)byteCount, charset); Segment 
      tmp129_127 = s;129127pos = ((int)(129127pos + byteCount));
    size -= byteCount;
    
    if (pos == limit) {
      head = s.pop();
      SegmentPool.recycle(s);
    }
    
    return tmp129_127;
  }
  
  @Nullable
  public String readUtf8Line() throws EOFException { long newline = indexOf((byte)10);
    
    if (newline == -1L) {
      return size != 0L ? readUtf8(size) : null;
    }
    
    return readUtf8Line(newline);
  }
  
  public String readUtf8LineStrict() throws EOFException {
    return readUtf8LineStrict(Long.MAX_VALUE);
  }
  
  public String readUtf8LineStrict(long limit) throws EOFException {
    if (limit < 0L) throw new IllegalArgumentException("limit < 0: " + limit);
    long scanLength = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1L;
    long newline = indexOf((byte)10, 0L, scanLength);
    if (newline != -1L) return readUtf8Line(newline);
    if ((scanLength < size()) && 
      (getByte(scanLength - 1L) == 13) && (getByte(scanLength) == 10)) {
      return readUtf8Line(scanLength);
    }
    Buffer data = new Buffer();
    copyTo(data, 0L, Math.min(32L, size()));
    
    throw new EOFException("\\n not found: limit=" + Math.min(size(), limit) + " content=" + data.readByteString().hex() + '…');
  }
  
  String readUtf8Line(long newline) throws EOFException {
    if ((newline > 0L) && (getByte(newline - 1L) == 13))
    {
      String result = readUtf8(newline - 1L);
      skip(2L);
      return result;
    }
    

    String result = readUtf8(newline);
    skip(1L);
    return result;
  }
  
  public int readUtf8CodePoint() throws EOFException
  {
    if (size == 0L) { throw new EOFException();
    }
    byte b0 = getByte(0L);
    

    int min;
    
    if ((b0 & 0x80) == 0)
    {
      int codePoint = b0 & 0x7F;
      int byteCount = 1;
      min = 0;
    } else { int min;
      if ((b0 & 0xE0) == 192)
      {
        int codePoint = b0 & 0x1F;
        int byteCount = 2;
        min = 128;
      } else { int min;
        if ((b0 & 0xF0) == 224)
        {
          int codePoint = b0 & 0xF;
          int byteCount = 3;
          min = 2048;
        } else { int min;
          if ((b0 & 0xF8) == 240)
          {
            int codePoint = b0 & 0x7;
            int byteCount = 4;
            min = 65536;
          }
          else
          {
            skip(1L);
            return 65533; } } } }
    int min;
    int byteCount;
    int codePoint; if (size < byteCount)
    {
      throw new EOFException("size < " + byteCount + ": " + size + " (to read code point prefixed 0x" + Integer.toHexString(b0) + ")");
    }
    



    for (int i = 1; i < byteCount; i++) {
      byte b = getByte(i);
      if ((b & 0xC0) == 128)
      {
        codePoint <<= 6;
        codePoint |= b & 0x3F;
      } else {
        skip(i);
        return 65533;
      }
    }
    
    skip(byteCount);
    
    if (codePoint > 1114111) {
      return 65533;
    }
    
    if ((codePoint >= 55296) && (codePoint <= 57343)) {
      return 65533;
    }
    
    if (codePoint < min) {
      return 65533;
    }
    
    return codePoint;
  }
  
  public byte[] readByteArray() {
    try {
      return readByteArray(size);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }
  
  public byte[] readByteArray(long byteCount) throws EOFException {
    Util.checkOffsetAndCount(size, 0L, byteCount);
    if (byteCount > 2147483647L) {
      throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
    }
    
    byte[] result = new byte[(int)byteCount];
    readFully(result);
    return result;
  }
  
  public int read(byte[] sink) {
    return read(sink, 0, sink.length);
  }
  
  public void readFully(byte[] sink) throws EOFException {
    int offset = 0;
    while (offset < sink.length) {
      int read = read(sink, offset, sink.length - offset);
      if (read == -1) throw new EOFException();
      offset += read;
    }
  }
  
  public int read(byte[] sink, int offset, int byteCount) {
    Util.checkOffsetAndCount(sink.length, offset, byteCount);
    
    Segment s = head;
    if (s == null) return -1;
    int toCopy = Math.min(byteCount, limit - pos);
    System.arraycopy(data, pos, sink, offset, toCopy);
    
    pos += toCopy;
    size -= toCopy;
    
    if (pos == limit) {
      head = s.pop();
      SegmentPool.recycle(s);
    }
    
    return toCopy;
  }
  
  public int read(ByteBuffer sink) throws IOException {
    Segment s = head;
    if (s == null) { return -1;
    }
    int toCopy = Math.min(sink.remaining(), limit - pos);
    sink.put(data, pos, toCopy);
    
    pos += toCopy;
    size -= toCopy;
    
    if (pos == limit) {
      head = s.pop();
      SegmentPool.recycle(s);
    }
    
    return toCopy;
  }
  


  public final void clear()
  {
    try
    {
      skip(size);
    } catch (EOFException e) {
      throw new AssertionError(e);
    }
  }
  
  public void skip(long byteCount) throws EOFException
  {
    while (byteCount > 0L) {
      if (head == null) { throw new EOFException();
      }
      int toSkip = (int)Math.min(byteCount, head.limit - head.pos);
      size -= toSkip;
      byteCount -= toSkip;
      head.pos += toSkip;
      
      if (head.pos == head.limit) {
        Segment toRecycle = head;
        head = toRecycle.pop();
        SegmentPool.recycle(toRecycle);
      }
    }
  }
  
  public Buffer write(ByteString byteString) {
    if (byteString == null) throw new IllegalArgumentException("byteString == null");
    byteString.write(this);
    return this;
  }
  
  public Buffer writeUtf8(String string) {
    return writeUtf8(string, 0, string.length());
  }
  
  public Buffer writeUtf8(String string, int beginIndex, int endIndex) {
    if (string == null) throw new IllegalArgumentException("string == null");
    if (beginIndex < 0) throw new IllegalArgumentException("beginIndex < 0: " + beginIndex);
    if (endIndex < beginIndex) {
      throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
    }
    if (endIndex > string.length())
    {
      throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
    }
    

    for (int i = beginIndex; i < endIndex;) {
      int c = string.charAt(i);
      
      if (c < 128) {
        Segment tail = writableSegment(1);
        byte[] data = data;
        int segmentOffset = limit - i;
        int runLimit = Math.min(endIndex, 8192 - segmentOffset);
        

        data[(segmentOffset + i++)] = ((byte)c);
        


        while (i < runLimit) {
          c = string.charAt(i);
          if (c >= 128) break;
          data[(segmentOffset + i++)] = ((byte)c);
        }
        
        int runSize = i + segmentOffset - limit;
        limit += runSize;
        size += runSize;
      }
      else if (c < 2048)
      {
        writeByte(c >> 6 | 0xC0);
        writeByte(c & 0x3F | 0x80);
        i++;
      }
      else if ((c < 55296) || (c > 57343))
      {
        writeByte(c >> 12 | 0xE0);
        writeByte(c >> 6 & 0x3F | 0x80);
        writeByte(c & 0x3F | 0x80);
        i++;

      }
      else
      {
        int low = i + 1 < endIndex ? string.charAt(i + 1) : 0;
        if ((c > 56319) || (low < 56320) || (low > 57343)) {
          writeByte(63);
          i++;


        }
        else
        {

          int codePoint = 65536 + ((c & 0xFFFF27FF) << 10 | low & 0xFFFF23FF);
          

          writeByte(codePoint >> 18 | 0xF0);
          writeByte(codePoint >> 12 & 0x3F | 0x80);
          writeByte(codePoint >> 6 & 0x3F | 0x80);
          writeByte(codePoint & 0x3F | 0x80);
          i += 2;
        }
      }
    }
    return this;
  }
  
  public Buffer writeUtf8CodePoint(int codePoint) {
    if (codePoint < 128)
    {
      writeByte(codePoint);
    }
    else if (codePoint < 2048)
    {
      writeByte(codePoint >> 6 | 0xC0);
      writeByte(codePoint & 0x3F | 0x80);
    }
    else if (codePoint < 65536) {
      if ((codePoint >= 55296) && (codePoint <= 57343))
      {
        writeByte(63);
      }
      else {
        writeByte(codePoint >> 12 | 0xE0);
        writeByte(codePoint >> 6 & 0x3F | 0x80);
        writeByte(codePoint & 0x3F | 0x80);
      }
    }
    else if (codePoint <= 1114111)
    {
      writeByte(codePoint >> 18 | 0xF0);
      writeByte(codePoint >> 12 & 0x3F | 0x80);
      writeByte(codePoint >> 6 & 0x3F | 0x80);
      writeByte(codePoint & 0x3F | 0x80);
    }
    else
    {
      throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(codePoint));
    }
    
    return this;
  }
  
  public Buffer writeString(String string, Charset charset) {
    return writeString(string, 0, string.length(), charset);
  }
  
  public Buffer writeString(String string, int beginIndex, int endIndex, Charset charset)
  {
    if (string == null) throw new IllegalArgumentException("string == null");
    if (beginIndex < 0) throw new IllegalAccessError("beginIndex < 0: " + beginIndex);
    if (endIndex < beginIndex) {
      throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
    }
    if (endIndex > string.length())
    {
      throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
    }
    if (charset == null) throw new IllegalArgumentException("charset == null");
    if (charset.equals(Util.UTF_8)) return writeUtf8(string, beginIndex, endIndex);
    byte[] data = string.substring(beginIndex, endIndex).getBytes(charset);
    return write(data, 0, data.length);
  }
  
  public Buffer write(byte[] source) {
    if (source == null) throw new IllegalArgumentException("source == null");
    return write(source, 0, source.length);
  }
  
  public Buffer write(byte[] source, int offset, int byteCount) {
    if (source == null) throw new IllegalArgumentException("source == null");
    Util.checkOffsetAndCount(source.length, offset, byteCount);
    
    int limit = offset + byteCount;
    while (offset < limit) {
      Segment tail = writableSegment(1);
      
      int toCopy = Math.min(limit - offset, 8192 - limit);
      System.arraycopy(source, offset, data, limit, toCopy);
      
      offset += toCopy;
      limit += toCopy;
    }
    
    size += byteCount;
    return this;
  }
  
  public int write(ByteBuffer source) throws IOException {
    if (source == null) { throw new IllegalArgumentException("source == null");
    }
    int byteCount = source.remaining();
    int remaining = byteCount;
    while (remaining > 0) {
      Segment tail = writableSegment(1);
      
      int toCopy = Math.min(remaining, 8192 - limit);
      source.get(data, limit, toCopy);
      
      remaining -= toCopy;
      limit += toCopy;
    }
    
    size += byteCount;
    return byteCount;
  }
  
  public long writeAll(Source source) throws IOException {
    if (source == null) throw new IllegalArgumentException("source == null");
    long totalBytesRead = 0L;
    long readCount; while ((readCount = source.read(this, 8192L)) != -1L) {
      totalBytesRead += readCount;
    }
    return totalBytesRead;
  }
  
  public BufferedSink write(Source source, long byteCount) throws IOException {
    while (byteCount > 0L) {
      long read = source.read(this, byteCount);
      if (read == -1L) throw new EOFException();
      byteCount -= read;
    }
    return this;
  }
  
  public Buffer writeByte(int b) {
    Segment tail = writableSegment(1);
    data[(limit++)] = ((byte)b);
    size += 1L;
    return this;
  }
  
  public Buffer writeShort(int s) {
    Segment tail = writableSegment(2);
    byte[] data = data;
    int limit = limit;
    data[(limit++)] = ((byte)(s >>> 8 & 0xFF));
    data[(limit++)] = ((byte)(s & 0xFF));
    limit = limit;
    size += 2L;
    return this;
  }
  
  public Buffer writeShortLe(int s) {
    return writeShort(Util.reverseBytesShort((short)s));
  }
  
  public Buffer writeInt(int i) {
    Segment tail = writableSegment(4);
    byte[] data = data;
    int limit = limit;
    data[(limit++)] = ((byte)(i >>> 24 & 0xFF));
    data[(limit++)] = ((byte)(i >>> 16 & 0xFF));
    data[(limit++)] = ((byte)(i >>> 8 & 0xFF));
    data[(limit++)] = ((byte)(i & 0xFF));
    limit = limit;
    size += 4L;
    return this;
  }
  
  public Buffer writeIntLe(int i) {
    return writeInt(Util.reverseBytesInt(i));
  }
  
  public Buffer writeLong(long v) {
    Segment tail = writableSegment(8);
    byte[] data = data;
    int limit = limit;
    data[(limit++)] = ((byte)(int)(v >>> 56 & 0xFF));
    data[(limit++)] = ((byte)(int)(v >>> 48 & 0xFF));
    data[(limit++)] = ((byte)(int)(v >>> 40 & 0xFF));
    data[(limit++)] = ((byte)(int)(v >>> 32 & 0xFF));
    data[(limit++)] = ((byte)(int)(v >>> 24 & 0xFF));
    data[(limit++)] = ((byte)(int)(v >>> 16 & 0xFF));
    data[(limit++)] = ((byte)(int)(v >>> 8 & 0xFF));
    data[(limit++)] = ((byte)(int)(v & 0xFF));
    limit = limit;
    size += 8L;
    return this;
  }
  
  public Buffer writeLongLe(long v) {
    return writeLong(Util.reverseBytesLong(v));
  }
  
  public Buffer writeDecimalLong(long v) {
    if (v == 0L)
    {
      return writeByte(48);
    }
    
    boolean negative = false;
    if (v < 0L) {
      v = -v;
      if (v < 0L) {
        return writeUtf8("-9223372036854775808");
      }
      negative = true;
    }
    



















    int width = v < 1000000000000000000L ? 18 : v < 100000000000000000L ? 17 : v < 10000000000000000L ? 16 : v < 1000000000000000L ? 15 : v < 100000000000000L ? 14 : v < 10000000000000L ? 13 : v < 1000000000000L ? 12 : v < 100000000000L ? 11 : v < 10000000000L ? 10 : v < 1000000000L ? 9 : v < 100000000L ? 8 : v < 10000000L ? 7 : v < 1000000L ? 6 : v < 100000L ? 5 : v < 10000L ? 4 : v < 1000L ? 3 : v < 100L ? 2 : v < 10L ? 1 : 19;
    if (negative) {
      width++;
    }
    
    Segment tail = writableSegment(width);
    byte[] data = data;
    int pos = limit + width;
    while (v != 0L) {
      int digit = (int)(v % 10L);
      data[(--pos)] = DIGITS[digit];
      v /= 10L;
    }
    if (negative) {
      data[(--pos)] = 45;
    }
    
    limit += width;
    size += width;
    return this;
  }
  
  public Buffer writeHexadecimalUnsignedLong(long v) {
    if (v == 0L)
    {
      return writeByte(48);
    }
    
    int width = Long.numberOfTrailingZeros(Long.highestOneBit(v)) / 4 + 1;
    
    Segment tail = writableSegment(width);
    byte[] data = data;
    int pos = limit + width - 1; for (int start = limit; pos >= start; pos--) {
      data[pos] = DIGITS[((int)(v & 0xF))];
      v >>>= 4;
    }
    limit += width;
    size += width;
    return this;
  }
  



  Segment writableSegment(int minimumCapacity)
  {
    if ((minimumCapacity < 1) || (minimumCapacity > 8192)) { throw new IllegalArgumentException();
    }
    if (head == null) {
      head = SegmentPool.take();
      return head.next = head.prev = head;
    }
    
    Segment tail = head.prev;
    if ((limit + minimumCapacity > 8192) || (!owner)) {
      tail = tail.push(SegmentPool.take());
    }
    return tail;
  }
  

















































  public void write(Buffer source, long byteCount)
  {
    if (source == null) throw new IllegalArgumentException("source == null");
    if (source == this) throw new IllegalArgumentException("source == this");
    Util.checkOffsetAndCount(size, 0L, byteCount);
    
    while (byteCount > 0L)
    {
      if (byteCount < head.limit - head.pos) {
        Segment tail = head != null ? head.prev : null;
        if ((tail != null) && (owner)) {
          if (byteCount + limit - (shared ? 0 : pos) <= 8192L)
          {
            head.writeTo(tail, (int)byteCount);
            size -= byteCount;
            size += byteCount;
            return;
          }
        }
        
        head = head.split((int)byteCount);
      }
      


      Segment segmentToMove = head;
      long movedByteCount = limit - pos;
      head = segmentToMove.pop();
      if (head == null) {
        head = segmentToMove;
        head.next = (head.prev = head);
      } else {
        Segment tail = head.prev;
        tail = tail.push(segmentToMove);
        tail.compact();
      }
      size -= movedByteCount;
      size += movedByteCount;
      byteCount -= movedByteCount;
    }
  }
  
  public long read(Buffer sink, long byteCount) {
    if (sink == null) throw new IllegalArgumentException("sink == null");
    if (byteCount < 0L) throw new IllegalArgumentException("byteCount < 0: " + byteCount);
    if (size == 0L) return -1L;
    if (byteCount > size) byteCount = size;
    sink.write(this, byteCount);
    return byteCount;
  }
  
  public long indexOf(byte b) {
    return indexOf(b, 0L, Long.MAX_VALUE);
  }
  



  public long indexOf(byte b, long fromIndex)
  {
    return indexOf(b, fromIndex, Long.MAX_VALUE);
  }
  
  public long indexOf(byte b, long fromIndex, long toIndex) {
    if ((fromIndex < 0L) || (toIndex < fromIndex))
    {
      throw new IllegalArgumentException(String.format("size=%s fromIndex=%s toIndex=%s", new Object[] {Long.valueOf(size), Long.valueOf(fromIndex), Long.valueOf(toIndex) }));
    }
    
    if (toIndex > size) toIndex = size;
    if (fromIndex == toIndex) { return -1L;
    }
    





    Segment s = head;
    if (s == null)
    {
      return -1L; }
    if (size - fromIndex < fromIndex)
    {
      long offset = size;
      while (offset > fromIndex) {
        s = prev;
        offset -= limit - pos;
      }
    }
    
    long offset = 0L;
    long nextOffset; while ((nextOffset = offset + (limit - pos)) < fromIndex) {
      s = next;
      offset = nextOffset;
    }
    



    while (offset < toIndex) {
      byte[] data = data;
      int limit = (int)Math.min(limit, pos + toIndex - offset);
      for (int pos = (int)(pos + fromIndex - offset); 
          pos < limit; pos++) {
        if (data[pos] == b) {
          return pos - pos + offset;
        }
      }
      

      offset += limit - pos;
      fromIndex = offset;
      s = next;
    }
    
    return -1L;
  }
  
  public long indexOf(ByteString bytes) throws IOException {
    return indexOf(bytes, 0L);
  }
  
  public long indexOf(ByteString bytes, long fromIndex) throws IOException {
    if (bytes.size() == 0) throw new IllegalArgumentException("bytes is empty");
    if (fromIndex < 0L) { throw new IllegalArgumentException("fromIndex < 0");
    }
    





    Segment s = head;
    if (s == null)
    {
      return -1L; }
    if (size - fromIndex < fromIndex)
    {
      long offset = size;
      while (offset > fromIndex) {
        s = prev;
        offset -= limit - pos;
      }
    }
    
    long offset = 0L;
    long nextOffset; while ((nextOffset = offset + (limit - pos)) < fromIndex) {
      s = next;
      offset = nextOffset;
    }
    




    byte b0 = bytes.getByte(0);
    int bytesSize = bytes.size();
    long resultLimit = size - bytesSize + 1L;
    while (offset < resultLimit)
    {
      byte[] data = data;
      int segmentLimit = (int)Math.min(limit, pos + resultLimit - offset);
      for (int pos = (int)(pos + fromIndex - offset); pos < segmentLimit; pos++) {
        if ((data[pos] == b0) && (rangeEquals(s, pos + 1, bytes, 1, bytesSize))) {
          return pos - pos + offset;
        }
      }
      

      offset += limit - pos;
      fromIndex = offset;
      s = next;
    }
    
    return -1L;
  }
  
  public long indexOfElement(ByteString targetBytes) {
    return indexOfElement(targetBytes, 0L);
  }
  
  public long indexOfElement(ByteString targetBytes, long fromIndex) {
    if (fromIndex < 0L) { throw new IllegalArgumentException("fromIndex < 0");
    }
    





    Segment s = head;
    if (s == null)
    {
      return -1L; }
    if (size - fromIndex < fromIndex)
    {
      long offset = size;
      while (offset > fromIndex) {
        s = prev;
        offset -= limit - pos;
      }
    }
    
    long offset = 0L;
    long nextOffset; while ((nextOffset = offset + (limit - pos)) < fromIndex) {
      s = next;
      offset = nextOffset;
    }
    


    int b;
    

    if (targetBytes.size() == 2)
    {
      byte b0 = targetBytes.getByte(0);
      byte b1 = targetBytes.getByte(1);
      while (offset < size) {
        byte[] data = data;
        int pos = (int)(pos + fromIndex - offset); for (int limit = limit; pos < limit; pos++) {
          b = data[pos];
          if ((b == b0) || (b == b1)) {
            return pos - pos + offset;
          }
        }
        

        offset += limit - pos;
        fromIndex = offset;
        s = next;
      }
    }
    else {
      byte[] targetByteArray = targetBytes.internalArray();
      while (offset < size) {
        byte[] data = data;
        int pos = (int)(pos + fromIndex - offset); for (int limit = limit; pos < limit; pos++) {
          int b = data[pos];
          for (byte t : targetByteArray) {
            if (b == t) { return pos - pos + offset;
            }
          }
        }
        
        offset += limit - pos;
        fromIndex = offset;
        s = next;
      }
    }
    
    return -1L;
  }
  
  public boolean rangeEquals(long offset, ByteString bytes) {
    return rangeEquals(offset, bytes, 0, bytes.size());
  }
  
  public boolean rangeEquals(long offset, ByteString bytes, int bytesOffset, int byteCount)
  {
    if ((offset < 0L) || (bytesOffset < 0) || (byteCount < 0) || (size - offset < byteCount) || 
    


      (bytes.size() - bytesOffset < byteCount)) {
      return false;
    }
    for (int i = 0; i < byteCount; i++) {
      if (getByte(offset + i) != bytes.getByte(bytesOffset + i)) {
        return false;
      }
    }
    return true;
  }
  




  private boolean rangeEquals(Segment segment, int segmentPos, ByteString bytes, int bytesOffset, int bytesLimit)
  {
    int segmentLimit = limit;
    byte[] data = data;
    
    for (int i = bytesOffset; i < bytesLimit;) {
      if (segmentPos == segmentLimit) {
        segment = next;
        data = data;
        segmentPos = pos;
        segmentLimit = limit;
      }
      
      if (data[segmentPos] != bytes.getByte(i)) {
        return false;
      }
      
      segmentPos++;
      i++;
    }
    
    return true;
  }
  
  public void flush() {}
  
  public boolean isOpen()
  {
    return true;
  }
  
  public void close() {}
  
  public Timeout timeout()
  {
    return Timeout.NONE;
  }
  
  List<Integer> segmentSizes()
  {
    if (head == null) return Collections.emptyList();
    List<Integer> result = new ArrayList();
    result.add(Integer.valueOf(head.limit - head.pos));
    for (Segment s = head.next; s != head; s = next) {
      result.add(Integer.valueOf(limit - pos));
    }
    return result;
  }
  
  public final ByteString md5()
  {
    return digest("MD5");
  }
  
  public final ByteString sha1()
  {
    return digest("SHA-1");
  }
  
  public final ByteString sha256()
  {
    return digest("SHA-256");
  }
  
  public final ByteString sha512()
  {
    return digest("SHA-512");
  }
  
  private ByteString digest(String algorithm) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      if (head != null) {
        messageDigest.update(head.data, head.pos, head.limit - head.pos);
        for (Segment s = head.next; s != head; s = next) {
          messageDigest.update(data, pos, limit - pos);
        }
      }
      return ByteString.of(messageDigest.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    }
  }
  
  public final ByteString hmacSha1(ByteString key)
  {
    return hmac("HmacSHA1", key);
  }
  
  public final ByteString hmacSha256(ByteString key)
  {
    return hmac("HmacSHA256", key);
  }
  
  public final ByteString hmacSha512(ByteString key)
  {
    return hmac("HmacSHA512", key);
  }
  
  private ByteString hmac(String algorithm, ByteString key) {
    try {
      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
      if (head != null) {
        mac.update(head.data, head.pos, head.limit - head.pos);
        for (Segment s = head.next; s != head; s = next) {
          mac.update(data, pos, limit - pos);
        }
      }
      return ByteString.of(mac.doFinal());
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError();
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Buffer)) return false;
    Buffer that = (Buffer)o;
    if (size != size) return false;
    if (size == 0L) { return true;
    }
    Segment sa = head;
    Segment sb = head;
    int posA = pos;
    int posB = pos;
    long count;
    for (long pos = 0L; pos < size; pos += count) {
      count = Math.min(limit - posA, limit - posB);
      
      for (int i = 0; i < count; i++) {
        if (data[(posA++)] != data[(posB++)]) { return false;
        }
      }
      if (posA == limit) {
        sa = next;
        posA = pos;
      }
      
      if (posB == limit) {
        sb = next;
        posB = pos;
      }
    }
    
    return true;
  }
  
  public int hashCode() {
    Segment s = head;
    if (s == null) return 0;
    int result = 1;
    do {
      int pos = pos; for (int limit = limit; pos < limit; pos++) {
        result = 31 * result + data[pos];
      }
      s = next;
    } while (s != head);
    return result;
  }
  



  public String toString()
  {
    return snapshot().toString();
  }
  
  public Buffer clone()
  {
    Buffer result = new Buffer();
    if (size == 0L) { return result;
    }
    head = head.sharedCopy();
    head.next = (head.prev = head);
    for (Segment s = head.next; s != head; s = next) {
      head.prev.push(s.sharedCopy());
    }
    size = size;
    return result;
  }
  
  public final ByteString snapshot()
  {
    if (size > 2147483647L) {
      throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + size);
    }
    return snapshot((int)size);
  }
  


  public final ByteString snapshot(int byteCount)
  {
    if (byteCount == 0) return ByteString.EMPTY;
    return new SegmentedByteString(this, byteCount);
  }
  
  public final UnsafeCursor readUnsafe() {
    return readUnsafe(new UnsafeCursor());
  }
  
  public final UnsafeCursor readUnsafe(UnsafeCursor unsafeCursor) {
    if (buffer != null) {
      throw new IllegalStateException("already attached to a buffer");
    }
    
    buffer = this;
    readWrite = false;
    return unsafeCursor;
  }
  
  public final UnsafeCursor readAndWriteUnsafe() {
    return readAndWriteUnsafe(new UnsafeCursor());
  }
  
  public final UnsafeCursor readAndWriteUnsafe(UnsafeCursor unsafeCursor) {
    if (buffer != null) {
      throw new IllegalStateException("already attached to a buffer");
    }
    
    buffer = this;
    readWrite = true;
    return unsafeCursor;
  }
  


















































  public static final class UnsafeCursor
    implements Closeable
  {
    public Buffer buffer;
    

















































    public boolean readWrite;
    

















































    private Segment segment;
    
















































    public long offset = -1L;
    public byte[] data;
    public int start = -1;
    public int end = -1;
    

    public UnsafeCursor() {}
    

    public final int next()
    {
      if (offset == buffer.size) throw new IllegalStateException();
      if (offset == -1L) return seek(0L);
      return seek(offset + (end - start));
    }
    




    public final int seek(long offset)
    {
      if ((offset < -1L) || (offset > buffer.size))
      {
        throw new ArrayIndexOutOfBoundsException(String.format("offset=%s > size=%s", new Object[] {Long.valueOf(offset), Long.valueOf(buffer.size) }));
      }
      
      if ((offset == -1L) || (offset == buffer.size)) {
        segment = null;
        this.offset = offset;
        data = null;
        start = -1;
        end = -1;
        return -1;
      }
      

      long min = 0L;
      long max = buffer.size;
      Segment head = buffer.head;
      Segment tail = buffer.head;
      if (segment != null) {
        long segmentOffset = this.offset - (start - segment.pos);
        if (segmentOffset > offset)
        {
          max = segmentOffset;
          tail = segment;
        }
        else {
          min = segmentOffset;
          head = segment;
        }
      }
      


      if (max - offset > offset - min)
      {
        Segment next = head;
        long nextOffset = min;
        while (offset >= nextOffset + (limit - pos)) {
          nextOffset += limit - pos;
          next = next;
        }
      }
      
      Segment next = tail;
      long nextOffset = max;
      while (nextOffset > offset) {
        next = prev;
        nextOffset -= limit - pos;
      }
      


      if ((readWrite) && (shared)) {
        Segment unsharedNext = next.unsharedCopy();
        if (buffer.head == next) {
          buffer.head = unsharedNext;
        }
        next = next.push(unsharedNext);
        prev.pop();
      }
      

      segment = next;
      this.offset = offset;
      data = data;
      start = (pos + (int)(offset - nextOffset));
      end = limit;
      return end - start;
    }
    
















    public final long resizeBuffer(long newSize)
    {
      if (buffer == null) {
        throw new IllegalStateException("not attached to a buffer");
      }
      if (!readWrite) {
        throw new IllegalStateException("resizeBuffer() only permitted for read/write buffers");
      }
      
      long oldSize = buffer.size;
      boolean needsToSeek; long bytesToAdd; if (newSize <= oldSize) {
        if (newSize < 0L) {
          throw new IllegalArgumentException("newSize < 0: " + newSize);
        }
        
        for (long bytesToSubtract = oldSize - newSize; bytesToSubtract > 0L;) {
          Segment tail = buffer.head.prev;
          int tailSize = limit - pos;
          if (tailSize <= bytesToSubtract) {
            buffer.head = tail.pop();
            SegmentPool.recycle(tail);
            bytesToSubtract -= tailSize;
          } else {
            Segment tmp157_155 = tail;157155limit = ((int)(157155limit - bytesToSubtract));
            break;
          }
        }
        
        segment = null;
        offset = newSize;
        data = null;
        start = -1;
        end = -1;
      } else if (newSize > oldSize)
      {
        needsToSeek = true;
        for (bytesToAdd = newSize - oldSize; bytesToAdd > 0L;) {
          Segment tail = buffer.writableSegment(1);
          int segmentBytesToAdd = (int)Math.min(bytesToAdd, 8192 - limit);
          limit += segmentBytesToAdd;
          bytesToAdd -= segmentBytesToAdd;
          

          if (needsToSeek) {
            segment = tail;
            offset = oldSize;
            data = data;
            start = (limit - segmentBytesToAdd);
            end = limit;
            needsToSeek = false;
          }
        }
      }
      
      buffer.size = newSize;
      
      return oldSize;
    }
    






















    public final long expandBuffer(int minByteCount)
    {
      if (minByteCount <= 0) {
        throw new IllegalArgumentException("minByteCount <= 0: " + minByteCount);
      }
      if (minByteCount > 8192) {
        throw new IllegalArgumentException("minByteCount > Segment.SIZE: " + minByteCount);
      }
      if (buffer == null) {
        throw new IllegalStateException("not attached to a buffer");
      }
      if (!readWrite) {
        throw new IllegalStateException("expandBuffer() only permitted for read/write buffers");
      }
      
      long oldSize = buffer.size;
      Segment tail = buffer.writableSegment(minByteCount);
      int result = 8192 - limit;
      limit = 8192;
      buffer.size = (oldSize + result);
      

      segment = tail;
      offset = oldSize;
      data = data;
      start = (8192 - result);
      end = 8192;
      
      return result;
    }
    
    public void close()
    {
      if (buffer == null) {
        throw new IllegalStateException("not attached to a buffer");
      }
      
      buffer = null;
      segment = null;
      offset = -1L;
      data = null;
      start = -1;
      end = -1;
    }
  }
}
