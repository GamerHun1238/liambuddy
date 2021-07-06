package org.apache.commons.collections4.keyvalue;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.KeyValue;




































public class TiedMapEntry<K, V>
  implements Map.Entry<K, V>, KeyValue<K, V>, Serializable
{
  private static final long serialVersionUID = -8453869361373831205L;
  private final Map<K, V> map;
  private final K key;
  
  public TiedMapEntry(Map<K, V> map, K key)
  {
    this.map = map;
    this.key = key;
  }
  






  public K getKey()
  {
    return key;
  }
  




  public V getValue()
  {
    return map.get(key);
  }
  






  public V setValue(V value)
  {
    if (value == this) {
      throw new IllegalArgumentException("Cannot set value to this map entry");
    }
    return map.put(key, value);
  }
  








  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Map.Entry)) {
      return false;
    }
    Map.Entry<?, ?> other = (Map.Entry)obj;
    Object value = getValue();
    return (key == null ? other.getKey() == null : key.equals(other.getKey())) && (value == null ? other.getValue() == null : value.equals(other.getValue()));
  }
  









  public int hashCode()
  {
    Object value = getValue();
    return (getKey() == null ? 0 : getKey().hashCode()) ^ (value == null ? 0 : value.hashCode());
  }
  






  public String toString()
  {
    return getKey() + "=" + getValue();
  }
}
