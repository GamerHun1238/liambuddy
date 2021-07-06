package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.AccessPattern;

public abstract interface NullValueProvider
{
  public abstract Object getNullValue(DeserializationContext paramDeserializationContext)
    throws JsonMappingException;
  
  public abstract AccessPattern getNullAccessPattern();
}
