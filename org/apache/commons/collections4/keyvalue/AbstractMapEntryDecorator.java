package org.apache.commons.collections4.keyvalue;

import java.util.Map.Entry;
import org.apache.commons.collections4.KeyValue;






























public abstract class AbstractMapEntryDecorator<K, V>
  implements Map.Entry<K, V>, KeyValue<K, V>
{
  private final Map.Entry<K, V> entry;
  
  public AbstractMapEntryDecorator(Map.Entry<K, V> entry)
  {
    if (entry == null) {
      throw new NullPointerException("Map Entry must not be null.");
    }
    this.entry = entry;
  }
  




  protected Map.Entry<K, V> getMapEntry()
  {
    return entry;
  }
  

  public K getKey()
  {
    return entry.getKey();
  }
  
  public V getValue() {
    return entry.getValue();
  }
  
  public V setValue(V object) {
    return entry.setValue(object);
  }
  
  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    return entry.equals(object);
  }
  
  public int hashCode()
  {
    return entry.hashCode();
  }
  
  public String toString()
  {
    return entry.toString();
  }
}
