package org.apache.commons.collections4;

import java.util.Collection;

public abstract interface BoundedCollection<E>
  extends Collection<E>
{
  public abstract boolean isFull();
  
  public abstract int maxSize();
}
