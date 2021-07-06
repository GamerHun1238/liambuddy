package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Value;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonSetter.Value;























































public abstract class ConfigOverride
{
  protected JsonFormat.Value _format;
  protected JsonInclude.Value _include;
  protected JsonInclude.Value _includeAsProperty;
  protected JsonIgnoreProperties.Value _ignorals;
  protected JsonSetter.Value _setterInfo;
  protected JsonAutoDetect.Value _visibility;
  protected Boolean _isIgnoredType;
  protected Boolean _mergeable;
  
  protected ConfigOverride() {}
  
  protected ConfigOverride(ConfigOverride src)
  {
    _format = _format;
    _include = _include;
    _includeAsProperty = _includeAsProperty;
    _ignorals = _ignorals;
    _setterInfo = _setterInfo;
    _visibility = _visibility;
    _isIgnoredType = _isIgnoredType;
    _mergeable = _mergeable;
  }
  




  public static ConfigOverride empty()
  {
    return Empty.INSTANCE;
  }
  
  public JsonFormat.Value getFormat() { return _format; }
  public JsonInclude.Value getInclude() { return _include; }
  



  public JsonInclude.Value getIncludeAsProperty() { return _includeAsProperty; }
  
  public JsonIgnoreProperties.Value getIgnorals() { return _ignorals; }
  
  public Boolean getIsIgnoredType() {
    return _isIgnoredType;
  }
  

  public JsonSetter.Value getSetterInfo()
  {
    return _setterInfo;
  }
  
  public JsonAutoDetect.Value getVisibility()
  {
    return _visibility;
  }
  
  public Boolean getMergeable()
  {
    return _mergeable;
  }
  



  static final class Empty
    extends ConfigOverride
  {
    static final Empty INSTANCE = new Empty();
    
    private Empty() {}
  }
}
