package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TDoubleFunction;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;









































public class TUnmodifiableDoubleList
  extends TUnmodifiableDoubleCollection
  implements TDoubleList
{
  static final long serialVersionUID = -283967356065247728L;
  final TDoubleList list;
  
  public TUnmodifiableDoubleList(TDoubleList list)
  {
    super(list);
    this.list = list;
  }
  
  public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
  public int hashCode() { return list.hashCode(); }
  
  public double get(int index) { return list.get(index); }
  public int indexOf(double o) { return list.indexOf(o); }
  public int lastIndexOf(double o) { return list.lastIndexOf(o); }
  
  public double[] toArray(int offset, int len) {
    return list.toArray(offset, len);
  }
  
  public double[] toArray(double[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
  
  public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
    return list.toArray(dest, source_pos, dest_pos, len);
  }
  
  public boolean forEachDescending(TDoubleProcedure procedure) {
    return list.forEachDescending(procedure);
  }
  
  public int binarySearch(double value) { return list.binarySearch(value); }
  
  public int binarySearch(double value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
  

  public int indexOf(int offset, double value) { return list.indexOf(offset, value); }
  public int lastIndexOf(int offset, double value) { return list.lastIndexOf(offset, value); }
  public TDoubleList grep(TDoubleProcedure condition) { return list.grep(condition); }
  public TDoubleList inverseGrep(TDoubleProcedure condition) { return list.inverseGrep(condition); }
  
  public double max() { return list.max(); }
  public double min() { return list.min(); }
  public double sum() { return list.sum(); }
  
  public TDoubleList subList(int fromIndex, int toIndex) {
    return new TUnmodifiableDoubleList(list.subList(fromIndex, toIndex));
  }
  





































  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TUnmodifiableRandomAccessDoubleList(list) : this;
  }
  


  public void add(double[] vals) { throw new UnsupportedOperationException(); }
  public void add(double[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
  
  public double removeAt(int offset) { throw new UnsupportedOperationException(); }
  public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
  
  public void insert(int offset, double value) { throw new UnsupportedOperationException(); }
  public void insert(int offset, double[] values) { throw new UnsupportedOperationException(); }
  public void insert(int offset, double[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
  
  public double set(int offset, double val) { throw new UnsupportedOperationException(); }
  public void set(int offset, double[] values) { throw new UnsupportedOperationException(); }
  public void set(int offset, double[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
  
  public double replace(int offset, double val) { throw new UnsupportedOperationException(); }
  
  public void transformValues(TDoubleFunction function) { throw new UnsupportedOperationException(); }
  
  public void reverse() { throw new UnsupportedOperationException(); }
  public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
  public void shuffle(Random rand) { throw new UnsupportedOperationException(); }
  
  public void sort() { throw new UnsupportedOperationException(); }
  public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
  public void fill(double val) { throw new UnsupportedOperationException(); }
  public void fill(int fromIndex, int toIndex, double val) { throw new UnsupportedOperationException(); }
}
