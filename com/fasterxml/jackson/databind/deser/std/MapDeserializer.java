package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;












































@JacksonStdImpl
public class MapDeserializer
  extends ContainerDeserializerBase<Map<Object, Object>>
  implements ContextualDeserializer, ResolvableDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final KeyDeserializer _keyDeserializer;
  protected boolean _standardStringKey;
  protected final JsonDeserializer<Object> _valueDeserializer;
  protected final TypeDeserializer _valueTypeDeserializer;
  protected final ValueInstantiator _valueInstantiator;
  protected JsonDeserializer<Object> _delegateDeserializer;
  protected PropertyBasedCreator _propertyBasedCreator;
  protected final boolean _hasDefaultCreator;
  protected Set<String> _ignorableProperties;
  
  public MapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser)
  {
    super(mapType, null, null);
    _keyDeserializer = keyDeser;
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = valueTypeDeser;
    _valueInstantiator = valueInstantiator;
    _hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
    _delegateDeserializer = null;
    _propertyBasedCreator = null;
    _standardStringKey = _isStdKeyDeser(mapType, keyDeser);
  }
  




  protected MapDeserializer(MapDeserializer src)
  {
    super(src);
    _keyDeserializer = _keyDeserializer;
    _valueDeserializer = _valueDeserializer;
    _valueTypeDeserializer = _valueTypeDeserializer;
    _valueInstantiator = _valueInstantiator;
    _propertyBasedCreator = _propertyBasedCreator;
    _delegateDeserializer = _delegateDeserializer;
    _hasDefaultCreator = _hasDefaultCreator;
    
    _ignorableProperties = _ignorableProperties;
    
    _standardStringKey = _standardStringKey;
  }
  




  protected MapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller, Set<String> ignorable)
  {
    super(src, nuller, _unwrapSingle);
    _keyDeserializer = keyDeser;
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = valueTypeDeser;
    _valueInstantiator = _valueInstantiator;
    _propertyBasedCreator = _propertyBasedCreator;
    _delegateDeserializer = _delegateDeserializer;
    _hasDefaultCreator = _hasDefaultCreator;
    _ignorableProperties = ignorable;
    
    _standardStringKey = _isStdKeyDeser(_containerType, keyDeser);
  }
  









  protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable)
  {
    if ((_keyDeserializer == keyDeser) && (_valueDeserializer == valueDeser) && (_valueTypeDeserializer == valueTypeDeser) && (_nullProvider == nuller) && (_ignorableProperties == ignorable))
    {

      return this;
    }
    return new MapDeserializer(this, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable);
  }
  






  protected final boolean _isStdKeyDeser(JavaType mapType, KeyDeserializer keyDeser)
  {
    if (keyDeser == null) {
      return true;
    }
    JavaType keyType = mapType.getKeyType();
    if (keyType == null) {
      return true;
    }
    Class<?> rawKeyType = keyType.getRawClass();
    return ((rawKeyType == String.class) || (rawKeyType == Object.class)) && 
      (isDefaultKeyDeserializer(keyDeser));
  }
  
  public void setIgnorableProperties(String[] ignorable)
  {
    _ignorableProperties = ((ignorable == null) || (ignorable.length == 0) ? null : ArrayBuilders.arrayToSet(ignorable));
  }
  
  public void setIgnorableProperties(Set<String> ignorable) {
    _ignorableProperties = ((ignorable == null) || (ignorable.size() == 0) ? null : ignorable);
  }
  








  public void resolve(DeserializationContext ctxt)
    throws JsonMappingException
  {
    if (_valueInstantiator.canCreateUsingDelegate()) {
      JavaType delegateType = _valueInstantiator.getDelegateType(ctxt.getConfig());
      if (delegateType == null) {
        ctxt.reportBadDefinition(_containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] { _containerType, _valueInstantiator
        

          .getClass().getName() }));
      }
      


      _delegateDeserializer = findDeserializer(ctxt, delegateType, null);
    } else if (_valueInstantiator.canCreateUsingArrayDelegate()) {
      JavaType delegateType = _valueInstantiator.getArrayDelegateType(ctxt.getConfig());
      if (delegateType == null) {
        ctxt.reportBadDefinition(_containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] { _containerType, _valueInstantiator
        

          .getClass().getName() }));
      }
      _delegateDeserializer = findDeserializer(ctxt, delegateType, null);
    }
    if (_valueInstantiator.canCreateFromObjectWith()) {
      SettableBeanProperty[] creatorProps = _valueInstantiator.getFromObjectArguments(ctxt.getConfig());
      _propertyBasedCreator = PropertyBasedCreator.construct(ctxt, _valueInstantiator, creatorProps, ctxt
        .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
    }
    _standardStringKey = _isStdKeyDeser(_containerType, _keyDeserializer);
  }
  





  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    KeyDeserializer keyDeser = _keyDeserializer;
    if (keyDeser == null) {
      keyDeser = ctxt.findKeyDeserializer(_containerType.getKeyType(), property);
    }
    else if ((keyDeser instanceof ContextualKeyDeserializer)) {
      keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, property);
    }
    

    JsonDeserializer<?> valueDeser = _valueDeserializer;
    
    if (property != null) {
      valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
    }
    JavaType vt = _containerType.getContentType();
    if (valueDeser == null) {
      valueDeser = ctxt.findContextualValueDeserializer(vt, property);
    } else {
      valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
    }
    TypeDeserializer vtd = _valueTypeDeserializer;
    if (vtd != null) {
      vtd = vtd.forProperty(property);
    }
    Set<String> ignored = _ignorableProperties;
    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
    if (_neitherNull(intr, property)) {
      AnnotatedMember member = property.getMember();
      if (member != null) {
        JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(member);
        if (ignorals != null) {
          Set<String> ignoresToAdd = ignorals.findIgnoredForDeserialization();
          if (!ignoresToAdd.isEmpty()) {
            ignored = ignored == null ? new HashSet() : new HashSet(ignored);
            for (String str : ignoresToAdd) {
              ignored.add(str);
            }
          }
        }
      }
    }
    return withResolved(keyDeser, vtd, valueDeser, 
      findContentNullProvider(ctxt, property, valueDeser), ignored);
  }
  






  public JsonDeserializer<Object> getContentDeserializer()
  {
    return _valueDeserializer;
  }
  
  public ValueInstantiator getValueInstantiator()
  {
    return _valueInstantiator;
  }
  





















  public boolean isCachable()
  {
    return (_valueDeserializer == null) && (_keyDeserializer == null) && (_valueTypeDeserializer == null) && (_ignorableProperties == null);
  }
  




  public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_propertyBasedCreator != null) {
      return _deserializeUsingCreator(p, ctxt);
    }
    if (_delegateDeserializer != null) {
      return (Map)_valueInstantiator.createUsingDelegate(ctxt, _delegateDeserializer
        .deserialize(p, ctxt));
    }
    if (!_hasDefaultCreator) {
      return (Map)ctxt.handleMissingInstantiator(getMapClass(), 
        getValueInstantiator(), p, "no default constructor found", new Object[0]);
    }
    

    JsonToken t = p.getCurrentToken();
    if ((t != JsonToken.START_OBJECT) && (t != JsonToken.FIELD_NAME) && (t != JsonToken.END_OBJECT))
    {
      if (t == JsonToken.VALUE_STRING) {
        return (Map)_valueInstantiator.createFromString(ctxt, p.getText());
      }
      
      return (Map)_deserializeFromEmpty(p, ctxt);
    }
    Map<Object, Object> result = (Map)_valueInstantiator.createUsingDefault(ctxt);
    if (_standardStringKey) {
      _readAndBindStringKeyMap(p, ctxt, result);
      return result;
    }
    _readAndBind(p, ctxt, result);
    return result;
  }
  




  public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
    throws IOException
  {
    p.setCurrentValue(result);
    

    JsonToken t = p.getCurrentToken();
    if ((t != JsonToken.START_OBJECT) && (t != JsonToken.FIELD_NAME)) {
      return (Map)ctxt.handleUnexpectedToken(getMapClass(), p);
    }
    
    if (_standardStringKey) {
      _readAndUpdateStringKeyMap(p, ctxt, result);
      return result;
    }
    _readAndUpdate(p, ctxt, result);
    return result;
  }
  



  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromObject(p, ctxt);
  }
  







  public final Class<?> getMapClass() { return _containerType.getRawClass(); }
  
  public JavaType getValueType() { return _containerType; }
  






  protected final void _readAndBind(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
    throws IOException
  {
    KeyDeserializer keyDes = _keyDeserializer;
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    
    MapReferringAccumulator referringAccumulator = null;
    boolean useObjectId = valueDes.getObjectIdReader() != null;
    if (useObjectId) {
      referringAccumulator = new MapReferringAccumulator(_containerType.getContentType().getRawClass(), result);
    }
    
    String keyStr;
    
    if (p.isExpectedStartObjectToken()) {
      keyStr = p.nextFieldName();
    } else {
      JsonToken t = p.getCurrentToken();
      if (t != JsonToken.FIELD_NAME) {
        if (t == JsonToken.END_OBJECT) {
          return;
        }
        ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
      } }
    for (String keyStr = p.getCurrentName(); 
        

        keyStr != null; keyStr = p.nextFieldName()) {
      Object key = keyDes.deserializeKey(keyStr, ctxt);
      
      JsonToken t = p.nextToken();
      if ((_ignorableProperties != null) && (_ignorableProperties.contains(keyStr))) {
        p.skipChildren();
      } else {
        try
        {
          Object value;
          Object value;
          if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
              continue;
            }
            value = _nullProvider.getNullValue(ctxt); } else { Object value;
            if (typeDeser == null) {
              value = valueDes.deserialize(p, ctxt);
            } else
              value = valueDes.deserializeWithType(p, ctxt, typeDeser);
          }
          if (useObjectId) {
            referringAccumulator.put(key, value);
          } else {
            result.put(key, value);
          }
        } catch (UnresolvedForwardReference reference) {
          handleUnresolvedReference(ctxt, referringAccumulator, key, reference);
        } catch (Exception e) {
          wrapAndThrow(e, result, keyStr);
        }
      }
    }
  }
  




  protected final void _readAndBindStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
    throws IOException
  {
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    MapReferringAccumulator referringAccumulator = null;
    boolean useObjectId = valueDes.getObjectIdReader() != null;
    if (useObjectId) {
      referringAccumulator = new MapReferringAccumulator(_containerType.getContentType().getRawClass(), result);
    }
    
    String key;
    if (p.isExpectedStartObjectToken()) {
      key = p.nextFieldName();
    } else {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.END_OBJECT) {
        return;
      }
      if (t != JsonToken.FIELD_NAME)
        ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
    }
    for (String key = p.getCurrentName(); 
        

        key != null; key = p.nextFieldName()) {
      JsonToken t = p.nextToken();
      if ((_ignorableProperties != null) && (_ignorableProperties.contains(key))) {
        p.skipChildren();
      } else {
        try
        {
          Object value;
          Object value;
          if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
              continue;
            }
            value = _nullProvider.getNullValue(ctxt); } else { Object value;
            if (typeDeser == null) {
              value = valueDes.deserialize(p, ctxt);
            } else
              value = valueDes.deserializeWithType(p, ctxt, typeDeser);
          }
          if (useObjectId) {
            referringAccumulator.put(key, value);
          } else {
            result.put(key, value);
          }
        } catch (UnresolvedForwardReference reference) {
          handleUnresolvedReference(ctxt, referringAccumulator, key, reference);
        } catch (Exception e) {
          wrapAndThrow(e, result, key);
        }
      }
    }
  }
  
  public Map<Object, Object> _deserializeUsingCreator(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    PropertyBasedCreator creator = _propertyBasedCreator;
    
    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
    
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    
    String key;
    if (p.isExpectedStartObjectToken()) {
      key = p.nextFieldName(); } else { String key;
      if (p.hasToken(JsonToken.FIELD_NAME))
        key = p.getCurrentName();
    }
    for (String key = null; 
        

        key != null; key = p.nextFieldName()) {
      JsonToken t = p.nextToken();
      if ((_ignorableProperties != null) && (_ignorableProperties.contains(key))) {
        p.skipChildren();
      }
      else
      {
        SettableBeanProperty prop = creator.findCreatorProperty(key);
        if (prop != null)
        {
          if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
            p.nextToken();
            try
            {
              result = (Map)creator.build(ctxt, buffer);
            } catch (Exception e) { Map<Object, Object> result;
              return (Map)wrapAndThrow(e, _containerType.getRawClass(), key); }
            Map<Object, Object> result;
            _readAndBind(p, ctxt, result);
            return result;
          }
        }
        else
        {
          Object actualKey = _keyDeserializer.deserializeKey(key, ctxt);
          try
          {
            Object value;
            if (t == JsonToken.VALUE_NULL) {
              if (_skipNullValues) {
                continue;
              }
              value = _nullProvider.getNullValue(ctxt); } else { Object value;
              if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
              } else
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
          } catch (Exception e) { Object value;
            wrapAndThrow(e, _containerType.getRawClass(), key);
            return null; }
          Object value;
          buffer.bufferMapProperty(actualKey, value);
        }
      }
    }
    try {
      return (Map)creator.build(ctxt, buffer);
    } catch (Exception e) {
      wrapAndThrow(e, _containerType.getRawClass(), key); }
    return null;
  }
  










  protected final void _readAndUpdate(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
    throws IOException
  {
    KeyDeserializer keyDes = _keyDeserializer;
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    

    String keyStr;
    

    if (p.isExpectedStartObjectToken()) {
      keyStr = p.nextFieldName();
    } else {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.END_OBJECT) {
        return;
      }
      if (t != JsonToken.FIELD_NAME)
        ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
    }
    for (String keyStr = p.getCurrentName(); 
        

        keyStr != null; keyStr = p.nextFieldName()) {
      Object key = keyDes.deserializeKey(keyStr, ctxt);
      
      JsonToken t = p.nextToken();
      if ((_ignorableProperties != null) && (_ignorableProperties.contains(keyStr))) {
        p.skipChildren();
      }
      else {
        try
        {
          if (t == JsonToken.VALUE_NULL) {
            if (!_skipNullValues)
            {

              result.put(key, _nullProvider.getNullValue(ctxt));
            }
          } else {
            Object old = result.get(key);
            Object value;
            Object value; if (old != null) { Object value;
              if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt, old);
              } else
                value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
            } else { Object value;
              if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
              } else
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            if (value != old)
              result.put(key, value);
          }
        } catch (Exception e) {
          wrapAndThrow(e, result, keyStr);
        }
      }
    }
  }
  






  protected final void _readAndUpdateStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
    throws IOException
  {
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    

    String key;
    

    if (p.isExpectedStartObjectToken()) {
      key = p.nextFieldName();
    } else {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.END_OBJECT) {
        return;
      }
      if (t != JsonToken.FIELD_NAME)
        ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
    }
    for (String key = p.getCurrentName(); 
        

        key != null; key = p.nextFieldName()) {
      JsonToken t = p.nextToken();
      if ((_ignorableProperties != null) && (_ignorableProperties.contains(key))) {
        p.skipChildren();
      }
      else {
        try
        {
          if (t == JsonToken.VALUE_NULL) {
            if (!_skipNullValues)
            {

              result.put(key, _nullProvider.getNullValue(ctxt));
            }
          } else {
            Object old = result.get(key);
            Object value;
            Object value; if (old != null) { Object value;
              if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt, old);
              } else
                value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
            } else { Object value;
              if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
              } else
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            if (value != old)
              result.put(key, value);
          }
        } catch (Exception e) {
          wrapAndThrow(e, result, key);
        }
      }
    }
  }
  







  private void handleUnresolvedReference(DeserializationContext ctxt, MapReferringAccumulator accumulator, Object key, UnresolvedForwardReference reference)
    throws JsonMappingException
  {
    if (accumulator == null) {
      ctxt.reportInputMismatch(this, "Unresolved forward reference but no identity info: " + reference, new Object[0]);
    }
    
    ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
    reference.getRoid().appendReferring(referring);
  }
  

  private static final class MapReferringAccumulator
  {
    private final Class<?> _valueType;
    
    private Map<Object, Object> _result;
    private List<MapDeserializer.MapReferring> _accumulator = new ArrayList();
    
    public MapReferringAccumulator(Class<?> valueType, Map<Object, Object> result) {
      _valueType = valueType;
      _result = result;
    }
    
    public void put(Object key, Object value)
    {
      if (_accumulator.isEmpty()) {
        _result.put(key, value);
      } else {
        MapDeserializer.MapReferring ref = (MapDeserializer.MapReferring)_accumulator.get(_accumulator.size() - 1);
        next.put(key, value);
      }
    }
    
    public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference, Object key)
    {
      MapDeserializer.MapReferring id = new MapDeserializer.MapReferring(this, reference, _valueType, key);
      _accumulator.add(id);
      return id;
    }
    
    public void resolveForwardReference(Object id, Object value) throws IOException
    {
      Iterator<MapDeserializer.MapReferring> iterator = _accumulator.iterator();
      


      Map<Object, Object> previous = _result;
      while (iterator.hasNext()) {
        MapDeserializer.MapReferring ref = (MapDeserializer.MapReferring)iterator.next();
        if (ref.hasId(id)) {
          iterator.remove();
          previous.put(key, value);
          previous.putAll(next);
          return;
        }
        previous = next;
      }
      
      throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
    }
  }
  



  static class MapReferring
    extends ReadableObjectId.Referring
  {
    private final MapDeserializer.MapReferringAccumulator _parent;
    

    public final Map<Object, Object> next = new LinkedHashMap();
    
    public final Object key;
    
    MapReferring(MapDeserializer.MapReferringAccumulator parent, UnresolvedForwardReference ref, Class<?> valueType, Object key)
    {
      super(valueType);
      _parent = parent;
      this.key = key;
    }
    
    public void handleResolvedForwardReference(Object id, Object value) throws IOException
    {
      _parent.resolveForwardReference(id, value);
    }
  }
}
