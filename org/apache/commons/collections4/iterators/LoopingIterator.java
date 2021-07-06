package org.apache.commons.collections4.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableIterator;








































public class LoopingIterator<E>
  implements ResettableIterator<E>
{
  private final Collection<? extends E> collection;
  private Iterator<? extends E> iterator;
  
  public LoopingIterator(Collection<? extends E> coll)
  {
    if (coll == null) {
      throw new NullPointerException("The collection must not be null");
    }
    collection = coll;
    reset();
  }
  







  public boolean hasNext()
  {
    return collection.size() > 0;
  }
  








  public E next()
  {
    if (collection.size() == 0) {
      throw new NoSuchElementException("There are no elements for this iterator to loop on");
    }
    if (!iterator.hasNext()) {
      reset();
    }
    return iterator.next();
  }
  











  public void remove()
  {
    iterator.remove();
  }
  


  public void reset()
  {
    iterator = collection.iterator();
  }
  




  public int size()
  {
    return collection.size();
  }
}
