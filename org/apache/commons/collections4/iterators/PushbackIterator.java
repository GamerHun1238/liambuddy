package org.apache.commons.collections4.iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
































public class PushbackIterator<E>
  implements Iterator<E>
{
  private final Iterator<? extends E> iterator;
  private Deque<E> items = new ArrayDeque();
  










  public static <E> PushbackIterator<E> pushbackIterator(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if ((iterator instanceof PushbackIterator))
    {
      PushbackIterator<E> it = (PushbackIterator)iterator;
      return it;
    }
    return new PushbackIterator(iterator);
  }
  







  public PushbackIterator(Iterator<? extends E> iterator)
  {
    this.iterator = iterator;
  }
  






  public void pushback(E item)
  {
    items.push(item);
  }
  
  public boolean hasNext() {
    return !items.isEmpty() ? true : iterator.hasNext();
  }
  
  public E next() {
    return !items.isEmpty() ? items.pop() : iterator.next();
  }
  




  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
