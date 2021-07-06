package org.apache.commons.collections4;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.iterators.SingletonIterator;

































































public class FluentIterable<E>
  implements Iterable<E>
{
  private final Iterable<E> iterable;
  
  public static <T> FluentIterable<T> empty()
  {
    return IterableUtils.EMPTY_ITERABLE;
  }
  








  public static <T> FluentIterable<T> of(T singleton)
  {
    return of(IteratorUtils.asIterable(new SingletonIterator(singleton, false)));
  }
  








  public static <T> FluentIterable<T> of(T... elements)
  {
    return of(Arrays.asList(elements));
  }
  












  public static <T> FluentIterable<T> of(Iterable<T> iterable)
  {
    IterableUtils.checkNotNull(iterable);
    if ((iterable instanceof FluentIterable)) {
      return (FluentIterable)iterable;
    }
    return new FluentIterable(iterable);
  }
  






  FluentIterable()
  {
    iterable = this;
  }
  



  private FluentIterable(Iterable<E> iterable)
  {
    this.iterable = iterable;
  }
  










  public FluentIterable<E> append(E... elements)
  {
    return append(Arrays.asList(elements));
  }
  








  public FluentIterable<E> append(Iterable<? extends E> other)
  {
    return of(IterableUtils.chainedIterable(iterable, other));
  }
  

















  public FluentIterable<E> collate(Iterable<? extends E> other)
  {
    return of(IterableUtils.collatedIterable(iterable, other));
  }
  





















  public FluentIterable<E> collate(Iterable<? extends E> other, Comparator<? super E> comparator)
  {
    return of(IterableUtils.collatedIterable(comparator, iterable, other));
  }
  












  public FluentIterable<E> eval()
  {
    return of(toList());
  }
  







  public FluentIterable<E> filter(Predicate<? super E> predicate)
  {
    return of(IterableUtils.filteredIterable(iterable, predicate));
  }
  







  public FluentIterable<E> limit(long maxSize)
  {
    return of(IterableUtils.boundedIterable(iterable, maxSize));
  }
  





  public FluentIterable<E> loop()
  {
    return of(IterableUtils.loopingIterable(iterable));
  }
  





  public FluentIterable<E> reverse()
  {
    return of(IterableUtils.reversedIterable(iterable));
  }
  








  public FluentIterable<E> skip(long elementsToSkip)
  {
    return of(IterableUtils.skippingIterable(iterable, elementsToSkip));
  }
  








  public <O> FluentIterable<O> transform(Transformer<? super E, ? extends O> transformer)
  {
    return of(IterableUtils.transformedIterable(iterable, transformer));
  }
  





  public FluentIterable<E> unique()
  {
    return of(IterableUtils.uniqueIterable(iterable));
  }
  





  public FluentIterable<E> unmodifiable()
  {
    return of(IterableUtils.unmodifiableIterable(iterable));
  }
  








  public FluentIterable<E> zip(Iterable<? extends E> other)
  {
    return of(IterableUtils.zippingIterable(iterable, other));
  }
  








  public FluentIterable<E> zip(Iterable<? extends E>... others)
  {
    return of(IterableUtils.zippingIterable(iterable, others));
  }
  




  public Iterator<E> iterator()
  {
    return iterable.iterator();
  }
  





  public Enumeration<E> asEnumeration()
  {
    return IteratorUtils.asEnumeration(iterator());
  }
  










  public boolean allMatch(Predicate<? super E> predicate)
  {
    return IterableUtils.matchesAll(iterable, predicate);
  }
  









  public boolean anyMatch(Predicate<? super E> predicate)
  {
    return IterableUtils.matchesAny(iterable, predicate);
  }
  




  public boolean isEmpty()
  {
    return IterableUtils.isEmpty(iterable);
  }
  





  public boolean contains(Object object)
  {
    return IterableUtils.contains(iterable, object);
  }
  





  public void forEach(Closure<? super E> closure)
  {
    IterableUtils.forEach(iterable, closure);
  }
  









  public E get(int position)
  {
    return IterableUtils.get(iterable, position);
  }
  





  public int size()
  {
    return IterableUtils.size(iterable);
  }
  






  public void copyInto(Collection<? super E> collection)
  {
    if (collection == null) {
      throw new NullPointerException("Collection must not be null");
    }
    CollectionUtils.addAll(collection, iterable);
  }
  







  public E[] toArray(Class<E> arrayClass)
  {
    return IteratorUtils.toArray(iterator(), arrayClass);
  }
  







  public List<E> toList()
  {
    return IterableUtils.toList(iterable);
  }
  

  public String toString()
  {
    return IterableUtils.toString(iterable);
  }
}
