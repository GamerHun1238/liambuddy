package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;










public abstract class ArraySerializerBase<T>
  extends ContainerSerializer<T>
  implements ContextualSerializer
{
  protected final BeanProperty _property;
  protected final Boolean _unwrapSingle;
  
  protected ArraySerializerBase(Class<T> cls)
  {
    super(cls);
    _property = null;
    _unwrapSingle = null;
  }
  






  @Deprecated
  protected ArraySerializerBase(Class<T> cls, BeanProperty property)
  {
    super(cls);
    _property = property;
    _unwrapSingle = null;
  }
  
  protected ArraySerializerBase(ArraySerializerBase<?> src)
  {
    super(_handledType, false);
    _property = _property;
    _unwrapSingle = _unwrapSingle;
  }
  




  protected ArraySerializerBase(ArraySerializerBase<?> src, BeanProperty property, Boolean unwrapSingle)
  {
    super(_handledType, false);
    _property = property;
    _unwrapSingle = unwrapSingle;
  }
  



  @Deprecated
  protected ArraySerializerBase(ArraySerializerBase<?> src, BeanProperty property)
  {
    super(_handledType, false);
    _property = property;
    _unwrapSingle = _unwrapSingle;
  }
  



  public abstract JsonSerializer<?> _withResolved(BeanProperty paramBeanProperty, Boolean paramBoolean);
  


  public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
    throws JsonMappingException
  {
    Boolean unwrapSingle = null;
    

    if (property != null) {
      JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
      if (format != null) {
        unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        if (unwrapSingle != _unwrapSingle) {
          return _withResolved(property, unwrapSingle);
        }
      }
    }
    return this;
  }
  



  public void serialize(T value, JsonGenerator gen, SerializerProvider provider)
    throws IOException
  {
    if ((_shouldUnwrapSingle(provider)) && 
      (hasSingleElement(value))) {
      serializeContents(value, gen, provider);
      return;
    }
    
    gen.writeStartArray(value);
    serializeContents(value, gen, provider);
    gen.writeEndArray();
  }
  


  public final void serializeWithType(T value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
      .typeId(value, JsonToken.START_ARRAY));
    
    g.setCurrentValue(value);
    serializeContents(value, g, provider);
    typeSer.writeTypeSuffix(g, typeIdDef);
  }
  

  protected abstract void serializeContents(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
    throws IOException;
  

  protected final boolean _shouldUnwrapSingle(SerializerProvider provider)
  {
    if (_unwrapSingle == null) {
      return provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    }
    return _unwrapSingle.booleanValue();
  }
}
