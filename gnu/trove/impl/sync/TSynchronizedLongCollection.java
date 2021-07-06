package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;








































public class TSynchronizedLongCollection
  implements TLongCollection, Serializable
{
  private static final long serialVersionUID = 3053995032091335093L;
  final TLongCollection c;
  final Object mutex;
  
  public TSynchronizedLongCollection(TLongCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
    mutex = this;
  }
  
  public TSynchronizedLongCollection(TLongCollection c, Object mutex) { this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return c.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
    } }
  
  public boolean contains(long o) { synchronized (mutex) { return c.contains(o);
    } }
  
  public long[] toArray() { synchronized (mutex) { return c.toArray();
    } }
  
  public long[] toArray(long[] a) { synchronized (mutex) { return c.toArray(a);
    }
  }
  
  public TLongIterator iterator() { return c.iterator(); }
  
  public boolean add(long e)
  {
    synchronized (mutex) { return c.add(e);
    } }
  
  public boolean remove(long o) { synchronized (mutex) { return c.remove(o);
    }
  }
  
  public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(TLongCollection coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(long[] array) { synchronized (mutex) { return c.containsAll(array);
    }
  }
  
  public boolean addAll(Collection<? extends Long> coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(TLongCollection coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(long[] array) { synchronized (mutex) { return c.addAll(array);
    }
  }
  
  public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(TLongCollection coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(long[] array) { synchronized (mutex) { return c.removeAll(array);
    }
  }
  
  public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(TLongCollection coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(long[] array) { synchronized (mutex) { return c.retainAll(array);
    } }
  
  public long getNoEntryValue() { return c.getNoEntryValue(); }
  
  public boolean forEach(TLongProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
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
