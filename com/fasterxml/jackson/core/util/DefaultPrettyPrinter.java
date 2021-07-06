package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import java.io.IOException;
import java.io.Serializable;















public class DefaultPrettyPrinter
  implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable
{
  private static final long serialVersionUID = 1L;
  public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
  






















  protected Indenter _arrayIndenter = FixedSpaceIndenter.instance;
  






  protected Indenter _objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
  





  protected final SerializableString _rootSeparator;
  





  protected boolean _spacesInObjectEntries = true;
  




  protected transient int _nesting;
  




  protected Separators _separators;
  




  protected String _objectFieldValueSeparatorWithSpaces;
  





  public DefaultPrettyPrinter()
  {
    this(DEFAULT_ROOT_VALUE_SEPARATOR);
  }
  










  public DefaultPrettyPrinter(String rootSeparator)
  {
    this(rootSeparator == null ? null : new SerializedString(rootSeparator));
  }
  







  public DefaultPrettyPrinter(SerializableString rootSeparator)
  {
    _rootSeparator = rootSeparator;
    withSeparators(DEFAULT_SEPARATORS);
  }
  
  public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
    this(base, _rootSeparator);
  }
  

  public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator)
  {
    _arrayIndenter = _arrayIndenter;
    _objectIndenter = _objectIndenter;
    _spacesInObjectEntries = _spacesInObjectEntries;
    _nesting = _nesting;
    
    _separators = _separators;
    _objectFieldValueSeparatorWithSpaces = _objectFieldValueSeparatorWithSpaces;
    
    _rootSeparator = rootSeparator;
  }
  
  public DefaultPrettyPrinter withRootSeparator(SerializableString rootSeparator)
  {
    if ((_rootSeparator == rootSeparator) || ((rootSeparator != null) && 
      (rootSeparator.equals(_rootSeparator)))) {
      return this;
    }
    return new DefaultPrettyPrinter(this, rootSeparator);
  }
  


  public DefaultPrettyPrinter withRootSeparator(String rootSeparator)
  {
    return withRootSeparator(rootSeparator == null ? null : new SerializedString(rootSeparator));
  }
  
  public void indentArraysWith(Indenter i) {
    _arrayIndenter = (i == null ? NopIndenter.instance : i);
  }
  
  public void indentObjectsWith(Indenter i) {
    _objectIndenter = (i == null ? NopIndenter.instance : i);
  }
  


  public DefaultPrettyPrinter withArrayIndenter(Indenter i)
  {
    if (i == null) {
      i = NopIndenter.instance;
    }
    if (_arrayIndenter == i) {
      return this;
    }
    DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
    _arrayIndenter = i;
    return pp;
  }
  


  public DefaultPrettyPrinter withObjectIndenter(Indenter i)
  {
    if (i == null) {
      i = NopIndenter.instance;
    }
    if (_objectIndenter == i) {
      return this;
    }
    DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
    _objectIndenter = i;
    return pp;
  }
  







  public DefaultPrettyPrinter withSpacesInObjectEntries()
  {
    return _withSpaces(true);
  }
  







  public DefaultPrettyPrinter withoutSpacesInObjectEntries()
  {
    return _withSpaces(false);
  }
  
  protected DefaultPrettyPrinter _withSpaces(boolean state)
  {
    if (_spacesInObjectEntries == state) {
      return this;
    }
    DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
    _spacesInObjectEntries = state;
    return pp;
  }
  


  public DefaultPrettyPrinter withSeparators(Separators separators)
  {
    _separators = separators;
    _objectFieldValueSeparatorWithSpaces = (" " + separators.getObjectFieldValueSeparator() + " ");
    return this;
  }
  






  public DefaultPrettyPrinter createInstance()
  {
    if (getClass() != DefaultPrettyPrinter.class) {
      throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName() + " does not override method; it has to");
    }
    
    return new DefaultPrettyPrinter(this);
  }
  






  public void writeRootValueSeparator(JsonGenerator g)
    throws IOException
  {
    if (_rootSeparator != null) {
      g.writeRaw(_rootSeparator);
    }
  }
  
  public void writeStartObject(JsonGenerator g)
    throws IOException
  {
    g.writeRaw('{');
    if (!_objectIndenter.isInline()) {
      _nesting += 1;
    }
  }
  
  public void beforeObjectEntries(JsonGenerator g)
    throws IOException
  {
    _objectIndenter.writeIndentation(g, _nesting);
  }
  









  public void writeObjectFieldValueSeparator(JsonGenerator g)
    throws IOException
  {
    if (_spacesInObjectEntries) {
      g.writeRaw(_objectFieldValueSeparatorWithSpaces);
    } else {
      g.writeRaw(_separators.getObjectFieldValueSeparator());
    }
  }
  









  public void writeObjectEntrySeparator(JsonGenerator g)
    throws IOException
  {
    g.writeRaw(_separators.getObjectEntrySeparator());
    _objectIndenter.writeIndentation(g, _nesting);
  }
  
  public void writeEndObject(JsonGenerator g, int nrOfEntries)
    throws IOException
  {
    if (!_objectIndenter.isInline()) {
      _nesting -= 1;
    }
    if (nrOfEntries > 0) {
      _objectIndenter.writeIndentation(g, _nesting);
    } else {
      g.writeRaw(' ');
    }
    g.writeRaw('}');
  }
  
  public void writeStartArray(JsonGenerator g)
    throws IOException
  {
    if (!_arrayIndenter.isInline()) {
      _nesting += 1;
    }
    g.writeRaw('[');
  }
  
  public void beforeArrayValues(JsonGenerator g) throws IOException
  {
    _arrayIndenter.writeIndentation(g, _nesting);
  }
  









  public void writeArrayValueSeparator(JsonGenerator g)
    throws IOException
  {
    g.writeRaw(_separators.getArrayValueSeparator());
    _arrayIndenter.writeIndentation(g, _nesting);
  }
  
  public void writeEndArray(JsonGenerator g, int nrOfValues)
    throws IOException
  {
    if (!_arrayIndenter.isInline()) {
      _nesting -= 1;
    }
    if (nrOfValues > 0) {
      _arrayIndenter.writeIndentation(g, _nesting);
    } else {
      g.writeRaw(' ');
    }
    g.writeRaw(']');
  }
  
  public static abstract interface Indenter
  {
    public abstract void writeIndentation(JsonGenerator paramJsonGenerator, int paramInt)
      throws IOException;
    
    public abstract boolean isInline();
  }
  
  public static class NopIndenter implements DefaultPrettyPrinter.Indenter, Serializable
  {
    public NopIndenter() {}
    
    public static final NopIndenter instance = new NopIndenter();
    
    public void writeIndentation(JsonGenerator g, int level) throws IOException
    {}
    
    public boolean isInline() {
      return true;
    }
  }
  



  public static class FixedSpaceIndenter
    extends DefaultPrettyPrinter.NopIndenter
  {
    public static final FixedSpaceIndenter instance = new FixedSpaceIndenter();
    
    public FixedSpaceIndenter() {}
    
    public void writeIndentation(JsonGenerator g, int level) throws IOException {
      g.writeRaw(' ');
    }
    
    public boolean isInline() {
      return true;
    }
  }
}
