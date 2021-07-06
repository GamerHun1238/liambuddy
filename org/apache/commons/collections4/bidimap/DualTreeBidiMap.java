package org.apache.commons.collections4.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.map.AbstractSortedMapDecorator;






































public class DualTreeBidiMap<K, V>
  extends AbstractDualBidiMap<K, V>
  implements SortedBidiMap<K, V>, Serializable
{
  private static final long serialVersionUID = 721969328361809L;
  private final Comparator<? super K> comparator;
  private final Comparator<? super V> valueComparator;
  
  public DualTreeBidiMap()
  {
    super(new TreeMap(), new TreeMap());
    comparator = null;
    valueComparator = null;
  }
  





  public DualTreeBidiMap(Map<? extends K, ? extends V> map)
  {
    super(new TreeMap(), new TreeMap());
    putAll(map);
    comparator = null;
    valueComparator = null;
  }
  





  public DualTreeBidiMap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator)
  {
    super(new TreeMap(keyComparator), new TreeMap(valueComparator));
    comparator = keyComparator;
    this.valueComparator = valueComparator;
  }
  







  protected DualTreeBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap)
  {
    super(normalMap, reverseMap, inverseBidiMap);
    comparator = ((SortedMap)normalMap).comparator();
    valueComparator = ((SortedMap)reverseMap).comparator();
  }
  









  protected DualTreeBidiMap<V, K> createBidiMap(Map<V, K> normalMap, Map<K, V> reverseMap, BidiMap<K, V> inverseMap)
  {
    return new DualTreeBidiMap(normalMap, reverseMap, inverseMap);
  }
  


  public Comparator<? super K> comparator()
  {
    return ((SortedMap)normalMap).comparator();
  }
  
  public Comparator<? super V> valueComparator()
  {
    return ((SortedMap)reverseMap).comparator();
  }
  
  public K firstKey()
  {
    return ((SortedMap)normalMap).firstKey();
  }
  
  public K lastKey()
  {
    return ((SortedMap)normalMap).lastKey();
  }
  
  public K nextKey(K key)
  {
    if (isEmpty()) {
      return null;
    }
    if ((normalMap instanceof OrderedMap)) {
      return ((OrderedMap)normalMap).nextKey(key);
    }
    SortedMap<K, V> sm = (SortedMap)normalMap;
    Iterator<K> it = sm.tailMap(key).keySet().iterator();
    it.next();
    if (it.hasNext()) {
      return it.next();
    }
    return null;
  }
  
  public K previousKey(K key)
  {
    if (isEmpty()) {
      return null;
    }
    if ((normalMap instanceof OrderedMap)) {
      return ((OrderedMap)normalMap).previousKey(key);
    }
    SortedMap<K, V> sm = (SortedMap)normalMap;
    SortedMap<K, V> hm = sm.headMap(key);
    if (hm.isEmpty()) {
      return null;
    }
    return hm.lastKey();
  }
  









  public OrderedMapIterator<K, V> mapIterator()
  {
    return new BidiOrderedMapIterator(this);
  }
  
  public SortedBidiMap<V, K> inverseSortedBidiMap() {
    return inverseBidiMap();
  }
  
  public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
    return inverseBidiMap();
  }
  


  public SortedMap<K, V> headMap(K toKey)
  {
    SortedMap<K, V> sub = ((SortedMap)normalMap).headMap(toKey);
    return new ViewMap(this, sub);
  }
  
  public SortedMap<K, V> tailMap(K fromKey)
  {
    SortedMap<K, V> sub = ((SortedMap)normalMap).tailMap(fromKey);
    return new ViewMap(this, sub);
  }
  
  public SortedMap<K, V> subMap(K fromKey, K toKey)
  {
    SortedMap<K, V> sub = ((SortedMap)normalMap).subMap(fromKey, toKey);
    return new ViewMap(this, sub);
  }
  
  public SortedBidiMap<V, K> inverseBidiMap()
  {
    return (SortedBidiMap)super.inverseBidiMap();
  }
  









  protected static class ViewMap<K, V>
    extends AbstractSortedMapDecorator<K, V>
  {
    protected ViewMap(DualTreeBidiMap<K, V> bidi, SortedMap<K, V> sm)
    {
      super();
    }
    

    public boolean containsValue(Object value)
    {
      return decoratednormalMap.containsValue(value);
    }
    

    public void clear()
    {
      for (Iterator<K> it = keySet().iterator(); it.hasNext();) {
        it.next();
        it.remove();
      }
    }
    
    public SortedMap<K, V> headMap(K toKey)
    {
      return new ViewMap(decorated(), super.headMap(toKey));
    }
    
    public SortedMap<K, V> tailMap(K fromKey)
    {
      return new ViewMap(decorated(), super.tailMap(fromKey));
    }
    
    public SortedMap<K, V> subMap(K fromKey, K toKey)
    {
      return new ViewMap(decorated(), super.subMap(fromKey, toKey));
    }
    
    protected DualTreeBidiMap<K, V> decorated()
    {
      return (DualTreeBidiMap)super.decorated();
    }
    
    public K previousKey(K key)
    {
      return decorated().previousKey(key);
    }
    
    public K nextKey(K key)
    {
      return decorated().nextKey(key);
    }
  }
  



  protected static class BidiOrderedMapIterator<K, V>
    implements OrderedMapIterator<K, V>, ResettableIterator<K>
  {
    private final AbstractDualBidiMap<K, V> parent;
    


    private ListIterator<Map.Entry<K, V>> iterator;
    

    private Map.Entry<K, V> last = null;
    




    protected BidiOrderedMapIterator(AbstractDualBidiMap<K, V> parent)
    {
      this.parent = parent;
      iterator = new ArrayList(parent.entrySet()).listIterator();
    }
    
    public boolean hasNext()
    {
      return iterator.hasNext();
    }
    
    public K next()
    {
      last = ((Map.Entry)iterator.next());
      return last.getKey();
    }
    
    public boolean hasPrevious()
    {
      return iterator.hasPrevious();
    }
    
    public K previous()
    {
      last = ((Map.Entry)iterator.previous());
      return last.getKey();
    }
    
    public void remove()
    {
      iterator.remove();
      parent.remove(last.getKey());
      last = null;
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
      
      V oldValue = parent.put(last.getKey(), value);
      


      last.setValue(value);
      return oldValue;
    }
    
    public void reset()
    {
      iterator = new ArrayList(parent.entrySet()).listIterator();
      last = null;
    }
    
    public String toString()
    {
      if (last != null) {
        return "MapIterator[" + getKey() + "=" + getValue() + "]";
      }
      return "MapIterator[]";
    }
  }
  
  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(normalMap);
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    normalMap = new TreeMap(comparator);
    reverseMap = new TreeMap(valueComparator);
    
    Map<K, V> map = (Map)in.readObject();
    putAll(map);
  }
}
