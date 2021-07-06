package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableIterator;



























public class SingletonIterator<E>
  implements ResettableIterator<E>
{
  private final boolean removeAllowed;
  private boolean beforeFirst = true;
  
  private boolean removed = false;
  


  private E object;
  



  public SingletonIterator(E object)
  {
    this(object, true);
  }
  








  public SingletonIterator(E object, boolean removeAllowed)
  {
    this.object = object;
    this.removeAllowed = removeAllowed;
  }
  







  public boolean hasNext()
  {
    return (beforeFirst) && (!removed);
  }
  








  public E next()
  {
    if ((!beforeFirst) || (removed)) {
      throw new NoSuchElementException();
    }
    beforeFirst = false;
    return object;
  }
  








  public void remove()
  {
    if (removeAllowed) {
      if ((removed) || (beforeFirst)) {
        throw new IllegalStateException();
      }
      object = null;
      removed = true;
    } else {
      throw new UnsupportedOperationException();
    }
  }
  


  public void reset()
  {
    beforeFirst = true;
  }
}
