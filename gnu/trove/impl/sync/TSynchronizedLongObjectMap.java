package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;





































public class TSynchronizedLongObjectMap<V>
  implements TLongObjectMap<V>, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TLongObjectMap<V> m;
  final Object mutex;
  
  public TSynchronizedLongObjectMap(TLongObjectMap<V> m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedLongObjectMap(TLongObjectMap<V> m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return m.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
    } }
  
  public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
    } }
  
  public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public V get(long key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public V put(long key, V value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public V remove(long key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Long, ? extends V> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TLongObjectMap<? extends V> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TLongSet keySet = null;
  private transient Collection<V> values = null;
  
  public TLongSet keySet() {
    synchronized (mutex) {
      if (keySet == null)
        keySet = new TSynchronizedLongSet(m.keySet(), mutex);
      return keySet;
    }
  }
  
  public long[] keys() { synchronized (mutex) { return m.keys();
    } }
  
  public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
    }
  }
  
  public Collection<V> valueCollection() { synchronized (mutex) {
      if (values == null) {
        values = new SynchronizedCollection(m.valueCollection(), mutex);
      }
      return values;
    }
  }
  
  public Object[] values() { synchronized (mutex) { return m.values();
    } }
  
  public V[] values(V[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TLongObjectIterator<V> iterator() { return m.iterator(); }
  


  public long getNoEntryKey() { return m.getNoEntryKey(); }
  
  public V putIfAbsent(long key, V value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TLongObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TLongObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
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
