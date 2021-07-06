package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;
import org.apache.commons.collections4.Predicate;

















































public class PredicatedNavigableSet<E>
  extends PredicatedSortedSet<E>
  implements NavigableSet<E>
{
  private static final long serialVersionUID = 20150528L;
  
  public static <E> PredicatedNavigableSet<E> predicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate)
  {
    return new PredicatedNavigableSet(set, predicate);
  }
  











  protected PredicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate)
  {
    super(set, predicate);
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
    return predicatedNavigableSet(decorated().descendingSet(), predicate);
  }
  
  public Iterator<E> descendingIterator()
  {
    return decorated().descendingIterator();
  }
  
  public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
  {
    NavigableSet<E> sub = decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
    return predicatedNavigableSet(sub, predicate);
  }
  
  public NavigableSet<E> headSet(E toElement, boolean inclusive)
  {
    NavigableSet<E> head = decorated().headSet(toElement, inclusive);
    return predicatedNavigableSet(head, predicate);
  }
  
  public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
  {
    NavigableSet<E> tail = decorated().tailSet(fromElement, inclusive);
    return predicatedNavigableSet(tail, predicate);
  }
}
