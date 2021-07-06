package org.apache.commons.collections4;

import java.util.Set;

public abstract interface SetValuedMap<K, V>
  extends MultiValuedMap<K, V>
{
  public abstract Set<V> get(K paramK);
  
  public abstract Set<V> remove(Object paramObject);
}
