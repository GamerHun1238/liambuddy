package org.apache.commons.collections4.trie;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
































public class UnmodifiableTrie<K, V>
  implements Trie<K, V>, Serializable, Unmodifiable
{
  private static final long serialVersionUID = -7156426030315945159L;
  private final Trie<K, V> delegate;
  
  public static <K, V> Trie<K, V> unmodifiableTrie(Trie<K, ? extends V> trie)
  {
    if ((trie instanceof Unmodifiable))
    {
      Trie<K, V> tmpTrie = trie;
      return tmpTrie;
    }
    return new UnmodifiableTrie(trie);
  }
  






  public UnmodifiableTrie(Trie<K, ? extends V> trie)
  {
    if (trie == null) {
      throw new NullPointerException("Trie must not be null");
    }
    
    Trie<K, V> tmpTrie = trie;
    delegate = tmpTrie;
  }
  

  public Set<Map.Entry<K, V>> entrySet()
  {
    return Collections.unmodifiableSet(delegate.entrySet());
  }
  
  public Set<K> keySet() {
    return Collections.unmodifiableSet(delegate.keySet());
  }
  
  public Collection<V> values() {
    return Collections.unmodifiableCollection(delegate.values());
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsKey(Object key) {
    return delegate.containsKey(key);
  }
  
  public boolean containsValue(Object value) {
    return delegate.containsValue(value);
  }
  
  public V get(Object key) {
    return delegate.get(key);
  }
  
  public boolean isEmpty() {
    return delegate.isEmpty();
  }
  
  public V put(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }
  
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public int size() {
    return delegate.size();
  }
  
  public K firstKey() {
    return delegate.firstKey();
  }
  
  public SortedMap<K, V> headMap(K toKey) {
    return Collections.unmodifiableSortedMap(delegate.headMap(toKey));
  }
  
  public K lastKey() {
    return delegate.lastKey();
  }
  
  public SortedMap<K, V> subMap(K fromKey, K toKey) {
    return Collections.unmodifiableSortedMap(delegate.subMap(fromKey, toKey));
  }
  
  public SortedMap<K, V> tailMap(K fromKey) {
    return Collections.unmodifiableSortedMap(delegate.tailMap(fromKey));
  }
  
  public SortedMap<K, V> prefixMap(K key) {
    return Collections.unmodifiableSortedMap(delegate.prefixMap(key));
  }
  
  public Comparator<? super K> comparator() {
    return delegate.comparator();
  }
  
  public OrderedMapIterator<K, V> mapIterator()
  {
    OrderedMapIterator<K, V> it = delegate.mapIterator();
    return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator(it);
  }
  
  public K nextKey(K key) {
    return delegate.nextKey(key);
  }
  
  public K previousKey(K key) {
    return delegate.previousKey(key);
  }
  

  public int hashCode()
  {
    return delegate.hashCode();
  }
  
  public boolean equals(Object obj)
  {
    return delegate.equals(obj);
  }
  
  public String toString()
  {
    return delegate.toString();
  }
}
