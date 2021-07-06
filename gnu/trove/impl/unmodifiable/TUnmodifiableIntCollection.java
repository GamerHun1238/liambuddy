package gnu.trove.impl.unmodifiable;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.Serializable;
import java.util.Collection;










































public class TUnmodifiableIntCollection
  implements TIntCollection, Serializable
{
  private static final long serialVersionUID = 1820017752578914078L;
  final TIntCollection c;
  
  public TUnmodifiableIntCollection(TIntCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
  }
  
  public int size() { return c.size(); }
  public boolean isEmpty() { return c.isEmpty(); }
  public boolean contains(int o) { return c.contains(o); }
  public int[] toArray() { return c.toArray(); }
  public int[] toArray(int[] a) { return c.toArray(a); }
  public String toString() { return c.toString(); }
  public int getNoEntryValue() { return c.getNoEntryValue(); }
  public boolean forEach(TIntProcedure procedure) { return c.forEach(procedure); }
  
  public TIntIterator iterator() {
    new TIntIterator() {
      TIntIterator i = c.iterator();
      
      public boolean hasNext() { return i.hasNext(); }
      public int next() { return i.next(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public boolean add(int e) { throw new UnsupportedOperationException(); }
  public boolean remove(int o) { throw new UnsupportedOperationException(); }
  
  public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
  public boolean containsAll(TIntCollection coll) { return c.containsAll(coll); }
  public boolean containsAll(int[] array) { return c.containsAll(array); }
  
  public boolean addAll(TIntCollection coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(Collection<? extends Integer> coll) { throw new UnsupportedOperationException(); }
  public boolean addAll(int[] array) { throw new UnsupportedOperationException(); }
  
  public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(TIntCollection coll) { throw new UnsupportedOperationException(); }
  public boolean removeAll(int[] array) { throw new UnsupportedOperationException(); }
  
  public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(TIntCollection coll) { throw new UnsupportedOperationException(); }
  public boolean retainAll(int[] array) { throw new UnsupportedOperationException(); }
  
  public void clear() { throw new UnsupportedOperationException(); }
}
