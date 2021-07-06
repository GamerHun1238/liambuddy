package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;






public abstract class GeneratorBase
  extends JsonGenerator
{
  public static final int SURR1_FIRST = 55296;
  public static final int SURR1_LAST = 56319;
  public static final int SURR2_FIRST = 56320;
  public static final int SURR2_LAST = 57343;
  protected static final int DERIVED_FEATURES_MASK = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
    .getMask() | JsonGenerator.Feature.ESCAPE_NON_ASCII
    .getMask() | JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION
    .getMask();
  




  protected static final String WRITE_BINARY = "write a binary value";
  




  protected static final String WRITE_BOOLEAN = "write a boolean value";
  




  protected static final String WRITE_NULL = "write a null";
  




  protected static final String WRITE_NUMBER = "write a number";
  



  protected static final String WRITE_RAW = "write a raw (unencoded) value";
  



  protected static final String WRITE_STRING = "write a string";
  



  protected static final int MAX_BIG_DECIMAL_SCALE = 9999;
  



  protected ObjectCodec _objectCodec;
  



  protected int _features;
  



  protected boolean _cfgNumbersAsStrings;
  



  protected JsonWriteContext _writeContext;
  



  protected boolean _closed;
  




  protected GeneratorBase(int features, ObjectCodec codec)
  {
    _features = features;
    _objectCodec = codec;
    
    DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
    _writeContext = JsonWriteContext.createRootContext(dups);
    _cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
  }
  




  protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt)
  {
    _features = features;
    _objectCodec = codec;
    _writeContext = ctxt;
    _cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
  }
  



  public Version version()
  {
    return PackageVersion.VERSION;
  }
  
  public Object getCurrentValue() {
    return _writeContext.getCurrentValue();
  }
  
  public void setCurrentValue(Object v)
  {
    if (_writeContext != null) {
      _writeContext.setCurrentValue(v);
    }
  }
  







  public final boolean isEnabled(JsonGenerator.Feature f) { return (_features & f.getMask()) != 0; }
  public int getFeatureMask() { return _features; }
  



  public JsonGenerator enable(JsonGenerator.Feature f)
  {
    int mask = f.getMask();
    _features |= mask;
    if ((mask & DERIVED_FEATURES_MASK) != 0)
    {
      if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
        _cfgNumbersAsStrings = true;
      } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
        setHighestNonEscapedChar(127);
      } else if ((f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) && 
        (_writeContext.getDupDetector() == null)) {
        _writeContext = _writeContext.withDupDetector(DupDetector.rootDetector(this));
      }
    }
    
    return this;
  }
  

  public JsonGenerator disable(JsonGenerator.Feature f)
  {
    int mask = f.getMask();
    _features &= (mask ^ 0xFFFFFFFF);
    if ((mask & DERIVED_FEATURES_MASK) != 0) {
      if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
        _cfgNumbersAsStrings = false;
      } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
        setHighestNonEscapedChar(0);
      } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) {
        _writeContext = _writeContext.withDupDetector(null);
      }
    }
    return this;
  }
  
  @Deprecated
  public JsonGenerator setFeatureMask(int newMask)
  {
    int changed = newMask ^ _features;
    _features = newMask;
    if (changed != 0) {
      _checkStdFeatureChanges(newMask, changed);
    }
    return this;
  }
  
  public JsonGenerator overrideStdFeatures(int values, int mask)
  {
    int oldState = _features;
    int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
    int changed = oldState ^ newState;
    if (changed != 0) {
      _features = newState;
      _checkStdFeatureChanges(newState, changed);
    }
    return this;
  }
  










  protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures)
  {
    if ((changedFeatures & DERIVED_FEATURES_MASK) == 0) {
      return;
    }
    _cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(newFeatureFlags);
    if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(changedFeatures)) {
      if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(newFeatureFlags)) {
        setHighestNonEscapedChar(127);
      } else {
        setHighestNonEscapedChar(0);
      }
    }
    if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(changedFeatures)) {
      if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(newFeatureFlags)) {
        if (_writeContext.getDupDetector() == null) {
          _writeContext = _writeContext.withDupDetector(DupDetector.rootDetector(this));
        }
      } else {
        _writeContext = _writeContext.withDupDetector(null);
      }
    }
  }
  
  public JsonGenerator useDefaultPrettyPrinter()
  {
    if (getPrettyPrinter() != null) {
      return this;
    }
    return setPrettyPrinter(_constructDefaultPrettyPrinter());
  }
  
  public JsonGenerator setCodec(ObjectCodec oc) {
    _objectCodec = oc;
    return this;
  }
  
  public ObjectCodec getCodec() { return _objectCodec; }
  









  public JsonStreamContext getOutputContext()
  {
    return _writeContext;
  }
  










  public void writeStartObject(Object forValue)
    throws IOException
  {
    writeStartObject();
    if (forValue != null) {
      setCurrentValue(forValue);
    }
  }
  




  public void writeFieldName(SerializableString name)
    throws IOException
  {
    writeFieldName(name.getValue());
  }
  









  public void writeString(SerializableString text)
    throws IOException
  {
    writeString(text.getValue());
  }
  
  public void writeRawValue(String text) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text);
  }
  
  public void writeRawValue(String text, int offset, int len) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text, offset, len);
  }
  
  public void writeRawValue(char[] text, int offset, int len) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text, offset, len);
  }
  
  public void writeRawValue(SerializableString text) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text);
  }
  
  public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
    throws IOException
  {
    _reportUnsupportedOperation();
    return 0;
  }
  























  public void writeObject(Object value)
    throws IOException
  {
    if (value == null)
    {
      writeNull();


    }
    else
    {

      if (_objectCodec != null) {
        _objectCodec.writeValue(this, value);
        return;
      }
      _writeSimpleObject(value);
    }
  }
  
  public void writeTree(TreeNode rootNode)
    throws IOException
  {
    if (rootNode == null) {
      writeNull();
    } else {
      if (_objectCodec == null) {
        throw new IllegalStateException("No ObjectCodec defined");
      }
      _objectCodec.writeValue(this, rootNode);
    }
  }
  


  public abstract void flush()
    throws IOException;
  

  public void close()
    throws IOException { _closed = true; }
  public boolean isClosed() { return _closed; }
  








  protected abstract void _releaseBuffers();
  







  protected abstract void _verifyValueWrite(String paramString)
    throws IOException;
  







  protected PrettyPrinter _constructDefaultPrettyPrinter()
  {
    return new DefaultPrettyPrinter();
  }
  




  protected String _asString(BigDecimal value)
    throws IOException
  {
    if (JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(_features))
    {
      int scale = value.scale();
      if ((scale < 55537) || (scale > 9999)) {
        _reportError(String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", new Object[] {
        
          Integer.valueOf(scale), Integer.valueOf(9999), Integer.valueOf(9999) }));
      }
      return value.toPlainString();
    }
    return value.toString();
  }
  









  protected final int _decodeSurrogate(int surr1, int surr2)
    throws IOException
  {
    if ((surr2 < 56320) || (surr2 > 57343)) {
      String msg = "Incomplete surrogate pair: first char 0x" + Integer.toHexString(surr1) + ", second 0x" + Integer.toHexString(surr2);
      _reportError(msg);
    }
    int c = 65536 + (surr1 - 55296 << 10) + (surr2 - 56320);
    return c;
  }
}
