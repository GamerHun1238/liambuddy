package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSet.Entry;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;







































public abstract class AbstractMapMultiSet<E>
  extends AbstractMultiSet<E>
{
  private transient Map<E, MutableInteger> map;
  private transient int size;
  private transient int modCount;
  
  protected AbstractMapMultiSet() {}
  
  protected AbstractMapMultiSet(Map<E, MutableInteger> map)
  {
    this.map = map;
  }
  





  protected Map<E, MutableInteger> getMap()
  {
    return map;
  }
  






  protected void setMap(Map<E, MutableInteger> map)
  {
    this.map = map;
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
  







  public Iterator<E> iterator()
  {
    return new MapBasedMultiSetIterator(this);
  }
  

  private static class MapBasedMultiSetIterator<E>
    implements Iterator<E>
  {
    private final AbstractMapMultiSet<E> parent;
    
    private final Iterator<Map.Entry<E, AbstractMapMultiSet.MutableInteger>> entryIterator;
    
    private Map.Entry<E, AbstractMapMultiSet.MutableInteger> current;
    
    private int itemCount;
    
    private final int mods;
    private boolean canRemove;
    
    public MapBasedMultiSetIterator(AbstractMapMultiSet<E> parent)
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
      AbstractMapMultiSet.MutableInteger mut = (AbstractMapMultiSet.MutableInteger)current.getValue();
      if (value > 1) {
        value -= 1;
      } else {
        entryIterator.remove();
      }
      AbstractMapMultiSet.access$210(parent);
      canRemove = false;
    }
  }
  

  public int add(E object, int occurrences)
  {
    if (occurrences < 0) {
      throw new IllegalArgumentException("Occurrences must not be negative.");
    }
    
    MutableInteger mut = (MutableInteger)map.get(object);
    int oldCount = mut != null ? value : 0;
    
    if (occurrences > 0) {
      modCount += 1;
      size += occurrences;
      if (mut == null) {
        map.put(object, new MutableInteger(occurrences));
      } else {
        value += occurrences;
      }
    }
    return oldCount;
  }
  




  public void clear()
  {
    modCount += 1;
    map.clear();
    size = 0;
  }
  
  public int remove(Object object, int occurrences)
  {
    if (occurrences < 0) {
      throw new IllegalArgumentException("Occurrences must not be negative.");
    }
    
    MutableInteger mut = (MutableInteger)map.get(object);
    if (mut == null) {
      return 0;
    }
    int oldCount = value;
    if (occurrences > 0) {
      modCount += 1;
      if (occurrences < value) {
        value -= occurrences;
        size -= occurrences;
      } else {
        map.remove(object);
        size -= value;
      }
    }
    return oldCount;
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
  

  protected Iterator<E> createUniqueSetIterator()
  {
    return new UniqueSetIterator(getMap().keySet().iterator(), this);
  }
  
  protected int uniqueElements()
  {
    return map.size();
  }
  
  protected Iterator<MultiSet.Entry<E>> createEntrySetIterator()
  {
    return new EntrySetIterator(map.entrySet().iterator(), this);
  }
  



  protected static class UniqueSetIterator<E>
    extends AbstractIteratorDecorator<E>
  {
    protected final AbstractMapMultiSet<E> parent;
    


    protected E lastElement = null;
    

    protected boolean canRemove = false;
    




    protected UniqueSetIterator(Iterator<E> iterator, AbstractMapMultiSet<E> parent)
    {
      super();
      this.parent = parent;
    }
    
    public E next()
    {
      lastElement = super.next();
      canRemove = true;
      return lastElement;
    }
    
    public void remove()
    {
      if (!canRemove) {
        throw new IllegalStateException("Iterator remove() can only be called once after next()");
      }
      int count = parent.getCount(lastElement);
      super.remove();
      parent.remove(lastElement, count);
      lastElement = null;
      canRemove = false;
    }
  }
  


  protected static class EntrySetIterator<E>
    implements Iterator<MultiSet.Entry<E>>
  {
    protected final AbstractMapMultiSet<E> parent;
    

    protected final Iterator<Map.Entry<E, AbstractMapMultiSet.MutableInteger>> decorated;
    

    protected MultiSet.Entry<E> last = null;
    

    protected boolean canRemove = false;
    





    protected EntrySetIterator(Iterator<Map.Entry<E, AbstractMapMultiSet.MutableInteger>> iterator, AbstractMapMultiSet<E> parent)
    {
      decorated = iterator;
      this.parent = parent;
    }
    
    public boolean hasNext()
    {
      return decorated.hasNext();
    }
    
    public MultiSet.Entry<E> next()
    {
      last = new AbstractMapMultiSet.MultiSetEntry((Map.Entry)decorated.next());
      canRemove = true;
      return last;
    }
    
    public void remove()
    {
      if (!canRemove) {
        throw new IllegalStateException("Iterator remove() can only be called once after next()");
      }
      decorated.remove();
      last = null;
      canRemove = false;
    }
  }
  



  protected static class MultiSetEntry<E>
    extends AbstractMultiSet.AbstractEntry<E>
  {
    protected final Map.Entry<E, AbstractMapMultiSet.MutableInteger> parentEntry;
    


    protected MultiSetEntry(Map.Entry<E, AbstractMapMultiSet.MutableInteger> parentEntry)
    {
      this.parentEntry = parentEntry;
    }
    
    public E getElement()
    {
      return parentEntry.getKey();
    }
    
    public int getCount()
    {
      return parentEntry.getValue()).value;
    }
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
  







  protected void doReadObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    int entrySize = in.readInt();
    for (int i = 0; i < entrySize; i++)
    {
      E obj = in.readObject();
      int count = in.readInt();
      map.put(obj, new MutableInteger(count));
      size += count;
    }
  }
  






  public Object[] toArray()
  {
    Object[] result = new Object[size()];
    int i = 0;
    for (Map.Entry<E, MutableInteger> entry : map.entrySet()) {
      E current = entry.getKey();
      MutableInteger count = (MutableInteger)entry.getValue();
      for (int index = value; index > 0; index--) {
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
    for (Map.Entry<E, MutableInteger> entry : map.entrySet()) {
      E current = entry.getKey();
      MutableInteger count = (MutableInteger)entry.getValue();
      for (int index = value; index > 0; index--)
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
  

  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    if (!(object instanceof MultiSet)) {
      return false;
    }
    MultiSet<?> other = (MultiSet)object;
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
}
