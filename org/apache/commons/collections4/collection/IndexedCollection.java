package org.apache.commons.collections4.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.MultiValueMap;























































public class IndexedCollection<K, C>
  extends AbstractCollectionDecorator<C>
{
  private static final long serialVersionUID = -5512610452568370038L;
  private final Transformer<C, K> keyTransformer;
  private final MultiMap<K, C> index;
  private final boolean uniqueIndex;
  
  public static <K, C> IndexedCollection<K, C> uniqueIndexedCollection(Collection<C> coll, Transformer<C, K> keyTransformer)
  {
    return new IndexedCollection(coll, keyTransformer, MultiValueMap.multiValueMap(new HashMap()), true);
  }
  











  public static <K, C> IndexedCollection<K, C> nonUniqueIndexedCollection(Collection<C> coll, Transformer<C, K> keyTransformer)
  {
    return new IndexedCollection(coll, keyTransformer, MultiValueMap.multiValueMap(new HashMap()), false);
  }
  










  public IndexedCollection(Collection<C> coll, Transformer<C, K> keyTransformer, MultiMap<K, C> map, boolean uniqueIndex)
  {
    super(coll);
    this.keyTransformer = keyTransformer;
    index = map;
    this.uniqueIndex = uniqueIndex;
    reindex();
  }
  






  public boolean add(C object)
  {
    boolean added = super.add(object);
    if (added) {
      addToIndex(object);
    }
    return added;
  }
  
  public boolean addAll(Collection<? extends C> coll)
  {
    boolean changed = false;
    for (C c : coll) {
      changed |= add(c);
    }
    return changed;
  }
  
  public void clear()
  {
    super.clear();
    index.clear();
  }
  






  public boolean contains(Object object)
  {
    return index.containsKey(keyTransformer.transform(object));
  }
  





  public boolean containsAll(Collection<?> coll)
  {
    for (Object o : coll) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }
  











  public C get(K key)
  {
    Collection<C> coll = (Collection)index.get(key);
    return coll == null ? null : coll.iterator().next();
  }
  






  public Collection<C> values(K key)
  {
    return (Collection)index.get(key);
  }
  


  public void reindex()
  {
    index.clear();
    for (C c : decorated()) {
      addToIndex(c);
    }
  }
  

  public boolean remove(Object object)
  {
    boolean removed = super.remove(object);
    if (removed) {
      removeFromIndex(object);
    }
    return removed;
  }
  
  public boolean removeAll(Collection<?> coll)
  {
    boolean changed = false;
    for (Object o : coll) {
      changed |= remove(o);
    }
    return changed;
  }
  
  public boolean retainAll(Collection<?> coll)
  {
    boolean changed = super.retainAll(coll);
    if (changed) {
      reindex();
    }
    return changed;
  }
  








  private void addToIndex(C object)
  {
    K key = keyTransformer.transform(object);
    if ((uniqueIndex) && (index.containsKey(key))) {
      throw new IllegalArgumentException("Duplicate key in uniquely indexed collection.");
    }
    index.put(key, object);
  }
  




  private void removeFromIndex(C object)
  {
    index.remove(keyTransformer.transform(object));
  }
}
