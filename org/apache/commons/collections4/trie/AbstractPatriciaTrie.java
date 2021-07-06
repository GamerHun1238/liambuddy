package org.apache.commons.collections4.trie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;

























abstract class AbstractPatriciaTrie<K, V>
  extends AbstractBitwiseTrie<K, V>
{
  private static final long serialVersionUID = 5155253417231339498L;
  private transient TrieEntry<K, V> root = new TrieEntry(null, null, -1);
  

  private volatile transient Set<K> keySet;
  

  private volatile transient Collection<V> values;
  

  private volatile transient Set<Map.Entry<K, V>> entrySet;
  

  private transient int size = 0;
  




  protected transient int modCount = 0;
  
  protected AbstractPatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer) {
    super(keyAnalyzer);
  }
  





  protected AbstractPatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer, Map<? extends K, ? extends V> map)
  {
    super(keyAnalyzer);
    putAll(map);
  }
  

  public void clear()
  {
    root.key = null;
    root.bitIndex = -1;
    root.value = null;
    
    root.parent = null;
    root.left = root;
    root.right = null;
    root.predecessor = root;
    
    size = 0;
    incrementModCount();
  }
  
  public int size()
  {
    return size;
  }
  


  void incrementSize()
  {
    size += 1;
    incrementModCount();
  }
  


  void decrementSize()
  {
    size -= 1;
    incrementModCount();
  }
  


  private void incrementModCount()
  {
    modCount += 1;
  }
  
  public V put(K key, V value)
  {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }
    
    int lengthInBits = lengthInBits(key);
    


    if (lengthInBits == 0) {
      if (root.isEmpty()) {
        incrementSize();
      } else {
        incrementModCount();
      }
      return root.setKeyValue(key, value);
    }
    
    TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
    if (compareKeys(key, key)) {
      if (found.isEmpty()) {
        incrementSize();
      } else {
        incrementModCount();
      }
      return found.setKeyValue(key, value);
    }
    
    int bitIndex = bitIndex(key, key);
    if (!KeyAnalyzer.isOutOfBoundsIndex(bitIndex)) {
      if (KeyAnalyzer.isValidBitIndex(bitIndex))
      {
        TrieEntry<K, V> t = new TrieEntry(key, value, bitIndex);
        addEntry(t, lengthInBits);
        incrementSize();
        return null; }
      if (KeyAnalyzer.isNullBitKey(bitIndex))
      {



        if (root.isEmpty()) {
          incrementSize();
        } else {
          incrementModCount();
        }
        return root.setKeyValue(key, value);
      }
      if (KeyAnalyzer.isEqualBitKey(bitIndex))
      {


        if (found != root) {
          incrementModCount();
          return found.setKeyValue(key, value);
        }
      }
    }
    
    throw new IllegalArgumentException("Failed to put: " + key + " -> " + value + ", " + bitIndex);
  }
  


  TrieEntry<K, V> addEntry(TrieEntry<K, V> entry, int lengthInBits)
  {
    TrieEntry<K, V> current = root.left;
    TrieEntry<K, V> path = root;
    for (;;) {
      if ((bitIndex >= bitIndex) || (bitIndex <= bitIndex))
      {
        predecessor = entry;
        
        if (!isBitSet(key, bitIndex, lengthInBits)) {
          left = entry;
          right = current;
        } else {
          left = current;
          right = entry;
        }
        
        parent = path;
        if (bitIndex >= bitIndex) {
          parent = entry;
        }
        

        if (bitIndex <= bitIndex) {
          predecessor = entry;
        }
        
        if ((path == root) || (!isBitSet(key, bitIndex, lengthInBits))) {
          left = entry;
        } else {
          right = entry;
        }
        
        return entry;
      }
      
      path = current;
      
      if (!isBitSet(key, bitIndex, lengthInBits)) {
        current = left;
      } else {
        current = right;
      }
    }
  }
  
  public V get(Object k)
  {
    TrieEntry<K, V> entry = getEntry(k);
    return entry != null ? entry.getValue() : null;
  }
  






  TrieEntry<K, V> getEntry(Object k)
  {
    K key = castKey(k);
    if (key == null) {
      return null;
    }
    
    int lengthInBits = lengthInBits(key);
    TrieEntry<K, V> entry = getNearestEntryForKey(key, lengthInBits);
    return (!entry.isEmpty()) && (compareKeys(key, key)) ? entry : null;
  }
  


















  public Map.Entry<K, V> select(K key)
  {
    int lengthInBits = lengthInBits(key);
    Reference<Map.Entry<K, V>> reference = new Reference(null);
    if (!selectR(root.left, -1, key, lengthInBits, reference)) {
      return (Map.Entry)reference.get();
    }
    return null;
  }
  


















  public K selectKey(K key)
  {
    Map.Entry<K, V> entry = select(key);
    if (entry == null) {
      return null;
    }
    return entry.getKey();
  }
  



















  public V selectValue(K key)
  {
    Map.Entry<K, V> entry = select(key);
    if (entry == null) {
      return null;
    }
    return entry.getValue();
  }
  






  private boolean selectR(TrieEntry<K, V> h, int bitIndex, K key, int lengthInBits, Reference<Map.Entry<K, V>> reference)
  {
    if (bitIndex <= bitIndex)
    {


      if (!h.isEmpty()) {
        reference.set(h);
        return false;
      }
      return true;
    }
    
    if (!isBitSet(key, bitIndex, lengthInBits)) {
      if (selectR(left, bitIndex, key, lengthInBits, reference)) {
        return selectR(right, bitIndex, key, lengthInBits, reference);
      }
    }
    else if (selectR(right, bitIndex, key, lengthInBits, reference)) {
      return selectR(left, bitIndex, key, lengthInBits, reference);
    }
    
    return false;
  }
  
  public boolean containsKey(Object k)
  {
    if (k == null) {
      return false;
    }
    
    K key = castKey(k);
    int lengthInBits = lengthInBits(key);
    TrieEntry<K, V> entry = getNearestEntryForKey(key, lengthInBits);
    return (!entry.isEmpty()) && (compareKeys(key, key));
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    if (entrySet == null) {
      entrySet = new EntrySet(null);
    }
    return entrySet;
  }
  
  public Set<K> keySet()
  {
    if (keySet == null) {
      keySet = new KeySet(null);
    }
    return keySet;
  }
  
  public Collection<V> values()
  {
    if (values == null) {
      values = new Values(null);
    }
    return values;
  }
  





  public V remove(Object k)
  {
    if (k == null) {
      return null;
    }
    
    K key = castKey(k);
    int lengthInBits = lengthInBits(key);
    TrieEntry<K, V> current = root.left;
    TrieEntry<K, V> path = root;
    for (;;) {
      if (bitIndex <= bitIndex) {
        if ((!current.isEmpty()) && (compareKeys(key, key))) {
          return removeEntry(current);
        }
        return null;
      }
      
      path = current;
      
      if (!isBitSet(key, bitIndex, lengthInBits)) {
        current = left;
      } else {
        current = right;
      }
    }
  }
  








  TrieEntry<K, V> getNearestEntryForKey(K key, int lengthInBits)
  {
    TrieEntry<K, V> current = root.left;
    TrieEntry<K, V> path = root;
    for (;;) {
      if (bitIndex <= bitIndex) {
        return current;
      }
      
      path = current;
      if (!isBitSet(key, bitIndex, lengthInBits)) {
        current = left;
      } else {
        current = right;
      }
    }
  }
  






  V removeEntry(TrieEntry<K, V> h)
  {
    if (h != root) {
      if (h.isInternalNode()) {
        removeInternalEntry(h);
      } else {
        removeExternalEntry(h);
      }
    }
    
    decrementSize();
    return h.setKeyValue(null, null);
  }
  





  private void removeExternalEntry(TrieEntry<K, V> h)
  {
    if (h == root)
      throw new IllegalArgumentException("Cannot delete root Entry!");
    if (!h.isExternalNode()) {
      throw new IllegalArgumentException(h + " is not an external Entry!");
    }
    
    TrieEntry<K, V> parent = parent;
    TrieEntry<K, V> child = left == h ? right : left;
    
    if (left == h) {
      left = child;
    } else {
      right = child;
    }
    

    if (bitIndex > bitIndex) {
      parent = parent;
    } else {
      predecessor = parent;
    }
  }
  







  private void removeInternalEntry(TrieEntry<K, V> h)
  {
    if (h == root)
      throw new IllegalArgumentException("Cannot delete root Entry!");
    if (!h.isInternalNode()) {
      throw new IllegalArgumentException(h + " is not an internal Entry!");
    }
    
    TrieEntry<K, V> p = predecessor;
    

    bitIndex = bitIndex;
    


    TrieEntry<K, V> parent = parent;
    TrieEntry<K, V> child = left == h ? right : left;
    






    if ((predecessor == p) && (parent != h)) {
      predecessor = parent;
    }
    
    if (left == p) {
      left = child;
    } else {
      right = child;
    }
    
    if (bitIndex > bitIndex) {
      parent = parent;
    }
    





    if (left.parent == h) {
      left.parent = p;
    }
    
    if (right.parent == h) {
      right.parent = p;
    }
    

    if (parent.left == h) {
      parent.left = p;
    } else {
      parent.right = p;
    }
    



    parent = parent;
    left = left;
    right = right;
    


    if (isValidUplink(left, p)) {
      left.predecessor = p;
    }
    
    if (isValidUplink(right, p)) {
      right.predecessor = p;
    }
  }
  



  TrieEntry<K, V> nextEntry(TrieEntry<K, V> node)
  {
    if (node == null) {
      return firstEntry();
    }
    return nextEntryImpl(predecessor, node, null);
  }
  


































  TrieEntry<K, V> nextEntryImpl(TrieEntry<K, V> start, TrieEntry<K, V> previous, TrieEntry<K, V> tree)
  {
    TrieEntry<K, V> current = start;
    



    if ((previous == null) || (start != predecessor)) {
      while (!left.isEmpty())
      {

        if (previous == left) {
          break;
        }
        
        if (isValidUplink(left, current)) {
          return left;
        }
        
        current = left;
      }
    }
    

    if (current.isEmpty()) {
      return null;
    }
    









    if (right == null) {
      return null;
    }
    

    if (previous != right)
    {
      if (isValidUplink(right, current)) {
        return right;
      }
      

      return nextEntryImpl(right, previous, tree);
    }
    


    while (current == parent.right)
    {
      if (current == tree) {
        return null;
      }
      
      current = parent;
    }
    

    if (current == tree) {
      return null;
    }
    

    if (parent.right == null) {
      return null;
    }
    

    if ((previous != parent.right) && (isValidUplink(parent.right, parent)))
    {
      return parent.right;
    }
    

    if (parent.right == parent) {
      return null;
    }
    

    return nextEntryImpl(parent.right, previous, tree);
  }
  






  TrieEntry<K, V> firstEntry()
  {
    if (isEmpty()) {
      return null;
    }
    
    return followLeft(root);
  }
  

  TrieEntry<K, V> followLeft(TrieEntry<K, V> node)
  {
    for (;;)
    {
      TrieEntry<K, V> child = left;
      
      if (child.isEmpty()) {
        child = right;
      }
      
      if (bitIndex <= bitIndex) {
        return child;
      }
      
      node = child;
    }
  }
  

  public Comparator<? super K> comparator()
  {
    return getKeyAnalyzer();
  }
  
  public K firstKey() {
    if (size() == 0) {
      throw new NoSuchElementException();
    }
    return firstEntry().getKey();
  }
  
  public K lastKey() {
    TrieEntry<K, V> entry = lastEntry();
    if (entry != null) {
      return entry.getKey();
    }
    throw new NoSuchElementException();
  }
  
  public K nextKey(K key) {
    if (key == null) {
      throw new NullPointerException();
    }
    TrieEntry<K, V> entry = getEntry(key);
    if (entry != null) {
      TrieEntry<K, V> nextEntry = nextEntry(entry);
      return nextEntry != null ? nextEntry.getKey() : null;
    }
    return null;
  }
  
  public K previousKey(K key) {
    if (key == null) {
      throw new NullPointerException();
    }
    TrieEntry<K, V> entry = getEntry(key);
    if (entry != null) {
      TrieEntry<K, V> prevEntry = previousEntry(entry);
      return prevEntry != null ? prevEntry.getKey() : null;
    }
    return null;
  }
  
  public OrderedMapIterator<K, V> mapIterator() {
    return new TrieMapIterator(null);
  }
  
  public SortedMap<K, V> prefixMap(K key) {
    return getPrefixMapByBits(key, 0, lengthInBits(key));
  }
  





















  private SortedMap<K, V> getPrefixMapByBits(K key, int offsetInBits, int lengthInBits)
  {
    int offsetLength = offsetInBits + lengthInBits;
    if (offsetLength > lengthInBits(key)) {
      throw new IllegalArgumentException(offsetInBits + " + " + lengthInBits + " > " + lengthInBits(key));
    }
    

    if (offsetLength == 0) {
      return this;
    }
    
    return new PrefixRangeMap(key, offsetInBits, lengthInBits, null);
  }
  
  public SortedMap<K, V> headMap(K toKey) {
    return new RangeEntryMap(null, toKey);
  }
  
  public SortedMap<K, V> subMap(K fromKey, K toKey) {
    return new RangeEntryMap(fromKey, toKey);
  }
  
  public SortedMap<K, V> tailMap(K fromKey) {
    return new RangeEntryMap(fromKey, null);
  }
  






  TrieEntry<K, V> higherEntry(K key)
  {
    int lengthInBits = lengthInBits(key);
    
    if (lengthInBits == 0) {
      if (!root.isEmpty())
      {
        if (size() > 1) {
          return nextEntry(root);
        }
        
        return null;
      }
      
      return firstEntry();
    }
    
    TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
    if (compareKeys(key, key)) {
      return nextEntry(found);
    }
    
    int bitIndex = bitIndex(key, key);
    if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
      TrieEntry<K, V> added = new TrieEntry(key, null, bitIndex);
      addEntry(added, lengthInBits);
      incrementSize();
      TrieEntry<K, V> ceil = nextEntry(added);
      removeEntry(added);
      modCount -= 2;
      return ceil; }
    if (KeyAnalyzer.isNullBitKey(bitIndex)) {
      if (!root.isEmpty())
        return firstEntry();
      if (size() > 1) {
        return nextEntry(firstEntry());
      }
      return null;
    }
    if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
      return nextEntry(found);
    }
    

    throw new IllegalStateException("invalid lookup: " + key);
  }
  





















  TrieEntry<K, V> ceilingEntry(K key)
  {
    int lengthInBits = lengthInBits(key);
    
    if (lengthInBits == 0) {
      if (!root.isEmpty()) {
        return root;
      }
      return firstEntry();
    }
    
    TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
    if (compareKeys(key, key)) {
      return found;
    }
    
    int bitIndex = bitIndex(key, key);
    if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
      TrieEntry<K, V> added = new TrieEntry(key, null, bitIndex);
      addEntry(added, lengthInBits);
      incrementSize();
      TrieEntry<K, V> ceil = nextEntry(added);
      removeEntry(added);
      modCount -= 2;
      return ceil; }
    if (KeyAnalyzer.isNullBitKey(bitIndex)) {
      if (!root.isEmpty()) {
        return root;
      }
      return firstEntry(); }
    if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
      return found;
    }
    

    throw new IllegalStateException("invalid lookup: " + key);
  }
  




















  TrieEntry<K, V> lowerEntry(K key)
  {
    int lengthInBits = lengthInBits(key);
    
    if (lengthInBits == 0) {
      return null;
    }
    
    TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
    if (compareKeys(key, key)) {
      return previousEntry(found);
    }
    
    int bitIndex = bitIndex(key, key);
    if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
      TrieEntry<K, V> added = new TrieEntry(key, null, bitIndex);
      addEntry(added, lengthInBits);
      incrementSize();
      TrieEntry<K, V> prior = previousEntry(added);
      removeEntry(added);
      modCount -= 2;
      return prior; }
    if (KeyAnalyzer.isNullBitKey(bitIndex))
      return null;
    if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
      return previousEntry(found);
    }
    

    throw new IllegalStateException("invalid lookup: " + key);
  }
  






  TrieEntry<K, V> floorEntry(K key)
  {
    int lengthInBits = lengthInBits(key);
    
    if (lengthInBits == 0) {
      if (!root.isEmpty()) {
        return root;
      }
      return null;
    }
    
    TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
    if (compareKeys(key, key)) {
      return found;
    }
    
    int bitIndex = bitIndex(key, key);
    if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
      TrieEntry<K, V> added = new TrieEntry(key, null, bitIndex);
      addEntry(added, lengthInBits);
      incrementSize();
      TrieEntry<K, V> floor = previousEntry(added);
      removeEntry(added);
      modCount -= 2;
      return floor; }
    if (KeyAnalyzer.isNullBitKey(bitIndex)) {
      if (!root.isEmpty()) {
        return root;
      }
      return null; }
    if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
      return found;
    }
    

    throw new IllegalStateException("invalid lookup: " + key);
  }
  





  TrieEntry<K, V> subtree(K prefix, int offsetInBits, int lengthInBits)
  {
    TrieEntry<K, V> current = root.left;
    TrieEntry<K, V> path = root;
    
    while ((bitIndex > bitIndex) && (lengthInBits > bitIndex))
    {


      path = current;
      if (!isBitSet(prefix, offsetInBits + bitIndex, offsetInBits + lengthInBits)) {
        current = left;
      } else {
        current = right;
      }
    }
    

    TrieEntry<K, V> entry = current.isEmpty() ? path : current;
    

    if (entry.isEmpty()) {
      return null;
    }
    
    int endIndexInBits = offsetInBits + lengthInBits;
    




    if ((entry == root) && (lengthInBits(entry.getKey()) < endIndexInBits)) {
      return null;
    }
    


    if (isBitSet(prefix, endIndexInBits - 1, endIndexInBits) != isBitSet(key, lengthInBits - 1, lengthInBits(key)))
    {
      return null;
    }
    

    int bitIndex = getKeyAnalyzer().bitIndex(prefix, offsetInBits, lengthInBits, key, 0, lengthInBits(entry.getKey()));
    

    if ((bitIndex >= 0) && (bitIndex < lengthInBits)) {
      return null;
    }
    
    return entry;
  }
  





  TrieEntry<K, V> lastEntry()
  {
    return followRight(root.left);
  }
  



  TrieEntry<K, V> followRight(TrieEntry<K, V> node)
  {
    if (right == null) {
      return null;
    }
    

    while (right.bitIndex > bitIndex) {
      node = right;
    }
    
    return right;
  }
  


















  TrieEntry<K, V> previousEntry(TrieEntry<K, V> start)
  {
    if (predecessor == null) {
      throw new IllegalArgumentException("must have come from somewhere!");
    }
    
    if (predecessor.right == start) {
      if (isValidUplink(predecessor.left, predecessor)) {
        return predecessor.left;
      }
      return followRight(predecessor.left);
    }
    TrieEntry<K, V> node = predecessor;
    while ((parent != null) && (node == parent.left)) {
      node = parent;
    }
    
    if (parent == null) {
      return null;
    }
    
    if (isValidUplink(parent.left, parent)) {
      if (parent.left == root) {
        if (root.isEmpty()) {
          return null;
        }
        return root;
      }
      
      return parent.left;
    }
    return followRight(parent.left);
  }
  







  TrieEntry<K, V> nextEntryInSubtree(TrieEntry<K, V> node, TrieEntry<K, V> parentOfSubtree)
  {
    if (node == null) {
      return firstEntry();
    }
    return nextEntryImpl(predecessor, node, parentOfSubtree);
  }
  


  static boolean isValidUplink(TrieEntry<?, ?> next, TrieEntry<?, ?> from)
  {
    return (next != null) && (bitIndex <= bitIndex) && (!next.isEmpty());
  }
  


  private static class Reference<E>
  {
    private E item;
    

    private Reference() {}
    

    public void set(E item)
    {
      this.item = item;
    }
    
    public E get() {
      return item;
    }
  }
  


  protected static class TrieEntry<K, V>
    extends AbstractBitwiseTrie.BasicEntry<K, V>
  {
    private static final long serialVersionUID = 4596023148184140013L;
    

    protected int bitIndex;
    

    protected TrieEntry<K, V> parent;
    

    protected TrieEntry<K, V> left;
    
    protected TrieEntry<K, V> right;
    
    protected TrieEntry<K, V> predecessor;
    

    public TrieEntry(K key, V value, int bitIndex)
    {
      super(value);
      
      this.bitIndex = bitIndex;
      
      parent = null;
      left = this;
      right = null;
      predecessor = this;
    }
    




    public boolean isEmpty()
    {
      return key == null;
    }
    


    public boolean isInternalNode()
    {
      return (left != this) && (right != this);
    }
    


    public boolean isExternalNode()
    {
      return !isInternalNode();
    }
    
    public String toString()
    {
      StringBuilder buffer = new StringBuilder();
      
      if (bitIndex == -1) {
        buffer.append("RootEntry(");
      } else {
        buffer.append("Entry(");
      }
      
      buffer.append("key=").append(getKey()).append(" [").append(bitIndex).append("], ");
      buffer.append("value=").append(getValue()).append(", ");
      

      if (parent != null) {
        if (parent.bitIndex == -1) {
          buffer.append("parent=").append("ROOT");
        } else {
          buffer.append("parent=").append(parent.getKey()).append(" [").append(parent.bitIndex).append("]");
        }
      } else {
        buffer.append("parent=").append("null");
      }
      buffer.append(", ");
      
      if (left != null) {
        if (left.bitIndex == -1) {
          buffer.append("left=").append("ROOT");
        } else {
          buffer.append("left=").append(left.getKey()).append(" [").append(left.bitIndex).append("]");
        }
      } else {
        buffer.append("left=").append("null");
      }
      buffer.append(", ");
      
      if (right != null) {
        if (right.bitIndex == -1) {
          buffer.append("right=").append("ROOT");
        } else {
          buffer.append("right=").append(right.getKey()).append(" [").append(right.bitIndex).append("]");
        }
      } else {
        buffer.append("right=").append("null");
      }
      buffer.append(", ");
      
      if (predecessor != null) {
        if (predecessor.bitIndex == -1) {
          buffer.append("predecessor=").append("ROOT");
        } else {
          buffer.append("predecessor=").append(predecessor.getKey()).append(" [").append(predecessor.bitIndex).append("]");
        }
      }
      

      buffer.append(")");
      return buffer.toString();
    }
  }
  

  private class EntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new EntryIterator(null);
    }
    
    public boolean contains(Object o)
    {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      
      AbstractPatriciaTrie.TrieEntry<K, V> candidate = getEntry(((Map.Entry)o).getKey());
      return (candidate != null) && (candidate.equals(o));
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
      remove(entry.getKey());
      return true;
    }
    
    public int size()
    {
      return AbstractPatriciaTrie.this.size();
    }
    
    public void clear()
    {
      AbstractPatriciaTrie.this.clear();
    }
    
    private class EntryIterator
      extends AbstractPatriciaTrie<K, V>.TrieIterator<Map.Entry<K, V>>
    {
      private EntryIterator() { super(); }
      
      public Map.Entry<K, V> next() { return nextEntry(); }
    }
  }
  

  private class KeySet
    extends AbstractSet<K>
  {
    private KeySet() {}
    
    public Iterator<K> iterator()
    {
      return new KeyIterator(null);
    }
    
    public int size()
    {
      return AbstractPatriciaTrie.this.size();
    }
    
    public boolean contains(Object o)
    {
      return containsKey(o);
    }
    
    public boolean remove(Object o)
    {
      int size = size();
      remove(o);
      return size != size();
    }
    
    public void clear()
    {
      AbstractPatriciaTrie.this.clear();
    }
    
    private class KeyIterator
      extends AbstractPatriciaTrie<K, V>.TrieIterator<K>
    {
      private KeyIterator() { super(); }
      
      public K next() { return nextEntry().getKey(); }
    }
  }
  

  private class Values
    extends AbstractCollection<V>
  {
    private Values() {}
    
    public Iterator<V> iterator()
    {
      return new ValueIterator(null);
    }
    
    public int size()
    {
      return AbstractPatriciaTrie.this.size();
    }
    
    public boolean contains(Object o)
    {
      return containsValue(o);
    }
    
    public void clear()
    {
      AbstractPatriciaTrie.this.clear();
    }
    
    public boolean remove(Object o)
    {
      for (Iterator<V> it = iterator(); it.hasNext();) {
        V value = it.next();
        if (AbstractBitwiseTrie.compare(value, o)) {
          it.remove();
          return true;
        }
      }
      return false;
    }
    
    private class ValueIterator
      extends AbstractPatriciaTrie<K, V>.TrieIterator<V>
    {
      private ValueIterator() { super(); }
      
      public V next() { return nextEntry().getValue(); }
    }
  }
  




  abstract class TrieIterator<E>
    implements Iterator<E>
  {
    protected int expectedModCount = modCount;
    
    protected AbstractPatriciaTrie.TrieEntry<K, V> next;
    
    protected AbstractPatriciaTrie.TrieEntry<K, V> current;
    

    protected TrieIterator()
    {
      next = nextEntry(null);
    }
    


    protected TrieIterator()
    {
      next = firstEntry;
    }
    


    protected AbstractPatriciaTrie.TrieEntry<K, V> nextEntry()
    {
      if (expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
      
      AbstractPatriciaTrie.TrieEntry<K, V> e = next;
      if (e == null) {
        throw new NoSuchElementException();
      }
      
      next = findNext(e);
      current = e;
      return e;
    }
    


    protected AbstractPatriciaTrie.TrieEntry<K, V> findNext(AbstractPatriciaTrie.TrieEntry<K, V> prior)
    {
      return nextEntry(prior);
    }
    
    public boolean hasNext() {
      return next != null;
    }
    
    public void remove() {
      if (current == null) {
        throw new IllegalStateException();
      }
      
      if (expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
      
      AbstractPatriciaTrie.TrieEntry<K, V> node = current;
      current = null;
      removeEntry(node);
      
      expectedModCount = modCount;
    }
  }
  
  private class TrieMapIterator extends AbstractPatriciaTrie<K, V>.TrieIterator<K> implements OrderedMapIterator<K, V> {
    protected AbstractPatriciaTrie.TrieEntry<K, V> previous;
    
    private TrieMapIterator() { super(); }
    

    public K next()
    {
      return nextEntry().getKey();
    }
    
    public K getKey() {
      if (current == null) {
        throw new IllegalStateException();
      }
      return current.getKey();
    }
    
    public V getValue() {
      if (current == null) {
        throw new IllegalStateException();
      }
      return current.getValue();
    }
    
    public V setValue(V value) {
      if (current == null) {
        throw new IllegalStateException();
      }
      return current.setValue(value);
    }
    
    public boolean hasPrevious() {
      return previous != null;
    }
    
    public K previous() {
      return previousEntry().getKey();
    }
    
    protected AbstractPatriciaTrie.TrieEntry<K, V> nextEntry()
    {
      AbstractPatriciaTrie.TrieEntry<K, V> nextEntry = super.nextEntry();
      previous = nextEntry;
      return nextEntry;
    }
    
    protected AbstractPatriciaTrie.TrieEntry<K, V> previousEntry() {
      if (expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
      
      AbstractPatriciaTrie.TrieEntry<K, V> e = previous;
      if (e == null) {
        throw new NoSuchElementException();
      }
      
      previous = previousEntry(e);
      next = current;
      current = e;
      return current;
    }
  }
  


  private abstract class RangeMap
    extends AbstractMap<K, V>
    implements SortedMap<K, V>
  {
    private volatile transient Set<Map.Entry<K, V>> entrySet;
    


    private RangeMap() {}
    


    protected abstract Set<Map.Entry<K, V>> createEntrySet();
    


    protected abstract K getFromKey();
    


    protected abstract boolean isFromInclusive();
    


    protected abstract K getToKey();
    


    protected abstract boolean isToInclusive();
    


    public Comparator<? super K> comparator()
    {
      return AbstractPatriciaTrie.this.comparator();
    }
    
    public boolean containsKey(Object key)
    {
      if (!inRange(castKey(key))) {
        return false;
      }
      
      return AbstractPatriciaTrie.this.containsKey(key);
    }
    
    public V remove(Object key)
    {
      if (!inRange(castKey(key))) {
        return null;
      }
      
      return AbstractPatriciaTrie.this.remove(key);
    }
    
    public V get(Object key)
    {
      if (!inRange(castKey(key))) {
        return null;
      }
      
      return AbstractPatriciaTrie.this.get(key);
    }
    
    public V put(K key, V value)
    {
      if (!inRange(key)) {
        throw new IllegalArgumentException("Key is out of range: " + key);
      }
      return AbstractPatriciaTrie.this.put(key, value);
    }
    
    public Set<Map.Entry<K, V>> entrySet()
    {
      if (entrySet == null) {
        entrySet = createEntrySet();
      }
      return entrySet;
    }
    
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      if (!inRange2(fromKey)) {
        throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
      }
      
      if (!inRange2(toKey)) {
        throw new IllegalArgumentException("ToKey is out of range: " + toKey);
      }
      
      return createRangeMap(fromKey, isFromInclusive(), toKey, isToInclusive());
    }
    
    public SortedMap<K, V> headMap(K toKey) {
      if (!inRange2(toKey)) {
        throw new IllegalArgumentException("ToKey is out of range: " + toKey);
      }
      return createRangeMap(getFromKey(), isFromInclusive(), toKey, isToInclusive());
    }
    
    public SortedMap<K, V> tailMap(K fromKey) {
      if (!inRange2(fromKey)) {
        throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
      }
      return createRangeMap(fromKey, isFromInclusive(), getToKey(), isToInclusive());
    }
    


    protected boolean inRange(K key)
    {
      K fromKey = getFromKey();
      K toKey = getToKey();
      
      return ((fromKey == null) || (inFromRange(key, false))) && ((toKey == null) || (inToRange(key, false)));
    }
    


    protected boolean inRange2(K key)
    {
      K fromKey = getFromKey();
      K toKey = getToKey();
      
      return ((fromKey == null) || (inFromRange(key, false))) && ((toKey == null) || (inToRange(key, true)));
    }
    


    protected boolean inFromRange(K key, boolean forceInclusive)
    {
      K fromKey = getFromKey();
      boolean fromInclusive = isFromInclusive();
      
      int ret = getKeyAnalyzer().compare(key, fromKey);
      if ((fromInclusive) || (forceInclusive)) {
        return ret >= 0;
      }
      return ret > 0;
    }
    


    protected boolean inToRange(K key, boolean forceInclusive)
    {
      K toKey = getToKey();
      boolean toInclusive = isToInclusive();
      
      int ret = getKeyAnalyzer().compare(key, toKey);
      if ((toInclusive) || (forceInclusive)) {
        return ret <= 0;
      }
      return ret < 0;
    }
    



    protected abstract SortedMap<K, V> createRangeMap(K paramK1, boolean paramBoolean1, K paramK2, boolean paramBoolean2);
  }
  



  private class RangeEntryMap
    extends AbstractPatriciaTrie<K, V>.RangeMap
  {
    private final K fromKey;
    


    private final K toKey;
    


    private final boolean fromInclusive;
    

    private final boolean toInclusive;
    


    protected RangeEntryMap(K fromKey)
    {
      this(fromKey, true, toKey, false);
    }
    


    protected RangeEntryMap(boolean fromKey, K fromInclusive, boolean toKey)
    {
      super(null);
      
      if ((fromKey == null) && (toKey == null)) {
        throw new IllegalArgumentException("must have a from or to!");
      }
      
      if ((fromKey != null) && (toKey != null) && (getKeyAnalyzer().compare(fromKey, toKey) > 0)) {
        throw new IllegalArgumentException("fromKey > toKey");
      }
      
      this.fromKey = fromKey;
      this.fromInclusive = fromInclusive;
      this.toKey = toKey;
      this.toInclusive = toInclusive;
    }
    
    public K firstKey() {
      Map.Entry<K, V> e = null;
      if (fromKey == null) {
        e = firstEntry();
      }
      else if (fromInclusive) {
        e = ceilingEntry(fromKey);
      } else {
        e = higherEntry(fromKey);
      }
      

      K first = e != null ? e.getKey() : null;
      if ((e == null) || ((toKey != null) && (!inToRange(first, false)))) {
        throw new NoSuchElementException();
      }
      return first;
    }
    
    public K lastKey() { Map.Entry<K, V> e;
      Map.Entry<K, V> e;
      if (toKey == null) {
        e = lastEntry();
      } else { Map.Entry<K, V> e;
        if (toInclusive) {
          e = floorEntry(toKey);
        } else {
          e = lowerEntry(toKey);
        }
      }
      
      K last = e != null ? e.getKey() : null;
      if ((e == null) || ((fromKey != null) && (!inFromRange(last, false)))) {
        throw new NoSuchElementException();
      }
      return last;
    }
    
    protected Set<Map.Entry<K, V>> createEntrySet()
    {
      return new AbstractPatriciaTrie.RangeEntrySet(AbstractPatriciaTrie.this, this);
    }
    
    public K getFromKey()
    {
      return fromKey;
    }
    
    public K getToKey()
    {
      return toKey;
    }
    
    public boolean isFromInclusive()
    {
      return fromInclusive;
    }
    
    public boolean isToInclusive()
    {
      return toInclusive;
    }
    

    protected SortedMap<K, V> createRangeMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
    {
      return new RangeEntryMap(AbstractPatriciaTrie.this, fromKey, fromInclusive, toKey, toInclusive);
    }
  }
  


  private class RangeEntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    private final AbstractPatriciaTrie<K, V>.RangeMap delegate;
    
    private transient int size = -1;
    

    private transient int expectedModCount;
    

    public RangeEntrySet()
    {
      if (delegate == null) {
        throw new NullPointerException("delegate");
      }
      
      this.delegate = delegate;
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      K fromKey = delegate.getFromKey();
      K toKey = delegate.getToKey();
      
      AbstractPatriciaTrie.TrieEntry<K, V> first = null;
      if (fromKey == null) {
        first = firstEntry();
      } else {
        first = ceilingEntry(fromKey);
      }
      
      AbstractPatriciaTrie.TrieEntry<K, V> last = null;
      if (toKey != null) {
        last = ceilingEntry(toKey);
      }
      
      return new EntryIterator(first, last, null);
    }
    
    public int size()
    {
      if ((size == -1) || (expectedModCount != modCount)) {
        size = 0;
        
        for (Iterator<?> it = iterator(); it.hasNext(); it.next()) {
          size += 1;
        }
        
        expectedModCount = modCount;
      }
      return size;
    }
    
    public boolean isEmpty()
    {
      return !iterator().hasNext();
    }
    

    public boolean contains(Object o)
    {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      
      Map.Entry<K, V> entry = (Map.Entry)o;
      K key = entry.getKey();
      if (!delegate.inRange(key)) {
        return false;
      }
      
      AbstractPatriciaTrie.TrieEntry<K, V> node = getEntry(key);
      return (node != null) && (AbstractBitwiseTrie.compare(node.getValue(), entry.getValue()));
    }
    

    public boolean remove(Object o)
    {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      
      Map.Entry<K, V> entry = (Map.Entry)o;
      K key = entry.getKey();
      if (!delegate.inRange(key)) {
        return false;
      }
      
      AbstractPatriciaTrie.TrieEntry<K, V> node = getEntry(key);
      if ((node != null) && (AbstractBitwiseTrie.compare(node.getValue(), entry.getValue()))) {
        removeEntry(node);
        return true;
      }
      return false;
    }
    


    private final class EntryIterator
      extends AbstractPatriciaTrie<K, V>.TrieIterator<Map.Entry<K, V>>
    {
      private final K excludedKey;
      


      private EntryIterator(AbstractPatriciaTrie.TrieEntry<K, V> first)
      {
        super(first);
        excludedKey = (last != null ? last.getKey() : null);
      }
      
      public boolean hasNext()
      {
        return (next != null) && (!AbstractBitwiseTrie.compare(next.key, excludedKey));
      }
      
      public Map.Entry<K, V> next() {
        if ((next == null) || (AbstractBitwiseTrie.compare(next.key, excludedKey))) {
          throw new NoSuchElementException();
        }
        return nextEntry();
      }
    }
  }
  


  private class PrefixRangeMap
    extends AbstractPatriciaTrie<K, V>.RangeMap
  {
    private final K prefix;
    
    private final int offsetInBits;
    
    private final int lengthInBits;
    
    private K fromKey = null;
    
    private K toKey = null;
    
    private transient int expectedModCount = 0;
    
    private int size = -1;
    

    private PrefixRangeMap(int prefix, int offsetInBits)
    {
      super(null);
      this.prefix = prefix;
      this.offsetInBits = offsetInBits;
      this.lengthInBits = lengthInBits;
    }
    






    private int fixup()
    {
      if ((size == -1) || (modCount != expectedModCount)) {
        Iterator<Map.Entry<K, V>> it = super.entrySet().iterator();
        size = 0;
        
        Map.Entry<K, V> entry = null;
        if (it.hasNext()) {
          entry = (Map.Entry)it.next();
          size = 1;
        }
        
        fromKey = (entry == null ? null : entry.getKey());
        if (fromKey != null) {
          AbstractPatriciaTrie.TrieEntry<K, V> prior = previousEntry((AbstractPatriciaTrie.TrieEntry)entry);
          fromKey = (prior == null ? null : prior.getKey());
        }
        
        toKey = fromKey;
        
        while (it.hasNext()) {
          size += 1;
          entry = (Map.Entry)it.next();
        }
        
        toKey = (entry == null ? null : entry.getKey());
        
        if (toKey != null) {
          entry = nextEntry((AbstractPatriciaTrie.TrieEntry)entry);
          toKey = (entry == null ? null : entry.getKey());
        }
        
        expectedModCount = modCount;
      }
      
      return size;
    }
    
    public K firstKey() {
      fixup();
      
      Map.Entry<K, V> e = null;
      if (fromKey == null) {
        e = firstEntry();
      } else {
        e = higherEntry(fromKey);
      }
      
      K first = e != null ? e.getKey() : null;
      if ((e == null) || (!getKeyAnalyzer().isPrefix(prefix, offsetInBits, lengthInBits, first))) {
        throw new NoSuchElementException();
      }
      
      return first;
    }
    
    public K lastKey() {
      fixup();
      
      Map.Entry<K, V> e = null;
      if (toKey == null) {
        e = lastEntry();
      } else {
        e = lowerEntry(toKey);
      }
      
      K last = e != null ? e.getKey() : null;
      if ((e == null) || (!getKeyAnalyzer().isPrefix(prefix, offsetInBits, lengthInBits, last))) {
        throw new NoSuchElementException();
      }
      
      return last;
    }
    



    protected boolean inRange(K key)
    {
      return getKeyAnalyzer().isPrefix(prefix, offsetInBits, lengthInBits, key);
    }
    



    protected boolean inRange2(K key)
    {
      return inRange(key);
    }
    



    protected boolean inFromRange(K key, boolean forceInclusive)
    {
      return getKeyAnalyzer().isPrefix(prefix, offsetInBits, lengthInBits, key);
    }
    



    protected boolean inToRange(K key, boolean forceInclusive)
    {
      return getKeyAnalyzer().isPrefix(prefix, offsetInBits, lengthInBits, key);
    }
    
    protected Set<Map.Entry<K, V>> createEntrySet()
    {
      return new AbstractPatriciaTrie.PrefixRangeEntrySet(AbstractPatriciaTrie.this, this);
    }
    
    public K getFromKey()
    {
      return fromKey;
    }
    
    public K getToKey()
    {
      return toKey;
    }
    
    public boolean isFromInclusive()
    {
      return false;
    }
    
    public boolean isToInclusive()
    {
      return false;
    }
    

    protected SortedMap<K, V> createRangeMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
    {
      return new AbstractPatriciaTrie.RangeEntryMap(AbstractPatriciaTrie.this, fromKey, fromInclusive, toKey, toInclusive);
    }
  }
  


  private final class PrefixRangeEntrySet
    extends AbstractPatriciaTrie<K, V>.RangeEntrySet
  {
    private final AbstractPatriciaTrie<K, V>.PrefixRangeMap delegate;
    
    private AbstractPatriciaTrie.TrieEntry<K, V> prefixStart;
    
    private int expectedModCount = 0;
    


    public PrefixRangeEntrySet()
    {
      super(delegate);
      this.delegate = delegate;
    }
    
    public int size()
    {
      return AbstractPatriciaTrie.PrefixRangeMap.access$1100(delegate);
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      if (modCount != expectedModCount) {
        prefixStart = subtree(AbstractPatriciaTrie.PrefixRangeMap.access$1200(delegate), AbstractPatriciaTrie.PrefixRangeMap.access$1300(delegate), AbstractPatriciaTrie.PrefixRangeMap.access$1400(delegate));
        expectedModCount = modCount;
      }
      
      if (prefixStart == null) {
        Set<Map.Entry<K, V>> empty = Collections.emptySet();
        return empty.iterator(); }
      if (AbstractPatriciaTrie.PrefixRangeMap.access$1400(delegate) > prefixStart.bitIndex) {
        return new SingletonIterator(prefixStart);
      }
      return new EntryIterator(prefixStart, AbstractPatriciaTrie.PrefixRangeMap.access$1200(delegate), AbstractPatriciaTrie.PrefixRangeMap.access$1300(delegate), AbstractPatriciaTrie.PrefixRangeMap.access$1400(delegate));
    }
    


    private final class SingletonIterator
      implements Iterator<Map.Entry<K, V>>
    {
      private final AbstractPatriciaTrie.TrieEntry<K, V> entry;
      

      private int hit = 0;
      
      public SingletonIterator() {
        this.entry = entry;
      }
      
      public boolean hasNext() {
        return hit == 0;
      }
      
      public Map.Entry<K, V> next() {
        if (hit != 0) {
          throw new NoSuchElementException();
        }
        
        hit += 1;
        return entry;
      }
      
      public void remove() {
        if (hit != 1) {
          throw new IllegalStateException();
        }
        
        hit += 1;
        removeEntry(entry);
      }
    }
    


    private final class EntryIterator
      extends AbstractPatriciaTrie<K, V>.TrieIterator<Map.Entry<K, V>>
    {
      private final K prefix;
      
      private final int offset;
      
      private final int lengthInBits;
      
      private boolean lastOne;
      
      private AbstractPatriciaTrie.TrieEntry<K, V> subtree;
      

      EntryIterator(K startScan, int prefix, int offset)
      {
        super();
        subtree = startScan;
        next = followLeft(startScan);
        this.prefix = prefix;
        this.offset = offset;
        this.lengthInBits = lengthInBits;
      }
      
      public Map.Entry<K, V> next() {
        Map.Entry<K, V> entry = nextEntry();
        if (lastOne) {
          next = null;
        }
        return entry;
      }
      
      protected AbstractPatriciaTrie.TrieEntry<K, V> findNext(AbstractPatriciaTrie.TrieEntry<K, V> prior)
      {
        return nextEntryInSubtree(prior, subtree);
      }
      


      public void remove()
      {
        boolean needsFixing = false;
        int bitIdx = subtree.bitIndex;
        if (current == subtree) {
          needsFixing = true;
        }
        
        super.remove();
        


        if ((bitIdx != subtree.bitIndex) || (needsFixing)) {
          subtree = subtree(prefix, offset, lengthInBits);
        }
        



        if (lengthInBits >= subtree.bitIndex) {
          lastOne = true;
        }
      }
    }
  }
  




  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    root = new TrieEntry(null, null, -1);
    int size = stream.readInt();
    for (int i = 0; i < size; i++) {
      K k = stream.readObject();
      V v = stream.readObject();
      put(k, v);
    }
  }
  

  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    stream.defaultWriteObject();
    stream.writeInt(size());
    for (Map.Entry<K, V> entry : entrySet()) {
      stream.writeObject(entry.getKey());
      stream.writeObject(entry.getValue());
    }
  }
}
