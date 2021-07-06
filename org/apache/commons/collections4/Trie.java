package org.apache.commons.collections4;

import java.util.SortedMap;

public abstract interface Trie<K, V>
  extends IterableSortedMap<K, V>
{
  public abstract SortedMap<K, V> prefixMap(K paramK);
}
