package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableIntLongMap
  implements TIntLongMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TIntLongMap m;
  
  public TUnmodifiableIntLongMap(TIntLongMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(int key) { return m.containsKey(key); }
  public boolean containsValue(long val) { return m.containsValue(val); }
  public long get(int key) { return m.get(key); }
  
  public long put(int key, long value) { throw new UnsupportedOperationException(); }
  public long remove(int key) { throw new UnsupportedOperationException(); }
  public void putAll(TIntLongMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Integer, ? extends Long> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TIntSet keySet = null;
  private transient TLongCollection values = null;
  
  public TIntSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public int[] keys() { return m.keys(); }
  public int[] keys(int[] array) { return m.keys(array); }
  
  public TLongCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public long[] values() { return m.values(); }
  public long[] values(long[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public int getNoEntryKey() { return m.getNoEntryKey(); }
  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TIntLongProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TIntLongIterator iterator() {
    new TIntLongIterator() {
      TIntLongIterator iter = m.iterator();
      
      public int key() { return iter.key(); }
      public long value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public long setValue(long val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public long putIfAbsent(int key, long value) { throw new UnsupportedOperationException(); }
  public void transformValues(TLongFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TIntLongProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(int key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(int key, long amount) { throw new UnsupportedOperationException(); }
  public long adjustOrPutValue(int key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
}
