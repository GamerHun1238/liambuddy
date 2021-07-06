package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;








































public class TSynchronizedByteCollection
  implements TByteCollection, Serializable
{
  private static final long serialVersionUID = 3053995032091335093L;
  final TByteCollection c;
  final Object mutex;
  
  public TSynchronizedByteCollection(TByteCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
    mutex = this;
  }
  
  public TSynchronizedByteCollection(TByteCollection c, Object mutex) { this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return c.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
    } }
  
  public boolean contains(byte o) { synchronized (mutex) { return c.contains(o);
    } }
  
  public byte[] toArray() { synchronized (mutex) { return c.toArray();
    } }
  
  public byte[] toArray(byte[] a) { synchronized (mutex) { return c.toArray(a);
    }
  }
  
  public TByteIterator iterator() { return c.iterator(); }
  
  public boolean add(byte e)
  {
    synchronized (mutex) { return c.add(e);
    } }
  
  public boolean remove(byte o) { synchronized (mutex) { return c.remove(o);
    }
  }
  
  public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(TByteCollection coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(byte[] array) { synchronized (mutex) { return c.containsAll(array);
    }
  }
  
  public boolean addAll(Collection<? extends Byte> coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(TByteCollection coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(byte[] array) { synchronized (mutex) { return c.addAll(array);
    }
  }
  
  public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(TByteCollection coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(byte[] array) { synchronized (mutex) { return c.removeAll(array);
    }
  }
  
  public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(TByteCollection coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(byte[] array) { synchronized (mutex) { return c.retainAll(array);
    } }
  
  public byte getNoEntryValue() { return c.getNoEntryValue(); }
  
  public boolean forEach(TByteProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
    }
  }
  
  public void clear() { synchronized (mutex) { c.clear();
    } }
  
  public String toString() { synchronized (mutex) { return c.toString();
    } }
  
  private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
    }
  }
}
