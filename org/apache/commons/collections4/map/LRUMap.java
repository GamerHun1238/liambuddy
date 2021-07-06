package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.BoundedMap;

























































public class LRUMap<K, V>
  extends AbstractLinkedMap<K, V>
  implements BoundedMap<K, V>, Serializable, Cloneable
{
  private static final long serialVersionUID = -612114643488955218L;
  protected static final int DEFAULT_MAX_SIZE = 100;
  private transient int maxSize;
  private boolean scanUntilRemovable;
  
  public LRUMap()
  {
    this(100, 0.75F, false);
  }
  





  public LRUMap(int maxSize)
  {
    this(maxSize, 0.75F);
  }
  








  public LRUMap(int maxSize, int initialSize)
  {
    this(maxSize, initialSize, 0.75F);
  }
  







  public LRUMap(int maxSize, boolean scanUntilRemovable)
  {
    this(maxSize, 0.75F, scanUntilRemovable);
  }
  








  public LRUMap(int maxSize, float loadFactor)
  {
    this(maxSize, loadFactor, false);
  }
  











  public LRUMap(int maxSize, int initialSize, float loadFactor)
  {
    this(maxSize, initialSize, loadFactor, false);
  }
  









  public LRUMap(int maxSize, float loadFactor, boolean scanUntilRemovable)
  {
    this(maxSize, maxSize, loadFactor, scanUntilRemovable);
  }
  















  public LRUMap(int maxSize, int initialSize, float loadFactor, boolean scanUntilRemovable)
  {
    super(initialSize, loadFactor);
    if (maxSize < 1) {
      throw new IllegalArgumentException("LRUMap max size must be greater than 0");
    }
    if (initialSize > maxSize) {
      throw new IllegalArgumentException("LRUMap initial size must not be greather than max size");
    }
    this.maxSize = maxSize;
    this.scanUntilRemovable = scanUntilRemovable;
  }
  








  public LRUMap(Map<? extends K, ? extends V> map)
  {
    this(map, false);
  }
  










  public LRUMap(Map<? extends K, ? extends V> map, boolean scanUntilRemovable)
  {
    this(map.size(), 0.75F, scanUntilRemovable);
    putAll(map);
  }
  










  public V get(Object key)
  {
    return get(key, true);
  }
  












  public V get(Object key, boolean updateToMRU)
  {
    AbstractLinkedMap.LinkEntry<K, V> entry = getEntry(key);
    if (entry == null) {
      return null;
    }
    if (updateToMRU) {
      moveToMRU(entry);
    }
    return entry.getValue();
  }
  







  protected void moveToMRU(AbstractLinkedMap.LinkEntry<K, V> entry)
  {
    if (after != header) {
      modCount += 1;
      
      if (before == null) {
        throw new IllegalStateException("Entry.before is null. Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug.");
      }
      

      before.after = after;
      after.before = before;
      
      after = header;
      before = header.before;
      header.before.after = entry;
      header.before = entry;
    } else if (entry == header) {
      throw new IllegalStateException("Can't move header to MRU (please report this to dev@commons.apache.org)");
    }
  }
  










  protected void updateEntry(AbstractHashedMap.HashEntry<K, V> entry, V newValue)
  {
    moveToMRU((AbstractLinkedMap.LinkEntry)entry);
    entry.setValue(newValue);
  }
  















  protected void addMapping(int hashIndex, int hashCode, K key, V value)
  {
    if (isFull()) {
      AbstractLinkedMap.LinkEntry<K, V> reuse = header.after;
      boolean removeLRUEntry = false;
      if (scanUntilRemovable) {
        while ((reuse != header) && (reuse != null)) {
          if (removeLRU(reuse)) {
            removeLRUEntry = true;
            break;
          }
          reuse = after;
        }
        if (reuse == null) {
          throw new IllegalStateException("Entry.after=null, header.after" + header.after + " header.before" + header.before + " key=" + key + " value=" + value + " size=" + size + " maxSize=" + maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
        }
        

      }
      else
      {
        removeLRUEntry = removeLRU(reuse);
      }
      
      if (removeLRUEntry) {
        if (reuse == null) {
          throw new IllegalStateException("reuse=null, header.after=" + header.after + " header.before" + header.before + " key=" + key + " value=" + value + " size=" + size + " maxSize=" + maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
        }
        



        reuseMapping(reuse, hashIndex, hashCode, key, value);
      } else {
        super.addMapping(hashIndex, hashCode, key, value);
      }
    } else {
      super.addMapping(hashIndex, hashCode, key, value);
    }
  }
  













  protected void reuseMapping(AbstractLinkedMap.LinkEntry<K, V> entry, int hashIndex, int hashCode, K key, V value)
  {
    try
    {
      int removeIndex = hashIndex(hashCode, data.length);
      AbstractHashedMap.HashEntry<K, V>[] tmp = data;
      AbstractHashedMap.HashEntry<K, V> loop = tmp[removeIndex];
      AbstractHashedMap.HashEntry<K, V> previous = null;
      while ((loop != entry) && (loop != null)) {
        previous = loop;
        loop = next;
      }
      if (loop == null) {
        throw new IllegalStateException("Entry.next=null, data[removeIndex]=" + data[removeIndex] + " previous=" + previous + " key=" + key + " value=" + value + " size=" + size + " maxSize=" + maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
      }
      





      modCount += 1;
      removeEntry(entry, removeIndex, previous);
      reuseEntry(entry, hashIndex, hashCode, key, value);
      addEntry(entry, hashIndex);
    } catch (NullPointerException ex) {
      throw new IllegalStateException("NPE, entry=" + entry + " entryIsHeader=" + (entry == header) + " key=" + key + " value=" + value + " size=" + size + " maxSize=" + maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
    }
  }
  





































  protected boolean removeLRU(AbstractLinkedMap.LinkEntry<K, V> entry)
  {
    return true;
  }
  





  public boolean isFull()
  {
    return size >= maxSize;
  }
  




  public int maxSize()
  {
    return maxSize;
  }
  






  public boolean isScanUntilRemovable()
  {
    return scanUntilRemovable;
  }
  






  public LRUMap<K, V> clone()
  {
    return (LRUMap)super.clone();
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
  





  protected void doWriteObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(maxSize);
    super.doWriteObject(out);
  }
  






  protected void doReadObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    maxSize = in.readInt();
    super.doReadObject(in);
  }
}
