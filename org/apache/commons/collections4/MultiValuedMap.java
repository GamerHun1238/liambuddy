package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract interface MultiValuedMap<K, V>
{
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract boolean containsKey(Object paramObject);
  
  public abstract boolean containsValue(Object paramObject);
  
  public abstract boolean containsMapping(Object paramObject1, Object paramObject2);
  
  public abstract Collection<V> get(K paramK);
  
  public abstract boolean put(K paramK, V paramV);
  
  public abstract boolean putAll(K paramK, Iterable<? extends V> paramIterable);
  
  public abstract boolean putAll(Map<? extends K, ? extends V> paramMap);
  
  public abstract boolean putAll(MultiValuedMap<? extends K, ? extends V> paramMultiValuedMap);
  
  public abstract Collection<V> remove(Object paramObject);
  
  public abstract boolean removeMapping(Object paramObject1, Object paramObject2);
  
  public abstract void clear();
  
  public abstract Collection<Map.Entry<K, V>> entries();
  
  public abstract MultiSet<K> keys();
  
  public abstract Set<K> keySet();
  
  public abstract Collection<V> values();
  
  public abstract Map<K, Collection<V>> asMap();
  
  public abstract MapIterator<K, V> mapIterator();
}
