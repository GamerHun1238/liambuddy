package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;





public class UnwrappedPropertyHandler
{
  protected final List<SettableBeanProperty> _properties;
  
  public UnwrappedPropertyHandler()
  {
    _properties = new ArrayList();
  }
  
  protected UnwrappedPropertyHandler(List<SettableBeanProperty> props) { _properties = props; }
  
  public void addProperty(SettableBeanProperty property)
  {
    _properties.add(property);
  }
  
  public UnwrappedPropertyHandler renameAll(NameTransformer transformer)
  {
    ArrayList<SettableBeanProperty> newProps = new ArrayList(_properties.size());
    for (SettableBeanProperty prop : _properties) {
      String newName = transformer.transform(prop.getName());
      prop = prop.withSimpleName(newName);
      JsonDeserializer<?> deser = prop.getValueDeserializer();
      if (deser != null)
      {

        JsonDeserializer<Object> newDeser = deser.unwrappingDeserializer(transformer);
        if (newDeser != deser) {
          prop = prop.withValueDeserializer(newDeser);
        }
      }
      newProps.add(prop);
    }
    return new UnwrappedPropertyHandler(newProps);
  }
  


  public Object processUnwrapped(JsonParser originalParser, DeserializationContext ctxt, Object bean, TokenBuffer buffered)
    throws IOException
  {
    int i = 0; for (int len = _properties.size(); i < len; i++) {
      SettableBeanProperty prop = (SettableBeanProperty)_properties.get(i);
      JsonParser p = buffered.asParser();
      p.nextToken();
      prop.deserializeAndSet(p, ctxt, bean);
    }
    return bean;
  }
}