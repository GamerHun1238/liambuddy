package org.apache.commons.collections4.keyvalue;

import java.io.Serializable;
import java.util.Arrays;





















































public class MultiKey<K>
  implements Serializable
{
  private static final long serialVersionUID = 4465448607415788805L;
  private final K[] keys;
  private transient int hashCode;
  
  public MultiKey(K key1, K key2)
  {
    this((Object[])new Object[] { key1, key2 }, false);
  }
  










  public MultiKey(K key1, K key2, K key3)
  {
    this((Object[])new Object[] { key1, key2, key3 }, false);
  }
  











  public MultiKey(K key1, K key2, K key3, K key4)
  {
    this((Object[])new Object[] { key1, key2, key3, key4 }, false);
  }
  












  public MultiKey(K key1, K key2, K key3, K key4, K key5)
  {
    this((Object[])new Object[] { key1, key2, key3, key4, key5 }, false);
  }
  










  public MultiKey(K[] keys)
  {
    this(keys, true);
  }
  
























  public MultiKey(K[] keys, boolean makeClone)
  {
    if (keys == null) {
      throw new IllegalArgumentException("The array of keys must not be null");
    }
    if (makeClone) {
      this.keys = ((Object[])keys.clone());
    } else {
      this.keys = keys;
    }
    
    calculateHashCode(keys);
  }
  








  public K[] getKeys()
  {
    return (Object[])keys.clone();
  }
  










  public K getKey(int index)
  {
    return keys[index];
  }
  





  public int size()
  {
    return keys.length;
  }
  










  public boolean equals(Object other)
  {
    if (other == this) {
      return true;
    }
    if ((other instanceof MultiKey)) {
      MultiKey<?> otherMulti = (MultiKey)other;
      return Arrays.equals(keys, keys);
    }
    return false;
  }
  










  public int hashCode()
  {
    return hashCode;
  }
  





  public String toString()
  {
    return "MultiKey" + Arrays.toString(keys);
  }
  




  private void calculateHashCode(Object[] keys)
  {
    int total = 0;
    for (Object key : keys) {
      if (key != null) {
        total ^= key.hashCode();
      }
    }
    hashCode = total;
  }
  





  protected Object readResolve()
  {
    calculateHashCode(keys);
    return this;
  }
}
