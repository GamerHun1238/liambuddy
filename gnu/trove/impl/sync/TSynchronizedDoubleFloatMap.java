package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedDoubleFloatMap
  implements TDoubleFloatMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TDoubleFloatMap m;
  final Object mutex;
  
  public TSynchronizedDoubleFloatMap(TDoubleFloatMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedDoubleFloatMap(TDoubleFloatMap m, Object mutex) {
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
  
  public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public float get(double key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public float put(double key, float value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public float remove(double key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Double, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TDoubleFloatMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TDoubleSet keySet = null;
  private transient TFloatCollection values = null;
  
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
  
  public TFloatCollection valueCollection() { synchronized (mutex) {
      if (values == null)
        values = new TSynchronizedFloatCollection(m.valueCollection(), mutex);
      return values;
    }
  }
  
  public float[] values() { synchronized (mutex) { return m.values();
    } }
  
  public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TDoubleFloatIterator iterator() { return m.iterator(); }
  


  public double getNoEntryKey() { return m.getNoEntryKey(); }
  public float getNoEntryValue() { return m.getNoEntryValue(); }
  
  public float putIfAbsent(double key, float value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TDoubleFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TDoubleFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(double key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public float adjustOrPutValue(double key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
