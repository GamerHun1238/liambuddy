package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.map.TIntByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedIntByteMap
  implements TIntByteMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TIntByteMap m;
  final Object mutex;
  
  public TSynchronizedIntByteMap(TIntByteMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedIntByteMap(TIntByteMap m, Object mutex) {
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
  
  public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public byte get(int key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public byte put(int key, byte value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public byte remove(int key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Integer, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TIntByteMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TIntSet keySet = null;
  private transient TByteCollection values = null;
  
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
  
  public TByteCollection valueCollection() { synchronized (mutex) {
      if (values == null)
        values = new TSynchronizedByteCollection(m.valueCollection(), mutex);
      return values;
    }
  }
  
  public byte[] values() { synchronized (mutex) { return m.values();
    } }
  
  public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
    }
  }
  
  public TIntByteIterator iterator() { return m.iterator(); }
  


  public int getNoEntryKey() { return m.getNoEntryKey(); }
  public byte getNoEntryValue() { return m.getNoEntryValue(); }
  
  public byte putIfAbsent(int key, byte value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TIntByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TByteFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TIntByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(int key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public byte adjustOrPutValue(int key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
