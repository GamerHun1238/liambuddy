package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract interface Bag<E>
  extends Collection<E>
{
  public abstract int getCount(Object paramObject);
  
  public abstract boolean add(E paramE);
  
  public abstract boolean add(E paramE, int paramInt);
  
  public abstract boolean remove(Object paramObject);
  
  public abstract boolean remove(Object paramObject, int paramInt);
  
  public abstract Set<E> uniqueSet();
  
  public abstract int size();
  
  public abstract boolean containsAll(Collection<?> paramCollection);
  
  public abstract boolean removeAll(Collection<?> paramCollection);
  
  public abstract boolean retainAll(Collection<?> paramCollection);
  
  public abstract Iterator<E> iterator();
}
