package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.list.UnmodifiableList;
























































public class ListOrderedMap<K, V>
  extends AbstractMapDecorator<K, V>
  implements OrderedMap<K, V>, Serializable
{
  private static final long serialVersionUID = 2728177751851003750L;
  private final List<K> insertOrder = new ArrayList();
  











  public static <K, V> ListOrderedMap<K, V> listOrderedMap(Map<K, V> map)
  {
    return new ListOrderedMap(map);
  }
  






  public ListOrderedMap()
  {
    this(new HashMap());
  }
  





  protected ListOrderedMap(Map<K, V> map)
  {
    super(map);
    insertOrder.addAll(decorated().keySet());
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
  


  public OrderedMapIterator<K, V> mapIterator()
  {
    return new ListOrderedMapIterator(this);
  }
  





  public K firstKey()
  {
    if (size() == 0) {
      throw new NoSuchElementException("Map is empty");
    }
    return insertOrder.get(0);
  }
  





  public K lastKey()
  {
    if (size() == 0) {
      throw new NoSuchElementException("Map is empty");
    }
    return insertOrder.get(size() - 1);
  }
  






  public K nextKey(Object key)
  {
    int index = insertOrder.indexOf(key);
    if ((index >= 0) && (index < size() - 1)) {
      return insertOrder.get(index + 1);
    }
    return null;
  }
  






  public K previousKey(Object key)
  {
    int index = insertOrder.indexOf(key);
    if (index > 0) {
      return insertOrder.get(index - 1);
    }
    return null;
  }
  

  public V put(K key, V value)
  {
    if (decorated().containsKey(key))
    {
      return decorated().put(key, value);
    }
    
    V result = decorated().put(key, value);
    insertOrder.add(key);
    return result;
  }
  
  public void putAll(Map<? extends K, ? extends V> map)
  {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }
  







  public void putAll(int index, Map<? extends K, ? extends V> map)
  {
    if ((index < 0) || (index > insertOrder.size())) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + insertOrder.size());
    }
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      K key = entry.getKey();
      boolean contains = containsKey(key);
      

      put(index, entry.getKey(), entry.getValue());
      if (!contains)
      {
        index++;
      }
      else {
        index = indexOf(entry.getKey()) + 1;
      }
    }
  }
  
  public V remove(Object key)
  {
    V result = null;
    if (decorated().containsKey(key)) {
      result = decorated().remove(key);
      insertOrder.remove(key);
    }
    return result;
  }
  
  public void clear()
  {
    decorated().clear();
    insertOrder.clear();
  }
  









  public Set<K> keySet()
  {
    return new KeySetView(this);
  }
  









  public List<K> keyList()
  {
    return UnmodifiableList.unmodifiableList(insertOrder);
  }
  











  public Collection<V> values()
  {
    return new ValuesView(this);
  }
  









  public List<V> valueList()
  {
    return new ValuesView(this);
  }
  







  public Set<Map.Entry<K, V>> entrySet()
  {
    return new EntrySetView(this, insertOrder);
  }
  






  public String toString()
  {
    if (isEmpty()) {
      return "{}";
    }
    StringBuilder buf = new StringBuilder();
    buf.append('{');
    boolean first = true;
    for (Map.Entry<K, V> entry : entrySet()) {
      K key = entry.getKey();
      V value = entry.getValue();
      if (first) {
        first = false;
      } else {
        buf.append(", ");
      }
      buf.append(key == this ? "(this Map)" : key);
      buf.append('=');
      buf.append(value == this ? "(this Map)" : value);
    }
    buf.append('}');
    return buf.toString();
  }
  







  public K get(int index)
  {
    return insertOrder.get(index);
  }
  






  public V getValue(int index)
  {
    return get(insertOrder.get(index));
  }
  





  public int indexOf(Object key)
  {
    return insertOrder.indexOf(key);
  }
  








  public V setValue(int index, V value)
  {
    K key = insertOrder.get(index);
    return put(key, value);
  }
  


















  public V put(int index, K key, V value)
  {
    if ((index < 0) || (index > insertOrder.size())) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + insertOrder.size());
    }
    
    Map<K, V> m = decorated();
    if (m.containsKey(key)) {
      V result = m.remove(key);
      int pos = insertOrder.indexOf(key);
      insertOrder.remove(pos);
      if (pos < index) {
        index--;
      }
      insertOrder.add(index, key);
      m.put(key, value);
      return result;
    }
    insertOrder.add(index, key);
    m.put(key, value);
    return null;
  }
  






  public V remove(int index)
  {
    return remove(get(index));
  }
  
















  public List<K> asList()
  {
    return keyList();
  }
  
  static class ValuesView<V>
    extends AbstractList<V>
  {
    private final ListOrderedMap<Object, V> parent;
    
    ValuesView(ListOrderedMap<?, V> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return parent.size();
    }
    
    public boolean contains(Object value)
    {
      return parent.containsValue(value);
    }
    
    public void clear()
    {
      parent.clear();
    }
    
    public Iterator<V> iterator()
    {
      new AbstractUntypedIteratorDecorator(parent.entrySet().iterator()) {
        public V next() {
          return ((Map.Entry)getIterator().next()).getValue();
        }
      };
    }
    
    public V get(int index)
    {
      return parent.getValue(index);
    }
    
    public V set(int index, V value)
    {
      return parent.setValue(index, value);
    }
    
    public V remove(int index)
    {
      return parent.remove(index);
    }
  }
  
  static class KeySetView<K>
    extends AbstractSet<K>
  {
    private final ListOrderedMap<K, Object> parent;
    
    KeySetView(ListOrderedMap<K, ?> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return parent.size();
    }
    
    public boolean contains(Object value)
    {
      return parent.containsKey(value);
    }
    
    public void clear()
    {
      parent.clear();
    }
    
    public Iterator<K> iterator()
    {
      new AbstractUntypedIteratorDecorator(parent.entrySet().iterator()) {
        public K next() {
          return ((Map.Entry)getIterator().next()).getKey();
        }
      };
    }
  }
  
  static class EntrySetView<K, V> extends AbstractSet<Map.Entry<K, V>>
  {
    private final ListOrderedMap<K, V> parent;
    private final List<K> insertOrder;
    private Set<Map.Entry<K, V>> entrySet;
    
    public EntrySetView(ListOrderedMap<K, V> parent, List<K> insertOrder)
    {
      this.parent = parent;
      this.insertOrder = insertOrder;
    }
    
    private Set<Map.Entry<K, V>> getEntrySet() {
      if (entrySet == null) {
        entrySet = parent.decorated().entrySet();
      }
      return entrySet;
    }
    
    public int size()
    {
      return parent.size();
    }
    
    public boolean isEmpty() {
      return parent.isEmpty();
    }
    
    public boolean contains(Object obj)
    {
      return getEntrySet().contains(obj);
    }
    
    public boolean containsAll(Collection<?> coll)
    {
      return getEntrySet().containsAll(coll);
    }
    

    public boolean remove(Object obj)
    {
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      if (getEntrySet().contains(obj)) {
        Object key = ((Map.Entry)obj).getKey();
        parent.remove(key);
        return true;
      }
      return false;
    }
    
    public void clear()
    {
      parent.clear();
    }
    
    public boolean equals(Object obj)
    {
      if (obj == this) {
        return true;
      }
      return getEntrySet().equals(obj);
    }
    
    public int hashCode()
    {
      return getEntrySet().hashCode();
    }
    
    public String toString()
    {
      return getEntrySet().toString();
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new ListOrderedMap.ListOrderedIterator(parent, insertOrder);
    }
  }
  
  static class ListOrderedIterator<K, V> extends AbstractUntypedIteratorDecorator<K, Map.Entry<K, V>>
  {
    private final ListOrderedMap<K, V> parent;
    private K last = null;
    
    ListOrderedIterator(ListOrderedMap<K, V> parent, List<K> insertOrder) {
      super();
      this.parent = parent;
    }
    
    public Map.Entry<K, V> next() {
      last = getIterator().next();
      return new ListOrderedMap.ListOrderedMapEntry(parent, last);
    }
    
    public void remove()
    {
      super.remove();
      parent.decorated().remove(last);
    }
  }
  
  static class ListOrderedMapEntry<K, V> extends AbstractMapEntry<K, V>
  {
    private final ListOrderedMap<K, V> parent;
    
    ListOrderedMapEntry(ListOrderedMap<K, V> parent, K key) {
      super(null);
      this.parent = parent;
    }
    
    public V getValue()
    {
      return parent.get(getKey());
    }
    
    public V setValue(V value)
    {
      return parent.decorated().put(getKey(), value);
    }
  }
  
  static class ListOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K>
  {
    private final ListOrderedMap<K, V> parent;
    private ListIterator<K> iterator;
    private K last = null;
    private boolean readable = false;
    
    ListOrderedMapIterator(ListOrderedMap<K, V> parent)
    {
      this.parent = parent;
      iterator = insertOrder.listIterator();
    }
    
    public boolean hasNext() {
      return iterator.hasNext();
    }
    
    public K next() {
      last = iterator.next();
      readable = true;
      return last;
    }
    
    public boolean hasPrevious() {
      return iterator.hasPrevious();
    }
    
    public K previous() {
      last = iterator.previous();
      readable = true;
      return last;
    }
    
    public void remove() {
      if (!readable) {
        throw new IllegalStateException("remove() can only be called once after next()");
      }
      iterator.remove();
      parent.map.remove(last);
      readable = false;
    }
    
    public K getKey() {
      if (!readable) {
        throw new IllegalStateException("getKey() can only be called after next() and before remove()");
      }
      return last;
    }
    
    public V getValue() {
      if (!readable) {
        throw new IllegalStateException("getValue() can only be called after next() and before remove()");
      }
      return parent.get(last);
    }
    
    public V setValue(V value) {
      if (!readable) {
        throw new IllegalStateException("setValue() can only be called after next() and before remove()");
      }
      return parent.map.put(last, value);
    }
    
    public void reset() {
      iterator = parent.insertOrder.listIterator();
      last = null;
      readable = false;
    }
    
    public String toString()
    {
      if (readable == true) {
        return "Iterator[" + getKey() + "=" + getValue() + "]";
      }
      return "Iterator[]";
    }
  }
}
