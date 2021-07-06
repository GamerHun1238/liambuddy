package org.apache.commons.collections4.multiset;

import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSet.Entry;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;
















































public class PredicatedMultiSet<E>
  extends PredicatedCollection<E>
  implements MultiSet<E>
{
  private static final long serialVersionUID = 20150629L;
  
  public static <E> PredicatedMultiSet<E> predicatedMultiSet(MultiSet<E> multiset, Predicate<? super E> predicate)
  {
    return new PredicatedMultiSet(multiset, predicate);
  }
  











  protected PredicatedMultiSet(MultiSet<E> multiset, Predicate<? super E> predicate)
  {
    super(multiset, predicate);
  }
  





  protected MultiSet<E> decorated()
  {
    return (MultiSet)super.decorated();
  }
  
  public boolean equals(Object object)
  {
    return (object == this) || (decorated().equals(object));
  }
  
  public int hashCode()
  {
    return decorated().hashCode();
  }
  


  public int add(E object, int count)
  {
    validate(object);
    return decorated().add(object, count);
  }
  
  public int remove(Object object, int count)
  {
    return decorated().remove(object, count);
  }
  
  public int getCount(Object object)
  {
    return decorated().getCount(object);
  }
  
  public int setCount(E object, int count)
  {
    validate(object);
    return decorated().setCount(object, count);
  }
  
  public Set<E> uniqueSet()
  {
    return decorated().uniqueSet();
  }
  
  public Set<MultiSet.Entry<E>> entrySet()
  {
    return decorated().entrySet();
  }
}
