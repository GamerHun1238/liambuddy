package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.procedure.TFloatLongProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableFloatLongMap
  implements TFloatLongMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TFloatLongMap m;
  
  public TUnmodifiableFloatLongMap(TFloatLongMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(float key) { return m.containsKey(key); }
  public boolean containsValue(long val) { return m.containsValue(val); }
  public long get(float key) { return m.get(key); }
  
  public long put(float key, long value) { throw new UnsupportedOperationException(); }
  public long remove(float key) { throw new UnsupportedOperationException(); }
  public void putAll(TFloatLongMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Float, ? extends Long> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TFloatSet keySet = null;
  private transient TLongCollection values = null;
  
  public TFloatSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public float[] keys() { return m.keys(); }
  public float[] keys(float[] array) { return m.keys(array); }
  
  public TLongCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public long[] values() { return m.values(); }
  public long[] values(long[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public float getNoEntryKey() { return m.getNoEntryKey(); }
  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TFloatLongProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TFloatLongIterator iterator() {
    new TFloatLongIterator() {
      TFloatLongIterator iter = m.iterator();
      
      public float key() { return iter.key(); }
      public long value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public long setValue(long val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public long putIfAbsent(float key, long value) { throw new UnsupportedOperationException(); }
  public void transformValues(TLongFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TFloatLongProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(float key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(float key, long amount) { throw new UnsupportedOperationException(); }
  public long adjustOrPutValue(float key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
}
