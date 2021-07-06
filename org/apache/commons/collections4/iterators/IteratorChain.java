package org.apache.commons.collections4.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;












































public class IteratorChain<E>
  implements Iterator<E>
{
  private final Queue<Iterator<? extends E>> iteratorChain = new LinkedList();
  

  private Iterator<? extends E> currentIterator = null;
  




  private Iterator<? extends E> lastUsedIterator = null;
  




  private boolean isLocked = false;
  











  public IteratorChain() {}
  











  public IteratorChain(Iterator<? extends E> iterator)
  {
    addIterator(iterator);
  }
  










  public IteratorChain(Iterator<? extends E> first, Iterator<? extends E> second)
  {
    addIterator(first);
    addIterator(second);
  }
  









  public IteratorChain(Iterator<? extends E>... iteratorChain)
  {
    for (Iterator<? extends E> element : iteratorChain) {
      addIterator(element);
    }
  }
  












  public IteratorChain(Collection<Iterator<? extends E>> iteratorChain)
  {
    for (Iterator<? extends E> iterator : iteratorChain) {
      addIterator(iterator);
    }
  }
  







  public void addIterator(Iterator<? extends E> iterator)
  {
    checkLocked();
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    iteratorChain.add(iterator);
  }
  




  public int size()
  {
    return iteratorChain.size();
  }
  






  public boolean isLocked()
  {
    return isLocked;
  }
  


  private void checkLocked()
  {
    if (isLocked == true) {
      throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
    }
  }
  




  private void lockChain()
  {
    if (!isLocked) {
      isLocked = true;
    }
  }
  



  protected void updateCurrentIterator()
  {
    if (currentIterator == null) {
      if (iteratorChain.isEmpty()) {
        currentIterator = EmptyIterator.emptyIterator();
      } else {
        currentIterator = ((Iterator)iteratorChain.remove());
      }
      

      lastUsedIterator = currentIterator;
    }
    
    while ((!currentIterator.hasNext()) && (!iteratorChain.isEmpty())) {
      currentIterator = ((Iterator)iteratorChain.remove());
    }
  }
  





  public boolean hasNext()
  {
    lockChain();
    updateCurrentIterator();
    lastUsedIterator = currentIterator;
    
    return currentIterator.hasNext();
  }
  






  public E next()
  {
    lockChain();
    updateCurrentIterator();
    lastUsedIterator = currentIterator;
    
    return currentIterator.next();
  }
  












  public void remove()
  {
    lockChain();
    if (currentIterator == null) {
      updateCurrentIterator();
    }
    lastUsedIterator.remove();
  }
}
