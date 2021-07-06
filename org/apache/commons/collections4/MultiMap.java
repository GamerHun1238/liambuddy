package org.apache.commons.collections4;

import java.util.Collection;

@Deprecated
public abstract interface MultiMap<K, V>
  extends IterableMap<K, Object>
{
  public abstract boolean removeMapping(K paramK, V paramV);
  
  public abstract int size();
  
  public abstract Object get(Object paramObject);
  
  public abstract boolean containsValue(Object paramObject);
  
  public abstract Object put(K paramK, Object paramObject);
  
  public abstract Object remove(Object paramObject);
  
  public abstract Collection<Object> values();
}
