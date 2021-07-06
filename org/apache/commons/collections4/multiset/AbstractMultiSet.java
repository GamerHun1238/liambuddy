package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSet.Entry;
import org.apache.commons.collections4.Transformer;



































public abstract class AbstractMultiSet<E>
  extends AbstractCollection<E>
  implements MultiSet<E>
{
  private transient Set<E> uniqueSet;
  private transient Set<MultiSet.Entry<E>> entrySet;
  
  protected AbstractMultiSet() {}
  
  public int size()
  {
    int totalSize = 0;
    for (MultiSet.Entry<E> entry : entrySet()) {
      totalSize += entry.getCount();
    }
    return totalSize;
  }
  







  public int getCount(Object object)
  {
    for (MultiSet.Entry<E> entry : entrySet()) {
      E element = entry.getElement();
      if ((element == object) || ((element != null) && (element.equals(object))))
      {
        return entry.getCount();
      }
    }
    return 0;
  }
  
  public int setCount(E object, int count)
  {
    if (count < 0) {
      throw new IllegalArgumentException("Count must not be negative.");
    }
    
    int oldCount = getCount(object);
    if (oldCount < count) {
      add(object, count - oldCount);
    } else {
      remove(object, oldCount - count);
    }
    return oldCount;
  }
  







  public boolean contains(Object object)
  {
    return getCount(object) > 0;
  }
  







  public Iterator<E> iterator()
  {
    return new MultiSetIterator(this);
  }
  

  private static class MultiSetIterator<E>
    implements Iterator<E>
  {
    private final AbstractMultiSet<E> parent;
    
    private final Iterator<MultiSet.Entry<E>> entryIterator;
    
    private MultiSet.Entry<E> current;
    
    private int itemCount;
    
    private boolean canRemove;
    
    public MultiSetIterator(AbstractMultiSet<E> parent)
    {
      this.parent = parent;
      entryIterator = parent.entrySet().iterator();
      current = null;
      canRemove = false;
    }
    

    public boolean hasNext()
    {
      return (itemCount > 0) || (entryIterator.hasNext());
    }
    

    public E next()
    {
      if (itemCount == 0) {
        current = ((MultiSet.Entry)entryIterator.next());
        itemCount = current.getCount();
      }
      canRemove = true;
      itemCount -= 1;
      return current.getElement();
    }
    

    public void remove()
    {
      if (!canRemove) {
        throw new IllegalStateException();
      }
      int count = current.getCount();
      if (count > 1) {
        parent.remove(current.getElement());
      } else {
        entryIterator.remove();
      }
      canRemove = false;
    }
  }
  

  public boolean add(E object)
  {
    add(object, 1);
    return true;
  }
  
  public int add(E object, int occurrences)
  {
    throw new UnsupportedOperationException();
  }
  




  public void clear()
  {
    Iterator<MultiSet.Entry<E>> it = entrySet().iterator();
    while (it.hasNext()) {
      it.next();
      it.remove();
    }
  }
  
  public boolean remove(Object object)
  {
    return remove(object, 1) != 0;
  }
  
  public int remove(Object object, int occurrences)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection<?> coll)
  {
    boolean result = false;
    Iterator<?> i = coll.iterator();
    while (i.hasNext()) {
      Object obj = i.next();
      boolean changed = remove(obj, getCount(obj)) != 0;
      result = (result) || (changed);
    }
    return result;
  }
  






  public Set<E> uniqueSet()
  {
    if (uniqueSet == null) {
      uniqueSet = createUniqueSet();
    }
    return uniqueSet;
  }
  




  protected Set<E> createUniqueSet()
  {
    return new UniqueSet(this);
  }
  





  protected Iterator<E> createUniqueSetIterator()
  {
    Transformer<MultiSet.Entry<E>, E> transformer = new Transformer()
    {
      public E transform(MultiSet.Entry<E> entry) {
        return entry.getElement();
      }
    };
    return IteratorUtils.transformedIterator(entrySet().iterator(), transformer);
  }
  





  public Set<MultiSet.Entry<E>> entrySet()
  {
    if (entrySet == null) {
      entrySet = createEntrySet();
    }
    return entrySet;
  }
  




  protected Set<MultiSet.Entry<E>> createEntrySet()
  {
    return new EntrySet(this);
  }
  





  protected abstract int uniqueElements();
  





  protected abstract Iterator<MultiSet.Entry<E>> createEntrySetIterator();
  





  protected static class UniqueSet<E>
    extends AbstractSet<E>
  {
    protected final AbstractMultiSet<E> parent;
    




    protected UniqueSet(AbstractMultiSet<E> parent)
    {
      this.parent = parent;
    }
    
    public Iterator<E> iterator()
    {
      return parent.createUniqueSetIterator();
    }
    
    public boolean contains(Object key)
    {
      return parent.contains(key);
    }
    
    public boolean containsAll(Collection<?> coll)
    {
      return parent.containsAll(coll);
    }
    
    public boolean remove(Object key)
    {
      return parent.remove(key, parent.getCount(key)) != 0;
    }
    
    public int size()
    {
      return parent.uniqueElements();
    }
    
    public void clear()
    {
      parent.clear();
    }
  }
  




  protected static class EntrySet<E>
    extends AbstractSet<MultiSet.Entry<E>>
  {
    private final AbstractMultiSet<E> parent;
    



    protected EntrySet(AbstractMultiSet<E> parent)
    {
      this.parent = parent;
    }
    
    public int size()
    {
      return parent.uniqueElements();
    }
    
    public Iterator<MultiSet.Entry<E>> iterator()
    {
      return parent.createEntrySetIterator();
    }
    
    public boolean contains(Object obj)
    {
      if (!(obj instanceof MultiSet.Entry)) {
        return false;
      }
      MultiSet.Entry<?> entry = (MultiSet.Entry)obj;
      Object element = entry.getElement();
      return parent.getCount(element) == entry.getCount();
    }
    
    public boolean remove(Object obj)
    {
      if (!(obj instanceof MultiSet.Entry)) {
        return false;
      }
      MultiSet.Entry<?> entry = (MultiSet.Entry)obj;
      Object element = entry.getElement();
      if (parent.contains(element)) {
        int count = parent.getCount(element);
        if (entry.getCount() == count) {
          parent.remove(element, count);
          return true;
        }
      }
      return false;
    }
  }
  
  protected static abstract class AbstractEntry<E>
    implements MultiSet.Entry<E>
  {
    protected AbstractEntry() {}
    
    public boolean equals(Object object)
    {
      if ((object instanceof MultiSet.Entry)) {
        MultiSet.Entry<?> other = (MultiSet.Entry)object;
        E element = getElement();
        Object otherElement = other.getElement();
        
        return (getCount() == other.getCount()) && ((element == otherElement) || ((element != null) && (element.equals(otherElement))));
      }
      

      return false;
    }
    
    public int hashCode()
    {
      E element = getElement();
      return (element == null ? 0 : element.hashCode()) ^ getCount();
    }
    
    public String toString()
    {
      return String.format("%s:%d", new Object[] { getElement(), Integer.valueOf(getCount()) });
    }
  }
  





  protected void doWriteObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(entrySet().size());
    for (MultiSet.Entry<E> entry : entrySet()) {
      out.writeObject(entry.getElement());
      out.writeInt(entry.getCount());
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
      setCount(obj, count);
    }
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
    for (MultiSet.Entry<E> entry : entrySet()) {
      if (other.getCount(entry.getElement()) != getCount(entry.getElement())) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    return entrySet().hashCode();
  }
  





  public String toString()
  {
    return entrySet().toString();
  }
}
