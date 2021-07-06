package org.apache.commons.collections4;

public abstract interface OrderedBidiMap<K, V>
  extends BidiMap<K, V>, OrderedMap<K, V>
{
  public abstract OrderedBidiMap<V, K> inverseBidiMap();
}
