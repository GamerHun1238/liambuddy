package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;





































public final class JsonReadContext
  extends JsonStreamContext
{
  protected final JsonReadContext _parent;
  protected DupDetector _dups;
  protected JsonReadContext _child;
  protected String _currentName;
  protected Object _currentValue;
  protected int _lineNr;
  protected int _columnNr;
  
  public JsonReadContext(JsonReadContext parent, DupDetector dups, int type, int lineNr, int colNr)
  {
    _parent = parent;
    _dups = dups;
    _type = type;
    _lineNr = lineNr;
    _columnNr = colNr;
    _index = -1;
  }
  
  protected void reset(int type, int lineNr, int colNr) {
    _type = type;
    _index = -1;
    _lineNr = lineNr;
    _columnNr = colNr;
    _currentName = null;
    _currentValue = null;
    if (_dups != null) {
      _dups.reset();
    }
  }
  





  public JsonReadContext withDupDetector(DupDetector dups)
  {
    _dups = dups;
    return this;
  }
  
  public Object getCurrentValue()
  {
    return _currentValue;
  }
  
  public void setCurrentValue(Object v)
  {
    _currentValue = v;
  }
  





  public static JsonReadContext createRootContext(int lineNr, int colNr, DupDetector dups)
  {
    return new JsonReadContext(null, dups, 0, lineNr, colNr);
  }
  
  public static JsonReadContext createRootContext(DupDetector dups) {
    return new JsonReadContext(null, dups, 0, 1, 0);
  }
  
  public JsonReadContext createChildArrayContext(int lineNr, int colNr) {
    JsonReadContext ctxt = _child;
    if (ctxt == null)
    {
      _child = (ctxt = new JsonReadContext(this, _dups == null ? null : _dups.child(), 1, lineNr, colNr));
    } else {
      ctxt.reset(1, lineNr, colNr);
    }
    return ctxt;
  }
  
  public JsonReadContext createChildObjectContext(int lineNr, int colNr) {
    JsonReadContext ctxt = _child;
    if (ctxt == null)
    {
      _child = (ctxt = new JsonReadContext(this, _dups == null ? null : _dups.child(), 2, lineNr, colNr));
      return ctxt;
    }
    ctxt.reset(2, lineNr, colNr);
    return ctxt;
  }
  




  public String getCurrentName()
  {
    return _currentName;
  }
  
  public boolean hasCurrentName() { return _currentName != null; }
  
  public JsonReadContext getParent() { return _parent; }
  

  public JsonLocation getStartLocation(Object srcRef)
  {
    long totalChars = -1L;
    return new JsonLocation(srcRef, totalChars, _lineNr, _columnNr);
  }
  















  public JsonReadContext clearAndGetParent()
  {
    _currentValue = null;
    
    return _parent;
  }
  
  public DupDetector getDupDetector() {
    return _dups;
  }
  









  public boolean expectComma()
  {
    int ix = ++_index;
    return (_type != 0) && (ix > 0);
  }
  
  public void setCurrentName(String name) throws JsonProcessingException {
    _currentName = name;
    if (_dups != null) _checkDup(_dups, name);
  }
  
  private void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
    if (dd.isDup(name)) {
      Object src = dd.getSource();
      throw new JsonParseException((src instanceof JsonParser) ? (JsonParser)src : null, "Duplicate field '" + name + "'");
    }
  }
}
