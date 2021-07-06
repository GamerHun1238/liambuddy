package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;








































public class TSynchronizedFloatCollection
  implements TFloatCollection, Serializable
{
  private static final long serialVersionUID = 3053995032091335093L;
  final TFloatCollection c;
  final Object mutex;
  
  public TSynchronizedFloatCollection(TFloatCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
    mutex = this;
  }
  
  public TSynchronizedFloatCollection(TFloatCollection c, Object mutex) { this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return c.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
    } }
  
  public boolean contains(float o) { synchronized (mutex) { return c.contains(o);
    } }
  
  public float[] toArray() { synchronized (mutex) { return c.toArray();
    } }
  
  public float[] toArray(float[] a) { synchronized (mutex) { return c.toArray(a);
    }
  }
  
  public TFloatIterator iterator() { return c.iterator(); }
  
  public boolean add(float e)
  {
    synchronized (mutex) { return c.add(e);
    } }
  
  public boolean remove(float o) { synchronized (mutex) { return c.remove(o);
    }
  }
  
  public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(TFloatCollection coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(float[] array) { synchronized (mutex) { return c.containsAll(array);
    }
  }
  
  public boolean addAll(Collection<? extends Float> coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(TFloatCollection coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(float[] array) { synchronized (mutex) { return c.addAll(array);
    }
  }
  
  public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(TFloatCollection coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(float[] array) { synchronized (mutex) { return c.removeAll(array);
    }
  }
  
  public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(TFloatCollection coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(float[] array) { synchronized (mutex) { return c.retainAll(array);
    } }
  
  public float getNoEntryValue() { return c.getNoEntryValue(); }
  
  public boolean forEach(TFloatProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
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
