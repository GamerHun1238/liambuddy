package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.json.JsonReadContext;

















public class TokenBufferReadContext
  extends JsonStreamContext
{
  protected final JsonStreamContext _parent;
  protected final JsonLocation _startLocation;
  protected String _currentName;
  protected Object _currentValue;
  
  protected TokenBufferReadContext(JsonStreamContext base, Object srcRef)
  {
    super(base);
    _parent = base.getParent();
    _currentName = base.getCurrentName();
    _currentValue = base.getCurrentValue();
    if ((base instanceof JsonReadContext)) {
      JsonReadContext rc = (JsonReadContext)base;
      _startLocation = rc.getStartLocation(srcRef);
    } else {
      _startLocation = JsonLocation.NA;
    }
  }
  
  protected TokenBufferReadContext(JsonStreamContext base, JsonLocation startLoc) {
    super(base);
    _parent = base.getParent();
    _currentName = base.getCurrentName();
    _currentValue = base.getCurrentValue();
    _startLocation = startLoc;
  }
  



  protected TokenBufferReadContext()
  {
    super(0, -1);
    _parent = null;
    _startLocation = JsonLocation.NA;
  }
  
  protected TokenBufferReadContext(TokenBufferReadContext parent, int type, int index) {
    super(type, index);
    _parent = parent;
    _startLocation = _startLocation;
  }
  
  public Object getCurrentValue()
  {
    return _currentValue;
  }
  
  public void setCurrentValue(Object v)
  {
    _currentValue = v;
  }
  






  public static TokenBufferReadContext createRootContext(JsonStreamContext origContext)
  {
    if (origContext == null) {
      return new TokenBufferReadContext();
    }
    return new TokenBufferReadContext(origContext, null);
  }
  
  public TokenBufferReadContext createChildArrayContext()
  {
    _index += 1;
    return new TokenBufferReadContext(this, 1, -1);
  }
  
  public TokenBufferReadContext createChildObjectContext()
  {
    _index += 1;
    return new TokenBufferReadContext(this, 2, -1);
  }
  






  public TokenBufferReadContext parentOrCopy()
  {
    if ((_parent instanceof TokenBufferReadContext)) {
      return (TokenBufferReadContext)_parent;
    }
    if (_parent == null) {
      return new TokenBufferReadContext();
    }
    return new TokenBufferReadContext(_parent, _startLocation);
  }
  




  public String getCurrentName()
  {
    return _currentName;
  }
  
  public boolean hasCurrentName() { return _currentName != null; }
  
  public JsonStreamContext getParent() { return _parent; }
  
  public void setCurrentName(String name) throws JsonProcessingException {
    _currentName = name;
  }
  








  public void updateForValue()
  {
    _index += 1;
  }
}
