package org.apache.commons.collections4.set;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;











































public final class MapBackedSet<E, V>
  implements Set<E>, Serializable
{
  private static final long serialVersionUID = 6723912213766056587L;
  private final Map<E, ? super V> map;
  private final V dummyValue;
  
  public static <E, V> MapBackedSet<E, V> mapBackedSet(Map<E, ? super V> map)
  {
    return mapBackedSet(map, null);
  }
  










  public static <E, V> MapBackedSet<E, V> mapBackedSet(Map<E, ? super V> map, V dummyValue)
  {
    return new MapBackedSet(map, dummyValue);
  }
  








  private MapBackedSet(Map<E, ? super V> map, V dummyValue)
  {
    if (map == null) {
      throw new NullPointerException("The map must not be null");
    }
    this.map = map;
    this.dummyValue = dummyValue;
  }
  
  public int size()
  {
    return map.size();
  }
  
  public boolean isEmpty() {
    return map.isEmpty();
  }
  
  public Iterator<E> iterator() {
    return map.keySet().iterator();
  }
  
  public boolean contains(Object obj) {
    return map.containsKey(obj);
  }
  
  public boolean containsAll(Collection<?> coll) {
    return map.keySet().containsAll(coll);
  }
  
  public boolean add(E obj) {
    int size = map.size();
    map.put(obj, dummyValue);
    return map.size() != size;
  }
  
  public boolean addAll(Collection<? extends E> coll) {
    int size = map.size();
    for (E e : coll) {
      map.put(e, dummyValue);
    }
    return map.size() != size;
  }
  
  public boolean remove(Object obj) {
    int size = map.size();
    map.remove(obj);
    return map.size() != size;
  }
  
  public boolean removeAll(Collection<?> coll) {
    return map.keySet().removeAll(coll);
  }
  
  public boolean retainAll(Collection<?> coll) {
    return map.keySet().retainAll(coll);
  }
  
  public void clear() {
    map.clear();
  }
  
  public Object[] toArray() {
    return map.keySet().toArray();
  }
  
  public <T> T[] toArray(T[] array) {
    return map.keySet().toArray(array);
  }
  
  public boolean equals(Object obj)
  {
    return map.keySet().equals(obj);
  }
  
  public int hashCode()
  {
    return map.keySet().hashCode();
  }
}
