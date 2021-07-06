package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;





public abstract class ReferenceTypeSerializer<T>
  extends StdSerializer<T>
  implements ContextualSerializer
{
  private static final long serialVersionUID = 1L;
  public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
  





  protected final JavaType _referredType;
  





  protected final BeanProperty _property;
  





  protected final TypeSerializer _valueTypeSerializer;
  





  protected final JsonSerializer<Object> _valueSerializer;
  





  protected final NameTransformer _unwrapper;
  





  protected transient PropertySerializerMap _dynamicSerializers;
  





  protected final Object _suppressableValue;
  





  protected final boolean _suppressNulls;
  





  public ReferenceTypeSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser)
  {
    super(fullType);
    _referredType = fullType.getReferencedType();
    _property = null;
    _valueTypeSerializer = vts;
    _valueSerializer = ser;
    _unwrapper = null;
    _suppressableValue = null;
    _suppressNulls = false;
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
  }
  




  protected ReferenceTypeSerializer(ReferenceTypeSerializer<?> base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Object suppressableValue, boolean suppressNulls)
  {
    super(base);
    _referredType = _referredType;
    
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _property = property;
    _valueTypeSerializer = vts;
    _valueSerializer = valueSer;
    _unwrapper = unwrapper;
    _suppressableValue = suppressableValue;
    _suppressNulls = suppressNulls;
  }
  
  public JsonSerializer<T> unwrappingSerializer(NameTransformer transformer)
  {
    JsonSerializer<Object> valueSer = _valueSerializer;
    if (valueSer != null) {
      valueSer = valueSer.unwrappingSerializer(transformer);
    }
    
    NameTransformer unwrapper = _unwrapper == null ? transformer : NameTransformer.chainedTransformer(transformer, _unwrapper);
    if ((_valueSerializer == valueSer) && (_unwrapper == unwrapper)) {
      return this;
    }
    return withResolved(_property, _valueTypeSerializer, valueSer, unwrapper);
  }
  







  protected abstract ReferenceTypeSerializer<T> withResolved(BeanProperty paramBeanProperty, TypeSerializer paramTypeSerializer, JsonSerializer<?> paramJsonSerializer, NameTransformer paramNameTransformer);
  







  public abstract ReferenceTypeSerializer<T> withContentInclusion(Object paramObject, boolean paramBoolean);
  







  protected abstract boolean _isValuePresent(T paramT);
  







  protected abstract Object _getReferenced(T paramT);
  






  protected abstract Object _getReferencedIfPresent(T paramT);
  






  public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
    throws JsonMappingException
  {
    TypeSerializer typeSer = _valueTypeSerializer;
    if (typeSer != null) {
      typeSer = typeSer.forProperty(property);
    }
    
    JsonSerializer<?> ser = findAnnotatedContentSerializer(provider, property);
    if (ser == null)
    {
      ser = _valueSerializer;
      if (ser == null)
      {
        if (_useStatic(provider, property, _referredType)) {
          ser = _findSerializer(provider, _referredType, property);
        }
      } else {
        ser = provider.handlePrimaryContextualization(ser, property);
      }
    }
    ReferenceTypeSerializer<?> refSer;
    ReferenceTypeSerializer<?> refSer;
    if ((_property == property) && (_valueTypeSerializer == typeSer) && (_valueSerializer == ser))
    {
      refSer = this;
    } else {
      refSer = withResolved(property, typeSer, ser, _unwrapper);
    }
    

    if (property != null) {
      JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), handledType());
      if (inclV != null) {
        JsonInclude.Include incl = inclV.getContentInclusion();
        
        if (incl != JsonInclude.Include.USE_DEFAULTS) { Object valueToSuppress;
          Object valueToSuppress;
          boolean suppressNulls;
          boolean suppressNulls; Object valueToSuppress; boolean suppressNulls; switch (1.$SwitchMap$com$fasterxml$jackson$annotation$JsonInclude$Include[incl.ordinal()]) {
          case 1: 
            Object valueToSuppress = BeanUtil.getDefaultValue(_referredType);
            boolean suppressNulls = true;
            if ((valueToSuppress != null) && 
              (valueToSuppress.getClass().isArray())) {
              valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
            }
            
            break;
          case 2: 
            boolean suppressNulls = true;
            valueToSuppress = _referredType.isReferenceType() ? MARKER_FOR_EMPTY : null;
            break;
          case 3: 
            boolean suppressNulls = true;
            valueToSuppress = MARKER_FOR_EMPTY;
            break;
          case 4: 
            Object valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
            boolean suppressNulls; if (valueToSuppress == null) {
              suppressNulls = true;
            } else {
              suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
            }
            break;
          case 5: 
            Object valueToSuppress = null;
            suppressNulls = true;
            break;
          case 6: 
          default: 
            valueToSuppress = null;
            suppressNulls = false;
          }
          
          if ((_suppressableValue != valueToSuppress) || (_suppressNulls != suppressNulls))
          {
            refSer = refSer.withContentInclusion(valueToSuppress, suppressNulls);
          }
        }
      }
    }
    return refSer;
  }
  


  protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType)
  {
    if (referredType.isJavaLangObject()) {
      return false;
    }
    
    if (referredType.isFinal()) {
      return true;
    }
    
    if (referredType.useStaticType()) {
      return true;
    }
    
    AnnotationIntrospector intr = provider.getAnnotationIntrospector();
    if ((intr != null) && (property != null)) {
      Annotated ann = property.getMember();
      if (ann != null) {
        JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
        if (t == JsonSerialize.Typing.STATIC) {
          return true;
        }
        if (t == JsonSerialize.Typing.DYNAMIC) {
          return false;
        }
      }
    }
    
    return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
  }
  








  public boolean isEmpty(SerializerProvider provider, T value)
  {
    if (!_isValuePresent(value)) {
      return true;
    }
    Object contents = _getReferenced(value);
    if (contents == null) {
      return _suppressNulls;
    }
    if (_suppressableValue == null) {
      return false;
    }
    JsonSerializer<Object> ser = _valueSerializer;
    if (ser == null) {
      try {
        ser = _findCachedSerializer(provider, contents.getClass());
      } catch (JsonMappingException e) {
        throw new RuntimeJsonMappingException(e);
      }
    }
    if (_suppressableValue == MARKER_FOR_EMPTY) {
      return ser.isEmpty(provider, contents);
    }
    return _suppressableValue.equals(contents);
  }
  
  public boolean isUnwrappingSerializer()
  {
    return _unwrapper != null;
  }
  


  public JavaType getReferredType()
  {
    return _referredType;
  }
  







  public void serialize(T ref, JsonGenerator g, SerializerProvider provider)
    throws IOException
  {
    Object value = _getReferencedIfPresent(ref);
    if (value == null) {
      if (_unwrapper == null) {
        provider.defaultSerializeNull(g);
      }
      return;
    }
    JsonSerializer<Object> ser = _valueSerializer;
    if (ser == null) {
      ser = _findCachedSerializer(provider, value.getClass());
    }
    if (_valueTypeSerializer != null) {
      ser.serializeWithType(value, g, provider, _valueTypeSerializer);
    } else {
      ser.serialize(value, g, provider);
    }
  }
  


  public void serializeWithType(T ref, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException
  {
    Object value = _getReferencedIfPresent(ref);
    if (value == null) {
      if (_unwrapper == null) {
        provider.defaultSerializeNull(g);
      }
      return;
    }
    










    JsonSerializer<Object> ser = _valueSerializer;
    if (ser == null) {
      ser = _findCachedSerializer(provider, value.getClass());
    }
    ser.serializeWithType(value, g, provider, typeSer);
  }
  







  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    JsonSerializer<?> ser = _valueSerializer;
    if (ser == null) {
      ser = _findSerializer(visitor.getProvider(), _referredType, _property);
      if (_unwrapper != null) {
        ser = ser.unwrappingSerializer(_unwrapper);
      }
    }
    ser.acceptJsonFormatVisitor(visitor, _referredType);
  }
  










  private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider, Class<?> rawType)
    throws JsonMappingException
  {
    JsonSerializer<Object> ser = _dynamicSerializers.serializerFor(rawType);
    if (ser == null)
    {


      if (_referredType.hasGenericTypes())
      {

        JavaType fullType = provider.constructSpecializedType(_referredType, rawType);
        ser = provider.findValueSerializer(fullType, _property);
      } else {
        ser = provider.findValueSerializer(rawType, _property);
      }
      if (_unwrapper != null) {
        ser = ser.unwrappingSerializer(_unwrapper);
      }
      _dynamicSerializers = _dynamicSerializers.newWith(rawType, ser);
    }
    return ser;
  }
  





  private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop)
    throws JsonMappingException
  {
    return provider.findValueSerializer(type, prop);
  }
}
