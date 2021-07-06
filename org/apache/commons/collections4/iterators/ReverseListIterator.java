package org.apache.commons.collections4.iterators;

import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.ResettableListIterator;



































public class ReverseListIterator<E>
  implements ResettableListIterator<E>
{
  private final List<E> list;
  private ListIterator<E> iterator;
  private boolean validForUpdate = true;
  






  public ReverseListIterator(List<E> list)
  {
    if (list == null) {
      throw new NullPointerException("List must not be null.");
    }
    this.list = list;
    iterator = list.listIterator(list.size());
  }
  





  public boolean hasNext()
  {
    return iterator.hasPrevious();
  }
  





  public E next()
  {
    E obj = iterator.previous();
    validForUpdate = true;
    return obj;
  }
  




  public int nextIndex()
  {
    return iterator.previousIndex();
  }
  




  public boolean hasPrevious()
  {
    return iterator.hasNext();
  }
  





  public E previous()
  {
    E obj = iterator.next();
    validForUpdate = true;
    return obj;
  }
  




  public int previousIndex()
  {
    return iterator.nextIndex();
  }
  





  public void remove()
  {
    if (!validForUpdate) {
      throw new IllegalStateException("Cannot remove from list until next() or previous() called");
    }
    iterator.remove();
  }
  






  public void set(E obj)
  {
    if (!validForUpdate) {
      throw new IllegalStateException("Cannot set to list until next() or previous() called");
    }
    iterator.set(obj);
  }
  








  public void add(E obj)
  {
    if (!validForUpdate) {
      throw new IllegalStateException("Cannot add to list until next() or previous() called");
    }
    validForUpdate = false;
    iterator.add(obj);
    iterator.previous();
  }
  



  public void reset()
  {
    iterator = list.listIterator(list.size());
  }
}
