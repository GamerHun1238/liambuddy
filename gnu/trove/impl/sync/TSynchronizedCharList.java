package gnu.trove.impl.sync;

import gnu.trove.function.TCharFunction;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;
import java.util.RandomAccess;











































public class TSynchronizedCharList
  extends TSynchronizedCharCollection
  implements TCharList
{
  static final long serialVersionUID = -7754090372962971524L;
  final TCharList list;
  
  public TSynchronizedCharList(TCharList list)
  {
    super(list);
    this.list = list;
  }
  
  public TSynchronizedCharList(TCharList list, Object mutex) { super(list, mutex);
    this.list = list;
  }
  
  public boolean equals(Object o) {
    synchronized (mutex) { return list.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return list.hashCode();
    }
  }
  
  public char get(int index) { synchronized (mutex) { return list.get(index);
    } }
  
  public char set(int index, char element) { synchronized (mutex) { return list.set(index, element);
    } }
  
  public void set(int offset, char[] values) { synchronized (mutex) { list.set(offset, values);
    } }
  
  public void set(int offset, char[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
    }
  }
  
  public char replace(int offset, char val) { synchronized (mutex) { return list.replace(offset, val);
    } }
  
  public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
    } }
  
  public char removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
    }
  }
  
  public void add(char[] vals) { synchronized (mutex) { list.add(vals);
    } }
  
  public void add(char[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
    }
  }
  
  public void insert(int offset, char value) { synchronized (mutex) { list.insert(offset, value);
    } }
  
  public void insert(int offset, char[] values) { synchronized (mutex) { list.insert(offset, values);
    } }
  
  public void insert(int offset, char[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
    }
  }
  
  public int indexOf(char o) { synchronized (mutex) { return list.indexOf(o);
    } }
  
  public int lastIndexOf(char o) { synchronized (mutex) { return list.lastIndexOf(o);
    }
  }
  






  public TCharList subList(int fromIndex, int toIndex)
  {
    synchronized (mutex) {
      return new TSynchronizedCharList(list.subList(fromIndex, toIndex), mutex);
    }
  }
  
  public char[] toArray(int offset, int len)
  {
    synchronized (mutex) { return list.toArray(offset, len);
    } }
  
  public char[] toArray(char[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
    } }
  
  public char[] toArray(char[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
    }
  }
  
  public int indexOf(int offset, char value) { synchronized (mutex) { return list.indexOf(offset, value);
    } }
  
  public int lastIndexOf(int offset, char value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
    }
  }
  
  public void fill(char val) { synchronized (mutex) { list.fill(val);
    } }
  
  public void fill(int fromIndex, int toIndex, char val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
    }
  }
  
  public void reverse() { synchronized (mutex) { list.reverse();
    } }
  
  public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
    }
  }
  
  public void shuffle(Random rand) { synchronized (mutex) { list.shuffle(rand);
    }
  }
  
  public void sort() { synchronized (mutex) { list.sort();
    } }
  
  public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
    }
  }
  
  public int binarySearch(char value) { synchronized (mutex) { return list.binarySearch(value);
    } }
  
  public int binarySearch(char value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
    }
  }
  
  public TCharList grep(TCharProcedure condition) { synchronized (mutex) { return list.grep(condition);
    } }
  
  public TCharList inverseGrep(TCharProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
    } }
  
  public char max() { synchronized (mutex) { return list.max(); } }
  public char min() { synchronized (mutex) { return list.min(); } }
  public char sum() { synchronized (mutex) { return list.sum();
    } }
  
  public boolean forEachDescending(TCharProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
    }
  }
  
  public void transformValues(TCharFunction function) { synchronized (mutex) { list.transformValues(function);
    }
  }
  










  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TSynchronizedRandomAccessCharList(list) : this;
  }
}
