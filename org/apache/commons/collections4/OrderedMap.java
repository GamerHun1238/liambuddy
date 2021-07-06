package org.apache.commons.collections4;

public abstract interface OrderedMap<K, V>
  extends IterableMap<K, V>
{
  public abstract OrderedMapIterator<K, V> mapIterator();
  
  public abstract K firstKey();
  
  public abstract K lastKey();
  
  public abstract K nextKey(K paramK);
  
  public abstract K previousKey(K paramK);
}
