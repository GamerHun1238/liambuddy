package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import org.apache.commons.collections4.set.UnmodifiableSet;









































public final class UnmodifiableMultiValuedMap<K, V>
  extends AbstractMultiValuedMapDecorator<K, V>
  implements Unmodifiable
{
  private static final long serialVersionUID = 20150612L;
  
  public static <K, V> UnmodifiableMultiValuedMap<K, V> unmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map)
  {
    if ((map instanceof Unmodifiable)) {
      return (UnmodifiableMultiValuedMap)map;
    }
    return new UnmodifiableMultiValuedMap(map);
  }
  






  private UnmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map)
  {
    super(map);
  }
  
  public Collection<V> remove(Object key)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeMapping(Object key, Object item)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public Collection<V> get(K key)
  {
    return UnmodifiableCollection.unmodifiableCollection(decorated().get(key));
  }
  
  public boolean put(K key, V value)
  {
    throw new UnsupportedOperationException();
  }
  
  public Set<K> keySet()
  {
    return UnmodifiableSet.unmodifiableSet(decorated().keySet());
  }
  
  public Collection<Map.Entry<K, V>> entries()
  {
    return UnmodifiableCollection.unmodifiableCollection(decorated().entries());
  }
  
  public MultiSet<K> keys()
  {
    return UnmodifiableMultiSet.unmodifiableMultiSet(decorated().keys());
  }
  
  public Collection<V> values()
  {
    return UnmodifiableCollection.unmodifiableCollection(decorated().values());
  }
  
  public Map<K, Collection<V>> asMap()
  {
    return UnmodifiableMap.unmodifiableMap(decorated().asMap());
  }
  
  public MapIterator<K, V> mapIterator()
  {
    return UnmodifiableMapIterator.unmodifiableMapIterator(decorated().mapIterator());
  }
  
  public boolean putAll(K key, Iterable<? extends V> values)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean putAll(Map<? extends K, ? extends V> map)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean putAll(MultiValuedMap<? extends K, ? extends V> map)
  {
    throw new UnsupportedOperationException();
  }
}
