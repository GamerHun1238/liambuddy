package org.apache.commons.collections4;

import java.util.Iterator;

public abstract interface ResettableIterator<E>
  extends Iterator<E>
{
  public abstract void reset();
}
