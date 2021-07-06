package org.apache.commons.collections4;

import java.util.Comparator;
import java.util.SortedMap;

public abstract interface SortedBidiMap<K, V>
  extends OrderedBidiMap<K, V>, SortedMap<K, V>
{
  public abstract SortedBidiMap<V, K> inverseBidiMap();
  
  public abstract Comparator<? super V> valueComparator();
}
