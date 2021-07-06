package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

public abstract interface Get<K, V>
{
  public abstract boolean containsKey(Object paramObject);
  
  public abstract boolean containsValue(Object paramObject);
  
  public abstract Set<Map.Entry<K, V>> entrySet();
  
  public abstract V get(Object paramObject);
  
  public abstract V remove(Object paramObject);
  
  public abstract boolean isEmpty();
  
  public abstract Set<K> keySet();
  
  public abstract int size();
  
  public abstract Collection<V> values();
}
