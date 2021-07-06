package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;




































public class TUnmodifiableCharDoubleMap
  implements TCharDoubleMap, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TCharDoubleMap m;
  
  public TUnmodifiableCharDoubleMap(TCharDoubleMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(char key) { return m.containsKey(key); }
  public boolean containsValue(double val) { return m.containsValue(val); }
  public double get(char key) { return m.get(key); }
  
  public double put(char key, double value) { throw new UnsupportedOperationException(); }
  public double remove(char key) { throw new UnsupportedOperationException(); }
  public void putAll(TCharDoubleMap m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends Character, ? extends Double> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient TCharSet keySet = null;
  private transient TDoubleCollection values = null;
  
  public TCharSet keySet() {
    if (keySet == null)
      keySet = TCollections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public char[] keys() { return m.keys(); }
  public char[] keys(char[] array) { return m.keys(array); }
  
  public TDoubleCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public double[] values() { return m.values(); }
  public double[] values(double[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public char getNoEntryKey() { return m.getNoEntryKey(); }
  public double getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TCharDoubleProcedure procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TCharDoubleIterator iterator() {
    new TCharDoubleIterator() {
      TCharDoubleIterator iter = m.iterator();
      
      public char key() { return iter.key(); }
      public double value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public double setValue(double val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public double putIfAbsent(char key, double value) { throw new UnsupportedOperationException(); }
  public void transformValues(TDoubleFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TCharDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(char key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(char key, double amount) { throw new UnsupportedOperationException(); }
  public double adjustOrPutValue(char key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
}
