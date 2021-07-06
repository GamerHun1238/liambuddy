package org.apache.commons.collections4;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.ArrayListIterator;
import org.apache.commons.collections4.iterators.BoundedIterator;
import org.apache.commons.collections4.iterators.CollatingIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyListIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.iterators.FilterListIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.apache.commons.collections4.iterators.LoopingIterator;
import org.apache.commons.collections4.iterators.LoopingListIterator;
import org.apache.commons.collections4.iterators.NodeListIterator;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.collections4.iterators.ObjectArrayListIterator;
import org.apache.commons.collections4.iterators.ObjectGraphIterator;
import org.apache.commons.collections4.iterators.PeekingIterator;
import org.apache.commons.collections4.iterators.PushbackIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.iterators.SingletonListIterator;
import org.apache.commons.collections4.iterators.SkippingIterator;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.iterators.ZippingIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;































public class IteratorUtils
{
  public static final ResettableIterator EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;
  




  public static final ResettableListIterator EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;
  




  public static final OrderedIterator EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;
  




  public static final MapIterator EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;
  




  public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR = EmptyOrderedMapIterator.INSTANCE;
  




  private static final String DEFAULT_TOSTRING_PREFIX = "[";
  




  private static final String DEFAULT_TOSTRING_SUFFIX = "]";
  




  private static final String DEFAULT_TOSTRING_DELIMITER = ", ";
  





  private IteratorUtils() {}
  





  public static <E> ResettableIterator<E> emptyIterator()
  {
    return EmptyIterator.resettableEmptyIterator();
  }
  








  public static <E> ResettableListIterator<E> emptyListIterator()
  {
    return EmptyListIterator.resettableEmptyListIterator();
  }
  








  public static <E> OrderedIterator<E> emptyOrderedIterator()
  {
    return EmptyOrderedIterator.emptyOrderedIterator();
  }
  









  public static <K, V> MapIterator<K, V> emptyMapIterator()
  {
    return EmptyMapIterator.emptyMapIterator();
  }
  









  public static <K, V> OrderedMapIterator<K, V> emptyOrderedMapIterator()
  {
    return EmptyOrderedMapIterator.emptyOrderedMapIterator();
  }
  











  public static <E> ResettableIterator<E> singletonIterator(E object)
  {
    return new SingletonIterator(object);
  }
  









  public static <E> ListIterator<E> singletonListIterator(E object)
  {
    return new SingletonListIterator(object);
  }
  









  public static <E> ResettableIterator<E> arrayIterator(E... array)
  {
    return new ObjectArrayIterator(array);
  }
  











  public static <E> ResettableIterator<E> arrayIterator(Object array)
  {
    return new ArrayIterator(array);
  }
  










  public static <E> ResettableIterator<E> arrayIterator(E[] array, int start)
  {
    return new ObjectArrayIterator(array, start);
  }
  














  public static <E> ResettableIterator<E> arrayIterator(Object array, int start)
  {
    return new ArrayIterator(array, start);
  }
  











  public static <E> ResettableIterator<E> arrayIterator(E[] array, int start, int end)
  {
    return new ObjectArrayIterator(array, start, end);
  }
  














  public static <E> ResettableIterator<E> arrayIterator(Object array, int start, int end)
  {
    return new ArrayIterator(array, start, end);
  }
  








  public static <E> ResettableListIterator<E> arrayListIterator(E... array)
  {
    return new ObjectArrayListIterator(array);
  }
  











  public static <E> ResettableListIterator<E> arrayListIterator(Object array)
  {
    return new ArrayListIterator(array);
  }
  









  public static <E> ResettableListIterator<E> arrayListIterator(E[] array, int start)
  {
    return new ObjectArrayListIterator(array, start);
  }
  













  public static <E> ResettableListIterator<E> arrayListIterator(Object array, int start)
  {
    return new ArrayListIterator(array, start);
  }
  











  public static <E> ResettableListIterator<E> arrayListIterator(E[] array, int start, int end)
  {
    return new ObjectArrayListIterator(array, start, end);
  }
  














  public static <E> ResettableListIterator<E> arrayListIterator(Object array, int start, int end)
  {
    return new ArrayListIterator(array, start, end);
  }
  













