package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;



















































public class FixedSizeMap<K, V>
  extends AbstractMapDecorator<K, V>
  implements BoundedMap<K, V>, Serializable
{
  private static final long serialVersionUID = 7450927208116179316L;
  
  public static <K, V> FixedSizeMap<K, V> fixedSizeMap(Map<K, V> map)
  {
    return new FixedSizeMap(map);
  }
  






  protected FixedSizeMap(Map<K, V> map)
  {
    super(map);
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
    for (K key : mapToCopy.keySet()) {
      if (!containsKey(key)) {
        throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
      }
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
    Set<Map.Entry<K, V>> set = map.entrySet();
    
    return UnmodifiableSet.unmodifiableSet(set);
  }
  
  public Set<K> keySet()
  {
    Set<K> set = map.keySet();
    return UnmodifiableSet.unmodifiableSet(set);
  }
  
  public Collection<V> values()
  {
    Collection<V> coll = map.values();
    return UnmodifiableCollection.unmodifiableCollection(coll);
  }
  
  public boolean isFull() {
    return true;
  }
  
  public int maxSize() {
    return size();
  }
}
