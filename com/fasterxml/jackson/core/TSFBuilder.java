package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.InputDecorator;
import com.fasterxml.jackson.core.io.OutputDecorator;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;

















public abstract class TSFBuilder<F extends JsonFactory, B extends TSFBuilder<F, B>>
{
  protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = ;
  




  protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
  




  protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
  






  protected int _factoryFeatures;
  






  protected int _streamReadFeatures;
  






  protected int _streamWriteFeatures;
  





  protected InputDecorator _inputDecorator;
  





  protected OutputDecorator _outputDecorator;
  






  protected TSFBuilder()
  {
    _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
    _streamReadFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
    _streamWriteFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
    _inputDecorator = null;
    _outputDecorator = null;
  }
  
  protected TSFBuilder(JsonFactory base)
  {
    this(_factoryFeatures, _parserFeatures, _generatorFeatures);
  }
  


  protected TSFBuilder(int factoryFeatures, int parserFeatures, int generatorFeatures)
  {
    _factoryFeatures = factoryFeatures;
    _streamReadFeatures = parserFeatures;
    _streamWriteFeatures = generatorFeatures;
  }
  


  public int factoryFeaturesMask() { return _factoryFeatures; }
  public int streamReadFeatures() { return _streamReadFeatures; }
  public int streamWriteFeatures() { return _streamWriteFeatures; }
  
  public InputDecorator inputDecorator() { return _inputDecorator; }
  public OutputDecorator outputDecorator() { return _outputDecorator; }
  

  public B enable(JsonFactory.Feature f)
  {
    _factoryFeatures |= f.getMask();
    return _this();
  }
  
  public B disable(JsonFactory.Feature f) {
    _factoryFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    return _this();
  }
  
  public B configure(JsonFactory.Feature f, boolean state) {
    return state ? enable(f) : disable(f);
  }
  

  public B enable(StreamReadFeature f)
  {
    _streamReadFeatures |= f.mappedFeature().getMask();
    return _this();
  }
  
  public B enable(StreamReadFeature first, StreamReadFeature... other) {
    _streamReadFeatures |= first.mappedFeature().getMask();
    for (StreamReadFeature f : other) {
      _streamReadFeatures |= f.mappedFeature().getMask();
    }
    return _this();
  }
  
  public B disable(StreamReadFeature f) {
    _streamReadFeatures &= (f.mappedFeature().getMask() ^ 0xFFFFFFFF);
    return _this();
  }
  
  public B disable(StreamReadFeature first, StreamReadFeature... other) {
    _streamReadFeatures &= (first.mappedFeature().getMask() ^ 0xFFFFFFFF);
    for (StreamReadFeature f : other) {
      _streamReadFeatures &= (f.mappedFeature().getMask() ^ 0xFFFFFFFF);
    }
    return _this();
  }
  
  public B configure(StreamReadFeature f, boolean state) {
    return state ? enable(f) : disable(f);
  }
  

  public B enable(StreamWriteFeature f)
  {
    _streamWriteFeatures |= f.mappedFeature().getMask();
    return _this();
  }
  
  public B enable(StreamWriteFeature first, StreamWriteFeature... other) {
    _streamWriteFeatures |= first.mappedFeature().getMask();
    for (StreamWriteFeature f : other) {
      _streamWriteFeatures |= f.mappedFeature().getMask();
    }
    return _this();
  }
  
  public B disable(StreamWriteFeature f) {
    _streamWriteFeatures &= (f.mappedFeature().getMask() ^ 0xFFFFFFFF);
    return _this();
  }
  
  public B disable(StreamWriteFeature first, StreamWriteFeature... other) {
    _streamWriteFeatures &= (first.mappedFeature().getMask() ^ 0xFFFFFFFF);
    for (StreamWriteFeature f : other) {
      _streamWriteFeatures &= (f.mappedFeature().getMask() ^ 0xFFFFFFFF);
    }
    return _this();
  }
  
  public B configure(StreamWriteFeature f, boolean state) {
    return state ? enable(f) : disable(f);
  }
  








  public B enable(JsonReadFeature f)
  {
    return _failNonJSON(f);
  }
  
  public B enable(JsonReadFeature first, JsonReadFeature... other) {
    return _failNonJSON(first);
  }
  
  public B disable(JsonReadFeature f) {
    return _failNonJSON(f);
  }
  
  public B disable(JsonReadFeature first, JsonReadFeature... other) {
    return _failNonJSON(first);
  }
  
  public B configure(JsonReadFeature f, boolean state) {
    return _failNonJSON(f);
  }
  
  private B _failNonJSON(Object feature)
  {
    throw new IllegalArgumentException("Feature " + feature.getClass().getName() + "#" + feature.toString() + " not supported for non-JSON backend");
  }
  

  public B enable(JsonWriteFeature f)
  {
    return _failNonJSON(f);
  }
  
  public B enable(JsonWriteFeature first, JsonWriteFeature... other) {
    return _failNonJSON(first);
  }
  
  public B disable(JsonWriteFeature f) {
    return _failNonJSON(f);
  }
  
  public B disable(JsonWriteFeature first, JsonWriteFeature... other) {
    return _failNonJSON(first);
  }
  
  public B configure(JsonWriteFeature f, boolean state) {
    return _failNonJSON(f);
  }
  

  public B inputDecorator(InputDecorator dec)
  {
    _inputDecorator = dec;
    return _this();
  }
  
  public B outputDecorator(OutputDecorator dec) {
    _outputDecorator = dec;
    return _this();
  }
  



  public abstract F build();
  



  protected final B _this()
  {
    return this;
  }
  
  protected void _legacyEnable(JsonParser.Feature f)
  {
    _streamReadFeatures |= f.getMask();
  }
  
  protected void _legacyDisable(JsonParser.Feature f) { _streamReadFeatures &= (f.getMask() ^ 0xFFFFFFFF); }
  
  protected void _legacyEnable(JsonGenerator.Feature f)
  {
    _streamWriteFeatures |= f.getMask();
  }
  
  protected void _legacyDisable(JsonGenerator.Feature f) { _streamWriteFeatures &= (f.getMask() ^ 0xFFFFFFFF); }
}
