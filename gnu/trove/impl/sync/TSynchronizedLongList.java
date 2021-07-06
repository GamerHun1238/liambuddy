package gnu.trove.impl.sync;

import gnu.trove.function.TLongFunction;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;
import java.util.RandomAccess;











































public class TSynchronizedLongList
  extends TSynchronizedLongCollection
  implements TLongList
{
  static final long serialVersionUID = -7754090372962971524L;
  final TLongList list;
  
  public TSynchronizedLongList(TLongList list)
  {
    super(list);
    this.list = list;
  }
  
  public TSynchronizedLongList(TLongList list, Object mutex) { super(list, mutex);
    this.list = list;
  }
  
  public boolean equals(Object o) {
    synchronized (mutex) { return list.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return list.hashCode();
    }
  }
  
  public long get(int index) { synchronized (mutex) { return list.get(index);
    } }
  
  public long set(int index, long element) { synchronized (mutex) { return list.set(index, element);
    } }
  
  public void set(int offset, long[] values) { synchronized (mutex) { list.set(offset, values);
    } }
  
  public void set(int offset, long[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
    }
  }
  
  public long replace(int offset, long val) { synchronized (mutex) { return list.replace(offset, val);
    } }
  
  public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
    } }
  
  public long removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
    }
  }
  
  public void add(long[] vals) { synchronized (mutex) { list.add(vals);
    } }
  
  public void add(long[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
    }
  }
  
  public void insert(int offset, long value) { synchronized (mutex) { list.insert(offset, value);
    } }
  
  public void insert(int offset, long[] values) { synchronized (mutex) { list.insert(offset, values);
    } }
  
  public void insert(int offset, long[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
    }
  }
  
  public int indexOf(long o) { synchronized (mutex) { return list.indexOf(o);
    } }
  
  public int lastIndexOf(long o) { synchronized (mutex) { return list.lastIndexOf(o);
    }
  }
  






  public TLongList subList(int fromIndex, int toIndex)
  {
    synchronized (mutex) {
      return new TSynchronizedLongList(list.subList(fromIndex, toIndex), mutex);
    }
  }
  
  public long[] toArray(int offset, int len)
  {
    synchronized (mutex) { return list.toArray(offset, len);
    } }
  
  public long[] toArray(long[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
    } }
  
  public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
    }
  }
  
  public int indexOf(int offset, long value) { synchronized (mutex) { return list.indexOf(offset, value);
    } }
  
  public int lastIndexOf(int offset, long value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
    }
  }
  
  public void fill(long val) { synchronized (mutex) { list.fill(val);
    } }
  
  public void fill(int fromIndex, int toIndex, long val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
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
  
  public int binarySearch(long value) { synchronized (mutex) { return list.binarySearch(value);
    } }
  
  public int binarySearch(long value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
    }
  }
  
  public TLongList grep(TLongProcedure condition) { synchronized (mutex) { return list.grep(condition);
    } }
  
  public TLongList inverseGrep(TLongProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
    } }
  
  public long max() { synchronized (mutex) { return list.max(); } }
  public long min() { synchronized (mutex) { return list.min(); } }
  public long sum() { synchronized (mutex) { return list.sum();
    } }
  
  public boolean forEachDescending(TLongProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
    }
  }
  
  public void transformValues(TLongFunction function) { synchronized (mutex) { list.transformValues(function);
    }
  }
  










  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TSynchronizedRandomAccessLongList(list) : this;
  }
}
