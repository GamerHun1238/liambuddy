package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

















































public class CursorableLinkedList<E>
  extends AbstractLinkedList<E>
  implements Serializable
{
  private static final long serialVersionUID = 8836393098519411393L;
  private transient List<WeakReference<Cursor<E>>> cursors;
  
  public CursorableLinkedList()
  {
    init();
  }
  




  public CursorableLinkedList(Collection<? extends E> coll)
  {
    super(coll);
  }
  




  protected void init()
  {
    super.init();
    cursors = new ArrayList();
  }
  










  public Iterator<E> iterator()
  {
    return super.listIterator(0);
  }
  















  public ListIterator<E> listIterator()
  {
    return cursor(0);
  }
  
















  public ListIterator<E> listIterator(int fromIndex)
  {
    return cursor(fromIndex);
  }
  






















  public Cursor<E> cursor()
  {
    return cursor(0);
  }
  


























  public Cursor<E> cursor(int fromIndex)
  {
    Cursor<E> cursor = new Cursor(this, fromIndex);
    registerCursor(cursor);
    return cursor;
  }
  









  protected void updateNode(AbstractLinkedList.Node<E> node, E value)
  {
    super.updateNode(node, value);
    broadcastNodeChanged(node);
  }
  







  protected void addNode(AbstractLinkedList.Node<E> nodeToInsert, AbstractLinkedList.Node<E> insertBeforeNode)
  {
    super.addNode(nodeToInsert, insertBeforeNode);
    broadcastNodeInserted(nodeToInsert);
  }
  






  protected void removeNode(AbstractLinkedList.Node<E> node)
  {
    super.removeNode(node);
    broadcastNodeRemoved(node);
  }
  



  protected void removeAllNodes()
  {
    if (size() > 0)
    {
      Iterator<E> it = iterator();
      while (it.hasNext()) {
        it.next();
        it.remove();
      }
    }
  }
  







  protected void registerCursor(Cursor<E> cursor)
  {
    for (Iterator<WeakReference<Cursor<E>>> it = cursors.iterator(); it.hasNext();) {
      WeakReference<Cursor<E>> ref = (WeakReference)it.next();
      if (ref.get() == null) {
        it.remove();
      }
    }
    cursors.add(new WeakReference(cursor));
  }
  




  protected void unregisterCursor(Cursor<E> cursor)
  {
    for (Iterator<WeakReference<Cursor<E>>> it = cursors.iterator(); it.hasNext();) {
      WeakReference<Cursor<E>> ref = (WeakReference)it.next();
      Cursor<E> cur = (Cursor)ref.get();
      if (cur == null)
      {


        it.remove();
      } else if (cur == cursor) {
        ref.clear();
        it.remove();
        break;
      }
    }
  }
  






  protected void broadcastNodeChanged(AbstractLinkedList.Node<E> node)
  {
    Iterator<WeakReference<Cursor<E>>> it = cursors.iterator();
    while (it.hasNext()) {
      WeakReference<Cursor<E>> ref = (WeakReference)it.next();
      Cursor<E> cursor = (Cursor)ref.get();
      if (cursor == null) {
        it.remove();
      } else {
        cursor.nodeChanged(node);
      }
    }
  }
  





  protected void broadcastNodeRemoved(AbstractLinkedList.Node<E> node)
  {
    Iterator<WeakReference<Cursor<E>>> it = cursors.iterator();
    while (it.hasNext()) {
      WeakReference<Cursor<E>> ref = (WeakReference)it.next();
      Cursor<E> cursor = (Cursor)ref.get();
      if (cursor == null) {
        it.remove();
      } else {
        cursor.nodeRemoved(node);
      }
    }
  }
  





  protected void broadcastNodeInserted(AbstractLinkedList.Node<E> node)
  {
    Iterator<WeakReference<Cursor<E>>> it = cursors.iterator();
    while (it.hasNext()) {
      WeakReference<Cursor<E>> ref = (WeakReference)it.next();
      Cursor<E> cursor = (Cursor)ref.get();
      if (cursor == null) {
        it.remove();
      } else {
        cursor.nodeInserted(node);
      }
    }
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    doWriteObject(out);
  }
  

  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    doReadObject(in);
  }
  








  protected ListIterator<E> createSubListListIterator(AbstractLinkedList.LinkedSubList<E> subList, int fromIndex)
  {
    SubCursor<E> cursor = new SubCursor(subList, fromIndex);
    registerCursor(cursor);
    return cursor;
  }
  




  public static class Cursor<E>
    extends AbstractLinkedList.LinkedListIterator<E>
  {
    boolean valid = true;
    
    boolean nextIndexValid = true;
    
    boolean currentRemovedByAnother = false;
    





    protected Cursor(CursorableLinkedList<E> parent, int index)
    {
      super(index);
      valid = true;
    }
    













    public void remove()
    {
      if ((current != null) || (!currentRemovedByAnother))
      {




        checkModCount();
        parent.removeNode(getLastNodeReturned());
      }
      currentRemovedByAnother = false;
    }
    







    public void add(E obj)
    {
      super.add(obj);
      

      next = next.next;
    }
    










    public int nextIndex()
    {
      if (!nextIndexValid) {
        if (next == parent.header) {
          nextIndex = parent.size();
        } else {
          int pos = 0;
          AbstractLinkedList.Node<E> temp = parent.header.next;
          while (temp != next) {
            pos++;
            temp = next;
          }
          nextIndex = pos;
        }
        nextIndexValid = true;
      }
      return nextIndex;
    }
    






    protected void nodeChanged(AbstractLinkedList.Node<E> node) {}
    





    protected void nodeRemoved(AbstractLinkedList.Node<E> node)
    {
      if ((node == next) && (node == current))
      {
        next = next;
        current = null;
        currentRemovedByAnother = true;
      } else if (node == next)
      {

        next = next;
        currentRemovedByAnother = false;
      } else if (node == current)
      {

        current = null;
        currentRemovedByAnother = true;
        nextIndex -= 1;
      } else {
        nextIndexValid = false;
        currentRemovedByAnother = false;
      }
    }
    




    protected void nodeInserted(AbstractLinkedList.Node<E> node)
    {
      if (previous == current) {
        next = node;
      } else if (next.previous == node) {
        next = node;
      } else {
        nextIndexValid = false;
      }
    }
    



    protected void checkModCount()
    {
      if (!valid) {
        throw new ConcurrentModificationException("Cursor closed");
      }
    }
    







    public void close()
    {
      if (valid) {
        ((CursorableLinkedList)parent).unregisterCursor(this);
        valid = false;
      }
    }
  }
  






  protected static class SubCursor<E>
    extends CursorableLinkedList.Cursor<E>
  {
    protected final AbstractLinkedList.LinkedSubList<E> sub;
    





    protected SubCursor(AbstractLinkedList.LinkedSubList<E> sub, int index)
    {
      super(index + offset);
      this.sub = sub;
    }
    
    public boolean hasNext()
    {
      return nextIndex() < sub.size;
    }
    
    public boolean hasPrevious()
    {
      return previousIndex() >= 0;
    }
    
    public int nextIndex()
    {
      return super.nextIndex() - sub.offset;
    }
    
    public void add(E obj)
    {
      super.add(obj);
      sub.expectedModCount = parent.modCount;
      sub.size += 1;
    }
    
    public void remove()
    {
      super.remove();
      sub.expectedModCount = parent.modCount;
      sub.size -= 1;
    }
  }
}
