package gnu.trove.impl.sync;

import gnu.trove.function.TShortFunction;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;
import java.util.RandomAccess;











































public class TSynchronizedShortList
  extends TSynchronizedShortCollection
  implements TShortList
{
  static final long serialVersionUID = -7754090372962971524L;
  final TShortList list;
  
  public TSynchronizedShortList(TShortList list)
  {
    super(list);
    this.list = list;
  }
  
  public TSynchronizedShortList(TShortList list, Object mutex) { super(list, mutex);
    this.list = list;
  }
  
  public boolean equals(Object o) {
    synchronized (mutex) { return list.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return list.hashCode();
    }
  }
  
  public short get(int index) { synchronized (mutex) { return list.get(index);
    } }
  
  public short set(int index, short element) { synchronized (mutex) { return list.set(index, element);
    } }
  
  public void set(int offset, short[] values) { synchronized (mutex) { list.set(offset, values);
    } }
  
  public void set(int offset, short[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
    }
  }
  
  public short replace(int offset, short val) { synchronized (mutex) { return list.replace(offset, val);
    } }
  
  public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
    } }
  
  public short removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
    }
  }
  
  public void add(short[] vals) { synchronized (mutex) { list.add(vals);
    } }
  
  public void add(short[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
    }
  }
  
  public void insert(int offset, short value) { synchronized (mutex) { list.insert(offset, value);
    } }
  
  public void insert(int offset, short[] values) { synchronized (mutex) { list.insert(offset, values);
    } }
  
  public void insert(int offset, short[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
    }
  }
  
  public int indexOf(short o) { synchronized (mutex) { return list.indexOf(o);
    } }
  
  public int lastIndexOf(short o) { synchronized (mutex) { return list.lastIndexOf(o);
    }
  }
  






  public TShortList subList(int fromIndex, int toIndex)
  {
    synchronized (mutex) {
      return new TSynchronizedShortList(list.subList(fromIndex, toIndex), mutex);
    }
  }
  
  public short[] toArray(int offset, int len)
  {
    synchronized (mutex) { return list.toArray(offset, len);
    } }
  
  public short[] toArray(short[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
    } }
  
  public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
    }
  }
  
  public int indexOf(int offset, short value) { synchronized (mutex) { return list.indexOf(offset, value);
    } }
  
  public int lastIndexOf(int offset, short value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
    }
  }
  
  public void fill(short val) { synchronized (mutex) { list.fill(val);
    } }
  
  public void fill(int fromIndex, int toIndex, short val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
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
  
  public int binarySearch(short value) { synchronized (mutex) { return list.binarySearch(value);
    } }
  
  public int binarySearch(short value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
    }
  }
  
  public TShortList grep(TShortProcedure condition) { synchronized (mutex) { return list.grep(condition);
    } }
  
  public TShortList inverseGrep(TShortProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
    } }
  
  public short max() { synchronized (mutex) { return list.max(); } }
  public short min() { synchronized (mutex) { return list.min(); } }
  public short sum() { synchronized (mutex) { return list.sum();
    } }
  
  public boolean forEachDescending(TShortProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
    }
  }
  
  public void transformValues(TShortFunction function) { synchronized (mutex) { list.transformValues(function);
    }
  }
  










  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TSynchronizedRandomAccessShortList(list) : this;
  }
}
