package org.apache.commons.collections4;

public abstract interface OrderedMapIterator<K, V>
  extends MapIterator<K, V>, OrderedIterator<K>
{
  public abstract boolean hasPrevious();
  
  public abstract K previous();
}
