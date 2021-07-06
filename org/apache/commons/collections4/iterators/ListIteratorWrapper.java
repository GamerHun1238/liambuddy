package org.apache.commons.collections4.iterators;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;












































public class ListIteratorWrapper<E>
  implements ResettableListIterator<E>
{
  private static final String UNSUPPORTED_OPERATION_MESSAGE = "ListIteratorWrapper does not support optional operations of ListIterator.";
  private static final String CANNOT_REMOVE_MESSAGE = "Cannot remove element at index {0}.";
  private final Iterator<? extends E> iterator;
  private final List<E> list = new ArrayList();
  

  private int currentIndex = 0;
  
  private int wrappedIteratorIndex = 0;
  




  private boolean removeState;
  





  public ListIteratorWrapper(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    this.iterator = iterator;
  }
  








  public void add(E obj)
    throws UnsupportedOperationException
  {
    if ((iterator instanceof ListIterator))
    {
      ListIterator<E> li = (ListIterator)iterator;
      li.add(obj);
      return;
    }
    throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
  }
  




  public boolean hasNext()
  {
    if ((currentIndex == wrappedIteratorIndex) || ((iterator instanceof ListIterator))) {
      return iterator.hasNext();
    }
    return true;
  }
  




  public boolean hasPrevious()
  {
    if ((iterator instanceof ListIterator)) {
      ListIterator<?> li = (ListIterator)iterator;
      return li.hasPrevious();
    }
    return currentIndex > 0;
  }
  




  public E next()
    throws NoSuchElementException
  {
    if ((iterator instanceof ListIterator)) {
      return iterator.next();
    }
    
    if (currentIndex < wrappedIteratorIndex) {
      currentIndex += 1;
      return list.get(currentIndex - 1);
    }
    
    E retval = iterator.next();
    list.add(retval);
    currentIndex += 1;
    wrappedIteratorIndex += 1;
    removeState = true;
    return retval;
  }
  




  public int nextIndex()
  {
    if ((iterator instanceof ListIterator)) {
      ListIterator<?> li = (ListIterator)iterator;
      return li.nextIndex();
    }
    return currentIndex;
  }
  




  public E previous()
    throws NoSuchElementException
  {
    if ((iterator instanceof ListIterator))
    {
      ListIterator<E> li = (ListIterator)iterator;
      return li.previous();
    }
    
    if (currentIndex == 0) {
      throw new NoSuchElementException();
    }
    removeState = (wrappedIteratorIndex == currentIndex);
    return list.get(--currentIndex);
  }
  




  public int previousIndex()
  {
    if ((iterator instanceof ListIterator)) {
      ListIterator<?> li = (ListIterator)iterator;
      return li.previousIndex();
    }
    return currentIndex - 1;
  }
  



  public void remove()
    throws UnsupportedOperationException
  {
    if ((iterator instanceof ListIterator)) {
      iterator.remove();
      return;
    }
    int removeIndex = currentIndex;
    if (currentIndex == wrappedIteratorIndex) {
      removeIndex--;
    }
    if ((!removeState) || (wrappedIteratorIndex - currentIndex > 1)) {
      throw new IllegalStateException(MessageFormat.format("Cannot remove element at index {0}.", new Object[] { Integer.valueOf(removeIndex) }));
    }
    iterator.remove();
    list.remove(removeIndex);
    currentIndex = removeIndex;
    wrappedIteratorIndex -= 1;
    removeState = false;
  }
  






  public void set(E obj)
    throws UnsupportedOperationException
  {
    if ((iterator instanceof ListIterator))
    {
      ListIterator<E> li = (ListIterator)iterator;
      li.set(obj);
      return;
    }
    throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
  }
  







  public void reset()
  {
    if ((iterator instanceof ListIterator)) {
      ListIterator<?> li = (ListIterator)iterator;
      while (li.previousIndex() >= 0) {
        li.previous();
      }
      return;
    }
    currentIndex = 0;
  }
}
