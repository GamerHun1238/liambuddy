package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.io.SerializedString;
import java.io.IOException;
import java.io.Serializable;



























public class MinimalPrettyPrinter
  implements PrettyPrinter, Serializable
{
  private static final long serialVersionUID = 1L;
  protected String _rootValueSeparator;
  protected Separators _separators;
  
  public MinimalPrettyPrinter()
  {
    this(DEFAULT_ROOT_VALUE_SEPARATOR.toString());
  }
  
  public MinimalPrettyPrinter(String rootValueSeparator) {
    _rootValueSeparator = rootValueSeparator;
    _separators = DEFAULT_SEPARATORS;
  }
  
  public void setRootValueSeparator(String sep) {
    _rootValueSeparator = sep;
  }
  


  public MinimalPrettyPrinter setSeparators(Separators separators)
  {
    _separators = separators;
    return this;
  }
  






  public void writeRootValueSeparator(JsonGenerator g)
    throws IOException
  {
    if (_rootValueSeparator != null) {
      g.writeRaw(_rootValueSeparator);
    }
  }
  
  public void writeStartObject(JsonGenerator g)
    throws IOException
  {
    g.writeRaw('{');
  }
  





  public void beforeObjectEntries(JsonGenerator g)
    throws IOException
  {}
  




  public void writeObjectFieldValueSeparator(JsonGenerator g)
    throws IOException
  {
    g.writeRaw(_separators.getObjectFieldValueSeparator());
  }
  







  public void writeObjectEntrySeparator(JsonGenerator g)
    throws IOException
  {
    g.writeRaw(_separators.getObjectEntrySeparator());
  }
  
  public void writeEndObject(JsonGenerator g, int nrOfEntries)
    throws IOException
  {
    g.writeRaw('}');
  }
  
  public void writeStartArray(JsonGenerator g)
    throws IOException
  {
    g.writeRaw('[');
  }
  





  public void beforeArrayValues(JsonGenerator g)
    throws IOException
  {}
  




  public void writeArrayValueSeparator(JsonGenerator g)
    throws IOException
  {
    g.writeRaw(_separators.getArrayValueSeparator());
  }
  
  public void writeEndArray(JsonGenerator g, int nrOfValues)
    throws IOException
  {
    g.writeRaw(']');
  }
}
