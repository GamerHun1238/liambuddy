package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TShortFunction;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;
import java.util.RandomAccess;









































public class TUnmodifiableShortList
  extends TUnmodifiableShortCollection
  implements TShortList
{
  static final long serialVersionUID = -283967356065247728L;
  final TShortList list;
  
  public TUnmodifiableShortList(TShortList list)
  {
    super(list);
    this.list = list;
  }
  
  public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
  public int hashCode() { return list.hashCode(); }
  
  public short get(int index) { return list.get(index); }
  public int indexOf(short o) { return list.indexOf(o); }
  public int lastIndexOf(short o) { return list.lastIndexOf(o); }
  
  public short[] toArray(int offset, int len) {
    return list.toArray(offset, len);
  }
  
  public short[] toArray(short[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
  
  public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) {
    return list.toArray(dest, source_pos, dest_pos, len);
  }
  
  public boolean forEachDescending(TShortProcedure procedure) {
    return list.forEachDescending(procedure);
  }
  
  public int binarySearch(short value) { return list.binarySearch(value); }
  
  public int binarySearch(short value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
  

  public int indexOf(int offset, short value) { return list.indexOf(offset, value); }
  public int lastIndexOf(int offset, short value) { return list.lastIndexOf(offset, value); }
  public TShortList grep(TShortProcedure condition) { return list.grep(condition); }
  public TShortList inverseGrep(TShortProcedure condition) { return list.inverseGrep(condition); }
  
  public short max() { return list.max(); }
  public short min() { return list.min(); }
  public short sum() { return list.sum(); }
  
  public TShortList subList(int fromIndex, int toIndex) {
    return new TUnmodifiableShortList(list.subList(fromIndex, toIndex));
  }
  





































  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TUnmodifiableRandomAccessShortList(list) : this;
  }
  


  public void add(short[] vals) { throw new UnsupportedOperationException(); }
  public void add(short[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
  
  public short removeAt(int offset) { throw new UnsupportedOperationException(); }
  public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
  
  public void insert(int offset, short value) { throw new UnsupportedOperationException(); }
  public void insert(int offset, short[] values) { throw new UnsupportedOperationException(); }
  public void insert(int offset, short[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
  
  public short set(int offset, short val) { throw new UnsupportedOperationException(); }
  public void set(int offset, short[] values) { throw new UnsupportedOperationException(); }
  public void set(int offset, short[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
  
  public short replace(int offset, short val) { throw new UnsupportedOperationException(); }
  
  public void transformValues(TShortFunction function) { throw new UnsupportedOperationException(); }
  
  public void reverse() { throw new UnsupportedOperationException(); }
  public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
  public void shuffle(Random rand) { throw new UnsupportedOperationException(); }
  
  public void sort() { throw new UnsupportedOperationException(); }
  public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
  public void fill(short val) { throw new UnsupportedOperationException(); }
  public void fill(int fromIndex, int toIndex, short val) { throw new UnsupportedOperationException(); }
}
