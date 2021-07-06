package org.apache.commons.collections4.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;














































public class SynchronizedCollection<E>
  implements Collection<E>, Serializable
{
  private static final long serialVersionUID = 2412805092710877986L;
  private final Collection<E> collection;
  protected final Object lock;
  
  public static <T> SynchronizedCollection<T> synchronizedCollection(Collection<T> coll)
  {
    return new SynchronizedCollection(coll);
  }
  






  protected SynchronizedCollection(Collection<E> collection)
  {
    if (collection == null) {
      throw new NullPointerException("Collection must not be null.");
    }
    this.collection = collection;
    lock = this;
  }
  






  protected SynchronizedCollection(Collection<E> collection, Object lock)
  {
    if (collection == null) {
      throw new NullPointerException("Collection must not be null.");
    }
    if (lock == null) {
      throw new NullPointerException("Lock must not be null.");
    }
    this.collection = collection;
    this.lock = lock;
  }
  




  protected Collection<E> decorated()
  {
    return collection;
  }
  


  public boolean add(E object)
  {
    synchronized (lock) {
      return decorated().add(object);
    }
  }
  
  public boolean addAll(Collection<? extends E> coll)
  {
    synchronized (lock) {
      return decorated().addAll(coll);
    }
  }
  
  public void clear()
  {
    synchronized (lock) {
      decorated().clear();
    }
  }
  
  public boolean contains(Object object)
  {
    synchronized (lock) {
      return decorated().contains(object);
    }
  }
  
  public boolean containsAll(Collection<?> coll)
  {
    synchronized (lock) {
      return decorated().containsAll(coll);
    }
  }
  
  public boolean isEmpty()
  {
    synchronized (lock) {
      return decorated().isEmpty();
    }
  }
  











  public Iterator<E> iterator()
  {
    return decorated().iterator();
  }
  
  public Object[] toArray()
  {
    synchronized (lock) {
      return decorated().toArray();
    }
  }
  
  public <T> T[] toArray(T[] object)
  {
    synchronized (lock) {
      return decorated().toArray(object);
    }
  }
  
  public boolean remove(Object object)
  {
    synchronized (lock) {
      return decorated().remove(object);
    }
  }
  
  public boolean removeAll(Collection<?> coll)
  {
    synchronized (lock) {
      return decorated().removeAll(coll);
    }
  }
  
  public boolean retainAll(Collection<?> coll)
  {
    synchronized (lock) {
      return decorated().retainAll(coll);
    }
  }
  
  public int size()
  {
    synchronized (lock) {
      return decorated().size();
    }
  }
  
  public boolean equals(Object object)
  {
    synchronized (lock) {
      if (object == this) {
        return true;
      }
      return (object == this) || (decorated().equals(object));
    }
  }
  
  public int hashCode()
  {
    synchronized (lock) {
      return decorated().hashCode();
    }
  }
  
  public String toString()
  {
    synchronized (lock) {
      return decorated().toString();
    }
  }
}
