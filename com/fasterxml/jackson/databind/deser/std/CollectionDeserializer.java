package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


































@JacksonStdImpl
public class CollectionDeserializer
  extends ContainerDeserializerBase<Collection<Object>>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = -1L;
  protected final JsonDeserializer<Object> _valueDeserializer;
  protected final TypeDeserializer _valueTypeDeserializer;
  protected final ValueInstantiator _valueInstantiator;
  protected final JsonDeserializer<Object> _delegateDeserializer;
  
  public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator)
  {
    this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, null, null, null);
  }
  








  protected CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super(collectionType, nuller, unwrapSingle);
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = valueTypeDeser;
    _valueInstantiator = valueInstantiator;
    _delegateDeserializer = delegateDeser;
  }
  




  protected CollectionDeserializer(CollectionDeserializer src)
  {
    super(src);
    _valueDeserializer = _valueDeserializer;
    _valueTypeDeserializer = _valueTypeDeserializer;
    _valueInstantiator = _valueInstantiator;
    _delegateDeserializer = _delegateDeserializer;
  }
  









  protected CollectionDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, NullValueProvider nuller, Boolean unwrapSingle)
  {
    return new CollectionDeserializer(_containerType, vd, vtd, _valueInstantiator, dd, nuller, unwrapSingle);
  }
  





  public boolean isCachable()
  {
    return (_valueDeserializer == null) && (_valueTypeDeserializer == null) && (_delegateDeserializer == null);
  }
  
















  public CollectionDeserializer createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    JsonDeserializer<Object> delegateDeser = null;
    if (_valueInstantiator != null) {
      if (_valueInstantiator.canCreateUsingDelegate()) {
        JavaType delegateType = _valueInstantiator.getDelegateType(ctxt.getConfig());
        if (delegateType == null) {
          ctxt.reportBadDefinition(_containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] { _containerType, _valueInstantiator
          

            .getClass().getName() }));
        }
        delegateDeser = findDeserializer(ctxt, delegateType, property);
      } else if (_valueInstantiator.canCreateUsingArrayDelegate()) {
        JavaType delegateType = _valueInstantiator.getArrayDelegateType(ctxt.getConfig());
        if (delegateType == null) {
          ctxt.reportBadDefinition(_containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] { _containerType, _valueInstantiator
          

            .getClass().getName() }));
        }
        delegateDeser = findDeserializer(ctxt, delegateType, property);
      }
    }
    


    Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    

    JsonDeserializer<?> valueDeser = _valueDeserializer;
    

    valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
    JavaType vt = _containerType.getContentType();
    if (valueDeser == null) {
      valueDeser = ctxt.findContextualValueDeserializer(vt, property);
    } else {
      valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
    }
    
    TypeDeserializer valueTypeDeser = _valueTypeDeserializer;
    if (valueTypeDeser != null) {
      valueTypeDeser = valueTypeDeser.forProperty(property);
    }
    NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
    if ((unwrapSingle != _unwrapSingle) || (nuller != _nullProvider) || (delegateDeser != _delegateDeserializer) || (valueDeser != _valueDeserializer) || (valueTypeDeser != _valueTypeDeserializer))
    {




      return withResolved(delegateDeser, valueDeser, valueTypeDeser, nuller, unwrapSingle);
    }
    
    return this;
  }
  






  public JsonDeserializer<Object> getContentDeserializer()
  {
    return _valueDeserializer;
  }
  
  public ValueInstantiator getValueInstantiator()
  {
    return _valueInstantiator;
  }
  








  public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_delegateDeserializer != null) {
      return (Collection)_valueInstantiator.createUsingDelegate(ctxt, _delegateDeserializer
        .deserialize(p, ctxt));
    }
    


    if (p.hasToken(JsonToken.VALUE_STRING)) {
      String str = p.getText();
      if (str.length() == 0) {
        return (Collection)_valueInstantiator.createFromString(ctxt, str);
      }
    }
    return deserialize(p, ctxt, createDefaultInstance(ctxt));
  }
  




  protected Collection<Object> createDefaultInstance(DeserializationContext ctxt)
    throws IOException
  {
    return (Collection)_valueInstantiator.createUsingDefault(ctxt);
  }
  



  public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      return handleNonArray(p, ctxt, result);
    }
    
    p.setCurrentValue(result);
    
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    
    if (valueDes.getObjectIdReader() != null) {
      return _deserializeWithObjectId(p, ctxt, result);
    }
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    JsonToken t;
    while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
      try {
        Object value;
        if (t == JsonToken.VALUE_NULL) {
          if (_skipNullValues) {
            continue;
          }
          Object value = _nullProvider.getNullValue(ctxt); } else { Object value;
          if (typeDeser == null) {
            value = valueDes.deserialize(p, ctxt);
          } else
            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
        }
        result.add(value);


      }
      catch (Exception e)
      {


        boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
        if (!wrap) {
          ClassUtil.throwIfRTE(e);
        }
        throw JsonMappingException.wrapWithPath(e, result, result.size());
      }
    }
    return result;
  }
  



  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromArray(p, ctxt);
  }
  








  protected final Collection<Object> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result)
    throws IOException
  {
    if (_unwrapSingle != Boolean.TRUE) if (_unwrapSingle != null) break label31;
    label31:
    boolean canWrap = ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    if (!canWrap) {
      return (Collection)ctxt.handleUnexpectedToken(_containerType, p);
    }
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    
    try
    {
      Object value;
      if (p.hasToken(JsonToken.VALUE_NULL))
      {
        if (_skipNullValues) {
          return result;
        }
        value = _nullProvider.getNullValue(ctxt); } else { Object value;
        if (typeDeser == null) {
          value = valueDes.deserialize(p, ctxt);
        } else
          value = valueDes.deserializeWithType(p, ctxt, typeDeser);
      }
    } catch (Exception e) {
      Object value;
      throw JsonMappingException.wrapWithPath(e, Object.class, result.size()); }
    Object value;
    result.add(value);
    return result;
  }
  


  protected Collection<Object> _deserializeWithObjectId(JsonParser p, DeserializationContext ctxt, Collection<Object> result)
    throws IOException
  {
    if (!p.isExpectedStartArrayToken()) {
      return handleNonArray(p, ctxt, result);
    }
    
    p.setCurrentValue(result);
    
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    
    CollectionReferringAccumulator referringAccumulator = new CollectionReferringAccumulator(_containerType.getContentType().getRawClass(), result);
    
    JsonToken t;
    while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
      try {
        Object value;
        if (t == JsonToken.VALUE_NULL) {
          if (_skipNullValues) {
            continue;
          }
          Object value = _nullProvider.getNullValue(ctxt); } else { Object value;
          if (typeDeser == null) {
            value = valueDes.deserialize(p, ctxt);
          } else
            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
        }
        referringAccumulator.add(value);
      } catch (UnresolvedForwardReference reference) {
        ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
        reference.getRoid().appendReferring(ref);
      } catch (Exception e) {
        boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
        if (!wrap) {
          ClassUtil.throwIfRTE(e);
        }
        throw JsonMappingException.wrapWithPath(e, result, result.size());
      }
    }
    return result;
  }
  



  public static class CollectionReferringAccumulator
  {
    private final Class<?> _elementType;
    

    private final Collection<Object> _result;
    

    private List<CollectionDeserializer.CollectionReferring> _accumulator = new ArrayList();
    
    public CollectionReferringAccumulator(Class<?> elementType, Collection<Object> result) {
      _elementType = elementType;
      _result = result;
    }
    
    public void add(Object value)
    {
      if (_accumulator.isEmpty()) {
        _result.add(value);
      } else {
        CollectionDeserializer.CollectionReferring ref = (CollectionDeserializer.CollectionReferring)_accumulator.get(_accumulator.size() - 1);
        next.add(value);
      }
    }
    
    public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference)
    {
      CollectionDeserializer.CollectionReferring id = new CollectionDeserializer.CollectionReferring(this, reference, _elementType);
      _accumulator.add(id);
      return id;
    }
    
    public void resolveForwardReference(Object id, Object value) throws IOException
    {
      Iterator<CollectionDeserializer.CollectionReferring> iterator = _accumulator.iterator();
      


      Collection<Object> previous = _result;
      while (iterator.hasNext()) {
        CollectionDeserializer.CollectionReferring ref = (CollectionDeserializer.CollectionReferring)iterator.next();
        if (ref.hasId(id)) {
          iterator.remove();
          previous.add(value);
          previous.addAll(next);
          return;
        }
        previous = next;
      }
      
      throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
    }
  }
  


  private static final class CollectionReferring
    extends ReadableObjectId.Referring
  {
    private final CollectionDeserializer.CollectionReferringAccumulator _parent;
    

    public final List<Object> next = new ArrayList();
    

    CollectionReferring(CollectionDeserializer.CollectionReferringAccumulator parent, UnresolvedForwardReference reference, Class<?> contentType)
    {
      super(contentType);
      _parent = parent;
    }
    
    public void handleResolvedForwardReference(Object id, Object value) throws IOException
    {
      _parent.resolveForwardReference(id, value);
    }
  }
}
