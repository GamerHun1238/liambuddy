package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.io.Serializable;
import java.text.DateFormat;









public final class SerializationConfig
  extends MapperConfigBase<SerializationFeature, SerializationConfig>
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  protected static final PrettyPrinter DEFAULT_PRETTY_PRINTER = new DefaultPrettyPrinter();
  

  private static final int SER_FEATURE_DEFAULTS = collectFeatureDefaults(SerializationFeature.class);
  








  protected final FilterProvider _filterProvider;
  








  protected final PrettyPrinter _defaultPrettyPrinter;
  







  protected final int _serFeatures;
  







  protected final int _generatorFeatures;
  







  protected final int _generatorFeaturesToChange;
  







  protected final int _formatWriteFeatures;
  







  protected final int _formatWriteFeaturesToChange;
  








  public SerializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides)
  {
    super(base, str, mixins, rootNames, configOverrides);
    _serFeatures = SER_FEATURE_DEFAULTS;
    _filterProvider = null;
    _defaultPrettyPrinter = DEFAULT_PRETTY_PRINTER;
    _generatorFeatures = 0;
    _generatorFeaturesToChange = 0;
    _formatWriteFeatures = 0;
    _formatWriteFeaturesToChange = 0;
  }
  







  protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides)
  {
    super(src, mixins, rootNames, configOverrides);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  







  private SerializationConfig(SerializationConfig src, SubtypeResolver str)
  {
    super(src, str);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  



  private SerializationConfig(SerializationConfig src, int mapperFeatures, int serFeatures, int generatorFeatures, int generatorFeatureMask, int formatFeatures, int formatFeaturesMask)
  {
    super(src, mapperFeatures);
    _serFeatures = serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = generatorFeatures;
    _generatorFeaturesToChange = generatorFeatureMask;
    _formatWriteFeatures = formatFeatures;
    _formatWriteFeaturesToChange = formatFeaturesMask;
  }
  
  private SerializationConfig(SerializationConfig src, BaseSettings base)
  {
    super(src, base);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  
  private SerializationConfig(SerializationConfig src, FilterProvider filters)
  {
    super(src);
    _serFeatures = _serFeatures;
    _filterProvider = filters;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  
  private SerializationConfig(SerializationConfig src, Class<?> view)
  {
    super(src, view);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  
  private SerializationConfig(SerializationConfig src, PropertyName rootName)
  {
    super(src, rootName);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  



  protected SerializationConfig(SerializationConfig src, ContextAttributes attrs)
  {
    super(src, attrs);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  



  protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins)
  {
    super(src, mixins);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = _defaultPrettyPrinter;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  



  protected SerializationConfig(SerializationConfig src, PrettyPrinter defaultPP)
  {
    super(src);
    _serFeatures = _serFeatures;
    _filterProvider = _filterProvider;
    _defaultPrettyPrinter = defaultPP;
    _generatorFeatures = _generatorFeatures;
    _generatorFeaturesToChange = _generatorFeaturesToChange;
    _formatWriteFeatures = _formatWriteFeatures;
    _formatWriteFeaturesToChange = _formatWriteFeaturesToChange;
  }
  






  protected final SerializationConfig _withBase(BaseSettings newBase)
  {
    return _base == newBase ? this : new SerializationConfig(this, newBase);
  }
  
  protected final SerializationConfig _withMapperFeatures(int mapperFeatures)
  {
    return new SerializationConfig(this, mapperFeatures, _serFeatures, _generatorFeatures, _generatorFeaturesToChange, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  


  public SerializationConfig withRootName(PropertyName rootName)
  {
    if (rootName == null) {
      if (_rootName == null) {
        return this;
      }
    } else if (rootName.equals(_rootName)) {
      return this;
    }
    return new SerializationConfig(this, rootName);
  }
  
  public SerializationConfig with(SubtypeResolver str)
  {
    return str == _subtypeResolver ? this : new SerializationConfig(this, str);
  }
  
  public SerializationConfig withView(Class<?> view)
  {
    return _view == view ? this : new SerializationConfig(this, view);
  }
  
  public SerializationConfig with(ContextAttributes attrs)
  {
    return attrs == _attributes ? this : new SerializationConfig(this, attrs);
  }
  











  public SerializationConfig with(DateFormat df)
  {
    SerializationConfig cfg = (SerializationConfig)super.with(df);
    
    if (df == null) {
      return cfg.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    return cfg.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
  










  public SerializationConfig with(SerializationFeature feature)
  {
    int newSerFeatures = _serFeatures | feature.getMask();
    return newSerFeatures == _serFeatures ? this : new SerializationConfig(this, _mapperFeatures, newSerFeatures, _generatorFeatures, _generatorFeaturesToChange, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  







  public SerializationConfig with(SerializationFeature first, SerializationFeature... features)
  {
    int newSerFeatures = _serFeatures | first.getMask();
    for (SerializationFeature f : features) {
      newSerFeatures |= f.getMask();
    }
    return newSerFeatures == _serFeatures ? this : new SerializationConfig(this, _mapperFeatures, newSerFeatures, _generatorFeatures, _generatorFeaturesToChange, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  







  public SerializationConfig withFeatures(SerializationFeature... features)
  {
    int newSerFeatures = _serFeatures;
    for (SerializationFeature f : features) {
      newSerFeatures |= f.getMask();
    }
    return newSerFeatures == _serFeatures ? this : new SerializationConfig(this, _mapperFeatures, newSerFeatures, _generatorFeatures, _generatorFeaturesToChange, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  







  public SerializationConfig without(SerializationFeature feature)
  {
    int newSerFeatures = _serFeatures & (feature.getMask() ^ 0xFFFFFFFF);
    return newSerFeatures == _serFeatures ? this : new SerializationConfig(this, _mapperFeatures, newSerFeatures, _generatorFeatures, _generatorFeaturesToChange, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  







  public SerializationConfig without(SerializationFeature first, SerializationFeature... features)
  {
    int newSerFeatures = _serFeatures & (first.getMask() ^ 0xFFFFFFFF);
    for (SerializationFeature f : features) {
      newSerFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    }
    return newSerFeatures == _serFeatures ? this : new SerializationConfig(this, _mapperFeatures, newSerFeatures, _generatorFeatures, _generatorFeaturesToChange, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  







  public SerializationConfig withoutFeatures(SerializationFeature... features)
  {
    int newSerFeatures = _serFeatures;
    for (SerializationFeature f : features) {
      newSerFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    }
    return newSerFeatures == _serFeatures ? this : new SerializationConfig(this, _mapperFeatures, newSerFeatures, _generatorFeatures, _generatorFeaturesToChange, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  














  public SerializationConfig with(JsonGenerator.Feature feature)
  {
    int newSet = _generatorFeatures | feature.getMask();
    int newMask = _generatorFeaturesToChange | feature.getMask();
    return (_generatorFeatures == newSet) && (_generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, newSet, newMask, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  









  public SerializationConfig withFeatures(JsonGenerator.Feature... features)
  {
    int newSet = _generatorFeatures;
    int newMask = _generatorFeaturesToChange;
    for (JsonGenerator.Feature f : features) {
      int mask = f.getMask();
      newSet |= mask;
      newMask |= mask;
    }
    return (_generatorFeatures == newSet) && (_generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, newSet, newMask, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  









  public SerializationConfig without(JsonGenerator.Feature feature)
  {
    int newSet = _generatorFeatures & (feature.getMask() ^ 0xFFFFFFFF);
    int newMask = _generatorFeaturesToChange | feature.getMask();
    return (_generatorFeatures == newSet) && (_generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, newSet, newMask, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  









  public SerializationConfig withoutFeatures(JsonGenerator.Feature... features)
  {
    int newSet = _generatorFeatures;
    int newMask = _generatorFeaturesToChange;
    for (JsonGenerator.Feature f : features) {
      int mask = f.getMask();
      newSet &= (mask ^ 0xFFFFFFFF);
      newMask |= mask;
    }
    return (_generatorFeatures == newSet) && (_generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, newSet, newMask, _formatWriteFeatures, _formatWriteFeaturesToChange);
  }
  















  public SerializationConfig with(FormatFeature feature)
  {
    if ((feature instanceof JsonWriteFeature)) {
      return _withJsonWriteFeatures(new FormatFeature[] { feature });
    }
    int newSet = _formatWriteFeatures | feature.getMask();
    int newMask = _formatWriteFeaturesToChange | feature.getMask();
    return (_formatWriteFeatures == newSet) && (_formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, _generatorFeatures, _generatorFeaturesToChange, newSet, newMask);
  }
  










  public SerializationConfig withFeatures(FormatFeature... features)
  {
    if ((features.length > 0) && ((features[0] instanceof JsonWriteFeature))) {
      return _withJsonWriteFeatures(features);
    }
    int newSet = _formatWriteFeatures;
    int newMask = _formatWriteFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet |= mask;
      newMask |= mask;
    }
    return (_formatWriteFeatures == newSet) && (_formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, _generatorFeatures, _generatorFeaturesToChange, newSet, newMask);
  }
  










  public SerializationConfig without(FormatFeature feature)
  {
    if ((feature instanceof JsonWriteFeature)) {
      return _withoutJsonWriteFeatures(new FormatFeature[] { feature });
    }
    int newSet = _formatWriteFeatures & (feature.getMask() ^ 0xFFFFFFFF);
    int newMask = _formatWriteFeaturesToChange | feature.getMask();
    return (_formatWriteFeatures == newSet) && (_formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, _generatorFeatures, _generatorFeaturesToChange, newSet, newMask);
  }
  









  public SerializationConfig withoutFeatures(FormatFeature... features)
  {
    if ((features.length > 0) && ((features[0] instanceof JsonWriteFeature))) {
      return _withoutJsonWriteFeatures(features);
    }
    int newSet = _formatWriteFeatures;
    int newMask = _formatWriteFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet &= (mask ^ 0xFFFFFFFF);
      newMask |= mask;
    }
    return (_formatWriteFeatures == newSet) && (_formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, _generatorFeatures, _generatorFeaturesToChange, newSet, newMask);
  }
  



  private SerializationConfig _withJsonWriteFeatures(FormatFeature... features)
  {
    int parserSet = _generatorFeatures;
    int parserMask = _generatorFeaturesToChange;
    int newSet = _formatWriteFeatures;
    int newMask = _formatWriteFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet |= mask;
      newMask |= mask;
      
      if ((f instanceof JsonWriteFeature)) {
        JsonGenerator.Feature oldF = ((JsonWriteFeature)f).mappedFeature();
        if (oldF != null) {
          int pmask = oldF.getMask();
          parserSet |= pmask;
          parserMask |= pmask;
        }
      }
    }
    return (_formatWriteFeatures == newSet) && (_formatWriteFeaturesToChange == newMask) && (_generatorFeatures == parserSet) && (_generatorFeaturesToChange == parserMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, parserSet, parserMask, newSet, newMask);
  }
  




  private SerializationConfig _withoutJsonWriteFeatures(FormatFeature... features)
  {
    int parserSet = _generatorFeatures;
    int parserMask = _generatorFeaturesToChange;
    int newSet = _formatWriteFeatures;
    int newMask = _formatWriteFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet &= (mask ^ 0xFFFFFFFF);
      newMask |= mask;
      
      if ((f instanceof JsonWriteFeature)) {
        JsonGenerator.Feature oldF = ((JsonWriteFeature)f).mappedFeature();
        if (oldF != null) {
          int pmask = oldF.getMask();
          parserSet &= (pmask ^ 0xFFFFFFFF);
          parserMask |= pmask;
        }
      }
    }
    return (_formatWriteFeatures == newSet) && (_formatWriteFeaturesToChange == newMask) && (_generatorFeatures == parserSet) && (_generatorFeaturesToChange == parserMask) ? this : new SerializationConfig(this, _mapperFeatures, _serFeatures, parserSet, parserMask, newSet, newMask);
  }
  









  public SerializationConfig withFilters(FilterProvider filterProvider)
  {
    return filterProvider == _filterProvider ? this : new SerializationConfig(this, filterProvider);
  }
  







  @Deprecated
  public SerializationConfig withPropertyInclusion(JsonInclude.Value incl)
  {
    _configOverrides.setDefaultInclusion(incl);
    return this;
  }
  


  public SerializationConfig withDefaultPrettyPrinter(PrettyPrinter pp)
  {
    return _defaultPrettyPrinter == pp ? this : new SerializationConfig(this, pp);
  }
  





  public PrettyPrinter constructDefaultPrettyPrinter()
  {
    PrettyPrinter pp = _defaultPrettyPrinter;
    if ((pp instanceof Instantiatable)) {
      pp = (PrettyPrinter)((Instantiatable)pp).createInstance();
    }
    return pp;
  }
  













  public void initialize(JsonGenerator g)
  {
    if (SerializationFeature.INDENT_OUTPUT.enabledIn(_serFeatures))
    {
      if (g.getPrettyPrinter() == null) {
        PrettyPrinter pp = constructDefaultPrettyPrinter();
        if (pp != null) {
          g.setPrettyPrinter(pp);
        }
      }
    }
    
    boolean useBigDec = SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(_serFeatures);
    
    int mask = _generatorFeaturesToChange;
    if ((mask != 0) || (useBigDec)) {
      int newFlags = _generatorFeatures;
      
      if (useBigDec) {
        int f = JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.getMask();
        newFlags |= f;
        mask |= f;
      }
      g.overrideStdFeatures(newFlags, mask);
    }
    if (_formatWriteFeaturesToChange != 0) {
      g.overrideFormatFeatures(_formatWriteFeatures, _formatWriteFeaturesToChange);
    }
  }
  









  @Deprecated
  public JsonInclude.Include getSerializationInclusion()
  {
    JsonInclude.Include incl = getDefaultPropertyInclusion().getValueInclusion();
    return incl == JsonInclude.Include.USE_DEFAULTS ? JsonInclude.Include.ALWAYS : incl;
  }
  







  public boolean useRootWrapping()
  {
    if (_rootName != null) {
      return !_rootName.isEmpty();
    }
    return isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
  }
  
  public final boolean isEnabled(SerializationFeature f) {
    return (_serFeatures & f.getMask()) != 0;
  }
  






  public final boolean isEnabled(JsonGenerator.Feature f, JsonFactory factory)
  {
    int mask = f.getMask();
    if ((_generatorFeaturesToChange & mask) != 0) {
      return (_generatorFeatures & f.getMask()) != 0;
    }
    return factory.isEnabled(f);
  }
  





  public final boolean hasSerializationFeatures(int featureMask)
  {
    return (_serFeatures & featureMask) == featureMask;
  }
  
  public final int getSerializationFeatures() {
    return _serFeatures;
  }
  





  public FilterProvider getFilterProvider()
  {
    return _filterProvider;
  }
  









  public PrettyPrinter getDefaultPrettyPrinter()
  {
    return _defaultPrettyPrinter;
  }
  










  public <T extends BeanDescription> T introspect(JavaType type)
  {
    return getClassIntrospector().forSerialization(this, type, this);
  }
}
