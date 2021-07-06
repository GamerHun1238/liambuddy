package com.fasterxml.jackson.databind;

import java.io.IOException;

public abstract class KeyDeserializer
{
  public KeyDeserializer() {}
  
  public abstract Object deserializeKey(String paramString, DeserializationContext paramDeserializationContext)
    throws IOException;
  
  public static abstract class None
    extends KeyDeserializer
  {
    public None() {}
  }
}
