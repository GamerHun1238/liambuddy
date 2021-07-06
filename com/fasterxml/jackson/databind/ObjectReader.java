package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.filter.FilteringParserDelegate;
import com.fasterxml.jackson.core.filter.JsonPointerBasedFilter;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;






































































































public class ObjectReader
  extends ObjectCodec
  implements Versioned, Serializable
{
  private static final long serialVersionUID = 2L;
  protected final DeserializationConfig _config;
  protected final DefaultDeserializationContext _context;
  protected final JsonFactory _parserFactory;
  protected final boolean _unwrapRoot;
  private final TokenFilter _filter;
  protected final JavaType _valueType;
  protected final JsonDeserializer<Object> _rootDeserializer;
  protected final Object _valueToUpdate;
  protected final FormatSchema _schema;
  protected final InjectableValues _injectableValues;
  protected final DataFormatReaders _dataFormatReaders;
  protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
  protected transient JavaType _jsonNodeType;
  
  protected ObjectReader(ObjectMapper mapper, DeserializationConfig config)
  {
    this(mapper, config, null, null, null, null);
  }
  






  protected ObjectReader(ObjectMapper mapper, DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues)
  {
    _config = config;
    _context = _deserializationContext;
    _rootDeserializers = _rootDeserializers;
    _parserFactory = _jsonFactory;
    _valueType = valueType;
    _valueToUpdate = valueToUpdate;
    _schema = schema;
    _injectableValues = injectableValues;
    _unwrapRoot = config.useRootWrapping();
    
    _rootDeserializer = _prefetchRootDeserializer(valueType);
    _dataFormatReaders = null;
    _filter = null;
  }
  






  protected ObjectReader(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders)
  {
    _config = config;
    _context = _context;
    
    _rootDeserializers = _rootDeserializers;
    _parserFactory = _parserFactory;
    
    _valueType = valueType;
    _rootDeserializer = rootDeser;
    _valueToUpdate = valueToUpdate;
    _schema = schema;
    _injectableValues = injectableValues;
    _unwrapRoot = config.useRootWrapping();
    _dataFormatReaders = dataFormatReaders;
    _filter = _filter;
  }
  



  protected ObjectReader(ObjectReader base, DeserializationConfig config)
  {
    _config = config;
    _context = _context;
    
    _rootDeserializers = _rootDeserializers;
    _parserFactory = _parserFactory;
    
    _valueType = _valueType;
    _rootDeserializer = _rootDeserializer;
    _valueToUpdate = _valueToUpdate;
    _schema = _schema;
    _injectableValues = _injectableValues;
    _unwrapRoot = config.useRootWrapping();
    _dataFormatReaders = _dataFormatReaders;
    _filter = _filter;
  }
  


  protected ObjectReader(ObjectReader base, JsonFactory f)
  {
    _config = ((DeserializationConfig)_config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering()));
    _context = _context;
    
    _rootDeserializers = _rootDeserializers;
    _parserFactory = f;
    
    _valueType = _valueType;
    _rootDeserializer = _rootDeserializer;
    _valueToUpdate = _valueToUpdate;
    _schema = _schema;
    _injectableValues = _injectableValues;
    _unwrapRoot = _unwrapRoot;
    _dataFormatReaders = _dataFormatReaders;
    _filter = _filter;
  }
  
  protected ObjectReader(ObjectReader base, TokenFilter filter) {
    _config = _config;
    _context = _context;
    _rootDeserializers = _rootDeserializers;
    _parserFactory = _parserFactory;
    _valueType = _valueType;
    _rootDeserializer = _rootDeserializer;
    _valueToUpdate = _valueToUpdate;
    _schema = _schema;
    _injectableValues = _injectableValues;
    _unwrapRoot = _unwrapRoot;
    _dataFormatReaders = _dataFormatReaders;
    _filter = filter;
  }
  




  public Version version()
  {
    return PackageVersion.VERSION;
  }
  












  protected ObjectReader _new(ObjectReader base, JsonFactory f)
  {
    return new ObjectReader(base, f);
  }
  




  protected ObjectReader _new(ObjectReader base, DeserializationConfig config)
  {
    return new ObjectReader(base, config);
  }
  







  protected ObjectReader _new(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders)
  {
    return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
  }
  








  protected <T> MappingIterator<T> _newIterator(JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean parserManaged)
  {
    return new MappingIterator(_valueType, p, ctxt, deser, parserManaged, _valueToUpdate);
  }
  







  protected JsonToken _initForReading(DeserializationContext ctxt, JsonParser p)
    throws IOException
  {
    if (_schema != null) {
      p.setSchema(_schema);
    }
    _config.initialize(p);
    




    JsonToken t = p.getCurrentToken();
    if (t == null) {
      t = p.nextToken();
      if (t == null)
      {
        ctxt.reportInputMismatch(_valueType, "No content to map due to end-of-input", new Object[0]);
      }
    }
    
    return t;
  }
  









  protected void _initForMultiRead(DeserializationContext ctxt, JsonParser p)
    throws IOException
  {
    if (_schema != null) {
      p.setSchema(_schema);
    }
    _config.initialize(p);
  }
  









  public ObjectReader with(DeserializationFeature feature)
  {
    return _with(_config.with(feature));
  }
  





  public ObjectReader with(DeserializationFeature first, DeserializationFeature... other)
  {
    return _with(_config.with(first, other));
  }
  



  public ObjectReader withFeatures(DeserializationFeature... features)
  {
    return _with(_config.withFeatures(features));
  }
  



  public ObjectReader without(DeserializationFeature feature)
  {
    return _with(_config.without(feature));
  }
  




  public ObjectReader without(DeserializationFeature first, DeserializationFeature... other)
  {
    return _with(_config.without(first, other));
  }
  



  public ObjectReader withoutFeatures(DeserializationFeature... features)
  {
    return _with(_config.withoutFeatures(features));
  }
  









  public ObjectReader with(JsonParser.Feature feature)
  {
    return _with(_config.with(feature));
  }
  



  public ObjectReader withFeatures(JsonParser.Feature... features)
  {
    return _with(_config.withFeatures(features));
  }
  



  public ObjectReader without(JsonParser.Feature feature)
  {
    return _with(_config.without(feature));
  }
  



  public ObjectReader withoutFeatures(JsonParser.Feature... features)
  {
    return _with(_config.withoutFeatures(features));
  }
  











  public ObjectReader with(FormatFeature feature)
  {
    return _with(_config.with(feature));
  }
  





  public ObjectReader withFeatures(FormatFeature... features)
  {
    return _with(_config.withFeatures(features));
  }
  





  public ObjectReader without(FormatFeature feature)
  {
    return _with(_config.without(feature));
  }
  





  public ObjectReader withoutFeatures(FormatFeature... features)
  {
    return _with(_config.withoutFeatures(features));
  }
  










  public ObjectReader at(String pointerExpr)
  {
    _assertNotNull("pointerExpr", pointerExpr);
    return new ObjectReader(this, new JsonPointerBasedFilter(pointerExpr));
  }
  




  public ObjectReader at(JsonPointer pointer)
  {
    _assertNotNull("pointer", pointer);
    return new ObjectReader(this, new JsonPointerBasedFilter(pointer));
  }
  






  public ObjectReader with(DeserializationConfig config)
  {
    return _with(config);
  }
  







  public ObjectReader with(InjectableValues injectableValues)
  {
    if (_injectableValues == injectableValues) {
      return this;
    }
    return _new(this, _config, _valueType, _rootDeserializer, _valueToUpdate, _schema, injectableValues, _dataFormatReaders);
  }
  









  public ObjectReader with(JsonNodeFactory f)
  {
    return _with(_config.with(f));
  }
  










  public ObjectReader with(JsonFactory f)
  {
    if (f == _parserFactory) {
      return this;
    }
    ObjectReader r = _new(this, f);
    
    if (f.getCodec() == null) {
      f.setCodec(r);
    }
    return r;
  }
  








  public ObjectReader withRootName(String rootName)
  {
    return _with((DeserializationConfig)_config.withRootName(rootName));
  }
  


  public ObjectReader withRootName(PropertyName rootName)
  {
    return _with(_config.withRootName(rootName));
  }
  









  public ObjectReader withoutRootName()
  {
    return _with(_config.withRootName(PropertyName.NO_NAME));
  }
  








  public ObjectReader with(FormatSchema schema)
  {
    if (_schema == schema) {
      return this;
    }
    _verifySchemaType(schema);
    return _new(this, _config, _valueType, _rootDeserializer, _valueToUpdate, schema, _injectableValues, _dataFormatReaders);
  }
  










  public ObjectReader forType(JavaType valueType)
  {
    if ((valueType != null) && (valueType.equals(_valueType))) {
      return this;
    }
    JsonDeserializer<Object> rootDeser = _prefetchRootDeserializer(valueType);
    
    DataFormatReaders det = _dataFormatReaders;
    if (det != null) {
      det = det.withType(valueType);
    }
    return _new(this, _config, valueType, rootDeser, _valueToUpdate, _schema, _injectableValues, det);
  }
  









  public ObjectReader forType(Class<?> valueType)
  {
    return forType(_config.constructType(valueType));
  }
  








  public ObjectReader forType(TypeReference<?> valueTypeRef)
  {
    return forType(_config.getTypeFactory().constructType(valueTypeRef.getType()));
  }
  


  @Deprecated
  public ObjectReader withType(JavaType valueType)
  {
    return forType(valueType);
  }
  


  @Deprecated
  public ObjectReader withType(Class<?> valueType)
  {
    return forType(_config.constructType(valueType));
  }
  


  @Deprecated
  public ObjectReader withType(Type valueType)
  {
    return forType(_config.getTypeFactory().constructType(valueType));
  }
  


  @Deprecated
  public ObjectReader withType(TypeReference<?> valueTypeRef)
  {
    return forType(_config.getTypeFactory().constructType(valueTypeRef.getType()));
  }
  








  public ObjectReader withValueToUpdate(Object value)
  {
    if (value == _valueToUpdate) return this;
    if (value == null)
    {

      return _new(this, _config, _valueType, _rootDeserializer, null, _schema, _injectableValues, _dataFormatReaders);
    }
    

    JavaType t;
    

    JavaType t;
    
    if (_valueType == null) {
      t = _config.constructType(value.getClass());
    } else {
      t = _valueType;
    }
    return _new(this, _config, t, _rootDeserializer, value, _schema, _injectableValues, _dataFormatReaders);
  }
  







  public ObjectReader withView(Class<?> activeView)
  {
    return _with(_config.withView(activeView));
  }
  
  public ObjectReader with(Locale l) {
    return _with((DeserializationConfig)_config.with(l));
  }
  
  public ObjectReader with(TimeZone tz) {
    return _with((DeserializationConfig)_config.with(tz));
  }
  
  public ObjectReader withHandler(DeserializationProblemHandler h) {
    return _with(_config.withHandler(h));
  }
  
  public ObjectReader with(Base64Variant defaultBase64) {
    return _with((DeserializationConfig)_config.with(defaultBase64));
  }
  





















  public ObjectReader withFormatDetection(ObjectReader... readers)
  {
    return withFormatDetection(new DataFormatReaders(readers));
  }
  














  public ObjectReader withFormatDetection(DataFormatReaders readers)
  {
    return _new(this, _config, _valueType, _rootDeserializer, _valueToUpdate, _schema, _injectableValues, readers);
  }
  



  public ObjectReader with(ContextAttributes attrs)
  {
    return _with(_config.with(attrs));
  }
  


  public ObjectReader withAttributes(Map<?, ?> attrs)
  {
    return _with((DeserializationConfig)_config.withAttributes(attrs));
  }
  


  public ObjectReader withAttribute(Object key, Object value)
  {
    return _with((DeserializationConfig)_config.withAttribute(key, value));
  }
  


  public ObjectReader withoutAttribute(Object key)
  {
    return _with((DeserializationConfig)_config.withoutAttribute(key));
  }
  





  protected ObjectReader _with(DeserializationConfig newConfig)
  {
    if (newConfig == _config) {
      return this;
    }
    ObjectReader r = _new(this, newConfig);
    if (_dataFormatReaders != null) {
      r = r.withFormatDetection(_dataFormatReaders.with(newConfig));
    }
    return r;
  }
  





  public boolean isEnabled(DeserializationFeature f)
  {
    return _config.isEnabled(f);
  }
  
  public boolean isEnabled(MapperFeature f) {
    return _config.isEnabled(f);
  }
  
  public boolean isEnabled(JsonParser.Feature f) {
    return _parserFactory.isEnabled(f);
  }
  


  public DeserializationConfig getConfig()
  {
    return _config;
  }
  



  public JsonFactory getFactory()
  {
    return _parserFactory;
  }
  
  public TypeFactory getTypeFactory() {
    return _config.getTypeFactory();
  }
  


  public ContextAttributes getAttributes()
  {
    return _config.getAttributes();
  }
  


  public InjectableValues getInjectableValues()
  {
    return _injectableValues;
  }
  


  public JavaType getValueType()
  {
    return _valueType;
  }
  
















  public <T> T readValue(JsonParser p)
    throws IOException
  {
    _assertNotNull("p", p);
    return _bind(p, _valueToUpdate);
  }
  











  public <T> T readValue(JsonParser p, Class<T> valueType)
    throws IOException
  {
    _assertNotNull("p", p);
    return forType(valueType).readValue(p);
  }
  











  public <T> T readValue(JsonParser p, TypeReference<T> valueTypeRef)
    throws IOException
  {
    _assertNotNull("p", p);
    return forType(valueTypeRef).readValue(p);
  }
  










  public <T> T readValue(JsonParser p, ResolvedType valueType)
    throws IOException
  {
    _assertNotNull("p", p);
    return forType((JavaType)valueType).readValue(p);
  }
  





  public <T> T readValue(JsonParser p, JavaType valueType)
    throws IOException
  {
    _assertNotNull("p", p);
    return forType(valueType).readValue(p);
  }
  


















  public <T> Iterator<T> readValues(JsonParser p, Class<T> valueType)
    throws IOException
  {
    _assertNotNull("p", p);
    return forType(valueType).readValues(p);
  }
  


















  public <T> Iterator<T> readValues(JsonParser p, TypeReference<T> valueTypeRef)
    throws IOException
  {
    _assertNotNull("p", p);
    return forType(valueTypeRef).readValues(p);
  }
  


















  public <T> Iterator<T> readValues(JsonParser p, ResolvedType valueType)
    throws IOException
  {
    _assertNotNull("p", p);
    return readValues(p, (JavaType)valueType);
  }
  

















  public <T> Iterator<T> readValues(JsonParser p, JavaType valueType)
    throws IOException
  {
    _assertNotNull("p", p);
    return forType(valueType).readValues(p);
  }
  






  public JsonNode createArrayNode()
  {
    return _config.getNodeFactory().arrayNode();
  }
  
  public JsonNode createObjectNode()
  {
    return _config.getNodeFactory().objectNode();
  }
  
  public JsonNode missingNode()
  {
    return _config.getNodeFactory().missingNode();
  }
  
  public JsonNode nullNode()
  {
    return _config.getNodeFactory().nullNode();
  }
  
  public JsonParser treeAsTokens(TreeNode n)
  {
    _assertNotNull("n", n);
    

    ObjectReader codec = withValueToUpdate(null);
    return new TreeTraversingParser((JsonNode)n, codec);
  }
  


















  public <T extends TreeNode> T readTree(JsonParser p)
    throws IOException
  {
    _assertNotNull("p", p);
    return _bindAsTreeOrNull(p);
  }
  
  public void writeTree(JsonGenerator g, TreeNode rootNode)
  {
    throw new UnsupportedOperationException();
  }
  












  public <T> T readValue(InputStream src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndClose(_dataFormatReaders.findFormat(src), false);
    }
    return _bindAndClose(_considerFilter(_parserFactory.createParser(src), false));
  }
  






  public <T> T readValue(Reader src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    return _bindAndClose(_considerFilter(_parserFactory.createParser(src), false));
  }
  






  public <T> T readValue(String src)
    throws JsonProcessingException, JsonMappingException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    try {
      return _bindAndClose(_considerFilter(_parserFactory.createParser(src), false));
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
  }
  






  public <T> T readValue(byte[] src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndClose(src, 0, src.length);
    }
    return _bindAndClose(_considerFilter(_parserFactory.createParser(src), false));
  }
  






  public <T> T readValue(byte[] src, int offset, int length)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndClose(src, offset, length);
    }
    return _bindAndClose(_considerFilter(_parserFactory.createParser(src, offset, length), false));
  }
  

  public <T> T readValue(File src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndClose(_dataFormatReaders.findFormat(_inputStream(src)), true);
    }
    
    return _bindAndClose(_considerFilter(_parserFactory.createParser(src), false));
  }
  













  public <T> T readValue(URL src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndClose(_dataFormatReaders.findFormat(_inputStream(src)), true);
    }
    return _bindAndClose(_considerFilter(_parserFactory.createParser(src), false));
  }
  







  public <T> T readValue(JsonNode src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    return _bindAndClose(_considerFilter(treeAsTokens(src), false));
  }
  



  public <T> T readValue(DataInput src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    return _bindAndClose(_considerFilter(_parserFactory.createParser(src), false));
  }
  






















  public JsonNode readTree(InputStream src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndCloseAsTree(src);
    }
    return _bindAndCloseAsTree(_considerFilter(_parserFactory.createParser(src), false));
  }
  



  public JsonNode readTree(Reader src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    return _bindAndCloseAsTree(_considerFilter(_parserFactory.createParser(src), false));
  }
  



  public JsonNode readTree(String json)
    throws JsonProcessingException, JsonMappingException
  {
    _assertNotNull("json", json);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(json);
    }
    try {
      return _bindAndCloseAsTree(_considerFilter(_parserFactory.createParser(json), false));
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
  }
  



  public JsonNode readTree(byte[] json)
    throws IOException
  {
    _assertNotNull("json", json);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(json);
    }
    return _bindAndCloseAsTree(_considerFilter(_parserFactory.createParser(json), false));
  }
  



  public JsonNode readTree(byte[] json, int offset, int len)
    throws IOException
  {
    _assertNotNull("json", json);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(json);
    }
    return _bindAndCloseAsTree(_considerFilter(_parserFactory.createParser(json, offset, len), false));
  }
  



  public JsonNode readTree(DataInput src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    return _bindAndCloseAsTree(_considerFilter(_parserFactory.createParser(src), false));
  }
  
















  public <T> MappingIterator<T> readValues(JsonParser p)
    throws IOException
  {
    _assertNotNull("p", p);
    DeserializationContext ctxt = createDeserializationContext(p);
    
    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), false);
  }
  



















  public <T> MappingIterator<T> readValues(InputStream src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndReadValues(_dataFormatReaders.findFormat(src), false);
    }
    
    return _bindAndReadValues(_considerFilter(_parserFactory.createParser(src), true));
  }
  



  public <T> MappingIterator<T> readValues(Reader src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    JsonParser p = _considerFilter(_parserFactory.createParser(src), true);
    DeserializationContext ctxt = createDeserializationContext(p);
    _initForMultiRead(ctxt, p);
    p.nextToken();
    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
  }
  





  public <T> MappingIterator<T> readValues(String json)
    throws IOException
  {
    _assertNotNull("json", json);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(json);
    }
    JsonParser p = _considerFilter(_parserFactory.createParser(json), true);
    DeserializationContext ctxt = createDeserializationContext(p);
    _initForMultiRead(ctxt, p);
    p.nextToken();
    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
  }
  


  public <T> MappingIterator<T> readValues(byte[] src, int offset, int length)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndReadValues(_dataFormatReaders.findFormat(src, offset, length), false);
    }
    return _bindAndReadValues(_considerFilter(_parserFactory.createParser(src, offset, length), true));
  }
  


  public final <T> MappingIterator<T> readValues(byte[] src)
    throws IOException
  {
    _assertNotNull("src", src);
    return readValues(src, 0, src.length);
  }
  


  public <T> MappingIterator<T> readValues(File src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndReadValues(_dataFormatReaders
        .findFormat(_inputStream(src)), false);
    }
    return _bindAndReadValues(_considerFilter(_parserFactory.createParser(src), true));
  }
  










  public <T> MappingIterator<T> readValues(URL src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      return _detectBindAndReadValues(_dataFormatReaders
        .findFormat(_inputStream(src)), true);
    }
    return _bindAndReadValues(_considerFilter(_parserFactory.createParser(src), true));
  }
  


  public <T> MappingIterator<T> readValues(DataInput src)
    throws IOException
  {
    _assertNotNull("src", src);
    if (_dataFormatReaders != null) {
      _reportUndetectableSource(src);
    }
    return _bindAndReadValues(_considerFilter(_parserFactory.createParser(src), true));
  }
  






  public <T> T treeToValue(TreeNode n, Class<T> valueType)
    throws JsonProcessingException
  {
    _assertNotNull("n", n);
    try {
      return readValue(treeAsTokens(n), valueType);
    } catch (JsonProcessingException e) {
      throw e;
    } catch (IOException e) {
      throw JsonMappingException.fromUnexpectedIOE(e);
    }
  }
  
  public void writeValue(JsonGenerator gen, Object value) throws IOException
  {
    throw new UnsupportedOperationException("Not implemented for ObjectReader");
  }
  












  protected Object _bind(JsonParser p, Object valueToUpdate)
    throws IOException
  {
    DeserializationContext ctxt = createDeserializationContext(p);
    JsonToken t = _initForReading(ctxt, p);
    Object result; Object result; if (t == JsonToken.VALUE_NULL) { Object result;
      if (valueToUpdate == null) {
        result = _findRootDeserializer(ctxt).getNullValue(ctxt);
      } else
        result = valueToUpdate;
    } else { Object result;
      if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
        result = valueToUpdate;
      } else {
        JsonDeserializer<Object> deser = _findRootDeserializer(ctxt);
        Object result; if (_unwrapRoot) {
          result = _unwrapAndDeserialize(p, ctxt, _valueType, deser);
        } else { Object result;
          if (valueToUpdate == null) {
            result = deser.deserialize(p, ctxt);
          }
          else
          {
            result = deser.deserialize(p, ctxt, valueToUpdate);
          }
        }
      }
    }
    p.clearCurrentToken();
    if (_config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
      _verifyNoTrailingTokens(p, ctxt, _valueType);
    }
    return result;
  }
  
  protected Object _bindAndClose(JsonParser p0) throws IOException
  {
    JsonParser p = p0;Throwable localThrowable3 = null;
    try
    {
      DeserializationContext ctxt = createDeserializationContext(p);
      JsonToken t = _initForReading(ctxt, p);
      Object result; JsonDeserializer<Object> deser; Object result; if (t == JsonToken.VALUE_NULL) { Object result;
        if (_valueToUpdate == null) {
          result = _findRootDeserializer(ctxt).getNullValue(ctxt);
        } else
          result = _valueToUpdate;
      } else { Object result;
        if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
          result = _valueToUpdate;
        } else {
          deser = _findRootDeserializer(ctxt);
          Object result; if (_unwrapRoot) {
            result = _unwrapAndDeserialize(p, ctxt, _valueType, deser);
          } else { Object result;
            if (_valueToUpdate == null) {
              result = deser.deserialize(p, ctxt);
            } else {
              deser.deserialize(p, ctxt, _valueToUpdate);
              result = _valueToUpdate;
            }
          }
        } }
      if (_config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
        _verifyNoTrailingTokens(p, ctxt, _valueType);
      }
      return result;
    }
    catch (Throwable localThrowable1)
    {
      localThrowable3 = localThrowable1;throw localThrowable1;













    }
    finally
    {













      if (p != null) if (localThrowable3 != null) try { p.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else p.close();
    }
  }
  
  protected final JsonNode _bindAndCloseAsTree(JsonParser p0) throws IOException { JsonParser p = p0;Throwable localThrowable3 = null;
    try { return _bindAsTree(p);
    }
    catch (Throwable localThrowable4)
    {
      localThrowable3 = localThrowable4;throw localThrowable4;
    } finally {
      if (p != null) if (localThrowable3 != null) try { p.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else p.close();
    }
  }
  
  protected final JsonNode _bindAsTree(JsonParser p) throws IOException
  {
    _config.initialize(p);
    if (_schema != null) {
      p.setSchema(_schema);
    }
    
    JsonToken t = p.getCurrentToken();
    if (t == null) {
      t = p.nextToken();
      if (t == null) {
        return _config.getNodeFactory().missingNode();
      }
    }
    

    boolean checkTrailing = _config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    DeserializationContext ctxt;
    DeserializationContext ctxt; JsonNode resultNode; if (t == JsonToken.VALUE_NULL) {
      JsonNode resultNode = _config.getNodeFactory().nullNode();
      if (!checkTrailing) {
        return resultNode;
      }
      ctxt = createDeserializationContext(p);
    } else {
      ctxt = createDeserializationContext(p);
      JsonDeserializer<Object> deser = _findTreeDeserializer(ctxt);
      JsonNode resultNode; if (_unwrapRoot) {
        resultNode = (JsonNode)_unwrapAndDeserialize(p, ctxt, _jsonNodeType(), deser);
      } else {
        resultNode = (JsonNode)deser.deserialize(p, ctxt);
      }
    }
    if (_config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
      _verifyNoTrailingTokens(p, ctxt, _jsonNodeType());
    }
    return resultNode;
  }
  



  protected final JsonNode _bindAsTreeOrNull(JsonParser p)
    throws IOException
  {
    _config.initialize(p);
    if (_schema != null) {
      p.setSchema(_schema);
    }
    JsonToken t = p.getCurrentToken();
    if (t == null) {
      t = p.nextToken();
      if (t == null) {
        return null;
      }
    }
    

    boolean checkTrailing = _config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    DeserializationContext ctxt; DeserializationContext ctxt; JsonNode resultNode; if (t == JsonToken.VALUE_NULL) {
      JsonNode resultNode = _config.getNodeFactory().nullNode();
      if (!checkTrailing) {
        return resultNode;
      }
      ctxt = createDeserializationContext(p);
    } else {
      ctxt = createDeserializationContext(p);
      JsonDeserializer<Object> deser = _findTreeDeserializer(ctxt);
      JsonNode resultNode; if (_unwrapRoot) {
        resultNode = (JsonNode)_unwrapAndDeserialize(p, ctxt, _jsonNodeType(), deser);
      } else {
        resultNode = (JsonNode)deser.deserialize(p, ctxt);
      }
    }
    if (checkTrailing) {
      _verifyNoTrailingTokens(p, ctxt, _jsonNodeType());
    }
    return resultNode;
  }
  


  protected <T> MappingIterator<T> _bindAndReadValues(JsonParser p)
    throws IOException
  {
    DeserializationContext ctxt = createDeserializationContext(p);
    _initForMultiRead(ctxt, p);
    p.nextToken();
    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
  }
  
  protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, JavaType rootType, JsonDeserializer<Object> deser)
    throws IOException
  {
    PropertyName expRootName = _config.findRootName(rootType);
    
    String expSimpleName = expRootName.getSimpleName();
    
    if (p.getCurrentToken() != JsonToken.START_OBJECT) {
      ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", new Object[] { expSimpleName, p
      
        .getCurrentToken() });
    }
    if (p.nextToken() != JsonToken.FIELD_NAME) {
      ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", new Object[] { expSimpleName, p
      
        .getCurrentToken() });
    }
    String actualName = p.getCurrentName();
    if (!expSimpleName.equals(actualName)) {
      ctxt.reportPropertyInputMismatch(rootType, actualName, "Root name '%s' does not match expected ('%s') for type %s", new Object[] { actualName, expSimpleName, rootType });
    }
    


    p.nextToken();
    Object result;
    Object result; if (_valueToUpdate == null) {
      result = deser.deserialize(p, ctxt);
    } else {
      deser.deserialize(p, ctxt, _valueToUpdate);
      result = _valueToUpdate;
    }
    
    if (p.nextToken() != JsonToken.END_OBJECT) {
      ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", new Object[] { expSimpleName, p
      
        .getCurrentToken() });
    }
    if (_config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
      _verifyNoTrailingTokens(p, ctxt, _valueType);
    }
    return result;
  }
  




  protected JsonParser _considerFilter(JsonParser p, boolean multiValue)
  {
    return (_filter == null) || (FilteringParserDelegate.class.isInstance(p)) ? p : new FilteringParserDelegate(p, _filter, false, multiValue);
  }
  





  protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType)
    throws IOException
  {
    JsonToken t = p.nextToken();
    if (t != null) {
      Class<?> bt = ClassUtil.rawClass(bindType);
      if ((bt == null) && 
        (_valueToUpdate != null)) {
        bt = _valueToUpdate.getClass();
      }
      
      ctxt.reportTrailingTokens(bt, p, t);
    }
  }
  






  protected Object _detectBindAndClose(byte[] src, int offset, int length)
    throws IOException
  {
    DataFormatReaders.Match match = _dataFormatReaders.findFormat(src, offset, length);
    if (!match.hasMatch()) {
      _reportUnkownFormat(_dataFormatReaders, match);
    }
    JsonParser p = match.createParserWithMatch();
    return match.getReader()._bindAndClose(p);
  }
  

  protected Object _detectBindAndClose(DataFormatReaders.Match match, boolean forceClosing)
    throws IOException
  {
    if (!match.hasMatch()) {
      _reportUnkownFormat(_dataFormatReaders, match);
    }
    JsonParser p = match.createParserWithMatch();
    

    if (forceClosing) {
      p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    }
    
    return match.getReader()._bindAndClose(p);
  }
  

  protected <T> MappingIterator<T> _detectBindAndReadValues(DataFormatReaders.Match match, boolean forceClosing)
    throws IOException
  {
    if (!match.hasMatch()) {
      _reportUnkownFormat(_dataFormatReaders, match);
    }
    JsonParser p = match.createParserWithMatch();
    

    if (forceClosing) {
      p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    }
    
    return match.getReader()._bindAndReadValues(p);
  }
  
  protected JsonNode _detectBindAndCloseAsTree(InputStream in)
    throws IOException
  {
    DataFormatReaders.Match match = _dataFormatReaders.findFormat(in);
    if (!match.hasMatch()) {
      _reportUnkownFormat(_dataFormatReaders, match);
    }
    JsonParser p = match.createParserWithMatch();
    p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    return match.getReader()._bindAndCloseAsTree(p);
  }
  






  protected void _reportUnkownFormat(DataFormatReaders detector, DataFormatReaders.Match match)
    throws JsonProcessingException
  {
    throw new JsonParseException(null, "Cannot detect format from input, does not look like any of detectable formats " + detector.toString());
  }
  









  protected void _verifySchemaType(FormatSchema schema)
  {
    if ((schema != null) && 
      (!_parserFactory.canUseSchema(schema)))
    {
      throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + _parserFactory.getFormatName());
    }
  }
  





  protected DefaultDeserializationContext createDeserializationContext(JsonParser p)
  {
    return _context.createInstance(_config, p, _injectableValues);
  }
  
  protected InputStream _inputStream(URL src) throws IOException {
    return src.openStream();
  }
  
  protected InputStream _inputStream(File f) throws IOException {
    return new FileInputStream(f);
  }
  

  protected void _reportUndetectableSource(Object src)
    throws JsonParseException
  {
    throw new JsonParseException(null, "Cannot use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based");
  }
  









  protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt)
    throws JsonMappingException
  {
    if (_rootDeserializer != null) {
      return _rootDeserializer;
    }
    

    JavaType t = _valueType;
    if (t == null) {
      ctxt.reportBadDefinition((JavaType)null, "No value type configured for ObjectReader");
    }
    

    JsonDeserializer<Object> deser = (JsonDeserializer)_rootDeserializers.get(t);
    if (deser != null) {
      return deser;
    }
    
    deser = ctxt.findRootValueDeserializer(t);
    if (deser == null) {
      ctxt.reportBadDefinition(t, "Cannot find a deserializer for type " + t);
    }
    _rootDeserializers.put(t, deser);
    return deser;
  }
  



  protected JsonDeserializer<Object> _findTreeDeserializer(DeserializationContext ctxt)
    throws JsonMappingException
  {
    JavaType nodeType = _jsonNodeType();
    JsonDeserializer<Object> deser = (JsonDeserializer)_rootDeserializers.get(nodeType);
    if (deser == null)
    {
      deser = ctxt.findRootValueDeserializer(nodeType);
      if (deser == null) {
        ctxt.reportBadDefinition(nodeType, "Cannot find a deserializer for type " + nodeType);
      }
      
      _rootDeserializers.put(nodeType, deser);
    }
    return deser;
  }
  





  protected JsonDeserializer<Object> _prefetchRootDeserializer(JavaType valueType)
  {
    if ((valueType == null) || (!_config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH))) {
      return null;
    }
    
    JsonDeserializer<Object> deser = (JsonDeserializer)_rootDeserializers.get(valueType);
    if (deser == null) {
      try
      {
        DeserializationContext ctxt = createDeserializationContext(null);
        deser = ctxt.findRootValueDeserializer(valueType);
        if (deser != null) {
          _rootDeserializers.put(valueType, deser);
        }
        return deser;
      }
      catch (JsonProcessingException localJsonProcessingException) {}
    }
    
    return deser;
  }
  


  protected final JavaType _jsonNodeType()
  {
    JavaType t = _jsonNodeType;
    if (t == null) {
      t = getTypeFactory().constructType(JsonNode.class);
      _jsonNodeType = t;
    }
    return t;
  }
  
  protected final void _assertNotNull(String paramName, Object src) {
    if (src == null) {
      throw new IllegalArgumentException(String.format("argument \"%s\" is null", new Object[] { paramName }));
    }
  }
}
