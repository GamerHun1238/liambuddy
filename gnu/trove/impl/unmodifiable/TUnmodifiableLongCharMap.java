package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableLongCharMap
  implements TLongCharMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TLongCharMap m;
  
  public TUnmodifiableLongCharMap(TLongCharMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(long key) { return m.containsKey(key); }
  public boolean containsValue(char val) { return m.containsValue(val); }
  public char get(long key) { return m.get(key); }
  
  public char put(long key, char value) { throw new UnsupportedOperationException(); }
  public char remove(long key) { throw new UnsupportedOperationException(); }
  public void putAll(TLongCharMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Long, ? extends Character> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TLongSet keySet = null;
  private transient TCharCollection values = null;
  
  public TLongSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public long[] keys() { return m.keys(); }
  public long[] keys(long[] array) { return m.keys(array); }
  
  public TCharCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public char[] values() { return m.values(); }
  public char[] values(char[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public long getNoEntryKey() { return m.getNoEntryKey(); }
  public char getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TLongCharProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TLongCharIterator iterator() {
    new TLongCharIterator() {
      TLongCharIterator iter = m.iterator();
      
      public long key() { return iter.key(); }
      public char value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public char setValue(char val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public char putIfAbsent(long key, char value) { throw new UnsupportedOperationException(); }
  public void transformValues(TCharFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TLongCharProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(long key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(long key, char amount) { throw new UnsupportedOperationException(); }
  public char adjustOrPutValue(long key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
}
