package org.apache.commons.collections4.bag;

import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.collection.SynchronizedCollection;





































public class SynchronizedBag<E>
  extends SynchronizedCollection<E>
  implements Bag<E>
{
  private static final long serialVersionUID = 8084674570753837109L;
  
  public static <E> SynchronizedBag<E> synchronizedBag(Bag<E> bag)
  {
    return new SynchronizedBag(bag);
  }
  






  protected SynchronizedBag(Bag<E> bag)
  {
    super(bag);
  }
  






  protected SynchronizedBag(Bag<E> bag, Object lock)
  {
    super(bag, lock);
  }
  




  protected Bag<E> getBag()
  {
    return (Bag)decorated();
  }
  
  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    synchronized (lock) {
      return getBag().equals(object);
    }
  }
  
  public int hashCode()
  {
    synchronized (lock) {
      return getBag().hashCode();
    }
  }
  


  public boolean add(E object, int count)
  {
    synchronized (lock) {
      return getBag().add(object, count);
    }
  }
  
  public boolean remove(Object object, int count)
  {
    synchronized (lock) {
      return getBag().remove(object, count);
    }
  }
  
  public Set<E> uniqueSet()
  {
    synchronized (lock) {
      Set<E> set = getBag().uniqueSet();
      return new SynchronizedBagSet(set, lock);
    }
  }
  
  public int getCount(Object object)
  {
    synchronized (lock) {
      return getBag().getCount(object);
    }
  }
  



  class SynchronizedBagSet
    extends SynchronizedCollection<E>
    implements Set<E>
  {
    private static final long serialVersionUID = 2990565892366827855L;
    



    SynchronizedBagSet(Object set)
    {
      super(lock);
    }
  }
}
