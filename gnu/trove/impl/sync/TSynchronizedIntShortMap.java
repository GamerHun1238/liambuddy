package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedIntShortMap
  implements TIntShortMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TIntShortMap m;
  final Object mutex;
  
  public TSynchronizedIntShortMap(TIntShortMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedIntShortMap(TIntShortMap m, Object mutex) {
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
  
  public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public short get(int key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public short put(int key, short value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public short remove(int key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Integer, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TIntShortMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TIntSet keySet = null;
  private transient TShortCollection values = null;
  
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
  
  public TIntShortIterator iterator() { return m.iterator(); }
  


  public int getNoEntryKey() { return m.getNoEntryKey(); }
  public short getNoEntryValue() { return m.getNoEntryValue(); }
  
  public short putIfAbsent(int key, short value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TIntShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TShortFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TIntShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(int key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public short adjustOrPutValue(int key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
