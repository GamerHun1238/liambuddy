package com.fasterxml.jackson.annotation;

import java.util.HashMap;
import java.util.Map;







public class SimpleObjectIdResolver
  implements ObjectIdResolver
{
  protected Map<ObjectIdGenerator.IdKey, Object> _items;
  
  public SimpleObjectIdResolver() {}
  
  public void bindItem(ObjectIdGenerator.IdKey id, Object ob)
  {
    if (_items == null) {
      _items = new HashMap();
    } else if (_items.containsKey(id)) {
      throw new IllegalStateException("Already had POJO for id (" + key.getClass().getName() + ") [" + id + "]");
    }
    
    _items.put(id, ob);
  }
  
  public Object resolveId(ObjectIdGenerator.IdKey id)
  {
    return _items == null ? null : _items.get(id);
  }
  
  public boolean canUseFor(ObjectIdResolver resolverType)
  {
    return resolverType.getClass() == getClass();
  }
  


  public ObjectIdResolver newForDeserialization(Object context)
  {
    return new SimpleObjectIdResolver();
  }
}
