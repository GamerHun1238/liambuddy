package gnu.trove.impl.sync;

import gnu.trove.function.TFloatFunction;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;











































public class TSynchronizedFloatList
  extends TSynchronizedFloatCollection
  implements TFloatList
{
  static final long serialVersionUID = -7754090372962971524L;
  final TFloatList list;
  
  public TSynchronizedFloatList(TFloatList list)
  {
    super(list);
    this.list = list;
  }
  
  public TSynchronizedFloatList(TFloatList list, Object mutex) { super(list, mutex);
    this.list = list;
  }
  
  public boolean equals(Object o) {
    synchronized (mutex) { return list.equals(o);
    } }
  
  public int hashCode() { synchronized (mutex) { return list.hashCode();
    }
  }
  
  public float get(int index) { synchronized (mutex) { return list.get(index);
    } }
  
  public float set(int index, float element) { synchronized (mutex) { return list.set(index, element);
    } }
  
  public void set(int offset, float[] values) { synchronized (mutex) { list.set(offset, values);
    } }
  
  public void set(int offset, float[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
    }
  }
  
  public float replace(int offset, float val) { synchronized (mutex) { return list.replace(offset, val);
    } }
  
  public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
    } }
  
  public float removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
    }
  }
  
  public void add(float[] vals) { synchronized (mutex) { list.add(vals);
    } }
  
  public void add(float[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
    }
  }
  
  public void insert(int offset, float value) { synchronized (mutex) { list.insert(offset, value);
    } }
  
  public void insert(int offset, float[] values) { synchronized (mutex) { list.insert(offset, values);
    } }
  
  public void insert(int offset, float[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
    }
  }
  
  public int indexOf(float o) { synchronized (mutex) { return list.indexOf(o);
    } }
  
  public int lastIndexOf(float o) { synchronized (mutex) { return list.lastIndexOf(o);
    }
  }
  






  public TFloatList subList(int fromIndex, int toIndex)
  {
    synchronized (mutex) {
      return new TSynchronizedFloatList(list.subList(fromIndex, toIndex), mutex);
    }
  }
  
  public float[] toArray(int offset, int len)
  {
    synchronized (mutex) { return list.toArray(offset, len);
    } }
  
  public float[] toArray(float[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
    } }
  
  public float[] toArray(float[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
    }
  }
  
  public int indexOf(int offset, float value) { synchronized (mutex) { return list.indexOf(offset, value);
    } }
  
  public int lastIndexOf(int offset, float value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
    }
  }
  
  public void fill(float val) { synchronized (mutex) { list.fill(val);
    } }
  
  public void fill(int fromIndex, int toIndex, float val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
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
  
  public int binarySearch(float value) { synchronized (mutex) { return list.binarySearch(value);
    } }
  
  public int binarySearch(float value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
    }
  }
  
  public TFloatList grep(TFloatProcedure condition) { synchronized (mutex) { return list.grep(condition);
    } }
  
  public TFloatList inverseGrep(TFloatProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
    } }
  
  public float max() { synchronized (mutex) { return list.max(); } }
  public float min() { synchronized (mutex) { return list.min(); } }
  public float sum() { synchronized (mutex) { return list.sum();
    } }
  
  public boolean forEachDescending(TFloatProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
    }
  }
  
  public void transformValues(TFloatFunction function) { synchronized (mutex) { list.transformValues(function);
    }
  }
  










  private Object readResolve()
  {
    return (list instanceof RandomAccess) ? new TSynchronizedRandomAccessFloatList(list) : this;
  }
}
