package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator.Base;





public class JsonLocationInstantiator
  extends ValueInstantiator.Base
{
  private static final long serialVersionUID = 1L;
  
  public JsonLocationInstantiator()
  {
    super(JsonLocation.class);
  }
  
  public boolean canCreateFromObjectWith() {
    return true;
  }
  
  public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
    JavaType intType = config.constructType(Integer.TYPE);
    JavaType longType = config.constructType(Long.TYPE);
    return new SettableBeanProperty[] {
      creatorProp("sourceRef", config.constructType(Object.class), 0), 
      creatorProp("byteOffset", longType, 1), 
      creatorProp("charOffset", longType, 2), 
      creatorProp("lineNr", intType, 3), 
      creatorProp("columnNr", intType, 4) };
  }
  
  private static CreatorProperty creatorProp(String name, JavaType type, int index)
  {
    return new CreatorProperty(PropertyName.construct(name), type, null, null, null, null, index, null, PropertyMetadata.STD_REQUIRED);
  }
  

  public Object createFromObjectWith(DeserializationContext ctxt, Object[] args)
  {
    return new JsonLocation(args[0], _long(args[1]), _long(args[2]), 
      _int(args[3]), _int(args[4]));
  }
  
  private static final long _long(Object o) {
    return o == null ? 0L : ((Number)o).longValue();
  }
  
  private static final int _int(Object o) {
    return o == null ? 0 : ((Number)o).intValue();
  }
}
