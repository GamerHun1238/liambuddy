package org.apache.commons.collections4.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.CompositeCollection;
import org.apache.commons.collections4.set.CompositeSet;








































public class CompositeMap<K, V>
  extends AbstractIterableMap<K, V>
  implements Serializable
{
  private static final long serialVersionUID = -6096931280583808322L;
  private Map<K, V>[] composite;
  private MapMutator<K, V> mutator;
  
  public CompositeMap()
  {
    this(new Map[0], null);
  }
  







  public CompositeMap(Map<K, V> one, Map<K, V> two)
  {
    this(new Map[] { one, two }, null);
  }
  







  public CompositeMap(Map<K, V> one, Map<K, V> two, MapMutator<K, V> mutator)
  {
    this(new Map[] { one, two }, mutator);
  }
  






  public CompositeMap(Map<K, V>... composite)
  {
    this(composite, null);
  }
  







  public CompositeMap(Map<K, V>[] composite, MapMutator<K, V> mutator)
  {
    this.mutator = mutator;
    this.composite = new Map[0];
    for (int i = composite.length - 1; i >= 0; i--) {
      addComposited(composite[i]);
    }
  }
  





  public void setMutator(MapMutator<K, V> mutator)
  {
    this.mutator = mutator;
  }
  






  public synchronized void addComposited(Map<K, V> map)
    throws IllegalArgumentException
  {
    for (int i = composite.length - 1; i >= 0; i--) {
      Collection<K> intersect = CollectionUtils.intersection(composite[i].keySet(), map.keySet());
      if (intersect.size() != 0) {
        if (mutator == null) {
          throw new IllegalArgumentException("Key collision adding Map to CompositeMap");
        }
        mutator.resolveCollision(this, composite[i], map, intersect);
      }
    }
    Map<K, V>[] temp = new Map[composite.length + 1];
    System.arraycopy(composite, 0, temp, 0, composite.length);
    temp[(temp.length - 1)] = map;
    composite = temp;
  }
  






  public synchronized Map<K, V> removeComposited(Map<K, V> map)
  {
    int size = composite.length;
    for (int i = 0; i < size; i++) {
      if (composite[i].equals(map)) {
        Map<K, V>[] temp = new Map[size - 1];
        System.arraycopy(composite, 0, temp, 0, i);
        System.arraycopy(composite, i + 1, temp, i, size - i - 1);
        composite = temp;
        return map;
      }
    }
    return null;
  }
  





  public void clear()
  {
    for (int i = composite.length - 1; i >= 0; i--) {
      composite[i].clear();
    }
  }
  















  public boolean containsKey(Object key)
  {
    for (int i = composite.length - 1; i >= 0; i--) {
      if (composite[i].containsKey(key)) {
        return true;
      }
    }
    return false;
  }
  















  public boolean containsValue(Object value)
  {
    for (int i = composite.length - 1; i >= 0; i--) {
      if (composite[i].containsValue(value)) {
        return true;
      }
    }
    return false;
  }
  
















  public Set<Map.Entry<K, V>> entrySet()
  {
    CompositeSet<Map.Entry<K, V>> entries = new CompositeSet();
    for (int i = composite.length - 1; i >= 0; i--) {
      entries.addComposited(composite[i].entrySet());
    }
    return entries;
  }
  























  public V get(Object key)
  {
    for (int i = composite.length - 1; i >= 0; i--) {
      if (composite[i].containsKey(key)) {
        return composite[i].get(key);
      }
    }
    return null;
  }
  




  public boolean isEmpty()
  {
    for (int i = composite.length - 1; i >= 0; i--) {
      if (!composite[i].isEmpty()) {
        return false;
      }
    }
    return true;
  }
  














  public Set<K> keySet()
  {
    CompositeSet<K> keys = new CompositeSet();
    for (int i = composite.length - 1; i >= 0; i--) {
      keys.addComposited(composite[i].keySet());
    }
    return keys;
  }
  
























  public V put(K key, V value)
  {
    if (mutator == null) {
      throw new UnsupportedOperationException("No mutator specified");
    }
    return mutator.put(this, composite, key, value);
  }
  





















  public void putAll(Map<? extends K, ? extends V> map)
  {
    if (mutator == null) {
      throw new UnsupportedOperationException("No mutator specified");
    }
    mutator.putAll(this, composite, map);
  }
  
























  public V remove(Object key)
  {
    for (int i = composite.length - 1; i >= 0; i--) {
      if (composite[i].containsKey(key)) {
        return composite[i].remove(key);
      }
    }
    return null;
  }
  






  public int size()
  {
    int size = 0;
    for (int i = composite.length - 1; i >= 0; i--) {
      size += composite[i].size();
    }
    return size;
  }
  












  public Collection<V> values()
  {
    CompositeCollection<V> values = new CompositeCollection();
    for (int i = composite.length - 1; i >= 0; i--) {
      values.addComposited(composite[i].values());
    }
    return values;
  }
  






  public boolean equals(Object obj)
  {
    if ((obj instanceof Map)) {
      Map<?, ?> map = (Map)obj;
      return entrySet().equals(map.entrySet());
    }
    return false;
  }
  




  public int hashCode()
  {
    int code = 0;
    for (Map.Entry<K, V> entry : entrySet()) {
      code += entry.hashCode();
    }
    return code;
  }
  
  public static abstract interface MapMutator<K, V>
    extends Serializable
  {
    public abstract void resolveCollision(CompositeMap<K, V> paramCompositeMap, Map<K, V> paramMap1, Map<K, V> paramMap2, Collection<K> paramCollection);
    
    public abstract V put(CompositeMap<K, V> paramCompositeMap, Map<K, V>[] paramArrayOfMap, K paramK, V paramV);
    
    public abstract void putAll(CompositeMap<K, V> paramCompositeMap, Map<K, V>[] paramArrayOfMap, Map<? extends K, ? extends V> paramMap);
  }
}
