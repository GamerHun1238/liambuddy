package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;





































public class TSynchronizedFloatObjectMap<V>
  implements TFloatObjectMap<V>, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TFloatObjectMap<V> m;
  final Object mutex;
  
  public TSynchronizedFloatObjectMap(TFloatObjectMap<V> m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedFloatObjectMap(TFloatObjectMap<V> m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return m.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
    } }
  
  public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
    } }
  
  public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public V get(float key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public V put(float key, V value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public V remove(float key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Float, ? extends V> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TFloatObjectMap<? extends V> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TFloatSet keySet = null;
  private transient Collection<V> values = null;
  
  public TFloatSet keySet() {
    synchronized (mutex) {
      if (keySet == null)
        keySet = new TSynchronizedFloatSet(m.keySet(), mutex);
      return keySet;
    }
  }
  
  public float[] keys() { synchronized (mutex) { return m.keys();
    } }
  
  public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
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
  
  public TFloatObjectIterator<V> iterator() { return m.iterator(); }
  


  public float getNoEntryKey() { return m.getNoEntryKey(); }
  
  public V putIfAbsent(float key, V value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TFloatObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TFloatObjectProcedure<? super V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
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
