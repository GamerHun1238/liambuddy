package org.apache.commons.collections4;

import java.util.Iterator;

public abstract interface MapIterator<K, V>
  extends Iterator<K>
{
  public abstract boolean hasNext();
  
  public abstract K next();
  
  public abstract K getKey();
  
  public abstract V getValue();
  
  public abstract void remove();
  
  public abstract V setValue(V paramV);
}
