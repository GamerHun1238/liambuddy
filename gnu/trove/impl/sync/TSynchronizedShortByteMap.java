package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.map.TShortByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TShortByteProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;



































public class TSynchronizedShortByteMap
  implements TShortByteMap, Serializable
{
  private static final long serialVersionUID = 1978198479659022715L;
  private final TShortByteMap m;
  final Object mutex;
  
  public TSynchronizedShortByteMap(TShortByteMap m)
  {
    if (m == null)
      throw new NullPointerException();
    this.m = m;
    mutex = this;
  }
  
  public TSynchronizedShortByteMap(TShortByteMap m, Object mutex) {
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
  
  public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
    } }
  
  public byte get(short key) { synchronized (mutex) { return m.get(key);
    }
  }
  
  public byte put(short key, byte value) { synchronized (mutex) { return m.put(key, value);
    } }
  
  public byte remove(short key) { synchronized (mutex) { return m.remove(key);
    } }
  
  public void putAll(Map<? extends Short, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void putAll(TShortByteMap map) { synchronized (mutex) { m.putAll(map);
    } }
  
  public void clear() { synchronized (mutex) { m.clear();
    } }
  
  private transient TShortSet keySet = null;
  private transient TByteCollection values = null;
  
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
  
  public TShortByteIterator iterator() { return m.iterator(); }
  


  public short getNoEntryKey() { return m.getNoEntryKey(); }
  public byte getNoEntryValue() { return m.getNoEntryValue(); }
  
  public byte putIfAbsent(short key, byte value) {
    synchronized (mutex) { return m.putIfAbsent(key, value);
    } }
  
  public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
    } }
  
  public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
    } }
  
  public boolean forEachEntry(TShortByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
    } }
  
  public void transformValues(TByteFunction function) { synchronized (mutex) { m.transformValues(function);
    } }
  
  public boolean retainEntries(TShortByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
    } }
  
  public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
    } }
  
  public boolean adjustValue(short key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
    } }
  
  public byte adjustOrPutValue(short key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
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
