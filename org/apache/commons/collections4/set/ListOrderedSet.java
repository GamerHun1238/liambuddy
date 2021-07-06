package org.apache.commons.collections4.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.list.UnmodifiableList;























































public class ListOrderedSet<E>
  extends AbstractSerializableSetDecorator<E>
{
  private static final long serialVersionUID = -228664372470420141L;
  private final List<E> setOrder;
  
  public static <E> ListOrderedSet<E> listOrderedSet(Set<E> set, List<E> list)
  {
    if (set == null) {
      throw new NullPointerException("Set must not be null");
    }
    if (list == null) {
      throw new NullPointerException("List must not be null");
    }
    if ((set.size() > 0) || (list.size() > 0)) {
      throw new IllegalArgumentException("Set and List must be empty");
    }
    return new ListOrderedSet(set, list);
  }
  










  public static <E> ListOrderedSet<E> listOrderedSet(Set<E> set)
  {
    return new ListOrderedSet(set);
  }
  













  public static <E> ListOrderedSet<E> listOrderedSet(List<E> list)
  {
    if (list == null) {
      throw new NullPointerException("List must not be null");
    }
    CollectionUtils.filter(list, UniquePredicate.uniquePredicate());
    Set<E> set = new HashSet(list);
    
    return new ListOrderedSet(set, list);
  }
  






  public ListOrderedSet()
  {
    super(new HashSet());
    setOrder = new ArrayList();
  }
  





  protected ListOrderedSet(Set<E> set)
  {
    super(set);
    setOrder = new ArrayList(set);
  }
  









  protected ListOrderedSet(Set<E> set, List<E> list)
  {
    super(set);
    if (list == null) {
      throw new NullPointerException("List must not be null");
    }
    setOrder = list;
  }
  





  public List<E> asList()
  {
    return UnmodifiableList.unmodifiableList(setOrder);
  }
  

  public void clear()
  {
    decorated().clear();
    setOrder.clear();
  }
  
  public OrderedIterator<E> iterator()
  {
    return new OrderedSetIterator(setOrder.listIterator(), decorated(), null);
  }
  
  public boolean add(E object)
  {
    if (decorated().add(object)) {
      setOrder.add(object);
      return true;
    }
    return false;
  }
  
  public boolean addAll(Collection<? extends E> coll)
  {
    boolean result = false;
    for (E e : coll) {
      result |= add(e);
    }
    return result;
  }
  
  public boolean remove(Object object)
  {
    boolean result = decorated().remove(object);
    if (result) {
      setOrder.remove(object);
    }
    return result;
  }
  
  public boolean removeAll(Collection<?> coll)
  {
    boolean result = false;
    for (Object name : coll) {
      result |= remove(name);
    }
    return result;
  }
  









  public boolean retainAll(Collection<?> coll)
  {
    boolean result = decorated().retainAll(coll);
    if (!result)
      return false;
    Iterator<E> it;
    if (decorated().size() == 0) {
      setOrder.clear();
    } else {
      for (it = setOrder.iterator(); it.hasNext();) {
        if (!decorated().contains(it.next())) {
          it.remove();
        }
      }
    }
    return result;
  }
  
  public Object[] toArray()
  {
    return setOrder.toArray();
  }
  
  public <T> T[] toArray(T[] a)
  {
    return setOrder.toArray(a);
  }
  










  public E get(int index)
  {
    return setOrder.get(index);
  }
  








  public int indexOf(Object object)
  {
    return setOrder.indexOf(object);
  }
  








  public void add(int index, E object)
  {
    if (!contains(object)) {
      decorated().add(object);
      setOrder.add(index, object);
    }
  }
  










  public boolean addAll(int index, Collection<? extends E> coll)
  {
    boolean changed = false;
    
    List<E> toAdd = new ArrayList();
    for (E e : coll) {
      if (!contains(e))
      {

        decorated().add(e);
        toAdd.add(e);
        changed = true;
      }
    }
    if (changed) {
      setOrder.addAll(index, toAdd);
    }
    
    return changed;
  }
  







  public E remove(int index)
  {
    E obj = setOrder.remove(index);
    remove(obj);
    return obj;
  }
  








  public String toString()
  {
    return setOrder.toString();
  }
  


  static class OrderedSetIterator<E>
    extends AbstractIteratorDecorator<E>
    implements OrderedIterator<E>
  {
    private final Collection<E> set;
    

    private E last;
    


    private OrderedSetIterator(ListIterator<E> iterator, Collection<E> set)
    {
      super();
      this.set = set;
    }
    
    public E next()
    {
      last = getIterator().next();
      return last;
    }
    
    public void remove()
    {
      set.remove(last);
      getIterator().remove();
      last = null;
    }
    
    public boolean hasPrevious() {
      return ((ListIterator)getIterator()).hasPrevious();
    }
    
    public E previous() {
      last = ((ListIterator)getIterator()).previous();
      return last;
    }
  }
}
