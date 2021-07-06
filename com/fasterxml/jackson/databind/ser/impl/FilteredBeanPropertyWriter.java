package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;


public abstract class FilteredBeanPropertyWriter
{
  public FilteredBeanPropertyWriter() {}
  
  public static BeanPropertyWriter constructViewBased(BeanPropertyWriter base, Class<?>[] viewsToIncludeIn)
  {
    if (viewsToIncludeIn.length == 1) {
      return new SingleView(base, viewsToIncludeIn[0]);
    }
    return new MultiView(base, viewsToIncludeIn);
  }
  


  private static final class SingleView
    extends BeanPropertyWriter
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    

    protected final BeanPropertyWriter _delegate;
    

    protected final Class<?> _view;
    


    protected SingleView(BeanPropertyWriter delegate, Class<?> view)
    {
      super();
      _delegate = delegate;
      _view = view;
    }
    
    public SingleView rename(NameTransformer transformer)
    {
      return new SingleView(_delegate.rename(transformer), _view);
    }
    
    public void assignSerializer(JsonSerializer<Object> ser)
    {
      _delegate.assignSerializer(ser);
    }
    
    public void assignNullSerializer(JsonSerializer<Object> nullSer)
    {
      _delegate.assignNullSerializer(nullSer);
    }
    

    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
      throws Exception
    {
      Class<?> activeView = prov.getActiveView();
      if ((activeView == null) || (_view.isAssignableFrom(activeView))) {
        _delegate.serializeAsField(bean, gen, prov);
      } else {
        _delegate.serializeAsOmittedField(bean, gen, prov);
      }
    }
    

    public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov)
      throws Exception
    {
      Class<?> activeView = prov.getActiveView();
      if ((activeView == null) || (_view.isAssignableFrom(activeView))) {
        _delegate.serializeAsElement(bean, gen, prov);
      } else {
        _delegate.serializeAsPlaceholder(bean, gen, prov);
      }
    }
    

    public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider)
      throws JsonMappingException
    {
      Class<?> activeView = provider.getActiveView();
      if ((activeView == null) || (_view.isAssignableFrom(activeView))) {
        super.depositSchemaProperty(v, provider);
      }
    }
  }
  

  private static final class MultiView
    extends BeanPropertyWriter
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    protected final BeanPropertyWriter _delegate;
    protected final Class<?>[] _views;
    
    protected MultiView(BeanPropertyWriter delegate, Class<?>[] views)
    {
      super();
      _delegate = delegate;
      _views = views;
    }
    
    public MultiView rename(NameTransformer transformer)
    {
      return new MultiView(_delegate.rename(transformer), _views);
    }
    
    public void assignSerializer(JsonSerializer<Object> ser)
    {
      _delegate.assignSerializer(ser);
    }
    
    public void assignNullSerializer(JsonSerializer<Object> nullSer)
    {
      _delegate.assignNullSerializer(nullSer);
    }
    

    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
      throws Exception
    {
      if (_inView(prov.getActiveView())) {
        _delegate.serializeAsField(bean, gen, prov);
        return;
      }
      _delegate.serializeAsOmittedField(bean, gen, prov);
    }
    

    public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov)
      throws Exception
    {
      if (_inView(prov.getActiveView())) {
        _delegate.serializeAsElement(bean, gen, prov);
        return;
      }
      _delegate.serializeAsPlaceholder(bean, gen, prov);
    }
    

    public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider)
      throws JsonMappingException
    {
      if (_inView(provider.getActiveView())) {
        super.depositSchemaProperty(v, provider);
      }
    }
    
    private final boolean _inView(Class<?> activeView)
    {
      if (activeView == null) {
        return true;
      }
      int len = _views.length;
      for (int i = 0; i < len; i++) {
        if (_views[i].isAssignableFrom(activeView)) {
          return true;
        }
      }
      return false;
    }
  }
}
