package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.impl.FailingDeserializer;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ViewMatcher;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;





public abstract class SettableBeanProperty
  extends ConcreteBeanPropertyBase
  implements Serializable
{
  protected static final JsonDeserializer<Object> MISSING_VALUE_DESERIALIZER = new FailingDeserializer("No _valueDeserializer assigned");
  







  protected final PropertyName _propName;
  






  protected final JavaType _type;
  






  protected final PropertyName _wrapperName;
  






  protected final transient Annotations _contextAnnotations;
  






  protected final JsonDeserializer<Object> _valueDeserializer;
  






  protected final TypeDeserializer _valueTypeDeserializer;
  






  protected final NullValueProvider _nullProvider;
  






  protected String _managedReferenceName;
  






  protected ObjectIdInfo _objectIdInfo;
  






  protected ViewMatcher _viewMatcher;
  






  protected int _propertyIndex = -1;
  







  protected SettableBeanProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations)
  {
    this(propDef.getFullName(), type, propDef.getWrapperName(), typeDeser, contextAnnotations, propDef
      .getMetadata());
  }
  


  protected SettableBeanProperty(PropertyName propName, JavaType type, PropertyName wrapper, TypeDeserializer typeDeser, Annotations contextAnnotations, PropertyMetadata metadata)
  {
    super(metadata);
    




    if (propName == null) {
      _propName = PropertyName.NO_NAME;
    } else {
      _propName = propName.internSimpleName();
    }
    _type = type;
    _wrapperName = wrapper;
    _contextAnnotations = contextAnnotations;
    _viewMatcher = null;
    

    if (typeDeser != null) {
      typeDeser = typeDeser.forProperty(this);
    }
    _valueTypeDeserializer = typeDeser;
    _valueDeserializer = MISSING_VALUE_DESERIALIZER;
    _nullProvider = MISSING_VALUE_DESERIALIZER;
  }
  






  protected SettableBeanProperty(PropertyName propName, JavaType type, PropertyMetadata metadata, JsonDeserializer<Object> valueDeser)
  {
    super(metadata);
    
    if (propName == null) {
      _propName = PropertyName.NO_NAME;
    } else {
      _propName = propName.internSimpleName();
    }
    _type = type;
    _wrapperName = null;
    _contextAnnotations = null;
    _viewMatcher = null;
    _valueTypeDeserializer = null;
    _valueDeserializer = valueDeser;
    
    _nullProvider = valueDeser;
  }
  



  protected SettableBeanProperty(SettableBeanProperty src)
  {
    super(src);
    _propName = _propName;
    _type = _type;
    _wrapperName = _wrapperName;
    _contextAnnotations = _contextAnnotations;
    _valueDeserializer = _valueDeserializer;
    _valueTypeDeserializer = _valueTypeDeserializer;
    _managedReferenceName = _managedReferenceName;
    _propertyIndex = _propertyIndex;
    _viewMatcher = _viewMatcher;
    _nullProvider = _nullProvider;
  }
  





  protected SettableBeanProperty(SettableBeanProperty src, JsonDeserializer<?> deser, NullValueProvider nuller)
  {
    super(src);
    _propName = _propName;
    _type = _type;
    _wrapperName = _wrapperName;
    _contextAnnotations = _contextAnnotations;
    _valueTypeDeserializer = _valueTypeDeserializer;
    _managedReferenceName = _managedReferenceName;
    _propertyIndex = _propertyIndex;
    
    if (deser == null) {
      _valueDeserializer = MISSING_VALUE_DESERIALIZER;
    } else {
      _valueDeserializer = deser;
    }
    _viewMatcher = _viewMatcher;
    
    if (nuller == MISSING_VALUE_DESERIALIZER) {
      nuller = _valueDeserializer;
    }
    _nullProvider = nuller;
  }
  



  protected SettableBeanProperty(SettableBeanProperty src, PropertyName newName)
  {
    super(src);
    _propName = newName;
    _type = _type;
    _wrapperName = _wrapperName;
    _contextAnnotations = _contextAnnotations;
    _valueDeserializer = _valueDeserializer;
    _valueTypeDeserializer = _valueTypeDeserializer;
    _managedReferenceName = _managedReferenceName;
    _propertyIndex = _propertyIndex;
    _viewMatcher = _viewMatcher;
    _nullProvider = _nullProvider;
  }
  








  public abstract SettableBeanProperty withValueDeserializer(JsonDeserializer<?> paramJsonDeserializer);
  








  public abstract SettableBeanProperty withName(PropertyName paramPropertyName);
  







  public SettableBeanProperty withSimpleName(String simpleName)
  {
    PropertyName n = _propName == null ? new PropertyName(simpleName) : _propName.withSimpleName(simpleName);
    return n == _propName ? this : withName(n);
  }
  

  public abstract SettableBeanProperty withNullProvider(NullValueProvider paramNullValueProvider);
  

  public void setManagedReferenceName(String n)
  {
    _managedReferenceName = n;
  }
  
  public void setObjectIdInfo(ObjectIdInfo objectIdInfo) {
    _objectIdInfo = objectIdInfo;
  }
  
  public void setViews(Class<?>[] views) {
    if (views == null) {
      _viewMatcher = null;
    } else {
      _viewMatcher = ViewMatcher.construct(views);
    }
  }
  


  public void assignIndex(int index)
  {
    if (_propertyIndex != -1) {
      throw new IllegalStateException("Property '" + getName() + "' already had index (" + _propertyIndex + "), trying to assign " + index);
    }
    _propertyIndex = index;
  }
  





  public void fixAccess(DeserializationConfig config) {}
  




  public void markAsIgnorable() {}
  




  public boolean isIgnorable()
  {
    return false;
  }
  





  public final String getName()
  {
    return _propName.getSimpleName();
  }
  
  public PropertyName getFullName()
  {
    return _propName;
  }
  
  public JavaType getType() {
    return _type;
  }
  
  public PropertyName getWrapperName() {
    return _wrapperName;
  }
  

  public abstract AnnotatedMember getMember();
  

  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
  
  public <A extends Annotation> A getContextAnnotation(Class<A> acls)
  {
    return _contextAnnotations.get(acls);
  }
  


  public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
    throws JsonMappingException
  {
    if (isRequired()) {
      objectVisitor.property(this);
    } else {
      objectVisitor.optionalProperty(this);
    }
  }
  





  protected Class<?> getDeclaringClass()
  {
    return getMember().getDeclaringClass();
  }
  
  public String getManagedReferenceName() { return _managedReferenceName; }
  
  public ObjectIdInfo getObjectIdInfo() { return _objectIdInfo; }
  
  public boolean hasValueDeserializer() {
    return (_valueDeserializer != null) && (_valueDeserializer != MISSING_VALUE_DESERIALIZER);
  }
  
  public boolean hasValueTypeDeserializer() { return _valueTypeDeserializer != null; }
  
  public JsonDeserializer<Object> getValueDeserializer() {
    JsonDeserializer<Object> deser = _valueDeserializer;
    if (deser == MISSING_VALUE_DESERIALIZER) {
      return null;
    }
    return deser;
  }
  
  public TypeDeserializer getValueTypeDeserializer() { return _valueTypeDeserializer; }
  

  public NullValueProvider getNullValueProvider()
  {
    return _nullProvider;
  }
  
  public boolean visibleInView(Class<?> activeView) { return (_viewMatcher == null) || (_viewMatcher.isVisibleForView(activeView)); }
  
  public boolean hasViews() {
    return _viewMatcher != null;
  }
  




  public int getPropertyIndex()
  {
    return _propertyIndex;
  }
  





  public int getCreatorIndex()
  {
    throw new IllegalStateException(String.format("Internal error: no creator index for property '%s' (of type %s)", new Object[] {
    
      getName(), getClass().getName() }));
  }
  


  public Object getInjectableValueId()
  {
    return null;
  }
  










  public abstract void deserializeAndSet(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext, Object paramObject)
    throws IOException;
  










  public abstract Object deserializeSetAndReturn(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext, Object paramObject)
    throws IOException;
  









  public abstract void set(Object paramObject1, Object paramObject2)
    throws IOException;
  









  public abstract Object setAndReturn(Object paramObject1, Object paramObject2)
    throws IOException;
  









  public final Object deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NULL)) {
      return _nullProvider.getNullValue(ctxt);
    }
    if (_valueTypeDeserializer != null) {
      return _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }
    
    Object value = _valueDeserializer.deserialize(p, ctxt);
    if (value == null) {
      value = _nullProvider.getNullValue(ctxt);
    }
    return value;
  }
  





  public final Object deserializeWith(JsonParser p, DeserializationContext ctxt, Object toUpdate)
    throws IOException
  {
    if (p.hasToken(JsonToken.VALUE_NULL))
    {
      if (NullsConstantProvider.isSkipper(_nullProvider)) {
        return toUpdate;
      }
      return _nullProvider.getNullValue(ctxt);
    }
    
    if (_valueTypeDeserializer != null) {
      ctxt.reportBadDefinition(getType(), 
        String.format("Cannot merge polymorphic property '%s'", new Object[] {
        getName() }));
    }
    

    Object value = _valueDeserializer.deserialize(p, ctxt, toUpdate);
    if (value == null) {
      if (NullsConstantProvider.isSkipper(_nullProvider)) {
        return toUpdate;
      }
      value = _nullProvider.getNullValue(ctxt);
    }
    return value;
  }
  









  protected void _throwAsIOE(JsonParser p, Exception e, Object value)
    throws IOException
  {
    if ((e instanceof IllegalArgumentException)) {
      String actType = ClassUtil.classNameOf(value);
      




      StringBuilder msg = new StringBuilder("Problem deserializing property '").append(getName()).append("' (expected type: ").append(getType()).append("; actual type: ").append(actType).append(")");
      String origMsg = ClassUtil.exceptionMessage(e);
      if (origMsg != null)
      {
        msg.append(", problem: ").append(origMsg);
      } else {
        msg.append(" (no error message provided)");
      }
      throw JsonMappingException.from(p, msg.toString(), e);
    }
    _throwAsIOE(p, e);
  }
  


  protected IOException _throwAsIOE(JsonParser p, Exception e)
    throws IOException
  {
    ClassUtil.throwIfIOE(e);
    ClassUtil.throwIfRTE(e);
    
    Throwable th = ClassUtil.getRootCause(e);
    throw JsonMappingException.from(p, ClassUtil.exceptionMessage(th), th);
  }
  
  @Deprecated
  protected IOException _throwAsIOE(Exception e) throws IOException {
    return _throwAsIOE((JsonParser)null, e);
  }
  
  protected void _throwAsIOE(Exception e, Object value)
    throws IOException
  {
    _throwAsIOE((JsonParser)null, e, value);
  }
  
  public String toString() { return "[property '" + getName() + "']"; }
  








  public static abstract class Delegating
    extends SettableBeanProperty
  {
    protected final SettableBeanProperty delegate;
    







    protected Delegating(SettableBeanProperty d)
    {
      super();
      delegate = d;
    }
    


    protected abstract SettableBeanProperty withDelegate(SettableBeanProperty paramSettableBeanProperty);
    

    protected SettableBeanProperty _with(SettableBeanProperty newDelegate)
    {
      if (newDelegate == delegate) {
        return this;
      }
      return withDelegate(newDelegate);
    }
    
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser)
    {
      return _with(delegate.withValueDeserializer(deser));
    }
    
    public SettableBeanProperty withName(PropertyName newName)
    {
      return _with(delegate.withName(newName));
    }
    
    public SettableBeanProperty withNullProvider(NullValueProvider nva)
    {
      return _with(delegate.withNullProvider(nva));
    }
    
    public void assignIndex(int index)
    {
      delegate.assignIndex(index);
    }
    
    public void fixAccess(DeserializationConfig config)
    {
      delegate.fixAccess(config);
    }
    





    protected Class<?> getDeclaringClass()
    {
      return delegate.getDeclaringClass();
    }
    
    public String getManagedReferenceName() { return delegate.getManagedReferenceName(); }
    
    public ObjectIdInfo getObjectIdInfo() {
      return delegate.getObjectIdInfo();
    }
    
    public boolean hasValueDeserializer() { return delegate.hasValueDeserializer(); }
    
    public boolean hasValueTypeDeserializer() {
      return delegate.hasValueTypeDeserializer();
    }
    
    public JsonDeserializer<Object> getValueDeserializer() { return delegate.getValueDeserializer(); }
    
    public TypeDeserializer getValueTypeDeserializer() {
      return delegate.getValueTypeDeserializer();
    }
    
    public boolean visibleInView(Class<?> activeView) { return delegate.visibleInView(activeView); }
    
    public boolean hasViews() {
      return delegate.hasViews();
    }
    
    public int getPropertyIndex() { return delegate.getPropertyIndex(); }
    
    public int getCreatorIndex() {
      return delegate.getCreatorIndex();
    }
    
    public Object getInjectableValueId() { return delegate.getInjectableValueId(); }
    
    public AnnotatedMember getMember()
    {
      return delegate.getMember();
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> acls)
    {
      return delegate.getAnnotation(acls);
    }
    





    public SettableBeanProperty getDelegate()
    {
      return delegate;
    }
    






    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
      throws IOException
    {
      delegate.deserializeAndSet(p, ctxt, instance);
    }
    

    public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
      throws IOException
    {
      return delegate.deserializeSetAndReturn(p, ctxt, instance);
    }
    
    public void set(Object instance, Object value) throws IOException
    {
      delegate.set(instance, value);
    }
    
    public Object setAndReturn(Object instance, Object value) throws IOException
    {
      return delegate.setAndReturn(instance, value);
    }
  }
}
