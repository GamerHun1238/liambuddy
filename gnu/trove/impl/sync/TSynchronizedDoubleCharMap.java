package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedDoubleCharMap
  implements TDoubleCharMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TDoubleCharMap m;
  final Object mutex;
  
  public TSynchronizedDoubleCharMap(TDoubleCharMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedDoubleCharMap(TDoubleCharMap m, Object mutex) {
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
  
  public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public char get(double key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public char put(double key, char value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public char remove(double key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Double, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TDoubleCharMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TDoubleSet keySet = null;
  private transient TCharCollection values = null;
  
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
  
  public TCharCollection valueCollection() { synchronized (mutex) {
      if (values == null)
        values = new TSynchronizedCharCollection(m.valueCollection(), mutex);
      return values;
    }
  }
  
  public char[] values() { synchronized (mutex) { return m.values();
    } }
  
  public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TDoubleCharIterator iterator() { return m.iterator(); }
  


  public double getNoEntryKey() { return m.getNoEntryKey(); }
  public char getNoEntryValue() { return m.getNoEntryValue(); }
  
  public char putIfAbsent(double key, char value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TDoubleCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TCharFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TDoubleCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(double key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public char adjustOrPutValue(double key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
