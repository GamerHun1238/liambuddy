package org.apache.commons.collections4.iterators;

import java.util.ListIterator;
































public class AbstractListIteratorDecorator<E>
  implements ListIterator<E>
{
  private final ListIterator<E> iterator;
  
  public AbstractListIteratorDecorator(ListIterator<E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("ListIterator must not be null");
    }
    this.iterator = iterator;
  }
  




  protected ListIterator<E> getListIterator()
  {
    return iterator;
  }
  


  public boolean hasNext()
  {
    return iterator.hasNext();
  }
  
  public E next()
  {
    return iterator.next();
  }
  
  public int nextIndex()
  {
    return iterator.nextIndex();
  }
  
  public boolean hasPrevious()
  {
    return iterator.hasPrevious();
  }
  
  public E previous()
  {
    return iterator.previous();
  }
  
  public int previousIndex()
  {
    return iterator.previousIndex();
  }
  
  public void remove()
  {
    iterator.remove();
  }
  
  public void set(E obj)
  {
    iterator.set(obj);
  }
  
  public void add(E obj)
  {
    iterator.add(obj);
  }
}
