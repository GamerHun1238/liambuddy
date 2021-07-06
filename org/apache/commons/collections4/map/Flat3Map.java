package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;












































































public class Flat3Map<K, V>
  implements IterableMap<K, V>, Serializable, Cloneable
{
  private static final long serialVersionUID = -6701087419741928296L;
  private transient int size;
  private transient int hash1;
  private transient int hash2;
  private transient int hash3;
  private transient K key1;
  private transient K key2;
  private transient K key3;
  private transient V value1;
  private transient V value2;
  private transient V value3;
  private transient AbstractHashedMap<K, V> delegateMap;
  
  public Flat3Map() {}
  
  public Flat3Map(Map<? extends K, ? extends V> map)
  {
    putAll(map);
  }
  






  public V get(Object key)
  {
    if (delegateMap != null) {
      return delegateMap.get(key);
    }
    if (key == null) {
      switch (size)
      {
      case 3: 
        if (key3 == null) {
          return value3;
        }
      case 2: 
        if (key2 == null) {
          return value2;
        }
      case 1: 
        if (key1 == null) {
          return value1;
        }
        break;
      }
    } else if (size > 0) {
      int hashCode = key.hashCode();
      switch (size)
      {
      case 3: 
        if ((hash3 == hashCode) && (key.equals(key3))) {
          return value3;
        }
      case 2: 
        if ((hash2 == hashCode) && (key.equals(key2))) {
          return value2;
        }
      case 1: 
        if ((hash1 == hashCode) && (key.equals(key1))) {
          return value1;
        }
        break;
      }
    }
    return null;
  }
  




  public int size()
  {
    if (delegateMap != null) {
      return delegateMap.size();
    }
    return size;
  }
  




  public boolean isEmpty()
  {
    return size() == 0;
  }
  






  public boolean containsKey(Object key)
  {
    if (delegateMap != null) {
      return delegateMap.containsKey(key);
    }
    if (key == null) {
      switch (size) {
      case 3: 
        if (key3 == null) {
          return true;
        }
      case 2: 
        if (key2 == null) {
          return true;
        }
      case 1: 
        if (key1 == null) {
          return true;
        }
        break;
      }
    } else if (size > 0) {
      int hashCode = key.hashCode();
      switch (size) {
      case 3: 
        if ((hash3 == hashCode) && (key.equals(key3))) {
          return true;
        }
      case 2: 
        if ((hash2 == hashCode) && (key.equals(key2))) {
          return true;
        }
      case 1: 
        if ((hash1 == hashCode) && (key.equals(key1))) {
          return true;
        }
        break;
      }
    }
    return false;
  }
  





  public boolean containsValue(Object value)
  {
    if (delegateMap != null) {
      return delegateMap.containsValue(value);
    }
    if (value == null) {
      switch (size) {
      case 3: 
        if (value3 == null) {
          return true;
        }
      case 2: 
        if (value2 == null) {
          return true;
        }
      case 1: 
        if (value1 == null)
          return true;
        break;
      }
    } else {
      switch (size) {
      case 3: 
        if (value.equals(value3)) {
          return true;
        }
      case 2: 
        if (value.equals(value2)) {
          return true;
        }
      case 1: 
        if (value.equals(value1))
          return true;
        break;
      }
    }
    return false;
  }
  







  public V put(K key, V value)
  {
    if (delegateMap != null) {
      return delegateMap.put(key, value);
    }
    
    if (key == null) {
      switch (size) {
      case 3: 
        if (key3 == null) {
          V old = value3;
          value3 = value;
          return old;
        }
      case 2: 
        if (key2 == null) {
          V old = value2;
          value2 = value;
          return old;
        }
      case 1: 
        if (key1 == null) {
          V old = value1;
          value1 = value;
          return old;
        }
        break;
      }
    } else if (size > 0) {
      int hashCode = key.hashCode();
      switch (size) {
      case 3: 
        if ((hash3 == hashCode) && (key.equals(key3))) {
          V old = value3;
          value3 = value;
          return old;
        }
      case 2: 
        if ((hash2 == hashCode) && (key.equals(key2))) {
          V old = value2;
          value2 = value;
          return old;
        }
      case 1: 
        if ((hash1 == hashCode) && (key.equals(key1))) {
          V old = value1;
          value1 = value;
          return old;
        }
        
        break;
      }
      
    }
    switch (size) {
    default: 
      convertToMap();
      delegateMap.put(key, value);
      return null;
    case 2: 
      hash3 = (key == null ? 0 : key.hashCode());
      key3 = key;
      value3 = value;
      break;
    case 1: 
      hash2 = (key == null ? 0 : key.hashCode());
      key2 = key;
      value2 = value;
      break;
    case 0: 
      hash1 = (key == null ? 0 : key.hashCode());
      key1 = key;
      value1 = value;
    }
    
    size += 1;
    return null;
  }
  





  public void putAll(Map<? extends K, ? extends V> map)
  {
    int size = map.size();
    if (size == 0) {
      return;
    }
    if (delegateMap != null) {
      delegateMap.putAll(map);
      return;
    }
    if (size < 4) {
      for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
        put(entry.getKey(), entry.getValue());
      }
    } else {
      convertToMap();
      delegateMap.putAll(map);
    }
  }
  


  private void convertToMap()
  {
    delegateMap = createDelegateMap();
    switch (size) {
    case 3: 
      delegateMap.put(key3, value3);
    case 2: 
      delegateMap.put(key2, value2);
    case 1: 
      delegateMap.put(key1, value1);
    case 0: 
      break;
    default: 
      throw new IllegalStateException("Invalid map index: " + size);
    }
    
    size = 0;
    hash1 = (this.hash2 = this.hash3 = 0);
    key1 = (this.key2 = this.key3 = null);
    value1 = (this.value2 = this.value3 = null);
  }
  









  protected AbstractHashedMap<K, V> createDelegateMap()
  {
    return new HashedMap();
  }
  





  public V remove(Object key)
  {
    if (delegateMap != null) {
      return delegateMap.remove(key);
    }
    if (size == 0) {
      return null;
    }
    if (key == null) {
      switch (size) {
      case 3: 
        if (key3 == null) {
          V old = value3;
          hash3 = 0;
          key3 = null;
          value3 = null;
          size = 2;
          return old;
        }
        if (key2 == null) {
          V old = value2;
          hash2 = hash3;
          key2 = key3;
          value2 = value3;
          hash3 = 0;
          key3 = null;
          value3 = null;
          size = 2;
          return old;
        }
        if (key1 == null) {
          V old = value1;
          hash1 = hash3;
          key1 = key3;
          value1 = value3;
          hash3 = 0;
          key3 = null;
          value3 = null;
          size = 2;
          return old;
        }
        return null;
      case 2: 
        if (key2 == null) {
          V old = value2;
          hash2 = 0;
          key2 = null;
          value2 = null;
          size = 1;
          return old;
        }
        if (key1 == null) {
          V old = value1;
          hash1 = hash2;
          key1 = key2;
          value1 = value2;
          hash2 = 0;
          key2 = null;
          value2 = null;
          size = 1;
          return old;
        }
        return null;
      case 1: 
        if (key1 == null) {
          V old = value1;
          hash1 = 0;
          key1 = null;
          value1 = null;
          size = 0;
          return old;
        }
        break;
      }
    } else if (size > 0) {
      int hashCode = key.hashCode();
      switch (size) {
      case 3: 
        if ((hash3 == hashCode) && (key.equals(key3))) {
          V old = value3;
          hash3 = 0;
          key3 = null;
          value3 = null;
          size = 2;
          return old;
        }
        if ((hash2 == hashCode) && (key.equals(key2))) {
          V old = value2;
          hash2 = hash3;
          key2 = key3;
          value2 = value3;
          hash3 = 0;
          key3 = null;
          value3 = null;
          size = 2;
          return old;
        }
        if ((hash1 == hashCode) && (key.equals(key1))) {
          V old = value1;
          hash1 = hash3;
          key1 = key3;
          value1 = value3;
          hash3 = 0;
          key3 = null;
          value3 = null;
          size = 2;
          return old;
        }
        return null;
      case 2: 
        if ((hash2 == hashCode) && (key.equals(key2))) {
          V old = value2;
          hash2 = 0;
          key2 = null;
          value2 = null;
          size = 1;
          return old;
        }
        if ((hash1 == hashCode) && (key.equals(key1))) {
          V old = value1;
          hash1 = hash2;
          key1 = key2;
          value1 = value2;
          hash2 = 0;
          key2 = null;
          value2 = null;
          size = 1;
          return old;
        }
        return null;
      case 1: 
        if ((hash1 == hashCode) && (key.equals(key1))) {
          V old = value1;
          hash1 = 0;
          key1 = null;
          value1 = null;
          size = 0;
          return old;
        }
        break;
      }
    }
    return null;
  }
  



  public void clear()
  {
    if (delegateMap != null) {
      delegateMap.clear();
      delegateMap = null;
    } else {
      size = 0;
      hash1 = (this.hash2 = this.hash3 = 0);
      key1 = (this.key2 = this.key3 = null);
      value1 = (this.value2 = this.value3 = null);
    }
  }
  











  public MapIterator<K, V> mapIterator()
  {
    if (delegateMap != null) {
      return delegateMap.mapIterator();
    }
    if (size == 0) {
      return EmptyMapIterator.emptyMapIterator();
    }
    return new FlatMapIterator(this);
  }
  

  static class FlatMapIterator<K, V>
    implements MapIterator<K, V>, ResettableIterator<K>
  {
    private final Flat3Map<K, V> parent;
    private int nextIndex = 0;
    private boolean canRemove = false;
    
    FlatMapIterator(Flat3Map<K, V> parent)
    {
      this.parent = parent;
    }
    
    public boolean hasNext() {
      return nextIndex < parent.size;
    }
    
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No next() entry in the iteration");
      }
      canRemove = true;
      nextIndex += 1;
      return getKey();
    }
    
    public void remove() {
      if (!canRemove) {
        throw new IllegalStateException("remove() can only be called once after next()");
      }
      parent.remove(getKey());
      nextIndex -= 1;
      canRemove = false;
    }
    
    public K getKey() {
      if (!canRemove) {
        throw new IllegalStateException("getKey() can only be called after next() and before remove()");
      }
      switch (nextIndex) {
      case 3: 
        return parent.key3;
      case 2: 
        return parent.key2;
      case 1: 
        return parent.key1;
      }
      throw new IllegalStateException("Invalid map index: " + nextIndex);
    }
    
    public V getValue() {
      if (!canRemove) {
        throw new IllegalStateException("getValue() can only be called after next() and before remove()");
      }
      switch (nextIndex) {
      case 3: 
        return parent.value3;
      case 2: 
        return parent.value2;
      case 1: 
        return parent.value1;
      }
      throw new IllegalStateException("Invalid map index: " + nextIndex);
    }
    
    public V setValue(V value) {
      if (!canRemove) {
        throw new IllegalStateException("setValue() can only be called after next() and before remove()");
      }
      V old = getValue();
      switch (nextIndex) {
      case 3: 
        parent.value3 = value;
        break;
      case 2: 
        parent.value2 = value;
        break;
      case 1: 
        parent.value1 = value;
        break;
      default: 
        throw new IllegalStateException("Invalid map index: " + nextIndex);
      }
      return old;
    }
    
    public void reset() {
      nextIndex = 0;
      canRemove = false;
    }
    
    public String toString()
    {
      if (canRemove) {
        return "Iterator[" + getKey() + "=" + getValue() + "]";
      }
      return "Iterator[]";
    }
  }
  









  public Set<Map.Entry<K, V>> entrySet()
  {
    if (delegateMap != null) {
      return delegateMap.entrySet();
    }
    return new EntrySet(this);
  }
  

  static class EntrySet<K, V>
    extends AbstractSet<Map.Entry<K, V>>
  {
    private final Flat3Map<K, V> parent;
    
    EntrySet(Flat3Map<K, V> parent)
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
    
    public boolean remove(Object obj)
    {
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<?, ?> entry = (Map.Entry)obj;
      Object key = entry.getKey();
      boolean result = parent.containsKey(key);
      parent.remove(key);
      return result;
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      if (parent.delegateMap != null) {
        return parent.delegateMap.entrySet().iterator();
      }
      if (parent.size() == 0) {
        return EmptyIterator.emptyIterator();
      }
      return new Flat3Map.EntrySetIterator(parent);
    }
  }
  
  static class FlatMapEntry<K, V> implements Map.Entry<K, V> {
    private final Flat3Map<K, V> parent;
    private final int index;
    private volatile boolean removed;
    
    public FlatMapEntry(Flat3Map<K, V> parent, int index) {
      this.parent = parent;
      this.index = index;
      removed = false;
    }
    








    void setRemoved(boolean flag)
    {
      removed = flag;
    }
    
    public K getKey() {
      if (removed) {
        throw new IllegalStateException("getKey() can only be called after next() and before remove()");
      }
      switch (index) {
      case 3: 
        return parent.key3;
      case 2: 
        return parent.key2;
      case 1: 
        return parent.key1;
      }
      throw new IllegalStateException("Invalid map index: " + index);
    }
    
    public V getValue() {
      if (removed) {
        throw new IllegalStateException("getValue() can only be called after next() and before remove()");
      }
      switch (index) {
      case 3: 
        return parent.value3;
      case 2: 
        return parent.value2;
      case 1: 
        return parent.value1;
      }
      throw new IllegalStateException("Invalid map index: " + index);
    }
    
    public V setValue(V value) {
      if (removed) {
        throw new IllegalStateException("setValue() can only be called after next() and before remove()");
      }
      V old = getValue();
      switch (index) {
      case 3: 
        parent.value3 = value;
        break;
      case 2: 
        parent.value2 = value;
        break;
      case 1: 
        parent.value1 = value;
        break;
      default: 
        throw new IllegalStateException("Invalid map index: " + index);
      }
      return old;
    }
    
    public boolean equals(Object obj)
    {
      if (removed) {
        return false;
      }
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<?, ?> other = (Map.Entry)obj;
      Object key = getKey();
      Object value = getValue();
      return (key == null ? other.getKey() == null : key.equals(other.getKey())) && (value == null ? other.getValue() == null : value.equals(other.getValue()));
    }
    

    public int hashCode()
    {
      if (removed) {
        return 0;
      }
      Object key = getKey();
      Object value = getValue();
      return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }
    

    public String toString()
    {
      if (!removed) {
        return getKey() + "=" + getValue();
      }
      return "";
    }
  }
  
  static abstract class EntryIterator<K, V>
  {
    private final Flat3Map<K, V> parent;
    private int nextIndex = 0;
    private Flat3Map.FlatMapEntry<K, V> currentEntry = null;
    


    public EntryIterator(Flat3Map<K, V> parent)
    {
      this.parent = parent;
    }
    
    public boolean hasNext() {
      return nextIndex < parent.size;
    }
    
    public Map.Entry<K, V> nextEntry() {
      if (!hasNext()) {
        throw new NoSuchElementException("No next() entry in the iteration");
      }
      currentEntry = new Flat3Map.FlatMapEntry(parent, ++nextIndex);
      return currentEntry;
    }
    
    public void remove() {
      if (currentEntry == null) {
        throw new IllegalStateException("remove() can only be called once after next()");
      }
      currentEntry.setRemoved(true);
      parent.remove(currentEntry.getKey());
      nextIndex -= 1;
      currentEntry = null;
    }
  }
  
  static class EntrySetIterator<K, V>
    extends Flat3Map.EntryIterator<K, V>
    implements Iterator<Map.Entry<K, V>>
  {
    EntrySetIterator(Flat3Map<K, V> parent)
    {
      super();
    }
    
    public Map.Entry<K, V> next() {
      return nextEntry();
    }
  }
  






  public Set<K> keySet()
  {
    if (delegateMap != null) {
      return delegateMap.keySet();
    }
    return new KeySet(this);
  }
  

  static class KeySet<K>
    extends AbstractSet<K>
  {
    private final Flat3Map<K, ?> parent;
    
    KeySet(Flat3Map<K, ?> parent)
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
      if (parent.delegateMap != null) {
        return parent.delegateMap.keySet().iterator();
      }
      if (parent.size() == 0) {
        return EmptyIterator.emptyIterator();
      }
      return new Flat3Map.KeySetIterator(parent);
    }
  }
  

  static class KeySetIterator<K>
    extends Flat3Map.EntryIterator<K, Object>
    implements Iterator<K>
  {
    KeySetIterator(Flat3Map<K, ?> parent)
    {
      super();
    }
    
    public K next() {
      return nextEntry().getKey();
    }
  }
  






  public Collection<V> values()
  {
    if (delegateMap != null) {
      return delegateMap.values();
    }
    return new Values(this);
  }
  

  static class Values<V>
    extends AbstractCollection<V>
  {
    private final Flat3Map<?, V> parent;
    
    Values(Flat3Map<?, V> parent)
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
      if (parent.delegateMap != null) {
        return parent.delegateMap.values().iterator();
      }
      if (parent.size() == 0) {
        return EmptyIterator.emptyIterator();
      }
      return new Flat3Map.ValuesIterator(parent);
    }
  }
  

  static class ValuesIterator<V>
    extends Flat3Map.EntryIterator<Object, V>
    implements Iterator<V>
  {
    ValuesIterator(Flat3Map<?, V> parent)
    {
      super();
    }
    
    public V next() {
      return nextEntry().getValue();
    }
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeInt(size());
    for (MapIterator<?, ?> it = mapIterator(); it.hasNext();) {
      out.writeObject(it.next());
      out.writeObject(it.getValue());
    }
  }
  


  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    int count = in.readInt();
    if (count > 3) {
      delegateMap = createDelegateMap();
    }
    for (int i = count; i > 0; i--) {
      put(in.readObject(), in.readObject());
    }
  }
  







  public Flat3Map<K, V> clone()
  {
    try
    {
      Flat3Map<K, V> cloned = (Flat3Map)super.clone();
      if (delegateMap != null) {
        delegateMap = delegateMap.clone();
      }
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
    if (delegateMap != null) {
      return delegateMap.equals(obj);
    }
    if (!(obj instanceof Map)) {
      return false;
    }
    Map<?, ?> other = (Map)obj;
    if (size != other.size()) {
      return false;
    }
    if (size > 0) {
      Object otherValue = null;
      switch (size) {
      case 3: 
        if (!other.containsKey(key3)) {
          return false;
        }
        otherValue = other.get(key3);
        if (value3 == null ? otherValue != null : !value3.equals(otherValue)) {
          return false;
        }
      case 2: 
        if (!other.containsKey(key2)) {
          return false;
        }
        otherValue = other.get(key2);
        if (value2 == null ? otherValue != null : !value2.equals(otherValue)) {
          return false;
        }
      case 1: 
        if (!other.containsKey(key1)) {
          return false;
        }
        otherValue = other.get(key1);
        if (value1 == null ? otherValue != null : !value1.equals(otherValue))
          return false;
        break;
      }
    }
    return true;
  }
  





  public int hashCode()
  {
    if (delegateMap != null) {
      return delegateMap.hashCode();
    }
    int total = 0;
    switch (size) {
    case 3: 
      total += (hash3 ^ (value3 == null ? 0 : value3.hashCode()));
    case 2: 
      total += (hash2 ^ (value2 == null ? 0 : value2.hashCode()));
    case 1: 
      total += (hash1 ^ (value1 == null ? 0 : value1.hashCode()));
    case 0: 
      break;
    default: 
      throw new IllegalStateException("Invalid map index: " + size);
    }
    return total;
  }
  





  public String toString()
  {
    if (delegateMap != null) {
      return delegateMap.toString();
    }
    if (size == 0) {
      return "{}";
    }
    StringBuilder buf = new StringBuilder(128);
    buf.append('{');
    switch (size) {
    case 3: 
      buf.append(key3 == this ? "(this Map)" : key3);
      buf.append('=');
      buf.append(value3 == this ? "(this Map)" : value3);
      buf.append(',');
    case 2: 
      buf.append(key2 == this ? "(this Map)" : key2);
      buf.append('=');
      buf.append(value2 == this ? "(this Map)" : value2);
      buf.append(',');
    case 1: 
      buf.append(key1 == this ? "(this Map)" : key1);
      buf.append('=');
      buf.append(value1 == this ? "(this Map)" : value1);
      break;
    
    default: 
      throw new IllegalStateException("Invalid map index: " + size);
    }
    buf.append('}');
    return buf.toString();
  }
}
