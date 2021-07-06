package org.apache.commons.collections4.map;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.KeyValue;























































































public final class StaticBucketMap<K, V>
  extends AbstractIterableMap<K, V>
{
  private static final int DEFAULT_BUCKETS = 255;
  private final Node<K, V>[] buckets;
  private final Lock[] locks;
  
  public StaticBucketMap()
  {
    this(255);
  }
  










  public StaticBucketMap(int numBuckets)
  {
    int size = Math.max(17, numBuckets);
    

    if (size % 2 == 0) {
      size--;
    }
    
    buckets = new Node[size];
    locks = new Lock[size];
    
    for (int i = 0; i < size; i++) {
      locks[i] = new Lock(null);
    }
  }
  













  private int getHash(Object key)
  {
    if (key == null) {
      return 0;
    }
    int hash = key.hashCode();
    hash += (hash << 15 ^ 0xFFFFFFFF);
    hash ^= hash >>> 10;
    hash += (hash << 3);
    hash ^= hash >>> 6;
    hash += (hash << 11 ^ 0xFFFFFFFF);
    hash ^= hash >>> 16;
    hash %= buckets.length;
    return hash < 0 ? hash * -1 : hash;
  }
  





  public int size()
  {
    int cnt = 0;
    
    for (int i = 0; i < buckets.length; i++) {
      synchronized (locks[i]) {
        cnt += locks[i].size;
      }
    }
    return cnt;
  }
  




  public boolean isEmpty()
  {
    return size() == 0;
  }
  





  public V get(Object key)
  {
    int hash = getHash(key);
    
    synchronized (locks[hash]) {
      Node<K, V> n = buckets[hash];
      
      while (n != null) {
        if ((key == key) || ((key != null) && (key.equals(key)))) {
          return value;
        }
        
        n = next;
      }
    }
    return null;
  }
  





  public boolean containsKey(Object key)
  {
    int hash = getHash(key);
    
    synchronized (locks[hash]) {
      Node<K, V> n = buckets[hash];
      
      while (n != null) {
        if ((key == key) || ((key != null) && (key.equals(key)))) {
          return true;
        }
        
        n = next;
      }
    }
    return false;
  }
  





  public boolean containsValue(Object value)
  {
    for (int i = 0; i < buckets.length; i++) {
      synchronized (locks[i]) {
        Node<K, V> n = buckets[i];
        
        while (n != null) {
          if ((value == value) || ((value != null) && (value.equals(value)))) {
            return true;
          }
          
          n = next;
        }
      }
    }
    return false;
  }
  







  public V put(K key, V value)
  {
    int hash = getHash(key);
    
    synchronized (locks[hash]) {
      Node<K, V> n = buckets[hash];
      
      if (n == null) {
        n = new Node(null);
        key = key;
        value = value;
        buckets[hash] = n;
        locks[hash].size += 1;
        return null;
      }
      



      for (Node<K, V> next = n; next != null; next = next) {
        n = next;
        
        if ((key == key) || ((key != null) && (key.equals(key)))) {
          V returnVal = value;
          value = value;
          return returnVal;
        }
      }
      


      Node<K, V> newNode = new Node(null);
      key = key;
      value = value;
      next = newNode;
      locks[hash].size += 1;
    }
    return null;
  }
  





  public V remove(Object key)
  {
    int hash = getHash(key);
    
    synchronized (locks[hash]) {
      Node<K, V> n = buckets[hash];
      Node<K, V> prev = null;
      
      while (n != null) {
        if ((key == key) || ((key != null) && (key.equals(key))))
        {
          if (null == prev)
          {
            buckets[hash] = next;
          }
          else {
            next = next;
          }
          locks[hash].size -= 1;
          return value;
        }
        
        prev = n;
        n = next;
      }
    }
    return null;
  }
  





  public Set<K> keySet()
  {
    return new KeySet(null);
  }
  




  public Collection<V> values()
  {
    return new Values(null);
  }
  




  public Set<Map.Entry<K, V>> entrySet()
  {
    return new EntrySet(null);
  }
  






  public void putAll(Map<? extends K, ? extends V> map)
  {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }
  


  public void clear()
  {
    for (int i = 0; i < buckets.length; i++) {
      Lock lock = locks[i];
      synchronized (lock) {
        buckets[i] = null;
        size = 0;
      }
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
    Map<?, ?> other = (Map)obj;
    return entrySet().equals(other.entrySet());
  }
  





  public int hashCode()
  {
    int hashCode = 0;
    
    for (int i = 0; i < buckets.length; i++) {
      synchronized (locks[i]) {
        Node<K, V> n = buckets[i];
        
        while (n != null) {
          hashCode += n.hashCode();
          n = next;
        }
      }
    }
    return hashCode;
  }
  
  private static final class Node<K, V> implements Map.Entry<K, V>, KeyValue<K, V>
  {
    protected K key;
    protected V value;
    protected Node<K, V> next;
    
    private Node() {}
    
    public K getKey()
    {
      return key;
    }
    
    public V getValue() {
      return value;
    }
    
    public int hashCode()
    {
      return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }
    

    public boolean equals(Object obj)
    {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      
      Map.Entry<?, ?> e2 = (Map.Entry)obj;
      return (key == null ? e2.getKey() == null : key.equals(e2.getKey())) && (value == null ? e2.getValue() == null : value.equals(e2.getValue()));
    }
    

    public V setValue(V obj)
    {
      V retVal = value;
      value = obj;
      return retVal;
    }
  }
  
  private static final class Lock
  {
    public int size;
    
    private Lock() {}
  }
  
  private class BaseIterator
  {
    private final ArrayList<Map.Entry<K, V>> current = new ArrayList();
    private int bucket;
    
    private BaseIterator() {}
    
    public boolean hasNext() { if (current.size() > 0) {
        return true;
      }
      while (bucket < buckets.length) {
        synchronized (locks[bucket]) {
          StaticBucketMap.Node<K, V> n = buckets[bucket];
          while (n != null) {
            current.add(n);
            n = next;
          }
          bucket += 1;
          if (current.size() > 0) {
            return true;
          }
        }
      }
      return false;
    }
    
    private Map.Entry<K, V> last;
    protected Map.Entry<K, V> nextEntry() { if (!hasNext()) {
        throw new NoSuchElementException();
      }
      last = ((Map.Entry)current.remove(current.size() - 1));
      return last;
    }
    
    public void remove() {
      if (last == null) {
        throw new IllegalStateException();
      }
      remove(last.getKey());
      last = null;
    }
  }
  
  private class EntryIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<Map.Entry<K, V>> { private EntryIterator() { super(null); }
    
    public Map.Entry<K, V> next() {
      return nextEntry();
    }
  }
  
  private class ValueIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<V> {
    private ValueIterator() { super(null); }
    
    public V next() {
      return nextEntry().getValue();
    }
  }
  
  private class KeyIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<K> {
    private KeyIterator() { super(null); }
    
    public K next() {
      return nextEntry().getKey();
    }
  }
  
  private class EntrySet extends AbstractSet<Map.Entry<K, V>>
  {
    private EntrySet() {}
    
    public int size() {
      return StaticBucketMap.this.size();
    }
    
    public void clear()
    {
      StaticBucketMap.this.clear();
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new StaticBucketMap.EntryIterator(StaticBucketMap.this, null);
    }
    
    public boolean contains(Object obj)
    {
      Map.Entry<?, ?> entry = (Map.Entry)obj;
      int hash = StaticBucketMap.this.getHash(entry.getKey());
      synchronized (locks[hash]) {
        for (StaticBucketMap.Node<K, V> n = buckets[hash]; n != null; n = next) {
          if (n.equals(entry)) {
            return true;
          }
        }
      }
      return false;
    }
    
    public boolean remove(Object obj)
    {
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<?, ?> entry = (Map.Entry)obj;
      int hash = StaticBucketMap.this.getHash(entry.getKey());
      synchronized (locks[hash]) {
        for (StaticBucketMap.Node<K, V> n = buckets[hash]; n != null; n = next) {
          if (n.equals(entry)) {
            remove(n.getKey());
            return true;
          }
        }
      }
      return false;
    }
  }
  
  private class KeySet extends AbstractSet<K>
  {
    private KeySet() {}
    
    public int size() {
      return StaticBucketMap.this.size();
    }
    
    public void clear()
    {
      StaticBucketMap.this.clear();
    }
    
    public Iterator<K> iterator()
    {
      return new StaticBucketMap.KeyIterator(StaticBucketMap.this, null);
    }
    
    public boolean contains(Object obj)
    {
      return containsKey(obj);
    }
    
    public boolean remove(Object obj)
    {
      int hash = StaticBucketMap.this.getHash(obj);
      synchronized (locks[hash]) {
        for (StaticBucketMap.Node<K, V> n = buckets[hash]; n != null; n = next) {
          Object k = n.getKey();
          if ((k == obj) || ((k != null) && (k.equals(obj)))) {
            remove(k);
            return true;
          }
        }
      }
      return false;
    }
  }
  
  private class Values extends AbstractCollection<V>
  {
    private Values() {}
    
    public int size()
    {
      return StaticBucketMap.this.size();
    }
    
    public void clear()
    {
      StaticBucketMap.this.clear();
    }
    
    public Iterator<V> iterator()
    {
      return new StaticBucketMap.ValueIterator(StaticBucketMap.this, null);
    }
  }
  


































  public void atomic(Runnable r)
  {
    if (r == null) {
      throw new NullPointerException();
    }
    atomic(r, 0);
  }
  
  private void atomic(Runnable r, int bucket) {
    if (bucket >= buckets.length) {
      r.run();
      return;
    }
    synchronized (locks[bucket]) {
      atomic(r, bucket + 1);
    }
  }
}
