package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;




































public class TSynchronizedIntIntMap
  implements TIntIntMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TIntIntMap m;
  final Object mutex;
  
  public TSynchronizedIntIntMap(TIntIntMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedIntIntMap(TIntIntMap m, Object mutex) {
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
  
  public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public int get(int key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public int put(int key, int value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public int remove(int key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Integer, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TIntIntMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TIntSet keySet = null;
  private transient TIntCollection values = null;
  
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
  
  public TIntIntIterator iterator() { return m.iterator(); }
  


  public int getNoEntryKey() { return m.getNoEntryKey(); }
  public int getNoEntryValue() { return m.getNoEntryValue(); }
  
  public int putIfAbsent(int key, int value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TIntIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TIntFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TIntIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(int key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public int adjustOrPutValue(int key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
