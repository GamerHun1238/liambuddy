package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.IterableSortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;













































public abstract class AbstractSortedMapDecorator<K, V>
  extends AbstractMapDecorator<K, V>
  implements IterableSortedMap<K, V>
{
  protected AbstractSortedMapDecorator() {}
  
  public AbstractSortedMapDecorator(SortedMap<K, V> map)
  {
    super(map);
  }
  





  protected SortedMap<K, V> decorated()
  {
    return (SortedMap)super.decorated();
  }
  
  public Comparator<? super K> comparator()
  {
    return decorated().comparator();
  }
  
  public K firstKey() {
    return decorated().firstKey();
  }
  
  public K lastKey() {
    return decorated().lastKey();
  }
  
  public SortedMap<K, V> subMap(K fromKey, K toKey) {
    return decorated().subMap(fromKey, toKey);
  }
  
  public SortedMap<K, V> headMap(K toKey) {
    return decorated().headMap(toKey);
  }
  
  public SortedMap<K, V> tailMap(K fromKey) {
    return decorated().tailMap(fromKey);
  }
  
  public K previousKey(K key) {
    SortedMap<K, V> headMap = headMap(key);
    return headMap.isEmpty() ? null : headMap.lastKey();
  }
  
  public K nextKey(K key) {
    Iterator<K> it = tailMap(key).keySet().iterator();
    it.next();
    return it.hasNext() ? it.next() : null;
  }
  



  public OrderedMapIterator<K, V> mapIterator()
  {
    return new SortedMapIterator(entrySet());
  }
  








  protected static class SortedMapIterator<K, V>
    extends EntrySetToMapIteratorAdapter<K, V>
    implements OrderedMapIterator<K, V>
  {
    protected SortedMapIterator(Set<Map.Entry<K, V>> entrySet)
    {
      super();
    }
    



    public synchronized void reset()
    {
      super.reset();
      iterator = new ListIteratorWrapper(iterator);
    }
    


    public boolean hasPrevious()
    {
      return ((ListIterator)iterator).hasPrevious();
    }
    


    public K previous()
    {
      entry = ((Map.Entry)((ListIterator)iterator).previous());
      return getKey();
    }
  }
}
