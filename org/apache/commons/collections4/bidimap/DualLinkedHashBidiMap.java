package org.apache.commons.collections4.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.collections4.BidiMap;




























public class DualLinkedHashBidiMap<K, V>
  extends AbstractDualBidiMap<K, V>
  implements Serializable
{
  private static final long serialVersionUID = 721969328361810L;
  
  public DualLinkedHashBidiMap()
  {
    super(new LinkedHashMap(), new LinkedHashMap());
  }
  





  public DualLinkedHashBidiMap(Map<? extends K, ? extends V> map)
  {
    super(new LinkedHashMap(), new LinkedHashMap());
    putAll(map);
  }
  







  protected DualLinkedHashBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap)
  {
    super(normalMap, reverseMap, inverseBidiMap);
  }
  









  protected BidiMap<V, K> createBidiMap(Map<V, K> normalMap, Map<K, V> reverseMap, BidiMap<K, V> inverseBidiMap)
  {
    return new DualLinkedHashBidiMap(normalMap, reverseMap, inverseBidiMap);
  }
  
  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(normalMap);
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    normalMap = new LinkedHashMap();
    reverseMap = new LinkedHashMap();
    
    Map<K, V> map = (Map)in.readObject();
    putAll(map);
  }
}
