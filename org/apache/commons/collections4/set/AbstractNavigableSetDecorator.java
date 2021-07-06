package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;





































public abstract class AbstractNavigableSetDecorator<E>
  extends AbstractSortedSetDecorator<E>
  implements NavigableSet<E>
{
  private static final long serialVersionUID = 20150528L;
  
  protected AbstractNavigableSetDecorator() {}
  
  protected AbstractNavigableSetDecorator(NavigableSet<E> set)
  {
    super(set);
  }
  





  protected NavigableSet<E> decorated()
  {
    return (NavigableSet)super.decorated();
  }
  


  public E lower(E e)
  {
    return decorated().lower(e);
  }
  
  public E floor(E e)
  {
    return decorated().floor(e);
  }
  
  public E ceiling(E e)
  {
    return decorated().ceiling(e);
  }
  
  public E higher(E e)
  {
    return decorated().higher(e);
  }
  
  public E pollFirst()
  {
    return decorated().pollFirst();
  }
  
  public E pollLast()
  {
    return decorated().pollLast();
  }
  
  public NavigableSet<E> descendingSet()
  {
    return decorated().descendingSet();
  }
  
  public Iterator<E> descendingIterator()
  {
    return decorated().descendingIterator();
  }
  
  public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
  {
    return decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
  }
  
  public NavigableSet<E> headSet(E toElement, boolean inclusive)
  {
    return decorated().headSet(toElement, inclusive);
  }
  
  public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
  {
    return decorated().tailSet(fromElement, inclusive);
  }
}
