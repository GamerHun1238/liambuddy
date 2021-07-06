package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Unmodifiable;





































public final class UnmodifiableOrderedMapIterator<K, V>
  implements OrderedMapIterator<K, V>, Unmodifiable
{
  private final OrderedMapIterator<? extends K, ? extends V> iterator;
  
  public static <K, V> OrderedMapIterator<K, V> unmodifiableOrderedMapIterator(OrderedMapIterator<K, ? extends V> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("OrderedMapIterator must not be null");
    }
    if ((iterator instanceof Unmodifiable))
    {
      OrderedMapIterator<K, V> tmpIterator = iterator;
      return tmpIterator;
    }
    return new UnmodifiableOrderedMapIterator(iterator);
  }
  






  private UnmodifiableOrderedMapIterator(OrderedMapIterator<K, ? extends V> iterator)
  {
    this.iterator = iterator;
  }
  
  public boolean hasNext()
  {
    return iterator.hasNext();
  }
  
  public K next() {
    return iterator.next();
  }
  
  public boolean hasPrevious() {
    return iterator.hasPrevious();
  }
  
  public K previous() {
    return iterator.previous();
  }
  
  public K getKey() {
    return iterator.getKey();
  }
  
  public V getValue() {
    return iterator.getValue();
  }
  
  public V setValue(V value) {
    throw new UnsupportedOperationException("setValue() is not supported");
  }
  
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported");
  }
}
