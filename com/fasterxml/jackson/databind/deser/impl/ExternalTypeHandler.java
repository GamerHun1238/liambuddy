package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;











public class ExternalTypeHandler
{
  private final JavaType _beanType;
  private final ExtTypedProperty[] _properties;
  private final Map<String, Object> _nameToPropertyIndex;
  private final String[] _typeIds;
  private final TokenBuffer[] _tokens;
  
  protected ExternalTypeHandler(JavaType beanType, ExtTypedProperty[] properties, Map<String, Object> nameToPropertyIndex, String[] typeIds, TokenBuffer[] tokens)
  {
    _beanType = beanType;
    _properties = properties;
    _nameToPropertyIndex = nameToPropertyIndex;
    _typeIds = typeIds;
    _tokens = tokens;
  }
  
  protected ExternalTypeHandler(ExternalTypeHandler h)
  {
    _beanType = _beanType;
    _properties = _properties;
    _nameToPropertyIndex = _nameToPropertyIndex;
    int len = _properties.length;
    _typeIds = new String[len];
    _tokens = new TokenBuffer[len];
  }
  


  public static Builder builder(JavaType beanType)
  {
    return new Builder(beanType);
  }
  



  public ExternalTypeHandler start()
  {
    return new ExternalTypeHandler(this);
  }
  









