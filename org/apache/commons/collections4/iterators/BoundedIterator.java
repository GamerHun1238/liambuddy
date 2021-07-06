package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

















































public class BoundedIterator<E>
  implements Iterator<E>
{
  private final Iterator<? extends E> iterator;
  private final long offset;
  private final long max;
  private long pos;
  
  public BoundedIterator(Iterator<? extends E> iterator, long offset, long max)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if (offset < 0L) {
      throw new IllegalArgumentException("Offset parameter must not be negative.");
    }
    if (max < 0L) {
      throw new IllegalArgumentException("Max parameter must not be negative.");
    }
    
    this.iterator = iterator;
    this.offset = offset;
    this.max = max;
    pos = 0L;
    init();
  }
  


  private void init()
  {
    while ((pos < offset) && (iterator.hasNext())) {
      iterator.next();
      pos += 1L;
    }
  }
  

  public boolean hasNext()
  {
    if (!checkBounds()) {
      return false;
    }
    return iterator.hasNext();
  }
  



  private boolean checkBounds()
  {
    if (pos - offset + 1L > max) {
      return false;
    }
    return true;
  }
  
  public E next() {
    if (!checkBounds()) {
      throw new NoSuchElementException();
    }
    E next = iterator.next();
    pos += 1L;
    return next;
  }
  







  public void remove()
  {
    if (pos <= offset) {
      throw new IllegalStateException("remove() can not be called before calling next()");
    }
    iterator.remove();
  }
}
