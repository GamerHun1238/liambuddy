package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.Predicate;



























































public class PredicatedMap<K, V>
  extends AbstractInputCheckedMapDecorator<K, V>
  implements Serializable
{
  private static final long serialVersionUID = 7412622456128415156L;
  protected final Predicate<? super K> keyPredicate;
  protected final Predicate<? super V> valuePredicate;
  
  public static <K, V> PredicatedMap<K, V> predicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate)
  {
    return new PredicatedMap(map, keyPredicate, valuePredicate);
  }
  









  protected PredicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate)
  {
    super(map);
    this.keyPredicate = keyPredicate;
    this.valuePredicate = valuePredicate;
    
    Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<K, V> entry = (Map.Entry)it.next();
      validate(entry.getKey(), entry.getValue());
    }
  }
  






  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(map);
  }
  







  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    map = ((Map)in.readObject());
  }
  







  protected void validate(K key, V value)
  {
    if ((keyPredicate != null) && (!keyPredicate.evaluate(key))) {
      throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
    }
    if ((valuePredicate != null) && (!valuePredicate.evaluate(value))) {
      throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
    }
  }
  








  protected V checkSetValue(V value)
  {
    if (!valuePredicate.evaluate(value)) {
      throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
    }
    return value;
  }
  






  protected boolean isSetValueChecking()
  {
    return valuePredicate != null;
  }
  

  public V put(K key, V value)
  {
    validate(key, value);
    return map.put(key, value);
  }
  
  public void putAll(Map<? extends K, ? extends V> mapToCopy)
  {
    for (Map.Entry<? extends K, ? extends V> entry : mapToCopy.entrySet()) {
      validate(entry.getKey(), entry.getValue());
    }
    super.putAll(mapToCopy);
  }
}
