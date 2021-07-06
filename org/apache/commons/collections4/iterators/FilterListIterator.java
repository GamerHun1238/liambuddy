package org.apache.commons.collections4.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.Predicate;







































public class FilterListIterator<E>
  implements ListIterator<E>
{
  private ListIterator<? extends E> iterator;
  private Predicate<? super E> predicate;
  private E nextObject;
  private boolean nextObjectSet = false;
  




  private E previousObject;
  




  private boolean previousObjectSet = false;
  



  private int nextIndex = 0;
  







  public FilterListIterator() {}
  







  public FilterListIterator(ListIterator<? extends E> iterator)
  {
    this.iterator = iterator;
  }
  






  public FilterListIterator(ListIterator<? extends E> iterator, Predicate<? super E> predicate)
  {
    this.iterator = iterator;
    this.predicate = predicate;
  }
  






  public FilterListIterator(Predicate<? super E> predicate)
  {
    this.predicate = predicate;
  }
  

  public void add(E o)
  {
    throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
  }
  
  public boolean hasNext() {
    return (nextObjectSet) || (setNextObject());
  }
  
  public boolean hasPrevious() {
    return (previousObjectSet) || (setPreviousObject());
  }
  
  public E next() {
    if ((!nextObjectSet) && 
      (!setNextObject())) {
      throw new NoSuchElementException();
    }
    
    nextIndex += 1;
    E temp = nextObject;
    clearNextObject();
    return temp;
  }
  
  public int nextIndex() {
    return nextIndex;
  }
  
  public E previous() {
    if ((!previousObjectSet) && 
      (!setPreviousObject())) {
      throw new NoSuchElementException();
    }
    
    nextIndex -= 1;
    E temp = previousObject;
    clearPreviousObject();
    return temp;
  }
  
  public int previousIndex() {
    return nextIndex - 1;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
  }
  
  public void set(E o)
  {
    throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
  }
  





  public ListIterator<? extends E> getListIterator()
  {
    return iterator;
  }
  





  public void setListIterator(ListIterator<? extends E> iterator)
  {
    this.iterator = iterator;
  }
  





  public Predicate<? super E> getPredicate()
  {
    return predicate;
  }
  




  public void setPredicate(Predicate<? super E> predicate)
  {
    this.predicate = predicate;
  }
  
  private void clearNextObject()
  {
    nextObject = null;
    nextObjectSet = false;
  }
  



  private boolean setNextObject()
  {
    if (previousObjectSet) {
      clearPreviousObject();
      if (!setNextObject()) {
        return false;
      }
      clearNextObject();
    }
    
    if (iterator == null) {
      return false;
    }
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
  
  private void clearPreviousObject() {
    previousObject = null;
    previousObjectSet = false;
  }
  



  private boolean setPreviousObject()
  {
    if (nextObjectSet) {
      clearNextObject();
      if (!setPreviousObject()) {
        return false;
      }
      clearPreviousObject();
    }
    
    if (iterator == null) {
      return false;
    }
    while (iterator.hasPrevious()) {
      E object = iterator.previous();
      if (predicate.evaluate(object)) {
        previousObject = object;
        previousObjectSet = true;
        return true;
      }
    }
    return false;
  }
}
