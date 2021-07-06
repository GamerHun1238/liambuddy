package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;








public class UnresolvedForwardReference
  extends JsonMappingException
{
  private static final long serialVersionUID = 1L;
  private ReadableObjectId _roid;
  private List<UnresolvedId> _unresolvedIds;
  
  public UnresolvedForwardReference(JsonParser p, String msg, JsonLocation loc, ReadableObjectId roid)
  {
    super(p, msg, loc);
    _roid = roid;
  }
  


  public UnresolvedForwardReference(JsonParser p, String msg)
  {
    super(p, msg);
    _unresolvedIds = new ArrayList();
  }
  


  @Deprecated
  public UnresolvedForwardReference(String msg, JsonLocation loc, ReadableObjectId roid)
  {
    super(msg, loc);
    _roid = roid;
  }
  


  @Deprecated
  public UnresolvedForwardReference(String msg)
  {
    super(msg);
    _unresolvedIds = new ArrayList();
  }
  





  public ReadableObjectId getRoid()
  {
    return _roid;
  }
  
  public Object getUnresolvedId() {
    return _roid.getKey().key;
  }
  
  public void addUnresolvedId(Object id, Class<?> type, JsonLocation where) {
    _unresolvedIds.add(new UnresolvedId(id, type, where));
  }
  
  public List<UnresolvedId> getUnresolvedIds() {
    return _unresolvedIds;
  }
  

  public String getMessage()
  {
    String msg = super.getMessage();
    if (_unresolvedIds == null) {
      return msg;
    }
    
    StringBuilder sb = new StringBuilder(msg);
    Iterator<UnresolvedId> iterator = _unresolvedIds.iterator();
    while (iterator.hasNext()) {
      UnresolvedId unresolvedId = (UnresolvedId)iterator.next();
      sb.append(unresolvedId.toString());
      if (iterator.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append('.');
    return sb.toString();
  }
}
