package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Set;






public class ThrowableDeserializer
  extends BeanDeserializer
{
  private static final long serialVersionUID = 1L;
  protected static final String PROP_NAME_MESSAGE = "message";
  
  public ThrowableDeserializer(BeanDeserializer baseDeserializer)
  {
    super(baseDeserializer);
    
    _vanillaProcessing = false;
  }
  


  protected ThrowableDeserializer(BeanDeserializer src, NameTransformer unwrapper)
  {
    super(src, unwrapper);
  }
  
  public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper)
  {
    if (getClass() != ThrowableDeserializer.class) {
      return this;
    }
    



    return new ThrowableDeserializer(this, unwrapper);
  }
  







  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (_propertyBasedCreator != null) {
      return _deserializeUsingPropertyBased(p, ctxt);
    }
    if (_delegateDeserializer != null) {
      return _valueInstantiator.createUsingDelegate(ctxt, _delegateDeserializer
        .deserialize(p, ctxt));
    }
    if (_beanType.isAbstract()) {
      return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "abstract type (need to add/enable type information?)", new Object[0]);
    }
    
    boolean hasStringCreator = _valueInstantiator.canCreateFromString();
    boolean hasDefaultCtor = _valueInstantiator.canCreateUsingDefault();
    
    if ((!hasStringCreator) && (!hasDefaultCtor)) {
      return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "Throwable needs a default constructor, a single-String-arg constructor; or explicit @JsonCreator", new Object[0]);
    }
    

    Object throwable = null;
    Object[] pending = null;
    int pendingIx = 0;
    for (; 
        !p.hasToken(JsonToken.END_OBJECT); p.nextToken()) {
      String propName = p.getCurrentName();
      SettableBeanProperty prop = _beanProperties.find(propName);
      p.nextToken();
      
      if (prop != null) {
        if (throwable != null) {
          prop.deserializeAndSet(p, ctxt, throwable);
        }
        else
        {
          if (pending == null) {
            int len = _beanProperties.size();
            pending = new Object[len + len];
          }
          pending[(pendingIx++)] = prop;
          pending[(pendingIx++)] = prop.deserialize(p, ctxt);
        }
      }
      else
      {
        boolean isMessage = "message".equals(propName);
        if ((isMessage) && 
          (hasStringCreator)) {
          throwable = _valueInstantiator.createFromString(ctxt, p.getValueAsString());
          
          if (pending != null) {
            int i = 0; for (int len = pendingIx; i < len; i += 2) {
              prop = (SettableBeanProperty)pending[i];
              prop.set(throwable, pending[(i + 1)]);
            }
            pending = null;

          }
          

        }
        else if ((_ignorableProps != null) && (_ignorableProps.contains(propName))) {
          p.skipChildren();

        }
        else if (_anySetter != null) {
          _anySetter.deserializeAndSet(p, ctxt, throwable, propName);


        }
        else
        {

          handleUnknownProperty(p, ctxt, throwable, propName);
        }
      } }
    if (throwable == null)
    {





      if (hasStringCreator) {
        throwable = _valueInstantiator.createFromString(ctxt, null);
      } else {
        throwable = _valueInstantiator.createUsingDefault(ctxt);
      }
      
      if (pending != null) {
        int i = 0; for (int len = pendingIx; i < len; i += 2) {
          SettableBeanProperty prop = (SettableBeanProperty)pending[i];
          prop.set(throwable, pending[(i + 1)]);
        }
      }
    }
    return throwable;
  }
}
