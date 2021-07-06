package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


























public abstract class InjectableValues
{
  public InjectableValues() {}
  
  public abstract Object findInjectableValue(Object paramObject1, DeserializationContext paramDeserializationContext, BeanProperty paramBeanProperty, Object paramObject2)
    throws JsonMappingException;
  
  public static class Std
    extends InjectableValues
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    protected final Map<String, Object> _values;
    
    public Std()
    {
      this(new HashMap());
    }
    
    public Std(Map<String, Object> values) {
      _values = values;
    }
    
    public Std addValue(String key, Object value) {
      _values.put(key, value);
      return this;
    }
    
    public Std addValue(Class<?> classKey, Object value) {
      _values.put(classKey.getName(), value);
      return this;
    }
    

    public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance)
      throws JsonMappingException
    {
      if (!(valueId instanceof String)) {
        ctxt.reportBadDefinition(ClassUtil.classOf(valueId), 
          String.format("Unrecognized inject value id type (%s), expecting String", new Object[] {
          
          ClassUtil.classNameOf(valueId) }));
      }
      String key = (String)valueId;
      Object ob = _values.get(key);
      if ((ob == null) && (!_values.containsKey(key))) {
        throw new IllegalArgumentException("No injectable id with value '" + key + "' found (for property '" + forProperty.getName() + "')");
      }
      return ob;
    }
  }
}
