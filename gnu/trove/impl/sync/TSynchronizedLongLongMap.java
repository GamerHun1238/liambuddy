package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;




































public class TSynchronizedLongLongMap
  implements TLongLongMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TLongLongMap m;
  final Object mutex;
  
  public TSynchronizedLongLongMap(TLongLongMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedLongLongMap(TLongLongMap m, Object mutex) {
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
  
  public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public long get(long key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public long put(long key, long value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public long remove(long key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Long, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TLongLongMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TLongSet keySet = null;
  private transient TLongCollection values = null;
  
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
  
  public TLongLongIterator iterator() { return m.iterator(); }
  


  public long getNoEntryKey() { return m.getNoEntryKey(); }
  public long getNoEntryValue() { return m.getNoEntryValue(); }
  
  public long putIfAbsent(long key, long value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TLongLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TLongFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TLongLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(long key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public long adjustOrPutValue(long key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
