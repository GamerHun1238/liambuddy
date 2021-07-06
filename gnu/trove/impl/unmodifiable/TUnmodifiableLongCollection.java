package gnu.trove.impl.unmodifiable;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.Serializable;
import java.util.Collection;










































public class TUnmodifiableLongCollection
  implements TLongCollection, Serializable
{
  private static final long serialVersionUID = 1820017752578914078L;
  final TLongCollection c;
  
  public TUnmodifiableLongCollection(TLongCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
  }
  
  public int size() { return c.size(); }
  public boolean isEmpty() { return c.isEmpty(); }
  public boolean contains(long o) { return c.contains(o); }
  public long[] toArray() { return c.toArray(); }
  public long[] toArray(long[] a) { return c.toArray(a); }
  public String toString() { return c.toString(); }
  public long getNoEntryValue() { return c.getNoEntryValue(); }
  public boolean forEach(TLongProcedure procedure) { return c.forEach(procedure); }
  
  public TLongIterator iterator() {
    new TLongIterator() {
      TLongIterator i = c.iterator();
      
      public boolean hasNext() { return i.hasNext(); }
      public long next() { return i.next(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public boolean add(long e) { throw new UnsupportedOperationException(); }
  public boolean remove(long o) { throw new UnsupportedOperationException(); }
  
  public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
  public boolean containsAll(TLongCollection coll) { return c.containsAll(coll); }
  public boolean containsAll(long[] array) { return c.containsAll(array); }
  
  public boolean addAll(TLongCollection coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(Collection<? extends Long> coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(long[] array) { throw new UnsupportedOperationException(); }
  
  public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(TLongCollection coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(long[] array) { throw new UnsupportedOperationException(); }
  
  public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(TLongCollection coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(long[] array) { throw new UnsupportedOperationException(); }
  
  public void clear() { throw new UnsupportedOperationException(); }
}
