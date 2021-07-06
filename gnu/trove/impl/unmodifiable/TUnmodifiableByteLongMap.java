package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableByteLongMap
  implements TByteLongMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TByteLongMap m;
  
  public TUnmodifiableByteLongMap(TByteLongMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(byte key) { return m.containsKey(key); }
  public boolean containsValue(long val) { return m.containsValue(val); }
  public long get(byte key) { return m.get(key); }
  
  public long put(byte key, long value) { throw new UnsupportedOperationException(); }
  public long remove(byte key) { throw new UnsupportedOperationException(); }
  public void putAll(TByteLongMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Byte, ? extends Long> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TByteSet keySet = null;
  private transient TLongCollection values = null;
  
  public TByteSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public byte[] keys() { return m.keys(); }
  public byte[] keys(byte[] array) { return m.keys(array); }
  
  public TLongCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public long[] values() { return m.values(); }
  public long[] values(long[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public byte getNoEntryKey() { return m.getNoEntryKey(); }
  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TByteLongProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TByteLongIterator iterator() {
    new TByteLongIterator() {
      TByteLongIterator iter = m.iterator();
      
      public byte key() { return iter.key(); }
      public long value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public long setValue(long val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public long putIfAbsent(byte key, long value) { throw new UnsupportedOperationException(); }
  public void transformValues(TLongFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TByteLongProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(byte key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(byte key, long amount) { throw new UnsupportedOperationException(); }
  public long adjustOrPutValue(byte key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
}
