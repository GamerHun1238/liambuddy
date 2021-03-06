package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;














public class UnwrappingBeanSerializer
  extends BeanSerializerBase
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final NameTransformer _nameTransformer;
  
  public UnwrappingBeanSerializer(BeanSerializerBase src, NameTransformer transformer)
  {
    super(src, transformer);
    _nameTransformer = transformer;
  }
  
  public UnwrappingBeanSerializer(UnwrappingBeanSerializer src, ObjectIdWriter objectIdWriter)
  {
    super(src, objectIdWriter);
    _nameTransformer = _nameTransformer;
  }
  
  public UnwrappingBeanSerializer(UnwrappingBeanSerializer src, ObjectIdWriter objectIdWriter, Object filterId)
  {
    super(src, objectIdWriter, filterId);
    _nameTransformer = _nameTransformer;
  }
  
  protected UnwrappingBeanSerializer(UnwrappingBeanSerializer src, Set<String> toIgnore) {
    super(src, toIgnore);
    _nameTransformer = _nameTransformer;
  }
  







  public JsonSerializer<Object> unwrappingSerializer(NameTransformer transformer)
  {
    return new UnwrappingBeanSerializer(this, transformer);
  }
  
  public boolean isUnwrappingSerializer()
  {
    return true;
  }
  
  public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter)
  {
    return new UnwrappingBeanSerializer(this, objectIdWriter);
  }
  
  public BeanSerializerBase withFilterId(Object filterId)
  {
    return new UnwrappingBeanSerializer(this, _objectIdWriter, filterId);
  }
  
  protected BeanSerializerBase withIgnorals(Set<String> toIgnore)
  {
    return new UnwrappingBeanSerializer(this, toIgnore);
  }
  




  protected BeanSerializerBase asArraySerializer()
  {
    return this;
  }
  











  public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    gen.setCurrentValue(bean);
    if (_objectIdWriter != null) {
      _serializeWithObjectId(bean, gen, provider, false);
      return;
    }
    if (_propertyFilterId != null) {
      serializeFieldsFiltered(bean, gen, provider);
    } else {
      serializeFields(bean, gen, provider);
    }
  }
  

  public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    if (provider.isEnabled(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)) {
      provider.reportBadDefinition(handledType(), "Unwrapped property requires use of type information: cannot serialize without disabling `SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS`");
    }
    
    gen.setCurrentValue(bean);
    if (_objectIdWriter != null) {
      _serializeWithObjectId(bean, gen, provider, typeSer);
      return;
    }
    if (_propertyFilterId != null) {
      serializeFieldsFiltered(bean, gen, provider);
    } else {
      serializeFields(bean, gen, provider);
    }
  }
  





  public String toString()
  {
    return "UnwrappingBeanSerializer for " + handledType().getName();
  }
}
