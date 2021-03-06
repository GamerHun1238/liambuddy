package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import org.apache.commons.collections4.list.UnmodifiableList;















































public class LinkedMap<K, V>
  extends AbstractLinkedMap<K, V>
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 9077234323521161066L;
  
  public LinkedMap()
  {
    super(16, 0.75F, 12);
  }
  





  public LinkedMap(int initialCapacity)
  {
    super(initialCapacity);
  }
  








  public LinkedMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  





  public LinkedMap(Map<? extends K, ? extends V> map)
  {
    super(map);
  }
  






  public LinkedMap<K, V> clone()
  {
    return (LinkedMap)super.clone();
  }
  

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    doWriteObject(out);
  }
  

  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    doReadObject(in);
  }
  







  public K get(int index)
  {
    return getEntry(index).getKey();
  }
  






  public V getValue(int index)
  {
    return getEntry(index).getValue();
  }
  





  public int indexOf(Object key)
  {
    key = convertKey(key);
    int i = 0;
    for (AbstractLinkedMap.LinkEntry<K, V> entry = header.after; entry != header; i++) {
      if (isEqualKey(key, key)) {
        return i;
      }
      entry = after;
    }
    


    return -1;
  }
  







  public V remove(int index)
  {
    return remove(get(index));
  }
  














  public List<K> asList()
  {
    return new LinkedMapList(this);
  }
  

  static class LinkedMapList<K>
    extends AbstractList<K>
  {
    private final LinkedMap<K, ?> parent;
    
    LinkedMapList(LinkedMap<K, ?> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return parent.size();
    }
    
    public K get(int index)
    {
      return parent.get(index);
    }
    
    public boolean contains(Object obj)
    {
      return parent.containsKey(obj);
    }
    
    public int indexOf(Object obj)
    {
      return parent.indexOf(obj);
    }
    
    public int lastIndexOf(Object obj)
    {
      return parent.indexOf(obj);
    }
    
    public boolean containsAll(Collection<?> coll)
    {
      return parent.keySet().containsAll(coll);
    }
    
    public K remove(int index)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object obj)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection<?> coll)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> coll)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public Object[] toArray()
    {
      return parent.keySet().toArray();
    }
    
    public <T> T[] toArray(T[] array)
    {
      return parent.keySet().toArray(array);
    }
    
    public Iterator<K> iterator()
    {
      return UnmodifiableIterator.unmodifiableIterator(parent.keySet().iterator());
    }
    
    public ListIterator<K> listIterator()
    {
      return UnmodifiableListIterator.umodifiableListIterator(super.listIterator());
    }
    
    public ListIterator<K> listIterator(int fromIndex)
    {
      return UnmodifiableListIterator.umodifiableListIterator(super.listIterator(fromIndex));
    }
    
    public List<K> subList(int fromIndexInclusive, int toIndexExclusive)
    {
      return UnmodifiableList.unmodifiableList(super.subList(fromIndexInclusive, toIndexExclusive));
    }
  }
}
