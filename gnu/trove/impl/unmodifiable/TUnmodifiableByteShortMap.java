package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableByteShortMap
  implements TByteShortMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TByteShortMap m;
  
  public TUnmodifiableByteShortMap(TByteShortMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(byte key) { return m.containsKey(key); }
  public boolean containsValue(short val) { return m.containsValue(val); }
  public short get(byte key) { return m.get(key); }
  
  public short put(byte key, short value) { throw new UnsupportedOperationException(); }
  public short remove(byte key) { throw new UnsupportedOperationException(); }
  public void putAll(TByteShortMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Byte, ? extends Short> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TByteSet keySet = null;
  private transient TShortCollection values = null;
  
  public TByteSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public byte[] keys() { return m.keys(); }
  public byte[] keys(byte[] array) { return m.keys(array); }
  
  public TShortCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public short[] values() { return m.values(); }
  public short[] values(short[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public byte getNoEntryKey() { return m.getNoEntryKey(); }
  public short getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TByteShortProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TByteShortIterator iterator() {
    new TByteShortIterator() {
      TByteShortIterator iter = m.iterator();
      
      public byte key() { return iter.key(); }
      public short value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public short setValue(short val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public short putIfAbsent(byte key, short value) { throw new UnsupportedOperationException(); }
  public void transformValues(TShortFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TByteShortProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(byte key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(byte key, short amount) { throw new UnsupportedOperationException(); }
  public short adjustOrPutValue(byte key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
}
