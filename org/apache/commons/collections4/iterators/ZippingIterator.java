package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.FluentIterable;































public class ZippingIterator<E>
  implements Iterator<E>
{
  private final Iterator<Iterator<? extends E>> iterators;
  private Iterator<? extends E> nextIterator = null;
  

  private Iterator<? extends E> lastReturned = null;
  











  public ZippingIterator(Iterator<? extends E> a, Iterator<? extends E> b)
  {
    this(new Iterator[] { a, b });
  }
  











  public ZippingIterator(Iterator<? extends E> a, Iterator<? extends E> b, Iterator<? extends E> c)
  {
    this(new Iterator[] { a, b, c });
  }
  







  public ZippingIterator(Iterator<? extends E>... iterators)
  {
    List<Iterator<? extends E>> list = new ArrayList();
    for (Iterator<? extends E> iterator : iterators) {
      if (iterator == null) {
        throw new NullPointerException("Iterator must not be null.");
      }
      list.add(iterator);
    }
    this.iterators = FluentIterable.of(list).loop().iterator();
  }
  









  public boolean hasNext()
  {
    if (nextIterator != null) {
      return true;
    }
    
    while (iterators.hasNext()) {
      Iterator<? extends E> childIterator = (Iterator)iterators.next();
      if (childIterator.hasNext()) {
        nextIterator = childIterator;
        return true;
      }
      
      iterators.remove();
    }
    
    return false;
  }
  




  public E next()
    throws NoSuchElementException
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    
    E val = nextIterator.next();
    lastReturned = nextIterator;
    nextIterator = null;
    return val;
  }
  





  public void remove()
  {
    if (lastReturned == null) {
      throw new IllegalStateException("No value can be removed at present");
    }
    lastReturned.remove();
    lastReturned = null;
  }
}
