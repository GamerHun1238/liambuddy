package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;






























public class TUnmodifiableObjectLongMap<K>
  implements TObjectLongMap<K>, Serializable
{
  private static final long serialVersionUID = -1034234728574286014L;
  private final TObjectLongMap<K> m;
  
  public TUnmodifiableObjectLongMap(TObjectLongMap<K> m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
  }
  
  public int size() { return m.size(); }
  public boolean isEmpty() { return m.isEmpty(); }
  public boolean containsKey(Object key) { return m.containsKey(key); }
  public boolean containsValue(long val) { return m.containsValue(val); }
  public long get(Object key) { return m.get(key); }
  
  public long put(K key, long value) { throw new UnsupportedOperationException(); }
  public long remove(Object key) { throw new UnsupportedOperationException(); }
  public void putAll(TObjectLongMap<? extends K> m) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends K, ? extends Long> map) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  private transient Set<K> keySet = null;
  private transient TLongCollection values = null;
  
  public Set<K> keySet() {
    if (keySet == null)
      keySet = Collections.unmodifiableSet(m.keySet());
    return keySet; }
  
  public Object[] keys() { return m.keys(); }
  public K[] keys(K[] array) { return m.keys(array); }
  
  public TLongCollection valueCollection() {
    if (values == null)
      values = TCollections.unmodifiableCollection(m.valueCollection());
    return values; }
  
  public long[] values() { return m.values(); }
  public long[] values(long[] array) { return m.values(array); }
  
  public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
  public int hashCode() { return m.hashCode(); }
  public String toString() { return m.toString(); }
  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return m.forEachKey(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
  
  public boolean forEachEntry(TObjectLongProcedure<? super K> procedure) {
    return m.forEachEntry(procedure);
  }
  
  public TObjectLongIterator<K> iterator() {
    new TObjectLongIterator() {
      TObjectLongIterator<K> iter = m.iterator();
      
      public K key() { return iter.key(); }
      public long value() { return iter.value(); }
      public void advance() { iter.advance(); }
      public boolean hasNext() { return iter.hasNext(); }
      public long setValue(long val) { throw new UnsupportedOperationException(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  
  public long putIfAbsent(K key, long value) { throw new UnsupportedOperationException(); }
  public void transformValues(TLongFunction function) { throw new UnsupportedOperationException(); }
  public boolean retainEntries(TObjectLongProcedure<? super K> procedure) { throw new UnsupportedOperationException(); }
  public boolean increment(K key) { throw new UnsupportedOperationException(); }
  public boolean adjustValue(K key, long amount) { throw new UnsupportedOperationException(); }
  public long adjustOrPutValue(K key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
}
