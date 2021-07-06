package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedShortFloatMap
  implements TShortFloatMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TShortFloatMap m;
  final Object mutex;
  
  public TSynchronizedShortFloatMap(TShortFloatMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedShortFloatMap(TShortFloatMap m, Object mutex) {
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
  
  public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public float get(short key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public float put(short key, float value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public float remove(short key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Short, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TShortFloatMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TShortSet keySet = null;
  private transient TFloatCollection values = null;
  
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
  
  public TShortFloatIterator iterator() { return m.iterator(); }
  


  public short getNoEntryKey() { return m.getNoEntryKey(); }
  public float getNoEntryValue() { return m.getNoEntryValue(); }
  
  public float putIfAbsent(short key, float value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TShortFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TShortFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(short key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public float adjustOrPutValue(short key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
