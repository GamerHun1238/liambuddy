package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;



































public class TSynchronizedObjectShortMap<K>
  implements TObjectShortMap<K>, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TObjectShortMap<K> m;
  final Object mutex;
  
  public TSynchronizedObjectShortMap(TObjectShortMap<K> m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedObjectShortMap(TObjectShortMap<K> m, Object mutex) {
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
  
  public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public short get(Object key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public short put(K key, short value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public short remove(Object key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends K, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TObjectShortMap<? extends K> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient Set<K> keySet = null;
  private transient TShortCollection values = null;
  
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
  
  public TShortCollection valueCollection() { synchronized (mutex) {
      if (values == null)
        values = new TSynchronizedShortCollection(m.valueCollection(), mutex);
      return values;
    }
  }
  
  public short[] values() { synchronized (mutex) { return m.values();
    } }
  
  public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TObjectShortIterator<K> iterator() { return m.iterator(); }
  


  public short getNoEntryValue() { return m.getNoEntryValue(); }
  
  public short putIfAbsent(K key, short value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TObjectShortProcedure<? super K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TShortFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TObjectShortProcedure<? super K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(K key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public short adjustOrPutValue(K key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
