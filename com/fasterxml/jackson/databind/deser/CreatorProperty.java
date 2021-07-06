package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.annotation.Annotation;
































































public class CreatorProperty
  extends SettableBeanProperty
{
  private static final long serialVersionUID = 1L;
  protected final AnnotatedParameter _annotated;
  protected final Object _injectableValueId;
  protected SettableBeanProperty _fallbackSetter;
  protected final int _creatorIndex;
  protected boolean _ignorable;
  
  public CreatorProperty(PropertyName name, JavaType type, PropertyName wrapperName, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedParameter param, int index, Object injectableValueId, PropertyMetadata metadata)
  {
    super(name, type, wrapperName, typeDeser, contextAnnotations, metadata);
    _annotated = param;
    _creatorIndex = index;
    _injectableValueId = injectableValueId;
    _fallbackSetter = null;
  }
  


  protected CreatorProperty(CreatorProperty src, PropertyName newName)
  {
    super(src, newName);
    _annotated = _annotated;
    _injectableValueId = _injectableValueId;
    _fallbackSetter = _fallbackSetter;
    _creatorIndex = _creatorIndex;
    _ignorable = _ignorable;
  }
  
  protected CreatorProperty(CreatorProperty src, JsonDeserializer<?> deser, NullValueProvider nva)
  {
    super(src, deser, nva);
    _annotated = _annotated;
    _injectableValueId = _injectableValueId;
    _fallbackSetter = _fallbackSetter;
    _creatorIndex = _creatorIndex;
    _ignorable = _ignorable;
  }
  
  public SettableBeanProperty withName(PropertyName newName)
  {
    return new CreatorProperty(this, newName);
  }
  
  public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser)
  {
    if (_valueDeserializer == deser) {
      return this;
    }
    
    NullValueProvider nvp = _valueDeserializer == _nullProvider ? deser : _nullProvider;
    return new CreatorProperty(this, deser, nvp);
  }
  
  public SettableBeanProperty withNullProvider(NullValueProvider nva)
  {
    return new CreatorProperty(this, _valueDeserializer, nva);
  }
  
  public void fixAccess(DeserializationConfig config)
  {
    if (_fallbackSetter != null) {
      _fallbackSetter.fixAccess(config);
    }
  }
  





  public void setFallbackSetter(SettableBeanProperty fallbackSetter)
  {
    _fallbackSetter = fallbackSetter;
  }
  
  public void markAsIgnorable()
  {
    _ignorable = true;
  }
  
  public boolean isIgnorable()
  {
    return _ignorable;
  }
  










  public Object findInjectableValue(DeserializationContext context, Object beanInstance)
    throws JsonMappingException
  {
    if (_injectableValueId == null) {
      context.reportBadDefinition(ClassUtil.classOf(beanInstance), 
        String.format("Property '%s' (type %s) has no injectable value id configured", new Object[] {
        getName(), getClass().getName() }));
    }
    return context.findInjectableValue(_injectableValueId, this, beanInstance);
  }
  



  public void inject(DeserializationContext context, Object beanInstance)
    throws IOException
  {
    set(beanInstance, findInjectableValue(context, beanInstance));
  }
  






  public <A extends Annotation> A getAnnotation(Class<A> acls)
  {
    if (_annotated == null) {
      return null;
    }
    return _annotated.getAnnotation(acls);
  }
  
  public AnnotatedMember getMember() { return _annotated; }
  
  public int getCreatorIndex() {
    return _creatorIndex;
  }
  







  public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    _verifySetter();
    _fallbackSetter.set(instance, deserialize(p, ctxt));
  }
  

  public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
    throws IOException
  {
    _verifySetter();
    return _fallbackSetter.setAndReturn(instance, deserialize(p, ctxt));
  }
  
  public void set(Object instance, Object value)
    throws IOException
  {
    _verifySetter();
    _fallbackSetter.set(instance, value);
  }
  
  public Object setAndReturn(Object instance, Object value)
    throws IOException
  {
    _verifySetter();
    return _fallbackSetter.setAndReturn(instance, value);
  }
  





  public PropertyMetadata getMetadata()
  {
    PropertyMetadata md = super.getMetadata();
    if (_fallbackSetter != null) {
      return md.withMergeInfo(_fallbackSetter.getMetadata().getMergeInfo());
    }
    return md;
  }
  
  public Object getInjectableValueId()
  {
    return _injectableValueId;
  }
  
  public String toString() {
    return "[creator property, name '" + getName() + "'; inject id '" + _injectableValueId + "']";
  }
  
  private final void _verifySetter() throws IOException {
    if (_fallbackSetter == null) {
      _reportMissingSetter(null, null);
    }
  }
  
  private void _reportMissingSetter(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    String msg = "No fallback setter/field defined for creator property '" + getName() + "'";
    

    if (ctxt != null) {
      ctxt.reportBadDefinition(getType(), msg);
    } else {
      throw InvalidDefinitionException.from(p, msg, getType());
    }
  }
}
