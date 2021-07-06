package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.Serializable;
import java.util.Collection;










































public class TUnmodifiableByteCollection
  implements TByteCollection, Serializable
{
  private static final long serialVersionUID = 1820017752578914078L;
  final TByteCollection c;
  
  public TUnmodifiableByteCollection(TByteCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
  }
  
  public int size() { return c.size(); }
  public boolean isEmpty() { return c.isEmpty(); }
  public boolean contains(byte o) { return c.contains(o); }
  public byte[] toArray() { return c.toArray(); }
  public byte[] toArray(byte[] a) { return c.toArray(a); }
  public String toString() { return c.toString(); }
  public byte getNoEntryValue() { return c.getNoEntryValue(); }
  public boolean forEach(TByteProcedure procedure) { return c.forEach(procedure); }
  
  public TByteIterator iterator() {
    new TByteIterator() {
      TByteIterator i = c.iterator();
      
      public boolean hasNext() { return i.hasNext(); }
      public byte next() { return i.next(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public boolean add(byte e) { throw new UnsupportedOperationException(); }
  public boolean remove(byte o) { throw new UnsupportedOperationException(); }
  
  public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
  public boolean containsAll(TByteCollection coll) { return c.containsAll(coll); }
  public boolean containsAll(byte[] array) { return c.containsAll(array); }
  
  public boolean addAll(TByteCollection coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(Collection<? extends Byte> coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(byte[] array) { throw new UnsupportedOperationException(); }
  
  public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(TByteCollection coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(byte[] array) { throw new UnsupportedOperationException(); }
  
  public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(TByteCollection coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(byte[] array) { throw new UnsupportedOperationException(); }
  
  public void clear() { throw new UnsupportedOperationException(); }
}
