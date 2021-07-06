package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;




























@JacksonStdImpl
public class MapEntryDeserializer
  extends ContainerDeserializerBase<Map.Entry<Object, Object>>
  implements ContextualDeserializer
{
  private static final long serialVersionUID = 1L;
  protected final KeyDeserializer _keyDeserializer;
  protected final JsonDeserializer<Object> _valueDeserializer;
  protected final TypeDeserializer _valueTypeDeserializer;
  
  public MapEntryDeserializer(JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser)
  {
    super(type);
    if (type.containedTypeCount() != 2) {
      throw new IllegalArgumentException("Missing generic type information for " + type);
    }
    _keyDeserializer = keyDeser;
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = valueTypeDeser;
  }
  




  protected MapEntryDeserializer(MapEntryDeserializer src)
  {
    super(src);
    _keyDeserializer = _keyDeserializer;
    _valueDeserializer = _valueDeserializer;
    _valueTypeDeserializer = _valueTypeDeserializer;
  }
  


  protected MapEntryDeserializer(MapEntryDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser)
  {
    super(src);
    _keyDeserializer = keyDeser;
    _valueDeserializer = valueDeser;
    _valueTypeDeserializer = valueTypeDeser;
  }
  







  protected MapEntryDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser)
  {
    if ((_keyDeserializer == keyDeser) && (_valueDeserializer == valueDeser) && (_valueTypeDeserializer == valueTypeDeser))
    {
      return this;
    }
    return new MapEntryDeserializer(this, keyDeser, valueDeser, valueTypeDeser);
  }
  












  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    throws JsonMappingException
  {
    KeyDeserializer kd = _keyDeserializer;
    if (kd == null) {
      kd = ctxt.findKeyDeserializer(_containerType.containedType(0), property);
    }
    else if ((kd instanceof ContextualKeyDeserializer)) {
      kd = ((ContextualKeyDeserializer)kd).createContextual(ctxt, property);
    }
    
    JsonDeserializer<?> vd = _valueDeserializer;
    vd = findConvertingContentDeserializer(ctxt, property, vd);
    JavaType contentType = _containerType.containedType(1);
    if (vd == null) {
      vd = ctxt.findContextualValueDeserializer(contentType, property);
    } else {
      vd = ctxt.handleSecondaryContextualization(vd, property, contentType);
    }
    TypeDeserializer vtd = _valueTypeDeserializer;
    if (vtd != null) {
      vtd = vtd.forProperty(property);
    }
    return withResolved(kd, vtd, vd);
  }
  






  public JavaType getContentType()
  {
    return _containerType.containedType(1);
  }
  
  public JsonDeserializer<Object> getContentDeserializer()
  {
    return _valueDeserializer;
  }
  








  public Map.Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException
  {
    JsonToken t = p.currentToken();
    if ((t != JsonToken.START_OBJECT) && (t != JsonToken.FIELD_NAME) && (t != JsonToken.END_OBJECT))
    {

      return (Map.Entry)_deserializeFromEmpty(p, ctxt);
    }
    if (t == JsonToken.START_OBJECT) {
      t = p.nextToken();
    }
    if (t != JsonToken.FIELD_NAME) {
      if (t == JsonToken.END_OBJECT) {
        return (Map.Entry)ctxt.reportInputMismatch(this, "Cannot deserialize a Map.Entry out of empty JSON Object", new Object[0]);
      }
      
      return (Map.Entry)ctxt.handleUnexpectedToken(handledType(), p);
    }
    
    KeyDeserializer keyDes = _keyDeserializer;
    JsonDeserializer<Object> valueDes = _valueDeserializer;
    TypeDeserializer typeDeser = _valueTypeDeserializer;
    
    String keyStr = p.getCurrentName();
    Object key = keyDes.deserializeKey(keyStr, ctxt);
    Object value = null;
    
    t = p.nextToken();
    try
    {
      if (t == JsonToken.VALUE_NULL) {
        value = valueDes.getNullValue(ctxt);
      } else if (typeDeser == null) {
        value = valueDes.deserialize(p, ctxt);
      } else {
        value = valueDes.deserializeWithType(p, ctxt, typeDeser);
      }
    } catch (Exception e) {
      wrapAndThrow(e, Map.Entry.class, keyStr);
    }
    

    t = p.nextToken();
    if (t != JsonToken.END_OBJECT) {
      if (t == JsonToken.FIELD_NAME) {
        ctxt.reportInputMismatch(this, "Problem binding JSON into Map.Entry: more than one entry in JSON (second field: '%s')", new Object[] {p
        
          .getCurrentName() });
      }
      else {
        ctxt.reportInputMismatch(this, "Problem binding JSON into Map.Entry: unexpected content after JSON Object entry: " + t, new Object[0]);
      }
      
      return null;
    }
    return new AbstractMap.SimpleEntry(key, value);
  }
  

  public Map.Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map.Entry<Object, Object> result)
    throws IOException
  {
    throw new IllegalStateException("Cannot update Map.Entry values");
  }
  



  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
    throws IOException
  {
    return typeDeserializer.deserializeTypedFromObject(p, ctxt);
  }
}
