package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ObjectWriter implements com.fasterxml.jackson.core.Versioned, Serializable
{
  private static final long serialVersionUID = 1L;
  protected static final PrettyPrinter NULL_PRETTY_PRINTER = new com.fasterxml.jackson.core.util.MinimalPrettyPrinter();
  






  protected final SerializationConfig _config;
  






  protected final DefaultSerializerProvider _serializerProvider;
  






  protected final com.fasterxml.jackson.databind.ser.SerializerFactory _serializerFactory;
  






  protected final JsonFactory _generatorFactory;
  






  protected final GeneratorSettings _generatorSettings;
  






  protected final Prefetch _prefetch;
  






  protected ObjectWriter(ObjectMapper mapper, SerializationConfig config, JavaType rootType, PrettyPrinter pp)
  {
    _config = config;
    _serializerProvider = _serializerProvider;
    _serializerFactory = _serializerFactory;
    _generatorFactory = _jsonFactory;
    _generatorSettings = (pp == null ? GeneratorSettings.empty : new GeneratorSettings(pp, null, null, null));
    

    if (rootType == null) {
      _prefetch = Prefetch.empty;
    } else if (rootType.hasRawClass(Object.class))
    {

      _prefetch = Prefetch.empty.forRootType(this, rootType);
    } else {
      _prefetch = Prefetch.empty.forRootType(this, rootType.withStaticTyping());
    }
  }
  



  protected ObjectWriter(ObjectMapper mapper, SerializationConfig config)
  {
    _config = config;
    _serializerProvider = _serializerProvider;
    _serializerFactory = _serializerFactory;
    _generatorFactory = _jsonFactory;
    
    _generatorSettings = GeneratorSettings.empty;
    _prefetch = Prefetch.empty;
  }
  




  protected ObjectWriter(ObjectMapper mapper, SerializationConfig config, FormatSchema s)
  {
    _config = config;
    
    _serializerProvider = _serializerProvider;
    _serializerFactory = _serializerFactory;
    _generatorFactory = _jsonFactory;
    
    _generatorSettings = (s == null ? GeneratorSettings.empty : new GeneratorSettings(null, s, null, null));
    
    _prefetch = Prefetch.empty;
  }
  




  protected ObjectWriter(ObjectWriter base, SerializationConfig config, GeneratorSettings genSettings, Prefetch prefetch)
  {
    _config = config;
    
    _serializerProvider = _serializerProvider;
    _serializerFactory = _serializerFactory;
    _generatorFactory = _generatorFactory;
    
    _generatorSettings = genSettings;
    _prefetch = prefetch;
  }
  



  protected ObjectWriter(ObjectWriter base, SerializationConfig config)
  {
    _config = config;
    
    _serializerProvider = _serializerProvider;
    _serializerFactory = _serializerFactory;
    _generatorFactory = _generatorFactory;
    
    _generatorSettings = _generatorSettings;
    _prefetch = _prefetch;
  }
  





  protected ObjectWriter(ObjectWriter base, JsonFactory f)
  {
    _config = ((SerializationConfig)_config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering()));
    
    _serializerProvider = _serializerProvider;
    _serializerFactory = _serializerFactory;
    _generatorFactory = f;
    
    _generatorSettings = _generatorSettings;
    _prefetch = _prefetch;
  }
  




  public com.fasterxml.jackson.core.Version version()
  {
    return com.fasterxml.jackson.databind.cfg.PackageVersion.VERSION;
  }
  










  protected ObjectWriter _new(ObjectWriter base, JsonFactory f)
  {
    return new ObjectWriter(base, f);
  }
  




  protected ObjectWriter _new(ObjectWriter base, SerializationConfig config)
  {
    if (config == _config) {
      return this;
    }
    return new ObjectWriter(base, config);
  }
  






  protected ObjectWriter _new(GeneratorSettings genSettings, Prefetch prefetch)
  {
    if ((_generatorSettings == genSettings) && (_prefetch == prefetch)) {
      return this;
    }
    return new ObjectWriter(this, _config, genSettings, prefetch);
  }
  








  protected SequenceWriter _newSequenceWriter(boolean wrapInArray, JsonGenerator gen, boolean managedInput)
    throws IOException
  {
    _configureGenerator(gen);
    return new SequenceWriter(_serializerProvider(), gen, managedInput, _prefetch)
    
      .init(wrapInArray);
  }
  









  public ObjectWriter with(SerializationFeature feature)
  {
    return _new(this, _config.with(feature));
  }
  



  public ObjectWriter with(SerializationFeature first, SerializationFeature... other)
  {
    return _new(this, _config.with(first, other));
  }
  



  public ObjectWriter withFeatures(SerializationFeature... features)
  {
    return _new(this, _config.withFeatures(features));
  }
  



  public ObjectWriter without(SerializationFeature feature)
  {
    return _new(this, _config.without(feature));
  }
  



  public ObjectWriter without(SerializationFeature first, SerializationFeature... other)
  {
    return _new(this, _config.without(first, other));
  }
  



  public ObjectWriter withoutFeatures(SerializationFeature... features)
  {
    return _new(this, _config.withoutFeatures(features));
  }
  








  public ObjectWriter with(JsonGenerator.Feature feature)
  {
    return _new(this, _config.with(feature));
  }
  


  public ObjectWriter withFeatures(JsonGenerator.Feature... features)
  {
    return _new(this, _config.withFeatures(features));
  }
  


  public ObjectWriter without(JsonGenerator.Feature feature)
  {
    return _new(this, _config.without(feature));
  }
  


  public ObjectWriter withoutFeatures(JsonGenerator.Feature... features)
  {
    return _new(this, _config.withoutFeatures(features));
  }
  








  public ObjectWriter with(FormatFeature feature)
  {
    return _new(this, _config.with(feature));
  }
  


  public ObjectWriter withFeatures(FormatFeature... features)
  {
    return _new(this, _config.withFeatures(features));
  }
  


  public ObjectWriter without(FormatFeature feature)
  {
    return _new(this, _config.without(feature));
  }
  


  public ObjectWriter withoutFeatures(FormatFeature... features)
  {
    return _new(this, _config.withoutFeatures(features));
  }
  















  public ObjectWriter forType(JavaType rootType)
  {
    return _new(_generatorSettings, _prefetch.forRootType(this, rootType));
  }
  






  public ObjectWriter forType(Class<?> rootType)
  {
    return forType(_config.constructType(rootType));
  }
  






  public ObjectWriter forType(TypeReference<?> rootType)
  {
    return forType(_config.getTypeFactory().constructType(rootType.getType()));
  }
  


  @Deprecated
  public ObjectWriter withType(JavaType rootType)
  {
    return forType(rootType);
  }
  


  @Deprecated
  public ObjectWriter withType(Class<?> rootType)
  {
    return forType(rootType);
  }
  


  @Deprecated
  public ObjectWriter withType(TypeReference<?> rootType)
  {
    return forType(rootType);
  }
  













  public ObjectWriter with(DateFormat df)
  {
    return _new(this, _config.with(df));
  }
  



  public ObjectWriter withDefaultPrettyPrinter()
  {
    return with(_config.getDefaultPrettyPrinter());
  }
  



  public ObjectWriter with(FilterProvider filterProvider)
  {
    if (filterProvider == _config.getFilterProvider()) {
      return this;
    }
    return _new(this, _config.withFilters(filterProvider));
  }
  



  public ObjectWriter with(PrettyPrinter pp)
  {
    return _new(_generatorSettings.with(pp), _prefetch);
  }
  










  public ObjectWriter withRootName(String rootName)
  {
    return _new(this, (SerializationConfig)_config.withRootName(rootName));
  }
  


  public ObjectWriter withRootName(PropertyName rootName)
  {
    return _new(this, _config.withRootName(rootName));
  }
  









  public ObjectWriter withoutRootName()
  {
    return _new(this, _config.withRootName(PropertyName.NO_NAME));
  }
  






  public ObjectWriter with(FormatSchema schema)
  {
    _verifySchemaType(schema);
    return _new(_generatorSettings.with(schema), _prefetch);
  }
  


  @Deprecated
  public ObjectWriter withSchema(FormatSchema schema)
  {
    return with(schema);
  }
  







  public ObjectWriter withView(Class<?> view)
  {
    return _new(this, _config.withView(view));
  }
  
  public ObjectWriter with(Locale l) {
    return _new(this, (SerializationConfig)_config.with(l));
  }
  
  public ObjectWriter with(TimeZone tz) {
    return _new(this, (SerializationConfig)_config.with(tz));
  }
  





  public ObjectWriter with(com.fasterxml.jackson.core.Base64Variant b64variant)
  {
    return _new(this, (SerializationConfig)_config.with(b64variant));
  }
  


  public ObjectWriter with(CharacterEscapes escapes)
  {
    return _new(_generatorSettings.with(escapes), _prefetch);
  }
  


  public ObjectWriter with(JsonFactory f)
  {
    return f == _generatorFactory ? this : _new(this, f);
  }
  


  public ObjectWriter with(ContextAttributes attrs)
  {
    return _new(this, _config.with(attrs));
  }
  





  public ObjectWriter withAttributes(Map<?, ?> attrs)
  {
    return _new(this, (SerializationConfig)_config.withAttributes(attrs));
  }
  


  public ObjectWriter withAttribute(Object key, Object value)
  {
    return _new(this, (SerializationConfig)_config.withAttribute(key, value));
  }
  


  public ObjectWriter withoutAttribute(Object key)
  {
    return _new(this, (SerializationConfig)_config.withoutAttribute(key));
  }
  


  public ObjectWriter withRootValueSeparator(String sep)
  {
    return _new(_generatorSettings.withRootValueSeparator(sep), _prefetch);
  }
  


  public ObjectWriter withRootValueSeparator(SerializableString sep)
  {
    return _new(_generatorSettings.withRootValueSeparator(sep), _prefetch);
  }
  

















  public SequenceWriter writeValues(File out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(false, _generatorFactory
      .createGenerator(out, JsonEncoding.UTF8), true);
  }
  













  public SequenceWriter writeValues(JsonGenerator g)
    throws IOException
  {
    _assertNotNull("g", g);
    _configureGenerator(g);
    return _newSequenceWriter(false, g, false);
  }
  











  public SequenceWriter writeValues(Writer out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(false, _generatorFactory
      .createGenerator(out), true);
  }
  











  public SequenceWriter writeValues(OutputStream out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(false, _generatorFactory
      .createGenerator(out, JsonEncoding.UTF8), true);
  }
  

  public SequenceWriter writeValues(DataOutput out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(false, _generatorFactory
      .createGenerator(out), true);
  }
  













  public SequenceWriter writeValuesAsArray(File out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(true, _generatorFactory
      .createGenerator(out, JsonEncoding.UTF8), true);
  }
  














  public SequenceWriter writeValuesAsArray(JsonGenerator gen)
    throws IOException
  {
    _assertNotNull("gen", gen);
    return _newSequenceWriter(true, gen, false);
  }
  













  public SequenceWriter writeValuesAsArray(Writer out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(true, _generatorFactory.createGenerator(out), true);
  }
  













  public SequenceWriter writeValuesAsArray(OutputStream out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(true, _generatorFactory
      .createGenerator(out, JsonEncoding.UTF8), true);
  }
  

  public SequenceWriter writeValuesAsArray(DataOutput out)
    throws IOException
  {
    _assertNotNull("out", out);
    return _newSequenceWriter(true, _generatorFactory.createGenerator(out), true);
  }
  





  public boolean isEnabled(SerializationFeature f)
  {
    return _config.isEnabled(f);
  }
  
  public boolean isEnabled(MapperFeature f) {
    return _config.isEnabled(f);
  }
  


  @Deprecated
  public boolean isEnabled(JsonParser.Feature f)
  {
    return _generatorFactory.isEnabled(f);
  }
  


  public boolean isEnabled(JsonGenerator.Feature f)
  {
    return _generatorFactory.isEnabled(f);
  }
  


  public SerializationConfig getConfig()
  {
    return _config;
  }
  


  public JsonFactory getFactory()
  {
    return _generatorFactory;
  }
  
  public TypeFactory getTypeFactory() {
    return _config.getTypeFactory();
  }
  







  public boolean hasPrefetchedSerializer()
  {
    return _prefetch.hasSerializer();
  }
  


  public ContextAttributes getAttributes()
  {
    return _config.getAttributes();
  }
  









  public void writeValue(JsonGenerator g, Object value)
    throws IOException
  {
    _assertNotNull("g", g);
    _configureGenerator(g);
    if ((_config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE)) && ((value instanceof Closeable)))
    {

      Closeable toClose = (Closeable)value;
      try {
        _prefetch.serialize(g, value, _serializerProvider());
        if (_config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
          g.flush();
        }
      } catch (Exception e) {
        ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
        return;
      }
      toClose.close();
    } else {
      _prefetch.serialize(g, value, _serializerProvider());
      if (_config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
        g.flush();
      }
    }
  }
  










  public void writeValue(File resultFile, Object value)
    throws IOException, JsonGenerationException, JsonMappingException
  {
    _assertNotNull("resultFile", resultFile);
    _configAndWriteValue(_generatorFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
  }
  











  public void writeValue(OutputStream out, Object value)
    throws IOException, JsonGenerationException, JsonMappingException
  {
    _assertNotNull("out", out);
    _configAndWriteValue(_generatorFactory.createGenerator(out, JsonEncoding.UTF8), value);
  }
  










  public void writeValue(Writer w, Object value)
    throws IOException, JsonGenerationException, JsonMappingException
  {
    _assertNotNull("w", w);
    _configAndWriteValue(_generatorFactory.createGenerator(w), value);
  }
  



  public void writeValue(DataOutput out, Object value)
    throws IOException
  {
    _assertNotNull("out", out);
    _configAndWriteValue(_generatorFactory.createGenerator(out), value);
  }
  










  public String writeValueAsString(Object value)
    throws JsonProcessingException
  {
    SegmentedStringWriter sw = new SegmentedStringWriter(_generatorFactory._getBufferRecycler());
    try {
      _configAndWriteValue(_generatorFactory.createGenerator(sw), value);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
    return sw.getAndClear();
  }
  










  public byte[] writeValueAsBytes(Object value)
    throws JsonProcessingException
  {
    ByteArrayBuilder bb = new ByteArrayBuilder(_generatorFactory._getBufferRecycler());
    try {
      _configAndWriteValue(_generatorFactory.createGenerator(bb, JsonEncoding.UTF8), value);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
    byte[] result = bb.toByteArray();
    bb.release();
    return result;
  }
  

















  public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor)
    throws JsonMappingException
  {
    _assertNotNull("type", type);
    _assertNotNull("visitor", visitor);
    _serializerProvider().acceptJsonFormatVisitor(type, visitor);
  }
  

  public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor)
    throws JsonMappingException
  {
    _assertNotNull("type", type);
    _assertNotNull("visitor", visitor);
    acceptJsonFormatVisitor(_config.constructType(type), visitor);
  }
  
  public boolean canSerialize(Class<?> type) {
    _assertNotNull("type", type);
    return _serializerProvider().hasSerializerFor(type, null);
  }
  





  public boolean canSerialize(Class<?> type, java.util.concurrent.atomic.AtomicReference<Throwable> cause)
  {
    _assertNotNull("type", type);
    return _serializerProvider().hasSerializerFor(type, cause);
  }
  









  protected DefaultSerializerProvider _serializerProvider()
  {
    return _serializerProvider.createInstance(_config, _serializerFactory);
  }
  









  protected void _verifySchemaType(FormatSchema schema)
  {
    if ((schema != null) && 
      (!_generatorFactory.canUseSchema(schema)))
    {
      throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + _generatorFactory.getFormatName());
    }
  }
  




  protected final void _configAndWriteValue(JsonGenerator gen, Object value)
    throws IOException
  {
    _configureGenerator(gen);
    if ((_config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE)) && ((value instanceof Closeable))) {
      _writeCloseable(gen, value);
      return;
    }
    try {
      _prefetch.serialize(gen, value, _serializerProvider());
    } catch (Exception e) {
      ClassUtil.closeOnFailAndThrowAsIOE(gen, e);
      return;
    }
    gen.close();
  }
  




  private final void _writeCloseable(JsonGenerator gen, Object value)
    throws IOException
  {
    Closeable toClose = (Closeable)value;
    try {
      _prefetch.serialize(gen, value, _serializerProvider());
      Closeable tmpToClose = toClose;
      toClose = null;
      tmpToClose.close();
    } catch (Exception e) {
      ClassUtil.closeOnFailAndThrowAsIOE(gen, toClose, e);
      return;
    }
    gen.close();
  }
  








  protected final void _configureGenerator(JsonGenerator gen)
  {
    _config.initialize(gen);
    _generatorSettings.initialize(gen);
  }
  
  protected final void _assertNotNull(String paramName, Object src) {
    if (src == null) {
      throw new IllegalArgumentException(String.format("argument \"%s\" is null", new Object[] { paramName }));
    }
  }
  







  public static final class GeneratorSettings
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    






    public static final GeneratorSettings empty = new GeneratorSettings(null, null, null, null);
    




    public final PrettyPrinter prettyPrinter;
    




    public final FormatSchema schema;
    




    public final CharacterEscapes characterEscapes;
    



    public final SerializableString rootValueSeparator;
    




    public GeneratorSettings(PrettyPrinter pp, FormatSchema sch, CharacterEscapes esc, SerializableString rootSep)
    {
      prettyPrinter = pp;
      schema = sch;
      characterEscapes = esc;
      rootValueSeparator = rootSep;
    }
    
    public GeneratorSettings with(PrettyPrinter pp)
    {
      if (pp == null) {
        pp = ObjectWriter.NULL_PRETTY_PRINTER;
      }
      return pp == prettyPrinter ? this : new GeneratorSettings(pp, schema, characterEscapes, rootValueSeparator);
    }
    
    public GeneratorSettings with(FormatSchema sch)
    {
      return schema == sch ? this : new GeneratorSettings(prettyPrinter, sch, characterEscapes, rootValueSeparator);
    }
    
    public GeneratorSettings with(CharacterEscapes esc)
    {
      return characterEscapes == esc ? this : new GeneratorSettings(prettyPrinter, schema, esc, rootValueSeparator);
    }
    
    public GeneratorSettings withRootValueSeparator(String sep)
    {
      if (sep == null) {
        if (rootValueSeparator == null) {
          return this;
        }
        return new GeneratorSettings(prettyPrinter, schema, characterEscapes, null);
      }
      if (sep.equals(_rootValueSeparatorAsString())) {
        return this;
      }
      return new GeneratorSettings(prettyPrinter, schema, characterEscapes, new com.fasterxml.jackson.core.io.SerializedString(sep));
    }
    
    public GeneratorSettings withRootValueSeparator(SerializableString sep)
    {
      if (sep == null) {
        if (rootValueSeparator == null) {
          return this;
        }
        return new GeneratorSettings(prettyPrinter, schema, characterEscapes, null);
      }
      if (sep.equals(rootValueSeparator)) {
        return this;
      }
      return new GeneratorSettings(prettyPrinter, schema, characterEscapes, sep);
    }
    
    private final String _rootValueSeparatorAsString() {
      return rootValueSeparator == null ? null : rootValueSeparator.getValue();
    }
    



    public void initialize(JsonGenerator gen)
    {
      PrettyPrinter pp = prettyPrinter;
      if (prettyPrinter != null) {
        if (pp == ObjectWriter.NULL_PRETTY_PRINTER) {
          gen.setPrettyPrinter(null);
        } else {
          if ((pp instanceof Instantiatable)) {
            pp = (PrettyPrinter)((Instantiatable)pp).createInstance();
          }
          gen.setPrettyPrinter(pp);
        }
      }
      if (characterEscapes != null) {
        gen.setCharacterEscapes(characterEscapes);
      }
      if (schema != null) {
        gen.setSchema(schema);
      }
      if (rootValueSeparator != null) {
        gen.setRootValueSeparator(rootValueSeparator);
      }
    }
  }
  




  public static final class Prefetch
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    



    public static final Prefetch empty = new Prefetch(null, null, null);
    




    private final JavaType rootType;
    




    private final JsonSerializer<Object> valueSerializer;
    




    private final TypeSerializer typeSerializer;
    




    private Prefetch(JavaType rootT, JsonSerializer<Object> ser, TypeSerializer typeSer)
    {
      rootType = rootT;
      valueSerializer = ser;
      typeSerializer = typeSer;
    }
    
    public Prefetch forRootType(ObjectWriter parent, JavaType newType)
    {
      if (newType == null) {
        if ((rootType == null) || (valueSerializer == null)) {
          return this;
        }
        return new Prefetch(null, null, null);
      }
      

      if (newType.equals(rootType)) {
        return this;
      }
      


      if (newType.isJavaLangObject()) {
        DefaultSerializerProvider prov = parent._serializerProvider();
        
        try
        {
          typeSer = prov.findTypeSerializer(newType);
        }
        catch (JsonMappingException e) {
          TypeSerializer typeSer;
          throw new RuntimeJsonMappingException(e); }
        TypeSerializer typeSer;
        return new Prefetch(null, null, typeSer);
      }
      
      if (parent.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH)) {
        DefaultSerializerProvider prov = parent._serializerProvider();
        


        try
        {
          JsonSerializer<Object> ser = prov.findTypedValueSerializer(newType, true, null);
          
          if ((ser instanceof TypeWrappedSerializer)) {
            return new Prefetch(newType, null, ((TypeWrappedSerializer)ser)
              .typeSerializer());
          }
          return new Prefetch(newType, ser, null);
        }
        catch (JsonMappingException localJsonMappingException1) {}
      }
      

      return new Prefetch(newType, null, typeSerializer);
    }
    
    public final JsonSerializer<Object> getValueSerializer() {
      return valueSerializer;
    }
    
    public final TypeSerializer getTypeSerializer() {
      return typeSerializer;
    }
    
    public boolean hasSerializer() {
      return (valueSerializer != null) || (typeSerializer != null);
    }
    
    public void serialize(JsonGenerator gen, Object value, DefaultSerializerProvider prov)
      throws IOException
    {
      if (typeSerializer != null) {
        prov.serializePolymorphic(gen, value, rootType, valueSerializer, typeSerializer);
      } else if (valueSerializer != null) {
        prov.serializeValue(gen, value, rootType, valueSerializer);
      } else if (rootType != null) {
        prov.serializeValue(gen, value, rootType);
      } else {
        prov.serializeValue(gen, value);
      }
    }
  }
}
