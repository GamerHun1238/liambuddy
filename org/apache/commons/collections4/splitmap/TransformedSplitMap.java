package org.apache.commons.collections4.splitmap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.Put;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.LinkedMap;





































































public class TransformedSplitMap<J, K, U, V>
  extends AbstractIterableGetMapDecorator<K, V>
  implements Put<J, U>, Serializable
{
  private static final long serialVersionUID = 5966875321133456994L;
  private final Transformer<? super J, ? extends K> keyTransformer;
  private final Transformer<? super U, ? extends V> valueTransformer;
  
  public static <J, K, U, V> TransformedSplitMap<J, K, U, V> transformingMap(Map<K, V> map, Transformer<? super J, ? extends K> keyTransformer, Transformer<? super U, ? extends V> valueTransformer)
  {
    return new TransformedSplitMap(map, keyTransformer, valueTransformer);
  }
  












  protected TransformedSplitMap(Map<K, V> map, Transformer<? super J, ? extends K> keyTransformer, Transformer<? super U, ? extends V> valueTransformer)
  {
    super(map);
    if (keyTransformer == null) {
      throw new NullPointerException("KeyTransformer must not be null.");
    }
    this.keyTransformer = keyTransformer;
    if (valueTransformer == null) {
      throw new NullPointerException("ValueTransformer must not be null.");
    }
    this.valueTransformer = valueTransformer;
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(decorated());
  }
  







  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    map = ((Map)in.readObject());
  }
  








  protected K transformKey(J object)
  {
    return keyTransformer.transform(object);
  }
  







  protected V transformValue(U object)
  {
    return valueTransformer.transform(object);
  }
  








  protected Map<K, V> transformMap(Map<? extends J, ? extends U> map)
  {
    if (map.isEmpty()) {
      return map;
    }
    Map<K, V> result = new LinkedMap(map.size());
    
    for (Map.Entry<? extends J, ? extends U> entry : map.entrySet()) {
      result.put(transformKey(entry.getKey()), transformValue(entry.getValue()));
    }
    return result;
  }
  





  protected V checkSetValue(U value)
  {
    return valueTransformer.transform(value);
  }
  
  public V put(J key, U value)
  {
    return decorated().put(transformKey(key), transformValue(value));
  }
  
  public void putAll(Map<? extends J, ? extends U> mapToCopy) {
    decorated().putAll(transformMap(mapToCopy));
  }
  
  public void clear() {
    decorated().clear();
  }
}
