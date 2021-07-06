package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;

























































public abstract class AbstractReferenceMap<K, V>
  extends AbstractHashedMap<K, V>
{
  private ReferenceStrength keyType;
  private ReferenceStrength valueType;
  private boolean purgeValues;
  private transient ReferenceQueue<Object> queue;
  protected AbstractReferenceMap() {}
  
  public static enum ReferenceStrength
  {
    HARD(0),  SOFT(1),  WEAK(2);
    



    public final int value;
    



    public static ReferenceStrength resolve(int value)
    {
      switch (value) {
      case 0: 
        return HARD;
      case 1: 
        return SOFT;
      case 2: 
        return WEAK;
      }
      throw new IllegalArgumentException();
    }
    
    private ReferenceStrength(int value)
    {
      this.value = value;
    }
  }
  
















































  protected AbstractReferenceMap(ReferenceStrength keyType, ReferenceStrength valueType, int capacity, float loadFactor, boolean purgeValues)
  {
    super(capacity, loadFactor);
    this.keyType = keyType;
    this.valueType = valueType;
    this.purgeValues = purgeValues;
  }
  



  protected void init()
  {
    queue = new ReferenceQueue();
  }
  






  public int size()
  {
    purgeBeforeRead();
    return super.size();
  }
  





  public boolean isEmpty()
  {
    purgeBeforeRead();
    return super.isEmpty();
  }
  






  public boolean containsKey(Object key)
  {
    purgeBeforeRead();
    Map.Entry<K, V> entry = getEntry(key);
    if (entry == null) {
      return false;
    }
    return entry.getValue() != null;
  }
  






  public boolean containsValue(Object value)
  {
    purgeBeforeRead();
    if (value == null) {
      return false;
    }
    return super.containsValue(value);
  }
  






  public V get(Object key)
  {
    purgeBeforeRead();
    Map.Entry<K, V> entry = getEntry(key);
    if (entry == null) {
      return null;
    }
    return entry.getValue();
  }
  










  public V put(K key, V value)
  {
    if (key == null) {
      throw new NullPointerException("null keys not allowed");
    }
    if (value == null) {
      throw new NullPointerException("null values not allowed");
    }
    
    purgeBeforeWrite();
    return super.put(key, value);
  }
  






  public V remove(Object key)
  {
    if (key == null) {
      return null;
    }
    purgeBeforeWrite();
    return super.remove(key);
  }
  



  public void clear()
  {
    super.clear();
    while (queue.poll() != null) {}
  }
  







  public MapIterator<K, V> mapIterator()
  {
    return new ReferenceMapIterator(this);
  }
  







  public Set<Map.Entry<K, V>> entrySet()
  {
    if (entrySet == null) {
      entrySet = new ReferenceEntrySet(this);
    }
    return entrySet;
  }
  





  public Set<K> keySet()
  {
    if (keySet == null) {
      keySet = new ReferenceKeySet(this);
    }
    return keySet;
  }
  





  public Collection<V> values()
  {
    if (values == null) {
      values = new ReferenceValues(this);
    }
    return values;
  }
  





  protected void purgeBeforeRead()
  {
    purge();
  }
  




  protected void purgeBeforeWrite()
  {
    purge();
  }
  







  protected void purge()
  {
    Reference<?> ref = queue.poll();
    while (ref != null) {
      purge(ref);
      ref = queue.poll();
    }
  }
  







  protected void purge(Reference<?> ref)
  {
    int hash = ref.hashCode();
    int index = hashIndex(hash, data.length);
    AbstractHashedMap.HashEntry<K, V> previous = null;
    AbstractHashedMap.HashEntry<K, V> entry = data[index];
    while (entry != null) {
      if (((ReferenceEntry)entry).purge(ref)) {
        if (previous == null) {
          data[index] = next;
        } else {
          next = next;
        }
        size -= 1;
        return;
      }
      previous = entry;
      entry = next;
    }
  }
  








  protected AbstractHashedMap.HashEntry<K, V> getEntry(Object key)
  {
    if (key == null) {
      return null;
    }
    return super.getEntry(key);
  }
  







  protected int hashEntry(Object key, Object value)
  {
    return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
  }
  












  protected boolean isEqualKey(Object key1, Object key2)
  {
    key2 = keyType == ReferenceStrength.HARD ? key2 : ((Reference)key2).get();
    return (key1 == key2) || (key1.equals(key2));
  }
  










  protected ReferenceEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value)
  {
    return new ReferenceEntry(this, next, hashCode, key, value);
  }
  





  protected Iterator<Map.Entry<K, V>> createEntrySetIterator()
  {
    return new ReferenceEntrySetIterator(this);
  }
  





  protected Iterator<K> createKeySetIterator()
  {
    return new ReferenceKeySetIterator(this);
  }
  





  protected Iterator<V> createValuesIterator()
  {
    return new ReferenceValuesIterator(this);
  }
  


  static class ReferenceEntrySet<K, V>
    extends AbstractHashedMap.EntrySet<K, V>
  {
    protected ReferenceEntrySet(AbstractHashedMap<K, V> parent)
    {
      super();
    }
    
    public Object[] toArray()
    {
      return toArray(new Object[size()]);
    }
    

    public <T> T[] toArray(T[] arr)
    {
      ArrayList<Map.Entry<K, V>> list = new ArrayList(size());
      for (Map.Entry<K, V> entry : this) {
        list.add(new DefaultMapEntry(entry));
      }
      return list.toArray(arr);
    }
  }
  


  static class ReferenceKeySet<K>
    extends AbstractHashedMap.KeySet<K>
  {
    protected ReferenceKeySet(AbstractHashedMap<K, ?> parent)
    {
      super();
    }
    
    public Object[] toArray()
    {
      return toArray(new Object[size()]);
    }
    

    public <T> T[] toArray(T[] arr)
    {
      List<K> list = new ArrayList(size());
      for (K key : this) {
        list.add(key);
      }
      return list.toArray(arr);
    }
  }
  


  static class ReferenceValues<V>
    extends AbstractHashedMap.Values<V>
  {
    protected ReferenceValues(AbstractHashedMap<?, V> parent)
    {
      super();
    }
    
    public Object[] toArray()
    {
      return toArray(new Object[size()]);
    }
    

    public <T> T[] toArray(T[] arr)
    {
      List<V> list = new ArrayList(size());
      for (V value : this) {
        list.add(value);
      }
      return list.toArray(arr);
    }
  }
  









  protected static class ReferenceEntry<K, V>
    extends AbstractHashedMap.HashEntry<K, V>
  {
    private final AbstractReferenceMap<K, V> parent;
    








    public ReferenceEntry(AbstractReferenceMap<K, V> parent, AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value)
    {
      super(hashCode, null, null);
      this.parent = parent;
      this.key = toReference(keyType, key, hashCode);
      this.value = toReference(valueType, value, hashCode);
    }
    







    public K getKey()
    {
      return parent.keyType == AbstractReferenceMap.ReferenceStrength.HARD ? key : ((Reference)key).get();
    }
    







    public V getValue()
    {
      return parent.valueType == AbstractReferenceMap.ReferenceStrength.HARD ? value : ((Reference)value).get();
    }
    







    public V setValue(V obj)
    {
      V old = getValue();
      if (parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD) {
        ((Reference)value).clear();
      }
      value = toReference(parent.valueType, obj, hashCode);
      return old;
    }
    









    public boolean equals(Object obj)
    {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof Map.Entry)) {
        return false;
      }
      
      Map.Entry<?, ?> entry = (Map.Entry)obj;
      Object entryKey = entry.getKey();
      Object entryValue = entry.getValue();
      if ((entryKey == null) || (entryValue == null)) {
        return false;
      }
      

      return (parent.isEqualKey(entryKey, key)) && (parent.isEqualValue(entryValue, getValue()));
    }
    








    public int hashCode()
    {
      return parent.hashEntry(getKey(), getValue());
    }
    











    protected <T> Object toReference(AbstractReferenceMap.ReferenceStrength type, T referent, int hash)
    {
      if (type == AbstractReferenceMap.ReferenceStrength.HARD) {
        return referent;
      }
      if (type == AbstractReferenceMap.ReferenceStrength.SOFT) {
        return new AbstractReferenceMap.SoftRef(hash, referent, parent.queue);
      }
      if (type == AbstractReferenceMap.ReferenceStrength.WEAK) {
        return new AbstractReferenceMap.WeakRef(hash, referent, parent.queue);
      }
      throw new Error();
    }
    




    boolean purge(Reference<?> ref)
    {
      boolean r = (parent.keyType != AbstractReferenceMap.ReferenceStrength.HARD) && (key == ref);
      r = (r) || ((parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD) && (value == ref));
      if (r) {
        if (parent.keyType != AbstractReferenceMap.ReferenceStrength.HARD) {
          ((Reference)key).clear();
        }
        if (parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD) {
          ((Reference)value).clear();
        } else if (parent.purgeValues) {
          value = null;
        }
      }
      return r;
    }
    




    protected ReferenceEntry<K, V> next()
    {
      return (ReferenceEntry)next;
    }
  }
  

  static class ReferenceBaseIterator<K, V>
  {
    final AbstractReferenceMap<K, V> parent;
    
    int index;
    
    AbstractReferenceMap.ReferenceEntry<K, V> entry;
    
    AbstractReferenceMap.ReferenceEntry<K, V> previous;
    
    K currentKey;
    
    K nextKey;
    
    V currentValue;
    
    V nextValue;
    
    int expectedModCount;
    
    public ReferenceBaseIterator(AbstractReferenceMap<K, V> parent)
    {
      this.parent = parent;
      index = (parent.size() != 0 ? data.length : 0);
      

      expectedModCount = modCount;
    }
    
    public boolean hasNext() {
      checkMod();
      while (nextNull()) {
        AbstractReferenceMap.ReferenceEntry<K, V> e = entry;
        int i = index;
        while ((e == null) && (i > 0)) {
          i--;
          e = (AbstractReferenceMap.ReferenceEntry)parent.data[i];
        }
        entry = e;
        index = i;
        if (e == null) {
          currentKey = null;
          currentValue = null;
          return false;
        }
        nextKey = e.getKey();
        nextValue = e.getValue();
        if (nextNull()) {
          entry = entry.next();
        }
      }
      return true;
    }
    
    private void checkMod() {
      if (parent.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
    
    private boolean nextNull() {
      return (nextKey == null) || (nextValue == null);
    }
    
    protected AbstractReferenceMap.ReferenceEntry<K, V> nextEntry() {
      checkMod();
      if ((nextNull()) && (!hasNext())) {
        throw new NoSuchElementException();
      }
      previous = entry;
      entry = entry.next();
      currentKey = nextKey;
      currentValue = nextValue;
      nextKey = null;
      nextValue = null;
      return previous;
    }
    
    protected AbstractReferenceMap.ReferenceEntry<K, V> currentEntry() {
      checkMod();
      return previous;
    }
    
    public void remove() {
      checkMod();
      if (previous == null) {
        throw new IllegalStateException();
      }
      parent.remove(currentKey);
      previous = null;
      currentKey = null;
      currentValue = null;
      expectedModCount = parent.modCount;
    }
  }
  

  static class ReferenceEntrySetIterator<K, V>
    extends AbstractReferenceMap.ReferenceBaseIterator<K, V>
    implements Iterator<Map.Entry<K, V>>
  {
    public ReferenceEntrySetIterator(AbstractReferenceMap<K, V> parent)
    {
      super();
    }
    
    public Map.Entry<K, V> next() {
      return nextEntry();
    }
  }
  


  static class ReferenceKeySetIterator<K>
    extends AbstractReferenceMap.ReferenceBaseIterator<K, Object>
    implements Iterator<K>
  {
    ReferenceKeySetIterator(AbstractReferenceMap<K, ?> parent)
    {
      super();
    }
    
    public K next() {
      return nextEntry().getKey();
    }
  }
  

  static class ReferenceValuesIterator<V>
    extends AbstractReferenceMap.ReferenceBaseIterator<Object, V>
    implements Iterator<V>
  {
    ReferenceValuesIterator(AbstractReferenceMap<?, V> parent)
    {
      super();
    }
    
    public V next() {
      return nextEntry().getValue();
    }
  }
  
  static class ReferenceMapIterator<K, V>
    extends AbstractReferenceMap.ReferenceBaseIterator<K, V>
    implements MapIterator<K, V>
  {
    protected ReferenceMapIterator(AbstractReferenceMap<K, V> parent)
    {
      super();
    }
    
    public K next() {
      return nextEntry().getKey();
    }
    
    public K getKey() {
      AbstractHashedMap.HashEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("getKey() can only be called after next() and before remove()");
      }
      return current.getKey();
    }
    
    public V getValue() {
      AbstractHashedMap.HashEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("getValue() can only be called after next() and before remove()");
      }
      return current.getValue();
    }
    
    public V setValue(V value) {
      AbstractHashedMap.HashEntry<K, V> current = currentEntry();
      if (current == null) {
        throw new IllegalStateException("setValue() can only be called after next() and before remove()");
      }
      return current.setValue(value);
    }
  }
  



  static class SoftRef<T>
    extends SoftReference<T>
  {
    private final int hash;
    



    public SoftRef(int hash, T r, ReferenceQueue<? super T> q)
    {
      super(q);
      this.hash = hash;
    }
    
    public int hashCode()
    {
      return hash;
    }
  }
  

  static class WeakRef<T>
    extends WeakReference<T>
  {
    private final int hash;
    
    public WeakRef(int hash, T r, ReferenceQueue<? super T> q)
    {
      super(q);
      this.hash = hash;
    }
    
    public int hashCode()
    {
      return hash;
    }
  }
  



















  protected void doWriteObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(keyType.value);
    out.writeInt(valueType.value);
    out.writeBoolean(purgeValues);
    out.writeFloat(loadFactor);
    out.writeInt(data.length);
    for (MapIterator<K, V> it = mapIterator(); it.hasNext();) {
      out.writeObject(it.next());
      out.writeObject(it.getValue());
    }
    out.writeObject(null);
  }
  




















  protected void doReadObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    keyType = ReferenceStrength.resolve(in.readInt());
    valueType = ReferenceStrength.resolve(in.readInt());
    purgeValues = in.readBoolean();
    loadFactor = in.readFloat();
    int capacity = in.readInt();
    init();
    data = new AbstractHashedMap.HashEntry[capacity];
    for (;;) {
      K key = in.readObject();
      if (key == null) {
        break;
      }
      V value = in.readObject();
      put(key, value);
    }
    threshold = calculateThreshold(data.length, loadFactor);
  }
  





  protected boolean isKeyType(ReferenceStrength type)
  {
    return keyType == type;
  }
}
