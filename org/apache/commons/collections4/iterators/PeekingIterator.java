package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;






























public class PeekingIterator<E>
  implements Iterator<E>
{
  private final Iterator<? extends E> iterator;
  private boolean exhausted = false;
  

  private boolean slotFilled = false;
  





  private E slot;
  






  public static <E> PeekingIterator<E> peekingIterator(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if ((iterator instanceof PeekingIterator))
    {
      PeekingIterator<E> it = (PeekingIterator)iterator;
      return it;
    }
    return new PeekingIterator(iterator);
  }
  






  public PeekingIterator(Iterator<? extends E> iterator)
  {
    this.iterator = iterator;
  }
  
  private void fill() {
    if ((exhausted) || (slotFilled)) {
      return;
    }
    if (iterator.hasNext()) {
      slot = iterator.next();
      slotFilled = true;
    } else {
      exhausted = true;
      slot = null;
      slotFilled = false;
    }
  }
  
  public boolean hasNext()
  {
    if (exhausted) {
      return false;
    }
    return slotFilled ? true : iterator.hasNext();
  }
  











  public E peek()
  {
    fill();
    return exhausted ? null : slot;
  }
  






  public E element()
  {
    fill();
    if (exhausted) {
      throw new NoSuchElementException();
    }
    return slot;
  }
  
  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    E x = slotFilled ? slot : iterator.next();
    
    slot = null;
    slotFilled = false;
    return x;
  }
  





  public void remove()
  {
    if (slotFilled) {
      throw new IllegalStateException("peek() or element() called before remove()");
    }
    iterator.remove();
  }
}
