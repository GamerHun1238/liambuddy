package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;






























public class EntrySetMapIterator<K, V>
  implements MapIterator<K, V>, ResettableIterator<K>
{
  private final Map<K, V> map;
  private Iterator<Map.Entry<K, V>> iterator;
  private Map.Entry<K, V> last;
  private boolean canRemove = false;
  





  public EntrySetMapIterator(Map<K, V> map)
  {
    this.map = map;
    iterator = map.entrySet().iterator();
  }
  





  public boolean hasNext()
  {
    return iterator.hasNext();
  }
  





  public K next()
  {
    last = ((Map.Entry)iterator.next());
    canRemove = true;
    return last.getKey();
  }
  










  public void remove()
  {
    if (!canRemove) {
      throw new IllegalStateException("Iterator remove() can only be called once after next()");
    }
    iterator.remove();
    last = null;
    canRemove = false;
  }
  







  public K getKey()
  {
    if (last == null) {
      throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
    }
    return last.getKey();
  }
  






  public V getValue()
  {
    if (last == null) {
      throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
    }
    return last.getValue();
  }
  









  public V setValue(V value)
  {
    if (last == null) {
      throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
    }
    return last.setValue(value);
  }
  



  public void reset()
  {
    iterator = map.entrySet().iterator();
    last = null;
    canRemove = false;
  }
  





  public String toString()
  {
    if (last != null) {
      return "MapIterator[" + getKey() + "=" + getValue() + "]";
    }
    return "MapIterator[]";
  }
}
