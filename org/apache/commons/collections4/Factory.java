package org.apache.commons.collections4;

public abstract interface Factory<T>
{
  public abstract T create();
}
