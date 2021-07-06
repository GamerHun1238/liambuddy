package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.VersionUtil;
import java.io.IOException;












public abstract class JsonGeneratorImpl
  extends GeneratorBase
{
  protected static final int[] sOutputEscapes = ;
  









  protected final IOContext _ioContext;
  









  protected int[] _outputEscapes = sOutputEscapes;
  









  protected int _maximumNonEscapedChar;
  








  protected CharacterEscapes _characterEscapes;
  








  protected SerializableString _rootValueSeparator = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
  







  protected boolean _cfgUnqNames;
  







  public JsonGeneratorImpl(IOContext ctxt, int features, ObjectCodec codec)
  {
    super(features, codec);
    _ioContext = ctxt;
    if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(features))
    {
      _maximumNonEscapedChar = 127;
    }
    _cfgUnqNames = (!JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(features));
  }
  






  public Version version()
  {
    return VersionUtil.versionFor(getClass());
  }
  







  public JsonGenerator enable(JsonGenerator.Feature f)
  {
    super.enable(f);
    if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
      _cfgUnqNames = false;
    }
    return this;
  }
  

  public JsonGenerator disable(JsonGenerator.Feature f)
  {
    super.disable(f);
    if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
      _cfgUnqNames = true;
    }
    return this;
  }
  

  protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures)
  {
    super._checkStdFeatureChanges(newFeatureFlags, changedFeatures);
    _cfgUnqNames = (!JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(newFeatureFlags));
  }
  
  public JsonGenerator setHighestNonEscapedChar(int charCode)
  {
    _maximumNonEscapedChar = (charCode < 0 ? 0 : charCode);
    return this;
  }
  
  public int getHighestEscapedChar()
  {
    return _maximumNonEscapedChar;
  }
  

  public JsonGenerator setCharacterEscapes(CharacterEscapes esc)
  {
    _characterEscapes = esc;
    if (esc == null) {
      _outputEscapes = sOutputEscapes;
    } else {
      _outputEscapes = esc.getEscapeCodesForAscii();
    }
    return this;
  }
  




  public CharacterEscapes getCharacterEscapes()
  {
    return _characterEscapes;
  }
  
  public JsonGenerator setRootValueSeparator(SerializableString sep)
  {
    _rootValueSeparator = sep;
    return this;
  }
  








  public final void writeStringField(String fieldName, String value)
    throws IOException
  {
    writeFieldName(fieldName);
    writeString(value);
  }
  






  protected void _verifyPrettyValueWrite(String typeMsg, int status)
    throws IOException
  {
    switch (status) {
    case 1: 
      _cfgPrettyPrinter.writeArrayValueSeparator(this);
      break;
    case 2: 
      _cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
      break;
    case 3: 
      _cfgPrettyPrinter.writeRootValueSeparator(this);
      break;
    
    case 0: 
      if (_writeContext.inArray()) {
        _cfgPrettyPrinter.beforeArrayValues(this);
      } else if (_writeContext.inObject()) {
        _cfgPrettyPrinter.beforeObjectEntries(this);
      }
      break;
    case 5: 
      _reportCantWriteValueExpectName(typeMsg);
      break;
    case 4: default: 
      _throwInternal();
    }
  }
  
  protected void _reportCantWriteValueExpectName(String typeMsg)
    throws IOException
  {
    _reportError(String.format("Can not %s, expecting field name (context: %s)", new Object[] { typeMsg, _writeContext
      .typeDesc() }));
  }
}
