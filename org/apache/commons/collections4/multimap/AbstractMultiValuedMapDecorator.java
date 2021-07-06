package org.apache.commons.collections4.multimap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;







































public abstract class AbstractMultiValuedMapDecorator<K, V>
  implements MultiValuedMap<K, V>, Serializable
{
  private static final long serialVersionUID = 20150612L;
  private final MultiValuedMap<K, V> map;
  
  protected AbstractMultiValuedMapDecorator(MultiValuedMap<K, V> map)
  {
    if (map == null) {
      throw new NullPointerException("MultiValuedMap must not be null.");
    }
    this.map = map;
  }
  





  protected MultiValuedMap<K, V> decorated()
  {
    return map;
  }
  

  public int size()
  {
    return decorated().size();
  }
  
  public boolean isEmpty()
  {
    return decorated().isEmpty();
  }
  
  public boolean containsKey(Object key)
  {
    return decorated().containsKey(key);
  }
  
  public boolean containsValue(Object value)
  {
    return decorated().containsValue(value);
  }
  
  public boolean containsMapping(Object key, Object value)
  {
    return decorated().containsMapping(key, value);
  }
  
  public Collection<V> get(K key)
  {
    return decorated().get(key);
  }
  
  public Collection<V> remove(Object key)
  {
    return decorated().remove(key);
  }
  
  public boolean removeMapping(Object key, Object item)
  {
    return decorated().removeMapping(key, item);
  }
  
  public void clear()
  {
    decorated().clear();
  }
  
  public boolean put(K key, V value)
  {
    return decorated().put(key, value);
  }
  
  public Set<K> keySet()
  {
    return decorated().keySet();
  }
  
  public Collection<Map.Entry<K, V>> entries()
  {
    return decorated().entries();
  }
  
  public MultiSet<K> keys()
  {
    return decorated().keys();
  }
  
  public Collection<V> values()
  {
    return decorated().values();
  }
  
  public Map<K, Collection<V>> asMap()
  {
    return decorated().asMap();
  }
  
  public boolean putAll(K key, Iterable<? extends V> values)
  {
    return decorated().putAll(key, values);
  }
  
  public boolean putAll(Map<? extends K, ? extends V> map)
  {
    return decorated().putAll(map);
  }
  
  public boolean putAll(MultiValuedMap<? extends K, ? extends V> map)
  {
    return decorated().putAll(map);
  }
  
  public MapIterator<K, V> mapIterator()
  {
    return decorated().mapIterator();
  }
  
  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    return decorated().equals(object);
  }
  
  public int hashCode()
  {
    return decorated().hashCode();
  }
  
  public String toString()
  {
    return decorated().toString();
  }
}
