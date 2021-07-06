package org.apache.commons.collections4.map;

import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;










































public abstract class AbstractOrderedMapDecorator<K, V>
  extends AbstractMapDecorator<K, V>
  implements OrderedMap<K, V>
{
  protected AbstractOrderedMapDecorator() {}
  
  public AbstractOrderedMapDecorator(OrderedMap<K, V> map)
  {
    super(map);
  }
  





  protected OrderedMap<K, V> decorated()
  {
    return (OrderedMap)super.decorated();
  }
  
  public K firstKey()
  {
    return decorated().firstKey();
  }
  
  public K lastKey() {
    return decorated().lastKey();
  }
  
  public K nextKey(K key) {
    return decorated().nextKey(key);
  }
  
  public K previousKey(K key) {
    return decorated().previousKey(key);
  }
  
  public OrderedMapIterator<K, V> mapIterator()
  {
    return decorated().mapIterator();
  }
}
