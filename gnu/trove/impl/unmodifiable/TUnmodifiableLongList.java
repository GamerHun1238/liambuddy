package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TLongFunction;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;
import java.util.RandomAccess;









































public class TUnmodifiableLongList
  extends TUnmodifiableLongCollection
  implements TLongList
{
  static final long serialVersionUID = -283967356065247728L;
  final TLongList list;
  
  public TUnmodifiableLongList(TLongList list)
  {
    super(list);
    this.list = list;
  }
  
  public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
  public int hashCode() { return list.hashCode(); }
  
  public long get(int index) { return list.get(index); }
  public int indexOf(long o) { return list.indexOf(o); }
  public int lastIndexOf(long o) { return list.lastIndexOf(o); }
  
  public long[] toArray(int offset, int len) {
    return list.toArray(offset, len);
  }
  
  public long[] toArray(long[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
  
  public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) {
    return list.toArray(dest, source_pos, dest_pos, len);
  }
  
  public boolean forEachDescending(TLongProcedure procedure) {
    return list.forEachDescending(procedure);
  }
  
  public int binarySearch(long value) { return list.binarySearch(value); }
  
  public int binarySearch(long value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
  

  public int indexOf(int offset, long value) { return list.indexOf(offset, value); }
  public int lastIndexOf(int offset, long value) { return list.lastIndexOf(offset, value); }
  public TLongList grep(TLongProcedure condition) { return list.grep(condition); }
  public TLongList inverseGrep(TLongProcedure condition) { return list.inverseGrep(condition); }
  
  public long max() { return list.max(); }
  public long min() { return list.min(); }
  public long sum() { return list.sum(); }
  
  public TLongList subList(int fromIndex, int toIndex) {
    return new TUnmodifiableLongList(list.subList(fromIndex, toIndex));
  }
  





































  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TUnmodifiableRandomAccessLongList(list) : this;
  }
  


  public void add(long[] vals) { throw new UnsupportedOperationException(); }
  public void add(long[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
  
  public long removeAt(int offset) { throw new UnsupportedOperationException(); }
  public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
  
  public void insert(int offset, long value) { throw new UnsupportedOperationException(); }
  public void insert(int offset, long[] values) { throw new UnsupportedOperationException(); }
  public void insert(int offset, long[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
  
  public long set(int offset, long val) { throw new UnsupportedOperationException(); }
  public void set(int offset, long[] values) { throw new UnsupportedOperationException(); }
  public void set(int offset, long[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
  
  public long replace(int offset, long val) { throw new UnsupportedOperationException(); }
  
  public void transformValues(TLongFunction function) { throw new UnsupportedOperationException(); }
  
  public void reverse() { throw new UnsupportedOperationException(); }
  public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
  public void shuffle(Random rand) { throw new UnsupportedOperationException(); }
  
  public void sort() { throw new UnsupportedOperationException(); }
  public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
  public void fill(long val) { throw new UnsupportedOperationException(); }
  public void fill(int fromIndex, int toIndex, long val) { throw new UnsupportedOperationException(); }
}
