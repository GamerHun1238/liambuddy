package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.Predicate;






























public class FilterIterator<E>
  implements Iterator<E>
{
  private Iterator<? extends E> iterator;
  private Predicate<? super E> predicate;
  private E nextObject;
  private boolean nextObjectSet = false;
  







  public FilterIterator() {}
  






  public FilterIterator(Iterator<? extends E> iterator)
  {
    this.iterator = iterator;
  }
  







  public FilterIterator(Iterator<? extends E> iterator, Predicate<? super E> predicate)
  {
    this.iterator = iterator;
    this.predicate = predicate;
  }
  







  public boolean hasNext()
  {
    return (nextObjectSet) || (setNextObject());
  }
  







  public E next()
  {
    if ((!nextObjectSet) && 
      (!setNextObject())) {
      throw new NoSuchElementException();
    }
    
    nextObjectSet = false;
    return nextObject;
  }
  










  public void remove()
  {
    if (nextObjectSet) {
      throw new IllegalStateException("remove() cannot be called");
    }
    iterator.remove();
  }
  





  public Iterator<? extends E> getIterator()
  {
    return iterator;
  }
  





  public void setIterator(Iterator<? extends E> iterator)
  {
    this.iterator = iterator;
    nextObject = null;
    nextObjectSet = false;
  }
  





  public Predicate<? super E> getPredicate()
  {
    return predicate;
  }
  




  public void setPredicate(Predicate<? super E> predicate)
  {
    this.predicate = predicate;
    nextObject = null;
    nextObjectSet = false;
  }
  




  private boolean setNextObject()
  {
    while (iterator.hasNext()) {
      E object = iterator.next();
      if (predicate.evaluate(object)) {
        nextObject = object;
        nextObjectSet = true;
        return true;
      }
    }
    return false;
  }
}
