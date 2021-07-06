package org.apache.commons.collections4.map;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections4.set.AbstractSetDecorator;












































abstract class AbstractInputCheckedMapDecorator<K, V>
  extends AbstractMapDecorator<K, V>
{
  protected AbstractInputCheckedMapDecorator() {}
  
  protected AbstractInputCheckedMapDecorator(Map<K, V> map)
  {
    super(map);
  }
  












  protected abstract V checkSetValue(V paramV);
  












  protected boolean isSetValueChecking()
  {
    return true;
  }
  

  public Set<Map.Entry<K, V>> entrySet()
  {
    if (isSetValueChecking()) {
      return new EntrySet(map.entrySet(), this);
    }
    return map.entrySet();
  }
  


  private class EntrySet
    extends AbstractSetDecorator<Map.Entry<K, V>>
  {
    private static final long serialVersionUID = 4354731610923110264L;
    

    private final AbstractInputCheckedMapDecorator<K, V> parent;
    

    protected EntrySet(AbstractInputCheckedMapDecorator<K, V> set)
    {
      super();
      this.parent = parent;
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new AbstractInputCheckedMapDecorator.EntrySetIterator(AbstractInputCheckedMapDecorator.this, decorated().iterator(), parent);
    }
    

    public Object[] toArray()
    {
      Object[] array = decorated().toArray();
      for (int i = 0; i < array.length; i++) {
        array[i] = new AbstractInputCheckedMapDecorator.MapEntry(AbstractInputCheckedMapDecorator.this, (Map.Entry)array[i], parent);
      }
      return array;
    }
    

    public <T> T[] toArray(T[] array)
    {
      Object[] result = array;
      if (array.length > 0)
      {

        result = (Object[])Array.newInstance(array.getClass().getComponentType(), 0);
      }
      result = decorated().toArray(result);
      for (int i = 0; i < result.length; i++) {
        result[i] = new AbstractInputCheckedMapDecorator.MapEntry(AbstractInputCheckedMapDecorator.this, (Map.Entry)result[i], parent);
      }
      

      if (result.length > array.length) {
        return (Object[])result;
      }
      

      System.arraycopy(result, 0, array, 0, result.length);
      if (array.length > result.length) {
        array[result.length] = null;
      }
      return array;
    }
  }
  


  private class EntrySetIterator
    extends AbstractIteratorDecorator<Map.Entry<K, V>>
  {
    private final AbstractInputCheckedMapDecorator<K, V> parent;
    

    protected EntrySetIterator(AbstractInputCheckedMapDecorator<K, V> iterator)
    {
      super();
      this.parent = parent;
    }
    
    public Map.Entry<K, V> next()
    {
      Map.Entry<K, V> entry = (Map.Entry)getIterator().next();
      return new AbstractInputCheckedMapDecorator.MapEntry(AbstractInputCheckedMapDecorator.this, entry, parent);
    }
  }
  

  private class MapEntry
    extends AbstractMapEntryDecorator<K, V>
  {
    private final AbstractInputCheckedMapDecorator<K, V> parent;
    

    protected MapEntry(AbstractInputCheckedMapDecorator<K, V> entry)
    {
      super();
      this.parent = parent;
    }
    
    public V setValue(V value)
    {
      value = parent.checkSetValue(value);
      return getMapEntry().setValue(value);
    }
  }
}
