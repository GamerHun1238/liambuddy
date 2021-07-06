package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSet.Entry;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;


































public final class UnmodifiableMultiSet<E>
  extends AbstractMultiSetDecorator<E>
  implements Unmodifiable
{
  private static final long serialVersionUID = 20150611L;
  
  public static <E> MultiSet<E> unmodifiableMultiSet(MultiSet<? extends E> multiset)
  {
    if ((multiset instanceof Unmodifiable))
    {
      MultiSet<E> tmpMultiSet = multiset;
      return tmpMultiSet;
    }
    return new UnmodifiableMultiSet(multiset);
  }
  







  private UnmodifiableMultiSet(MultiSet<? extends E> multiset)
  {
    super(multiset);
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(decorated());
  }
  







  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    setCollection((Collection)in.readObject());
  }
  

  public Iterator<E> iterator()
  {
    return UnmodifiableIterator.unmodifiableIterator(decorated().iterator());
  }
  
  public boolean add(E object)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends E> coll)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(Object object)
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
  

  public int setCount(E object, int count)
  {
    throw new UnsupportedOperationException();
  }
  
  public int add(E object, int count)
  {
    throw new UnsupportedOperationException();
  }
  
  public int remove(Object object, int count)
  {
    throw new UnsupportedOperationException();
  }
  
  public Set<E> uniqueSet()
  {
    Set<E> set = decorated().uniqueSet();
    return UnmodifiableSet.unmodifiableSet(set);
  }
  
  public Set<MultiSet.Entry<E>> entrySet()
  {
    Set<MultiSet.Entry<E>> set = decorated().entrySet();
    return UnmodifiableSet.unmodifiableSet(set);
  }
}
