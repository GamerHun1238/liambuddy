package org.apache.commons.collections4;

public abstract interface Transformer<I, O>
{
  public abstract O transform(I paramI);
}
