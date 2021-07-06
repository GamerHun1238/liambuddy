package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
















public class UnwrappingBeanPropertyWriter
  extends BeanPropertyWriter
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected final NameTransformer _nameTransformer;
  
  public UnwrappingBeanPropertyWriter(BeanPropertyWriter base, NameTransformer unwrapper)
  {
    super(base);
    _nameTransformer = unwrapper;
  }
  
  protected UnwrappingBeanPropertyWriter(UnwrappingBeanPropertyWriter base, NameTransformer transformer, SerializedString name)
  {
    super(base, name);
    _nameTransformer = transformer;
  }
  

  public UnwrappingBeanPropertyWriter rename(NameTransformer transformer)
  {
    String oldName = _name.getValue();
    String newName = transformer.transform(oldName);
    

    transformer = NameTransformer.chainedTransformer(transformer, _nameTransformer);
    
    return _new(transformer, new SerializedString(newName));
  }
  





  protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName)
  {
    return new UnwrappingBeanPropertyWriter(this, transformer, newName);
  }
  






  public boolean isUnwrapping()
  {
    return true;
  }
  

  public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
    throws Exception
  {
    Object value = get(bean);
    if (value == null)
    {

      return;
    }
    JsonSerializer<Object> ser = _serializer;
    if (ser == null) {
      Class<?> cls = value.getClass();
      PropertySerializerMap map = _dynamicSerializers;
      ser = map.serializerFor(cls);
      if (ser == null) {
        ser = _findAndAddDynamic(map, cls, prov);
      }
    }
    if (_suppressableValue != null) {
      if (MARKER_FOR_EMPTY == _suppressableValue) {
        if (!ser.isEmpty(prov, value)) {}

      }
      else if (_suppressableValue.equals(value)) {
        return;
      }
    }
    
    if ((value == bean) && 
      (_handleSelfReference(bean, gen, prov, ser))) {
      return;
    }
    


    if (!ser.isUnwrappingSerializer()) {
      gen.writeFieldName(_name);
    }
    
    if (_typeSerializer == null) {
      ser.serialize(value, gen, prov);
    } else {
      ser.serializeWithType(value, gen, prov, _typeSerializer);
    }
  }
  


  public void assignSerializer(JsonSerializer<Object> ser)
  {
    if (ser != null) {
      NameTransformer t = _nameTransformer;
      if ((ser.isUnwrappingSerializer()) && ((ser instanceof UnwrappingBeanSerializer)))
      {


        t = NameTransformer.chainedTransformer(t, _nameTransformer);
      }
      ser = ser.unwrappingSerializer(t);
    }
    super.assignSerializer(ser);
  }
  









  public void depositSchemaProperty(final JsonObjectFormatVisitor visitor, SerializerProvider provider)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = provider.findValueSerializer(getType(), this).unwrappingSerializer(_nameTransformer);
    
    if (ser.isUnwrappingSerializer()) {
      ser.acceptJsonFormatVisitor(new JsonFormatVisitorWrapper.Base(provider)
      {

        public JsonObjectFormatVisitor expectObjectFormat(JavaType type)
          throws JsonMappingException
        {
          return visitor;
        }
      }, getType());
    } else {
      super.depositSchemaProperty(visitor, provider);
    }
  }
  


  protected void _depositSchemaProperty(ObjectNode propertiesNode, JsonNode schemaNode)
  {
    JsonNode props = schemaNode.get("properties");
    if (props != null) {
      Iterator<Map.Entry<String, JsonNode>> it = props.fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> entry = (Map.Entry)it.next();
        String name = (String)entry.getKey();
        if (_nameTransformer != null) {
          name = _nameTransformer.transform(name);
        }
        propertiesNode.set(name, (JsonNode)entry.getValue());
      }
    }
  }
  




  protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
    throws JsonMappingException
  {
    JsonSerializer<Object> serializer;
    



    if (_nonTrivialBaseType != null) {
      JavaType subtype = provider.constructSpecializedType(_nonTrivialBaseType, type);
      serializer = provider.findValueSerializer(subtype, this);
    } else {
      serializer = provider.findValueSerializer(type, this);
    }
    NameTransformer t = _nameTransformer;
    if ((serializer.isUnwrappingSerializer()) && ((serializer instanceof UnwrappingBeanSerializer)))
    {


      t = NameTransformer.chainedTransformer(t, _nameTransformer);
    }
    JsonSerializer<Object> serializer = serializer.unwrappingSerializer(t);
    
    _dynamicSerializers = _dynamicSerializers.newWith(type, serializer);
    return serializer;
  }
}
