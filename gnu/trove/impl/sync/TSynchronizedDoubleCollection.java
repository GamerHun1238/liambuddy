package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;








































public class TSynchronizedDoubleCollection
  implements TDoubleCollection, Serializable
{
  private static final long serialVersionUID = 3053995032091335093L;
  final TDoubleCollection c;
  final Object mutex;
  
  public TSynchronizedDoubleCollection(TDoubleCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
    mutex = this;
  }
  
  public TSynchronizedDoubleCollection(TDoubleCollection c, Object mutex) { this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return c.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
    } }
  
  public boolean contains(double o) { synchronized (mutex) { return c.contains(o);
    } }
  
  public double[] toArray() { synchronized (mutex) { return c.toArray();
    } }
  
  public double[] toArray(double[] a) { synchronized (mutex) { return c.toArray(a);
    }
  }
  
  public TDoubleIterator iterator() { return c.iterator(); }
  
  public boolean add(double e)
  {
    synchronized (mutex) { return c.add(e);
    } }
  
  public boolean remove(double o) { synchronized (mutex) { return c.remove(o);
    }
  }
  
  public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(TDoubleCollection coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(double[] array) { synchronized (mutex) { return c.containsAll(array);
    }
  }
  
  public boolean addAll(Collection<? extends Double> coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(TDoubleCollection coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(double[] array) { synchronized (mutex) { return c.addAll(array);
    }
  }
  
  public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(TDoubleCollection coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(double[] array) { synchronized (mutex) { return c.removeAll(array);
    }
  }
  
  public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(TDoubleCollection coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(double[] array) { synchronized (mutex) { return c.retainAll(array);
    } }
  
  public double getNoEntryValue() { return c.getNoEntryValue(); }
  
  public boolean forEach(TDoubleProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
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
