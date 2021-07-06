package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.set.UnmodifiableSet;











































public abstract class AbstractMapBag<E>
  implements Bag<E>
{
  private transient Map<E, MutableInteger> map;
  private int size;
  private transient int modCount;
  private transient Set<E> uniqueSet;
  
  protected AbstractMapBag() {}
  
  protected AbstractMapBag(Map<E, MutableInteger> map)
  {
    this.map = map;
  }
  





  protected Map<E, MutableInteger> getMap()
  {
    return map;
  }
  






  public int size()
  {
    return size;
  }
  





  public boolean isEmpty()
  {
    return map.isEmpty();
  }
  







  public int getCount(Object object)
  {
    MutableInteger count = (MutableInteger)map.get(object);
    if (count != null) {
      return value;
    }
    return 0;
  }
  








  public boolean contains(Object object)
  {
    return map.containsKey(object);
  }
  






  public boolean containsAll(Collection<?> coll)
  {
    if ((coll instanceof Bag)) {
      return containsAll((Bag)coll);
    }
    return containsAll(new HashBag(coll));
  }
  






  boolean containsAll(Bag<?> other)
  {
    Iterator<?> it = other.uniqueSet().iterator();
    while (it.hasNext()) {
      Object current = it.next();
      if (getCount(current) < other.getCount(current)) {
        return false;
      }
    }
    return true;
  }
  







  public Iterator<E> iterator()
  {
    return new BagIterator(this);
  }
  

  static class BagIterator<E>
    implements Iterator<E>
  {
    private final AbstractMapBag<E> parent;
    
    private final Iterator<Map.Entry<E, AbstractMapBag.MutableInteger>> entryIterator;
    
    private Map.Entry<E, AbstractMapBag.MutableInteger> current;
    
    private int itemCount;
    
    private final int mods;
    private boolean canRemove;
    
    public BagIterator(AbstractMapBag<E> parent)
    {
      this.parent = parent;
      entryIterator = map.entrySet().iterator();
      current = null;
      mods = modCount;
      canRemove = false;
    }
    

    public boolean hasNext()
    {
      return (itemCount > 0) || (entryIterator.hasNext());
    }
    

    public E next()
    {
      if (parent.modCount != mods) {
        throw new ConcurrentModificationException();
      }
      if (itemCount == 0) {
        current = ((Map.Entry)entryIterator.next());
        itemCount = current.getValue()).value;
      }
      canRemove = true;
      itemCount -= 1;
      return current.getKey();
    }
    

    public void remove()
    {
      if (parent.modCount != mods) {
        throw new ConcurrentModificationException();
      }
      if (!canRemove) {
        throw new IllegalStateException();
      }
      AbstractMapBag.MutableInteger mut = (AbstractMapBag.MutableInteger)current.getValue();
      if (value > 1) {
        value -= 1;
      } else {
        entryIterator.remove();
      }
      AbstractMapBag.access$210(parent);
      canRemove = false;
    }
  }
  







  public boolean add(E object)
  {
    return add(object, 1);
  }
  







  public boolean add(E object, int nCopies)
  {
    modCount += 1;
    if (nCopies > 0) {
      MutableInteger mut = (MutableInteger)map.get(object);
      size += nCopies;
      if (mut == null) {
        map.put(object, new MutableInteger(nCopies));
        return true;
      }
      value += nCopies;
      return false;
    }
    return false;
  }
  






  public boolean addAll(Collection<? extends E> coll)
  {
    boolean changed = false;
    Iterator<? extends E> i = coll.iterator();
    while (i.hasNext()) {
      boolean added = add(i.next());
      changed = (changed) || (added);
    }
    return changed;
  }
  




  public void clear()
  {
    modCount += 1;
    map.clear();
    size = 0;
  }
  






  public boolean remove(Object object)
  {
    MutableInteger mut = (MutableInteger)map.get(object);
    if (mut == null) {
      return false;
    }
    modCount += 1;
    map.remove(object);
    size -= value;
    return true;
  }
  







  public boolean remove(Object object, int nCopies)
  {
    MutableInteger mut = (MutableInteger)map.get(object);
    if (mut == null) {
      return false;
    }
    if (nCopies <= 0) {
      return false;
    }
    modCount += 1;
    if (nCopies < value) {
      value -= nCopies;
      size -= nCopies;
    } else {
      map.remove(object);
      size -= value;
    }
    return true;
  }
  







  public boolean removeAll(Collection<?> coll)
  {
    boolean result = false;
    if (coll != null) {
      Iterator<?> i = coll.iterator();
      while (i.hasNext()) {
        boolean changed = remove(i.next(), 1);
        result = (result) || (changed);
      }
    }
    return result;
  }
  







  public boolean retainAll(Collection<?> coll)
  {
    if ((coll instanceof Bag)) {
      return retainAll((Bag)coll);
    }
    return retainAll(new HashBag(coll));
  }
  







  boolean retainAll(Bag<?> other)
  {
    boolean result = false;
    Bag<E> excess = new HashBag();
    Iterator<E> i = uniqueSet().iterator();
    while (i.hasNext()) {
      E current = i.next();
      int myCount = getCount(current);
      int otherCount = other.getCount(current);
      if ((1 <= otherCount) && (otherCount <= myCount)) {
        excess.add(current, myCount - otherCount);
      } else {
        excess.add(current, myCount);
      }
    }
    if (!excess.isEmpty()) {
      result = removeAll(excess);
    }
    return result;
  }
  




  protected static class MutableInteger
  {
    protected int value;
    



    MutableInteger(int value)
    {
      this.value = value;
    }
    
    public boolean equals(Object obj)
    {
      if (!(obj instanceof MutableInteger)) {
        return false;
      }
      return value == value;
    }
    
    public int hashCode()
    {
      return value;
    }
  }
  






  public Object[] toArray()
  {
    Object[] result = new Object[size()];
    int i = 0;
    Iterator<E> it = map.keySet().iterator();
    while (it.hasNext()) {
      E current = it.next();
      for (int index = getCount(current); index > 0; index--) {
        result[(i++)] = current;
      }
    }
    return result;
  }
  












  public <T> T[] toArray(T[] array)
  {
    int size = size();
    if (array.length < size)
    {
      T[] unchecked = (Object[])Array.newInstance(array.getClass().getComponentType(), size);
      array = unchecked;
    }
    
    int i = 0;
    Iterator<E> it = map.keySet().iterator();
    while (it.hasNext()) {
      E current = it.next();
      for (int index = getCount(current); index > 0; index--)
      {

        T unchecked = current;
        array[(i++)] = unchecked;
      }
    }
    while (i < array.length) {
      array[(i++)] = null;
    }
    return array;
  }
  





  public Set<E> uniqueSet()
  {
    if (uniqueSet == null) {
      uniqueSet = UnmodifiableSet.unmodifiableSet(map.keySet());
    }
    return uniqueSet;
  }
  




  protected void doWriteObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(map.size());
    for (Map.Entry<E, MutableInteger> entry : map.entrySet()) {
      out.writeObject(entry.getKey());
      out.writeInt(getValuevalue);
    }
  }
  







  protected void doReadObject(Map<E, MutableInteger> map, ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    this.map = map;
    int entrySize = in.readInt();
    for (int i = 0; i < entrySize; i++)
    {
      E obj = in.readObject();
      int count = in.readInt();
      map.put(obj, new MutableInteger(count));
      size += count;
    }
  }
  








  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    if (!(object instanceof Bag)) {
      return false;
    }
    Bag<?> other = (Bag)object;
    if (other.size() != size()) {
      return false;
    }
    for (E element : map.keySet()) {
      if (other.getCount(element) != getCount(element)) {
        return false;
      }
    }
    return true;
  }
  









  public int hashCode()
  {
    int total = 0;
    for (Map.Entry<E, MutableInteger> entry : map.entrySet()) {
      E element = entry.getKey();
      MutableInteger count = (MutableInteger)entry.getValue();
      total += ((element == null ? 0 : element.hashCode()) ^ value);
    }
    return total;
  }
  





  public String toString()
  {
    if (size() == 0) {
      return "[]";
    }
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    Iterator<E> it = uniqueSet().iterator();
    while (it.hasNext()) {
      Object current = it.next();
      int count = getCount(current);
      buf.append(count);
      buf.append(':');
      buf.append(current);
      if (it.hasNext()) {
        buf.append(',');
      }
    }
    buf.append(']');
    return buf.toString();
  }
}
