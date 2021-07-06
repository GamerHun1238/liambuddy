package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;







































public class ObjectArrayListIterator<E>
  extends ObjectArrayIterator<E>
  implements ResettableListIterator<E>
{
  private int lastItemIndex = -1;
  







  public ObjectArrayListIterator(E... array)
  {
    super(array);
  }
  








  public ObjectArrayListIterator(E[] array, int start)
  {
    super(array, start);
  }
  










  public ObjectArrayListIterator(E[] array, int start, int end)
  {
    super(array, start, end);
  }
  







  public boolean hasPrevious()
  {
    return index > getStartIndex();
  }
  





  public E previous()
  {
    if (!hasPrevious()) {
      throw new NoSuchElementException();
    }
    lastItemIndex = (--index);
    return array[index];
  }
  






  public E next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    lastItemIndex = index;
    return array[(index++)];
  }
  




  public int nextIndex()
  {
    return index - getStartIndex();
  }
  




  public int previousIndex()
  {
    return index - getStartIndex() - 1;
  }
  






  public void add(E obj)
  {
    throw new UnsupportedOperationException("add() method is not supported");
  }
  
















  public void set(E obj)
  {
    if (lastItemIndex == -1) {
      throw new IllegalStateException("must call next() or previous() before a call to set()");
    }
    
    array[lastItemIndex] = obj;
  }
  



  public void reset()
  {
    super.reset();
    lastItemIndex = -1;
  }
}
