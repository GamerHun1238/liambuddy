package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.collections4.iterators.UniqueFilterIterator;








































public class IterableUtils
{
  static final FluentIterable EMPTY_ITERABLE = new FluentIterable()
  {
    public Iterator<Object> iterator() {
      return IteratorUtils.emptyIterator();
    }
  };
  





  public IterableUtils() {}
  




  public static <E> Iterable<E> emptyIterable()
  {
    return EMPTY_ITERABLE;
  }
  




















  public static <E> Iterable<E> chainedIterable(Iterable<? extends E> a, Iterable<? extends E> b)
  {
    return chainedIterable(new Iterable[] { a, b });
  }
  



















  public static <E> Iterable<E> chainedIterable(Iterable<? extends E> a, Iterable<? extends E> b, Iterable<? extends E> c)
  {
    return chainedIterable(new Iterable[] { a, b, c });
  }
  





















  public static <E> Iterable<E> chainedIterable(Iterable<? extends E> a, Iterable<? extends E> b, Iterable<? extends E> c, Iterable<? extends E> d)
  {
    return chainedIterable(new Iterable[] { a, b, c, d });
  }
  














  public static <E> Iterable<E> chainedIterable(Iterable<? extends E>... iterables)
  {
    checkNotNull(iterables);
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        new LazyIteratorChain()
        {
          protected Iterator<? extends E> nextIterator(int count) {
            if (count > val$iterables.length) {
              return null;
            }
            return val$iterables[(count - 1)].iterator();
          }
        };
      }
    };
  }
  

















  public static <E> Iterable<E> collatedIterable(Iterable<? extends E> a, final Iterable<? extends E> b)
  {
    checkNotNull(new Iterable[] { a, b });
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        return IteratorUtils.collatedIterator(null, val$a.iterator(), b.iterator());
      }
    };
  }
  

















  public static <E> Iterable<E> collatedIterable(Comparator<? super E> comparator, final Iterable<? extends E> a, final Iterable<? extends E> b)
  {
    checkNotNull(new Iterable[] { a, b });
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        return IteratorUtils.collatedIterator(val$comparator, a.iterator(), b.iterator());
      }
    };
  }
  
















  public static <E> Iterable<E> filteredIterable(Iterable<E> iterable, final Predicate<? super E> predicate)
  {
    checkNotNull(iterable);
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null.");
    }
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        return IteratorUtils.filteredIterator(IterableUtils.emptyIteratorIfNull(val$iterable), predicate);
      }
    };
  }
  
















  public static <E> Iterable<E> boundedIterable(Iterable<E> iterable, final long maxSize)
  {
    checkNotNull(iterable);
    if (maxSize < 0L) {
      throw new IllegalArgumentException("MaxSize parameter must not be negative.");
    }
    
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        return IteratorUtils.boundedIterator(val$iterable.iterator(), maxSize);
      }
    };
  }
  

















  public static <E> Iterable<E> loopingIterable(Iterable<E> iterable)
  {
    checkNotNull(iterable);
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        new LazyIteratorChain()
        {
          protected Iterator<? extends E> nextIterator(int count) {
            if (IterableUtils.isEmpty(val$iterable)) {
              return null;
            }
            return val$iterable.iterator();
          }
        };
      }
    };
  }
  



















  public static <E> Iterable<E> reversedIterable(Iterable<E> iterable)
  {
    checkNotNull(iterable);
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        List<E> list = (val$iterable instanceof List) ? (List)val$iterable : IteratorUtils.toList(val$iterable.iterator());
        

        return new ReverseListIterator(list);
      }
    };
  }
  















  public static <E> Iterable<E> skippingIterable(Iterable<E> iterable, final long elementsToSkip)
  {
    checkNotNull(iterable);
    if (elementsToSkip < 0L) {
      throw new IllegalArgumentException("ElementsToSkip parameter must not be negative.");
    }
    
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        return IteratorUtils.skippingIterator(val$iterable.iterator(), elementsToSkip);
      }
    };
  }
  

















  public static <I, O> Iterable<O> transformedIterable(Iterable<I> iterable, final Transformer<? super I, ? extends O> transformer)
  {
    checkNotNull(iterable);
    if (transformer == null) {
      throw new NullPointerException("Transformer must not be null.");
    }
    new FluentIterable()
    {
      public Iterator<O> iterator() {
        return IteratorUtils.transformedIterator(val$iterable.iterator(), transformer);
      }
    };
  }
  














  public static <E> Iterable<E> uniqueIterable(Iterable<E> iterable)
  {
    checkNotNull(iterable);
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        return new UniqueFilterIterator(val$iterable.iterator());
      }
    };
  }
  












  public static <E> Iterable<E> unmodifiableIterable(Iterable<E> iterable)
  {
    checkNotNull(iterable);
    if ((iterable instanceof UnmodifiableIterable)) {
      return iterable;
    }
    return new UnmodifiableIterable(iterable);
  }
  

  private static final class UnmodifiableIterable<E>
    extends FluentIterable<E>
  {
    private final Iterable<E> unmodifiable;
    
    public UnmodifiableIterable(Iterable<E> iterable)
    {
      unmodifiable = iterable;
    }
    
    public Iterator<E> iterator()
    {
      return IteratorUtils.unmodifiableIterator(unmodifiable.iterator());
    }
  }
  



















  public static <E> Iterable<E> zippingIterable(Iterable<? extends E> a, final Iterable<? extends E> b)
  {
    checkNotNull(a);
    checkNotNull(b);
    new FluentIterable()
    {
      public Iterator<E> iterator() {
        return IteratorUtils.zippingIterator(val$a.iterator(), b.iterator());
      }
    };
  }
  
















  public static <E> Iterable<E> zippingIterable(final Iterable<? extends E> first, Iterable<? extends E>... others)
  {
    checkNotNull(first);
    checkNotNull(others);
    new FluentIterable()
    {
      public Iterator<E> iterator()
      {
        Iterator<? extends E>[] iterators = new Iterator[val$others.length + 1];
        iterators[0] = first.iterator();
        for (int i = 0; i < val$others.length; i++) {
          iterators[(i + 1)] = val$others[i].iterator();
        }
        return IteratorUtils.zippingIterator(iterators);
      }
    };
  }
  










  public static <E> Iterable<E> emptyIfNull(Iterable<E> iterable)
  {
    return iterable == null ? emptyIterable() : iterable;
  }
  







  public static <E> void forEach(Iterable<E> iterable, Closure<? super E> closure)
  {
    IteratorUtils.forEach(emptyIteratorIfNull(iterable), closure);
  }
  









  public static <E> E forEachButLast(Iterable<E> iterable, Closure<? super E> closure)
  {
    return IteratorUtils.forEachButLast(emptyIteratorIfNull(iterable), closure);
  }
  










  public static <E> E find(Iterable<E> iterable, Predicate<? super E> predicate)
  {
    return IteratorUtils.find(emptyIteratorIfNull(iterable), predicate);
  }
  











  public static <E> int indexOf(Iterable<E> iterable, Predicate<? super E> predicate)
  {
    return IteratorUtils.indexOf(emptyIteratorIfNull(iterable), predicate);
  }
  











  public static <E> boolean matchesAll(Iterable<E> iterable, Predicate<? super E> predicate)
  {
    return IteratorUtils.matchesAll(emptyIteratorIfNull(iterable), predicate);
  }
  










  public static <E> boolean matchesAny(Iterable<E> iterable, Predicate<? super E> predicate)
  {
    return IteratorUtils.matchesAny(emptyIteratorIfNull(iterable), predicate);
  }
  










  public static <E> long countMatches(Iterable<E> input, Predicate<? super E> predicate)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null.");
    }
    return size(filteredIterable(emptyIfNull(input), predicate));
  }
  







  public static boolean isEmpty(Iterable<?> iterable)
  {
    if ((iterable instanceof Collection)) {
      return ((Collection)iterable).isEmpty();
    }
    return IteratorUtils.isEmpty(emptyIteratorIfNull(iterable));
  }
  










  public static <E> boolean contains(Iterable<E> iterable, Object object)
  {
    if ((iterable instanceof Collection)) {
      return ((Collection)iterable).contains(object);
    }
    return IteratorUtils.contains(emptyIteratorIfNull(iterable), object);
  }
  


















  public static <E> boolean contains(Iterable<? extends E> iterable, E object, Equator<? super E> equator)
  {
    if (equator == null) {
      throw new NullPointerException("Equator must not be null.");
    }
    return matchesAny(iterable, EqualPredicate.equalPredicate(object, equator));
  }
  








  public static <E, T extends E> int frequency(Iterable<E> iterable, T obj)
  {
    if ((iterable instanceof Set)) {
      return ((Set)iterable).contains(obj) ? 1 : 0;
    }
    if ((iterable instanceof Bag)) {
      return ((Bag)iterable).getCount(obj);
    }
    return size(filteredIterable(emptyIfNull(iterable), EqualPredicate.equalPredicate(obj)));
  }
  











  public static <T> T get(Iterable<T> iterable, int index)
  {
    CollectionUtils.checkIndexBounds(index);
    if ((iterable instanceof List)) {
      return ((List)iterable).get(index);
    }
    return IteratorUtils.get(emptyIteratorIfNull(iterable), index);
  }
  







  public static int size(Iterable<?> iterable)
  {
    if ((iterable instanceof Collection)) {
      return ((Collection)iterable).size();
    }
    return IteratorUtils.size(emptyIteratorIfNull(iterable));
  }
  




























  public static <O> List<List<O>> partition(Iterable<? extends O> iterable, Predicate<? super O> predicate)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null.");
    }
    
    Factory<List<O>> factory = FactoryUtils.instantiateFactory(ArrayList.class);
    
    Predicate<? super O>[] predicates = { predicate };
    return partition(iterable, factory, predicates);
  }
  


































  public static <O> List<List<O>> partition(Iterable<? extends O> iterable, Predicate<? super O>... predicates)
  {
    Factory<List<O>> factory = FactoryUtils.instantiateFactory(ArrayList.class);
    return partition(iterable, factory, predicates);
  }
  





































  public static <O, R extends Collection<O>> List<R> partition(Iterable<? extends O> iterable, Factory<R> partitionFactory, Predicate<? super O>... predicates)
  {
    if (iterable == null) {
      Iterable<O> empty = emptyIterable();
      return partition(empty, partitionFactory, predicates);
    }
    
    if (predicates == null) {
      throw new NullPointerException("Predicates must not be null.");
    }
    
    for (Predicate<?> p : predicates) {
      if (p == null) {
        throw new NullPointerException("Predicate must not be null.");
      }
    }
    
    if (predicates.length < 1)
    {
      R singlePartition = (Collection)partitionFactory.create();
      CollectionUtils.addAll(singlePartition, iterable);
      return Collections.singletonList(singlePartition);
    }
    

    int numberOfPredicates = predicates.length;
    int numberOfPartitions = numberOfPredicates + 1;
    List<R> partitions = new ArrayList(numberOfPartitions);
    for (int i = 0; i < numberOfPartitions; i++) {
      partitions.add(partitionFactory.create());
    }
    




    for (O element : iterable) {
      boolean elementAssigned = false;
      for (int i = 0; i < numberOfPredicates; i++) {
        if (predicates[i].evaluate(element)) {
          ((Collection)partitions.get(i)).add(element);
          elementAssigned = true;
          break;
        }
      }
      
      if (!elementAssigned)
      {

        ((Collection)partitions.get(numberOfPredicates)).add(element);
      }
    }
    
    return partitions;
  }
  






  public static <E> List<E> toList(Iterable<E> iterable)
  {
    return IteratorUtils.toList(emptyIteratorIfNull(iterable));
  }
  











  public static <E> String toString(Iterable<E> iterable)
  {
    return IteratorUtils.toString(emptyIteratorIfNull(iterable));
  }
  














  public static <E> String toString(Iterable<E> iterable, Transformer<? super E, String> transformer)
  {
    if (transformer == null) {
      throw new NullPointerException("Transformer must not be null.");
    }
    return IteratorUtils.toString(emptyIteratorIfNull(iterable), transformer);
  }
  




















  public static <E> String toString(Iterable<E> iterable, Transformer<? super E, String> transformer, String delimiter, String prefix, String suffix)
  {
    return IteratorUtils.toString(emptyIteratorIfNull(iterable), transformer, delimiter, prefix, suffix);
  }
  









  static void checkNotNull(Iterable<?> iterable)
  {
    if (iterable == null) {
      throw new NullPointerException("Iterable must not be null.");
    }
  }
  





  static void checkNotNull(Iterable<?>... iterables)
  {
    if (iterables == null) {
      throw new NullPointerException("Iterables must not be null.");
    }
    for (Iterable<?> iterable : iterables) {
      checkNotNull(iterable);
    }
  }
  







  private static <E> Iterator<E> emptyIteratorIfNull(Iterable<E> iterable)
  {
    return iterable != null ? iterable.iterator() : IteratorUtils.emptyIterator();
  }
}
