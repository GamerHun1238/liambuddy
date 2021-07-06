package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableFloatShortMap
  implements TFloatShortMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TFloatShortMap m;
  
  public TUnmodifiableFloatShortMap(TFloatShortMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(float key) { return m.containsKey(key); }
  public boolean containsValue(short val) { return m.containsValue(val); }
  public short get(float key) { return m.get(key); }
  
  public short put(float key, short value) { throw new UnsupportedOperationException(); }
  public short remove(float key) { throw new UnsupportedOperationException(); }
  public void putAll(TFloatShortMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Float, ? extends Short> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TFloatSet keySet = null;
  private transient TShortCollection values = null;
  
  public TFloatSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public float[] keys() { return m.keys(); }
  public float[] keys(float[] array) { return m.keys(array); }
  
  public TShortCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public short[] values() { return m.values(); }
  public short[] values(short[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public float getNoEntryKey() { return m.getNoEntryKey(); }
  public short getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TFloatShortProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TFloatShortIterator iterator() {
    new TFloatShortIterator() {
      TFloatShortIterator iter = m.iterator();
      
      public float key() { return iter.key(); }
      public short value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public short setValue(short val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public short putIfAbsent(float key, short value) { throw new UnsupportedOperationException(); }
  public void transformValues(TShortFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TFloatShortProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(float key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(float key, short amount) { throw new UnsupportedOperationException(); }
  public short adjustOrPutValue(float key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
}
