package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;
























public class SingletonListIterator<E>
  implements ResettableListIterator<E>
{
  private boolean beforeFirst = true;
  private boolean nextCalled = false;
  private boolean removed = false;
  


  private E object;
  


  public SingletonListIterator(E object)
  {
    this.object = object;
  }
  






  public boolean hasNext()
  {
    return (beforeFirst) && (!removed);
  }
  






  public boolean hasPrevious()
  {
    return (!beforeFirst) && (!removed);
  }
  





  public int nextIndex()
  {
    return beforeFirst ? 0 : 1;
  }
  






  public int previousIndex()
  {
    return beforeFirst ? -1 : 0;
  }
  








  public E next()
  {
    if ((!beforeFirst) || (removed)) {
      throw new NoSuchElementException();
    }
    beforeFirst = false;
    nextCalled = true;
    return object;
  }
  








  public E previous()
  {
    if ((beforeFirst) || (removed)) {
      throw new NoSuchElementException();
    }
    beforeFirst = true;
    return object;
  }
  






  public void remove()
  {
    if ((!nextCalled) || (removed)) {
      throw new IllegalStateException();
    }
    object = null;
    removed = true;
  }
  





  public void add(E obj)
  {
    throw new UnsupportedOperationException("add() is not supported by this iterator");
  }
  






  public void set(E obj)
  {
    if ((!nextCalled) || (removed)) {
      throw new IllegalStateException();
    }
    object = obj;
  }
  


  public void reset()
  {
    beforeFirst = true;
    nextCalled = false;
  }
}
