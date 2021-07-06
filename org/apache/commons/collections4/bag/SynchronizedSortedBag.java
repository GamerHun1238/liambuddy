package org.apache.commons.collections4.bag;

import java.util.Comparator;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.SortedBag;





































public class SynchronizedSortedBag<E>
  extends SynchronizedBag<E>
  implements SortedBag<E>
{
  private static final long serialVersionUID = 722374056718497858L;
  
  public static <E> SynchronizedSortedBag<E> synchronizedSortedBag(SortedBag<E> bag)
  {
    return new SynchronizedSortedBag(bag);
  }
  






  protected SynchronizedSortedBag(SortedBag<E> bag)
  {
    super(bag);
  }
  






  protected SynchronizedSortedBag(Bag<E> bag, Object lock)
  {
    super(bag, lock);
  }
  




  protected SortedBag<E> getSortedBag()
  {
    return (SortedBag)decorated();
  }
  


  public synchronized E first()
  {
    synchronized (lock) {
      return getSortedBag().first();
    }
  }
  
  public synchronized E last()
  {
    synchronized (lock) {
      return getSortedBag().last();
    }
  }
  
  public synchronized Comparator<? super E> comparator()
  {
    synchronized (lock) {
      return getSortedBag().comparator();
    }
  }
}