  public static <E> BoundedIterator<E> boundedIterator(Iterator<? extends E> iterator, long max)
  {
    return boundedIterator(iterator, 0L, max);
  }
  

















  public static <E> BoundedIterator<E> boundedIterator(Iterator<? extends E> iterator, long offset, long max)
  {
    return new BoundedIterator(iterator, offset, max);
  }
  










  public static <E> Iterator<E> unmodifiableIterator(Iterator<E> iterator)
  {
    return UnmodifiableIterator.unmodifiableIterator(iterator);
  }
  









  public static <E> ListIterator<E> unmodifiableListIterator(ListIterator<E> listIterator)
  {
    return UnmodifiableListIterator.umodifiableListIterator(listIterator);
  }
  









  public static <K, V> MapIterator<K, V> unmodifiableMapIterator(MapIterator<K, V> mapIterator)
  {
    return UnmodifiableMapIterator.unmodifiableMapIterator(mapIterator);
  }
  















  public static <E> Iterator<E> chainedIterator(Iterator<? extends E> iterator1, Iterator<? extends E> iterator2)
  {
    return new IteratorChain(iterator1, iterator2);
  }
  








  public static <E> Iterator<E> chainedIterator(Iterator<? extends E>... iterators)
  {
    return new IteratorChain(iterators);
  }
  









  public static <E> Iterator<E> chainedIterator(Collection<Iterator<? extends E>> iterators)
  {
    return new IteratorChain(iterators);
  }
  





















