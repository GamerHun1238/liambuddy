package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedDoubleShortMap
  implements TDoubleShortMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TDoubleShortMap m;
  final Object mutex;
  
  public TSynchronizedDoubleShortMap(TDoubleShortMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedDoubleShortMap(TDoubleShortMap m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return m.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
    } }
  
  public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
    } }
  
  public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public short get(double key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public short put(double key, short value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public short remove(double key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Double, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TDoubleShortMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TDoubleSet keySet = null;
  private transient TShortCollection values = null;
  
  public TDoubleSet keySet() {
    synchronized (mutex) {
      if (keySet == null)
        keySet = new TSynchronizedDoubleSet(m.keySet(), mutex);
      return keySet;
    }
  }
  
  public double[] keys() { synchronized (mutex) { return m.keys();
    } }
  
  public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
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
  
  public TDoubleShortIterator iterator() { return m.iterator(); }
  


  public double getNoEntryKey() { return m.getNoEntryKey(); }
  public short getNoEntryValue() { return m.getNoEntryValue(); }
  
  public short putIfAbsent(double key, short value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TDoubleShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TShortFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TDoubleShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(double key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public short adjustOrPutValue(double key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
