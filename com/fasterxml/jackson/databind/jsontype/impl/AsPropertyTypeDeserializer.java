package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;









public class AsPropertyTypeDeserializer
  extends AsArrayTypeDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final JsonTypeInfo.As _inclusion;
  
  public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl)
  {
    this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
  }
  





  public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion)
  {
    super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    _inclusion = inclusion;
  }
  
  public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
    super(src, property);
    _inclusion = _inclusion;
  }
  
  public TypeDeserializer forProperty(BeanProperty prop)
  {
    return prop == _property ? this : new AsPropertyTypeDeserializer(this, prop);
  }
  
  public JsonTypeInfo.As getTypeInclusion() {
    return _inclusion;
  }
  





  public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.canReadTypeId()) {
      Object typeId = p.getTypeId();
      if (typeId != null) {
        return _deserializeWithNativeTypeId(p, ctxt, typeId);
      }
    }
    

    JsonToken t = p.currentToken();
    if (t == JsonToken.START_OBJECT) {
      t = p.nextToken();
    } else if (t != JsonToken.FIELD_NAME)
    {






      return _deserializeTypedUsingDefaultImpl(p, ctxt, null);
    }
    
    TokenBuffer tb = null;
    for (; 
        t == JsonToken.FIELD_NAME; t = p.nextToken()) {
      String name = p.getCurrentName();
      p.nextToken();
      if (name.equals(_typePropertyName)) {
        return _deserializeTypedForId(p, ctxt, tb);
      }
      if (tb == null) {
        tb = new TokenBuffer(p, ctxt);
      }
      tb.writeFieldName(name);
      tb.copyCurrentStructure(p);
    }
    return _deserializeTypedUsingDefaultImpl(p, ctxt, tb);
  }
  

  protected Object _deserializeTypedForId(JsonParser p, DeserializationContext ctxt, TokenBuffer tb)
    throws IOException
  {
    String typeId = p.getText();
    JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
    if (_typeIdVisible) {
      if (tb == null) {
        tb = new TokenBuffer(p, ctxt);
      }
      tb.writeFieldName(p.getCurrentName());
      tb.writeString(typeId);
    }
    if (tb != null)
    {

      p.clearCurrentToken();
      p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
    }
    
    p.nextToken();
    
    return deser.deserialize(p, ctxt);
  }
  



  protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb)
    throws IOException
  {
    JsonDeserializer<Object> deser = _findDefaultImplDeserializer(ctxt);
    if (deser == null)
    {
      Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, _baseType);
      if (result != null) {
        return result;
      }
      
      if (p.isExpectedStartArrayToken()) {
        return super.deserializeTypedFromAny(p, ctxt);
      }
      if ((p.hasToken(JsonToken.VALUE_STRING)) && 
        (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT))) {
        String str = p.getText().trim();
        if (str.isEmpty()) {
          return null;
        }
      }
      
      String msg = String.format("missing type id property '%s'", new Object[] { _typePropertyName });
      

      if (_property != null) {
        msg = String.format("%s (for POJO property '%s')", new Object[] { msg, _property.getName() });
      }
      JavaType t = _handleMissingTypeId(ctxt, msg);
      if (t == null)
      {
        return null;
      }
      
      deser = ctxt.findContextualValueDeserializer(t, _property);
    }
    if (tb != null) {
      tb.writeEndObject();
      p = tb.asParser(p);
      
      p.nextToken();
    }
    return deser.deserialize(p, ctxt);
  }
  






  public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    if (p.hasToken(JsonToken.START_ARRAY)) {
      return super.deserializeTypedFromArray(p, ctxt);
    }
    return deserializeTypedFromObject(p, ctxt);
  }
}
