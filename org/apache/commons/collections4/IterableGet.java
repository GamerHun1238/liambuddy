package org.apache.commons.collections4;

public abstract interface IterableGet<K, V>
  extends Get<K, V>
{
  public abstract MapIterator<K, V> mapIterator();
}
