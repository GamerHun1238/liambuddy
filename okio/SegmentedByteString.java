package okio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;









































final class SegmentedByteString
  extends ByteString
{
  final transient byte[][] segments;
  final transient int[] directory;
  
  SegmentedByteString(Buffer buffer, int byteCount)
  {
    super(null);
    Util.checkOffsetAndCount(size, 0L, byteCount);
    

    int offset = 0;
    int segmentCount = 0;
    for (Segment s = head; offset < byteCount; s = next) {
      if (limit == pos) {
        throw new AssertionError("s.limit == s.pos");
      }
      offset += limit - pos;
      segmentCount++;
    }
    

    segments = new byte[segmentCount][];
    directory = new int[segmentCount * 2];
    offset = 0;
    segmentCount = 0;
    for (Segment s = head; offset < byteCount; s = next) {
      segments[segmentCount] = data;
      offset += limit - pos;
      if (offset > byteCount) {
        offset = byteCount;
      }
      directory[segmentCount] = offset;
      directory[(segmentCount + segments.length)] = pos;
      shared = true;
      segmentCount++;
    }
  }
  
  public String utf8() {
    return toByteString().utf8();
  }
  
  public String string(Charset charset) {
    return toByteString().string(charset);
  }
  
  public String base64() {
    return toByteString().base64();
  }
  
  public String hex() {
    return toByteString().hex();
  }
  
  public ByteString toAsciiLowercase() {
    return toByteString().toAsciiLowercase();
  }
  
  public ByteString toAsciiUppercase() {
    return toByteString().toAsciiUppercase();
  }
  
  public ByteString md5() {
    return toByteString().md5();
  }
  
  public ByteString sha1() {
    return toByteString().sha1();
  }
  
  public ByteString sha256() {
    return toByteString().sha256();
  }
  
  public ByteString hmacSha1(ByteString key) {
    return toByteString().hmacSha1(key);
  }
  
  public ByteString hmacSha256(ByteString key) {
    return toByteString().hmacSha256(key);
  }
  
  public String base64Url() {
    return toByteString().base64Url();
  }
  
  public ByteString substring(int beginIndex) {
    return toByteString().substring(beginIndex);
  }
  
  public ByteString substring(int beginIndex, int endIndex) {
    return toByteString().substring(beginIndex, endIndex);
  }
  
  public byte getByte(int pos) {
    Util.checkOffsetAndCount(directory[(segments.length - 1)], pos, 1L);
    int segment = segment(pos);
    int segmentOffset = segment == 0 ? 0 : directory[(segment - 1)];
    int segmentPos = directory[(segment + segments.length)];
    return segments[segment][(pos - segmentOffset + segmentPos)];
  }
  

  private int segment(int pos)
  {
    int i = Arrays.binarySearch(directory, 0, segments.length, pos + 1);
    return i >= 0 ? i : i ^ 0xFFFFFFFF;
  }
  
  public int size() {
    return directory[(segments.length - 1)];
  }
  
  public byte[] toByteArray() {
    byte[] result = new byte[directory[(segments.length - 1)]];
    int segmentOffset = 0;
    int s = 0; for (int segmentCount = segments.length; s < segmentCount; s++) {
      int segmentPos = directory[(segmentCount + s)];
      int nextSegmentOffset = directory[s];
      System.arraycopy(segments[s], segmentPos, result, segmentOffset, nextSegmentOffset - segmentOffset);
      
      segmentOffset = nextSegmentOffset;
    }
    return result;
  }
  
  public ByteBuffer asByteBuffer() {
    return ByteBuffer.wrap(toByteArray()).asReadOnlyBuffer();
  }
  
  public void write(OutputStream out) throws IOException {
    if (out == null) throw new IllegalArgumentException("out == null");
    int segmentOffset = 0;
    int s = 0; for (int segmentCount = segments.length; s < segmentCount; s++) {
      int segmentPos = directory[(segmentCount + s)];
      int nextSegmentOffset = directory[s];
      out.write(segments[s], segmentPos, nextSegmentOffset - segmentOffset);
      segmentOffset = nextSegmentOffset;
    }
  }
  
  void write(Buffer buffer) {
    int segmentOffset = 0;
    int s = 0; for (int segmentCount = segments.length; s < segmentCount; s++) {
      int segmentPos = directory[(segmentCount + s)];
      int nextSegmentOffset = directory[s];
      Segment segment = new Segment(segments[s], segmentPos, segmentPos + nextSegmentOffset - segmentOffset, true, false);
      
      if (head == null) {
        head = (segment.next = segment.prev = segment);
      } else {
        head.prev.push(segment);
      }
      segmentOffset = nextSegmentOffset;
    }
    size += segmentOffset;
  }
  
  public boolean rangeEquals(int offset, ByteString other, int otherOffset, int byteCount)
  {
    if ((offset < 0) || (offset > size() - byteCount)) { return false;
    }
    for (int s = segment(offset); byteCount > 0; s++) {
      int segmentOffset = s == 0 ? 0 : directory[(s - 1)];
      int segmentSize = directory[s] - segmentOffset;
      int stepSize = Math.min(byteCount, segmentOffset + segmentSize - offset);
      int segmentPos = directory[(segments.length + s)];
      int arrayOffset = offset - segmentOffset + segmentPos;
      if (!other.rangeEquals(otherOffset, segments[s], arrayOffset, stepSize)) return false;
      offset += stepSize;
      otherOffset += stepSize;
      byteCount -= stepSize;
    }
    return true;
  }
  
  public boolean rangeEquals(int offset, byte[] other, int otherOffset, int byteCount) {
    if ((offset < 0) || (offset > size() - byteCount) || (otherOffset < 0) || (otherOffset > other.length - byteCount))
    {
      return false;
    }
    
    for (int s = segment(offset); byteCount > 0; s++) {
      int segmentOffset = s == 0 ? 0 : directory[(s - 1)];
      int segmentSize = directory[s] - segmentOffset;
      int stepSize = Math.min(byteCount, segmentOffset + segmentSize - offset);
      int segmentPos = directory[(segments.length + s)];
      int arrayOffset = offset - segmentOffset + segmentPos;
      if (!Util.arrayRangeEquals(segments[s], arrayOffset, other, otherOffset, stepSize)) return false;
      offset += stepSize;
      otherOffset += stepSize;
      byteCount -= stepSize;
    }
    return true;
  }
  
  public int indexOf(byte[] other, int fromIndex) {
    return toByteString().indexOf(other, fromIndex);
  }
  
  public int lastIndexOf(byte[] other, int fromIndex) {
    return toByteString().lastIndexOf(other, fromIndex);
  }
  
  private ByteString toByteString()
  {
    return new ByteString(toByteArray());
  }
  
  byte[] internalArray() {
    return toByteArray();
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    return ((o instanceof ByteString)) && 
      (((ByteString)o).size() == size()) && 
      (rangeEquals(0, (ByteString)o, 0, size()));
  }
  
  public int hashCode() {
    int result = hashCode;
    if (result != 0) { return result;
    }
    
    result = 1;
    int segmentOffset = 0;
    int s = 0; for (int segmentCount = segments.length; s < segmentCount; s++) {
      byte[] segment = segments[s];
      int segmentPos = directory[(segmentCount + s)];
      int nextSegmentOffset = directory[s];
      int segmentSize = nextSegmentOffset - segmentOffset;
      int i = segmentPos; for (int limit = segmentPos + segmentSize; i < limit; i++) {
        result = 31 * result + segment[i];
      }
      segmentOffset = nextSegmentOffset;
    }
    return this.hashCode = result;
  }
  
  public String toString() {
    return toByteString().toString();
  }
  
  private Object writeReplace() {
    return toByteString();
  }
}
