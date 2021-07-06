package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.OrderedIterator;





























































public abstract class AbstractLinkedList<E>
  implements List<E>
{
  transient Node<E> header;
  transient int size;
  transient int modCount;
  
  protected AbstractLinkedList() {}
  
  protected AbstractLinkedList(Collection<? extends E> coll)
  {
    init();
    addAll(coll);
  }
  





  protected void init()
  {
    header = createHeaderNode();
  }
  

  public int size()
  {
    return size;
  }
  
  public boolean isEmpty() {
    return size() == 0;
  }
  
  public E get(int index) {
    Node<E> node = getNode(index, false);
    return node.getValue();
  }
  

  public Iterator<E> iterator()
  {
    return listIterator();
  }
  
  public ListIterator<E> listIterator() {
    return new LinkedListIterator(this, 0);
  }
  
  public ListIterator<E> listIterator(int fromIndex) {
    return new LinkedListIterator(this, fromIndex);
  }
  

  public int indexOf(Object value)
  {
    int i = 0;
    for (Node<E> node = header.next; node != header; node = next) {
      if (isEqualValue(node.getValue(), value)) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  public int lastIndexOf(Object value) {
    int i = size - 1;
    for (Node<E> node = header.previous; node != header; node = previous) {
      if (isEqualValue(node.getValue(), value)) {
        return i;
      }
      i--;
    }
    return -1;
  }
  
  public boolean contains(Object value) {
    return indexOf(value) != -1;
  }
  
  public boolean containsAll(Collection<?> coll) {
    for (Object o : coll) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }
  

  public Object[] toArray()
  {
    return toArray(new Object[size]);
  }
  

  public <T> T[] toArray(T[] array)
  {
    if (array.length < size) {
      Class<?> componentType = array.getClass().getComponentType();
      array = (Object[])Array.newInstance(componentType, size);
    }
    
    int i = 0;
    for (Node<E> node = header.next; node != header; i++) {
      array[i] = node.getValue();node = next;
    }
    
    if (array.length > size) {
      array[size] = null;
    }
    return array;
  }
  






  public List<E> subList(int fromIndexInclusive, int toIndexExclusive)
  {
    return new LinkedSubList(this, fromIndexInclusive, toIndexExclusive);
  }
  

  public boolean add(E value)
  {
    addLast(value);
    return true;
  }
  
  public void add(int index, E value) {
    Node<E> node = getNode(index, true);
    addNodeBefore(node, value);
  }
  
  public boolean addAll(Collection<? extends E> coll) {
    return addAll(size, coll);
  }
  
  public boolean addAll(int index, Collection<? extends E> coll) {
    Node<E> node = getNode(index, true);
    for (E e : coll) {
      addNodeBefore(node, e);
    }
    return true;
  }
  

  public E remove(int index)
  {
    Node<E> node = getNode(index, false);
    E oldValue = node.getValue();
    removeNode(node);
    return oldValue;
  }
  
  public boolean remove(Object value) {
    for (Node<E> node = header.next; node != header; node = next) {
      if (isEqualValue(node.getValue(), value)) {
        removeNode(node);
        return true;
      }
    }
    return false;
  }
  








  public boolean removeAll(Collection<?> coll)
  {
    boolean modified = false;
    Iterator<E> it = iterator();
    while (it.hasNext()) {
      if (coll.contains(it.next())) {
        it.remove();
        modified = true;
      }
    }
    return modified;
  }
  










  public boolean retainAll(Collection<?> coll)
  {
    boolean modified = false;
    Iterator<E> it = iterator();
    while (it.hasNext()) {
      if (!coll.contains(it.next())) {
        it.remove();
        modified = true;
      }
    }
    return modified;
  }
  
  public E set(int index, E value) {
    Node<E> node = getNode(index, false);
    E oldValue = node.getValue();
    updateNode(node, value);
    return oldValue;
  }
  
  public void clear() {
    removeAllNodes();
  }
  

  public E getFirst()
  {
    Node<E> node = header.next;
    if (node == header) {
      throw new NoSuchElementException();
    }
    return node.getValue();
  }
  
  public E getLast() {
    Node<E> node = header.previous;
    if (node == header) {
      throw new NoSuchElementException();
    }
    return node.getValue();
  }
  
  public boolean addFirst(E o) {
    addNodeAfter(header, o);
    return true;
  }
  
  public boolean addLast(E o) {
    addNodeBefore(header, o);
    return true;
  }
  
  public E removeFirst() {
    Node<E> node = header.next;
    if (node == header) {
      throw new NoSuchElementException();
    }
    E oldValue = node.getValue();
    removeNode(node);
    return oldValue;
  }
  
  public E removeLast() {
    Node<E> node = header.previous;
    if (node == header) {
      throw new NoSuchElementException();
    }
    E oldValue = node.getValue();
    removeNode(node);
    return oldValue;
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof List)) {
      return false;
    }
    List<?> other = (List)obj;
    if (other.size() != size()) {
      return false;
    }
    ListIterator<?> it1 = listIterator();
    ListIterator<?> it2 = other.listIterator();
    while ((it1.hasNext()) && (it2.hasNext())) {
      Object o1 = it1.next();
      Object o2 = it2.next();
      if (o1 == null ? o2 != null : !o1.equals(o2)) {
        return false;
      }
    }
    return (!it1.hasNext()) && (!it2.hasNext());
  }
  
  public int hashCode()
  {
    int hashCode = 1;
    for (E e : this) {
      hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
    }
    return hashCode;
  }
  
  public String toString()
  {
    if (size() == 0) {
      return "[]";
    }
    StringBuilder buf = new StringBuilder(16 * size());
    buf.append('[');
    
    Iterator<E> it = iterator();
    boolean hasNext = it.hasNext();
    while (hasNext) {
      Object value = it.next();
      buf.append(value == this ? "(this Collection)" : value);
      hasNext = it.hasNext();
      if (hasNext) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }
  









  protected boolean isEqualValue(Object value1, Object value2)
  {
    return (value1 == value2) || ((value1 != null) && (value1.equals(value2)));
  }
  







  protected void updateNode(Node<E> node, E value)
  {
    node.setValue(value);
  }
  






  protected Node<E> createHeaderNode()
  {
    return new Node();
  }
  







  protected Node<E> createNode(E value)
  {
    return new Node(value);
  }
  










  protected void addNodeBefore(Node<E> node, E value)
  {
    Node<E> newNode = createNode(value);
    addNode(newNode, node);
  }
  










  protected void addNodeAfter(Node<E> node, E value)
  {
    Node<E> newNode = createNode(value);
    addNode(newNode, next);
  }
  






  protected void addNode(Node<E> nodeToInsert, Node<E> insertBeforeNode)
  {
    next = insertBeforeNode;
    previous = previous;
    previous.next = nodeToInsert;
    previous = nodeToInsert;
    size += 1;
    modCount += 1;
  }
  





  protected void removeNode(Node<E> node)
  {
    previous.next = next;
    next.previous = previous;
    size -= 1;
    modCount += 1;
  }
  


  protected void removeAllNodes()
  {
    header.next = header;
    header.previous = header;
    size = 0;
    modCount += 1;
  }
  










  protected Node<E> getNode(int index, boolean endMarkerAllowed)
    throws IndexOutOfBoundsException
  {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") less than zero.");
    }
    
    if ((!endMarkerAllowed) && (index == size)) {
      throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") is the size of the list.");
    }
    
    if (index > size) {
      throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") greater than the size of the " + "list (" + size + ").");
    }
    

    Node<E> node;
    
    if (index < size / 2)
    {
      Node<E> node = header.next;
      for (int currentIndex = 0; currentIndex < index; currentIndex++) {
        node = next;
      }
    }
    else {
      node = header;
      for (int currentIndex = size; currentIndex > index; currentIndex--) {
        node = previous;
      }
    }
    return node;
  }
  






  protected Iterator<E> createSubListIterator(LinkedSubList<E> subList)
  {
    return createSubListListIterator(subList, 0);
  }
  






  protected ListIterator<E> createSubListListIterator(LinkedSubList<E> subList, int fromIndex)
  {
    return new LinkedSubListIterator(subList, fromIndex);
  }
  









  protected void doWriteObject(ObjectOutputStream outputStream)
    throws IOException
  {
    outputStream.writeInt(size());
    for (E e : this) {
      outputStream.writeObject(e);
    }
  }
  









  protected void doReadObject(ObjectInputStream inputStream)
    throws IOException, ClassNotFoundException
  {
    init();
    int size = inputStream.readInt();
    for (int i = 0; i < size; i++) {
      add(inputStream.readObject());
    }
  }
  




  protected static class Node<E>
  {
    protected Node<E> previous;
    


    protected Node<E> next;
    


    protected E value;
    



    protected Node()
    {
      previous = this;
      next = this;
    }
    





    protected Node(E value)
    {
      this.value = value;
    }
    







    protected Node(Node<E> previous, Node<E> next, E value)
    {
      this.previous = previous;
      this.next = next;
      this.value = value;
    }
    





    protected E getValue()
    {
      return value;
    }
    





    protected void setValue(E value)
    {
      this.value = value;
    }
    





    protected Node<E> getPreviousNode()
    {
      return previous;
    }
    





    protected void setPreviousNode(Node<E> previous)
    {
      this.previous = previous;
    }
    





    protected Node<E> getNextNode()
    {
      return next;
    }
    





    protected void setNextNode(Node<E> next)
    {
      this.next = next;
    }
  }
  






  protected static class LinkedListIterator<E>
    implements ListIterator<E>, OrderedIterator<E>
  {
    protected final AbstractLinkedList<E> parent;
    





    protected AbstractLinkedList.Node<E> next;
    





    protected int nextIndex;
    





    protected AbstractLinkedList.Node<E> current;
    





    protected int expectedModCount;
    






    protected LinkedListIterator(AbstractLinkedList<E> parent, int fromIndex)
      throws IndexOutOfBoundsException
    {
      this.parent = parent;
      expectedModCount = modCount;
      next = parent.getNode(fromIndex, true);
      nextIndex = fromIndex;
    }
    






    protected void checkModCount()
    {
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
    





    protected AbstractLinkedList.Node<E> getLastNodeReturned()
      throws IllegalStateException
    {
      if (current == null) {
        throw new IllegalStateException();
      }
      return current;
    }
    
    public boolean hasNext() {
      return next != parent.header;
    }
    
    public E next() {
      checkModCount();
      if (!hasNext()) {
        throw new NoSuchElementException("No element at index " + nextIndex + ".");
      }
      E value = next.getValue();
      current = next;
      next = next.next;
      nextIndex += 1;
      return value;
    }
    
    public boolean hasPrevious() {
      return next.previous != parent.header;
    }
    
    public E previous() {
      checkModCount();
      if (!hasPrevious()) {
        throw new NoSuchElementException("Already at start of list.");
      }
      next = next.previous;
      E value = next.getValue();
      current = next;
      nextIndex -= 1;
      return value;
    }
    
    public int nextIndex() {
      return nextIndex;
    }
    
    public int previousIndex()
    {
      return nextIndex() - 1;
    }
    
    public void remove() {
      checkModCount();
      if (current == next)
      {
        next = next.next;
        parent.removeNode(getLastNodeReturned());
      }
      else {
        parent.removeNode(getLastNodeReturned());
        nextIndex -= 1;
      }
      current = null;
      expectedModCount += 1;
    }
    
    public void set(E obj) {
      checkModCount();
      getLastNodeReturned().setValue(obj);
    }
    
    public void add(E obj) {
      checkModCount();
      parent.addNodeBefore(next, obj);
      current = null;
      nextIndex += 1;
      expectedModCount += 1;
    }
  }
  


  protected static class LinkedSubListIterator<E>
    extends AbstractLinkedList.LinkedListIterator<E>
  {
    protected final AbstractLinkedList.LinkedSubList<E> sub;
    


    protected LinkedSubListIterator(AbstractLinkedList.LinkedSubList<E> sub, int startIndex)
    {
      super(startIndex + offset);
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
  

  protected static class LinkedSubList<E>
    extends AbstractList<E>
  {
    AbstractLinkedList<E> parent;
    
    int offset;
    
    int size;
    
    int expectedModCount;
    

    protected LinkedSubList(AbstractLinkedList<E> parent, int fromIndex, int toIndex)
    {
      if (fromIndex < 0) {
        throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
      }
      if (toIndex > parent.size()) {
        throw new IndexOutOfBoundsException("toIndex = " + toIndex);
      }
      if (fromIndex > toIndex) {
        throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
      }
      this.parent = parent;
      offset = fromIndex;
      size = (toIndex - fromIndex);
      expectedModCount = modCount;
    }
    
    public int size()
    {
      checkModCount();
      return size;
    }
    
    public E get(int index)
    {
      rangeCheck(index, size);
      checkModCount();
      return parent.get(index + offset);
    }
    
    public void add(int index, E obj)
    {
      rangeCheck(index, size + 1);
      checkModCount();
      parent.add(index + offset, obj);
      expectedModCount = parent.modCount;
      size += 1;
      modCount += 1;
    }
    
    public E remove(int index)
    {
      rangeCheck(index, size);
      checkModCount();
      E result = parent.remove(index + offset);
      expectedModCount = parent.modCount;
      size -= 1;
      modCount += 1;
      return result;
    }
    
    public boolean addAll(Collection<? extends E> coll)
    {
      return addAll(size, coll);
    }
    
    public boolean addAll(int index, Collection<? extends E> coll)
    {
      rangeCheck(index, size + 1);
      int cSize = coll.size();
      if (cSize == 0) {
        return false;
      }
      
      checkModCount();
      parent.addAll(offset + index, coll);
      expectedModCount = parent.modCount;
      size += cSize;
      modCount += 1;
      return true;
    }
    
    public E set(int index, E obj)
    {
      rangeCheck(index, size);
      checkModCount();
      return parent.set(index + offset, obj);
    }
    
    public void clear()
    {
      checkModCount();
      Iterator<E> it = iterator();
      while (it.hasNext()) {
        it.next();
        it.remove();
      }
    }
    
    public Iterator<E> iterator()
    {
      checkModCount();
      return parent.createSubListIterator(this);
    }
    
    public ListIterator<E> listIterator(int index)
    {
      rangeCheck(index, size + 1);
      checkModCount();
      return parent.createSubListListIterator(this, index);
    }
    
    public List<E> subList(int fromIndexInclusive, int toIndexExclusive)
    {
      return new LinkedSubList(parent, fromIndexInclusive + offset, toIndexExclusive + offset);
    }
    
    protected void rangeCheck(int index, int beyond) {
      if ((index < 0) || (index >= beyond)) {
        throw new IndexOutOfBoundsException("Index '" + index + "' out of bounds for size '" + size + "'");
      }
    }
    
    protected void checkModCount() {
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
}
