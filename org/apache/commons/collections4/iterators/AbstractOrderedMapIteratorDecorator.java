package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.OrderedMapIterator;
































public class AbstractOrderedMapIteratorDecorator<K, V>
  implements OrderedMapIterator<K, V>
{
  private final OrderedMapIterator<K, V> iterator;
  
  public AbstractOrderedMapIteratorDecorator(OrderedMapIterator<K, V> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("OrderedMapIterator must not be null");
    }
    this.iterator = iterator;
  }
  




  protected OrderedMapIterator<K, V> getOrderedMapIterator()
  {
    return iterator;
  }
  


  public boolean hasNext()
  {
    return iterator.hasNext();
  }
  
  public K next()
  {
    return iterator.next();
  }
  
  public boolean hasPrevious()
  {
    return iterator.hasPrevious();
  }
  
  public K previous()
  {
    return iterator.previous();
  }
  
  public void remove()
  {
    iterator.remove();
  }
  
  public K getKey()
  {
    return iterator.getKey();
  }
  
  public V getValue()
  {
    return iterator.getValue();
  }
  
  public V setValue(V obj)
  {
    return iterator.setValue(obj);
  }
}
