package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;








































public class TSynchronizedIntCollection
  implements TIntCollection, Serializable
{
  private static final long serialVersionUID = 3053995032091335093L;
  final TIntCollection c;
  final Object mutex;
  
  public TSynchronizedIntCollection(TIntCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
    mutex = this;
  }
  
  public TSynchronizedIntCollection(TIntCollection c, Object mutex) { this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return c.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
    } }
  
  public boolean contains(int o) { synchronized (mutex) { return c.contains(o);
    } }
  
  public int[] toArray() { synchronized (mutex) { return c.toArray();
    } }
  
  public int[] toArray(int[] a) { synchronized (mutex) { return c.toArray(a);
    }
  }
  
  public TIntIterator iterator() { return c.iterator(); }
  
  public boolean add(int e)
  {
    synchronized (mutex) { return c.add(e);
    } }
  
  public boolean remove(int o) { synchronized (mutex) { return c.remove(o);
    }
  }
  
  public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(TIntCollection coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(int[] array) { synchronized (mutex) { return c.containsAll(array);
    }
  }
  
  public boolean addAll(Collection<? extends Integer> coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(TIntCollection coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(int[] array) { synchronized (mutex) { return c.addAll(array);
    }
  }
  
  public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(TIntCollection coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(int[] array) { synchronized (mutex) { return c.removeAll(array);
    }
  }
  
  public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(TIntCollection coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(int[] array) { synchronized (mutex) { return c.retainAll(array);
    } }
  
  public int getNoEntryValue() { return c.getNoEntryValue(); }
  
  public boolean forEach(TIntProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
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
