package org.apache.commons.collections4.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;









































public class ArrayListIterator<E>
  extends ArrayIterator<E>
  implements ResettableListIterator<E>
{
  private int lastItemIndex = -1;
  









  public ArrayListIterator(Object array)
  {
    super(array);
  }
  









  public ArrayListIterator(Object array, int startIndex)
  {
    super(array, startIndex);
  }
  











  public ArrayListIterator(Object array, int startIndex, int endIndex)
  {
    super(array, startIndex, endIndex);
  }
  






  public boolean hasPrevious()
  {
    return index > startIndex;
  }
  






  public E previous()
  {
    if (!hasPrevious()) {
      throw new NoSuchElementException();
    }
    lastItemIndex = (--index);
    return Array.get(array, index);
  }
  







  public E next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    lastItemIndex = index;
    return Array.get(array, index++);
  }
  




  public int nextIndex()
  {
    return index - startIndex;
  }
  




  public int previousIndex()
  {
    return index - startIndex - 1;
  }
  







  public void add(Object o)
  {
    throw new UnsupportedOperationException("add() method is not supported");
  }
  

















  public void set(Object o)
  {
    if (lastItemIndex == -1) {
      throw new IllegalStateException("must call next() or previous() before a call to set()");
    }
    
    Array.set(array, lastItemIndex, o);
  }
  



  public void reset()
  {
    super.reset();
    lastItemIndex = -1;
  }
}
