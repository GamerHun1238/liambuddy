package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;























public abstract class ContainerDeserializerBase<T>
  extends StdDeserializer<T>
  implements ValueInstantiator.Gettable
{
  protected final JavaType _containerType;
  protected final NullValueProvider _nullProvider;
  protected final boolean _skipNullValues;
  protected final Boolean _unwrapSingle;
  
  protected ContainerDeserializerBase(JavaType selfType, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super(selfType);
    _containerType = selfType;
    _unwrapSingle = unwrapSingle;
    _nullProvider = nuller;
    _skipNullValues = NullsConstantProvider.isSkipper(nuller);
  }
  
  protected ContainerDeserializerBase(JavaType selfType) {
    this(selfType, null, null);
  }
  


  protected ContainerDeserializerBase(ContainerDeserializerBase<?> base)
  {
    this(base, _nullProvider, _unwrapSingle);
  }
  



  protected ContainerDeserializerBase(ContainerDeserializerBase<?> base, NullValueProvider nuller, Boolean unwrapSingle)
  {
    super(_containerType);
    _containerType = _containerType;
    _nullProvider = nuller;
    _unwrapSingle = unwrapSingle;
    _skipNullValues = NullsConstantProvider.isSkipper(nuller);
  }
  





  public JavaType getValueType()
  {
    return _containerType;
  }
  
  public Boolean supportsUpdate(DeserializationConfig config) {
    return Boolean.TRUE;
  }
  
  public SettableBeanProperty findBackReference(String refName)
  {
    JsonDeserializer<Object> valueDeser = getContentDeserializer();
    if (valueDeser == null) {
      throw new IllegalArgumentException(String.format("Cannot handle managed/back reference '%s': type: container deserializer of type %s returned null for 'getContentDeserializer()'", new Object[] { refName, 
      
        getClass().getName() }));
    }
    return valueDeser.findBackReference(refName);
  }
  









  public JavaType getContentType()
  {
    if (_containerType == null) {
      return TypeFactory.unknownType();
    }
    return _containerType.getContentType();
  }
  



  public abstract JsonDeserializer<Object> getContentDeserializer();
  



  public ValueInstantiator getValueInstantiator()
  {
    return null;
  }
  


  public AccessPattern getEmptyAccessPattern()
  {
    return AccessPattern.DYNAMIC;
  }
  
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException
  {
    ValueInstantiator vi = getValueInstantiator();
    if ((vi == null) || (!vi.canCreateUsingDefault())) {
      JavaType type = getValueType();
      ctxt.reportBadDefinition(type, 
        String.format("Cannot create empty instance of %s, no default Creator", new Object[] { type }));
    }
    try {
      return vi.createUsingDefault(ctxt);
    } catch (IOException e) {
      return ClassUtil.throwAsMappingException(ctxt, e);
    }
  }
  









  protected <BOGUS> BOGUS wrapAndThrow(Throwable t, Object ref, String key)
    throws IOException
  {
    while (((t instanceof InvocationTargetException)) && (t.getCause() != null)) {
      t = t.getCause();
    }
    
    ClassUtil.throwIfError(t);
    
    if (((t instanceof IOException)) && (!(t instanceof JsonMappingException))) {
      throw ((IOException)t);
    }
    
    throw JsonMappingException.wrapWithPath(t, ref, 
      (String)ClassUtil.nonNull(key, "N/A"));
  }
}
