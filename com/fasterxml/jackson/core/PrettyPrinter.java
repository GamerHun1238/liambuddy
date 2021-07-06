package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.util.Separators;
import java.io.IOException;























public abstract interface PrettyPrinter
{
  public static final Separators DEFAULT_SEPARATORS = ;
  





  public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
  
  public abstract void writeRootValueSeparator(JsonGenerator paramJsonGenerator)
    throws IOException;
  
  public abstract void writeStartObject(JsonGenerator paramJsonGenerator)
    throws IOException;
  
  public abstract void writeEndObject(JsonGenerator paramJsonGenerator, int paramInt)
    throws IOException;
  
  public abstract void writeObjectEntrySeparator(JsonGenerator paramJsonGenerator)
    throws IOException;
  
  public abstract void writeObjectFieldValueSeparator(JsonGenerator paramJsonGenerator)
    throws IOException;
  
  public abstract void writeStartArray(JsonGenerator paramJsonGenerator)
    throws IOException;
  
  public abstract void writeEndArray(JsonGenerator paramJsonGenerator, int paramInt)
    throws IOException;
  
  public abstract void writeArrayValueSeparator(JsonGenerator paramJsonGenerator)
    throws IOException;
  
  public abstract void beforeArrayValues(JsonGenerator paramJsonGenerator)
    throws IOException;
  
  public abstract void beforeObjectEntries(JsonGenerator paramJsonGenerator)
    throws IOException;
}
