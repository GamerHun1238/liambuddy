package org.apache.commons.collections4.queue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.apache.commons.collections4.BoundedCollection;







































public class CircularFifoQueue<E>
  extends AbstractCollection<E>
  implements Queue<E>, BoundedCollection<E>, Serializable
{
  private static final long serialVersionUID = -8423413834657610406L;
  private transient E[] elements;
  private transient int start = 0;
  







  private transient int end = 0;
  

  private transient boolean full = false;
  

  private final int maxElements;
  


  public CircularFifoQueue()
  {
    this(32);
  }
  






  public CircularFifoQueue(int size)
  {
    if (size <= 0) {
      throw new IllegalArgumentException("The size must be greater than 0");
    }
    elements = ((Object[])new Object[size]);
    maxElements = elements.length;
  }
  






  public CircularFifoQueue(Collection<? extends E> coll)
  {
    this(coll.size());
    addAll(coll);
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeInt(size());
    for (E e : this) {
      out.writeObject(e);
    }
  }
  






  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    elements = ((Object[])new Object[maxElements]);
    int size = in.readInt();
    for (int i = 0; i < size; i++) {
      elements[i] = in.readObject();
    }
    start = 0;
    full = (size == maxElements);
    if (full) {
      end = 0;
    } else {
      end = size;
    }
  }
  






  public int size()
  {
    int size = 0;
    
    if (end < start) {
      size = maxElements - start + end;
    } else if (end == start) {
      size = full ? maxElements : 0;
    } else {
      size = end - start;
    }
    
    return size;
  }
  





  public boolean isEmpty()
  {
    return size() == 0;
  }
  







  public boolean isFull()
  {
    return false;
  }
  






  public boolean isAtFullCapacity()
  {
    return size() == maxElements;
  }
  




  public int maxSize()
  {
    return maxElements;
  }
  



  public void clear()
  {
    full = false;
    start = 0;
    end = 0;
    Arrays.fill(elements, null);
  }
  








  public boolean add(E element)
  {
    if (null == element) {
      throw new NullPointerException("Attempted to add null object to queue");
    }
    
    if (isAtFullCapacity()) {
      remove();
    }
    
    elements[(end++)] = element;
    
    if (end >= maxElements) {
      end = 0;
    }
    
    if (end == start) {
      full = true;
    }
    
    return true;
  }
  






  public E get(int index)
  {
    int sz = size();
    if ((index < 0) || (index >= sz)) {
      throw new NoSuchElementException(String.format("The specified index (%1$d) is outside the available range [0, %2$d)", new Object[] { Integer.valueOf(index), Integer.valueOf(sz) }));
    }
    


    int idx = (start + index) % maxElements;
    return elements[idx];
  }
  









  public boolean offer(E element)
  {
    return add(element);
  }
  
  public E poll() {
    if (isEmpty()) {
      return null;
    }
    return remove();
  }
  
  public E element() {
    if (isEmpty()) {
      throw new NoSuchElementException("queue is empty");
    }
    return peek();
  }
  
  public E peek() {
    if (isEmpty()) {
      return null;
    }
    return elements[start];
  }
  
  public E remove() {
    if (isEmpty()) {
      throw new NoSuchElementException("queue is empty");
    }
    
    E element = elements[start];
    if (null != element) {
      elements[(start++)] = null;
      
      if (start >= maxElements) {
        start = 0;
      }
      full = false;
    }
    return element;
  }
  



  private int increment(int index)
  {
    
    


    if (index >= maxElements) {
      index = 0;
    }
    return index;
  }
  



  private int decrement(int index)
  {
    
    

    if (index < 0) {
      index = maxElements - 1;
    }
    return index;
  }
  





  public Iterator<E> iterator()
  {
    new Iterator()
    {
      private int index = start;
      private int lastReturnedIndex = -1;
      private boolean isFirst = full;
      
      public boolean hasNext() {
        return (isFirst) || (index != end);
      }
      
      public E next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        isFirst = false;
        lastReturnedIndex = index;
        index = CircularFifoQueue.this.increment(index);
        return elements[lastReturnedIndex];
      }
      
      public void remove() {
        if (lastReturnedIndex == -1) {
          throw new IllegalStateException();
        }
        

        if (lastReturnedIndex == start) {
          remove();
          lastReturnedIndex = -1;
          return;
        }
        
        int pos = lastReturnedIndex + 1;
        if ((start < lastReturnedIndex) && (pos < end))
        {
          System.arraycopy(elements, pos, elements, lastReturnedIndex, end - pos);
        }
        else {
          while (pos != end) {
            if (pos >= maxElements) {
              elements[(pos - 1)] = elements[0];
              pos = 0;
            } else {
              elements[CircularFifoQueue.this.decrement(pos)] = elements[pos];
              pos = CircularFifoQueue.this.increment(pos);
            }
          }
        }
        
        lastReturnedIndex = -1;
        end = CircularFifoQueue.this.decrement(end);
        elements[end] = null;
        full = false;
        index = CircularFifoQueue.this.decrement(index);
      }
    };
  }
}
