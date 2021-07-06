package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.ResettableIterator;

























































public class IteratorIterable<E>
  implements Iterable<E>
{
  private final Iterator<? extends E> iterator;
  private final Iterator<E> typeSafeIterator;
  
  private static <E> Iterator<E> createTypesafeIterator(Iterator<? extends E> iterator)
  {
    new Iterator() {
      public boolean hasNext() {
        return val$iterator.hasNext();
      }
      
      public E next() {
        return val$iterator.next();
      }
      
      public void remove() {
        val$iterator.remove();
      }
    };
  }
  











  public IteratorIterable(Iterator<? extends E> iterator)
  {
    this(iterator, false);
  }
  







  public IteratorIterable(Iterator<? extends E> iterator, boolean multipleUse)
  {
    if ((multipleUse) && (!(iterator instanceof ResettableIterator))) {
      this.iterator = new ListIteratorWrapper(iterator);
    } else {
      this.iterator = iterator;
    }
    typeSafeIterator = createTypesafeIterator(this.iterator);
  }
  




  public Iterator<E> iterator()
  {
    if ((iterator instanceof ResettableIterator)) {
      ((ResettableIterator)iterator).reset();
    }
    return typeSafeIterator;
  }
}
