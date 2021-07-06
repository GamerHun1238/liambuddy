package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;





































public class TUnmodifiableFloatFloatMap
  implements TFloatFloatMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TFloatFloatMap m;
  
  public TUnmodifiableFloatFloatMap(TFloatFloatMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(float key) { return m.containsKey(key); }
  public boolean containsValue(float val) { return m.containsValue(val); }
  public float get(float key) { return m.get(key); }
  
  public float put(float key, float value) { throw new UnsupportedOperationException(); }
  public float remove(float key) { throw new UnsupportedOperationException(); }
  public void putAll(TFloatFloatMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Float, ? extends Float> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TFloatSet keySet = null;
  private transient TFloatCollection values = null;
  
  public TFloatSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public float[] keys() { return m.keys(); }
  public float[] keys(float[] array) { return m.keys(array); }
  
  public TFloatCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public float[] values() { return m.values(); }
  public float[] values(float[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public float getNoEntryKey() { return m.getNoEntryKey(); }
  public float getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TFloatFloatProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TFloatFloatIterator iterator() {
    new TFloatFloatIterator() {
      TFloatFloatIterator iter = m.iterator();
      
      public float key() { return iter.key(); }
      public float value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public float setValue(float val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public float putIfAbsent(float key, float value) { throw new UnsupportedOperationException(); }
  public void transformValues(TFloatFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TFloatFloatProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(float key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(float key, float amount) { throw new UnsupportedOperationException(); }
  public float adjustOrPutValue(float key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
}
