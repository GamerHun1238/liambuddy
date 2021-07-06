package org.apache.commons.collections4.multimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSet.Entry;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;
import org.apache.commons.collections4.multiset.AbstractMultiSet;
import org.apache.commons.collections4.multiset.AbstractMultiSet.AbstractEntry;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;












































public abstract class AbstractMultiValuedMap<K, V>
  implements MultiValuedMap<K, V>
{
  private transient Collection<V> valuesView;
  private transient AbstractMultiValuedMap<K, V>.EntryValues entryValuesView;
  private transient MultiSet<K> keysMultiSetView;
  private transient AbstractMultiValuedMap<K, V>.AsMap asMapView;
  private transient Map<K, Collection<V>> map;
  
  protected AbstractMultiValuedMap() {}
  
  protected AbstractMultiValuedMap(Map<K, ? extends Collection<V>> map)
  {
    if (map == null) {
      throw new NullPointerException("Map must not be null.");
    }
    this.map = map;
  }
  





  protected Map<K, ? extends Collection<V>> getMap()
  {
    return map;
  }
  







  protected void setMap(Map<K, ? extends Collection<V>> map)
  {
    this.map = map;
  }
  

  protected abstract Collection<V> createCollection();
  
  public boolean containsKey(Object key)
  {
    return getMap().containsKey(key);
  }
  
  public boolean containsValue(Object value)
  {
    return values().contains(value);
  }
  
  public boolean containsMapping(Object key, Object value)
  {
    Collection<V> coll = (Collection)getMap().get(key);
    return (coll != null) && (coll.contains(value));
  }
  
  public Collection<Map.Entry<K, V>> entries()
  {
    return this.entryValuesView = new EntryValues(null);
  }
  







  public Collection<V> get(K key)
  {
    return wrappedCollection(key);
  }
  
  Collection<V> wrappedCollection(K key) {
    return new WrappedCollection(key);
  }
  









  public Collection<V> remove(Object key)
  {
    return CollectionUtils.emptyIfNull((Collection)getMap().remove(key));
  }
  













  public boolean removeMapping(Object key, Object value)
  {
    Collection<V> coll = (Collection)getMap().get(key);
    if (coll == null) {
      return false;
    }
    boolean changed = coll.remove(value);
    if (coll.isEmpty()) {
      getMap().remove(key);
    }
    return changed;
  }
  
  public boolean isEmpty()
  {
    return getMap().isEmpty();
  }
  
  public Set<K> keySet()
  {
    return getMap().keySet();
  }
  











  public int size()
  {
    int size = 0;
    for (Collection<V> col : getMap().values()) {
      size += col.size();
    }
    return size;
  }
  







  public Collection<V> values()
  {
    Collection<V> vs = valuesView;
    return this.valuesView = new Values(null);
  }
  
  public void clear()
  {
    getMap().clear();
  }
  










  public boolean put(K key, V value)
  {
    Collection<V> coll = (Collection)getMap().get(key);
    if (coll == null) {
      coll = createCollection();
      if (coll.add(value)) {
        map.put(key, coll);
        return true;
      }
      return false;
    }
    
    return coll.add(value);
  }
  













  public boolean putAll(Map<? extends K, ? extends V> map)
  {
    if (map == null) {
      throw new NullPointerException("Map must not be null.");
    }
    boolean changed = false;
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      changed |= put(entry.getKey(), entry.getValue());
    }
    return changed;
  }
  












  public boolean putAll(MultiValuedMap<? extends K, ? extends V> map)
  {
    if (map == null) {
      throw new NullPointerException("Map must not be null.");
    }
    boolean changed = false;
    for (Map.Entry<? extends K, ? extends V> entry : map.entries()) {
      changed |= put(entry.getKey(), entry.getValue());
    }
    return changed;
  }
  










  public MultiSet<K> keys()
  {
    if (keysMultiSetView == null) {
      keysMultiSetView = UnmodifiableMultiSet.unmodifiableMultiSet(new KeysMultiSet(null));
    }
    return keysMultiSetView;
  }
  
  public Map<K, Collection<V>> asMap()
  {
    return this.asMapView = new AsMap(map);
  }
  








  public boolean putAll(K key, Iterable<? extends V> values)
  {
    if (values == null) {
      throw new NullPointerException("Values must not be null.");
    }
    
    if ((values instanceof Collection)) {
      Collection<? extends V> valueCollection = (Collection)values;
      return (!valueCollection.isEmpty()) && (get(key).addAll(valueCollection));
    }
    Iterator<? extends V> it = values.iterator();
    return (it.hasNext()) && (CollectionUtils.addAll(get(key), it));
  }
  

  public MapIterator<K, V> mapIterator()
  {
    if (size() == 0) {
      return EmptyMapIterator.emptyMapIterator();
    }
    return new MultiValuedMapIterator();
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if ((obj instanceof MultiValuedMap)) {
      return asMap().equals(((MultiValuedMap)obj).asMap());
    }
    return false;
  }
  
  public int hashCode()
  {
    return getMap().hashCode();
  }
  
  public String toString()
  {
    return getMap().toString();
  }
  





  class WrappedCollection
    implements Collection<V>
  {
    protected final K key;
    





    public WrappedCollection()
    {
      this.key = key;
    }
    
    protected Collection<V> getMapping() {
      return (Collection)getMap().get(key);
    }
    
    public boolean add(V value)
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        coll = createCollection();
        map.put(key, coll);
      }
      return coll.add(value);
    }
    
    public boolean addAll(Collection<? extends V> other)
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        coll = createCollection();
        map.put(key, coll);
      }
      return coll.addAll(other);
    }
    
    public void clear()
    {
      Collection<V> coll = getMapping();
      if (coll != null) {
        coll.clear();
        remove(key);
      }
    }
    

    public Iterator<V> iterator()
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        return IteratorUtils.EMPTY_ITERATOR;
      }
      return new AbstractMultiValuedMap.ValuesIterator(AbstractMultiValuedMap.this, key);
    }
    
    public int size()
    {
      Collection<V> coll = getMapping();
      return coll == null ? 0 : coll.size();
    }
    
    public boolean contains(Object obj)
    {
      Collection<V> coll = getMapping();
      return coll == null ? false : coll.contains(obj);
    }
    
    public boolean containsAll(Collection<?> other)
    {
      Collection<V> coll = getMapping();
      return coll == null ? false : coll.containsAll(other);
    }
    
    public boolean isEmpty()
    {
      Collection<V> coll = getMapping();
      return coll == null ? true : coll.isEmpty();
    }
    
    public boolean remove(Object item)
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        return false;
      }
      
      boolean result = coll.remove(item);
      if (coll.isEmpty()) {
        remove(key);
      }
      return result;
    }
    
    public boolean removeAll(Collection<?> c)
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        return false;
      }
      
      boolean result = coll.removeAll(c);
      if (coll.isEmpty()) {
        remove(key);
      }
      return result;
    }
    
    public boolean retainAll(Collection<?> c)
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        return false;
      }
      
      boolean result = coll.retainAll(c);
      if (coll.isEmpty()) {
        remove(key);
      }
      return result;
    }
    
    public Object[] toArray()
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        return CollectionUtils.EMPTY_COLLECTION.toArray();
      }
      return coll.toArray();
    }
    

    public <T> T[] toArray(T[] a)
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        return (Object[])CollectionUtils.EMPTY_COLLECTION.toArray(a);
      }
      return coll.toArray(a);
    }
    
    public String toString()
    {
      Collection<V> coll = getMapping();
      if (coll == null) {
        return CollectionUtils.EMPTY_COLLECTION.toString();
      }
      return coll.toString();
    }
  }
  

  private class KeysMultiSet
    extends AbstractMultiSet<K>
  {
    private KeysMultiSet() {}
    
    public boolean contains(Object o)
    {
      return getMap().containsKey(o);
    }
    
    public boolean isEmpty()
    {
      return getMap().isEmpty();
    }
    
    public int size()
    {
      return AbstractMultiValuedMap.this.size();
    }
    
    protected int uniqueElements()
    {
      return getMap().size();
    }
    
    public int getCount(Object object)
    {
      int count = 0;
      Collection<V> col = (Collection)getMap().get(object);
      if (col != null) {
        count = col.size();
      }
      return count;
    }
    
    protected Iterator<MultiSet.Entry<K>> createEntrySetIterator()
    {
      AbstractMultiValuedMap<K, V>.KeysMultiSet.MapEntryTransformer transformer = new MapEntryTransformer(null);
      return IteratorUtils.transformedIterator(map.entrySet().iterator(), transformer);
    }
    
    private final class MapEntryTransformer implements Transformer<Map.Entry<K, Collection<V>>, MultiSet.Entry<K>> {
      private MapEntryTransformer() {}
      
      public MultiSet.Entry<K> transform(final Map.Entry<K, Collection<V>> mapEntry) {
        new AbstractMultiSet.AbstractEntry()
        {
          public K getElement() {
            return mapEntry.getKey();
          }
          
          public int getCount()
          {
            return ((Collection)mapEntry.getValue()).size();
          }
        };
      }
    }
  }
  
  private class EntryValues
    extends AbstractCollection<Map.Entry<K, V>>
  {
    private EntryValues() {}
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      new LazyIteratorChain()
      {
        final Collection<K> keysCol = new ArrayList(getMap().keySet());
        final Iterator<K> keyIterator = keysCol.iterator();
        
        protected Iterator<? extends Map.Entry<K, V>> nextIterator(int count)
        {
          if (!keyIterator.hasNext()) {
            return null;
          }
          final K key = keyIterator.next();
          Transformer<V, Map.Entry<K, V>> entryTransformer = new Transformer()
          {
            public Map.Entry<K, V> transform(V input)
            {
              return new AbstractMultiValuedMap.MultiValuedMapEntry(AbstractMultiValuedMap.this, key, input);
            }
            
          };
          return new TransformIterator(new AbstractMultiValuedMap.ValuesIterator(AbstractMultiValuedMap.this, key), entryTransformer);
        }
      };
    }
    
    public int size()
    {
      return AbstractMultiValuedMap.this.size();
    }
  }
  


  private class MultiValuedMapEntry
    extends AbstractMapEntry<K, V>
  {
    public MultiValuedMapEntry(V key)
    {
      super(value);
    }
    
    public V setValue(V value)
    {
      throw new UnsupportedOperationException();
    }
  }
  


  private class MultiValuedMapIterator
    implements MapIterator<K, V>
  {
    private final Iterator<Map.Entry<K, V>> it;
    

    private Map.Entry<K, V> current = null;
    
    public MultiValuedMapIterator() {
      it = entries().iterator();
    }
    
    public boolean hasNext()
    {
      return it.hasNext();
    }
    
    public K next()
    {
      current = ((Map.Entry)it.next());
      return current.getKey();
    }
    
    public K getKey()
    {
      if (current == null) {
        throw new IllegalStateException();
      }
      return current.getKey();
    }
    
    public V getValue()
    {
      if (current == null) {
        throw new IllegalStateException();
      }
      return current.getValue();
    }
    
    public void remove()
    {
      it.remove();
    }
    
    public V setValue(V value)
    {
      if (current == null) {
        throw new IllegalStateException();
      }
      return current.setValue(value);
    }
  }
  
  private class Values
    extends AbstractCollection<V>
  {
    private Values() {}
    
    public Iterator<V> iterator()
    {
      IteratorChain<V> chain = new IteratorChain();
      for (K k : keySet()) {
        chain.addIterator(new AbstractMultiValuedMap.ValuesIterator(AbstractMultiValuedMap.this, k));
      }
      return chain;
    }
    
    public int size()
    {
      return AbstractMultiValuedMap.this.size();
    }
    
    public void clear()
    {
      AbstractMultiValuedMap.this.clear();
    }
  }
  
  private class ValuesIterator
    implements Iterator<V>
  {
    private final Object key;
    private final Collection<V> values;
    private final Iterator<V> iterator;
    
    public ValuesIterator(Object key)
    {
      this.key = key;
      values = ((Collection)getMap().get(key));
      iterator = values.iterator();
    }
    
    public void remove()
    {
      iterator.remove();
      if (values.isEmpty()) {
        remove(key);
      }
    }
    
    public boolean hasNext()
    {
      return iterator.hasNext();
    }
    
    public V next()
    {
      return iterator.next();
    }
  }
  
  private class AsMap
    extends AbstractMap<K, Collection<V>>
  {
    final transient Map<K, Collection<V>> decoratedMap;
    
    AsMap()
    {
      decoratedMap = map;
    }
    
    public Set<Map.Entry<K, Collection<V>>> entrySet()
    {
      return new AsMapEntrySet();
    }
    
    public boolean containsKey(Object key)
    {
      return decoratedMap.containsKey(key);
    }
    
    public Collection<V> get(Object key)
    {
      Collection<V> collection = (Collection)decoratedMap.get(key);
      if (collection == null) {
        return null;
      }
      
      K k = key;
      return wrappedCollection(k);
    }
    
    public Set<K> keySet()
    {
      return AbstractMultiValuedMap.this.keySet();
    }
    
    public int size()
    {
      return decoratedMap.size();
    }
    
    public Collection<V> remove(Object key)
    {
      Collection<V> collection = (Collection)decoratedMap.remove(key);
      if (collection == null) {
        return null;
      }
      
      Collection<V> output = createCollection();
      output.addAll(collection);
      collection.clear();
      return output;
    }
    
    public boolean equals(Object object)
    {
      return (this == object) || (decoratedMap.equals(object));
    }
    
    public int hashCode()
    {
      return decoratedMap.hashCode();
    }
    
    public String toString()
    {
      return decoratedMap.toString();
    }
    
    public void clear()
    {
      AbstractMultiValuedMap.this.clear();
    }
    
    class AsMapEntrySet extends AbstractSet<Map.Entry<K, Collection<V>>> {
      AsMapEntrySet() {}
      
      public Iterator<Map.Entry<K, Collection<V>>> iterator() {
        return new AbstractMultiValuedMap.AsMap.AsMapEntrySetIterator(AbstractMultiValuedMap.AsMap.this, decoratedMap.entrySet().iterator());
      }
      
      public int size()
      {
        return AbstractMultiValuedMap.AsMap.this.size();
      }
      
      public void clear()
      {
        AbstractMultiValuedMap.AsMap.this.clear();
      }
      
      public boolean contains(Object o)
      {
        return decoratedMap.entrySet().contains(o);
      }
      
      public boolean remove(Object o)
      {
        if (!contains(o)) {
          return false;
        }
        Map.Entry<?, ?> entry = (Map.Entry)o;
        AbstractMultiValuedMap.this.remove(entry.getKey());
        return true;
      }
    }
    

    class AsMapEntrySetIterator
      extends AbstractIteratorDecorator<Map.Entry<K, Collection<V>>>
    {
      AsMapEntrySetIterator()
      {
        super();
      }
      
      public Map.Entry<K, Collection<V>> next()
      {
        Map.Entry<K, Collection<V>> entry = (Map.Entry)super.next();
        K key = entry.getKey();
        return new UnmodifiableMapEntry(key, wrappedCollection(key));
      }
    }
  }
  




  protected void doWriteObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(map.size());
    for (Map.Entry<K, Collection<V>> entry : map.entrySet()) {
      out.writeObject(entry.getKey());
      out.writeInt(((Collection)entry.getValue()).size());
      for (V value : (Collection)entry.getValue()) {
        out.writeObject(value);
      }
    }
  }
  






  protected void doReadObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    int entrySize = in.readInt();
    for (int i = 0; i < entrySize; i++)
    {
      K key = in.readObject();
      Collection<V> values = get(key);
      int valueSize = in.readInt();
      for (int j = 0; j < valueSize; j++)
      {
        V value = in.readObject();
        values.add(value);
      }
    }
  }
}
