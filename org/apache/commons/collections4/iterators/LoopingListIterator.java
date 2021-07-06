package org.apache.commons.collections4.iterators;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;











































public class LoopingListIterator<E>
  implements ResettableListIterator<E>
{
  private final List<E> list;
  private ListIterator<E> iterator;
  
  public LoopingListIterator(List<E> list)
  {
    if (list == null) {
      throw new NullPointerException("The list must not be null");
    }
    this.list = list;
    _reset();
  }
  







  public boolean hasNext()
  {
    return !list.isEmpty();
  }
  







  public E next()
  {
    if (list.isEmpty()) {
      throw new NoSuchElementException("There are no elements for this iterator to loop on");
    }
    
    if (!iterator.hasNext()) {
      reset();
    }
    return iterator.next();
  }
  










  public int nextIndex()
  {
    if (list.isEmpty()) {
      throw new NoSuchElementException("There are no elements for this iterator to loop on");
    }
    
    if (!iterator.hasNext()) {
      return 0;
    }
    return iterator.nextIndex();
  }
  







  public boolean hasPrevious()
  {
    return !list.isEmpty();
  }
  








  public E previous()
  {
    if (list.isEmpty()) {
      throw new NoSuchElementException("There are no elements for this iterator to loop on");
    }
    
    if (!iterator.hasPrevious()) {
      E result = null;
      while (iterator.hasNext()) {
        result = iterator.next();
      }
      iterator.previous();
      return result;
    }
    return iterator.previous();
  }
  










  public int previousIndex()
  {
    if (list.isEmpty()) {
      throw new NoSuchElementException("There are no elements for this iterator to loop on");
    }
    
    if (!iterator.hasPrevious()) {
      return list.size() - 1;
    }
    return iterator.previousIndex();
  }
  

















  public void remove()
  {
    iterator.remove();
  }
  














  public void add(E obj)
  {
    iterator.add(obj);
  }
  











  public void set(E obj)
  {
    iterator.set(obj);
  }
  


  public void reset()
  {
    _reset();
  }
  
  private void _reset() {
    iterator = list.listIterator();
  }
  




  public int size()
  {
    return list.size();
  }
}
