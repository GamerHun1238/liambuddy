package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedCharDoubleMap
  implements TCharDoubleMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TCharDoubleMap m;
  final Object mutex;
  
  public TSynchronizedCharDoubleMap(TCharDoubleMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedCharDoubleMap(TCharDoubleMap m, Object mutex) {
    this.m = m;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return m.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
    } }
  
  public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
    } }
  
  public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public double get(char key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public double put(char key, double value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public double remove(char key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Character, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TCharDoubleMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TCharSet keySet = null;
  private transient TDoubleCollection values = null;
  
  public TCharSet keySet() {
    synchronized (mutex) {
      if (keySet == null)
        keySet = new TSynchronizedCharSet(m.keySet(), mutex);
      return keySet;
    }
  }
  
  public char[] keys() { synchronized (mutex) { return m.keys();
    } }
  
  public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
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
  
  public TCharDoubleIterator iterator() { return m.iterator(); }
  


  public char getNoEntryKey() { return m.getNoEntryKey(); }
  public double getNoEntryValue() { return m.getNoEntryValue(); }
  
  public double putIfAbsent(char key, double value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TCharDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TCharDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(char key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public double adjustOrPutValue(char key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
