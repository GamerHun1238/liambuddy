package org.apache.commons.collections4.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.list.UnmodifiableList;

































public class CompositeCollection<E>
  implements Collection<E>, Serializable
{
  private static final long serialVersionUID = 8417515734108306801L;
  private CollectionMutator<E> mutator;
  private final List<Collection<E>> all = new ArrayList();
  





  public CompositeCollection() {}
  





  public CompositeCollection(Collection<E> compositeCollection)
  {
    addComposited(compositeCollection);
  }
  







  public CompositeCollection(Collection<E> compositeCollection1, Collection<E> compositeCollection2)
  {
    addComposited(compositeCollection1, compositeCollection2);
  }
  





  public CompositeCollection(Collection<E>... compositeCollections)
  {
    addComposited(compositeCollections);
  }
  








  public int size()
  {
    int size = 0;
    for (Collection<E> item : all) {
      size += item.size();
    }
    return size;
  }
  







  public boolean isEmpty()
  {
    for (Collection<E> item : all) {
      if (!item.isEmpty()) {
        return false;
      }
    }
    return true;
  }
  








  public boolean contains(Object obj)
  {
    for (Collection<E> item : all) {
      if (item.contains(obj)) {
        return true;
      }
    }
    return false;
  }
  










  public Iterator<E> iterator()
  {
    if (all.isEmpty()) {
      return EmptyIterator.emptyIterator();
    }
    IteratorChain<E> chain = new IteratorChain();
    for (Collection<E> item : all) {
      chain.addIterator(item.iterator());
    }
    return chain;
  }
  





  public Object[] toArray()
  {
    Object[] result = new Object[size()];
    int i = 0;
    for (Iterator<E> it = iterator(); it.hasNext(); i++) {
      result[i] = it.next();
    }
    return result;
  }
  









  public <T> T[] toArray(T[] array)
  {
    int size = size();
    Object[] result = null;
    if (array.length >= size) {
      result = array;
    } else {
      result = (Object[])Array.newInstance(array.getClass().getComponentType(), size);
    }
    
    int offset = 0;
    for (Collection<E> item : all) {
      for (E e : item) {
        result[(offset++)] = e;
      }
    }
    if (result.length > size) {
      result[size] = null;
    }
    return (Object[])result;
  }
  












  public boolean add(E obj)
  {
    if (mutator == null) {
      throw new UnsupportedOperationException("add() is not supported on CompositeCollection without a CollectionMutator strategy");
    }
    
    return mutator.add(this, all, obj);
  }
  











  public boolean remove(Object obj)
  {
    if (mutator == null) {
      throw new UnsupportedOperationException("remove() is not supported on CompositeCollection without a CollectionMutator strategy");
    }
    
    return mutator.remove(this, all, obj);
  }
  









  public boolean containsAll(Collection<?> coll)
  {
    for (Object item : coll) {
      if (!contains(item)) {
        return false;
      }
    }
    return true;
  }
  












  public boolean addAll(Collection<? extends E> coll)
  {
    if (mutator == null) {
      throw new UnsupportedOperationException("addAll() is not supported on CompositeCollection without a CollectionMutator strategy");
    }
    
    return mutator.addAll(this, all, coll);
  }
  









  public boolean removeAll(Collection<?> coll)
  {
    if (coll.size() == 0) {
      return false;
    }
    boolean changed = false;
    for (Collection<E> item : all) {
      changed |= item.removeAll(coll);
    }
    return changed;
  }
  










  public boolean retainAll(Collection<?> coll)
  {
    boolean changed = false;
    for (Collection<E> item : all) {
      changed |= item.retainAll(coll);
    }
    return changed;
  }
  







  public void clear()
  {
    for (Collection<E> coll : all) {
      coll.clear();
    }
  }
  





  public void setMutator(CollectionMutator<E> mutator)
  {
    this.mutator = mutator;
  }
  




  public void addComposited(Collection<E> compositeCollection)
  {
    all.add(compositeCollection);
  }
  






  public void addComposited(Collection<E> compositeCollection1, Collection<E> compositeCollection2)
  {
    all.add(compositeCollection1);
    all.add(compositeCollection2);
  }
  




  public void addComposited(Collection<E>... compositeCollections)
  {
    all.addAll(Arrays.asList(compositeCollections));
  }
  




  public void removeComposited(Collection<E> coll)
  {
    all.remove(coll);
  }
  






  public Collection<E> toCollection()
  {
    return new ArrayList(this);
  }
  




  public List<Collection<E>> getCollections()
  {
    return UnmodifiableList.unmodifiableList(all);
  }
  



  protected CollectionMutator<E> getMutator()
  {
    return mutator;
  }
  
  public static abstract interface CollectionMutator<E>
    extends Serializable
  {
    public abstract boolean add(CompositeCollection<E> paramCompositeCollection, List<Collection<E>> paramList, E paramE);
    
    public abstract boolean addAll(CompositeCollection<E> paramCompositeCollection, List<Collection<E>> paramList, Collection<? extends E> paramCollection);
    
    public abstract boolean remove(CompositeCollection<E> paramCompositeCollection, List<Collection<E>> paramList, Object paramObject);
  }
}
