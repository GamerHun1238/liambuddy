package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableShortFloatMap
  implements TShortFloatMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TShortFloatMap m;
  
  public TUnmodifiableShortFloatMap(TShortFloatMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(short key) { return m.containsKey(key); }
  public boolean containsValue(float val) { return m.containsValue(val); }
  public float get(short key) { return m.get(key); }
  
  public float put(short key, float value) { throw new UnsupportedOperationException(); }
  public float remove(short key) { throw new UnsupportedOperationException(); }
  public void putAll(TShortFloatMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Short, ? extends Float> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TShortSet keySet = null;
  private transient TFloatCollection values = null;
  
  public TShortSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public short[] keys() { return m.keys(); }
  public short[] keys(short[] array) { return m.keys(array); }
  
  public TFloatCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public float[] values() { return m.values(); }
  public float[] values(float[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public short getNoEntryKey() { return m.getNoEntryKey(); }
  public float getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TShortFloatProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TShortFloatIterator iterator() {
    new TShortFloatIterator() {
      TShortFloatIterator iter = m.iterator();
      
      public short key() { return iter.key(); }
      public float value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public float setValue(float val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public float putIfAbsent(short key, float value) { throw new UnsupportedOperationException(); }
  public void transformValues(TFloatFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TShortFloatProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(short key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(short key, float amount) { throw new UnsupportedOperationException(); }
  public float adjustOrPutValue(short key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
}
