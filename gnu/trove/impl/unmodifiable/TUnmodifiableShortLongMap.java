package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableShortLongMap
  implements TShortLongMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TShortLongMap m;
  
  public TUnmodifiableShortLongMap(TShortLongMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(short key) { return m.containsKey(key); }
  public boolean containsValue(long val) { return m.containsValue(val); }
  public long get(short key) { return m.get(key); }
  
  public long put(short key, long value) { throw new UnsupportedOperationException(); }
  public long remove(short key) { throw new UnsupportedOperationException(); }
  public void putAll(TShortLongMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Short, ? extends Long> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TShortSet keySet = null;
  private transient TLongCollection values = null;
  
  public TShortSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public short[] keys() { return m.keys(); }
  public short[] keys(short[] array) { return m.keys(array); }
  
  public TLongCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public long[] values() { return m.values(); }
  public long[] values(long[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public short getNoEntryKey() { return m.getNoEntryKey(); }
  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TShortLongProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TShortLongIterator iterator() {
    new TShortLongIterator() {
      TShortLongIterator iter = m.iterator();
      
      public short key() { return iter.key(); }
      public long value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public long setValue(long val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public long putIfAbsent(short key, long value) { throw new UnsupportedOperationException(); }
  public void transformValues(TLongFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TShortLongProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(short key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(short key, long amount) { throw new UnsupportedOperationException(); }
  public long adjustOrPutValue(short key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
}
