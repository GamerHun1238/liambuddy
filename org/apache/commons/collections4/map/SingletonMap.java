package org.apache.commons.collections4.map;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;















































public class SingletonMap<K, V>
  implements OrderedMap<K, V>, BoundedMap<K, V>, KeyValue<K, V>, Serializable, Cloneable
{
  private static final long serialVersionUID = -8931271118676803261L;
  private final K key;
  private V value;
  
  public SingletonMap()
  {
    key = null;
  }
  






  public SingletonMap(K key, V value)
  {
    this.key = key;
    this.value = value;
  }
  





  public SingletonMap(KeyValue<K, V> keyValue)
  {
    key = keyValue.getKey();
    value = keyValue.getValue();
  }
  





  public SingletonMap(Map.Entry<? extends K, ? extends V> mapEntry)
  {
    key = mapEntry.getKey();
    value = mapEntry.getValue();
  }
  







  public SingletonMap(Map<? extends K, ? extends V> map)
  {
    if (map.size() != 1) {
      throw new IllegalArgumentException("The map size must be 1");
    }
    Map.Entry<? extends K, ? extends V> entry = (Map.Entry)map.entrySet().iterator().next();
    key = entry.getKey();
    value = entry.getValue();
  }
  






  public K getKey()
  {
    return key;
  }
  




  public V getValue()
  {
    return value;
  }
  





  public V setValue(V value)
  {
    V old = this.value;
    this.value = value;
    return old;
  }
  






  public boolean isFull()
  {
    return true;
  }
  




  public int maxSize()
  {
    return 1;
  }
  







  public V get(Object key)
  {
    if (isEqualKey(key)) {
      return value;
    }
    return null;
  }
  




  public int size()
  {
    return 1;
  }
  




  public boolean isEmpty()
  {
    return false;
  }
  






  public boolean containsKey(Object key)
  {
    return isEqualKey(key);
  }
  





  public boolean containsValue(Object value)
  {
    return isEqualValue(value);
  }
  











  public V put(K key, V value)
  {
    if (isEqualKey(key)) {
      return setValue(value);
    }
    throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size singleton");
  }
  










  public void putAll(Map<? extends K, ? extends V> map)
  {
    switch (map.size()) {
    case 0: 
      return;
    
    case 1: 
      Map.Entry<? extends K, ? extends V> entry = (Map.Entry)map.entrySet().iterator().next();
      put(entry.getKey(), entry.getValue());
      return;
    }
    
    throw new IllegalArgumentException("The map size must be 0 or 1");
  }
  







  public V remove(Object key)
  {
    throw new UnsupportedOperationException();
  }
  


  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  







  public Set<Map.Entry<K, V>> entrySet()
  {
    Map.Entry<K, V> entry = new TiedMapEntry(this, getKey());
    return Collections.singleton(entry);
  }
  






  public Set<K> keySet()
  {
    return Collections.singleton(key);
  }
  






  public Collection<V> values()
  {
    return new SingletonValues(this);
  }
  


  public OrderedMapIterator<K, V> mapIterator()
  {
    return new SingletonMapIterator(this);
  }
  




  public K firstKey()
  {
    return getKey();
  }
  




  public K lastKey()
  {
    return getKey();
  }
  





  public K nextKey(K key)
  {
    return null;
  }
  





  public K previousKey(K key)
  {
    return null;
  }
  






  protected boolean isEqualKey(Object key)
  {
    return key == null ? false : getKey() == null ? true : key.equals(getKey());
  }
  





  protected boolean isEqualValue(Object value)
  {
    return value == null ? false : getValue() == null ? true : value.equals(getValue());
  }
  

  static class SingletonMapIterator<K, V>
    implements OrderedMapIterator<K, V>, ResettableIterator<K>
  {
    private final SingletonMap<K, V> parent;
    
    private boolean hasNext = true;
    private boolean canGetSet = false;
    
    SingletonMapIterator(SingletonMap<K, V> parent)
    {
      this.parent = parent;
    }
    
    public boolean hasNext() {
      return hasNext;
    }
    
    public K next() {
      if (!hasNext) {
        throw new NoSuchElementException("No next() entry in the iteration");
      }
      hasNext = false;
      canGetSet = true;
      return parent.getKey();
    }
    
    public boolean hasPrevious() {
      return !hasNext;
    }
    
    public K previous() {
      if (hasNext == true) {
        throw new NoSuchElementException("No previous() entry in the iteration");
      }
      hasNext = true;
      return parent.getKey();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
    
    public K getKey() {
      if (!canGetSet) {
        throw new IllegalStateException("getKey() can only be called after next() and before remove()");
      }
      return parent.getKey();
    }
    
    public V getValue() {
      if (!canGetSet) {
        throw new IllegalStateException("getValue() can only be called after next() and before remove()");
      }
      return parent.getValue();
    }
    
    public V setValue(V value) {
      if (!canGetSet) {
        throw new IllegalStateException("setValue() can only be called after next() and before remove()");
      }
      return parent.setValue(value);
    }
    
    public void reset() {
      hasNext = true;
    }
    
    public String toString()
    {
      if (hasNext) {
        return "Iterator[]";
      }
      return "Iterator[" + getKey() + "=" + getValue() + "]";
    }
  }
  

  static class SingletonValues<V>
    extends AbstractSet<V>
    implements Serializable
  {
    private static final long serialVersionUID = -3689524741863047872L;
    private final SingletonMap<?, V> parent;
    
    SingletonValues(SingletonMap<?, V> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return 1;
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public boolean contains(Object object) {
      return parent.containsValue(object);
    }
    
    public void clear() {
      throw new UnsupportedOperationException();
    }
    
    public Iterator<V> iterator() {
      return new SingletonIterator(parent.getValue(), false);
    }
  }
  






  public SingletonMap<K, V> clone()
  {
    try
    {
      return (SingletonMap)super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new InternalError();
    }
  }
  






  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Map)) {
      return false;
    }
    Map<?, ?> other = (Map)obj;
    if (other.size() != 1) {
      return false;
    }
    Map.Entry<?, ?> entry = (Map.Entry)other.entrySet().iterator().next();
    return (isEqualKey(entry.getKey())) && (isEqualValue(entry.getValue()));
  }
  





  public int hashCode()
  {
    return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
  }
  






  public String toString()
  {
    return 128 + '{' + (getKey() == this ? "(this Map)" : getKey()) + '=' + (getValue() == this ? "(this Map)" : getValue()) + '}';
  }
}
