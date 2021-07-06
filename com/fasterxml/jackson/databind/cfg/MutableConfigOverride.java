package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Value;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import java.io.Serializable;








public class MutableConfigOverride
  extends ConfigOverride
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public MutableConfigOverride() {}
  
  protected MutableConfigOverride(MutableConfigOverride src)
  {
    super(src);
  }
  
  public MutableConfigOverride copy() {
    return new MutableConfigOverride(this);
  }
  
  public MutableConfigOverride setFormat(JsonFormat.Value v) {
    _format = v;
    return this;
  }
  





  public MutableConfigOverride setInclude(JsonInclude.Value v)
  {
    _include = v;
    return this;
  }
  







  public MutableConfigOverride setIncludeAsProperty(JsonInclude.Value v)
  {
    _includeAsProperty = v;
    return this;
  }
  
  public MutableConfigOverride setIgnorals(JsonIgnoreProperties.Value v) {
    _ignorals = v;
    return this;
  }
  
  public MutableConfigOverride setIsIgnoredType(Boolean v) {
    _isIgnoredType = v;
    return this;
  }
  


  public MutableConfigOverride setSetterInfo(JsonSetter.Value v)
  {
    _setterInfo = v;
    return this;
  }
  


  public MutableConfigOverride setVisibility(JsonAutoDetect.Value v)
  {
    _visibility = v;
    return this;
  }
  


  public MutableConfigOverride setMergeable(Boolean v)
  {
    _mergeable = v;
    return this;
  }
}
