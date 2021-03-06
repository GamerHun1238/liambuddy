package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean>
{
  private static final long serialVersionUID = 1L;
  
  public AtomicBooleanDeserializer()
  {
    super(AtomicBoolean.class);
  }
  
  public AtomicBoolean deserialize(JsonParser jp, DeserializationContext ctxt) throws java.io.IOException {
    return new AtomicBoolean(_parseBooleanPrimitive(jp, ctxt));
  }
}
