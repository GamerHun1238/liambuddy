package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;

public abstract interface ResolvableDeserializer
{
  public abstract void resolve(DeserializationContext paramDeserializationContext)
    throws JsonMappingException;
}
