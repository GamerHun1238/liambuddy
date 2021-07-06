package org.apache.commons.collections4;

public abstract interface KeyValue<K, V>
{
  public abstract K getKey();
  
  public abstract V getValue();
}
