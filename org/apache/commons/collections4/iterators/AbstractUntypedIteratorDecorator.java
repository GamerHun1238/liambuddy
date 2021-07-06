package org.apache.commons.collections4.iterators;

import java.util.Iterator;
































public abstract class AbstractUntypedIteratorDecorator<I, O>
  implements Iterator<O>
{
  private final Iterator<I> iterator;
  
  protected AbstractUntypedIteratorDecorator(Iterator<I> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    this.iterator = iterator;
  }
  




  protected Iterator<I> getIterator()
  {
    return iterator;
  }
  
  public boolean hasNext() {
    return iterator.hasNext();
  }
  
  public void remove() {
    iterator.remove();
  }
}
