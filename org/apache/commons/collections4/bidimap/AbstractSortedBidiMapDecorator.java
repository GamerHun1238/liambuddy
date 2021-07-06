package org.apache.commons.collections4.bidimap;

import java.util.Comparator;
import java.util.SortedMap;
import org.apache.commons.collections4.SortedBidiMap;





































public abstract class AbstractSortedBidiMapDecorator<K, V>
  extends AbstractOrderedBidiMapDecorator<K, V>
  implements SortedBidiMap<K, V>
{
  public AbstractSortedBidiMapDecorator(SortedBidiMap<K, V> map)
  {
    super(map);
  }
  





  protected SortedBidiMap<K, V> decorated()
  {
    return (SortedBidiMap)super.decorated();
  }
  

  public SortedBidiMap<V, K> inverseBidiMap()
  {
    return decorated().inverseBidiMap();
  }
  
  public Comparator<? super K> comparator()
  {
    return decorated().comparator();
  }
  
  public Comparator<? super V> valueComparator()
  {
    return decorated().valueComparator();
  }
  
  public SortedMap<K, V> subMap(K fromKey, K toKey)
  {
    return decorated().subMap(fromKey, toKey);
  }
  
  public SortedMap<K, V> headMap(K toKey)
  {
    return decorated().headMap(toKey);
  }
  
  public SortedMap<K, V> tailMap(K fromKey)
  {
    return decorated().tailMap(fromKey);
  }
}
