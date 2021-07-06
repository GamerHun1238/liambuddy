package com.fasterxml.jackson.core.type;

import com.fasterxml.jackson.core.JsonToken;






















public class WritableTypeId
{
  public Object forValue;
  public Class<?> forValueType;
  public Object id;
  public String asProperty;
  public Inclusion include;
  public JsonToken valueShape;
  public boolean wrapperWritten;
  public Object extra;
  public WritableTypeId() {}
  
  public static enum Inclusion
  {
    WRAPPER_ARRAY, 
    






    WRAPPER_OBJECT, 
    










    METADATA_PROPERTY, 
    














    PAYLOAD_PROPERTY, 
    














    PARENT_PROPERTY;
    
    private Inclusion() {}
    public boolean requiresObjectContext() { return (this == METADATA_PROPERTY) || (this == PAYLOAD_PROPERTY); }
  }
  































































  public WritableTypeId(Object value, JsonToken valueShape0)
  {
    this(value, valueShape0, null);
  }
  




  public WritableTypeId(Object value, Class<?> valueType0, JsonToken valueShape0)
  {
    this(value, valueShape0, null);
    forValueType = valueType0;
  }
  





  public WritableTypeId(Object value, JsonToken valueShape0, Object id0)
  {
    forValue = value;
    id = id0;
    valueShape = valueShape0;
  }
}
