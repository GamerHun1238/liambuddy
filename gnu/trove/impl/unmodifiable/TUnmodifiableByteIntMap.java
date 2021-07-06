package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.map.TByteIntMap;
import gnu.trove.procedure.TByteIntProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableByteIntMap
  implements TByteIntMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TByteIntMap m;
  
  public TUnmodifiableByteIntMap(TByteIntMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(byte key) { return m.containsKey(key); }
  public boolean containsValue(int val) { return m.containsValue(val); }
  public int get(byte key) { return m.get(key); }
  
  public int put(byte key, int value) { throw new UnsupportedOperationException(); }
  public int remove(byte key) { throw new UnsupportedOperationException(); }
  public void putAll(TByteIntMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Byte, ? extends Integer> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TByteSet keySet = null;
  private transient TIntCollection values = null;
  
  public TByteSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public byte[] keys() { return m.keys(); }
  public byte[] keys(byte[] array) { return m.keys(array); }
  
  public TIntCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public int[] values() { return m.values(); }
  public int[] values(int[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public byte getNoEntryKey() { return m.getNoEntryKey(); }
  public int getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TByteIntProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TByteIntIterator iterator() {
    new TByteIntIterator() {
      TByteIntIterator iter = m.iterator();
      
      public byte key() { return iter.key(); }
      public int value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public int setValue(int val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public int putIfAbsent(byte key, int value) { throw new UnsupportedOperationException(); }
  public void transformValues(TIntFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TByteIntProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(byte key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(byte key, int amount) { throw new UnsupportedOperationException(); }
  public int adjustOrPutValue(byte key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
}
