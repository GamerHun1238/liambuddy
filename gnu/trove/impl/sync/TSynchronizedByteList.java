package gnu.trove.impl.sync;

import gnu.trove.function.TByteFunction;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;











































public class TSynchronizedByteList
  extends TSynchronizedByteCollection
  implements TByteList
{
  static final long serialVersionUID = -7754090372962971524L;
  final TByteList list;
  
  public TSynchronizedByteList(TByteList list)
  {
    super(list);
    this.list = list;
  }
  
  public TSynchronizedByteList(TByteList list, Object mutex) { super(list, mutex);
    this.list = list;
  }
  
  public boolean equals(Object o) {
    synchronized (mutex) { return list.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return list.hashCode();
    }
  }
  
  public byte get(int index) { synchronized (mutex) { return list.get(index);
    } }
  
  public byte set(int index, byte element) { synchronized (mutex) { return list.set(index, element);
    } }
  
  public void set(int offset, byte[] values) { synchronized (mutex) { list.set(offset, values);
    } }
  
  public void set(int offset, byte[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
    }
  }
  
  public byte replace(int offset, byte val) { synchronized (mutex) { return list.replace(offset, val);
    } }
  
  public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
    } }
  
  public byte removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
    }
  }
  
  public void add(byte[] vals) { synchronized (mutex) { list.add(vals);
    } }
  
  public void add(byte[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
    }
  }
  
  public void insert(int offset, byte value) { synchronized (mutex) { list.insert(offset, value);
    } }
  
  public void insert(int offset, byte[] values) { synchronized (mutex) { list.insert(offset, values);
    } }
  
  public void insert(int offset, byte[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
    }
  }
  
  public int indexOf(byte o) { synchronized (mutex) { return list.indexOf(o);
    } }
  
  public int lastIndexOf(byte o) { synchronized (mutex) { return list.lastIndexOf(o);
    }
  }
  






  public TByteList subList(int fromIndex, int toIndex)
  {
    synchronized (mutex) {
      return new TSynchronizedByteList(list.subList(fromIndex, toIndex), mutex);
    }
  }
  
  public byte[] toArray(int offset, int len)
  {
    synchronized (mutex) { return list.toArray(offset, len);
    } }
  
  public byte[] toArray(byte[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
    } }
  
  public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
    }
  }
  
  public int indexOf(int offset, byte value) { synchronized (mutex) { return list.indexOf(offset, value);
    } }
  
  public int lastIndexOf(int offset, byte value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
    }
  }
  
  public void fill(byte val) { synchronized (mutex) { list.fill(val);
    } }
  
  public void fill(int fromIndex, int toIndex, byte val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
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
  
  public int binarySearch(byte value) { synchronized (mutex) { return list.binarySearch(value);
    } }
  
  public int binarySearch(byte value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
    }
  }
  
  public TByteList grep(TByteProcedure condition) { synchronized (mutex) { return list.grep(condition);
    } }
  
  public TByteList inverseGrep(TByteProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
    } }
  
  public byte max() { synchronized (mutex) { return list.max(); } }
  public byte min() { synchronized (mutex) { return list.min(); } }
  public byte sum() { synchronized (mutex) { return list.sum();
    } }
  
  public boolean forEachDescending(TByteProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
    }
  }
  
  public void transformValues(TByteFunction function) { synchronized (mutex) { list.transformValues(function);
    }
  }
  










  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TSynchronizedRandomAccessByteList(list) : this;
  }
}
