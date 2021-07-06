package org.apache.commons.collections4.map;

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;






















public abstract class AbstractIterableMap<K, V>
  implements IterableMap<K, V>
{
  public AbstractIterableMap() {}
  
  public MapIterator<K, V> mapIterator()
  {
    return new EntrySetToMapIteratorAdapter(entrySet());
  }
}
