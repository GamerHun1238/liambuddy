package org.apache.commons.collections4;

import java.util.List;

public abstract interface ListValuedMap<K, V>
  extends MultiValuedMap<K, V>
{
  public abstract List<V> get(K paramK);
  
  public abstract List<V> remove(Object paramObject);
}
