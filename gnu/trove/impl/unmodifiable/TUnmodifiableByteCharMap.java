package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.map.TByteCharMap;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableByteCharMap
  implements TByteCharMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TByteCharMap m;
  
  public TUnmodifiableByteCharMap(TByteCharMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(byte key) { return m.containsKey(key); }
  public boolean containsValue(char val) { return m.containsValue(val); }
  public char get(byte key) { return m.get(key); }
  
  public char put(byte key, char value) { throw new UnsupportedOperationException(); }
  public char remove(byte key) { throw new UnsupportedOperationException(); }
  public void putAll(TByteCharMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Byte, ? extends Character> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TByteSet keySet = null;
  private transient TCharCollection values = null;
  
  public TByteSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public byte[] keys() { return m.keys(); }
  public byte[] keys(byte[] array) { return m.keys(array); }
  
  public TCharCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public char[] values() { return m.values(); }
  public char[] values(char[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public byte getNoEntryKey() { return m.getNoEntryKey(); }
  public char getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TByteCharProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TByteCharIterator iterator() {
    new TByteCharIterator() {
      TByteCharIterator iter = m.iterator();
      
      public byte key() { return iter.key(); }
      public char value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public char setValue(char val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public char putIfAbsent(byte key, char value) { throw new UnsupportedOperationException(); }
  public void transformValues(TCharFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TByteCharProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(byte key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(byte key, char amount) { throw new UnsupportedOperationException(); }
  public char adjustOrPutValue(byte key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
}
