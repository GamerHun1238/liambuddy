package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.Unmodifiable;




































public final class UnmodifiableIterator<E>
  implements Iterator<E>, Unmodifiable
{
  private final Iterator<? extends E> iterator;
  
  public static <E> Iterator<E> unmodifiableIterator(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if ((iterator instanceof Unmodifiable))
    {
      Iterator<E> tmpIterator = iterator;
      return tmpIterator;
    }
    return new UnmodifiableIterator(iterator);
  }
  






  private UnmodifiableIterator(Iterator<? extends E> iterator)
  {
    this.iterator = iterator;
  }
  
  public boolean hasNext()
  {
    return iterator.hasNext();
  }
  
  public E next() {
    return iterator.next();
  }
  
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported");
  }
}
