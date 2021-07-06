package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableCharShortMap
  implements TCharShortMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TCharShortMap m;
  
  public TUnmodifiableCharShortMap(TCharShortMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(char key) { return m.containsKey(key); }
  public boolean containsValue(short val) { return m.containsValue(val); }
  public short get(char key) { return m.get(key); }
  
  public short put(char key, short value) { throw new UnsupportedOperationException(); }
  public short remove(char key) { throw new UnsupportedOperationException(); }
  public void putAll(TCharShortMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Character, ? extends Short> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TCharSet keySet = null;
  private transient TShortCollection values = null;
  
  public TCharSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public char[] keys() { return m.keys(); }
  public char[] keys(char[] array) { return m.keys(array); }
  
  public TShortCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public short[] values() { return m.values(); }
  public short[] values(short[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public char getNoEntryKey() { return m.getNoEntryKey(); }
  public short getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TCharShortProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TCharShortIterator iterator() {
    new TCharShortIterator() {
      TCharShortIterator iter = m.iterator();
      
      public char key() { return iter.key(); }
      public short value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public short setValue(short val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public short putIfAbsent(char key, short value) { throw new UnsupportedOperationException(); }
  public void transformValues(TShortFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TCharShortProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(char key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(char key, short amount) { throw new UnsupportedOperationException(); }
  public short adjustOrPutValue(char key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
}