  public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Iterator<? extends E> iterator1, Iterator<? extends E> iterator2)
  {
    Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
    
    return new CollatingIterator(comp, iterator1, iterator2);
  }
  

















  public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Iterator<? extends E>... iterators)
  {
    Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
    
    return new CollatingIterator(comp, iterators);
  }
  


















  public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Collection<Iterator<? extends E>> iterators)
  {
    Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
    
    return new CollatingIterator(comp, iterators);
  }
  
























































  public static <E> Iterator<E> objectGraphIterator(E root, Transformer<? super E, ? extends E> transformer)
  {
    return new ObjectGraphIterator(root, transformer);
  }
  
















  public static <I, O> Iterator<O> transformedIterator(Iterator<? extends I> iterator, Transformer<? super I, ? extends O> transform)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if (transform == null) {
      throw new NullPointerException("Transformer must not be null");
    }
    return new TransformIterator(iterator, transform);
  }
  














  public static <E> Iterator<E> filteredIterator(Iterator<? extends E> iterator, Predicate<? super E> predicate)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    return new FilterIterator(iterator, predicate);
  }
  













  public static <E> ListIterator<E> filteredListIterator(ListIterator<? extends E> listIterator, Predicate<? super E> predicate)
  {
    if (listIterator == null) {
      throw new NullPointerException("ListIterator must not be null");
    }
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    return new FilterListIterator(listIterator, predicate);
  }
  













  public static <E> ResettableIterator<E> loopingIterator(Collection<? extends E> coll)
  {
    if (coll == null) {
      throw new NullPointerException("Collection must not be null");
    }
    return new LoopingIterator(coll);
  }
  











  public static <E> ResettableListIterator<E> loopingListIterator(List<E> list)
  {
    if (list == null) {
      throw new NullPointerException("List must not be null");
    }
    return new LoopingListIterator(list);
  }
  










  public static NodeListIterator nodeListIterator(NodeList nodeList)
  {
    if (nodeList == null) {
      throw new NullPointerException("NodeList must not be null");
    }
    return new NodeListIterator(nodeList);
  }
  
















  public static NodeListIterator nodeListIterator(Node node)
  {
    if (node == null) {
      throw new NullPointerException("Node must not be null");
    }
    return new NodeListIterator(node);
  }
  











  public static <E> Iterator<E> peekingIterator(Iterator<? extends E> iterator)
  {
    return PeekingIterator.peekingIterator(iterator);
  }
  











  public static <E> Iterator<E> pushbackIterator(Iterator<? extends E> iterator)
  {
    return PushbackIterator.pushbackIterator(iterator);
  }
  












  public static <E> SkippingIterator<E> skippingIterator(Iterator<E> iterator, long offset)
  {
    return new SkippingIterator(iterator, offset);
  }
  












  public static <E> ZippingIterator<E> zippingIterator(Iterator<? extends E> a, Iterator<? extends E> b)
  {
    return new ZippingIterator(a, b);
  }
  












  public static <E> ZippingIterator<E> zippingIterator(Iterator<? extends E> a, Iterator<? extends E> b, Iterator<? extends E> c)
  {
    return new ZippingIterator(a, b, c);
  }
  








  public static <E> ZippingIterator<E> zippingIterator(Iterator<? extends E>... iterators)
  {
    return new ZippingIterator(iterators);
  }
  









  public static <E> Iterator<E> asIterator(Enumeration<? extends E> enumeration)
  {
    if (enumeration == null) {
      throw new NullPointerException("Enumeration must not be null");
    }
    return new EnumerationIterator(enumeration);
  }
  










  public static <E> Iterator<E> asIterator(Enumeration<? extends E> enumeration, Collection<? super E> removeCollection)
  {
    if (enumeration == null) {
      throw new NullPointerException("Enumeration must not be null");
    }
    if (removeCollection == null) {
      throw new NullPointerException("Collection must not be null");
    }
    return new EnumerationIterator(enumeration, removeCollection);
  }
  







  public static <E> Enumeration<E> asEnumeration(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    return new IteratorEnumeration(iterator);
  }
  








  public static <E> Iterable<E> asIterable(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    return new IteratorIterable(iterator, false);
  }
  








  public static <E> Iterable<E> asMultipleUseIterable(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    return new IteratorIterable(iterator, true);
  }
  










  public static <E> ListIterator<E> toListIterator(Iterator<? extends E> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    return new ListIteratorWrapper(iterator);
  }
  









  public static Object[] toArray(Iterator<?> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    List<?> list = toList(iterator, 100);
    return list.toArray();
  }
  












  public static <E> E[] toArray(Iterator<? extends E> iterator, Class<E> arrayClass)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if (arrayClass == null) {
      throw new NullPointerException("Array class must not be null");
    }
    List<E> list = toList(iterator, 100);
    
    E[] array = (Object[])Array.newInstance(arrayClass, list.size());
    return list.toArray(array);
  }
  










  public static <E> List<E> toList(Iterator<? extends E> iterator)
  {
    return toList(iterator, 10);
  }
  












  public static <E> List<E> toList(Iterator<? extends E> iterator, int estimatedSize)
  {
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    if (estimatedSize < 1) {
      throw new IllegalArgumentException("Estimated size must be greater than 0");
    }
    List<E> list = new ArrayList(estimatedSize);
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return list;
  }
  




















  public static Iterator<?> getIterator(Object obj)
  {
    if (obj == null) {
      return emptyIterator();
    }
    if ((obj instanceof Iterator)) {
      return (Iterator)obj;
    }
    if ((obj instanceof Iterable)) {
      return ((Iterable)obj).iterator();
    }
    if ((obj instanceof Object[])) {
      return new ObjectArrayIterator((Object[])obj);
    }
    if ((obj instanceof Enumeration)) {
      return new EnumerationIterator((Enumeration)obj);
    }
    if ((obj instanceof Map)) {
      return ((Map)obj).values().iterator();
    }
    if ((obj instanceof NodeList)) {
      return new NodeListIterator((NodeList)obj);
    }
    if ((obj instanceof Node)) {
      return new NodeListIterator((Node)obj);
    }
    if ((obj instanceof Dictionary))
      return new EnumerationIterator(((Dictionary)obj).elements());
    if (obj.getClass().isArray()) {
      return new ArrayIterator(obj);
    }
    try {
      Method method = obj.getClass().getMethod("iterator", (Class[])null);
      if (Iterator.class.isAssignableFrom(method.getReturnType())) {
        Iterator<?> it = (Iterator)method.invoke(obj, (Object[])null);
        if (it != null) {
          return it;
        }
      }
    }
    catch (RuntimeException e) {}catch (NoSuchMethodException e) {}catch (IllegalAccessException e) {}catch (InvocationTargetException e) {}
    






    return singletonIterator(obj);
  }
  











  public static <E> void forEach(Iterator<E> iterator, Closure<? super E> closure)
  {
    if (closure == null) {
      throw new NullPointerException("Closure must not be null");
    }
    
    if (iterator != null) {
      while (iterator.hasNext()) {
        E element = iterator.next();
        closure.execute(element);
      }
    }
  }
  











  public static <E> E forEachButLast(Iterator<E> iterator, Closure<? super E> closure)
  {
    if (closure == null) {
      throw new NullPointerException("Closure must not be null.");
    }
    if (iterator != null) {
      while (iterator.hasNext()) {
        E element = iterator.next();
        if (iterator.hasNext()) {
          closure.execute(element);
        } else {
          return element;
        }
      }
    }
    return null;
  }
  











  public static <E> E find(Iterator<E> iterator, Predicate<? super E> predicate)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    
    if (iterator != null) {
      while (iterator.hasNext()) {
        E element = iterator.next();
        if (predicate.evaluate(element)) {
          return element;
        }
      }
    }
    return null;
  }
  












  public static <E> int indexOf(Iterator<E> iterator, Predicate<? super E> predicate)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    
    if (iterator != null) {
      for (int index = 0; iterator.hasNext(); index++) {
        E element = iterator.next();
        if (predicate.evaluate(element)) {
          return index;
        }
      }
    }
    return -1;
  }
  











  public static <E> boolean matchesAny(Iterator<E> iterator, Predicate<? super E> predicate)
  {
    return indexOf(iterator, predicate) != -1;
  }
  












  public static <E> boolean matchesAll(Iterator<E> iterator, Predicate<? super E> predicate)
  {
    if (predicate == null) {
      throw new NullPointerException("Predicate must not be null");
    }
    
    if (iterator != null) {
      while (iterator.hasNext()) {
        E element = iterator.next();
        if (!predicate.evaluate(element)) {
          return false;
        }
      }
    }
    return true;
  }
  








  public static boolean isEmpty(Iterator<?> iterator)
  {
    return (iterator == null) || (!iterator.hasNext());
  }
  










  public static <E> boolean contains(Iterator<E> iterator, Object object)
  {
    return matchesAny(iterator, EqualPredicate.equalPredicate(object));
  }
  













  public static <E> E get(Iterator<E> iterator, int index)
  {
    int i = index;
    CollectionUtils.checkIndexBounds(i);
    while (iterator.hasNext()) {
      i--;
      if (i == -1) {
        return iterator.next();
      }
      iterator.next();
    }
    throw new IndexOutOfBoundsException("Entry does not exist: " + i);
  }
  








  public static int size(Iterator<?> iterator)
  {
    int size = 0;
    if (iterator != null) {
      while (iterator.hasNext()) {
        iterator.next();
        size++;
      }
    }
    return size;
  }
  












  public static <E> String toString(Iterator<E> iterator)
  {
    return toString(iterator, TransformerUtils.stringValueTransformer(), ", ", "[", "]");
  }
  

















  public static <E> String toString(Iterator<E> iterator, Transformer<? super E, String> transformer)
  {
    return toString(iterator, transformer, ", ", "[", "]");
  }
  






















  public static <E> String toString(Iterator<E> iterator, Transformer<? super E, String> transformer, String delimiter, String prefix, String suffix)
  {
    if (transformer == null) {
      throw new NullPointerException("transformer may not be null");
    }
    if (delimiter == null) {
      throw new NullPointerException("delimiter may not be null");
    }
    if (prefix == null) {
      throw new NullPointerException("prefix may not be null");
    }
    if (suffix == null) {
      throw new NullPointerException("suffix may not be null");
    }
    StringBuilder stringBuilder = new StringBuilder(prefix);
    if (iterator != null) {
      while (iterator.hasNext()) {
        E element = iterator.next();
        stringBuilder.append((String)transformer.transform(element));
        stringBuilder.append(delimiter);
      }
      if (stringBuilder.length() > prefix.length()) {
        stringBuilder.setLength(stringBuilder.length() - delimiter.length());
      }
    }
    stringBuilder.append(suffix);
    return stringBuilder.toString();
  }
}
