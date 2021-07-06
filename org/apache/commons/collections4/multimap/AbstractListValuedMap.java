package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.ListValuedMap;





































public abstract class AbstractListValuedMap<K, V>
  extends AbstractMultiValuedMap<K, V>
  implements ListValuedMap<K, V>
{
  protected AbstractListValuedMap() {}
  
  protected AbstractListValuedMap(Map<K, ? extends List<V>> map)
  {
    super(map);
  }
  


  protected Map<K, List<V>> getMap()
  {
    return super.getMap();
  }
  







  protected abstract List<V> createCollection();
  






  public List<V> get(K key)
  {
    return wrappedCollection(key);
  }
  
  List<V> wrappedCollection(K key)
  {
    return new WrappedList(key);
  }
  









  public List<V> remove(Object key)
  {
    return ListUtils.emptyIfNull((List)getMap().remove(key));
  }
  

  private class WrappedList
    extends AbstractMultiValuedMap<K, V>.WrappedCollection
    implements List<V>
  {
    public WrappedList()
    {
      super(key);
    }
    
    protected List<V> getMapping()
    {
      return (List)getMap().get(key);
    }
    
    public void add(int index, V value)
    {
      List<V> list = getMapping();
      if (list == null) {
        list = createCollection();
        getMap().put(key, list);
      }
      list.add(index, value);
    }
    
    public boolean addAll(int index, Collection<? extends V> c)
    {
      List<V> list = getMapping();
      if (list == null) {
        list = createCollection();
        boolean changed = list.addAll(index, c);
        if (changed) {
          getMap().put(key, list);
        }
        return changed;
      }
      return list.addAll(index, c);
    }
    
    public V get(int index)
    {
      List<V> list = ListUtils.emptyIfNull(getMapping());
      return list.get(index);
    }
    
    public int indexOf(Object o)
    {
      List<V> list = ListUtils.emptyIfNull(getMapping());
      return list.indexOf(o);
    }
    
    public int lastIndexOf(Object o)
    {
      List<V> list = ListUtils.emptyIfNull(getMapping());
      return list.lastIndexOf(o);
    }
    
    public ListIterator<V> listIterator()
    {
      return new AbstractListValuedMap.ValuesListIterator(AbstractListValuedMap.this, key);
    }
    
    public ListIterator<V> listIterator(int index)
    {
      return new AbstractListValuedMap.ValuesListIterator(AbstractListValuedMap.this, key, index);
    }
    
    public V remove(int index)
    {
      List<V> list = ListUtils.emptyIfNull(getMapping());
      V value = list.remove(index);
      if (list.isEmpty()) {
        remove(key);
      }
      return value;
    }
    
    public V set(int index, V value)
    {
      List<V> list = ListUtils.emptyIfNull(getMapping());
      return list.set(index, value);
    }
    
    public List<V> subList(int fromIndex, int toIndex)
    {
      List<V> list = ListUtils.emptyIfNull(getMapping());
      return list.subList(fromIndex, toIndex);
    }
    
    public boolean equals(Object other)
    {
      List<V> list = getMapping();
      if (list == null) {
        return Collections.emptyList().equals(other);
      }
      if (!(other instanceof List)) {
        return false;
      }
      List<?> otherList = (List)other;
      return ListUtils.isEqualList(list, otherList);
    }
    
    public int hashCode()
    {
      List<V> list = getMapping();
      return ListUtils.hashCodeForList(list);
    }
  }
  
  private class ValuesListIterator
    implements ListIterator<V>
  {
    private final K key;
    private List<V> values;
    private ListIterator<V> iterator;
    
    public ValuesListIterator()
    {
      this.key = key;
      values = ListUtils.emptyIfNull((List)getMap().get(key));
      iterator = values.listIterator();
    }
    
    public ValuesListIterator(int key) {
      this.key = key;
      values = ListUtils.emptyIfNull((List)getMap().get(key));
      iterator = values.listIterator(index);
    }
    
    public void add(V value)
    {
      if (getMap().get(key) == null) {
        List<V> list = createCollection();
        getMap().put(key, list);
        values = list;
        iterator = list.listIterator();
      }
      iterator.add(value);
    }
    
    public boolean hasNext()
    {
      return iterator.hasNext();
    }
    
    public boolean hasPrevious()
    {
      return iterator.hasPrevious();
    }
    
    public V next()
    {
      return iterator.next();
    }
    
    public int nextIndex()
    {
      return iterator.nextIndex();
    }
    
    public V previous()
    {
      return iterator.previous();
    }
    
    public int previousIndex()
    {
      return iterator.previousIndex();
    }
    
    public void remove()
    {
      iterator.remove();
      if (values.isEmpty()) {
        getMap().remove(key);
      }
    }
    
    public void set(V value)
    {
      iterator.set(value);
    }
  }
}
