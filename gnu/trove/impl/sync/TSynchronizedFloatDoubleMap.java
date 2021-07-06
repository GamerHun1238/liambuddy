package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedFloatDoubleMap
  implements TFloatDoubleMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TFloatDoubleMap m;
  final Object mutex;
  
  public TSynchronizedFloatDoubleMap(TFloatDoubleMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedFloatDoubleMap(TFloatDoubleMap m, Object mutex) {
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
  
  public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public double get(float key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public double put(float key, double value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public double remove(float key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Float, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TFloatDoubleMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TFloatSet keySet = null;
  private transient TDoubleCollection values = null;
  
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
  
  public TDoubleCollection valueCollection() { synchronized (mutex) {
      if (values == null)
        values = new TSynchronizedDoubleCollection(m.valueCollection(), mutex);
      return values;
    }
  }
  
  public double[] values() { synchronized (mutex) { return m.values();
    } }
  
  public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TFloatDoubleIterator iterator() { return m.iterator(); }
  


  public float getNoEntryKey() { return m.getNoEntryKey(); }
  public double getNoEntryValue() { return m.getNoEntryValue(); }
  
  public double putIfAbsent(float key, double value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TFloatDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TFloatDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(float key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public double adjustOrPutValue(float key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
