package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.MapIterator;
































public class AbstractMapIteratorDecorator<K, V>
  implements MapIterator<K, V>
{
  private final MapIterator<K, V> iterator;
  
  public AbstractMapIteratorDecorator(MapIterator<K, V> iterator)
  {
    if (iterator == null) {
      throw new NullPointerException("MapIterator must not be null");
    }
    this.iterator = iterator;
  }
  




  protected MapIterator<K, V> getMapIterator()
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
