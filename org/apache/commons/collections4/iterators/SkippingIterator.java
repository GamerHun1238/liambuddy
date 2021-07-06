package org.apache.commons.collections4.iterators;

import java.util.Iterator;







































public class SkippingIterator<E>
  extends AbstractIteratorDecorator<E>
{
  private final long offset;
  private long pos;
  
  public SkippingIterator(Iterator<E> iterator, long offset)
  {
    super(iterator);
    
    if (offset < 0L) {
      throw new IllegalArgumentException("Offset parameter must not be negative.");
    }
    
    this.offset = offset;
    pos = 0L;
    init();
  }
  


  private void init()
  {
    while ((pos < offset) && (hasNext())) {
      next();
    }
  }
  


  public E next()
  {
    E next = super.next();
    pos += 1L;
    return next;
  }
  








  public void remove()
  {
    if (pos <= offset) {
      throw new IllegalStateException("remove() can not be called before calling next()");
    }
    super.remove();
  }
}
