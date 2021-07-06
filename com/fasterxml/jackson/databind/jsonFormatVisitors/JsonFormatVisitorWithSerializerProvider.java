package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.SerializerProvider;

public abstract interface JsonFormatVisitorWithSerializerProvider
{
  public abstract SerializerProvider getProvider();
  
  public abstract void setProvider(SerializerProvider paramSerializerProvider);
}