  public boolean handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean)
    throws IOException
  {
    Object ob = _nameToPropertyIndex.get(propName);
    if (ob == null) {
      return false;
    }
    String typeId = p.getText();
    
    if ((ob instanceof List)) {
      boolean result = false;
      for (Integer index : (List)ob) {
        if (_handleTypePropertyValue(p, ctxt, propName, bean, typeId, index
          .intValue())) {
          result = true;
        }
      }
      return result;
    }
    return _handleTypePropertyValue(p, ctxt, propName, bean, typeId, ((Integer)ob)
      .intValue());
  }
  

  private final boolean _handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean, String typeId, int index)
    throws IOException
  {
    ExtTypedProperty prop = _properties[index];
    if (!prop.hasTypePropertyName(propName)) {
      return false;
    }
    
    boolean canDeserialize = (bean != null) && (_tokens[index] != null);
    
    if (canDeserialize) {
      _deserializeAndSet(p, ctxt, bean, index, typeId);
      
      _tokens[index] = null;
    } else {
      _typeIds[index] = typeId;
    }
    return true;
  }
  









  public boolean handlePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean)
    throws IOException
  {
    Object ob = _nameToPropertyIndex.get(propName);
    if (ob == null) {
      return false;
    }
    
    if ((ob instanceof List)) {
      Iterator<Integer> it = ((List)ob).iterator();
      Integer index = (Integer)it.next();
      
      ExtTypedProperty prop = _properties[index.intValue()];
      

      if (prop.hasTypePropertyName(propName)) {
        String typeId = p.getText();
        p.skipChildren();
        _typeIds[index.intValue()] = typeId;
        while (it.hasNext()) {
          _typeIds[((Integer)it.next()).intValue()] = typeId;
        }
      }
      else {
        TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.copyCurrentStructure(p);
        _tokens[index.intValue()] = tokens;
        while (it.hasNext()) {
          _tokens[((Integer)it.next()).intValue()] = tokens;
        }
      }
      return true;
    }
    


    int index = ((Integer)ob).intValue();
    ExtTypedProperty prop = _properties[index];
    boolean canDeserialize;
    boolean canDeserialize; if (prop.hasTypePropertyName(propName)) {
      _typeIds[index] = p.getText();
      p.skipChildren();
      canDeserialize = (bean != null) && (_tokens[index] != null);
    }
    else {
      TokenBuffer tokens = new TokenBuffer(p, ctxt);
      tokens.copyCurrentStructure(p);
      _tokens[index] = tokens;
      canDeserialize = (bean != null) && (_typeIds[index] != null);
    }
    

    if (canDeserialize) {
      String typeId = _typeIds[index];
      
      _typeIds[index] = null;
      _deserializeAndSet(p, ctxt, bean, index, typeId);
      _tokens[index] = null;
    }
    return true;
  }
  





  public Object complete(JsonParser p, DeserializationContext ctxt, Object bean)
    throws IOException
  {
    int i = 0; for (int len = _properties.length; i < len; i++) {
      String typeId = _typeIds[i];
      if (typeId == null) {
        TokenBuffer tokens = _tokens[i];
        

        if (tokens == null) {
          continue;
        }
        

        JsonToken t = tokens.firstToken();
        if (t.isScalarValue()) {
          JsonParser buffered = tokens.asParser(p);
          buffered.nextToken();
          SettableBeanProperty extProp = _properties[i].getProperty();
          Object result = TypeDeserializer.deserializeIfNatural(buffered, ctxt, extProp.getType());
          if (result != null) {
            extProp.set(bean, result);
            continue;
          }
          
          if (!_properties[i].hasDefaultType()) {
            ctxt.reportPropertyInputMismatch(bean.getClass(), extProp.getName(), "Missing external type id property '%s'", new Object[] {_properties[i]
            
              .getTypePropertyName() });
          } else {
            typeId = _properties[i].getDefaultTypeId();
          }
        }
      } else if (_tokens[i] == null) {
        SettableBeanProperty prop = _properties[i].getProperty();
        
        if ((prop.isRequired()) || 
          (ctxt.isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY))) {
          ctxt.reportPropertyInputMismatch(bean.getClass(), prop.getName(), "Missing property '%s' for external type id '%s'", new Object[] {prop
          
            .getName(), _properties[i].getTypePropertyName() });
        }
        return bean;
      }
      _deserializeAndSet(p, ctxt, bean, i, typeId);
    }
    return bean;
  }
  






  public Object complete(JsonParser p, DeserializationContext ctxt, PropertyValueBuffer buffer, PropertyBasedCreator creator)
    throws IOException
  {
    int len = _properties.length;
    Object[] values = new Object[len];
    for (int i = 0; i < len; i++) {
      String typeId = _typeIds[i];
      ExtTypedProperty extProp = _properties[i];
      if (typeId == null)
      {
        if (_tokens[i] == null) {
          continue;
        }
        

        if (!extProp.hasDefaultType()) {
          ctxt.reportPropertyInputMismatch(_beanType, extProp.getProperty().getName(), "Missing external type id property '%s'", new Object[] {extProp
          
            .getTypePropertyName() });
        } else {
          typeId = extProp.getDefaultTypeId();
        }
      } else if (_tokens[i] == null) {
        SettableBeanProperty prop = extProp.getProperty();
        if ((prop.isRequired()) || 
          (ctxt.isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY))) {
          ctxt.reportPropertyInputMismatch(_beanType, prop.getName(), "Missing property '%s' for external type id '%s'", new Object[] {prop
          
            .getName(), _properties[i].getTypePropertyName() });
        }
      }
      if (_tokens[i] != null) {
        values[i] = _deserialize(p, ctxt, i, typeId);
      }
      
      SettableBeanProperty prop = extProp.getProperty();
      
      if (prop.getCreatorIndex() >= 0) {
        buffer.assignParameter(prop, values[i]);
        

        SettableBeanProperty typeProp = extProp.getTypeProperty();
        
        if ((typeProp != null) && (typeProp.getCreatorIndex() >= 0))
        {
          Object v;
          Object v;
          if (typeProp.getType().hasRawClass(String.class)) {
            v = typeId;
          } else {
            TokenBuffer tb = new TokenBuffer(p, ctxt);
            tb.writeString(typeId);
            v = typeProp.getValueDeserializer().deserialize(tb.asParserOnFirstToken(), ctxt);
            tb.close();
          }
          buffer.assignParameter(typeProp, v);
        }
      }
    }
    Object bean = creator.build(ctxt, buffer);
    
    for (int i = 0; i < len; i++) {
      SettableBeanProperty prop = _properties[i].getProperty();
      if (prop.getCreatorIndex() < 0) {
        prop.set(bean, values[i]);
      }
    }
    return bean;
  }
  

  protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, int index, String typeId)
    throws IOException
  {
    JsonParser p2 = _tokens[index].asParser(p);
    JsonToken t = p2.nextToken();
    
    if (t == JsonToken.VALUE_NULL) {
      return null;
    }
    TokenBuffer merged = new TokenBuffer(p, ctxt);
    merged.writeStartArray();
    merged.writeString(typeId);
    merged.copyCurrentStructure(p2);
    merged.writeEndArray();
    

    JsonParser mp = merged.asParser(p);
    mp.nextToken();
    return _properties[index].getProperty().deserialize(mp, ctxt);
  }
  




  protected final void _deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, int index, String typeId)
    throws IOException
  {
    JsonParser p2 = _tokens[index].asParser(p);
    JsonToken t = p2.nextToken();
    
    if (t == JsonToken.VALUE_NULL) {
      _properties[index].getProperty().set(bean, null);
      return;
    }
    TokenBuffer merged = new TokenBuffer(p, ctxt);
    merged.writeStartArray();
    merged.writeString(typeId);
    
    merged.copyCurrentStructure(p2);
    merged.writeEndArray();
    
    JsonParser mp = merged.asParser(p);
    mp.nextToken();
    _properties[index].getProperty().deserializeAndSet(mp, ctxt, bean);
  }
  




  public static class Builder
  {
    private final JavaType _beanType;
    


    private final List<ExternalTypeHandler.ExtTypedProperty> _properties = new ArrayList();
    private final Map<String, Object> _nameToPropertyIndex = new HashMap();
    
    protected Builder(JavaType t) {
      _beanType = t;
    }
    
    public void addExternal(SettableBeanProperty property, TypeDeserializer typeDeser)
    {
      Integer index = Integer.valueOf(_properties.size());
      _properties.add(new ExternalTypeHandler.ExtTypedProperty(property, typeDeser));
      _addPropertyIndex(property.getName(), index);
      _addPropertyIndex(typeDeser.getPropertyName(), index);
    }
    
    private void _addPropertyIndex(String name, Integer index) {
      Object ob = _nameToPropertyIndex.get(name);
      if (ob == null) {
        _nameToPropertyIndex.put(name, index);
      } else if ((ob instanceof List))
      {
        List<Object> list = (List)ob;
        list.add(index);
      } else {
        List<Object> list = new LinkedList();
        list.add(ob);
        list.add(index);
        _nameToPropertyIndex.put(name, list);
      }
    }
    







    public ExternalTypeHandler build(BeanPropertyMap otherProps)
    {
      int len = _properties.size();
      ExternalTypeHandler.ExtTypedProperty[] extProps = new ExternalTypeHandler.ExtTypedProperty[len];
      for (int i = 0; i < len; i++) {
        ExternalTypeHandler.ExtTypedProperty extProp = (ExternalTypeHandler.ExtTypedProperty)_properties.get(i);
        String typePropId = extProp.getTypePropertyName();
        SettableBeanProperty typeProp = otherProps.find(typePropId);
        if (typeProp != null) {
          extProp.linkTypeProperty(typeProp);
        }
        extProps[i] = extProp;
      }
      return new ExternalTypeHandler(_beanType, extProps, _nameToPropertyIndex, null, null);
    }
  }
  

  private static final class ExtTypedProperty
  {
    private final SettableBeanProperty _property;
    
    private final TypeDeserializer _typeDeserializer;
    
    private final String _typePropertyName;
    
    private SettableBeanProperty _typeProperty;
    

    public ExtTypedProperty(SettableBeanProperty property, TypeDeserializer typeDeser)
    {
      _property = property;
      _typeDeserializer = typeDeser;
      _typePropertyName = typeDeser.getPropertyName();
    }
    


    public void linkTypeProperty(SettableBeanProperty p)
    {
      _typeProperty = p;
    }
    
    public boolean hasTypePropertyName(String n) {
      return n.equals(_typePropertyName);
    }
    
    public boolean hasDefaultType() {
      return _typeDeserializer.getDefaultImpl() != null;
    }
    




    public String getDefaultTypeId()
    {
      Class<?> defaultType = _typeDeserializer.getDefaultImpl();
      if (defaultType == null) {
        return null;
      }
      return _typeDeserializer.getTypeIdResolver().idFromValueAndType(null, defaultType);
    }
    
    public String getTypePropertyName() { return _typePropertyName; }
    
    public SettableBeanProperty getProperty() {
      return _property;
    }
    


    public SettableBeanProperty getTypeProperty()
    {
      return _typeProperty;
    }
  }
}
