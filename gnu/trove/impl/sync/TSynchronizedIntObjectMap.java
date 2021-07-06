package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;





































public class TSynchronizedIntObjectMap<V>
  implements TIntObjectMap<V>, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TIntObjectMap<V> m;
  final Object mutex;
  
  public TSynchronizedIntObjectMap(TIntObjectMap<V> m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedIntObjectMap(TIntObjectMap<V> m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return m.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
    } }
  
  public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
    } }
  
  public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public V get(int key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public V put(int key, V value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public V remove(int key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Integer, ? extends V> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TIntObjectMap<? extends V> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TIntSet keySet = null;
  private transient Collection<V> values = null;
  
  public TIntSet keySet() {
    synchronized (mutex) {
      if (keySet == null)
        keySet = new TSynchronizedIntSet(m.keySet(), mutex);
      return keySet;
    }
  }
  
  public int[] keys() { synchronized (mutex) { return m.keys();
    } }
  
  public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
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
  
  public TIntObjectIterator<V> iterator() { return m.iterator(); }
  


  public int getNoEntryKey() { return m.getNoEntryKey(); }
  
  public V putIfAbsent(int key, V value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TIntObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TIntObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
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
