package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.list.UnmodifiableList;






























public class CollatingIterator<E>
  implements Iterator<E>
{
  private Comparator<? super E> comparator = null;
  

  private List<Iterator<? extends E>> iterators = null;
  

  private List<E> values = null;
  

  private BitSet valueSet = null;
  




  private int lastReturned = -1;
  








  public CollatingIterator()
  {
    this(null, 2);
  }
  







  public CollatingIterator(Comparator<? super E> comp)
  {
    this(comp, 2);
  }
  










  public CollatingIterator(Comparator<? super E> comp, int initIterCapacity)
  {
    iterators = new ArrayList(initIterCapacity);
    setComparator(comp);
  }
  











  public CollatingIterator(Comparator<? super E> comp, Iterator<? extends E> a, Iterator<? extends E> b)
  {
    this(comp, 2);
    addIterator(a);
    addIterator(b);
  }
  









  public CollatingIterator(Comparator<? super E> comp, Iterator<? extends E>[] iterators)
  {
    this(comp, iterators.length);
    for (Iterator<? extends E> iterator : iterators) {
      addIterator(iterator);
    }
  }
  











  public CollatingIterator(Comparator<? super E> comp, Collection<Iterator<? extends E>> iterators)
  {
    this(comp, iterators.size());
    for (Iterator<? extends E> iterator : iterators) {
      addIterator(iterator);
    }
  }
  








  public void addIterator(Iterator<? extends E> iterator)
  {
    checkNotStarted();
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    iterators.add(iterator);
  }
  








  public void setIterator(int index, Iterator<? extends E> iterator)
  {
    checkNotStarted();
    if (iterator == null) {
      throw new NullPointerException("Iterator must not be null");
    }
    iterators.set(index, iterator);
  }
  




  public List<Iterator<? extends E>> getIterators()
  {
    return UnmodifiableList.unmodifiableList(iterators);
  }
  




  public Comparator<? super E> getComparator()
  {
    return comparator;
  }
  









  public void setComparator(Comparator<? super E> comp)
  {
    checkNotStarted();
    comparator = comp;
  }
  






  public boolean hasNext()
  {
    start();
    return (anyValueSet(valueSet)) || (anyHasNext(iterators));
  }
  




  public E next()
    throws NoSuchElementException
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    int leastIndex = least();
    if (leastIndex == -1) {
      throw new NoSuchElementException();
    }
    E val = values.get(leastIndex);
    clear(leastIndex);
    lastReturned = leastIndex;
    return val;
  }
  





  public void remove()
  {
    if (lastReturned == -1) {
      throw new IllegalStateException("No value can be removed at present");
    }
    ((Iterator)iterators.get(lastReturned)).remove();
  }
  





  public int getIteratorIndex()
  {
    if (lastReturned == -1) {
      throw new IllegalStateException("No value has been returned yet");
    }
    
    return lastReturned;
  }
  




  private void start()
  {
    if (values == null) {
      values = new ArrayList(iterators.size());
      valueSet = new BitSet(iterators.size());
      for (int i = 0; i < iterators.size(); i++) {
        values.add(null);
        valueSet.clear(i);
      }
    }
  }
  







  private boolean set(int i)
  {
    Iterator<? extends E> it = (Iterator)iterators.get(i);
    if (it.hasNext()) {
      values.set(i, it.next());
      valueSet.set(i);
      return true;
    }
    values.set(i, null);
    valueSet.clear(i);
    return false;
  }
  



  private void clear(int i)
  {
    values.set(i, null);
    valueSet.clear(i);
  }
  




  private void checkNotStarted()
    throws IllegalStateException
  {
    if (values != null) {
      throw new IllegalStateException("Can't do that after next or hasNext has been called.");
    }
  }
  





  private int least()
  {
    int leastIndex = -1;
    E leastObject = null;
    for (int i = 0; i < values.size(); i++) {
      if (!valueSet.get(i)) {
        set(i);
      }
      if (valueSet.get(i)) {
        if (leastIndex == -1) {
          leastIndex = i;
          leastObject = values.get(i);
        } else {
          E curObject = values.get(i);
          if (comparator == null) {
            throw new NullPointerException("You must invoke setComparator() to set a comparator first.");
          }
          if (comparator.compare(curObject, leastObject) < 0) {
            leastObject = curObject;
            leastIndex = i;
          }
        }
      }
    }
    return leastIndex;
  }
  



  private boolean anyValueSet(BitSet set)
  {
    for (int i = 0; i < set.size(); i++) {
      if (set.get(i)) {
        return true;
      }
    }
    return false;
  }
  



  private boolean anyHasNext(List<Iterator<? extends E>> iters)
  {
    for (Iterator<? extends E> iterator : iters) {
      if (iterator.hasNext()) {
        return true;
      }
    }
    return false;
  }
}
