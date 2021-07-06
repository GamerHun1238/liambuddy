package org.apache.commons.collections4.iterators;

import java.util.Iterator;













































public abstract class LazyIteratorChain<E>
  implements Iterator<E>
{
  private int callCounter = 0;
  

  private boolean chainExhausted = false;
  

  private Iterator<? extends E> currentIterator = null;
  




  private Iterator<? extends E> lastUsedIterator = null;
  




  public LazyIteratorChain() {}
  




  protected abstract Iterator<? extends E> nextIterator(int paramInt);
  



  private void updateCurrentIterator()
  {
    if (callCounter == 0) {
      currentIterator = nextIterator(++callCounter);
      if (currentIterator == null) {
        currentIterator = EmptyIterator.emptyIterator();
        chainExhausted = true;
      }
      

      lastUsedIterator = currentIterator;
    }
    
    while ((!currentIterator.hasNext()) && (!chainExhausted)) {
      Iterator<? extends E> nextIterator = nextIterator(++callCounter);
      if (nextIterator != null) {
        currentIterator = nextIterator;
      } else {
        chainExhausted = true;
      }
    }
  }
  






  public boolean hasNext()
  {
    updateCurrentIterator();
    lastUsedIterator = currentIterator;
    
    return currentIterator.hasNext();
  }
  





  public E next()
  {
    updateCurrentIterator();
    lastUsedIterator = currentIterator;
    
    return currentIterator.next();
  }
  











  public void remove()
  {
    if (currentIterator == null) {
      updateCurrentIterator();
    }
    lastUsedIterator.remove();
  }
}
