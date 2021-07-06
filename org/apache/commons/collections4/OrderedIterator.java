package org.apache.commons.collections4;

import java.util.Iterator;

public abstract interface OrderedIterator<E>
  extends Iterator<E>
{
  public abstract boolean hasPrevious();
  
  public abstract E previous();
}
