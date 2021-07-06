package org.apache.commons.collections4;

public abstract interface BoundedMap<K, V>
  extends IterableMap<K, V>
{
  public abstract boolean isFull();
  
  public abstract int maxSize();
}
