package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;













public abstract interface JsonArrayFormatVisitor
  extends JsonFormatVisitorWithSerializerProvider
{
  public abstract void itemsFormat(JsonFormatVisitable paramJsonFormatVisitable, JavaType paramJavaType)
    throws JsonMappingException;
  
  public abstract void itemsFormat(JsonFormatTypes paramJsonFormatTypes)
    throws JsonMappingException;
  
  public static class Base
    implements JsonArrayFormatVisitor
  {
    protected SerializerProvider _provider;
    
    public Base() {}
    
    public Base(SerializerProvider p)
    {
      _provider = p;
    }
    
    public SerializerProvider getProvider() { return _provider; }
    
    public void setProvider(SerializerProvider p) {
      _provider = p;
    }
    
    public void itemsFormat(JsonFormatVisitable handler, JavaType elementType)
      throws JsonMappingException
    {}
    
    public void itemsFormat(JsonFormatTypes format)
      throws JsonMappingException
    {}
  }
}
