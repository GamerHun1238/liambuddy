package org.apache.commons.collections4.bidimap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;






































public abstract class AbstractDualBidiMap<K, V>
  implements BidiMap<K, V>
{
  transient Map<K, V> normalMap;
  transient Map<V, K> reverseMap;
  transient BidiMap<V, K> inverseBidiMap = null;
  



  transient Set<K> keySet = null;
  



  transient Set<V> values = null;
  



  transient Set<Map.Entry<K, V>> entrySet = null;
  












  protected AbstractDualBidiMap() {}
  











  protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap)
  {
    this.normalMap = normalMap;
    this.reverseMap = reverseMap;
  }
  









  protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap)
  {
    this.normalMap = normalMap;
    this.reverseMap = reverseMap;
    this.inverseBidiMap = inverseBidiMap;
  }
  






  protected abstract BidiMap<V, K> createBidiMap(Map<V, K> paramMap, Map<K, V> paramMap1, BidiMap<K, V> paramBidiMap);
  





  public V get(Object key)
  {
    return normalMap.get(key);
  }
  
  public int size()
  {
    return normalMap.size();
  }
  
  public boolean isEmpty()
  {
    return normalMap.isEmpty();
  }
  
  public boolean containsKey(Object key)
  {
    return normalMap.containsKey(key);
  }
  
  public boolean equals(Object obj)
  {
    return normalMap.equals(obj);
  }
  
  public int hashCode()
  {
    return normalMap.hashCode();
  }
  
  public String toString()
  {
    return normalMap.toString();
  }
  



  public V put(K key, V value)
  {
    if (normalMap.containsKey(key)) {
      reverseMap.remove(normalMap.get(key));
    }
    if (reverseMap.containsKey(value)) {
      normalMap.remove(reverseMap.get(value));
    }
    V obj = normalMap.put(key, value);
    reverseMap.put(value, key);
    return obj;
  }
  
  public void putAll(Map<? extends K, ? extends V> map)
  {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }
  
  public V remove(Object key)
  {
    V value = null;
    if (normalMap.containsKey(key)) {
      value = normalMap.remove(key);
      reverseMap.remove(value);
    }
    return value;
  }
  
  public void clear()
  {
    normalMap.clear();
    reverseMap.clear();
  }
  
  public boolean containsValue(Object value)
  {
    return reverseMap.containsKey(value);
  }
  













  public MapIterator<K, V> mapIterator()
  {
    return new BidiMapIterator(this);
  }
  
  public K getKey(Object value)
  {
    return reverseMap.get(value);
  }
  
  public K removeValue(Object value)
  {
    K key = null;
    if (reverseMap.containsKey(value)) {
      key = reverseMap.remove(value);
      normalMap.remove(key);
    }
    return key;
  }
  
  public BidiMap<V, K> inverseBidiMap()
  {
    if (inverseBidiMap == null) {
      inverseBidiMap = createBidiMap(reverseMap, normalMap, this);
    }
    return inverseBidiMap;
  }
  









  public Set<K> keySet()
  {
    if (keySet == null) {
      keySet = new KeySet(this);
    }
    return keySet;
  }
  






  protected Iterator<K> createKeySetIterator(Iterator<K> iterator)
  {
    return new KeySetIterator(iterator, this);
  }
  







  public Set<V> values()
  {
    if (values == null) {
      values = new Values(this);
    }
    return values;
  }
  






  protected Iterator<V> createValuesIterator(Iterator<V> iterator)
  {
    return new ValuesIterator(iterator, this);
  }
  











  public Set<Map.Entry<K, V>> entrySet()
  {
    if (entrySet == null) {
      entrySet = new EntrySet(this);
    }
    return entrySet;
  }
  






  protected Iterator<Map.Entry<K, V>> createEntrySetIterator(Iterator<Map.Entry<K, V>> iterator)
  {
    return new EntrySetIterator(iterator, this);
  }
  




  protected static abstract class View<K, V, E>
    extends AbstractCollectionDecorator<E>
  {
    private static final long serialVersionUID = 4621510560119690639L;
    



    protected final AbstractDualBidiMap<K, V> parent;
    



    protected View(Collection<E> coll, AbstractDualBidiMap<K, V> parent)
    {
      super();
      this.parent = parent;
    }
    
    public boolean equals(Object object)
    {
      return (object == this) || (decorated().equals(object));
    }
    
    public int hashCode()
    {
      return decorated().hashCode();
    }
    
    public boolean removeAll(Collection<?> coll)
    {
      if ((parent.isEmpty()) || (coll.isEmpty())) {
        return false;
      }
      boolean modified = false;
      Iterator<?> it = coll.iterator();
      while (it.hasNext()) {
        modified |= remove(it.next());
      }
      return modified;
    }
    









    public boolean retainAll(Collection<?> coll)
    {
      if (parent.isEmpty()) {
        return false;
      }
      if (coll.isEmpty()) {
        parent.clear();
        return true;
      }
      boolean modified = false;
      Iterator<E> it = iterator();
      while (it.hasNext()) {
        if (!coll.contains(it.next())) {
          it.remove();
          modified = true;
        }
      }
      return modified;
    }
    
    public void clear()
    {
      parent.clear();
    }
  }
  




  protected static class KeySet<K>
    extends AbstractDualBidiMap.View<K, Object, K>
    implements Set<K>
  {
    private static final long serialVersionUID = -7107935777385040694L;
    




    protected KeySet(AbstractDualBidiMap<K, ?> parent)
    {
      super(parent);
    }
    
    public Iterator<K> iterator()
    {
      return parent.createKeySetIterator(super.iterator());
    }
    
    public boolean contains(Object key)
    {
      return parent.normalMap.containsKey(key);
    }
    
    public boolean remove(Object key)
    {
      if (parent.normalMap.containsKey(key)) {
        Object value = parent.normalMap.remove(key);
        parent.reverseMap.remove(value);
        return true;
      }
      return false;
    }
  }
  



  protected static class KeySetIterator<K>
    extends AbstractIteratorDecorator<K>
  {
    protected final AbstractDualBidiMap<K, ?> parent;
    

    protected K lastKey = null;
    

    protected boolean canRemove = false;
    




    protected KeySetIterator(Iterator<K> iterator, AbstractDualBidiMap<K, ?> parent)
    {
      super();
      this.parent = parent;
    }
    
    public K next()
    {
      lastKey = super.next();
      canRemove = true;
      return lastKey;
    }
    
    public void remove()
    {
      if (!canRemove) {
        throw new IllegalStateException("Iterator remove() can only be called once after next()");
      }
      Object value = parent.normalMap.get(lastKey);
      super.remove();
      parent.reverseMap.remove(value);
      lastKey = null;
      canRemove = false;
    }
  }
  




  protected static class Values<V>
    extends AbstractDualBidiMap.View<Object, V, V>
    implements Set<V>
  {
    private static final long serialVersionUID = 4023777119829639864L;
    




    protected Values(AbstractDualBidiMap<?, V> parent)
    {
      super(parent);
    }
    
    public Iterator<V> iterator()
    {
      return parent.createValuesIterator(super.iterator());
    }
    
    public boolean contains(Object value)
    {
      return parent.reverseMap.containsKey(value);
    }
    
    public boolean remove(Object value)
    {
      if (parent.reverseMap.containsKey(value)) {
        Object key = parent.reverseMap.remove(value);
        parent.normalMap.remove(key);
        return true;
      }
      return false;
    }
  }
  



  protected static class ValuesIterator<V>
    extends AbstractIteratorDecorator<V>
  {
    protected final AbstractDualBidiMap<Object, V> parent;
    

    protected V lastValue = null;
    

    protected boolean canRemove = false;
    





    protected ValuesIterator(Iterator<V> iterator, AbstractDualBidiMap<?, V> parent)
    {
      super();
      this.parent = parent;
    }
    
    public V next()
    {
      lastValue = super.next();
      canRemove = true;
      return lastValue;
    }
    
    public void remove()
    {
      if (!canRemove) {
        throw new IllegalStateException("Iterator remove() can only be called once after next()");
      }
      super.remove();
      parent.reverseMap.remove(lastValue);
      lastValue = null;
      canRemove = false;
    }
  }
  




  protected static class EntrySet<K, V>
    extends AbstractDualBidiMap.View<K, V, Map.Entry<K, V>>
    implements Set<Map.Entry<K, V>>
  {
    private static final long serialVersionUID = 4040410962603292348L;
    



    protected EntrySet(AbstractDualBidiMap<K, V> parent)
    {
      super(parent);
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return parent.createEntrySetIterator(super.iterator());
    }
    
    public boolean remove(Object obj)
    {
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<?, ?> entry = (Map.Entry)obj;
      Object key = entry.getKey();
      if (parent.containsKey(key)) {
        V value = parent.normalMap.get(key);
        if (value == null ? entry.getValue() == null : value.equals(entry.getValue())) {
          parent.normalMap.remove(key);
          parent.reverseMap.remove(value);
          return true;
        }
      }
      return false;
    }
  }
  



  protected static class EntrySetIterator<K, V>
    extends AbstractIteratorDecorator<Map.Entry<K, V>>
  {
    protected final AbstractDualBidiMap<K, V> parent;
    

    protected Map.Entry<K, V> last = null;
    

    protected boolean canRemove = false;
    




    protected EntrySetIterator(Iterator<Map.Entry<K, V>> iterator, AbstractDualBidiMap<K, V> parent)
    {
      super();
      this.parent = parent;
    }
    
    public Map.Entry<K, V> next()
    {
      last = new AbstractDualBidiMap.MapEntry((Map.Entry)super.next(), parent);
      canRemove = true;
      return last;
    }
    
    public void remove()
    {
      if (!canRemove) {
        throw new IllegalStateException("Iterator remove() can only be called once after next()");
      }
      
      Object value = last.getValue();
      super.remove();
      parent.reverseMap.remove(value);
      last = null;
      canRemove = false;
    }
  }
  




  protected static class MapEntry<K, V>
    extends AbstractMapEntryDecorator<K, V>
  {
    protected final AbstractDualBidiMap<K, V> parent;
    



    protected MapEntry(Map.Entry<K, V> entry, AbstractDualBidiMap<K, V> parent)
    {
      super();
      this.parent = parent;
    }
    
    public V setValue(V value)
    {
      K key = getKey();
      if ((parent.reverseMap.containsKey(value)) && (parent.reverseMap.get(value) != key))
      {
        throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
      }
      
      parent.put(key, value);
      return super.setValue(value);
    }
  }
  



  protected static class BidiMapIterator<K, V>
    implements MapIterator<K, V>, ResettableIterator<K>
  {
    protected final AbstractDualBidiMap<K, V> parent;
    

    protected Iterator<Map.Entry<K, V>> iterator;
    

    protected Map.Entry<K, V> last = null;
    

    protected boolean canRemove = false;
    




    protected BidiMapIterator(AbstractDualBidiMap<K, V> parent)
    {
      this.parent = parent;
      iterator = normalMap.entrySet().iterator();
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
      
      V value = last.getValue();
      iterator.remove();
      parent.reverseMap.remove(value);
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
      
      if ((parent.reverseMap.containsKey(value)) && (parent.reverseMap.get(value) != last.getKey()))
      {
        throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
      }
      
      return parent.put(last.getKey(), value);
    }
    
    public void reset()
    {
      iterator = parent.normalMap.entrySet().iterator();
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
}
