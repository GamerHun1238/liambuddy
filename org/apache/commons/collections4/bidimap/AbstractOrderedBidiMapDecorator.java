package org.apache.commons.collections4.bidimap;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMapIterator;





































public abstract class AbstractOrderedBidiMapDecorator<K, V>
  extends AbstractBidiMapDecorator<K, V>
  implements OrderedBidiMap<K, V>
{
  protected AbstractOrderedBidiMapDecorator(OrderedBidiMap<K, V> map)
  {
    super(map);
  }
  





  protected OrderedBidiMap<K, V> decorated()
  {
    return (OrderedBidiMap)super.decorated();
  }
  

  public OrderedMapIterator<K, V> mapIterator()
  {
    return decorated().mapIterator();
  }
  
  public K firstKey()
  {
    return decorated().firstKey();
  }
  
  public K lastKey()
  {
    return decorated().lastKey();
  }
  
  public K nextKey(K key)
  {
    return decorated().nextKey(key);
  }
  
  public K previousKey(K key)
  {
    return decorated().previousKey(key);
  }
  
  public OrderedBidiMap<V, K> inverseBidiMap()
  {
    return decorated().inverseBidiMap();
  }
}
