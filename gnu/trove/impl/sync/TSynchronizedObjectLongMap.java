package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;



































public class TSynchronizedObjectLongMap<K>
  implements TObjectLongMap<K>, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TObjectLongMap<K> m;
  final Object mutex;
  
  public TSynchronizedObjectLongMap(TObjectLongMap<K> m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedObjectLongMap(TObjectLongMap<K> m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return m.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
    } }
  
  public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
    } }
  
  public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public long get(Object key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public long put(K key, long value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public long remove(Object key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends K, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TObjectLongMap<? extends K> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient Set<K> keySet = null;
  private transient TLongCollection values = null;
  
  public Set<K> keySet() {
    synchronized (mutex) {
      if (keySet == null) {
        keySet = new SynchronizedSet(m.keySet(), mutex);
      }
      return keySet;
    }
  }
  
  public Object[] keys() { synchronized (mutex) { return m.keys();
    } }
  
  public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
    }
  }
  
  public TLongCollection valueCollection() { synchronized (mutex) {
      if (values == null)
        values = new TSynchronizedLongCollection(m.valueCollection(), mutex);
      return values;
    }
  }
  
  public long[] values() { synchronized (mutex) { return m.values();
    } }
  
  public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TObjectLongIterator<K> iterator() { return m.iterator(); }
  


  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public long putIfAbsent(K key, long value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TObjectLongProcedure<? super K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TLongFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TObjectLongProcedure<? super K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(K key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public long adjustOrPutValue(K key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
    }
  }
  
  public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return m.hashCode();
    } }
  
  public String toString() { synchronized (mutex) { return m.toString();
    } }
  
  private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
    }
  }
}
