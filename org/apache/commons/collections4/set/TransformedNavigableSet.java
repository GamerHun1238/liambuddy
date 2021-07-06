package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;
import org.apache.commons.collections4.Transformer;









































public class TransformedNavigableSet<E>
  extends TransformedSortedSet<E>
  implements NavigableSet<E>
{
  private static final long serialVersionUID = 20150528L;
  
  public static <E> TransformedNavigableSet<E> transformingNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer)
  {
    return new TransformedNavigableSet(set, transformer);
  }
  















  public static <E> TransformedNavigableSet<E> transformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer)
  {
    TransformedNavigableSet<E> decorated = new TransformedNavigableSet(set, transformer);
    if (set.size() > 0)
    {
      E[] values = (Object[])set.toArray();
      set.clear();
      for (E value : values) {
        decorated.decorated().add(transformer.transform(value));
      }
    }
    return decorated;
  }
  











  protected TransformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer)
  {
    super(set, transformer);
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
    return transformingNavigableSet(decorated().descendingSet(), transformer);
  }
  
  public Iterator<E> descendingIterator()
  {
    return decorated().descendingIterator();
  }
  
  public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
  {
    NavigableSet<E> sub = decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
    return transformingNavigableSet(sub, transformer);
  }
  
  public NavigableSet<E> headSet(E toElement, boolean inclusive)
  {
    NavigableSet<E> head = decorated().headSet(toElement, inclusive);
    return transformingNavigableSet(head, transformer);
  }
  
  public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
  {
    NavigableSet<E> tail = decorated().tailSet(fromElement, inclusive);
    return transformingNavigableSet(tail, transformer);
  }
}
