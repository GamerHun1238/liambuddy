package okio;

import java.nio.charset.Charset;
















final class Util
{
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  
  private Util() {}
  
  public static void checkOffsetAndCount(long size, long offset, long byteCount)
  {
    if (((offset | byteCount) < 0L) || (offset > size) || (size - offset < byteCount))
    {
      throw new ArrayIndexOutOfBoundsException(String.format("size=%s offset=%s byteCount=%s", new Object[] {Long.valueOf(size), Long.valueOf(offset), Long.valueOf(byteCount) }));
    }
  }
  
  public static short reverseBytesShort(short s) {
    int i = s & 0xFFFF;
    int reversed = (i & 0xFF00) >>> 8 | (i & 0xFF) << 8;
    
    return (short)reversed;
  }
  
  public static int reverseBytesInt(int i) {
    return (i & 0xFF000000) >>> 24 | (i & 0xFF0000) >>> 8 | (i & 0xFF00) << 8 | (i & 0xFF) << 24;
  }
  


  public static long reverseBytesLong(long v)
  {
    return (v & 0xFF00000000000000) >>> 56 | (v & 0xFF000000000000) >>> 40 | (v & 0xFF0000000000) >>> 24 | (v & 0xFF00000000) >>> 8 | (v & 0xFF000000) << 8 | (v & 0xFF0000) << 24 | (v & 0xFF00) << 40 | (v & 0xFF) << 56;
  }
  











  public static void sneakyRethrow(Throwable t)
  {
    sneakyThrow2(t);
  }
  
  private static <T extends Throwable> void sneakyThrow2(Throwable t) throws Throwable
  {
    throw t;
  }
  
  public static boolean arrayRangeEquals(byte[] a, int aOffset, byte[] b, int bOffset, int byteCount)
  {
    for (int i = 0; i < byteCount; i++) {
      if (a[(i + aOffset)] != b[(i + bOffset)]) return false;
    }
    return true;
  }
}
