package org.apache.commons.collections4;

import java.util.Map;

public abstract interface Put<K, V>
{
  public abstract void clear();
  
  public abstract Object put(K paramK, V paramV);
  
  public abstract void putAll(Map<? extends K, ? extends V> paramMap);
}
