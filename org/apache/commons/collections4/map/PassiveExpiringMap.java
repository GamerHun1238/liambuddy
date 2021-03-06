package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

























































public class PassiveExpiringMap<K, V>
  extends AbstractMapDecorator<K, V>
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public static abstract interface ExpirationPolicy<K, V>
    extends Serializable
  {
    public abstract long expirationTime(K paramK, V paramV);
  }
  
  public static class ConstantTimeToLiveExpirationPolicy<K, V>
    implements PassiveExpiringMap.ExpirationPolicy<K, V>
  {
    private static final long serialVersionUID = 1L;
    private final long timeToLiveMillis;
    
    public ConstantTimeToLiveExpirationPolicy()
    {
      this(-1L);
    }
    











    public ConstantTimeToLiveExpirationPolicy(long timeToLiveMillis)
    {
      this.timeToLiveMillis = timeToLiveMillis;
    }
    












    public ConstantTimeToLiveExpirationPolicy(long timeToLive, TimeUnit timeUnit)
    {
      this(PassiveExpiringMap.validateAndConvertToMillis(timeToLive, timeUnit));
    }
    









    public long expirationTime(K key, V value)
    {
      if (timeToLiveMillis >= 0L)
      {
        long now = System.currentTimeMillis();
        if (now > Long.MAX_VALUE - timeToLiveMillis)
        {

          return -1L;
        }
        

        return now + timeToLiveMillis;
      }
      

      return -1L;
    }
  }
  





































  private static long validateAndConvertToMillis(long timeToLive, TimeUnit timeUnit)
  {
    if (timeUnit == null) {
      throw new NullPointerException("Time unit must not be null");
    }
    return TimeUnit.MILLISECONDS.convert(timeToLive, timeUnit);
  }
  

  private final Map<Object, Long> expirationMap = new HashMap();
  


  private final ExpirationPolicy<K, V> expiringPolicy;
  


  public PassiveExpiringMap()
  {
    this(-1L);
  }
  







  public PassiveExpiringMap(ExpirationPolicy<K, V> expiringPolicy)
  {
    this(expiringPolicy, new HashMap());
  }
  











  public PassiveExpiringMap(ExpirationPolicy<K, V> expiringPolicy, Map<K, V> map)
  {
    super(map);
    if (expiringPolicy == null) {
      throw new NullPointerException("Policy must not be null.");
    }
    this.expiringPolicy = expiringPolicy;
  }
  









  public PassiveExpiringMap(long timeToLiveMillis)
  {
    this(new ConstantTimeToLiveExpirationPolicy(timeToLiveMillis), new HashMap());
  }
  














  public PassiveExpiringMap(long timeToLiveMillis, Map<K, V> map)
  {
    this(new ConstantTimeToLiveExpirationPolicy(timeToLiveMillis), map);
  }
  












  public PassiveExpiringMap(long timeToLive, TimeUnit timeUnit)
  {
    this(validateAndConvertToMillis(timeToLive, timeUnit));
  }
  















  public PassiveExpiringMap(long timeToLive, TimeUnit timeUnit, Map<K, V> map)
  {
    this(validateAndConvertToMillis(timeToLive, timeUnit), map);
  }
  







  public PassiveExpiringMap(Map<K, V> map)
  {
    this(-1L, map);
  }
  




  public void clear()
  {
    super.clear();
    expirationMap.clear();
  }
  





  public boolean containsKey(Object key)
  {
    removeIfExpired(key, now());
    return super.containsKey(key);
  }
  





  public boolean containsValue(Object value)
  {
    removeAllExpired(now());
    return super.containsValue(value);
  }
  




  public Set<Map.Entry<K, V>> entrySet()
  {
    removeAllExpired(now());
    return super.entrySet();
  }
  




  public V get(Object key)
  {
    removeIfExpired(key, now());
    return super.get(key);
  }
  




  public boolean isEmpty()
  {
    removeAllExpired(now());
    return super.isEmpty();
  }
  










  private boolean isExpired(long now, Long expirationTimeObject)
  {
    if (expirationTimeObject != null) {
      long expirationTime = expirationTimeObject.longValue();
      return (expirationTime >= 0L) && (now >= expirationTime);
    }
    return false;
  }
  




  public Set<K> keySet()
  {
    removeAllExpired(now());
    return super.keySet();
  }
  


  private long now()
  {
    return System.currentTimeMillis();
  }
  







  public V put(K key, V value)
  {
    long expirationTime = expiringPolicy.expirationTime(key, value);
    expirationMap.put(key, Long.valueOf(expirationTime));
    
    return super.put(key, value);
  }
  
  public void putAll(Map<? extends K, ? extends V> mapToCopy)
  {
    for (Map.Entry<? extends K, ? extends V> entry : mapToCopy.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }
  





  public V remove(Object key)
  {
    expirationMap.remove(key);
    return super.remove(key);
  }
  






  private void removeAllExpired(long now)
  {
    Iterator<Map.Entry<Object, Long>> iter = expirationMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<Object, Long> expirationEntry = (Map.Entry)iter.next();
      if (isExpired(now, (Long)expirationEntry.getValue()))
      {
        super.remove(expirationEntry.getKey());
        
        iter.remove();
      }
    }
  }
  




  private void removeIfExpired(Object key, long now)
  {
    Long expirationTimeObject = (Long)expirationMap.get(key);
    if (isExpired(now, expirationTimeObject)) {
      remove(key);
    }
  }
  




  public int size()
  {
    removeAllExpired(now());
    return super.size();
  }
  








  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    map = ((Map)in.readObject());
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(map);
  }
  




  public Collection<V> values()
  {
    removeAllExpired(now());
    return super.values();
  }
}
