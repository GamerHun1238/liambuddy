package gnu.trove.set.hash;

import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
















public class TLinkedHashSet<E>
  extends THashSet<E>
{
  TIntList order;
  
  public TLinkedHashSet() {}
  
  public TLinkedHashSet(int initialCapacity)
  {
    super(initialCapacity);
  }
  







  public TLinkedHashSet(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  





  public TLinkedHashSet(Collection<? extends E> es)
  {
    super(es);
  }
  






  public int setUp(int initialCapacity)
  {
    order = new TIntArrayList(initialCapacity)
    {


      public void ensureCapacity(int capacity)
      {


        if (capacity > _data.length) {
          int newCap = Math.max(_set.length, capacity);
          int[] tmp = new int[newCap];
          System.arraycopy(_data, 0, tmp, 0, _data.length);
          _data = tmp;
        }
      }
    };
    return super.setUp(initialCapacity);
  }
  




  public void clear()
  {
    super.clear();
    order.clear();
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("{");
    boolean first = true;
    
    for (Iterator<E> it = iterator(); it.hasNext();) {
      if (first) {
        first = false;
      } else {
        buf.append(", ");
      }
      
      buf.append(it.next());
    }
    
    buf.append("}");
    return buf.toString();
  }
  






  public boolean add(E obj)
  {
    int index = insertKey(obj);
    
    if (index < 0) {
      return false;
    }
    
    if (!order.add(index)) {
      throw new IllegalStateException("Order not changed after insert");
    }
    postInsertHook(consumeFreeSlot);
    return true;
  }
  


  protected void removeAt(int index)
  {
    order.remove(index);
    super.removeAt(index);
  }
  






  protected void rehash(int newCapacity)
  {
    TIntLinkedList oldOrder = new TIntLinkedList(order);
    int oldSize = size();
    
    Object[] oldSet = _set;
    
    order.clear();
    _set = new Object[newCapacity];
    Arrays.fill(_set, FREE);
    
    for (TIntIterator iterator = oldOrder.iterator(); iterator.hasNext();) {
      int i = iterator.next();
      E o = oldSet[i];
      if ((o == FREE) || (o == REMOVED)) {
        throw new IllegalStateException("Iterating over empty location while rehashing");
      }
      
      if ((o != FREE) && (o != REMOVED)) {
        int index = insertKey(o);
        if (index < 0) {
          throwObjectContractViolation(_set[(-index - 1)], o, size(), oldSize, oldSet);
        }
        
        if (!order.add(index)) {
          throw new IllegalStateException("Order not changed after insert");
        }
      }
    }
  }
  
  class WriteProcedure implements TIntProcedure {
    final ObjectOutput output;
    IOException ioException;
    
    WriteProcedure(ObjectOutput output) {
      this.output = output;
    }
    
    public IOException getIoException() {
      return ioException;
    }
    
    public boolean execute(int value) {
      try {
        output.writeObject(_set[value]);
      } catch (IOException e) {
        ioException = e;
        return false;
      }
      return true;
    }
  }
  

  protected void writeEntries(ObjectOutput out)
    throws IOException
  {
    TLinkedHashSet<E>.WriteProcedure writeProcedure = new WriteProcedure(out);
    if (!order.forEach(writeProcedure)) {
      throw writeProcedure.getIoException();
    }
  }
  





  public TObjectHashIterator<E> iterator()
  {
    new TObjectHashIterator(this) {
      TIntIterator localIterator = order.iterator();
      





      int lastIndex;
      





      public E next()
      {
        lastIndex = localIterator.next();
        return objectAtIndex(lastIndex);
      }
      






      public boolean hasNext()
      {
        return localIterator.hasNext();
      }
      







      public void remove()
      {
        localIterator.remove();
        
        try
        {
          _hash.tempDisableAutoCompaction();
          removeAt(lastIndex);
        } finally {
          _hash.reenableAutoCompaction(false);
        }
      }
    };
  }
  
  class ForEachProcedure implements TIntProcedure {
    boolean changed = false;
    final Object[] set;
    final TObjectProcedure<? super E> procedure;
    
    public ForEachProcedure(TObjectProcedure<? super E> set) {
      this.set = set;
      this.procedure = procedure;
    }
    








    public boolean execute(int value)
    {
      return procedure.execute(set[value]);
    }
  }
  







  public boolean forEach(TObjectProcedure<? super E> procedure)
  {
    TLinkedHashSet<E>.ForEachProcedure forEachProcedure = new ForEachProcedure(_set, procedure);
    return order.forEach(forEachProcedure);
  }
}
