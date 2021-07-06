package org.apache.commons.collections4;

import java.util.Comparator;

public abstract interface SortedBag<E>
  extends Bag<E>
{
  public abstract Comparator<? super E> comparator();
  
  public abstract E first();
  
  public abstract E last();
}
