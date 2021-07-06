package org.apache.commons.collections4.map;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections4.set.AbstractSetDecorator;



































public final class UnmodifiableEntrySet<K, V>
  extends AbstractSetDecorator<Map.Entry<K, V>>
  implements Unmodifiable
{
  private static final long serialVersionUID = 1678353579659253473L;
  
  public static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(Set<Map.Entry<K, V>> set)
  {
    if ((set instanceof Unmodifiable)) {
      return set;
    }
    return new UnmodifiableEntrySet(set);
  }
  






  private UnmodifiableEntrySet(Set<Map.Entry<K, V>> set)
  {
    super(set);
  }
  

  public boolean add(Map.Entry<K, V> object)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection<? extends Map.Entry<K, V>> coll)
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
  

  public Iterator<Map.Entry<K, V>> iterator()
  {
    return new UnmodifiableEntrySetIterator(decorated().iterator());
  }
  

  public Object[] toArray()
  {
    Object[] array = decorated().toArray();
    for (int i = 0; i < array.length; i++) {
      array[i] = new UnmodifiableEntry((Map.Entry)array[i]);
    }
    return array;
  }
  

  public <T> T[] toArray(T[] array)
  {
    Object[] result = array;
    if (array.length > 0)
    {

      result = (Object[])Array.newInstance(array.getClass().getComponentType(), 0);
    }
    result = decorated().toArray(result);
    for (int i = 0; i < result.length; i++) {
      result[i] = new UnmodifiableEntry((Map.Entry)result[i]);
    }
    

    if (result.length > array.length) {
      return (Object[])result;
    }
    

    System.arraycopy(result, 0, array, 0, result.length);
    if (array.length > result.length) {
      array[result.length] = null;
    }
    return array;
  }
  


  private class UnmodifiableEntrySetIterator
    extends AbstractIteratorDecorator<Map.Entry<K, V>>
  {
    protected UnmodifiableEntrySetIterator()
    {
      super();
    }
    
    public Map.Entry<K, V> next()
    {
      return new UnmodifiableEntrySet.UnmodifiableEntry(UnmodifiableEntrySet.this, (Map.Entry)getIterator().next());
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  


  private class UnmodifiableEntry
    extends AbstractMapEntryDecorator<K, V>
  {
    protected UnmodifiableEntry()
    {
      super();
    }
    
    public V setValue(V obj)
    {
      throw new UnsupportedOperationException();
    }
  }
}
