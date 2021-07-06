package gnu.trove.impl.unmodifiable;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.io.Serializable;
import java.util.Collection;










































public class TUnmodifiableShortCollection
  implements TShortCollection, Serializable
{
  private static final long serialVersionUID = 1820017752578914078L;
  final TShortCollection c;
  
  public TUnmodifiableShortCollection(TShortCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
  }
  
  public int size() { return c.size(); }
  public boolean isEmpty() { return c.isEmpty(); }
  public boolean contains(short o) { return c.contains(o); }
  public short[] toArray() { return c.toArray(); }
  public short[] toArray(short[] a) { return c.toArray(a); }
  public String toString() { return c.toString(); }
  public short getNoEntryValue() { return c.getNoEntryValue(); }
  public boolean forEach(TShortProcedure procedure) { return c.forEach(procedure); }
  
  public TShortIterator iterator() {
    new TShortIterator() {
      TShortIterator i = c.iterator();
      
      public boolean hasNext() { return i.hasNext(); }
      public short next() { return i.next(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public boolean add(short e) { throw new UnsupportedOperationException(); }
  public boolean remove(short o) { throw new UnsupportedOperationException(); }
  
  public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
  public boolean containsAll(TShortCollection coll) { return c.containsAll(coll); }
  public boolean containsAll(short[] array) { return c.containsAll(array); }
  
  public boolean addAll(TShortCollection coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(Collection<? extends Short> coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(short[] array) { throw new UnsupportedOperationException(); }
  
  public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(TShortCollection coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(short[] array) { throw new UnsupportedOperationException(); }
  
  public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(TShortCollection coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(short[] array) { throw new UnsupportedOperationException(); }
  
  public void clear() { throw new UnsupportedOperationException(); }
}
