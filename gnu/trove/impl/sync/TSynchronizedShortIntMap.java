package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.map.TShortIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedShortIntMap
  implements TShortIntMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TShortIntMap m;
  final Object mutex;
  
  public TSynchronizedShortIntMap(TShortIntMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedShortIntMap(TShortIntMap m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return m.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
    } }
  
  public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
    } }
  
  public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public int get(short key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public int put(short key, int value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public int remove(short key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Short, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TShortIntMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TShortSet keySet = null;
  private transient TIntCollection values = null;
  
  public TShortSet keySet() {
    synchronized (mutex) {
      if (keySet == null)
        keySet = new TSynchronizedShortSet(m.keySet(), mutex);
      return keySet;
    }
  }
  
  public short[] keys() { synchronized (mutex) { return m.keys();
    } }
  
  public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
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
  
  public TShortIntIterator iterator() { return m.iterator(); }
  


  public short getNoEntryKey() { return m.getNoEntryKey(); }
  public int getNoEntryValue() { return m.getNoEntryValue(); }
  
  public int putIfAbsent(short key, int value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TShortIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TIntFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TShortIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(short key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public int adjustOrPutValue(short key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
