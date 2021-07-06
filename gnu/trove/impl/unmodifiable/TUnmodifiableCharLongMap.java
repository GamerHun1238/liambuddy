package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.map.TCharLongMap;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableCharLongMap
  implements TCharLongMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TCharLongMap m;
  
  public TUnmodifiableCharLongMap(TCharLongMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(char key) { return m.containsKey(key); }
  public boolean containsValue(long val) { return m.containsValue(val); }
  public long get(char key) { return m.get(key); }
  
  public long put(char key, long value) { throw new UnsupportedOperationException(); }
  public long remove(char key) { throw new UnsupportedOperationException(); }
  public void putAll(TCharLongMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Character, ? extends Long> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TCharSet keySet = null;
  private transient TLongCollection values = null;
  
  public TCharSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public char[] keys() { return m.keys(); }
  public char[] keys(char[] array) { return m.keys(array); }
  
  public TLongCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public long[] values() { return m.values(); }
  public long[] values(long[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public char getNoEntryKey() { return m.getNoEntryKey(); }
  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TCharLongProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TCharLongIterator iterator() {
    new TCharLongIterator() {
      TCharLongIterator iter = m.iterator();
      
      public char key() { return iter.key(); }
      public long value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public long setValue(long val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public long putIfAbsent(char key, long value) { throw new UnsupportedOperationException(); }
  public void transformValues(TLongFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TCharLongProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(char key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(char key, long amount) { throw new UnsupportedOperationException(); }
  public long adjustOrPutValue(char key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
}
