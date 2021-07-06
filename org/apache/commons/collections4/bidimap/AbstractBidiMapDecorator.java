package org.apache.commons.collections4.bidimap;

import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.AbstractMapDecorator;





































public abstract class AbstractBidiMapDecorator<K, V>
  extends AbstractMapDecorator<K, V>
  implements BidiMap<K, V>
{
  protected AbstractBidiMapDecorator(BidiMap<K, V> map)
  {
    super(map);
  }
  





  protected BidiMap<K, V> decorated()
  {
    return (BidiMap)super.decorated();
  }
  

  public MapIterator<K, V> mapIterator()
  {
    return decorated().mapIterator();
  }
  
  public K getKey(Object value)
  {
    return decorated().getKey(value);
  }
  
  public K removeValue(Object value)
  {
    return decorated().removeValue(value);
  }
  
  public BidiMap<V, K> inverseBidiMap()
  {
    return decorated().inverseBidiMap();
  }
  
  public Set<V> values()
  {
    return decorated().values();
  }
}
