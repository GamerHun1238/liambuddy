package okio;

import javax.annotation.Nullable;













































final class Segment
{
  static final int SIZE = 8192;
  static final int SHARE_MINIMUM = 1024;
  final byte[] data;
  int pos;
  int limit;
  boolean shared;
  boolean owner;
  Segment next;
  Segment prev;
  
  Segment()
  {
    data = new byte[' '];
    owner = true;
    shared = false;
  }
  
  Segment(byte[] data, int pos, int limit, boolean shared, boolean owner) {
    this.data = data;
    this.pos = pos;
    this.limit = limit;
    this.shared = shared;
    this.owner = owner;
  }
  




  final Segment sharedCopy()
  {
    shared = true;
    return new Segment(data, pos, limit, true, false);
  }
  
  final Segment unsharedCopy()
  {
    return new Segment((byte[])data.clone(), pos, limit, false, true);
  }
  


  @Nullable
  public final Segment pop()
  {
    Segment result = next != this ? next : null;
    prev.next = next;
    next.prev = prev;
    next = null;
    prev = null;
    return result;
  }
  



  public final Segment push(Segment segment)
  {
    prev = this;
    next = next;
    next.prev = segment;
    next = segment;
    return segment;
  }
  







  public final Segment split(int byteCount)
  {
    if ((byteCount <= 0) || (byteCount > limit - pos)) { throw new IllegalArgumentException();
    }
    

    Segment prefix;
    
    Segment prefix;
    
    if (byteCount >= 1024) {
      prefix = sharedCopy();
    } else {
      prefix = SegmentPool.take();
      System.arraycopy(data, pos, data, 0, byteCount);
    }
    
    limit = (pos + byteCount);
    pos += byteCount;
    prev.push(prefix);
    return prefix;
  }
  



  public final void compact()
  {
    if (prev == this) throw new IllegalStateException();
    if (!prev.owner) return;
    int byteCount = limit - pos;
    int availableByteCount = 8192 - prev.limit + (prev.shared ? 0 : prev.pos);
    if (byteCount > availableByteCount) return;
    writeTo(prev, byteCount);
    pop();
    SegmentPool.recycle(this);
  }
  
  public final void writeTo(Segment sink, int byteCount)
  {
    if (!owner) throw new IllegalArgumentException();
    if (limit + byteCount > 8192)
    {
      if (shared) throw new IllegalArgumentException();
      if (limit + byteCount - pos > 8192) throw new IllegalArgumentException();
      System.arraycopy(data, pos, data, 0, limit - pos);
      limit -= pos;
      pos = 0;
    }
    
    System.arraycopy(data, pos, data, limit, byteCount);
    limit += byteCount;
    pos += byteCount;
  }
}
