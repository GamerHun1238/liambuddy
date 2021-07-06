package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract interface MultiSet<E>
  extends Collection<E>
{
  public abstract int getCount(Object paramObject);
  
  public abstract int setCount(E paramE, int paramInt);
  
  public abstract boolean add(E paramE);
  
  public abstract int add(E paramE, int paramInt);
  
  public abstract boolean remove(Object paramObject);
  
  public abstract int remove(Object paramObject, int paramInt);
  
  public abstract Set<E> uniqueSet();
  
  public abstract Set<Entry<E>> entrySet();
  
  public abstract Iterator<E> iterator();
  
  public abstract int size();
  
  public abstract boolean containsAll(Collection<?> paramCollection);
  
  public abstract boolean removeAll(Collection<?> paramCollection);
  
  public abstract boolean retainAll(Collection<?> paramCollection);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public static abstract interface Entry<E>
  {
    public abstract E getElement();
    
    public abstract int getCount();
    
    public abstract boolean equals(Object paramObject);
    
    public abstract int hashCode();
  }
}
