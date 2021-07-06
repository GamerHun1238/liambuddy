package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;






















































public class NodeCachingLinkedList<E>
  extends AbstractLinkedList<E>
  implements Serializable
{
  private static final long serialVersionUID = 6897789178562232073L;
  private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 20;
  private transient AbstractLinkedList.Node<E> firstCachedNode;
  private transient int cacheSize;
  private int maximumCacheSize;
  
  public NodeCachingLinkedList()
  {
    this(20);
  }
  




  public NodeCachingLinkedList(Collection<? extends E> coll)
  {
    super(coll);
    maximumCacheSize = 20;
  }
  





  public NodeCachingLinkedList(int maximumCacheSize)
  {
    this.maximumCacheSize = maximumCacheSize;
    init();
  }
  





  protected int getMaximumCacheSize()
  {
    return maximumCacheSize;
  }
  




  protected void setMaximumCacheSize(int maximumCacheSize)
  {
    this.maximumCacheSize = maximumCacheSize;
    shrinkCacheToMaximumSize();
  }
  



  protected void shrinkCacheToMaximumSize()
  {
    while (cacheSize > maximumCacheSize) {
      getNodeFromCache();
    }
  }
  






  protected AbstractLinkedList.Node<E> getNodeFromCache()
  {
    if (cacheSize == 0) {
      return null;
    }
    AbstractLinkedList.Node<E> cachedNode = firstCachedNode;
    firstCachedNode = next;
    next = null;
    
    cacheSize -= 1;
    return cachedNode;
  }
  




  protected boolean isCacheFull()
  {
    return cacheSize >= maximumCacheSize;
  }
  





  protected void addNodeToCache(AbstractLinkedList.Node<E> node)
  {
    if (isCacheFull())
    {
      return;
    }
    
    AbstractLinkedList.Node<E> nextCachedNode = firstCachedNode;
    previous = null;
    next = nextCachedNode;
    node.setValue(null);
    firstCachedNode = node;
    cacheSize += 1;
  }
  








  protected AbstractLinkedList.Node<E> createNode(E value)
  {
    AbstractLinkedList.Node<E> cachedNode = getNodeFromCache();
    if (cachedNode == null) {
      return super.createNode(value);
    }
    cachedNode.setValue(value);
    return cachedNode;
  }
  






  protected void removeNode(AbstractLinkedList.Node<E> node)
  {
    super.removeNode(node);
    addNodeToCache(node);
  }
  









  protected void removeAllNodes()
  {
    int numberOfNodesToCache = Math.min(size, maximumCacheSize - cacheSize);
    AbstractLinkedList.Node<E> node = header.next;
    for (int currentIndex = 0; currentIndex < numberOfNodesToCache; currentIndex++) {
      AbstractLinkedList.Node<E> oldNode = node;
      node = next;
      addNodeToCache(oldNode);
    }
    super.removeAllNodes();
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
}
