package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.util.ClassUtil;




public class UnresolvedId
{
  private final Object _id;
  private final JsonLocation _location;
  private final Class<?> _type;
  
  public UnresolvedId(Object id, Class<?> type, JsonLocation where)
  {
    _id = id;
    _type = type;
    _location = where;
  }
  

  public Object getId()
  {
    return _id;
  }
  


  public Class<?> getType() { return _type; }
  public JsonLocation getLocation() { return _location; }
  
  public String toString()
  {
    return String.format("Object id [%s] (for %s) at %s", new Object[] { _id, 
      ClassUtil.nameOf(_type), _location });
  }
}
