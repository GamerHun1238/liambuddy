package org.apache.commons.collections4.iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.Transformer;





































































public class ObjectGraphIterator<E>
  implements Iterator<E>
{
  private final Deque<Iterator<? extends E>> stack = new ArrayDeque(8);
  

  private E root;
  
  private final Transformer<? super E, ? extends E> transformer;
  
  private boolean hasNext = false;
  



  private Iterator<? extends E> currentIterator;
  



  private E currentValue;
  


  private Iterator<? extends E> lastUsedIterator;
  



  public ObjectGraphIterator(E root, Transformer<? super E, ? extends E> transformer)
  {
    if ((root instanceof Iterator)) {
      currentIterator = ((Iterator)root);
    } else {
      this.root = root;
    }
    this.transformer = transformer;
  }
  










  public ObjectGraphIterator(Iterator<? extends E> rootIterator)
  {
    currentIterator = rootIterator;
    transformer = null;
  }
  



  protected void updateCurrentIterator()
  {
    if (hasNext) {
      return;
    }
    if (currentIterator == null) {
      if (root != null)
      {

        if (transformer == null) {
          findNext(root);
        } else {
          findNext(transformer.transform(root));
        }
        root = null;
      }
    } else {
      findNextByIterator(currentIterator);
    }
  }
  





  protected void findNext(E value)
  {
    if ((value instanceof Iterator))
    {
      findNextByIterator((Iterator)value);
    }
    else {
      currentValue = value;
      hasNext = true;
    }
  }
  




  protected void findNextByIterator(Iterator<? extends E> iterator)
  {
    if (iterator != currentIterator)
    {
      if (currentIterator != null) {
        stack.push(currentIterator);
      }
      currentIterator = iterator;
    }
    
    while ((currentIterator.hasNext()) && (!hasNext)) {
      E next = currentIterator.next();
      if (transformer != null) {
        next = transformer.transform(next);
      }
      findNext(next);
    }
    
    if ((!hasNext) && (!stack.isEmpty()))
    {
      currentIterator = ((Iterator)stack.pop());
      findNextByIterator(currentIterator);
    }
  }
  





  public boolean hasNext()
  {
    updateCurrentIterator();
    return hasNext;
  }
  





  public E next()
  {
    updateCurrentIterator();
    if (!hasNext) {
      throw new NoSuchElementException("No more elements in the iteration");
    }
    lastUsedIterator = currentIterator;
    E result = currentValue;
    currentValue = null;
    hasNext = false;
    return result;
  }
  












  public void remove()
  {
    if (lastUsedIterator == null) {
      throw new IllegalStateException("Iterator remove() cannot be called at this time");
    }
    lastUsedIterator.remove();
    lastUsedIterator = null;
  }
}
