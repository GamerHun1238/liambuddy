package com.fasterxml.jackson.core.util;

public abstract interface Instantiatable<T>
{
  public abstract T createInstance();
}
