package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.util.LinkedNode;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.io.Serializable;
import java.util.Collection;


public final class DeserializationConfig
  extends MapperConfigBase<DeserializationFeature, DeserializationConfig>
  implements Serializable
{
  private static final long serialVersionUID = 2L;
  private static final int DESER_FEATURE_DEFAULTS = collectFeatureDefaults(DeserializationFeature.class);
  







  protected final LinkedNode<DeserializationProblemHandler> _problemHandlers;
  







  protected final JsonNodeFactory _nodeFactory;
  







  protected final int _deserFeatures;
  







  protected final int _parserFeatures;
  







  protected final int _parserFeaturesToChange;
  







  protected final int _formatReadFeatures;
  







  protected final int _formatReadFeaturesToChange;
  







  public DeserializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides)
  {
    super(base, str, mixins, rootNames, configOverrides);
    _deserFeatures = DESER_FEATURE_DEFAULTS;
    _nodeFactory = JsonNodeFactory.instance;
    _problemHandlers = null;
    _parserFeatures = 0;
    _parserFeaturesToChange = 0;
    _formatReadFeatures = 0;
    _formatReadFeaturesToChange = 0;
  }
  







  protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides)
  {
    super(src, mixins, rootNames, configOverrides);
    _deserFeatures = _deserFeatures;
    _problemHandlers = _problemHandlers;
    _nodeFactory = _nodeFactory;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  










  private DeserializationConfig(DeserializationConfig src, int mapperFeatures, int deserFeatures, int parserFeatures, int parserFeatureMask, int formatFeatures, int formatFeatureMask)
  {
    super(src, mapperFeatures);
    _deserFeatures = deserFeatures;
    _nodeFactory = _nodeFactory;
    _problemHandlers = _problemHandlers;
    _parserFeatures = parserFeatures;
    _parserFeaturesToChange = parserFeatureMask;
    _formatReadFeatures = formatFeatures;
    _formatReadFeaturesToChange = formatFeatureMask;
  }
  




  private DeserializationConfig(DeserializationConfig src, SubtypeResolver str)
  {
    super(src, str);
    _deserFeatures = _deserFeatures;
    _nodeFactory = _nodeFactory;
    _problemHandlers = _problemHandlers;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  
  private DeserializationConfig(DeserializationConfig src, BaseSettings base)
  {
    super(src, base);
    _deserFeatures = _deserFeatures;
    _nodeFactory = _nodeFactory;
    _problemHandlers = _problemHandlers;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  
  private DeserializationConfig(DeserializationConfig src, JsonNodeFactory f)
  {
    super(src);
    _deserFeatures = _deserFeatures;
    _problemHandlers = _problemHandlers;
    _nodeFactory = f;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  

  private DeserializationConfig(DeserializationConfig src, LinkedNode<DeserializationProblemHandler> problemHandlers)
  {
    super(src);
    _deserFeatures = _deserFeatures;
    _problemHandlers = problemHandlers;
    _nodeFactory = _nodeFactory;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  
  private DeserializationConfig(DeserializationConfig src, PropertyName rootName)
  {
    super(src, rootName);
    _deserFeatures = _deserFeatures;
    _problemHandlers = _problemHandlers;
    _nodeFactory = _nodeFactory;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  
  private DeserializationConfig(DeserializationConfig src, Class<?> view)
  {
    super(src, view);
    _deserFeatures = _deserFeatures;
    _problemHandlers = _problemHandlers;
    _nodeFactory = _nodeFactory;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  
  protected DeserializationConfig(DeserializationConfig src, ContextAttributes attrs)
  {
    super(src, attrs);
    _deserFeatures = _deserFeatures;
    _problemHandlers = _problemHandlers;
    _nodeFactory = _nodeFactory;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  
  protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins)
  {
    super(src, mixins);
    _deserFeatures = _deserFeatures;
    _problemHandlers = _problemHandlers;
    _nodeFactory = _nodeFactory;
    _parserFeatures = _parserFeatures;
    _parserFeaturesToChange = _parserFeaturesToChange;
    _formatReadFeatures = _formatReadFeatures;
    _formatReadFeaturesToChange = _formatReadFeaturesToChange;
  }
  
  protected BaseSettings getBaseSettings() {
    return _base;
  }
  





  protected final DeserializationConfig _withBase(BaseSettings newBase)
  {
    return _base == newBase ? this : new DeserializationConfig(this, newBase);
  }
  
  protected final DeserializationConfig _withMapperFeatures(int mapperFeatures)
  {
    return new DeserializationConfig(this, mapperFeatures, _deserFeatures, _parserFeatures, _parserFeaturesToChange, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  








  public DeserializationConfig with(SubtypeResolver str)
  {
    return _subtypeResolver == str ? this : new DeserializationConfig(this, str);
  }
  
  public DeserializationConfig withRootName(PropertyName rootName)
  {
    if (rootName == null) {
      if (_rootName == null) {
        return this;
      }
    } else if (rootName.equals(_rootName)) {
      return this;
    }
    return new DeserializationConfig(this, rootName);
  }
  
  public DeserializationConfig withView(Class<?> view)
  {
    return _view == view ? this : new DeserializationConfig(this, view);
  }
  
  public DeserializationConfig with(ContextAttributes attrs)
  {
    return attrs == _attributes ? this : new DeserializationConfig(this, attrs);
  }
  










  public DeserializationConfig with(DeserializationFeature feature)
  {
    int newDeserFeatures = _deserFeatures | feature.getMask();
    return newDeserFeatures == _deserFeatures ? this : new DeserializationConfig(this, _mapperFeatures, newDeserFeatures, _parserFeatures, _parserFeaturesToChange, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  








  public DeserializationConfig with(DeserializationFeature first, DeserializationFeature... features)
  {
    int newDeserFeatures = _deserFeatures | first.getMask();
    for (DeserializationFeature f : features) {
      newDeserFeatures |= f.getMask();
    }
    return newDeserFeatures == _deserFeatures ? this : new DeserializationConfig(this, _mapperFeatures, newDeserFeatures, _parserFeatures, _parserFeaturesToChange, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  







  public DeserializationConfig withFeatures(DeserializationFeature... features)
  {
    int newDeserFeatures = _deserFeatures;
    for (DeserializationFeature f : features) {
      newDeserFeatures |= f.getMask();
    }
    return newDeserFeatures == _deserFeatures ? this : new DeserializationConfig(this, _mapperFeatures, newDeserFeatures, _parserFeatures, _parserFeaturesToChange, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  







  public DeserializationConfig without(DeserializationFeature feature)
  {
    int newDeserFeatures = _deserFeatures & (feature.getMask() ^ 0xFFFFFFFF);
    return newDeserFeatures == _deserFeatures ? this : new DeserializationConfig(this, _mapperFeatures, newDeserFeatures, _parserFeatures, _parserFeaturesToChange, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  








  public DeserializationConfig without(DeserializationFeature first, DeserializationFeature... features)
  {
    int newDeserFeatures = _deserFeatures & (first.getMask() ^ 0xFFFFFFFF);
    for (DeserializationFeature f : features) {
      newDeserFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    }
    return newDeserFeatures == _deserFeatures ? this : new DeserializationConfig(this, _mapperFeatures, newDeserFeatures, _parserFeatures, _parserFeaturesToChange, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  







  public DeserializationConfig withoutFeatures(DeserializationFeature... features)
  {
    int newDeserFeatures = _deserFeatures;
    for (DeserializationFeature f : features) {
      newDeserFeatures &= (f.getMask() ^ 0xFFFFFFFF);
    }
    return newDeserFeatures == _deserFeatures ? this : new DeserializationConfig(this, _mapperFeatures, newDeserFeatures, _parserFeatures, _parserFeaturesToChange, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  















  public DeserializationConfig with(JsonParser.Feature feature)
  {
    int newSet = _parserFeatures | feature.getMask();
    int newMask = _parserFeaturesToChange | feature.getMask();
    return (_parserFeatures == newSet) && (_parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, newSet, newMask, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  









  public DeserializationConfig withFeatures(JsonParser.Feature... features)
  {
    int newSet = _parserFeatures;
    int newMask = _parserFeaturesToChange;
    for (JsonParser.Feature f : features) {
      int mask = f.getMask();
      newSet |= mask;
      newMask |= mask;
    }
    return (_parserFeatures == newSet) && (_parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, newSet, newMask, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  









  public DeserializationConfig without(JsonParser.Feature feature)
  {
    int newSet = _parserFeatures & (feature.getMask() ^ 0xFFFFFFFF);
    int newMask = _parserFeaturesToChange | feature.getMask();
    return (_parserFeatures == newSet) && (_parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, newSet, newMask, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  









  public DeserializationConfig withoutFeatures(JsonParser.Feature... features)
  {
    int newSet = _parserFeatures;
    int newMask = _parserFeaturesToChange;
    for (JsonParser.Feature f : features) {
      int mask = f.getMask();
      newSet &= (mask ^ 0xFFFFFFFF);
      newMask |= mask;
    }
    return (_parserFeatures == newSet) && (_parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, newSet, newMask, _formatReadFeatures, _formatReadFeaturesToChange);
  }
  
















  public DeserializationConfig with(FormatFeature feature)
  {
    if ((feature instanceof JsonReadFeature)) {
      return _withJsonReadFeatures(new FormatFeature[] { feature });
    }
    int newSet = _formatReadFeatures | feature.getMask();
    int newMask = _formatReadFeaturesToChange | feature.getMask();
    return (_formatReadFeatures == newSet) && (_formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, _parserFeatures, _parserFeaturesToChange, newSet, newMask);
  }
  










  public DeserializationConfig withFeatures(FormatFeature... features)
  {
    if ((features.length > 0) && ((features[0] instanceof JsonReadFeature))) {
      return _withJsonReadFeatures(features);
    }
    int newSet = _formatReadFeatures;
    int newMask = _formatReadFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet |= mask;
      newMask |= mask;
    }
    return (_formatReadFeatures == newSet) && (_formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, _parserFeatures, _parserFeaturesToChange, newSet, newMask);
  }
  










  public DeserializationConfig without(FormatFeature feature)
  {
    if ((feature instanceof JsonReadFeature)) {
      return _withoutJsonReadFeatures(new FormatFeature[] { feature });
    }
    int newSet = _formatReadFeatures & (feature.getMask() ^ 0xFFFFFFFF);
    int newMask = _formatReadFeaturesToChange | feature.getMask();
    return (_formatReadFeatures == newSet) && (_formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, _parserFeatures, _parserFeaturesToChange, newSet, newMask);
  }
  










  public DeserializationConfig withoutFeatures(FormatFeature... features)
  {
    if ((features.length > 0) && ((features[0] instanceof JsonReadFeature))) {
      return _withoutJsonReadFeatures(features);
    }
    int newSet = _formatReadFeatures;
    int newMask = _formatReadFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet &= (mask ^ 0xFFFFFFFF);
      newMask |= mask;
    }
    return (_formatReadFeatures == newSet) && (_formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, _parserFeatures, _parserFeaturesToChange, newSet, newMask);
  }
  



  private DeserializationConfig _withJsonReadFeatures(FormatFeature... features)
  {
    int parserSet = _parserFeatures;
    int parserMask = _parserFeaturesToChange;
    int newSet = _formatReadFeatures;
    int newMask = _formatReadFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet |= mask;
      newMask |= mask;
      
      if ((f instanceof JsonReadFeature)) {
        JsonParser.Feature oldF = ((JsonReadFeature)f).mappedFeature();
        if (oldF != null) {
          int pmask = oldF.getMask();
          parserSet |= pmask;
          parserMask |= pmask;
        }
      }
    }
    return (_formatReadFeatures == newSet) && (_formatReadFeaturesToChange == newMask) && (_parserFeatures == parserSet) && (_parserFeaturesToChange == parserMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, parserSet, parserMask, newSet, newMask);
  }
  




  private DeserializationConfig _withoutJsonReadFeatures(FormatFeature... features)
  {
    int parserSet = _parserFeatures;
    int parserMask = _parserFeaturesToChange;
    int newSet = _formatReadFeatures;
    int newMask = _formatReadFeaturesToChange;
    for (FormatFeature f : features) {
      int mask = f.getMask();
      newSet &= (mask ^ 0xFFFFFFFF);
      newMask |= mask;
      
      if ((f instanceof JsonReadFeature)) {
        JsonParser.Feature oldF = ((JsonReadFeature)f).mappedFeature();
        if (oldF != null) {
          int pmask = oldF.getMask();
          parserSet &= (pmask ^ 0xFFFFFFFF);
          parserMask |= pmask;
        }
      }
    }
    return (_formatReadFeatures == newSet) && (_formatReadFeaturesToChange == newMask) && (_parserFeatures == parserSet) && (_parserFeaturesToChange == parserMask) ? this : new DeserializationConfig(this, _mapperFeatures, _deserFeatures, parserSet, parserMask, newSet, newMask);
  }
  













  public DeserializationConfig with(JsonNodeFactory f)
  {
    if (_nodeFactory == f) {
      return this;
    }
    return new DeserializationConfig(this, f);
  }
  





  public DeserializationConfig withHandler(DeserializationProblemHandler h)
  {
    if (LinkedNode.contains(_problemHandlers, h)) {
      return this;
    }
    return new DeserializationConfig(this, new LinkedNode(h, _problemHandlers));
  }
  




  public DeserializationConfig withNoProblemHandlers()
  {
    if (_problemHandlers == null) {
      return this;
    }
    return new DeserializationConfig(this, (LinkedNode)null);
  }
  













  public void initialize(JsonParser p)
  {
    if (_parserFeaturesToChange != 0) {
      p.overrideStdFeatures(_parserFeatures, _parserFeaturesToChange);
    }
    if (_formatReadFeaturesToChange != 0) {
      p.overrideFormatFeatures(_formatReadFeatures, _formatReadFeaturesToChange);
    }
  }
  







  public boolean useRootWrapping()
  {
    if (_rootName != null) {
      return !_rootName.isEmpty();
    }
    return isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE);
  }
  
  public final boolean isEnabled(DeserializationFeature f) {
    return (_deserFeatures & f.getMask()) != 0;
  }
  
  public final boolean isEnabled(JsonParser.Feature f, JsonFactory factory) {
    int mask = f.getMask();
    if ((_parserFeaturesToChange & mask) != 0) {
      return (_parserFeatures & f.getMask()) != 0;
    }
    return factory.isEnabled(f);
  }
  





  public final boolean hasDeserializationFeatures(int featureMask)
  {
    return (_deserFeatures & featureMask) == featureMask;
  }
  





  public final boolean hasSomeOfFeatures(int featureMask)
  {
    return (_deserFeatures & featureMask) != 0;
  }
  



  public final int getDeserializationFeatures()
  {
    return _deserFeatures;
  }
  







  public final boolean requiresFullValue()
  {
    return DeserializationFeature.FAIL_ON_TRAILING_TOKENS.enabledIn(_deserFeatures);
  }
  









  public LinkedNode<DeserializationProblemHandler> getProblemHandlers()
  {
    return _problemHandlers;
  }
  
  public final JsonNodeFactory getNodeFactory() {
    return _nodeFactory;
  }
  












  public <T extends BeanDescription> T introspect(JavaType type)
  {
    return getClassIntrospector().forDeserialization(this, type, this);
  }
  




  public <T extends BeanDescription> T introspectForCreation(JavaType type)
  {
    return getClassIntrospector().forCreation(this, type, this);
  }
  



  public <T extends BeanDescription> T introspectForBuilder(JavaType type)
  {
    return getClassIntrospector().forDeserializationWithBuilder(this, type, this);
  }
  













  public TypeDeserializer findTypeDeserializer(JavaType baseType)
    throws JsonMappingException
  {
    BeanDescription bean = introspectClassAnnotations(baseType.getRawClass());
    AnnotatedClass ac = bean.getClassInfo();
    TypeResolverBuilder<?> b = getAnnotationIntrospector().findTypeResolver(this, ac, baseType);
    



    Collection<NamedType> subtypes = null;
    if (b == null) {
      b = getDefaultTyper(baseType);
      if (b == null) {
        return null;
      }
    } else {
      subtypes = getSubtypeResolver().collectAndResolveSubtypesByTypeId(this, ac);
    }
    return b.buildTypeDeserializer(this, baseType, subtypes);
  }
}
