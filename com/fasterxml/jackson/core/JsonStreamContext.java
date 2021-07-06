package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.CharTypes;






































public abstract class JsonStreamContext
{
  protected static final int TYPE_ROOT = 0;
  protected static final int TYPE_ARRAY = 1;
  protected static final int TYPE_OBJECT = 2;
  protected int _type;
  protected int _index;
  
  protected JsonStreamContext() {}
  
  protected JsonStreamContext(JsonStreamContext base)
  {
    _type = _type;
    _index = _index;
  }
  


  protected JsonStreamContext(int type, int index)
  {
    _type = type;
    _index = index;
  }
  






  public abstract JsonStreamContext getParent();
  






  public final boolean inArray()
  {
    return _type == 1;
  }
  


  public final boolean inRoot()
  {
    return _type == 0;
  }
  

  public final boolean inObject()
  {
    return _type == 2;
  }
  





  @Deprecated
  public final String getTypeDesc()
  {
    switch (_type) {
    case 0:  return "ROOT";
    case 1:  return "ARRAY";
    case 2:  return "OBJECT";
    }
    return "?";
  }
  


  public String typeDesc()
  {
    switch (_type) {
    case 0:  return "root";
    case 1:  return "Array";
    case 2:  return "Object";
    }
    return "?";
  }
  

  public final int getEntryCount()
  {
    return _index + 1;
  }
  
  public final int getCurrentIndex()
  {
    return _index < 0 ? 0 : _index;
  }
  




  public boolean hasCurrentIndex()
  {
    return _index >= 0;
  }
  















  public boolean hasPathSegment()
  {
    if (_type == 2)
      return hasCurrentName();
    if (_type == 1) {
      return hasCurrentIndex();
    }
    return false;
  }
  



  public abstract String getCurrentName();
  



  public boolean hasCurrentName()
  {
    return getCurrentName() != null;
  }
  












  public Object getCurrentValue()
  {
    return null;
  }
  







  public void setCurrentValue(Object v) {}
  






  public JsonPointer pathAsPointer()
  {
    return JsonPointer.forPath(this, false);
  }
  








  public JsonPointer pathAsPointer(boolean includeRoot)
  {
    return JsonPointer.forPath(this, includeRoot);
  }
  














  public JsonLocation getStartLocation(Object srcRef)
  {
    return JsonLocation.NA;
  }
  






  public String toString()
  {
    StringBuilder sb = new StringBuilder(64);
    switch (_type) {
    case 0: 
      sb.append("/");
      break;
    case 1: 
      sb.append('[');
      sb.append(getCurrentIndex());
      sb.append(']');
      break;
    case 2: 
    default: 
      sb.append('{');
      String currentName = getCurrentName();
      if (currentName != null) {
        sb.append('"');
        CharTypes.appendQuoted(sb, currentName);
        sb.append('"');
      } else {
        sb.append('?');
      }
      sb.append('}');
    }
    
    return sb.toString();
  }
}
