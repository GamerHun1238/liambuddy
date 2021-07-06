package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;



















































public class FixedSizeSortedMap<K, V>
  extends AbstractSortedMapDecorator<K, V>
  implements BoundedMap<K, V>, Serializable
{
  private static final long serialVersionUID = 3126019624511683653L;
  
  public static <K, V> FixedSizeSortedMap<K, V> fixedSizeSortedMap(SortedMap<K, V> map)
  {
    return new FixedSizeSortedMap(map);
  }
  






  protected FixedSizeSortedMap(SortedMap<K, V> map)
  {
    super(map);
  }
  




  protected SortedMap<K, V> getSortedMap()
  {
    return (SortedMap)map;
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(map);
  }
  


  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    map = ((Map)in.readObject());
  }
  

  public V put(K key, V value)
  {
    if (!map.containsKey(key)) {
      throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
    }
    return map.put(key, value);
  }
  
  public void putAll(Map<? extends K, ? extends V> mapToCopy)
  {
    if (CollectionUtils.isSubCollection(mapToCopy.keySet(), keySet())) {
      throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
    }
    map.putAll(mapToCopy);
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException("Map is fixed size");
  }
  
  public V remove(Object key)
  {
    throw new UnsupportedOperationException("Map is fixed size");
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    return UnmodifiableSet.unmodifiableSet(map.entrySet());
  }
  
  public Set<K> keySet()
  {
    return UnmodifiableSet.unmodifiableSet(map.keySet());
  }
  
  public Collection<V> values()
  {
    return UnmodifiableCollection.unmodifiableCollection(map.values());
  }
  

  public SortedMap<K, V> subMap(K fromKey, K toKey)
  {
    return new FixedSizeSortedMap(getSortedMap().subMap(fromKey, toKey));
  }
  
  public SortedMap<K, V> headMap(K toKey)
  {
    return new FixedSizeSortedMap(getSortedMap().headMap(toKey));
  }
  
  public SortedMap<K, V> tailMap(K fromKey)
  {
    return new FixedSizeSortedMap(getSortedMap().tailMap(fromKey));
  }
  
  public boolean isFull() {
    return true;
  }
  
  public int maxSize() {
    return size();
  }
}
