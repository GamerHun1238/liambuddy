package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;








































public class TSynchronizedShortCollection
  implements TShortCollection, Serializable
{
  private static final long serialVersionUID = 3053995032091335093L;
  final TShortCollection c;
  final Object mutex;
  
  public TSynchronizedShortCollection(TShortCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
    mutex = this;
  }
  
  public TSynchronizedShortCollection(TShortCollection c, Object mutex) { this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return c.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
    } }
  
  public boolean contains(short o) { synchronized (mutex) { return c.contains(o);
    } }
  
  public short[] toArray() { synchronized (mutex) { return c.toArray();
    } }
  
  public short[] toArray(short[] a) { synchronized (mutex) { return c.toArray(a);
    }
  }
  
  public TShortIterator iterator() { return c.iterator(); }
  
  public boolean add(short e)
  {
    synchronized (mutex) { return c.add(e);
    } }
  
  public boolean remove(short o) { synchronized (mutex) { return c.remove(o);
    }
  }
  
  public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(TShortCollection coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(short[] array) { synchronized (mutex) { return c.containsAll(array);
    }
  }
  
  public boolean addAll(Collection<? extends Short> coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(TShortCollection coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(short[] array) { synchronized (mutex) { return c.addAll(array);
    }
  }
  
  public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(TShortCollection coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(short[] array) { synchronized (mutex) { return c.removeAll(array);
    }
  }
  
  public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(TShortCollection coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(short[] array) { synchronized (mutex) { return c.retainAll(array);
    } }
  
  public short getNoEntryValue() { return c.getNoEntryValue(); }
  
  public boolean forEach(TShortProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
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
