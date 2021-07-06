package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import java.util.Collections;
import java.util.List;





public class BeanSerializerBuilder
{
  private static final BeanPropertyWriter[] NO_PROPERTIES = new BeanPropertyWriter[0];
  





  protected final BeanDescription _beanDesc;
  





  protected SerializationConfig _config;
  





  protected List<BeanPropertyWriter> _properties = Collections.emptyList();
  




  protected BeanPropertyWriter[] _filteredProperties;
  




  protected AnyGetterWriter _anyGetter;
  




  protected Object _filterId;
  




  protected AnnotatedMember _typeId;
  



  protected ObjectIdWriter _objectIdWriter;
  




  public BeanSerializerBuilder(BeanDescription beanDesc)
  {
    _beanDesc = beanDesc;
  }
  


  protected BeanSerializerBuilder(BeanSerializerBuilder src)
  {
    _beanDesc = _beanDesc;
    _properties = _properties;
    _filteredProperties = _filteredProperties;
    _anyGetter = _anyGetter;
    _filterId = _filterId;
  }
  








  protected void setConfig(SerializationConfig config)
  {
    _config = config;
  }
  
  public void setProperties(List<BeanPropertyWriter> properties) {
    _properties = properties;
  }
  




  public void setFilteredProperties(BeanPropertyWriter[] properties)
  {
    if ((properties != null) && 
      (properties.length != _properties.size())) {
      throw new IllegalArgumentException(String.format("Trying to set %d filtered properties; must match length of non-filtered `properties` (%d)", new Object[] {
      
        Integer.valueOf(properties.length), Integer.valueOf(_properties.size()) }));
    }
    
    _filteredProperties = properties;
  }
  
  public void setAnyGetter(AnyGetterWriter anyGetter) {
    _anyGetter = anyGetter;
  }
  
  public void setFilterId(Object filterId) {
    _filterId = filterId;
  }
  
  public void setTypeId(AnnotatedMember idProp)
  {
    if (_typeId != null) {
      throw new IllegalArgumentException("Multiple type ids specified with " + _typeId + " and " + idProp);
    }
    _typeId = idProp;
  }
  
  public void setObjectIdWriter(ObjectIdWriter w) {
    _objectIdWriter = w;
  }
  








  public AnnotatedClass getClassInfo() { return _beanDesc.getClassInfo(); }
  
  public BeanDescription getBeanDescription() { return _beanDesc; }
  
  public List<BeanPropertyWriter> getProperties() { return _properties; }
  
  public boolean hasProperties() { return (_properties != null) && (_properties.size() > 0); }
  

  public BeanPropertyWriter[] getFilteredProperties() { return _filteredProperties; }
  
  public AnyGetterWriter getAnyGetter() { return _anyGetter; }
  
  public Object getFilterId() { return _filterId; }
  
  public AnnotatedMember getTypeId() { return _typeId; }
  
  public ObjectIdWriter getObjectIdWriter() { return _objectIdWriter; }
  




  public JsonSerializer<?> build()
  {
    BeanPropertyWriter[] properties;
    



    BeanPropertyWriter[] properties;
    



    if ((_properties == null) || (_properties.isEmpty())) {
      if ((_anyGetter == null) && (_objectIdWriter == null)) {
        return null;
      }
      properties = NO_PROPERTIES;
    } else {
      properties = (BeanPropertyWriter[])_properties.toArray(new BeanPropertyWriter[_properties.size()]);
      if (_config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
        int i = 0; for (int end = properties.length; i < end; i++) {
          properties[i].fixAccess(_config);
        }
      }
    }
    
    if ((_filteredProperties != null) && 
      (_filteredProperties.length != _properties.size())) {
      throw new IllegalStateException(String.format("Mismatch between `properties` size (%d), `filteredProperties` (%s): should have as many (or `null` for latter)", new Object[] {
      
        Integer.valueOf(_properties.size()), Integer.valueOf(_filteredProperties.length) }));
    }
    
    if (_anyGetter != null) {
      _anyGetter.fixAccess(_config);
    }
    if ((_typeId != null) && 
      (_config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS))) {
      _typeId.fixAccess(_config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    
    return new BeanSerializer(_beanDesc.getType(), this, properties, _filteredProperties);
  }
  






  public BeanSerializer createDummy()
  {
    return BeanSerializer.createDummy(_beanDesc.getType(), this);
  }
}
