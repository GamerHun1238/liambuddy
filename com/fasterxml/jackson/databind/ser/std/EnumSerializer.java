package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.EnumValues;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;



















@JacksonStdImpl
public class EnumSerializer
  extends StdScalarSerializer<Enum<?>>
  implements ContextualSerializer
{
  private static final long serialVersionUID = 1L;
  protected final EnumValues _values;
  protected final Boolean _serializeAsIndex;
  
  public EnumSerializer(EnumValues v, Boolean serializeAsIndex)
  {
    super(v.getEnumClass(), false);
    _values = v;
    _serializeAsIndex = serializeAsIndex;
  }
  












  public static EnumSerializer construct(Class<?> enumClass, SerializationConfig config, BeanDescription beanDesc, JsonFormat.Value format)
  {
    EnumValues v = EnumValues.constructFromName(config, enumClass);
    Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true, null);
    return new EnumSerializer(v, serializeAsIndex);
  }
  






  public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
    throws JsonMappingException
  {
    JsonFormat.Value format = findFormatOverrides(serializers, property, 
      handledType());
    if (format != null) {
      Class<?> type = handledType();
      Boolean serializeAsIndex = _isShapeWrittenUsingIndex(type, format, false, _serializeAsIndex);
      
      if (serializeAsIndex != _serializeAsIndex) {
        return new EnumSerializer(_values, serializeAsIndex);
      }
    }
    return this;
  }
  




  public EnumValues getEnumValues()
  {
    return _values;
  }
  






  public final void serialize(Enum<?> en, JsonGenerator gen, SerializerProvider serializers)
    throws IOException
  {
    if (_serializeAsIndex(serializers)) {
      gen.writeNumber(en.ordinal());
      return;
    }
    
    if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
      gen.writeString(en.toString());
      return;
    }
    gen.writeString(_values.serializedValueFor(en));
  }
  







  public JsonNode getSchema(SerializerProvider provider, Type typeHint)
  {
    if (_serializeAsIndex(provider)) {
      return createSchemaNode("integer", true);
    }
    ObjectNode objectNode = createSchemaNode("string", true);
    ArrayNode enumNode; if (typeHint != null) {
      JavaType type = provider.constructType(typeHint);
      if (type.isEnumType()) {
        enumNode = objectNode.putArray("enum");
        for (SerializableString value : _values.values()) {
          enumNode.add(value.getValue());
        }
      }
    }
    return objectNode;
  }
  

  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    throws JsonMappingException
  {
    SerializerProvider serializers = visitor.getProvider();
    if (_serializeAsIndex(serializers)) {
      visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
      return;
    }
    JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
    if (stringVisitor != null) {
      Set<String> enums = new LinkedHashSet();
      

      if ((serializers != null) && 
        (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING))) {
        for (Enum<?> e : _values.enums()) {
          enums.add(e.toString());
        }
        
      } else {
        for (SerializableString value : _values.values()) {
          enums.add(value.getValue());
        }
      }
      stringVisitor.enumTypes(enums);
    }
  }
  






  protected final boolean _serializeAsIndex(SerializerProvider serializers)
  {
    if (_serializeAsIndex != null) {
      return _serializeAsIndex.booleanValue();
    }
    return serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
  }
  






  protected static Boolean _isShapeWrittenUsingIndex(Class<?> enumClass, JsonFormat.Value format, boolean fromClass, Boolean defaultValue)
  {
    JsonFormat.Shape shape = format == null ? null : format.getShape();
    if (shape == null) {
      return defaultValue;
    }
    
    if ((shape == JsonFormat.Shape.ANY) || (shape == JsonFormat.Shape.SCALAR)) {
      return defaultValue;
    }
    
    if ((shape == JsonFormat.Shape.STRING) || (shape == JsonFormat.Shape.NATURAL)) {
      return Boolean.FALSE;
    }
    
    if ((shape.isNumeric()) || (shape == JsonFormat.Shape.ARRAY)) {
      return Boolean.TRUE;
    }
    
    throw new IllegalArgumentException(String.format("Unsupported serialization shape (%s) for Enum %s, not supported as %s annotation", new Object[] { shape, enumClass
    
      .getName(), fromClass ? "class" : "property" }));
  }
}
