package okio;

import javax.annotation.Nullable;
























final class SegmentPool
{
  static final long MAX_SIZE = 65536L;
  @Nullable
  static Segment next;
  static long byteCount;
  
  private SegmentPool() {}
  
  static Segment take()
  {
    synchronized (SegmentPool.class) {
      if (next != null) {
        Segment result = next;
        next = next;
        next = null;
        byteCount -= 8192L;
        return result;
      }
    }
    return new Segment();
  }
  
  static void recycle(Segment segment) {
    if ((next != null) || (prev != null)) throw new IllegalArgumentException();
    if (shared) return;
    synchronized (SegmentPool.class) {
      if (byteCount + 8192L > 65536L) return;
      byteCount += 8192L;
      next = next;
      pos = (segment.limit = 0);
      next = segment;
    }
  }
}
