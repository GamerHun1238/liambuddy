package org.apache.commons.collections4.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;


























































public abstract class AbstractLinkedMap<K, V>
  extends AbstractHashedMap<K, V>
  implements OrderedMap<K, V>
{
  transient LinkEntry<K, V> header;
  
  protected AbstractLinkedMap() {}
  
  protected AbstractLinkedMap(int initialCapacity, float loadFactor, int threshold)
  {
    super(initialCapacity, loadFactor, threshold);
  }
  





  protected AbstractLinkedMap(int initialCapacity)
  {
    super(initialCapacity);
  }
  








  protected AbstractLinkedMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  





  protected AbstractLinkedMap(Map<? extends K, ? extends V> map)
  {
    super(map);
  }
  







  protected void init()
  {
    header = createEntry(null, -1, null, null);
    header.before = (header.after = header);
  }
  








  public boolean containsValue(Object value)
  {
    if (value == null) {
      for (LinkEntry<K, V> entry = header.after; entry != header; entry = after) {
        if (entry.getValue() == null) {
          return true;
        }
      }
    } else {
      for (LinkEntry<K, V> entry = header.after; entry != header; entry = after) {
        if (isEqualValue(value, entry.getValue())) {
          return true;
        }
      }
    }
    return false;
  }
  





  public void clear()
  {
    super.clear();
    header.before = (header.after = header);
  }
  





  public K firstKey()
  {
    if (size == 0) {
      throw new NoSuchElementException("Map is empty");
    }
    return header.after.getKey();
  }
  




  public K lastKey()
  {
    if (size == 0) {
      throw new NoSuchElementException("Map is empty");
    }
    return header.before.getKey();
  }
  





  public K nextKey(Object key)
  {
    LinkEntry<K, V> entry = getEntry(key);
    return (entry == null) || (after == header) ? null : after.getKey();
  }
  
  protected LinkEntry<K, V> getEntry(Object key)
  {
    return (LinkEntry)super.getEntry(key);
  }
  





  public K previousKey(Object key)
  {
    LinkEntry<K, V> entry = getEntry(key);
    return (entry == null) || (before == header) ? null : before.getKey();
  }
  







  protected LinkEntry<K, V> getEntry(int index)
  {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
    }
    if (index >= size) {
      throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + size);
    }
    LinkEntry<K, V> entry;
    if (index < size / 2)
    {
      LinkEntry<K, V> entry = header.after;
      for (int currentIndex = 0; currentIndex < index; currentIndex++) {
        entry = after;
      }
    }
    else {
      entry = header;
      for (int currentIndex = size; currentIndex > index; currentIndex--) {
        entry = before;
      }
    }
    return entry;
  }
  









  protected void addEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex)
  {
    LinkEntry<K, V> link = (LinkEntry)entry;
    after = header;
    before = header.before;
    header.before.after = link;
    header.before = link;
    data[hashIndex] = link;
  }
  











  protected LinkEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value)
  {
    return new LinkEntry(next, hashCode, convertKey(key), value);
  }
  










  protected void removeEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex, AbstractHashedMap.HashEntry<K, V> previous)
  {
    LinkEntry<K, V> link = (LinkEntry)entry;
    before.after = after;
    after.before = before;
    after = null;
    before = null;
    super.removeEntry(entry, hashIndex, previous);
  }
  









  protected LinkEntry<K, V> entryBefore(LinkEntry<K, V> entry)
  {
    return before;
  }
  








  protected LinkEntry<K, V> entryAfter(LinkEntry<K, V> entry)
  {
    return after;
  }
  




  public OrderedMapIterator<K, V> mapIterator()
  {
    if (size == 0) {
      return EmptyOrderedMapIterator.emptyOrderedMapIterator();
    }
    return new LinkMapIterator(this);
  }
  

  protected static class LinkMapIterator<K, V>
    extends AbstractLinkedMap.LinkIterator<K, V>
    implements OrderedMapIterator<K, V>, ResettableIterator<K>
  {
    protected LinkMapIterator(AbstractLinkedMap<K, V> parent)
    {
      super();
    }
    
    public K next() {
      return super.nextEntry().getKey();
    }
    
    public K previous() {
      return super.previousEntry().getKey();
    }
    
    public K getKey() {
      AbstractLinkedMap.LinkEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("getKey() can only be called after next() and before remove()");
      }
      return current.getKey();
    }
    
    public V getValue() {
      AbstractLinkedMap.LinkEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("getValue() can only be called after next() and before remove()");
      }
      return current.getValue();
    }
    
    public V setValue(V value) {
      AbstractLinkedMap.LinkEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("setValue() can only be called after next() and before remove()");
      }
      return current.setValue(value);
    }
  }
  







  protected Iterator<Map.Entry<K, V>> createEntrySetIterator()
  {
    if (size() == 0) {
      return EmptyOrderedIterator.emptyOrderedIterator();
    }
    return new EntrySetIterator(this);
  }
  

  protected static class EntrySetIterator<K, V>
    extends AbstractLinkedMap.LinkIterator<K, V>
    implements OrderedIterator<Map.Entry<K, V>>, ResettableIterator<Map.Entry<K, V>>
  {
    protected EntrySetIterator(AbstractLinkedMap<K, V> parent)
    {
      super();
    }
    
    public Map.Entry<K, V> next() {
      return super.nextEntry();
    }
    
    public Map.Entry<K, V> previous() {
      return super.previousEntry();
    }
  }
  







  protected Iterator<K> createKeySetIterator()
  {
    if (size() == 0) {
      return EmptyOrderedIterator.emptyOrderedIterator();
    }
    return new KeySetIterator(this);
  }
  


  protected static class KeySetIterator<K>
    extends AbstractLinkedMap.LinkIterator<K, Object>
    implements OrderedIterator<K>, ResettableIterator<K>
  {
    protected KeySetIterator(AbstractLinkedMap<K, ?> parent)
    {
      super();
    }
    
    public K next() {
      return super.nextEntry().getKey();
    }
    
    public K previous() {
      return super.previousEntry().getKey();
    }
  }
  







  protected Iterator<V> createValuesIterator()
  {
    if (size() == 0) {
      return EmptyOrderedIterator.emptyOrderedIterator();
    }
    return new ValuesIterator(this);
  }
  


  protected static class ValuesIterator<V>
    extends AbstractLinkedMap.LinkIterator<Object, V>
    implements OrderedIterator<V>, ResettableIterator<V>
  {
    protected ValuesIterator(AbstractLinkedMap<?, V> parent)
    {
      super();
    }
    
    public V next() {
      return super.nextEntry().getValue();
    }
    
    public V previous() {
      return super.previousEntry().getValue();
    }
  }
  






  protected static class LinkEntry<K, V>
    extends AbstractHashedMap.HashEntry<K, V>
  {
    protected LinkEntry<K, V> before;
    




    protected LinkEntry<K, V> after;
    





    protected LinkEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, Object key, V value)
    {
      super(hashCode, key, value);
    }
  }
  


  protected static abstract class LinkIterator<K, V>
  {
    protected final AbstractLinkedMap<K, V> parent;
    

    protected AbstractLinkedMap.LinkEntry<K, V> last;
    
    protected AbstractLinkedMap.LinkEntry<K, V> next;
    
    protected int expectedModCount;
    

    protected LinkIterator(AbstractLinkedMap<K, V> parent)
    {
      this.parent = parent;
      next = header.after;
      expectedModCount = modCount;
    }
    
    public boolean hasNext() {
      return next != parent.header;
    }
    
    public boolean hasPrevious() {
      return next.before != parent.header;
    }
    
    protected AbstractLinkedMap.LinkEntry<K, V> nextEntry() {
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if (next == parent.header) {
        throw new NoSuchElementException("No next() entry in the iteration");
      }
      last = next;
      next = next.after;
      return last;
    }
    
    protected AbstractLinkedMap.LinkEntry<K, V> previousEntry() {
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      AbstractLinkedMap.LinkEntry<K, V> previous = next.before;
      if (previous == parent.header) {
        throw new NoSuchElementException("No previous() entry in the iteration");
      }
      next = previous;
      last = previous;
      return last;
    }
    
    protected AbstractLinkedMap.LinkEntry<K, V> currentEntry() {
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
    
    public void reset() {
      last = null;
      next = parent.header.after;
    }
    
    public String toString()
    {
      if (last != null) {
        return "Iterator[" + last.getKey() + "=" + last.getValue() + "]";
      }
      return "Iterator[]";
    }
  }
}
