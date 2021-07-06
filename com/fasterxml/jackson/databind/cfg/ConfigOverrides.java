package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;







































public class ConfigOverrides
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected Map<Class<?>, MutableConfigOverride> _overrides;
  protected JsonInclude.Value _defaultInclusion;
  protected JsonSetter.Value _defaultSetterInfo;
  protected VisibilityChecker<?> _visibilityChecker;
  protected Boolean _defaultMergeable;
  protected Boolean _defaultLeniency;
  
  public ConfigOverrides()
  {
    this(null, 
    
      JsonInclude.Value.empty(), 
      JsonSetter.Value.empty(), 
      VisibilityChecker.Std.defaultInstance(), null, null);
  }
  







  protected ConfigOverrides(Map<Class<?>, MutableConfigOverride> overrides, JsonInclude.Value defIncl, JsonSetter.Value defSetter, VisibilityChecker<?> defVisibility, Boolean defMergeable, Boolean defLeniency)
  {
    _overrides = overrides;
    _defaultInclusion = defIncl;
    _defaultSetterInfo = defSetter;
    _visibilityChecker = defVisibility;
    _defaultMergeable = defMergeable;
    _defaultLeniency = defLeniency;
  }
  




  @Deprecated
  protected ConfigOverrides(Map<Class<?>, MutableConfigOverride> overrides, JsonInclude.Value defIncl, JsonSetter.Value defSetter, VisibilityChecker<?> defVisibility, Boolean defMergeable)
  {
    this(overrides, defIncl, defSetter, defVisibility, defMergeable, null);
  }
  
  public ConfigOverrides copy() {
    Map<Class<?>, MutableConfigOverride> newOverrides;
    Map<Class<?>, MutableConfigOverride> newOverrides;
    if (_overrides == null) {
      newOverrides = null;
    } else {
      newOverrides = _newMap();
      for (Map.Entry<Class<?>, MutableConfigOverride> entry : _overrides.entrySet()) {
        newOverrides.put(entry.getKey(), ((MutableConfigOverride)entry.getValue()).copy());
      }
    }
    return new ConfigOverrides(newOverrides, _defaultInclusion, _defaultSetterInfo, _visibilityChecker, _defaultMergeable, _defaultLeniency);
  }
  







  public ConfigOverride findOverride(Class<?> type)
  {
    if (_overrides == null) {
      return null;
    }
    return (ConfigOverride)_overrides.get(type);
  }
  
  public MutableConfigOverride findOrCreateOverride(Class<?> type) {
    if (_overrides == null) {
      _overrides = _newMap();
    }
    MutableConfigOverride override = (MutableConfigOverride)_overrides.get(type);
    if (override == null) {
      override = new MutableConfigOverride();
      _overrides.put(type, override);
    }
    return override;
  }
  








  public JsonFormat.Value findFormatDefaults(Class<?> type)
  {
    if (_overrides != null) {
      ConfigOverride override = (ConfigOverride)_overrides.get(type);
      if (override != null) {
        JsonFormat.Value format = override.getFormat();
        if (format != null) {
          if (!format.hasLenient()) {
            return format.withLenient(_defaultLeniency);
          }
          return format;
        }
      }
    }
    if (_defaultLeniency == null) {
      return JsonFormat.Value.empty();
    }
    return JsonFormat.Value.forLeniency(_defaultLeniency.booleanValue());
  }
  





  public JsonInclude.Value getDefaultInclusion()
  {
    return _defaultInclusion;
  }
  
  public JsonSetter.Value getDefaultSetterInfo() {
    return _defaultSetterInfo;
  }
  
  public Boolean getDefaultMergeable() {
    return _defaultMergeable;
  }
  


  public Boolean getDefaultLeniency()
  {
    return _defaultLeniency;
  }
  


  public VisibilityChecker<?> getDefaultVisibility()
  {
    return _visibilityChecker;
  }
  


  public void setDefaultInclusion(JsonInclude.Value v)
  {
    _defaultInclusion = v;
  }
  


  public void setDefaultSetterInfo(JsonSetter.Value v)
  {
    _defaultSetterInfo = v;
  }
  


  public void setDefaultMergeable(Boolean v)
  {
    _defaultMergeable = v;
  }
  


  public void setDefaultLeniency(Boolean v)
  {
    _defaultLeniency = v;
  }
  


  public void setDefaultVisibility(VisibilityChecker<?> v)
  {
    _visibilityChecker = v;
  }
  





  protected Map<Class<?>, MutableConfigOverride> _newMap()
  {
    return new HashMap();
  }
}
