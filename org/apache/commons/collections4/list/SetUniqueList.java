package org.apache.commons.collections4.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections4.set.UnmodifiableSet;


















































public class SetUniqueList<E>
  extends AbstractSerializableListDecorator<E>
{
  private static final long serialVersionUID = 7196982186153478694L;
  private final Set<E> set;
  
  public static <E> SetUniqueList<E> setUniqueList(List<E> list)
  {
    if (list == null) {
      throw new NullPointerException("List must not be null");
    }
    if (list.isEmpty()) {
      return new SetUniqueList(list, new HashSet());
    }
    List<E> temp = new ArrayList(list);
    list.clear();
    SetUniqueList<E> sl = new SetUniqueList(list, new HashSet());
    sl.addAll(temp);
    return sl;
  }
  









  protected SetUniqueList(List<E> list, Set<E> set)
  {
    super(list);
    if (set == null) {
      throw new NullPointerException("Set must not be null");
    }
    this.set = set;
  }
  





  public Set<E> asSet()
  {
    return UnmodifiableSet.unmodifiableSet(set);
  }
  












  public boolean add(E object)
  {
    int sizeBefore = size();
    

    add(size(), object);
    

    return sizeBefore != size();
  }
  












  public void add(int index, E object)
  {
    if (!set.contains(object)) {
      super.add(index, object);
      set.add(object);
    }
  }
  













  public boolean addAll(Collection<? extends E> coll)
  {
    return addAll(size(), coll);
  }
  















  public boolean addAll(int index, Collection<? extends E> coll)
  {
    List<E> temp = new ArrayList();
    for (E e : coll) {
      if (set.add(e)) {
        temp.add(e);
      }
    }
    return super.addAll(index, temp);
  }
  












  public E set(int index, E object)
  {
    int pos = indexOf(object);
    E removed = super.set(index, object);
    
    if ((pos != -1) && (pos != index))
    {

      super.remove(pos);
    }
    
    set.remove(removed);
    set.add(object);
    
    return removed;
  }
  
  public boolean remove(Object object)
  {
    boolean result = set.remove(object);
    if (result) {
      super.remove(object);
    }
    return result;
  }
  
  public E remove(int index)
  {
    E result = super.remove(index);
    set.remove(result);
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
    boolean result = set.retainAll(coll);
    if (!result) {
      return false;
    }
    if (set.size() == 0) {
      super.clear();
    }
    else {
      super.retainAll(set);
    }
    return result;
  }
  
  public void clear()
  {
    super.clear();
    set.clear();
  }
  
  public boolean contains(Object object)
  {
    return set.contains(object);
  }
  
  public boolean containsAll(Collection<?> coll)
  {
    return set.containsAll(coll);
  }
  
  public Iterator<E> iterator()
  {
    return new SetListIterator(super.iterator(), set);
  }
  
  public ListIterator<E> listIterator()
  {
    return new SetListListIterator(super.listIterator(), set);
  }
  
  public ListIterator<E> listIterator(int index)
  {
    return new SetListListIterator(super.listIterator(index), set);
  }
  






  public List<E> subList(int fromIndex, int toIndex)
  {
    List<E> superSubList = super.subList(fromIndex, toIndex);
    Set<E> subSet = createSetBasedOnList(set, superSubList);
    return ListUtils.unmodifiableList(new SetUniqueList(superSubList, subSet));
  }
  



  protected Set<E> createSetBasedOnList(Set<E> set, List<E> list)
  {
    Set<E> subSet;
    


    Set<E> subSet;
    

    if (set.getClass().equals(HashSet.class)) {
      subSet = new HashSet(list.size());
    } else {
      try {
        subSet = (Set)set.getClass().newInstance();
      } catch (InstantiationException ie) {
        subSet = new HashSet();
      } catch (IllegalAccessException iae) {
        subSet = new HashSet();
      }
    }
    subSet.addAll(list);
    return subSet;
  }
  


  static class SetListIterator<E>
    extends AbstractIteratorDecorator<E>
  {
    private final Set<E> set;
    
    private E last = null;
    
    protected SetListIterator(Iterator<E> it, Set<E> set) {
      super();
      this.set = set;
    }
    
    public E next()
    {
      last = super.next();
      return last;
    }
    
    public void remove()
    {
      super.remove();
      set.remove(last);
      last = null;
    }
  }
  


  static class SetListListIterator<E>
    extends AbstractListIteratorDecorator<E>
  {
    private final Set<E> set;
    
    private E last = null;
    
    protected SetListListIterator(ListIterator<E> it, Set<E> set) {
      super();
      this.set = set;
    }
    
    public E next()
    {
      last = super.next();
      return last;
    }
    
    public E previous()
    {
      last = super.previous();
      return last;
    }
    
    public void remove()
    {
      super.remove();
      set.remove(last);
      last = null;
    }
    
    public void add(E object)
    {
      if (!set.contains(object)) {
        super.add(object);
        set.add(object);
      }
    }
    
    public void set(E object)
    {
      throw new UnsupportedOperationException("ListIterator does not support set");
    }
  }
}
