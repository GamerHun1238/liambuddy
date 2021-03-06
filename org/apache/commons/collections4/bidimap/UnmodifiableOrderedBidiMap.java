package org.apache.commons.collections4.bidimap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;





































public final class UnmodifiableOrderedBidiMap<K, V>
  extends AbstractOrderedBidiMapDecorator<K, V>
  implements Unmodifiable
{
  private UnmodifiableOrderedBidiMap<V, K> inverse;
  
  public static <K, V> OrderedBidiMap<K, V> unmodifiableOrderedBidiMap(OrderedBidiMap<? extends K, ? extends V> map)
  {
    if ((map instanceof Unmodifiable))
    {
      OrderedBidiMap<K, V> tmpMap = map;
      return tmpMap;
    }
    return new UnmodifiableOrderedBidiMap(map);
  }
  







  private UnmodifiableOrderedBidiMap(OrderedBidiMap<? extends K, ? extends V> map)
  {
    super(map);
  }
  

  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public V put(K key, V value)
  {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends V> mapToCopy)
  {
    throw new UnsupportedOperationException();
  }
  
  public V remove(Object key)
  {
    throw new UnsupportedOperationException();
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    Set<Map.Entry<K, V>> set = super.entrySet();
    return UnmodifiableEntrySet.unmodifiableEntrySet(set);
  }
  
  public Set<K> keySet()
  {
    Set<K> set = super.keySet();
    return UnmodifiableSet.unmodifiableSet(set);
  }
  
  public Set<V> values()
  {
    Set<V> set = super.values();
    return UnmodifiableSet.unmodifiableSet(set);
  }
  

  public K removeValue(Object value)
  {
    throw new UnsupportedOperationException();
  }
  
  public OrderedBidiMap<V, K> inverseBidiMap()
  {
    return inverseOrderedBidiMap();
  }
  

  public OrderedMapIterator<K, V> mapIterator()
  {
    OrderedMapIterator<K, V> it = decorated().mapIterator();
    return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator(it);
  }
  




  public OrderedBidiMap<V, K> inverseOrderedBidiMap()
  {
    if (inverse == null) {
      inverse = new UnmodifiableOrderedBidiMap(decorated().inverseBidiMap());
      inverse.inverse = this;
    }
    return inverse;
  }
}
