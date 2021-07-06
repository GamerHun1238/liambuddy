package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;








































public class TSynchronizedCharCollection
  implements TCharCollection, Serializable
{
  private static final long serialVersionUID = 3053995032091335093L;
  final TCharCollection c;
  final Object mutex;
  
  public TSynchronizedCharCollection(TCharCollection c)
  {
    if (c == null)
      throw new NullPointerException();
    this.c = c;
    mutex = this;
  }
  
  public TSynchronizedCharCollection(TCharCollection c, Object mutex) { this.c = c;
    this.mutex = mutex;
  }
  
  public int size() {
    synchronized (mutex) { return c.size();
    } }
  
  public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
    } }
  
  public boolean contains(char o) { synchronized (mutex) { return c.contains(o);
    } }
  
  public char[] toArray() { synchronized (mutex) { return c.toArray();
    } }
  
  public char[] toArray(char[] a) { synchronized (mutex) { return c.toArray(a);
    }
  }
  
  public TCharIterator iterator() { return c.iterator(); }
  
  public boolean add(char e)
  {
    synchronized (mutex) { return c.add(e);
    } }
  
  public boolean remove(char o) { synchronized (mutex) { return c.remove(o);
    }
  }
  
  public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(TCharCollection coll) { synchronized (mutex) { return c.containsAll(coll);
    } }
  
  public boolean containsAll(char[] array) { synchronized (mutex) { return c.containsAll(array);
    }
  }
  
  public boolean addAll(Collection<? extends Character> coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(TCharCollection coll) { synchronized (mutex) { return c.addAll(coll);
    } }
  
  public boolean addAll(char[] array) { synchronized (mutex) { return c.addAll(array);
    }
  }
  
  public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(TCharCollection coll) { synchronized (mutex) { return c.removeAll(coll);
    } }
  
  public boolean removeAll(char[] array) { synchronized (mutex) { return c.removeAll(array);
    }
  }
  
  public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(TCharCollection coll) { synchronized (mutex) { return c.retainAll(coll);
    } }
  
  public boolean retainAll(char[] array) { synchronized (mutex) { return c.retainAll(array);
    } }
  
  public char getNoEntryValue() { return c.getNoEntryValue(); }
  
  public boolean forEach(TCharProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
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
