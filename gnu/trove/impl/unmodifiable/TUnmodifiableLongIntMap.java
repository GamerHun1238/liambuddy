package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableLongIntMap
  implements TLongIntMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TLongIntMap m;
  
  public TUnmodifiableLongIntMap(TLongIntMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(long key) { return m.containsKey(key); }
  public boolean containsValue(int val) { return m.containsValue(val); }
  public int get(long key) { return m.get(key); }
  
  public int put(long key, int value) { throw new UnsupportedOperationException(); }
  public int remove(long key) { throw new UnsupportedOperationException(); }
  public void putAll(TLongIntMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Long, ? extends Integer> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TLongSet keySet = null;
  private transient TIntCollection values = null;
  
  public TLongSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public long[] keys() { return m.keys(); }
  public long[] keys(long[] array) { return m.keys(array); }
  
  public TIntCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public int[] values() { return m.values(); }
  public int[] values(int[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public long getNoEntryKey() { return m.getNoEntryKey(); }
  public int getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TLongIntProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TLongIntIterator iterator() {
    new TLongIntIterator() {
      TLongIntIterator iter = m.iterator();
      
      public long key() { return iter.key(); }
      public int value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public int setValue(int val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public int putIfAbsent(long key, int value) { throw new UnsupportedOperationException(); }
  public void transformValues(TIntFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TLongIntProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(long key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(long key, int amount) { throw new UnsupportedOperationException(); }
  public int adjustOrPutValue(long key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
}
