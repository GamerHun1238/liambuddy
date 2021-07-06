package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.InputDecorator;
import com.fasterxml.jackson.core.io.OutputDecorator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.io.UTF8Writer;
import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.CharArrayReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
































public class JsonFactory
  extends TokenStreamFactory
  implements Versioned, Serializable
{
  private static final long serialVersionUID = 2L;
  public static final String FORMAT_NAME_JSON = "JSON";
  
  public static enum Feature
  {
    INTERN_FIELD_NAMES(true), 
    








    CANONICALIZE_FIELD_NAMES(true), 
    














    FAIL_ON_SYMBOL_HASH_OVERFLOW(true), 
    















    USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true);
    




    private final boolean _defaultState;
    




    public static int collectDefaults()
    {
      int flags = 0;
      for (Feature f : values()) {
        if (f.enabledByDefault()) flags |= f.getMask();
      }
      return flags;
    }
    
    private Feature(boolean defaultState) { _defaultState = defaultState; }
    
    public boolean enabledByDefault() { return _defaultState; }
    public boolean enabledIn(int flags) { return (flags & getMask()) != 0; }
    public int getMask() { return 1 << ordinal(); }
  }
  















  protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = ;
  




  protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
  




  protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
  
  public static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
  







  public static final char DEFAULT_QUOTE_CHAR = '"';
  







  protected final transient CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer.createRoot();
  









  protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
  









  protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
  



  protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
  



  protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
  







  protected ObjectCodec _objectCodec;
  







  protected CharacterEscapes _characterEscapes;
  







  protected InputDecorator _inputDecorator;
  






  protected OutputDecorator _outputDecorator;
  






  protected SerializableString _rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
  










  protected int _maximumNonEscapedChar;
  










  protected final char _quoteChar;
  









  public JsonFactory() { this((ObjectCodec)null); }
  
  public JsonFactory(ObjectCodec oc) {
    _objectCodec = oc;
    _quoteChar = '"';
  }
  





  protected JsonFactory(JsonFactory src, ObjectCodec codec)
  {
    _objectCodec = codec;
    

    _factoryFeatures = _factoryFeatures;
    _parserFeatures = _parserFeatures;
    _generatorFeatures = _generatorFeatures;
    _inputDecorator = _inputDecorator;
    _outputDecorator = _outputDecorator;
    

    _characterEscapes = _characterEscapes;
    _rootValueSeparator = _rootValueSeparator;
    _maximumNonEscapedChar = _maximumNonEscapedChar;
    _quoteChar = _quoteChar;
  }
  




  public JsonFactory(JsonFactoryBuilder b)
  {
    _objectCodec = null;
    

    _factoryFeatures = _factoryFeatures;
    _parserFeatures = _streamReadFeatures;
    _generatorFeatures = _streamWriteFeatures;
    _inputDecorator = _inputDecorator;
    _outputDecorator = _outputDecorator;
    

    _characterEscapes = _characterEscapes;
    _rootValueSeparator = _rootValueSeparator;
    _maximumNonEscapedChar = _maximumNonEscapedChar;
    _quoteChar = _quoteChar;
  }
  







  protected JsonFactory(TSFBuilder<?, ?> b, boolean bogus)
  {
    _objectCodec = null;
    
    _factoryFeatures = _factoryFeatures;
    _parserFeatures = _streamReadFeatures;
    _generatorFeatures = _streamWriteFeatures;
    _inputDecorator = _inputDecorator;
    _outputDecorator = _outputDecorator;
    

    _characterEscapes = null;
    _rootValueSeparator = null;
    _maximumNonEscapedChar = 0;
    _quoteChar = '"';
  }
  






  public TSFBuilder<?, ?> rebuild()
  {
    _requireJSONFactory("Factory implementation for format (%s) MUST override `rebuild()` method");
    return new JsonFactoryBuilder(this);
  }
  








  public static TSFBuilder<?, ?> builder()
  {
    return new JsonFactoryBuilder();
  }
  














  public JsonFactory copy()
  {
    _checkInvalidCopy(JsonFactory.class);
    
    return new JsonFactory(this, null);
  }
  



  protected void _checkInvalidCopy(Class<?> exp)
  {
    if (getClass() != exp)
    {
      throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to");
    }
  }
  










  protected Object readResolve()
  {
    return new JsonFactory(this, _objectCodec);
  }
  




















  public boolean requiresPropertyOrdering()
  {
    return false;
  }
  










  public boolean canHandleBinaryNatively()
  {
    return false;
  }
  









  public boolean canUseCharArrays()
  {
    return true;
  }
  









  public boolean canParseAsync()
  {
    return _isJSONFactory();
  }
  
  public Class<? extends FormatFeature> getFormatReadFeatureType()
  {
    return null;
  }
  
  public Class<? extends FormatFeature> getFormatWriteFeatureType()
  {
    return null;
  }
  
















  public boolean canUseSchema(FormatSchema schema)
  {
    if (schema == null) {
      return false;
    }
    String ourFormat = getFormatName();
    return (ourFormat != null) && (ourFormat.equals(schema.getSchemaType()));
  }
  












  public String getFormatName()
  {
    if (getClass() == JsonFactory.class) {
      return "JSON";
    }
    return null;
  }
  




  public MatchStrength hasFormat(InputAccessor acc)
    throws IOException
  {
    if (getClass() == JsonFactory.class) {
      return hasJSONFormat(acc);
    }
    return null;
  }
  












  public boolean requiresCustomCodec()
  {
    return false;
  }
  



  protected MatchStrength hasJSONFormat(InputAccessor acc)
    throws IOException
  {
    return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
  }
  






  public Version version()
  {
    return PackageVersion.VERSION;
  }
  











  @Deprecated
  public final JsonFactory configure(Feature f, boolean state)
  {
    return state ? enable(f) : disable(f);
  }
  





  @Deprecated
  public JsonFactory enable(Feature f)
  {
    _factoryFeatures |= f.getMask();
    return this;
  }
  





  @Deprecated
  public JsonFactory disable(Feature f)
  {
    _factoryFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    return this;
  }
  


  public final boolean isEnabled(Feature f)
  {
    return (_factoryFeatures & f.getMask()) != 0;
  }
  
  public final int getParserFeatures()
  {
    return _parserFeatures;
  }
  
  public final int getGeneratorFeatures()
  {
    return _generatorFeatures;
  }
  

  public int getFormatParserFeatures()
  {
    return 0;
  }
  

  public int getFormatGeneratorFeatures()
  {
    return 0;
  }
  









  public final JsonFactory configure(JsonParser.Feature f, boolean state)
  {
    return state ? enable(f) : disable(f);
  }
  



  public JsonFactory enable(JsonParser.Feature f)
  {
    _parserFeatures |= f.getMask();
    return this;
  }
  



  public JsonFactory disable(JsonParser.Feature f)
  {
    _parserFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    return this;
  }
  



  public final boolean isEnabled(JsonParser.Feature f)
  {
    return (_parserFeatures & f.getMask()) != 0;
  }
  


  public final boolean isEnabled(StreamReadFeature f)
  {
    return (_parserFeatures & f.mappedFeature().getMask()) != 0;
  }
  



  public InputDecorator getInputDecorator()
  {
    return _inputDecorator;
  }
  




  @Deprecated
  public JsonFactory setInputDecorator(InputDecorator d)
  {
    _inputDecorator = d;
    return this;
  }
  









  public final JsonFactory configure(JsonGenerator.Feature f, boolean state)
  {
    return state ? enable(f) : disable(f);
  }
  



  public JsonFactory enable(JsonGenerator.Feature f)
  {
    _generatorFeatures |= f.getMask();
    return this;
  }
  



  public JsonFactory disable(JsonGenerator.Feature f)
  {
    _generatorFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    return this;
  }
  



  public final boolean isEnabled(JsonGenerator.Feature f)
  {
    return (_generatorFeatures & f.getMask()) != 0;
  }
  


  public final boolean isEnabled(StreamWriteFeature f)
  {
    return (_generatorFeatures & f.mappedFeature().getMask()) != 0;
  }
  


  public CharacterEscapes getCharacterEscapes()
  {
    return _characterEscapes;
  }
  


  public JsonFactory setCharacterEscapes(CharacterEscapes esc)
  {
    _characterEscapes = esc;
    return this;
  }
  



  public OutputDecorator getOutputDecorator()
  {
    return _outputDecorator;
  }
  




  @Deprecated
  public JsonFactory setOutputDecorator(OutputDecorator d)
  {
    _outputDecorator = d;
    return this;
  }
  








  public JsonFactory setRootValueSeparator(String sep)
  {
    _rootValueSeparator = (sep == null ? null : new SerializedString(sep));
    return this;
  }
  


  public String getRootValueSeparator()
  {
    return _rootValueSeparator == null ? null : _rootValueSeparator.getValue();
  }
  












  public JsonFactory setCodec(ObjectCodec oc)
  {
    _objectCodec = oc;
    return this;
  }
  
  public ObjectCodec getCodec() { return _objectCodec; }
  


























  public JsonParser createParser(File f)
    throws IOException, JsonParseException
  {
    IOContext ctxt = _createContext(f, true);
    InputStream in = new FileInputStream(f);
    return _createParser(_decorate(in, ctxt), ctxt);
  }
  




















  public JsonParser createParser(URL url)
    throws IOException, JsonParseException
  {
    IOContext ctxt = _createContext(url, true);
    InputStream in = _optimizedStreamFromURL(url);
    return _createParser(_decorate(in, ctxt), ctxt);
  }
  




















  public JsonParser createParser(InputStream in)
    throws IOException, JsonParseException
  {
    IOContext ctxt = _createContext(in, false);
    return _createParser(_decorate(in, ctxt), ctxt);
  }
  














  public JsonParser createParser(Reader r)
    throws IOException, JsonParseException
  {
    IOContext ctxt = _createContext(r, false);
    return _createParser(_decorate(r, ctxt), ctxt);
  }
  





  public JsonParser createParser(byte[] data)
    throws IOException, JsonParseException
  {
    IOContext ctxt = _createContext(data, true);
    if (_inputDecorator != null) {
      InputStream in = _inputDecorator.decorate(ctxt, data, 0, data.length);
      if (in != null) {
        return _createParser(in, ctxt);
      }
    }
    return _createParser(data, 0, data.length, ctxt);
  }
  









  public JsonParser createParser(byte[] data, int offset, int len)
    throws IOException, JsonParseException
  {
    IOContext ctxt = _createContext(data, true);
    
    if (_inputDecorator != null) {
      InputStream in = _inputDecorator.decorate(ctxt, data, offset, len);
      if (in != null) {
        return _createParser(in, ctxt);
      }
    }
    return _createParser(data, offset, len, ctxt);
  }
  





  public JsonParser createParser(String content)
    throws IOException, JsonParseException
  {
    int strLen = content.length();
    
    if ((_inputDecorator != null) || (strLen > 32768) || (!canUseCharArrays()))
    {

      return createParser(new StringReader(content));
    }
    IOContext ctxt = _createContext(content, true);
    char[] buf = ctxt.allocTokenBuffer(strLen);
    content.getChars(0, strLen, buf, 0);
    return _createParser(buf, 0, strLen, ctxt, true);
  }
  





  public JsonParser createParser(char[] content)
    throws IOException
  {
    return createParser(content, 0, content.length);
  }
  




  public JsonParser createParser(char[] content, int offset, int len)
    throws IOException
  {
    if (_inputDecorator != null) {
      return createParser(new CharArrayReader(content, offset, len));
    }
    return _createParser(content, offset, len, _createContext(content, true), false);
  }
  










  public JsonParser createParser(DataInput in)
    throws IOException
  {
    IOContext ctxt = _createContext(in, false);
    return _createParser(_decorate(in, ctxt), ctxt);
  }
  




















  public JsonParser createNonBlockingByteArrayParser()
    throws IOException
  {
    _requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
    IOContext ctxt = _createNonBlockingContext(null);
    ByteQuadsCanonicalizer can = _byteSymbolCanonicalizer.makeChild(_factoryFeatures);
    return new NonBlockingJsonParser(ctxt, _parserFeatures, can);
  }
  






























  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc)
    throws IOException
  {
    IOContext ctxt = _createContext(out, false);
    ctxt.setEncoding(enc);
    if (enc == JsonEncoding.UTF8) {
      return _createUTF8Generator(_decorate(out, ctxt), ctxt);
    }
    Writer w = _createWriter(out, enc, ctxt);
    return _createGenerator(_decorate(w, ctxt), ctxt);
  }
  







  public JsonGenerator createGenerator(OutputStream out)
    throws IOException
  {
    return createGenerator(out, JsonEncoding.UTF8);
  }
  














  public JsonGenerator createGenerator(Writer w)
    throws IOException
  {
    IOContext ctxt = _createContext(w, false);
    return _createGenerator(_decorate(w, ctxt), ctxt);
  }
  
















  public JsonGenerator createGenerator(File f, JsonEncoding enc)
    throws IOException
  {
    OutputStream out = new FileOutputStream(f);
    
    IOContext ctxt = _createContext(out, true);
    ctxt.setEncoding(enc);
    if (enc == JsonEncoding.UTF8) {
      return _createUTF8Generator(_decorate(out, ctxt), ctxt);
    }
    Writer w = _createWriter(out, enc, ctxt);
    return _createGenerator(_decorate(w, ctxt), ctxt);
  }
  





  public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc)
    throws IOException
  {
    return createGenerator(_createDataOutputWrapper(out), enc);
  }
  







  public JsonGenerator createGenerator(DataOutput out)
    throws IOException
  {
    return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
  }
  























  @Deprecated
  public JsonParser createJsonParser(File f)
    throws IOException, JsonParseException
  {
    return createParser(f);
  }
  


















  @Deprecated
  public JsonParser createJsonParser(URL url)
    throws IOException, JsonParseException
  {
    return createParser(url);
  }
  



















  @Deprecated
  public JsonParser createJsonParser(InputStream in)
    throws IOException, JsonParseException
  {
    return createParser(in);
  }
  












  @Deprecated
  public JsonParser createJsonParser(Reader r)
    throws IOException, JsonParseException
  {
    return createParser(r);
  }
  



  @Deprecated
  public JsonParser createJsonParser(byte[] data)
    throws IOException, JsonParseException
  {
    return createParser(data);
  }
  








  @Deprecated
  public JsonParser createJsonParser(byte[] data, int offset, int len)
    throws IOException, JsonParseException
  {
    return createParser(data, offset, len);
  }
  




  @Deprecated
  public JsonParser createJsonParser(String content)
    throws IOException, JsonParseException
  {
    return createParser(content);
  }
  


























  @Deprecated
  public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc)
    throws IOException
  {
    return createGenerator(out, enc);
  }
  













  @Deprecated
  public JsonGenerator createJsonGenerator(Writer out)
    throws IOException
  {
    return createGenerator(out);
  }
  






  @Deprecated
  public JsonGenerator createJsonGenerator(OutputStream out)
    throws IOException
  {
    return createGenerator(out, JsonEncoding.UTF8);
  }
  


















  protected JsonParser _createParser(InputStream in, IOContext ctxt)
    throws IOException
  {
    return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(_parserFeatures, _objectCodec, _byteSymbolCanonicalizer, _rootCharSymbols, _factoryFeatures);
  }
  











  protected JsonParser _createParser(Reader r, IOContext ctxt)
    throws IOException
  {
    return new ReaderBasedJsonParser(ctxt, _parserFeatures, r, _objectCodec, _rootCharSymbols
      .makeChild(_factoryFeatures));
  }
  





  protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable)
    throws IOException
  {
    return new ReaderBasedJsonParser(ctxt, _parserFeatures, null, _objectCodec, _rootCharSymbols
      .makeChild(_factoryFeatures), data, offset, offset + len, recyclable);
  }
  











  protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt)
    throws IOException
  {
    return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(_parserFeatures, _objectCodec, _byteSymbolCanonicalizer, _rootCharSymbols, _factoryFeatures);
  }
  







  protected JsonParser _createParser(DataInput input, IOContext ctxt)
    throws IOException
  {
    _requireJSONFactory("InputData source not (yet?) supported for this format (%s)");
    

    int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
    ByteQuadsCanonicalizer can = _byteSymbolCanonicalizer.makeChild(_factoryFeatures);
    return new UTF8DataInputJsonParser(ctxt, _parserFeatures, input, _objectCodec, can, firstByte);
  }
  

















  protected JsonGenerator _createGenerator(Writer out, IOContext ctxt)
    throws IOException
  {
    WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, _generatorFeatures, _objectCodec, out, _quoteChar);
    
    if (_maximumNonEscapedChar > 0) {
      gen.setHighestNonEscapedChar(_maximumNonEscapedChar);
    }
    if (_characterEscapes != null) {
      gen.setCharacterEscapes(_characterEscapes);
    }
    SerializableString rootSep = _rootValueSeparator;
    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
      gen.setRootValueSeparator(rootSep);
    }
    return gen;
  }
  








  protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt)
    throws IOException
  {
    UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, _generatorFeatures, _objectCodec, out, _quoteChar);
    
    if (_maximumNonEscapedChar > 0) {
      gen.setHighestNonEscapedChar(_maximumNonEscapedChar);
    }
    if (_characterEscapes != null) {
      gen.setCharacterEscapes(_characterEscapes);
    }
    SerializableString rootSep = _rootValueSeparator;
    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
      gen.setRootValueSeparator(rootSep);
    }
    return gen;
  }
  
  protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt)
    throws IOException
  {
    if (enc == JsonEncoding.UTF8) {
      return new UTF8Writer(ctxt, out);
    }
    
    return new OutputStreamWriter(out, enc.getJavaName());
  }
  







  protected final InputStream _decorate(InputStream in, IOContext ctxt)
    throws IOException
  {
    if (_inputDecorator != null) {
      InputStream in2 = _inputDecorator.decorate(ctxt, in);
      if (in2 != null) {
        return in2;
      }
    }
    return in;
  }
  

  protected final Reader _decorate(Reader in, IOContext ctxt)
    throws IOException
  {
    if (_inputDecorator != null) {
      Reader in2 = _inputDecorator.decorate(ctxt, in);
      if (in2 != null) {
        return in2;
      }
    }
    return in;
  }
  

  protected final DataInput _decorate(DataInput in, IOContext ctxt)
    throws IOException
  {
    if (_inputDecorator != null) {
      DataInput in2 = _inputDecorator.decorate(ctxt, in);
      if (in2 != null) {
        return in2;
      }
    }
    return in;
  }
  

  protected final OutputStream _decorate(OutputStream out, IOContext ctxt)
    throws IOException
  {
    if (_outputDecorator != null) {
      OutputStream out2 = _outputDecorator.decorate(ctxt, out);
      if (out2 != null) {
        return out2;
      }
    }
    return out;
  }
  

  protected final Writer _decorate(Writer out, IOContext ctxt)
    throws IOException
  {
    if (_outputDecorator != null) {
      Writer out2 = _outputDecorator.decorate(ctxt, out);
      if (out2 != null) {
        return out2;
      }
    }
    return out;
  }
  
















  public BufferRecycler _getBufferRecycler()
  {
    if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(_factoryFeatures)) {
      return BufferRecyclers.getBufferRecycler();
    }
    return new BufferRecycler();
  }
  



  protected IOContext _createContext(Object srcRef, boolean resourceManaged)
  {
    return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
  }
  







  protected IOContext _createNonBlockingContext(Object srcRef)
  {
    return new IOContext(_getBufferRecycler(), srcRef, false);
  }
  

















  private final void _requireJSONFactory(String msg)
  {
    if (!_isJSONFactory()) {
      throw new UnsupportedOperationException(String.format(msg, new Object[] { getFormatName() }));
    }
  }
  

  private final boolean _isJSONFactory()
  {
    return getFormatName() == "JSON";
  }
}
