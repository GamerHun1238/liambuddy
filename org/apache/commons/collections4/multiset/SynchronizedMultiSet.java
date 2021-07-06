package org.apache.commons.collections4.multiset;

import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSet.Entry;
import org.apache.commons.collections4.collection.SynchronizedCollection;

































public class SynchronizedMultiSet<E>
  extends SynchronizedCollection<E>
  implements MultiSet<E>
{
  private static final long serialVersionUID = 20150629L;
  
  public static <E> SynchronizedMultiSet<E> synchronizedMultiSet(MultiSet<E> multiset)
  {
    return new SynchronizedMultiSet(multiset);
  }
  






  protected SynchronizedMultiSet(MultiSet<E> multiset)
  {
    super(multiset);
  }
  






  protected SynchronizedMultiSet(MultiSet<E> multiset, Object lock)
  {
    super(multiset, lock);
  }
  





  protected MultiSet<E> decorated()
  {
    return (MultiSet)super.decorated();
  }
  
  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    synchronized (lock) {
      return decorated().equals(object);
    }
  }
  
  public int hashCode()
  {
    synchronized (lock) {
      return decorated().hashCode();
    }
  }
  


  public int add(E object, int count)
  {
    synchronized (lock) {
      return decorated().add(object, count);
    }
  }
  
  public int remove(Object object, int count)
  {
    synchronized (lock) {
      return decorated().remove(object, count);
    }
  }
  
  public int getCount(Object object)
  {
    synchronized (lock) {
      return decorated().getCount(object);
    }
  }
  
  public int setCount(E object, int count)
  {
    synchronized (lock) {
      return decorated().setCount(object, count);
    }
  }
  
  public Set<E> uniqueSet()
  {
    synchronized (lock) {
      Set<E> set = decorated().uniqueSet();
      return new SynchronizedSet(set, lock);
    }
  }
  
  public Set<MultiSet.Entry<E>> entrySet()
  {
    synchronized (lock) {
      Set<MultiSet.Entry<E>> set = decorated().entrySet();
      return new SynchronizedSet(set, lock);
    }
  }
  



  static class SynchronizedSet<T>
    extends SynchronizedCollection<T>
    implements Set<T>
  {
    private static final long serialVersionUID = 20150629L;
    



    SynchronizedSet(Set<T> set, Object lock)
    {
      super(lock);
    }
  }
}
