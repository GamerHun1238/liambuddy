package org.apache.commons.collections4.list;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.OrderedIterator;
































































public class TreeList<E>
  extends AbstractList<E>
{
  private AVLNode<E> root;
  private int size;
  
  public TreeList() {}
  
  public TreeList(Collection<? extends E> coll)
  {
    if (!coll.isEmpty()) {
      root = new AVLNode(coll, null);
      size = coll.size();
    }
  }
  







  public E get(int index)
  {
    checkInterval(index, 0, size() - 1);
    return root.get(index).getValue();
  }
  





  public int size()
  {
    return size;
  }
  






  public Iterator<E> iterator()
  {
    return listIterator(0);
  }
  






  public ListIterator<E> listIterator()
  {
    return listIterator(0);
  }
  








  public ListIterator<E> listIterator(int fromIndex)
  {
    checkInterval(fromIndex, 0, size());
    return new TreeListIterator(this, fromIndex);
  }
  







  public int indexOf(Object object)
  {
    if (root == null) {
      return -1;
    }
    return root.indexOf(object, root.relativePosition);
  }
  






  public boolean contains(Object object)
  {
    return indexOf(object) >= 0;
  }
  






  public Object[] toArray()
  {
    Object[] array = new Object[size()];
    if (root != null) {
      root.toArray(array, root.relativePosition);
    }
    return array;
  }
  







  public void add(int index, E obj)
  {
    modCount += 1;
    checkInterval(index, 0, size());
    if (root == null) {
      root = new AVLNode(index, obj, null, null, null);
    } else {
      root = root.insert(index, obj);
    }
    size += 1;
  }
  











  public boolean addAll(Collection<? extends E> c)
  {
    if (c.isEmpty()) {
      return false;
    }
    modCount += c.size();
    AVLNode<E> cTree = new AVLNode(c, null);
    root = (root == null ? cTree : root.addAll(cTree, size));
    size += c.size();
    return true;
  }
  








  public E set(int index, E obj)
  {
    checkInterval(index, 0, size() - 1);
    AVLNode<E> node = root.get(index);
    E result = value;
    node.setValue(obj);
    return result;
  }
  






  public E remove(int index)
  {
    modCount += 1;
    checkInterval(index, 0, size() - 1);
    E result = get(index);
    root = root.remove(index);
    size -= 1;
    return result;
  }
  



  public void clear()
  {
    modCount += 1;
    root = null;
    size = 0;
  }
  








  private void checkInterval(int index, int startIndex, int endIndex)
  {
    if ((index < startIndex) || (index > endIndex)) {
      throw new IndexOutOfBoundsException("Invalid index:" + index + ", size=" + size());
    }
  }
  




  static class AVLNode<E>
  {
    private AVLNode<E> left;
    



    private boolean leftIsPrevious;
    



    private AVLNode<E> right;
    


    private boolean rightIsNext;
    


    private int height;
    


    private int relativePosition;
    


    private E value;
    



    private AVLNode(int relativePosition, E obj, AVLNode<E> rightFollower, AVLNode<E> leftFollower)
    {
      this.relativePosition = relativePosition;
      value = obj;
      rightIsNext = true;
      leftIsPrevious = true;
      right = rightFollower;
      left = leftFollower;
    }
    






    private AVLNode(Collection<? extends E> coll)
    {
      this(coll.iterator(), 0, coll.size() - 1, 0, null, null);
    }
    





















    private AVLNode(Iterator<? extends E> iterator, int start, int end, int absolutePositionOfParent, AVLNode<E> prev, AVLNode<E> next)
    {
      int mid = start + (end - start) / 2;
      if (start < mid) {
        left = new AVLNode(iterator, start, mid - 1, mid, prev, this);
      } else {
        leftIsPrevious = true;
        left = prev;
      }
      value = iterator.next();
      relativePosition = (mid - absolutePositionOfParent);
      if (mid < end) {
        right = new AVLNode(iterator, mid + 1, end, mid, this, next);
      } else {
        rightIsNext = true;
        right = next;
      }
      recalcHeight();
    }
    




    E getValue()
    {
      return value;
    }
    




    void setValue(E obj)
    {
      value = obj;
    }
    



    AVLNode<E> get(int index)
    {
      int indexRelativeToMe = index - relativePosition;
      
      if (indexRelativeToMe == 0) {
        return this;
      }
      
      AVLNode<E> nextNode = indexRelativeToMe < 0 ? getLeftSubTree() : getRightSubTree();
      if (nextNode == null) {
        return null;
      }
      return nextNode.get(indexRelativeToMe);
    }
    


    int indexOf(Object object, int index)
    {
      if (getLeftSubTree() != null) {
        int result = left.indexOf(object, index + left.relativePosition);
        if (result != -1) {
          return result;
        }
      }
      if (value == null ? value == object : value.equals(object)) {
        return index;
      }
      if (getRightSubTree() != null) {
        return right.indexOf(object, index + right.relativePosition);
      }
      return -1;
    }
    





    void toArray(Object[] array, int index)
    {
      array[index] = value;
      if (getLeftSubTree() != null) {
        left.toArray(array, index + left.relativePosition);
      }
      if (getRightSubTree() != null) {
        right.toArray(array, index + right.relativePosition);
      }
    }
    




    AVLNode<E> next()
    {
      if ((rightIsNext) || (right == null)) {
        return right;
      }
      return right.min();
    }
    




    AVLNode<E> previous()
    {
      if ((leftIsPrevious) || (left == null)) {
        return left;
      }
      return left.max();
    }
    






    AVLNode<E> insert(int index, E obj)
    {
      int indexRelativeToMe = index - relativePosition;
      
      if (indexRelativeToMe <= 0) {
        return insertOnLeft(indexRelativeToMe, obj);
      }
      return insertOnRight(indexRelativeToMe, obj);
    }
    
    private AVLNode<E> insertOnLeft(int indexRelativeToMe, E obj) {
      if (getLeftSubTree() == null) {
        setLeft(new AVLNode(-1, obj, this, left), null);
      } else {
        setLeft(left.insert(indexRelativeToMe, obj), null);
      }
      
      if (relativePosition >= 0) {
        relativePosition += 1;
      }
      AVLNode<E> ret = balance();
      recalcHeight();
      return ret;
    }
    
    private AVLNode<E> insertOnRight(int indexRelativeToMe, E obj) {
      if (getRightSubTree() == null) {
        setRight(new AVLNode(1, obj, right, this), null);
      } else {
        setRight(right.insert(indexRelativeToMe, obj), null);
      }
      if (relativePosition < 0) {
        relativePosition -= 1;
      }
      AVLNode<E> ret = balance();
      recalcHeight();
      return ret;
    }
    



    private AVLNode<E> getLeftSubTree()
    {
      return leftIsPrevious ? null : left;
    }
    


    private AVLNode<E> getRightSubTree()
    {
      return rightIsNext ? null : right;
    }
    




    private AVLNode<E> max()
    {
      return getRightSubTree() == null ? this : right.max();
    }
    




    private AVLNode<E> min()
    {
      return getLeftSubTree() == null ? this : left.min();
    }
    





    AVLNode<E> remove(int index)
    {
      int indexRelativeToMe = index - relativePosition;
      
      if (indexRelativeToMe == 0) {
        return removeSelf();
      }
      if (indexRelativeToMe > 0) {
        setRight(right.remove(indexRelativeToMe), right.right);
        if (relativePosition < 0) {
          relativePosition += 1;
        }
      } else {
        setLeft(left.remove(indexRelativeToMe), left.left);
        if (relativePosition > 0) {
          relativePosition -= 1;
        }
      }
      recalcHeight();
      return balance();
    }
    
    private AVLNode<E> removeMax() {
      if (getRightSubTree() == null) {
        return removeSelf();
      }
      setRight(right.removeMax(), right.right);
      if (relativePosition < 0) {
        relativePosition += 1;
      }
      recalcHeight();
      return balance();
    }
    
    private AVLNode<E> removeMin() {
      if (getLeftSubTree() == null) {
        return removeSelf();
      }
      setLeft(left.removeMin(), left.left);
      if (relativePosition > 0) {
        relativePosition -= 1;
      }
      recalcHeight();
      return balance();
    }
    




    private AVLNode<E> removeSelf()
    {
      if ((getRightSubTree() == null) && (getLeftSubTree() == null)) {
        return null;
      }
      if (getRightSubTree() == null) {
        if (relativePosition > 0) {
          left.relativePosition += relativePosition + (relativePosition > 0 ? 0 : 1);
        }
        left.max().setRight(null, right);
        return left;
      }
      if (getLeftSubTree() == null) {
        right.relativePosition += relativePosition - (relativePosition < 0 ? 0 : 1);
        right.min().setLeft(null, left);
        return right;
      }
      
      if (heightRightMinusLeft() > 0)
      {
        AVLNode<E> rightMin = right.min();
        value = value;
        if (leftIsPrevious) {
          left = left;
        }
        right = right.removeMin();
        if (relativePosition < 0) {
          relativePosition += 1;
        }
      }
      else {
        AVLNode<E> leftMax = left.max();
        value = value;
        if (rightIsNext) {
          right = right;
        }
        AVLNode<E> leftPrevious = left.left;
        left = left.removeMax();
        if (left == null)
        {

          left = leftPrevious;
          leftIsPrevious = true;
        }
        if (relativePosition > 0) {
          relativePosition -= 1;
        }
      }
      recalcHeight();
      return this;
    }
    



    private AVLNode<E> balance()
    {
      switch (heightRightMinusLeft()) {
      case -1: 
      case 0: 
      case 1: 
        return this;
      case -2: 
        if (left.heightRightMinusLeft() > 0) {
          setLeft(left.rotateLeft(), null);
        }
        return rotateRight();
      case 2: 
        if (right.heightRightMinusLeft() < 0) {
          setRight(right.rotateRight(), null);
        }
        return rotateLeft();
      }
      throw new RuntimeException("tree inconsistent!");
    }
    



    private int getOffset(AVLNode<E> node)
    {
      if (node == null) {
        return 0;
      }
      return relativePosition;
    }
    


    private int setOffset(AVLNode<E> node, int newOffest)
    {
      if (node == null) {
        return 0;
      }
      int oldOffset = getOffset(node);
      relativePosition = newOffest;
      return oldOffset;
    }
    


    private void recalcHeight()
    {
      height = (Math.max(getLeftSubTree() == null ? -1 : getLeftSubTreeheight, getRightSubTree() == null ? -1 : getRightSubTreeheight) + 1);
    }
    




    private int getHeight(AVLNode<E> node)
    {
      return node == null ? -1 : height;
    }
    


    private int heightRightMinusLeft()
    {
      return getHeight(getRightSubTree()) - getHeight(getLeftSubTree());
    }
    
    private AVLNode<E> rotateLeft() {
      AVLNode<E> newTop = right;
      AVLNode<E> movedNode = getRightSubTree().getLeftSubTree();
      
      int newTopPosition = relativePosition + getOffset(newTop);
      int myNewPosition = -relativePosition;
      int movedPosition = getOffset(newTop) + getOffset(movedNode);
      
      setRight(movedNode, newTop);
      newTop.setLeft(this, null);
      
      setOffset(newTop, newTopPosition);
      setOffset(this, myNewPosition);
      setOffset(movedNode, movedPosition);
      return newTop;
    }
    
    private AVLNode<E> rotateRight() {
      AVLNode<E> newTop = left;
      AVLNode<E> movedNode = getLeftSubTree().getRightSubTree();
      
      int newTopPosition = relativePosition + getOffset(newTop);
      int myNewPosition = -relativePosition;
      int movedPosition = getOffset(newTop) + getOffset(movedNode);
      
      setLeft(movedNode, newTop);
      newTop.setRight(this, null);
      
      setOffset(newTop, newTopPosition);
      setOffset(this, myNewPosition);
      setOffset(movedNode, movedPosition);
      return newTop;
    }
    





    private void setLeft(AVLNode<E> node, AVLNode<E> previous)
    {
      leftIsPrevious = (node == null);
      left = (leftIsPrevious ? previous : node);
      recalcHeight();
    }
    





    private void setRight(AVLNode<E> node, AVLNode<E> next)
    {
      rightIsNext = (node == null);
      right = (rightIsNext ? next : node);
      recalcHeight();
    }
    










    private AVLNode<E> addAll(AVLNode<E> otherTree, int currentSize)
    {
      AVLNode<E> maxNode = max();
      AVLNode<E> otherTreeMin = otherTree.min();
      






      if (height > height)
      {



        AVLNode<E> leftSubTree = removeMax();
        




        Deque<AVLNode<E>> sAncestors = new ArrayDeque();
        AVLNode<E> s = otherTree;
        int sAbsolutePosition = relativePosition + currentSize;
        int sParentAbsolutePosition = 0;
        while ((s != null) && (height > getHeight(leftSubTree))) {
          sParentAbsolutePosition = sAbsolutePosition;
          sAncestors.push(s);
          s = left;
          if (s != null) {
            sAbsolutePosition += relativePosition;
          }
        }
        



        maxNode.setLeft(leftSubTree, null);
        maxNode.setRight(s, otherTreeMin);
        if (leftSubTree != null) {
          leftSubTree.max().setRight(null, maxNode);
          relativePosition -= currentSize - 1;
        }
        if (s != null) {
          s.min().setLeft(null, maxNode);
          relativePosition = (sAbsolutePosition - currentSize + 1);
        }
        relativePosition = (currentSize - 1 - sParentAbsolutePosition);
        relativePosition += currentSize;
        

        s = maxNode;
        while (!sAncestors.isEmpty()) {
          AVLNode<E> sAncestor = (AVLNode)sAncestors.pop();
          sAncestor.setLeft(s, null);
          s = sAncestor.balance();
        }
        return s;
      }
      otherTree = otherTree.removeMin();
      
      Deque<AVLNode<E>> sAncestors = new ArrayDeque();
      AVLNode<E> s = this;
      int sAbsolutePosition = relativePosition;
      int sParentAbsolutePosition = 0;
      while ((s != null) && (height > getHeight(otherTree))) {
        sParentAbsolutePosition = sAbsolutePosition;
        sAncestors.push(s);
        s = right;
        if (s != null) {
          sAbsolutePosition += relativePosition;
        }
      }
      
      otherTreeMin.setRight(otherTree, null);
      otherTreeMin.setLeft(s, maxNode);
      if (otherTree != null) {
        otherTree.min().setLeft(null, otherTreeMin);
        relativePosition += 1;
      }
      if (s != null) {
        s.max().setRight(null, otherTreeMin);
        relativePosition = (sAbsolutePosition - currentSize);
      }
      relativePosition = (currentSize - sParentAbsolutePosition);
      
      s = otherTreeMin;
      while (!sAncestors.isEmpty()) {
        AVLNode<E> sAncestor = (AVLNode)sAncestors.pop();
        sAncestor.setRight(s, null);
        s = sAncestor.balance();
      }
      return s;
    }
    























































    public String toString()
    {
      return "AVLNode(" + relativePosition + ',' + (left != null) + ',' + value + ',' + (getRightSubTree() != null) + ", faedelung " + rightIsNext + " )";
    }
  }
  






  static class TreeListIterator<E>
    implements ListIterator<E>, OrderedIterator<E>
  {
    private final TreeList<E> parent;
    





    private TreeList.AVLNode<E> next;
    





    private int nextIndex;
    




    private TreeList.AVLNode<E> current;
    




    private int currentIndex;
    




    private int expectedModCount;
    





    protected TreeListIterator(TreeList<E> parent, int fromIndex)
      throws IndexOutOfBoundsException
    {
      this.parent = parent;
      expectedModCount = modCount;
      next = (root == null ? null : root.get(fromIndex));
      nextIndex = fromIndex;
      currentIndex = -1;
    }
    






    protected void checkModCount()
    {
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
    
    public boolean hasNext() {
      return nextIndex < parent.size();
    }
    
    public E next() {
      checkModCount();
      if (!hasNext()) {
        throw new NoSuchElementException("No element at index " + nextIndex + ".");
      }
      if (next == null) {
        next = parent.root.get(nextIndex);
      }
      E value = next.getValue();
      current = next;
      currentIndex = (nextIndex++);
      next = next.next();
      return value;
    }
    
    public boolean hasPrevious() {
      return nextIndex > 0;
    }
    
    public E previous() {
      checkModCount();
      if (!hasPrevious()) {
        throw new NoSuchElementException("Already at start of list.");
      }
      if (next == null) {
        next = parent.root.get(nextIndex - 1);
      } else {
        next = next.previous();
      }
      E value = next.getValue();
      current = next;
      currentIndex = (--nextIndex);
      return value;
    }
    
    public int nextIndex() {
      return nextIndex;
    }
    
    public int previousIndex() {
      return nextIndex() - 1;
    }
    
    public void remove() {
      checkModCount();
      if (currentIndex == -1) {
        throw new IllegalStateException();
      }
      parent.remove(currentIndex);
      if (nextIndex != currentIndex)
      {
        nextIndex -= 1;
      }
      

      next = null;
      current = null;
      currentIndex = -1;
      expectedModCount += 1;
    }
    
    public void set(E obj) {
      checkModCount();
      if (current == null) {
        throw new IllegalStateException();
      }
      current.setValue(obj);
    }
    
    public void add(E obj) {
      checkModCount();
      parent.add(nextIndex, obj);
      current = null;
      currentIndex = -1;
      nextIndex += 1;
      expectedModCount += 1;
    }
  }
}
