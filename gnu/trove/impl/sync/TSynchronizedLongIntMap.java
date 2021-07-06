package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedLongIntMap
  implements TLongIntMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TLongIntMap m;
  final Object mutex;
  
  public TSynchronizedLongIntMap(TLongIntMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedLongIntMap(TLongIntMap m, Object mutex) {
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
  
  public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public int get(long key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public int put(long key, int value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public int remove(long key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Long, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TLongIntMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TLongSet keySet = null;
  private transient TIntCollection values = null;
  
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
  
  public TIntCollection valueCollection() { synchronized (mutex) {
      if (values == null)
        values = new TSynchronizedIntCollection(m.valueCollection(), mutex);
      return values;
    }
  }
  
  public int[] values() { synchronized (mutex) { return m.values();
    } }
  
  public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TLongIntIterator iterator() { return m.iterator(); }
  


  public long getNoEntryKey() { return m.getNoEntryKey(); }
  public int getNoEntryValue() { return m.getNoEntryValue(); }
  
  public int putIfAbsent(long key, int value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TLongIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TIntFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TLongIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(long key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public int adjustOrPutValue(long key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
