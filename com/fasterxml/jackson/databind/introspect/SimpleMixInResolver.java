package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.type.ClassKey;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;



















public class SimpleMixInResolver
  implements ClassIntrospector.MixInResolver, Serializable
{
  private static final long serialVersionUID = 1L;
  protected final ClassIntrospector.MixInResolver _overrides;
  protected Map<ClassKey, Class<?>> _localMixIns;
  
  public SimpleMixInResolver(ClassIntrospector.MixInResolver overrides)
  {
    _overrides = overrides;
  }
  
  protected SimpleMixInResolver(ClassIntrospector.MixInResolver overrides, Map<ClassKey, Class<?>> mixins)
  {
    _overrides = overrides;
    _localMixIns = mixins;
  }
  



  public SimpleMixInResolver withOverrides(ClassIntrospector.MixInResolver overrides)
  {
    return new SimpleMixInResolver(overrides, _localMixIns);
  }
  



  public SimpleMixInResolver withoutLocalDefinitions()
  {
    return new SimpleMixInResolver(_overrides, null);
  }
  
  public void setLocalDefinitions(Map<Class<?>, Class<?>> sourceMixins) {
    if ((sourceMixins == null) || (sourceMixins.isEmpty())) {
      _localMixIns = null;
    } else {
      Map<ClassKey, Class<?>> mixIns = new HashMap(sourceMixins.size());
      for (Map.Entry<Class<?>, Class<?>> en : sourceMixins.entrySet()) {
        mixIns.put(new ClassKey((Class)en.getKey()), en.getValue());
      }
      _localMixIns = mixIns;
    }
  }
  
  public void addLocalDefinition(Class<?> target, Class<?> mixinSource) {
    if (_localMixIns == null) {
      _localMixIns = new HashMap();
    }
    _localMixIns.put(new ClassKey(target), mixinSource);
  }
  

  public SimpleMixInResolver copy()
  {
    ClassIntrospector.MixInResolver overrides = _overrides == null ? null : _overrides.copy();
    Map<ClassKey, Class<?>> mixIns = _localMixIns == null ? null : new HashMap(_localMixIns);
    
    return new SimpleMixInResolver(overrides, mixIns);
  }
  

  public Class<?> findMixInClassFor(Class<?> cls)
  {
    Class<?> mixin = _overrides == null ? null : _overrides.findMixInClassFor(cls);
    if ((mixin == null) && (_localMixIns != null)) {
      mixin = (Class)_localMixIns.get(new ClassKey(cls));
    }
    return mixin;
  }
  
  public int localSize() {
    return _localMixIns == null ? 0 : _localMixIns.size();
  }
  













  public boolean hasMixIns()
  {
    if (_localMixIns == null)
    {
      if (_overrides == null) {
        return false;
      }
      
      if ((_overrides instanceof SimpleMixInResolver)) {
        return ((SimpleMixInResolver)_overrides).hasMixIns();
      }
    }
    
    return true;
  }
}
