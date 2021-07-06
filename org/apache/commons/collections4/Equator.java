package org.apache.commons.collections4;

public abstract interface Equator<T>
{
  public abstract boolean equate(T paramT1, T paramT2);
  
  public abstract int hash(T paramT);
}
