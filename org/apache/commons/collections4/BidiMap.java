package org.apache.commons.collections4;

import java.util.Set;

public abstract interface BidiMap<K, V>
  extends IterableMap<K, V>
{
  public abstract V put(K paramK, V paramV);
  
  public abstract K getKey(Object paramObject);
  
  public abstract K removeValue(Object paramObject);
  
  public abstract BidiMap<V, K> inverseBidiMap();
  
  public abstract Set<V> values();
}
