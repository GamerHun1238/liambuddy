package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;









































public class AbstractHashedMap<K, V>
  extends AbstractMap<K, V>
  implements IterableMap<K, V>
{
  protected static final String NO_NEXT_ENTRY = "No next() entry in the iteration";
  protected static final String NO_PREVIOUS_ENTRY = "No previous() entry in the iteration";
  protected static final String REMOVE_INVALID = "remove() can only be called once after next()";
  protected static final String GETKEY_INVALID = "getKey() can only be called after next() and before remove()";
  protected static final String GETVALUE_INVALID = "getValue() can only be called after next() and before remove()";
  protected static final String SETVALUE_INVALID = "setValue() can only be called after next() and before remove()";
  protected static final int DEFAULT_CAPACITY = 16;
  protected static final int DEFAULT_THRESHOLD = 12;
  protected static final float DEFAULT_LOAD_FACTOR = 0.75F;
  protected static final int MAXIMUM_CAPACITY = 1073741824;
  protected static final Object NULL = new Object();
  


  transient float loadFactor;
  


  transient int size;
  

  transient HashEntry<K, V>[] data;
  

  transient int threshold;
  

  transient int modCount;
  

  transient EntrySet<K, V> entrySet;
  

  transient KeySet<K> keySet;
  

  transient Values<V> values;
  


  protected AbstractHashedMap() {}
  


  protected AbstractHashedMap(int initialCapacity, float loadFactor, int threshold)
  {
    this.loadFactor = loadFactor;
    data = new HashEntry[initialCapacity];
    this.threshold = threshold;
    init();
  }
  






  protected AbstractHashedMap(int initialCapacity)
  {
    this(initialCapacity, 0.75F);
  }
  










  protected AbstractHashedMap(int initialCapacity, float loadFactor)
  {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Initial capacity must be a non negative number");
    }
    if ((loadFactor <= 0.0F) || (Float.isNaN(loadFactor))) {
      throw new IllegalArgumentException("Load factor must be greater than 0");
    }
    this.loadFactor = loadFactor;
    initialCapacity = calculateNewCapacity(initialCapacity);
    threshold = calculateThreshold(initialCapacity, loadFactor);
    data = new HashEntry[initialCapacity];
    init();
  }
  





  protected AbstractHashedMap(Map<? extends K, ? extends V> map)
  {
    this(Math.max(2 * map.size(), 16), 0.75F);
    _putAll(map);
  }
  






  protected void init() {}
  





  public V get(Object key)
  {
    key = convertKey(key);
    int hashCode = hash(key);
    HashEntry<K, V> entry = data[hashIndex(hashCode, data.length)];
    while (entry != null) {
      if ((hashCode == hashCode) && (isEqualKey(key, key))) {
        return entry.getValue();
      }
      entry = next;
    }
    return null;
  }
  





  public int size()
  {
    return size;
  }
  





  public boolean isEmpty()
  {
    return size == 0;
  }
  







  public boolean containsKey(Object key)
  {
    key = convertKey(key);
    int hashCode = hash(key);
    HashEntry<K, V> entry = data[hashIndex(hashCode, data.length)];
    while (entry != null) {
      if ((hashCode == hashCode) && (isEqualKey(key, key))) {
        return true;
      }
      entry = next;
    }
    return false;
  }
  






  public boolean containsValue(Object value)
  {
    if (value == null) {
      for (HashEntry<K, V> element : data) {
        HashEntry<K, V> entry = element;
        while (entry != null) {
          if (entry.getValue() == null) {
            return true;
          }
          entry = next;
        }
      }
    } else {
      for (HashEntry<K, V> element : data) {
        HashEntry<K, V> entry = element;
        while (entry != null) {
          if (isEqualValue(value, entry.getValue())) {
            return true;
          }
          entry = next;
        }
      }
    }
    return false;
  }
  








  public V put(K key, V value)
  {
    Object convertedKey = convertKey(key);
    int hashCode = hash(convertedKey);
    int index = hashIndex(hashCode, data.length);
    HashEntry<K, V> entry = data[index];
    while (entry != null) {
      if ((hashCode == hashCode) && (isEqualKey(convertedKey, key))) {
        V oldValue = entry.getValue();
        updateEntry(entry, value);
        return oldValue;
      }
      entry = next;
    }
    
    addMapping(index, hashCode, key, value);
    return null;
  }
  









  public void putAll(Map<? extends K, ? extends V> map)
  {
    _putAll(map);
  }
  











  private void _putAll(Map<? extends K, ? extends V> map)
  {
    int mapSize = map.size();
    if (mapSize == 0) {
      return;
    }
    int newSize = (int)((size + mapSize) / loadFactor + 1.0F);
    ensureCapacity(calculateNewCapacity(newSize));
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }
  






  public V remove(Object key)
  {
    key = convertKey(key);
    int hashCode = hash(key);
    int index = hashIndex(hashCode, data.length);
    HashEntry<K, V> entry = data[index];
    HashEntry<K, V> previous = null;
    while (entry != null) {
      if ((hashCode == hashCode) && (isEqualKey(key, key))) {
        V oldValue = entry.getValue();
        removeMapping(entry, index, previous);
        return oldValue;
      }
      previous = entry;
      entry = next;
    }
    return null;
  }
  




  public void clear()
  {
    modCount += 1;
    HashEntry<K, V>[] data = this.data;
    for (int i = data.length - 1; i >= 0; i--) {
      data[i] = null;
    }
    size = 0;
  }
  











  protected Object convertKey(Object key)
  {
    return key == null ? NULL : key;
  }
  








  protected int hash(Object key)
  {
    int h = key.hashCode();
    h += (h << 9 ^ 0xFFFFFFFF);
    h ^= h >>> 14;
    h += (h << 4);
    h ^= h >>> 10;
    return h;
  }
  








  protected boolean isEqualKey(Object key1, Object key2)
  {
    return (key1 == key2) || (key1.equals(key2));
  }
  








  protected boolean isEqualValue(Object value1, Object value2)
  {
    return (value1 == value2) || (value1.equals(value2));
  }
  








  protected int hashIndex(int hashCode, int dataSize)
  {
    return hashCode & dataSize - 1;
  }
  










  protected HashEntry<K, V> getEntry(Object key)
  {
    key = convertKey(key);
    int hashCode = hash(key);
    HashEntry<K, V> entry = data[hashIndex(hashCode, data.length)];
    while (entry != null) {
      if ((hashCode == hashCode) && (isEqualKey(key, key))) {
        return entry;
      }
      entry = next;
    }
    return null;
  }
  









  protected void updateEntry(HashEntry<K, V> entry, V newValue)
  {
    entry.setValue(newValue);
  }
  












  protected void reuseEntry(HashEntry<K, V> entry, int hashIndex, int hashCode, K key, V value)
  {
    next = data[hashIndex];
    hashCode = hashCode;
    key = key;
    value = value;
  }
  













  protected void addMapping(int hashIndex, int hashCode, K key, V value)
  {
    modCount += 1;
    HashEntry<K, V> entry = createEntry(data[hashIndex], hashCode, key, value);
    addEntry(entry, hashIndex);
    size += 1;
    checkCapacity();
  }
  












  protected HashEntry<K, V> createEntry(HashEntry<K, V> next, int hashCode, K key, V value)
  {
    return new HashEntry(next, hashCode, convertKey(key), value);
  }
  








  protected void addEntry(HashEntry<K, V> entry, int hashIndex)
  {
    data[hashIndex] = entry;
  }
  











  protected void removeMapping(HashEntry<K, V> entry, int hashIndex, HashEntry<K, V> previous)
  {
    modCount += 1;
    removeEntry(entry, hashIndex, previous);
    size -= 1;
    destroyEntry(entry);
  }
  










  protected void removeEntry(HashEntry<K, V> entry, int hashIndex, HashEntry<K, V> previous)
  {
    if (previous == null) {
      data[hashIndex] = next;
    } else {
      next = next;
    }
  }
  







  protected void destroyEntry(HashEntry<K, V> entry)
  {
    next = null;
    key = null;
    value = null;
  }
  





  protected void checkCapacity()
  {
    if (size >= threshold) {
      int newCapacity = data.length * 2;
      if (newCapacity <= 1073741824) {
        ensureCapacity(newCapacity);
      }
    }
  }
  





  protected void ensureCapacity(int newCapacity)
  {
    int oldCapacity = data.length;
    if (newCapacity <= oldCapacity) {
      return;
    }
    if (size == 0) {
      threshold = calculateThreshold(newCapacity, loadFactor);
      data = new HashEntry[newCapacity];
    } else {
      HashEntry<K, V>[] oldEntries = data;
      HashEntry<K, V>[] newEntries = new HashEntry[newCapacity];
      
      modCount += 1;
      for (int i = oldCapacity - 1; i >= 0; i--) {
        HashEntry<K, V> entry = oldEntries[i];
        if (entry != null) {
          oldEntries[i] = null;
          do {
            HashEntry<K, V> next = next;
            int index = hashIndex(hashCode, newCapacity);
            next = newEntries[index];
            newEntries[index] = entry;
            entry = next;
          } while (entry != null);
        }
      }
      threshold = calculateThreshold(newCapacity, loadFactor);
      data = newEntries;
    }
  }
  






  protected int calculateNewCapacity(int proposedCapacity)
  {
    int newCapacity = 1;
    if (proposedCapacity > 1073741824) {
      newCapacity = 1073741824;
    } else {
      while (newCapacity < proposedCapacity) {
        newCapacity <<= 1;
      }
      if (newCapacity > 1073741824) {
        newCapacity = 1073741824;
      }
    }
    return newCapacity;
  }
  







  protected int calculateThreshold(int newCapacity, float factor)
  {
    return (int)(newCapacity * factor);
  }
  









  protected HashEntry<K, V> entryNext(HashEntry<K, V> entry)
  {
    return next;
  }
  








  protected int entryHashCode(HashEntry<K, V> entry)
  {
    return hashCode;
  }
  








  protected K entryKey(HashEntry<K, V> entry)
  {
    return entry.getKey();
  }
  








  protected V entryValue(HashEntry<K, V> entry)
  {
    return entry.getValue();
  }
  











  public MapIterator<K, V> mapIterator()
  {
    if (size == 0) {
      return EmptyMapIterator.emptyMapIterator();
    }
    return new HashMapIterator(this);
  }
  
  protected static class HashMapIterator<K, V>
    extends AbstractHashedMap.HashIterator<K, V>
    implements MapIterator<K, V>
  {
    protected HashMapIterator(AbstractHashedMap<K, V> parent)
    {
      super();
    }
    
    public K next() {
      return super.nextEntry().getKey();
    }
    
    public K getKey() {
      AbstractHashedMap.HashEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("getKey() can only be called after next() and before remove()");
      }
      return current.getKey();
    }
    
    public V getValue() {
      AbstractHashedMap.HashEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("getValue() can only be called after next() and before remove()");
      }
      return current.getValue();
    }
    
    public V setValue(V value) {
      AbstractHashedMap.HashEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("setValue() can only be called after next() and before remove()");
      }
      return current.setValue(value);
    }
  }
  








  public Set<Map.Entry<K, V>> entrySet()
  {
    if (entrySet == null) {
      entrySet = new EntrySet(this);
    }
    return entrySet;
  }
  





  protected Iterator<Map.Entry<K, V>> createEntrySetIterator()
  {
    if (size() == 0) {
      return EmptyIterator.emptyIterator();
    }
    return new EntrySetIterator(this);
  }
  

  protected static class EntrySet<K, V>
    extends AbstractSet<Map.Entry<K, V>>
  {
    private final AbstractHashedMap<K, V> parent;
    

    protected EntrySet(AbstractHashedMap<K, V> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return parent.size();
    }
    
    public void clear()
    {
      parent.clear();
    }
    
    public boolean contains(Object entry)
    {
      if ((entry instanceof Map.Entry)) {
        Map.Entry<?, ?> e = (Map.Entry)entry;
        Map.Entry<K, V> match = parent.getEntry(e.getKey());
        return (match != null) && (match.equals(e));
      }
      return false;
    }
    
    public boolean remove(Object obj)
    {
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      if (!contains(obj)) {
        return false;
      }
      Map.Entry<?, ?> entry = (Map.Entry)obj;
      parent.remove(entry.getKey());
      return true;
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return parent.createEntrySetIterator();
    }
  }
  
  protected static class EntrySetIterator<K, V>
    extends AbstractHashedMap.HashIterator<K, V>
    implements Iterator<Map.Entry<K, V>>
  {
    protected EntrySetIterator(AbstractHashedMap<K, V> parent)
    {
      super();
    }
    
    public Map.Entry<K, V> next() {
      return super.nextEntry();
    }
  }
  








  public Set<K> keySet()
  {
    if (keySet == null) {
      keySet = new KeySet(this);
    }
    return keySet;
  }
  





  protected Iterator<K> createKeySetIterator()
  {
    if (size() == 0) {
      return EmptyIterator.emptyIterator();
    }
    return new KeySetIterator(this);
  }
  

  protected static class KeySet<K>
    extends AbstractSet<K>
  {
    private final AbstractHashedMap<K, ?> parent;
    

    protected KeySet(AbstractHashedMap<K, ?> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return parent.size();
    }
    
    public void clear()
    {
      parent.clear();
    }
    
    public boolean contains(Object key)
    {
      return parent.containsKey(key);
    }
    
    public boolean remove(Object key)
    {
      boolean result = parent.containsKey(key);
      parent.remove(key);
      return result;
    }
    
    public Iterator<K> iterator()
    {
      return parent.createKeySetIterator();
    }
  }
  

  protected static class KeySetIterator<K>
    extends AbstractHashedMap.HashIterator<K, Object>
    implements Iterator<K>
  {
    protected KeySetIterator(AbstractHashedMap<K, ?> parent)
    {
      super();
    }
    
    public K next() {
      return super.nextEntry().getKey();
    }
  }
  








  public Collection<V> values()
  {
    if (values == null) {
      values = new Values(this);
    }
    return values;
  }
  





  protected Iterator<V> createValuesIterator()
  {
    if (size() == 0) {
      return EmptyIterator.emptyIterator();
    }
    return new ValuesIterator(this);
  }
  

  protected static class Values<V>
    extends AbstractCollection<V>
  {
    private final AbstractHashedMap<?, V> parent;
    

    protected Values(AbstractHashedMap<?, V> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return parent.size();
    }
    
    public void clear()
    {
      parent.clear();
    }
    
    public boolean contains(Object value)
    {
      return parent.containsValue(value);
    }
    
    public Iterator<V> iterator()
    {
      return parent.createValuesIterator();
    }
  }
  

  protected static class ValuesIterator<V>
    extends AbstractHashedMap.HashIterator<Object, V>
    implements Iterator<V>
  {
    protected ValuesIterator(AbstractHashedMap<?, V> parent)
    {
      super();
    }
    
    public V next() {
      return super.nextEntry().getValue();
    }
  }
  



  protected static class HashEntry<K, V>
    implements Map.Entry<K, V>, KeyValue<K, V>
  {
    protected HashEntry<K, V> next;
    

    protected int hashCode;
    

    protected Object key;
    

    protected Object value;
    


    protected HashEntry(HashEntry<K, V> next, int hashCode, Object key, V value)
    {
      this.next = next;
      this.hashCode = hashCode;
      this.key = key;
      this.value = value;
    }
    
    public K getKey()
    {
      if (key == AbstractHashedMap.NULL) {
        return null;
      }
      return key;
    }
    
    public V getValue()
    {
      return value;
    }
    
    public V setValue(V value)
    {
      Object old = this.value;
      this.value = value;
      return old;
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
      return (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) && (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
    }
    


    public int hashCode()
    {
      return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
    }
    

    public String toString()
    {
      return getKey() + '=' + getValue();
    }
  }
  


  protected static abstract class HashIterator<K, V>
  {
    private final AbstractHashedMap<K, V> parent;
    

    private int hashIndex;
    
    private AbstractHashedMap.HashEntry<K, V> last;
    
    private AbstractHashedMap.HashEntry<K, V> next;
    
    private int expectedModCount;
    

    protected HashIterator(AbstractHashedMap<K, V> parent)
    {
      this.parent = parent;
      AbstractHashedMap.HashEntry<K, V>[] data = data;
      int i = data.length;
      AbstractHashedMap.HashEntry<K, V> next = null;
      while ((i > 0) && (next == null)) {
        next = data[(--i)];
      }
      this.next = next;
      hashIndex = i;
      expectedModCount = modCount;
    }
    
    public boolean hasNext() {
      return next != null;
    }
    
    protected AbstractHashedMap.HashEntry<K, V> nextEntry() {
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      AbstractHashedMap.HashEntry<K, V> newCurrent = next;
      if (newCurrent == null) {
        throw new NoSuchElementException("No next() entry in the iteration");
      }
      AbstractHashedMap.HashEntry<K, V>[] data = parent.data;
      int i = hashIndex;
      AbstractHashedMap.HashEntry<K, V> n = next;
      while ((n == null) && (i > 0)) {
        n = data[(--i)];
      }
      next = n;
      hashIndex = i;
      last = newCurrent;
      return newCurrent;
    }
    
    protected AbstractHashedMap.HashEntry<K, V> currentEntry() {
      return last;
    }
    
    public void remove() {
      if (last == null) {
        throw new IllegalStateException("remove() can only be called once after next()");
      }
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      parent.remove(last.getKey());
      last = null;
      expectedModCount = parent.modCount;
    }
    
    public String toString()
    {
      if (last != null) {
        return "Iterator[" + last.getKey() + "=" + last.getValue() + "]";
      }
      return "Iterator[]";
    }
  }
  



















  protected void doWriteObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeFloat(loadFactor);
    out.writeInt(data.length);
    out.writeInt(size);
    for (MapIterator<K, V> it = mapIterator(); it.hasNext();) {
      out.writeObject(it.next());
      out.writeObject(it.getValue());
    }
  }
  



















  protected void doReadObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    loadFactor = in.readFloat();
    int capacity = in.readInt();
    int size = in.readInt();
    init();
    threshold = calculateThreshold(capacity, loadFactor);
    data = new HashEntry[capacity];
    for (int i = 0; i < size; i++) {
      K key = in.readObject();
      V value = in.readObject();
      put(key, value);
    }
  }
  










  protected AbstractHashedMap<K, V> clone()
  {
    try
    {
      AbstractHashedMap<K, V> cloned = (AbstractHashedMap)super.clone();
      data = new HashEntry[data.length];
      entrySet = null;
      keySet = null;
      values = null;
      modCount = 0;
      size = 0;
      cloned.init();
      cloned.putAll(this);
      return cloned;
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
    Map<?, ?> map = (Map)obj;
    if (map.size() != size()) {
      return false;
    }
    MapIterator<?, ?> it = mapIterator();
    try {
      while (it.hasNext()) {
        Object key = it.next();
        Object value = it.getValue();
        if (value == null) {
          if ((map.get(key) != null) || (!map.containsKey(key))) {
            return false;
          }
        }
        else if (!value.equals(map.get(key))) {
          return false;
        }
      }
    }
    catch (ClassCastException ignored) {
      return false;
    } catch (NullPointerException ignored) {
      return false;
    }
    return true;
  }
  





  public int hashCode()
  {
    int total = 0;
    Iterator<Map.Entry<K, V>> it = createEntrySetIterator();
    while (it.hasNext()) {
      total += ((Map.Entry)it.next()).hashCode();
    }
    return total;
  }
  





  public String toString()
  {
    if (size() == 0) {
      return "{}";
    }
    StringBuilder buf = new StringBuilder(32 * size());
    buf.append('{');
    
    MapIterator<K, V> it = mapIterator();
    boolean hasNext = it.hasNext();
    while (hasNext) {
      K key = it.next();
      V value = it.getValue();
      buf.append(key == this ? "(this Map)" : key).append('=').append(value == this ? "(this Map)" : value);
      


      hasNext = it.hasNext();
      if (hasNext) {
        buf.append(',').append(' ');
      }
    }
    
    buf.append('}');
    return buf.toString();
  }
}
