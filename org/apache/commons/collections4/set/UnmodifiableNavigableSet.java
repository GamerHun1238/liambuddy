package org.apache.commons.collections4.set;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;


































public final class UnmodifiableNavigableSet<E>
  extends AbstractNavigableSetDecorator<E>
  implements Unmodifiable
{
  private static final long serialVersionUID = 20150528L;
  
  public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> set)
  {
    if ((set instanceof Unmodifiable)) {
      return set;
    }
    return new UnmodifiableNavigableSet(set);
  }
  






  private UnmodifiableNavigableSet(NavigableSet<E> set)
  {
    super(set);
  }
  

  public Iterator<E> iterator()
  {
    return UnmodifiableIterator.unmodifiableIterator(decorated().iterator());
  }
  
  public boolean add(E object)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends E> coll)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(Object object)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection<?> coll)
  {
    throw new UnsupportedOperationException();
  }
  


  public SortedSet<E> subSet(E fromElement, E toElement)
  {
    SortedSet<E> sub = decorated().subSet(fromElement, toElement);
    return UnmodifiableSortedSet.unmodifiableSortedSet(sub);
  }
  
  public SortedSet<E> headSet(E toElement)
  {
    SortedSet<E> head = decorated().headSet(toElement);
    return UnmodifiableSortedSet.unmodifiableSortedSet(head);
  }
  
  public SortedSet<E> tailSet(E fromElement)
  {
    SortedSet<E> tail = decorated().tailSet(fromElement);
    return UnmodifiableSortedSet.unmodifiableSortedSet(tail);
  }
  


  public NavigableSet<E> descendingSet()
  {
    return unmodifiableNavigableSet(decorated().descendingSet());
  }
  
  public Iterator<E> descendingIterator()
  {
    return UnmodifiableIterator.unmodifiableIterator(decorated().descendingIterator());
  }
  
  public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
  {
    NavigableSet<E> sub = decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
    return unmodifiableNavigableSet(sub);
  }
  
  public NavigableSet<E> headSet(E toElement, boolean inclusive)
  {
    NavigableSet<E> head = decorated().headSet(toElement, inclusive);
    return unmodifiableNavigableSet(head);
  }
  
  public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
  {
    NavigableSet<E> tail = decorated().tailSet(fromElement, inclusive);
    return unmodifiableNavigableSet(tail);
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(decorated());
  }
  






  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    setCollection((Collection)in.readObject());
  }
}
